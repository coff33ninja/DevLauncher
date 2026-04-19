package com.devlauncher.data

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repository for accessing installed applications
 * 
 * This is part of the CORE - keeps it minimal:
 * - Get installed apps
 * - Launch apps
 * - Search apps
 * 
 * Everything else (favorites, categories, usage stats) is handled by plugins
 */
class AppRepository(private val context: Context) {
    
    private val packageManager: PackageManager = context.packageManager
    private var cachedApps: List<AppInfo>? = null
    
    /**
     * Get all installed launcher apps
     * Cached in memory for performance
     */
    suspend fun getInstalledApps(): List<AppInfo> = withContext(Dispatchers.IO) {
        cachedApps?.let { return@withContext it }
        
        val intent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        
        val apps = packageManager.queryIntentActivities(intent, 0)
            .map { resolveInfo ->
                AppInfo(
                    packageName = resolveInfo.activityInfo.packageName,
                    appName = resolveInfo.loadLabel(packageManager).toString(),
                    icon = resolveInfo.loadIcon(packageManager),
                    category = categorizeApp(resolveInfo.activityInfo.packageName)
                )
            }
            .sortedBy { it.appName.lowercase() }
        
        cachedApps = apps
        apps
    }
    
    /**
     * Launch an app by package name
     */
    fun launchApp(packageName: String) {
        val intent = packageManager.getLaunchIntentForPackage(packageName)
        intent?.let {
            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(it)
        }
    }
    
    /**
     * Search apps by name (fuzzy match)
     */
    suspend fun searchApps(query: String): List<AppInfo> = withContext(Dispatchers.Default) {
        if (query.isBlank()) return@withContext emptyList()
        
        val apps = getInstalledApps()
        val lowerQuery = query.lowercase()
        
        apps.filter { app ->
            app.appName.lowercase().contains(lowerQuery) ||
            app.packageName.lowercase().contains(lowerQuery)
        }
    }
    
    /**
     * Clear cached apps (call when apps are installed/uninstalled)
     */
    fun invalidateCache() {
        cachedApps = null
    }
    
    /**
     * Basic app categorization based on package name patterns
     * More sophisticated categorization can be done by plugins
     */
    private fun categorizeApp(packageName: String): AppCategory {
        return when {
            packageName.contains("termux") ||
            packageName.contains("git") ||
            packageName.contains("ide") ||
            packageName.contains("editor") ||
            packageName.contains("studio") ||
            packageName.contains("code") -> AppCategory.DEVELOPMENT
            
            packageName.contains("slack") ||
            packageName.contains("discord") ||
            packageName.contains("teams") ||
            packageName.contains("whatsapp") ||
            packageName.contains("telegram") ||
            packageName.contains("messenger") -> AppCategory.COMMUNICATION
            
            packageName.contains("notion") ||
            packageName.contains("evernote") ||
            packageName.contains("todoist") ||
            packageName.contains("trello") ||
            packageName.contains("calendar") -> AppCategory.PRODUCTIVITY
            
            else -> AppCategory.OTHER
        }
    }
}
