# Plugin Architecture Guide

## Philosophy

**"If it can be a plugin… it MUST be a plugin."**

The launcher core is intentionally minimal. All features beyond basic app launching and search are implemented as plugins.

## Plugin Types

### 1. UI Plugin

Renders UI components, optionally requests a dedicated screen.

**Example: Terminal Plugin**

```kotlin
class TerminalPlugin : UIPlugin, CommandPlugin {
    override val id = "terminal"
    override val name = "Terminal"
    override val version = "1.0.0"
    override val description = "Embedded terminal emulator"
    override val permissions = PluginPermissions(
        shellAccess = true,
        storage = true
    )
    
    private lateinit var context: PluginContext
    private val sessions = mutableListOf<TerminalSession>()
    
    override fun onLoad(context: PluginContext) {
        this.context = context
        
        // Subscribe to events
        context.eventBus.subscribe("app.launched") { event ->
            // Track app launches for context
        }
        
        // Restore saved sessions
        restoreSessions()
    }
    
    override fun onUnload() {
        // Save sessions
        sessions.forEach { it.close() }
    }
    
    override fun commands(): List<Command> = listOf(
        Command(
            name = "term",
            description = "Open terminal",
            aliases = listOf("terminal", "shell"),
            execute = { args ->
                CommandResult.OpenUI { TerminalUI(sessions) }
            }
        ),
        Command(
            name = "run",
            description = "Run shell command",
            aliases = listOf("exec", "sh"),
            execute = { args ->
                val command = args.joinToString(" ")
                val output = executeCommand(command)
                CommandResult.Success(output)
            }
        )
    )
    
    override fun render(): @Composable () -> Unit = {
        TerminalUI(sessions)
    }
    
    override fun getScreenConfig() = ScreenConfig(
        title = "Terminal",
        icon = Icons.Default.Terminal,
        category = ScreenCategory.DEVELOPMENT
    )
    
    private fun executeCommand(command: String): String {
        // Execute command in PTY
        val session = sessions.firstOrNull() ?: createSession()
        return session.execute(command)
    }
    
    private fun createSession(): TerminalSession {
        val session = TerminalSession(
            shell = "/system/bin/sh",
            workingDir = context.appContext.filesDir.absolutePath
        )
        sessions.add(session)
        return session
    }
    
    private fun restoreSessions() {
        val savedSessions = context.storage.getString("sessions")
        // Restore from JSON
    }
}

@Composable
fun TerminalUI(sessions: List<TerminalSession>) {
    Column {
        // Tab bar for multiple sessions
        TabRow(selectedTabIndex = 0) {
            sessions.forEachIndexed { index, session ->
                Tab(
                    selected = index == 0,
                    onClick = { /* switch session */ },
                    text = { Text("Session ${index + 1}") }
                )
            }
        }
        
        // Terminal display
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(sessions[0].outputBuffer) { line ->
                Text(line, fontFamily = FontFamily.Monospace)
            }
        }
        
        // Input field
        TextField(
            value = "",
            onValueChange = { /* handle input */ },
            placeholder = { Text("Enter command...") },
            modifier = Modifier.fillMaxWidth()
        )
    }
}
```

### 2. Command Plugin

Registers commands for global search without UI.

**Example: AI Assistant Plugin**

```kotlin
class AIAssistantPlugin : CommandPlugin {
    override val id = "ai-assistant"
    override val name = "AI Assistant"
    override val version = "1.0.0"
    override val description = "Gemini AI integration (BYO API key)"
    override val permissions = PluginPermissions(internet = true)
    
    private lateinit var context: PluginContext
    private var geminiClient: GeminiClient? = null
    
    override fun onLoad(context: PluginContext) {
        this.context = context
        
        // Load API key from plugin storage
        val apiKey = context.storage.getString("gemini_api_key")
        if (apiKey != null) {
            geminiClient = GeminiClient(apiKey)
        }
    }
    
    override fun onUnload() {
        geminiClient = null
    }
    
    override fun commands(): List<Command> = listOf(
        Command(
            name = "ask",
            description = "Ask Gemini AI a question",
            aliases = listOf("ai", "gemini"),
            execute = { args ->
                val query = args.joinToString(" ")
                if (geminiClient == null) {
                    CommandResult.Error("Gemini API key not configured. Set it in plugin settings.")
                } else {
                    val response = geminiClient!!.ask(query)
                    CommandResult.Success(response)
                }
            }
        ),
        Command(
            name = "explain",
            description = "Explain a concept",
            aliases = listOf("what", "define"),
            execute = { args ->
                val topic = args.joinToString(" ")
                val response = geminiClient?.ask("Explain: $topic") 
                    ?: "API key not configured"
                CommandResult.Success(response)
            }
        )
    )
}

class GeminiClient(private val apiKey: String) {
    private val client = OkHttpClient()
    private val baseUrl = "https://generativelanguage.googleapis.com/v1beta"
    
    suspend fun ask(query: String): String = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url("$baseUrl/models/gemini-2.5-flash:generateContent?key=$apiKey")
            .post(
                JSONObject().apply {
                    put("contents", JSONArray().apply {
                        put(JSONObject().apply {
                            put("parts", JSONArray().apply {
                                put(JSONObject().put("text", query))
                            })
                        })
                    })
                }.toString().toRequestBody("application/json".toMediaType())
            )
            .build()
        
        val response = client.newCall(request).execute()
        if (!response.isSuccessful) {
            throw IOException("API error: ${response.code}")
        }
        
        val json = JSONObject(response.body!!.string())
        json.getJSONArray("candidates")
            .getJSONObject(0)
            .getJSONObject("content")
            .getJSONArray("parts")
            .getJSONObject(0)
            .getString("text")
    }
}
```

