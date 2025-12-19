package com.screetime.child.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

/**
 * Receiver to detect system time changes (potential bypass attempt).
 */
class TimeChangeReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "TimeChangeReceiver"
    }

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_TIME_SET,
            Intent.ACTION_TIMEZONE_CHANGED -> {
                Log.w(TAG, "System time changed - potential tampering detected!")

                // Log security event
                logTimeChangeEvent(context)

                // Immediately sync with server to get correct time
                syncWithServer(context)
            }
        }
    }

    private fun logTimeChangeEvent(context: Context) {
        // TODO: Log to Firestore as critical security event
        Log.e(TAG, "TIME CHANGE DETECTED - Security event logged")
    }

    private fun syncWithServer(context: Context) {
        // TODO: Trigger immediate server sync to validate time
        Log.i(TAG, "Initiating server time sync")
    }
}
