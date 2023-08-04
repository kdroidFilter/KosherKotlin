/*
 * Zmanim Java API
 * Copyright (C) 2004-2023 Eliyahu Hershfeld
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General
 * Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful,but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to
 * the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA,
 * or connect to: https://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 */
package com.kosherjava.zmanim

import com.kosherjava.zmanim.util.AstronomicalCalculator
import com.kosherjava.zmanim.util.GeoLocation
import kotlinx.datetime.*
import kotlin.time.Duration.Companion.milliseconds

/**
 * A Java calendar that calculates astronomical times such as [sunrise], [sunset] and twilight times. This class contains a [Calendar][calendar] and can therefore use the standard
 * Calendar functionality to change dates etc. The calculation engine used to calculate the astronomical times can be
 * changed to a different implementation by implementing the abstract [AstronomicalCalculator] and setting it with
 * the [astronomicalCalculator]. A number of different calculation engine
 * implementations are included in the util package.
 * **Note:** There are times when the algorithms can't calculate proper values for sunrise, sunset and twilight. This
 * is usually caused by trying to calculate times for areas either very far North or South, where sunrise / sunset never
 * happen on that date. This is common when calculating twilight with a deep dip below the horizon for locations as far
 * south of the North Pole as London, in the northern hemisphere. The sun never reaches this dip at certain times of the
 * year. When the calculations encounter this condition a null will be returned when a
 * `[Date]` is expected and [Long.MIN_VALUE] when a `long` is expected. The
 * reason that `Exception`s are not thrown in these cases is because the lack of a rise/set or twilight is
 * not an exception, but an expected condition in many parts of the world.
 *
 * Here is a simple example of how to use the API to calculate sunrise.
 * First create the Calendar for the location you would like to calculate sunrise or sunset times for:
 *
 * <pre>
 * String locationName = &quot;Lakewood, NJ&quot;;
 * double latitude = 40.0828; // Lakewood, NJ
 * double longitude = -74.2094; // Lakewood, NJ
 * double elevation = 20; // optional elevation correction in Meters
 * // the String parameter in timeZone has to be a valid timezone listed in
 * // [TimeZone.getAvailableIDs]
 * TimeZone timeZone = TimeZone.getTimeZone(&quot;America/New_York&quot;);
 * GeoLocation location = new GeoLocation(locationName, latitude, longitude, elevation, timeZone);
 * AstronomicalCalendar ac = new AstronomicalCalendar(location);
</pre> *
 *
 * To get the time of sunrise, first set the date you want (if not set, the date will default to today):
 *
 * <pre>
 * ac.calendar.set(Calendar.MONTH, Calendar.FEBRUARY);
 * ac.calendar.set(Calendar.DAY_OF_MONTH, 8);
 * Date sunrise = ac.getSunrise();
</pre> *
 *
 *
 * @author  Eliyahu Hershfeld 2004 - 2023
 */
