// ── Guard ──────────────────────────────────────────────────────────────────
const adminUser = guardRole('admin');

// ── Init ───────────────────────────────────────────────────────────────────
document.addEventListener('DOMContentLoaded', () => {
  if (!adminUser) return;

  // Populate sidebar user info from JWT
  const name  = adminUser.name || adminUser.sub || 'Admin';
  const email = adminUser.email || adminUser.sub || '';
  document.getElementById('admin-name').textContent    = name;
  document.getElementById('admin-email').textContent   = email;
  document.getElementById('admin-avatar').textContent  = getUserInitials(name);

  loadOverviewRequests();
  loadStats();
});

// ── Stats ──────────────────────────────────────────────────────────────────
async function loadStats() {
  try {
    const [devices, requests] = await Promise.all([
      apiGet('/api/devices'),
      apiGet('/api/repairs')
    ]);

    document.getElementById('stat-devices').textContent = (devices || []).length;

    const reqs = requests || [];
    const pending    = reqs.filter(r => r.status === 'PENDING').length;
    const inprogress = reqs.filter(r => ['IN_PROGRESS','INPROGRESS'].includes(r.status)).length;
    const completed  = reqs.filter(r => r.status === 'COMPLETED' || r.status === 'CLOSED').length;

    document.getElementById('stat-pending').textContent    = pending;
    document.getElementById('stat-inprogress').textContent = inprogress;
    document.getElementById('stat-completed').textContent  = completed;

    // Badge
    if (pending > 0) {
      const badge = document.getElementById('admin-req-badge');
      badge.textContent = pending;
      badge.style.display = '';
    }
  } catch (err) {
    console.error('Stats load failed:', err.message);
  }
}

// ── Overview Requests (pending) ────────────────────────────────────────────
async function loadOverviewRequests() {
  tableLoading('overview-requests-body', 7);
  try {
    const requests = await apiGet('/api/repairs');
    const filtered = (requests || []).filter(r => r.status === 'PENDING' || r.status === 'COMPLETED');
    renderRequestsTable('overview-requests-body', filtered, true);
  } catch (err) {
    tableEmpty('overview-requests-body', 7, `Failed to load: ${err.message}`);
  }
}

// ── Admin All Requests ─────────────────────────────────────────────────────
async function loadAdminRequests() {
  tableLoading('admin-requests-body', 7);
  try {
    const requests = await apiGet('/api/repairs');
    renderRequestsTable('admin-requests-body', requests || [], false);
  } catch (err) {
    tableEmpty('admin-requests-body', 7, `Failed to load: ${err.message}`);
  }
}

function renderRequestsTable(tbodyId, requests, compact) {
  const tbody = document.getElementById(tbodyId);
  if (!requests.length) {
    tbody.innerHTML = `<tr><td colspan="7"><div class="empty-state"><div class="empty-icon">📋</div><div class="empty-title">No requests found</div><div class="empty-sub">Nothing here yet</div></div></td></tr>`;
    return;
  }

  tbody.innerHTML = requests.map(r => {
    const canClose = r.status === 'COMPLETED';
    const actions = canClose
      ? `<button class="btn btn-sm btn-success" onclick="openCloseModal(${r.id})">Mark Closed</button>`
      : `<span style="color:var(--muted);font-size:12px">${r.status === 'CLOSED' ? 'Closed' : '—'}</span>`;

    return `
      <tr>
        <td><span style="font-family:'DM Mono',monospace;font-size:12px">#${r.id}</span></td>
        <td>${r.deviceName || r.deviceId || '—'}</td>
        <td>${compact ? (r.raisedBy || r.userId || '—') : (r.issue || r.description || '—')}</td>
        <td>${compact ? (r.issue || r.description || '—') : statusBadge(r.status)}</td>
        <td>${compact ? statusBadge(r.status) : (r.vendorName || r.vendorId || '—')}</td>
        <td style="font-size:12px;color:var(--muted)">${fmtDate(r.createdAt || r.raisedAt)}</td>
        <td>${actions}</td>
      </tr>`;
  }).join('');
}

// ── Admin Devices ──────────────────────────────────────────────────────────
async function loadAdminDevices() {
  tableLoading('admin-devices-body', 6);
  try {
    const devices = await apiGet('/api/devices');
    const tbody = document.getElementById('admin-devices-body');
    if (!(devices || []).length) {
      tbody.innerHTML = `<tr><td colspan="6"><div class="empty-state"><div class="empty-icon">🖥️</div><div class="empty-title">No devices registered</div></div></td></tr>`;
      return;
    }
    tbody.innerHTML = (devices || []).map(d => `
      <tr>
        <td><span style="font-family:'DM Mono',monospace;font-size:12px">${d.id}</span></td>
        <td>${d.name || '—'}</td>
        <td>${d.type || d.category || '—'}</td>
        <td style="font-family:'DM Mono',monospace;font-size:12px">${d.serialNumber || '—'}</td>
        <td>${d.assignedTo || d.userId || '—'}</td>
        <td>${statusBadge(d.status || 'ACTIVE')}</td>
      </tr>`).join('');
  } catch (err) {
    tableEmpty('admin-devices-body', 6, `Failed to load: ${err.message}`);
  }
}

