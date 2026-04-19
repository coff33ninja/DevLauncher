package com.devlauncher.data

import android.graphics.drawable.Drawable

/**
 * Represents an installed application
 */
data class AppInfo(
    val packageName: String,
    val appName: String,
    val icon: Drawable,
    val category: AppCategory = AppCategory.OTHER
)

enum class AppCategory {
    DEVELOPMENT,
    COMMUNICATION,
    PRODUCTIVITY,
    UTILITIES,
    ENTERTAINMENT,
    OTHER
}
