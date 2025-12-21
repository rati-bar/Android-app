import { onDocumentCreated } from 'firebase-functions/v2/firestore';
import * as admin from 'firebase-admin';

const db = admin.firestore();
const messaging = admin.messaging();

/**
 * Cloud Function triggered when child's time is depleted.
 * Sends notification to parent immediately.
 */
export const onTimeDepletionNotification = onDocumentCreated('timeLogs/{logId}', async (event) => {
    const logData = event.data?.data();

    if (!logData) {
      console.error('No log data found');
      return;
    }

    // Check if this is a time depletion event (remaining time reached 0)
    if (logData.action === 'USED' && logData.balanceAfter === 0) {
      const childId = logData.childId;

      console.log(`Time depleted for child: ${childId}`);

      try {
        // Get child information
        const childDoc = await db.collection('users').doc(childId).get();

        if (!childDoc.exists) {
          console.error(`Child user not found: ${childId}`);
          return;
        }

        const childData = childDoc.data()!;
        const familyId = childData.familyId;

        if (!familyId) {
          console.error(`Child ${childId} has no familyId`);
          return;
        }

        // Get family to find parent
        const familyDoc = await db.collection('families').doc(familyId).get();

        if (!familyDoc.exists) {
          console.error(`Family not found: ${familyId}`);
          return;
        }

        const parentId = familyDoc.data()!.parentId;

        // Get parent information
        const parentDoc = await db.collection('users').doc(parentId).get();

        if (!parentDoc.exists) {
          console.error(`Parent not found: ${parentId}`);
          return;
        }

        const parentData = parentDoc.data()!;

        // Send notification to parent if they have FCM token
        if (parentData.fcmToken) {
          await messaging.send({
            token: parentData.fcmToken,
            notification: {
              title: '⏰ Screen Time Alert',
              body: `${childData.name}'s screen time has ended`
            },
            data: {
              type: 'TIME_DEPLETED',
              childId: childId,
              childName: childData.name,
              timestamp: admin.firestore.Timestamp.now().toMillis().toString()
            },
            android: {
              priority: 'high',
              notification: {
                sound: 'default',
                priority: 'max',
                defaultVibrateTimings: true,
                channelId: 'time_alerts'
              }
            }
          });

          console.log(`Notification sent to parent ${parentId} about child ${childId}`);
        } else {
          console.warn(`Parent ${parentId} has no FCM token`);
        }

        // Create a notification document for parent to see in app
        await db.collection('notifications').add({
          userId: parentId,
          type: 'TIME_DEPLETED',
          title: 'Screen Time Ended',
          body: `${childData.name} has used all their screen time`,
          data: {
            childId: childId,
            childName: childData.name
          },
          timestamp: admin.firestore.FieldValue.serverTimestamp(),
          isRead: false
        });

      } catch (error) {
        console.error('Error sending time depletion notification:', error);
        throw error;
      }
    }
  });

/**
 * Alternative: Simpler version that listens to a dedicated timeDepleted collection
 */
export const onTimeDepletedEvent = onDocumentCreated('timeDepletedEvents/{eventId}', async (event) => {
    const eventData = event.data?.data();

    if (!eventData) {
      console.error('No event data found');
      return;
    }

    const childId = eventData.childId;

    console.log(`Time depleted event for child: ${childId}`);

    try {
      // Get child info
      const childDoc = await db.collection('users').doc(childId).get();
      if (!childDoc.exists) return;

      const childData = childDoc.data()!;
      const familyId = childData.familyId;

      // Get parent info
      const familyDoc = await db.collection('families').doc(familyId).get();
      if (!familyDoc.exists) return;

      const parentId = familyDoc.data()!.parentId;
      const parentDoc = await db.collection('users').doc(parentId).get();
      if (!parentDoc.exists) return;

      const parentData = parentDoc.data()!;

      // Send FCM notification
      if (parentData.fcmToken) {
        await messaging.send({
          token: parentData.fcmToken,
          notification: {
            title: '⏰ Time\'s Up',
            body: `${childData.name} has run out of screen time`
          },
          data: {
            type: 'TIME_DEPLETED',
            childId: childId,
            childName: childData.name
          },
          android: {
            priority: 'high',
            notification: {
              channelId: 'time_alerts',
              priority: 'max'
            }
          }
        });

        console.log(`Parent ${parentId} notified of time depletion`);
      }

    } catch (error) {
      console.error('Error in time depleted event handler:', error);
    }
  });
