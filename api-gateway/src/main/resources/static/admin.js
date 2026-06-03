// ── Guard ──────────────────────────────────────────────────────────────────
const adminUser = guardRole('admin');

// ── State ──────────────────────────────────────────────────────────────────
let allAdminRequests = [];
let allAdminDevices  = [];
let adminReqFilter   = 'ALL';
let deviceSearch     = '';

// ── Init ───────────────────────────────────────────────────────────────────
document.addEventListener('DOMContentLoaded', () => {
  if (!adminUser) return;

  const name  = adminUser.name  || adminUser.sub  || 'Admin';
  const email = adminUser.email || adminUser.sub  || '';
  document.getElementById('admin-name').textContent   = name;
  document.getElementById('admin-email').textContent  = email;
  document.getElementById('admin-avatar').textContent = getUserInitials(name);

  loadStats();
  loadOverviewRequests();

  // Device search input live filter
  document.getElementById('device-search')?.addEventListener('input', e => {
    deviceSearch = e.target.value.toLowerCase();
    renderDevicesTable();
  });
});

// ── Stats ──────────────────────────────────────────────────────────────────
async function loadStats() {
  try {
    const [devices, requests, vendors] = await Promise.allSettled([
      apiGet('/api/devices'),
      apiGet('/api/repairs'),
      apiGet('/api/vendors')
    ]);

    const devList = devices.status === 'fulfilled' ? (devices.value || []) : [];
    const reqs    = requests.status === 'fulfilled' ? (requests.value || []) : [];
    const venList = vendors.status  === 'fulfilled' ? (vendors.value  || []) : [];

    document.getElementById('stat-devices').textContent  = devList.length;
    document.getElementById('stat-vendors').textContent  = venList.length;

    const pending    = reqs.filter(r => r.status === 'PENDING').length;
    const inprogress = reqs.filter(r => ['IN_PROGRESS','INPROGRESS'].includes(r.status)).length;
    const completed  = reqs.filter(r => r.status === 'COMPLETED' || r.status === 'CLOSED').length;

    document.getElementById('stat-pending').textContent    = pending;
    document.getElementById('stat-inprogress').textContent = inprogress;
    document.getElementById('stat-completed').textContent  = completed;

    if (pending > 0) {
      const badge = document.getElementById('admin-req-badge');
      if (badge) { badge.textContent = pending; badge.style.display = ''; }
    }
  } catch (err) {
    console.error('Stats load failed:', err.message);
  }
}

// ── Overview Requests ──────────────────────────────────────────────────────
async function loadOverviewRequests() {
  tableLoading('overview-requests-body', 7);
  try {
    const requests = await apiGet('/api/repairs');
    const filtered = (requests || []).filter(r =>
        r.status === 'PENDING' || r.status === 'ACKNOWLEDGED' || r.status === 'COMPLETED'
    );
    renderRequestsTable('overview-requests-body', filtered, true);
  } catch (err) {
    tableEmpty('overview-requests-body', 7, `Failed to load: ${err.message}`);
  }
}

// ── Admin All Requests ─────────────────────────────────────────────────────
async function loadAdminRequests() {
  tableLoading('admin-requests-body', 8);
  try {
    const [requests, vendors] = await Promise.all([
      apiGet('/api/repairs'),
      apiGet('/api/vendors').catch(() => [])
    ]);
    allAdminRequests = requests || [];
    window._vendorList = vendors || [];
    renderAdminRequestsTable();
  } catch (err) {
    tableEmpty('admin-requests-body', 8, `Failed to load: ${err.message}`);
  }
}

function setAdminReqFilter(f) {
  adminReqFilter = f;
  document.querySelectorAll('.req-filter-btn').forEach(b => {
    b.classList.toggle('active', b.dataset.filter === f);
  });
  renderAdminRequestsTable();
}

