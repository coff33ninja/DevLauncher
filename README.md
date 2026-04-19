# Developer Launcher

## The Idea

A **plugin-extensible Android launcher** designed specifically for developers and power users who want complete control over their mobile experience.

### Core Concept

Traditional Android launchers are rigid and limited. This project aims to create a launcher that works like a **development platform** rather than just an app drawer:

- **Plugin Architecture**: Extend functionality through plugins, similar to VS Code or Neovim
- **Command-Driven Interface**: Global search that acts as a command palette
- **Developer-First Design**: Built for automation, scripting, and customization
- **Minimal Core, Maximum Flexibility**: The launcher itself is just a framework; plugins provide the features

### Why This Matters

**For Developers:**
- Quick access to development tools and workflows
- Scriptable actions and automations
- Integration with development environments
- Custom shortcuts and commands

**For Power Users:**
- Complete customization without rooting
- Workflow automation
- Advanced app management
- Personalized UI/UX

### Key Features (Planned)

1. **Plugin System**
   - Hot-reloadable plugins
   - Event-driven architecture
   - Command registry for global actions
   - Plugin marketplace

2. **Global Search/Command Palette**
   - Fuzzy search across apps, contacts, files
   - Execute custom commands
   - Plugin-contributed actions
   - Keyboard shortcuts

3. **Extensible UI**
   - Plugins can contribute UI components
   - Themeable and customizable
   - Gesture support
   - Widget system

4. **Developer Tools Integration**
   - ADB command execution
   - Log viewing
   - Package management
   - Quick settings toggles

### Technology Stack

**Current Status**: Migrating from Kotlin/Jetpack Compose to Flutter

**Why Flutter?**
- Superior design flexibility and hot reload
- Cross-platform potential (Android, iOS, desktop)
- Rich widget ecosystem
- Better tooling for rapid UI iteration
- Unified codebase for consistent experience

### Architecture

```
┌─────────────────────────────────────┐
│         Launcher Core               │
│  (Minimal, Framework Only)          │
├─────────────────────────────────────┤
│         Plugin System               │
│  - Plugin Loader                    │
│  - Event Bus                        │
│  - Command Registry                 │
├─────────────────────────────────────┤
│         Plugin Ecosystem            │
│  - App Drawer Plugin                │
│  - Search Plugin                    │
│  - Widgets Plugin                   │
│  - Developer Tools Plugin           │
│  - Custom User Plugins              │
└─────────────────────────────────────┘
```

### Philosophy

1. **Extensibility Over Features**: The core should be minimal; plugins provide functionality
2. **Developer Experience First**: Optimize for customization and automation
3. **Open and Transparent**: Open source, community-driven development
4. **Performance Matters**: Fast, responsive, battery-efficient
5. **Privacy Focused**: No telemetry, no ads, user data stays local

### Use Cases

- **Development Workflow**: Quick access to terminals, IDEs, documentation
- **Automation**: Trigger scripts and workflows from home screen
- **Custom Interfaces**: Build your own launcher UI through plugins
- **Power User Tools**: Advanced file management, system controls
- **Learning Platform**: Experiment with Android APIs and UI patterns

### Project Status

🚧 **In Development** - Currently migrating to Flutter for better design capabilities

The original Kotlin/Jetpack Compose implementation proved the concept but lacked the design flexibility needed for a truly customizable launcher. Flutter provides the tools to create the beautiful, fluid interface this project deserves.

### Contributing

This project is in early stages. Contributions, ideas, and feedback are welcome!

### License

To be determined - will be open source

---

**Note**: This is a passion project exploring what's possible when you treat a launcher as a development platform rather than just an app drawer.
