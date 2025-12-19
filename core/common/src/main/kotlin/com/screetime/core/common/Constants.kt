package com.screetime.core.common

object Constants {
    // Time constants
    const val SECONDS_PER_MINUTE = 60
    const val MILLIS_PER_SECOND = 1000L
    const val MILLIS_PER_MINUTE = 60_000L
    const val MILLIS_PER_HOUR = 3_600_000L
    const val MILLIS_PER_DAY = 86_400_000L

    // Time tracking
    const val TIME_TRACKING_INTERVAL_MS = 1000L // 1 second
    const val TIME_SYNC_INTERVAL_MS = 60_000L   // 1 minute
    const val TIME_VALIDATION_TOLERANCE_MS = 5_000L // 5 seconds

    // Security
    const val INTEGRITY_CHECK_INTERVAL_MINUTES = 15L
    const val MAX_TIME_DRIFT_SECONDS = 5
    const val ROOT_CHECK_INTERVAL_MINUTES = 30L

    // Notifications
    const val CHANNEL_ID_TIME_TRACKING = "time_tracking"
    const val CHANNEL_ID_SECURITY = "security_alerts"
    const val CHANNEL_ID_TASKS = "task_notifications"
    const val CHANNEL_ID_GENERAL = "general"

    // Notification IDs
    const val NOTIFICATION_ID_TIME_TRACKING = 1001
    const val NOTIFICATION_ID_TIME_LOW = 1002
    const val NOTIFICATION_ID_TIME_DEPLETED = 1003
    const val NOTIFICATION_ID_SECURITY = 1004

    // SharedPreferences / DataStore
    const val PREFS_NAME = "screetime_prefs"
    const val KEY_USER_ID = "user_id"
    const val KEY_FAMILY_ID = "family_id"
    const val KEY_IS_DEVICE_OWNER = "is_device_owner"
    const val KEY_LAST_SYNC_TIME = "last_sync_time"
    const val KEY_REMAINING_SECONDS = "remaining_seconds"

    // Firebase Collections
    const val COLLECTION_USERS = "users"
    const val COLLECTION_FAMILIES = "families"
    const val COLLECTION_TASKS = "tasks"
    const val COLLECTION_TIME_BALANCES = "timeBalances"
    const val COLLECTION_TIME_LOGS = "timeLogs"
    const val COLLECTION_APP_USAGE = "appUsage"
    const val COLLECTION_BLOCKED_SESSIONS = "blockedSessions"
    const val COLLECTION_SECURITY_EVENTS = "securityEvents"
    const val COLLECTION_NOTIFICATIONS = "notifications"

    // Task limits
    const val MAX_TASK_TITLE_LENGTH = 50
    const val MAX_TASK_DESCRIPTION_LENGTH = 200
    const val MIN_REWARD_MINUTES = 1
    const val MAX_REWARD_MINUTES = 120

    // Time warnings
    const val TIME_WARNING_THRESHOLD_MINUTES = 5
    const val TIME_CRITICAL_THRESHOLD_MINUTES = 1

    // Date format
    const val DATE_FORMAT_YYYY_MM_DD = "yyyy-MM-dd"
    const val DATE_FORMAT_FULL = "yyyy-MM-dd HH:mm:ss"

    // Emergency unlock
    const val EMERGENCY_UNLOCK_DURATION_MINUTES = 15
}

object ErrorMessages {
    const val NETWORK_ERROR = "Network error. Please check your connection."
    const val AUTHENTICATION_ERROR = "Authentication failed. Please sign in again."
    const val PERMISSION_DENIED = "Permission denied. Please contact your parent."
    const val DEVICE_OWNER_NOT_SET = "Device Owner not configured. Please complete setup."
    const val TIME_MANIPULATION_DETECTED = "Time manipulation detected. Security event logged."
    const val GENERIC_ERROR = "An error occurred. Please try again."
}
