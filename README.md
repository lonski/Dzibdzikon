# Dzibdzikon

## Project settings

Use Java 21 (required — Java 17 will not work with the Android build).

## Desktop

```bash
./gradlew lwjgl3:run
```

## Android

### Prerequisites

1. Install the Android SDK (via Android Studio or command-line tools).
2. Create a `local.properties` file in the project root:

```properties
sdk.dir=/path/to/Android/Sdk
```

3. Required SDK components:
   - **Platform:** `android-34`
   - **Build-Tools:** `36.1.0`

### Enable the Android subproject

The `android` subproject is included in `settings.gradle` by default. If it was excluded (e.g. for CI), make sure `settings.gradle` contains:

```
include 'android'
```

### Build & install to a connected device

Enable **USB debugging** on your phone, then:

```bash
./gradlew android:installDebug
```

This builds the APK and installs it directly via ADB.

### Uninstall from device

Use `adb` from your SDK's `platform-tools`:

```bash
~/Android/Sdk/platform-tools/adb uninstall pl.lonski.dzibdzikon
```

> You need to uninstall first if you previously installed a version signed with a different key (e.g. switching between debug builds from different machines).

### Build APK without installing

```bash
./gradlew android:assembleDebug
```

Output: `android/build/outputs/apk/debug/android-debug.apk`

### Launch the app on device via ADB (without reinstalling)

```bash
~/Android/Sdk/platform-tools/adb shell am start -n pl.lonski.dzibdzikon/pl.lonski.dzibdzikon.android.AndroidLauncher
```
