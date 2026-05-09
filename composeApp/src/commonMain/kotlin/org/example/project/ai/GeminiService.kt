package org.example.project.ai

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import org.example.project.config.ApiConfig

class GeminiService(
    private val client: HttpClient
) {
    private val baseUrl = "https://generativelanguage.googleapis.com/v1beta"
    private val model = "gemini-2.5-flash"

    suspend fun generateContent(prompt: String): Result<String> {
        return generateConversation(
            contents = listOf(
                GeminiContent(parts = listOf(GeminiPart(text = prompt)), role = "user")
            )
        )
    }

    suspend fun generateConversation(contents: List<GeminiContent>): Result<String> = runCatching {
        val apiKey = ApiConfig.geminiApiKey.trim()
        if (apiKey.isBlank() || apiKey == "YOUR_GEMINI_API_KEY_HERE") {
            throw AiError.MissingApiKey()
        }

        val response: GeminiResponse = client.post("$baseUrl/models/$model:generateContent") {
            contentType(ContentType.Application.Json)
            parameter("key", apiKey)
            setBody(
                GeminiRequest(
                    contents = contents,
                    generationConfig = GeminiGenerationConfig(
                        temperature = 0.7,
                        maxOutputTokens = 1200,
                        topP = 0.95
                    )
                )
            )
        }.body()

        response.candidates
            .firstOrNull()
            ?.content
            ?.parts
            ?.firstOrNull()
            ?.text
            ?.takeIf { it.isNotBlank() }
            ?: throw AiError.EmptyResponse()
    }.recoverCatching { error ->
        throw mapError(error)
    }

    private fun mapError(error: Throwable): Throwable {
        if (error is AiError) return error

        return when (error) {
            is ClientRequestException -> when (error.response.status.value) {
                401, 403 -> AiError.Unauthorized()
                429 -> AiError.RateLimited()
                else -> AiError.Unknown("Request AI tidak valid: ${error.response.status.value}")
            }
            is ServerResponseException -> AiError.Server()
            else -> AiError.Network()
        }
    }
}
