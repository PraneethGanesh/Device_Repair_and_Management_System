// ── Shared API utilities ────────────────────────────────────────────────────
const API_BASE = ''; // Same-host gateway. Change to http://localhost:8080 for local dev

function getToken() { return localStorage.getItem('token') || ''; }

function authHeaders() {
  return {
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${getToken()}`
  };
}

async function apiGet(path) {
  const res = await fetch(API_BASE + path, { headers: authHeaders() });
  if (res.status === 401) { logout(); throw new Error('Unauthorized'); }
  if (!res.ok) {
    const e = await res.json().catch(() => ({}));
    throw new Error(e.message || e.error || `HTTP ${res.status}`);
  }
  return res.json();
}

async function apiPost(path, body) {
  const res = await fetch(API_BASE + path, {
    method: 'POST',
    headers: authHeaders(),
    body: JSON.stringify(body)
  });
  if (res.status === 401) { logout(); throw new Error('Unauthorized'); }
  if (!res.ok) {
    const e = await res.json().catch(() => ({}));
    throw new Error(e.message || e.error || `HTTP ${res.status}`);
  }
  return res.json().catch(() => ({}));
}

async function apiPut(path, body) {
  const res = await fetch(API_BASE + path, {
    method: 'PUT',
    headers: authHeaders(),
    body: body !== undefined ? JSON.stringify(body) : undefined
  });
  if (res.status === 401) { logout(); throw new Error('Unauthorized'); }
  if (!res.ok) {
    const e = await res.json().catch(() => ({}));
    throw new Error(e.message || e.error || `HTTP ${res.status}`);
  }
  return res.json().catch(() => ({}));
}

async function apiPatch(path, body) {
  const res = await fetch(API_BASE + path, {
    method: 'PATCH',
    headers: authHeaders(),
    body: body !== undefined ? JSON.stringify(body) : undefined
  });
  if (res.status === 401) { logout(); throw new Error('Unauthorized'); }
  if (!res.ok) {
    const e = await res.json().catch(() => ({}));
    throw new Error(e.message || e.error || `HTTP ${res.status}`);
  }
  return res.json().catch(() => ({}));
}

// ── JWT decode ──────────────────────────────────────────────────────────────
function decodeJwt(token) {
  try {
    const payload = token.split('.')[1];
    return JSON.parse(atob(payload.replace(/-/g, '+').replace(/_/g, '/')));
  } catch { return null; }
}

// ── Auth guard ──────────────────────────────────────────────────────────────
function guardRole(expectedRole) {
  const token = getToken();
  if (!token) { window.location.href = 'index.html'; return false; }
  const claims = decodeJwt(token);
  if (!claims || claims.exp * 1000 < Date.now()) { logout(); return false; }
  const role = (claims.role || '').toLowerCase();
  if (expectedRole && role !== expectedRole.toLowerCase()) {
    window.location.href = 'index.html';
    return false;
  }
  return claims;
}

// ── Current user ────────────────────────────────────────────────────────────
function getCurrentUser() {
  const token = getToken();
  return decodeJwt(token) || {};
}

function getUserInitials(name) {
  if (!name) return '?';
  return name.split(' ').slice(0, 2).map(w => w[0].toUpperCase()).join('');
}

// ── Logout ──────────────────────────────────────────────────────────────────
function logout() {
  localStorage.clear();
  window.location.href = 'index.html';
}

// ── Toast ────────────────────────────────────────────────────────────────────
function toast(msg, type = 'info') {
  let container = document.getElementById('toast-container');
  if (!container) {
    container = document.createElement('div');
    container.id = 'toast-container';
    document.body.appendChild(container);
  }
  const el = document.createElement('div');
  el.className = `toast ${type}`;
  el.textContent = msg;
  container.appendChild(el);
  setTimeout(() => { el.remove(); }, 3500);
}

// ── Page routing (tabs) ──────────────────────────────────────────────────────
function showPage(id) {
  document.querySelectorAll('.page').forEach(p => p.classList.remove('active'));
  document.querySelectorAll('.nav-item').forEach(n => n.classList.remove('active'));
  const page = document.getElementById(id);
  if (page) page.classList.add('active');
  const navItem = document.querySelector(`.nav-item[data-page="${id}"]`);
  if (navItem) navItem.classList.add('active');
  document.querySelector('.topbar-title').textContent = navItem?.dataset.title || id;
}

// ── Format date ──────────────────────────────────────────────────────────────
function fmtDate(str) {
  if (!str) return '—';
  try { return new Date(str).toLocaleDateString('en-IN', { day: 'numeric', month: 'short', year: 'numeric' }); }
  catch { return str; }
}

function fmtDateTime(str) {
  if (!str) return '—';
  try { return new Date(str).toLocaleString('en-IN', { day: 'numeric', month: 'short', hour: '2-digit', minute: '2-digit' }); }
  catch { return str; }
}

// ── Status badge HTML ─────────────────────────────────────────────────────────
function statusBadge(status) {
  const map = {
    PENDING: 'status-pending',
    ACKNOWLEDGED: 'status-acknowledged',
    IN_PROGRESS: 'status-inprogress',
    INPROGRESS: 'status-inprogress',
    COMPLETED: 'status-completed',
    CLOSED: 'status-closed',
    ACTIVE: 'status-active',
  };
  const cls = map[(status || '').toUpperCase()] || '';
  const label = (status || '').replace(/_/g, ' ');
  return `<span class="status ${cls}">${label}</span>`;
}

// ── Table loader helper ───────────────────────────────────────────────────────
function tableLoading(tbodyId, cols) {
  const tbody = document.getElementById(tbodyId);
  if (tbody) tbody.innerHTML = `<tr><td colspan="${cols}" style="text-align:center;padding:32px;color:var(--muted)">
    <span class="spinner"></span> Loading…
  </td></tr>`;
}

function tableEmpty(tbodyId, cols, message) {
  const tbody = document.getElementById(tbodyId);
  if (tbody) tbody.innerHTML = `<tr><td colspan="${cols}" style="text-align:center;padding:32px;color:var(--muted)">${message}</td></tr>`;
}
