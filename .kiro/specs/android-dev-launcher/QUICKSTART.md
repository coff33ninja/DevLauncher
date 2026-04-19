# Quick Start Guide: Building the Plugin-Based Launcher

## TL;DR

Build this in order:
1. Core (app grid + search)
2. Plugin system (loader + registry)
3. First plugin (Terminal or AI)
4. Validate architecture
5. Add more plugins

**Critical Rule**: If it can be a plugin, it MUST be a plugin.

## Project Setup

### 1. Create Android Project

```bash
# Using Android Studio or command line
android create project \
  --name "DevLauncher" \
  --package "com.devlauncher" \
  --min-sdk 26 \
  --target-sdk 34 \
  --language kotlin \
  --ui compose
```

### 2. Dependencies (build.gradle.kts)

```kotlin
dependencies {
    // Jetpack Compose
    implementation("androidx.compose.ui:ui:1.5.4")
    implementation("androidx.compose.material3:material3:1.1.2")
    implementation("androidx.activity:activity-compose:1.8.1")
    
    // Lifecycle
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")
    
    // DataStore (settings)
    implementation("androidx.datastore:datastore-preferences:1.0.0")
    
    // WorkManager (background tasks)
    implementation("androidx.work:work-runtime-ktx:2.9.0")
    
    // OkHttp (for AI plugin)
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    
    // Testing
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.5.4")
}
```

### 3. Manifest Permissions

```xml
<manifest>
    <!-- Core permissions -->
    <uses-permission android:name="android.permission.QUERY_ALL_PACKAGES" />
    
    <!-- Plugin permissions (requested at runtime) -->
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    
    <application>
        <activity
            android:name=".MainActivity"
            android:exported="true"
            android:theme="@style/Theme.DevLauncher">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.HOME" />
                <category android:name="android.intent.category.DEFAULT" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>
</manifest>
```

## Implementation Order

### Phase 1: Core (Days 1-3)

#### 1.1 App Repository

```kotlin
// data/AppRepository.kt
class AppRepository(private val context: Context) {
    private val packageManager = context.packageManager
    
    fun getInstalledApps(): List<AppInfo> {
        val intent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        
        return packageManager.queryIntentActivities(intent, 0)
            .map { resolveInfo ->
                AppInfo(
                    packageName = resolveInfo.activityInfo.packageName,
                    appName = resolveInfo.loadLabel(packageManager).toString(),
                    icon = resolveInfo.loadIcon(packageManager)
                )
            }
            .sortedBy { it.appName }
    }
    
    fun launchApp(packageName: String) {
        val intent = packageManager.getLaunchIntentForPackage(packageName)
        context.startActivity(intent)
    }
    
    fun search(query: String): List<AppInfo> {
        return getInstalledApps().filter { app ->
            app.appName.contains(query, ignoreCase = true)
        }
    }
}

data class AppInfo(
    val packageName: String,
    val appName: String,
    val icon: Drawable
)
```

#### 1.2 Home Screen

```kotlin
// ui/HomeScreen.kt
@Composable
fun HomeScreen(
    apps: List<AppInfo>,
    onAppClick: (AppInfo) -> Unit,
    onSearchClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Search bar
        SearchBar(
            onClick = onSearchClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
        
        // App grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(apps) { app ->
                AppIcon(
                    app = app,
                    onClick = { onAppClick(app) }
                )
            }
        }
    }
}

@Composable
fun AppIcon(app: AppInfo, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Image(
            bitmap = app.icon.toBitmap().asImageBitmap(),
            contentDescription = app.appName,
            modifier = Modifier.size(48.dp)
        )
        Text(
            text = app.appName,
            fontSize = 12.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
```

#### 1.3 Global Search

```kotlin
// ui/GlobalSearch.kt
@Composable
fun GlobalSearchScreen(
    query: String,
    onQueryChange: (String) -> Unit,
    results: List<SearchResult>,
    onResultClick: (SearchResult) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Search input
        TextField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = { Text("Search apps or type command...") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
        
        // Results
        LazyColumn {
            items(results) { result ->
                SearchResultItem(
                    result = result,
                    onClick = { onResultClick(result) }
                )
            }
        }
    }
}

sealed class SearchResult {
    data class App(val app: AppInfo) : SearchResult()
    data class Command(val command: String, val description: String) : SearchResult()
}
```

