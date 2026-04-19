package com.devlauncher.plugin

/**
 * Base interface for all plugins
 * 
 * Plugins extend the launcher with additional functionality.
 * The core launcher is intentionally minimal - everything else is a plugin.
 */
interface Plugin {
    /** Unique plugin identifier (e.g., "terminal", "ai-assistant") */
    val id: String
    
    /** Display name shown to users */
    val name: String
    
    /** Semantic version (e.g., "1.0.0") */
    val version: String
    
    /** Short description of what the plugin does */
    val description: String
    
    /** Permissions required by this plugin */
    val permissions: PluginPermissions
    
    /**
     * Called when the plugin is loaded
     * Initialize resources, subscribe to events, register commands
     */
    fun onLoad(context: PluginContext)
    
    /**
     * Called when the plugin is unloaded
     * Clean up resources, unsubscribe from events
     */
    fun onUnload()
}

/**
 * Permissions that a plugin can request
 * Users must approve these before the plugin loads
 */
data class PluginPermissions(
    val internet: Boolean = false,
    val storage: Boolean = false,
    val shellAccess: Boolean = false,
    val systemInfo: Boolean = false,
    val location: Boolean = false,
    val camera: Boolean = false,
    val microphone: Boolean = false
) {
    fun hasAnyPermission(): Boolean {
        return internet || storage || shellAccess || systemInfo || 
               location || camera || microphone
    }
    
    fun toList(): List<String> {
        return buildList {
            if (internet) add("Internet Access")
            if (storage) add("Storage Access")
            if (shellAccess) add("Shell Command Execution")
            if (systemInfo) add("System Information")
            if (location) add("Location")
            if (camera) add("Camera")
            if (microphone) add("Microphone")
        }
    }
}
