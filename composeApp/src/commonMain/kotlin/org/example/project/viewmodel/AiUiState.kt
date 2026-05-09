package org.example.project.viewmodel

import org.example.project.ai.AiChatMessage

data class AiUiState(
    val messages: List<AiChatMessage> = emptyList(),
    val inputText: String = "",
    val imageInputText: String = "",
    val isLoading: Boolean = false,
    val isStreaming: Boolean = false,
    val errorMessage: String? = null,
    val toolResultTitle: String? = null,
    val toolResult: String? = null,
    val suggestedCategory: String? = null
) {
    val hasToolResult: Boolean
        get() = toolResultTitle != null && toolResult != null
}
