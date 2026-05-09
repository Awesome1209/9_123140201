# 🤖 Tugas 9 - Smart Notes App with AI Assistant

<p align="center">
  <img src="https://img.shields.io/badge/Platform-Compose%20Multiplatform-blue?style=for-the-badge&logo=kotlin" alt="Platform">
  <img src="https://img.shields.io/badge/AI-Gemini%202.5%20Flash-purple?style=for-the-badge" alt="AI">
  <img src="https://img.shields.io/badge/Architecture-MVVM%20%2B%20Repository-green?style=for-the-badge" alt="Architecture">
  <img src="https://img.shields.io/badge/Course-Mobile%20Development-orange?style=for-the-badge" alt="Course">
</p>

## 👤 Informasi Mahasiswa

| Data Diri | Keterangan |
| :--- | :--- |
| **Nama** | Awi Septian Prasetyo |
| **NIM** | 123140201 |
| **Mata Kuliah** | Pengembangan Aplikasi Mobile (PAM) |
| **Program Studi** | Teknik Informatika |
| **Institusi** | Institut Teknologi Sumatera (ITERA) |

---

## 📖 Deskripsi Proyek

Project ini merupakan evolusi dari **Tugas 8 - Platform Specific Features** menjadi **Tugas 9 - Integrasi AI API**. Aplikasi tetap mempertahankan konsep utama sebagai **Notes App**, tetapi sekarang ditingkatkan menjadi **Smart Notes App with AI Assistant**.

Aplikasi tetap memiliki fitur local database, favorite notes, settings, platform dashboard, dan offline-first dari tugas sebelumnya. Pada Tugas 9, aplikasi ditambahkan integrasi **Google Gemini API** untuk fitur AI seperti chat assistant, note summarization, improve note, suggest category, dan image analysis prompt mode.

Bottom navigation aplikasi:

```text
Notes | Favorit | AI | Profile
```

---

## 🎯 Fitur Tugas 9

### ✅ Fitur AI Utama

* **AI Assistant**  
  Halaman chat AI untuk membantu user membuat ide, bertanya, merangkum, dan mengorganisasi catatan.

* **Gemini API Integration**  
  Menggunakan Google Gemini API dengan model `gemini-2.5-flash`.

* **Multi-turn Conversation**  
  Chat menyimpan history percakapan agar AI dapat memahami konteks pesan sebelumnya.

* **Summarize Note**  
  AI dapat merangkum isi note dari halaman detail catatan.

* **Improve Note**  
  AI dapat membantu merapikan dan memperbaiki isi catatan agar lebih mudah dibaca.

* **Suggest Category**  
  AI dapat menyarankan kategori note berdasarkan title dan content di halaman Add/Edit Note.

* **Image Analysis Prompt Mode**  
  User dapat memasukkan deskripsi atau URL gambar untuk dianalisis AI sebagai catatan.

* **Streaming-like Response**  
  Response AI ditampilkan bertahap dengan efek typing/streaming-like untuk pengalaman UI yang lebih interaktif.

* **Prompt Engineering**  
  Prompt dipisahkan dalam `SystemPrompts` agar AI menjawab sesuai konteks aplikasi notes, menggunakan Bahasa Indonesia, dan output lebih terstruktur.

* **Error Handling**  
  Menangani API key kosong, unauthorized, rate limit, server error, network error, dan response kosong.

---

## 🧠 AI Features Detail

| Fitur | Lokasi | Fungsi |
| :--- | :--- | :--- |
| AI Assistant | Tab AI | Chat dengan Gemini untuk membantu catatan. |
| Multi-turn Chat | Tab AI | Percakapan menyimpan konteks sebelumnya. |
| Summarize Note | Note Detail | Merangkum isi catatan. |
| Improve Note | Note Detail | Merapikan catatan menjadi lebih terstruktur. |
| Suggest Category | Add/Edit Note | Memberikan rekomendasi kategori note. |
| Image Analysis Prompt | Tab AI | Analisis gambar berdasarkan deskripsi atau URL. |
| Streaming-like Response | Tab AI | Menampilkan jawaban AI bertahap seperti typing. |

---

