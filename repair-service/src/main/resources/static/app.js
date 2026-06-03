const STORAGE_KEY = "drms-console-state";

const state = {
  baseUrl: "http://localhost:8085",
  token: "",
  user: null,
  role: "",
};

const $ = (selector) => document.querySelector(selector);
const $$ = (selector) => Array.from(document.querySelectorAll(selector));

function saveState() {
  localStorage.setItem(STORAGE_KEY, JSON.stringify(state));
}

function loadState() {
  const saved = localStorage.getItem(STORAGE_KEY);
  if (!saved) return;
  try {
    Object.assign(state, JSON.parse(saved));
  } catch {
    localStorage.removeItem(STORAGE_KEY);
  }
}

function setAlert(message, type = "ok") {
  const alert = $("#alert");
  alert.textContent = message;
  alert.classList.toggle("error", type === "error");
  alert.classList.remove("hidden");
  window.clearTimeout(setAlert.timer);
  setAlert.timer = window.setTimeout(() => alert.classList.add("hidden"), 5200);
}

function updateSession() {
  $("#baseUrl").value = state.baseUrl;
  $("#sessionName").textContent = state.user?.name || state.user?.email || "Guest";
  $("#sessionRole").textContent = state.role || "No active token";
  $("#metricSession").textContent = state.role || "Guest";
}

function endpoint(path) {
  return `${state.baseUrl.replace(/\/$/, "")}${path}`;
}

async function api(path, options = {}) {
  const headers = {
    "Content-Type": "application/json",
    ...(options.headers || {}),
  };

  if (state.token) {
    headers.Authorization = `Bearer ${state.token}`;
  }

  const response = await fetch(endpoint(path), {
    ...options,
    headers,
  });

  const text = await response.text();
  let body = text;
  if (text) {
    try {
      body = JSON.parse(text);
    } catch {
      body = text;
    }
  }

  $("#responseBox").textContent = JSON.stringify(body || { status: response.status }, null, 2);

  if (!response.ok) {
    const message = typeof body === "string" ? body : body?.error || body?.message || response.statusText;
    throw new Error(message || `HTTP ${response.status}`);
  }

  return body;
}

function formData(form) {
  const data = new FormData(form);
  return Object.fromEntries(data.entries());
}

function numberValue(value) {
  return Number(value);
}

function normalizeAuth(body, fallbackRole) {
  if (typeof body === "string") {
    return { token: body, role: fallbackRole, user: { name: fallbackRole, email: "" } };
  }
  return {
    token: body.token,
    role: body.role || fallbackRole,
    user: body,
  };
}

async function handleAuth(path, payload, fallbackRole) {
  const body = await api(path, {
    method: "POST",
    body: JSON.stringify(payload),
  });
  const auth = normalizeAuth(body, fallbackRole);
  state.token = auth.token;
  state.role = auth.role;
  state.user = auth.user;
  saveState();
  updateSession();
  setAlert("Signed in through API gateway.");
}

function renderTable(target, rows, columns) {
  const el = $(target);
  if (!Array.isArray(rows) || rows.length === 0) {
    el.innerHTML = '<div class="empty-state">No records returned.</div>';
    return;
  }

  const head = columns.map((col) => `<th>${col.label}</th>`).join("");
  const body = rows.map((row) => {
    const cells = columns.map((col) => {
      const raw = col.value(row);
      const value = raw === undefined || raw === null || raw === "" ? "-" : raw;
      return `<td>${col.status ? `<span class="status-pill">${escapeHtml(value)}</span>` : escapeHtml(value)}</td>`;
    }).join("");
    return `<tr>${cells}</tr>`;
  }).join("");

  el.innerHTML = `<table><thead><tr>${head}</tr></thead><tbody>${body}</tbody></table>`;
}

function escapeHtml(value) {
  return String(value)
    .replaceAll("&", "&amp;")
    .replaceAll("<", "&lt;")
    .replaceAll(">", "&gt;")
    .replaceAll('"', "&quot;")
    .replaceAll("'", "&#039;");
}

