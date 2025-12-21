import { onDocumentCreated } from 'firebase-functions/v2/firestore';
import * as admin from 'firebase-admin';

const db = admin.firestore();

/**
 * Cloud Function triggered when a new family is created.
 * Sets up default tasks and initializes time balances for children.
 */
export const onFamilyCreated = onDocumentCreated('families/{familyId}', async (event) => {
    const familyId = event.params.familyId;
    const familyData = event.data?.data();

    if (!familyData) {
      console.error('No family data found');
      return;
    }

    console.log(`New family created: ${familyId}`);

    try {
      const batch = db.batch();

      // Create default tasks for the family
      const defaultTasks = [
        { title: 'Make bed', description: 'Make your bed neatly', rewardMinutes: 10 },
        { title: 'Brush teeth (morning)', description: 'Brush your teeth in the morning', rewardMinutes: 5 },
        { title: 'Brush teeth (evening)', description: 'Brush your teeth before bed', rewardMinutes: 5 },
        { title: 'Homework', description: 'Complete your homework', rewardMinutes: 30 },
        { title: 'Reading (20 min)', description: 'Read for 20 minutes', rewardMinutes: 20 },
        { title: 'Clean room', description: 'Clean and organize your room', rewardMinutes: 15 },
        { title: 'Help with dishes', description: 'Help wash or dry the dishes', rewardMinutes: 10 },
        { title: 'Physical activity (30 min)', description: 'Exercise or play outside', rewardMinutes: 20 }
      ];

      // Create tasks for each child
      for (const childId of familyData.childIds) {
        // Initialize time balance
        const balanceRef = db.collection('timeBalances').doc(childId);
        batch.set(balanceRef, {
          childId: childId,
          totalMinutes: 0,
          usedMinutes: 0,
          remainingMinutes: 0,
          lastResetAt: admin.firestore.FieldValue.serverTimestamp(),
          lastUpdatedAt: admin.firestore.FieldValue.serverTimestamp(),
          isBlocked: false,
          blockReason: null
        });

        // Create default tasks
        for (const task of defaultTasks) {
          const taskRef = db.collection('tasks').doc();
          batch.set(taskRef, {
            familyId: familyId,
            title: task.title,
            description: task.description,
            rewardMinutes: task.rewardMinutes,
            childId: childId,
            status: 'PENDING',
            createdAt: admin.firestore.FieldValue.serverTimestamp(),
            completedAt: null,
            approvedAt: null,
            approvedBy: null,
            rejectedAt: null,
            rejectedBy: null,
            rejectionReason: null,
            isRecurring: true,
            recurrenceType: 'DAILY',
            deadline: null
          });
        }
      }

      await batch.commit();

      console.log(`Successfully initialized family ${familyId} with default tasks`);

    } catch (error) {
      console.error('Error setting up new family:', error);
      throw error;
    }
  });
