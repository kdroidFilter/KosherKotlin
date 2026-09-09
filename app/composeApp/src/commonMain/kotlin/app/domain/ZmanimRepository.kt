package app.domain

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import io.github.kdroidfilter.kosherkotlin.ComplexZmanimCalendar
import io.github.kdroidfilter.kosherkotlin.Zman
import io.github.kdroidfilter.kosherkotlin.hebrewcalendar.HebrewDateFormatter
import io.github.kdroidfilter.kosherkotlin.hebrewcalendar.JewishCalendar
import io.github.kdroidfilter.kosherkotlin.util.GeoLocation
import io.github.kdroidfilter.kosherkotlin.util.NOAACalculator
import io.github.kdroidfilter.kosherkotlin.util.SunTimesCalculator
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant

/**
 * Turns the mutable KosherKotlin calendars into flat immutable snapshots the UI can render.
 *
 * Every function here is pure and synchronous: same inputs, same output, and no scope of its
 * own — the caller (the ViewModel) owns the coroutine that runs it.
 */
@SingleIn(AppScope::class)
@Inject
class ZmanimRepository {

    private val hebrew = HebrewDateFormatter().apply {
        isHebrewFormat = true
        isUseGershGershayim = true
    }

    fun day(city: City, date: LocalDate, now: Instant, settings: LuachSettings): DaySnapshot {
        val zone = TimeZone.of(city.timeZoneId)
        val calendar = calendarFor(city, date, settings)
        val jewishCalendar = calendar.jewishCalendar

        // `Zman` properties rebuild on every access, so read each one exactly once.
        val alos = calendar.alos16Point1Degrees
        val chatzos = calendar.chatzos
        val tzais = calendar.tzaisGeonim3Point7Degrees
        // `elevationAdjustedSunrise/Sunset` are protected in ZmanimCalendar, so make the same
        // choice here: bare `sunrise`/`sunset` are always elevation-adjusted.
        val sunrise = calendar.horizonSunrise(settings)
        val sunset = calendar.horizonSunset(settings)
        val elevationNote =
            if (settings.useElevation) "גובה ${city.elevationMeters.trimNumber()} מ׳" else "גובה פני הים"

        val plan = listOf(
            row(NIGHT, calendar.solarMidnight),
            // alos120 is deprecated in the library as lechumra-only; MGA 72 is the standard pair
            // with the 16.1 degree opinion below.
            row(MORNING, calendar.alos72),
            row(MORNING, alos),
            row(MORNING, calendar.misheyakir11Point5Degrees),
            PlanRow(MORNING, "הנץ החמה", elevationNote, sunrise),
            row(MORNING, calendar.sofZmanShmaMGA72Minutes),
            row(MORNING, calendar.sofZmanShmaGRA),
            row(MORNING, calendar.sofZmanTfilaGRA),
            row(NOON, chatzos),
            row(NOON, calendar.minchaGedola),
            row(NOON, calendar.minchaKetana),
            row(EVENING, calendar.plagHamincha),
            row(EVENING, calendar.candleLighting, appliesToday = jewishCalendar.hasCandleLighting),
            PlanRow(EVENING, "שקיעה", elevationNote, sunset),
            row(EVENING, tzais),
            row(EVENING, calendar.tzais72),
        )

        val resolved = plan.mapNotNull { planRow ->
            if (!planRow.appliesToday) return@mapNotNull null
            val moment = planRow.moment ?: return@mapNotNull null
            planRow.group to ZmanEntry(
                name = planRow.name,
                opinion = planRow.opinion,
                time = moment.clock(zone),
                minuteOfDay = moment.minuteOfDay(zone),
            )
        }

        val nowMinute = now.minuteOfDay(zone)
        val byTime = resolved.map { it.second }.sortedBy { it.minuteOfDay }
        val next = byTime.firstOrNull { it.minuteOfDay > nowMinute } ?: byTime.firstOrNull()

        val groups = listOf(NIGHT, MORNING, NOON, EVENING).mapIndexedNotNull { index, label ->
            val rows = resolved.filter { it.first == label }.map { it.second }.sortedBy { it.minuteOfDay }
            if (rows.isEmpty()) null else ZmanGroup(
                label = label,
                ordinal = (index + 1).toString().padStart(2, '0'),
                rows = rows.toImmutableList(),
            )
        }

        return DaySnapshot(
            hebrewDate = hebrew.format(jewishCalendar),
            gregorianDate = date.hebrewGregorianLabel(),
            parsha = parshaLabel(date, city),
            clock = now.clock(zone),
            nowMinuteOfDay = nowMinute,
            sunriseMinuteOfDay = sunrise?.minuteOfDay(zone),
            sunsetMinuteOfDay = sunset?.minuteOfDay(zone),
            sunriseLabel = "הנץ ${sunrise.clockOr(zone)}",
            chatzosLabel = "חצות ${chatzos.clockOr(zone)}",
            sunsetLabel = "שקיעה ${sunset.clockOr(zone)}",
            next = next?.let {
                NextZman(it.name, it.opinion, it.time, countdown(nowMinute, it.minuteOfDay))
            },
            pillars = persistentListOf(
                Pillar("עלות השחר", alos.clockOr(zone), accent = false),
                Pillar("הנץ החמה", sunrise.clockOr(zone), accent = false),
                Pillar("שקיעה", sunset.clockOr(zone), accent = true),
                Pillar("צאת הכוכבים", tzais.clockOr(zone), accent = false),
            ),
            groups = groups.toImmutableList(),
        )
    }