### 3. Background Plugin

Runs periodic background tasks.

**Example: Usage Stats Plugin**

```kotlin
class UsageStatsPlugin : BackgroundPlugin {
    override val id = "usage-stats"
    override val name = "Usage Statistics"
    override val version = "1.0.0"
    override val description = "Tracks app usage for AI suggestions"
    override val permissions = PluginPermissions(systemInfo = true)
    
    private lateinit var context: PluginContext
    
    override fun onLoad(context: PluginContext) {
        this.context = context
        
        // Subscribe to app launch events
        context.eventBus.subscribe("app.launched") { event ->
            val launchEvent = event as AppLaunchEvent
            recordLaunch(launchEvent)
        }
    }
    
    override fun onUnload() {
        // Cleanup
    }
    
    override fun onBackgroundStart() {
        // Start collecting stats
    }
    
    override fun onBackgroundStop() {
        // Stop collecting stats
    }
    
    override fun getWorkRequest(): PeriodicWorkRequest {
        return PeriodicWorkRequestBuilder<UsageStatsWorker>(
            repeatInterval = 1,
            repeatIntervalTimeUnit = TimeUnit.HOURS
        ).build()
    }
    
    private fun recordLaunch(event: AppLaunchEvent) {
        val stats = context.storage.getString("stats") ?: "{}"
        // Update stats JSON
        context.storage.putString("stats", updatedStats)
        
        // Publish stats update event for AI plugin
        context.eventBus.publish("usage.updated", UsageStatsEvent(stats))
    }
}

class UsageStatsWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        // Aggregate usage stats
        return Result.success()
    }
}
```

## Plugin Communication

Plugins communicate via the **Event Bus** to avoid tight coupling.

### Example: AI Plugin Uses Usage Stats

```kotlin
// In AIAssistantPlugin.onLoad()
context.eventBus.subscribe("usage.updated") { event ->
    val statsEvent = event as UsageStatsEvent
    // Update AI context with usage stats
    this.usageContext = statsEvent.stats
}

// When generating suggestions
override fun commands(): List<Command> = listOf(
    Command(
        name = "suggest",
        description = "Get AI app suggestions",
        execute = { args ->
            val suggestions = geminiClient?.getSuggestions(
                context = usageContext,
                time = System.currentTimeMillis()
            ) ?: emptyList()
            
            CommandResult.Success(suggestions.joinToString("\n"))
        }
    )
)
```

## Plugin Lifecycle

```
User enables plugin in settings
    ↓
PluginLoader.loadPlugin(plugin)
    ↓
Validate permissions
    ↓
Create PluginContext
    ↓
plugin.onLoad(context)
    ↓
Register commands (if CommandPlugin)
    ↓
Register UI (if UIPlugin)
    ↓
Start background work (if BackgroundPlugin)
    ↓
Plugin is active
    ↓
User disables plugin
    ↓
plugin.onUnload()
    ↓
Unregister commands
    ↓
Stop background work
    ↓
Plugin is inactive
```

## Plugin Security

### Permission System

Plugins declare required permissions:

```kotlin
data class PluginPermissions(
    val internet: Boolean = false,        // Network access
    val storage: Boolean = false,         // File system access
    val shellAccess: Boolean = false,     // Execute shell commands
    val systemInfo: Boolean = false,      // Access usage stats, device info
    val location: Boolean = false,        // GPS location
    val camera: Boolean = false,          // Camera access
    val microphone: Boolean = false       // Microphone access
)
```

**Permission Approval Flow**:
1. User enables plugin
2. Launcher shows permission dialog listing required permissions
3. User approves or denies
4. If approved, plugin loads
5. If denied, plugin remains disabled

### Sandboxing

- Plugins run in the same process (for simplicity) but with restricted access
- `PluginContext` provides controlled access to core functionality
- Plugins cannot directly access other plugins' data
- Event Bus is the only inter-plugin communication channel

### Dangerous Operations

Plugins that request `shellAccess` or `internet` permissions should:
- Warn users before executing risky commands
- Implement command blacklists (e.g., `rm -rf /`)
- Log all operations for audit

