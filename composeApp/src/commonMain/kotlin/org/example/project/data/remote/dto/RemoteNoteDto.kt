package org.example.project.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class RemoteNoteDto(
    val userId: Int = 1,
    val id: Long? = null,
    val title: String,
    val body: String
)

@Serializable
data class RemoteNoteRequest(
    val userId: Int = 1,
    val title: String,
    val body: String
)
