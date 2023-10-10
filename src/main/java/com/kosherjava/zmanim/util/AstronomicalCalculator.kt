/*
 * Zmanim Java API
 * Copyright (C) 2004-2023 Eliyahu Hershfeld
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General
 * Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful,but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more
 * details.
 * You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to
 * the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA,
 * or connect to: https://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 */
package com.kosherjava.zmanim.util

import kotlinx.datetime.LocalDate
import kotlin.math.acos

/**
 * An abstract class that all sun time calculating classes extend. This allows the algorithm used to be changed at
 * runtime, easily allowing comparison the results of using different algorithms.
 * @todo Consider methods that would allow atmospheric modeling. This can currently be adjusted by [refraction].
 *
 * @author  Eliyahu Hershfeld 2004 - 2023
 */
abstract class AstronomicalCalculator {
    /**
     * Method to get the refraction value to be used when calculating sunrise and sunset. The default value is 34 arc
     * minutes. The [Errata and Notes for Calendrical Calculations: The Millennium Edition](https://web.archive.org/web/20150915094635/http://emr.cs.iit.edu/home/reingold/calendar-book/second-edition/errata.pdf) by Edward M. Reingold and Nachum Dershowitz
     * lists the actual average refraction value as 34.478885263888294 or approximately 34' 29". The refraction value as well
     * as the solarRadius and elevation adjustment are added to the zenith used to calculate sunrise and sunset.
     *
     * @return The refraction in arc minutes.
     */
    /**
     * A method to allow overriding the default refraction of the calculator.
     * @todo At some point in the future, an AtmosphericModel or Refraction object that models the atmosphere of different
     * locations might be used for increased accuracy.
     *
     * @param refraction
     * The refraction in arc minutes.
     * @see .getRefraction
     */
    /**
     * The commonly used average solar refraction. Calendrical Calculations lists a more accurate global average of
     * 34.478885263888294
     *
     * @see .getRefraction
     */
    var refraction: Double = 34 / 60.0
    /**
     * Method to get the sun's radius. The default value is 16 arc minutes. The sun's radius as it appears from earth is
     * almost universally given as 16 arc minutes but in fact it differs by the time of the year. At the [perihelion](https://en.wikipedia.org/wiki/Perihelion) it has an apparent radius of 16.293, while at the
     * [aphelion](https://en.wikipedia.org/wiki/Aphelion) it has an apparent radius of 15.755. There is little
     * affect for most location, but at high and low latitudes the difference becomes more apparent. My Calculations for
     * the difference at the location of the [Royal Observatory, Greenwich](https://www.rmg.co.uk/royal-observatory)
     * shows only a 4.494 second difference between the perihelion and aphelion radii, but moving into the arctic circle the
     * difference becomes more noticeable. Tests for Tromso, Norway (latitude 69.672312, longitude 19.049787) show that
     * on May 17, the rise of the midnight sun, a 2 minute 23 second difference is observed between the perihelion and
     * aphelion radii using the USNO algorithm, but only 1 minute and 6 seconds difference using the NOAA algorithm.
     * Areas farther north show an even greater difference. Note that these test are not real valid test cases because
     * they show the extreme difference on days that are not the perihelion or aphelion, but are shown for illustrative
     * purposes only.
     *
     * @return The sun's radius in arc minutes.
     */
    /**
     * Method to set the sun's radius.
     *
     * @param solarRadius
     * The sun's radius in arc minutes.
     * @see solarRadius
     */
    /**
     * The commonly used average solar radius in minutes of a degree.
     *
     * @see solarRadius
     */
    var solarRadius: Double = 16 / 60.0
    /**
     * A method that returns the earth radius in KM. The value currently defaults to 6356.9 KM if not set.
     *
     * @return the earthRadius the earth radius in KM.
     */
    /**
     * A method that allows setting the earth's radius.
     *
     * @param earthRadius
     * the earthRadius to set in KM
     */
    /**
     * The commonly used average earth radius in KM. At this time, this only affects elevation adjustment and not the
     * sunrise and sunset calculations. The value currently defaults to 6356.9 KM.
     *
     * @see .getEarthRadius
     * @see .setEarthRadius
     */
    var earthRadius: Double = 6356.9 // in KM

