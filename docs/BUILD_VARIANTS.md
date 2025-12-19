# Build Variants Guide

The Screen Time Calculator app now supports **two versions** from a single codebase:

1. **Play Store Edition** (Lite) - Notification-only, Play Store compliant
2. **Pro Edition** - Full enforcement with Device Owner

## 📦 Quick Reference

| Feature | Play Store | Pro |
|---------|-----------|-----|
| **Distribution** | Google Play Store | Direct APK |
| **Setup Complexity** | Low (normal install) | High (factory reset) |
| **Device Owner** | ❌ No | ✅ Required |
| **App Blocking** | ❌ No | ✅ Yes |
| **Kiosk Mode** | ❌ No | ✅ Yes |
| **Uninstall Prevention** | ❌ No | ✅ Yes |
| **Time Enforcement** | Honor system (20-30%) | Full enforcement (95%) |
| **Parent Notification** | ✅ Yes | ✅ Yes |
| **Task Management** | ✅ Full | ✅ Full |
| **Usage Tracking** | ✅ Yes | ✅ Yes |
| **Target Users** | Younger/compliant kids | All ages, serious control |

## 🔨 Building the Apps

### Build Play Store Version (Lite)

```bash
# Debug build
./gradlew :app-child:assemblePlaystoreDebug

# Release build
./gradlew :app-child:assemblePlaystoreRelease

# Output location
app-child/build/outputs/apk/playstore/release/app-child-playstore-release.apk
```

**App ID:** `com.screetime.child.lite`
**App Name:** "Screen Time Lite"

### Build Pro Version

```bash
# Debug build
./gradlew :app-child:assembleProDebug

# Release build
./gradlew :app-child:assembleProRelease

# Output location
app-child/build/outputs/apk/pro/release/app-child-pro-release.apk
```

**App ID:** `com.screetime.child`
**App Name:** "Screen Time Pro"

### Install Variants

```bash
# Install Play Store version
adb install app-child/build/outputs/apk/playstore/debug/app-child-playstore-debug.apk

# Install Pro version
adb install app-child/build/outputs/apk/pro/debug/app-child-pro-debug.apk

# Both can be installed side-by-side (different app IDs)
```

## 🏗️ Architecture

### Build Configuration

The app uses **product flavors** to create two distinct versions:

```kotlin
// build.gradle.kts
productFlavors {
    create("playstore") {
        dimension = "version"
        buildConfigField("Boolean", "ENFORCEMENT_ENABLED", "false")
        // Notification-only mode
    }

    create("pro") {
        dimension = "version"
        buildConfigField("Boolean", "ENFORCEMENT_ENABLED", "true")
        // Full enforcement mode
    }
}
```

### Runtime Detection

Code uses `BuildConfig.ENFORCEMENT_ENABLED` to switch behavior:

```kotlin
if (BuildConfig.ENFORCEMENT_ENABLED) {
    // Pro version: Block all apps
    startAppBlocking()
    enterKioskMode()
} else {
    // Play Store version: Show notification
    showTimeDepletedScreen()
    notifyParent()
}
```

### Manifest Differences

Each flavor has its own `AndroidManifest.xml`:

**Play Store** (`app-child/src/playstore/AndroidManifest.xml`):
- ❌ No Device Admin receiver
- ❌ No `QUERY_ALL_PACKAGES`
- ❌ No `SYSTEM_ALERT_WINDOW`
- ❌ No boot receiver
- ❌ No AppBlockingService
- ✅ Only safe permissions

**Pro** (`app-child/src/pro/AndroidManifest.xml`):
- ✅ Full Device Admin receiver
- ✅ All permissions
- ✅ Boot receiver
- ✅ AppBlockingService
- ✅ BlockedActivity (kiosk mode)

## 📱 What Users See

### When Time Reaches 0

#### Play Store Version:
```
1. Persistent notification appears: "⏰ Time's up!"
2. Dismissible fullscreen alert shows
3. "View Tasks" or "I Understand" buttons
4. Parent receives FCM notification
5. Child can still use any app (honor system)
6. Can dismiss and continue using phone
```

#### Pro Version:
```
1. ALL apps immediately hidden/suspended
2. Device enters kiosk mode
3. Only Screen Time app accessible
4. Cannot exit or switch apps
5. Cannot uninstall
6. Must complete tasks to unlock
7. Parent receives notification
```

### UI Differences

**Play Store:**
- Friendly, educational tone
- "Screen Time Ended" title
- Dismissible dialog
- Encourages task completion
- Blue/neutral colors

**Pro:**
- Firm, enforcement tone
- "Time's Up!" title
- Cannot dismiss (until time earned)
- Red/warning colors
- Lock icon prominent

## 🔧 Development

### Running Specific Variant

In Android Studio:
1. Go to **Build Variants** panel (bottom left)
2. Select `playstoreDebug` or `proDebug`
3. Run app (Shift + F10)

Command line:
```bash
# Run Play Store version
./gradlew :app-child:installPlaystoreDebug
adb shell am start -n com.screetime.child.lite/.ui.MainActivity

# Run Pro version
./gradlew :app-child:installProDebug
adb shell am start -n com.screetime.child/.ui.MainActivity
```

### Testing Both Versions

Both can be installed simultaneously:
```bash
# Install both
./gradlew :app-child:installPlaystoreDebug :app-child:installProDebug

# Now you have:
# - Screen Time Lite (com.screetime.child.lite)
# - Screen Time Pro (com.screetime.child)
```

### Debugging Enforcement Logic

Check which version is running:
```kotlin
Log.d(TAG, "Version: ${BuildConfig.VERSION_TYPE}")
Log.d(TAG, "Enforcement: ${BuildConfig.ENFORCEMENT_ENABLED}")
```

## 📂 File Structure

