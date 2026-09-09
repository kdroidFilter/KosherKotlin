package app.domain

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/**
 * A place the luach can be computed for. Deliberately a plain immutable value: the
 * `GeoLocation` it maps to is mutable and is only built inside the repository.
 */
@Immutable
data class City(
    val hebrewName: String,
    val latinName: String,
    val latitude: Double,
    val longitude: Double,
    val elevationMeters: Double,
    val timeZoneId: String,
    /**
     * Drives `JewishCalendar.inIsrael` (yom tov sheni shel galuyot). It is a fact about the
     * place, not a preference, so it travels with the city instead of the settings.
     */
    val inIsrael: Boolean,
)

val DefaultCities: ImmutableList<City> = persistentListOf(
    City("ירושלים", "JERUSALEM", 31.7683, 35.2137, 800.0, "Asia/Jerusalem", inIsrael = true),
    City("בני ברק", "BNEI BRAK", 32.0833, 34.8333, 30.0, "Asia/Jerusalem", inIsrael = true),
    City("אשדוד", "ASHDOD", 31.8044, 34.6553, 50.0, "Asia/Jerusalem", inIsrael = true),
    City("נתניה", "NETANYA", 32.3310, 34.8599, 14.0, "Asia/Jerusalem", inIsrael = true),
    City("תל אביב", "TEL AVIV", 32.0853, 34.7818, 5.0, "Asia/Jerusalem", inIsrael = true),
    City("חיפה", "HAIFA", 32.7940, 34.9896, 300.0, "Asia/Jerusalem", inIsrael = true),
    City("באר שבע", "BEER SHEVA", 31.2530, 34.7915, 260.0, "Asia/Jerusalem", inIsrael = true),
    City("צפת", "TZFAT", 32.9646, 35.4960, 900.0, "Asia/Jerusalem", inIsrael = true),
    City("ניו יורק", "NEW YORK", 40.7128, -74.0060, 10.0, "America/New_York", inIsrael = false),
    City("לונדון", "LONDON", 51.5072, -0.1276, 11.0, "Europe/London", inIsrael = false),
    City("פריז", "PARIS", 48.8566, 2.3522, 35.0, "Europe/Paris", inIsrael = false),
    City("אנטוורפן", "ANTWERP", 51.2194, 4.4025, 7.0, "Europe/Brussels", inIsrael = false),
)

/** The destinations of the side rail, in display order. [NOW] is the landing page. */
enum class LuachSection(val hebrewLabel: String) {
    NOW("עכשיו"),
    DAY("זמני היום"),
    PLACE("מקום"),
    MONTH("לוח החודש"),
    SHABBAT("שבת וחגים"),
    LIMUD("פרשה ודף יומי"),
    SETTINGS("הגדרות"),
}

/** Light/dark preference. SYSTEM defers to the host, which is the default. */
enum class ThemeMode(val hebrewLabel: String) {
    SYSTEM("מערכת"),
    LIGHT("בהיר"),
    DARK("כהה"),
}

enum class SunCalculator(val label: String) {
    NOAA("NOAA"),
    SUN_TIMES("SunTimes"),
}

@Immutable
data class LuachSettings(
    val useElevation: Boolean = false,
    val candleLightingOffset: Int = 18,
    val calculator: SunCalculator = SunCalculator.NOAA,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
)

/** One row of the זמני היום list. */
@Immutable
data class ZmanEntry(
    val name: String,
    val opinion: String,
    val time: String,
    val minuteOfDay: Int,
)

@Immutable
data class ZmanGroup(
    val label: String,
    val ordinal: String,
    val rows: ImmutableList<ZmanEntry>,
)

@Immutable
data class Pillar(val label: String, val time: String, val accent: Boolean)

@Immutable
data class NextZman(
    val name: String,
    val opinion: String,
    val time: String,
    val inLabel: String,
)

/** Everything the זמני היום screen and the hero need for one (city, date, settings). */
@Immutable
data class DaySnapshot(
    val hebrewDate: String,
    val gregorianDate: String,
    val parsha: String,
    val clock: String,
    val nowMinuteOfDay: Int,
    val sunriseMinuteOfDay: Int?,
    val sunsetMinuteOfDay: Int?,
    val sunriseLabel: String,
    val chatzosLabel: String,
    val sunsetLabel: String,
    val next: NextZman?,
    val pillars: ImmutableList<Pillar>,
    val groups: ImmutableList<ZmanGroup>,
)

@Immutable
data class MonthCell(
    val hebrewDay: String,
    val gregorianDay: String,
    val tag: String,
    val isToday: Boolean,
    val isShabbat: Boolean,
    val isFiller: Boolean,
)

@Immutable
data class MonthGrid(
    val title: String,
    val subtitle: String,
    val cells: ImmutableList<MonthCell>,
)

@Immutable
data class LabeledTime(val label: String, val value: String, val accent: Boolean)

@Immutable
data class HolidayEvent(
    val dayLabel: String,
    val weekday: String,
    val name: String,
    val detail: String,
    val times: ImmutableList<LabeledTime>,
    val highlighted: Boolean,
)

@Immutable
data class LimudCard(val kicker: String, val value: String, val note: String)

val HebrewWeekdays: ImmutableList<String> =
    persistentListOf("ראשון", "שני", "שלישי", "רביעי", "חמישי", "שישי", "שבת")
