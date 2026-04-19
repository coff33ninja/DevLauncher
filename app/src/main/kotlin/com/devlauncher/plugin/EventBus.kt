package com.devlauncher.plugin

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Event bus for decoupled plugin communication
 * 
 * Plugins publish events and subscribe to events without knowing about each other.
 * This keeps plugins isolated and maintainable.
 * 
 * Example events:
 * - "app.launched" -> AppLaunchEvent(packageName, timestamp)
 * - "search.query" -> SearchQueryEvent(query)
 * - "terminal.command" -> TerminalCommandEvent(command, output)
 * - "usage.updated" -> UsageStatsEvent(stats)
 */
class EventBus {
    private val listeners = mutableMapOf<String, MutableList<suspend (Any) -> Unit>>()
    private val scope = CoroutineScope(Dispatchers.Default)
    
    /**
     * Subscribe to an event
     * Handler will be called when the event is published
     */
    fun subscribe(event: String, handler: suspend (Any) -> Unit) {
        synchronized(listeners) {
            listeners.getOrPut(event) { mutableListOf() }.add(handler)
        }
        Log.d(TAG, "Subscribed to event: $event")
    }
    
    /**
     * Publish an event
     * All subscribed handlers will be called asynchronously
     */
    fun publish(event: String, data: Any) {
        val eventListeners = synchronized(listeners) {
            listeners[event]?.toList() ?: emptyList()
        }
        
        if (eventListeners.isEmpty()) {
            Log.d(TAG, "No listeners for event: $event")
            return
        }
        
        Log.d(TAG, "Publishing event: $event to ${eventListeners.size} listeners")
        
        eventListeners.forEach { handler ->
            scope.launch {
                try {
                    handler(data)
                } catch (e: Exception) {
                    Log.e(TAG, "Error in event handler for $event", e)
                }
            }
        }
    }
    
    /**
     * Unsubscribe from an event
     */
    fun unsubscribe(event: String, handler: suspend (Any) -> Unit) {
        synchronized(listeners) {
            listeners[event]?.remove(handler)
        }
        Log.d(TAG, "Unsubscribed from event: $event")
    }
    
    /**
     * Unsubscribe from all events
     */
    fun unsubscribeAll() {
        synchronized(listeners) {
            listeners.clear()
        }
        Log.d(TAG, "Unsubscribed from all events")
    }
    
    companion object {
        private const val TAG = "EventBus"
    }
}

// Common event types
data class AppLaunchEvent(val packageName: String, val timestamp: Long)
data class SearchQueryEvent(val query: String)
data class PluginLoadedEvent(val pluginId: String)
data class PluginUnloadedEvent(val pluginId: String)
