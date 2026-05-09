package org.example.project.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.example.project.data.Profile
import org.example.project.data.SortOrder
import org.example.project.data.ThemeMode

@Composable
fun ProfileSettingsCard(
    profile: Profile,
    themeMode: ThemeMode,
    sortOrder: SortOrder,
    totalNotes: Int,
    totalFavorites: Int,
    pendingSyncCount: Long,
    isSyncing: Boolean,
    onThemeModeChange: (ThemeMode) -> Unit,
    onSortOrderChange: (SortOrder) -> Unit,
    onSyncClick: () -> Unit,
    onImportRemoteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Profile & Local Settings",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            SettingRow(icon = Icons.Default.Person, title = "Name", value = profile.name)
            HorizontalDivider()
            SettingRow(icon = Icons.Default.Email, title = "E-mail", value = profile.email)
            HorizontalDivider()
            SettingRow(icon = Icons.Default.Place, title = "Location", value = profile.location)
            HorizontalDivider()
            SettingRow(icon = Icons.Default.Description, title = "Total Notes", value = totalNotes.toString())
            SettingRow(icon = Icons.Default.Favorite, title = "Favorite Notes", value = totalFavorites.toString())
            SettingRow(icon = Icons.Default.Sync, title = "Pending Sync", value = pendingSyncCount.toString())
            HorizontalDivider()

            ChoiceSection(
                title = "Theme Mode",
                icon = if (themeMode == ThemeMode.DARK) Icons.Default.DarkMode else Icons.Default.LightMode,
                selectedLabel = themeMode.label
            ) {
                ThemeMode.entries.forEach { mode ->
                    ChoiceButton(
                        text = mode.label,
                        selected = themeMode == mode,
                        onClick = { onThemeModeChange(mode) }
                    )
                }
            }

            ChoiceSection(
                title = "Sort Order",
                icon = Icons.Default.Sort,
                selectedLabel = sortOrder.label
            ) {
                SortOrder.entries.forEach { order ->
                    ChoiceButton(
                        text = order.label,
                        selected = sortOrder == order,
                        onClick = { onSortOrderChange(order) }
                    )
                }
            }

            HorizontalDivider()

            Button(
                onClick = onSyncClick,
                enabled = !isSyncing,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Sync, contentDescription = "Sync")
                Spacer(Modifier.width(8.dp))
                Text(if (isSyncing) "Syncing..." else "Sync Pending Notes")
            }

            OutlinedButton(
                onClick = onImportRemoteClick,
                enabled = !isSyncing,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Download, contentDescription = "Import")
                Spacer(Modifier.width(8.dp))
                Text("Import Remote Sample")
            }
        }
    }
}

@Composable
private fun SettingRow(
    icon: ImageVector,
    title: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.width(14.dp))
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun ChoiceSection(
    title: String,
    icon: ImageVector,
    selectedLabel: String,
    content: @Composable () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.width(14.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Selected: $selectedLabel",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            content()
        }
    }
}

@Composable
private fun ChoiceButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    if (selected) {
        Button(onClick = onClick) { Text(text) }
    } else {
        OutlinedButton(onClick = onClick) { Text(text) }
    }
}
