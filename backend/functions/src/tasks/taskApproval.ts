import { onDocumentUpdated } from 'firebase-functions/v2/firestore';
import { HttpsError } from 'firebase-functions/v2/https';
import * as admin from 'firebase-admin';

const db = admin.firestore();
const messaging = admin.messaging();

/**
 * Cloud Function triggered when a task status changes to APPROVED.
 * Grants time minutes to the child's balance and sends notification.
 */
export const onTaskApproved = onDocumentUpdated('tasks/{taskId}', async (event) => {
    if (!event.data) {
      console.error('No data in event');
      return;
    }

    const before = event.data.before.data();
    const after = event.data.after.data();
    const taskId = event.params.taskId;

    if (!before || !after) {
      console.error('Missing before or after data');
      return;
    }

    // Check if task was just approved
    if (before.status !== 'APPROVED' && after.status === 'APPROVED') {
      console.log(`Task approved: ${taskId}`);

      const childId = after.childId;
      const rewardMinutes = after.rewardMinutes;
      const taskTitle = after.title;

      try {
        // Update child's time balance
        const balanceRef = db.collection('timeBalances').doc(childId);
        const balanceDoc = await balanceRef.get();

        if (!balanceDoc.exists) {
          // Create initial balance
          await balanceRef.set({
            childId: childId,
            totalMinutes: rewardMinutes,
            usedMinutes: 0,
            remainingMinutes: rewardMinutes,
            lastResetAt: admin.firestore.FieldValue.serverTimestamp(),
            lastUpdatedAt: admin.firestore.FieldValue.serverTimestamp(),
            isBlocked: false,
            blockReason: null
          });
        } else {
          // Update existing balance
          await balanceRef.update({
            totalMinutes: admin.firestore.FieldValue.increment(rewardMinutes),
            remainingMinutes: admin.firestore.FieldValue.increment(rewardMinutes),
            lastUpdatedAt: admin.firestore.FieldValue.serverTimestamp()
          });
        }

        // Log the transaction
        await db.collection('timeLogs').add({
          childId: childId,
          action: 'EARNED',
          minutesChange: rewardMinutes,
          balanceAfter: balanceDoc.exists ?
            balanceDoc.data()!.totalMinutes + rewardMinutes : rewardMinutes,
          taskId: taskId,
          timestamp: admin.firestore.FieldValue.serverTimestamp(),
          reason: `Task approved: ${taskTitle}`
        });

        // Send notification to child
        const childDoc = await db.collection('users').doc(childId).get();
        if (childDoc.exists && childDoc.data()!.fcmToken) {
          await messaging.send({
            token: childDoc.data()!.fcmToken,
            notification: {
              title: 'Task Approved! 🎉',
              body: `You earned ${rewardMinutes} minutes for "${taskTitle}"`
            },
            data: {
              type: 'TASK_APPROVED',
              taskId: taskId,
              minutes: rewardMinutes.toString()
            }
          });
        }

        console.log(`Successfully granted ${rewardMinutes} minutes to ${childId}`);

      } catch (error) {
        console.error('Error processing task approval:', error);
        throw new HttpsError('internal', 'Failed to process task approval');
      }
    }
  });

/**
 * Cloud Function triggered when a task status changes to REJECTED.
 * Sends notification to child with rejection reason.
 */
export const onTaskRejected = onDocumentUpdated('tasks/{taskId}', async (event) => {
    if (!event.data) {
      console.error('No data in event');
      return;
    }

    const before = event.data.before.data();
    const after = event.data.after.data();
    const taskId = event.params.taskId;

    if (!before || !after) {
      console.error('Missing before or after data');
      return;
    }

    // Check if task was just rejected
    if (before.status !== 'REJECTED' && after.status === 'REJECTED') {
      console.log(`Task rejected: ${taskId}`);

      const childId = after.childId;
      const taskTitle = after.title;
      const rejectionReason = after.rejectionReason || 'No reason provided';

      try {
        // Send notification to child
        const childDoc = await db.collection('users').doc(childId).get();
        if (childDoc.exists && childDoc.data()!.fcmToken) {
          await messaging.send({
            token: childDoc.data()!.fcmToken,
            notification: {
              title: 'Task Not Approved',
              body: `"${taskTitle}" was not approved. ${rejectionReason}`
            },
            data: {
              type: 'TASK_REJECTED',
              taskId: taskId,
              reason: rejectionReason
            }
          });
        }

        console.log(`Rejection notification sent to ${childId}`);

      } catch (error) {
        console.error('Error processing task rejection:', error);
      }
    }
  });
