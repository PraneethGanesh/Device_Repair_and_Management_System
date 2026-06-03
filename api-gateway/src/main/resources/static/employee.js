// ── Guard ──────────────────────────────────────────────────────────────────
// Accept 'user' or 'employee' role
const empUser = (function() {
  const token = localStorage.getItem('token');
  if (!token) { window.location.href = 'index.html'; return null; }
  const claims = decodeJwt(token);
  if (!claims || claims.exp * 1000 < Date.now()) { logout(); return null; }
  const role = (claims.role || '').toLowerCase();
  if (role !== 'user' && role !== 'employee') { window.location.href = 'index.html'; return null; }
  return claims;
})();

// ── Init ───────────────────────────────────────────────────────────────────
document.addEventListener('DOMContentLoaded', () => {
  if (!empUser) return;
  const name  = empUser.name || empUser.sub || 'Employee';
  const email = empUser.email || empUser.sub || '';
  document.getElementById('emp-name').textContent   = name;
  document.getElementById('emp-email').textContent  = email;
  document.getElementById('emp-avatar').textContent = getUserInitials(name);

  loadEmpOverview();
});

// ── Overview ───────────────────────────────────────────────────────────────
async function loadEmpOverview() {
  tableLoading('emp-overview-body', 4);
  try {
    const [devices, requests] = await Promise.all([
      apiGet('/api/users/devices'),
      apiGet('/api/users/myRequests')
    ]);

    const devs = devices || [];
    const reqs = requests || [];
    const open   = reqs.filter(r => !['CLOSED','COMPLETED'].includes(r.status)).length;
    const closed = reqs.filter(r => ['CLOSED','COMPLETED'].includes(r.status)).length;

    document.getElementById('emp-stat-devices').textContent = devs.length;
    document.getElementById('emp-stat-pending').textContent = open;
    document.getElementById('emp-stat-closed').textContent  = closed;

    // Recent 5
    const recent = reqs.slice(0, 5);
    if (!recent.length) {
      tableEmpty('emp-overview-body', 4, 'No requests yet. Raise one to get started.');
      return;
    }
    document.getElementById('emp-overview-body').innerHTML = recent.map(r => `
      <tr>
        <td><span style="font-family:'DM Mono',monospace;font-size:12px">#${r.requestId}</span></td>
        <td><span style="font-family:'DM Mono',monospace;font-size:12px">${r.deviceId}</span></td>
        <td>${r.issueDescription || '—'}</td>
        <td>${statusBadge(r.status)}</td>
      </tr>`).join('');
  } catch (err) {
    tableEmpty('emp-overview-body', 4, `Error: ${err.message}`);
  }
}

// ── Devices ────────────────────────────────────────────────────────────────
async function loadEmpDevices() {
  tableLoading('emp-devices-body', 6);
  try {
    const devices = await apiGet('/api/users/devices').catch(() => apiGet('/api/devices'));
    const tbody = document.getElementById('emp-devices-body');
    if (!(devices || []).length) {
      tbody.innerHTML = `<tr><td colspan="6"><div class="empty-state"><div class="empty-icon">🖥️</div><div class="empty-title">No devices assigned</div><div class="empty-sub">Contact your admin to assign devices</div></div></td></tr>`;
      return;
    }
    tbody.innerHTML = (devices || []).map(d => `
      <tr>
        <td>${d.DeviceName || '—'}</td>
        <td>${d.deviceType || d.category || '—'}</td>
        <td>${d.status || 'ACTIVE'}</td>
        <td>${d.warrantyExpiry || 'Not known'}</td>
        <td><button class="btn btn-sm btn-secondary" onclick="openRaiseModalForDevice('${d.id}','${d.name}')">Raise Request</button></td>
      </tr>`).join('');
  } catch (err) {
    tableEmpty('emp-devices-body', 6, `Failed to load: ${err.message}`);
  }
}

