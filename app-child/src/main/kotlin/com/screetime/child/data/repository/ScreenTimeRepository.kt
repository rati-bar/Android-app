package com.screetime.child.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.screetime.child.data.model.Task
import com.screetime.child.data.model.TaskStatus
import com.screetime.child.data.model.TimeBalance
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScreenTimeRepository @Inject constructor() {

    private val firestore = FirebaseFirestore.getInstance()
    private val TAG = "ScreenTimeRepository"

    // Hardcoded child ID for testing - in production, this would come from authentication
    private val childId = "test-child-001"

    fun getTimeBalance(): Flow<TimeBalance?> = flow {
        try {
            Log.d(TAG, "Fetching time balance for child: $childId")
            val snapshot = firestore.collection("timeBalances")
                .document(childId)
                .get()
                .await()

            val balance = snapshot.toObject(TimeBalance::class.java)
            Log.d(TAG, "Time balance fetched: $balance")
            emit(balance)
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching time balance", e)
            emit(null)
        }
    }

    fun getTasks(): Flow<List<Task>> = flow {
        try {
            Log.d(TAG, "Fetching tasks for child: $childId")
            val snapshot = firestore.collection("tasks")
                .whereEqualTo("childId", childId)
                .get()
                .await()

            val tasks = snapshot.documents.mapNotNull { doc ->
                try {
                    Task(
                        id = doc.id,
                        childId = doc.getString("childId") ?: "",
                        familyId = doc.getString("familyId") ?: "",
                        title = doc.getString("title") ?: "",
                        description = doc.getString("description") ?: "",
                        rewardMinutes = doc.getLong("rewardMinutes")?.toInt() ?: 0,
                        status = TaskStatus.valueOf(doc.getString("status") ?: "PENDING"),
                        isRecurring = doc.getBoolean("isRecurring") ?: false
                    )
                } catch (e: Exception) {
                    Log.e(TAG, "Error parsing task document: ${doc.id}", e)
                    null
                }
            }

            Log.d(TAG, "Tasks fetched: ${tasks.size} tasks")
            emit(tasks)
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching tasks", e)
            emit(emptyList())
        }
    }

    suspend fun completeTask(taskId: String): Boolean {
        return try {
            Log.d(TAG, "Completing task: $taskId")
            firestore.collection("tasks")
                .document(taskId)
                .update("status", "COMPLETED")
                .await()
            Log.d(TAG, "Task completed successfully")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error completing task", e)
            false
        }
    }
}
