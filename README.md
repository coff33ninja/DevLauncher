# DevLauncher - Plugin-Based Android Launcher

A minimal, plugin-based Android launcher designed for developers.

## Architecture

**Core Philosophy**: "The launcher does almost nothing… but enables everything."

### Core (< 5,000 LOC)
- App launcher (grid)
- Global search (command palette)
- Plugin manager
- Event bus

### Everything Else = Plugins
- Terminal
- AI Assistant (BYO Gemini)
- API Tester
- Remote Desktop
- Docker Manager
- ... and more

## Project Structure

```
app/src/main/kotlin/com/devlauncher/
├── data/
│   ├── AppInfo.kt              # App data model
│   └── AppRepository.kt        # App access layer
├── plugin/
│   ├── Plugin.kt               # Base plugin interface
│   ├── PluginTypes.kt          # UIPlugin, CommandPlugin, BackgroundPlugin
│   ├── PluginContext.kt        # Plugin context and storage
│   ├── EventBus.kt             # Plugin communication
│   ├── CommandRegistry.kt      # Command registration
│   └── PluginLoader.kt         # Plugin lifecycle management
├── ui/
│   ├── LauncherViewModel.kt    # Main view model
│   ├── HomeScreen.kt           # Home screen UI
│   ├── GlobalSearchScreen.kt   # Search UI
│   └── theme/                  # Material 3 theme
├── LauncherApplication.kt      # Application class
└── MainActivity.kt             # Main activity
```

## Building

### Prerequisites
- Android Studio Hedgehog or later
- JDK 17
- Android SDK 34

### Build Steps

1. Open project in Android Studio
2. Sync Gradle
3. Build APK: `./gradlew assembleDebug`
4. Install: `./gradlew installDebug`

Or use Android Studio's build/run buttons.

## Running

1. Build and install the APK
2. Press the home button
3. Select "DevLauncher" as your default launcher
4. Enjoy!

## Current Status

✅ **Core Implemented**:
- App launcher with grid layout
- Global search (command palette)
- Plugin system (loader, registry, event bus)
- Material 3 UI with Jetpack Compose

⏳ **Next Steps**:
1. Build first plugin (Terminal or AI Assistant)
2. Test plugin system
3. Validate architecture
4. Add more plugins

## Adding Plugins

Plugins go in `app/src/main/kotlin/com/devlauncher/plugins/`

Example plugin structure:
```kotlin
class MyPlugin : CommandPlugin {
    override val id = "my-plugin"
    override val name = "My Plugin"
    override val version = "1.0.0"
    override val description = "Does something cool"
    override val permissions = PluginPermissions()
    
    override fun onLoad(context: PluginContext) {
        // Initialize
    }
    
    override fun onUnload() {
        // Cleanup
    }
    
    override fun commands(): List<Command> = listOf(
        Command(
            name = "mycommand",
            description = "My command",
            execute = { args ->
                CommandResult.Success("Hello!")
            }
        )
    )
}
```

Then register in `PluginLoader.loadBuiltInPlugins()`:
```kotlin
val builtInPlugins = listOf(
    MyPlugin()
)
```

## Documentation

See `.kiro/specs/android-dev-launcher/` for detailed documentation:
- `README.md` - Overview and navigation
- `ARCHITECTURE_SUMMARY.md` - Architecture explanation
- `PLUGIN_ARCHITECTURE.md` - Plugin system details
- `QUICKSTART.md` - Implementation guide
- `REQUIREMENTS_V2.md` - Requirements

## Philosophy

> **"If it can be a plugin… it MUST be a plugin."**

The core is intentionally minimal. All features beyond basic app launching and search are implemented as plugins.

This keeps the core:
- Fast (< 50MB RAM idle)
- Simple (< 5,000 LOC)
- Maintainable (clear boundaries)
- Extensible (plugins are isolated)

## License

MIT License - See LICENSE file

## Contributing

1. Read the architecture docs
2. Build the core
3. Create a plugin
4. Submit a PR

Focus on:
- Keeping core minimal
- Making features as plugins
- Following the architecture
- Writing tests

## Contact

Questions? Open an issue or discussion.

---

**Built with**: Kotlin, Jetpack Compose, Material 3  
**Inspired by**: Raycast, Alfred, VS Code  
**Motto**: "If it can be a plugin… it MUST be a plugin."
