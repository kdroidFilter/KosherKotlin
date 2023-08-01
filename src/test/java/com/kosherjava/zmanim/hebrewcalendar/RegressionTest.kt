package com.kosherjava.zmanim.hebrewcalendar

import com.kosherjava.zmanim.ComplexZmanimCalendar
import com.kosherjava.zmanim.util.GeoLocation
import com.kosherjava.zmanim.util.GeoLocation.Companion.rawOffset
import kotlinx.datetime.*
import kotlinx.datetime.TimeZone
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.File
import java.nio.file.Files
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.time.Duration.Companion.milliseconds

class RegressionTest {
    val hdf = HebrewDateFormatter()

    val mYear = 3762
    val mMonth = 1
    val mDay = 1

    fun Int.toHrMinSec(): Triple<Int, Int, Int> {
        var hour = 0
        var minute = 0
        var second = this
        minute += (second / 60)
        hour += (minute / 60)
        second %= 60
        minute %= 60
        return Triple(hour, minute, second)
    }
    @Test
    fun assertConstructorWorks() {
        val year = mYear + 1
        val month = mMonth + 1
        val day = mDay + 1
        val jd = JewishDate(year, HebrewMonth.getMonthForValue(month), day)
        assertEquals(year, jd.hebrewLocalDate.year.toInt())
        assertEquals(month, jd.hebrewLocalDate.month.value)
        assertEquals(day, jd.jewishDayOfMonth)
    }

    @Test
    fun assertSetYearWorks() {
        val year = mYear + 1
        val jd = JewishDate(mYear, HebrewMonth.getMonthForValue(mMonth), mDay)
        jd.setJewishYear(year)
        assertEquals(year, jd.hebrewLocalDate.year.toInt())
        assertEquals(mMonth, jd.hebrewLocalDate.month.value)
        assertEquals(mDay, jd.jewishDayOfMonth)
    }
    @Test
    fun assertSetMonthWorks() {
        val month = mMonth + 1
        val jd = JewishDate(mYear, HebrewMonth.getMonthForValue(mMonth), mDay)
        jd.setJewishMonth(HebrewMonth.getMonthForValue(month))
        assertEquals(mYear, jd.hebrewLocalDate.year.toInt())
        assertEquals(month, jd.hebrewLocalDate.month.value)
        assertEquals(mDay, jd.jewishDayOfMonth)
    }

    fun Instant?.toDate(): Date? = this?.let { Date.from(it.toJavaInstant()) }

