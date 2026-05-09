package org.example.project.data

data class Note(
    val id: Long,
    val remoteId: Long? = null,
    val title: String,
    val content: String,
    val category: String = "General",
    val isFavorite: Boolean = false,
    val syncStatus: NoteSyncStatus = NoteSyncStatus.PENDING,
    val syncAction: SyncAction = SyncAction.CREATE,
    val createdAt: Long,
    val updatedAt: Long
) {
    val shortContent: String
        get() = content.lineSequence().firstOrNull().orEmpty().ifBlank { content }
}

enum class NoteSyncStatus(val label: String) {
    PENDING("Pending"),
    SYNCED("Synced"),
    FAILED("Failed");

    companion object {
        fun from(value: String): NoteSyncStatus = entries.firstOrNull { it.name == value } ?: PENDING
    }
}

enum class SyncAction {
    NONE, CREATE, UPDATE, DELETE;

    companion object {
        fun from(value: String): SyncAction = entries.firstOrNull { it.name == value } ?: NONE
    }
}

enum class SortOrder(val label: String) {
    NEWEST("Newest"),
    OLDEST("Oldest"),
    TITLE("Title A-Z");

    companion object {
        fun from(value: String): SortOrder = entries.firstOrNull { it.name == value } ?: NEWEST
    }
}

enum class ThemeMode(val label: String) {
    SYSTEM("System"),
    LIGHT("Light"),
    DARK("Dark");

    companion object {
        fun from(value: String): ThemeMode = entries.firstOrNull { it.name == value } ?: SYSTEM
    }
}
