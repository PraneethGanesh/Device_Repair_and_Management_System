const state = {
    session: JSON.parse(localStorage.getItem("userServiceSession") || "null"),
    employees: []
};

const el = {
    apiBase: document.querySelector("#apiBase"),
    status: document.querySelector("#status"),
    navItems: document.querySelectorAll(".nav-item"),
    viewTitle: document.querySelector("#viewTitle"),
    views: {
        dashboard: document.querySelector("#dashboardView"),
        auth: document.querySelector("#authView"),
        employees: document.querySelector("#employeesView"),
        devices: document.querySelector("#devicesView")
    },
    sessionName: document.querySelector("#sessionName"),
    sessionMeta: document.querySelector("#sessionMeta"),
    logoutBtn: document.querySelector("#logoutBtn"),
    metricUser: document.querySelector("#metricUser"),
    metricRole: document.querySelector("#metricRole"),
    metricDept: document.querySelector("#metricDept"),
    tokenPreview: document.querySelector("#tokenPreview"),
    profileResult: document.querySelector("#profileResult"),
    profileId: document.querySelector("#profileId"),
    employeesTable: document.querySelector("#employeesTable"),
    devicesEmployeeId: document.querySelector("#devicesEmployeeId"),
    devicesResult: document.querySelector("#devicesResult")
};

const viewTitles = {
    dashboard: "Dashboard",
    auth: "Sign In",
    employees: "Employees",
    devices: "Assigned Devices"
};

function getApiBase() {
    return el.apiBase.value.trim().replace(/\/$/, "") || "/api/users";
}

function authHeaders() {
    if (!state.session?.token) {
        return {};
    }

    return { Authorization: `Bearer ${state.session.token}` };
}

async function request(path, options = {}) {
    const response = await fetch(`${getApiBase()}${path}`, {
        ...options,
        headers: {
            "Content-Type": "application/json",
            ...authHeaders(),
            ...(options.headers || {})
        }
    });

    if (response.status === 204) {
        return null;
    }

    const text = await response.text();
    const data = text ? parseJson(text) : null;

    if (!response.ok) {
        throw new Error(extractError(data, response.status));
    }

    return data;
}

function parseJson(text) {
    try {
        return JSON.parse(text);
    } catch {
        return text;
    }
}

function extractError(data, status) {
    if (typeof data === "string" && data.trim()) {
        return data;
    }

    return data?.message || data?.error || `Request failed with status ${status}`;
}

function showStatus(message, type = "success") {
    el.status.textContent = message;
    el.status.className = `status ${type === "success" ? "" : type}`;
    el.status.classList.remove("hidden");
}

function clearStatus() {
    el.status.classList.add("hidden");
    el.status.textContent = "";
}

function setBusy(formOrButton, busy) {
    const controls = formOrButton.matches?.("button, input, select")
        ? [formOrButton]
        : formOrButton.querySelectorAll("button, input, select");

    controls.forEach((control) => {
        control.disabled = busy;
    });
}

function saveSession(session) {
    state.session = session;
    localStorage.setItem("userServiceSession", JSON.stringify(session));
    renderSession();
}

function clearSession() {
    state.session = null;
    localStorage.removeItem("userServiceSession");
    renderSession();
    showStatus("Logged out.");
}

function renderSession() {
    const user = state.session;
    el.sessionName.textContent = user ? user.name : "Not signed in";
    el.sessionMeta.textContent = user ? `${user.role} | ${user.email}` : "Use login or register";
    el.metricUser.textContent = user ? user.name : "Guest";
    el.metricRole.textContent = user ? user.role : "None";
    el.metricDept.textContent = user?.department || "None";
    el.tokenPreview.textContent = user?.token || "No token available.";
    el.logoutBtn.classList.toggle("hidden", !user);

    if (user?.id) {
        el.profileId.value = user.id;
        el.devicesEmployeeId.value = user.id;
    }
}

function switchView(viewName) {
    Object.entries(el.views).forEach(([name, view]) => {
        view.classList.toggle("active", name === viewName);
    });

    el.navItems.forEach((item) => {
        item.classList.toggle("active", item.dataset.view === viewName);
    });

    el.viewTitle.textContent = viewTitles[viewName];
    clearStatus();
}

