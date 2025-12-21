import { onSchedule } from 'firebase-functions/v2/scheduler';
import * as admin from 'firebase-admin';

const db = admin.firestore();
const messaging = admin.messaging();

/**
 * Scheduled Cloud Function that runs daily at midnight to reset tasks and time.
 * Schedule: Every day at 00:00 (midnight)
 */
export const dailyReset = onSchedule(
  {
    schedule: '0 0 * * *',
    timeZone: 'America/New_York', // Change to your timezone
  },
  async (event) => {
    console.log('Starting daily reset...');

    try {
      const batch = db.batch();
      let resetCount = 0;

      // 1. Reset all time balances
      const balancesSnapshot = await db.collection('timeBalances').get();

      balancesSnapshot.forEach(doc => {
        const data = doc.data();

        batch.update(doc.ref, {
          usedMinutes: 0,
          remainingMinutes: data.totalMinutes,
          isBlocked: false,
          blockReason: null,
          lastResetAt: admin.firestore.FieldValue.serverTimestamp(),
          lastUpdatedAt: admin.firestore.FieldValue.serverTimestamp()
        });

        // Log reset event
        const logRef = db.collection('timeLogs').doc();
        batch.set(logRef, {
          childId: doc.id,
          action: 'RESET',
          minutesChange: 0,
          balanceAfter: data.totalMinutes,
          taskId: null,
          timestamp: admin.firestore.FieldValue.serverTimestamp(),
          reason: 'Daily automatic reset'
        });

        resetCount++;
      });

      // 2. Reset recurring tasks
      const tasksSnapshot = await db.collection('tasks')
        .where('isRecurring', '==', true)
        .where('status', 'in', ['COMPLETED', 'APPROVED', 'REJECTED'])
        .get();

      tasksSnapshot.forEach(doc => {
        batch.update(doc.ref, {
          status: 'PENDING',
          completedAt: null,
          approvedAt: null,
          approvedBy: null,
          rejectedAt: null,
          rejectedBy: null,
          rejectionReason: null
        });
      });

      // 3. Commit all changes
      await batch.commit();

      // 4. Send notifications to all children
      const usersSnapshot = await db.collection('users')
        .where('role', '==', 'CHILD')
        .get();

      const notificationPromises = usersSnapshot.docs
        .filter(doc => doc.data().fcmToken)
        .map(doc =>
          messaging.send({
            token: doc.data().fcmToken!,
            notification: {
              title: 'New Day Started! 🌅',
              body: 'Your time has been reset. Complete today\'s tasks to earn screen time!'
            },
            data: {
              type: 'DAILY_RESET'
            }
          }).catch(error => {
            console.error(`Failed to send notification to ${doc.id}:`, error);
          })
        );

      await Promise.all(notificationPromises);

      console.log(`Daily reset completed successfully. Reset ${resetCount} time balances and ${tasksSnapshot.size} tasks.`);

    } catch (error) {
      console.error('Error during daily reset:', error);
      throw error;
    }
  });
