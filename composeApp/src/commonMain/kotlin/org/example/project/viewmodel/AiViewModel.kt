package org.example.project.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.project.ai.AiChatMessage
import org.example.project.ai.AiRepository

class AiViewModel(
    private val aiRepository: AiRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        AiUiState(
            messages = listOf(
                AiChatMessage(
                    text = "Halo! Saya AI Assistant untuk membantu merangkum, merapikan, membuat ide, dan mengorganisasi catatan kamu.",
                    isUser = false
                )
            )
        )
    )
    val uiState: StateFlow<AiUiState> = _uiState.asStateFlow()

    fun onInputChange(value: String) {
        _uiState.update { it.copy(inputText = value) }
    }

    fun onImageInputChange(value: String) {
        _uiState.update { it.copy(imageInputText = value) }
    }

    fun sendCurrentMessage() {
        val message = _uiState.value.inputText.trim()
        if (message.isBlank() || _uiState.value.isLoading) return
        sendMessage(message)
    }

    fun sendQuickPrompt(prompt: String) {
        if (prompt.isBlank() || _uiState.value.isLoading) return
        sendMessage(prompt)
    }

    private fun sendMessage(message: String) {
        val currentHistory = _uiState.value.messages
        _uiState.update {
            it.copy(
                inputText = "",
                isLoading = true,
                isStreaming = false,
                errorMessage = null,
                messages = it.messages + AiChatMessage(text = message, isUser = true)
            )
        }

        viewModelScope.launch {
            aiRepository.chat(currentHistory, message)
                .onSuccess { response -> streamAssistantResponse(response) }
                .onFailure { error -> showError(error.message ?: "AI gagal memberikan jawaban.") }
        }
    }

    fun summarizeNote(title: String, content: String) {
        runTool(title = "AI Summary") {
            aiRepository.summarizeNote(title, content)
        }
    }

    fun improveNote(title: String, content: String) {
        runTool(title = "Improved Note") {
            aiRepository.improveNote(title, content)
        }
    }

    fun suggestCategory(title: String, content: String) {
        if (title.isBlank() && content.isBlank()) {
            showError("Isi title atau content terlebih dahulu sebelum meminta kategori.")
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null, suggestedCategory = null) }
        viewModelScope.launch {
            aiRepository.suggestCategory(title, content)
                .onSuccess { category ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            suggestedCategory = category,
                            toolResultTitle = "Suggested Category",
                            toolResult = "Kategori yang disarankan: $category"
                        )
                    }
                }
                .onFailure { error -> showError(error.message ?: "Gagal menyarankan kategori.") }
        }
    }

    fun analyzeImagePrompt() {
        val input = _uiState.value.imageInputText.trim()
        if (input.isBlank()) {
            showError("Masukkan deskripsi atau URL gambar terlebih dahulu.")
            return
        }
        runTool(title = "Image Analysis") {
            aiRepository.analyzeImagePrompt(input)
        }
    }

    fun clearToolResult() {
        _uiState.update { it.copy(toolResultTitle = null, toolResult = null, suggestedCategory = null, errorMessage = null) }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun clearChat() {
        _uiState.update {
            it.copy(
                messages = listOf(
                    AiChatMessage(
                        text = "Percakapan dibersihkan. Silakan mulai bertanya lagi tentang catatan kamu.",
                        isUser = false
                    )
                ),
                inputText = "",
                errorMessage = null
            )
        }
    }

    private fun runTool(
        title: String,
        block: suspend () -> Result<String>
    ) {
        _uiState.update {
            it.copy(
                isLoading = true,
                errorMessage = null,
                toolResultTitle = null,
                toolResult = null
            )
        }
        viewModelScope.launch {
            block()
                .onSuccess { result ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            toolResultTitle = title,
                            toolResult = result
                        )
                    }
                }
                .onFailure { error -> showError(error.message ?: "AI tool gagal diproses.") }
        }
    }

    private suspend fun streamAssistantResponse(fullResponse: String) {
        _uiState.update {
            it.copy(
                isLoading = false,
                isStreaming = true,
                messages = it.messages + AiChatMessage(text = "", isUser = false, isStreaming = true)
            )
        }

        val step = 18
        var index = 0
        while (index < fullResponse.length) {
            index = (index + step).coerceAtMost(fullResponse.length)
            val partial = fullResponse.take(index)
            _uiState.update { state ->
                state.copy(
                    messages = state.messages.dropLast(1) + AiChatMessage(
                        text = partial,
                        isUser = false,
                        isStreaming = index < fullResponse.length
                    )
                )
            }
            delay(24)
        }

        _uiState.update { state ->
            state.copy(
                isStreaming = false,
                messages = state.messages.dropLast(1) + AiChatMessage(fullResponse, isUser = false)
            )
        }
    }

    private fun showError(message: String) {
        _uiState.update {
            it.copy(
                isLoading = false,
                isStreaming = false,
                errorMessage = message
            )
        }
    }
}
