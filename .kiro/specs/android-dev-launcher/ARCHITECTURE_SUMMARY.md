# Android Developer Launcher - Architecture Summary

## The Pivot

**From**: Overengineered all-in-one tool with Python backend, round-robin key management, and built-in everything

**To**: Lean, plugin-based platform with Kotlin-only, BYO Gemini, and minimal core

## Core Philosophy

> **"The launcher does almost nothing… but enables everything."**

If your core grows fat → you've already lost.

## What You Just Built

### Before (Bloated)

```
┌─────────────────────────────────────────┐
│  Launcher Core                          │
│  ├─ Python Backend                      │
│  ├─ Round-Robin Key Manager             │
│  ├─ Context Analyzer                    │
│  ├─ Usage Stats Collector               │
│  ├─ Speedtest Service                   │
│  ├─ Terminal Service                    │
│  ├─ Docker Service                      │
│  ├─ Remote Desktop Service              │
│  ├─ API Tester                          │
│  ├─ Clipboard Manager                   │
│  ├─ JSON Formatter                      │
│  ├─ Port Scanner                        │
│  ├─ Base64 Encoder                      │
│  ├─ Environment Variables Manager       │
│  ├─ Git Repository Widget               │
│  ├─ VPN Manager                         │
│  └─ ... and more                        │
└─────────────────────────────────────────┘
```

**Problems**:
- Heavy (Python + Kotlin)
- Complex (IPC, lifecycle hell)
- Bloated (too many features)
- Fragile (tight coupling)
- Hard to maintain

### After (Lean)

```
┌─────────────────────────────────────────┐
│  Launcher Core (Sacred - DO NOT BLOAT)  │
│  ├─ App Launcher (grid)                 │
│  ├─ Global Search (command palette)     │
│  ├─ Plugin Manager                      │
│  └─ Event Bus                           │
└─────────────────────────────────────────┘
              ↓
┌─────────────────────────────────────────┐
│  Plugin System (The Real Product)       │
│  ├─ Plugin Loader                       │
│  ├─ Command Registry                    │
│  ├─ Plugin Context                      │
│  └─ Permission System                   │
└─────────────────────────────────────────┘
              ↓
┌─────────────────────────────────────────┐
│  Plugins (Everything Else)              │
│  ├─ Terminal Plugin                     │
│  ├─ AI Assistant Plugin (BYO Gemini)    │
│  ├─ App Enhancements Plugin             │
│  ├─ API Tester Plugin                   │
│  ├─ Remote Desktop Plugin               │
│  ├─ Docker Plugin                       │
│  └─ ... more plugins                    │
└─────────────────────────────────────────┘
```

**Benefits**:
- Light (Kotlin-only)
- Simple (no IPC)
- Focused (minimal core)
- Flexible (plugins are isolated)
- Maintainable (clear boundaries)

## Key Architectural Decisions

### 1. Kotlin-Only (No Python)

**Before**: Python backend for Gemini integration  
**After**: Kotlin with OkHttp for API calls

**Why**: 
- No IPC complexity
- No lifecycle hell
- No packaging nightmare
- No battery drain from background Python process

### 2. BYO Gemini (No Round-Robin)

**Before**: Round-robin API key management with SQLite, rate limit tracking, automatic rotation  
**After**: User provides one API key in plugin settings

**Why**:
- You're solving "enterprise scale" before you have users
- Round-robin is clever but premature
- Start simple: one key, optional fallback
- Add rotation later if needed

### 3. Plugin-First (No Built-In Everything)

**Before**: Terminal, speedtest, docker, remote desktop, API tester, clipboard, JSON formatter, port scanner, base64 encoder, env vars, git status, VPN manager all built-in  
**After**: Core launcher + plugin system, everything else is a plugin

**Why**:
- Core stays lean and fast
- Features are isolated and maintainable
- Users enable only what they need
- Easy to add/remove features
- Clear separation of concerns

### 4. Command-Driven (No Widget Hell)

**Before**: Complex widget system with placement, resizing, multiple home screens  
**After**: Global search as command palette (Raycast/Spotlight style)

**Why**:
- Faster interaction (keyboard-driven)
- Less UI clutter
- More powerful (commands > widgets)
- Familiar pattern (developers know Raycast/Alfred)

## Architecture Layers

