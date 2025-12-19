package com.screetime.child.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.screetime.child.R

/**
 * Firebase Cloud Messaging service for receiving push notifications.
 */
class ScreenTimeFCMService : FirebaseMessagingService() {

    companion object {
        private const val TAG = "FCMService"
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d(TAG, "Message received from: ${message.from}")

        // Handle data payload
        message.data.isNotEmpty().let {
            Log.d(TAG, "Message data: ${message.data}")
            handleDataMessage(message.data)
        }

        // Handle notification payload
        message.notification?.let {
            Log.d(TAG, "Message notification: ${it.title}")
            showNotification(it.title, it.body)
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "New FCM token: $token")

        // TODO: Send token to server
        sendTokenToServer(token)
    }

    private fun handleDataMessage(data: Map<String, String>) {
        when (data["type"]) {
            "TASK_APPROVED" -> {
                // Task approved - add time
                val minutes = data["minutes"]?.toIntOrNull() ?: 0
                val taskId = data["taskId"]
                Log.i(TAG, "Task approved: +$minutes minutes")

                // TODO: Update time balance and show notification
                updateTimeBalance(minutes)
            }
            "TASK_REJECTED" -> {
                val taskId = data["taskId"]
                val reason = data["reason"]
                Log.i(TAG, "Task rejected: $reason")

                // TODO: Show notification
            }
            "EMERGENCY_UNLOCK" -> {
                val duration = data["duration"]?.toIntOrNull() ?: 15
                Log.i(TAG, "Emergency unlock granted: $duration minutes")

                // TODO: Unblock device temporarily
                handleEmergencyUnlock(duration)
            }
            "PARENT_MESSAGE" -> {
                val message = data["message"]
                Log.i(TAG, "Parent message: $message")

                // TODO: Show notification
            }
        }
    }

    private fun showNotification(title: String?, body: String?) {
        val notificationManager = getSystemService(NotificationManager::class.java)

        // Create channel
        val channel = NotificationChannel(
            "fcm_messages",
            "Messages",
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationManager.createNotificationChannel(channel)

        // Build notification
        val notification = NotificationCompat.Builder(this, "fcm_messages")
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(R.drawable.ic_notification)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }

    private fun sendTokenToServer(token: String) {
        // TODO: Save FCM token to Firestore user document
    }

    private fun updateTimeBalance(minutes: Int) {
        // TODO: Add minutes to time balance and update service
    }

    private fun handleEmergencyUnlock(durationMinutes: Int) {
        // TODO: Temporarily unblock device
    }
}
