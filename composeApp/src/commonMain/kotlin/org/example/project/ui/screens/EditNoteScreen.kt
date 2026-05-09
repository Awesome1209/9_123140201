@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package org.example.project.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import org.example.project.components.NoteForm
import org.example.project.data.Note
import org.example.project.viewmodel.AiUiState

@Composable
fun EditNoteScreen(
    note: Note?,
    aiUiState: AiUiState,
    onBack: () -> Unit,
    onSuggestCategory: (String, String) -> Unit,
    onClearAiResult: () -> Unit,
    onSave: (String, String, String) -> Unit
) {
    if (note == null) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = { Text("Edit Note") },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text("Back") }
                }
            )
            Text("Note not found")
        }
        return
    }

    var title by rememberSaveable { mutableStateOf("") }
    var content by rememberSaveable { mutableStateOf("") }
    var category by rememberSaveable { mutableStateOf("") }
    var showMenu by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(note.id) {
        title = note.title
        content = note.content
        category = note.category
        onClearAiResult()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Edit Note") },
            navigationIcon = {
                TextButton(onClick = onBack) { Text("Back") }
            },
            actions = {
                TextButton(onClick = { onSave(title, content, category) }) {
                    Text("Save")
                }
                TextButton(onClick = { showMenu = true }) {
                    Text("More")
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Close menu") },
                        onClick = { showMenu = false }
                    )
                }
            }
        )

        NoteForm(
            title = title,
            content = content,
            category = category,
            onTitleChange = { title = it },
            onContentChange = { content = it },
            onCategoryChange = { category = it },
            onSave = { onSave(title, content, category) },
            saveLabel = "Update Note"
        )

        AiSuggestCategoryCard(
            suggestedCategory = aiUiState.suggestedCategory,
            isLoading = aiUiState.isLoading,
            errorMessage = aiUiState.errorMessage,
            onSuggest = { onSuggestCategory(title, content) },
            onUseCategory = { category = it }
        )
    }
}
