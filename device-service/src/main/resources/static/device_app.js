const API_BASE = "/api/devices";
const deviceTypes = ["LAPTOP", "DESKTOP", "TABLET", "ROUTER", "SENSOR", "HEADPHONE"];
const deviceStatuses = ["AVAILABLE", "ASSIGNED", "UNDER_REPAIR", "RETIRED"];

const state = {
  devices: [],
  visibleDevices: [],
  lookupMode: "all",
};

const els = {
  refreshButton: document.querySelector("#refreshButton"),
  deviceForm: document.querySelector("#deviceForm"),
  assignForm: document.querySelector("#assignForm"),
  cancelEditButton: document.querySelector("#cancelEditButton"),
  formTitle: document.querySelector("#formTitle"),
  deviceId: document.querySelector("#deviceId"),
  deviceName: document.querySelector("#deviceName"),
  deviceType: document.querySelector("#deviceType"),
  warrantyExpiry: document.querySelector("#warrantyExpiry"),
  vendorId: document.querySelector("#vendorId"),
  assignDeviceId: document.querySelector("#assignDeviceId"),
  assignUserId: document.querySelector("#assignUserId"),
  searchInput: document.querySelector("#searchInput"),
  statusFilter: document.querySelector("#statusFilter"),
  typeFilter: document.querySelector("#typeFilter"),
  vendorFilterForm: document.querySelector("#vendorFilterForm"),
  vendorFilterId: document.querySelector("#vendorFilterId"),
  employeeFilterForm: document.querySelector("#employeeFilterForm"),
  employeeFilterId: document.querySelector("#employeeFilterId"),
  clearFiltersButton: document.querySelector("#clearFiltersButton"),
  resultHint: document.querySelector("#resultHint"),
  deviceRows: document.querySelector("#deviceRows"),
  totalCount: document.querySelector("#totalCount"),
  availableCount: document.querySelector("#availableCount"),
  assignedCount: document.querySelector("#assignedCount"),
  attentionCount: document.querySelector("#attentionCount"),
  toast: document.querySelector("#toast"),
};

function fillSelect(select, values, placeholder) {
  if (placeholder) {
    select.append(new Option(placeholder, ""));
  }

  values.forEach((value) => {
    select.append(new Option(formatEnum(value), value));
  });
}

function formatEnum(value) {
  return String(value || "-")
    .toLowerCase()
    .split("_")
    .map((part) => part.charAt(0).toUpperCase() + part.slice(1))
    .join(" ");
}

async function request(path = "", options = {}) {
  const response = await fetch(`${API_BASE}${path}`, {
    headers: {
      "Content-Type": "application/json",
      ...(options.headers || {}),
    },
    ...options,
  });

  if (!response.ok) {
    const message = await response.text();
    throw new Error(message || `Request failed with status ${response.status}`);
  }

  if (response.status === 204) {
    return null;
  }

  return response.json();
}

function showToast(message, type = "success") {
  els.toast.textContent = message;
  els.toast.className = `toast visible ${type === "error" ? "error" : ""}`;
  window.clearTimeout(showToast.timeout);
  showToast.timeout = window.setTimeout(() => {
    els.toast.className = "toast";
  }, 3200);
}

function setBusy(button, busy) {
  button.disabled = busy;
}

async function loadDevices() {
  setBusy(els.refreshButton, true);
  try {
    state.devices = await request();
    state.lookupMode = "all";
    applyFilters();
    showToast("Device list refreshed");
  } catch (error) {
    renderEmpty("Could not load devices. Check that the Spring Boot service and database are running.");
    showToast(error.message, "error");
  } finally {
    setBusy(els.refreshButton, false);
  }
}

function applyFilters() {
  const query = els.searchInput.value.trim().toLowerCase();
  const status = els.statusFilter.value;
  const type = els.typeFilter.value;

  state.visibleDevices = state.devices.filter((device) => {
    const searchable = [
      device.id,
      device.deviceName,
      device.serialNumber,
      device.vendorId,
      device.assignedToId,
    ]
      .join(" ")
      .toLowerCase();

    return (!query || searchable.includes(query))
      && (!status || device.deviceStatus === status)
      && (!type || device.deviceType === type);
  });

  render();
}

