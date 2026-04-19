package com.devlauncher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
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
        
        setContent {
            DevLauncherTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LauncherScreen(viewModel = viewModel)
                }
            }
        }
    }
}

/**
 * Main launcher screen
 * Manages navigation between home and search
 */
@Composable
fun LauncherScreen(viewModel: LauncherViewModel) {
    val apps by viewModel.apps.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val commandResult by viewModel.commandResult.collectAsState()
    
    var showSearch by remember { mutableStateOf(false) }
    
    if (showSearch) {
        GlobalSearchScreen(
            query = searchQuery,
            onQueryChange = { viewModel.updateSearchQuery(it) },
            results = searchResults,
            onResultClick = { result ->
                when (result) {
                    is SearchResult.App -> {
                        viewModel.launchApp(result.app)
                        showSearch = false
                        viewModel.updateSearchQuery("")
                    }
                    is SearchResult.Command -> {
                        val (_, args) = viewModel.getCommandRegistry().parseCommand(searchQuery)
                        viewModel.executeCommand(result.registered, args)
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
    } else {
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
    }
}

// Extension to access command registry from ViewModel
private fun LauncherViewModel.getCommandRegistry() =
    (getApplication<LauncherApplication>()).pluginLoader.getCommandRegistry()
