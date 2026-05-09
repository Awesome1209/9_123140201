package org.example.project.navigation

sealed class Screen(val route: String) {
    data object Notes : Screen("notes")
    data object Favorites : Screen("favorites")
    data object Ai : Screen("ai")
    data object Profile : Screen("profile")
    data object AddNote : Screen("add_note")
    data object NoteDetail : Screen("note_detail")
    data object EditNote : Screen("edit_note")
}