// ── Requests ───────────────────────────────────────────────────────────────
async function loadEmpRequests() {
  tableLoading('emp-requests-body', 5);
  try {
    const requests = await apiGet('/api/users/myRequests');
    const tbody = document.getElementById('emp-requests-body');
    if (!(requests || []).length) {
      tbody.innerHTML = `<tr><td colspan="5"><div class="empty-state"><div class="empty-icon">📋</div><div class="empty-title">No requests raised</div><div class="empty-sub">Use the "Raise Request" button above</div></div></td></tr>`;
      return;
    }
    tbody.innerHTML = (requests || []).map(r => `
      <tr>
        <td><span style="font-family:'DM Mono',monospace;font-size:12px">#${r.requestId}</span></td>
        <td><span style="font-family:'DM Mono',monospace;font-size:12px">${r.deviceId}</span></td>
        <td>${r.issueDescription || '—'}</td>
        <td>${statusBadge(r.status)}</td>
        <td><span style="font-family:'DM Mono',monospace;font-size:12px">${r.vendorId ?? '—'}</span></td>
      </tr>`).join('');
  } catch (err) {
    tableEmpty('emp-requests-body', 5, `Failed to load: ${err.message}`);
  }
}

// ── Profile ────────────────────────────────────────────────────────────────
async function loadEmpProfile() {
  const body = document.getElementById('emp-profile-body');
  try {
    const profile = await apiGet('/api/users/profile');
    const u = profile || empUser;
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
        <div class="detail-item"><div class="detail-key">Role</div><div class="detail-value">Employee</div></div>
        <div class="detail-item"><div class="detail-key">Department</div><div class="detail-value">${u.department || '—'}</div></div>
      </div>`;
  } catch {
    const u = empUser;
    body.innerHTML = `
      <div style="display:flex;align-items:center;gap:20px;margin-bottom:28px">
        <div class="user-avatar" style="width:60px;height:60px;font-size:22px;border-radius:16px">${getUserInitials(u.name || u.sub || '')}</div>
        <div>
          <div style="font-family:'DM Serif Display',serif;font-size:22px">${u.name || u.sub || '—'}</div>
          <div style="color:var(--muted);font-size:13px">${u.email || u.sub || '—'}</div>
        </div>
      </div>
      <div style="color:var(--muted);font-size:13px">Showing JWT session data. Could not reach profile endpoint.</div>`;
  }
}

// ── Raise Modal ────────────────────────────────────────────────────────────
async function openRaiseModal() {
  await populateDeviceDropdown();
  document.getElementById('raise-issue').value = '';
  document.getElementById('raise-modal').classList.remove('hidden');
}

async function openRaiseModalForDevice(deviceId, deviceName) {
  await populateDeviceDropdown();
  const sel = document.getElementById('raise-device');
  sel.value = deviceId;
  document.getElementById('raise-issue').value = '';
  document.getElementById('raise-modal').classList.remove('hidden');
}

async function populateDeviceDropdown() {
  const sel = document.getElementById('raise-device');
  sel.innerHTML = '<option>Loading…</option>';
  try {
    const devices = await apiGet('/api/users/devices').catch(() => apiGet('/api/devices'));
   sel.innerHTML = (devices || []).map(d =>
     `<option value="${d.deviceId}">${d.deviceName || d.deviceId}</option>`
   ).join('');
    if (!(devices || []).length) sel.innerHTML = '<option value="">No devices found</option>';
  } catch { sel.innerHTML = '<option value="">Failed to load</option>'; }
}

async function submitRaiseRequest() {
  const deviceId        = document.getElementById('raise-device').value;
  const issueDescription = document.getElementById('raise-issue').value.trim();
  const urgent          = document.getElementById('raise-urgent').checked;

  if (!deviceId)          return toast('Please select a device.', 'error');
  if (!issueDescription)  return toast('Please describe the issue.', 'error');

  try {
    await apiPost('/api/users/request/raise', { deviceId, issueDescription, urgent });
    closeModal('raise-modal');
    toast('Request submitted!', 'success');
    loadEmpOverview();
    if (document.getElementById('page-requests').classList.contains('active')) loadEmpRequests();
  } catch (err) {
    toast(`Failed: ${err.message}`, 'error');
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
