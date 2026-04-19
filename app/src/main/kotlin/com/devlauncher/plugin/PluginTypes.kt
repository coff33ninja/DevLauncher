package com.devlauncher.plugin

import androidx.compose.runtime.Composable

/**
 * Plugin that provides UI components
 */
interface UIPlugin : Plugin {
    /**
     * Render the plugin's UI
     * Called when the plugin's screen is displayed
     */
    @Composable
    fun render()
    
    /**
     * Optional: Request a dedicated screen in the launcher
     * Return null if the plugin doesn't need a dedicated screen
     */
    fun getScreenConfig(): ScreenConfig? = null
}

/**
 * Configuration for a plugin's dedicated screen
 */
data class ScreenConfig(
    val title: String,
    val category: ScreenCategory
)

enum class ScreenCategory {
    DEVELOPMENT,
    COMMUNICATION,
    PRODUCTIVITY,
    MONITORING,
    CUSTOM
}

/**
 * Plugin that registers commands for global search
 * This is the primary way plugins expose functionality
 */
interface CommandPlugin : Plugin {
    /**
     * Return list of commands this plugin provides
     * Commands are registered in the global search
     */
    fun commands(): List<Command>
}

/**
 * A command that can be executed from global search
 */
data class Command(
    val name: String,
    val description: String,
    val aliases: List<String> = emptyList(),
    val syntax: String? = null,  // e.g., "ssh <host>", "ask <question>"
    val execute: suspend (args: List<String>) -> CommandResult
)

/**
 * Result of command execution
 */
sealed class CommandResult {
    data class Success(val message: String) : CommandResult()
    data class Error(val error: String) : CommandResult()
    data class OpenUI(val content: @Composable () -> Unit) : CommandResult()
}

/**
 * Plugin that runs background tasks
 * Use sparingly - Android kills background processes aggressively
 */
interface BackgroundPlugin : Plugin {
    /**
     * Called when background work should start
     */
    fun onBackgroundStart()
    
    /**
     * Called when background work should stop
     */
    fun onBackgroundStop()
}