    @Test
    fun testJavaCompatibility() {
        val location = GeoLocation(
            "Lakewood, NJ",
            40.096,
            -74.222,
            29.02,
            TimeZone.of("America/New_York")
        )
        val calc = ComplexZmanimCalendar(location)
        val javaCalc = com.kosherjava.zmanim.java.zmanim.ComplexZmanimCalendar(
            com.kosherjava.zmanim.java.zmanim.util.GeoLocation(
                location.locationName,
                location.latitude,
                location.longitude,
                java.util.TimeZone.getTimeZone(location.timeZone.toJavaZoneId())
            )
        )
        assertEquals(javaCalc.geoLocation.timeZone.rawOffset, calc.geoLocation.timeZone.rawOffset)
        assertEquals(javaCalc.sunrise, calc.sunrise?.toDate())
        assertEquals(javaCalc.alos60, calc.alos60?.toDate())
        assertEquals(javaCalc.tzais60, calc.tzais60?.toDate())
        calc.apply {
            val values = listOf(
                shaahZmanis19Point8Degrees to  javaCalc.shaahZmanis19Point8Degrees,
                shaahZmanis18Degrees to  javaCalc.shaahZmanis18Degrees,
                shaahZmanis26Degrees to  javaCalc.shaahZmanis26Degrees,
                shaahZmanis16Point1Degrees to  javaCalc.shaahZmanis16Point1Degrees,
                shaahZmanis60Minutes to  javaCalc.shaahZmanis60Minutes,
                shaahZmanis72MinutesZmanis to  javaCalc.shaahZmanis72MinutesZmanis,
                shaahZmanis90Minutes to  javaCalc.shaahZmanis90Minutes,
                shaahZmanis90MinutesZmanis to  javaCalc.shaahZmanis90MinutesZmanis,
                shaahZmanis96MinutesZmanis to  javaCalc.shaahZmanis96MinutesZmanis,
                shaahZmanisAteretTorah to  javaCalc.shaahZmanisAteretTorah,
                shaahZmanisAlos16Point1ToTzais3Point8 to  javaCalc.shaahZmanisAlos16Point1ToTzais3Point8,
                shaahZmanisAlos16Point1ToTzais3Point7 to  javaCalc.shaahZmanisAlos16Point1ToTzais3Point7,
                shaahZmanis96Minutes to  javaCalc.shaahZmanis96Minutes,
                shaahZmanis120Minutes to  javaCalc.shaahZmanis120Minutes,
                shaahZmanis120MinutesZmanis to javaCalc.shaahZmanis120MinutesZmanis,
            )
            val listOfZmanim = listOf(
                plagHamincha120MinutesZmanis to javaCalc.plagHamincha120MinutesZmanis,
                plagHamincha120Minutes to javaCalc.plagHamincha120Minutes,
                alos60 to javaCalc.alos60,
                alos72Zmanis to javaCalc.alos72Zmanis,
                alos96 to javaCalc.alos96,
                alos90Zmanis to javaCalc.alos90Zmanis,
                alos96Zmanis to javaCalc.alos96Zmanis,
                alos90 to javaCalc.alos90,
                alos120 to javaCalc.alos120,
                alos120Zmanis to javaCalc.alos120Zmanis,
                alos26Degrees to javaCalc.alos26Degrees,
                alos18Degrees to javaCalc.alos18Degrees,
                alos19Degrees to javaCalc.alos19Degrees,
                alos19Point8Degrees to javaCalc.alos19Point8Degrees,
                alos16Point1Degrees to javaCalc.alos16Point1Degrees,
                misheyakir11Point5Degrees to javaCalc.misheyakir11Point5Degrees,
                misheyakir11Degrees to javaCalc.misheyakir11Degrees,
                misheyakir10Point2Degrees to javaCalc.misheyakir10Point2Degrees,
                misheyakir7Point65Degrees to javaCalc.misheyakir7Point65Degrees,
                misheyakir9Point5Degrees to javaCalc.misheyakir9Point5Degrees,
                sofZmanShmaMGA19Point8Degrees to javaCalc.sofZmanShmaMGA19Point8Degrees,
                sofZmanShmaMGA16Point1Degrees to javaCalc.sofZmanShmaMGA16Point1Degrees,
                sofZmanShmaMGA18Degrees to javaCalc.sofZmanShmaMGA18Degrees,
                sofZmanShmaMGA72MinutesZmanis to javaCalc.sofZmanShmaMGA72MinutesZmanis,
                sofZmanShmaMGA90Minutes to javaCalc.sofZmanShmaMGA90Minutes,
                sofZmanShmaMGA90MinutesZmanis to javaCalc.sofZmanShmaMGA90MinutesZmanis,
                sofZmanShmaMGA96Minutes to javaCalc.sofZmanShmaMGA96Minutes,
                sofZmanShmaMGA96MinutesZmanis to javaCalc.sofZmanShmaMGA96MinutesZmanis,
                sofZmanShma3HoursBeforeChatzos to javaCalc.sofZmanShma3HoursBeforeChatzos,
                sofZmanShmaMGA120Minutes to javaCalc.sofZmanShmaMGA120Minutes,
                sofZmanShmaAlos16Point1ToSunset to javaCalc.sofZmanShmaAlos16Point1ToSunset,
                sofZmanShmaAlos16Point1ToTzaisGeonim7Point083Degrees to javaCalc.sofZmanShmaAlos16Point1ToTzaisGeonim7Point083Degrees,
                sofZmanTfilaMGA19Point8Degrees to javaCalc.sofZmanTfilaMGA19Point8Degrees,
                sofZmanTfilaMGA16Point1Degrees to javaCalc.sofZmanTfilaMGA16Point1Degrees,
                sofZmanTfilaMGA18Degrees to javaCalc.sofZmanTfilaMGA18Degrees,
                sofZmanTfilaMGA72MinutesZmanis to javaCalc.sofZmanTfilaMGA72MinutesZmanis,
                sofZmanTfilaMGA90MinutesZmanis to javaCalc.sofZmanTfilaMGA90MinutesZmanis,
                sofZmanTfilaMGA96MinutesZmanis to javaCalc.sofZmanTfilaMGA96MinutesZmanis,
                sofZmanTfila2HoursBeforeChatzos to javaCalc.sofZmanTfila2HoursBeforeChatzos,
                minchaGedola30Minutes to javaCalc.minchaGedola30Minutes,
                minchaGedola16Point1Degrees to javaCalc.minchaGedola16Point1Degrees,
                minchaGedolaAhavatShalom to javaCalc.minchaGedolaAhavatShalom,
                minchaGedolaGreaterThan30 to javaCalc.minchaGedolaGreaterThan30,
                minchaKetana16Point1Degrees to javaCalc.minchaKetana16Point1Degrees,
                minchaKetanaAhavatShalom to javaCalc.minchaKetanaAhavatShalom,
                minchaKetana72Minutes to javaCalc.minchaKetana72Minutes,
                plagHamincha60Minutes to javaCalc.plagHamincha60Minutes,
                plagHamincha72Minutes to javaCalc.plagHamincha72Minutes,
                plagHamincha90Minutes to javaCalc.plagHamincha90Minutes,
                plagHamincha96Minutes to javaCalc.plagHamincha96Minutes,
                plagHamincha96MinutesZmanis to javaCalc.plagHamincha96MinutesZmanis,
                plagHamincha90MinutesZmanis to javaCalc.plagHamincha90MinutesZmanis,
                plagHamincha72MinutesZmanis to javaCalc.plagHamincha72MinutesZmanis,
                plagHamincha16Point1Degrees to javaCalc.plagHamincha16Point1Degrees,
                plagHamincha19Point8Degrees to javaCalc.plagHamincha19Point8Degrees,
                plagHamincha26Degrees to javaCalc.plagHamincha26Degrees,
                plagHamincha18Degrees to javaCalc.plagHamincha18Degrees,
                plagAlosToSunset to javaCalc.plagAlosToSunset,
                plagAlos16Point1ToTzaisGeonim7Point083Degrees to javaCalc.plagAlos16Point1ToTzaisGeonim7Point083Degrees,
                plagAhavatShalom to javaCalc.plagAhavatShalom,
                bainHashmashosRT13Point24Degrees to javaCalc.bainHashmashosRT13Point24Degrees,
                bainHashmashosRT58Point5Minutes to javaCalc.bainHashmashosRT58Point5Minutes,
                bainHashmashosRT13Point5MinutesBefore7Point083Degrees to javaCalc.bainHashmashosRT13Point5MinutesBefore7Point083Degrees,
                bainHashmashosRT2Stars to javaCalc.bainHashmashosRT2Stars,
                bainHashmashosYereim18Minutes to javaCalc.bainHashmashosYereim18Minutes,
                bainHashmashosYereim3Point05Degrees to javaCalc.bainHashmashosYereim3Point05Degrees,
                bainHashmashosYereim16Point875Minutes to javaCalc.bainHashmashosYereim16Point875Minutes,
                bainHashmashosYereim2Point8Degrees to javaCalc.bainHashmashosYereim2Point8Degrees,
                bainHashmashosYereim13Point5Minutes to javaCalc.bainHashmashosYereim13Point5Minutes,
                bainHashmashosYereim2Point1Degrees to javaCalc.bainHashmashosYereim2Point1Degrees,
                tzaisGeonim3Point7Degrees to javaCalc.tzaisGeonim3Point7Degrees,
                tzaisGeonim3Point8Degrees to javaCalc.tzaisGeonim3Point8Degrees,
                tzaisGeonim5Point95Degrees to javaCalc.tzaisGeonim5Point95Degrees,
                tzaisGeonim3Point65Degrees to javaCalc.tzaisGeonim3Point65Degrees,
                tzaisGeonim3Point676Degrees to javaCalc.tzaisGeonim3Point676Degrees,
                tzaisGeonim4Point61Degrees to javaCalc.tzaisGeonim4Point61Degrees,
                tzaisGeonim4Point37Degrees to javaCalc.tzaisGeonim4Point37Degrees,
                tzaisGeonim5Point88Degrees to javaCalc.tzaisGeonim5Point88Degrees,
                tzaisGeonim4Point8Degrees to javaCalc.tzaisGeonim4Point8Degrees,
                tzaisGeonim6Point45Degrees to javaCalc.tzaisGeonim6Point45Degrees,
                tzaisGeonim7Point083Degrees to javaCalc.tzaisGeonim7Point083Degrees,
                tzaisGeonim7Point67Degrees to javaCalc.tzaisGeonim7Point67Degrees,
                tzaisGeonim8Point5Degrees to javaCalc.tzaisGeonim8Point5Degrees,
                tzaisGeonim9Point3Degrees to javaCalc.tzaisGeonim9Point3Degrees,
                tzaisGeonim9Point75Degrees to javaCalc.tzaisGeonim9Point75Degrees,
                tzais60 to javaCalc.tzais60,
                tzaisAteretTorah to javaCalc.tzaisAteretTorah,
                tzais90Zmanis to javaCalc.tzais90Zmanis,
                tzais96Zmanis to javaCalc.tzais96Zmanis,
                tzais90 to javaCalc.tzais90,
                tzais120 to javaCalc.tzais120,
                tzais120Zmanis to javaCalc.tzais120Zmanis,
                tzais16Point1Degrees to javaCalc.tzais16Point1Degrees,
                tzais26Degrees to javaCalc.tzais26Degrees,
                tzais18Degrees to javaCalc.tzais18Degrees,
                tzais19Point8Degrees to javaCalc.tzais19Point8Degrees,
                tzais96 to javaCalc.tzais96,
                fixedLocalChatzos to javaCalc.fixedLocalChatzos,
                sofZmanKidushLevanaBetweenMoldos to javaCalc.sofZmanKidushLevanaBetweenMoldos,
                sofZmanKidushLevana15Days to javaCalc.sofZmanKidushLevana15Days,
                zmanMolad to javaCalc.zmanMolad,
                sofZmanBiurChametzGRA to javaCalc.sofZmanBiurChametzGRA,
                getSofZmanBiurChametzMGA72Minutes to javaCalc.getSofZmanBiurChametzMGA72Minutes(),
                sofZmanBiurChametzMGA16Point1Degrees to javaCalc.sofZmanBiurChametzMGA16Point1Degrees,
                solarMidnight to javaCalc.solarMidnight,
                sofZmanShmaBaalHatanya to javaCalc.sofZmanShmaBaalHatanya,
                sofZmanTfilaBaalHatanya to javaCalc.sofZmanTfilaBaalHatanya,
                sofZmanBiurChametzBaalHatanya to javaCalc.sofZmanBiurChametzBaalHatanya,
                minchaGedolaBaalHatanya to javaCalc.minchaGedolaBaalHatanya,
                minchaGedolaBaalHatanyaGreaterThan30 to javaCalc.minchaGedolaBaalHatanyaGreaterThan30,
                minchaKetanaBaalHatanya to javaCalc.minchaKetanaBaalHatanya,
                plagHaminchaBaalHatanya to javaCalc.plagHaminchaBaalHatanya,
                tzaisBaalHatanya to javaCalc.tzaisBaalHatanya,
                sofZmanShmaMGA18DegreesToFixedLocalChatzos to javaCalc.sofZmanShmaMGA18DegreesToFixedLocalChatzos,
                sofZmanShmaMGA16Point1DegreesToFixedLocalChatzos to javaCalc.sofZmanShmaMGA16Point1DegreesToFixedLocalChatzos,
                sofZmanShmaMGA90MinutesToFixedLocalChatzos to javaCalc.sofZmanShmaMGA90MinutesToFixedLocalChatzos,
                sofZmanShmaMGA72MinutesToFixedLocalChatzos to javaCalc.sofZmanShmaMGA72MinutesToFixedLocalChatzos,
                sofZmanShmaGRASunriseToFixedLocalChatzos to javaCalc.sofZmanShmaGRASunriseToFixedLocalChatzos,
                sofZmanTfilaGRASunriseToFixedLocalChatzos to javaCalc.sofZmanTfilaGRASunriseToFixedLocalChatzos,
                minchaGedolaGRAFixedLocalChatzos30Minutes to javaCalc.minchaGedolaGRAFixedLocalChatzos30Minutes,
                minchaKetanaGRAFixedLocalChatzosToSunset to javaCalc.minchaKetanaGRAFixedLocalChatzosToSunset,
                plagHaminchaGRAFixedLocalChatzosToSunset to javaCalc.plagHaminchaGRAFixedLocalChatzosToSunset,
                tzais50 to javaCalc.tzais50,
                samuchLeMinchaKetanaGRA to javaCalc.samuchLeMinchaKetanaGRA,
                samuchLeMinchaKetana16Point1Degrees to javaCalc.samuchLeMinchaKetana16Point1Degrees,
            )
            testValues(values) { it }
            testValues(listOfZmanim) {
                it?.toDate()
            }
        }
    }

