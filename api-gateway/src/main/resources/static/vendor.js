// ── Guard ──────────────────────────────────────────────────────────────────
const vendorUser = guardRole('vendor');

let allRequests  = [];
let activeFilter = 'ALL';
let vendorDevSearch = '';

// ── Init ───────────────────────────────────────────────────────────────────
document.addEventListener('DOMContentLoaded', () => {
  if (!vendorUser) return;
  const name  = vendorUser.name  || vendorUser.sub  || 'Vendor';
  const email = vendorUser.email || vendorUser.sub  || '';
  document.getElementById('vendor-name').textContent   = name;
  document.getElementById('vendor-email').textContent  = email;
  document.getElementById('vendor-avatar').textContent = getUserInitials(name);
  loadVendorOverview();

  document.getElementById('vendor-dev-search')?.addEventListener('input', e => {
    vendorDevSearch = e.target.value.toLowerCase();
    renderVendorDevicesTable();
  });
});

// ── Overview ───────────────────────────────────────────────────────────────
async function loadVendorOverview() {
  tableLoading('vendor-ack-body', 6);
  tableLoading('vendor-inprogress-body', 6);
  try {
    const [requestsRes, devicesRes] = await Promise.allSettled([
      apiGet('/api/repairs'),
      apiGet('/api/devices')
    ]);

    allRequests = requestsRes.status === 'fulfilled' ? (requestsRes.value || []) : [];
    const devices = devicesRes.status === 'fulfilled' ? (devicesRes.value || []) : [];

    const acked  = allRequests.filter(r => r.status === 'ACKNOWLEDGED');
    const inprog = allRequests.filter(r => ['IN_PROGRESS','INPROGRESS'].includes(r.status));
    const done   = allRequests.filter(r => r.status === 'COMPLETED');
    const closed = allRequests.filter(r => r.status === 'CLOSED');

    document.getElementById('vstat-acknowledged').textContent = acked.length;
    document.getElementById('vstat-inprogress').textContent   = inprog.length;
    document.getElementById('vstat-completed').textContent    = done.length;
    document.getElementById('vstat-closed').textContent       = closed.length;
    document.getElementById('vstat-devices').textContent      = devices.length;

    const badge = document.getElementById('vendor-req-badge');
    if (acked.length > 0) { badge.textContent = acked.length; badge.style.display = ''; }
    else badge.style.display = 'none';

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
        ? `<button class="btn btn-sm btn-info" onclick="markInProgress(${r.id}, this)">▶ Start</button>`
        : `<button class="btn btn-sm btn-success" onclick="openCompleteModal(${r.id})">✓ Complete</button>`;
    return `
      <tr>
        <td><span style="font-family:'DM Mono',monospace;font-size:12px">#${r.id}</span></td>
        <td>${r.deviceName || r.deviceId || '—'}</td>
        <td title="${r.issue || r.description || ''}" style="max-width:220px;white-space:nowrap;overflow:hidden;text-overflow:ellipsis">${r.issue || r.description || '—'}</td>
        <td><span class="priority-badge priority-${(r.priority||'low').toLowerCase()}">${r.priority || '—'}</span></td>
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
      : allRequests.filter(r => {
        if (activeFilter === 'IN_PROGRESS') return ['IN_PROGRESS','INPROGRESS'].includes(r.status);
        return r.status === activeFilter;
      });

  const tbody = document.getElementById('vendor-all-requests-body');
  if (!filtered.length) {
    tbody.innerHTML = `<tr><td colspan="7"><div class="empty-state"><div class="empty-icon">🔧</div><div class="empty-title">No requests</div><div class="empty-sub">No requests match this filter</div></div></td></tr>`;
    return;
  }
  tbody.innerHTML = filtered.map(r => {
    let action = '—';
    if (r.status === 'ACKNOWLEDGED') {
      action = `<button class="btn btn-sm btn-info" onclick="markInProgress(${r.id}, this)">▶ Start</button>`;
    } else if (['IN_PROGRESS','INPROGRESS'].includes(r.status)) {
      action = `<button class="btn btn-sm btn-success" onclick="openCompleteModal(${r.id})">✓ Complete</button>`;
    }
    return `
      <tr>
        <td><span style="font-family:'DM Mono',monospace;font-size:12px">#${r.id}</span></td>
        <td>${r.deviceName || r.deviceId || '—'}</td>
        <td title="${r.issue || r.description || ''}" style="max-width:200px;white-space:nowrap;overflow:hidden;text-overflow:ellipsis">${r.issue || r.description || '—'}</td>
        <td><span class="priority-badge priority-${(r.priority||'low').toLowerCase()}">${r.priority || '—'}</span></td>
        <td>${statusBadge(r.status)}</td>
        <td style="font-size:12px;color:var(--muted)">${fmtDate(r.createdAt)}</td>
        <td>${action}</td>
      </tr>`;
  }).join('');
}

// ── Status actions ─────────────────────────────────────────────────────────
async function markInProgress(id, btn) {
  if (btn) { btn.disabled = true; btn.textContent = '…'; }
  try {
    await apiPatch(`/api/repairs/${id}/inprogress`);
    toast('Marked as In Progress!', 'success');
    await loadVendorOverview();
    if (document.getElementById('page-repair').classList.contains('active')) loadVendorRequests();
  } catch (err) {
    toast(`Failed: ${err.message}`, 'error');
    if (btn) { btn.disabled = false; btn.innerHTML = '▶ Start'; }
  }
}

// ── Complete Modal ──────────────────────────────────────────────────────────
let completeRequestId = null;

function openCompleteModal(id) {
  completeRequestId = id;
  document.getElementById('complete-notes').value = '';
  document.getElementById('complete-modal').classList.remove('hidden');
}

async function submitComplete() {
  if (!completeRequestId) return;
  const notes = document.getElementById('complete-notes').value.trim();

  const btn = document.getElementById('complete-submit-btn');
  btn.disabled = true; btn.textContent = 'Completing…';
  try {
    await apiPatch(`/api/repairs/${completeRequestId}/complete`, notes ? { notes } : {});
    closeModal('complete-modal');
    toast('Marked as Completed!', 'success');
    await loadVendorOverview();
    if (document.getElementById('page-repair').classList.contains('active')) loadVendorRequests();
  } catch (err) {
    toast(`Failed: ${err.message}`, 'error');
  } finally {
    btn.disabled = false; btn.textContent = 'Mark Complete';
  }
}

// ── Devices ────────────────────────────────────────────────────────────────
let vendorDevices = [];

async function loadVendorDevices() {
  tableLoading('vendor-devices-body', 5);
  try {
    const devices = await apiGet('/api/devices');
    vendorDevices = devices || [];
    renderVendorDevicesTable();
  } catch (err) {
    tableEmpty('vendor-devices-body', 5, `Error: ${err.message}`);
  }
}

function renderVendorDevicesTable() {
  const tbody = document.getElementById('vendor-devices-body');
  const filtered = vendorDevSearch
      ? vendorDevices.filter(d =>
          (d.name || '').toLowerCase().includes(vendorDevSearch) ||
          (d.type || d.category || '').toLowerCase().includes(vendorDevSearch) ||
          (d.serialNumber || '').toLowerCase().includes(vendorDevSearch)
      )
      : vendorDevices;

  if (!filtered.length) {
    tbody.innerHTML = `<tr><td colspan="5"><div class="empty-state"><div class="empty-icon">🔧</div><div class="empty-title">${vendorDevSearch ? 'No matches found' : 'No devices'}</div><div class="empty-sub">${vendorDevSearch ? 'Try different keywords' : 'Add your first device'}</div></div></td></tr>`;
    return;
  }
  tbody.innerHTML = filtered.map(d => `
    <tr>
      <td><span style="font-family:'DM Mono',monospace;font-size:12px">${d.id}</span></td>
      <td>${d.name || '—'}</td>
      <td>${d.type || d.category || '—'}</td>
      <td style="font-family:'DM Mono',monospace;font-size:12px">${d.serialNumber || '—'}</td>
      <td>${statusBadge(d.status || 'ACTIVE')}</td>
    </tr>`).join('');
}

// ── Add Device ─────────────────────────────────────────────────────────────
function openAddDeviceModal() {
  ['dev-name','dev-type','dev-serial','dev-user'].forEach(id => {
    const el = document.getElementById(id);
    if (el) el.value = '';
  });
  document.getElementById('add-device-modal').classList.remove('hidden');
}

async function submitAddDevice() {
  const name   = document.getElementById('dev-name').value.trim();
  const type   = document.getElementById('dev-type').value.trim();
  const serial = document.getElementById('dev-serial').value.trim();
  const userId = document.getElementById('dev-user').value.trim();

  if (!name) return toast('Device name is required.', 'error');

  const btn = document.getElementById('add-dev-btn');
  btn.disabled = true; btn.textContent = 'Adding…';
  try {
    await apiPost('/api/devices', { name, type, serialNumber: serial, userId: userId || undefined });
    closeModal('add-device-modal');
    toast('Device added!', 'success');
    loadVendorDevices();
  } catch (err) {
    toast(`Failed: ${err.message}`, 'error');
  } finally {
    btn.disabled = false; btn.textContent = 'Add Device';
  }
}

// ── Profile ────────────────────────────────────────────────────────────────
async function loadVendorProfile() {
  const body = document.getElementById('vendor-profile-body');
  body.innerHTML = `<div style="color:var(--muted)">Loading…</div>`;
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
