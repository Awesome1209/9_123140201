@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package org.example.project.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.example.project.components.EmptyState
import org.example.project.components.NoteCard
import org.example.project.viewmodel.NotesUiState

@Composable
fun NotesScreen(
    uiState: NotesUiState,
    isConnected: Boolean,
    onSearchQueryChange: (String) -> Unit,
    onCycleSort: () -> Unit,
    onSyncClick: () -> Unit,
    onImportRemoteClick: () -> Unit,
    onDismissMessage: () -> Unit,
    onNoteClick: (Long) -> Unit,
    onToggleFavorite: (Long) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 104.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Notes Tugas 9",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Simpan catatanmu disini!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onCycleSort) {
                        Icon(Icons.Default.Sort, contentDescription = "Cycle sort")
                    }
                    IconButton(onClick = onImportRemoteClick) {
                        Icon(Icons.Default.Download, contentDescription = "Import remote notes")
                    }
                    IconButton(onClick = onSyncClick) {
                        Icon(Icons.Default.Sync, contentDescription = "Sync pending notes")
                    }
                }
            )
        }

        item {
            NetworkStatusBanner(isConnected = isConnected)
        }

        item {
            NotesHeroCard(
                totalNotes = uiState.totalNotes,
                favoriteCount = uiState.totalFavorites,
                pendingSyncCount = uiState.pendingSyncCount,
                sortLabel = uiState.sortOrder.label,
                isSyncing = uiState.isSyncing
            )
        }

        item {
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = { Text("Cari Catatan mu!") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                singleLine = true,
                shape = RoundedCornerShape(22.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ModernChip(
                    text = "All Notes",
                    icon = Icons.Default.Description,
                    selected = true
                )
                ModernChip(
                    text = "Favorites",
                    icon = Icons.Default.Favorite,
                    selected = false
                )
                ModernChip(
                    text = "Pending Sync",
                    icon = Icons.Default.CloudDone,
                    selected = false
                )
            }
        }

        uiState.errorMessage?.let { message ->
            item {
                MessageCard(
                    title = "Error",
                    message = message,
                    isError = true,
                    actionLabel = "Dismiss",
                    onAction = onDismissMessage
                )
            }
        }

        uiState.lastSyncMessage?.let { message ->
            item {
                MessageCard(
                    title = "Sync Info",
                    message = message,
                    isError = false,
                    actionLabel = "OK",
                    onAction = onDismissMessage
                )
            }
        }

        when {
            uiState.isLoading -> {
                item { LoadingState() }
            }

            uiState.visibleNotes.isEmpty() -> {
                item {
                    EmptyState(
                        title = "Belum ada catatan",
                        subtitle = "Tekan tombol + untuk membuat note lokal pertama, atau tekan icon download untuk import contoh dari remote API.",
                        icon = Icons.Default.Description
                    )
                }
            }

            else -> {
                items(
                    items = uiState.visibleNotes,
                    key = { it.id }
                ) { note ->
                    NoteCard(
                        note = note,
                        onClick = { onNoteClick(note.id) },
                        onToggleFavorite = onToggleFavorite,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun NetworkStatusBanner(isConnected: Boolean) {
    val background = if (isConnected) Color(0xFFE7F8EC) else Color(0xFFFFF3D8)
    val content = if (isConnected) Color(0xFF087A31) else Color(0xFFD97706)
    val icon = if (isConnected) Icons.Default.CheckCircle else Icons.Default.Warning
    val text = if (isConnected) "Online • Remote sync available" else "Offline Mode • Local notes still available"

    Surface(
        modifier = Modifier.padding(horizontal = 16.dp),
        shape = RoundedCornerShape(999.dp),
        color = background
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = content,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                color = content,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun NotesHeroCard(
    totalNotes: Int,
    favoriteCount: Int,
    pendingSyncCount: Long,
    sortLabel: String,
    isSyncing: Boolean
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(28.dp),
        tonalElevation = 2.dp,
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.72f)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "My Notes Workspace",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "Status penyimpanan catatan mu!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.76f)
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                HeroStatTile(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.Description,
                    label = "Notes",
                    value = totalNotes.toString(),
                    tint = Color(0xFF6246EA)
                )
                HeroStatTile(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.Favorite,
                    label = "Favorites",
                    value = favoriteCount.toString(),
                    tint = Color(0xFFE91E63)
                )
                HeroStatTile(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.CloudDone,
                    label = "Pending",
                    value = pendingSyncCount.toString(),
                    tint = Color(0xFFF59E0B)
                )
            }

            Surface(
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.72f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Sort, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Sort: $sortLabel", style = MaterialTheme.typography.labelLarge)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (isSyncing) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                            Spacer(Modifier.width(8.dp))
                            Text("Syncing...", style = MaterialTheme.typography.labelLarge)
                        } else {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF087A31), modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Ready", style = MaterialTheme.typography.labelLarge, color = Color(0xFF087A31))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HeroStatTile(
    icon: ImageVector,
    label: String,
    value: String,
    tint: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.88f),
        tonalElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(tint.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = label, tint = tint)
            }
            Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = tint)
            Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun ModernChip(
    text: String,
    icon: ImageVector,
    selected: Boolean
) {
    val background = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
    val content = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = background,
        tonalElevation = if (selected) 2.dp else 1.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(icon, contentDescription = text, tint = content, modifier = Modifier.size(18.dp))
            Text(text, color = content, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun LoadingState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        CircularProgressIndicator()
        Text(
            text = "Loading local notes...",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun MessageCard(
    title: String,
    message: String,
    isError: Boolean,
    actionLabel: String,
    onAction: () -> Unit
) {
    val background = if (isError) Color(0xFFFFEBEE) else Color(0xFFE8F5E9)
    val content = if (isError) Color(0xFFC62828) else Color(0xFF2E7D32)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(20.dp),
        color = background
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = if (isError) Icons.Default.ErrorOutline else Icons.Default.Sync,
                contentDescription = title,
                tint = content
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, color = content)
                Text(message, color = content, style = MaterialTheme.typography.bodySmall)
            }
            TextButton(onClick = onAction) { Text(actionLabel) }
        }
    }
}
