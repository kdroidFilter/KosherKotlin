package app.ui

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.domain.City
import app.domain.HebrewWeekdays
import app.domain.HolidayEvent
import app.domain.LimudCard
import app.domain.MonthCell
import kotlinx.collections.immutable.ImmutableList
import app.domain.SunCalculator
import app.domain.ThemeMode
import app.domain.ZmanEntry
import app.ui.theme.LuachTheme

// --- זמני היום --------------------------------------------------------------------------

fun LazyListScope.daySection(state: LuachUiState, horizontal: Dp) {
    val now = state.day.nowMinuteOfDay
    val next = state.day.next

    state.day.groups.forEach { group ->
        item(key = "head-${group.label}") {
            SectionHeader(
                ordinal = group.ordinal,
                label = group.label,
                trailing = "${group.rows.size} זמנים",
                horizontal = horizontal,
                topPadding = 34.dp,
            )
        }
        items(group.rows, key = { "${group.label}-${it.name}-${it.time}" }) { row ->
            ZmanRow(
                row = row,
                isNext = next != null && row.name == next.name && row.time == next.time,
                past = row.minuteOfDay <= now,
                minutesAway = row.minuteOfDay - now,
                horizontal = horizontal,
            )
        }
    }
}

@Composable
private fun ZmanRow(
    row: ZmanEntry,
    isNext: Boolean,
    past: Boolean,
    minutesAway: Int,
    horizontal: Dp,
) {
    val colors = LuachTheme.colors
    val fonts = LuachTheme.fonts
    val interaction = remember { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()
    val shift by animateDpAsState(if (hovered) (-4).dp else 0.dp, label = "rowShift")

    val foreground = when {
        isNext -> colors.gold
        past -> colors.muted
        else -> colors.ink
    }
    val relative = when {
        past -> "עבר"
        minutesAway in 1..89 -> "בעוד $minutesAway׳"
        else -> ""
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = horizontal, vertical = 2.dp)
            // Layout-phase read: hovering shifts the row without recomposing it.
            .offset { IntOffset(shift.roundToPx(), 0) }
            .clip(RoundedCornerShape(12.dp))
            .background(if (isNext) colors.goldSoft else Color.Transparent)
            .border(
                width = 1.dp,
                color = if (isNext) colors.gold else colors.line,
                shape = RoundedCornerShape(12.dp),
            )
            .hoverable(interaction)
            .drawBehind {
                if (isNext) {
                    drawRect(
                        color = colors.gold,
                        size = Size(3.dp.toPx(), size.height),
                        topLeft = Offset(0f, 0f),
                    )
                }
            }
            .padding(horizontal = 20.dp, vertical = 15.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Text(
            text = row.time,
            fontFamily = fonts.display,
            fontSize = 32.sp,
            letterSpacing = (-1.5).sp,
            color = foreground,
            modifier = Modifier.width(110.dp),
        )
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(row.name, fontFamily = fonts.body, fontSize = 16.5.sp, color = foreground)
            if (row.opinion.isNotBlank()) {
                Text(row.opinion, fontFamily = fonts.body, fontSize = 12.sp, color = colors.muted)
            }
        }
        Text(
            text = relative,
            fontFamily = fonts.body,
            fontSize = 10.5.sp,
            letterSpacing = 2.sp,
            color = if (isNext) colors.gold else colors.dim,
        )
    }
}

// --- מקום ------------------------------------------------------------------------------

