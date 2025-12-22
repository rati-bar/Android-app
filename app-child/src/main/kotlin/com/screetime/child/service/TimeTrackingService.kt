package com.screetime.child.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.firestore.FirebaseFirestore
import com.screetime.child.R
import com.screetime.child.ui.MainActivity
import com.screetime.core.common.Constants
import com.screetime.core.common.formatAsTimeDetailed
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.util.concurrent.atomic.AtomicInteger
import kotlin.coroutines.CoroutineContext

/**
 * Foreground service that tracks remaining screen time and triggers blocking when depleted.
 * Runs continuously to ensure time limits are enforced.
 */
class TimeTrackingService : Service(), CoroutineScope {

    private val job = SupervisorJob()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Default + job

    private val remainingSeconds = AtomicInteger(0)
    private var isTracking = false
    private var trackingJob: Job? = null
    private var syncJob: Job? = null
    private var lastSyncedSeconds = 0

    private lateinit var notificationManager: NotificationManager
    private val firestore = FirebaseFirestore.getInstance()
    private val childId = "test-child-001" // TODO: Get from auth

    companion object {
        private const val TAG = "TimeTrackingService"
        const val ACTION_START_TRACKING = "com.screetime.child.START_TRACKING"
        const val ACTION_STOP_TRACKING = "com.screetime.child.STOP_TRACKING"
        const val ACTION_UPDATE_TIME = "com.screetime.child.UPDATE_TIME"
        const val EXTRA_REMAINING_SECONDS = "remaining_seconds"
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Service created")
        notificationManager = getSystemService(NotificationManager::class.java)
        createNotificationChannels()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand: ${intent?.action}")

        when (intent?.action) {
            ACTION_START_TRACKING -> {
                val seconds = intent.getIntExtra(EXTRA_REMAINING_SECONDS, 0)
                startTimeTracking(seconds)
            }
            ACTION_STOP_TRACKING -> {
                stopTimeTracking()
            }
            ACTION_UPDATE_TIME -> {
                val seconds = intent.getIntExtra(EXTRA_REMAINING_SECONDS, 0)
                updateRemainingTime(seconds)
            }
            else -> {
                // Default: fetch time from Firestore
                launch {
                    val seconds = fetchTimeFromFirestore()
                    startTimeTracking(seconds)
                }
            }
        }

        // Start as foreground service
        startForeground(Constants.NOTIFICATION_ID_TIME_TRACKING, createNotification())

        return START_STICKY // Restart if killed
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "Service destroyed")
        trackingJob?.cancel()
        syncJob?.cancel()
        // Final sync before destroy
        launch {
            syncTimeWithFirestore()
        }
        job.cancel()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        Log.d(TAG, "Task removed - restarting service")