## 🔐 Setup Gemini API Key

Project membaca API key dari file `local.properties`. File ini **tidak boleh di-commit ke GitHub**.

1. Buka Google AI Studio: `https://aistudio.google.com`
2. Pilih **Get API key**.
3. Buat API key baru.
4. Duplikat file:

```text
local.properties.example
```

lalu rename menjadi:

```text
local.properties
```

5. Isi file `local.properties`:

```properties
GEMINI_API_KEY=ISI_API_KEY_GEMINI_KAMU
```

Jika Android Studio sudah membuat `local.properties` dengan `sdk.dir`, cukup tambahkan baris `GEMINI_API_KEY` di bawahnya.

Contoh:

```properties
sdk.dir=C\:\\Users\\awise\\AppData\\Local\\Android\\Sdk
GEMINI_API_KEY=ISI_API_KEY_GEMINI_KAMU
```

Pastikan `.gitignore` berisi:

```gitignore
local.properties
```

---

## 🚦 Alur Navigasi

```mermaid
graph LR
    Main[Bottom Navigation] --> Notes[Notes]
    Main --> Fav[Favorit]
    Main --> AI[AI Assistant]
    Main --> Profile[Profile]

    Notes --> Add[Add Note]
    Notes --> Detail[Note Detail]
    Detail --> Edit[Edit Note]
    Detail --> Summary[Summarize Note]
    Detail --> Improve[Improve Note]

    Add --> Suggest[Suggest Category]
    Edit --> Suggest
    AI --> Chat[Multi-turn Chat]
    AI --> Image[Image Analysis Prompt]
```

---

## 🧱 Arsitektur AI

```mermaid
graph TD
    UI[AI Screen / Note Detail / Add Edit] --> VM[AiViewModel]
    VM --> Repo[AiRepository]
    Repo --> Service[GeminiService]
    Service --> Client[Ktor HttpClient]
    Client --> API[Google Gemini API]
    API --> LLM[Gemini 2.5 Flash]
    LLM --> API
    API --> Client
    Client --> Service
    Service --> Repo
    Repo --> VM
    VM --> UI
```

### Layer AI

| Layer | File | Fungsi |
| :--- | :--- | :--- |
| Config | `ApiConfig.kt` | Membaca API key dari platform/local.properties. |
| Service | `GeminiService.kt` | Melakukan request ke Gemini API dengan Ktor. |
| Repository | `AiRepository.kt` | Menyediakan fungsi chat, summarize, improve, suggest category, image analysis. |
| Prompt | `SystemPrompts.kt` | Menyimpan prompt terstruktur. |
| ViewModel | `AiViewModel.kt` | Mengelola state UI, loading, error, history chat, dan streaming-like response. |
| UI | `AiAssistantScreen.kt` | Menampilkan chat, quick actions, image analysis, dan input. |

---

## 📂 Struktur Folder Tambahan Tugas 9

```text
composeApp/src/commonMain/kotlin/org/example/project/
├── ai/
│   ├── AiError.kt
│   ├── AiModels.kt
│   ├── AiRepository.kt
│   ├── GeminiService.kt
│   └── SystemPrompts.kt
│
├── config/
│   └── ApiConfig.kt
│
├── components/
│   ├── AiActionCard.kt
│   ├── ChatBubble.kt
│   └── TypingIndicator.kt
│
├── ui/screens/
│   └── AiAssistantScreen.kt
│
└── viewmodel/
    ├── AiUiState.kt
    └── AiViewModel.kt
```

Platform-specific config:

```text
composeApp/src/androidMain/kotlin/org/example/project/config/ApiConfig.android.kt
composeApp/src/iosMain/kotlin/org/example/project/config/ApiConfig.ios.kt
composeApp/src/jvmMain/kotlin/org/example/project/config/ApiConfig.jvm.kt
```

---

## 🛠️ Teknologi yang Digunakan

* Kotlin Multiplatform
* Compose Multiplatform
* Material 3
* SQLDelight
* Ktor Client
* Kotlinx Serialization
* Koin Dependency Injection
* Google Gemini API
* MVVM Architecture
* Repository Pattern
* Platform-specific expect/actual

---

