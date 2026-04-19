# Android Developer Launcher - Plugin-Based Architecture

## What Just Happened?

You pivoted from an **overengineered all-in-one tool** to a **lean, plugin-based platform**.

### Before → After

| Aspect | Before (Bloated) | After (Lean) |
|--------|------------------|--------------|
| **Backend** | Python + Kotlin | Kotlin-only |
| **AI Integration** | Round-robin key management | BYO Gemini (user's key) |
| **Features** | Built-in everything | Plugin system |
| **Architecture** | Monolithic | Modular |
| **Complexity** | High (IPC, lifecycle hell) | Low (clean boundaries) |
| **Core Size** | Massive | Minimal (< 5,000 LOC) |
| **Maintainability** | Fragile | Solid |

## Core Philosophy

> **"The launcher does almost nothing… but enables everything."**

The core is intentionally minimal:
- App launcher (grid)
- Global search (command palette)
- Plugin manager
- Event bus

Everything else is a **plugin**.

## Documents in This Spec

### Start Here

1. **`README.md`** (this file) - Overview and navigation
2. **`ARCHITECTURE_SUMMARY.md`** - High-level architecture explanation
3. **`QUICKSTART.md`** - Step-by-step implementation guide

### Deep Dives

4. **`PLUGIN_ARCHITECTURE.md`** - Detailed plugin system design with examples
5. **`REQUIREMENTS_V2.md`** - Clean, focused requirements for plugin-based architecture

### Legacy (Reference Only)

6. **`design.md`** - Original design (partially refactored)
7. **`requirements.md`** - Original requirements (kept for reference)

## Quick Navigation

### Want to understand the architecture?
→ Read `ARCHITECTURE_SUMMARY.md`

### Want to start building?
→ Read `QUICKSTART.md`

### Want to understand plugins?
→ Read `PLUGIN_ARCHITECTURE.md`

### Want to see requirements?
→ Read `REQUIREMENTS_V2.md`

### Want to see what changed?
→ Compare `design.md` (old) with `ARCHITECTURE_SUMMARY.md` (new)

## Key Decisions

### 1. Kotlin-Only (No Python)

**Why**: Eliminates IPC complexity, lifecycle hell, packaging nightmare, and battery drain.

**Impact**: Simpler, faster, more maintainable.

### 2. BYO Gemini (No Round-Robin)

**Why**: You were solving "enterprise scale" before having users. Start simple.

**Impact**: User provides one API key. Add rotation later if needed.

### 3. Plugin-First (No Built-In Everything)

**Why**: Core stays lean. Features are isolated. Users enable only what they need.

**Impact**: Minimal core, maximum extensibility.

### 4. Command-Driven (No Widget Hell)

**Why**: Faster interaction, less clutter, more powerful, familiar pattern (Raycast/Alfred).

**Impact**: Global search becomes the primary interface.

## What You're Building

### Phase 1: Core (Week 1-2)
- App launcher (grid)
- Global search (command palette)
- Plugin manager
- Event bus
- Settings

### Phase 2: First Plugin (Week 3)
- Terminal Plugin
  - Commands: `term`, `run <command>`
  - PTY integration
  - Session persistence

### Phase 3: Second Plugin (Week 4)
- AI Assistant Plugin
  - Commands: `ask <question>`, `explain <topic>`
  - BYO Gemini API key
  - Error handling

### Phase 4: Polish (Week 5)
- App Enhancements Plugin (favorites, recent, categories)
- Performance optimization
- Testing

### Phase 5: More Plugins (Week 6+)
- API Tester
- Remote Desktop
- Docker Manager
- ... and more

## Architecture at a Glance

```
┌─────────────────────────────────────────┐
│  Launcher Core (Minimal)                │
│  ├─ App Launcher                        │
│  ├─ Global Search                       │
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
│  ├─ Terminal                            │
│  ├─ AI Assistant (BYO Gemini)           │
│  ├─ App Enhancements                    │
│  ├─ API Tester                          │
│  ├─ Remote Desktop                      │
│  └─ ... more                            │
└─────────────────────────────────────────┘
```

## Plugin Communication

Plugins communicate via **Event Bus** (no tight coupling):

```
User launches app
    ↓
Core publishes "app.launched" event
    ↓
Usage Stats Plugin updates stats
    ↓
Usage Stats Plugin publishes "usage.updated" event
    ↓
AI Assistant Plugin updates context
    ↓
User types "suggest"
    ↓
AI Assistant generates suggestions
```

## Global Search (The Secret Weapon)

User types → Core searches:
1. Installed apps (fuzzy match)
2. Plugin commands (exact + fuzzy)
3. AI suggestions (if enabled)

Results ranked by relevance → User selects → Action executes

**Examples**:
- `ssh home-server` → Remote Desktop Plugin
- `ask explain docker` → AI Assistant Plugin
- `run ls -la` → Terminal Plugin
- `spotify` → Launches Spotify

## Performance Targets

| Metric | Target |
|--------|--------|
| Launch time | < 200ms |
| Memory (idle) | < 50MB |
| Memory (5 plugins) | < 100MB |
| Search response | < 100ms |
| Battery drain | < 1% per hour |

## Critical Rules

### DO

✅ Keep core minimal (< 5,000 LOC)  
✅ Build plugin system first  
✅ Then build ONE plugin  
✅ Validate architecture before adding more  
✅ Use EventBus for plugin communication  
✅ Declare all permissions upfront  
✅ Handle errors gracefully  

### DON'T

❌ Add features to core (make them plugins)  
❌ Let plugins access each other's storage  
❌ Run long-blocking operations on main thread  
❌ Assume other plugins are installed  
❌ Leak memory  
❌ Bypass permission system  

## One Line to Remember

> **"If it can be a plugin… it MUST be a plugin."**

## Getting Started

1. **Read**: `ARCHITECTURE_SUMMARY.md` for high-level overview
2. **Read**: `QUICKSTART.md` for implementation guide
3. **Build**: Core launcher + plugin system
4. **Build**: First plugin (Terminal or AI)
5. **Validate**: Architecture feels clean?
6. **Continue**: Add more plugins

## What You Achieved

**Before**: Big messy all-in-one tool  
**After**: Modular dev launcher platform

This is how tools like Raycast, Alfred, and VS Code become ecosystems.

## Questions?

### "Why not keep Python backend?"
Adds complexity without clear benefit. Kotlin + OkHttp is simpler.

### "Why not round-robin key management?"
Solving enterprise scale before having users. Start simple.

### "Why not built-in everything?"
Core bloat. Plugin system allows users to enable only what they need.

### "Why command palette instead of widgets?"
Faster, more powerful, keyboard-driven. Developers know Raycast/Alfred.

### "What if I need a feature that's not a plugin?"
Make it a plugin. Seriously.

## Next Steps

1. Read `ARCHITECTURE_SUMMARY.md`
2. Read `QUICKSTART.md`
3. Start building
4. Validate architecture
5. Keep building

## Final Thoughts

You just pivoted from:
- "What can I build?"

To:
- "What MUST exist first?"

That's the difference between a prototype and a product.

Now go build the core + plugin system. Then build ONE plugin. If that feels clean, you're on the right track.

**Remember**: The plugin system is the product. The core is just infrastructure.

Good luck. 🔥

---

## File Structure

```
.kiro/specs/android-dev-launcher/
├── README.md                      # This file - start here
├── ARCHITECTURE_SUMMARY.md        # High-level architecture
├── QUICKSTART.md                  # Implementation guide
├── PLUGIN_ARCHITECTURE.md         # Detailed plugin design
├── REQUIREMENTS_V2.md             # Clean requirements
├── design.md                      # Original design (legacy)
└── requirements.md                # Original requirements (legacy)
```

## Status

- ✅ Architecture defined
- ✅ Requirements documented
- ✅ Plugin system designed
- ✅ Implementation guide ready
- ⏳ Core implementation (next step)
- ⏳ First plugin (after core)
- ⏳ Validation (after first plugin)

## Contact

Questions? Issues? Feedback?

Open an issue or start a discussion.

---

**Built with**: Kotlin, Jetpack Compose, Android  
**Inspired by**: Raycast, Alfred, VS Code  
**Philosophy**: Minimal core, maximum extensibility  
**Motto**: "If it can be a plugin… it MUST be a plugin."
