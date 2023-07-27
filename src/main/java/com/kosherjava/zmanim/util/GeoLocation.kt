/*
 * Zmanim Java API
 * Copyright (C) 2004-2022 Eliyahu Hershfeld
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

import com.kosherjava.zmanim.util.AstronomicalCalculator.Companion.toDegrees
import com.kosherjava.zmanim.util.AstronomicalCalculator.Companion.toRadians
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.asTimeZone
import kotlinx.datetime.offsetAt
import kotlinx.datetime.offsetIn
import kotlin.time.Duration.Companion.seconds


import kotlin.math.*

/**
 * A class that contains location information such as latitude and longitude required for astronomical calculations. The
 * elevation field may not be used by some calculation engines and would be ignored if set. Check the documentation for
 * specific implementations of the [AstronomicalCalculator] to see if elevation is calculated as part of the
 * algorithm.
 *
 * @author  Eliyahu Hershfeld 2004 - 2022
 */
data class GeoLocation(
    /**
     * If this is ever set after the GeoLocation is set in the
     * [com.kosherjava.zmanim.AstronomicalCalendar], it is critical that
     * [com.kosherjava.zmanim.AstronomicalCalendar.localDate].[setTimeZone(TimeZone)][java.util.Calendar.setTimeZone] be called in order for the
     * AstronomicalCalendar to output times in the expected offset. This situation will arise if the
     * AstronomicalCalendar is ever [cloned][com.kosherjava.zmanim.AstronomicalCalendar.clone].
     */
    var timeZone: TimeZone,
    var locationName: String
) {
    var latitude: Double = 0.0
        /**
         * The degrees of latitude to set. The values should be between -90 and 90. An
         * [IllegalArgumentException] will be thrown if the value exceeds the limit. For example 40.095965 would be
         * used for Lakewood, NJ. **Note:** For latitudes south of the equator, a negative value should be
         * used.
         */
        set(value) {
            require(!(latitude > 90 || latitude < -90)) { "Latitude must be between -90 and  90" }
            field = value
        }
    var longitude: Double = 0.0
        /**
         * The degrees of longitude to set in a double format between -180 and 180. An
         * IllegalArgumentException will be thrown if the value exceeds the limit. For example -74.2094 would be
         * used for Lakewood, NJ. Note: for longitudes east of the [Prime Meridian](https://en.wikipedia.org/wiki/Prime_Meridian) (Greenwich) a negative value
         * should be used.
         */
        set(value) {
            require(!(longitude > 180 || longitude < -180)) { "Longitude must be between -180 and  180" }
            field = value
        }

    /**
     * The elevation in Meters
     */
    var elevation = 0.0
        /**
         * Method to set the elevation in Meters **above** sea level.
         *
         * @param elevation
         * The elevation to set in Meters. An IllegalArgumentException will be thrown if the value is a negative, NaN or infinite.
         */
        set(elevation) {
            require(elevation >= 0) { "Elevation cannot be negative" }
            require(!(elevation.isNaN() || elevation.isInfinite())) { "Elevation must not be NaN or infinite" }
            field = elevation
        }

    /**
     * GeoLocation constructor with parameters for all required fields.
     *
     * @param name
     * The location name for display use such as &quot;Lakewood, NJ&quot;
     * @param latitude
     * the latitude in a double format such as 40.095965 for Lakewood, NJ.
     * **Note:** For latitudes south of the equator, a negative value should be used.
     * @param longitude
     * double the longitude in a double format such as -74.222130 for Lakewood, NJ.
     * **Note:** For longitudes east of the [Prime Meridian](https://en.wikipedia.org/wiki/Prime_Meridian) (Greenwich),
     * a negative value should be used.
     * @param timeZone
     * the `TimeZone` for the location.
     */
    constructor(name: String, latitude: Double, longitude: Double, timeZone: TimeZone) : this(
        name,
        latitude,
        longitude,
        0.0,
        timeZone
    )

    /**
     * GeoLocation constructor with parameters for all required fields.
     *
     * @param name
     * The location name for display use such as &quot;Lakewood, NJ&quot;
     * @param latitude
     * the latitude in a double format such as 40.095965 for Lakewood, NJ.
     * **Note:** For latitudes south of the equator, a negative value should be used.
     * @param longitude
     * double the longitude in a double format such as -74.222130 for Lakewood, NJ.
     * **Note:** For longitudes east of the [Prime
 * Meridian](https://en.wikipedia.org/wiki/Prime_Meridian) (Greenwich), a negative value should be used.
     * @param elevation
     * the elevation above sea level in Meters. Elevation is not used in most algorithms used for calculating
     * sunrise and set.
     * @param timeZone
     * the `TimeZone` for the location.
     */
    constructor(
        name: String,
        latitude: Double,
        longitude: Double,
        elevation: Double,
        timeZone: TimeZone
    ) : this(timeZone, name) {
        this.latitude = latitude
        this.longitude = longitude
        this.elevation = elevation
    }

    /**
     * Default GeoLocation constructor will set location to the Prime Meridian at Greenwich, England and a TimeZone of
     * GMT. The longitude will be set to 0 and the latitude will be 51.4772 to match the location of the [Royal Observatory, Greenwich](https://www.rmg.co.uk/royal-observatory). No daylight savings time will be used.
     */
    constructor() : this(TimeZone.UTC, "Greenwich, England") {
        longitude = 0.0 // added for clarity
        latitude = 51.4772
    }

    /**
     * Method to set the latitude in degrees, minutes and seconds.
     *
     * @param degrees
     * The degrees of latitude to set between 0 and 90. For example 40 would be used for Lakewood, NJ.
     * An IllegalArgumentException will be thrown if the value exceeds the limit.
     * @param minutes
     * [minutes of arc](https://en.wikipedia.org/wiki/Minute_of_arc#Cartography)
     * @param seconds
     * [seconds of arc](https://en.wikipedia.org/wiki/Minute_of_arc#Cartography)
     * @param direction
     * N for north and S for south. An IllegalArgumentException will be thrown if the value is not S or N.
     */
    fun setLatitude(degrees: Int, minutes: Int, seconds: Double, direction: String): GeoLocation {
        var tempLat = degrees + (minutes + seconds / 60.0) / 60.0
        require(!(tempLat > 90 || tempLat < 0)) {  //FIXME An exception should be thrown if degrees, minutes or seconds are negative
            "Latitude must be between 0 and  90. Use direction of S instead of negative."
        }
        if (direction == "S") {
            tempLat *= -1.0
        } else require(direction == "N") { "Latitude direction must be N or S" }
        latitude = tempLat
        return this
    }

    /**
     * Method to set the longitude in degrees, minutes and seconds.
     *
     * @param degrees
     * The degrees of longitude to set between 0 and 180. As an example 74 would be set for Lakewood, NJ.
     * An IllegalArgumentException will be thrown if the value exceeds the limits.
     * @param minutes
     * [minutes of arc](https://en.wikipedia.org/wiki/Minute_of_arc#Cartography)
     * @param seconds
     * [seconds of arc](https://en.wikipedia.org/wiki/Minute_of_arc#Cartography)
     * @param direction
     * E for east of the [Prime Meridian](https://en.wikipedia.org/wiki/Prime_Meridian) or W for west of it.
     * An IllegalArgumentException will be thrown if
     * the value is not E or W.
     */
    fun setLongitude(degrees: Int, minutes: Int, seconds: Double, direction: String): GeoLocation {
        var longTemp = degrees + (minutes + seconds / 60.0) / 60.0
        if (longTemp > 180 || longitude < 0) { //FIXME An exception should be thrown if degrees, minutes or seconds are negative
            throw IllegalArgumentException("Longitude must be between 0 and  180.  Use a direction of W instead of negative.")
        }
        if (direction == "W") {
            longTemp *= -1.0
        } else require(direction == "E") { "Longitude direction must be E or W" }
        longitude = longTemp
        return this
    }

    /**
     * A method that will return the location's local mean time offset in milliseconds from local [standard time](https://en.wikipedia.org/wiki/Standard_time). The globe is split into 360, with
     * 15 per hour of the day. For a local that is at a longitude that is evenly divisible by 15 (longitude % 15 ==
     * 0), at solar [noon][com.kosherjava.zmanim.AstronomicalCalendar.getSunTransit] (with adjustment for the [equation of time](https://en.wikipedia.org/wiki/Equation_of_time)) the sun should be directly overhead,
     * so a user who is 1 west of this will have noon at 4 minutes after standard time noon, and conversely, a user
     * who is 1 east of the 15 longitude will have noon at 11:56 AM. Lakewood, N.J., whose longitude is
     * -74.2094, is 0.7906 away from the closest multiple of 15 at -75. This is multiplied by 4 to yield 3 minutes
     * and 10 seconds earlier than standard time. The offset returned does not account for the [Daylight saving time](https://en.wikipedia.org/wiki/Daylight_saving_time) offset since this class is
     * unaware of dates.
     *
     * @return the offset in milliseconds not accounting for Daylight saving time. A positive value will be returned
     * East of the 15 timezone line, and a negative value West of it.
     * @since 1.1
     */
    val localMeanTimeOffset: Long
        get() =
            /*
            * if the offset is 20 hours or more in the past
            * (no current location is known that crosses the antimeridian to the west, but better safe than sorry)
            * roll the date back a day
            * 99.999% of the world will have no adjustment
            * if the offset is 20 hours or more in the future (never expected anywhere other
            * than a location using a timezone across the anti meridian to the east such as Samoa)
            * roll the date forward a day
            */
            (longitude * 4 * MINUTE_MILLIS - timeZone.rawOffset).toLong()
    /**
     * Adjust the date for [antimeridian](https://en.wikipedia.org/wiki/180th_meridian) crossover. This is
     * needed to deal with edge cases such as Samoa that use a different LocalDate date than expected based on their
     * geographic location.
     *
     * The actual Time Zone offset may deviate from the expected offset based on the longitude. Since the 'absolute time'
     * calculations are always based on longitudinal offset from UTC for a given date, the date is presumed to only
     * increase East of the Prime Meridian, and to only decrease West of it. For Time Zones that cross the antimeridian,
     * the date will be artificially adjusted before calculation to conform with this presumption.
     *
     * For example, Apia, Samoa with a longitude of -171.75 uses a local offset of +14:00.  When calculating sunrise for
     * 2018-02-03, the calculator should operate using 2018-02-02 since the expected zone is -11.  After determining the
     * UTC time, the local DST offset of [UTC+14:00](https://en.wikipedia.org/wiki/UTC%2B14:00) should be applied
     * to bring the date back to 2018-02-03.
     *
     * @return the number of days to adjust the date This will typically be 0 unless the date crosses the antimeridian
     */
    val antimeridianAdjustment: Int
        get() {
            val localHoursOffset = localMeanTimeOffset / HOUR_MILLIS.toDouble()
            if (localHoursOffset >= 20) { // if the offset is 20 hours or more in the future (never expected anywhere other
                // than a location using a timezone across the anti meridian to the east such as Samoa)
                return 1 // roll the date forward a day
            } else if (localHoursOffset <= -20) {    // if the offset is 20 hours or more in the past (no current location is known
                //that crosses the antimeridian to the west, but better safe than sorry)
                return -1 // roll the date back a day
            }
            return 0 //99.999% of the world will have no adjustment
        }

    /**
     * Calculate the initial [geodesic](https://en.wikipedia.org/wiki/Great_circle) bearing between this
     * Object and a second Object passed to this method using [Thaddeus Vincenty's](https://en.wikipedia.org/wiki/Thaddeus_Vincenty) inverse formula See T Vincenty, "[Direct and Inverse Solutions of Geodesics on the Ellipsoid
     * with application of nested equations](https://www.ngs.noaa.gov/PUBS_LIB/inverse.pdf)", Survey Review, vol XXII no 176, 1975
     *
     * @param location
     * the destination location
     * @return the initial bearing
     */
    fun getGeodesicInitialBearing(location: GeoLocation): Double = vincentyFormula(location, INITIAL_BEARING)

    /**
     * Calculate the final [geodesic](https://en.wikipedia.org/wiki/Great_circle) bearing between this Object
     * and a second Object passed to this method using [Thaddeus
     * Vincenty's](https://en.wikipedia.org/wiki/Thaddeus_Vincenty) inverse formula See T Vincenty, "[Direct and
     * Inverse Solutions of Geodesics on the Ellipsoid with application of nested equations](https://www.ngs.noaa.gov/PUBS_LIB/inverse.pdf)", Survey Review, vol
     * XXII no 176, 1975
     *
     * @param location
     * the destination location
     * @return the final bearing
     */
    fun getGeodesicFinalBearing(location: GeoLocation): Double = vincentyFormula(location, FINAL_BEARING)

    /**
     * Calculate [geodesic distance](https://en.wikipedia.org/wiki/Great-circle_distance) in Meters between
     * this Object and a second Object passed to this method using [Thaddeus Vincenty's](https://en.wikipedia.org/wiki/Thaddeus_Vincenty) inverse formula See T Vincenty, "[Direct and Inverse Solutions of Geodesics on the Ellipsoid
     * with application of nested equations](https://www.ngs.noaa.gov/PUBS_LIB/inverse.pdf)", Survey Review, vol XXII no 176, 1975
     *
     * @see .vincentyFormula
     * @param location
     * the destination location
     * @return the geodesic distance in Meters
     */
    fun getGeodesicDistance(location: GeoLocation): Double = vincentyFormula(location, DISTANCE)

    /**
     * Calculate [geodesic distance](https://en.wikipedia.org/wiki/Great-circle_distance) in Meters between
     * this Object and a second Object passed to this method using [Thaddeus Vincenty's](https://en.wikipedia.org/wiki/Thaddeus_Vincenty) inverse formula See T Vincenty, "[Direct and Inverse Solutions of Geodesics on the Ellipsoid
     * with application of nested equations](https://www.ngs.noaa.gov/PUBS_LIB/inverse.pdf)", Survey Review, vol XXII no 176, 1975
     *
     * @param location
     * the destination location
     * @param formula
     * This formula calculates initial bearing ([.INITIAL_BEARING]), final bearing (
     * [.FINAL_BEARING]) and distance ([.DISTANCE]).
     * @return geodesic distance in Meters
     */
    private fun vincentyFormula(location: GeoLocation, formula: Int): Double {
        val a = 6378137.0
        val b = 6356752.3142
        val f = 1 / 298.257223563 // WGS-84 ellipsiod
        val L = toRadians(location.longitude - longitude)
        val U1 = atan((1 - f) * tan(toRadians(latitude)))
        val U2 = atan((1 - f) * tan(toRadians(location.latitude)))
        val sinU1 = sin(U1)
        val cosU1 = cos(U1)
        val sinU2 = sin(U2)
        val cosU2 = cos(U2)
        var lambda = L
        var lambdaP = 2 * PI
        var iterLimit = 20.0
        var sinLambda = 0.0
        var cosLambda = 0.0
        var sinSigma = 0.0
        var cosSigma = 0.0
        var sigma = 0.0
        var sinAlpha: Double
        var cosSqAlpha = 0.0
        var cos2SigmaM = 0.0
        var C: Double
        while (abs(lambda - lambdaP) > 1e-12 && --iterLimit > 0) {
            sinLambda = sin(lambda)
            cosLambda = cos(lambda)
            sinSigma = sqrt(
                cosU2 * sinLambda * (cosU2 * sinLambda)
                        + (cosU1 * sinU2 - sinU1 * cosU2 * cosLambda) * (cosU1 * sinU2 - sinU1 * cosU2 * cosLambda)
            )
            if (sinSigma == 0.0) return 0.0 // co-incident points
            cosSigma = sinU1 * sinU2 + cosU1 * cosU2 * cosLambda
            sigma = atan2(sinSigma, cosSigma)
            sinAlpha = cosU1 * cosU2 * sinLambda / sinSigma
            cosSqAlpha = 1 - sinAlpha * sinAlpha
            cos2SigmaM = cosSigma - 2 * sinU1 * sinU2 / cosSqAlpha
            if (cos2SigmaM.isNaN()) cos2SigmaM = 0.0 // equatorial line: cosSqAlpha=0 (§6)
            C = f / 16 * cosSqAlpha * (4 + f * (4 - 3 * cosSqAlpha))
            lambdaP = lambda
            lambda = L + ((1 - C) * f * sinAlpha
                    * (sigma + C * sinSigma * (cos2SigmaM + C * cosSigma * (-1 + 2 * cos2SigmaM * cos2SigmaM))))
        }
        if (iterLimit == 0.0) return Double.NaN // formula failed to converge
        val uSq = cosSqAlpha * (a * a - b * b) / (b * b)
        val A = 1 + uSq / 16384 * (4096 + uSq * (-768 + uSq * (320 - 175 * uSq)))
        val B = uSq / 1024 * (256 + uSq * (-128 + uSq * (74 - 47 * uSq)))
        val deltaSigma = (B
                * sinSigma
                * (cos2SigmaM + B
                / 4
                * (cosSigma * (-1 + 2 * cos2SigmaM * cos2SigmaM) - (B / 6 * cos2SigmaM
                * (-3 + 4 * sinSigma * sinSigma) * (-3 + 4 * cos2SigmaM * cos2SigmaM)))))
        val distance = b * A * (sigma - deltaSigma)

        // initial bearing
        val fwdAz = toDegrees(atan2(cosU2 * sinLambda, cosU1 * sinU2 - sinU1 * cosU2 * cosLambda))

        // final bearing
        val revAz = toDegrees(atan2(cosU1 * sinLambda, -sinU1 * cosU2 + cosU1 * sinU2 * cosLambda))
        return when (formula) {
            DISTANCE -> distance
            INITIAL_BEARING -> fwdAz
            FINAL_BEARING -> revAz
            else -> Double.NaN // should never happen
        }
    }

    /**
     * Returns the [rhumb line](https://en.wikipedia.org/wiki/Rhumb_line) bearing from the current location to
     * the GeoLocation passed in.
     *
     * @param location
     * destination location
     * @return the bearing in degrees
     */
    fun getRhumbLineBearing(location: GeoLocation): Double {
        var dLon = toRadians(location.longitude - longitude)
        val dPhi = ln(
            tan(toRadians(location.latitude) / 2 + PI / 4)
                    / tan(toRadians(latitude) / 2 + PI / 4)
        )
        if (abs(dLon) > PI) dLon = if (dLon > 0) -(2 * PI - dLon) else 2 * PI + dLon
        return toDegrees(atan2(dLon, dPhi))
    }

    /**
     * Returns the [rhumb line](https://en.wikipedia.org/wiki/Rhumb_line) distance from the current location
     * to the GeoLocation passed in.
     *
     * @param location
     * the destination location
     * @return the distance in Meters
     */
    fun getRhumbLineDistance(location: GeoLocation): Double {
        val earthRadius = 6378137.0 // Earth's radius in meters (WGS-84)
        val dLat = toRadians(location.latitude) - toRadians(latitude)
        var dLon = abs(toRadians(location.longitude) - toRadians(longitude))
        val dPhi = ln(
            tan(toRadians(location.latitude) / 2 + PI / 4)
                    / tan(toRadians(latitude) / 2 + PI / 4)
        )
        var q = dLat / dPhi
        if (abs(q) > Double.MAX_VALUE) {
            q = cos(toRadians(latitude))
        }
        // if dLon over 180° take shorter rhumb across 180° meridian:
        if (dLon > PI) {
            dLon = 2 * PI - dLon
        }
        val d = sqrt(dLat * dLat + q * q * dLon * dLon)
        return d * earthRadius
    }

    /**
     * A method that returns an XML formatted `String` representing the serialized `Object`. Very
     * similar to the toString method but the return value is in an xml format. The format currently used (subject to
     * change) is:
     *
     * <pre>
     * <GeoLocation>
     * <LocationName>Lakewood, NJ</LocationName>
     * <Latitude>40.0828&amp;deg</Latitude>
     * <Longitude>-74.2094&amp;deg</Longitude>
     * <Elevation>0 Meters</Elevation>
     * <TimezoneName>America/New_York</TimezoneName>
     * <TimeZoneDisplayName>Eastern Standard Time</TimeZoneDisplayName>
     * <TimezoneGMTOffset>-5</TimezoneGMTOffset>
     * <TimezoneDSTOffset>1</TimezoneDSTOffset>
     * </GeoLocation>
    </pre> *
     *
     * @return The XML formatted `String`.
     */
    fun toXML(): String {
        val sb = StringBuilder()
        sb.append("<GeoLocation>\n")
        sb.append("\t<LocationName>").append(locationName).append("</LocationName>\n")
        sb.append("\t<Latitude>").append(latitude).append("</Latitude>\n")
        sb.append("\t<Longitude>").append(longitude).append("</Longitude>\n")
        sb.append("\t<Elevation>").append(elevation).append(" Meters").append("</Elevation>\n")
        sb.append("\t<TimezoneName>").append(timeZone!!.id).append("</TimezoneName>\n")
        sb.append("\t<TimeZoneDisplayName>").append(timeZone!!.id).append("</TimeZoneDisplayName>\n") //TODO kinda broke with kotlinx
        sb.append("\t<TimezoneGMTOffset>").append(timeZone!!.rawOffset / HOUR_MILLIS)
            .append("</TimezoneGMTOffset>\n")
        sb.append("\t<TimezoneDSTOffset>").append(timeZone!!.rawOffset / HOUR_MILLIS)//TODO kinda broke with kotlinx
            .append("</TimezoneDSTOffset>\n")
        sb.append("</GeoLocation>")
        return sb.toString()
    }

    /**
     * @see Object.equals
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is GeoLocation) return false
        return latitude.toBits() == other.latitude.toBits() &&
                longitude.toBits() == other.longitude.toBits() &&
                elevation == other.elevation &&
                locationName == other.locationName &&
                timeZone == other.timeZone
    }

    /**
     * @see Object.hashCode
     */
    override fun hashCode(): Int {
        var result = 17
        val latLong = latitude.toBits()
        val lonLong = longitude.toBits()
        val elevLong = elevation.toBits()
        val latInt = (latLong xor (latLong ushr 32)).toInt()
        val lonInt = (lonLong xor (lonLong ushr 32)).toInt()
        val elevInt = (elevLong xor (elevLong ushr 32)).toInt()
        result = 37 * result + this::class.hashCode()
        result += 37 * result + latInt
        result += 37 * result + lonInt
        result += 37 * result + elevInt
        result += 37 * result + locationName.hashCode()
        result += 37 * result + timeZone.hashCode()
        return result
    }

    /**
     * @see Object.toString
     */
    override fun toString(): String {
        val sb = StringBuilder()
        sb.append("\nLocation Name:\t\t\t").append(locationName)
        sb.append("\nLatitude:\t\t\t").append(latitude).append("\u00B0")
        sb.append("\nLongitude:\t\t\t").append(longitude).append("\u00B0")
        sb.append("\nElevation:\t\t\t").append(elevation).append(" Meters")
        sb.append("\nTimezone ID:\t\t\t").append(timeZone!!.id)
        sb.append("\nTimezone Display Name:\t\t").append(timeZone!!.id) //TODO kinda broke with kotlinx
            .append(" (").append(timeZone!!.id).append(")")//TODO kinda broke with kotlinx
        sb.append("\nTimezone GMT Offset:\t\t").append(timeZone!!.rawOffset / HOUR_MILLIS)
        sb.append("\nTimezone DST Offset:\t\t").append(timeZone!!.rawOffset / HOUR_MILLIS)//TODO kinda broke with kotlinx
        return sb.toString()
    }

    /**
     * An implementation of the [Object.clone] method that creates a [deep copy](https://en.wikipedia.org/wiki/Object_copy#Deep_copy) of the object.
     * **Note:** If the [TimeZone] in the clone will be changed from the original, it is critical
     * that [com.kosherjava.zmanim.AstronomicalCalendar.getCalendar].
     * [setTimeZone(TimeZone)][java.util.Calendar.setTimeZone] is called after cloning in order for the
     * AstronomicalCalendar to output times in the expected offset.
     *
     * @see Object.clone
     * @since 1.1
     */
    /*public override fun clone(): Any {
        var clone: GeoLocation? = null
        try {
            clone = super.clone()
        } catch (cnse: CloneNotSupportedException) {
            //Required by the compiler. Should never be reached since we implement clone()
        }
        clone!!.timeZone = timeZone!!.clone() as TimeZone
        clone.locationName = locationName
        return clone
    }*/

    companion object {
        /**
         * This is a best approximation of [java.util.TimeZone.getRawOffset], which returns the offset,
         * regardless of daylight savings time - possibly even with historic accuracy. TODO kinda broke with kotlinx
         * */
        val TimeZone.rawOffset: Long get() =
            if(this is kotlinx.datetime.FixedOffsetTimeZone) this.offset.totalSeconds.seconds.inWholeMilliseconds
            else offsetAt(Clock.System.now()).totalSeconds.seconds.inWholeMilliseconds

        /**
         * Constant for a distance type calculation.
         * @see .getGeodesicDistance
         */
        private const val DISTANCE = 0

        /**
         * Constant for a initial bearing type calculation.
         * @see .getGeodesicInitialBearing
         */
        private const val INITIAL_BEARING = 1

        /**
         * Constant for a final bearing type calculation.
         * @see .getGeodesicFinalBearing
         */
        private const val FINAL_BEARING = 2

        /** constant for milliseconds in a minute (60,000)  */
        private const val MINUTE_MILLIS = (60 * 1000).toLong()

        /** constant for milliseconds in an hour (3,600,000)  */
        private const val HOUR_MILLIS = MINUTE_MILLIS * 60
    }
}