## Plugin Distribution

### Phase 1: Built-In Plugins (Current)

All plugins are compiled into the app:

```kotlin
// In PluginLoader
fun loadBuiltInPlugins() {
    val plugins = listOf(
        TerminalPlugin(),
        AIAssistantPlugin(),
        APITesterPlugin(),
        // ... more
    )
    plugins.forEach { loadPlugin(it) }
}
```

### Phase 2: External Plugins (Future)

Load plugins from `/plugins/` directory or APK splits:

```kotlin
fun loadExternalPlugin(pluginPath: String) {
    // Load plugin class from DEX file
    val dexClassLoader = DexClassLoader(
        pluginPath,
        context.cacheDir.absolutePath,
        null,
        context.classLoader
    )
    
    val pluginClass = dexClassLoader.loadClass("com.example.MyPlugin")
    val plugin = pluginClass.newInstance() as Plugin
    
    loadPlugin(plugin)
}
```

**Security Considerations for External Plugins**:
- Verify plugin signature
- Sandbox plugin execution
- Require explicit user approval for each permission
- Provide plugin marketplace with reviews/ratings

## Example Plugins to Build

### Core Plugins (Phase 1)

1. **Terminal** - Shell emulator with PTY
2. **AI Assistant** - Gemini integration (BYO key)
3. **App Launcher Enhancements** - Favorites, recent apps, categories

### Developer Plugins (Phase 2)

4. **API Tester** - REST/GraphQL client
5. **Remote Desktop** - RDP/VNC/SSH connections
6. **Docker Manager** - Container management
7. **Git Status** - Repository status viewer
8. **Clipboard History** - Developer-focused clipboard manager
9. **JSON/XML Formatter** - Format and validate data
10. **Port Scanner** - Network diagnostics
11. **Base64/Hash Tools** - Encoding/hashing utilities
12. **Environment Variables** - Manage env vars

### Productivity Plugins (Phase 3)

13. **Notes** - Quick note-taking
14. **Tasks** - Simple task manager
15. **Pomodoro Timer** - Focus timer
16. **Calculator** - Developer calculator (hex, binary, etc.)

## Plugin Best Practices

### DO

✅ Keep plugins focused on a single responsibility  
✅ Use EventBus for inter-plugin communication  
✅ Declare all required permissions upfront  
✅ Handle errors gracefully (don't crash the launcher)  
✅ Respect Android lifecycle (save state, release resources)  
✅ Use coroutines for async operations  
✅ Provide clear command names and descriptions  

### DON'T

❌ Don't access other plugins' storage directly  
❌ Don't run long-blocking operations on main thread  
❌ Don't assume other plugins are installed  
❌ Don't leak memory (clean up in onUnload)  
❌ Don't bypass permission system  
❌ Don't hardcode API keys (use PluginStorage)  

## Testing Plugins

### Unit Tests

```kotlin
@Test
fun testTerminalPlugin_commandExecution() {
    val plugin = TerminalPlugin()
    val mockContext = mockPluginContext()
    
    plugin.onLoad(mockContext)
    
    val commands = plugin.commands()
    val runCommand = commands.find { it.name == "run" }
    
    assertNotNull(runCommand)
    
    val result = runCommand!!.execute(listOf("echo", "hello"))
    assertTrue(result is CommandResult.Success)
    assertEquals("hello\n", (result as CommandResult.Success).message)
}
```

### Integration Tests

```kotlin
@Test
fun testPluginCommunication_viaEventBus() {
    val eventBus = EventBus()
    val context1 = PluginContext(appContext, eventBus, ...)
    val context2 = PluginContext(appContext, eventBus, ...)
    
    val plugin1 = UsageStatsPlugin()
    val plugin2 = AIAssistantPlugin()
    
    plugin1.onLoad(context1)
    plugin2.onLoad(context2)
    
    // Trigger event from plugin1
    eventBus.publish("usage.updated", UsageStatsEvent(...))
    
    // Verify plugin2 received event
    // (requires plugin2 to expose internal state for testing)
}
```

## Summary

The plugin system transforms the launcher from a monolithic app into a **platform**. 

**Core Principles**:
1. **Minimal Core** - Launcher does almost nothing
2. **Plugin-First** - Everything is a plugin
3. **Command-Driven** - Global search is the primary interface
4. **Event-Based Communication** - Plugins are decoupled
5. **Permission-Based Security** - Users control what plugins can do

This architecture allows:
- **Fast core** - No bloat, no complexity
- **Extensibility** - Add features without touching core
- **Maintainability** - Plugins are isolated, easy to update
- **User control** - Enable only what you need
- **Future-proof** - External plugins can be added later

**Next Steps**:
1. Build the core (launcher + plugin system)
2. Implement first plugin (Terminal or AI Assistant)
3. If that feels clean → continue
4. If it feels painful → fix architecture before adding more
