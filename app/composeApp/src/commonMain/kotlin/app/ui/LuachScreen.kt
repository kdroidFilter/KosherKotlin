package app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.domain.City
import app.domain.LuachSection
import app.domain.Pillar
import kotlinx.collections.immutable.ImmutableList
import app.ui.theme.LuachTheme

private val RailWidth = 244.dp
private val CompactBreakpoint = 900.dp

/**
 * Pure rendering: immutable state in, intents out. No app dependencies, so it previews and
 * tests without building the Metro graph.
 */
@Composable
fun LuachScreen(
    state: LuachUiState,
    onIntent: (LuachIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LuachTheme.colors

    BoxWithConstraints(modifier.fillMaxSize().background(colors.background)) {
        // ponytail: one breakpoint. The design is a desktop rail; below 900dp the rail
        // becomes a scrolling strip so phones stay usable.
        val compact = maxWidth < CompactBreakpoint

        if (compact) {
            Column(Modifier.fillMaxSize()) {
                CompactNav(state, onIntent)
                MainContent(state, onIntent, compact = true, modifier = Modifier.weight(1f))
            }
        } else {
            Row(Modifier.fillMaxSize()) {
                NavRail(state, onIntent)
                MainContent(state, onIntent, compact = false, modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun MainContent(
    state: LuachUiState,
    onIntent: (LuachIntent) -> Unit,
    compact: Boolean,
    modifier: Modifier = Modifier,
) {
    val horizontal = if (compact) 20.dp else 46.dp
    val listState = rememberLazyListState()

    // Switching sections replaces everything below the hero, so start the new one at the top
    // instead of stranding the reader past the end of a shorter section.
    LaunchedEffect(state.section) { listState.animateScrollToItem(0) }

    Box(modifier.fillMaxSize()) {
        LazyColumn(Modifier.fillMaxSize(), state = listState) {
            if (state.section == LuachSection.NOW) {
                // The landing page is the hero and nothing else: it takes the whole window,
                // and only scrolls when the window is too short to hold it.
                item(key = "hero") {
                    Hero(
                        day = state.day,
                        cityName = state.city.hebrewName,
                        sectionLabel = state.section.hebrewLabel,
                        compact = compact,
                        modifier = Modifier.fillParentMaxSize(),
                    )
                }
            } else {
                item(key = "page-header") {
                    PageHeader(state = state, horizontal = horizontal, compact = compact)
                }

                when (state.section) {
                    LuachSection.NOW -> Unit
                    LuachSection.DAY -> {
                        item(key = "pillars") { PillarRow(state.day.pillars, compact) }
                        daySection(state, horizontal)
                    }
                    LuachSection.PLACE -> placeSection(state, onIntent, horizontal)
                    LuachSection.MONTH -> monthSection(state, onIntent, horizontal, compact)
                    LuachSection.SHABBAT -> shabbatSection(state, horizontal)
                    LuachSection.LIMUD -> limudSection(state, horizontal, compact)
                    LuachSection.SETTINGS -> settingsSection(state, onIntent, horizontal)
                }

                item(key = "tail") { Spacer(Modifier.height(64.dp)) }
            }
        }

        LuachVerticalScrollbar(
            listState = listState,
            color = LuachTheme.colors.gold,
            modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight().padding(vertical = 4.dp),
        )
    }
}

/** Section identity for the pages that do not carry the hero. */
@Composable
private fun PageHeader(state: LuachUiState, horizontal: Dp, compact: Boolean) {
    val colors = LuachTheme.colors
    val fonts = LuachTheme.fonts
    val ordinal = (state.section.ordinal + 1).toString().padStart(2, '0')

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.surface)
            .padding(horizontal = horizontal)
            .padding(top = if (compact) 24.dp else 40.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(
            text = "$ordinal · ${state.city.hebrewName}",
            fontFamily = fonts.body,
            fontSize = 10.5.sp,
            letterSpacing = 3.sp,
            color = colors.gold,
        )
        Text(
            text = state.section.hebrewLabel,
            fontFamily = fonts.display,
            fontSize = if (compact) 34.sp else 46.sp,
            letterSpacing = (-1).sp,
            color = colors.ink,
        )
        Text(
            text = "${state.day.hebrewDate} · ${state.day.gregorianDate}",
            fontFamily = fonts.body,
            fontSize = 13.sp,
            color = colors.muted,
        )
        Box(
            Modifier
                .padding(top = 10.dp)
                .fillMaxWidth()
                .height(1.dp)
                .background(Brush.horizontalGradient(listOf(colors.gold, Color.Transparent)))
        )
    }
}

@Composable
private fun PillarRow(pillars: ImmutableList<Pillar>, compact: Boolean) {
    val colors = LuachTheme.colors
    val fonts = LuachTheme.fonts

    Row(
        Modifier
            .fillMaxWidth()
            .background(colors.surface)
            .drawBehind {
                drawLine(
                    color = colors.line,
                    start = Offset(0f, size.height),
                    end = Offset(size.width, size.height),
                    strokeWidth = 1.dp.toPx(),
                )
            }
    ) {
        pillars.forEach { pillar ->
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = if (compact) 14.dp else 24.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp),
            ) {
                Text(
                    text = pillar.label,
                    fontFamily = fonts.body,
                    fontSize = 10.sp,
                    letterSpacing = 2.2.sp,
                    color = colors.dim,
                )
                Text(
                    text = pillar.time,
                    fontFamily = fonts.display,
                    fontSize = if (compact) 21.sp else 27.sp,
                    letterSpacing = (-1).sp,
                    color = if (pillar.accent) colors.gold else colors.ink,
                )
            }
        }
    }
}

@Composable
private fun NavRail(state: LuachUiState, onIntent: (LuachIntent) -> Unit) {
    val colors = LuachTheme.colors
    val fonts = LuachTheme.fonts

    Column(
        modifier = Modifier
            .width(RailWidth)
            .fillMaxHeight()
            .background(Brush.verticalGradient(listOf(colors.railTop, colors.railBottom)))
            .padding(horizontal = 20.dp, vertical = 30.dp),
        verticalArrangement = Arrangement.spacedBy(30.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = "לוח זמנים",
                fontFamily = fonts.display,
                fontSize = 33.sp,
                letterSpacing = 1.sp,
                color = colors.railInk,
            )
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(
                        Brush.horizontalGradient(listOf(colors.gold, Color.Transparent))
                    )
            )
            Text(
                text = "KOSHERKOTLIN · KMP",
                fontFamily = fonts.body,
                fontSize = 10.sp,
                letterSpacing = 2.4.sp,
                color = colors.railDim,
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
            LuachSection.entries.forEachIndexed { index, section ->
                NavItem(
                    label = section.hebrewLabel,
                    ordinal = (index + 1).toString().padStart(2, '0'),
                    selected = section == state.section,
                    onClick = { onIntent(LuachIntent.SelectSection(section)) },
                )
            }
        }

        Spacer(Modifier.weight(1f))

        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Box(Modifier.fillMaxWidth().height(1.dp).background(colors.line))
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "מקום",
                    fontFamily = fonts.body,
                    fontSize = 10.sp,
                    letterSpacing = 2.2.sp,
                    color = colors.railDim,
                )
                Text(
                    text = state.city.hebrewName,
                    fontFamily = fonts.display,
                    fontSize = 21.sp,
                    color = colors.railInk,
                )
                Text(
                    text = state.city.coordinates(),
                    fontFamily = fonts.body,
                    fontSize = 11.5.sp,
                    color = colors.railMuted,
                )
            }
        }
    }
}

