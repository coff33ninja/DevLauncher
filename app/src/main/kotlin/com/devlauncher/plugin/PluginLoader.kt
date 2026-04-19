package com.devlauncher.plugin

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

/**
 * Plugin loader and manager
 * 
 * Responsible for:
 * - Loading plugins
 * - Managing plugin lifecycle
 * - Enforcing permissions
 * - Coordinating plugin communication
 */
class PluginLoader(private val context: Context) {
    
    private val plugins = mutableMapOf<String, Plugin>()
    private val eventBus = EventBus()
    private val commandRegistry = CommandRegistry()
    
    // DataStore for plugin storage
    private val Context.pluginDataStore: DataStore<Preferences> by preferencesDataStore(
        name = "plugin_storage"
    )
    
    /**
     * Load all built-in plugins
     * Called on launcher startup
     */
    fun loadBuiltInPlugins() {
        Log.d(TAG, "Loading built-in plugins...")
        
        // TODO: Add built-in plugins here as they're implemented
        // val builtInPlugins = listOf(
        //     TerminalPlugin(),
        //     AIAssistantPlugin(),
        //     AppEnhancementsPlugin()
        // )
        // 
        // builtInPlugins.forEach { plugin ->
        //     try {
        //         loadPlugin(plugin)
        //     } catch (e: Exception) {
        //         Log.e(TAG, "Failed to load plugin: ${plugin.id}", e)
        //     }
        // }
        
        Log.d(TAG, "Built-in plugins loaded: ${plugins.size}")
    }
    
    /**
     * Load a plugin
     * Validates permissions and initializes the plugin
     */
    fun loadPlugin(plugin: Plugin) {
        if (plugins.containsKey(plugin.id)) {
            Log.w(TAG, "Plugin already loaded: ${plugin.id}")
            return
        }
        
        Log.d(TAG, "Loading plugin: ${plugin.id} v${plugin.version}")
        
        // TODO: Check if user has approved permissions
        // For now, we'll assume all permissions are approved
        if (plugin.permissions.hasAnyPermission()) {
            Log.d(TAG, "Plugin ${plugin.id} requires permissions: ${plugin.permissions.toList()}")
        }
        
        // Create plugin context
        val pluginContext = PluginContext(
            appContext = context,
            eventBus = eventBus,
            dataStore = context.pluginDataStore,
            commandRegistry = commandRegistry,
            pluginId = plugin.id
        )
        
        try {
            // Initialize plugin
            plugin.onLoad(pluginContext)
            
            // Register commands if CommandPlugin
            if (plugin is CommandPlugin) {
                plugin.commands().forEach { command ->
                    commandRegistry.register(plugin.id, command)
                }
            }
            
            // Start background work if BackgroundPlugin
            if (plugin is BackgroundPlugin) {
                plugin.onBackgroundStart()
            }
            
            // Store plugin
            plugins[plugin.id] = plugin
            
            // Publish event
            eventBus.publish("plugin.loaded", PluginLoadedEvent(plugin.id))
            
            Log.d(TAG, "Plugin loaded successfully: ${plugin.id}")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load plugin: ${plugin.id}", e)
            throw e
        }
    }
    
    /**
     * Unload a plugin
     * Cleans up resources and unregisters commands
     */
    fun unloadPlugin(pluginId: String) {
        val plugin = plugins[pluginId] ?: run {
            Log.w(TAG, "Plugin not loaded: $pluginId")
            return
        }
        
        Log.d(TAG, "Unloading plugin: $pluginId")
        
        try {
            // Stop background work if BackgroundPlugin
            if (plugin is BackgroundPlugin) {
                plugin.onBackgroundStop()
            }
            
            // Unregister commands
            commandRegistry.unregisterAll(pluginId)
            
            // Clean up plugin
            plugin.onUnload()
            
            // Remove from registry
            plugins.remove(pluginId)
            
            // Publish event
            eventBus.publish("plugin.unloaded", PluginUnloadedEvent(pluginId))
            
            Log.d(TAG, "Plugin unloaded successfully: $pluginId")
        } catch (e: Exception) {
            Log.e(TAG, "Error unloading plugin: $pluginId", e)
        }
    }
    
    /**
     * Get a loaded plugin by ID
     */
    fun getPlugin(pluginId: String): Plugin? {
        return plugins[pluginId]
    }
    
    /**
     * Get all loaded plugins
     */
    fun getAllPlugins(): List<Plugin> {
        return plugins.values.toList()
    }
    
    /**
     * Check if a plugin is loaded
     */
    fun isPluginLoaded(pluginId: String): Boolean {
        return plugins.containsKey(pluginId)
    }
    
    /**
     * Get the command registry
     * Used by global search to find and execute commands
     */
    fun getCommandRegistry(): CommandRegistry {
        return commandRegistry
    }
    
    /**
     * Get the event bus
     * Used by core to publish events
     */
    fun getEventBus(): EventBus {
        return eventBus
    }
    
    /**
     * Unload all plugins
     * Called on launcher shutdown
     */
    fun unloadAll() {
        Log.d(TAG, "Unloading all plugins...")
        plugins.keys.toList().forEach { pluginId ->
            unloadPlugin(pluginId)
        }
    }
    
    companion object {
        private const val TAG = "PluginLoader"
    }
}