```
app-child/
├── src/
│   ├── main/                    # Shared code
│   │   ├── kotlin/
│   │   │   ├── service/
│   │   │   │   ├── TimeTrackingService.kt     # Uses BuildConfig
│   │   │   │   ├── AppBlockingService.kt      # Pro only
│   │   │   │   └── ScreenTimeDeviceAdminReceiver.kt  # Pro only
│   │   │   └── ui/
│   │   │       ├── timedepleted/
│   │   │       │   └── TimeDepletedActivity.kt  # Both versions
│   │   │       └── blocked/
│   │   │           └── BlockedActivity.kt       # Pro only
│   │   └── AndroidManifest.xml  # Base manifest
│   │
│   ├── playstore/               # Play Store specific
│   │   └── AndroidManifest.xml  # Removes Device Admin
│   │
│   └── pro/                     # Pro specific
│       └── AndroidManifest.xml  # Full permissions
│
└── build.gradle.kts             # Flavor configuration
```

## 🎯 Use Cases

### Use Play Store Version When:
- Child is young (5-8 years old)
- Child is generally compliant
- Teaching time management
- Don't want factory reset hassle
- Want easy installation
- Trust-based approach
- Want it on Play Store

### Use Pro Version When:
- Child is older (9-14 years old)
- Need real enforcement
- Child has bypassed other controls
- Screen addiction concerns
- Medical necessity for limits
- Want absolute control
- Willing to factory reset

## 📋 Checklist for Release

### Play Store Version

- [ ] Build with `assemblePlaystoreRelease`
- [ ] Test on device (normal install)
- [ ] Verify no Device Admin prompts
- [ ] Test time depletion (shows dismissible alert)
- [ ] Verify can uninstall normally
- [ ] Check notification to parent works
- [ ] Test app switching still works after time = 0
- [ ] Prepare Play Store listing
- [ ] Create privacy policy
- [ ] Submit to Play Store

### Pro Version

- [ ] Build with `assembleProRelease`
- [ ] Test Device Owner setup
- [ ] Verify app blocking works
- [ ] Test kiosk mode (cannot exit)
- [ ] Verify uninstall is prevented
- [ ] Test boot receiver (auto-restart)
- [ ] Check notification to parent works
- [ ] Test all anti-bypass measures
- [ ] Prepare website for download
- [ ] Create setup video tutorial

## 🚨 Common Issues

### Issue: "BuildConfig.ENFORCEMENT_ENABLED not found"

**Solution:** Rebuild project
```bash
./gradlew clean
./gradlew :app-child:assembleDebug
```

### Issue: Both apps installed but same icon

**Solution:** They have different app IDs, check launcher carefully:
- "Screen Time Lite" (lite version)
- "Screen Time Pro" (pro version)

### Issue: Play Store version trying to use Device Owner

**Solution:** Check you're building correct flavor:
```bash
./gradlew :app-child:assemblePlaystoreDebug
```

### Issue: Pro version not blocking apps

**Solution:** Verify Device Owner is set:
```bash
adb shell dumpsys device_policy | grep "Device Owner"
```

## 📈 Analytics

Track which version users have:

```kotlin
Firebase.analytics.logEvent("app_version") {
    param("type", BuildConfig.VERSION_TYPE)
    param("enforcement", BuildConfig.ENFORCEMENT_ENABLED.toString())
}
```

## 💡 Marketing Strategy

### Positioning

**Play Store Lite:**
- "Teach your child time management"
- "Trust-based screen time tracking"
- "Perfect for cooperative children"
- Price: Free or $2.99
- Rating: E (Everyone)

**Pro Edition:**
- "Real parental control Google won't allow"
- "Unbypassable time limits"
- "For when trust isn't enough"
- Price: $9.99-14.99
- Website exclusive

### Upsell Flow

1. User finds Lite on Play Store
2. Installs and tries it
3. Realizes child bypasses it
4. In-app message: "Need stronger controls? Try Pro Edition"
5. Link to website
6. Download Pro version

## 🔄 Updating Both Versions

When adding new features:

1. **Add to shared code** (`src/main/`)
2. **Use BuildConfig check** if behavior differs
3. **Test both flavors**
4. **Update both manifests** if needed
5. **Increment version for both**

Example:
```kotlin
// src/main/kotlin/feature/NewFeature.kt
class NewFeature {
    fun execute() {
        // Shared logic
        doCommonStuff()

        // Variant-specific
        if (BuildConfig.ENFORCEMENT_ENABLED) {
            doProVersionLogic()
        } else {
            doPlayStoreLogic()
        }
    }
}
```

## 📊 Version Comparison Matrix

| Aspect | Play Store | Pro |
|--------|-----------|-----|
| **Installation** | Play Store | APK sideload |
| **Setup Time** | 5 minutes | 30-60 minutes |
| **Factory Reset** | Not required | Required |
| **Device Owner** | No | Yes |
| **Effectiveness** | 20-30% | 95%+ |
| **Can Uninstall** | Yes | No |
| **Can Bypass** | Easy | Very difficult |
| **Target Age** | 5-8 years | 9-14 years |
| **Parent Control** | Moderate | Complete |
| **Updates** | Auto (Play Store) | Manual |
| **Privacy** | Standard | Enhanced |

---

## Summary

Two versions from one codebase give parents **choice**:
- **Trust-based** for younger/compliant children (Play Store)
- **Control-based** for older/resistant children (Pro)

Both share:
- ✅ Task management
- ✅ Parent approval workflow
- ✅ Time tracking
- ✅ Notifications
- ✅ Usage analytics

Only Pro has:
- ✅ App blocking
- ✅ Kiosk mode
- ✅ Uninstall prevention
- ✅ Device Owner enforcement

**Result:** Broader market reach + premium product offering!
