import * as functions from 'firebase-functions';
import * as admin from 'firebase-admin';

const db = admin.firestore();
const messaging = admin.messaging();

/**
 * Cloud Function triggered when a security event is logged.
 * Notifies parent of critical security events.
 */
export const logSecurityEvent = functions.firestore
  .document('securityEvents/{eventId}')
  .onCreate(async (snap, context) => {
    const eventData = snap.data();
    const eventId = context.params.eventId;

    console.log(`Security event logged: ${eventData.eventType}`);

    // Determine if this is a critical event that requires immediate parent notification
    const criticalEvents = [
      'ROOT_DETECTED',
      'DEVICE_ADMIN_REMOVAL_ATTEMPT',
      'FACTORY_RESET_ATTEMPT',
      'TIME_CHANGE_ATTEMPT',
      'INTEGRITY_CHECK_FAILED'
    ];

    const isCritical = criticalEvents.includes(eventData.eventType);

    if (isCritical) {
      try {
        // Get child user to find family
        const childDoc = await db.collection('users').doc(eventData.childId).get();

        if (!childDoc.exists) {
          console.error(`Child user not found: ${eventData.childId}`);
          return;
        }

        const familyId = childDoc.data()!.familyId;

        // Get family to find parent
        const familyDoc = await db.collection('families').doc(familyId).get();

        if (!familyDoc.exists) {
          console.error(`Family not found: ${familyId}`);
          return;
        }

        const parentId = familyDoc.data()!.parentId;

        // Get parent user
        const parentDoc = await db.collection('users').doc(parentId).get();

        if (!parentDoc.exists || !parentDoc.data()!.fcmToken) {
          console.error(`Parent not found or no FCM token: ${parentId}`);
          return;
        }

        // Send critical security alert to parent
        await messaging.send({
          token: parentDoc.data()!.fcmToken,
          notification: {
            title: '🚨 Security Alert',
            body: `${eventData.eventType.replace(/_/g, ' ')} detected on ${childDoc.data()!.name}'s device`
          },
          data: {
            type: 'SECURITY_ALERT',
            eventId: eventId,
            eventType: eventData.eventType,
            childId: eventData.childId,
            severity: 'CRITICAL'
          },
          android: {
            priority: 'high',
            notification: {
              priority: 'max',
              defaultSound: true,
              defaultVibrateTimings: true
            }
          }
        });

        console.log(`Critical security alert sent to parent ${parentId}`);

      } catch (error) {
        console.error('Error processing security event:', error);
      }
    }
  });
