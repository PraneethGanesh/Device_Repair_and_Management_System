package com.example.repair_service.enums;

public enum RepairStatus {
    RAISED,             // employee raised the ticket
    ASSIGNED_TO_VENDOR, // company admin assigned it to vendor
    IN_PROGRESS,        // vendor started working
    REPAIR_DONE,        // vendor completed repair
    CLOSED          // Admin closed ticket and reassigned device
}
