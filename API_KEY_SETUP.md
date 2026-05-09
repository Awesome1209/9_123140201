# Gemini API Key Setup

Tugas 9 menggunakan Google Gemini API untuk fitur AI Assistant, summarize note, improve note, suggest category, dan image analysis prompt mode.

## 1. Buat API key

1. Buka Google AI Studio: https://aistudio.google.com
2. Login dengan akun Google.
3. Pilih **Get API key**.
4. Pilih **Create API key**.
5. Copy API key.

## 2. Buat `local.properties`

Di root project, duplikat file:

```text
local.properties.example
```

lalu rename menjadi:

```text
local.properties
```

Isi:

```properties
GEMINI_API_KEY=ISI_API_KEY_GEMINI_KAMU
```

Kalau Android Studio sudah membuat `local.properties` dengan `sdk.dir`, cukup tambahkan baris `GEMINI_API_KEY` di bawahnya.

## 3. Jangan commit API key

Pastikan `.gitignore` berisi:

```gitignore
local.properties
```

File `local.properties.example` boleh di-commit karena hanya berisi placeholder.

## 4. Build ulang

```powershell
.\gradlew clean
.\gradlew build --no-configuration-cache
```