    /**
     * Returns the name of the algorithm.
     *
     * @return the descriptive name of the algorithm.
     */
    abstract val calculatorName: String
    /**
     * Setter method for the descriptive name of the calculator. This will typically not have to be set
     *
     * @param calculatorName
     * descriptive name of the algorithm.
     */
    /**
     * A method that calculates UTC sunrise as well as any time based on an angle above or below sunrise. This abstract
     * method is implemented by the classes that extend this class.
     *
     * @param calendar
     * Used to calculate day of year.
     * @param geoLocation
     * The location information used for astronomical calculating sun times.
     * @param zenith
     * the azimuth below the vertical zenith of 90 degrees. for sunrise typically the [zenith][.adjustZenith] used for the calculation uses geometric zenith of 90 and [adjusts][.adjustZenith]
     * this slightly to account for solar refraction and the sun's radius. Another example would be
     * [com.kosherjava.zmanim.AstronomicalCalendar.getBeginNauticalTwilight] that passes
     * [com.kosherjava.zmanim.AstronomicalCalendar.NAUTICAL_ZENITH] to this method.
     * @param adjustForElevation
     * Should the time be adjusted for elevation
     * @return The UTC time of sunrise in 24 hour format. 5:45:00 AM will return 5.75.0. If an error was encountered in
     * the calculation (expected behavior for some locations such as near the poles,
     * [Double.NaN] will be returned.
     * @see .getElevationAdjustment
     */
    abstract fun getUTCSunrise(
        LocalDate: LocalDate, geoLocation: GeoLocation, zenith: Double,
        adjustForElevation: Boolean,
    ): Double

    /**
     * A method that calculates UTC sunset as well as any time based on an angle above or below sunset. This abstract
     * method is implemented by the classes that extend this class.
     *
     * @param LocalDate
     * Used to calculate day of year.
     * @param geoLocation
     * The location information used for astronomical calculating sun times.
     * @param zenith
     * the azimuth below the vertical zenith of 90. For sunset typically the [zenith][.adjustZenith] used for the calculation uses geometric zenith of 90 and [adjusts][.adjustZenith]
     * this slightly to account for solar refraction and the sun's radius. Another example would be
     * [com.kosherjava.zmanim.AstronomicalCalendar.getEndNauticalTwilight] that passes
     * [com.kosherjava.zmanim.AstronomicalCalendar.NAUTICAL_ZENITH] to this method.
     * @param adjustForElevation
     * Should the time be adjusted for elevation
     * @return The UTC time of sunset in 24 hour format. 5:45:00 AM will return 5.75.0. If an error was encountered in
     * the calculation (expected behavior for some locations such as near the poles,
     * [Double.NaN] will be returned.
     * @see .getElevationAdjustment
     */
    abstract fun getUTCSunset(
        LocalDate: LocalDate, geoLocation: GeoLocation, zenith: Double,
        adjustForElevation: Boolean,
    ): Double

    /**
     * Return [solar noon](https://en.wikipedia.org/wiki/Noon#Solar_noon) (UTC) for the given day at the
     * given location on earth. The the [com.kosherjava.zmanim.util.NOAACalculator] implementation calculates
     * true solar noon, while the [com.kosherjava.zmanim.util.SunTimesCalculator] approximates it, calculating
     * the time as halfway between sunrise and sunset.
     *
     * @param LocalDate
     * Used to calculate day of year.
     * @param geoLocation
     * The location information used for astronomical calculating sun times.
     *
     * @return the time in minutes from zero UTC
     */
    abstract fun getUTCNoon(LocalDate: LocalDate, geoLocation: GeoLocation): Double

    /**
     * Method to return the adjustment to the zenith required to account for the elevation. Since a person at a higher
     * elevation can see farther below the horizon, the calculation for sunrise / sunset is calculated below the horizon
     * used at sea level. This is only used for sunrise and sunset and not times before or after it such as
     * [nautical twilight][com.kosherjava.zmanim.AstronomicalCalendar.getBeginNauticalTwilight] since those
     * calculations are based on the level of available light at the given dip below the horizon, something that is not
     * affected by elevation, the adjustment should only made if the zenith == 90 [adjusted][.adjustZenith]
     * for refraction and solar radius. The algorithm used is
     *
     * <pre>
     * elevationAdjustment = Math.toDegrees(Math.acos(earthRadiusInMeters / (earthRadiusInMeters + elevationMeters)));
    </pre> *
     *
     * The source of this algorithm is [Calendrical Calculations](http://www.calendarists.com) by Edward M.
     * Reingold and Nachum Dershowitz. An alternate algorithm that produces an almost identical (but not accurate)
     * result found in Ma'aglay Tzedek by Moishe Kosower and other sources is:
     *
     * <pre>
     * elevationAdjustment = 0.0347 * Math.sqrt(elevationMeters);
    </pre> *
     *
     * @param elevation
     * elevation in Meters.
     * @return the adjusted zenith
     */
    fun getElevationAdjustment(elevation: Double): Double =
        toDegrees(
            acos(
                earthRadius /
                        (earthRadius + (elevation / 1000))
            )
        )

