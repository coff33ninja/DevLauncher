# Development Guide

## Getting Started

### Prerequisites
- Flutter SDK (latest stable)
- Android Studio or VS Code with Flutter extensions
- Android device or emulator (API 21+)

### Setup
```bash
# Get dependencies
flutter pub get

# Run on connected device
flutter run

# Run in debug mode with hot reload
flutter run --debug

# Build release APK
flutter build apk --release
```

## Project Structure

```
lib/
├── main.dart           # Entry point with minimal launcher UI
├── core/              # Core functionality (coming soon)
├── plugin/            # Plugin system (coming soon)
├── ui/                # UI components (coming soon)
└── utils/             # Utilities (coming soon)
```

## Current Features

✅ **Implemented:**
- Basic launcher UI with Material 3 dark theme
- Swipe-up gesture to show search
- Search overlay with text input
- Quick action chips (placeholders)
- Fullscreen immersive mode
- Android launcher manifest configuration

🚧 **In Progress:**
- App discovery and listing
- App launching functionality
- Search and filtering

📋 **Planned:**
- Plugin system architecture
- Settings screen
- Customization options
- Developer tools integration

## Testing the Launcher

### On Android Device:
1. Build and install: `flutter run`
2. Press Home button
3. Select "Dev Launcher" as your home app
4. Choose "Always" to set as default launcher

### Reverting to Default Launcher:
1. Go to Settings → Apps → Default apps
2. Select Home app
3. Choose your previous launcher

## Development Workflow

### Hot Reload
- Save files to trigger hot reload
- Press `r` in terminal for manual reload
- Press `R` for hot restart (full app restart)

### Debugging
```bash
# Run with verbose logging
flutter run -v

# Check for issues
flutter doctor

# Analyze code
flutter analyze
```

### Building

```bash
# Debug APK
flutter build apk --debug

# Release APK
flutter build apk --release

# App bundle for Play Store
flutter build appbundle --release
```

## Code Style

- Follow [Effective Dart](https://dart.dev/guides/language/effective-dart) guidelines
- Use `flutter format` before committing
- Run `flutter analyze` to check for issues
- Add comments for complex logic
- Keep widgets small and focused

## Git Workflow

```bash
# Create feature branch
git checkout -b feature/your-feature

# Make changes and commit
git add .
git commit -m "feat: add your feature"

# Push and create PR
git push origin feature/your-feature
```

## Commit Message Convention

- `feat:` New feature
- `fix:` Bug fix
- `docs:` Documentation changes
- `style:` Code style changes (formatting)
- `refactor:` Code refactoring
- `test:` Adding tests
- `chore:` Maintenance tasks

## Useful Commands

```bash
# Clean build artifacts
flutter clean

# Update dependencies
flutter pub upgrade

# Check outdated packages
flutter pub outdated

# Generate code (if using code generation)
flutter pub run build_runner build

# Run tests
flutter test

# Check app size
flutter build apk --analyze-size
```

## Troubleshooting

### Build Issues
```bash
flutter clean
flutter pub get
flutter run
```

### Android Issues
- Check Android SDK is installed
- Verify `ANDROID_HOME` environment variable
- Update Android SDK tools
- Check device is connected: `flutter devices`

### Permission Issues
- Ensure `QUERY_ALL_PACKAGES` is in manifest
- For Android 11+, may need to declare package visibility

## Resources

- [Flutter Documentation](https://docs.flutter.dev/)
- [Dart Language Tour](https://dart.dev/guides/language/language-tour)
- [Material Design 3](https://m3.material.io/)
- [Android Launcher Development](https://developer.android.com/guide/components/activities/recents)

## Next Steps

1. Implement app discovery using `device_apps` package
2. Add app launching functionality
3. Build search and filter logic
4. Design plugin system architecture
5. Create settings screen

See [ROADMAP.md](ROADMAP.md) for detailed development phases.
