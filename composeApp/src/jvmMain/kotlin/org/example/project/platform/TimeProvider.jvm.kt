package org.example.project.platform

actual fun currentTimeMillis(): Long {
    return java.lang.System.currentTimeMillis()
}
