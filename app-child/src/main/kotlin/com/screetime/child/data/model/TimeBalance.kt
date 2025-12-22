package com.screetime.child.data.model

data class TimeBalance(
    val childId: String = "",
    val totalMinutes: Int = 0,
    val usedMinutes: Int = 0,
    val remainingMinutes: Int = 0,
    val isBlocked: Boolean = false,
    val blockReason: String? = null
) {
    fun toHoursMinutesSeconds(): Triple<Int, Int, Int> {
        val hours = remainingMinutes / 60
        val minutes = remainingMinutes % 60
        val seconds = 0
        return Triple(hours, minutes, seconds)
    }

    fun formatTime(): String {
        val (hours, minutes, _) = toHoursMinutesSeconds()
        return String.format("%02d:%02d:%02d", hours, minutes, 0)
    }
}
