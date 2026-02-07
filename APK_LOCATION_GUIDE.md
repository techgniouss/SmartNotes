# APK Location Guide

This guide explains how to find the APK files for the Smart Notes Android application after building.

## Standard APK Locations

### Debug Build
After running a debug build, the APK will be located at:
```
app/build/outputs/apk/debug/app-debug.apk
```

### Release Build
After running a release build, the APK will be located at:
```
app/build/outputs/apk/release/app-release.apk
```

## Building APKs

### Build Debug APK
To build a debug APK, run:
```bash
./gradlew assembleDebug
```

The output APK will be ready for installation on devices and includes debugging information.

### Build Release APK
To build a release APK, run:
```bash
./gradlew assembleRelease
```

**Note:** The release APK will be minified (code obfuscation enabled) as per the project configuration. You'll need to sign it before distribution.

### Build All Variants
To build both debug and release APKs at once:
```bash
./gradlew assemble
```

### Clean and Rebuild
If you need to clean previous builds before creating new APKs:
```bash
./gradlew clean
./gradlew assembleDebug  # or assembleRelease
```

## Android App Bundle (AAB)

For Google Play Store distribution, you should build an Android App Bundle instead:

```bash
./gradlew bundleRelease
```

The output AAB will be located at:
```
app/build/outputs/bundle/release/app-release.aab
```

## Finding APKs via Command Line

### Find All APK Files
To locate all APK files in the project directory:
```bash
find . -name "*.apk"
```

### Find Only Recent APK Files
To find APK files modified in the last 24 hours:
```bash
find . -name "*.apk" -mtime -1
```

### List with Details
To see APK files with size and modification date:
```bash
find . -name "*.apk" -exec ls -lh {} \;
```

## Using Android Studio

If you're using Android Studio:

1. **Menu Build:**
   - Go to `Build → Build Bundle(s) / APK(s) → Build APK(s)`
   - Once complete, click "locate" in the notification popup

2. **Project View:**
   - Switch to "Project" view (not "Android" view)
   - Navigate to `app/build/outputs/apk/debug/` or `app/build/outputs/apk/release/`
   - Right-click on the APK file and select "Show in Files" or "Reveal in Finder"

3. **Build Variants:**
   - Open the "Build Variants" panel (usually on the left side)
   - Select "debug" or "release" variant
   - Build the project

## Installing APKs

### Via ADB (Android Debug Bridge)
Once you have the APK, install it on a connected device:

```bash
# Install debug APK
adb install app/build/outputs/apk/debug/app-debug.apk

# Install release APK
adb install app/build/outputs/apk/release/app-release.apk

# Reinstall (preserves data)
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

### Check Connected Devices
```bash
adb devices
```

### Install on Specific Device
If you have multiple devices connected:
```bash
adb -s <device-id> install app/build/outputs/apk/debug/app-debug.apk
```

## APK Information

### Get APK Details
To view information about the built APK:

```bash
# Using aapt (Android Asset Packaging Tool)
aapt dump badging app/build/outputs/apk/debug/app-debug.apk

# View package name and version
aapt dump badging app/build/outputs/apk/debug/app-debug.apk | grep package
```

### Check APK Size
```bash
ls -lh app/build/outputs/apk/debug/app-debug.apk
```

### Analyze APK
```bash
# Using Android Studio's APK Analyzer
# Build → Analyze APK... → Select your APK file
```

## Troubleshooting

### APK Not Found
If the APK isn't where expected:

1. **Check build was successful:**
   ```bash
   ./gradlew assembleDebug --info
   ```

2. **Verify build output directory exists:**
   ```bash
   ls -la app/build/outputs/apk/
   ```

3. **Clean and rebuild:**
   ```bash
   ./gradlew clean assembleDebug
   ```

### Build Output Directory Structure
```
app/build/outputs/
├── apk/
│   ├── debug/
│   │   ├── app-debug.apk
│   │   └── output-metadata.json
│   └── release/
│       ├── app-release-unsigned.apk  (if not signed)
│       ├── app-release.apk           (if signed)
│       └── output-metadata.json
├── bundle/
│   └── release/
│       └── app-release.aab
└── logs/
    └── manifest-merger-*.txt
```

## Project-Specific Information

### Application Details
- **Package Name:** com.smartnotes
- **Version Code:** 1
- **Version Name:** 1.0
- **Min SDK:** 26 (Android 8.0)
- **Target SDK:** 34 (Android 14)

### Build Configuration
- **Release build:** Minification enabled (ProGuard)
- **Debug build:** No minification, debuggable

## Quick Reference Commands

```bash
# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Build AAB for Play Store
./gradlew bundleRelease

# Clean build
./gradlew clean

# Find all APKs
find . -name "*.apk"

# Install debug APK
adb install app/build/outputs/apk/debug/app-debug.apk

# View APK info
aapt dump badging app/build/outputs/apk/debug/app-debug.apk
```

## Additional Resources

- [Android Developer Guide - Build Your App](https://developer.android.com/studio/build)
- [Android App Bundle Documentation](https://developer.android.com/guide/app-bundle)
- [ProGuard Configuration](https://developer.android.com/studio/build/shrink-code)
