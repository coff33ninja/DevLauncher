package com.devlauncher.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.devlauncher.LauncherApplication
import com.devlauncher.data.AppInfo
import com.devlauncher.data.AppRepository
import com.devlauncher.plugin.AppLaunchEvent
import com.devlauncher.plugin.CommandResult
import com.devlauncher.plugin.RegisteredCommand
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the launcher
 * Manages app list, search, and command execution
 */
class LauncherViewModel(application: Application) : AndroidViewModel(application) {
    
    private val appRepository = AppRepository(application)
    private val pluginLoader = (application as LauncherApplication).pluginLoader
    private val commandRegistry = pluginLoader.getCommandRegistry()
    private val eventBus = pluginLoader.getEventBus()
    
    // UI State
    private val _apps = MutableStateFlow<List<AppInfo>>(emptyList())
    val apps: StateFlow<List<AppInfo>> = _apps.asStateFlow()
    
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    private val _searchResults = MutableStateFlow<List<SearchResult>>(emptyList())
    val searchResults: StateFlow<List<SearchResult>> = _searchResults.asStateFlow()
    
    private val _commandResult = MutableStateFlow<CommandResult?>(null)
    val commandResult: StateFlow<CommandResult?> = _commandResult.asStateFlow()
    
    init {
        loadApps()
    }
    
    /**
     * Load installed apps
     */
    private fun loadApps() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val installedApps = appRepository.getInstalledApps()
                _apps.value = installedApps
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Launch an app
     */
    fun launchApp(app: AppInfo) {
        appRepository.launchApp(app.packageName)
        
        // Publish event for plugins
        eventBus.publish("app.launched", AppLaunchEvent(app.packageName, System.currentTimeMillis()))
    }
    
    /**
     * Update search query and perform search
     */
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        performSearch(query)
    }
    
    /**
     * Perform search across apps and commands
     */
    private fun performSearch(query: String) {
        if (query.isBlank()) {
            _searchResults.value = emptyList()
            return
        }
        
        viewModelScope.launch {
            val results = mutableListOf<SearchResult>()
            
            // 1. Check for exact command match
            val (commandName, args) = commandRegistry.parseCommand(query)
            commandRegistry.findCommand(commandName)?.let { registered ->
                results.add(SearchResult.Command(registered, priority = 100))
            }
            
            // 2. Search apps (fuzzy match)
            val matchingApps = appRepository.searchApps(query)
            results.addAll(matchingApps.map { SearchResult.App(it, priority = 50) })
            
            // 3. Fuzzy search commands
            val matchingCommands = commandRegistry.fuzzySearch(query)
            results.addAll(matchingCommands.map { SearchResult.Command(it, priority = 30) })
            
            // Sort by priority
            _searchResults.value = results.sortedByDescending { it.priority }
        }
    }
    
    /**
     * Execute a command
     */
    fun executeCommand(registered: RegisteredCommand, args: List<String>) {
        viewModelScope.launch {
            try {
                val result = registered.command.execute(args)
                _commandResult.value = result
            } catch (e: Exception) {
                _commandResult.value = CommandResult.Error("Error: ${e.message}")
            }
        }
    }
    
    /**
     * Clear command result
     */
    fun clearCommandResult() {
        _commandResult.value = null
    }
    
    /**
     * Refresh app list
     */
    fun refreshApps() {
        appRepository.invalidateCache()
        loadApps()
    }
}

/**
 * Search result types
 */
sealed class SearchResult(open val priority: Int) {
    data class App(val app: AppInfo, override val priority: Int) : SearchResult(priority)
    data class Command(val registered: RegisteredCommand, override val priority: Int) : SearchResult(priority)
}
