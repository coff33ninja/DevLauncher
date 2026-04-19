# Architecture Overview

## Project Structure

```
lib/
├── main.dart                 # App entry point
├── core/                     # Core launcher functionality
│   ├── app_manager.dart      # App discovery and management
│   ├── launcher_service.dart # Launch apps and handle intents
│   └── settings.dart         # App settings and preferences
├── plugin/                   # Plugin system
│   ├── plugin_loader.dart    # Load and manage plugins
│   ├── plugin_api.dart       # Plugin API interface
│   ├── event_bus.dart        # Event system for plugins
│   └── command_registry.dart # Command registration
├── ui/                       # User interface
│   ├── screens/              # Main screens
│   │   ├── home_screen.dart
│   │   ├── search_screen.dart
│   │   ├── settings_screen.dart
│   │   └── plugins_screen.dart
│   ├── widgets/              # Reusable widgets
│   │   ├── app_icon.dart
│   │   ├── search_bar.dart
│   │   └── plugin_card.dart
│   └── theme/                # Theming
│       ├── colors.dart
│       └── styles.dart
└── utils/                    # Utilities
    ├── logger.dart
    └── helpers.dart
```

## Core Components

### 1. App Manager
Responsible for:
- Discovering installed apps
- Caching app information
- Monitoring app installations/uninstallations
- Providing app metadata (name, icon, package)

### 2. Launcher Service
Handles:
- Launching applications
- Intent handling
- Deep linking
- App shortcuts

### 3. Plugin System
Architecture:
- **Plugin Loader**: Discovers and loads plugins
- **Plugin API**: Interface for plugins to interact with launcher
- **Event Bus**: Pub/sub system for plugin communication
- **Command Registry**: Register and execute commands

### 4. UI Layer
Built with:
- Material Design 3
- Responsive layouts
- Gesture-based navigation
- Smooth animations

## Plugin Architecture

### Plugin Interface
```dart
abstract class LauncherPlugin {
  String get id;
  String get name;
  String get version;
  
  Future<void> onLoad(PluginContext context);
  Future<void> onUnload();
  
  List<Command> get commands;
  List<Widget> get widgets;
}
```

### Plugin Context
Provides plugins access to:
- App manager
- Settings
- Event bus
- Command registry
- UI hooks

### Event System
Plugins can:
- Subscribe to events (app launched, search query, etc.)
- Publish custom events
- React to system events

## Data Flow

```
User Input → UI Layer → Core Services → Plugin System
                ↓           ↓              ↓
            State Mgmt   App Manager   Event Bus
                ↓           ↓              ↓
            UI Update   Launch App    Plugin Actions
```

## State Management

Using **Provider** pattern for:
- App list state
- Search state
- Settings state
- Plugin state

## Performance Considerations

1. **Lazy Loading**: Load apps and plugins on demand
2. **Caching**: Cache app icons and metadata
3. **Background Processing**: Use isolates for heavy operations
4. **Efficient Rendering**: Minimize rebuilds with proper state management

## Security

1. **Plugin Sandboxing**: Limit plugin access to sensitive APIs
2. **Permission System**: Plugins request permissions
3. **Code Signing**: Verify plugin integrity
4. **Secure Storage**: Encrypt sensitive data

## Testing Strategy

1. **Unit Tests**: Core logic and utilities
2. **Widget Tests**: UI components
3. **Integration Tests**: End-to-end flows
4. **Plugin Tests**: Plugin API and lifecycle

## Build & Deployment

- **Development**: Hot reload for rapid iteration
- **Staging**: Internal testing builds
- **Production**: Signed release builds
- **Distribution**: GitHub releases, F-Droid (future)
