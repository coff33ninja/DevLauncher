# Contributing to DevLauncher

Thanks for your interest in contributing! 🔥

## Philosophy

> **"If it can be a plugin… it MUST be a plugin."**

Keep the core minimal. Add features as plugins.

## How to Contribute

### 1. Core Development

**Before touching core**:
- Read `.kiro/specs/android-dev-launcher/ARCHITECTURE_SUMMARY.md`
- Understand the plugin-first philosophy
- Ask yourself: "Can this be a plugin instead?"

**Core changes should**:
- Keep core < 5,000 LOC
- Maintain < 200ms launch time
- Keep memory < 50MB idle
- Not add features (make them plugins)

### 2. Plugin Development

**This is where most contributions should go!**

**Steps**:
1. Read `.kiro/specs/android-dev-launcher/PLUGIN_ARCHITECTURE.md`
2. Create plugin in `app/src/main/kotlin/com/devlauncher/plugins/`
3. Implement `Plugin` interface
4. Register in `PluginLoader.loadBuiltInPlugins()`
5. Test thoroughly
6. Submit PR

**Plugin checklist**:
- [ ] Implements `Plugin` interface
- [ ] Declares all required permissions
- [ ] Handles errors gracefully
- [ ] Cleans up in `onUnload()`
- [ ] Includes tests
- [ ] Updates documentation

### 3. Documentation

**Always welcome**:
- Fix typos
- Improve clarity
- Add examples
- Update outdated info

### 4. Bug Fixes

**Steps**:
1. Open an issue describing the bug
2. Fork the repo
3. Create a branch: `fix/bug-description`
4. Fix the bug
5. Add tests
6. Submit PR referencing the issue

## Development Setup

### Prerequisites
- Android Studio Hedgehog or later
- JDK 17
- Android SDK 34

### Setup
```bash
# Clone
git clone https://github.com/coff33ninja/DevLauncher.git
cd DevLauncher

# Open in Android Studio
# File → Open → Select directory

# Build
./gradlew assembleDebug

# Run tests
./gradlew test
```

## Code Style

- **Kotlin**: Follow official Kotlin style guide
- **Compose**: Use Material 3 components
- **Comments**: Explain why, not what
- **Naming**: Clear, descriptive names

## Testing

**Required**:
- Unit tests for new functionality
- Integration tests for plugin system changes
- Manual testing on real device

**Run tests**:
```bash
./gradlew test
./gradlew connectedAndroidTest
```

## Pull Request Process

1. **Fork** the repository
2. **Create** a feature branch
3. **Make** your changes
4. **Test** thoroughly
5. **Update** documentation
6. **Submit** PR with clear description

### PR Checklist

- [ ] Code follows style guide
- [ ] Tests pass
- [ ] Documentation updated
- [ ] Commit messages are clear
- [ ] No merge conflicts
- [ ] PR description explains changes

### PR Title Format

```
[Type] Brief description

Types:
- feat: New feature
- fix: Bug fix
- docs: Documentation
- refactor: Code refactoring
- test: Tests
- chore: Maintenance
```

**Examples**:
- `feat: Add Terminal plugin`
- `fix: Search crash on empty query`
- `docs: Update plugin architecture guide`

## Plugin Ideas

Looking for contribution ideas? Build these plugins:

**High Priority**:
- Terminal Plugin (shell emulator)
- AI Assistant Plugin (Gemini integration)
- App Enhancements Plugin (favorites, recent)

**Medium Priority**:
- API Tester Plugin
- Remote Desktop Plugin
- Docker Manager Plugin
- Git Status Plugin

**Low Priority**:
- Clipboard History Plugin
- JSON Formatter Plugin
- Port Scanner Plugin
- Calculator Plugin

## Questions?

- **Architecture**: Read `.kiro/specs/android-dev-launcher/`
- **Issues**: Open a GitHub issue
- **Discussions**: Use GitHub Discussions

## Code of Conduct

**Be respectful**:
- Constructive feedback
- Inclusive language
- Professional behavior

**Zero tolerance for**:
- Harassment
- Discrimination
- Spam

## Recognition

Contributors will be:
- Listed in README.md
- Credited in release notes
- Appreciated forever 🙏

## License

By contributing, you agree that your contributions will be licensed under the MIT License.

---

**Remember**: The plugin system is the product. The core is just infrastructure.

Thanks for contributing! 🔥
