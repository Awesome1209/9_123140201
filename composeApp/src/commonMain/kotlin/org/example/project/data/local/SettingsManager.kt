package org.example.project.data.local

import com.russhwolf.settings.Settings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.example.project.data.SortOrder
import org.example.project.data.ThemeMode

class SettingsManager(
    private val settings: Settings = Settings()
) {
    private val _themeMode = MutableStateFlow(
        ThemeMode.from(settings.getString(KEY_THEME_MODE, ThemeMode.SYSTEM.name))
    )
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

    private val _sortOrder = MutableStateFlow(
        SortOrder.from(settings.getString(KEY_SORT_ORDER, SortOrder.NEWEST.name))
    )
    val sortOrder: StateFlow<SortOrder> = _sortOrder.asStateFlow()

    fun setThemeMode(mode: ThemeMode) {
        settings.putString(KEY_THEME_MODE, mode.name)
        _themeMode.value = mode
    }

    fun setSortOrder(order: SortOrder) {
        settings.putString(KEY_SORT_ORDER, order.name)
        _sortOrder.value = order
    }

    fun cycleSortOrder() {
        val next = when (_sortOrder.value) {
            SortOrder.NEWEST -> SortOrder.OLDEST
            SortOrder.OLDEST -> SortOrder.TITLE
            SortOrder.TITLE -> SortOrder.NEWEST
        }
        setSortOrder(next)
    }

    companion object {
        private const val KEY_THEME_MODE = "theme_mode"
        private const val KEY_SORT_ORDER = "sort_order"
    }
}
