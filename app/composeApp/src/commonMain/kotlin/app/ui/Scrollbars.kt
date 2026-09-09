package app.ui

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

/**
 * Scrollbars for the pointer-driven targets.
 *
 * Compose ships them on every Skiko backend (desktop, web, iOS) but not on Android, where
 * touch scrolling plus the platform's own overscroll cues already tell you where you are.
 * The Android actual therefore draws nothing rather than reimplementing a thumb.
 */
@Composable
expect fun LuachVerticalScrollbar(
    listState: LazyListState,
    color: Color,
    modifier: Modifier = Modifier,
)

@Composable
expect fun LuachHorizontalScrollbar(
    scrollState: ScrollState,
    color: Color,
    modifier: Modifier = Modifier,
)
