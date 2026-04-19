package com.devlauncher.plugin

import android.util.Log

/**
 * Registry for plugin commands
 * 
 * Commands are registered by plugins and executed via global search.
 * This is the primary way plugins expose functionality to users.
 */
class CommandRegistry {
    private val commands = mutableMapOf<String, RegisteredCommand>()
    private val commandsByPlugin = mutableMapOf<String, MutableList<String>>()
    
    /**
     * Register a command from a plugin
     */
    fun register(pluginId: String, command: Command) {
        val registeredCommand = RegisteredCommand(pluginId, command)
        
        // Register primary name
        commands[command.name] = registeredCommand
        
        // Register aliases
        command.aliases.forEach { alias ->
            commands[alias] = registeredCommand
        }
        
        // Track commands by plugin for cleanup
        commandsByPlugin.getOrPut(pluginId) { mutableListOf() }.add(command.name)
        
        Log.d(TAG, "Registered command: ${command.name} from plugin: $pluginId")
    }
    
    /**
     * Find a command by exact name or alias
     */
    fun findCommand(name: String): RegisteredCommand? {
        return commands[name.lowercase()]
    }
    
    /**
     * Fuzzy search for commands
     * Returns commands that contain the query string
     */
    fun fuzzySearch(query: String): List<RegisteredCommand> {
        if (query.isBlank()) return emptyList()
        
        val lowerQuery = query.lowercase()
        return commands.values
            .distinctBy { it.command.name }
            .filter { registered ->
                registered.command.name.lowercase().contains(lowerQuery) ||
                registered.command.description.lowercase().contains(lowerQuery) ||
                registered.command.aliases.any { it.lowercase().contains(lowerQuery) }
            }
            .sortedBy { registered ->
                // Prioritize exact matches
                when {
                    registered.command.name.lowercase() == lowerQuery -> 0
                    registered.command.name.lowercase().startsWith(lowerQuery) -> 1
                    else -> 2
                }
            }
    }
    
    /**
     * Get all registered commands
     */
    fun getAllCommands(): List<RegisteredCommand> {
        return commands.values.distinctBy { it.command.name }
    }
    
    /**
     * Unregister all commands from a plugin
     */
    fun unregisterAll(pluginId: String) {
        val pluginCommands = commandsByPlugin[pluginId] ?: return
        
        pluginCommands.forEach { commandName ->
            val registered = commands[commandName]
            if (registered != null) {
                // Remove primary name
                commands.remove(commandName)
                
                // Remove aliases
                registered.command.aliases.forEach { alias ->
                    commands.remove(alias)
                }
            }
        }
        
        commandsByPlugin.remove(pluginId)
        Log.d(TAG, "Unregistered all commands from plugin: $pluginId")
    }
    
    /**
     * Parse command string into name and arguments
     * Example: "ssh home-server" -> ("ssh", ["home-server"])
     */
    fun parseCommand(input: String): Pair<String, List<String>> {
        val parts = input.trim().split(Regex("\\s+"))
        return if (parts.isEmpty()) {
            "" to emptyList()
        } else {
            parts[0] to parts.drop(1)
        }
    }
    
    companion object {
        private const val TAG = "CommandRegistry"
    }
}

/**
 * A command registered by a plugin
 */
data class RegisteredCommand(
    val pluginId: String,
    val command: Command
)
