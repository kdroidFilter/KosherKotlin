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
import com.kosherjava.zmanim.util.ZmanimFormatter
import java.math.BigDecimal
import java.util.*

/**
 * A Java calendar that calculates astronomical times such as [sunrise][.getSunrise], [ sunset][.getSunset] and twilight times. This class contains a [Calendar][.getCalendar] and can therefore use the standard
 * Calendar functionality to change dates etc. The calculation engine used to calculate the astronomical times can be
 * changed to a different implementation by implementing the abstract [AstronomicalCalculator] and setting it with
 * the [.setAstronomicalCalculator]. A number of different calculation engine
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
open class AstronomicalCalendar @JvmOverloads constructor(geoLocation: GeoLocation = GeoLocation()) : Cloneable {
    /**
     * The Java Calendar encapsulated by this class to track the current date used by the class
     */
    var calendar: Calendar = Calendar.getInstance(geoLocation.timeZone) //TODO null check: can this ever be null?
        set(calendar) {
            field = calendar
            if (geoLocation != null) { // if available set the Calendar's timezone to the GeoLocation TimeZone
                calendar.timeZone = geoLocation!!.timeZone
            }
        }
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
    var geoLocation: GeoLocation? = null
        set(geoLocation) {
            field = geoLocation
            calendar.timeZone = geoLocation?.timeZone
        }
    /**
     * A method that returns the currently set AstronomicalCalculator.
     *
     * @return Returns the astronomicalCalculator.
     * @see .setAstronomicalCalculator
     */
    /**
     * A method to set the [AstronomicalCalculator] used for astronomical calculations. The Zmanim package ships
     * with a number of different implementations of the `abstract` [AstronomicalCalculator] based on
     * different algorithms, including the default [com.kosherjava.zmanim.util.NOAACalculator] based on [NOAA's](https://noaa.gov) implementation of Jean Meeus's algorithms as well as [ ] based on the [US
 * Naval Observatory's](https://www.cnmoc.usff.navy.mil/usno/) algorithm,. This allows easy runtime switching and comparison of different algorithms.
     *
     * @param astronomicalCalculator
     * The astronomicalCalculator to set.
     */
    /**
     * the internal [AstronomicalCalculator] used for calculating solar based times.
     * TODO make val - is only var for [clone]
     */
    var astronomicalCalculator: AstronomicalCalculator

    /**
     * The getSunrise method Returns a `Date` representing the
     * [elevation adjusted][AstronomicalCalculator.getElevationAdjustment] sunrise time. The zenith used
     * for the calculation uses [geometric zenith][.GEOMETRIC_ZENITH] of 90 plus
     * [AstronomicalCalculator.getElevationAdjustment]. This is adjusted by the
     * [AstronomicalCalculator] to add approximately 50/60 of a degree to account for 34 archminutes of refraction
     * and 16 archminutes for the sun's radius for a total of [90.83333&amp;deg;][AstronomicalCalculator.adjustZenith].
     * See documentation for the specific implementation of the [AstronomicalCalculator] that you are using.
     *
     * @return null if [geoLocation] is null or sunrise can't be computer (see [getUTCSunrise]). (TODO null check: is this a good contract?)
     * Otherwise, the `Date` representing the exact sunrise time. If the calculation can't be computed such as
     * in the Arctic Circle where there is at least one day a year where the sun does not rise, and one where it
     * does not set, a null will be returned. See detailed explanation on top of the page.
     * @see AstronomicalCalculator.adjustZenith
     *
     * @see [seaLevelSunrise]
     * @see AstronomicalCalendar.getUTCSunrise
     */
    val sunrise: Date?
        get() {
            val sunrise: Double? = getUTCSunrise(GEOMETRIC_ZENITH)
            return if (sunrise == null || sunrise.isNaN()) null else getDateFromTime(sunrise, true)
        }

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
    val seaLevelSunrise: Date?
        get() {
            val sunrise: Double? =
                getUTCSeaLevelSunrise(GEOMETRIC_ZENITH)
            return if (sunrise == null || sunrise.isNaN()) null else getDateFromTime(sunrise, true)
        }

    /**
     * A method that returns the beginning of [civil twilight](https://en.wikipedia.org/wiki/Twilight#Civil_twilight)
     * (dawn) using a zenith of [96&amp;deg;][.CIVIL_ZENITH].
     *
     * @return The `Date` of the beginning of civil twilight using a zenith of 96. If the calculation
     * can't be computed, null will be returned. See detailed explanation on top of the page.
     * @see .CIVIL_ZENITH
     */
    val beginCivilTwilight: Date?
        get() {
            return getSunriseOffsetByDegrees(CIVIL_ZENITH)
        }

    /**
     * A method that returns the beginning of [nautical twilight](https://en.wikipedia.org/wiki/Twilight#Nautical_twilight) using a zenith of [ ][.NAUTICAL_ZENITH].
     *
     * @return The `Date` of the beginning of nautical twilight using a zenith of 102. If the
     * calculation can't be computed null will be returned. See detailed explanation on top of the page.
     * @see .NAUTICAL_ZENITH
     */
    val beginNauticalTwilight: Date?
        get() {
            return getSunriseOffsetByDegrees(com.kosherjava.zmanim.AstronomicalCalendar.Companion.NAUTICAL_ZENITH)
        }

    /**
     * A method that returns the beginning of [astronomical twilight](https://en.wikipedia.org/wiki/Twilight#Astronomical_twilight) using a zenith of
     * [108&amp;deg;][.ASTRONOMICAL_ZENITH].
     *
     * @return The `Date` of the beginning of astronomical twilight using a zenith of 108. If the
     * calculation can't be computed, null will be returned. See detailed explanation on top of the page.
     * @see .ASTRONOMICAL_ZENITH
     */
    val beginAstronomicalTwilight: Date?
        get() {
            return getSunriseOffsetByDegrees(ASTRONOMICAL_ZENITH)
        }

    /**
     * The getSunset method Returns a `Date` representing the
     * [elevation adjusted][AstronomicalCalculator.getElevationAdjustment] sunset time. The zenith used for
     * the calculation uses [geometric zenith][.GEOMETRIC_ZENITH] of 90 plus
     * [AstronomicalCalculator.getElevationAdjustment]. This is adjusted by the
     * [AstronomicalCalculator] to add approximately 50/60 of a degree to account for 34 archminutes of refraction
     * and 16 archminutes for the sun's radius for a total of [90.83333&amp;deg;][AstronomicalCalculator.adjustZenith].
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
    val sunset: Date?
        get() {
            val sunset: Double? = getUTCSunset(GEOMETRIC_ZENITH)
            return if (sunset == null || sunset.isNaN()) null else getDateFromTime(sunset, false)
        }

    /**
     * A method that returns the sunset without [elevation][AstronomicalCalculator.getElevationAdjustment]. Non-sunrise and sunset calculations such as dawn and dusk, depend on the amount of visible light,
     * something that is not affected by elevation. This method returns sunset calculated at sea level. This forms the
     * base for dusk calculations that are calculated as a dip below the horizon after sunset.
     *
     * @return the `Date` representing the exact sea-level sunset time. If the calculation can't be computed
     * such as in the Arctic Circle where there is at least one day a year where the sun does not rise, and one
     * where it does not set, a null will be returned. See detailed explanation on top of the page.
     * @see AstronomicalCalendar.getSunset
     *
     * @see AstronomicalCalendar.getUTCSeaLevelSunset 2see {@link .getSunset
     */
    val seaLevelSunset: Date?
        get() {
            val sunset: Double? =
                getUTCSeaLevelSunset(GEOMETRIC_ZENITH)
            return if (sunset == null || sunset.isNaN()) null else getDateFromTime(sunset, false)
        }

    /**
     * A method that returns the end of [civil twilight](https://en.wikipedia.org/wiki/Twilight#Civil_twilight)
     * using a zenith of [96&amp;deg;][.CIVIL_ZENITH].
     *
     * @return The `Date` of the end of civil twilight using a zenith of [96&amp;deg;][.CIVIL_ZENITH]. If
     * the calculation can't be computed, null will be returned. See detailed explanation on top of the page.
     * @see .CIVIL_ZENITH
     */
    val endCivilTwilight: Date?
        get() {
            return getSunsetOffsetByDegrees(com.kosherjava.zmanim.AstronomicalCalendar.Companion.CIVIL_ZENITH)
        }

    /**
     * A method that returns the end of nautical twilight using a zenith of [102&amp;deg;][.NAUTICAL_ZENITH].
     *
     * @return The `Date` of the end of nautical twilight using a zenith of [102&amp;deg;][.NAUTICAL_ZENITH]
     * . If the calculation can't be computed, null will be returned. See detailed explanation on top of the
     * page.
     * @see .NAUTICAL_ZENITH
     */
    val endNauticalTwilight: Date?
        get() {
            return getSunsetOffsetByDegrees(com.kosherjava.zmanim.AstronomicalCalendar.Companion.NAUTICAL_ZENITH)
        }

    /**
     * A method that returns the end of astronomical twilight using a zenith of [108&amp;deg;][.ASTRONOMICAL_ZENITH].
     *
     * @return the `Date` of the end of astronomical twilight using a zenith of [         108&amp;deg;][.ASTRONOMICAL_ZENITH]. If the calculation can't be computed, null will be returned. See detailed explanation on top
     * of the page.
     * @see .ASTRONOMICAL_ZENITH
     */
    val endAstronomicalTwilight: Date?
        get() {
            return getSunsetOffsetByDegrees(ASTRONOMICAL_ZENITH)
        }

    /**
     * A utility method that returns the time of an offset by degrees below or above the horizon of
     * [sunrise][.getSunrise]. Note that the degree offset is from the vertical, so for a calculation of 14
     * before sunrise, an offset of 14 + [.GEOMETRIC_ZENITH] = 104 would have to be passed as a parameter.
     *
     * @param offsetZenith
     * the degrees before [.getSunrise] to use in the calculation. For time after sunrise use
     * negative numbers. Note that the degree offset is from the vertical, so for a calculation of 14
     * before sunrise, an offset of 14 + [.GEOMETRIC_ZENITH] = 104 would have to be passed as a
     * parameter.
     * @return The [Date] of the offset after (or before) [.getSunrise]. If the calculation
     * can't be computed such as in the Arctic Circle where there is at least one day a year where the sun does
     * not rise, and one where it does not set, a null will be returned. See detailed explanation on top of the
     * page.
     */
    fun getSunriseOffsetByDegrees(offsetZenith: Double): Date? {
        val dawn: Double? = getUTCSunrise(offsetZenith)
        return if (dawn == null || dawn.isNaN()) null else getDateFromTime(dawn, true)
    }

    /**
     * A utility method that returns the time of an offset by degrees below or above the horizon of [ sunset][.getSunset]. Note that the degree offset is from the vertical, so for a calculation of 14 after sunset, an
     * offset of 14 + [.GEOMETRIC_ZENITH] = 104 would have to be passed as a parameter.
     *
     * @param offsetZenith
     * the degrees after [.getSunset] to use in the calculation. For time before sunset use negative
     * numbers. Note that the degree offset is from the vertical, so for a calculation of 14 after
     * sunset, an offset of 14 + [.GEOMETRIC_ZENITH] = 104 would have to be passed as a parameter.
     * @return The [Date]of the offset after (or before) [.getSunset]. If the calculation can't
     * be computed such as in the Arctic Circle where there is at least one day a year where the sun does not
     * rise, and one where it does not set, a null will be returned. See detailed explanation on top of the
     * page.
     */
    fun getSunsetOffsetByDegrees(offsetZenith: Double): Date? {
        val sunset: Double? = getUTCSunset(offsetZenith)
        return if (sunset == null || sunset.isNaN()) null else getDateFromTime(sunset, false)
    }
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
     * Default constructor will set a default [GeoLocation.GeoLocation], a default
     * [AstronomicalCalculator][AstronomicalCalculator.getDefault] and default the calendar to the current date.
     */
    init {
        this.geoLocation = geoLocation // duplicate call
        astronomicalCalculator = AstronomicalCalculator.default
    }

    /**
     * A method that returns the sunrise in UTC time without correction for time zone offset from GMT and without using
     * daylight savings time.
     *
     * @param zenith
     * the degrees below the horizon. For time after sunrise use negative numbers.
     * @return null if [geoLocation] is null. (TODO null check: is this a good contract?)
     * Otherwise, The time in the format: 18.75 for 18:45:00 UTC/GMT. If the calculation can't be computed such as in the
     * Arctic Circle where there is at least one day a year where the sun does not rise, and one where it does
     * not set, [Double.NaN] will be returned. See detailed explanation on top of the page.
     */
    fun getUTCSunrise(zenith: Double): Double? {
        return geoLocation?.let { astronomicalCalculator.getUTCSunrise(adjustedCalendar, it, zenith, true) }
    }

    /**
     * A method that returns the sunrise in UTC time without correction for time zone offset from GMT and without using
     * daylight savings time. Non-sunrise and sunset calculations such as dawn and dusk, depend on the amount of visible
     * light, something that is not affected by elevation. This method returns UTC sunrise calculated at sea level. This
     * forms the base for dawn calculations that are calculated as a dip below the horizon before sunrise.
     *
     * @param zenith
     * the degrees below the horizon. For time after sunrise use negative numbers.
     * @return null if [geoLocation] is null. (TODO null check: is this a good contract?)
     * Otherwise, the time in the format: 18.75 for 18:45:00 UTC/GMT. If the calculation can't be computed such as in the
     * Arctic Circle where there is at least one day a year where the sun does not rise, and one where it does
     * not set, [Double.NaN] will be returned. See detailed explanation on top of the page.
     * @see AstronomicalCalendar.getUTCSunrise
     *
     * @see AstronomicalCalendar.getUTCSeaLevelSunset
     */
    fun getUTCSeaLevelSunrise(zenith: Double): Double? {
        return geoLocation?.let { astronomicalCalculator.getUTCSunrise(adjustedCalendar, it, zenith, false) }
    }

    /**
     * A method that returns the sunset in UTC time without correction for time zone offset from GMT and without using
     * daylight savings time.
     *
     * @param zenith
     * the degrees below the horizon. For time after sunset use negative numbers.
     * @return null if [geoLocation] is null. (TODO null check: is this a good contract?)
     * Otherwise, the time in the format: 18.75 for 18:45:00 UTC/GMT. If the calculation can't be computed such as in the
     * Arctic Circle where there is at least one day a year where the sun does not rise, and one where it does
     * not set, [Double.NaN] will be returned. See detailed explanation on top of the page.
     * @see AstronomicalCalendar.getUTCSeaLevelSunset
     */
    fun getUTCSunset(zenith: Double): Double? {
        return geoLocation?.let { astronomicalCalculator.getUTCSunset(adjustedCalendar, it, zenith, true) }
    }

    /**
     * A method that returns the sunset in UTC time without correction for elevation, time zone offset from GMT and
     * without using daylight savings time. Non-sunrise and sunset calculations such as dawn and dusk, depend on the
     * amount of visible light, something that is not affected by elevation. This method returns UTC sunset calculated
     * at sea level. This forms the base for dusk calculations that are calculated as a dip below the horizon after
     * sunset.
     *
     * @param zenith
     * the degrees below the horizon. For time before sunset use negative numbers.
     * @return null if [geoLocation] is null. (TODO null check: is this a good contract?)
     * Otherwise, the time in the format: 18.75 for 18:45:00 UTC/GMT. If the calculation can't be computed such as in the
     * Arctic Circle where there is at least one day a year where the sun does not rise, and one where it does
     * not set, [Double.NaN] will be returned. See detailed explanation on top of the page.
     * @see AstronomicalCalendar.getUTCSunset
     *
     * @see AstronomicalCalendar.getUTCSeaLevelSunrise
     */
    fun getUTCSeaLevelSunset(zenith: Double): Double? {
        return geoLocation?.let { astronomicalCalculator.getUTCSunset(adjustedCalendar, it, zenith, false) }
    }

    /**
     * A method that returns an [elevation adjusted][AstronomicalCalculator.getElevationAdjustment]
     * temporal (solar) hour. The day from [sunrise][.getSunrise] to [sunset][.getSunset] is split into 12
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
        get() {
            return getTemporalHour(seaLevelSunrise, seaLevelSunset)
        }

    /**
     * A utility method that will allow the calculation of a temporal (solar) hour based on the sunrise and sunset
     * passed as parameters to this method. An example of the use of this method would be the calculation of a
     * non-elevation adjusted temporal hour by passing in [sea level sunrise][.getSeaLevelSunrise] and
     * [sea level sunset][.getSeaLevelSunset] as parameters.
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
    fun getTemporalHour(startOfday: Date?, endOfDay: Date?): Long {
        if (startOfday == null || endOfDay == null) {
            return Long.MIN_VALUE
        }
        return (endOfDay.getTime() - startOfday.getTime()) / 12
    }

    /**
     * A method that returns sundial or solar noon. It occurs when the Sun is [transiting](https://en.wikipedia.org/wiki/Transit_%28astronomy%29) the [celestial meridian](https://en.wikipedia.org/wiki/Meridian_%28astronomy%29). The calculations used by
     * this class depend on the [AstronomicalCalculator] used. If this calendar instance is [ ][.setAstronomicalCalculator] to use the [com.kosherjava.zmanim.util.NOAACalculator]
     * (the default) it will calculate astronomical noon. If the calendar instance is  to use the
     * [com.kosherjava.zmanim.util.SunTimesCalculator], that does not have code to calculate astronomical noon, the
     * sun transit is calculated as halfway between sea level sunrise and sea level sunset, which can be slightly off the
     * real transit time due to changes in declination (the lengthening or shortening day). See [The Definition of Chatzos](https://kosherjava.com/2020/07/02/definition-of-chatzos/) for details on the proper
     * definition of solar noon / midday.
     *
     * @return null if [geoLocation] is null. (TODO null check: is this a good contract?)
     * Otherwise, the `Date` representing Sun's transit. If the calculation can't be computed such as in the
     * Arctic Circle where there is at least one day a year where the sun does not rise, and one where it does
     * not set, null will be returned. See detailed explanation on top of the page.
     * @see .getSunTransit
     * @see .getTemporalHour
     */
    val sunTransit: Date?
        get() {
            return geoLocation?.let {
                val noon: Double = astronomicalCalculator.getUTCNoon(adjustedCalendar, it)
                return getDateFromTime(noon, false)
            }
        }

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
    fun getSunTransit(startOfDay: Date?, endOfDay: Date?): Date? {
        val temporalHour: Long = getTemporalHour(startOfDay, endOfDay)
        return com.kosherjava.zmanim.AstronomicalCalendar.Companion.getTimeOffset(startOfDay, temporalHour * 6)
    }

    /**
     * A method that returns a `Date` from the time passed in as a parameter.
     *
     * @param time
     * The time to be set as the time for the `Date`. The time expected is in the format: 18.75
     * for 6:45:00 PM.time is sunrise and false if it is sunset
     * @param isSunrise true if the
     * @return The Date.
     */
    protected fun getDateFromTime(time: Double, isSunrise: Boolean): Date? {
        if (java.lang.Double.isNaN(time)) {
            return null
        }
        var calculatedTime: Double = time
        val adjustedCalendar: Calendar = adjustedCalendar
        val cal: Calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        cal.clear() // clear all fields
        cal.set(Calendar.YEAR, adjustedCalendar.get(Calendar.YEAR))
        cal.set(Calendar.MONTH, adjustedCalendar.get(Calendar.MONTH))
        cal.set(Calendar.DAY_OF_MONTH, adjustedCalendar.get(Calendar.DAY_OF_MONTH))
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
        //TODO what should be done if geoLocation is null in the following code?
        val localTimeHours: Int? = geoLocation?.longitude?.div(15)?.toInt()
        if (isSunrise && localTimeHours?.plus(hours)?.let { it > 18 } == true) {
            cal.add(Calendar.DAY_OF_MONTH, -1)
        } else if (!isSunrise && localTimeHours?.plus(hours)?.let { it < 6 } == true) {
            cal.add(Calendar.DAY_OF_MONTH, 1)
        }
        cal.set(Calendar.HOUR_OF_DAY, hours)
        cal.set(Calendar.MINUTE, minutes)
        cal.set(Calendar.SECOND, seconds)
        cal.set(Calendar.MILLISECOND, (calculatedTime * 1000).toInt())
        return cal.time
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
        var offsetByDegrees: Date? = seaLevelSunrise
        val offsetByTime: Date? = com.kosherjava.zmanim.AstronomicalCalendar.Companion.getTimeOffset(
            seaLevelSunrise,
            -(minutes * com.kosherjava.zmanim.AstronomicalCalendar.Companion.MINUTE_MILLIS)
        )
        var degrees = BigDecimal(0)
        val incrementor = BigDecimal("0.0001")
        while (offsetByDegrees == null || ((minutes < 0.0 && offsetByDegrees.time < offsetByTime!!.time) ||
                    (minutes > 0.0 && offsetByDegrees.time > offsetByTime!!.time))
        ) {
            degrees = if (minutes > 0.0) degrees.add(incrementor) else degrees.subtract(incrementor)
            offsetByDegrees =
                getSunriseOffsetByDegrees(GEOMETRIC_ZENITH + degrees.toDouble())
        }
        return degrees.toDouble()
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
        var offsetByDegrees: Date? = seaLevelSunset
        val offsetByTime: Date? = com.kosherjava.zmanim.AstronomicalCalendar.Companion.getTimeOffset(
            seaLevelSunset,
            minutes * com.kosherjava.zmanim.AstronomicalCalendar.Companion.MINUTE_MILLIS
        )
        var degrees: BigDecimal = BigDecimal(0)
        val incrementor: BigDecimal = BigDecimal("0.001")
        while (offsetByDegrees == null || ((minutes > 0.0 && offsetByDegrees.getTime() < offsetByTime!!.getTime()) ||
                    (minutes < 0.0 && offsetByDegrees.getTime() > offsetByTime!!.getTime()))
        ) {
            if (minutes > 0.0) {
                degrees = degrees.add(incrementor)
            } else {
                degrees = degrees.subtract(incrementor)
            }
            offsetByDegrees =
                getSunsetOffsetByDegrees(com.kosherjava.zmanim.AstronomicalCalendar.Companion.GEOMETRIC_ZENITH + degrees.toDouble())
        }
        return degrees.toDouble()
    }

    /**
     * Adjusts the `Calendar` to deal with edge cases where the location crosses the antimeridian.
     *
     * @see GeoLocation.getAntimeridianAdjustment
     * @return the adjusted Calendar
     */
    private val adjustedCalendar: Calendar
        get() {
            val offset = geoLocation?.antimeridianAdjustment
            if (offset == null /*TODO null check: should this return null or [calendar] if geoLocation is null?*/ || offset == 0) {
                return calendar
            }
            val adjustedCalendar: Calendar = calendar?.clone() as Calendar
            adjustedCalendar.add(Calendar.DAY_OF_MONTH, offset)
            return adjustedCalendar
        }

    /**
     * @return an XML formatted representation of the class. It returns the default output of the
     * [toXML][com.kosherjava.zmanim.util.ZmanimFormatter.toXML] method.
     * @see com.kosherjava.zmanim.util.ZmanimFormatter.toXML
     * @see Object.toString
     */
    override fun toString(): String {
        return ZmanimFormatter.toXML(this)
    }

    /**
     * @return a JSON formatted representation of the class. It returns the default output of the
     * [toJSON][com.kosherjava.zmanim.util.ZmanimFormatter.toJSON] method.
     * @see com.kosherjava.zmanim.util.ZmanimFormatter.toJSON
     * @see Object.toString
     */
    fun toJSON(): String {
        return ZmanimFormatter.toJSON(this)
    }

    /**
     * @see Object.equals
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other !is AstronomicalCalendar) {
            return false
        }
        return calendar == other.calendar &&
                geoLocation == other.geoLocation &&
                astronomicalCalculator == other.astronomicalCalculator
    }

    /**
     * @see Object.hashCode
     */
    override fun hashCode(): Int {
        var result = 17
        result = 37 * result + javaClass.hashCode() // needed or this and subclasses will return identical hash
        result += 37 * result + calendar.hashCode()
        result += 37 * result + geoLocation.hashCode()
        result += 37 * result + astronomicalCalculator.hashCode()
        return result
    }

    /**
     * A method that creates a [deep copy](https://en.wikipedia.org/wiki/Object_copy#Deep_copy) of the object.
     * **Note:** If the [TimeZone] in the cloned [com.kosherjava.zmanim.util.GeoLocation] will
     * be changed from the original, it is critical that
     * [com.kosherjava.zmanim.AstronomicalCalendar.getCalendar].
     * [setTimeZone(TimeZone)][Calendar.setTimeZone] be called in order for the
     * AstronomicalCalendar to output times in the expected offset after being cloned.
     *
     * @see Object.clone
     */
    public override fun clone(): Any {
        var clone: AstronomicalCalendar? = null
        try {
            clone = super.clone() as AstronomicalCalendar
        } catch (cnse: CloneNotSupportedException) {
            // Required by the compiler. Should never be reached since we implement clone()
        }
        clone!!.geoLocation = geoLocation?.copy()
        clone.calendar = calendar.clone() as Calendar
        clone.astronomicalCalculator = astronomicalCalculator.clone() as AstronomicalCalculator
        return (clone)
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
         * offset as a `long` and calls [.getTimeOffset].
         *
         * @param time
         * the start time
         * @param offset
         * the offset in milliseconds to add to the time
         * @return the [Date]with the offset added to it
         */
        fun getTimeOffset(time: Date?, offset: Double): Date? {
            return getTimeOffset(time, offset.toLong())
        }

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
         * @return the [Date] with the offset in milliseconds added to it
         */
        fun getTimeOffset(time: Date?, offset: Long): Date? {
            if (time == null || offset == Long.MIN_VALUE) {
                return null
            }
            return Date(time.time + offset)
        }
    }
}