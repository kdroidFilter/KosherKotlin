package com.kosherjava.zmanim.hebrewcalendar

import com.kosherjava.java.zmanim.AstronomicalCalendar
import com.kosherjava.zmanim.ComplexZmanimCalendar
import com.kosherjava.zmanim.ZmanType
import com.kosherjava.zmanim.util.GeoLocation.Companion.rawOffset
import org.junit.Assert
import org.junit.Test
import java.util.*

class UnitTest {
    @Test
    fun fixedLocalChatzosWorksInAllRegions() {
        for(location in TestHelper.allLocations) {
            println("Location: ${location.locationName}, timezone id: ${location.timeZone.id}")
            val locationJ = com.kosherjava.java.zmanim.util.GeoLocation(location.locationName, location.latitude, location.longitude, location.elevation, java.util.TimeZone.getTimeZone(location.timeZone.id))
            Assert.assertEquals(locationJ.timeZone.rawOffset, location.timeZone.rawOffset)
            val noElevation = com.kosherjava.java.zmanim.ComplexZmanimCalendar(locationJ).also {
                it.calendar = Calendar.getInstance().apply {
                    set(Calendar.MONTH, Calendar.MARCH)
                    set(Calendar.DAY_OF_MONTH, 1)
                }
            }
            val elevation = com.kosherjava.java.zmanim.ComplexZmanimCalendar(locationJ).also {
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
            println("noElev.sunrise = ${noElevation.elevationAdjustedSunrise}, noElev.sunset = ${noElevation.elevationAdjustedSunset}")
            println("noElev.minchaGedola30MinutesZmanis = ${AstronomicalCalendar.getTimeOffset(noElevation.getChatzos(), noElevation.getShaahZmanisAlos16Point1ToTzais3Point7() / 2)}, elev.minchaGedola30MinutesZmanis = ${AstronomicalCalendar.getTimeOffset(elevation.getChatzos(), elevation.getShaahZmanisAlos16Point1ToTzais3Point7() / 2)}")
            println("elev.sunrise = ${elevation.elevationAdjustedSunrise}, elev.sunset = ${elevation.elevationAdjustedSunset}")
            println("jNoShaaZmanis = $jNoShaaZmanis, jShaaZmanis = $jShaaZmanis")
            assert(jNoElevFixedLocalChatzos != null) { "Mincha gedolah failed for no elevation in $location" }
            assert(jElevFixedLocalChatzos != null) { "Fixed local chatzos failed for elevation in $location" }
            assert(noElevationk.fixedLocalChatzos.momentOfOccurrence != null) { "Fixed local chatzos failed for no elevation in $location" }
            assert(elevationk.fixedLocalChatzos.momentOfOccurrence != null) { "Fixed local chatzos failed for elevation in $location" }
        }
    }
    @Test
    fun alosIsNeverAfterSunrise() {
        for(location in TestHelper.allLocations) {
            val noElevation = ComplexZmanimCalendar(location)
            val elevation = ComplexZmanimCalendar(location).also { it.isUseElevation = true }
            val cals = listOf(elevation, noElevation)
            println("Raw offset for ${location.locationName}: ${location.timeZone.rawOffset} - fixedLocalChatzos = ${cals.map { it.fixedLocalChatzos } }")
            val alos = cals.flatMap { it.allZmanim.filter { it.rules.type == ZmanType.ALOS } }
            val sunrise = cals.flatMap { it.allZmanim.filter { it.rules.type == ZmanType.HANAITZ } }
            alos.forEach {
                for(zman in sunrise) {
                    if(it.momentOfOccurrence != null && zman.momentOfOccurrence != null)
                        assert(it.momentOfOccurrence!! < zman.momentOfOccurrence!!) { "Alos ($it) is after sunrise ($zman) in $location" }
                }
            }
        }
    }
}