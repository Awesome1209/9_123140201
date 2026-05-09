package org.example.project

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import org.example.project.data.local.DatabaseDriverFactory

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Tugas 8 Platform Notes App",
    ) {
        App(databaseDriverFactory = DatabaseDriverFactory())
    }
}
