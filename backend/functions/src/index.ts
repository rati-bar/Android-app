import * as admin from 'firebase-admin';
import * as functions from 'firebase-functions';

// Initialize Firebase Admin
admin.initializeApp();

// Import function modules
import { onTaskApproved, onTaskRejected } from './tasks/taskApproval';
import { dailyReset } from './time/dailyReset';
import { onFamilyCreated } from './family/familyManagement';
import { logSecurityEvent } from './security/securityMonitoring';
import { onTimeDepletionNotification, onTimeDepletedEvent } from './notifications/timeDepletion';

// Export Cloud Functions

// Task Management
export const taskApproved = onTaskApproved;
export const taskRejected = onTaskRejected;

// Time Management
export const scheduledDailyReset = dailyReset;

// Family Management
export const familyCreated = onFamilyCreated;

// Security
export const securityEventLogged = logSecurityEvent;

// Notifications
export const timeDepletionNotification = onTimeDepletionNotification;
export const timeDepletedEvent = onTimeDepletedEvent;

// Test function
export const helloWorld = functions.https.onRequest((request, response) => {
  response.send("Screen Time Calculator Backend is running!");
});