function renderProfile(profile) {
    el.profileResult.classList.remove("empty");
    el.profileResult.innerHTML = `
        <strong>${escapeHtml(profile.name || "Unknown")}</strong>
        <span>ID: ${escapeHtml(profile.id)}</span>
        <span>Email: ${escapeHtml(profile.email || "Not set")}</span>
        <span>Department: ${escapeHtml(profile.department || "Not set")}</span>
        <span>Role: ${escapeHtml(profile.role || "Not set")}</span>
        <span>Joined: ${escapeHtml(profile.joinedDate || "Not set")}</span>
    `;
}

function renderEmployees() {
    if (!state.employees.length) {
        el.employeesTable.innerHTML = `<tr><td colspan="7" class="empty-row">No employees loaded.</td></tr>`;
        return;
    }

    el.employeesTable.innerHTML = state.employees.map((employee) => `
        <tr>
            <td>${escapeHtml(employee.id)}</td>
            <td>${escapeHtml(employee.name || "")}</td>
            <td>${escapeHtml(employee.email || "")}</td>
            <td>${escapeHtml(employee.department || "Not set")}</td>
            <td>${escapeHtml(employee.role || "")}</td>
            <td>${escapeHtml(employee.joinedDate || "Not set")}</td>
            <td>
                <div class="table-actions">
                    <select data-role-id="${escapeHtml(employee.id)}" aria-label="Role for ${escapeHtml(employee.name || employee.id)}">
                        <option value="EMPLOYEE" ${employee.role === "EMPLOYEE" ? "selected" : ""}>Employee</option>
                        <option value="ADMIN" ${employee.role === "ADMIN" ? "selected" : ""}>Admin</option>
                    </select>
                    <button data-save-role="${escapeHtml(employee.id)}" type="button">Save</button>
                    <button data-delete-id="${escapeHtml(employee.id)}" class="danger-button" type="button">Delete</button>
                </div>
            </td>
        </tr>
    `).join("");
}

function renderDevices(devices) {
    if (!Array.isArray(devices) || !devices.length) {
        el.devicesResult.classList.add("empty");
        el.devicesResult.textContent = "No assigned devices found.";
        return;
    }

    el.devicesResult.classList.remove("empty");
    el.devicesResult.innerHTML = devices.map((device, index) => {
        const title = device.name || device.deviceName || device.model || device.serialNumber || `Device ${index + 1}`;
        const rows = Object.entries(device)
            .filter(([, value]) => value !== null && value !== undefined && typeof value !== "object")
            .slice(0, 5)
            .map(([key, value]) => `<span>${escapeHtml(formatKey(key))}: ${escapeHtml(value)}</span>`)
            .join("");

        return `<article class="device-item"><strong>${escapeHtml(title)}</strong>${rows}</article>`;
    }).join("");
}

function formatKey(key) {
    return String(key).replace(/([A-Z])/g, " $1").replace(/^./, (char) => char.toUpperCase());
}

function escapeHtml(value) {
    return String(value ?? "")
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#039;");
}

async function login(event) {
    event.preventDefault();
    clearStatus();
    setBusy(event.currentTarget, true);

    try {
        const session = await request("/login", {
            method: "POST",
            body: JSON.stringify({
                email: document.querySelector("#loginEmail").value.trim(),
                password: document.querySelector("#loginPassword").value
            })
        });
        saveSession(session);
        switchView("dashboard");
        showStatus(`Welcome back, ${session.name}.`);
    } catch (error) {
        showStatus(error.message, "error");
    } finally {
        setBusy(event.currentTarget, false);
    }
}

async function register(event) {
    event.preventDefault();
    clearStatus();
    setBusy(event.currentTarget, true);

    try {
        const session = await request("/register", {
            method: "POST",
            body: JSON.stringify({
                name: document.querySelector("#registerName").value.trim(),
                email: document.querySelector("#registerEmail").value.trim(),
                password: document.querySelector("#registerPassword").value,
                department: document.querySelector("#registerDepartment").value.trim(),
                role: document.querySelector("#registerRole").value
            })
        });
        saveSession(session);
        switchView("dashboard");
        showStatus(`Account created for ${session.name}.`);
    } catch (error) {
        showStatus(error.message, "error");
    } finally {
        setBusy(event.currentTarget, false);
    }
}

