# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.kts.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Keep plugin classes
-keep class * implements com.devlauncher.plugin.Plugin { *; }
-keep class com.devlauncher.plugin.** { *; }

# Keep data classes
-keep class com.devlauncher.data.** { *; }

# Compose
-dontwarn androidx.compose.**
-keep class androidx.compose.** { *; }
