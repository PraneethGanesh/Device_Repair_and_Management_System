// ── API Base ───────────────────────────────────────────────────────────────
const API = '';  // Same host as the API gateway static app.
const ROLE_ROUTES = {
  admin: '',
  vendor: '/vendor_dashboard',
  company_admin: '',
  company_employee: '',
  employee: ''
};

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
      toggleVendorFields();
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

function toggleVendorFields() {
  const fields = document.getElementById('vendor-fields');
  if (!fields) return;
  fields.classList.toggle('hidden', getActiveRole('register') !== 'vendor');
}

function normalizeRole(role) {
  return (role || '').toLowerCase();
}

function saveSession(data, fallbackRole) {
  const token = data.token || data.jwt || data.accessToken || data.access_token;
  if (!token) return null;
  const claims = decodeJwt(token) || {};
  const role = normalizeRole(data.role || claims.role || fallbackRole);
  const session = {
    token,
    id: data.id || claims.userId || claims.id || '',
    name: data.name || claims.name || claims.sub || '',
    email: data.email || claims.sub || '',
    role
  };
  localStorage.setItem('token', token);
  localStorage.setItem('role', role);
  localStorage.setItem('userInfo', JSON.stringify(session));
  return session;
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
  const stored = JSON.parse(localStorage.getItem('userInfo') || '{}');
  const role = normalizeRole(stored.role || claims?.role || localStorage.getItem('role'));
  window.location.href = ROLE_ROUTES[role] || '';
}

// ── Login ──────────────────────────────────────────────────────────────────
async function handleLogin() {
  hideMsg('login-error');
  const expectedRole = getActiveRole('login');
  const email = document.getElementById('login-email').value.trim();
  const pass  = document.getElementById('login-pass').value;

  if (!email || !pass) return showError('login-error', 'Please fill in all fields.');

  document.getElementById('login-btn').disabled = true;
  document.getElementById('login-btn').querySelector('span').textContent = 'Signing in…';

  try {
    const res = await fetch(`${API}/api/users/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email, password: pass })
    });

    const data = await res.json().catch(() => ({}));

    if (!res.ok) {
      showError('login-error', data.message || data.error || 'Invalid credentials.');
      return;
    }

    const session = saveSession(data, expectedRole);
    if (!session) {
      showError('login-error', 'Server did not return a token.');
      return;
    }

    if (expectedRole && session.role !== expectedRole) {
      showError('login-error', `This account is ${session.role.replace('_', ' ')}, not ${expectedRole.replace('_', ' ')}.`);
      localStorage.clear();
      return;
    }
    routeByRole(session.token);
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
  const companyName = document.getElementById('reg-company')?.value.trim();
  const gstNumber = document.getElementById('reg-gst')?.value.trim();
  const phone = document.getElementById('reg-phone')?.value.trim();
  const address = document.getElementById('reg-address')?.value.trim();

  if (!name || !email || !pass) return showError('register-error', 'Please fill in all fields.');
  if (pass.length < 8) return showError('register-error', 'Password must be at least 8 characters.');
  if (role === 'vendor' && (!companyName || !gstNumber || !phone || !address)) {
    return showError('register-error', 'Please fill in all vendor company details.');
  }

  document.getElementById('register-btn').disabled = true;
  document.getElementById('register-btn').querySelector('span').textContent = 'Creating…';

  try {
    const res = await fetch(`${API}/api/users/register/${role}`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ name, email, password: pass })
    });

    const data = await res.json().catch(() => ({}));

    if (!res.ok) {
      showError('register-error', data.message || data.error || 'Registration failed.');
      return;
    }

    const session = saveSession(data, role);

    if (role === 'vendor' && session?.token) {
      const vendorRes = await fetch(`${API}/api/vendors/register`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${session.token}`
        },
        body: JSON.stringify({ companyName, gstNumber, phone, address })
      });
      if (!vendorRes.ok) {
        const vendorErr = await vendorRes.json().catch(() => ({}));
        showError('register-error', vendorErr.message || vendorErr.error || 'User created, but vendor profile could not be created.');
        return;
      }
    }

    const el = document.getElementById('register-success');
    el.textContent = role === 'vendor'
      ? 'Vendor account created. Redirecting to dashboard…'
      : 'Account created. Redirecting to dashboard…';
    el.classList.remove('hidden');

    setTimeout(() => {
      if (session?.token) routeByRole(session.token);
      else document.querySelector('.tab[data-tab="login"]').click();
    }, 900);
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

toggleVendorFields();

// ── Enter key submit ───────────────────────────────────────────────────────
document.addEventListener('keydown', e => {
  if (e.key !== 'Enter') return;
  if (document.getElementById('login').classList.contains('active')) handleLogin();
  else handleRegister();
});