function renderAdminRequestsTable() {
  const filtered = adminReqFilter === 'ALL'
      ? allAdminRequests
      : allAdminRequests.filter(r => {
        if (adminReqFilter === 'IN_PROGRESS') return ['IN_PROGRESS','INPROGRESS'].includes(r.status);
        return r.status === adminReqFilter;
      });

  const tbody = document.getElementById('admin-requests-body');
  if (!filtered.length) {
    tbody.innerHTML = `<tr><td colspan="8"><div class="empty-state"><div class="empty-icon">📋</div><div class="empty-title">No requests found</div><div class="empty-sub">Try a different filter</div></div></td></tr>`;
    return;
  }

  tbody.innerHTML = filtered.map(r => {
    let actions = '';
    if (r.status === 'PENDING') {
      actions = `<button class="btn btn-sm btn-info" onclick="openAcknowledgeModal(${r.id})">✓ Acknowledge</button>`;
    } else if (r.status === 'ACKNOWLEDGED') {
      actions = `<button class="btn btn-sm btn-secondary" onclick="openAssignModal(${r.id})" style="font-size:11px">Assign Vendor</button>`;
    } else if (r.status === 'COMPLETED') {
      actions = `<button class="btn btn-sm btn-success" onclick="openCloseModal(${r.id})">Mark Closed</button>`;
    } else {
      actions = `<span style="color:var(--muted);font-size:12px">${r.status === 'CLOSED' ? 'Closed' : '—'}</span>`;
    }

    return `
      <tr>
        <td><span style="font-family:'DM Mono',monospace;font-size:12px">#${r.id}</span></td>
        <td>${r.deviceName || r.deviceId || '—'}</td>
        <td title="${r.issue || r.description || ''}" style="max-width:200px;white-space:nowrap;overflow:hidden;text-overflow:ellipsis">${r.issue || r.description || '—'}</td>
        <td>${r.raisedBy || r.userId || '—'}</td>
        <td>${statusBadge(r.status)}</td>
        <td><span style="font-size:12px;color:var(--muted)">${r.priority || '—'}</span></td>
        <td style="font-size:12px;color:var(--muted)">${fmtDate(r.createdAt || r.raisedAt)}</td>
        <td>${actions}</td>
      </tr>`;
  }).join('');
}

function renderRequestsTable(tbodyId, requests, compact) {
  const tbody = document.getElementById(tbodyId);
  if (!requests.length) {
    tbody.innerHTML = `<tr><td colspan="7"><div class="empty-state"><div class="empty-icon">📋</div><div class="empty-title">No requests found</div><div class="empty-sub">Nothing here yet</div></div></td></tr>`;
    return;
  }

  tbody.innerHTML = requests.map(r => {
    let actions = '';
    if (r.status === 'PENDING') {
      actions = `<button class="btn btn-sm btn-info" onclick="openAcknowledgeModal(${r.id})">✓ Acknowledge</button>`;
    } else if (r.status === 'COMPLETED') {
      actions = `<button class="btn btn-sm btn-success" onclick="openCloseModal(${r.id})">Mark Closed</button>`;
    } else {
      actions = `<span style="color:var(--muted);font-size:12px">${r.status}</span>`;
    }

    return `
      <tr>
        <td><span style="font-family:'DM Mono',monospace;font-size:12px">#${r.id}</span></td>
        <td>${r.deviceName || r.deviceId || '—'}</td>
        <td>${r.raisedBy || r.userId || '—'}</td>
        <td title="${r.issue || r.description || ''}" style="max-width:160px;white-space:nowrap;overflow:hidden;text-overflow:ellipsis">${r.issue || r.description || '—'}</td>
        <td>${statusBadge(r.status)}</td>
        <td style="font-size:12px;color:var(--muted)">${fmtDate(r.createdAt || r.raisedAt)}</td>
        <td>${actions}</td>
      </tr>`;
  }).join('');
}

// ── Admin Devices ──────────────────────────────────────────────────────────
async function loadAdminDevices() {
  tableLoading('admin-devices-body', 7);
  try {
    const devices = await apiGet('/api/devices');
    allAdminDevices = devices || [];
    renderDevicesTable();
  } catch (err) {
    tableEmpty('admin-devices-body', 7, `Failed to load: ${err.message}`);
  }
}

function renderDevicesTable() {
  const tbody = document.getElementById('admin-devices-body');
  const filtered = deviceSearch
      ? allAdminDevices.filter(d =>
          (d.name || '').toLowerCase().includes(deviceSearch) ||
          (d.type || d.category || '').toLowerCase().includes(deviceSearch) ||
          (d.serialNumber || '').toLowerCase().includes(deviceSearch) ||
          (d.assignedTo || d.userId || '').toLowerCase().includes(deviceSearch)
      )
      : allAdminDevices;

  if (!filtered.length) {
    tbody.innerHTML = `<tr><td colspan="7"><div class="empty-state"><div class="empty-icon">🖥️</div><div class="empty-title">${deviceSearch ? 'No matches found' : 'No devices registered'}</div></div></td></tr>`;
    return;
  }
  tbody.innerHTML = filtered.map(d => `
    <tr>
      <td><span style="font-family:'DM Mono',monospace;font-size:12px">${d.id}</span></td>
      <td>${d.name || '—'}</td>
      <td>${d.type || d.category || '—'}</td>
      <td style="font-family:'DM Mono',monospace;font-size:12px">${d.serialNumber || '—'}</td>
      <td>${d.assignedTo || d.userId || '—'}</td>
      <td>${statusBadge(d.status || 'ACTIVE')}</td>
      <td>
        <button class="btn btn-sm btn-secondary" onclick="openEditDeviceModal(${JSON.stringify(d).replace(/"/g, '&quot;')})" style="font-size:11px">Edit</button>
        <button class="btn btn-sm btn-danger" onclick="deleteDevice(${d.id})" style="font-size:11px;margin-left:4px">Delete</button>
      </td>
    </tr>`).join('');
}

