const API_BASE = "/api/vendors";
const DEVICE_TYPES = new Set(["LAPTOP", "DESKTOP", "TABLET", "ROUTER", "SENSOR", "HEADPHONE"]);

// Extended Client State Management
const state = {
    token: localStorage.getItem("vendorToken") || "",
    email: localStorage.getItem("vendorEmail") || "",
    name: localStorage.getItem("vendorName") || "",
    role: localStorage.getItem("vendorRole") || "VENDOR",
    devices: [], // Cache containing the fetched server payload
};

// DOM References
const authPanel = document.querySelector("#authPanel");
const dashboard = document.querySelector("#dashboard");
const loginTab = document.querySelector("#loginTab");
const registerTab = document.querySelector("#registerTab");
const loginForm = document.querySelector("#loginForm");
const registerForm = document.querySelector("#registerForm");
const deviceForm = document.querySelector("#deviceForm");

const authMessage = document.querySelector("#authMessage");
const deviceMessage = document.querySelector("#deviceMessage");

const deviceList = document.querySelector("#deviceList");
const emptyState = document.querySelector("#emptyState");

const deviceCount = document.querySelector("#deviceCount");
const activeWarrantyCount = document.querySelector("#activeWarrantyCount");
const activeWarrantyPercent = document.querySelector("#activeWarrantyPercent");
const expiredWarrantyCount = document.querySelector("#expiredWarrantyCount");
const expiredWarrantyTrend = document.querySelector("#expiredWarrantyTrend");

const roleValue = document.querySelector("#roleValue");
const sessionValue = document.querySelector("#sessionValue");
const welcomeTitle = document.querySelector("#welcomeTitle");

const logoutButton = document.querySelector("#logoutButton");
const refreshButton = document.querySelector("#refreshButton");

// Filtration Inputs
const searchBar = document.querySelector("#searchBar");
const typeFilter = document.querySelector("#typeFilter");
const sortSelector = document.querySelector("#sortSelector");

/* ==========================================================================
   DYNAMIC TOAST ALERT ENGINE
   ========================================================================== */
function showToast(title, message, type = "info") {
    const container = document.querySelector("#toastContainer");
    if (!container) return;

    const toast = document.createElement("div");
    toast.className = `toast ${type}`;

    let iconSvg = "";
    if (type === "success") {
        iconSvg = `
            <svg class="toast-content-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <path d="M22 4L12 14.01l-3-3" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>`;
    } else if (type === "error") {
        iconSvg = `
            <svg class="toast-content-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="2"/>
                <path d="M15 9l-6 6M9 9l6 6" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
            </svg>`;
    } else {
        iconSvg = `
            <svg class="toast-content-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="2"/>
                <path d="M12 16v-4M12 8h.01" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>`;
    }

    toast.innerHTML = `
        ${iconSvg}
        <div class="toast-body">
            <strong>${title}</strong>
            <span>${message}</span>
        </div>
        <button class="toast-close-btn" type="button">&times;</button>
    `;

    container.appendChild(toast);

    const closeBtn = toast.querySelector(".toast-close-btn");
    const dismiss = () => {
        toast.classList.add("fade-out");
        setTimeout(() => toast.remove(), 350);
    };

    closeBtn.addEventListener("click", dismiss);
    setTimeout(dismiss, 5000);
}

// Deprecated inline status writer (preserved for backwards-compatibility support)
function setMessage(element, text, type = "") {
    if (!text) {
        element.textContent = "";
        element.className = "message";
        return;
    }
    element.textContent = text;
    element.className = `message ${type}`.trim();
}

/* ==========================================================================
   BUTTON SUBMISSION THROTTLING & LOAD SHIMMER
   ========================================================================== */