### Layer 1: Launcher Core (< 5,000 LOC)

**Responsibilities**:
- Display apps in grid
- Handle app launches
- Provide global search interface
- Load and manage plugins
- Route commands to plugins

**What It Does NOT Do**:
- ❌ AI integration
- ❌ Terminal emulation
- ❌ Network tools
- ❌ Complex background services
- ❌ Usage tracking
- ❌ Context analysis

### Layer 2: Plugin System

**Components**:
- **Plugin Interface**: `onLoad()`, `onUnload()`
- **Plugin Types**: `UIPlugin`, `CommandPlugin`, `BackgroundPlugin`
- **Plugin Context**: Controlled access to core functionality
- **Event Bus**: Decoupled plugin communication
- **Command Registry**: Register commands for global search
- **Permission System**: User controls what plugins can do

**Key Insight**: This is the real product. The core is just infrastructure.

### Layer 3: Plugins

**Phase 1 (Core)**:
1. Terminal Plugin
2. AI Assistant Plugin (BYO Gemini)
3. App Enhancements Plugin (favorites, recent, categories)

**Phase 2 (Developer Tools)**:
4. API Tester Plugin
5. Remote Desktop Plugin
6. Docker Plugin
7. Git Status Plugin
8. Clipboard History Plugin
9. JSON/XML Formatter Plugin
10. Port Scanner Plugin
11. Base64/Hash Tools Plugin
12. Environment Variables Plugin

**Phase 3 (Productivity)**:
13. Notes Plugin
14. Tasks Plugin
15. Pomodoro Timer Plugin
16. Calculator Plugin

## Plugin Communication

Plugins communicate via **Event Bus** to avoid tight coupling.

**Example Flow**:

```
User launches app
    ↓
Core publishes "app.launched" event
    ↓
Usage Stats Plugin receives event
    ↓
Usage Stats Plugin updates stats
    ↓
Usage Stats Plugin publishes "usage.updated" event
    ↓
AI Assistant Plugin receives event
    ↓
AI Assistant Plugin updates context
    ↓
User types "suggest"
    ↓
AI Assistant Plugin generates suggestions using updated context
```

**Key Insight**: Plugins don't know about each other. They only know about events.

## Global Search (The Secret Weapon)

This is where everything converges.

**User types**:
- `ssh home-server` → Remote Desktop Plugin executes
- `ask explain docker` → AI Assistant Plugin executes
- `run ls -la` → Terminal Plugin executes
- `spotify` → Launches Spotify app

**How It Works**:
1. User types query
2. Core queries:
   - Installed apps (fuzzy match)
   - Registered commands (exact + fuzzy match)
   - AI suggestions (if AI plugin enabled)
3. Results ranked by relevance
4. User selects result
5. Core executes corresponding action

**Key Insight**: Think Raycast/Spotlight, not traditional launcher.

## Security Model

### Permission System

Plugins declare required permissions:

```kotlin
data class PluginPermissions(
    val internet: Boolean = false,
    val storage: Boolean = false,
    val shellAccess: Boolean = false,
    val systemInfo: Boolean = false
)
```

**Flow**:
1. User enables plugin
2. Launcher shows permission dialog
3. User approves or denies
4. If approved → plugin loads
5. If denied → plugin stays disabled

### Sandboxing

- Plugins run in same process (for simplicity)
- `PluginContext` provides controlled access
- Plugins cannot access other plugins' data
- Event Bus is the only inter-plugin communication

### Dangerous Operations

Plugins with `shellAccess` or `internet` should:
- Warn before risky commands
- Implement command blacklists
- Log all operations

## Performance Targets

| Metric | Target | Why |
|--------|--------|-----|
| Launch time | < 200ms | Faster than default launcher |
| Memory (idle) | < 50MB | Minimal footprint |
| Memory (5 plugins) | < 100MB | Still lightweight |
| Search response | < 100ms | Instant feedback |
| Battery drain | < 1% per hour | Respect device resources |

## What You Achieved

**Before**: Big messy all-in-one tool  
**After**: Modular dev launcher platform

This is how tools like:
- Raycast
- Alfred
- VS Code

...become ecosystems.

## Implementation Roadmap

### Phase 1: Core (Week 1-2)