// ── Delete Device ──────────────────────────────────────────────────────────
async function deleteDevice(id) {
  if (!confirm(`Delete device #${id}? This cannot be undone.`)) return;
  try {
    await apiDelete(`/api/devices/${id}`);
    toast('Device deleted.', 'success');
    await loadAdminDevices();
    await loadStats();
  } catch (err) {
    toast(`Failed: ${err.message}`, 'error');
  }
}

// ── Add Device Modal ───────────────────────────────────────────────────────
function openAddDeviceModal() {
  ['admin-dev-name','admin-dev-type','admin-dev-serial','admin-dev-user'].forEach(id => {
    const el = document.getElementById(id);
    if (el) el.value = '';
  });
  document.getElementById('add-device-modal').classList.remove('hidden');
}

async function submitAddDevice() {
  const name   = document.getElementById('admin-dev-name').value.trim();
  const type   = document.getElementById('admin-dev-type').value.trim();
  const serial = document.getElementById('admin-dev-serial').value.trim();
  const userId = document.getElementById('admin-dev-user').value.trim();

  if (!name) return toast('Device name is required.', 'error');

  const btn = document.getElementById('add-device-btn');
  btn.disabled = true; btn.textContent = 'Adding…';
  try {
    await apiPost('/api/devices', { name, type, serialNumber: serial, userId: userId || undefined });
    closeModal('add-device-modal');
    toast('Device added!', 'success');
    await loadAdminDevices();
    await loadStats();
  } catch (err) {
    toast(`Failed: ${err.message}`, 'error');
  } finally {
    btn.disabled = false; btn.textContent = 'Add Device';
  }
}

// ── Edit Device Modal ──────────────────────────────────────────────────────
let editingDeviceId = null;

function openEditDeviceModal(device) {
  editingDeviceId = device.id;
  document.getElementById('edit-dev-name').value   = device.name || '';
  document.getElementById('edit-dev-type').value   = device.type || device.category || '';
  document.getElementById('edit-dev-serial').value = device.serialNumber || '';
  document.getElementById('edit-dev-user').value   = device.assignedTo || device.userId || '';
  document.getElementById('edit-device-modal').classList.remove('hidden');
}

async function submitEditDevice() {
  if (!editingDeviceId) return;
  const name   = document.getElementById('edit-dev-name').value.trim();
  const type   = document.getElementById('edit-dev-type').value.trim();
  const serial = document.getElementById('edit-dev-serial').value.trim();
  const userId = document.getElementById('edit-dev-user').value.trim();

  if (!name) return toast('Device name is required.', 'error');

  const btn = document.getElementById('edit-device-btn');
  btn.disabled = true; btn.textContent = 'Saving…';
  try {
    await apiPut(`/api/devices/${editingDeviceId}`, { name, type, serialNumber: serial, userId: userId || undefined });
    closeModal('edit-device-modal');
    toast('Device updated!', 'success');
    await loadAdminDevices();
  } catch (err) {
    toast(`Failed: ${err.message}`, 'error');
  } finally {
    btn.disabled = false; btn.textContent = 'Save Changes';
  }
}

// ── Admin Profile ──────────────────────────────────────────────────────────
async function loadAdminProfile() {
  const body = document.getElementById('admin-profile-body');
  body.innerHTML = `<div style="color:var(--muted)">Loading…</div>`;
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
  } catch {
    const u = adminUser;
    body.innerHTML = `
      <div style="display:flex;align-items:center;gap:20px;margin-bottom:28px">
        <div class="user-avatar" style="width:60px;height:60px;font-size:22px;border-radius:16px">${getUserInitials(u.name || u.sub || '')}</div>
        <div>
          <div style="font-family:'DM Serif Display',serif;font-size:22px">${u.name || u.sub || '—'}</div>
          <div style="color:var(--muted);font-size:13px">${u.email || u.sub || '—'}</div>
        </div>
      </div>
      <div style="color:var(--muted);font-size:13px">Could not load full profile. Showing session data.</div>`;
  }
}

