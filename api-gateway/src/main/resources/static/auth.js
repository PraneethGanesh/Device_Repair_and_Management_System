// ── API Base ───────────────────────────────────────────────────────────────
const API = 'http://localhost:8085';  // Empty = same host (gateway). Change to http://localhost:8080 for local dev

// ── Tab switching ──────────────────────────────────────────────────────────
document.querySelectorAll('.tab').forEach(tab => {
  tab.addEventListener('click', () => {
    document.querySelectorAll('.tab').forEach(t => t.classList.remove('active'));
    document.querySelectorAll('.form-body').forEach(f => f.classList.remove('active'));
    tab.classList.add('active');
    document.getElementById(tab.dataset.tab).classList.add('active');
  });
});

// ── Role selection ─────────────────────────────────────────────────────────
document.querySelectorAll('.role-select').forEach(group => {
  group.querySelectorAll('.role-btn').forEach(btn => {
    btn.addEventListener('click', () => {
      group.querySelectorAll('.role-btn').forEach(b => b.classList.remove('active'));
      btn.classList.add('active');
    });
  });
});

function getActiveRole(formId) {
  const group = document.getElementById(formId).querySelector('.role-select');
  return group.querySelector('.role-btn.active')?.dataset.role || 'user';
}

// ── Utilities ──────────────────────────────────────────────────────────────
function togglePass(id, btn) {
  const input = document.getElementById(id);
  input.type = input.type === 'password' ? 'text' : 'password';
}

function showError(id, msg) {
  const el = document.getElementById(id);
  el.textContent = msg;
  el.classList.remove('hidden');
}

function hideMsg(id) {
  document.getElementById(id)?.classList.add('hidden');
}

function setLoading(btnId, loading) {
  const btn = document.getElementById(btnId);
  btn.disabled = loading;
  btn.querySelector('span').textContent = loading ? 'Please wait…' : btn.dataset.label || btn.querySelector('span').textContent;
}

// ── JWT decode (no verify — just read payload) ─────────────────────────────
function decodeJwt(token) {
  try {
    const payload = token.split('.')[1];
    return JSON.parse(atob(payload.replace(/-/g, '+').replace(/_/g, '/')));
  } catch {
    return null;
  }
}

// ── Route by role ──────────────────────────────────────────────────────────
function routeByRole(token) {
  const claims = decodeJwt(token);
  if (!claims) return;
  const role = (claims.role || '').toLowerCase();
  if (role === 'admin')  return (window.location.href = 'admin.html');
  if (role === 'vendor') return (window.location.href = 'vendor.html');
  return (window.location.href = 'employee.html');
}

// ── Login ──────────────────────────────────────────────────────────────────
async function handleLogin() {
  hideMsg('login-error');
  const role  = getActiveRole('login');
  const email = document.getElementById('login-email').value.trim();
  const pass  = document.getElementById('login-pass').value;

  if (!email || !pass) return showError('login-error', 'Please fill in all fields.');

  // Endpoint differs per role
  const endpoint = role === 'vendor'
    ? `${API}/api/vendors/login`
    : `${API}/api/users/login`;

  document.getElementById('login-btn').disabled = true;
  document.getElementById('login-btn').querySelector('span').textContent = 'Signing in…';

  try {
    const res = await fetch(endpoint, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email, password: pass })
    });

    const data = await res.json().catch(() => ({}));

    if (!res.ok) {
      showError('login-error', data.message || data.error || 'Invalid credentials.');
      return;
    }

    // Support token in different response shapes
    const token = data.token || data.jwt || data.accessToken || data.access_token;
    if (!token) {
      showError('login-error', 'Server did not return a token.');
      return;
    }

    localStorage.setItem('token', token);
    localStorage.setItem('role', role);

    // Try to persist user info
    const claims = decodeJwt(token);
    if (claims) localStorage.setItem('userInfo', JSON.stringify(claims));

    routeByRole(token);
  } catch (err) {
    showError('login-error', 'Network error. Is the gateway running?');
  } finally {
    document.getElementById('login-btn').disabled = false;
    document.getElementById('login-btn').querySelector('span').textContent = 'Sign In';
  }
}

// ── Register ───────────────────────────────────────────────────────────────
async function handleRegister() {
  hideMsg('register-error');
  hideMsg('register-success');
  const role  = getActiveRole('register');
  const name  = document.getElementById('reg-name').value.trim();
  const email = document.getElementById('reg-email').value.trim();
  const pass  = document.getElementById('reg-pass').value;

  if (!name || !email || !pass) return showError('register-error', 'Please fill in all fields.');
  if (pass.length < 8) return showError('register-error', 'Password must be at least 8 characters.');

  const endpoint = role === 'vendor'
    ? `${API}/api/vendors/register`
    : `${API}/api/users/register`;

  document.getElementById('register-btn').disabled = true;
  document.getElementById('register-btn').querySelector('span').textContent = 'Creating…';

  try {
    const res = await fetch(endpoint, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ name, email, password: pass })
    });

    const data = await res.json().catch(() => ({}));

    if (!res.ok) {
      showError('register-error', data.message || data.error || 'Registration failed.');
      return;
    }

    const el = document.getElementById('register-success');
    el.textContent = 'Account created! Redirecting to login…';
    el.classList.remove('hidden');

    setTimeout(() => {
      document.querySelector('.tab[data-tab="login"]').click();
    }, 1500);
  } catch (err) {
    showError('register-error', 'Network error. Is the gateway running?');
  } finally {
    document.getElementById('register-btn').disabled = false;
    document.getElementById('register-btn').querySelector('span').textContent = 'Create Account';
  }
}

// ── Guard: redirect if already logged in ──────────────────────────────────
(function() {
  const token = localStorage.getItem('token');
  if (token) {
    const claims = decodeJwt(token);
    if (claims && claims.exp * 1000 > Date.now()) {
      routeByRole(token);
    } else {
      localStorage.clear();
    }
  }
})();

// ── Enter key submit ───────────────────────────────────────────────────────
document.addEventListener('keydown', e => {
  if (e.key !== 'Enter') return;
  if (document.getElementById('login').classList.contains('active')) handleLogin();
  else handleRegister();
});
