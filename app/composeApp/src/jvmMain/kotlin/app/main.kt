package app

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.rememberWindowState
import app.ui.theme.LuachTheme
import dev.nucleusframework.application.NucleusBackend
import dev.nucleusframework.application.nucleusApplication
import dev.nucleusframework.window.DecoratedWindowScope
import dev.nucleusframework.window.TitleBarPlacement
import dev.nucleusframework.window.macOSLargeCornerRadius
import dev.nucleusframework.window.newFullscreenControls
import dev.nucleusframework.window.WindowScaffold
import dev.nucleusframework.window.material.MaterialDecoratedWindow
import dev.nucleusframework.window.material.MaterialTitleBar
import dev.nucleusframework.window.styling.LocalTitleBarStyle

fun main(args: Array<String>) = nucleusApplication(args, backend = NucleusBackend.Tao) {
    // The window chrome is themed out here, outside App(), so it can only follow the OS theme —
    // the in-app light/dark override lives behind the settings store App() owns.
    val dark = isSystemInDarkTheme()

    // Themed twice on purpose: MaterialDecoratedWindow reads MaterialTheme at the call site to
    // colour the chrome, and Tao gives each window its own ComposeScene, so the locals provided
    // out here do not reach the content inside.
    LuachTheme(dark = dark) {
        MaterialDecoratedWindow(
            onCloseRequest = ::exitApplication,
            state = rememberWindowState(width = 1280.dp, height = 900.dp),
            title = "Luach",
            minimumSize = DpSize(420.dp, 640.dp),
        ) {
            LuachTheme(dark = dark) {
                WindowScaffold(
                    // Marker modifier: opts the window into the macOS 26 large corner radius.
                    modifier = Modifier.macOSLargeCornerRadius(),
                    titleBar = { LuachTitleBar() },
                    // Overlay, not Docked: the sky runs to the top edge of the window and the
                    // bar floats on it, instead of the app starting under a separate strip.
                    // The bar stays composed in fullscreen — auto-hiding it takes the traffic
                    // lights with it, and there is then no way out of fullscreen from the app.
                    titleBarPlacement = TitleBarPlacement.Overlay(autoHideInFullscreen = false),
                ) { chrome ->
                    App(topInset = chrome.calculateTopPadding())
                }
            }
        }
    }
}

/** Traffic lights and a drag area, on nothing: no plate, no divider, no title — just the sky. */
@Composable
private fun DecoratedWindowScope.LuachTitleBar() {
    val style = LocalTitleBarStyle.current

    MaterialTitleBar(
        // Read by the backend's TitleBar: the bar rides the menu bar in native fullscreen
        // instead of staying pinned to the layout. A tag only on Tao, which animates it itself.
        modifier = Modifier.newFullscreenControls(),
        style = style.copy(
            colors = style.colors.copy(
                background = Color.Transparent,
                inactiveBackground = Color.Transparent,
                border = Color.Transparent,
                content = LuachTheme.colors.heroInk,
            ),
        ),
    )
}