// ── Raise Request Modal ────────────────────────────────────────────────────
async function openRaiseModal() {
  const sel = document.getElementById('raise-device');
  sel.innerHTML = '<option value="">Loading…</option>';
  try {
    const devices = await apiGet('/api/devices');
    sel.innerHTML = (devices || []).length
        ? (devices || []).map(d => `<option value="${d.id}">${d.name} (ID: ${d.id})</option>`).join('')
        : '<option value="">No devices available</option>';
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

  const btn = document.getElementById('raise-submit-btn');
  btn.disabled = true; btn.textContent = 'Submitting…';
  try {
    await apiPost('/api/repairs', { deviceId, issue, description: issue, priority });
    closeModal('raise-modal');
    toast('Repair request raised!', 'success');
    loadOverviewRequests();
    loadStats();
  } catch (err) {
    toast(`Failed: ${err.message}`, 'error');
  } finally {
    btn.disabled = false; btn.textContent = 'Submit Request';
  }
}

// ── Acknowledge Modal ──────────────────────────────────────────────────────
let acknowledgeRequestId = null;

async function openAcknowledgeModal(id) {
  acknowledgeRequestId = id;
  // Load vendors for assignment at acknowledge time
  const sel = document.getElementById('ack-vendor');
  sel.innerHTML = '<option value="">— Skip assignment (assign later) —</option>';
  try {
    const vendors = await apiGet('/api/vendors');
    (vendors || []).forEach(v => {
      const opt = document.createElement('option');
      opt.value = v.id;
      opt.textContent = `${v.name || v.email} (ID: ${v.id})`;
      sel.appendChild(opt);
    });
  } catch {
    // Not critical — vendor assignment is optional here
  }
  document.getElementById('ack-modal').classList.remove('hidden');
}

async function submitAcknowledge() {
  if (!acknowledgeRequestId) return;
  const vendorId = document.getElementById('ack-vendor').value;

  const btn = document.getElementById('ack-submit-btn');
  btn.disabled = true; btn.textContent = 'Acknowledging…';
  try {
    await apiPatch(`/api/repairs/${acknowledgeRequestId}/acknowledge`, vendorId ? { vendorId } : {});
    // Optionally assign vendor separately if the acknowledge endpoint doesn't support it
    if (vendorId) {
      await apiPatch(`/api/repairs/${acknowledgeRequestId}/assign`, { vendorId }).catch(() => {});
    }
    closeModal('ack-modal');
    toast('Request acknowledged!', 'success');
    loadOverviewRequests();
    loadAdminRequests().catch(() => {});
    loadStats();
  } catch (err) {
    toast(`Failed: ${err.message}`, 'error');
  } finally {
    btn.disabled = false; btn.textContent = 'Acknowledge';
  }
}

// ── Assign Vendor Modal ────────────────────────────────────────────────────
let assignRequestId = null;

async function openAssignModal(id) {
  assignRequestId = id;
  const sel = document.getElementById('assign-vendor');
  sel.innerHTML = '<option value="">Loading vendors…</option>';
  try {
    const vendors = await apiGet('/api/vendors');
    sel.innerHTML = (vendors || []).length
        ? (vendors || []).map(v => `<option value="${v.id}">${v.name || v.email} (ID: ${v.id})</option>`).join('')
        : '<option value="">No vendors available</option>';
  } catch {
    sel.innerHTML = '<option value="">Failed to load vendors</option>';
  }
  document.getElementById('assign-modal').classList.remove('hidden');
}

async function submitAssignVendor() {
  if (!assignRequestId) return;
  const vendorId = document.getElementById('assign-vendor').value;
  if (!vendorId) return toast('Please select a vendor.', 'error');

  const btn = document.getElementById('assign-submit-btn');
  btn.disabled = true; btn.textContent = 'Assigning…';
  try {
    await apiPatch(`/api/repairs/${assignRequestId}/assign`, { vendorId });
    closeModal('assign-modal');
    toast('Vendor assigned!', 'success');
    loadAdminRequests();
    loadOverviewRequests();
  } catch (err) {
    toast(`Failed: ${err.message}`, 'error');
  } finally {
    btn.disabled = false; btn.textContent = 'Assign';
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

  const btn = document.getElementById('close-submit-btn');
  btn.disabled = true; btn.textContent = 'Closing…';
  try {
    await apiPatch(`/api/repairs/${closeRequestId}/close`, { remark });
    closeModal('close-modal');
    toast('Request marked as Closed!', 'success');
    loadOverviewRequests();
    loadAdminRequests().catch(() => {});
    loadStats();
  } catch (err) {
    toast(`Failed: ${err.message}`, 'error');
  } finally {
    btn.disabled = false; btn.textContent = 'Mark as Closed';
  }
}

// ── Helpers ────────────────────────────────────────────────────────────────
function closeModal(id) {
  document.getElementById(id).classList.add('hidden');
}

document.querySelectorAll('.modal-overlay').forEach(overlay => {
  overlay.addEventListener('click', e => {
    if (e.target === overlay) overlay.classList.add('hidden');
  });
});
