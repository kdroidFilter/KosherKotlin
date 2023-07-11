package com.kosherjava.zmanim.hebrewcalendar

import com.kosherjava.zmanim.ComplexZmanimCalendar
import com.kosherjava.zmanim.util.GeoLocation
import org.junit.Assert
import org.junit.Test
import java.io.File
import java.lang.AssertionError
import java.nio.file.Files
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*

class RegressionTest {
    val hdf = HebrewDateFormatter()

    @Test
    fun testAllTimes() {
        val delimiter = "\t"
        val location = GeoLocation(
            "Lakewood, NJ",
            40.096,
            -74.222,
            29.02,
            TimeZone.getTimeZone("America/New_York")
        )
        val calc = ComplexZmanimCalendar(location)
//        calc.isUseElevation = true
        println("Time zone of calc.calendar: ${calc.calendar.timeZone}")
        val lines = Files.readAllLines(File("./zmanim_Lakewood_NJ.tsv").toPath()).drop(1)
        var zoneOffset: ZoneOffset? = null
        val pattern = DateTimeFormatter.ofPattern("MMM d, uuuu")

        for (line in lines) {
            val fields = line.split(delimiter)
            try {
                val quote = "\""
                val civilDate =
                    LocalDate.parse(fields.first().removeSurrounding(quote), pattern)
                val zoneId = calc.calendar.timeZone.toZoneId()
                println("Zone id: $zoneId")
                val atStartOfDay = civilDate.atStartOfDay(zoneId)
                println("At start of day: $atStartOfDay")
                if (zoneOffset == null) zoneOffset = atStartOfDay.offset
                calc.calendar.time = Date.from(atStartOfDay.toInstant())
                val jewishDate = JewishDate(calc.calendar)
                val jewishCalendar = JewishCalendar(calc.calendar)
                Assert.assertEquals(fields[1].removeSurrounding(quote), jewishDate.toString())
//                assertYomTovOrParshaMatches(jewishDate, jewishCalendar, fields, quote)
                val timeFormatter = DateTimeFormatter.ofPattern("h:mm:ss a")
                fun Date?.fmt() = this?.toInstant()?.atOffset(zoneOffset)?.also { println(it) }?.format(timeFormatter)
                var col = 4
                Assert.assertEquals(fields[col++], jewishCalendar.dafYomiBavli?.let { "${it.masechtaTransliterated} ${it.daf}" })
                Assert.assertEquals(fields[col++], calc.alos120.fmt())
                Assert.assertEquals(fields[col++], calc.alos120Zmanis.fmt())
                Assert.assertEquals(fields[col++], calc.alos26Degrees.fmt())
                Assert.assertEquals(fields[col++], calc.alos96.fmt())
                Assert.assertEquals(fields[col++], calc.alos96Zmanis.fmt())
                Assert.assertEquals(fields[col++], calc.alos19Point8Degrees.fmt())
                Assert.assertEquals(fields[col++], calc.alos19Degrees.fmt())
                Assert.assertEquals(fields[col++], calc.alos90.fmt())
                Assert.assertEquals(fields[col++], calc.alos90Zmanis.fmt())
                Assert.assertEquals(fields[col++], calc.alos18Degrees.fmt())
                Assert.assertEquals(fields[col++], calc.alosBaalHatanya.fmt())
                Assert.assertEquals(fields[col++], calc.alos16Point1Degrees.fmt())
                Assert.assertEquals(fields[col++], calc.alos72.fmt())
                Assert.assertEquals(fields[col++], calc.alos72Zmanis.fmt())
//                Assert.assertEquals(fields[col++], calc.alos60.fmt())
                Assert.assertEquals(fields[col++], calc.misheyakir11Point5Degrees.fmt())
                Assert.assertEquals(fields[col++], calc.misheyakir11Degrees.fmt())
                Assert.assertEquals(fields[col++], calc.misheyakir10Point2Degrees.fmt())
                Assert.assertEquals(fields[col++], calc.misheyakir9Point5Degrees.fmt())
                Assert.assertEquals(fields[col++], calc.misheyakir7Point65Degrees.fmt())
//                Assert.assertEquals(fields[col++], calc.sunrise.fmt())
                Assert.assertEquals(fields[col++], calc.seaLevelSunrise.fmt())
                Assert.assertEquals(fields[col++], calc.sofZmanShmaMGA120Minutes.fmt())
                Assert.assertEquals(fields[col++], calc.sofZmanShmaMGA16Point1Degrees.fmt())
                Assert.assertEquals(fields[col++], calc.sofZmanShmaMGA18Degrees.fmt())
                Assert.assertEquals(fields[col++], calc.sofZmanShmaMGA19Point8Degrees.fmt())
                Assert.assertEquals(fields[col++], calc.sofZmanShmaMGA72Minutes.fmt())
                Assert.assertEquals(fields[col++], calc.sofZmanShmaMGA72MinutesZmanis.fmt())
                Assert.assertEquals(fields[col++], calc.sofZmanShmaMGA90Minutes.fmt())
                Assert.assertEquals(fields[col++], calc.sofZmanShmaMGA90MinutesZmanis.fmt())
                Assert.assertEquals(fields[col++], calc.sofZmanShmaMGA96Minutes.fmt())
                Assert.assertEquals(fields[col++], calc.sofZmanShmaMGA96MinutesZmanis.fmt())
                Assert.assertEquals(fields[col++], calc.sofZmanShmaAlos16Point1ToSunset.fmt())
                Assert.assertEquals(fields[col++], calc.sofZmanShmaAlos16Point1ToTzaisGeonim7Point083Degrees.fmt())
                Assert.assertEquals(fields[col++], calc.sofZmanShmaMGA16Point1DegreesToFixedLocalChatzos.fmt())
                Assert.assertEquals(fields[col++], calc.sofZmanShmaMGA18DegreesToFixedLocalChatzos.fmt())
                Assert.assertEquals(fields[col++], calc.sofZmanShmaMGA90MinutesToFixedLocalChatzos.fmt())
                Assert.assertEquals(fields[col++], calc.sofZmanShmaMGA72MinutesToFixedLocalChatzos.fmt())
                Assert.assertEquals(fields[col++], calc.sofZmanShmaGRASunriseToFixedLocalChatzos.fmt())
                Assert.assertEquals(fields[col++], calc.sofZmanShma3HoursBeforeChatzos.fmt())
                Assert.assertEquals(fields[col++], calc.sofZmanShmaAteretTorah.fmt())
                Assert.assertEquals(fields[col++], calc.sofZmanShmaBaalHatanya.fmt())
                Assert.assertEquals(fields[col++], calc.sofZmanShmaGRA.fmt())
                Assert.assertEquals(fields[col++], calc.sofZmanTfilaMGA120Minutes.fmt())
                Assert.assertEquals(fields[col++], calc.sofZmanTfilaMGA16Point1Degrees.fmt())
                Assert.assertEquals(fields[col++], calc.sofZmanTfilaMGA18Degrees.fmt())
                Assert.assertEquals(fields[col++], calc.sofZmanTfilaMGA19Point8Degrees.fmt())
                Assert.assertEquals(fields[col++], calc.sofZmanTfilaMGA72Minutes.fmt())
                Assert.assertEquals(fields[col++], calc.sofZmanTfilaMGA72MinutesZmanis.fmt())
                Assert.assertEquals(fields[col++], calc.sofZmanTfilaMGA90Minutes.fmt())
                Assert.assertEquals(fields[col++], calc.sofZmanTfilaMGA90MinutesZmanis.fmt())
                Assert.assertEquals(fields[col++], calc.sofZmanTfilaMGA96Minutes.fmt())
                Assert.assertEquals(fields[col++], calc.sofZmanTfilaMGA96MinutesZmanis.fmt())
                Assert.assertEquals(fields[col++], calc.sofZmanTfilaGRASunriseToFixedLocalChatzos.fmt())
                Assert.assertEquals(fields[col++], calc.sofZmanTfila2HoursBeforeChatzos.fmt())
                Assert.assertEquals(fields[col++], calc.sofZmanTfilahAteretTorah.fmt())
                Assert.assertEquals(fields[col++], calc.sofZmanTfilaBaalHatanya.fmt())
                Assert.assertEquals(fields[col++], calc.sofZmanTfilaGRA.fmt())
                Assert.assertEquals(fields[col++], calc.chatzos.fmt())
                Assert.assertEquals(fields[col++], calc.fixedLocalChatzos.fmt())

                Assert.assertEquals(fields[col++], calc.minchaGedola.fmt()) // Mincha Gedola GRA in spreadsheet
                Assert.assertEquals(fields[col++], calc.minchaGedolaBaalHatanya.fmt())
                Assert.assertEquals(fields[col++], calc.minchaGedola72Minutes.fmt())
                Assert.assertEquals(fields[col++], calc.minchaGedola16Point1Degrees.fmt())
                Assert.assertEquals(fields[col++], calc.minchaGedolaAteretTorah.fmt())
                Assert.assertEquals(fields[col++], calc.minchaGedolaGreaterThan30.fmt())
                Assert.assertEquals(fields[col++], calc.minchaGedolaBaalHatanyaGreaterThan30.fmt())
                Assert.assertEquals(fields[col++], calc.minchaGedolaGRAFixedLocalChatzos30Minutes.fmt())
                Assert.assertEquals(fields[col++], calc.minchaGedolaAhavatShalom.fmt())

                Assert.assertEquals(fields[col++], calc.samuchLeMinchaKetanaGRA.fmt())

                Assert.assertEquals(fields[col++], calc.minchaKetana.fmt()) //gra?
                Assert.assertEquals(fields[col++], calc.minchaKetanaBaalHatanya.fmt())
                Assert.assertEquals(fields[col++], calc.samuchLeMinchaKetana16Point1Degrees.fmt())
                Assert.assertEquals(fields[col++], calc.minchaKetana16Point1Degrees.fmt())
                Assert.assertEquals(fields[col++], calc.samuchLeMinchaKetana72Minutes.fmt())
                Assert.assertEquals(fields[col++], calc.minchaKetana72Minutes.fmt())
                Assert.assertEquals(fields[col++], calc.minchaKetanaAteretTorah.fmt())
                Assert.assertEquals(fields[col++], calc.minchaKetanaGRAFixedLocalChatzosToSunset.fmt())
                Assert.assertEquals(fields[col++], calc.minchaKetanaAhavatShalom.fmt())

                Assert.assertEquals(fields[col++], calc.plagHamincha.fmt())
// Plag Hamincha
// Plag Hamincha Baal Hatanya
//Plag Hamincha 60 Minutes
//Plag Hamincha 72 Minutes
//Plag Hamincha 72 Minutes Zmaniyos
//Plag Hamincha 16.1°
//Plag Hamincha 18°
//Plag Hamincha 19.8°
//Plag Hamincha 90 Minutes
//Plag Hamincha 90 Minutes Zmaniyos
//Plag Hamincha 96 Minutes
//Plag Hamincha 96 Minutes Zmaniyos
//Plag Hamincha 120 Minutes
//Plag Hamincha 120 Minutes Zmaniyos
//Plag Hamincha Alos 16.1° To Sunset
// Plag Hamincha Ahavat Shalom
//Plag Hamincha Alos 16.1° To Tzais Geonim 7.083°
//Plag Hamincha 26°
// Plag Hamincha Ateret Torah
// Plag Hamincha Fixed Local Chatzos to Sunset
//Bain Hasmashos Yereim 3.05
//Bain Hasmashos Yereim 2.8
//Bain Hasmashos Yereim 18 Minutes
//Bain Hasmashos Yereim 16.875 Minutes
//Bain Hasmashos Yereim 2.1°
//Bain Hasmashos Yereim 13.5 Minutes
//Candle Lighting 18 Minutes
// Sunset (Sea Level)
//Sunset (25.58 Meters)
//Bain Hasmashos Rabainu Tam 13.5 Minutes Before 7.083°
//Bain Hasmashos Rabainu Tam 58.5 Minutes
//Bain Hasmashos Rabainu Tam 13.24°
//Bain Hasmashos Rabainu Tam 2 Stars
//Tzais Geonim 3.65°
//Tzais Geonim 3.676°
//Tzais Geonim 3.7°
//Tzais Geonim 3.8°
//Tzais Geonim 4.37°
//Tzais Geonim 4.61°
//Tzais Geonim 4.8°
//Tzais Geonim 5.88°
//Tzais Geonim 5.95°
// Tzais Baal Hatanya
//Tzais Geonim 6.45°
//Tzais Geonim 7.083°
//Tzais Geonim 7.67°
//Tzais 50 Minutes
//Tzais 60 Minutes
//Tzais Geonim 8.5°
//Tzais 72 Minutes
//Tzais Geonim 9.3°
//Tzais Geonim 9.75°
//Tzais 72 Minutes Zmaniyos
//Tzais 16.1°
//Tzais 90 Minutes
//Tzais 90 Minutes Zmaniyos
//Tzais 18°
//Tzais 19.8°
//Tzais 96 Minutes
//Tzais 96 Minutes Zmaniyos
//Tzais 26°
//Tzais 120 Minutes
//Tzais 120 Minutes Zmaniyos
// Chatzos Halayla
// Molad
//Techilas Zman Kiddush Levana 3 Days
//Techilas Zman Kiddush Levana 7 Days
// Sof Zman Kiddush Levana Between Moldos
//Sof Zman Kiddush Levana 15 Days
// Shaah Zmanis GRA
// Shaah Zmanis Baal Hatanya
//Shaah Zmanis 72 Minutes
//Shaah Zmanis 72 Minutes Zmaniyos
//Shaah Zmanis 16.1°
//Shaah Zmanis 90 Minutes
//Shaah Zmanis 90 Minutes Zmaniyos
//Shaah Zmanis 18°
//Shaah Zmanis 19.8°
//Shaah Zmanis 96 Minutes
//Shaah Zmanis 96 Minutes Zmaniyos
//Shaah Zmanis 26°
//Shaah Zmanis 120 Minutes
//Shaah Zmanis 120 Minutes Zmaniyos
// Shaah Zmanit Ateret Torah
// Shaah Zmanit Ahavat Shalom
// Tachanun Shacharis
// Tachanun Mincha
// Hallel
// Hallel Shalem
            } catch (t: Throwable) {
                if (t is AssertionError) throw t
                t.printStackTrace()
                continue
            }
        }
    }

