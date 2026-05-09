package org.example.project.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import org.example.project.data.remote.dto.RemoteNoteDto
import org.example.project.data.remote.dto.RemoteNoteRequest

class RemoteNoteApi(
    private val client: HttpClient
) {
    private val baseUrl = "https://jsonplaceholder.typicode.com"

    suspend fun fetchRemoteNotes(limit: Int = 10): List<RemoteNoteDto> {
        return client.get("$baseUrl/posts").body<List<RemoteNoteDto>>().take(limit)
    }

    suspend fun createNote(title: String, content: String): RemoteNoteDto {
        return client.post("$baseUrl/posts") {
            contentType(ContentType.Application.Json)
            setBody(RemoteNoteRequest(title = title, body = content))
        }.body()
    }

    suspend fun updateNote(remoteId: Long, title: String, content: String): RemoteNoteDto {
        return client.put("$baseUrl/posts/$remoteId") {
            contentType(ContentType.Application.Json)
            setBody(RemoteNoteRequest(title = title, body = content))
        }.body()
    }

    suspend fun deleteNote(remoteId: Long): Boolean {
        val response = client.delete("$baseUrl/posts/$remoteId")
        return response.status.isSuccess()
    }
}