### Phase 2: Plugin System (Days 4-6)

#### 2.1 Plugin Interface

```kotlin
// plugin/Plugin.kt
interface Plugin {
    val id: String
    val name: String
    val version: String
    val description: String
    val permissions: PluginPermissions
    
    fun onLoad(context: PluginContext)
    fun onUnload()
}

data class PluginPermissions(
    val internet: Boolean = false,
    val storage: Boolean = false,
    val shellAccess: Boolean = false,
    val systemInfo: Boolean = false
)
```

#### 2.2 Plugin Types

```kotlin
// plugin/PluginTypes.kt
interface CommandPlugin : Plugin {
    fun commands(): List<Command>
}

data class Command(
    val name: String,
    val description: String,
    val aliases: List<String> = emptyList(),
    val execute: suspend (args: List<String>) -> CommandResult
)

sealed class CommandResult {
    data class Success(val message: String) : CommandResult()
    data class Error(val error: String) : CommandResult()
}
```

#### 2.3 Plugin Context

```kotlin
// plugin/PluginContext.kt
class PluginContext(
    val appContext: Context,
    val eventBus: EventBus,
    val storage: PluginStorage,
    val commandRegistry: CommandRegistry
)

class PluginStorage(private val pluginId: String, private val dataStore: DataStore<Preferences>) {
    suspend fun getString(key: String): String? {
        return dataStore.data.map { it[stringPreferencesKey("$pluginId.$key")] }.first()
    }
    
    suspend fun putString(key: String, value: String) {
        dataStore.edit { it[stringPreferencesKey("$pluginId.$key")] = value }
    }
}
```

#### 2.4 Event Bus

```kotlin
// plugin/EventBus.kt
class EventBus {
    private val listeners = mutableMapOf<String, MutableList<(Any) -> Unit>>()
    
    fun subscribe(event: String, handler: (Any) -> Unit) {
        listeners.getOrPut(event) { mutableListOf() }.add(handler)
    }
    
    fun publish(event: String, data: Any) {
        listeners[event]?.forEach { handler ->
            try {
                handler(data)
            } catch (e: Exception) {
                Log.e("EventBus", "Error in event handler", e)
            }
        }
    }
    
    fun unsubscribe(event: String, handler: (Any) -> Unit) {
        listeners[event]?.remove(handler)
    }
}
```

#### 2.5 Plugin Loader

```kotlin
// plugin/PluginLoader.kt
class PluginLoader(private val context: Context) {
    private val plugins = mutableMapOf<String, Plugin>()
    private val eventBus = EventBus()
    private val commandRegistry = CommandRegistry()
    
    fun loadBuiltInPlugins() {
        val builtInPlugins = listOf(
            TerminalPlugin(),
            AIAssistantPlugin()
        )
        
        builtInPlugins.forEach { plugin ->
            loadPlugin(plugin)
        }
    }
    
    fun loadPlugin(plugin: Plugin) {
        val pluginContext = PluginContext(
            appContext = context,
            eventBus = eventBus,
            storage = PluginStorage(plugin.id, dataStore),
            commandRegistry = commandRegistry
        )
        
        plugin.onLoad(pluginContext)
        plugins[plugin.id] = plugin
        
        if (plugin is CommandPlugin) {
            plugin.commands().forEach { command ->
                commandRegistry.register(plugin.id, command)
            }
        }
    }
    
    fun getCommandRegistry() = commandRegistry
    fun getEventBus() = eventBus
}
```

### Phase 3: First Plugin - Terminal (Days 7-9)

```kotlin
// plugins/TerminalPlugin.kt
class TerminalPlugin : CommandPlugin {
    override val id = "terminal"
    override val name = "Terminal"
    override val version = "1.0.0"
    override val description = "Shell command execution"
    override val permissions = PluginPermissions(shellAccess = true)
    
    private lateinit var context: PluginContext
    
    override fun onLoad(context: PluginContext) {
        this.context = context
    }
    
    override fun onUnload() {
        // Cleanup
    }
    
    override fun commands(): List<Command> = listOf(
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
    
    private fun executeCommand(command: String): String {
        return try {
            val process = Runtime.getRuntime().exec(arrayOf("/system/bin/sh", "-c", command))
            val output = process.inputStream.bufferedReader().readText()
            val error = process.errorStream.bufferedReader().readText()
            process.waitFor()
            
            if (error.isNotEmpty()) "$output\n$error" else output
        } catch (e: Exception) {
            "Error: ${e.message}"
        }
    }
}
```

