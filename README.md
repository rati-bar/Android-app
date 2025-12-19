# Screen Time Calculator

A comprehensive parental control Android application that helps children earn phone usage time by completing real-world tasks approved by parents.

## 🎯 Overview

Screen Time Calculator is a parent-child linked mobile application that:
- Allows children to earn phone usage time by completing tasks
- Enforces time limits through Android Device Owner controls
- Blocks all apps when earned time runs out
- Provides parents with complete control and monitoring

## ✨ Key Features

### For Parents
- Create and manage family accounts
- Define daily tasks with customizable rewards
- Approve or reject completed tasks in real-time
- Monitor child's app usage and screen time
- View detailed reports and analytics
- Emergency unlock capability
- Security event notifications

### For Children
- View assigned daily tasks
- Mark tasks as completed
- Real-time time balance tracking
- Countdown timer display
- Earn minutes through task completion
- Gamified task interface

### Security Features
- **Device Owner Mode**: Prevents uninstall and bypass attempts
- **Time Validation**: Detects system time manipulation using NTP
- **Root Detection**: Identifies rooted devices
- **Integrity Checking**: Validates app hasn't been tampered with
- **App Blocking**: Complete device lockdown when time depletes
- **Kiosk Mode**: Only Screen Time app accessible when blocked
- **Anti-Bypass**: Multiple layers of security protection

## 🏗️ Architecture

### Technology Stack

**Frontend (Android)**
- **Language**: Kotlin
- **UI**: Jetpack Compose with Material Design 3
- **Architecture**: Clean Architecture (MVVM)
- **DI**: Hilt
- **Async**: Coroutines + Flow
- **Local DB**: Room
- **Preferences**: DataStore

**Backend (Firebase)**
- **Authentication**: Firebase Auth
- **Database**: Cloud Firestore
- **Functions**: Cloud Functions (TypeScript)
- **Messaging**: Firebase Cloud Messaging (FCM)
- **Analytics**: Firebase Analytics
- **Monitoring**: Firebase Crashlytics

**Device Management**
- DevicePolicyManager (Device Owner)
- UsageStatsManager
- ForegroundService
- WorkManager

### Project Structure

```
Android-app/
├── app-parent/              # Parent app (Master)
├── app-child/               # Child app (Slave) - THIS IS THE MAIN APP
│   ├── service/             # Time tracking, app blocking, device admin
│   ├── security/            # Anti-bypass, root detection, integrity checks
│   ├── ui/                  # Jetpack Compose UI
│   └── di/                  # Dependency injection
├── core/
│   ├── model/               # Shared data models
│   ├── common/              # Utilities and extensions
│   ├── designsystem/        # Material 3 theme
│   ├── network/             # Firebase integration
│   └── database/            # Room database
├── backend/
│   └── functions/           # Firebase Cloud Functions
└── docs/                    # Documentation
```

## 🚀 Quick Start

### Prerequisites

- Android Studio Hedgehog or later
- JDK 17
- Android SDK API 34
- Firebase account
- Node.js 18+ (for Cloud Functions)

### 1. Clone Repository

```bash
git clone https://github.com/yourusername/Android-app.git
cd Android-app
```

### 2. Firebase Setup

