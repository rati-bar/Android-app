package com.screetime.child.security

import android.util.Log
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Detects if device is rooted, which could allow bypass of parental controls.
 */
@Singleton
class RootDetector @Inject constructor() {

    companion object {
        private const val TAG = "RootDetector"
    }

    /**
     * Checks if device is rooted using multiple detection methods.
     */
    fun isDeviceRooted(): Boolean {
        return checkSuBinary() ||
                checkRootApps() ||
                checkBuildTags() ||
                checkRWPaths() ||
                checkDangerousProps()
    }

    /**
     * Check for su binary in common locations.
     */
    private fun checkSuBinary(): Boolean {
        val paths = arrayOf(
            "/system/app/Superuser.apk",
            "/system/xbin/su",
            "/system/bin/su",
            "/system/sbin/su",
            "/sbin/su",
            "/vendor/bin/su",
            "/data/local/xbin/su",
            "/data/local/bin/su",
            "/data/local/su",
            "/system/sd/xbin/su",
            "/system/bin/failsafe/su",
            "/data/adb/su"
        )

        for (path in paths) {
            if (File(path).exists()) {
                Log.w(TAG, "ROOT DETECTED: su binary found at $path")
                return true
            }
        }

        return false
    }

    /**
     * Check for known root/superuser apps.
     */
    private fun checkRootApps(): Boolean {
        val rootApps = arrayOf(
            "com.noshufou.android.su",
            "com.noshufou.android.su.elite",
            "eu.chainfire.supersu",
            "com.koushikdutta.superuser",
            "com.thirdparty.superuser",
            "com.yellowes.su",
            "com.topjohnwu.magisk",
            "com.kingroot.kinguser",
            "com.kingo.root",
            "com.smedialink.oneclickroot",
            "com.zhiqupk.root.global",
            "com.alephzain.framaroot"
        )

        for (packageName in rootApps) {
            if (isPackageInstalled(packageName)) {
                Log.w(TAG, "ROOT DETECTED: Root app found - $packageName")
                return true
            }
        }

        return false
    }

    /**
     * Check build tags for test-keys (unofficial build).
     */
    private fun checkBuildTags(): Boolean {
        val buildTags = android.os.Build.TAGS
        if (buildTags != null && buildTags.contains("test-keys")) {
            Log.w(TAG, "ROOT DETECTED: test-keys build tag")
            return true
        }
        return false
    }

    /**
     * Check if system directories are writable.
     */
    private fun checkRWPaths(): Boolean {
        val paths = arrayOf("/system", "/system/bin", "/system/sbin", "/system/xbin", "/vendor/bin", "/sbin", "/etc")

        for (path in paths) {
            val file = File(path)
            if (file.exists() && file.canWrite()) {
                Log.w(TAG, "ROOT DETECTED: Writable system directory - $path")
                return true
            }
        }

        return false
    }

    /**
     * Check for dangerous system properties.
     */
    private fun checkDangerousProps(): Boolean {
        try {
            val process = Runtime.getRuntime().exec("getprop ro.debuggable")
            val reader = process.inputStream.bufferedReader()
            val value = reader.readLine()
            reader.close()

            if (value == "1") {
                Log.w(TAG, "ROOT DETECTED: debuggable property set")
                return true
            }
        } catch (e: Exception) {
            // Ignore
        }

        return false
    }

    private fun isPackageInstalled(packageName: String): Boolean {
        return try {
            File("/data/data/$packageName").exists()
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Get detailed root detection report.
     */
    fun getRootDetectionReport(): RootDetectionReport {
        return RootDetectionReport(
            isRooted = isDeviceRooted(),
            suBinaryFound = checkSuBinary(),
            rootAppsFound = checkRootApps(),
            testKeysBuild = checkBuildTags(),
            systemWritable = checkRWPaths(),
            debuggableProperty = checkDangerousProps()
        )
    }
}

data class RootDetectionReport(
    val isRooted: Boolean,
    val suBinaryFound: Boolean,
    val rootAppsFound: Boolean,
    val testKeysBuild: Boolean,
    val systemWritable: Boolean,
    val debuggableProperty: Boolean
)
