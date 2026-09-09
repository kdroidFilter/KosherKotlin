package app.domain

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/** The smallest checks that fail if the snapshot mapping or the settings plumbing breaks. */
class ZmanimRepositoryTest {

    private val repository = ZmanimRepository()
    private val jerusalem = DefaultCities.first()
    private val zone = TimeZone.of(jerusalem.timeZoneId)
    private val date = LocalDate(2026, 9, 9) // a plain Wednesday
    private val afternoon = LocalDateTime(2026, 9, 9, 14, 7).toInstant(zone)
    private val erevShabbat = LocalDate(2026, 9, 11)
    private val erevShabbatAfternoon = LocalDateTime(2026, 9, 11, 14, 7).toInstant(zone)

    @Test
    fun dayGroupsAreOrderedAndLabelled() {
        val day = repository.day(jerusalem, date, afternoon, LuachSettings())

        assertEquals(listOf("לילה", "בוקר", "צהריים", "ערב"), day.groups.map { it.label })
        assertEquals(listOf("01", "02", "03", "04"), day.groups.map { it.ordinal })
        day.groups.forEach { group ->
            assertTrue(group.rows.isNotEmpty(), "group ${group.label} is empty")
            assertEquals(
                group.rows.map { it.minuteOfDay }.sorted(),
                group.rows.map { it.minuteOfDay },
                "group ${group.label} is not sorted by time",
            )
        }
    }

    @Test
    fun nextZmanIsTheFirstOneStillAhead() {
        val day = repository.day(jerusalem, date, afternoon, LuachSettings())
        val next = assertNotNull(day.next)

        val allRows = day.groups.flatMap { it.rows }
        val expected = allRows.filter { it.minuteOfDay > day.nowMinuteOfDay }.minBy { it.minuteOfDay }
        assertEquals(expected.name, next.name)
        assertEquals(expected.time, next.time)
    }

    @Test
    fun candleLightingFollowsTheConfiguredOffset() {
        fun candleTime(offset: Int): Int {
            val day = repository.day(
                jerusalem, erevShabbat, erevShabbatAfternoon,
                LuachSettings(candleLightingOffset = offset),
            )
            return day.groups.flatMap { it.rows }.first { it.name == CANDLE_LIGHTING }.minuteOfDay
        }

        assertEquals(22, candleTime(18) - candleTime(40))
    }

    @Test
    fun candleLightingOnlyAppearsWhenItApplies() {
        fun names(on: kotlinx.datetime.LocalDate, at: kotlin.time.Instant) =
            repository.day(jerusalem, on, at, LuachSettings()).groups.flatMap { it.rows }.map { it.name }

        assertTrue(
            CANDLE_LIGHTING !in names(date, afternoon),
            "a plain Wednesday has no candle lighting",
        )
        assertTrue(
            CANDLE_LIGHTING in names(erevShabbat, erevShabbatAfternoon),
            "erev Shabbos must list candle lighting",
        )
    }

    @Test
    fun theNextZmanIsNeverOneThatDoesNotApplyToday() {
        val day = repository.day(jerusalem, date, afternoon, LuachSettings())
        assertTrue(day.next?.name != CANDLE_LIGHTING)
    }

    @Test
    fun monthGridIsWholeWeeksStartingOnSunday() {
        val grid = repository.month(date, monthOffset = 0, city = jerusalem)

        assertEquals(0, grid.cells.size % 7, "grid must be whole weeks")
        assertTrue(grid.title.isNotBlank())

        val realDays = grid.cells.filterNot { it.isFiller }
        assertTrue(realDays.size in 29..30, "a Hebrew month has 29 or 30 days, got ${realDays.size}")
        // Leading fillers only ever appear before the first real day.
        val firstReal = grid.cells.indexOfFirst { !it.isFiller }
        assertTrue(grid.cells.take(firstReal).all { it.isFiller })
    }

    @Test
    fun monthNavigationMovesToADifferentMonth() {
        val current = repository.month(date, 0, jerusalem)
        val next = repository.month(date, 1, jerusalem)
        val previous = repository.month(date, -1, jerusalem)

        assertTrue(current.title != next.title, "next month should differ")
        assertTrue(current.title != previous.title, "previous month should differ")
    }

    @Test
    fun elevationChangesSunriseInAHighCity() {
        fun sunrise(useElevation: Boolean) = repository
            .day(jerusalem, date, afternoon, LuachSettings(useElevation = useElevation))
            .pillars.first { it.label == "הנץ החמה" }.time

        // Jerusalem sits at 800 m, so honouring elevation must move sunrise earlier.
        assertTrue(sunrise(true) < sunrise(false), "elevation should pull sunrise earlier")
    }

    @Test
    fun yomTovSheniFollowsTheCityRatherThanASetting() {
        val diaspora = DefaultCities.first { !it.inIsrael }
        val succos = LocalDate(2026, 9, 27)

        fun names(city: City) = repository
            .events(city, succos, LuachSettings(), horizonDays = 12, limit = 20)
            .map { "${it.dayLabel} ${it.name}" }

        // Shemini Atzeres / Simchas Torah are one day in Israel and two in the diaspora, so the
        // festival list must differ purely because the city differs.
        assertTrue(
            names(jerusalem) != names(diaspora),
            "expected Israel and ${diaspora.latinName} to differ: ${names(jerusalem)} vs ${names(diaspora)}",
        )
    }

    @Test
    fun theParshaCardShowsTheComingShabbosAndAdmitsWhenThereIsNone() {
        fun parsha(on: LocalDate) =
            repository.limud(on, jerusalem).first { it.kicker == "פרשת השבוע" }.value

        // 2026-09-12 is Rosh Hashana on Shabbos: the reading is the festival's, not a parsha.
        assertEquals("אין פרשה השבוע", parsha(LocalDate(2026, 9, 12)))
        // Standing midweek before it, the answer is still "none" — not a parsha ten days out.
        assertEquals("אין פרשה השבוע", parsha(date))

        // A normal week resolves to the coming Shabbos, from any day of that week.
        val shabbosShuva = LocalDate(2026, 9, 19)
        assertEquals("פרשת האזינו", parsha(shabbosShuva))
        assertEquals(parsha(shabbosShuva), parsha(LocalDate(2026, 9, 16)))
    }

    @Test
    fun theMoladCardNamesTheComingMonth() {
        // 9 September 2026 is in Elul, so the molad being announced is Tishrei's.
        val molad = repository.limud(date, jerusalem).first { it.kicker.startsWith("מולד") }
        assertEquals("מולד תשרי", molad.kicker)
    }

    private companion object {
        const val CANDLE_LIGHTING = "הדלקת נרות"
    }
}
