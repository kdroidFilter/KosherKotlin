package app.ui

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

// Android scrolls by touch and draws its own edge effects; a thumb would only add clutter.
@Composable
actual fun LuachVerticalScrollbar(listState: LazyListState, color: Color, modifier: Modifier) = Unit

@Composable
actual fun LuachHorizontalScrollbar(scrollState: ScrollState, color: Color, modifier: Modifier) = Unit