function setFormLoading(form, isLoading) {
    if (!form) return;
    const submitBtn = form.querySelector("button[type='submit']");
    if (!submitBtn) return;
    const spinner = submitBtn.querySelector(".btn-spinner");
    const btnText = submitBtn.querySelector("span");

    submitBtn.disabled = isLoading;
    if (isLoading) {
        if (spinner) spinner.classList.remove("hidden");
        if (btnText) btnText.style.opacity = "0.6";
    } else {
        if (spinner) spinner.classList.add("hidden");
        if (btnText) btnText.style.opacity = "1";
    }
}

function renderSkeletonLoaders() {
    deviceList.innerHTML = "";
    emptyState.classList.add("hidden");
    
    for (let i = 0; i < 3; i++) {
        const skeleton = document.createElement("div");
        skeleton.className = "skeleton-card glass-panel";
        skeleton.innerHTML = `
            <div class="device-card-header">
                <div class="device-info-wrapper">
                    <div class="skeleton-title skeleton-pulse"></div>
                    <div class="skeleton-subtitle skeleton-pulse"></div>
                </div>
                <div class="device-card-icon skeleton-pulse"></div>
            </div>
            <div class="skeleton-footer">
                <div class="skeleton-badge skeleton-pulse"></div>
                <div class="skeleton-text skeleton-pulse"></div>
            </div>
        `;
        deviceList.appendChild(skeleton);
    }
}

/* ==========================================================================
   STATE FLOW CONTROLLER & TAB SYSTEM
   ========================================================================== */
function switchAuthTab(tab) {
    const isLogin = tab === "login";
    loginTab.classList.toggle("active", isLogin);
    registerTab.classList.toggle("active", !isLogin);
    loginForm.classList.toggle("active", isLogin);
    registerForm.classList.toggle("active", !isLogin);
    setMessage(authMessage, "");
}

function saveSession(session) {
    state.token = session.token || state.token;
    state.email = session.email || state.email;
    state.name = session.name || state.name || state.email;
    state.role = session.role || state.role || "VENDOR";

    localStorage.setItem("vendorToken", state.token);
    localStorage.setItem("vendorEmail", state.email);
    localStorage.setItem("vendorName", state.name);
    localStorage.setItem("vendorRole", state.role);
}

function clearSession() {
    state.token = "";
    state.email = "";
    state.name = "";
    state.role = "VENDOR";
    state.devices = [];
    localStorage.removeItem("vendorToken");
    localStorage.removeItem("vendorEmail");
    localStorage.removeItem("vendorName");
    localStorage.removeItem("vendorRole");
}

function updateView() {
    const isLoggedIn = Boolean(state.token && state.email);
    authPanel.classList.toggle("hidden", isLoggedIn);
    dashboard.classList.toggle("hidden", !isLoggedIn);

    if (isLoggedIn) {
        welcomeTitle.textContent = state.name ? `${state.name}'s Panel` : "Vendor Workspace";
        roleValue.textContent = state.role;
        sessionValue.textContent = state.email;
    }
}

function getAuthHeaders() {
    return {
        "Content-Type": "application/json",
        "X-Auth-User": state.email,
        "X-Auth-Role": state.role || "VENDOR",
        "Authorization": `Bearer ${state.token}`,
    };
}

async function parseResponse(response) {
    const contentType = response.headers.get("content-type") || "";
    const body = contentType.includes("application/json")
        ? await response.json()
        : await response.text();

    if (!response.ok) {
        const message = typeof body === "string" ? body : body.message || "Operation failed";
        throw new Error(message);
    }

    return body;
}

async function request(path, options = {}) {
    const response = await fetch(`${API_BASE}${path}`, options);
    return parseResponse(response);
}

function normalizeDevice(device) {
    return {
        name: device.deviceName || device.DeviceName || "Unnamed hardware",
        type: DEVICE_TYPES.has(device.deviceType) ? device.deviceType : "DEVICE",
        warrantyExpiry: device.warrantyExpiry || "",
    };
}

/* ==========================================================================
   WARRANTY METRIC SYNTHESIZER
   ========================================================================== */
