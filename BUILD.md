# Build Instructions

## Quick Start

### Option 1: Android Studio (Recommended)

1. **Open Project**
   ```bash
   # Open Android Studio
   # File → Open → Select this directory
   ```

2. **Sync Gradle**
   - Android Studio will automatically prompt to sync
   - Or: File → Sync Project with Gradle Files

3. **Build & Run**
   - Click the green "Run" button
   - Or: Run → Run 'app'
   - Or: `Shift + F10`

4. **Select Device**
   - Choose a connected device or emulator
   - Press OK

5. **Set as Default Launcher**
   - Press the home button on your device
   - Select "DevLauncher"
   - Choose "Always" to set as default

### Option 2: Command Line

1. **Build Debug APK**
   ```bash
   ./gradlew assembleDebug
   ```
   APK location: `app/build/outputs/apk/debug/app-debug.apk`

2. **Install on Device**
   ```bash
   ./gradlew installDebug
   ```

3. **Build & Install in One Step**
   ```bash
   ./gradlew installDebug
   ```

4. **Set as Default Launcher**
   - Press home button
   - Select "DevLauncher"
   - Choose "Always"

### Option 3: Release Build

1. **Create Keystore** (first time only)
   ```bash
   keytool -genkey -v -keystore devlauncher.keystore \
     -alias devlauncher -keyalg RSA -keysize 2048 -validity 10000
   ```

2. **Configure Signing**
   Create `keystore.properties` in project root:
   ```properties
   storeFile=devlauncher.keystore
   storePassword=YOUR_PASSWORD
   keyAlias=devlauncher
   keyPassword=YOUR_PASSWORD
   ```

3. **Build Release APK**
   ```bash
   ./gradlew assembleRelease
   ```

## Troubleshooting

### Gradle Sync Failed

**Problem**: Gradle sync fails with dependency errors

**Solution**:
```bash
# Clean build
./gradlew clean

# Invalidate caches (Android Studio)
# File → Invalidate Caches → Invalidate and Restart
```

### SDK Not Found

**Problem**: Android SDK not found

**Solution**:
1. Install Android SDK via Android Studio
2. Or set `ANDROID_HOME` environment variable:
   ```bash
   export ANDROID_HOME=/path/to/android/sdk
   ```

### Build Tools Version

**Problem**: Build tools version mismatch

**Solution**:
- Open `app/build.gradle.kts`
- Update `compileSdk` and `targetSdk` to match your installed SDK
- Sync Gradle

### Permission Denied (Linux/Mac)

**Problem**: `./gradlew` permission denied

**Solution**:
```bash
chmod +x gradlew
./gradlew assembleDebug
```

### Device Not Found

**Problem**: ADB can't find device

**Solution**:
```bash
# Check connected devices
adb devices

# If empty, enable USB debugging on device
# Settings → Developer Options → USB Debugging

# Restart ADB
adb kill-server
adb start-server
```

## Verification

After installation, verify:

1. **App Installed**
   ```bash
   adb shell pm list packages | grep devlauncher
   ```
   Should show: `package:com.devlauncher`

2. **App Launches**
   ```bash
   adb shell am start -n com.devlauncher/.MainActivity
   ```

3. **Logs**
   ```bash
   adb logcat | grep DevLauncher
   ```

## Development Workflow

### Hot Reload (Android Studio)

1. Make code changes
2. Press `Ctrl + F9` (Build)
3. Changes apply automatically (Compose hot reload)

### Debug Mode

1. Set breakpoints in code
2. Click "Debug" button (bug icon)
3. Or: Run → Debug 'app'
4. Or: `Shift + F9`

### Run Tests

```bash
# Unit tests
./gradlew test

# Instrumented tests (requires device)
./gradlew connectedAndroidTest
```

## Performance Profiling

### Memory Usage

```bash
# Check memory
adb shell dumpsys meminfo com.devlauncher
```

### Launch Time

```bash
# Measure cold start
adb shell am start -W com.devlauncher/.MainActivity
```

Target: < 200ms

### APK Size

```bash
# Check APK size
ls -lh app/build/outputs/apk/debug/app-debug.apk
```

Target: < 10MB

## Next Steps

After successful build:

1. ✅ Core is running
2. ⏳ Build first plugin (Terminal or AI Assistant)
3. ⏳ Test plugin system
4. ⏳ Validate architecture

See `QUICKSTART.md` for plugin development guide.

## Common Commands

```bash
# Clean build
./gradlew clean

# Build debug
./gradlew assembleDebug

# Install debug
./gradlew installDebug

# Build release
./gradlew assembleRelease

# Run tests
./gradlew test

# Check dependencies
./gradlew dependencies

# List tasks
./gradlew tasks
```

## IDE Setup

### Android Studio Settings

Recommended settings:
- **Editor → Code Style → Kotlin**: Use official Kotlin style guide
- **Build → Compiler**: Enable parallel compilation
- **Appearance**: Enable Material UI

### Plugins

Recommended plugins:
- Kotlin (built-in)
- Android (built-in)
- Compose Multiplatform IDE Support

## Resources

- [Android Developer Docs](https://developer.android.com)
- [Jetpack Compose Docs](https://developer.android.com/jetpack/compose)
- [Gradle User Guide](https://docs.gradle.org)
- [Kotlin Docs](https://kotlinlang.org/docs)

## Support

Issues? Check:
1. This BUILD.md file
2. README.md
3. `.kiro/specs/android-dev-launcher/QUICKSTART.md`
4. Open an issue on GitHub