1. Create a new Firebase project at [Firebase Console](https://console.firebase.google.com/)
2. Enable Authentication (Email/Password)
3. Enable Firestore Database
4. Enable Cloud Messaging
5. Download `google-services.json` for the child app
6. Place it in `app-child/google-services.json`

### 3. Build the App

```bash
# Build child app (release)
./gradlew :app-child:assembleRelease

# The APK will be at: app-child/build/outputs/apk/release/app-child-release.apk
```

### 4. Device Owner Setup (CRITICAL)

**⚠️ IMPORTANT**: The child app MUST be set as Device Owner to function properly.

See [DEVICE_OWNER_SETUP.md](docs/DEVICE_OWNER_SETUP.md) for detailed instructions.

Quick summary:
```bash
# 1. Factory reset the child's device
# 2. During setup, skip Google account
# 3. Enable USB debugging
# 4. Connect to computer
# 5. Install app
adb install app-child-release.apk

# 6. Set as Device Owner
adb shell dpm set-device-owner com.screetime.child/.service.ScreenTimeDeviceAdminReceiver

# 7. Verify
adb shell dumpsys device_policy | grep "Device Owner"
```

### 5. Deploy Cloud Functions

```bash
cd backend
npm install
npm run build
firebase deploy --only functions
```

## 📱 How It Works

### Daily Flow

1. **Morning**
   - Child wakes up with 0 minutes of screen time
   - Views assigned tasks for the day
   - Completes tasks (e.g., make bed, brush teeth)
   - Marks tasks as completed in app

2. **Parent Approval**
   - Parent receives notification of completed task
   - Reviews task completion
   - Approves or rejects with optional feedback
   - Upon approval, minutes are automatically added

3. **Screen Time Usage**
   - Child uses phone for earned minutes
   - Countdown timer runs continuously
   - Warnings at 5 minutes and 1 minute remaining
   - When time reaches 0, all apps are blocked

4. **Blocked Mode**
   - Only Screen Time app is accessible (kiosk mode)
   - Shows available tasks to earn more time
   - Emergency unlock request option
   - Cannot exit or bypass the app

5. **Midnight Reset**
   - Automatic daily reset at midnight
   - Tasks return to "Pending" status
   - Time balance resets to 0
   - New day begins

## 🔒 Security Model

### Device Owner Enforcement

The app uses Android's Device Owner mode to ensure parental controls cannot be bypassed:

- **Uninstall Prevention**: App cannot be uninstalled without factory reset
- **App Blocking**: Hide and suspend all other apps when time depletes
- **Kiosk Mode**: Lock device to Screen Time app only
- **Policy Enforcement**: Disable USB debugging, developer options, etc.

### Anti-Bypass Mechanisms

1. **Time Manipulation Detection**
   - Validates system time against NTP servers
   - Cross-references with Firebase server time
   - Detects manual time changes
   - Logs security events

2. **Root Detection**
   - Checks for su binaries
   - Scans for root management apps
   - Validates build tags
   - Tests system write permissions

3. **Integrity Validation**
   - Verifies app signature
   - Checks installation source
   - Detects ADB/developer mode
   - Uses Play Integrity API

4. **Service Protection**
   - Foreground services with high priority
   - Automatic restart after device reboot
   - Protection against force-stop
   - Background task monitoring

## 📊 Default Tasks

The app comes with pre-configured tasks:

| Task | Reward | Description |
|------|--------|-------------|
| Make bed | 10 min | Make your bed neatly |
| Brush teeth (morning) | 5 min | Brush your teeth in the morning |
| Brush teeth (evening) | 5 min | Brush your teeth before bed |
| Homework | 30 min | Complete your homework |
| Reading (20 min) | 20 min | Read for 20 minutes |
| Clean room | 15 min | Clean and organize your room |
| Help with dishes | 10 min | Help wash or dry the dishes |
| Physical activity (30 min) | 20 min | Exercise or play outside |
| Prepare school bag | 5 min | Pack your school bag for tomorrow |

Parents can customize rewards and add new tasks.

## 🎮 Future Enhancements

- [ ] Parent mobile app (currently planned)
- [ ] Weekly/monthly task templates
- [ ] Achievement badges and rewards
- [ ] Scheduled bedtime auto-lock
- [ ] Multiple children per family
- [ ] App-specific time limits
- [ ] Educational content integration
- [ ] Parental web dashboard

## ⚠️ Important Notes

### Legal & Privacy
- This app is intended for parental supervision of minors
- Ensure compliance with local laws regarding parental monitoring
- Privacy policy must be provided for Play Store submission
- Obtain informed consent from children (age-appropriate)

### Device Requirements
- **Minimum**: Android 8.0 (API 26)
- **Target**: Android 14 (API 34)
- Device owner setup requires factory reset or new device
- Not compatible with existing Google accounts as primary

### Limitations
- **Android Only**: iOS version has significant limitations
- **One Device**: Child can only use one device with this app
- **Factory Reset Required**: Device owner setup needs clean device
- **No Multi-User**: Android multi-user mode may interfere

## 📚 Documentation

- [Setup Guide](docs/SETUP.md) - Detailed setup instructions
- [Device Owner Setup](docs/DEVICE_OWNER_SETUP.md) - Critical device configuration
- [Deployment Guide](docs/DEPLOYMENT.md) - Production deployment
- [Architecture](docs/ARCHITECTURE.md) - Technical architecture details
- [API Documentation](docs/API.md) - Firebase Cloud Functions API
- [Security](docs/SECURITY.md) - Security implementation details

## 🤝 Contributing

This is a private project. If you'd like to contribute, please contact the repository owner.

## 📄 License

Copyright © 2024. All rights reserved.

## 🆘 Support

For issues and questions:
1. Check the [Documentation](docs/)
2. Review [Common Issues](docs/TROUBLESHOOTING.md)
3. Open an issue on GitHub

## ⚙️ Build Information

- **Version**: 1.0.0
- **Build Tools**: Gradle 8.4, AGP 8.2.2
- **Kotlin Version**: 1.9.22
- **Min SDK**: 26 (Android 8.0)
- **Target SDK**: 34 (Android 14)

---

**⚠️ CRITICAL**: This app requires Device Owner privileges to function. Standard installation will NOT work. You MUST follow the Device Owner setup procedure.
