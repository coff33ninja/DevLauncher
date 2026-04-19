package com.devlauncher.ui

import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import com.devlauncher.data.AppInfo

/**
 * Main home screen
 * Shows app grid and search bar
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    apps: List<AppInfo>,
    isLoading: Boolean,
    onAppClick: (AppInfo) -> Unit,
    onSearchClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            SearchBar(
                onClick = onSearchClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
        }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                apps.isEmpty() -> {
                    Text(
                        text = "No apps found",
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                else -> {
                    AppGrid(
                        apps = apps,
                        onAppClick = onAppClick
                    )
                }
            }
        }
    }
}

/**
 * Search bar component
 */
@Composable
fun SearchBar(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.clickable(onClick = onClick),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Search apps or type command...",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * App grid component
 */
@Composable
fun AppGrid(
    apps: List<AppInfo>,
    onAppClick: (AppInfo) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier.fillMaxSize()
    ) {
        items(apps) { app ->
            AppIcon(
                app = app,
                onClick = { onAppClick(app) }
            )
        }
    }
}

/**
 * App icon component
 */
@Composable
fun AppIcon(
    app: AppInfo,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(8.dp)
    ) {
        // App icon
        DrawableImage(
            drawable = app.icon,
            contentDescription = app.appName,
            modifier = Modifier.size(48.dp)
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // App name
        Text(
            text = app.appName,
            fontSize = 12.sp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

/**
 * Helper to display Android Drawable in Compose
 */
@Composable
fun DrawableImage(
    drawable: Drawable,
    contentDescription: String?,
    modifier: Modifier = Modifier
) {
    Image(
        bitmap = drawable.toBitmap().asImageBitmap(),
        contentDescription = contentDescription,
        modifier = modifier
    )
}
