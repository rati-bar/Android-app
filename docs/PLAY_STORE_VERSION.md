# Play Store Compliant Version - Required Changes

## Changes Needed for Play Store Approval

### 1. Remove Device Owner

**Files to Modify:**
```kotlin
// app-child/src/main/AndroidManifest.xml
// REMOVE:
<receiver android:name=".service.ScreenTimeDeviceAdminReceiver" />

// REMOVE permissions:
<uses-permission android:name="android.permission.QUERY_ALL_PACKAGES" />
<uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />
```

### 2. Replace App Blocking

**Instead of DevicePolicyManager blocking:**
```kotlin
// Use Digital Wellbeing API (Android 9+)
// Or AccessibilityService (with user permission)
// Or just track usage without blocking
```

### 3. Make Time Limits "Soft"

```kotlin
// Show fullscreen reminder instead of blocking
// Allow "I understand" bypass
// Log to parent but don't prevent
```

### 4. Remove Anti-Uninstall

App must be uninstallable normally.

### 5. Add Data Disclosure

Declare all data collection in Privacy Policy and Play Store listing.

## Result

- App becomes educational/tracking tool
- No enforcement capabilities
- Can be uninstalled
- Based on trust, not control