@Composable
private fun NavItem(label: String, ordinal: String, selected: Boolean, onClick: () -> Unit) {
    val colors = LuachTheme.colors
    val fonts = LuachTheme.fonts
    val shape = RoundedCornerShape(9.dp)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (selected) colors.railActive else Color.Transparent, shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            fontFamily = fonts.body,
            fontSize = 15.sp,
            color = if (selected) colors.railInk else colors.railMuted,
        )
        Text(
            text = ordinal,
            fontFamily = fonts.display,
            fontSize = 11.sp,
            letterSpacing = 1.sp,
            color = if (selected) colors.gold else colors.railDim,
        )
    }
}

@Composable
private fun CompactNav(state: LuachUiState, onIntent: (LuachIntent) -> Unit) {
    val colors = LuachTheme.colors
    val fonts = LuachTheme.fonts

    Column(
        Modifier
            .fillMaxWidth()
            .background(Brush.verticalGradient(listOf(colors.railTop, colors.railBottom)))
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = "לוח זמנים",
            fontFamily = fonts.display,
            fontSize = 24.sp,
            color = colors.railInk,
        )
        val navScroll = rememberScrollState()
        Row(
            modifier = Modifier.horizontalScroll(navScroll),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            LuachSection.entries.forEach { section ->
                val selected = section == state.section
                Text(
                    text = section.hebrewLabel,
                    fontFamily = fonts.body,
                    fontSize = 14.sp,
                    color = if (selected) colors.railInk else colors.railMuted,
                    modifier = Modifier
                        .background(
                            if (selected) colors.railActive else Color.Transparent,
                            RoundedCornerShape(9.dp),
                        )
                        .clickable { onIntent(LuachIntent.SelectSection(section)) }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                )
            }
        }
        LuachHorizontalScrollbar(
            scrollState = navScroll,
            color = colors.gold,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

internal fun City.coordinates(): String =
    "${latitude.round4()} / ${longitude.round4()}"

private fun Double.round4(): String {
    val scaled = kotlin.math.round(this * 10_000) / 10_000
    val text = scaled.toString()
    val dot = text.indexOf('.')
    if (dot < 0) return "$text.0000"
    return text.padEnd(dot + 5, '0').take(dot + 5)
}