    private fun assertYomTovOrParshaMatches(
        jewishDate: JewishDate,
        jewishCalendar: JewishCalendar,
        fields: List<String>,
        quote: String
    ) {
        val formatted = hdf.format(jewishDate)
        println("Date format: $formatted, parsha/yom tov: ${getParshaOrYomTov(jewishCalendar)}")
        val escapedParshaOrYomTov = fields[3].removeSurrounding(quote)
        Assert.assertEquals(
            escapedParshaOrYomTov,
            getParshaOrYomTov(jewishCalendar)
        )
    }

    private fun getParshaOrYomTov(jewishCalendar: JewishCalendar) = hdf
        .transliteratedParshiosList[jewishCalendar.parshah]
        ?.let {
            val specialShabbos = jewishCalendar.specialShabbos
            if (specialShabbos != JewishCalendar.Parsha.NONE) "$it, ${hdf.transliteratedParshiosList[specialShabbos]}" else it
        }
        ?.ifBlank {
            jewishCalendar
                .yomTovIndex
                .takeIf { it >= 0 }
                ?.let { hdf.transliteratedHolidayList[it].takeIf { it !in arrayOf("Erev Pesach"/*, "Chol Hamoed Pesach"*/) } }
                ?: when {
                    jewishCalendar.isYomKippurKatan -> "Yom Kippur Katan"
                    jewishCalendar.isRoshChodesh -> {
                        "Rosh Chodesh ${
                            hdf.transliteratedMonthList[
                                    if (jewishCalendar.jewishDayOfMonth >= 29) jewishCalendar.jewishMonth/*intentionally +1 month*/
                                    else jewishCalendar.jewishMonth - 1
                            ]
                        }"
                    }
                    else -> null
                }
        } ?: ""
}