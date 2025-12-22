package com.screetime.child.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

/**
 * Receiver to restart time tracking service after device reboot.
 */
class BootReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "BootReceiver"
    }

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_LOCKED_BOOT_COMPLETED,
            "android.intent.action.QUICKBOOT_POWERON" -> {
                Log.i(TAG, "Device booted - starting services")
                startTrackingService(context)
                logRebootEvent(context)
            }
            Intent.ACTION_USER_PRESENT -> {
                // Screen unlocked - ensure service is running
                Log.d(TAG, "Screen unlocked - ensuring service is running")
                startTrackingService(context)
            }
            Intent.ACTION_SCREEN_ON -> {
                // Screen turned on - ensure service is running
                Log.d(TAG, "Screen turned on - ensuring service is running")
                startTrackingService(context)
            }
        }
    }

    private fun startTrackingService(context: Context) {
        try {
            val serviceIntent = Intent(context, TimeTrackingService::class.java)
            context.startForegroundService(serviceIntent)
            Log.d(TAG, "TimeTrackingService started")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start TimeTrackingService", e)
        }
    }

    private fun logRebootEvent(context: Context) {
        // TODO: Log reboot to Firestore as potential security event
        Log.w(TAG, "Device rebooted - logged as security event")
    }
}
