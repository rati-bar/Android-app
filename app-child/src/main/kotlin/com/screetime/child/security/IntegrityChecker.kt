package com.screetime.child.security

import android.content.Context
import android.content.pm.PackageManager
import android.provider.Settings
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Checks device and app integrity to prevent tampering and bypass attempts.
 */
@Singleton
class IntegrityChecker @Inject constructor(
    @ApplicationContext private val context: Context
) {

    companion object {
        private const val TAG = "IntegrityChecker"
    }

    /**
     * Performs comprehensive integrity check.
     */
    fun performIntegrityCheck(): IntegrityCheckResult {
        val checks = mutableListOf<IntegrityIssue>()

        // Check if ADB is enabled
        if (isAdbEnabled()) {
            checks.add(IntegrityIssue.ADB_ENABLED)
            Log.w(TAG, "ADB is enabled!")
        }

        // Check if developer options are enabled
        if (isDevelopmentSettingsEnabled()) {
            checks.add(IntegrityIssue.DEVELOPER_OPTIONS_ENABLED)
            Log.w(TAG, "Developer options enabled!")
        }

        // Check USB debugging
        if (isUsbDebuggingEnabled()) {
            checks.add(IntegrityIssue.USB_DEBUGGING_ENABLED)
            Log.w(TAG, "USB debugging enabled!")
        }

        // Check if installed from Play Store
        if (!isInstalledFromPlayStore()) {
            checks.add(IntegrityIssue.NOT_FROM_PLAY_STORE)
            Log.w(TAG, "App not installed from Play Store!")
        }

        // Check app signature
        if (!verifyAppSignature()) {
            checks.add(IntegrityIssue.INVALID_SIGNATURE)
            Log.e(TAG, "App signature verification FAILED!")
        }

        // Check for mock location
        if (isMockLocationEnabled()) {
            checks.add(IntegrityIssue.MOCK_LOCATION_ENABLED)
            Log.w(TAG, "Mock location enabled!")
        }

        val passed = checks.isEmpty()
        return IntegrityCheckResult(
            passed = passed,
            issues = checks,
            timestamp = System.currentTimeMillis()
        )
    }

    private fun isAdbEnabled(): Boolean {
        return try {
            Settings.Global.getInt(
                context.contentResolver,
                Settings.Global.ADB_ENABLED,
                0
            ) == 1
        } catch (e: Exception) {
            false
        }
    }

    private fun isDevelopmentSettingsEnabled(): Boolean {
        return try {
            Settings.Global.getInt(
                context.contentResolver,
                Settings.Global.DEVELOPMENT_SETTINGS_ENABLED,
                0
            ) == 1
        } catch (e: Exception) {
            false
        }
    }

    private fun isUsbDebuggingEnabled(): Boolean {
        return isAdbEnabled() // Same as ADB for most purposes
    }

    private fun isInstalledFromPlayStore(): Boolean {
        val installerPackage = try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                context.packageManager.getInstallSourceInfo(context.packageName).installingPackageName
            } else {
                @Suppress("DEPRECATION")
                context.packageManager.getInstallerPackageName(context.packageName)
            }
        } catch (e: Exception) {
            null
        }

        // Valid installers: Play Store, Package Installer (for sideload), null (for debug)
        return installerPackage == "com.android.vending" ||
               installerPackage == "com.google.android.packageinstaller" ||
               installerPackage == null // Allow null for debug builds
    }

    private fun verifyAppSignature(): Boolean {
        return try {
            val packageInfo = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                context.packageManager.getPackageInfo(
                    context.packageName,
                    PackageManager.GET_SIGNING_CERTIFICATES
                )
            } else {
                @Suppress("DEPRECATION")
                context.packageManager.getPackageInfo(
                    context.packageName,
                    PackageManager.GET_SIGNATURES
                )
            }

            // TODO: In production, verify against known release signature hash
            // For now, just check that signature exists
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                packageInfo.signingInfo != null
            } else {
                @Suppress("DEPRECATION")
                packageInfo.signatures?.isNotEmpty() == true
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error verifying signature", e)
            false
        }
    }

    private fun isMockLocationEnabled(): Boolean {
        return try {
            Settings.Secure.getInt(
                context.contentResolver,
                Settings.Secure.ALLOW_MOCK_LOCATION,
                0
            ) == 1
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Check if device has Google Play Services (required for Play Integrity API).
     */
    fun hasPlayServices(): Boolean {
        return try {
            context.packageManager.getPackageInfo("com.google.android.gms", 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }
}

data class IntegrityCheckResult(
    val passed: Boolean,
    val issues: List<IntegrityIssue>,
    val timestamp: Long
)

enum class IntegrityIssue {
    ADB_ENABLED,
    DEVELOPER_OPTIONS_ENABLED,
    USB_DEBUGGING_ENABLED,
    NOT_FROM_PLAY_STORE,
    INVALID_SIGNATURE,
    MOCK_LOCATION_ENABLED,
    PLAY_SERVICES_MISSING,
    DEVICE_INTEGRITY_FAILED
}
