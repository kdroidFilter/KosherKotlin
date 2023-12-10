package sternbach.software.kosherkotlin

import sternbach.software.kosherkotlin.hebrewcalendar.HebrewLocalDate
import sternbach.software.kosherkotlin.hebrewcalendar.HebrewMonth
import sternbach.software.kosherkotlin.util.GeoLocation
import kotlinx.datetime.*

fun main(args: Array<String>) {
    println(Clock.System.now().toString())
    /*val tz = java.util.TimeZone.getTimeZone("America/Toronto")
    val fortConger =
        com.kosherjava.java.zmanim.util.GeoLocation("Fort Conger, NU Canada", 81.7449398, -64.7945858, 127.0, tz)
    val october14_0001 = java.time.Instant.parse("0001-10-12T14:11:11.798989078Z")
    val elevation = com.kosherjava.java.zmanim.ComplexZmanimCalendar(fortConger).apply {
        calendar = Calendar.getInstance(tz).apply { time = Date.from(october14_0001) }
        isUseElevation = true
    }
    val noElevation = com.kosherjava.java.zmanim.ComplexZmanimCalendar(fortConger).apply {
        calendar = Calendar.getInstance(tz).apply { time = Date.from(october14_0001) }
        isUseElevation = false
    }
    val alos72Zmanis = elevation.alos72Zmanis
    println("alos72Zmanis = $alos72Zmanis")
    for((index, sunrise) in listOf(elevation.sunrise, noElevation.sunrise, elevation.seaLevelSunrise, noElevation.seaLevelSunset).withIndex()) {
        println("index = $index, sunrise = $sunrise")
        if(sunrise != null && alos72Zmanis != null && alos72Zmanis > sunrise) {
            println("index = $index, $alos72Zmanis > $sunrise")
        }
    }*//*
    val tz = kotlinx.datetime.TimeZone.of("America/Toronto")
    val fortConger = GeoLocation("Fort Conger, NU Canada", 81.7449398, -64.7945858, 127.0, tz)
    val october14_0001 = kotlinx.datetime.LocalDateTime(1,10,14,0,0,0)
    val elevation = ComplexZmanimCalendar(fortConger).apply {
        this.localDateTime = october14_0001
        isUseElevation = true
    }
    val noElevation = ComplexZmanimCalendar(fortConger).apply {
        this.localDateTime = october14_0001
        isUseElevation = false
    }
    val alos72Zmanis = elevation.alos72Zmanis
    println("alos72Zmanis = $alos72Zmanis")
    for((index, sunrise) in listOf(elevation.sunrise, noElevation.sunrise, elevation.seaLevelSunrise, noElevation.seaLevelSunset).withIndex()) {
        println("index = $index, sunrise = $sunrise")
        if(sunrise != null && alos72Zmanis.momentOfOccurrence != null && alos72Zmanis.momentOfOccurrence > sunrise) {
            println("index = $index, $alos72Zmanis > $sunrise")
        }
    }*/

val lakewood = GeoLocation("Lakewood, NJ", 40.0721087, -74.2400243, 15.0, kotlinx.datetime.TimeZone.of("America/New_York"))
    val samoa = GeoLocation("Apia, Samoa", -13.8599098, -171.8031745, 1858.0, kotlinx.datetime.TimeZone.of("Pacific/Apia"))
    val jerusalem = GeoLocation("Jerusalem, Israel", 31.7781161, 35.233804, 740.0, kotlinx.datetime.TimeZone.of("Asia/Jerusalem"))
    val losAngeles = GeoLocation("Los Angeles, CA", 34.0201613, -118.6919095, 71.0, kotlinx.datetime.TimeZone.of("America/Los_Angeles"))
    val tokyo = GeoLocation("Tokyo, Japan", 35.6733227, 139.6403486, 40.0, kotlinx.datetime.TimeZone.of("Asia/Tokyo"))
    val arcticNunavut =
        GeoLocation("Fort Conger, NU Canada", 81.7449398, -64.7945858, 127.0, kotlinx.datetime.TimeZone.of("America/Iqaluit"))
    val basicLocations = listOf(/*lakewood, jerusalem, losAngeles, tokyo, */arcticNunavut, samoa)

    val hooperBay = GeoLocation("Hooper Bay, Alaska", 61.520182, -166.1740437, 8.0, kotlinx.datetime.TimeZone.of("America/Anchorage"))
    val daneborg = GeoLocation("Daneborg, Greenland", 74.2999996, -20.2420877, 0.0, kotlinx.datetime.TimeZone.of("America/Godthab"))
    val arcticLocations = listOf(hooperBay, daneborg)

    val allLocations = basicLocations + arcticLocations

//    println("TimeZone.UTC.id = ${TimeZone.UTC.id}")
//    println(zmanim.minchaKetanaGRAFixedLocalChatzosToSunset.definition.mainCalculationMethodUsed?.format())
//    println(zmanim.minchaKetanaGRAFixedLocalChatzosToSunset.definition.mainCalculationMethodUsed?.valueToString())
    val formatter = ZmanDescriptionFormatter()
    val includeElevationDescription = false
//    val days = Int.MAX_VALUE * 1000L * 60L * 60L * 24L
//    println(days.milliseconds.inWholeNanoseconds)
    println(Long.MAX_VALUE)
    val year6000 = HebrewLocalDate(6000, HebrewMonth.TISHREI, 1).toLocalDateGregorian()
    val midnight = LocalTime(0,0,0)
//    fun kotlinx.datetime.TimeZone.toJava() = java.util.TimeZone.getTimeZone(id)
//    fun GeoLocation.toJava() = com.kosherjava.java.zmanim.util.GeoLocation(locationName, latitude, longitude, elevation, timeZone.toJava())
//    println(days.inWholeNanoseconds)
    /*for (location in allLocations) {
        val elevation = com.kosherjava.java.zmanim.ComplexZmanimCalendar(location.toJava()).apply { isUseElevation = true }
        val noElevation = com.kosherjava.java.zmanim.ComplexZmanimCalendar(location.toJava()).apply { isUseElevation = false }
        var date = LocalDate(2023,1,1)
        val dateTime = LocalDateTime(date, midnight)
        noElevation.calendar = java.util.Calendar.getInstance(location.timeZone.toJava()).apply {
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
            if(date.year % 500 == 0 && date.monthNumber == 1 && date.dayOfMonth == 1) println("Computing $date for ${location.locationName}")
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
            for (a in alos.keys)
                for (s in sunrise.keys)
                    if (
                        a?.let {
                                s?.let { it1 ->
                                    it > s
                                }
                            } == true
                    )
                        println("Alos after sunrise in ${location.locationName} on ${noElevation.calendar.toInstant()}: ${alos[a]}($a) > ${sunrise[s]}($s)")
            noElevation.calendar.add(Calendar.DAY_OF_MONTH, 1)
            elevation.calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
    }*/
}
//        listOf(
//            zmanim.allShaosZmaniyos,
//            zmanim.allZmanim
//        ).flatten()
//            .apply {
//                println("Short descriptions:")
//                forEach {
//                    println()
//                    println(formatter.formatShortDescription(it, includeElevationDescription))
//                }
//
//                println("Long descriptions:")
//                forEach {
//                    println()
//                    println(formatter.formatLongDescription(it))
//                }
//    println("Shaos zmaniyos:")
//    zmanim.allShaosZmaniyos.mapIndexed { index, it -> "$index(${it.definition.type}): " + formatter.formatShortDescription(it, includeElevationDescription) }.forEach { println(it) }
//    println("Zmanim:")
//    zmanim.allZmanim.mapIndexed { index, it -> "$index(${it.definition.type}): " + runCatching{ formatter.formatShortDescription(it, includeElevationDescription) }.getOrNull() }.forEach { println(it) }
//    println()
//    zmanim.allZmanim.mapIndexed { index, it -> "$index(${it.definition.type}) - ${it.definition}: " + runCatching{ formatter.formatShortDescription(it, includeElevationDescription) }.getOrNull() }.forEach { println(it) }
//            }
//    }
//}