    private fun <T, R> testValues(listOfZmanim: List<Pair<T, R>>, transform: (t: T) -> R) {
        for ((index, pair) in listOfZmanim.withIndex()) {
            val (kotlin, java) = pair
            runCatching {
                assertEquals(java, transform(kotlin))
            }.getOrElse {
                println("Error on index $index")
                throw it
            }
        }
    }

    /*fun testAllTimes() {
        val delimiter = "\t"
        val location = GeoLocation(
            "Lakewood, NJ",
            40.096,
            -74.222,
            29.02,
            TimeZone.of("America/New_York")
        )
        val calc = ComplexZmanimCalendar(location)
        val javaCalc = com.kosherjava.zmanim.java.zmanim.ComplexZmanimCalendar(
            com.kosherjava.zmanim.java.zmanim.util.GeoLocation(
                location.locationName,
                location.latitude,
                location.longitude,
                java.util.TimeZone.getTimeZone(location.timeZone.toJavaZoneId())
            )
        )

//        calc.isUseElevation = true
        println("Time zone of calc.calendar: ${calc.geoLocation.timeZone}")
        val lines = Files.readAllLines(File("./zmanim_Lakewood_NJ.tsv").toPath()).drop(1)
        var zoneOffset: UtcOffset? = null
        val pattern = DateTimeFormatter.ofPattern("MMM d, uuuu")

        for (line in lines) {
            val fields = line.split(delimiter)
            try {
                val quote = "\""
                val civilDate = java.time.LocalDate.parse(fields.first().removeSurrounding(quote), pattern).toKotlinLocalDate()
                val tz = calc.geoLocation.timeZone
                println("Zone id: $tz")
                val atStartOfDay = civilDate.atStartOfDayIn(tz)
                println("At start of day: $atStartOfDay")
                val offsetInSeconds = tz.rawOffset.milliseconds.inWholeSeconds.toInt()
                if (zoneOffset == null) {
                    val (hr, min, sec) = offsetInSeconds.toHrMinSec()
                    zoneOffset = UtcOffset(hr,min, sec)
                }
                calc.localDate = atStartOfDay.toLocalDateTime(tz)
                val jewishDate = JewishDate(calc.localDate.date)
                val jewishCalendar = JewishCalendar(calc.localDate.date)
                Assert.assertEquals(fields[1].removeSurrounding(quote), jewishDate.toString())
//                assertYomTovOrParshaMatches(jewishDate, jewishCalendar, fields, quote)
                val timeFormatter = DateTimeFormatter.ofPattern("h:mm:ss a")
                fun Instant?.fmt() =
                    this?.let { java.time.Instant.ofEpochSecond(it.epochSeconds)?.atOffset(ZoneOffset.ofTotalSeconds(offsetInSeconds))?.also { println(it) }?.format(timeFormatter) }
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
    }*/

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
                                    if (jewishCalendar.jewishDayOfMonth >= 29) jewishCalendar.hebrewLocalDate.month.value/*intentionally +1 month*/
                                    else jewishCalendar.hebrewLocalDate.month.value - 1
                            ]
                        }"
                    }
                    else -> null
                }
        } ?: ""
}