    /** The Hebrew month grid, [monthOffset] months away from the month containing [today]. */
    fun month(today: LocalDate, monthOffset: Int, city: City): MonthGrid {
        val anchor = JewishCalendar(today).apply {
            inIsrael = city.inIsrael
            setJewishDate(jewishYear, jewishMonth, 1)
        }
        if (monthOffset > 0) {
            anchor.forward(DateTimeUnit.MONTH, monthOffset)
        } else {
            repeat(-monthOffset) {
                anchor.back() // lands on the last day of the previous month
                anchor.setJewishDate(anchor.jewishYear, anchor.jewishMonth, 1)
            }
        }

        val firstGregorian = anchor.gregorianLocalDate
        val length = anchor.daysInJewishMonth
        val filler = MonthCell("", "", "", isToday = false, isShabbat = false, isFiller = true)

        val cells = buildList {
            repeat(firstGregorian.sundayFirstColumn()) { add(filler) }
            for (offset in 0 until length) {
                val gregorian = firstGregorian.plus(offset, DateTimeUnit.DAY)
                val day = JewishCalendar(gregorian).apply { inIsrael = city.inIsrael }
                val isToday = gregorian == today
                add(
                    MonthCell(
                        hebrewDay = hebrew.formatHebrewNumber(day.jewishDayOfMonth),
                        gregorianDay = gregorian.shortLabel(),
                        tag = if (isToday) "היום" else day.tagLabel(hebrew),
                        isToday = isToday,
                        isShabbat = gregorian.dayOfWeek == DayOfWeek.SATURDAY,
                        isFiller = false,
                    )
                )
            }
            while (size % 7 != 0) add(filler)
        }

        val lastGregorian = firstGregorian.plus(length - 1, DateTimeUnit.DAY)
        return MonthGrid(
            title = "${hebrew.formatMonth(anchor)} ${hebrew.formatHebrewNumber(anchor.jewishYear)}",
            subtitle = "$length יום · ${firstGregorian.shortLabel()} – ${lastGregorian.shortLabel()}.${lastGregorian.year}",
            cells = cells.toImmutableList(),
        )
    }

    /** Upcoming Shabbatot, festivals and fasts, scanning at most [horizonDays] days ahead. */
    fun events(
        city: City,
        today: LocalDate,
        settings: LuachSettings,
        horizonDays: Int = 60,
        limit: Int = 8,
    ): ImmutableList<HolidayEvent> {
        val zone = TimeZone.of(city.timeZoneId)
        val found = mutableListOf<HolidayEvent>()

        for (offset in 0..horizonDays) {
            if (found.size >= limit) break
            val date = today.plus(offset, DateTimeUnit.DAY)
            val day = JewishCalendar(date).apply { inIsrael = city.inIsrael }
            val isShabbat = date.dayOfWeek == DayOfWeek.SATURDAY
            if (!day.isYomTov && !day.isErevYomTov && !day.isTaanis && !isShabbat) continue

            val calendar = calendarFor(city, date, settings)
            val times = when {
                day.isTaanis && !isShabbat -> persistentListOf(
                    LabeledTime("תחילת הצום", calendar.alos16Point1Degrees.clockOr(zone), accent = false),
                    LabeledTime("סיום הצום", calendar.tzaisGeonim3Point7Degrees.clockOr(zone), accent = false),
                )

                day.hasCandleLighting -> persistentListOf(
                    LabeledTime("הדלקת נרות", calendar.candleLighting.clockOr(zone), accent = true),
                    LabeledTime("שקיעה", calendar.horizonSunset(settings).clockOr(zone), accent = false),
                )

                else -> persistentListOf(
                    LabeledTime("צאת הכוכבים", calendar.tzaisGeonim3Point7Degrees.clockOr(zone), accent = false),
                    LabeledTime("רבנו תם", calendar.tzais72.clockOr(zone), accent = false),
                )
            }

            found += HolidayEvent(
                dayLabel = date.shortLabel(),
                weekday = HebrewWeekdays[date.sundayFirstColumn()],
                name = day.eventName(hebrew, isShabbat),
                detail = "${hebrew.format(day)} · ${day.eventDetail(hebrew)}",
                times = times,
                highlighted = found.isEmpty(),
            )
        }
        return found.toImmutableList()
    }