        // Restart service when task is removed
        val restartIntent = Intent(applicationContext, TimeTrackingService::class.java)
        startService(restartIntent)
    }

    private fun startTimeTracking(seconds: Int) {
        if (isTracking) {
            Log.d(TAG, "Already tracking")
            return
        }

        Log.i(TAG, "Starting time tracking with $seconds seconds")
        remainingSeconds.set(seconds)
        lastSyncedSeconds = seconds
        isTracking = true

        trackingJob = launch {
            while (isActive && isTracking) {
                delay(Constants.TIME_TRACKING_INTERVAL_MS)

                val current = remainingSeconds.decrementAndGet()

                // Update notification
                updateNotification(current)

                // Save progress periodically
                if (current % 60 == 0) {
                    saveTime(current)
                }

                // Check if time depleted
                if (current <= 0) {
                    handleTimeDepletion()
                    break
                }

                // Show warnings
                when (current / 60) {
                    Constants.TIME_WARNING_THRESHOLD_MINUTES -> showTimeLowWarning(current)
                    Constants.TIME_CRITICAL_THRESHOLD_MINUTES -> showTimeCriticalWarning(current)
                }

                // Validate time integrity every minute
                if (current % 60 == 0) {
                    validateTimeIntegrity()
                }
            }
        }

        // Start periodic Firestore sync
        startFirestoreSync()
    }

    private fun startFirestoreSync() {
        syncJob?.cancel()
        syncJob = launch {
            while (isActive && isTracking) {
                delay(Constants.TIME_SYNC_INTERVAL_MS) // Sync every minute
                syncTimeWithFirestore()
            }
        }
    }

    private fun stopTimeTracking() {
        Log.i(TAG, "Stopping time tracking")
        isTracking = false
        trackingJob?.cancel()
        saveTime(remainingSeconds.get())
    }

    private fun updateRemainingTime(seconds: Int) {
        Log.d(TAG, "Updating remaining time to $seconds seconds")
        remainingSeconds.set(seconds)
        saveTime(seconds)
        updateNotification(seconds)
    }

    private fun handleTimeDepletion() {
        Log.w(TAG, "Time depleted!")
        isTracking = false

        // Save state
        saveTime(0)

        // Show depleted notification
        showTimeDepletedNotification()

        // Notify parent via Firestore
        notifyParentOfTimeDepletion()

        if (com.screetime.child.BuildConfig.ENFORCEMENT_ENABLED) {
            // PRO VERSION: Block all apps and enter kiosk mode
            Log.i(TAG, "Enforcement enabled - blocking device")

            // Start app blocking service
            val blockIntent = Intent(this, AppBlockingService::class.java).apply {
                action = AppBlockingService.ACTION_BLOCK_DEVICE
            }
            startService(blockIntent)

            // Launch blocked activity (kiosk mode)
            val blockedIntent = Intent(this, com.screetime.child.ui.blocked.BlockedActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(blockedIntent)
        } else {
            // PLAY STORE VERSION: Just show dismissible notification and alert
            Log.i(TAG, "Notification mode - showing time depleted screen")

            // Launch time depleted activity (dismissible)
            val depletedIntent = Intent(this, com.screetime.child.ui.timedepleted.TimeDepletedActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            startActivity(depletedIntent)
        }
    }

    private fun notifyParentOfTimeDepletion() {
        // TODO: Implement Firestore notification to parent
        // This will trigger Cloud Function to send FCM to parent
        Log.i(TAG, "Notifying parent of time depletion")
    }

    private fun createNotificationChannels() {
        val timeTrackingChannel = NotificationChannel(
            Constants.CHANNEL_ID_TIME_TRACKING,
            "Time Tracking",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Shows remaining screen time"
            setShowBadge(false)
        }

        val alertsChannel = NotificationChannel(
            Constants.CHANNEL_ID_GENERAL,
            "Time Alerts",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Alerts for low time and time depletion"
        }

        notificationManager.createNotificationChannel(timeTrackingChannel)
        notificationManager.createNotificationChannel(alertsChannel)
    }

    private fun createNotification(): Notification {
        val seconds = remainingSeconds.get()
        val timeText = seconds.formatAsTimeDetailed()

        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, Constants.CHANNEL_ID_TIME_TRACKING)
            .setContentTitle("Screen Time Remaining")
            .setContentText(timeText)
            .setSmallIcon(R.drawable.ic_notification)
            .setOngoing(true)
            .setContentIntent(pendingIntent)
            .build()
    }

    private fun updateNotification(seconds: Int) {
        val notification = createNotification()
        notificationManager.notify(Constants.NOTIFICATION_ID_TIME_TRACKING, notification)
    }

    private fun showTimeLowWarning(seconds: Int) {
        val minutes = seconds / 60
        val notification = NotificationCompat.Builder(this, Constants.CHANNEL_ID_GENERAL)
            .setContentTitle(getString(R.string.notification_time_low_title))
            .setContentText(getString(R.string.notification_time_low_message, minutes))
            .setSmallIcon(R.drawable.ic_notification)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(Constants.NOTIFICATION_ID_TIME_LOW, notification)
    }

    private fun showTimeCriticalWarning(seconds: Int) {
        // Similar to low warning but with different styling/urgency
        showTimeLowWarning(seconds)
    }

    private fun showTimeDepletedNotification() {
        val notification = NotificationCompat.Builder(this, Constants.CHANNEL_ID_GENERAL)
            .setContentTitle(getString(R.string.time_depleted))
            .setContentText(getString(R.string.blocked_message))
            .setSmallIcon(R.drawable.ic_notification)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setAutoCancel(false)
            .build()

        notificationManager.notify(Constants.NOTIFICATION_ID_TIME_DEPLETED, notification)
    }

    private fun validateTimeIntegrity() {
        // TODO: Implement time validation against server time
        // Check for system time manipulation
    }

    private fun saveTime(seconds: Int) {
        val prefs = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE)
        prefs.edit().apply {
            putInt(Constants.KEY_REMAINING_SECONDS, seconds)
            putLong(Constants.KEY_LAST_SYNC_TIME, System.currentTimeMillis())
            apply()
        }
    }

    private fun loadSavedTime(): Int {
        val prefs = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE)
        return prefs.getInt(Constants.KEY_REMAINING_SECONDS, 0)
    }

    /**
     * Fetch current remaining time from Firestore
     */
    private suspend fun fetchTimeFromFirestore(): Int {
        return try {
            Log.d(TAG, "Fetching time from Firestore")
            val snapshot = firestore.collection("timeBalances")
                .document(childId)
                .get()
                .await()

            val remainingMinutes = snapshot.getLong("remainingMinutes")?.toInt() ?: 0
            val seconds = remainingMinutes * 60
            Log.d(TAG, "Fetched $remainingMinutes minutes ($seconds seconds) from Firestore")
            seconds
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching time from Firestore", e)
            // Fallback to saved time
            loadSavedTime()
        }
    }

    /**
     * Sync time with Firestore - updates remainingMinutes and usedMinutes
     */
    private suspend fun syncTimeWithFirestore() {
        try {
            val currentSeconds = remainingSeconds.get()
            val secondsUsed = lastSyncedSeconds - currentSeconds

            if (secondsUsed <= 0) {
                // No time used since last sync
                return
            }

            Log.d(TAG, "Syncing with Firestore: $secondsUsed seconds used")

            val balanceRef = firestore.collection("timeBalances").document(childId)
            val snapshot = balanceRef.get().await()

            if (!snapshot.exists()) {
                Log.e(TAG, "Time balance document doesn't exist")
                return
            }

            val currentRemaining = snapshot.getLong("remainingMinutes")?.toInt() ?: 0
            val currentUsed = snapshot.getLong("usedMinutes")?.toInt() ?: 0

            // Convert seconds to minutes (round up)
            val minutesUsed = (secondsUsed + 59) / 60

            balanceRef.update(
                mapOf(
                    "usedMinutes" to (currentUsed + minutesUsed),
                    "remainingMinutes" to maxOf(0, currentRemaining - minutesUsed),
                    "lastUpdatedAt" to com.google.firebase.Timestamp.now()
                )
            ).await()

            lastSyncedSeconds = currentSeconds
            Log.d(TAG, "Firestore sync successful: $minutesUsed minutes deducted")
        } catch (e: Exception) {
            Log.e(TAG, "Error syncing with Firestore", e)
        }
    }
}
