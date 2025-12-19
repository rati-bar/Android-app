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
                Log.i(TAG, "Device booted - restarting services")

                // Restart time tracking service
                val serviceIntent = Intent(context, TimeTrackingService::class.java)
                context.startForegroundService(serviceIntent)

                // Log security event (potential bypass attempt)
                logRebootEvent(context)
            }
        }
    }

    private fun logRebootEvent(context: Context) {
        // TODO: Log reboot to Firestore as potential security event
        Log.w(TAG, "Device rebooted - logged as security event")
    }
}