    fun limud(today: LocalDate, city: City): ImmutableList<LimudCard> {
        val day = JewishCalendar(today).apply { inIsrael = city.inIsrael }
        val bavli = day.dafYomiBavli

        // The interesting molad is the one that announces the *coming* month. Name the card
        // after that month rather than after `molad`'s own date, which lands in the month before.
        val comingMonth = JewishCalendar(today).apply {
            inIsrael = city.inIsrael
            setJewishDate(jewishYear, jewishMonth, 1)
            forward(DateTimeUnit.MONTH, 1)
        }
        val molad = comingMonth.molad

        return persistentListOf(
            LimudCard(
                kicker = "פרשת השבוע",
                value = parshaLabel(today, city),
                note = "לפי מחזור קריאת התורה השנתי",
            ),
            LimudCard(
                kicker = "דף יומי · בבלי",
                value = bavli?.let { hebrew.formatDafYomiBavli(it) } ?: "—",
                note = "מחזור הדף היומי · תלמוד בבלי",
            ),
            LimudCard(
                kicker = "דף יומי · ירושלמי",
                value = hebrew.formatDafYomiYerushalmi(day.dafYomiYerushalmi).ifBlank { "—" },
                note = "מחזור הדף היומי · תלמוד ירושלמי",
            ),
            LimudCard(
                kicker = "מולד ${hebrew.formatMonth(comingMonth)}",
                value = "${molad.moladHours}:${molad.moladMinutes.pad()} · ${molad.moladChalakim.chalakim()}",
                note = "רגע כניסת החודש הבא",
            ),
        )
    }

    /**
     * `parshah` is only set on a Shabbos that actually has one, so ask the library which parsha
     * is coming (it skips Shabbosos swallowed by Yom Tov) and then walk to the Shabbos that
     * carries it, because `formatParsha` only ever reads a calendar's own `parshah`.
     *
     * The walk needs real headroom: in Tishrei, Rosh Hashana, Yom Kippur and Succos can take
     * three Shabbosos in a row before a parsha is read again.
     */
    private fun parshaLabel(date: LocalDate, city: City): String {
        val upcoming = JewishCalendar(date).apply { inIsrael = city.inIsrael }.upcomingParshah
        if (upcoming == JewishCalendar.Parsha.NONE) return "—"

        for (offset in 0..PARSHA_SEARCH_DAYS) {
            val day = JewishCalendar(date.plus(offset, DateTimeUnit.DAY))
                .apply { inIsrael = city.inIsrael }
            if (day.parshah == upcoming) {
                return hebrew.formatParsha(day)?.takeIf { it.isNotBlank() }?.let { "פרשת $it" } ?: "—"
            }
        }
        return "—"
    }

    private fun calendarFor(city: City, date: LocalDate, settings: LuachSettings) =
        ComplexZmanimCalendar(
            location = GeoLocation(
                city.latinName,
                city.latitude,
                city.longitude,
                city.elevationMeters,
                TimeZone.of(city.timeZoneId),
            ),
            date = date,
            useElevation = settings.useElevation,
            candleLightingOffset = settings.candleLightingOffset.toDouble(),
        ).apply {
            astronomicalCalculator = when (settings.calculator) {
                SunCalculator.NOAA -> NOAACalculator()
                SunCalculator.SUN_TIMES -> SunTimesCalculator()
            }
            jewishCalendar.inIsrael = city.inIsrael
        }

