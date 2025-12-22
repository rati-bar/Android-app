package com.screetime.parent.data.model

data class ChildInfo(
    val id: String = "",
    val name: String = "",
    val remainingMinutes: Int = 0,
    val totalMinutes: Int = 0,
    val pendingTasksCount: Int = 0
)
