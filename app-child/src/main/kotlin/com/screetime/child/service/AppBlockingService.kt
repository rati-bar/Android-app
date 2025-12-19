package com.screetime.child.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.app.admin.DevicePolicyManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.screetime.child.R
import com.screetime.core.common.Constants
import kotlinx.coroutines.*

/**
 * Service responsible for blocking all apps except Screen Time when time is depleted.
 * Uses Device Owner privileges to hide/suspend apps and enable kiosk mode.
 */
class AppBlockingService : Service() {

    private lateinit var devicePolicyManager: DevicePolicyManager
    private lateinit var adminComponentName: android.content.ComponentName
    private lateinit var notificationManager: NotificationManager

    companion object {
        private const val TAG = "AppBlockingService"
        const val ACTION_BLOCK_DEVICE = "com.screetime.child.BLOCK_DEVICE"
        const val ACTION_UNBLOCK_DEVICE = "com.screetime.child.UNBLOCK_DEVICE"
        private const val NOTIFICATION_ID = 1005
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Service created")

        devicePolicyManager = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        adminComponentName = ScreenTimeDeviceAdminReceiver.getComponentName(this)
        notificationManager = getSystemService(NotificationManager::class.java)

        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand: ${intent?.action}")

        when (intent?.action) {
            ACTION_BLOCK_DEVICE -> {
                blockDevice()
            }
            ACTION_UNBLOCK_DEVICE -> {
                unblockDevice()
            }
        }

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun blockDevice() {
        if (!ScreenTimeDeviceAdminReceiver.isDeviceOwner(this)) {
            Log.e(TAG, "Not device owner - cannot block apps")
            return
        }

        Log.i(TAG, "Blocking device...")

        try {
            startForeground(NOTIFICATION_ID, createBlockNotification())

            // Get all installed apps
            val packages = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)

            val blockedCount = packages.count { appInfo ->
                val packageName = appInfo.packageName

                // Don't block system-critical apps or Screen Time itself
                if (isSystemCritical(packageName) || packageName == this.packageName) {
                    false
                } else {
                    try {
                        // Hide app from launcher
                        devicePolicyManager.setApplicationHidden(
                            adminComponentName,
                            packageName,
                            true
                        )

                        // Suspend the app (prevents it from running)
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                            devicePolicyManager.setPackagesSuspended(
                                adminComponentName,
                                arrayOf(packageName),
                                true
                            )
                        }

                        true
                    } catch (e: Exception) {
                        Log.w(TAG, "Failed to block $packageName", e)
                        false
                    }
                }
            }

            Log.i(TAG, "Blocked $blockedCount apps")

            // Enable kiosk mode (lock task mode) for Screen Time app only
            enableKioskMode()

        } catch (e: Exception) {
            Log.e(TAG, "Failed to block device", e)
        }
    }

    private fun unblockDevice() {
        if (!ScreenTimeDeviceAdminReceiver.isDeviceOwner(this)) {
            Log.e(TAG, "Not device owner - cannot unblock apps")
            return
        }

        Log.i(TAG, "Unblocking device...")

        try {
            // Disable kiosk mode first
            disableKioskMode()

            // Get all installed apps
            val packages = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)

            packages.forEach { appInfo ->
                val packageName = appInfo.packageName

                if (packageName != this.packageName) {
                    try {
                        // Unhide app
                        devicePolicyManager.setApplicationHidden(
                            adminComponentName,
                            packageName,
                            false
                        )

                        // Unsuspend app
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                            devicePolicyManager.setPackagesSuspended(
                                adminComponentName,
                                arrayOf(packageName),
                                false
                            )
                        }
                    } catch (e: Exception) {
                        Log.w(TAG, "Failed to unblock $packageName", e)
                    }
                }
            }

            Log.i(TAG, "Unblocked all apps")
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()

        } catch (e: Exception) {
            Log.e(TAG, "Failed to unblock device", e)
        }
    }

    private fun enableKioskMode() {
        try {
            // Set only Screen Time app as allowed in lock task mode
            devicePolicyManager.setLockTaskPackages(
                adminComponentName,
                arrayOf(packageName)
            )

            Log.i(TAG, "Kiosk mode enabled")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to enable kiosk mode", e)
        }
    }

    private fun disableKioskMode() {
        try {
            // Remove lock task restrictions
            devicePolicyManager.setLockTaskPackages(
                adminComponentName,
                emptyArray()
            )

            Log.i(TAG, "Kiosk mode disabled")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to disable kiosk mode", e)
        }
    }

    private fun isSystemCritical(packageName: String): Boolean {
        // List of system-critical apps that should never be blocked
        val criticalPackages = setOf(
            "com.android.systemui",
            "com.android.settings",
            "com.android.launcher",
            "com.android.launcher3",
            "com.google.android.dialer",
            "com.android.phone",
            "com.android.contacts",
            "com.android.providers.contacts",
            "android",
            "com.android.vending", // Play Store (for updates)
        )

        return criticalPackages.any { packageName.contains(it, ignoreCase = true) }
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            "blocking",
            "App Blocking",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Active when apps are blocked due to time depletion"
            setShowBadge(false)
        }
        notificationManager.createNotificationChannel(channel)
    }

    private fun createBlockNotification(): Notification {
        return NotificationCompat.Builder(this, "blocking")
            .setContentTitle("Screen Time Protection Active")
            .setContentText("Apps are blocked. Complete tasks to earn more time.")
            .setSmallIcon(R.drawable.ic_notification)
            .setOngoing(true)
            .build()
    }
}
