package app.ui

import androidx.compose.foundation.HorizontalScrollbar
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.ScrollbarStyle
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
actual fun LuachVerticalScrollbar(
    listState: LazyListState,
    color: Color,
    modifier: Modifier,
) {
    VerticalScrollbar(
        adapter = rememberScrollbarAdapter(listState),
        modifier = modifier,
        style = luachScrollbarStyle(color),
    )
}

@Composable
actual fun LuachHorizontalScrollbar(
    scrollState: ScrollState,
    color: Color,
    modifier: Modifier,
) {
    HorizontalScrollbar(
        adapter = rememberScrollbarAdapter(scrollState),
        modifier = modifier,
        style = luachScrollbarStyle(color),
    )
}

@Composable
private fun luachScrollbarStyle(color: Color) = remember(color) {
    ScrollbarStyle(
        minimalHeight = 24.dp,
        thickness = 8.dp,
        shape = RoundedCornerShape(4.dp),
        hoverDurationMillis = 300,
        unhoverColor = color.copy(alpha = 0.24f),
        hoverColor = color.copy(alpha = 0.60f),
    )
}
