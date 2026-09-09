package app.ui

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.scene.Scene
import androidx.navigation3.ui.NavDisplay
import app.domain.City
import app.domain.LuachSection
import app.domain.Pillar
import kotlinx.collections.immutable.ImmutableList
import app.ui.theme.LuachTheme

private val RailWidth = 244.dp

/** Below this the rail folds into a top bar. Desktop pins its minimum window width to it. */
internal val CompactBreakpoint = 900.dp

/**
 * A cross-fade with a few pixels of lift, the same in both directions.
 *
 * Deliberately slighter than the Navigation 3 default slide: the sections are peers off a rail,
 * not a drill-down, so a page that slid in from the side would imply a depth that is not there.
 */
private val SectionTransition:
    AnimatedContentTransitionScope<Scene<LuachSection>>.() -> ContentTransform = {
    fadeIn(tween(220)) + slideInVertically(tween(220)) { it / 28 } togetherWith fadeOut(tween(160))
}

/**
 * Pure rendering: immutable state in, intents out. No app dependencies, so it previews and
 * tests without building the Metro graph.
 */
@Composable
fun LuachScreen(
    state: LuachUiState,
    onIntent: (LuachIntent) -> Unit,
    backStack: SnapshotStateList<LuachSection>,
    modifier: Modifier = Modifier,
    topInset: Dp = 0.dp,
) {
    val colors = LuachTheme.colors
    // The rail reflects the back stack rather than owning a selection of its own.
    val section = backStack.last()
    val onSelect: (LuachSection) -> Unit = { backStack.goTo(it) }

    val page = remember(colors) {
        Brush.verticalGradient(listOf(colors.background, colors.backgroundEnd))
    }

    BoxWithConstraints(modifier.fillMaxSize().background(page)) {
        // ponytail: one breakpoint. The design is a desktop rail; below 900dp the rail
        // becomes a scrolling strip so phones stay usable.
        val compact = maxWidth < CompactBreakpoint

        if (compact) {
            // The nav strip is the only thing under the chrome here, so it absorbs the inset.
            Column(Modifier.fillMaxSize()) {
                CompactNav(section, onSelect, topInset)
                MainContent(state, onIntent, backStack, compact = true, modifier = Modifier.weight(1f))
            }
        } else {
            // The rail floats on the hero's sky rather than standing beside it: the content runs
            // the full width and only its text is inset, so the glow and the stars reach the edge.
            Box(Modifier.fillMaxSize()) {
                MainContent(state, onIntent, backStack, compact = false, topInset, RailWidth)
                NavRail(section, state.city, onSelect, topInset)
            }
        }
    }
}

/** One Navigation 3 entry per section, so each page owns its scroll state and transitions. */
@Composable
private fun MainContent(
    state: LuachUiState,
    onIntent: (LuachIntent) -> Unit,
    backStack: SnapshotStateList<LuachSection>,
    compact: Boolean,
    topInset: Dp = 0.dp,
    /** Width the floating rail covers, kept clear of text but not of the hero's sky. */
    railInset: Dp = 0.dp,
    modifier: Modifier = Modifier,
) {
    // Navigation 3 remembers the NavEntry list until the back stack itself changes, so whatever
    // an entry's content lambda captured is frozen at the last navigation. Read the arguments
    // through State instead, or a setting toggled on the page it lives on never repaints.
    val latestState by rememberUpdatedState(state)
    val latestCompact by rememberUpdatedState(compact)
    val latestTopInset by rememberUpdatedState(topInset)
    val latestRailInset by rememberUpdatedState(railInset)

    NavDisplay(
        backStack = backStack,
        modifier = modifier.fillMaxSize(),
        onBack = { backStack.goTo(LuachSection.NOW) },
        transitionSpec = SectionTransition,
        popTransitionSpec = SectionTransition,
    ) { key ->
        NavEntry(key) { section ->
            SectionPage(
                section, latestState, onIntent, latestCompact, latestTopInset, latestRailInset,
            )
        }
    }
}