open class AstronomicalCalendar(

    /**
     * A method that returns the currently set [GeoLocation] which contains location information used for the
     * astronomical calculations.
     *
     * @return Returns the geoLocation.
     */
    /**
     * Sets the [GeoLocation] `Object` to be used for astronomical calculations.
     *
     * @param geoLocation
     * The geoLocation to set.
     * @todo Possibly adjust for horizon elevation. It may be smart to just have the calculator check the GeoLocation
     * though it doesn't really belong there.
     */
    /**
     * the [GeoLocation] used for calculations.
     */
    var geoLocation: GeoLocation
) {

    /**
     * The Java Calendar encapsulated by this class to track the current date used by the class
     */
    var localDateTime: LocalDateTime = Clock.System.now().toLocalDateTime(geoLocation.timeZone)

    /**
     * The internal [AstronomicalCalculator] used for calculating solar based times.
     * A method to set the [AstronomicalCalculator] used for astronomical calculations. The Zmanim package ships
     * with a number of different implementations of the `abstract` [AstronomicalCalculator] based on
     * different algorithms, including the default [com.kosherjava.zmanim.util.NOAACalculator] based on [NOAA's](https://noaa.gov) implementation of Jean Meeus's algorithms as well as [ ] based on the [US
     * Naval Observatory's](https://www.cnmoc.usff.navy.mil/usno/) algorithm,. This allows easy runtime switching and comparison of different algorithms.
     *
     */
    var astronomicalCalculator: AstronomicalCalculator = AstronomicalCalculator.default

    /**
     * The getSunrise method Returns a `Date` representing the
     * [elevation adjusted][AstronomicalCalculator.getElevationAdjustment] sunrise time. The zenith used
     * for the calculation uses [geometric zenith][.GEOMETRIC_ZENITH] of 90 plus
     * [AstronomicalCalculator.getElevationAdjustment]. This is adjusted by the
     * [AstronomicalCalculator] to add approximately 50/60 of a degree to account for 34 archminutes of refraction
     * and 16 archminutes for the sun's radius for a total of [90.83333˚][AstronomicalCalculator.adjustZenith].
     * See documentation for the specific implementation of the [AstronomicalCalculator] that you are using.
     *
     * @return null if sunrise can't be computed (see [getUTCSunrise]).
     * Otherwise, the `Date` representing the exact sunrise time. If the calculation can't be computed such as
     * in the Arctic Circle where there is at least one day a year where the sun does not rise, and one where it
     * does not set, a null will be returned. See detailed explanation on top of the page.
     * @see AstronomicalCalculator.adjustZenith
     *
     * @see [seaLevelSunrise]
     * @see AstronomicalCalendar.getUTCSunrise
     */
    val sunrise: Instant?
        get() = getUTCSunrise(GEOMETRIC_ZENITH)
            .takeUnless { it.isNaN() }
            ?.let { getDateFromTime(it, true) }

    /**
     * A method that returns the sunrise without [elevation][AstronomicalCalculator.getElevationAdjustment]. Non-sunrise and sunset calculations such as dawn and dusk, depend on the amount of visible light,
     * something that is not affected by elevation. This method returns sunrise calculated at sea level. This forms the
     * base for dawn calculations that are calculated as a dip below the horizon before sunrise.
     *
     * @return null if [geoLocation] is null or sunrise can't be computer (see [getUTCSunrise]). (TODO null check: is this a good contract?)
     * Otherwise, the `Date` representing the exact sea-level sunrise time. If the calculation can't be computed
     * such as in the Arctic Circle where there is at least one day a year where the sun does not rise, and one
     * where it does not set, a null will be returned. See detailed explanation on top of the page.
     * @see [sunrise]
     *
     * @see [getUTCSeaLevelSunrise]
     *
     * @see .getSeaLevelSunset
     */
    val seaLevelSunrise: Instant?
        get() = getUTCSeaLevelSunrise(GEOMETRIC_ZENITH)
            .takeUnless { it.isNaN() }
            ?.let { getDateFromTime(it, true) }

    /**
     * A method that returns the beginning of [civil twilight](https://en.wikipedia.org/wiki/Twilight#Civil_twilight)
     * (dawn) using a zenith of [96˚][.CIVIL_ZENITH].
     *
     * @return The `Date` of the beginning of civil twilight using a zenith of 96. If the calculation
     * can't be computed, null will be returned. See detailed explanation on top of the page.
     * @see .CIVIL_ZENITH
     */
    val beginCivilTwilight: Instant?
        get() = getSunriseOffsetByDegrees(CIVIL_ZENITH)

    /**
     * A method that returns the beginning of [nautical twilight](https://en.wikipedia.org/wiki/Twilight#Nautical_twilight) using a zenith of [NAUTICAL_ZENITH].
     *
     * @return The `Date` of the beginning of nautical twilight using a zenith of 102. If the
     * calculation can't be computed null will be returned. See detailed explanation on top of the page.
     * @see .NAUTICAL_ZENITH
     */
    val beginNauticalTwilight: Instant?
        get() = getSunriseOffsetByDegrees(NAUTICAL_ZENITH)

    /**
     * A method that returns the beginning of [astronomical twilight](https://en.wikipedia.org/wiki/Twilight#Astronomical_twilight) using a zenith of
     * [108˚][.ASTRONOMICAL_ZENITH].
     *
     * @return The `Date` of the beginning of astronomical twilight using a zenith of 108. If the
     * calculation can't be computed, null will be returned. See detailed explanation on top of the page.
     * @see .ASTRONOMICAL_ZENITH
     */
    val beginAstronomicalTwilight: Instant?
        get() = getSunriseOffsetByDegrees(ASTRONOMICAL_ZENITH)

    /**
     * The getSunset method Returns a `Date` representing the
     * [elevation adjusted][AstronomicalCalculator.getElevationAdjustment] sunset time. The zenith used for
     * the calculation uses [geometric zenith][.GEOMETRIC_ZENITH] of 90 plus
     * [AstronomicalCalculator.getElevationAdjustment]. This is adjusted by the
     * [AstronomicalCalculator] to add approximately 50/60 of a degree to account for 34 archminutes of refraction
     * and 16 archminutes for the sun's radius for a total of [90.83333˚][AstronomicalCalculator.adjustZenith].
     * See documentation for the specific implementation of the [AstronomicalCalculator] that you are using. Note:
     * In certain cases the calculates sunset will occur before sunrise. This will typically happen when a timezone
     * other than the local timezone is used (calculating Los Angeles sunset using a GMT timezone for example). In this
     * case the sunset date will be incremented to the following date.
     *
     * @return the `Date` representing the exact sunset time. If the calculation can't be computed such as in
     * the Arctic Circle where there is at least one day a year where the sun does not rise, and one where it
     * does not set, a null will be returned. See detailed explanation on top of the page.
     * @see AstronomicalCalculator.adjustZenith
     *
     * @see [seaLevelSunset]
     * @see AstronomicalCalendar.getUTCSunset
     */
    val sunset: Instant?
        get() = getUTCSunset(GEOMETRIC_ZENITH)
            .takeUnless { it.isNaN() }
            ?.let { getDateFromTime(it, false) }

    /**
     * A method that returns the sunset without [elevation][AstronomicalCalculator.getElevationAdjustment]. Non-sunrise and sunset calculations such as dawn and dusk, depend on the amount of visible light,
     * something that is not affected by elevation. This method returns sunset calculated at sea level. This forms the
     * base for dusk calculations that are calculated as a dip below the horizon after sunset.
     *
     * @return the `Date` representing the exact sea-level sunset time. If the calculation can't be computed
     * such as in the Arctic Circle where there is at least one day a year where the sun does not rise, and one
     * where it does not set, a null will be returned. See detailed explanation on top of the page.
     * @see AstronomicalCalendar.sunset
     *
     * @see AstronomicalCalendar.getUTCSeaLevelSunset
     * @see sunset
     */
    val seaLevelSunset: Instant?
        get() = getUTCSeaLevelSunset(GEOMETRIC_ZENITH)
            .takeUnless { it.isNaN() }
            ?.let { getDateFromTime(it, false) }

    /**
     * A method that returns the end of [civil twilight](https://en.wikipedia.org/wiki/Twilight#Civil_twilight)
     * using a zenith of [96˚][.CIVIL_ZENITH].
     *
     * @return The `Date` of the end of civil twilight using a zenith of [96˚][.CIVIL_ZENITH]. If
     * the calculation can't be computed, null will be returned. See detailed explanation on top of the page.
     * @see CIVIL_ZENITH
     */
    val endCivilTwilight: Instant?
        get() = getSunsetOffsetByDegrees(CIVIL_ZENITH)

    /**
     * A method that returns the end of nautical twilight using a zenith of [102˚][.NAUTICAL_ZENITH].
     *
     * @return The `Date` of the end of nautical twilight using a zenith of [102˚][.NAUTICAL_ZENITH]
     * . If the calculation can't be computed, null will be returned. See detailed explanation on top of the
     * page.
     * @see NAUTICAL_ZENITH
     */
    val endNauticalTwilight: Instant?
        get() = getSunsetOffsetByDegrees(NAUTICAL_ZENITH)

    /**
     * A method that returns the end of astronomical twilight using a zenith of [108˚][.ASTRONOMICAL_ZENITH].
     *
     * @return the `Date` of the end of astronomical twilight using a zenith of [         108˚][.ASTRONOMICAL_ZENITH]. If the calculation can't be computed, null will be returned. See detailed explanation on top
     * of the page.
     * @see .ASTRONOMICAL_ZENITH
     */
    val endAstronomicalTwilight: Instant?
        get() = getSunsetOffsetByDegrees(ASTRONOMICAL_ZENITH)

    /**
     * A utility method that returns the time of an offset by degrees below or above the horizon of
     * [sunrise]. Note that the degree offset is from the vertical, so for a calculation of 14
     * before sunrise, an offset of 14 + [.GEOMETRIC_ZENITH] = 104 would have to be passed as a parameter.
     *
     * @param offsetZenith
     * the degrees before [sunrise] to use in the calculation. For time after sunrise use
     * negative numbers. Note that the degree offset is from the vertical, so for a calculation of 14
     * before sunrise, an offset of 14 + [.GEOMETRIC_ZENITH] = 104 would have to be passed as a
     * parameter.
     * @return The [Date] of the offset after (or before) [sunrise]. If the calculation
     * can't be computed such as in the Arctic Circle where there is at least one day a year where the sun does
     * not rise, and one where it does not set, a null will be returned. See detailed explanation on top of the
     * page.
     */
    fun getSunriseOffsetByDegrees(offsetZenith: Double): Instant? =
        getUTCSunrise(offsetZenith)
            .takeUnless { it.isNaN() }
            ?.let { getDateFromTime(it, true) }

    /**
     * A utility method that returns the time of an offset by degrees below or above the horizon of [ sunset][.getSunset]. Note that the degree offset is from the vertical, so for a calculation of 14 after sunset, an
     * offset of 14 + [.GEOMETRIC_ZENITH] = 104 would have to be passed as a parameter.
     *
     * @param offsetZenith
     * the degrees after [sunset] to use in the calculation. For time before sunset use negative
     * numbers. Note that the degree offset is from the vertical, so for a calculation of 14 after
     * sunset, an offset of 14 + [.GEOMETRIC_ZENITH] = 104 would have to be passed as a parameter.
     * @return The [Date]of the offset after (or before) [sunset]. If the calculation can't
     * be computed such as in the Arctic Circle where there is at least one day a year where the sun does not
     * rise, and one where it does not set, a null will be returned. See detailed explanation on top of the
     * page.
     */
    fun getSunsetOffsetByDegrees(offsetZenith: Double): Instant? =
        getUTCSunset(offsetZenith)
            .takeUnless { it.isNaN() }
            ?.let { getDateFromTime(it, false) }
    /**
     * A constructor that takes in [geolocation](https://en.wikipedia.org/wiki/Geolocation) information as a
     * parameter. The default [AstronomicalCalculator][AstronomicalCalculator.default] used for solar
     * calculations is the the [com.kosherjava.zmanim.util.NOAACalculator].
     *
     * @param geoLocation
     * The location information used for calculating astronomical sun times.
     *
     * @see .setAstronomicalCalculator
     */
    /**
     * Default constructor will set a default [geoLocation], a default
     * [AstronomicalCalculator][AstronomicalCalculator.default] and default the LocalDate to the current date.
     *//*
    init {
        astronomicalCalculator
    }*/

    /**
     * A method that returns the sunrise in UTC time without correction for time zone offset from GMT and without using
     * daylight savings time.
     *
     * @param zenith
     * the degrees below the horizon. For time after sunrise use negative numbers.
     * @return The time in the format: 18.75 for 18:45:00 UTC/GMT. If the calculation can't be computed such as in the
     * Arctic Circle where there is at least one day a year where the sun does not rise, and one where it does
     * not set, [Double.NaN] will be returned. See detailed explanation on top of the page.
     */
    fun getUTCSunrise(zenith: Double): Double =
        astronomicalCalculator.getUTCSunrise(adjustedLocalDate.date, geoLocation, zenith, true)

    /**
     * A method that returns the sunrise in UTC time without correction for time zone offset from GMT and without using
     * daylight savings time. Non-sunrise and sunset calculations such as dawn and dusk, depend on the amount of visible
     * light, something that is not affected by elevation. This method returns UTC sunrise calculated at sea level. This
     * forms the base for dawn calculations that are calculated as a dip below the horizon before sunrise.
     *
     * @param zenith
     * the degrees below the horizon. For time after sunrise use negative numbers.
     * @return The time in the format: 18.75 for 18:45:00 UTC/GMT. If the calculation can't be computed such as in the
     * Arctic Circle where there is at least one day a year where the sun does not rise, and one where it does
     * not set, [Double.NaN] will be returned. See detailed explanation on top of the page.
     * @see AstronomicalCalendar.getUTCSunrise
     *
     * @see AstronomicalCalendar.getUTCSeaLevelSunset
     */
    fun getUTCSeaLevelSunrise(zenith: Double): Double =
        astronomicalCalculator.getUTCSunrise(adjustedLocalDate.date, geoLocation, zenith, false)

    /**
     * A method that returns the sunset in UTC time without correction for time zone offset from GMT and without using
     * daylight savings time.
     *
     * @param zenith
     * the degrees below the horizon. For time after sunset use negative numbers.
     * @return The time in the format: 18.75 for 18:45:00 UTC/GMT. If the calculation can't be computed such as in the
     * Arctic Circle where there is at least one day a year where the sun does not rise, and one where it does
     * not set, [Double.NaN] will be returned. See detailed explanation on top of the page.
     * @see AstronomicalCalendar.getUTCSeaLevelSunset
     */
    fun getUTCSunset(zenith: Double): Double =
        astronomicalCalculator.getUTCSunset(adjustedLocalDate.date, geoLocation, zenith, true)

    /**
     * A method that returns the sunset in UTC time without correction for elevation, time zone offset from GMT and
     * without using daylight savings time. Non-sunrise and sunset calculations such as dawn and dusk, depend on the
     * amount of visible light, something that is not affected by elevation. This method returns UTC sunset calculated
     * at sea level. This forms the base for dusk calculations that are calculated as a dip below the horizon after
     * sunset.
     *
     * @param zenith
     * the degrees below the horizon. For time before sunset use negative numbers.
     * @return The time in the format: 18.75 for 18:45:00 UTC/GMT. If the calculation can't be computed such as in the
     * Arctic Circle where there is at least one day a year where the sun does not rise, and one where it does
     * not set, [Double.NaN] will be returned. See detailed explanation on top of the page.
     * @see AstronomicalCalendar.getUTCSunset
     *
     * @see AstronomicalCalendar.getUTCSeaLevelSunrise
     */
    fun getUTCSeaLevelSunset(zenith: Double): Double =
        astronomicalCalculator.getUTCSunset(adjustedLocalDate.date, geoLocation, zenith, false)

    /**
     * A method that returns an [elevation adjusted][AstronomicalCalculator.getElevationAdjustment]
     * temporal (solar) hour. The day from [sunrise] to [sunset] is split into 12
     * equal parts with each one being a temporal hour.
     *
     * @see .getSunrise
     * @see .getSunset
     * @see .getTemporalHour
     * @return the `long` millisecond length of a temporal hour. If the calculation can't be computed,
     * [Long.MIN_VALUE] will be returned. See detailed explanation on top of the page.
     *
     * @see .getTemporalHour
     */
    val temporalHour: Long
        get() = getTemporalHour(seaLevelSunrise, seaLevelSunset)

    /**
     * A utility method that will allow the calculation of a temporal (solar) hour based on the sunrise and sunset
     * passed as parameters to this method. An example of the use of this method would be the calculation of a
     * non-elevation adjusted temporal hour by passing in [sea level sunrise][seaLevelSunrise] and
     * [sea level sunset][seaLevelSunset] as parameters.
     *
     * @param startOfday
     * The start of the day.
     * @param endOfDay
     * The end of the day.
     *
     * @return the `long` millisecond length of the temporal hour. If the calculation can't be computed a
     * [Long.MIN_VALUE] will be returned. See detailed explanation on top of the page.
     *
     * @see .getTemporalHour
     */
    fun getTemporalHour(startOfday: Instant?, endOfDay: Instant?): Long {
        if (startOfday == null || endOfDay == null) {
            return Long.MIN_VALUE
        }
        return (endOfDay - startOfday).div(12).inWholeMilliseconds
    }

    /**
     * A method that returns sundial or solar noon. It occurs when the Sun is [transiting](https://en.wikipedia.org/wiki/Transit_%28astronomy%29) the [celestial meridian](https://en.wikipedia.org/wiki/Meridian_%28astronomy%29). The calculations used by
     * this class depend on the [AstronomicalCalculator] used. If this calendar instance is [astronomicalCalculator] to use the [com.kosherjava.zmanim.util.NOAACalculator]
     * (the default) it will calculate astronomical noon. If the calendar instance is  to use the
     * [com.kosherjava.zmanim.util.SunTimesCalculator], that does not have code to calculate astronomical noon, the
     * sun transit is calculated as halfway between sea level sunrise and sea level sunset, which can be slightly off the
     * real transit time due to changes in declination (the lengthening or shortening day). See [The Definition of Chatzos](https://kosherjava.com/2020/07/02/definition-of-chatzos/) for details on the proper
     * definition of solar noon / midday.
     *
     * @return The `Date` representing Sun's transit. If the calculation can't be computed such as in the
     * Arctic Circle where there is at least one day a year where the sun does not rise, and one where it does
     * not set, null will be returned. See detailed explanation on top of the page.
     * @see .getSunTransit
     * @see .getTemporalHour
     */
    val sunTransit: Instant?
        get() = astronomicalCalculator
            .getUTCNoon(adjustedLocalDate.date, geoLocation)
            .takeUnless { it.isNaN() }
            ?.let { getDateFromTime(it, false) }

    /**
     * A method that returns sundial or solar noon. It occurs when the Sun is [transiting](https://en.wikipedia.org/wiki/Transit_%28astronomy%29) the [celestial meridian](https://en.wikipedia.org/wiki/Meridian_%28astronomy%29). In this class it is
     * calculated as halfway between the sunrise and sunset passed to this method. This time can be slightly off the
     * real transit time due to changes in declination (the lengthening or shortening day).
     *
     * @param startOfDay
     * the start of day for calculating the sun's transit. This can be sea level sunrise, visual sunrise (or
     * any arbitrary start of day) passed to this method.
     * @param endOfDay
     * the end of day for calculating the sun's transit. This can be sea level sunset, visual sunset (or any
     * arbitrary end of day) passed to this method.
     *
     * @return the `Date` representing Sun's transit. If the calculation can't be computed such as in the
     * Arctic Circle where there is at least one day a year where the sun does not rise, and one where it does
     * not set, null will be returned. See detailed explanation on top of the page.
     */
    fun getSunTransit(startOfDay: Instant?, endOfDay: Instant?): Instant? =
        getTimeOffset(startOfDay, getTemporalHour(startOfDay, endOfDay) * 6)

    /**
     * A method that returns a [Date] from the time passed in as a parameter.
     *
     * @param time
     * The time to be set as the time for the `Date`. The time expected is in the format: 18.75
     * for 6:45:00 PM.
     * @param isSunrise true if the time is sunrise and false if it is sunset
     * @return The Date. null if [time] is [Double.NaN]
     */
    protected fun getDateFromTime(time: Double, isSunrise: Boolean): Instant? {
        if (time.isNaN()) {
            return null
        }
        var calculatedTime: Double = time
        val adjustedLocalDate = this.adjustedLocalDate
        val timeZone = TimeZone.UTC
        var cal = adjustedLocalDate.toInstant(timeZone)
        val hours = calculatedTime.toInt() // retain only the hours
        calculatedTime -= hours.toDouble()

        // retain only the minutes
        calculatedTime *= 60
        val minutes = calculatedTime.toInt()
        calculatedTime -= minutes.toDouble()

        // retain only the seconds
        calculatedTime *= 60
        val seconds = calculatedTime.toInt()
        calculatedTime -= seconds.toDouble() // remaining milliseconds

        // Check if a date transition has occurred, or is about to occur - this indicates the date of the event is
        // actually not the target date, but the day prior or after
        val localTimeHours = (geoLocation.longitude / 15).toInt()
        if (isSunrise && localTimeHours.plus(hours) > 18)
            cal = cal.plus(DatePeriod(days = -1), timeZone)
        else if (!isSunrise && localTimeHours.plus(hours) < 6)
            cal = cal.plus(DatePeriod(days = 1), timeZone)
        val dt = cal.toLocalDateTime(timeZone)
        return LocalDateTime(dt.year, dt.month, dt.dayOfMonth, hours, minutes, seconds, (calculatedTime * 1000).milliseconds.inWholeNanoseconds.toInt()).toInstant(timeZone)
    }

    /**
     * Returns the dip below the horizon before sunrise that matches the offset minutes on passed in as a parameter. For
     * example passing in 72 minutes for a calendar set to the equinox in Jerusalem returns a value close to 16.1
     * Please note that this method is very slow and inefficient and should NEVER be used in a loop. TODO: Improve
     * efficiency.
     *
     * @param minutes
     * offset
     * @return the degrees below the horizon before sunrise that match the offset in minutes passed it as a parameter.
     * @see .getSunsetSolarDipFromOffset
     */
    fun getSunriseSolarDipFromOffset(minutes: Double): Double {
        var offsetByDegrees: Instant? = seaLevelSunrise
        val offsetByTime: Instant? = getTimeOffset(
            seaLevelSunrise, -(minutes * MINUTE_MILLIS)
        )
        var degrees = 0.0
        val incrementor = 0.0001 //TODO upstream uses BigDecimal
        while (
            offsetByDegrees == null ||
            (
                    (minutes < 0.0 && offsetByDegrees < offsetByTime!!) ||
                            (minutes > 0.0 && offsetByDegrees > offsetByTime!!)
                    )
        ) {
            degrees = if (minutes > 0.0) degrees + incrementor else degrees - incrementor
            offsetByDegrees = getSunriseOffsetByDegrees(GEOMETRIC_ZENITH + degrees)
        }
        return degrees
    }

    /**
     * Returns the dip below the horizon after sunset that matches the offset minutes on passed in as a parameter. For
     * example passing in 72 minutes for a calendar set to the equinox in Jerusalem returns a value close to 16.1
     * Please note that this method is very slow and inefficient and should NEVER be used in a loop. TODO: Improve
     * efficiency.
     *
     * @param minutes
     * offset
     * @return the degrees below the horizon after sunset that match the offset in minutes passed it as a parameter.
     * @see .getSunriseSolarDipFromOffset
     */
    fun getSunsetSolarDipFromOffset(minutes: Double): Double {
        var offsetByDegrees: Instant? = seaLevelSunset
        val offsetByTime: Instant? = getTimeOffset(
            seaLevelSunset, minutes * MINUTE_MILLIS
        )
        var degrees = 0.0
        val incrementor = 0.001 //TODO upstream uses BigDecimal
        while (
            offsetByDegrees == null ||
            minutes > 0.0 && offsetByDegrees < offsetByTime!! ||
            minutes < 0.0 && offsetByDegrees > offsetByTime!!
        ) {
            degrees = if (minutes > 0.0) degrees + incrementor else degrees - incrementor
            offsetByDegrees = getSunsetOffsetByDegrees(GEOMETRIC_ZENITH + degrees)
        }
        return degrees
    }

    /**
     * Adjusts the `LocalDate` to deal with edge cases where the location crosses the antimeridian.
     *
     * @see GeoLocation.antimeridianAdjustment
     * @return the adjusted LocalDate
     */
    internal val adjustedLocalDate: LocalDateTime
        get() {
            val offset = geoLocation.antimeridianAdjustment
            return if (offset == 0) localDateTime
            else localDateTime
                .toInstant(geoLocation.timeZone)
                .plus(DatePeriod(days = offset), geoLocation.timeZone)
                .toLocalDateTime(geoLocation.timeZone)
        }

    /**
     * @return an XML formatted representation of the class. It returns the default output of the
     * [toXML][com.kosherjava.zmanim.util.ZmanimFormatter.toXML] method.
     * @see com.kosherjava.zmanim.util.ZmanimFormatter.toXML
     * @see Object.toString
     */
//    override fun toString(): String = ZmanimFormatter.toXML(this)

    /**
     * @return a JSON formatted representation of the class. It returns the default output of the
     * [toJSON][com.kosherjava.zmanim.util.ZmanimFormatter.toJSON] method.
     * @see com.kosherjava.zmanim.util.ZmanimFormatter.toJSON
     * @see Object.toString
     */
//    fun toJSON(): String = ZmanimFormatter.toJSON(this)

    /**
     * @see Object.equals
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AstronomicalCalendar) return false
        return localDateTime == other.localDateTime &&
                geoLocation == other.geoLocation &&
                astronomicalCalculator == other.astronomicalCalculator
    }

    /**
     * @see Object.hashCode
     */
    override fun hashCode(): Int {
        var result = 17
        result =
            37 * result + this::class.hashCode() // needed or this and subclasses will return identical hash
        result += 37 * result + localDateTime.hashCode()
        result += 37 * result + geoLocation.hashCode()
        result += 37 * result + astronomicalCalculator.hashCode()
        return result
    }

    /**
     * A method that creates a [deep copy](https://en.wikipedia.org/wiki/Object_copy#Deep_copy) of the object.
     *
     * @see Object.clone
     */
    fun copy(): AstronomicalCalendar = AstronomicalCalendar(geoLocation.copy()).apply { //TODO copy over date once superclass constructors are figured out
        this.astronomicalCalculator = astronomicalCalculator.copy()
    }

    companion object {
        /**
         * 90 below the vertical. Used as a basis for most calculations since the location of the sun is 90 below
         * the horizon at sunrise and sunset.
         * **Note **: it is important to note that for sunrise and sunset the [ adjusted zenith][AstronomicalCalculator.adjustZenith] is required to account for the radius of the sun and refraction. The adjusted zenith should not
         * be used for calculations above or below 90 since they are usually calculated as an offset to 90.
         */
        const val GEOMETRIC_ZENITH: Double = 90.0

        /** Sun's zenith at civil twilight (96).  */
        const val CIVIL_ZENITH: Double = 96.0

        /** Sun's zenith at nautical twilight (102).  */
        const val NAUTICAL_ZENITH: Double = 102.0

        /** Sun's zenith at astronomical twilight (108).  */
        const val ASTRONOMICAL_ZENITH: Double = 108.0

        /** constant for milliseconds in a minute (60,000)  */
        const val MINUTE_MILLIS: Long = (60 * 1000).toLong()

        /** constant for milliseconds in an hour (3,600,000)  */
        const val HOUR_MILLIS: Long = MINUTE_MILLIS * 60

        /**
         * A utility method that returns a date offset by the offset time passed in as a parameter. This method casts the
         * offset as a `long` and calls [getTimeOffset].
         *
         * @param time
         * the start time
         * @param offset
         * the offset in milliseconds to add to the time
         * @return the [Date]with the offset added to it
         */
        fun getTimeOffset(time: Instant?, offset: Double): Instant? =
            getTimeOffset(time, offset.toLong())

        /**
         * A utility method that returns a date offset by the offset time passed in. Please note that the level of light
         * during twilight is not affected by elevation, so if this is being used to calculate an offset before sunrise or
         * after sunset with the intent of getting a rough "level of light" calculation, the sunrise or sunset time passed
         * to this method should be sea level sunrise and sunset.
         *
         * @param time
         * the start time
         * @param offset
         * the offset in milliseconds to add to the time.
         * @return the [Date] with the offset in milliseconds added to it, or null if [time] is null or [offset] is [Long.MIN_VALUE]
         */
        fun getTimeOffset(time: Instant?, offset: Long): Instant? {
            if (time == null || offset == Long.MIN_VALUE) {
                return null
            }
            return time + offset.milliseconds
        }
    }
}