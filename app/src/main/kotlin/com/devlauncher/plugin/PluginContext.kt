package com.devlauncher.plugin

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

/**
 * Context provided to plugins
 * Gives controlled access to core functionality
 */
class PluginContext(
    val appContext: Context,
    val eventBus: EventBus,
    private val dataStore: DataStore<Preferences>,
    val commandRegistry: CommandRegistry,
    private val pluginId: String
) {
    val storage: PluginStorage = PluginStorage(pluginId, dataStore)
}

/**
 * Isolated storage for each plugin
 * Plugins cannot access other plugins' storage
 */
class PluginStorage(
    private val pluginId: String,
    private val dataStore: DataStore<Preferences>
) {
    private fun key(key: String) = "$pluginId.$key"
    
    suspend fun getString(key: String): String? {
        return dataStore.data.map { prefs ->
            prefs[stringPreferencesKey(key(key))]
        }.first()
    }
    
    suspend fun putString(key: String, value: String) {
        dataStore.edit { prefs ->
            prefs[stringPreferencesKey(key(key))] = value
        }
    }
    
    suspend fun getInt(key: String, default: Int = 0): Int {
        return dataStore.data.map { prefs ->
            prefs[intPreferencesKey(key(key))] ?: default
        }.first()
    }
    
    suspend fun putInt(key: String, value: Int) {
        dataStore.edit { prefs ->
            prefs[intPreferencesKey(key(key))] = value
        }
    }
    
    suspend fun getBoolean(key: String, default: Boolean = false): Boolean {
        return dataStore.data.map { prefs ->
            prefs[booleanPreferencesKey(key(key))] ?: default
        }.first()
    }
    
    suspend fun putBoolean(key: String, value: Boolean) {
        dataStore.edit { prefs ->
            prefs[booleanPreferencesKey(key(key))] = value
        }
    }
    
    suspend fun getLong(key: String, default: Long = 0L): Long {
        return dataStore.data.map { prefs ->
            prefs[longPreferencesKey(key(key))] ?: default
        }.first()
    }
    
    suspend fun putLong(key: String, value: Long) {
        dataStore.edit { prefs ->
            prefs[longPreferencesKey(key(key))] = value
        }
    }
    
    suspend fun remove(key: String) {
        dataStore.edit { prefs ->
            prefs.remove(stringPreferencesKey(key(key)))
        }
    }
    
    suspend fun clear() {
        dataStore.edit { prefs ->
            val keysToRemove = prefs.asMap().keys.filter { 
                it.name.startsWith("$pluginId.")
            }
            keysToRemove.forEach { prefs.remove(it) }
        }
    }
}