async function loadProfile(id) {
    if (!id) {
        showStatus("Enter an employee ID first.", "warning");
        return;
    }

    try {
        const profile = await request(`/${id}`);
        renderProfile(profile);
        showStatus(`Loaded profile for ${profile.name}.`);
    } catch (error) {
        showStatus(error.message, "error");
    }
}

async function loadEmployees(button) {
    setBusy(button, true);

    try {
        state.employees = await request("/all");
        renderEmployees();
        showStatus(`Loaded ${state.employees.length} employee record${state.employees.length === 1 ? "" : "s"}.`);
    } catch (error) {
        showStatus(error.message, "error");
    } finally {
        setBusy(button, false);
    }
}

async function updateRole(id, role) {
    try {
        const updated = await request(`/${id}/role`, {
            method: "PUT",
            body: JSON.stringify({ role })
        });
        state.employees = state.employees.map((employee) => employee.id === updated.id ? updated : employee);
        renderEmployees();
        showStatus(`Updated ${updated.name} to ${updated.role}.`);
    } catch (error) {
        showStatus(error.message, "error");
    }
}

async function deleteEmployee(id) {
    const employee = state.employees.find((item) => String(item.id) === String(id));
    const label = employee?.name || `employee ${id}`;

    if (!confirm(`Delete ${label}?`)) {
        return;
    }

    try {
        await request(`/${id}`, { method: "DELETE" });
        state.employees = state.employees.filter((item) => String(item.id) !== String(id));
        renderEmployees();
        showStatus(`Deleted ${label}.`);
    } catch (error) {
        showStatus(error.message, "error");
    }
}

async function loadDevices(id) {
    if (!id) {
        showStatus("Enter an employee ID first.", "warning");
        return;
    }

    try {
        const devices = await request(`/${id}/devices`);
        renderDevices(devices);
        showStatus(`Loaded assigned devices for employee ${id}.`);
    } catch (error) {
        showStatus(error.message, "error");
    }
}

document.querySelector("#loginForm").addEventListener("submit", login);
document.querySelector("#registerForm").addEventListener("submit", register);

document.querySelector("#profileForm").addEventListener("submit", (event) => {
    event.preventDefault();
    loadProfile(el.profileId.value);
});

document.querySelector("#devicesForm").addEventListener("submit", (event) => {
    event.preventDefault();
    loadDevices(el.devicesEmployeeId.value);
});

document.querySelector("#loadCurrentProfileBtn").addEventListener("click", () => {
    loadProfile(state.session?.id);
});

document.querySelector("#loadCurrentDevicesBtn").addEventListener("click", () => {
    loadDevices(state.session?.id);
});

document.querySelector("#loadEmployeesBtn").addEventListener("click", (event) => {
    loadEmployees(event.currentTarget);
});

document.querySelector("#copyTokenBtn").addEventListener("click", async () => {
    if (!state.session?.token) {
        showStatus("No token to copy.", "warning");
        return;
    }

    try {
        await navigator.clipboard.writeText(state.session.token);
        showStatus("Token copied.");
    } catch {
        showStatus("Clipboard access is unavailable in this browser.", "warning");
    }
});

el.logoutBtn.addEventListener("click", clearSession);

el.navItems.forEach((item) => {
    item.addEventListener("click", () => switchView(item.dataset.view));
});

el.employeesTable.addEventListener("click", (event) => {
    const deleteId = event.target.dataset.deleteId;
    const saveRoleId = event.target.dataset.saveRole;

    if (deleteId) {
        deleteEmployee(deleteId);
    }

    if (saveRoleId) {
        const roleSelect = document.querySelector(`[data-role-id="${saveRoleId}"]`);
        updateRole(saveRoleId, roleSelect.value);
    }
});

renderSession();
