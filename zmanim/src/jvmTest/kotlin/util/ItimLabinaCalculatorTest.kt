package util

import io.github.kdroidfilter.kosherkotlin.ComplexZmanimCalendar
import io.github.kdroidfilter.kosherkotlin.util.AtmosphereModels
import io.github.kdroidfilter.kosherkotlin.util.GeoLocation
import io.github.kdroidfilter.kosherkotlin.util.ItimLabinaCalculator
import io.github.kdroidfilter.kosherkotlin.util.NOAACalculator
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ItimLabinaCalculatorTest {

    private val jerusalem = GeoLocation("Jerusalem", 31.7784, 35.2354, 750.0, TimeZone.of("Asia/Jerusalem"))

    @Test
    fun dayIsOrdered() {
        for (day in listOf("2026-01-15", "2026-06-21", "2026-12-21")) {
            val c = ComplexZmanimCalendar(location = jerusalem, date = LocalDate.parse(day), useElevation = true)
            c.astronomicalCalculator = ItimLabinaCalculator()
            val order = listOf(
                c.alos90ItimLabina, c.alos72ItimLabina, c.misheyakir11Point5Degrees, c.sunrise, c.seaLevelSunrise,
                c.sofZmanShmaGRA, c.chatzos, c.minchaGedola, c.plagHamincha, c.seaLevelSunset, c.sunset,
                c.tzaisGeonim18MinutesItimLabina, c.tzais72ItimLabina, c.solarMidnight,
            ).map {
                when (it) {
                    is io.github.kdroidfilter.kosherkotlin.Zman.DateBased -> it.momentOfOccurrence
                    else -> it as kotlin.time.Instant?
                }!!
            }
            assertTrue(order.zipWithNext().all { (a, b) -> a < b }, "$day: $order")
        }
    }

    @Test
    fun defaultHorizonIsTheFixedOne() {
        val noaa = NOAACalculator()
        val date = LocalDate(2026, 1, 15)
        assertEquals(noaa.adjustZenith(90.0, 750.0), noaa.horizonZenith(date, jerusalem, true, true), 1e-12)
        assertEquals(noaa.adjustZenith(90.0, 0.0), noaa.horizonZenith(date, jerusalem, false, false), 1e-12)
    }

    @Test
    fun horizonRefractionOfMenatsWinterAtmosphere() {
        // Ray tracing Dr. Menat's winter atmosphere: about 34.2' at sea level, thinner air higher up.
        val seaLevel = AtmosphereModels.MENAT_WINTER.refraction(0.0)
        assertTrue(abs(seaLevel - 0.5694) < 0.0005, "sea level $seaLevel")
        assertTrue(AtmosphereModels.MENAT_WINTER.refraction(1000.0) / seaLevel in 0.905..0.913)
    }
}
