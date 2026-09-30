import io.github.kdroidfilter.kosherkotlin.ComplexZmanimCalendar
import io.github.kdroidfilter.kosherkotlin.Zman
import io.github.kdroidfilter.kosherkotlin.util.GeoLocation
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Instant

/** The zmanim of the לוח המאור אור החיים, the luach Rabbi Ovadia Yosef used. */
class OhrHaChaimTest {

    private val bneiBrak = GeoLocation("Bnei Brak", 32.0853, 34.8338, 40.0, TimeZone.of("Asia/Jerusalem"))

    private fun calendar(day: String) =
        ComplexZmanimCalendar(location = bneiBrak, date = LocalDate.parse(day), useElevation = true)

    private val Zman.DateBased.at: Instant get() = momentOfOccurrence!!

    @Test
    fun zmanimAreFractionsOfTheGraDay() {
        for (day in listOf("2026-01-15", "2026-06-21", "2026-12-21")) {
            val c = calendar(day)
            val shaah = c.shaahZmanisGra.duration.inWholeMilliseconds
            val sunrise = c.sunrise!!.toEpochMilliseconds()
            val sunset = c.sunset!!.toEpochMilliseconds()

            assertEquals(sunrise - (1.1 * shaah).toLong(), c.misheyakir66MinutesZmanis.at.toEpochMilliseconds(), day)
            assertEquals(sunrise - shaah, c.misheyakir60MinutesZmanis.at.toEpochMilliseconds(), day)
            assertEquals(sunset + (0.225 * shaah).toLong(), c.tzais13Point5MinutesZmanis.at.toEpochMilliseconds(), day)
            assertEquals(sunset + 20 * 60_000L, c.tzais20.at.toEpochMilliseconds(), day)
            assertEquals(
                c.chatzos.at.toEpochMilliseconds() + maxOf(30 * 60_000L, shaah / 2),
                c.minchaGedolaOhrHaChaim.at.toEpochMilliseconds(),
                day,
            )
            assertEquals(
                c.tzais13Point5MinutesZmanis.at.toEpochMilliseconds() - (shaah + 15 * (shaah / 60)),
                c.plagHaminchaYalkutYosef.at.toEpochMilliseconds(),
                day,
            )
        }
    }

    @Test
    fun dayIsOrdered() {
        for (day in listOf("2026-01-15", "2026-06-21", "2026-12-21")) {
            val c = calendar(day)
            val order = listOf(
                c.alos72Zmanis.at, c.misheyakir66MinutesZmanis.at, c.misheyakir60MinutesZmanis.at, c.sunrise!!,
                c.sofZmanShmaGRA.at, c.chatzos.at, c.minchaGedolaOhrHaChaim.at, c.minchaKetana.at,
                c.plagHaminchaYalkutYosef.at, c.sunset!!, c.tzais13Point5MinutesZmanis.at,
                c.tzais20.at, c.tzais72Zmanis.at,
            )
            assertTrue(order.zipWithNext().all { (a, b) -> a < b }, "$day: $order")
        }
    }

    @Test
    fun chametzZmanimOnlyOnErevPesach() {
        val erevPesach = calendar("2026-04-01")
        val shaah = erevPesach.shaahZmanis72MinutesZmanis.duration.inWholeMilliseconds
        assertEquals(
            erevPesach.alos72Zmanis.at.toEpochMilliseconds() + 5 * shaah,
            erevPesach.sofZmanBiurChametzMGA72MinutesZmanis.at.toEpochMilliseconds(),
        )
        assertEquals(
            erevPesach.sofZmanTfilaMGA72MinutesZmanis.at,
            erevPesach.sofZmanAchilasChametzMGA72MinutesZmanis.at,
        )
        assertEquals(null, calendar("2026-04-05").sofZmanBiurChametzMGA72MinutesZmanis.momentOfOccurrence)
    }
}
