package app

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import app.di.AppGraph
import app.ui.LuachScreen
import app.domain.ThemeMode
import app.ui.LuachViewModel
import app.ui.theme.LuachTheme
import dev.zacsweers.metro.createGraph

/**
 * The one composable that knows about app wiring: it builds the Metro graph, gets the
 * ViewModel, collects state and hands plain state + callbacks to [LuachScreen].
 */
@Composable
fun App(
    /**
     * Height of window chrome drawn *over* the app — the desktop title bar floats on the hero
     * instead of sitting above it, so backgrounds still reach the top edge and only the text
     * inside them moves down. Zero everywhere the platform owns its own chrome.
     */
    topInset: Dp = 0.dp,
) {
    val graph = remember { createGraph<AppGraph>() }
    val viewModel: LuachViewModel = viewModel { graph.luachViewModel }
    val state by viewModel.state.collectAsStateWithLifecycle()

    // SYSTEM is resolved here rather than in the ViewModel: the host theme is a Compose
    // ambient, and the ViewModel has no business reading it.
    val dark = when (state.settings.themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
    }

    LuachTheme(dark = dark) {
        // The whole luach is Hebrew, so the tree is RTL regardless of the host locale.
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            LuachScreen(state = state, onIntent = viewModel::onIntent, topInset = topInset)
        }
    }
}
