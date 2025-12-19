package com.screetime.child.ui.blocked

import android.app.Activity
import android.app.ActivityManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.screetime.core.designsystem.theme.ScreenTimeTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Blocked screen shown when time is depleted.
 * This activity runs in kiosk mode (lock task mode) preventing child from exiting.
 */
@AndroidEntryPoint
class BlockedActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Start lock task mode (kiosk mode)
        startLockTask()

        setContent {
            ScreenTimeTheme {
                BlockedScreen(
                    onRequestUnlock = {
                        // TODO: Send unlock request to parent
                    }
                )
            }
        }
    }

    override fun onBackPressed() {
        // Prevent back button
        // Do nothing
    }

    @Deprecated("Deprecated in Java")
    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        // Prevent minimizing
        bringToFront()
    }

    private fun bringToFront() {
        val activityManager = getSystemService(Activity.ACTIVITY_SERVICE) as ActivityManager
        activityManager.moveTaskToFront(taskId, 0)
    }

    override fun onDestroy() {
        super.onDestroy()
        // Only stop lock task if we're actually stopping
        try {
            stopLockTask()
        } catch (e: Exception) {
            // Ignore - might not be in lock task mode
        }
    }
}

@Composable
fun BlockedScreen(
    onRequestUnlock: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.errorContainer),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Lock icon
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "Locked",
                modifier = Modifier.size(120.dp),
                tint = MaterialTheme.colorScheme.onErrorContainer
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Title
            Text(
                text = "Time's Up!",
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.onErrorContainer,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Message
            Text(
                text = "You've used all your screen time for today.\nComplete tasks to earn more time!",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onErrorContainer,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Available tasks section
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Available Tasks",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "No tasks available.\nPlease ask your parent to add tasks.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Emergency unlock request button
            OutlinedButton(
                onClick = onRequestUnlock,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Request Emergency Unlock")
            }
        }
    }
}
