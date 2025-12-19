package com.screetime.core.model

data class AppNotification(
    val id: String = "",
    val userId: String = "",
    val title: String = "",
    val body: String = "",
    val type: NotificationType = NotificationType.INFO,
    val data: Map<String, String> = emptyMap(),
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)

enum class NotificationType {
    TASK_COMPLETED,
    TASK_APPROVED,
    TASK_REJECTED,
    TIME_LOW,
    TIME_DEPLETED,
    DAILY_RESET,
    SECURITY_ALERT,
    CHILD_REQUEST,
    INFO
}
