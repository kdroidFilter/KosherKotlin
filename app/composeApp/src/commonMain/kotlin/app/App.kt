package app

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import app.di.AppGraph
import app.domain.LuachSection
import app.domain.ThemeMode
import app.ui.LuachIntent
import app.ui.LuachScreen
import app.ui.LuachUiState
import app.ui.LuachViewModel
import app.ui.rememberLuachBackStack
import app.ui.theme.LuachTheme
import dev.zacsweers.metro.createGraph

/**
 * The one composable that knows about app wiring: it builds the Metro graph, gets the
 * ViewModel, collects state and hands plain state + callbacks to [LuachApp].
 */
@Composable
fun App(
    /**
     * Height of window chrome drawn *over* the app — the desktop title bar floats on the hero
     * instead of sitting above it, so backgrounds still reach the top edge and only the text
     * inside them moves down. Zero everywhere the platform owns its own chrome.
     */
    topInset: Dp = 0.dp,
    /**
     * The Navigation 3 back stack. Hoisted so the web build can hand in the one it has bound to
     * the browser's history; every other platform takes the default.
     */
    backStack: SnapshotStateList<LuachSection> = rememberLuachBackStack(),
) {
    val graph = remember { createGraph<AppGraph>() }
    val viewModel: LuachViewModel = viewModel { graph.luachViewModel }
    val state by viewModel.state.collectAsStateWithLifecycle()

    LuachApp(
        state = state,
        onIntent = viewModel::onIntent,
        backStack = backStack,
        topInset = topInset,
    )
}

/**
 * The themed screen with no wiring of its own. Desktop hosts this next to the widget window
 * so both share one [LuachViewModel]; every other platform goes through [App].
 */
@Composable
fun LuachApp(
    state: LuachUiState,
    onIntent: (LuachIntent) -> Unit,
    topInset: Dp = 0.dp,
    backStack: SnapshotStateList<LuachSection> = rememberLuachBackStack(),
) {
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
            LuachScreen(
                state = state,
                onIntent = onIntent,
                backStack = backStack,
                topInset = topInset,
            )
        }
    }
}