// ── Admin Profile ──────────────────────────────────────────────────────────
async function loadAdminProfile() {
  const body = document.getElementById('admin-profile-body');
  try {
    const profile = await apiGet('/api/users/me');
    const u = profile || adminUser;
    body.innerHTML = `
      <div style="display:flex;align-items:center;gap:20px;margin-bottom:28px">
        <div class="user-avatar" style="width:60px;height:60px;font-size:22px;border-radius:16px">${getUserInitials(u.name || '')}</div>
        <div>
          <div style="font-family:'DM Serif Display',serif;font-size:22px">${u.name || '—'}</div>
          <div style="color:var(--muted);font-size:13px">${u.email || '—'}</div>
          <span class="status status-active" style="margin-top:6px;display:inline-flex">Active</span>
        </div>
      </div>
      <div class="detail-grid">
        <div class="detail-item"><div class="detail-key">User ID</div><div class="detail-value" style="font-family:'DM Mono',monospace">${u.id || '—'}</div></div>
        <div class="detail-item"><div class="detail-key">Role</div><div class="detail-value">Admin</div></div>
        <div class="detail-item"><div class="detail-key">Department</div><div class="detail-value">${u.department || '—'}</div></div>
        <div class="detail-item"><div class="detail-key">Phone</div><div class="detail-value">${u.phone || '—'}</div></div>
        <div class="detail-item"><div class="detail-key">Joined</div><div class="detail-value">${fmtDate(u.createdAt)}</div></div>
      </div>`;
  } catch (err) {
    // Fallback to JWT claims
    const u = adminUser;
    body.innerHTML = `
      <div style="display:flex;align-items:center;gap:20px;margin-bottom:28px">
        <div class="user-avatar" style="width:60px;height:60px;font-size:22px;border-radius:16px">${getUserInitials(u.name || u.sub || '')}</div>
        <div>
          <div style="font-family:'DM Serif Display',serif;font-size:22px">${u.name || u.sub || '—'}</div>
          <div style="color:var(--muted);font-size:13px">${u.email || u.sub || '—'}</div>
        </div>
      </div>
      <div style="color:var(--muted);font-size:13px">Could not load full profile from server. Showing JWT data.</div>`;
  }
}

// ── Raise Request Modal ────────────────────────────────────────────────────
async function openRaiseModal() {
  // Populate device dropdown
  const sel = document.getElementById('raise-device');
  sel.innerHTML = '<option value="">Loading…</option>';
  try {
    const devices = await apiGet('/api/devices');
    sel.innerHTML = (devices || []).map(d => `<option value="${d.id}">${d.name} (${d.id})</option>`).join('');
    if (!devices.length) sel.innerHTML = '<option value="">No devices available</option>';
  } catch {
    sel.innerHTML = '<option value="">Failed to load devices</option>';
  }
  document.getElementById('raise-issue').value = '';
  document.getElementById('raise-modal').classList.remove('hidden');
}

async function submitRaiseRequest() {
  const deviceId = document.getElementById('raise-device').value;
  const issue    = document.getElementById('raise-issue').value.trim();
  const priority = document.getElementById('raise-priority').value;

  if (!deviceId) return toast('Please select a device.', 'error');
  if (!issue)    return toast('Please describe the issue.', 'error');

  try {
    await apiPost('/api/repairs', { deviceId, issue, description: issue, priority });
    closeModal('raise-modal');
    toast('Repair request raised successfully!', 'success');
    loadOverviewRequests();
    loadStats();
  } catch (err) {
    toast(`Failed: ${err.message}`, 'error');
  }
}

// ── Close Request Modal ────────────────────────────────────────────────────
let closeRequestId = null;

function openCloseModal(id) {
  closeRequestId = id;
  document.getElementById('close-remark').value = '';
  document.getElementById('close-modal').classList.remove('hidden');
}

async function submitCloseRequest() {
  if (!closeRequestId) return;
  const remark = document.getElementById('close-remark').value.trim();
  try {
    await apiPatch(`/api/repairs/${closeRequestId}/close`, { remark });
    closeModal('close-modal');
    toast('Request marked as Closed!', 'success');
    loadOverviewRequests();
    loadAdminRequests();
    loadStats();
  } catch (err) {
    toast(`Failed: ${err.message}`, 'error');
  }
}

// ── Helpers ────────────────────────────────────────────────────────────────
function closeModal(id) {
  document.getElementById(id).classList.add('hidden');
}

// Close modal on overlay click
document.querySelectorAll('.modal-overlay').forEach(overlay => {
  overlay.addEventListener('click', e => {
    if (e.target === overlay) overlay.classList.add('hidden');
  });
});