1. ✅ App launcher (grid layout)
2. ✅ Global search (command palette)
3. ✅ Plugin manager (load/unload)
4. ✅ Event bus (pub/sub)
5. ✅ Settings (theme, grid size)

**Success Criteria**: Can launch apps and search

### Phase 2: First Plugin (Week 3)

6. ✅ Terminal Plugin (shell emulator)
   - Commands: `term`, `run <command>`
   - PTY integration
   - Session persistence

**Success Criteria**: Can run shell commands from launcher

### Phase 3: Second Plugin (Week 4)

7. ✅ AI Assistant Plugin (Gemini wrapper)
   - Commands: `ask <question>`, `explain <topic>`
   - BYO API key
   - Error handling

**Success Criteria**: Can ask AI questions from launcher

### Phase 4: Polish (Week 5)

8. ✅ App Enhancements Plugin
   - Favorites
   - Recent apps
   - Categories
9. ✅ Performance optimization
10. ✅ Testing (unit + integration)

**Success Criteria**: Ready for beta testing

### Phase 5: More Plugins (Week 6+)

11. API Tester Plugin
12. Remote Desktop Plugin
13. Docker Plugin
14. ... more as needed

**Success Criteria**: Ecosystem is growing

## Critical Rules

### DO

✅ Keep core minimal (< 5,000 LOC)  
✅ Build plugin system first  
✅ Then build ONE plugin (terminal or AI)  
✅ If that feels clean → continue  
✅ If it feels painful → fix architecture before adding features  
✅ Use EventBus for plugin communication  
✅ Declare all permissions upfront  
✅ Handle errors gracefully  
✅ Respect Android lifecycle  

### DON'T

❌ Don't add features to core (make them plugins)  
❌ Don't let plugins access each other's storage  
❌ Don't run long-blocking operations on main thread  
❌ Don't assume other plugins are installed  
❌ Don't leak memory  
❌ Don't bypass permission system  
❌ Don't hardcode API keys  

## One Line to Carry Forward

> **"If it can be a plugin… it MUST be a plugin."**

## Next Steps

1. **Read**: `PLUGIN_ARCHITECTURE.md` for detailed plugin design
2. **Read**: `REQUIREMENTS_V2.md` for clean requirements
3. **Build**: Core launcher + plugin system
4. **Build**: First plugin (Terminal or AI Assistant)
5. **Validate**: If architecture feels clean → continue
6. **Iterate**: If architecture feels painful → fix before adding more

## Files in This Spec

- `ARCHITECTURE_SUMMARY.md` (this file) - High-level overview
- `PLUGIN_ARCHITECTURE.md` - Detailed plugin system design
- `REQUIREMENTS_V2.md` - Clean, focused requirements
- `design.md` (updated) - Original design, refactored for plugin architecture
- `requirements.md` (legacy) - Original requirements, kept for reference

## Questions?

**Q: Why not keep Python backend?**  
A: Adds complexity (IPC, lifecycle, packaging) without clear benefit. Kotlin + OkHttp is simpler.

**Q: Why not round-robin key management?**  
A: Solving enterprise scale before you have users. Start simple, add later if needed.

**Q: Why not built-in everything?**  
A: Core bloat. Plugin system allows users to enable only what they need.

**Q: Why command palette instead of widgets?**  
A: Faster, more powerful, keyboard-driven. Developers know Raycast/Alfred.

**Q: What if I need a feature that's not a plugin?**  
A: Make it a plugin. Seriously. If it can be a plugin, it MUST be a plugin.

**Q: How do I add external plugins later?**  
A: Phase 2 feature. Load plugins from `/plugins/` directory or APK splits. Requires signature verification and sandboxing.

**Q: What about security?**  
A: Permission system + sandboxing + user approval. Plugins declare what they need, users approve or deny.

**Q: What about performance?**  
A: Minimal core (< 50MB RAM), lazy plugin loading, aggressive lifecycle management.

**Q: What about battery?**  
A: No long-running background services in core. Plugins manage their own background work with WorkManager.

## Final Thoughts

You just pivoted from:
- "What can I build?"

To:
- "What MUST exist first?"

That's the difference between a prototype and a product.

Now go build the core + plugin system. Then build ONE plugin. If that feels clean, you're on the right track.

If it feels painful, fix the architecture before adding more features.

**Remember**: The plugin system is the product. The core is just infrastructure.

Good luck. 🔥
