package org.example.project.viewmodel

import org.example.project.data.Note
import org.example.project.data.SortOrder

data class NotesUiState(
    val notes: List<Note> = emptyList(),
    val searchQuery: String = "",
    val favoriteSearchQuery: String = "",
    val sortOrder: SortOrder = SortOrder.NEWEST,
    val isLoading: Boolean = true,
    val isSyncing: Boolean = false,
    val errorMessage: String? = null,
    val lastSyncMessage: String? = null,
    val pendingSyncCount: Long = 0L
) {
    val visibleNotes: List<Note>
        get() = notes.applySearchAndSort(searchQuery, sortOrder)

    val favoriteNotes: List<Note>
        get() = notes.filter { it.isFavorite }.applySearchAndSort(favoriteSearchQuery, sortOrder)

    val totalNotes: Int
        get() = notes.size

    val totalFavorites: Int
        get() = notes.count { it.isFavorite }

    val hasError: Boolean
        get() = errorMessage != null
}

private fun List<Note>.applySearchAndSort(
    query: String,
    sortOrder: SortOrder
): List<Note> {
    val filtered = if (query.isBlank()) {
        this
    } else {
        val q = query.trim().lowercase()
        filter { note ->
            note.title.lowercase().contains(q) ||
                note.content.lowercase().contains(q) ||
                note.category.lowercase().contains(q)
        }
    }

    return when (sortOrder) {
        SortOrder.NEWEST -> filtered.sortedByDescending { it.updatedAt }
        SortOrder.OLDEST -> filtered.sortedBy { it.updatedAt }
        SortOrder.TITLE -> filtered.sortedBy { it.title.lowercase() }
    }
}
