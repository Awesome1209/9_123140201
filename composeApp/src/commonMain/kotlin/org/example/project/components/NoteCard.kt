package org.example.project.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.example.project.data.Note
import org.example.project.data.NoteSyncStatus
import org.example.project.platform.currentTimeMillis

@Composable
fun NoteCard(
    note: Note,
    onClick: () -> Unit,
    onToggleFavorite: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CategoryChip(note.category)
                    SyncStatusChip(note.syncStatus)
                }

                IconButton(onClick = { onToggleFavorite(note.id) }) {
                    Icon(
                        imageVector = if (note.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = if (note.isFavorite) "Remove favorite" else "Add favorite",
                        tint = if (note.isFavorite) Color(0xFFE91E63) else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Text(
                text = note.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = note.content,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = "Updated time",
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Updated ${formatMillis(note.updatedAt)}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun CategoryChip(category: String) {
    val normalized = category.lowercase()
    val icon = Icons.Default.Description
    val (background, content) = when (normalized) {
        "kuliah", "study", "remote" -> Color(0xFFEDE7FF) to Color(0xFF6246EA)
        "tugas", "tasks" -> Color(0xFFE8F5E9) to Color(0xFF1B8A45)
        "ide", "ideas" -> Color(0xFFFFF3E0) to Color(0xFFEF7C00)
        "pribadi", "personal" -> Color(0xFFFFE8EF) to Color(0xFFD92D59)
        else -> Color(0xFFF1F3F8) to Color(0xFF4B5565)
    }
    Pill(text = category, icon = icon, background = background, content = content)
}

@Composable
fun SyncStatusChip(status: NoteSyncStatus) {
    val (background, content, icon) = when (status) {
        NoteSyncStatus.SYNCED -> Triple(Color(0xFFE8F5E9), Color(0xFF087A31), Icons.Default.CloudDone)
        NoteSyncStatus.PENDING -> Triple(Color(0xFFFFF3D8), Color(0xFFD97706), Icons.Default.Sync)
        NoteSyncStatus.FAILED -> Triple(Color(0xFFFFE5E5), Color(0xFFC62828), Icons.Default.CloudOff)
    }
    Pill(text = status.label.uppercase(), icon = icon, background = background, content = content)
}

@Composable
private fun Pill(
    text: String,
    icon: ImageVector,
    background: Color,
    content: Color
) {
    Row(
        modifier = Modifier
            .background(background, RoundedCornerShape(999.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Box(
            modifier = Modifier
                .size(7.dp)
                .background(content, CircleShape)
        )
        Icon(
            imageVector = icon,
            contentDescription = text,
            tint = content,
            modifier = Modifier.size(15.dp)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = content,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1
        )
    }
}

fun formatMillis(value: Long): String {
    if (value <= 0L) return "-"
    val diff = (currentTimeMillis() - value).coerceAtLeast(0L)
    val minute = 60_000L
    val hour = 60 * minute
    val day = 24 * hour
    return when {
        diff < minute -> "just now"
        diff < hour -> "${diff / minute} minutes ago"
        diff < day -> "${diff / hour} hours ago"
        diff < 2 * day -> "yesterday"
        else -> "${diff / day} days ago"
    }
}
