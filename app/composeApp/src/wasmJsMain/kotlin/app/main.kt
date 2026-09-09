package app

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import app.ui.rememberLuachBackStack
import kotlinx.browser.document

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    val body = document.body ?: return
    ComposeViewport(body) {
        appLoaded()
        val backStack = rememberLuachBackStack()
        LuachBrowserHistory(backStack)
        App(backStack = backStack)
    }
}

external fun appLoaded()
