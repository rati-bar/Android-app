# Firebase Setup Guide

This guide will help you set up Firebase for the Screen Time Calculator app.

## Prerequisites

- Google account
- Node.js and npm installed (for Cloud Functions)
- Firebase CLI installed

## Step 1: Create Firebase Project

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Click "Add project" or "Create a project"
3. Enter project name: `screen-time-app` (or your preferred name)
4. Click "Continue"
5. Disable Google Analytics (optional for this app)
6. Click "Create project"
7. Wait for project creation to complete
8. Click "Continue"

## Step 2: Add Android App to Firebase

1. In Firebase Console, click the Android icon to add an Android app
2. Enter package name: `com.screetime.child.lite` (for Play Store version)
3. Enter app nickname: `Screen Time Lite`
4. Leave SHA-1 blank for now (optional)
5. Click "Register app"
6. **Download `google-services.json`** file
7. Click "Next" through the SDK setup steps (we already have this configured)
8. Click "Continue to console"

## Step 3: Add Debug Build Variant

Since we're using debug builds, we need to add the debug package too:

1. In Firebase Console, go to Project Settings (gear icon)
2. Scroll down to "Your apps"
3. Click "Add app" → Android
4. Enter package name: `com.screetime.child.lite.debug`
5. Enter app nickname: `Screen Time Lite Debug`
6. Click "Register app"
7. **Download the new `google-services.json`** (it will include both package names)
8. Click through to finish

## Step 4: Replace google-services.json

1. Copy the downloaded `google-services.json` file
2. Replace the file at: `Android-app/app-child/google-services.json`
3. Make sure it contains both package names:
   - `com.screetime.child.lite`
   - `com.screetime.child.lite.debug`

## Step 5: Enable Firebase Services

### Enable Firestore Database

1. In Firebase Console, click "Firestore Database" in left menu
2. Click "Create database"
3. Select "Start in test mode" (for development)
4. Choose your preferred location
5. Click "Enable"

### Enable Cloud Functions

1. In Firebase Console, click "Functions" in left menu
2. Click "Get started"
3. Click "Upgrade project" (if needed - Blaze plan required for Cloud Functions)
   - Note: Blaze plan is pay-as-you-go but has generous free tier
   - Cloud Functions free tier: 2M invocations/month
4. Follow the upgrade process

### Enable Firebase Cloud Messaging (FCM)

1. In Firebase Console, click "Cloud Messaging" in left menu
2. FCM should be automatically enabled
3. No additional setup needed

## Step 6: Install Firebase CLI

Open terminal/command prompt:

```bash
npm install -g firebase-tools
```

Verify installation:
```bash
firebase --version
```

## Step 7: Login to Firebase CLI

```bash
firebase login
```

This will open a browser window for authentication. Follow the prompts.

## Step 8: Initialize Firebase in Your Project

Navigate to the project backend directory:

```bash
cd D:\Projects\Android\App\Android-app\backend
```

Initialize Firebase:

```bash
firebase init
```

When prompted:
- Select "Functions" and "Firestore" using arrow keys and spacebar
- Choose "Use an existing project"
- Select your Firebase project from the list
- Language: TypeScript
- Use ESLint: Yes
- Install dependencies: Yes
- Firestore rules file: Use default (firestore.rules)
- Firestore indexes file: Use default (firestore.indexes.json)

## Step 9: Deploy Cloud Functions

Still in the `backend` directory:

```bash
# Install dependencies if not already done
cd functions
npm install
cd ..

# Deploy functions
firebase deploy --only functions
```

Wait for deployment to complete. You should see URLs for your deployed functions.

## Step 10: Set Up Firestore Security Rules

1. In Firebase Console, go to "Firestore Database"
2. Click "Rules" tab
3. Replace with the following rules:

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Users collection - parents and children
    match /users/{userId} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }

    // Tasks collection
    match /tasks/{taskId} {
      allow read: if request.auth != null;
      allow write: if request.auth != null;
    }

    // Time logs collection
    match /timeLogs/{logId} {
      allow read, write: if request.auth != null;
    }

    // For testing, allow all access (REMOVE IN PRODUCTION)
    match /{document=**} {
      allow read, write: if true;
    }
  }
}
```

4. Click "Publish"

**Important**: The last rule allows unrestricted access for testing. Remove it before production!

## Step 11: Remove Firebase Initialization Skip

Now that you have real Firebase config, we need to enable Firebase initialization:

Edit `app-child/src/main/kotlin/com/screetime/child/ScreenTimeChildApp.kt`:

Change this:
```kotlin
private fun initializeFirebase() {
    try {
        // Skip Firebase initialization in debug builds or if using demo config
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "Skipping Firebase initialization in debug build")
            return
        }
        // ... rest of code
```

To this:
```kotlin
private fun initializeFirebase() {
    try {
        // Initialize Firebase
        FirebaseApp.initializeApp(this)
        Log.i(TAG, "Firebase initialized")

        // ... rest of code (remove the DEBUG check)
```

## Step 12: Remove Firebase Provider Disable

Edit `app-child/src/main/AndroidManifest.xml`:

Remove or comment out these lines (around line 155-160):
```xml
<!-- Disable Firebase auto-initialization for debug builds -->
<provider
    android:name="com.google.firebase.provider.FirebaseInitProvider"
    android:authorities="${applicationId}.firebaseinitprovider"
    android:exported="false"
    tools:node="remove" />
```

## Step 13: Rebuild and Reinstall App

```bash
# Clean and rebuild
cd D:\Projects\Android\App\Android-app
gradlew clean :app-child:assemblePlaystoreDebug

# Reinstall
cd C:\platform-tools
adb install -r D:\Projects\Android\App\Android-app\app-child\build\outputs\apk\playstore\debug\app-child-playstore-debug.apk
```

## Step 14: Test Firebase Connection

1. Launch the app on your phone
2. Check logcat for Firebase initialization:
   ```bash
   cd C:\platform-tools
   adb logcat | findstr Firebase
   ```
3. You should see "Firebase initialized" in the logs

## What Should Work Now

✅ Firebase authentication (if implemented)
✅ Firestore database reads/writes
✅ Cloud Functions for notifications
✅ Firebase Cloud Messaging (FCM)

## Troubleshooting

### Issue: "Default Firebase app is not initialized"
- Make sure google-services.json is in the correct location
- Rebuild the app completely (gradlew clean)

### Issue: "Cloud Functions deployment failed"
- Make sure you're on Blaze (pay-as-you-go) plan
- Check that Node.js is installed: `node --version`
- Try `firebase login --reauth`

### Issue: "Permission denied" errors in Firestore
- Check Firestore security rules
- For testing, use the permissive rules shown above

## Next Steps

Once Firebase is set up and working:
1. Test time tracking functionality
2. Test task creation and approval
3. Test parent-child notifications
4. Monitor Firebase Console for activity

## Cost Information

Firebase Pricing (as of 2024):
- **Spark Plan (Free)**:
  - Firestore: 50K reads/day, 20K writes/day
  - Cloud Storage: 1GB, 10K downloads/day
  - Cloud Messaging: Unlimited

- **Blaze Plan (Pay as you go)**:
  - Required for Cloud Functions
  - Free tier includes: 2M function invocations/month
  - After free tier: $0.40 per million invocations
  - Firestore: First 50K reads/day free, then $0.06 per 100K

For a small family app, you'll likely stay within free tiers.
