package com.screetime.child.ui.timedepleted

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.screetime.child.BuildConfig
import com.screetime.core.designsystem.theme.ScreenTimeTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Time depleted notification screen.
 *
 * For Play Store version: Dismissible reminder screen
 * For Pro version: Could redirect to blocked kiosk mode
 */
@AndroidEntryPoint
class TimeDepletedActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ScreenTimeTheme {
                if (BuildConfig.ENFORCEMENT_ENABLED) {
                    // Pro version: Show option to enter kiosk mode
                    TimeDepletedScreenWithEnforcement(
                        onDismiss = { finish() },
                        onViewTasks = { navigateToTasks() },
                        onEnterKioskMode = { enterKioskMode() }
                    )
                } else {
                    // Play Store version: Just a reminder that can be dismissed
                    TimeDepletedScreenLite(
                        onDismiss = { finish() },
                        onViewTasks = { navigateToTasks() }
                    )
                }
            }
        }
    }

    private fun navigateToTasks() {
        // Navigate to main activity task list
        val intent = Intent(this, com.screetime.child.ui.MainActivity::class.java).apply {
            putExtra("NAVIGATE_TO", "TASKS")
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        startActivity(intent)
        finish()
    }

    private fun enterKioskMode() {
        // Navigate to BlockedActivity (kiosk mode) - Pro version only
        val intent = Intent(this, com.screetime.child.ui.blocked.BlockedActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }
}

/**
 * Play Store Lite version - Dismissible reminder
 */
@Composable
fun TimeDepletedScreenLite(
    onDismiss: () -> Unit,
    onViewTasks: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Icon
            Icon(
                imageVector = Icons.Default.AccessTime,
                contentDescription = "Time's up",
                modifier = Modifier.size(120.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Title
            Text(
                text = "⏰ Screen Time Ended",
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Message
            Text(
                text = "You've used all your earned time for today.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Complete tasks to earn more screen time!",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Call to action
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "View your tasks and start earning time",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Buttons
            Button(
                onClick = onViewTasks,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("View Tasks")
            }

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("I Understand")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Info text
            Text(
                text = "Your parent has been notified",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Pro version - With enforcement option
 */
@Composable
fun TimeDepletedScreenWithEnforcement(
    onDismiss: () -> Unit,
    onViewTasks: () -> Unit,
    onEnterKioskMode: () -> Unit
) {
    var showEnforcementDialog by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.errorContainer
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.AccessTime,
                contentDescription = "Time's up",
                modifier = Modifier.size(120.dp),
                tint = MaterialTheme.colorScheme.onErrorContainer
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "⏰ Time's Up!",
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.onErrorContainer,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "You've used all your screen time.\nComplete tasks to earn more!",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onErrorContainer,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(48.dp))

            Button(
                onClick = onViewTasks,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("View Tasks")
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = { showEnforcementDialog = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Enable Full Lock Mode")
            }

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Dismiss")
            }
        }
    }

    if (showEnforcementDialog) {
        AlertDialog(
            onDismissRequest = { showEnforcementDialog = false },
            title = { Text("Enable Full Lock Mode?") },
            text = {
                Text("This will lock the device to only Screen Time app until you earn more time. You won't be able to exit.")
            },
            confirmButton = {
                Button(onClick = {
                    showEnforcementDialog = false
                    onEnterKioskMode()
                }) {
                    Text("Enable")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEnforcementDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
