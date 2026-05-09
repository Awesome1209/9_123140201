package org.example.project

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import org.example.project.ai.AiRepository
import org.example.project.data.local.DatabaseDriverFactory
import org.example.project.data.local.SettingsManager
import org.example.project.data.repository.NotesRepository
import org.example.project.di.appModule
import org.example.project.navigation.AppNavigation
import org.example.project.platform.BatteryInfo
import org.example.project.platform.DeviceInfo
import org.example.project.platform.NetworkMonitor
import org.example.project.ui.theme.ProfileAppTheme
import org.example.project.viewmodel.AiViewModel
import org.example.project.viewmodel.NotesViewModel
import org.example.project.viewmodel.PlatformViewModel
import org.example.project.viewmodel.ProfileViewModel
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject

@Composable
fun App(
    databaseDriverFactory: DatabaseDriverFactory
) {
    KoinApplication(application = { modules(appModule(databaseDriverFactory)) }) {
        val settingsManager: SettingsManager = koinInject()
        val repository: NotesRepository = koinInject()
        val aiRepository: AiRepository = koinInject()
        val deviceInfo: DeviceInfo = koinInject()
        val networkMonitor: NetworkMonitor = koinInject()
        val batteryInfo: BatteryInfo = koinInject()

        val notesViewModel: NotesViewModel = viewModel {
            NotesViewModel(
                repository = repository,
                settingsManager = settingsManager
            )
        }
        val profileViewModel: ProfileViewModel = viewModel {
            ProfileViewModel(settingsManager = settingsManager)
        }
        val aiViewModel: AiViewModel = viewModel {
            AiViewModel(aiRepository = aiRepository)
        }
        val platformViewModel: PlatformViewModel = viewModel {
            PlatformViewModel(
                deviceInfo = deviceInfo,
                networkMonitor = networkMonitor,
                batteryInfo = batteryInfo
            )
        }

        val profileUiState by profileViewModel.uiState.collectAsState()

        ProfileAppTheme(darkTheme = profileUiState.isDarkMode) {
            AppNavigation(
                notesViewModel = notesViewModel,
                profileViewModel = profileViewModel,
                platformViewModel = platformViewModel,
                aiViewModel = aiViewModel
            )
        }
    }
}
