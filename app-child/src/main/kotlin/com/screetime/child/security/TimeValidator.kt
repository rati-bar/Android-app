package com.screetime.child.security

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.abs

/**
 * Validates system time against NTP servers and backend to detect time manipulation.
 */
@Singleton
class TimeValidator @Inject constructor() {

    companion object {
        private const val TAG = "TimeValidator"
        private const val NTP_SERVER = "time.google.com"
        private const val NTP_PORT = 123
        private const val TIME_TOLERANCE_MS = 5000L // 5 seconds tolerance
    }

    /**
     * Validates that system time matches network time.
     * Returns true if time is valid, false if manipulation detected.
     */
    suspend fun validateSystemTime(): Boolean = withContext(Dispatchers.IO) {
        try {
            val systemTime = System.currentTimeMillis()
            val ntpTime = getNTPTime()

            if (ntpTime == null) {
                Log.w(TAG, "Could not fetch NTP time - assuming valid")
                return@withContext true
            }

            val timeDiff = abs(systemTime - ntpTime)

            if (timeDiff > TIME_TOLERANCE_MS) {
                Log.e(TAG, "TIME MANIPULATION DETECTED! Diff: ${timeDiff}ms")
                logSecurityEvent("Time manipulation detected: ${timeDiff}ms difference")
                return@withContext false
            }

            Log.d(TAG, "Time validation passed (diff: ${timeDiff}ms)")
            true

        } catch (e: Exception) {
            Log.e(TAG, "Error validating time", e)
            // In case of error, assume time is valid (offline scenario)
            true
        }
    }

    /**
     * Gets current time from NTP server.
     * Returns null if unable to fetch.
     */
    private suspend fun getNTPTime(): Long? = withContext(Dispatchers.IO) {
        try {
            val socket = Socket()
            socket.soTimeout = 5000 // 5 second timeout

            socket.connect(InetSocketAddress(NTP_SERVER, NTP_PORT), 5000)

            // NTP time request packet
            val buffer = ByteArray(48)
            buffer[0] = 0x1B.toByte() // LI = 0, VN = 3, Mode = 3

            val outputStream = socket.getOutputStream()
            outputStream.write(buffer)
            outputStream.flush()

            val inputStream = socket.getInputStream()
            inputStream.read(buffer)

            socket.close()

            // Extract time from NTP response
            val secondsSince1900 = ((buffer[40].toLong() and 0xFF) shl 24) +
                    ((buffer[41].toLong() and 0xFF) shl 16) +
                    ((buffer[42].toLong() and 0xFF) shl 8) +
                    (buffer[43].toLong() and 0xFF)

            // Convert from 1900 epoch to Unix epoch (1970)
            val epochDiff = 2208988800L
            val unixTime = (secondsSince1900 - epochDiff) * 1000

            Log.d(TAG, "NTP time fetched: $unixTime")
            unixTime

        } catch (e: IOException) {
            Log.w(TAG, "Failed to fetch NTP time", e)
            null
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error fetching NTP time", e)
            null
        }
    }

    /**
     * Validates time against backend server time.
     */
    suspend fun validateAgainstServer(): Boolean = withContext(Dispatchers.IO) {
        try {
            // TODO: Implement Firebase server time validation
            // Use Firestore server timestamp and compare with system time

            true
        } catch (e: Exception) {
            Log.e(TAG, "Error validating against server", e)
            true
        }
    }

    private fun logSecurityEvent(message: String) {
        // TODO: Log to Firestore security events collection
        Log.e(TAG, "SECURITY EVENT: $message")
    }
}