### Phase 4: Second Plugin - AI Assistant (Days 10-12)

```kotlin
// plugins/AIAssistantPlugin.kt
class AIAssistantPlugin : CommandPlugin {
    override val id = "ai-assistant"
    override val name = "AI Assistant"
    override val version = "1.0.0"
    override val description = "Gemini AI integration"
    override val permissions = PluginPermissions(internet = true)
    
    private lateinit var context: PluginContext
    private var geminiClient: GeminiClient? = null
    
    override fun onLoad(context: PluginContext) {
        this.context = context
        
        // Load API key from storage
        CoroutineScope(Dispatchers.IO).launch {
            val apiKey = context.storage.getString("gemini_api_key")
            if (apiKey != null) {
                geminiClient = GeminiClient(apiKey)
            }
        }
    }
    
    override fun onUnload() {
        geminiClient = null
    }
    
    override fun commands(): List<Command> = listOf(
        Command(
            name = "ask",
            description = "Ask Gemini AI",
            aliases = listOf("ai", "gemini"),
            execute = { args ->
                val query = args.joinToString(" ")
                if (geminiClient == null) {
                    CommandResult.Error("API key not configured")
                } else {
                    try {
                        val response = geminiClient!!.ask(query)
                        CommandResult.Success(response)
                    } catch (e: Exception) {
                        CommandResult.Error("API error: ${e.message}")
                    }
                }
            }
        )
    )
}
```

## Testing

### Unit Tests

```kotlin
// test/PluginLoaderTest.kt
class PluginLoaderTest {
    @Test
    fun testPluginLoading() {
        val context = mockContext()
        val loader = PluginLoader(context)
        
        val plugin = TestPlugin()
        loader.loadPlugin(plugin)
        
        assertTrue(plugin.isLoaded)
    }
    
    @Test
    fun testCommandRegistration() {
        val loader = PluginLoader(mockContext())
        val plugin = TestCommandPlugin()
        
        loader.loadPlugin(plugin)
        
        val registry = loader.getCommandRegistry()
        assertNotNull(registry.findCommand("test"))
    }
}
```

## Running the Launcher

1. Build and install APK
2. Press home button
3. Select "DevLauncher" as default launcher
4. Test:
   - App grid displays
   - Search works
   - Commands execute (if plugins loaded)

## Validation Checklist

Before adding more plugins, validate:

- [ ] Core is minimal (< 5,000 LOC)
- [ ] Launch time < 200ms
- [ ] Memory usage < 50MB idle
- [ ] Plugin loading works
- [ ] Commands execute correctly
- [ ] Event bus works
- [ ] Plugin storage is isolated
- [ ] No crashes

If all checks pass → architecture is solid, continue.  
If any fail → fix before adding more features.

## Next Steps

1. Build core (app grid + search)
2. Build plugin system
3. Build first plugin (Terminal)
4. Validate architecture
5. Build second plugin (AI Assistant)
6. Polish and test
7. Add more plugins as needed

## Common Pitfalls

❌ **Adding features to core instead of plugins**  
✅ Make it a plugin

❌ **Tight coupling between plugins**  
✅ Use EventBus

❌ **Ignoring Android lifecycle**  
✅ Save state, release resources

❌ **Long-blocking operations on main thread**  
✅ Use coroutines

❌ **Assuming other plugins are installed**  
✅ Check before using

## Resources

- [Jetpack Compose Docs](https://developer.android.com/jetpack/compose)
- [Android Launcher Development](https://developer.android.com/guide/components/activities/recents)
- [WorkManager Guide](https://developer.android.com/topic/libraries/architecture/workmanager)
- [DataStore Guide](https://developer.android.com/topic/libraries/architecture/datastore)

## Questions?

Read:
- `ARCHITECTURE_SUMMARY.md` - High-level overview
- `PLUGIN_ARCHITECTURE.md` - Detailed plugin design
- `REQUIREMENTS_V2.md` - Clean requirements

## Remember

> **"If it can be a plugin… it MUST be a plugin."**

Now go build. 🔥
