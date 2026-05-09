package org.example.project.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.project.data.ThemeMode
import org.example.project.data.local.SettingsManager

class ProfileViewModel(
    private val settingsManager: SettingsManager
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            settingsManager.themeMode.collect { mode ->
                _uiState.update { it.copy(themeMode = mode) }
            }
        }
    }

    fun onEditNameChange(newName: String) {
        _uiState.update { current ->
            current.copy(
                editName = newName,
                statusMessage = null
            )
        }
    }

    fun onEditBioChange(newBio: String) {
        _uiState.update { current ->
            current.copy(
                editBio = newBio,
                statusMessage = null
            )
        }
    }

    fun startEditing() {
        _uiState.update { current ->
            current.copy(
                editName = current.profile.name,
                editBio = current.profile.bio,
                isEditing = true,
                statusMessage = null
            )
        }
    }

    fun saveProfile() {
        _uiState.update { current ->
            val safeName = current.editName.trim().ifBlank { current.profile.name }
            val safeBio = current.editBio.trim().ifBlank { current.profile.bio }

            current.copy(
                profile = current.profile.copy(
                    name = safeName,
                    bio = safeBio
                ),
                editName = safeName,
                editBio = safeBio,
                isEditing = false,
                statusMessage = "Profil berhasil diperbarui."
            )
        }
    }

    fun cancelEditing() {
        _uiState.update { current ->
            current.copy(
                editName = current.profile.name,
                editBio = current.profile.bio,
                isEditing = false,
                statusMessage = "Perubahan dibatalkan."
            )
        }
    }

    fun setThemeMode(mode: ThemeMode) {
        settingsManager.setThemeMode(mode)
        _uiState.update { it.copy(statusMessage = "Theme tersimpan: ${mode.label}.") }
    }

    fun setDarkMode(enabled: Boolean) {
        setThemeMode(if (enabled) ThemeMode.DARK else ThemeMode.LIGHT)
    }
}
