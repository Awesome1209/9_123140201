package org.example.project.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.NetworkWifi
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.example.project.data.SortOrder
import org.example.project.data.ThemeMode
import org.example.project.viewmodel.NotesUiState
import org.example.project.viewmodel.PlatformUiState
import org.example.project.viewmodel.ProfileViewModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import org.jetbrains.compose.resources.painterResource
import org.example.project.generated.resources.Res
import org.example.project.generated.resources.profile_photo


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    profileViewModel: ProfileViewModel,
    notesUiState: NotesUiState,
    platformUiState: PlatformUiState,
    onSortOrderChange: (SortOrder) -> Unit,
    onSyncClick: () -> Unit,
    onImportRemoteClick: () -> Unit,
    onRefreshPlatformInfo: () -> Unit
) {
    val uiState by profileViewModel.uiState.collectAsState()

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
                            text = "Profile",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Settings & device dashboard",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onImportRemoteClick) {
                        Icon(
                            imageVector = Icons.Default.CloudDownload,
                            contentDescription = "Import remote notes"
                        )
                    }
                    IconButton(onClick = onRefreshPlatformInfo) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh platform info"
                        )
                    }
                }
            )
        }

        item {
            ProfileHeaderCard(
                name = uiState.profile.name,
                nim = "123140201",
                subtitle = "Mobile Development Project"
            )
        }

        item {
            AppSummaryCard(
                totalNotes = notesUiState.totalNotes,
                totalFavorites = notesUiState.totalFavorites,
                pendingSync = notesUiState.pendingSyncCount
            )
        }

        item {
            SettingsDashboardCard(
                themeMode = uiState.themeMode,
                sortOrder = notesUiState.sortOrder,
                onThemeModeChange = profileViewModel::setThemeMode,
                onSortOrderChange = onSortOrderChange
            )
        }

        item {
            DeviceInfoCard(platformUiState = platformUiState)
        }

        item {
            NetworkInfoCard(platformUiState = platformUiState)
        }

        item {
            BatteryInfoCard(platformUiState = platformUiState)
        }

        item {
            UtilitiesCard(
                onSyncClick = onSyncClick,
                onImportRemoteClick = onImportRemoteClick
            )
        }
    }
}

@Composable
private fun ProfileHeaderCard(
    name: String,
    nim: String,
    subtitle: String
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(28.dp),
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.68f),
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(82.dp)
                    .clip(CircleShape)
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.16f),
                        shape = CircleShape
                    )
                    .border(
                        width = 3.dp,
                        color = MaterialTheme.colorScheme.surface,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(Res.drawable.profile_photo),
                    contentDescription = "Profile photo",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = nim,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.78f)
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.72f)
                )
            }
        }
    }
}

