import io.github.kdroidfilter.kosherkotlin.AstronomicalCalendar
import io.github.kdroidfilter.kosherkotlin.ComplexZmanimCalendar
// import io.github.kdroidfilter.kosherkotlin.ZmanDescriptionFormatter
import io.github.kdroidfilter.kosherkotlin.ZmanimCalendar
import io.github.kdroidfilter.kosherkotlin.hebrewcalendar.HebrewDateFormatter
import io.github.kdroidfilter.kosherkotlin.hebrewcalendar.JewishCalendar
import io.github.kdroidfilter.kosherkotlin.hebrewcalendar.YerushalmiYomiCalculator
import io.github.kdroidfilter.kosherkotlin.hebrewcalendar.YomiCalculator
import io.github.kdroidfilter.kosherkotlin.util.DateUtils
import io.github.kdroidfilter.kosherkotlin.util.GeoLocation
import io.github.kdroidfilter.kosherkotlin.util.SunTimesCalculator
import io.github.kdroidfilter.kosherkotlin.util.WeekFormat
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@OptIn(kotlin.time.ExperimentalTime::class)
fun main() {
    println("KosherKotlin terminal samples")
    println("------------------------------")

    // Shared location and tz
    val tzNY = TimeZone.of("America/New_York")
    val geo = GeoLocation(
        name = "New York, NY",
        latitude = 40.7128,
        longitude = -74.0060,
        elevation = 10.0,
        timeZone = tzNY
    )

    // 1) Tiniest example: sunrise today
    println("\n[1] Tiniest example: sunrise today in New York")
    val acTiny = AstronomicalCalendar(geo)
    val today = LocalDate(2025, 8, 18)
    acTiny.localDateTime = LocalDateTime(today, LocalTime(12, 0))
    val sunrise = acTiny.sunrise
    println("Sunrise: ${sunrise?.toLocalDateTime(tzNY)?.time}")

    // Skipping high-level ZmanimCalendar listing here to keep terminal sample compact.
    // See README and guides for full examples.

    // 4) Hebrew calendar in one minute
    println("\n[4] Hebrew calendar quick demo for Jerusalem time today")
    val tzIL = TimeZone.of("Asia/Jerusalem")
    val dateIL = today
    val jc = JewishCalendar(dateIL, isInIsrael = true)
    val hdf = HebrewDateFormatter()
    println(hdf.format(jc))
    println(hdf.formatParsha(jc))
    println(hdf.formatYomTov(jc))
    println(hdf.formatOmer(jc))

    // 5) AstronomicalCalendar quick start: sunrise/sunset and change calculator
    println("\n[5] AstronomicalCalendar sunrise/sunset and calculator swap")
    val ac = AstronomicalCalendar(geo)
    ac.localDateTime = LocalDateTime(today, LocalTime(12, 0))
    val acSunrise = ac.sunrise
    val acSunset = ac.sunset
    println("AC Sunrise: ${acSunrise?.toLocalDateTime(tzNY)?.time}")
    println("AC Sunset:  ${acSunset?.toLocalDateTime(tzNY)?.time}")
    // swap calculator
    ac.astronomicalCalculator = SunTimesCalculator()
    println("AC (SunTimes calc) Solar noon: ${ac.sunTransit?.toLocalDateTime(tzNY)?.time}")

    // 6) UTIL quick start: WeekFormat and Julian day
    println("\n[6] UTIL: week day names and Julian day for 2025-05-01")
    val sampleDate = LocalDate(2025, 5, 1)
    val longName = WeekFormat.long.format(sampleDate)
    val shortName = WeekFormat.short.format(sampleDate)
    val jd = DateUtils.getJulianDay(sampleDate)
    println("Weekday long=$longName short=$shortName | JD=$jd")

    // 7) Hebrew: Daf Yomi examples (may be null on Yom Kippur/Tisha B'Av)
    println("\n[7] Hebrew: Daf Yomi Bavli and Yerushalmi for today (Israel)")
    val dafBavli = YomiCalculator.getDafYomiBavli(jc)
    val dafYerushalmi = YerushalmiYomiCalculator.getDafYomiYerushalmi(jc)
    println("Daf Yomi Bavli: $dafBavli")
    println("Daf Yomi Yerushalmi: $dafYerushalmi")

    println("\nDone.")
}