## 📸 Screenshot Dokumentasi

| Notes Page | AI Assistant Page |
| :---: | :---: |
| <img width="720" height="1600" alt="Tugas9-NotesPage" src="ISI_LINK_SCREENSHOT_NOTES_PAGE" /> | <img width="720" height="1600" alt="Tugas9-AIAssistant" src="ISI_LINK_SCREENSHOT_AI_ASSISTANT" /> |
| Notes tetap menjadi fitur utama aplikasi. | Halaman AI Assistant dengan quick actions dan chat input. |

| Multi-turn Conversation | Summarize Note |
| :---: | :---: |
| <img width="720" height="1600" alt="Tugas9-MultiTurnChat" src="ISI_LINK_SCREENSHOT_MULTI_TURN" /> | <img width="720" height="1600" alt="Tugas9-SummarizeNote" src="ISI_LINK_SCREENSHOT_SUMMARIZE_NOTE" /> |
| Chat AI menyimpan konteks percakapan. | AI merangkum isi note dari halaman detail. |

| Suggest Category | Image Analysis Prompt |
| :---: | :---: |
| <img width="720" height="1600" alt="Tugas9-SuggestCategory" src="ISI_LINK_SCREENSHOT_SUGGEST_CATEGORY" /> | <img width="720" height="1600" alt="Tugas9-ImageAnalysis" src="ISI_LINK_SCREENSHOT_IMAGE_ANALYSIS" /> |
| AI menyarankan kategori pada Add/Edit Note. | AI menganalisis deskripsi atau URL gambar. |

| Error Handling | Profile Page |
| :---: | :---: |
| <img width="720" height="1600" alt="Tugas9-ErrorHandling" src="ISI_LINK_SCREENSHOT_ERROR_HANDLING" /> | <img width="720" height="1600" alt="Tugas9-Profile" src="ISI_LINK_SCREENSHOT_PROFILE" /> |
| Error API/key/network ditampilkan dengan jelas. | Fitur Tugas 8 tetap dipertahankan. |

---

## 🧪 Demo States

| State | Cara Demo |
| :--- | :--- |
| **Loading** | Kirim pesan di tab AI atau klik Summarize Note. |
| **Typing / Streaming-like** | AI response tampil bertahap setelah request sukses. |
| **Success** | AI membalas chat atau menghasilkan summary/category. |
| **Error API Key** | Kosongkan `GEMINI_API_KEY`. |
| **Network Error** | Matikan internet lalu kirim pesan AI. |
| **Multi-turn** | Kirim pertanyaan lanjutan setelah jawaban pertama. |
| **Image Analysis** | Isi deskripsi/URL gambar di tab AI lalu klik Analyze Image. |

---

## ▶️ Cara Menjalankan

1. Buka project di Android Studio.
2. Buat file `local.properties` dari `local.properties.example`.
3. Isi `GEMINI_API_KEY`.
4. Jalankan Gradle Sync.
5. Build:

```powershell
.\gradlew clean
.\gradlew build --no-configuration-cache
```

6. Run aplikasi di emulator/device.

---

## ✅ Checklist Ketentuan Tugas 9

| Ketentuan | Status |
| :--- | :---: |
| Integrasi AI API | ✅ |
| Gemini API / OpenAI API | ✅ Gemini API |
| Service layer | ✅ GeminiService |
| Repository pattern | ✅ AiRepository |
| Prompt Engineering | ✅ SystemPrompts |
| Proper Error Handling | ✅ AiError + safe mapping |
| Loading State | ✅ |
| UI Responsif | ✅ |
| Dokumentasi README | ✅ |
| Multi-turn Conversation Bonus | ✅ |
| Streaming Response Bonus | ✅ Streaming-like response |
| Image Analysis Bonus | ✅ Prompt mode |
| Fitur Tugas 8 tetap berjalan | ✅ |

---

## 📌 Catatan Keamanan

API key tidak ditanam langsung di source code. Gunakan `local.properties` untuk menyimpan key asli dan pastikan file tersebut tidak ikut ter-commit ke repository.

Jika API key pernah dibagikan secara publik, buat API key baru di Google AI Studio sebelum push repository final.