function render() {
  updateMetrics();
  renderRows();

  const count = state.visibleDevices.length;
  const suffix = count === 1 ? "device" : "devices";
  els.resultHint.textContent = `${state.lookupMode}. Showing ${count} ${suffix}`;
}

function updateMetrics() {
  const counts = state.devices.reduce((acc, device) => {
    acc.total += 1;
    acc[device.deviceStatus] = (acc[device.deviceStatus] || 0) + 1;
    return acc;
  }, { total: 0 });

  els.totalCount.textContent = counts.total;
  els.availableCount.textContent = counts.AVAILABLE || 0;
  els.assignedCount.textContent = counts.ASSIGNED || 0;
  els.attentionCount.textContent = (counts.UNDER_REPAIR || 0) + (counts.RETIRED || 0);
}

function renderRows() {
  if (!state.visibleDevices.length) {
    renderEmpty("No devices match the current view.");
    return;
  }

  els.deviceRows.innerHTML = state.visibleDevices.map((device) => `
    <tr>
      <td>${device.id ?? "-"}</td>
      <td>
        <div class="device-title">
          <strong>${escapeHtml(device.deviceName)}</strong>
          <span class="serial">${escapeHtml(device.serialNumber || "Serial assigned after creation")}</span>
        </div>
      </td>
      <td>${formatEnum(device.deviceType)}</td>
      <td><span class="badge ${device.deviceStatus || ""}">${formatEnum(device.deviceStatus)}</span></td>
      <td>${device.assignedToId || "-"}</td>
      <td>${device.vendorId ?? "-"}</td>
      <td>${device.warrantyExpiry || "-"}</td>
      <td>${renderActions(device)}</td>
    </tr>
  `).join("");
}

function renderActions(device) {
  if (!device.id) {
    return `<span class="serial">Lookup result</span>`;
  }

  return `
    <div class="row-actions">
      <select data-action="status" data-id="${device.id}" aria-label="Change status for ${escapeHtml(device.deviceName)}">
        ${deviceStatuses.map((status) => `
          <option value="${status}" ${device.deviceStatus === status ? "selected" : ""}>${formatEnum(status)}</option>
        `).join("")}
      </select>
      <button class="ghost-button" data-action="edit" data-id="${device.id}" type="button">Edit</button>
      <button class="danger-button" data-action="delete" data-id="${device.id}" type="button">Delete</button>
    </div>
  `;
}

function renderEmpty(message) {
  els.deviceRows.innerHTML = `<tr><td colspan="8" class="empty">${escapeHtml(message)}</td></tr>`;
}

function escapeHtml(value) {
  return String(value ?? "")
    .replaceAll("&", "&amp;")
    .replaceAll("<", "&lt;")
    .replaceAll(">", "&gt;")
    .replaceAll('"', "&quot;")
    .replaceAll("'", "&#039;");
}

function getDevicePayload() {
  return {
    deviceName: els.deviceName.value.trim(),
    deviceType: els.deviceType.value,
    warrantyExpiry: els.warrantyExpiry.value,
    vendorId: Number(els.vendorId.value),
  };
}

function resetDeviceForm() {
  els.deviceForm.reset();
  els.deviceId.value = "";
  els.formTitle.textContent = "Add device";
  els.cancelEditButton.classList.add("hidden");
}

function startEdit(deviceId) {
  const device = state.devices.find((item) => String(item.id) === String(deviceId));
  if (!device) {
    showToast("Device not found in current list", "error");
    return;
  }

  els.deviceId.value = device.id;
  els.deviceName.value = device.deviceName || "";
  els.deviceType.value = device.deviceType || "";
  els.warrantyExpiry.value = device.warrantyExpiry || "";
  els.vendorId.value = device.vendorId || "";
  els.formTitle.textContent = `Edit device #${device.id}`;
  els.cancelEditButton.classList.remove("hidden");
  els.deviceName.focus();
}

