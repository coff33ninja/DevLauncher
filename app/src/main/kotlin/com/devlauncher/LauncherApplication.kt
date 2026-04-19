package com.devlauncher

import android.app.Application
import com.devlauncher.plugin.PluginLoader

/**
 * Application class for the launcher
 * Initializes core components
 */
class LauncherApplication : Application() {
    
    lateinit var pluginLoader: PluginLoader
        private set
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize plugin loader
        pluginLoader = PluginLoader(this)
        
        // Load built-in plugins
        pluginLoader.loadBuiltInPlugins()
    }
    
    override fun onTerminate() {
        super.onTerminate()
        
        // Unload all plugins
        pluginLoader.unloadAll()
    }
}
