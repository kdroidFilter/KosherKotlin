package itimlabina

import io.github.kdroidfilter.kosherkotlin.itimlabina.ItimLabinaCalendar
import io.github.kdroidfilter.kosherkotlin.util.GeoLocation
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlin.test.Test
import kotlin.test.assertTrue
import io.github.kdroidfilter.kosherkotlin.itimlabina.AtmosphereModels
import kotlin.math.abs

class ItimLabinaCalendarTest {

    private val jerusalem = GeoLocation("Jerusalem", 31.7784, 35.2354, 750.0, TimeZone.of("Asia/Jerusalem"))

    @Test
    fun dayIsOrdered() {
        for (day in listOf("2026-01-15", "2026-06-21", "2026-12-21")) {
            val c = ItimLabinaCalendar(jerusalem, LocalDate.parse(day))
            val order = listOf(
                c.chatzosLayla, c.alos90Degrees, c.alos72Degrees, c.misheyakir11Point5Degrees, c.sunriseFromElevation,
                c.sunriseMishor, c.sofZmanShmaGra, c.chatzos, c.minchaGedola, c.plagHamincha, c.sunsetMishor,
                c.sunsetFromElevation, c.tzeisGeonim18Minutes, c.tzeis72Degrees,
            )
            assertTrue(order.zipWithNext().all { (a, b) -> a < b }, "$day: $order")
        }
    }

    @Test
    fun horizonRefractionOfMenatsWinterAtmosphere() {
        // Ray tracing Dr. Menat's winter atmosphere: about 34.2' at sea level, thinner air higher up.
        val seaLevel = AtmosphereModels.MENAT_WINTER.refraction(0.0)
        assertTrue(abs(seaLevel - 0.5694) < 0.0005, "sea level $seaLevel")
        assertTrue(AtmosphereModels.MENAT_WINTER.refraction(1000.0) / seaLevel in 0.905..0.913)
    }
}
