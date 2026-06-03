// ── Guard ──────────────────────────────────────────────────────────────────
const vendorUser = guardRole('vendor');

let allRequests = [];
let activeFilter = 'ALL';

// ── Init ───────────────────────────────────────────────────────────────────
document.addEventListener('DOMContentLoaded', () => {
  if (!vendorUser) return;
  const name  = vendorUser.name || vendorUser.sub || 'Vendor';
  const email = vendorUser.email || vendorUser.sub || '';
  document.getElementById('vendor-name').textContent   = name;
  document.getElementById('vendor-email').textContent  = email;
  document.getElementById('vendor-avatar').textContent = getUserInitials(name);
  loadVendorOverview();
});

// ── Overview ───────────────────────────────────────────────────────────────
async function loadVendorOverview() {
  tableLoading('vendor-ack-body', 6);
  tableLoading('vendor-inprogress-body', 6);
  try {
    const [requests, devices] = await Promise.all([
      apiGet('/api/repairs').catch(() => []),
      apiGet('/api/devices').catch(() => [])
    ]);

    allRequests = requests || [];

    const acked  = allRequests.filter(r => r.status === 'ACKNOWLEDGED');
    const inprog = allRequests.filter(r => ['IN_PROGRESS','INPROGRESS'].includes(r.status));
    const done   = allRequests.filter(r => r.status === 'COMPLETED');

    document.getElementById('vstat-acknowledged').textContent = acked.length;
    document.getElementById('vstat-inprogress').textContent   = inprog.length;
    document.getElementById('vstat-completed').textContent    = done.length;
    document.getElementById('vstat-devices').textContent      = (devices || []).length;

    // Badge
    const badge = document.getElementById('vendor-req-badge');
    if (acked.length > 0) { badge.textContent = acked.length; badge.style.display = ''; }
    else badge.style.display = 'none';

    // Render acknowledged queue
    renderActionTable('vendor-ack-body', acked, 'ack');
    renderActionTable('vendor-inprogress-body', inprog, 'inprogress');
  } catch (err) {
    tableEmpty('vendor-ack-body', 6, `Error: ${err.message}`);
    tableEmpty('vendor-inprogress-body', 6, '');
  }
}

function renderActionTable(tbodyId, requests, mode) {
  const tbody = document.getElementById(tbodyId);
  if (!requests.length) {
    tbody.innerHTML = `<tr><td colspan="6" style="text-align:center;padding:24px;color:var(--muted);font-size:13px">Nothing here</td></tr>`;
    return;
  }
  tbody.innerHTML = requests.map(r => {
    const action = mode === 'ack'
      ? `<button class="btn btn-sm btn-info" onclick="markInProgress(${r.id})">▶ Start</button>`
      : `<button class="btn btn-sm btn-success" onclick="markCompleted(${r.id})">✓ Complete</button>`;
    return `
      <tr>
        <td><span style="font-family:'DM Mono',monospace;font-size:12px">#${r.id}</span></td>
        <td>${r.deviceName || r.deviceId || '—'}</td>
        <td>${r.issue || r.description || '—'}</td>
        <td><span style="font-size:12px;color:var(--muted)">${r.priority || '—'}</span></td>
        <td style="font-size:12px;color:var(--muted)">${fmtDateTime(r.createdAt || r.updatedAt)}</td>
        <td>${action}</td>
      </tr>`;
  }).join('');
}

// ── All Requests w/ Filter ─────────────────────────────────────────────────
async function loadVendorRequests() {
  tableLoading('vendor-all-requests-body', 7);
  try {
    const requests = await apiGet('/api/repairs');
    allRequests = requests || [];
    renderFilteredTable();
  } catch (err) {
    tableEmpty('vendor-all-requests-body', 7, `Error: ${err.message}`);
  }
}

function setFilter(f) {
  activeFilter = f;
  document.querySelectorAll('.filter-btn').forEach(b => {
    b.classList.toggle('active', b.dataset.filter === f);
  });
  renderFilteredTable();
}

function renderFilteredTable() {
  const filtered = activeFilter === 'ALL'
    ? allRequests
    : allRequests.filter(r => r.status === activeFilter || r.status === activeFilter.replace('_',''));

  const tbody = document.getElementById('vendor-all-requests-body');
  if (!filtered.length) {
    tbody.innerHTML = `<tr><td colspan="7"><div class="empty-state"><div class="empty-icon">🔧</div><div class="empty-title">No requests</div></div></td></tr>`;
    return;
  }
  tbody.innerHTML = filtered.map(r => {
    let action = '—';
    if (r.status === 'ACKNOWLEDGED') {
      action = `<button class="btn btn-sm btn-info" onclick="markInProgress(${r.id})">▶ Start</button>`;
    } else if (['IN_PROGRESS','INPROGRESS'].includes(r.status)) {
      action = `<button class="btn btn-sm btn-success" onclick="markCompleted(${r.id})">✓ Complete</button>`;
    }
    return `
      <tr>
        <td><span style="font-family:'DM Mono',monospace;font-size:12px">#${r.id}</span></td>
        <td>${r.deviceName || r.deviceId || '—'}</td>
        <td>${r.issue || r.description || '—'}</td>
        <td><span style="font-size:12px;color:var(--muted)">${r.priority || '—'}</span></td>
        <td>${statusBadge(r.status)}</td>
        <td style="font-size:12px;color:var(--muted)">${fmtDate(r.createdAt)}</td>
        <td>${action}</td>
      </tr>`;
  }).join('');
}

