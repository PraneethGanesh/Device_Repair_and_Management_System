package com.example.repair_service.enums;

public enum RepairStatus {
    PENDING,            // Employee/Admin raised request
    ACKNOWLEDGED,       // Admin acknowledged and forwarded to vendor pool
    ASSIGNED_TO_VENDOR, // Vendor self-assigned
    IN_PROGRESS,        // Vendor started work
    COMPLETED,          // Vendor marked done — waiting for Admin to close
    CLOSED              // Admin closed ticket and reassigned device
}
