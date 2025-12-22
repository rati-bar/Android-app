package com.screetime.parent.data.model

data class Task(
    val id: String = "",
    val childId: String = "",
    val familyId: String = "",
    val title: String = "",
    val description: String = "",
    val rewardMinutes: Int = 0,
    val status: TaskStatus = TaskStatus.PENDING,
    val isRecurring: Boolean = false,
    val completedAt: Long? = null
)

enum class TaskStatus {
    PENDING,
    COMPLETED,
    APPROVED,
    REJECTED
}