function getWarrantyMetrics(expiryVal) {
    if (!expiryVal) return { label: "Unknown", daysLeft: 0, status: "expired" };

    const today = new Date();
    const expiry = new Date(`${expiryVal}T00:00:00`);
    const daysLeft = Math.ceil((expiry - today) / 86400000);

    if (Number.isNaN(daysLeft)) {
        return { label: expiryVal, daysLeft: 0, status: "expired" };
    }

    if (daysLeft < 0) {
        const absDays = Math.abs(daysLeft);
        return {
            label: `Expired ${absDays} day${absDays === 1 ? "" : "s"} ago`,
            daysLeft,
            status: "expired"
        };
    }

    if (daysLeft < 90) {
        return {
            label: `${daysLeft} day${daysLeft === 1 ? "" : "s"} left`,
            daysLeft,
            status: "warning"
        };
    }

    return {
        label: `${daysLeft} day${daysLeft === 1 ? "" : "s"} warranty`,
        daysLeft,
        status: "active"
    };
}

/* ==========================================================================
   HIGH-FIDELITY CUSTOM SVG ICON GENERATOR
   ========================================================================== */
function getDeviceTypeSvg(type) {
    switch (type) {
        case "LAPTOP":
            return `
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <rect x="2" y="3" width="20" height="14" rx="2" />
                    <line x1="2" y1="20" x2="22" y2="20" />
                    <line x1="12" y1="17" x2="12" y2="20" />
                </svg>`;
        case "DESKTOP":
            return `
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <rect x="3" y="3" width="18" height="12" rx="2" />
                    <line x1="12" y1="15" x2="12" y2="20" />
                    <line x1="9" y1="20" x2="15" y2="20" />
                </svg>`;
        case "TABLET":
            return `
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <rect x="4" y="2" width="16" height="20" rx="2" />
                    <line x1="12" y1="18" x2="12.01" y2="18" />
                </svg>`;
        case "ROUTER":
            return `
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <rect x="2" y="14" width="20" height="6" rx="2" />
                    <line x1="6" y1="14" x2="6" y2="10" />
                    <line x1="18" y1="14" x2="18" y2="10" />
                    <path d="M12 14V6l-3 3M12 6l3 3" />
                </svg>`;
        case "SENSOR":
            return `
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <circle cx="12" cy="12" r="9" />
                    <circle cx="12" cy="12" r="2" fill="currentColor" />
                    <path d="M12 3v3M12 18v3M3 12h3M18 12h3" />
                </svg>`;
        case "HEADPHONE":
            return `
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <path d="M3 18v-6a9 9 0 0 1 18 0v6" />
                    <path d="M21 19a2 2 0 0 1-2 2h-1a2 2 0 0 1-2-2v-3a2 2 0 0 1 2-2h3M3 19a2 2 0 0 0 2 2h1a2 2 0 0 0 2-2v-3a2 2 0 0 0-2-2H3" />
                </svg>`;
        default:
            return `
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <path d="M21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16z" />
                </svg>`;
    }
}

/* ==========================================================================
   STATE RENDERING PIPELINE
   ========================================================================== */
