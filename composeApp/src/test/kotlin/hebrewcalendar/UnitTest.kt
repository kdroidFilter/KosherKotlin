package hebrewcalendar

import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toKotlinLocalDate
import kotlinx.datetime.toKotlinLocalDateTime
import kotlinx.datetime.toLocalDateTime
import org.junit.Assert
import org.junit.Test
import sternbach.software.kosherkotlin.ComplexZmanimCalendar
import sternbach.software.kosherkotlin.hebrewcalendar.HebrewLocalDate
import sternbach.software.kosherkotlin.hebrewcalendar.HebrewMonth
import sternbach.software.kosherkotlin.metadata.ZmanType
import sternbach.software.kosherkotlin.util.GeoLocation.Companion.rawOffset
import java.util.Calendar
import java.util.TimeZone
import kotlin.time.Duration.Companion.days

class UnitTest {
    @Test
    fun fixedLocalChatzosWorksInAllRegions() {
        for (location in TestHelper.allLocations) {
            println("Location: ${location.locationName}, timezone id: ${location.timeZone.id}")
            val locationJ = com.kosherjava.zmanim.util.GeoLocation(
                location.locationName,
                location.latitude,
                location.longitude,
                location.elevation,
                TimeZone.getTimeZone(location.timeZone.id)
            )
            Assert.assertEquals(locationJ.timeZone.rawOffset, location.timeZone.rawOffset)
            val noElevation = com.kosherjava.zmanim.ComplexZmanimCalendar(
                locationJ
            ).also {
                it.calendar = Calendar.getInstance().apply {
                    set(Calendar.MONTH, Calendar.MARCH)
                    set(Calendar.DAY_OF_MONTH, 1)
                }
            }
            val elevation = com.kosherjava.zmanim.ComplexZmanimCalendar(locationJ)
                .also {
                    it.isUseElevation = true
                    it.calendar = Calendar.getInstance().apply {
                        set(Calendar.MONTH, Calendar.MARCH)
                        set(Calendar.DAY_OF_MONTH, 1)
                    }
                }
            val noElevationk = ComplexZmanimCalendar(location)
            println("No elevation? ${noElevation.isUseElevation}, ${noElevationk.isUseElevation}")
            val elevationk = ComplexZmanimCalendar(location).also { it.isUseElevation = true }
            val cals = listOf(elevation, noElevation)
            val jNoElevFixedLocalChatzos = noElevation.minchaGedola
            val jElevFixedLocalChatzos = elevation.minchaGedola
            val jNoElevMinchaGedola30 = noElevation.minchaGedola30Minutes
            val jElevMinchaGedola30 = elevation.minchaGedola30Minutes
            val jNoShaaZmanis = noElevation.shaahZmanisAlos16Point1ToTzais3Point7
            val jShaaZmanis = elevation.shaahZmanisAlos16Point1ToTzais3Point7
            println("noElevation.getMinchaGedola() = $jNoElevFixedLocalChatzos, elevation.getMinchaGedola() = $jElevFixedLocalChatzos")
            println("noElevation.getMinchaGedola30() = $jNoElevMinchaGedola30, elevation.getMinchaGedola30() = $jElevMinchaGedola30")
//            println("noElev.sunrise = ${noElevation.elevationAdjustedSunrise}, noElev.sunset = ${noElevation.elevationAdjustedSunset}")
//            println("elev.sunrise = ${elevation.elevationAdjustedSunrise}, elev.sunset = ${elevation.elevationAdjustedSunset}")
            println(
                "noElev.minchaGedola30MinutesZmanis = ${
                    com.kosherjava.zmanim.AstronomicalCalendar.getTimeOffset(
                        noElevation.getChatzos(),
                        noElevation.getShaahZmanisAlos16Point1ToTzais3Point7() / 2
                    )
                }, elev.minchaGedola30MinutesZmanis = ${
                    com.kosherjava.zmanim.AstronomicalCalendar.getTimeOffset(
                        elevation.getChatzos(),
                        elevation.getShaahZmanisAlos16Point1ToTzais3Point7() / 2
                    )
                }"
            )
            println("jNoShaaZmanis = $jNoShaaZmanis, jShaaZmanis = $jShaaZmanis")
            assert(jNoElevFixedLocalChatzos != null) { "Mincha gedolah failed for no elevation in $location" }
            assert(jElevFixedLocalChatzos != null) { "Fixed local chatzos failed for elevation in $location" }
            assert(noElevationk.fixedLocalChatzos.momentOfOccurrence != null) { "Fixed local chatzos failed for no elevation in $location" }
            assert(elevationk.fixedLocalChatzos.momentOfOccurrence != null) { "Fixed local chatzos failed for elevation in $location" }
        }
    }

    @Test
    fun alosIsNeverAfterSunrise() {
        fun kotlinx.datetime.TimeZone.toJava() = java.util.TimeZone.getTimeZone(id)
        fun sternbach.software.kosherkotlin.util.GeoLocation.toJava() =
            com.kosherjava.zmanim.util.GeoLocation(
                locationName,
                latitude,
                longitude,
                elevation,
                timeZone.toJava()
            )
        for (location in listOf(TestHelper.arcticNunavut)) {
            val elevation = com.kosherjava.zmanim.ComplexZmanimCalendar(location.toJava())
                .apply { isUseElevation = true }
            val noElevation = com.kosherjava.zmanim.ComplexZmanimCalendar(location.toJava())
                .apply { isUseElevation = false }
            val noElevationKotlin = ComplexZmanimCalendar(location)
                .apply { isUseElevation = false }


            var date = java.time.LocalDate.of(1, 9, 8)
            val year6000 = HebrewLocalDate(6000, HebrewMonth.TISHREI, 1).toLocalDateGregorian().toJavaLocalDate()
            val midnight = java.time.LocalTime.of(0, 0, 0)

            val dateTime = java.time.LocalDateTime.of(date, midnight)
            noElevationKotlin.localDateTime = dateTime.toKotlinLocalDateTime()
            noElevation.calendar =
                java.util.Calendar.getInstance(location.timeZone.toJava()).apply {
                    set(java.util.Calendar.YEAR, dateTime.year)
                    set(java.util.Calendar.MONTH, dateTime.month.value + 1)
                    set(java.util.Calendar.DAY_OF_MONTH, dateTime.dayOfMonth)
                }
            elevation.calendar = java.util.Calendar.getInstance(location.timeZone.toJava()).apply {
                set(java.util.Calendar.YEAR, dateTime.year)
                set(java.util.Calendar.MONTH, dateTime.month.value + 1)
                set(java.util.Calendar.DAY_OF_MONTH, dateTime.dayOfMonth)
            }
            while (date < year6000) {
                if (date.year % 500 == 0 && date.monthValue == 1 && date.dayOfMonth == 1) println("Computing $date for ${location.locationName}")
//            n.localDateTime = dateTime
//            elevation.localDateTime = dateTime

                val sunrise = mapOf(
                    elevation.sunrise to "elevation.sunrise",
                    elevation.seaLevelSunrise to "elevation.seaLevelSunrise",
                    noElevation.sunrise to "noElevation.sunrise",
                    noElevation.seaLevelSunrise to "noElevation.seaLevelSunrise",
                )
                val alos = mapOf(
                    elevation.alos60 to "elevation.alos60",
                    noElevation.alos60 to "noElevation.alos60",
                    elevation.alos72Zmanis to "elevation.alos72Zmanis",
                    noElevation.alos72Zmanis to "noElevation.alos72Zmanis",
                    elevation.alos96 to "elevation.alos96",
                    noElevation.alos96 to "noElevation.alos96",
                    elevation.alos90Zmanis to "elevation.alos90Zmanis",
                    noElevation.alos90Zmanis to "noElevation.alos90Zmanis",
                    elevation.alos96Zmanis to "elevation.alos96Zmanis",
                    noElevation.alos96Zmanis to "noElevation.alos96Zmanis",
                    elevation.alos90 to "elevation.alos90",
                    noElevation.alos90 to "noElevation.alos90",
                    elevation.alos120 to "elevation.alos120",
                    noElevation.alos120 to "noElevation.alos120",
                    elevation.alos120Zmanis to "elevation.alos120Zmanis",
                    noElevation.alos120Zmanis to "noElevation.alos120Zmanis",
                    elevation.alos26Degrees to "elevation.alos26Degrees",
                    noElevation.alos26Degrees to "noElevation.alos26Degrees",
                    elevation.alos18Degrees to "elevation.alos18Degrees",
                    noElevation.alos18Degrees to "noElevation.alos18Degrees",
                    elevation.alos19Degrees to "elevation.alos19Degrees",
                    noElevation.alos19Degrees to "noElevation.alos19Degrees",
                    elevation.alos19Point8Degrees to "elevation.alos19Point8Degrees",
                    noElevation.alos19Point8Degrees to "noElevation.alos19Point8Degrees",
                    elevation.alos16Point1Degrees to "elevation.alos16Point1Degrees",
                    noElevation.alos16Point1Degrees to "noElevation.alos16Point1Degrees",
                )
                if(noElevationKotlin.alos72Zmanis.momentOfOccurrence?.let {a ->
                        noElevationKotlin.sunrise?.let { s ->
                            a > s
                        }
                    } == true) println("Kotlin alos 72 zmanis (${noElevationKotlin.alos72Zmanis.momentOfOccurrence}) is after sunrise (${noElevationKotlin.sunrise})")
                for (a in alos.keys)
                    for (s in sunrise.keys)
                        assert(
                            a
                                ?.let {
                                    s?.let { it1 ->
                                        it > s
                                    }
                                }
                                .let {
                                    it == false || it == null
                                }
                                .also {
                                    if (!it) println("Alos after sunrise in ${location.locationName} on ${noElevation.calendar.toInstant()}: ${alos[a]}($a) > ${sunrise[s]}($s)")
                                }
                        )
                date = date.plusDays(1L)
                noElevationKotlin.localDateTime = date.toKotlinLocalDate().atStartOfDayIn(location.timeZone).toLocalDateTime(location.timeZone)
                noElevation.calendar.add(Calendar.DAY_OF_MONTH, 1)
                elevation.calendar.add(Calendar.DAY_OF_MONTH, 1)
            }
        }
        for (location in TestHelper.allLocations) {
            val noElevation = ComplexZmanimCalendar(location)
            val elevation = ComplexZmanimCalendar(location).also { it.isUseElevation = true }
            val cals = listOf(elevation, noElevation)
            println("Raw offset for ${location.locationName}: ${location.timeZone.rawOffset} - fixedLocalChatzos = ${cals.map { it.fixedLocalChatzos }}")
            val alos = cals.flatMap { it.allZmanim.filter { it.rules.type == ZmanType.ALOS } }
            val sunrise = cals.flatMap { it.allZmanim.filter { it.rules.type == ZmanType.HANAITZ } }
            alos.forEach {
                for (zman in sunrise) {
                    if (it.momentOfOccurrence != null && zman.momentOfOccurrence != null)
                        assert(it.momentOfOccurrence!! < zman.momentOfOccurrence!!) { "Alos ($it) is after sunrise ($zman) in $location" }
                }
            }
        }
    }
}