    /**
     * Adjusts the zenith of astronomical sunrise and sunset to account for solar refraction, solar radius and
     * elevation. The value for Sun's zenith and true rise/set Zenith (used in this class and subclasses) is the angle
     * that the center of the Sun makes to a line perpendicular to the Earth's surface. If the Sun were a point and the
     * Earth were without an atmosphere, true sunset and sunrise would correspond to a 90 zenith. Because the Sun
     * is not a point, and because the atmosphere refracts light, this 90 zenith does not, in fact, correspond to
     * true sunset or sunrise, instead the center of the Sun's disk must lie just below the horizon for the upper edge
     * to be obscured. This means that a zenith of just above 90 must be used. The Sun subtends an angle of 16
     * minutes of arc (this can be changed via the [.setSolarRadius] method , and atmospheric refraction
     * accounts for 34 minutes or so (this can be changed via the [.setRefraction] method), giving a total
     * of 50 arcminutes. The total value for ZENITH is 90+(5/6) or 90.8333333 for true sunrise/sunset. Since a
     * person at an elevation can see blow the horizon of a person at sea level, this will also adjust the zenith to
     * account for elevation if available. Note that this will only adjust the value if the zenith is exactly 90 degrees.
     * For values below and above this no correction is done. As an example, astronomical twilight is when the sun is
     * 18 below the horizon or [108˚][com.kosherjava.zmanim.AstronomicalCalendar.ASTRONOMICAL_ZENITH]. This is traditionally calculated with none of the above mentioned adjustments. The same goes
     * for various *tzais* and *alos* times such as the
     * [16.1˚][com.kosherjava.zmanim.ZmanimCalendar.ZENITH_16_POINT_1] dip used in
     * [com.kosherjava.zmanim.ComplexZmanimCalendar.getAlos16Point1Degrees].
     *
     * @param zenith
     * the azimuth below the vertical zenith of 90. For sunset typically the [zenith][.adjustZenith] used for the calculation uses geometric zenith of 90 and [adjusts][.adjustZenith]
     * this slightly to account for solar refraction and the sun's radius. Another example would be
     * [com.kosherjava.zmanim.AstronomicalCalendar.getEndNauticalTwilight] that passes
     * [com.kosherjava.zmanim.AstronomicalCalendar.NAUTICAL_ZENITH] to this method.
     * @param elevation
     * elevation in Meters.
     * @return The zenith adjusted to include the [sun&#39;s radius][solarRadius], [refraction][.getRefraction] and [elevation][.getElevationAdjustment] adjustment. This will only be adjusted for
     * sunrise and sunset (if the zenith == 90)
     * @see .getElevationAdjustment
     */
    fun adjustZenith(zenith: Double, elevation: Double): Double = zenith
        .takeUnless { it == GEOMETRIC_ZENITH } ?: // only adjust if it is exactly sunrise or sunset
    (zenith + solarRadius + refraction + getElevationAdjustment(elevation))


    abstract fun copy(): AstronomicalCalculator

    companion object {

        //Taken from java.lang.Math:

        /**
         * Constant by which to multiply an angular value in degrees to obtain an
         * angular value in radians.
         */
        const val DEGREES_TO_RADIANS = 0.017453292519943295

        /**
         * Constant by which to multiply an angular value in radians to obtain an
         * angular value in degrees.
         */
        const val RADIANS_TO_DEGREES = 57.29577951308232

        /**
         * Converts an angle measured in radians to an approximately
         * equivalent angle measured in degrees.  The conversion from
         * radians to degrees is generally inexact; users should
         * *not* expect `cos(toRadians(90.0))` to exactly
         * equal `0.0`.
         *
         * @param   angrad   an angle, in radians
         * @return  the measurement of the angle `angrad`
         * in degrees.
         * @since   1.2
         */
        fun toDegrees(angrad: Double): Double = angrad * RADIANS_TO_DEGREES
//        fun Double.toDegrees() = toDegrees(this)

        /**
         * Converts an angle measured in degrees to an approximately
         * equivalent angle measured in radians.  The conversion from
         * degrees to radians is generally inexact.
         *
         * @param   angdeg   an angle, in degrees
         * @return  the measurement of the angle `angdeg`
         * in radians.
         * @since   1.2
         */
        fun toRadians(angdeg: Double): Double = angdeg * DEGREES_TO_RADIANS
//        fun Double.toRadians() = toRadians(this)

        /**
         * The zenith of astronomical sunrise and sunset. The sun is 90 from the vertical 0
         */
        private const val GEOMETRIC_ZENITH: Double = 90.0

        /**
         * Returns the default class for calculating sunrise and sunset. This is currently the [NOAACalculator],
         * but this may change.
         *
         * @return AstronomicalCalculator the default class for calculating sunrise and sunset. In the current
         * implementation the default calculator returned is the [NOAACalculator].
         */
        val default: AstronomicalCalculator
            get() = NOAACalculator()
    }
}