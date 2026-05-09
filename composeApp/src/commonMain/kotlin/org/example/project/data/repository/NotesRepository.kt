package org.example.project.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOne
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.example.project.data.Note
import org.example.project.data.NoteSyncStatus
import org.example.project.data.SyncAction
import org.example.project.data.local.toDomain
import org.example.project.data.remote.RemoteNoteApi
import org.example.project.database.NotesDatabase
import org.example.project.database.Note as DbNote
import org.example.project.platform.currentTimeMillis

class NotesRepository(
    private val database: NotesDatabase,
    private val remoteNoteApi: RemoteNoteApi
) {
    private val queries = database.noteQueries

    fun observeNotes(): Flow<List<Note>> {
        return queries.selectVisibleNotes()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { notes -> notes.map { it.toDomain() } }
    }

    fun observePendingSyncCount(): Flow<Long> {
        return queries.countPendingSync()
            .asFlow()
            .mapToOne(Dispatchers.Default)
    }

    suspend fun addNote(
        title: String,
        content: String,
        category: String
    ): Result<Unit> {
        if (title.isBlank() || content.isBlank()) {
            return Result.failure(IllegalArgumentException("Title dan content wajib diisi."))
        }

        val id = withContext(Dispatchers.Default) {
            val now = nowMillis()
            queries.insertNote(
                remote_id = null,
                title = title.trim(),
                content = content.trim(),
                category = category.trim().ifBlank { "General" },
                is_favorite = 0L,
                sync_status = NoteSyncStatus.PENDING.name,
                sync_action = SyncAction.CREATE.name,
                is_deleted = 0L,
                created_at = now,
                updated_at = now
            )
            queries.lastInsertRowId().executeAsOne()
        }

        syncNoteById(id)
        return Result.success(Unit)
    }

    suspend fun updateNote(
        id: Long,
        title: String,
        content: String,
        category: String
    ): Result<Unit> {
        if (title.isBlank() || content.isBlank()) {
            return Result.failure(IllegalArgumentException("Title dan content wajib diisi."))
        }

        val existing = getDbNoteById(id)
            ?: return Result.failure(IllegalArgumentException("Note tidak ditemukan."))
        val action = if (existing.remote_id == null) SyncAction.CREATE else SyncAction.UPDATE

        withContext(Dispatchers.Default) {
            queries.updateNote(
                title = title.trim(),
                content = content.trim(),
                category = category.trim().ifBlank { "General" },
                sync_status = NoteSyncStatus.PENDING.name,
                sync_action = action.name,
                updated_at = nowMillis(),
                id = id
            )
        }

        syncNoteById(id)
        return Result.success(Unit)
    }

    suspend fun toggleFavorite(id: Long): Result<Unit> {
        val existing = getDbNoteById(id)
            ?: return Result.failure(IllegalArgumentException("Note tidak ditemukan."))
        val newFavoriteValue = if (existing.is_favorite == 0L) 1L else 0L

        // Favorite disimpan sebagai state lokal agar tidak mengubah status SYNCED menjadi FAILED.
        withContext(Dispatchers.Default) {
            queries.toggleFavorite(
                is_favorite = newFavoriteValue,
                sync_status = existing.sync_status,
                sync_action = existing.sync_action,
                updated_at = nowMillis(),
                id = id
            )
        }

        return Result.success(Unit)
    }

    suspend fun deleteNote(id: Long): Result<Unit> {
        val existing = getDbNoteById(id)
            ?: return Result.failure(IllegalArgumentException("Note tidak ditemukan."))

        if (existing.remote_id == null) {
            withContext(Dispatchers.Default) { queries.deletePermanently(id) }
            return Result.success(Unit)
        }

        withContext(Dispatchers.Default) {
            queries.softDeleteNote(
                sync_status = NoteSyncStatus.PENDING.name,
                updated_at = nowMillis(),
                id = id
            )
        }

        return try {
            remoteNoteApi.deleteNote(existing.remote_id)
            withContext(Dispatchers.Default) { queries.deletePermanently(id) }
            Result.success(Unit)
        } catch (error: Throwable) {
            markFailed(id)
            Result.failure(error)
        }
    }

    suspend fun syncAllPending(): Result<Int> {
        return runCatching {
            val pending = withContext(Dispatchers.Default) {
                queries.selectPendingSync().executeAsList()
            }
            var successCount = 0
            pending.forEach { dbNote ->
                syncDbNote(dbNote).onSuccess { successCount++ }
            }
            successCount
        }
    }

    suspend fun importRemoteNotes(): Result<Int> {
        return runCatching {
            val remoteNotes = remoteNoteApi.fetchRemoteNotes(limit = 8)
            val now = nowMillis()
            withContext(Dispatchers.Default) {
                remoteNotes.forEach { remote ->
                    queries.insertNote(
                        remote_id = remote.id,
                        title = remote.title,
                        content = remote.body,
                        category = "Remote",
                        is_favorite = 0L,
                        sync_status = NoteSyncStatus.SYNCED.name,
                        sync_action = SyncAction.NONE.name,
                        is_deleted = 0L,
                        created_at = now,
                        updated_at = now
                    )
                }
            }
            remoteNotes.size
        }
    }

    private suspend fun syncNoteById(id: Long): Result<Unit> {
        val dbNote = withContext(Dispatchers.Default) {
            queries.selectPendingSync().executeAsList().firstOrNull { it.id == id }
        } ?: return Result.success(Unit)

        return syncDbNote(dbNote)
    }

    private suspend fun syncDbNote(dbNote: DbNote): Result<Unit> {
        return try {
            when (SyncAction.from(dbNote.sync_action)) {
                SyncAction.CREATE -> {
                    val remote = remoteNoteApi.createNote(
                        title = dbNote.title,
                        content = dbNote.content
                    )
                    markSynced(dbNote.id, remote.id ?: dbNote.remote_id ?: 101L)
                }

                SyncAction.UPDATE -> {
                    val remoteId = dbNote.remote_id

                    if (remoteId == null || remoteId > 100L) {
                        val remote = remoteNoteApi.createNote(
                            title = dbNote.title,
                            content = dbNote.content
                        )

                        markSynced(
                            id = dbNote.id,
                            remoteId = remote.id ?: remoteId ?: 101L
                        )
                    } else {
                        remoteNoteApi.updateNote(
                            remoteId = remoteId,
                            title = dbNote.title,
                            content = dbNote.content
                        )

                        markSynced(
                            id = dbNote.id,
                            remoteId = remoteId
                        )
                    }
                }

                SyncAction.DELETE -> {
                    dbNote.remote_id?.let { remoteNoteApi.deleteNote(it) }
                    withContext(Dispatchers.Default) { queries.deletePermanently(dbNote.id) }
                }

                SyncAction.NONE -> {
                    markSynced(dbNote.id, dbNote.remote_id)
                }
            }
            Result.success(Unit)
        } catch (error: Throwable) {
            markFailed(dbNote.id)
            Result.failure(error)
        }
    }

    private suspend fun getDbNoteById(id: Long): DbNote? {
        return withContext(Dispatchers.Default) {
            queries.selectById(id).executeAsOneOrNull()
        }
    }

    private suspend fun markSynced(id: Long, remoteId: Long?) {
        withContext(Dispatchers.Default) {
            queries.markSynced(
                remote_id = remoteId,
                updated_at = nowMillis(),
                id = id
            )
        }
    }

    private suspend fun markFailed(id: Long) {
        withContext(Dispatchers.Default) {
            queries.markSyncFailed(
                updated_at = nowMillis(),
                id = id
            )
        }
    }

    private fun nowMillis(): Long = currentTimeMillis()
}
