@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package org.example.project.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.example.project.components.NoteForm
import org.example.project.viewmodel.AiUiState

@Composable
fun AddNoteScreen(
    aiUiState: AiUiState,
    onBack: () -> Unit,
    onSuggestCategory: (String, String) -> Unit,
    onClearAiResult: () -> Unit,
    onSave: (String, String, String) -> Unit
) {
    var title by rememberSaveable { mutableStateOf("") }
    var content by rememberSaveable { mutableStateOf("") }
    var category by rememberSaveable { mutableStateOf("General") }

    LaunchedEffect(Unit) {
        onClearAiResult()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Add Note") },
            navigationIcon = {
                TextButton(onClick = onBack) { Text("Back") }
            },
            actions = {
                TextButton(
                    onClick = { onSave(title, content, category) }
                ) {
                    Text("Save")
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
            saveLabel = "Save Note"
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

@Composable
fun AiSuggestCategoryCard(
    suggestedCategory: String?,
    isLoading: Boolean,
    errorMessage: String?,
    onSuggest: () -> Unit,
    onUseCategory: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.48f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "AI Suggest Category",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = suggestedCategory?.let { "Kategori yang disarankan: $it" }
                    ?: "Gunakan Gemini untuk membaca title/content dan menyarankan kategori note.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.74f),
                modifier = Modifier.padding(top = 6.dp)
            )
            if (errorMessage != null) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 6.dp)
                )
            }
            Button(
                onClick = {
                    if (suggestedCategory != null) onUseCategory(suggestedCategory) else onSuggest()
                },
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                shape = RoundedCornerShape(18.dp)
            ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null)
                Text(if (suggestedCategory != null) "Use This Category" else if (isLoading) "Thinking..." else "Suggest Category")
            }
        }
    }
}