const repairColumns = [
  { label: "ID", value: (r) => r.requestId },
  { label: "Device", value: (r) => r.deviceId },
  { label: "Raised By", value: (r) => r.raisedBy },
  { label: "Vendor", value: (r) => r.vendorId },
  { label: "Admin", value: (r) => r.adminId },
  { label: "Status", value: (r) => r.status, status: true },
  { label: "Urgent", value: (r) => r.urgent ? "YES" : "NO" },
  { label: "Issue", value: (r) => r.issueDescription },
];

const deviceColumns = [
  { label: "ID", value: (d) => d.deviceId || d.id },
  { label: "Name", value: (d) => d.deviceName || d.DeviceName },
  { label: "Type", value: (d) => d.deviceType },
  { label: "Status", value: (d) => d.deviceStatus || d.status, status: true },
  { label: "Vendor", value: (d) => d.vendorId },
  { label: "Assigned To", value: (d) => d.assignedToId || d.assignedtoId },
  { label: "Warranty", value: (d) => d.warrantyExpiry },
];

const userColumns = [
  { label: "ID", value: (u) => u.id },
  { label: "Name", value: (u) => u.name },
  { label: "Email", value: (u) => u.email },
  { label: "Role", value: (u) => u.role, status: true },
  { label: "Department", value: (u) => u.department },
];

async function loadRepairs() {
  const rows = await api("/api/repairs");
  renderTable("#repairsTable", rows, repairColumns);
  renderTable("#recentRepairs", rows.slice(0, 8), repairColumns);
  $("#metricRepairs").textContent = Array.isArray(rows) ? rows.length : "-";
}

async function loadDevices() {
  const rows = await api("/api/devices");
  renderTable("#devicesTable", rows, deviceColumns);
  $("#metricDevices").textContent = Array.isArray(rows) ? rows.length : "-";
}

async function loadUsers() {
  const rows = await api("/api/users/all");
  renderTable("#usersTable", rows, userColumns);
  $("#metricUsers").textContent = Array.isArray(rows) ? rows.length : "-";
}

async function loadAvailableRepairs() {
  const rows = await api("/api/repairs/available");
  renderTable("#vendorTable", rows, repairColumns);
}

function bindViews() {
  $$(".nav-item").forEach((button) => {
    button.addEventListener("click", () => showView(button.dataset.view));
  });
  $$("[data-jump]").forEach((button) => {
    button.addEventListener("click", () => showView(button.dataset.jump));
  });
}

function showView(view) {
  $$(".nav-item").forEach((item) => item.classList.toggle("active", item.dataset.view === view));
  $$(".view").forEach((panel) => panel.classList.toggle("active", panel.id === view));
  $("#viewTitle").textContent = $(`.nav-item[data-view="${view}"]`)?.textContent || "Dashboard";
}

