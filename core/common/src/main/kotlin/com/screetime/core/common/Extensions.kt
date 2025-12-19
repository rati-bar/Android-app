package com.screetime.core.common

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

/**
 * Time extensions
 */
fun Long.toMinutes(): Int = TimeUnit.MILLISECONDS.toMinutes(this).toInt()

fun Long.toSeconds(): Int = TimeUnit.MILLISECONDS.toSeconds(this).toInt()

fun Int.minutesToMillis(): Long = TimeUnit.MINUTES.toMillis(this.toLong())

fun Int.secondsToMillis(): Long = TimeUnit.SECONDS.toMillis(this.toLong())

fun Int.formatAsTime(): String {
    val hours = this / 60
    val minutes = this % 60
    return when {
        hours > 0 -> "${hours}h ${minutes}m"
        else -> "${minutes}m"
    }
}

fun Int.formatAsTimeDetailed(): String {
    val hours = this / 3600
    val minutes = (this % 3600) / 60
    val seconds = this % 60

    return when {
        hours > 0 -> String.format("%02d:%02d:%02d", hours, minutes, seconds)
        else -> String.format("%02d:%02d", minutes, seconds)
    }
}

/**
 * Date extensions
 */
fun Long.toDateString(format: String = Constants.DATE_FORMAT_YYYY_MM_DD): String {
    val sdf = SimpleDateFormat(format, Locale.getDefault())
    return sdf.format(Date(this))
}

fun Long.isToday(): Boolean {
    val today = SimpleDateFormat(Constants.DATE_FORMAT_YYYY_MM_DD, Locale.getDefault())
        .format(Date())
    val date = this.toDateString()
    return today == date
}

fun Long.isSameDay(other: Long): Boolean {
    return this.toDateString() == other.toDateString()
}

/**
 * String extensions
 */
fun String.isValidEmail(): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

fun String.truncate(maxLength: Int, suffix: String = "..."): String {
    return if (this.length > maxLength) {
        this.substring(0, maxLength - suffix.length) + suffix
    } else {
        this
    }
}

/**
 * Collection extensions
 */
fun <T> List<T>.takeIfNotEmpty(): List<T>? = if (isEmpty()) null else this

/**
 * Boolean extensions
 */
fun Boolean.toInt() = if (this) 1 else 0