@Composable
private fun AppSummaryCard(
    totalNotes: Int,
    totalFavorites: Int,
    pendingSync: Long
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            SummaryTile(
                icon = Icons.Default.Description,
                label = "Total Notes",
                value = totalNotes.toString(),
                tint = Color(0xFF6246EA),
                modifier = Modifier.weight(1f)
            )
            SummaryTile(
                icon = Icons.Default.Favorite,
                label = "Favorites",
                value = totalFavorites.toString(),
                tint = Color(0xFFE91E63),
                modifier = Modifier.weight(1f)
            )
            SummaryTile(
                icon = Icons.Default.CloudDone,
                label = "Pending",
                value = pendingSync.toString(),
                tint = Color(0xFFF59E0B),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun SummaryTile(
    icon: ImageVector,
    label: String,
    value: String,
    tint: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        IconBox(icon = icon, tint = tint)

        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = tint,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun SettingsDashboardCard(
    themeMode: ThemeMode,
    sortOrder: SortOrder,
    onThemeModeChange: (ThemeMode) -> Unit,
    onSortOrderChange: (SortOrder) -> Unit
) {
    DashboardCard(
        title = "Settings",
        icon = Icons.Default.Settings,
        iconColor = MaterialTheme.colorScheme.primary
    ) {
        SettingSelector(
            icon = Icons.Default.WbSunny,
            title = "Theme Mode",
            subtitle = "Pilih Tema!",
            currentLabel = themeMode.label,
            options = ThemeMode.entries.map { it.label },
            selectedIndex = ThemeMode.entries.indexOf(themeMode),
            onSelected = { index ->
                onThemeModeChange(ThemeMode.entries[index])
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        SettingSelector(
            icon = Icons.Default.Sort,
            title = "Sort Order",
            subtitle = "Urutkan notes!",
            currentLabel = sortOrder.label,
            options = SortOrder.entries.map { it.label },
            selectedIndex = SortOrder.entries.indexOf(sortOrder),
            onSelected = { index ->
                onSortOrderChange(SortOrder.entries[index])
            }
        )
    }
}

@Composable
private fun SettingSelector(
    icon: ImageVector,
    title: String,
    subtitle: String,
    currentLabel: String,
    options: List<String>,
    selectedIndex: Int,
    onSelected: (Int) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconBox(
                icon = icon,
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                text = currentLabel,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            options.forEachIndexed { index, label ->
                val selected = index == selectedIndex

                Surface(
                    modifier = Modifier.clickable {
                        onSelected(index)
                    },
                    shape = RoundedCornerShape(999.dp),
                    color = if (selected) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.36f)
                    }
                ) {
                    Text(
                        text = label,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = if (selected) {
                            MaterialTheme.colorScheme.onPrimary
                        } else {
                            MaterialTheme.colorScheme.primary
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun DeviceInfoCard(
    platformUiState: PlatformUiState
) {
    DashboardCard(
        title = "Device Information",
        icon = Icons.Default.Devices,
        iconColor = MaterialTheme.colorScheme.primary
    ) {
        InfoRow(label = "Device Name", value = platformUiState.deviceName)
        InfoRow(label = "Platform", value = platformUiState.platformName)
        InfoRow(label = "OS Version", value = platformUiState.osVersion)
        InfoRow(label = "Device Type", value = platformUiState.deviceType)
    }
}

@Composable
private fun NetworkInfoCard(
    platformUiState: PlatformUiState
) {
    val statusColor = if (platformUiState.isConnected) {
        Color(0xFF087A31)
    } else {
        Color(0xFFD97706)
    }

    DashboardCard(
        title = "Network Information",
        icon = Icons.Default.NetworkWifi,
        iconColor = statusColor
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = platformUiState.syncAvailabilityLabel,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(statusColor, CircleShape)
                )
                Text(
                    text = platformUiState.networkLabel,
                    color = statusColor,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun BatteryInfoCard(
    platformUiState: PlatformUiState
) {
    val level = platformUiState.batteryLevel?.coerceIn(0, 100) ?: 0
    val progress = level / 100f

    DashboardCard(
        title = "Battery Information",
        icon = Icons.Default.BatteryChargingFull,
        iconColor = Color(0xFF16A34A)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Level",
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = platformUiState.batteryLabel,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF16A34A)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            color = Color(0xFF16A34A),
            trackColor = Color(0xFFE0F2E8)
        )

        Spacer(modifier = Modifier.height(12.dp))

        InfoRow(
            label = "Charging",
            value = platformUiState.chargingLabel
        )
    }
}

@Composable
private fun UtilitiesCard(
    onSyncClick: () -> Unit,
    onImportRemoteClick: () -> Unit
) {
    DashboardCard(
        title = "Utilities",
        icon = Icons.Default.Settings,
        iconColor = MaterialTheme.colorScheme.primary
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedButton(
                onClick = onSyncClick,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(18.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Sync,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Sync Pending")
            }

            Button(
                onClick = onImportRemoteClick,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(18.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CloudDownload,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Import Remote")
            }
        }
    }
}

@Composable
private fun DashboardCard(
    title: String,
    icon: ImageVector,
    iconColor: Color,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                IconBox(
                    icon = icon,
                    tint = iconColor
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            content()
        }
    }
}

@Composable
private fun IconBox(
    icon: ImageVector,
    tint: Color
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .background(
                color = tint.copy(alpha = 0.12f),
                shape = RoundedCornerShape(12.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint
        )
    }
}

@Composable
private fun InfoRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            text = value,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}