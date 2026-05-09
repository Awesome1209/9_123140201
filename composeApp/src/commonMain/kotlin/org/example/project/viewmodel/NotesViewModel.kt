package org.example.project.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.project.data.SortOrder
import org.example.project.data.local.SettingsManager
import org.example.project.data.repository.NotesRepository

class NotesViewModel(
    private val repository: NotesRepository,
    private val settingsManager: SettingsManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotesUiState())
    val uiState: StateFlow<NotesUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.observeNotes().collect { notes ->
                _uiState.update { current ->
                    current.copy(
                        notes = notes,
                        isLoading = false,
                        errorMessage = null
                    )
                }
            }
        }

        viewModelScope.launch {
            repository.observePendingSyncCount().collect { count ->
                _uiState.update { it.copy(pendingSyncCount = count) }
            }
        }

        viewModelScope.launch {
            settingsManager.sortOrder.collect { order ->
                _uiState.update { it.copy(sortOrder = order) }
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun onFavoriteSearchQueryChange(query: String) {
        _uiState.update { it.copy(favoriteSearchQuery = query) }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, lastSyncMessage = null) }
    }

    fun cycleSortOrder() {
        settingsManager.cycleSortOrder()
    }

    fun setSortOrder(order: SortOrder) {
        settingsManager.setSortOrder(order)
    }

    fun addNote(title: String, content: String, category: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSyncing = true, errorMessage = null) }
            repository.addNote(title, content, category)
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            isSyncing = false,
                            lastSyncMessage = "Note disimpan lokal dan proses sync sudah dicoba."
                        )
                    }
                }
                .onFailure { error -> setError(error.message ?: "Gagal menambah note.") }
        }
    }

    fun updateNote(id: Long, title: String, content: String, category: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSyncing = true, errorMessage = null) }
            repository.updateNote(id, title, content, category)
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            isSyncing = false,
                            lastSyncMessage = "Note diperbarui lokal dan proses sync sudah dicoba."
                        )
                    }
                }
                .onFailure { error -> setError(error.message ?: "Gagal memperbarui note.") }
        }
    }

    fun toggleFavorite(id: Long) {
        viewModelScope.launch {
            repository.toggleFavorite(id)
                .onFailure { error -> setError(error.message ?: "Gagal mengubah favorit.") }
        }
    }

    fun deleteNote(id: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSyncing = true, errorMessage = null) }
            repository.deleteNote(id)
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            isSyncing = false,
                            lastSyncMessage = "Note dihapus lokal dan sync delete sudah dicoba."
                        )
                    }
                }
                .onFailure { error -> setError(error.message ?: "Gagal menghapus note.") }
        }
    }

    fun syncAllPending() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSyncing = true, errorMessage = null, lastSyncMessage = null) }
            repository.syncAllPending()
                .onSuccess { count ->
                    _uiState.update {
                        it.copy(
                            isSyncing = false,
                            lastSyncMessage = if (count > 0) {
                                "$count pending note berhasil disinkronkan."
                            } else {
                                "Tidak ada pending sync."
                            }
                        )
                    }
                }
                .onFailure { error -> setError(error.message ?: "Sync gagal. Data tetap aman di lokal.") }
        }
    }

    fun importRemoteNotes() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSyncing = true, errorMessage = null, lastSyncMessage = null) }
            repository.importRemoteNotes()
                .onSuccess { count ->
                    _uiState.update {
                        it.copy(
                            isSyncing = false,
                            lastSyncMessage = "$count remote notes berhasil diimpor sebagai data lokal."
                        )
                    }
                }
                .onFailure { error -> setError(error.message ?: "Import remote gagal.") }
        }
    }

    fun getNoteById(id: Long) = _uiState.value.notes.firstOrNull { it.id == id }

    private fun setError(message: String) {
        _uiState.update {
            it.copy(
                isSyncing = false,
                isLoading = false,
                errorMessage = message
            )
        }
    }
}
