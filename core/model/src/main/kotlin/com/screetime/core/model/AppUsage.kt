package com.screetime.core.model

data class AppUsage(
    val id: String = "",
    val childId: String = "",
    val packageName: String = "",
    val appName: String = "",
    val usageTimeMs: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val date: String = ""  // YYYY-MM-DD format
)

data class DailyAppUsage(
    val date: String,
    val totalUsageMs: Long,
    val apps: List<AppUsageDetail>
)

data class AppUsageDetail(
    val packageName: String,
    val appName: String,
    val usageTimeMs: Long,
    val percentage: Float
)
