package com.screetime.child

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.HiltAndroidApp

/**
 * Application class for Screen Time Child App.
 * Initializes Hilt, Firebase, and starts critical services.
 */
@HiltAndroidApp
class ScreenTimeChildApp : Application() {

    companion object {
        private const val TAG = "ScreenTimeChildApp"
    }

    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "App starting...")

        // Initialize Firebase
        initializeFirebase()

        // Start time tracking service if device owner is set
        startTimeTrackingIfNeeded()

        // Perform security checks
        performSecurityChecks()
    }

    private fun initializeFirebase() {
        try {
            // Skip Firebase initialization in debug builds or if using demo config
            if (BuildConfig.DEBUG) {
                Log.i(TAG, "Skipping Firebase initialization in debug build")
                return
            }

            FirebaseApp.initializeApp(this)
            Log.i(TAG, "Firebase initialized")

            // Configure Crashlytics
            FirebaseCrashlytics.getInstance().apply {
                setCrashlyticsCollectionEnabled(!BuildConfig.DEBUG)
                setCustomKey("app_type", "child")
                setCustomKey("version_name", BuildConfig.VERSION_NAME)
                setCustomKey("version_code", BuildConfig.VERSION_CODE)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize Firebase", e)
        }
    }

    private fun startTimeTrackingIfNeeded() {
        // Only check device owner in enforcement-enabled (Pro) version
        if (!BuildConfig.ENFORCEMENT_ENABLED) {
            Log.i(TAG, "Play Store Lite version - skipping device owner check")
            return
        }

        // Check if device owner is set
        if (com.screetime.child.service.ScreenTimeDeviceAdminReceiver.isDeviceOwner(this)) {
            Log.i(TAG, "Device owner is set - starting time tracking service")

            // TODO: Start time tracking service
            // val intent = Intent(this, TimeTrackingService::class.java)
            // startForegroundService(intent)
        } else {
            Log.w(TAG, "Device owner NOT set - app will not function properly")
        }
    }

    private fun performSecurityChecks() {
        // Security checks will be performed by the security layer
        // This is just a placeholder for initialization
        Log.d(TAG, "Security checks initialized")
    }
}