function bindForms() {
  $("#saveGatewayBtn").addEventListener("click", () => {
    state.baseUrl = $("#baseUrl").value.trim() || "http://localhost:8085";
    saveState();
    setAlert("Gateway URL saved.");
  });

  $("#logoutBtn").addEventListener("click", () => {
    state.token = "";
    state.user = null;
    state.role = "";
    saveState();
    updateSession();
    setAlert("Signed out.");
  });

  $("#userLoginForm").addEventListener("submit", withForm(async (form) => {
    await handleAuth("/api/users/login", formData(form), "EMPLOYEE");
  }));

  $("#vendorLoginForm").addEventListener("submit", withForm(async (form) => {
    await handleAuth("/api/vendors/login", formData(form), "VENDOR");
  }));

  $("#userRegisterForm").addEventListener("submit", withForm(async (form) => {
    await handleAuth("/api/users/register", formData(form), formData(form).role);
  }));

  $("#vendorRegisterForm").addEventListener("submit", withForm(async (form) => {
    await handleAuth("/api/vendors/register", formData(form), "VENDOR");
  }));

  $("#addDeviceForm").addEventListener("submit", withForm(async (form) => {
    const data = formData(form);
    await api(`/api/devices/${numberValue(data.vendorId)}`, {
      method: "POST",
      body: JSON.stringify({
        deviceName: data.deviceName,
        deviceType: data.deviceType,
        warrantyExpiry: data.warrantyExpiry,
      }),
    });
    setAlert("Device added.");
    await loadDevices();
  }));

  $("#assignDeviceForm").addEventListener("submit", withForm(async (form) => {
    const data = formData(form);
    await api("/api/devices/assign", {
      method: "POST",
      body: JSON.stringify({
        deviceId: numberValue(data.deviceId),
        userId: numberValue(data.userId),
      }),
    });
    setAlert("Device assigned.");
    await loadDevices();
  }));

  $("#raiseRepairForm").addEventListener("submit", withForm(async (form) => {
    const data = formData(form);
    await api(`/api/repairs/${numberValue(data.userId)}/${numberValue(data.vendorId)}`, {
      method: "POST",
      body: JSON.stringify({
        deviceId: numberValue(data.deviceId),
        issueDescription: data.issueDescription,
        urgent: Boolean(data.urgent),
      }),
    });
    setAlert("Repair request raised.");
    await loadRepairs();
  }));

  $("#ackRepairForm").addEventListener("submit", withForm(async (form) => {
    const data = formData(form);
    await api(`/api/repairs/${numberValue(data.repairId)}/acknowledge?adminId=${numberValue(data.adminId)}`, {
      method: "PUT",
    });
    setAlert("Repair acknowledged.");
    await loadRepairs();
  }));

  $("#closeRepairForm").addEventListener("submit", withForm(async (form) => {
    const data = formData(form);
    await api(`/api/repairs/${numberValue(data.repairId)}/close`, { method: "PUT" });
    setAlert("Repair closed.");
    await loadRepairs();
  }));

  $("#roleForm").addEventListener("submit", withForm(async (form) => {
    const data = formData(form);
    await api(`/api/users/${numberValue(data.id)}/role`, {
      method: "PUT",
      body: JSON.stringify({ role: data.role }),
    });
    setAlert("Role updated.");
    await loadUsers();
  }));

  $("#vendorProgressForm").addEventListener("submit", withForm(async (form) => {
    const data = formData(form);
    await api(`/api/vendors/mark/progress/${numberValue(data.repairId)}`, { method: "PUT" });
    setAlert("Repair marked in progress.");
  }));

  $("#vendorCompleteForm").addEventListener("submit", withForm(async (form) => {
    const data = formData(form);
    await api(`/api/vendors/mark/complete/${numberValue(data.repairId)}`, { method: "PUT" });
    setAlert("Repair marked completed.");
  }));

  $("#vendorRepairsForm").addEventListener("submit", withForm(async (form) => {
    const data = formData(form);
    const rows = await api(`/api/repairs/vendor/${numberValue(data.vendorId)}`);
    renderTable("#vendorTable", rows, repairColumns);
  }));

  $("#healthBtn").addEventListener("click", async () => {
    try {
      const body = await fetch(endpoint("/actuator/health")).then((res) => res.json());
      $("#responseBox").textContent = JSON.stringify(body, null, 2);
      setAlert("Gateway health loaded.");
    } catch (error) {
      setAlert(error.message, "error");
    }
  });

  $$("[data-action]").forEach((button) => {
    button.addEventListener("click", async () => {
      await runAction(button.dataset.action);
    });
  });
}

function withForm(handler) {
  return async (event) => {
    event.preventDefault();
    const form = event.currentTarget;
    try {
      await handler(form);
    } catch (error) {
      setAlert(error.message, "error");
    }
  };
}

async function runAction(action) {
  try {
    if (action === "load-repairs") await loadRepairs();
    if (action === "load-devices") await loadDevices();
    if (action === "load-users") await loadUsers();
    if (action === "load-available") await loadAvailableRepairs();
    setAlert("Data refreshed.");
  } catch (error) {
    setAlert(error.message, "error");
  }
}

async function loadDashboard() {
  await Promise.allSettled([loadRepairs(), loadDevices(), loadUsers()]);
}

loadState();
bindViews();
bindForms();
updateSession();
loadDashboard();