fun LazyListScope.placeSection(
    state: LuachUiState,
    onIntent: (LuachIntent) -> Unit,
    horizontal: Dp,
) {
    item(key = "place-search") {
        CitySearchField(
            query = state.query,
            onQueryChange = { onIntent(LuachIntent.Search(it)) },
            modifier = Modifier.padding(horizontal = horizontal).padding(top = 40.dp),
        )
    }
    items(state.visibleCities, key = { it.latinName }) { city ->
        CityRow(
            city = city,
            selected = city == state.city,
            onClick = { onIntent(LuachIntent.SelectCity(city)) },
            horizontal = horizontal,
        )
    }
    item(key = "place-note") {
        val colors = LuachTheme.colors
        Text(
            text = "לכל מקום נשמרים שם, קו רוחב, קו אורך, גובה ואזור זמן. " +
                "הגובה משפיע על הנץ ועל השקיעה כאשר ההתחשבות בגובה פעילה.",
            fontFamily = LuachTheme.fonts.body,
            fontSize = 12.5.sp,
            lineHeight = 21.sp,
            color = colors.muted,
            modifier = Modifier.padding(horizontal = horizontal).padding(top = 16.dp).widthIn(max = 780.dp),
        )
    }
}

@Composable
private fun CitySearchField(query: String, onQueryChange: (String) -> Unit, modifier: Modifier = Modifier) {
    val colors = LuachTheme.colors
    val fonts = LuachTheme.fonts

    Box(
        modifier
            .widthIn(max = 780.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(11.dp))
            .background(colors.surface)
            .border(1.dp, colors.line, RoundedCornerShape(11.dp))
            .padding(horizontal = 18.dp, vertical = 15.dp),
    ) {
        BasicTextField(
            value = query,
            onValueChange = onQueryChange,
            singleLine = true,
            textStyle = TextStyle(fontFamily = fonts.body, fontSize = 15.5.sp, color = colors.ink),
            cursorBrush = SolidColor(colors.gold),
            modifier = Modifier.fillMaxWidth(),
        )
        if (query.isEmpty()) {
            Text("חיפוש עיר…", fontFamily = fonts.body, fontSize = 15.5.sp, color = colors.dim)
        }
    }
}

@Composable
private fun CityRow(city: City, selected: Boolean, onClick: () -> Unit, horizontal: Dp) {
    val colors = LuachTheme.colors
    val fonts = LuachTheme.fonts
    val highElevation = city.elevationMeters >= 500

    Row(
        modifier = Modifier
            .padding(horizontal = horizontal, vertical = 5.dp)
            .widthIn(max = 780.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (selected) colors.goldSoft else colors.surface)
            .border(
                width = 1.dp,
                color = if (selected) colors.gold else colors.line,
                shape = RoundedCornerShape(12.dp),
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = city.hebrewName,
                fontFamily = fonts.display,
                fontSize = 24.sp,
                color = if (selected) colors.gold else colors.ink,
            )
            Text(
                text = city.latinName,
                fontFamily = fonts.body,
                fontSize = 12.sp,
                letterSpacing = 1.4.sp,
                color = colors.muted,
            )
        }
        Text(
            text = city.coordinates(),
            fontFamily = fonts.body,
            fontSize = 12.sp,
            color = colors.muted,
        )
        Text(
            text = "${city.elevationMeters.toInt()} מ׳",
            fontFamily = fonts.display,
            fontSize = 18.sp,
            color = if (highElevation) colors.gold else colors.muted,
        )
    }
}

// --- לוח החודש --------------------------------------------------------------------------