function renderDevices(devices) {
    const normalized = Array.isArray(devices) ? devices.map(normalizeDevice) : [];
    deviceList.innerHTML = "";
    
    // Update metric totals on full cache instead of filtered subset
    const totalAll = state.devices.length;
    let activeWCount = 0;
    let expiredWCount = 0;

    state.devices.map(normalizeDevice).forEach(dev => {
        const metrics = getWarrantyMetrics(dev.warrantyExpiry);
        if (metrics.status === "active") activeWCount++;
        else if (metrics.status === "expired") expiredWCount++;
        else activeWCount++; // Warn counts as active but expiring soon
    });

    deviceCount.textContent = totalAll;
    activeWarrantyCount.textContent = activeWCount;
    expiredWarrantyCount.textContent = expiredWCount;
    
    // Protection ratio
    const percent = totalAll > 0 ? Math.round((activeWCount / totalAll) * 100) : 100;
    activeWarrantyPercent.textContent = `${percent}% Asset Policy Protection`;
    expiredWarrantyTrend.textContent = expiredWCount > 0 ? "Requires Renewal Action" : "All policies compliant";

    emptyState.classList.toggle("hidden", normalized.length > 0);

    normalized.forEach((device) => {
        const card = document.createElement("article");
        const metrics = getWarrantyMetrics(device.warrantyExpiry);
        
        // Dynamic styling modifiers
        let statusBadgeClass = "";
        let statusSvg = "";
        
        if (metrics.status === "active") {
            card.className = "device-card glass-panel warranty-active";
            statusBadgeClass = "status-active";
            statusSvg = `
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z" />
                </svg>`;
        } else if (metrics.status === "warning") {
            card.className = "device-card glass-panel warranty-warning";
            statusBadgeClass = "status-warning";
            statusSvg = `
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0zM12 9v4M12 17h.01" />
                </svg>`;
        } else {
            card.className = "device-card glass-panel warranty-expired";
            statusBadgeClass = "status-expired";
            statusSvg = `
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <circle cx="12" cy="12" r="10" />
                    <line x1="15" y1="9" x2="9" y2="15" />
                    <line x1="9" y1="9" x2="15" y2="15" />
                </svg>`;
        }

        card.innerHTML = `
            <div class="device-card-header">
                <div class="device-info-wrapper">
                    <h3>${device.name}</h3>
                    <span class="device-type-label">${device.type}</span>
                </div>
                <div class="device-card-icon">
                    ${getDeviceTypeSvg(device.type)}
                </div>
            </div>
            <div class="device-card-footer">
                <span class="status-indicator-badge ${statusBadgeClass}">
                    ${statusSvg}
                    <span>${metrics.status === "active" ? "Active" : metrics.status === "warning" ? "Expiring" : "Expired"}</span>
                </span>
                <span class="warranty-value-text">${metrics.label}</span>
            </div>
        `;

        deviceList.append(card);
    });
}

/* ==========================================================================
   CLIENT SEARCH / FILTERING / SORTING PIPELINE
   ========================================================================== */
function filterAndRender() {
    let filtered = [...state.devices];
    
    // 1. Text Search Query Match
    const query = searchBar.value.trim().toLowerCase();
    if (query) {
        filtered = filtered.filter(device => {
            const normalized = normalizeDevice(device);
            return normalized.name.toLowerCase().includes(query) || 
                   normalized.type.toLowerCase().includes(query);
        });
    }

    // 2. Category Filtration Tag Match
    const category = typeFilter.value;
    if (category !== "ALL") {
        filtered = filtered.filter(device => {
            const normalized = normalizeDevice(device);
            return normalized.type === category;
        });
    }

    // 3. Dynamic Sorting
    const sortRule = sortSelector.value;
    filtered.sort((a, b) => {
        const normA = normalizeDevice(a);
        const normB = normalizeDevice(b);

        if (sortRule === "nameAsc") {
            return normA.name.localeCompare(normB.name);
        } else if (sortRule === "nameDesc") {
            return normB.name.localeCompare(normA.name);
        } else if (sortRule === "expiryAsc") {
            // Expiring soonest (or already expired)
            const dateA = normA.warrantyExpiry ? new Date(normA.warrantyExpiry) : new Date(0);
            const dateB = normB.warrantyExpiry ? new Date(normB.warrantyExpiry) : new Date(0);
            return dateA - dateB;
        } else if (sortRule === "expiryDesc") {
            // Expiring furthest in future
            const dateA = normA.warrantyExpiry ? new Date(normA.warrantyExpiry) : new Date(0);
            const dateB = normB.warrantyExpiry ? new Date(normB.warrantyExpiry) : new Date(0);
            return dateB - dateA;
        }
        return 0;
    });

    renderDevices(filtered);
}

// Attach filter listeners
searchBar.addEventListener("input", filterAndRender);
typeFilter.addEventListener("change", filterAndRender);
sortSelector.addEventListener("change", filterAndRender);

