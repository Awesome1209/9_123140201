package org.example.project.viewmodel

import org.example.project.data.Profile
import org.example.project.data.ThemeMode

data class ProfileUiState(
    val profile: Profile = Profile(),
    val editName: String = profile.name,
    val editBio: String = profile.bio,
    val isEditing: Boolean = false,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val statusMessage: String? = null
) {
    val isDarkMode: Boolean
        get() = themeMode == ThemeMode.DARK
}