fun LazyListScope.monthSection(
    state: LuachUiState,
    onIntent: (LuachIntent) -> Unit,
    horizontal: Dp,
    compact: Boolean,
) {
    item(key = "month-header") {
        val colors = LuachTheme.colors
        val fonts = LuachTheme.fonts
        FlowRow(
            modifier = Modifier.padding(horizontal = horizontal).padding(top = 40.dp, bottom = 20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            itemVerticalAlignment = Alignment.CenterVertically,
        ) {
            StepButton("›") { onIntent(LuachIntent.PreviousMonth) }
            Text(
                text = state.month.title,
                fontFamily = fonts.display,
                fontSize = 34.sp,
                color = colors.ink,
                modifier = Modifier.widthIn(min = if (compact) 0.dp else 230.dp),
            )
            StepButton("‹") { onIntent(LuachIntent.NextMonth) }
            Text(
                text = state.month.subtitle,
                fontFamily = fonts.body,
                fontSize = 12.5.sp,
                letterSpacing = 1.2.sp,
                color = colors.muted,
            )
        }
    }
    item(key = "month-grid") {
        MonthTable(state.month.cells, Modifier.padding(horizontal = horizontal))
    }
}

/** Below this the seven columns stop being readable, so the table scrolls instead of squeezing. */
private val MinMonthCellWidth = 96.dp

@Composable
private fun StepButton(glyph: String, onClick: () -> Unit) {
    val colors = LuachTheme.colors
    Box(
        modifier = Modifier
            .size(38.dp)
            .clip(RoundedCornerShape(9.dp))
            .border(1.dp, colors.line, RoundedCornerShape(9.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(glyph, fontFamily = LuachTheme.fonts.body, fontSize = 16.sp, color = colors.muted)
    }
}

@Composable
private fun MonthTable(cells: ImmutableList<MonthCell>, modifier: Modifier = Modifier) {
    val colors = LuachTheme.colors
    val fonts = LuachTheme.fonts

    BoxWithConstraints(modifier.fillMaxWidth()) {
        val cellWidth = maxOf(maxWidth / 7, MinMonthCellWidth)
        val tableWidth = cellWidth * 7
        val scrollable = tableWidth > maxWidth
        val scroll = rememberScrollState()

        Column(Modifier.fillMaxWidth()) {
            Box(if (scrollable) Modifier.horizontalScroll(scroll) else Modifier) {
                Column(
                    Modifier
                        .width(tableWidth)
                        .clip(RoundedCornerShape(16.dp))
                        .background(colors.surface)
                        .border(1.dp, colors.line, RoundedCornerShape(16.dp)),
                ) {
                    Row {
                        HebrewWeekdays.forEach { name ->
                            Text(
                                text = name,
                                fontFamily = fonts.body,
                                fontSize = 10.5.sp,
                                letterSpacing = 2.sp,
                                color = colors.dim,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.width(cellWidth).padding(vertical = 13.dp),
                            )
                        }
                    }
                    Box(Modifier.fillMaxWidth().height(1.dp).background(colors.line))

                    cells.chunked(7).forEach { week ->
                        Row {
                            week.forEach { cell -> MonthDayCell(cell, Modifier.width(cellWidth)) }
                        }
                    }
                }
            }
            if (scrollable) {
                LuachHorizontalScrollbar(
                    scrollState = scroll,
                    color = colors.gold,
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                )
            }
        }
    }
}

@Composable
private fun MonthDayCell(cell: MonthCell, modifier: Modifier = Modifier) {
    val colors = LuachTheme.colors
    val fonts = LuachTheme.fonts
    val background = when {
        cell.isFiller || cell.isShabbat -> colors.surfaceRaised
        cell.isToday -> colors.goldSoft
        else -> Color.Transparent
    }

    Column(
        modifier
            .heightIn(min = 96.dp)
            .background(background)
            .then(
                if (cell.isToday) Modifier.border(1.dp, colors.gold) else Modifier
            )
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        if (cell.isFiller) return@Column
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom,
        ) {
            Text(
                text = cell.hebrewDay,
                fontFamily = fonts.display,
                fontSize = 24.sp,
                color = if (cell.isToday) colors.gold else colors.ink,
            )
            Text(
                text = cell.gregorianDay,
                fontFamily = fonts.body,
                fontSize = 10.5.sp,
                color = colors.dim,
            )
        }
        if (cell.tag.isNotBlank()) {
            Text(
                text = cell.tag,
                fontFamily = fonts.body,
                fontSize = 11.sp,
                lineHeight = 15.sp,
                color = colors.gold,
            )
        }
    }
}

// --- שבת וחגים --------------------------------------------------------------------------

fun LazyListScope.shabbatSection(state: LuachUiState, horizontal: Dp) {
    item(key = "shabbat-top") { Spacer(Modifier.height(40.dp)) }
    items(state.events, key = { "${it.dayLabel}-${it.name}" }) { event ->
        EventRow(event, horizontal)
    }
}

@Composable
private fun EventRow(event: HolidayEvent, horizontal: Dp) {
    val colors = LuachTheme.colors
    val fonts = LuachTheme.fonts

    Row(
        modifier = Modifier
            .padding(horizontal = horizontal, vertical = 6.dp)
            .widthIn(max = 900.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(if (event.highlighted) colors.goldSoft else colors.surface)
            .border(
                width = 1.dp,
                color = if (event.highlighted) colors.gold else colors.line,
                shape = RoundedCornerShape(14.dp),
            )
            .padding(horizontal = 22.dp, vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        Column(Modifier.width(104.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = event.dayLabel,
                fontFamily = fonts.display,
                fontSize = 28.sp,
                letterSpacing = (-1).sp,
                color = colors.ink,
            )
            Text(
                text = event.weekday,
                fontFamily = fonts.body,
                fontSize = 10.5.sp,
                letterSpacing = 2.sp,
                color = colors.dim,
            )
        }
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
            Text(
                text = event.name,
                fontFamily = fonts.display,
                fontSize = 25.sp,
                color = if (event.highlighted) colors.gold else colors.ink,
            )
            Text(event.detail, fontFamily = fonts.body, fontSize = 12.5.sp, color = colors.muted)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(28.dp)) {
            event.times.forEach { time ->
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        text = time.label,
                        fontFamily = fonts.body,
                        fontSize = 10.sp,
                        letterSpacing = 2.sp,
                        color = colors.dim,
                    )
                    Text(
                        text = time.value,
                        fontFamily = fonts.display,
                        fontSize = 23.sp,
                        letterSpacing = (-0.5).sp,
                        color = if (time.accent) colors.gold else colors.ink,
                    )
                }
            }
        }
    }
}

// --- פרשה ודף יומי ----------------------------------------------------------------------

@OptIn(ExperimentalLayoutApi::class)
fun LazyListScope.limudSection(state: LuachUiState, horizontal: Dp, compact: Boolean) {
    item(key = "limud") {
        FlowRow(
            modifier = Modifier
                .padding(horizontal = horizontal)
                .padding(top = 40.dp)
                .widthIn(max = 940.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            maxItemsInEachRow = if (compact) 1 else 2,
        ) {
            state.limud.forEach { card ->
                LimudTile(card, Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun LimudTile(card: LimudCard, modifier: Modifier = Modifier) {
    val colors = LuachTheme.colors
    val fonts = LuachTheme.fonts

    Column(
        modifier
            .widthIn(min = 250.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(colors.surface)
            .border(1.dp, colors.line, RoundedCornerShape(16.dp))
            .drawBehind {
                drawRect(
                    brush = Brush.horizontalGradient(
                        listOf(Color.Transparent, colors.gold, Color.Transparent)
                    ),
                    size = Size(size.width, 1.dp.toPx()),
                )
            }
            .padding(horizontal = 24.dp, vertical = 26.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = card.kicker,
            fontFamily = fonts.body,
            fontSize = 10.sp,
            letterSpacing = 2.4.sp,
            color = colors.gold,
        )
        Text(
            text = card.value,
            fontFamily = fonts.display,
            fontSize = 34.sp,
            lineHeight = 38.sp,
            color = colors.ink,
        )
        Text(
            text = card.note,
            fontFamily = fonts.body,
            fontSize = 12.5.sp,
            lineHeight = 21.sp,
            color = colors.muted,
        )
    }
}

// --- הגדרות ----------------------------------------------------------------------------

fun LazyListScope.settingsSection(
    state: LuachUiState,
    onIntent: (LuachIntent) -> Unit,
    horizontal: Dp,
) {
    val settings = state.settings

    item(key = "settings-elevation") {
        SettingRow("שימוש בגובה", "התחשבות בגובה המקום בחישוב הנץ והשקיעה", horizontal, topPadding = 40.dp) {
            Segment("פעיל", settings.useElevation) { onIntent(LuachIntent.SetElevation(true)) }
            Segment("כבוי", !settings.useElevation) { onIntent(LuachIntent.SetElevation(false)) }
        }
    }
    item(key = "settings-candles") {
        SettingRow("הדלקת נרות", "מספר הדקות שלפני השקיעה", horizontal) {
            listOf(18, 20, 30, 40).forEach { minutes ->
                Segment("$minutes", settings.candleLightingOffset == minutes) {
                    onIntent(LuachIntent.SetCandleOffset(minutes))
                }
            }
        }
    }
    item(key = "settings-calculator") {
        SettingRow("מחשבון אסטרונומי", "שיטת החישוב של מיקום השמש", horizontal) {
            SunCalculator.entries.forEach { calculator ->
                Segment(calculator.label, settings.calculator == calculator) {
                    onIntent(LuachIntent.SetCalculator(calculator))
                }
            }
        }
    }
    item(key = "settings-theme") {
        SettingRow("ערכת נושא", "מצב כהה, בהיר, או לפי הגדרות המערכת", horizontal) {
            ThemeMode.entries.forEach { mode ->
                Segment(mode.hebrewLabel, settings.themeMode == mode) {
                    onIntent(LuachIntent.SetThemeMode(mode))
                }
            }
        }
    }
}

@Composable
private fun SettingRow(
    label: String,
    hint: String,
    horizontal: Dp,
    topPadding: Dp = 0.dp,
    options: @Composable RowScope.() -> Unit,
) {
    val colors = LuachTheme.colors
    val fonts = LuachTheme.fonts

    Column(Modifier.padding(horizontal = horizontal).padding(top = topPadding).widthIn(max = 760.dp)) {
        Row(
            Modifier.fillMaxWidth().padding(vertical = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text(label, fontFamily = fonts.display, fontSize = 20.sp, color = colors.ink)
                Text(hint, fontFamily = fonts.body, fontSize = 12.5.sp, color = colors.muted)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                options()
            }
        }
        Box(Modifier.fillMaxWidth().height(1.dp).background(colors.line))
    }
}

@Composable
private fun Segment(label: String, selected: Boolean, onClick: () -> Unit) {
    val colors = LuachTheme.colors
    val fonts = LuachTheme.fonts

    Text(
        text = label,
        fontFamily = fonts.body,
        fontSize = 13.sp,
        color = if (selected) colors.gold else colors.muted,
        modifier = Modifier
            .clip(RoundedCornerShape(9.dp))
            .background(if (selected) colors.goldSoft else Color.Transparent)
            .border(
                width = 1.dp,
                color = if (selected) colors.gold else colors.line,
                shape = RoundedCornerShape(9.dp),
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 15.dp, vertical = 9.dp),
    )
}

// --- shared ------------------------------------------------------------------------------

@Composable
internal fun SectionHeader(
    ordinal: String,
    label: String,
    trailing: String,
    horizontal: Dp,
    topPadding: Dp = 0.dp,
) {
    val colors = LuachTheme.colors
    val fonts = LuachTheme.fonts

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = horizontal)
            .padding(top = topPadding, bottom = 12.dp),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(ordinal, fontFamily = fonts.display, fontSize = 13.sp, letterSpacing = 2.sp, color = colors.gold)
        Text(label, fontFamily = fonts.display, fontSize = 26.sp, color = colors.ink)
        Box(
            Modifier
                .weight(1f)
                .height(1.dp)
                .background(Brush.horizontalGradient(listOf(colors.lineStrong, Color.Transparent)))
        )
        Text(
            text = trailing,
            fontFamily = fonts.body,
            fontSize = 11.sp,
            letterSpacing = 1.4.sp,
            color = colors.dim,
        )
    }
}
