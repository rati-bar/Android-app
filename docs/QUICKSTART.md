# Quick Start Guide

Get Screen Time Calculator running in 15 minutes.

## Prerequisites

- Android Studio installed
- Firebase account created
- Android device for testing (optional)

## Step 1: Firebase Setup (5 minutes)

### Create Firebase Project

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Click "Add Project"
3. Name it "Screen Time Calculator"
4. Disable Google Analytics (optional)
5. Click "Create Project"

### Enable Firebase Services

1. **Authentication**
   - Click "Authentication" → "Get Started"
   - Enable "Email/Password" provider

2. **Firestore Database**
   - Click "Firestore Database" → "Create Database"
   - Start in "Production Mode"
   - Choose location (closest to users)

3. **Cloud Messaging**
   - Already enabled by default

### Add Android App to Firebase

1. Click "Project Settings" (gear icon)
2. Click "Add App" → Android
3. Enter package name: `com.screetime.child`
4. Download `google-services.json`
5. Place in `app-child/` directory

## Step 2: Deploy Firestore Rules (2 minutes)

```bash
cd backend
firebase login
firebase use --add  # Select your project
firebase deploy --only firestore:rules
```

## Step 3: Build the App (3 minutes)

### Option A: Android Studio

1. Open project in Android Studio
2. Wait for Gradle sync
3. Select `app-child` configuration
4. Click Run or Build → Build APK

### Option B: Command Line

```bash
./gradlew :app-child:assembleDebug
```

APK location: `app-child/build/outputs/apk/debug/app-child-debug.apk`

## Step 4: Install on Device (2 minutes)

### For Testing (Debug Mode)

```bash
adb install app-child/build/outputs/apk/debug/app-child-debug.apk
```

⚠️ **Note**: App will NOT fully function without Device Owner setup.

### For Production (Device Owner Required)

See [DEVICE_OWNER_SETUP.md](DEVICE_OWNER_SETUP.md) for full instructions.

Quick steps:
```bash
# 1. Factory reset device
# 2. Skip Google account during setup
# 3. Enable USB debugging
# 4. Install app
adb install app-child-release.apk

# 5. Set Device Owner
adb shell dpm set-device-owner com.screetime.child/.service.ScreenTimeDeviceAdminReceiver

# 6. Verify
adb shell dumpsys device_policy | grep "Device Owner"
```

## Step 5: Deploy Cloud Functions (3 minutes)

```bash
cd backend
npm install
npm run build
firebase deploy --only functions
```

This deploys:
- Task approval/rejection handlers
- Daily reset scheduler
- Family creation handler
- Security monitoring

## First Time Use

### 1. Open App

Launch "Screen Time" app on device

### 2. Grant Permissions

Allow the following permissions when prompted:
- [ ] Usage Access
- [ ] Notification Access
- [ ] Display Over Other Apps
- [ ] Battery Optimization Exemption

### 3. Create Account

- Enter email and password
- Select role: "Child"
- Complete profile

### 4. Link to Family

- Enter family code (from parent)
- Or create new family

### 5. Start Using

- View assigned tasks
- Complete tasks and mark as done
- Wait for parent approval
- Earn screen time!

## Testing Without Device Owner

For development/testing, you can:

1. **Use Android Emulator**
   ```bash
   # Create emulator without Google Play
   # Then follow device owner setup
   ```

2. **Mock Mode** (Development)
   - App will show warnings
   - Core features work
   - Blocking/enforcement disabled

## Troubleshooting

### Build Fails

```bash
# Clean and rebuild
./gradlew clean
./gradlew :app-child:assembleDebug
```

### ADB Not Found

**Windows**: Add to PATH: `C:\Users\YourName\AppData\Local\Android\Sdk\platform-tools`

**Mac/Linux**:
```bash
export PATH=$PATH:~/Library/Android/sdk/platform-tools
```

### Device Not Detected

```bash
adb kill-server
adb start-server
adb devices
```

### Firebase Deploy Fails

```bash
firebase login --reauth
firebase use --add
```

## Next Steps

1. Read [README.md](../README.md) for full feature overview
2. Review [DEVICE_OWNER_SETUP.md](DEVICE_OWNER_SETUP.md) for production setup
3. Check [DEPLOYMENT.md](DEPLOYMENT.md) for Play Store release
4. Explore [ARCHITECTURE.md](ARCHITECTURE.md) for technical details

## Development Mode

For active development:

```bash
# Run with auto-reload
./gradlew :app-child:installDebug
adb shell am start -n com.screetime.child/.ui.MainActivity

# View logs
adb logcat | grep ScreenTime
```

## Production Checklist

Before releasing:

- [ ] Set up proper signing keys
- [ ] Update ProGuard rules
- [ ] Test on multiple devices
- [ ] Complete Device Owner setup
- [ ] Test all security features
- [ ] Deploy Cloud Functions
- [ ] Set up Firebase security rules
- [ ] Create privacy policy
- [ ] Prepare Play Store listing

---

**Need Help?**
- [Full Documentation](../README.md)
- [Common Issues](TROUBLESHOOTING.md)
- [GitHub Issues](https://github.com/yourusername/Android-app/issues)

**Estimated Total Time**: 15-20 minutes
**Difficulty**: Intermediate