    private companion object {
        /** Tishrei can hide three parsha-less Shabbosos in a row. */
        const val PARSHA_SEARCH_DAYS = 40

        const val NIGHT = "לילה"
        const val MORNING = "בוקר"
        const val NOON = "צהריים"
        const val EVENING = "ערב"
    }
}

/** One planned line of the day list, before it is known whether the zman occurs at all. */
private class PlanRow(
    val group: String,
    val name: String,
    val opinion: String,
    val moment: Instant?,
    /**
     * Some zmanim are computable every day but only *apply* on some days — candle lighting is
     * 18 minutes before sunset every evening, yet it is only a zman on erev Shabbos/Yom Tov.
     * Rows that do not apply are dropped, so they can never become "the next zman".
     */
    val appliesToday: Boolean = true,
)

private fun row(group: String, zman: Zman.DateBased, appliesToday: Boolean = true) = PlanRow(
    group = group,
    name = zman.definition.type.friendlyNameHebrew.trim(),
    opinion = zman.definition.hebrewOpinion(),
    moment = zman.momentOfOccurrence,
    appliesToday = appliesToday,
)

/** Mirrors the library's protected `elevationAdjustedSunrise`. */
private fun ComplexZmanimCalendar.horizonSunrise(settings: LuachSettings) =
    if (settings.useElevation) sunrise else seaLevelSunrise

/** Mirrors the library's protected `elevationAdjustedSunset`. */
private fun ComplexZmanimCalendar.horizonSunset(settings: LuachSettings) =
    if (settings.useElevation) sunset else seaLevelSunset

private fun Zman.DateBased.clockOr(zone: TimeZone): String = momentOfOccurrence.clockOr(zone)

private fun Instant?.clockOr(zone: TimeZone): String = this?.clock(zone) ?: "--:--"

private fun Instant.minuteOfDay(zone: TimeZone): Int =
    toLocalDateTime(zone).time.let { it.hour * 60 + it.minute }

private fun Instant.clock(zone: TimeZone): String =
    toLocalDateTime(zone).time.let { "${it.hour.pad()}:${it.minute.pad()}" }

private fun Int.pad(): String = toString().padStart(2, '0')

private fun Int.chalakim(): String = if (this == 1) "חלק אחד" else "$this חלקים"

private const val MINUTES_PER_DAY = 24 * 60

private fun countdown(nowMinute: Int, targetMinute: Int): String {
    val delta = ((targetMinute - nowMinute) + MINUTES_PER_DAY) % MINUTES_PER_DAY
    return "${delta / 60}:${(delta % 60).pad()}"
}

/** Sunday is column 0 in a Hebrew calendar grid; `DayOfWeek.ordinal` is Monday-based. */
private fun LocalDate.sundayFirstColumn(): Int = (dayOfWeek.ordinal + 1) % 7

private fun JewishCalendar.eventName(formatter: HebrewDateFormatter, isShabbat: Boolean): String {
    val yomTov = formatter.formatYomTov(this)
    if (yomTov.isNotBlank()) return yomTov
    if (isShabbat) {
        val parsha = formatter.formatParsha(this)
        return if (parsha.isNullOrBlank()) "שבת" else "שבת $parsha"
    }
    return formatter.formatRoshChodesh(this).ifBlank { "—" }
}

private fun JewishCalendar.eventDetail(formatter: HebrewDateFormatter): String = when {
    isTaanis -> "תענית"
    isYomTovAssurBemelacha -> "יום טוב"
    isCholHamoed -> "חול המועד"
    isErevYomTov -> "ערב יום טוב"
    isRoshChodesh -> formatter.formatRoshChodesh(this).ifBlank { "ראש חודש" }
    else -> "שבת קודש"
}

private fun JewishCalendar.tagLabel(formatter: HebrewDateFormatter): String {
    formatter.formatYomTov(this).takeIf { it.isNotBlank() }?.let { return it }
    if (isRoshChodesh) return formatter.formatRoshChodesh(this).ifBlank { "ראש חודש" }
    return ""
}

private val HebrewGregorianMonths = listOf(
    "בינואר", "בפברואר", "במרץ", "באפריל", "במאי", "ביוני",
    "ביולי", "באוגוסט", "בספטמבר", "באוקטובר", "בנובמבר", "בדצמבר",
)

private fun LocalDate.hebrewGregorianLabel(): String =
    "יום ${HebrewWeekdays[sundayFirstColumn()]}, $day ${HebrewGregorianMonths[month.ordinal]} $year"

private fun LocalDate.shortLabel(): String = "$day.${month.ordinal + 1}"
