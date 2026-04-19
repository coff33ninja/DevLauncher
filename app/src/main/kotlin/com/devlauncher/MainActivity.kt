package com.devlauncher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import com.devlauncher.ui.*
import com.devlauncher.ui.theme.DevLauncherTheme

/**
 * Main activity for the launcher
 * Displays home screen and global search
 */
class MainActivity : ComponentActivity() {
    
    private val viewModel: LauncherViewModel by viewModels()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Enable edge-to-edge support in the window
        WindowCompat.setDecorFitsSystemWindows(window, false)
        
        setContent {
            DevLauncherTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Transparent // Surface must be transparent for wallpaper
                ) {
                    LauncherScreen(viewModel = viewModel)
                }
            }
        }
    }
}

/**
 * Main launcher screen
 * Manages navigation between home and search with proper state handling
 */
@Composable
fun LauncherScreen(viewModel: LauncherViewModel) {
    val apps by viewModel.apps.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val commandResult by viewModel.commandResult.collectAsState()
    
    var showSearch by remember { mutableStateOf(false) }

    // Logic to handle back press when search is open
    if (showSearch) {
        BackHandler {
            if (commandResult != null) {
                viewModel.clearCommandResult()
            } else if (searchQuery.isNotEmpty()) {
                viewModel.updateSearchQuery("")
            } else {
                showSearch = false
            }
        }
    }

    // Always keep HomeScreen in the composition, but overlay Search
    HomeScreen(
        apps = apps,
        isLoading = isLoading,
        onAppClick = { app ->
            viewModel.launchApp(app)
        },
        onSearchClick = {
            showSearch = true
        }
    )

    // Animated overlay for search
    AnimatedVisibility(
        visible = showSearch,
        enter = fadeIn(animationSpec = tween(300)),
        exit = fadeOut(animationSpec = tween(300))
    ) {
        GlobalSearchScreen(
            query = searchQuery,
            onQueryChange = { viewModel.updateSearchQuery(it) },
            results = searchResults,
            onResultClick = { result ->
                when (result) {
                    is SearchResult.App -> {
                        viewModel.launchApp(result.app)
                        // Transition back to home after launch
                        showSearch = false
                        viewModel.updateSearchQuery("")
                    }
                    is SearchResult.Command -> {
                        // Execute the command
                        viewModel.executeCommand(result.registered, result.args)
                        // Note: For commands, we stay in Search to show results
                    }
                }
            },
            commandResult = commandResult,
            onDismissResult = {
                viewModel.clearCommandResult()
            },
            onClose = {
                showSearch = false
                viewModel.updateSearchQuery("")
                viewModel.clearCommandResult()
            }
        )
    }
}

// Extension to access command registry from ViewModel
private fun LauncherViewModel.getCommandRegistry() =
    (getApplication<LauncherApplication>()).pluginLoader.getCommandRegistry()
