@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package org.example.project.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.ImageSearch
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Spellcheck
import androidx.compose.material.icons.filled.Summarize
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.example.project.components.AiActionCard
import org.example.project.components.AiResultCard
import org.example.project.components.ChatBubble
import org.example.project.components.TypingIndicator
import org.example.project.viewmodel.AiUiState

@Composable
fun AiAssistantScreen(
    uiState: AiUiState,
    onInputChange: (String) -> Unit,
    onSend: () -> Unit,
    onQuickPrompt: (String) -> Unit,
    onImageInputChange: (String) -> Unit,
    onAnalyzeImage: () -> Unit,
    onClearChat: () -> Unit,
    onClearToolResult: () -> Unit,
    onClearError: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = {
                Column {
                    Text(
                        text = "AI Assistant",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "AI Catatan Pintar mu!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            actions = {
                IconButton(onClick = onClearChat) {
                    Icon(Icons.Default.ClearAll, contentDescription = "Clear chat")
                }
            }
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(bottom = 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                AiHeroCard()
            }

            item {
                QuickActionsGrid(onQuickPrompt = onQuickPrompt)
            }

            if (uiState.errorMessage != null) {
                item {
                    AiErrorCard(
                        message = uiState.errorMessage,
                        onDismiss = onClearError
                    )
                }
            }

            if (uiState.hasToolResult) {
                item {
                    AiResultCard(
                        title = uiState.toolResultTitle.orEmpty(),
                        result = uiState.toolResult.orEmpty(),
                        onDismiss = onClearToolResult,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }

            item {
                ImageAnalysisCard(
                    value = uiState.imageInputText,
                    onValueChange = onImageInputChange,
                    onAnalyze = onAnalyzeImage,
                    isLoading = uiState.isLoading
                )
            }

            items(uiState.messages) { message ->
                ChatBubble(
                    message = message.text.ifBlank { "..." },
                    isUser = message.isUser
                )
            }

            if (uiState.isLoading) {
                item { TypingIndicator() }
            }
        }

        ChatInputBar(
            value = uiState.inputText,
            onValueChange = onInputChange,
            onSend = onSend,
            enabled = !uiState.isLoading && !uiState.isStreaming
        )
    }
}

@Composable
private fun AiHeroCard() {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.68f)
        )
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.16f)
            ) {
                Icon(
                    imageVector = Icons.Default.SmartToy,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(14.dp)
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Smart Notes AI",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "Chat, summarize, improve, suggest category, and analyze image prompts with Gemini.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.74f)
                )
            }
        }
    }
}

@Composable
private fun QuickActionsGrid(
    onQuickPrompt: (String) -> Unit
) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            AiActionCard(
                icon = Icons.Default.Summarize,
                title = "Summarize",
                subtitle = "Ringkas catatan panjang",
                onClick = { onQuickPrompt("Bantu saya merangkum catatan tentang Kotlin Multiplatform dalam poin-poin singkat.") },
                modifier = Modifier.weight(1f)
            )
            AiActionCard(
                icon = Icons.Default.Spellcheck,
                title = "Improve",
                subtitle = "Rapikan tulisan",
                onClick = { onQuickPrompt("Bantu saya memperbaiki dan merapikan catatan belajar agar lebih mudah dibaca.") },
                modifier = Modifier.weight(1f)
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            AiActionCard(
                icon = Icons.Default.Category,
                title = "Category",
                subtitle = "Saran kategori note",
                onClick = { onQuickPrompt("Berikan rekomendasi kategori catatan untuk topik pemrograman mobile dan alasannya.") },
                modifier = Modifier.weight(1f)
            )
            AiActionCard(
                icon = Icons.Default.Lightbulb,
                title = "Ideas",
                subtitle = "Generate ide catatan",
                onClick = { onQuickPrompt("Buatkan 5 ide catatan produktif untuk belajar Pengembangan Aplikasi Mobile.") },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun ImageAnalysisCard(
    value: String,
    onValueChange: (String) -> Unit,
    onAnalyze: () -> Unit,
    isLoading: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Icon(Icons.Default.ImageSearch, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Text(
                    text = "AI Image Analysis",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = "Masukkan deskripsi atau URL gambar untuk dianalisis sebagai catatan.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                label = { Text("Deskripsi / URL gambar") }
            )
            Button(
                onClick = onAnalyze,
                enabled = value.isNotBlank() && !isLoading,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp)
            ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null)
                Spacer(Modifier.height(1.dp))
                Text("Analyze Image")
            }
        }
    }
}

@Composable
private fun AiErrorCard(
    message: String,
    onDismiss: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        shape = RoundedCornerShape(22.dp),
        color = MaterialTheme.colorScheme.errorContainer
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = message,
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.onErrorContainer,
                style = MaterialTheme.typography.bodyMedium
            )
            TextButton(onClick = onDismiss) {
                Text("OK")
            }
        }
    }
}

@Composable
private fun ChatInputBar(
    value: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
    enabled: Boolean
) {
    Surface(
        tonalElevation = 3.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text("Ask anything about your notes...") },
                singleLine = true,
                enabled = enabled
            )
            Button(
                onClick = onSend,
                enabled = enabled && value.isNotBlank(),
                shape = RoundedCornerShape(18.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send")
            }
        }
    }
}
