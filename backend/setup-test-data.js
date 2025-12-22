// Script to populate Firestore with test data
const admin = require('firebase-admin');

// Initialize Firebase Admin with your project
const serviceAccount = {
  "type": "service_account",
  "project_id": "screentimeapp-rati",
  "private_key_id": "YOUR_PRIVATE_KEY_ID",
  "private_key": "YOUR_PRIVATE_KEY",
  "client_email": "YOUR_CLIENT_EMAIL",
  "client_id": "YOUR_CLIENT_ID",
  "auth_uri": "https://accounts.google.com/o/oauth2/auth",
  "token_uri": "https://oauth2.googleapis.com/token",
  "auth_provider_x509_cert_url": "https://www.googleapis.com/oauth2/v1/certs"
};

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount)
});

const db = admin.firestore();

async function setupTestData() {
  try {
    console.log('Setting up test data...');

    // Create parent user
    const parentId = 'test-parent-001';
    await db.collection('users').doc(parentId).set({
      id: parentId,
      email: 'parent@test.com',
      name: 'Test Parent',
      role: 'PARENT',
      phoneNumber: '+1234567890',
      createdAt: admin.firestore.FieldValue.serverTimestamp(),
      fcmToken: null,
      familyId: 'test-family-001'
    });
    console.log('✓ Created parent user');

    // Create child user
    const childId = 'test-child-001';
    await db.collection('users').doc(childId).set({
      id: childId,
      email: 'child@test.com',
      name: 'Test Child',
      role: 'CHILD',
      age: 10,
      createdAt: admin.firestore.FieldValue.serverTimestamp(),
      fcmToken: null,
      familyId: 'test-family-001'
    });
    console.log('✓ Created child user');

    // Create family
    await db.collection('families').doc('test-family-001').set({
      id: 'test-family-001',
      name: 'Test Family',
      parentId: parentId,
      childIds: [childId],
      createdAt: admin.firestore.FieldValue.serverTimestamp()
    });
    console.log('✓ Created family');

    // Create time balance for child
    await db.collection('timeBalances').doc(childId).set({
      childId: childId,
      totalMinutes: 120,
      usedMinutes: 0,
      remainingMinutes: 120,
      lastResetAt: admin.firestore.FieldValue.serverTimestamp(),
      lastUpdatedAt: admin.firestore.FieldValue.serverTimestamp(),
      isBlocked: false,
      blockReason: null
    });
    console.log('✓ Created time balance (120 minutes)');

    // Create test tasks
    const tasks = [
      { title: 'Make bed', description: 'Make your bed neatly', rewardMinutes: 10, status: 'PENDING' },
      { title: 'Brush teeth (morning)', description: 'Brush your teeth in the morning', rewardMinutes: 5, status: 'PENDING' },
      { title: 'Homework', description: 'Complete your homework', rewardMinutes: 30, status: 'PENDING' },
      { title: 'Reading (20 min)', description: 'Read for 20 minutes', rewardMinutes: 20, status: 'COMPLETED' },
      { title: 'Clean room', description: 'Clean and organize your room', rewardMinutes: 15, status: 'PENDING' }
    ];

    for (const task of tasks) {
      await db.collection('tasks').add({
        familyId: 'test-family-001',
        childId: childId,
        title: task.title,
        description: task.description,
        rewardMinutes: task.rewardMinutes,
        status: task.status,
        createdAt: admin.firestore.FieldValue.serverTimestamp(),
        completedAt: task.status === 'COMPLETED' ? admin.firestore.FieldValue.serverTimestamp() : null,
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
    console.log('✓ Created 5 test tasks');

    // Create a time log entry
    await db.collection('timeLogs').add({
      childId: childId,
      action: 'EARNED',
      minutesChange: 120,
      balanceAfter: 120,
      taskId: null,
      timestamp: admin.firestore.FieldValue.serverTimestamp(),
      reason: 'Initial balance setup'
    });
    console.log('✓ Created time log entry');

    console.log('\n✅ Test data setup complete!');
    console.log('\nTest accounts:');
    console.log('Parent ID:', parentId);
    console.log('Child ID:', childId);
    console.log('Family ID: test-family-001');
    console.log('\nChild has 120 minutes of screen time available.');
    console.log('5 tasks are ready to be completed.');

  } catch (error) {
    console.error('Error setting up test data:', error);
  } finally {
    process.exit();
  }
}

setupTestData();
