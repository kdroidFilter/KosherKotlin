package app

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import java.awt.Dimension

fun main() = application {
    Window(
        title = "Luach",
        state = rememberWindowState(width = 1280.dp, height = 900.dp),
        onCloseRequest = ::exitApplication,
    ) {
        window.minimumSize = Dimension(420, 640)
        App()
    }
}