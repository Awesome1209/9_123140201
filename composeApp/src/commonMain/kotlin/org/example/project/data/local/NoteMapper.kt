package org.example.project.data.local

import org.example.project.data.Note
import org.example.project.data.NoteSyncStatus
import org.example.project.data.SyncAction
import org.example.project.database.Note as DbNote

fun DbNote.toDomain(): Note {
    return Note(
        id = id,
        remoteId = remote_id,
        title = title,
        content = content,
        category = category,
        isFavorite = is_favorite != 0L,
        syncStatus = NoteSyncStatus.from(sync_status),
        syncAction = SyncAction.from(sync_action),
        createdAt = created_at,
        updatedAt = updated_at
    )
}
