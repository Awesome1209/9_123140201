package org.example.project.data.local

import org.example.project.database.NotesDatabase

object NotesDatabaseProvider {
    fun create(driverFactory: DatabaseDriverFactory): NotesDatabase {
        return NotesDatabase(driverFactory.createDriver())
    }
}