async function saveDevice(event) {
  event.preventDefault();
  const id = els.deviceId.value;
  const payload = getDevicePayload();
  const submitButton = els.deviceForm.querySelector("button[type='submit']");

  setBusy(submitButton, true);
  try {
    if (id) {
      await request(`/${id}`, {
        method: "PUT",
        body: JSON.stringify(payload),
      });
      showToast("Device updated");
    } else {
      const { vendorId, ...devicePayload } = payload;
      await request(`/${vendorId}`, {
        method: "POST",
        body: JSON.stringify(devicePayload),
      });
      showToast("Device added");
    }

    resetDeviceForm();
    await loadDevices();
  } catch (error) {
    showToast(error.message, "error");
  } finally {
    setBusy(submitButton, false);
  }
}

async function assignDevice(event) {
  event.preventDefault();
  const submitButton = els.assignForm.querySelector("button[type='submit']");

  setBusy(submitButton, true);
  try {
    await request("/assign", {
      method: "POST",
      body: JSON.stringify({
        deviceId: Number(els.assignDeviceId.value),
        userId: Number(els.assignUserId.value),
      }),
    });
    els.assignForm.reset();
    showToast("Device assigned");
    await loadDevices();
  } catch (error) {
    showToast(error.message, "error");
  } finally {
    setBusy(submitButton, false);
  }
}

async function updateStatus(deviceId, status) {
  try {
    await request("/status", {
      method: "PUT",
      body: JSON.stringify({ deviceId: Number(deviceId), status }),
    });
    showToast("Status updated");
    await loadDevices();
  } catch (error) {
    showToast(error.message, "error");
  }
}

async function deleteDevice(deviceId) {
  const device = state.devices.find((item) => String(item.id) === String(deviceId));
  const name = device?.deviceName || `#${deviceId}`;

  if (!window.confirm(`Delete ${name}?`)) {
    return;
  }

  try {
    await request(`/${deviceId}`, { method: "DELETE" });
    showToast("Device deleted");
    await loadDevices();
  } catch (error) {
    showToast(error.message, "error");
  }
}

async function lookupBy(path, label) {
  try {
    const devices = await request(path);
    state.devices = devices;
    state.lookupMode = label;
    applyFilters();
  } catch (error) {
    showToast(error.message, "error");
  }
}

function wireEvents() {
  els.refreshButton.addEventListener("click", loadDevices);
  els.deviceForm.addEventListener("submit", saveDevice);
  els.assignForm.addEventListener("submit", assignDevice);
  els.cancelEditButton.addEventListener("click", resetDeviceForm);
  els.searchInput.addEventListener("input", applyFilters);
  els.statusFilter.addEventListener("change", applyFilters);
  els.typeFilter.addEventListener("change", applyFilters);

  els.vendorFilterForm.addEventListener("submit", (event) => {
    event.preventDefault();
    const vendorId = els.vendorFilterId.value;
    if (vendorId) {
      lookupBy(`/vendor/${vendorId}`, `Vendor ${vendorId} lookup`);
    }
  });

  els.employeeFilterForm.addEventListener("submit", (event) => {
    event.preventDefault();
    const employeeId = els.employeeFilterId.value;
    if (employeeId) {
      lookupBy(`/employee/${employeeId}`, `Employee ${employeeId} lookup`);
    }
  });

  els.clearFiltersButton.addEventListener("click", () => {
    els.searchInput.value = "";
    els.statusFilter.value = "";
    els.typeFilter.value = "";
    els.vendorFilterId.value = "";
    els.employeeFilterId.value = "";
    loadDevices();
  });

  els.deviceRows.addEventListener("click", (event) => {
    const button = event.target.closest("button[data-action]");
    if (!button) {
      return;
    }

    const { action, id } = button.dataset;
    if (action === "edit") {
      startEdit(id);
    }

    if (action === "delete") {
      deleteDevice(id);
    }
  });

  els.deviceRows.addEventListener("change", (event) => {
    const select = event.target.closest("select[data-action='status']");
    if (select) {
      updateStatus(select.dataset.id, select.value);
    }
  });
}

fillSelect(els.deviceType, deviceTypes);
fillSelect(els.statusFilter, deviceStatuses);
fillSelect(els.typeFilter, deviceTypes);
wireEvents();
loadDevices();