/* ==========================================================================
   SERVER SYNCHRONIZATION PIPELINE
   ========================================================================== */
async function loadDevices() {
    if (!state.email) {
        return;
    }

    renderSkeletonLoaders();
    setMessage(deviceMessage, "Syncing catalog...");
    
    try {
        const devices = await request("/devices", {
            method: "GET",
            headers: getAuthHeaders(),
        });
        
        state.devices = Array.isArray(devices) ? devices : [];
        filterAndRender();
        
        setMessage(deviceMessage, "Sync complete", "success");
        showToast("Synchronized", "Successfully fetched catalog updates.", "success");
    } catch (error) {
        state.devices = [];
        filterAndRender();
        setMessage(deviceMessage, error.message, "error");
        showToast("Synchronization Failed", error.message, "error");
    }
}

/* ==========================================================================
   EVENT LISTENER ATTRIBUTE BINDINGS
   ========================================================================== */
loginTab.addEventListener("click", () => switchAuthTab("login"));
registerTab.addEventListener("click", () => switchAuthTab("register"));

loginForm.addEventListener("submit", async (event) => {
    event.preventDefault();
    setMessage(authMessage, "Validating credentials...");
    setFormLoading(loginForm, true);

    const data = Object.fromEntries(new FormData(loginForm));

    try {
        const token = await request("/login", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(data),
        });
        
        saveSession({ token, email: data.username, name: data.username.split('@')[0], role: "VENDOR" });
        loginForm.reset();
        updateView();
        
        showToast("Authentication Success", `Welcome back, ${state.name}!`, "success");
        await loadDevices();
    } catch (error) {
        setMessage(authMessage, error.message, "error");
        showToast("Access Denied", error.message, "error");
    } finally {
        setFormLoading(loginForm, false);
    }
});

registerForm.addEventListener("submit", async (event) => {
    event.preventDefault();
    setMessage(authMessage, "Provisioning account...");
    setFormLoading(registerForm, true);

    const data = Object.fromEntries(new FormData(registerForm));

    try {
        const session = await request("/register", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(data),
        });
        
        saveSession(session);
        registerForm.reset();
        updateView();
        
        showToast("Account Created", "Enterprise workspace successfully provisioned.", "success");
        await loadDevices();
    } catch (error) {
        setMessage(authMessage, error.message, "error");
        showToast("Provisioning Failed", error.message, "error");
    } finally {
        setFormLoading(registerForm, false);
    }
});

deviceForm.addEventListener("submit", async (event) => {
    event.preventDefault();
    setMessage(deviceMessage, "Registering hardware...");
    setFormLoading(deviceForm, true);

    const values = Object.fromEntries(new FormData(deviceForm));
    const payload = {
        deviceName: values.deviceName,
        deviceType: values.deviceType,
        warrantyExpiry: values.warrantyExpiry,
    };

    try {
        await request("/devices", {
            method: "POST",
            headers: getAuthHeaders(),
            body: JSON.stringify(payload),
        });
        
        deviceForm.reset();
        setMessage(deviceMessage, "Asset registered.", "success");
        showToast("Asset Registered", `${payload.deviceName} successfully cataloged.`, "success");
        await loadDevices();
    } catch (error) {
        setMessage(deviceMessage, error.message, "error");
        showToast("Registration Failed", error.message, "error");
    } finally {
        setFormLoading(deviceForm, false);
    }
});

refreshButton.addEventListener("click", loadDevices);

logoutButton.addEventListener("click", () => {
    const prevName = state.name;
    clearSession();
    
    // Clear display inputs
    searchBar.value = "";
    typeFilter.value = "ALL";
    sortSelector.value = "expiryAsc";
    
    renderDevices([]);
    setMessage(authMessage, "");
    setMessage(deviceMessage, "");
    updateView();
    showToast("Signed Out", "Enterprise workspace session ended successfully.", "info");
});

// App Initialization
updateView();
if (state.token && state.email) {
    loadDevices();
}
