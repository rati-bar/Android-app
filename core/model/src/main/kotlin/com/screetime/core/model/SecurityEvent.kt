package com.screetime.core.model

data class SecurityEvent(
    val id: String = "",
    val childId: String = "",
    val eventType: SecurityEventType = SecurityEventType.TIME_CHANGE_ATTEMPT,
    val details: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val wasPrevented: Boolean = false,
    val severity: SecuritySeverity = SecuritySeverity.MEDIUM
)

enum class SecurityEventType {
    TIME_CHANGE_ATTEMPT,
    UNINSTALL_ATTEMPT,
    FORCE_STOP_ATTEMPT,
    ROOT_DETECTED,
    DEVICE_ADMIN_REMOVAL_ATTEMPT,
    FACTORY_RESET_ATTEMPT,
    ADB_ENABLED,
    UNKNOWN_APP_INSTALLED,
    DEVELOPER_OPTIONS_ENABLED,
    INTEGRITY_CHECK_FAILED,
    SUSPICIOUS_REBOOT,
    PACKAGE_MANAGER_TAMPERING
}

enum class SecuritySeverity {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL
}
