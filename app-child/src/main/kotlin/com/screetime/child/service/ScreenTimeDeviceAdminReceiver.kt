package com.screetime.child.service

import android.app.admin.DeviceAdminReceiver
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast

/**
 * Device Admin Receiver for Screen Time parental control.
 * This receiver manages device owner privileges required for app blocking and enforcement.
 */
class ScreenTimeDeviceAdminReceiver : DeviceAdminReceiver() {

    companion object {
        private const val TAG = "DeviceAdminReceiver"

        fun getComponentName(context: Context): ComponentName {
            return ComponentName(context, ScreenTimeDeviceAdminReceiver::class.java)
        }

        fun isDeviceOwner(context: Context): Boolean {
            val dpm = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
            return dpm.isDeviceOwnerApp(context.packageName)
        }

        fun isAdminActive(context: Context): Boolean {
            val dpm = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
            return dpm.isAdminActive(getComponentName(context))
        }
    }

    override fun onEnabled(context: Context, intent: Intent) {
        super.onEnabled(context, intent)
        Log.i(TAG, "Device Admin enabled")
        Toast.makeText(context, "Screen Time protection activated", Toast.LENGTH_LONG).show()

        // Configure device policies
        configureDevicePolicies(context)
    }

    override fun onDisableRequested(context: Context, intent: Intent): CharSequence {
        Log.w(TAG, "Device Admin disable requested")
        // Return message shown to user when they try to disable
        return "Screen Time protection cannot be disabled. Contact your parent if you need to remove this app."
    }

    override fun onDisabled(context: Context, intent: Intent) {
        super.onDisabled(context, intent)
        Log.e(TAG, "Device Admin disabled - protection removed!")
        Toast.makeText(context, "Screen Time protection removed", Toast.LENGTH_LONG).show()
    }

    override fun onPasswordChanged(context: Context, intent: Intent) {
        super.onPasswordChanged(context, intent)
        Log.i(TAG, "Password changed")
    }

    override fun onPasswordFailed(context: Context, intent: Intent) {
        super.onPasswordFailed(context, intent)
        Log.w(TAG, "Password attempt failed")
    }

    override fun onPasswordSucceeded(context: Context, intent: Intent) {
        super.onPasswordSucceeded(context, intent)
        Log.i(TAG, "Password succeeded")
    }

    private fun configureDevicePolicies(context: Context) {
        val dpm = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        val adminComponent = getComponentName(context)

        try {
            // Prevent uninstallation of this app
            dpm.setUninstallBlocked(adminComponent, context.packageName, true)
            Log.i(TAG, "Uninstall blocked for ${context.packageName}")

            // Disable camera if needed (optional, can be configured by parent)
            // dpm.setCameraDisabled(adminComponent, false)

            // Additional security restrictions can be configured here
        } catch (e: Exception) {
            Log.e(TAG, "Failed to configure device policies", e)
        }
    }
}
