package org.example.project.ai

sealed class AiError(message: String) : Exception(message) {
    class MissingApiKey : AiError("Gemini API key belum dikonfigurasi. Isi GEMINI_API_KEY di local.properties.")
    class Unauthorized : AiError("API key tidak valid atau tidak memiliki akses Gemini API.")
    class RateLimited : AiError("Request terlalu banyak. Coba lagi beberapa saat.")
    class Network : AiError("Tidak dapat terhubung ke AI API. Periksa koneksi internet.")
    class Server : AiError("Server AI sedang bermasalah. Coba lagi nanti.")
    class EmptyResponse : AiError("AI mengembalikan response kosong.")
    class Unknown(message: String) : AiError(message)
}
