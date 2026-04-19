# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added (Phase 1 Complete!)
- Initial Flutter project setup with Material 3 dark theme
- Android launcher manifest configuration (HOME intent)
- App discovery using device_apps package
- App launching functionality
- Search overlay with swipe-up gesture
- Real-time app search and filtering
- App drawer with grid view (4 columns)
- App icons with names
- Long-press for app info/settings
- Quick action chips (Apps, Settings, Plugins)
- Provider state management integration
- Fullscreen immersive mode
- **Favorites system** - Add/remove apps from favorites
- **Recent apps tracking** - Automatically track launched apps
- **Home screen favorites display** - Horizontal scrollable row
- **Persistent storage** - SharedPreferences for user data
- **Enhanced app options menu** - Favorite toggle + app info

### Changed
- Migrated from Kotlin/Jetpack Compose to Flutter
- Updated deprecated `withOpacity()` to `withValues(alpha:)`

### Technical
- Dependencies: device_apps, provider, shared_preferences
- Architecture: Provider pattern for state management
- Components: AppManager, AppIconWidget, AppListItem, AppDrawerScreen
- Permissions: QUERY_ALL_PACKAGES for app discovery

## [0.1.0] - 2026-04-19

### Initial Release
- Project inception and architecture design
- Documentation: README, ROADMAP, ARCHITECTURE, DEVELOPMENT guides
- Phase 1 core launcher features completed