// ── Status actions ─────────────────────────────────────────────────────────
async function markInProgress(id) {
  try {
    await apiPatch(`/api/repairs/${id}/inprogress`);
    toast('Marked as In Progress!', 'success');
    loadVendorOverview();
    if (document.getElementById('page-repair').classList.contains('active')) loadVendorRequests();
  } catch (err) {
    toast(`Failed: ${err.message}`, 'error');
  }
}

async function markCompleted(id) {
  try {
    await apiPatch(`/api/repairs/${id}/complete`);
    toast('Marked as Completed!', 'success');
    loadVendorOverview();
    if (document.getElementById('page-repair').classList.contains('active')) loadVendorRequests();
  } catch (err) {
    toast(`Failed: ${err.message}`, 'error');
  }
}

// ── Devices ────────────────────────────────────────────────────────────────
async function loadVendorDevices() {
  tableLoading('vendor-devices-body', 5);
  try {
    const devices = await apiGet('/api/devices');
    const tbody = document.getElementById('vendor-devices-body');
    if (!(devices || []).length) {
      tbody.innerHTML = `<tr><td colspan="5"><div class="empty-state"><div class="empty-icon">🔧</div><div class="empty-title">No devices</div><div class="empty-sub">Add your first device</div></div></td></tr>`;
      return;
    }
    tbody.innerHTML = (devices || []).map(d => `
      <tr>
        <td><span style="font-family:'DM Mono',monospace;font-size:12px">${d.id}</span></td>
        <td>${d.name || '—'}</td>
        <td>${d.type || d.category || '—'}</td>
        <td style="font-family:'DM Mono',monospace;font-size:12px">${d.serialNumber || '—'}</td>
        <td>${statusBadge(d.status || 'ACTIVE')}</td>
      </tr>`).join('');
  } catch (err) {
    tableEmpty('vendor-devices-body', 5, `Error: ${err.message}`);
  }
}

// ── Add Device ─────────────────────────────────────────────────────────────
function openAddDeviceModal() {
  ['dev-name','dev-type','dev-serial','dev-user'].forEach(id => {
    document.getElementById(id).value = '';
  });
  document.getElementById('add-device-modal').classList.remove('hidden');
}

async function submitAddDevice() {
  const name   = document.getElementById('dev-name').value.trim();
  const type   = document.getElementById('dev-type').value.trim();
  const serial = document.getElementById('dev-serial').value.trim();
  const userId = document.getElementById('dev-user').value.trim();

  if (!name) return toast('Device name is required.', 'error');

  try {
    await apiPost('/api/devices', { name, type, serialNumber: serial, userId: userId || undefined });
    closeModal('add-device-modal');
    toast('Device added!', 'success');
    loadVendorDevices();
  } catch (err) {
    toast(`Failed: ${err.message}`, 'error');
  }
}

// ── Profile ────────────────────────────────────────────────────────────────
async function loadVendorProfile() {
  const body = document.getElementById('vendor-profile-body');
  try {
    const profile = await apiGet('/api/vendors/me');
    const u = profile || vendorUser;
    body.innerHTML = `
      <div style="display:flex;align-items:center;gap:20px;margin-bottom:28px">
        <div class="user-avatar" style="width:60px;height:60px;font-size:22px;border-radius:16px;background:linear-gradient(135deg,var(--accent2),#2563eb)">${getUserInitials(u.name || '')}</div>
        <div>
          <div style="font-family:'DM Serif Display',serif;font-size:22px">${u.name || '—'}</div>
          <div style="color:var(--muted);font-size:13px">${u.email || '—'}</div>
          <span class="status status-active" style="margin-top:6px;display:inline-flex">Active</span>
        </div>
      </div>
      <div class="detail-grid">
        <div class="detail-item"><div class="detail-key">Vendor ID</div><div class="detail-value" style="font-family:'DM Mono',monospace">${u.id || '—'}</div></div>
        <div class="detail-item"><div class="detail-key">Company</div><div class="detail-value">${u.company || u.companyName || '—'}</div></div>
        <div class="detail-item"><div class="detail-key">Phone</div><div class="detail-value">${u.phone || '—'}</div></div>
        <div class="detail-item"><div class="detail-key">Specialization</div><div class="detail-value">${u.specialization || '—'}</div></div>
        <div class="detail-item"><div class="detail-key">Joined</div><div class="detail-value">${fmtDate(u.createdAt)}</div></div>
      </div>`;
  } catch {
    const u = vendorUser;
    body.innerHTML = `
      <div style="display:flex;align-items:center;gap:20px;margin-bottom:28px">
        <div class="user-avatar" style="width:60px;height:60px;font-size:22px;border-radius:16px;background:linear-gradient(135deg,var(--accent2),#2563eb)">${getUserInitials(u.name || u.sub || '')}</div>
        <div>
          <div style="font-family:'DM Serif Display',serif;font-size:22px">${u.name || u.sub || '—'}</div>
          <div style="color:var(--muted);font-size:13px">${u.email || u.sub || '—'}</div>
        </div>
      </div>
      <div style="color:var(--muted);font-size:13px">Showing JWT session data. Could not load full vendor profile.</div>`;
  }
}

// ── Helpers ────────────────────────────────────────────────────────────────
function closeModal(id) { document.getElementById(id).classList.add('hidden'); }

document.querySelectorAll('.modal-overlay').forEach(overlay => {
  overlay.addEventListener('click', e => {
    if (e.target === overlay) overlay.classList.add('hidden');
  });
});
