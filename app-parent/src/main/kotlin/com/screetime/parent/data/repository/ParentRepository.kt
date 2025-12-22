package com.screetime.parent.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.screetime.parent.data.model.ChildInfo
import com.screetime.parent.data.model.Task
import com.screetime.parent.data.model.TaskStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ParentRepository @Inject constructor() {

    private val firestore = FirebaseFirestore.getInstance()
    private val TAG = "ParentRepository"

    // Hardcoded for testing - in production, this would come from authentication
    private val childId = "test-child-001"

    fun getChildInfo(): Flow<ChildInfo?> = flow {
        try {
            Log.d(TAG, "Fetching child info for: $childId")

            // Get child's name
            val userDoc = firestore.collection("users")
                .document(childId)
                .get()
                .await()

            val childName = userDoc.getString("name") ?: "Unknown"

            // Get time balance
            val balanceDoc = firestore.collection("timeBalances")
                .document(childId)
                .get()
                .await()

            val remainingMinutes = balanceDoc.getLong("remainingMinutes")?.toInt() ?: 0
            val totalMinutes = balanceDoc.getLong("totalMinutes")?.toInt() ?: 0

            // Get pending tasks count
            val pendingTasks = firestore.collection("tasks")
                .whereEqualTo("childId", childId)
                .whereEqualTo("status", "COMPLETED")
                .get()
                .await()

            val childInfo = ChildInfo(
                id = childId,
                name = childName,
                remainingMinutes = remainingMinutes,
                totalMinutes = totalMinutes,
                pendingTasksCount = pendingTasks.size()
            )

            Log.d(TAG, "Child info fetched: $childInfo")
            emit(childInfo)
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching child info", e)
            emit(null)
        }
    }

    fun getPendingTasks(): Flow<List<Task>> = flow {
        try {
            Log.d(TAG, "Fetching pending tasks for child: $childId")
            val snapshot = firestore.collection("tasks")
                .whereEqualTo("childId", childId)
                .whereEqualTo("status", "COMPLETED")
                .orderBy("completedAt", Query.Direction.DESCENDING)
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
                        isRecurring = doc.getBoolean("isRecurring") ?: false,
                        completedAt = doc.getLong("completedAt")
                    )
                } catch (e: Exception) {
                    Log.e(TAG, "Error parsing task document: ${doc.id}", e)
                    null
                }
            }

            Log.d(TAG, "Pending tasks fetched: ${tasks.size} tasks")
            emit(tasks)
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching pending tasks", e)
            emit(emptyList())
        }
    }

    suspend fun approveTask(taskId: String): Boolean {
        return try {
            Log.d(TAG, "Approving task: $taskId")
            firestore.collection("tasks")
                .document(taskId)
                .update(
                    mapOf(
                        "status" to "APPROVED",
                        "approvedAt" to com.google.firebase.Timestamp.now(),
                        "approvedBy" to "test-parent-001"
                    )
                )
                .await()
            Log.d(TAG, "Task approved successfully")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error approving task", e)
            false
        }
    }

    suspend fun rejectTask(taskId: String, reason: String): Boolean {
        return try {
            Log.d(TAG, "Rejecting task: $taskId")
            firestore.collection("tasks")
                .document(taskId)
                .update(
                    mapOf(
                        "status" to "REJECTED",
                        "rejectedAt" to com.google.firebase.Timestamp.now(),
                        "rejectedBy" to "test-parent-001",
                        "rejectionReason" to reason
                    )
                )
                .await()
            Log.d(TAG, "Task rejected successfully")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error rejecting task", e)
            false
        }
    }
}