@Composable
private fun SectionPage(
    section: LuachSection,
    state: LuachUiState,
    onIntent: (LuachIntent) -> Unit,
    compact: Boolean,
    topInset: Dp,
    railInset: Dp,
) {
    val horizontal = if (compact) 20.dp else 46.dp
    val listState = rememberLazyListState()

    Box(Modifier.fillMaxSize()) {
        // Only the hero draws under the rail; every other page starts where the rail ends.
        val start = if (section == LuachSection.NOW) 0.dp else railInset
        LazyColumn(Modifier.fillMaxSize().padding(start = start), state = listState) {
            if (section == LuachSection.NOW) {
                // The landing page is the hero and nothing else: it takes the whole window,
                // and only scrolls when the window is too short to hold it.
                item(key = "hero") {
                    Hero(
                        day = state.day,
                        compact = compact,
                        topInset = topInset,
                        startInset = railInset,
                        modifier = Modifier.fillParentMaxSize(),
                    )
                }
            } else {
                item(key = "page-header") {
                    PageHeader(section, state, horizontal, compact, topInset)
                }

                when (section) {
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
private fun PageHeader(
    section: LuachSection,
    state: LuachUiState,
    horizontal: Dp,
    compact: Boolean,
    topInset: Dp,
) {
    val colors = LuachTheme.colors
    val fonts = LuachTheme.fonts

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.surface)
            .padding(horizontal = horizontal)
            .padding(top = topInset + if (compact) 24.dp else 40.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(
            text = section.hebrewLabel,
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
private fun NavRail(
    section: LuachSection,
    city: City,
    onSelect: (LuachSection) -> Unit,
    topInset: Dp,
) {
    val colors = LuachTheme.colors
    val fonts = LuachTheme.fonts

    Column(
        modifier = Modifier
            .width(RailWidth)
            .fillMaxHeight()
            // A veil, not a wall: the sky keeps showing through the rail.
            .background(
                Brush.verticalGradient(
                    listOf(colors.railTop.copy(alpha = 0.10f), colors.railBottom.copy(alpha = 0.22f))
                )
            )
            .padding(horizontal = 20.dp, vertical = 30.dp)
            .padding(top = topInset),
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
        }

        Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
            LuachSection.entries.forEach { entry ->
                NavItem(
                    section = entry,
                    selected = entry == section,
                    onClick = { onSelect(entry) },
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
                    text = city.hebrewName,
                    fontFamily = fonts.display,
                    fontSize = 21.sp,
                    color = colors.railInk,
                )
            }
        }
    }
}

/** Selection wins over hover, so a selected item does not dim when the pointer is on it. */
@Composable
private fun navFill(selected: Boolean, hovered: Boolean): Color {
    val colors = LuachTheme.colors
    return when {
        selected -> colors.railActive
        hovered -> colors.railHover
        else -> Color.Transparent
    }
}

@Composable
private fun NavItem(section: LuachSection, selected: Boolean, onClick: () -> Unit) {
    val colors = LuachTheme.colors
    val fonts = LuachTheme.fonts
    val shape = RoundedCornerShape(9.dp)
    val interaction = remember { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            // Clip first, then fill: Material's own indication is a rectangle, so it drew hover
            // to the row's square bounds while selection used the rounded shape. Own the hover
            // instead — same shape as selection, and visible in both themes.
            .clip(shape)
            .background(navFill(selected, hovered))
            .clickable(interactionSource = interaction, indication = null, onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SectionIcon(section, if (selected) colors.gold else colors.railMuted)
        Spacer(Modifier.width(10.dp))
        Text(
            text = section.hebrewLabel,
            fontFamily = fonts.body,
            fontSize = 15.sp,
            color = if (selected) colors.railInk else colors.railMuted,
        )
    }
}
/**
 * Hairline glyphs for the rail, drawn rather than imported.
 *
 * ponytail: seven shapes in a `when` instead of a Material icons artifact in every bundle —
 * and drawn, they match the hero's hand-made sky rather than fighting it.
 */
@Composable
private fun SectionIcon(section: LuachSection, tint: Color, modifier: Modifier = Modifier) {
    Canvas(modifier.size(17.dp)) {
        val u = size.minDimension
        val sw = u * 0.085f
        val pen = Stroke(width = sw)
        fun at(x: Float, y: Float) = Offset(x * u, y * u)
        fun line(x1: Float, y1: Float, x2: Float, y2: Float) =
            drawLine(tint, at(x1, y1), at(x2, y2), sw, StrokeCap.Round)
        fun ring(x: Float, y: Float, r: Float) = drawCircle(tint, r * u, at(x, y), style = pen)
        fun dot(x: Float, y: Float, r: Float) = drawCircle(tint, r * u, at(x, y))

        when (section) {
            // A clock, for the hour that is now.
            LuachSection.NOW -> {
                ring(0.5f, 0.5f, 0.40f)
                line(0.5f, 0.5f, 0.5f, 0.28f)
                line(0.5f, 0.5f, 0.68f, 0.58f)
            }
            // A sun, for the zmanim of the day.
            LuachSection.DAY -> {
                ring(0.5f, 0.5f, 0.22f)
                line(0.5f, 0.06f, 0.5f, 0.16f)
                line(0.5f, 0.84f, 0.5f, 0.94f)
                line(0.06f, 0.5f, 0.16f, 0.5f)
                line(0.84f, 0.5f, 0.94f, 0.5f)
            }
            // A pin, for the place the luach is computed for.
            LuachSection.PLACE -> {
                ring(0.5f, 0.38f, 0.30f)
                line(0.28f, 0.58f, 0.5f, 0.94f)
                line(0.72f, 0.58f, 0.5f, 0.94f)
            }
            // A calendar leaf, for the month.
            LuachSection.MONTH -> {
                drawRoundRect(
                    tint,
                    topLeft = at(0.08f, 0.18f),
                    size = Size(0.84f * u, 0.74f * u),
                    cornerRadius = CornerRadius(0.14f * u),
                    style = pen,
                )
                line(0.08f, 0.40f, 0.92f, 0.40f)
                line(0.32f, 0.08f, 0.32f, 0.26f)
                line(0.68f, 0.08f, 0.68f, 0.26f)
            }
            // Two candles, for Shabbat and the festivals.
            LuachSection.SHABBAT -> {
                line(0.33f, 0.94f, 0.33f, 0.44f)
                line(0.67f, 0.94f, 0.67f, 0.44f)
                dot(0.33f, 0.24f, 0.10f)
                dot(0.67f, 0.24f, 0.10f)
            }
            // An open book, for the parsha and the daf.
            LuachSection.LIMUD -> {
                line(0.5f, 0.30f, 0.5f, 0.92f)
                line(0.08f, 0.20f, 0.08f, 0.82f)
                line(0.92f, 0.20f, 0.92f, 0.82f)
                line(0.08f, 0.20f, 0.5f, 0.30f)
                line(0.92f, 0.20f, 0.5f, 0.30f)
                line(0.08f, 0.82f, 0.5f, 0.92f)
                line(0.92f, 0.82f, 0.5f, 0.92f)
            }
            // Sliders, for the settings.
            LuachSection.SETTINGS -> {
                line(0.08f, 0.26f, 0.92f, 0.26f)
                line(0.08f, 0.5f, 0.92f, 0.5f)
                line(0.08f, 0.74f, 0.92f, 0.74f)
                dot(0.66f, 0.26f, 0.12f)
                dot(0.34f, 0.5f, 0.12f)
                dot(0.58f, 0.74f, 0.12f)
            }
        }
    }
}


@Composable
private fun CompactNav(section: LuachSection, onSelect: (LuachSection) -> Unit, topInset: Dp) {
    val colors = LuachTheme.colors
    val fonts = LuachTheme.fonts

    Column(
        Modifier
            .fillMaxWidth()
            .background(Brush.verticalGradient(listOf(colors.railTop, colors.railBottom)))
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .padding(top = topInset),
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
            LuachSection.entries.forEach { entry ->
                val selected = entry == section
                val interaction = remember { MutableInteractionSource() }
                val hovered by interaction.collectIsHoveredAsState()
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(9.dp))
                        .background(navFill(selected, hovered))
                        .clickable(interactionSource = interaction, indication = null) {
                            onSelect(entry)
                        }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    SectionIcon(entry, if (selected) colors.gold else colors.railMuted)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = entry.hebrewLabel,
                        fontFamily = fonts.body,
                        fontSize = 14.sp,
                        color = if (selected) colors.railInk else colors.railMuted,
                    )
                }
            }
        }
        LuachHorizontalScrollbar(
            scrollState = navScroll,
            color = colors.gold,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

