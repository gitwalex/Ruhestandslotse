package com.gerwalex.retire

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    initKoin(
        extraModule = platformModule
    )
    Window(
        onCloseRequest = ::exitApplication,
        title = "Ruhestandslotse",
    ) {
        App()
    }
}