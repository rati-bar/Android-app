package com.screetime.core.model

data class TimeBalance(
    val childId: String = "",
    val totalMinutes: Int = 0,
    val usedMinutes: Int = 0,
    val remainingMinutes: Int = 0,
    val lastResetAt: Long = System.currentTimeMillis(),
    val lastUpdatedAt: Long = System.currentTimeMillis(),
    val isBlocked: Boolean = false,
    val blockReason: BlockReason? = null
) {
    val remainingSeconds: Int
        get() = remainingMinutes * 60
}

data class TimeLog(
    val id: String = "",
    val childId: String = "",
    val action: TimeAction = TimeAction.USED,
    val minutesChange: Int = 0,
    val balanceAfter: Int = 0,
    val taskId: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val reason: String = ""
)

enum class TimeAction {
    EARNED,         // From approved task
    USED,           // Time consumption
    RESET,          // Daily reset
    ADJUSTED,       // Manual adjustment by parent
    BONUS           // Parent bonus time
}

data class BlockedSession(
    val id: String = "",
    val childId: String = "",
    val startTime: Long = System.currentTimeMillis(),
    val endTime: Long? = null,
    val reason: BlockReason = BlockReason.TIME_DEPLETED,
    val isActive: Boolean = true
)

enum class BlockReason {
    TIME_DEPLETED,
    PARENT_OVERRIDE,
    SCHEDULED_BEDTIME,
    EMERGENCY_LOCK
}
