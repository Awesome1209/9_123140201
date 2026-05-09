package org.example.project.ai

interface AiRepository {
    suspend fun chat(history: List<AiChatMessage>, userMessage: String): Result<String>
    suspend fun summarizeNote(title: String, content: String): Result<String>
    suspend fun improveNote(title: String, content: String): Result<String>
    suspend fun suggestCategory(title: String, content: String): Result<String>
    suspend fun analyzeImagePrompt(input: String): Result<String>
}

class AiRepositoryImpl(
    private val geminiService: GeminiService
) : AiRepository {
    override suspend fun chat(history: List<AiChatMessage>, userMessage: String): Result<String> {
        val contents = buildList {
            add(GeminiContent(parts = listOf(GeminiPart(SystemPrompts.notesAssistant)), role = "user"))
            history.forEach { message ->
                add(
                    GeminiContent(
                        parts = listOf(GeminiPart(message.text)),
                        role = if (message.isUser) "user" else "model"
                    )
                )
            }
            add(GeminiContent(parts = listOf(GeminiPart(userMessage)), role = "user"))
        }
        return geminiService.generateConversation(contents)
    }

    override suspend fun summarizeNote(title: String, content: String): Result<String> {
        return geminiService.generateContent(SystemPrompts.summarizeNote(title, content))
    }

    override suspend fun improveNote(title: String, content: String): Result<String> {
        return geminiService.generateContent(SystemPrompts.improveNote(title, content))
    }

    override suspend fun suggestCategory(title: String, content: String): Result<String> {
        return geminiService.generateContent(SystemPrompts.suggestCategory(title, content))
            .map { response -> response.lineSequence().firstOrNull().orEmpty().trim().take(32).ifBlank { "General" } }
    }

    override suspend fun analyzeImagePrompt(input: String): Result<String> {
        return geminiService.generateContent(SystemPrompts.imageAnalysis(input))
    }
}
