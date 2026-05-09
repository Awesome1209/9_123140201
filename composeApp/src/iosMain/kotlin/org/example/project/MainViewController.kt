package org.example.project

import androidx.compose.ui.window.ComposeUIViewController
import org.example.project.data.local.DatabaseDriverFactory

fun MainViewController() = ComposeUIViewController {
    App(databaseDriverFactory = DatabaseDriverFactory())
}
