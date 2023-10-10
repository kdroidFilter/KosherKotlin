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
import kotlinx.datetime.TimeZone


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
     * [com.kosherjava.zmanim.AstronomicalCalendar.localDateTime].[setTimeZone(TimeZone)][java.util.Calendar.setTimeZone] be called in order for the
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
    constructor(location: Location): this(location.tz ?: TimeZone.currentSystemDefault(), location.locationName ?: "") {
        this.latitude = location.latitude
        this.longitude = location.longitude
        if(location.elevation != null) this.elevation = location.elevation
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
        val latLong = latitude.toBits()
        val lonLong = longitude.toBits()
        val elevLong = elevation.toBits()
        var result =    37 * 17     + this::class.hashCode()
        result +=       37 * result + (latLong xor (latLong ushr 32)).toInt()
        result +=       37 * result + (lonLong xor (lonLong ushr 32)).toInt()
        result +=       37 * result + (elevLong xor (elevLong ushr 32)).toInt()
        result +=       37 * result + locationName.hashCode()
        result +=       37 * result + timeZone.hashCode()
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
        val mapOfTimeZoneIdToRawOffsetInMillis: Map<String, Int> by lazy { mapOf(
            "Africa/Abidjan" to 0,
            "Africa/Accra" to 0,
            "Africa/Addis_Ababa" to 10800000,
            "Africa/Algiers" to 3600000,
            "Africa/Asmara" to 10800000,
            "Africa/Asmera" to 10800000,
            "Africa/Bamako" to 0,
            "Africa/Bangui" to 3600000,
            "Africa/Banjul" to 0,
            "Africa/Bissau" to 0,
            "Africa/Blantyre" to 7200000,
            "Africa/Brazzaville" to 3600000,
            "Africa/Bujumbura" to 7200000,
            "Africa/Cairo" to 7200000,
            "Africa/Casablanca" to 0,
            "Africa/Ceuta" to 3600000,
            "Africa/Conakry" to 0,
            "Africa/Dakar" to 0,
            "Africa/Dar_es_Salaam" to 10800000,
            "Africa/Djibouti" to 10800000,
            "Africa/Douala" to 3600000,
            "Africa/El_Aaiun" to 0,
            "Africa/Freetown" to 0,
            "Africa/Gaborone" to 7200000,
            "Africa/Harare" to 7200000,
            "Africa/Johannesburg" to 7200000,
            "Africa/Juba" to 7200000,
            "Africa/Kampala" to 10800000,
            "Africa/Khartoum" to 7200000,
            "Africa/Kigali" to 7200000,
            "Africa/Kinshasa" to 3600000,
            "Africa/Lagos" to 3600000,
            "Africa/Libreville" to 3600000,
            "Africa/Lome" to 0,
            "Africa/Luanda" to 3600000,
            "Africa/Lubumbashi" to 7200000,
            "Africa/Lusaka" to 7200000,
            "Africa/Malabo" to 3600000,
            "Africa/Maputo" to 7200000,
            "Africa/Maseru" to 7200000,
            "Africa/Mbabane" to 7200000,
            "Africa/Mogadishu" to 10800000,
            "Africa/Monrovia" to 0,
            "Africa/Nairobi" to 10800000,
            "Africa/Ndjamena" to 3600000,
            "Africa/Niamey" to 3600000,
            "Africa/Nouakchott" to 0,
            "Africa/Ouagadougou" to 0,
            "Africa/Porto-Novo" to 3600000,
            "Africa/Sao_Tome" to 0,
            "Africa/Timbuktu" to 0,
            "Africa/Tripoli" to 7200000,
            "Africa/Tunis" to 3600000,
            "Africa/Windhoek" to 3600000,
            "America/Adak" to -36000000,
            "America/Anchorage" to -32400000,
            "America/Anguilla" to -14400000,
            "America/Antigua" to -14400000,
            "America/Araguaina" to -10800000,
            "America/Argentina/Buenos_Aires" to -10800000,
            "America/Argentina/Catamarca" to -10800000,
            "America/Argentina/ComodRivadavia" to -10800000,
            "America/Argentina/Cordoba" to -10800000,
            "America/Argentina/Jujuy" to -10800000,
            "America/Argentina/La_Rioja" to -10800000,
            "America/Argentina/Mendoza" to -10800000,
            "America/Argentina/Rio_Gallegos" to -10800000,
            "America/Argentina/Salta" to -10800000,
            "America/Argentina/San_Juan" to -10800000,
            "America/Argentina/San_Luis" to -10800000,
            "America/Argentina/Tucuman" to -10800000,
            "America/Argentina/Ushuaia" to -10800000,
            "America/Aruba" to -14400000,
            "America/Asuncion" to -14400000,
            "America/Atikokan" to -18000000,
            "America/Atka" to -36000000,
            "America/Bahia" to -10800000,
            "America/Bahia_Banderas" to -21600000,
            "America/Barbados" to -14400000,
            "America/Belem" to -10800000,
            "America/Belize" to -21600000,
            "America/Blanc-Sablon" to -14400000,
            "America/Boa_Vista" to -14400000,
            "America/Bogota" to -18000000,
            "America/Boise" to -25200000,
            "America/Buenos_Aires" to -10800000,
            "America/Cambridge_Bay" to -25200000,
            "America/Campo_Grande" to -14400000,
            "America/Cancun" to -18000000,
            "America/Caracas" to -14400000,
            "America/Catamarca" to -10800000,
            "America/Cayenne" to -10800000,
            "America/Cayman" to -18000000,
            "America/Chicago" to -21600000,
            "America/Chihuahua" to -25200000,
            "America/Coral_Harbour" to -18000000,
            "America/Cordoba" to -10800000,
            "America/Costa_Rica" to -21600000,
            "America/Creston" to -25200000,
            "America/Cuiaba" to -14400000,
            "America/Curacao" to -14400000,
            "America/Danmarkshavn" to 0,
            "America/Dawson" to -25200000,
            "America/Dawson_Creek" to -25200000,
            "America/Denver" to -25200000,
            "America/Detroit" to -18000000,
            "America/Dominica" to -14400000,
            "America/Edmonton" to -25200000,
            "America/Eirunepe" to -18000000,
            "America/El_Salvador" to -21600000,
            "America/Ensenada" to -28800000,
            "America/Fort_Nelson" to -25200000,
            "America/Fort_Wayne" to -18000000,
            "America/Fortaleza" to -10800000,
            "America/Glace_Bay" to -14400000,
            "America/Godthab" to -10800000,
            "America/Goose_Bay" to -14400000,
            "America/Grand_Turk" to -18000000,
            "America/Grenada" to -14400000,
            "America/Guadeloupe" to -14400000,
            "America/Guatemala" to -21600000,
            "America/Guayaquil" to -18000000,
            "America/Guyana" to -14400000,
            "America/Halifax" to -14400000,
            "America/Havana" to -18000000,
            "America/Hermosillo" to -25200000,
            "America/Indiana/Indianapolis" to -18000000,
            "America/Indiana/Knox" to -21600000,
            "America/Indiana/Marengo" to -18000000,
            "America/Indiana/Petersburg" to -18000000,
            "America/Indiana/Tell_City" to -21600000,
            "America/Indiana/Vevay" to -18000000,
            "America/Indiana/Vincennes" to -18000000,
            "America/Indiana/Winamac" to -18000000,
            "America/Indianapolis" to -18000000,
            "America/Inuvik" to -25200000,
            "America/Iqaluit" to -18000000,
            "America/Jamaica" to -18000000,
            "America/Jujuy" to -10800000,
            "America/Juneau" to -32400000,
            "America/Kentucky/Louisville" to -18000000,
            "America/Kentucky/Monticello" to -18000000,
            "America/Knox_IN" to -21600000,
            "America/Kralendijk" to -14400000,
            "America/La_Paz" to -14400000,
            "America/Lima" to -18000000,
            "America/Los_Angeles" to -28800000,
            "America/Louisville" to -18000000,
            "America/Lower_Princes" to -14400000,
            "America/Maceio" to -10800000,
            "America/Managua" to -21600000,
            "America/Manaus" to -14400000,
            "America/Marigot" to -14400000,
            "America/Martinique" to -14400000,
            "America/Matamoros" to -21600000,
            "America/Mazatlan" to -25200000,
            "America/Mendoza" to -10800000,
            "America/Menominee" to -21600000,
            "America/Merida" to -21600000,
            "America/Metlakatla" to -32400000,
            "America/Mexico_City" to -21600000,
            "America/Miquelon" to -10800000,
            "America/Moncton" to -14400000,
            "America/Monterrey" to -21600000,
            "America/Montevideo" to -10800000,
            "America/Montreal" to -18000000,
            "America/Montserrat" to -14400000,
            "America/Nassau" to -18000000,
            "America/New_York" to -18000000,
            "America/Nipigon" to -18000000,
            "America/Nome" to -32400000,
            "America/Noronha" to -7200000,
            "America/North_Dakota/Beulah" to -21600000,
            "America/North_Dakota/Center" to -21600000,
            "America/North_Dakota/New_Salem" to -21600000,
            "America/Nuuk" to -10800000,
            "America/Ojinaga" to -25200000,
            "America/Panama" to -18000000,
            "America/Pangnirtung" to -18000000,
            "America/Paramaribo" to -10800000,
            "America/Phoenix" to -25200000,
            "America/Port-au-Prince" to -18000000,
            "America/Port_of_Spain" to -14400000,
            "America/Porto_Acre" to -18000000,
            "America/Porto_Velho" to -14400000,
            "America/Puerto_Rico" to -14400000,
            "America/Punta_Arenas" to -10800000,
            "America/Rainy_River" to -21600000,
            "America/Rankin_Inlet" to -21600000,
            "America/Recife" to -10800000,
            "America/Regina" to -21600000,
            "America/Resolute" to -21600000,
            "America/Rio_Branco" to -18000000,
            "America/Rosario" to -10800000,
            "America/Santa_Isabel" to -28800000,
            "America/Santarem" to -10800000,
            "America/Santiago" to -14400000,
            "America/Santo_Domingo" to -14400000,
            "America/Sao_Paulo" to -10800000,
            "America/Scoresbysund" to -3600000,
            "America/Shiprock" to -25200000,
            "America/Sitka" to -32400000,
            "America/St_Barthelemy" to -14400000,
            "America/St_Johns" to -12600000,
            "America/St_Kitts" to -14400000,
            "America/St_Lucia" to -14400000,
            "America/St_Thomas" to -14400000,
            "America/St_Vincent" to -14400000,
            "America/Swift_Current" to -21600000,
            "America/Tegucigalpa" to -21600000,
            "America/Thule" to -14400000,
            "America/Thunder_Bay" to -18000000,
            "America/Tijuana" to -28800000,
            "America/Toronto" to -18000000,
            "America/Tortola" to -14400000,
            "America/Vancouver" to -28800000,
            "America/Virgin" to -14400000,
            "America/Whitehorse" to -25200000,
            "America/Winnipeg" to -21600000,
            "America/Yakutat" to -32400000,
            "America/Yellowknife" to -25200000,
            "Antarctica/Casey" to 39600000,
            "Antarctica/Davis" to 25200000,
            "Antarctica/DumontDUrville" to 36000000,
            "Antarctica/Macquarie" to 36000000,
            "Antarctica/Mawson" to 18000000,
            "Antarctica/McMurdo" to 43200000,
            "Antarctica/Palmer" to -10800000,
            "Antarctica/Rothera" to -10800000,
            "Antarctica/South_Pole" to 43200000,
            "Antarctica/Syowa" to 10800000,
            "Antarctica/Troll" to 0,
            "Antarctica/Vostok" to 21600000,
            "Arctic/Longyearbyen" to 3600000,
            "Asia/Aden" to 10800000,
            "Asia/Almaty" to 21600000,
            "Asia/Amman" to 7200000,
            "Asia/Anadyr" to 43200000,
            "Asia/Aqtau" to 18000000,
            "Asia/Aqtobe" to 18000000,
            "Asia/Ashgabat" to 18000000,
            "Asia/Ashkhabad" to 18000000,
            "Asia/Atyrau" to 18000000,
            "Asia/Baghdad" to 10800000,
            "Asia/Bahrain" to 10800000,
            "Asia/Baku" to 14400000,
            "Asia/Bangkok" to 25200000,
            "Asia/Barnaul" to 25200000,
            "Asia/Beirut" to 7200000,
            "Asia/Bishkek" to 21600000,
            "Asia/Brunei" to 28800000,
            "Asia/Calcutta" to 19800000,
            "Asia/Chita" to 32400000,
            "Asia/Choibalsan" to 28800000,
            "Asia/Chongqing" to 28800000,
            "Asia/Chungking" to 28800000,
            "Asia/Colombo" to 19800000,
            "Asia/Dacca" to 21600000,
            "Asia/Damascus" to 7200000,
            "Asia/Dhaka" to 21600000,
            "Asia/Dili" to 32400000,
            "Asia/Dubai" to 14400000,
            "Asia/Dushanbe" to 18000000,
            "Asia/Famagusta" to 7200000,
            "Asia/Gaza" to 7200000,
            "Asia/Harbin" to 28800000,
            "Asia/Hebron" to 7200000,
            "Asia/Ho_Chi_Minh" to 25200000,
            "Asia/Hong_Kong" to 28800000,
            "Asia/Hovd" to 25200000,
            "Asia/Irkutsk" to 28800000,
            "Asia/Istanbul" to 10800000,
            "Asia/Jakarta" to 25200000,
            "Asia/Jayapura" to 32400000,
            "Asia/Jerusalem" to 7200000,
            "Asia/Kabul" to 16200000,
            "Asia/Kamchatka" to 43200000,
            "Asia/Karachi" to 18000000,
            "Asia/Kashgar" to 21600000,
            "Asia/Kathmandu" to 20700000,
            "Asia/Katmandu" to 20700000,
            "Asia/Khandyga" to 32400000,
            "Asia/Kolkata" to 19800000,
            "Asia/Krasnoyarsk" to 25200000,
            "Asia/Kuala_Lumpur" to 28800000,
            "Asia/Kuching" to 28800000,
            "Asia/Kuwait" to 10800000,
            "Asia/Macao" to 28800000,
            "Asia/Macau" to 28800000,
            "Asia/Magadan" to 39600000,
            "Asia/Makassar" to 28800000,
            "Asia/Manila" to 28800000,
            "Asia/Muscat" to 14400000,
            "Asia/Nicosia" to 7200000,
            "Asia/Novokuznetsk" to 25200000,
            "Asia/Novosibirsk" to 25200000,
            "Asia/Omsk" to 21600000,
            "Asia/Oral" to 18000000,
            "Asia/Phnom_Penh" to 25200000,
            "Asia/Pontianak" to 25200000,
            "Asia/Pyongyang" to 32400000,
            "Asia/Qatar" to 10800000,
            "Asia/Qostanay" to 21600000,
            "Asia/Qyzylorda" to 18000000,
            "Asia/Rangoon" to 23400000,
            "Asia/Riyadh" to 10800000,
            "Asia/Saigon" to 25200000,
            "Asia/Sakhalin" to 39600000,
            "Asia/Samarkand" to 18000000,
            "Asia/Seoul" to 32400000,
            "Asia/Shanghai" to 28800000,
            "Asia/Singapore" to 28800000,
            "Asia/Srednekolymsk" to 39600000,
            "Asia/Taipei" to 28800000,
            "Asia/Tashkent" to 18000000,
            "Asia/Tbilisi" to 14400000,
            "Asia/Tehran" to 12600000,
            "Asia/Tel_Aviv" to 7200000,
            "Asia/Thimbu" to 21600000,
            "Asia/Thimphu" to 21600000,
            "Asia/Tokyo" to 32400000,
            "Asia/Tomsk" to 25200000,
            "Asia/Ujung_Pandang" to 28800000,
            "Asia/Ulaanbaatar" to 28800000,
            "Asia/Ulan_Bator" to 28800000,
            "Asia/Urumqi" to 21600000,
            "Asia/Ust-Nera" to 36000000,
            "Asia/Vientiane" to 25200000,
            "Asia/Vladivostok" to 36000000,
            "Asia/Yakutsk" to 32400000,
            "Asia/Yangon" to 23400000,
            "Asia/Yekaterinburg" to 18000000,
            "Asia/Yerevan" to 14400000,
            "Atlantic/Azores" to -3600000,
            "Atlantic/Bermuda" to -14400000,
            "Atlantic/Canary" to 0,
            "Atlantic/Cape_Verde" to -3600000,
            "Atlantic/Faeroe" to 0,
            "Atlantic/Faroe" to 0,
            "Atlantic/Jan_Mayen" to 3600000,
            "Atlantic/Madeira" to 0,
            "Atlantic/Reykjavik" to 0,
            "Atlantic/South_Georgia" to -7200000,
            "Atlantic/St_Helena" to 0,
            "Atlantic/Stanley" to -10800000,
            "Australia/ACT" to 36000000,
            "Australia/Adelaide" to 34200000,
            "Australia/Brisbane" to 36000000,
            "Australia/Broken_Hill" to 34200000,
            "Australia/Canberra" to 36000000,
            "Australia/Currie" to 36000000,
            "Australia/Darwin" to 34200000,
            "Australia/Eucla" to 31500000,
            "Australia/Hobart" to 36000000,
            "Australia/LHI" to 37800000,
            "Australia/Lindeman" to 36000000,
            "Australia/Lord_Howe" to 37800000,
            "Australia/Melbourne" to 36000000,
            "Australia/NSW" to 36000000,
            "Australia/North" to 34200000,
            "Australia/Perth" to 28800000,
            "Australia/Queensland" to 36000000,
            "Australia/South" to 34200000,
            "Australia/Sydney" to 36000000,
            "Australia/Tasmania" to 36000000,
            "Australia/Victoria" to 36000000,
            "Australia/West" to 28800000,
            "Australia/Yancowinna" to 34200000,
            "Brazil/Acre" to -18000000,
            "Brazil/DeNoronha" to -7200000,
            "Brazil/East" to -10800000,
            "Brazil/West" to -14400000,
            "CET" to 3600000,
            "CST6CDT" to -21600000,
            "Canada/Atlantic" to -14400000,
            "Canada/Central" to -21600000,
            "Canada/Eastern" to -18000000,
            "Canada/Mountain" to -25200000,
            "Canada/Newfoundland" to -12600000,
            "Canada/Pacific" to -28800000,
            "Canada/Saskatchewan" to -21600000,
            "Canada/Yukon" to -25200000,
            "Chile/Continental" to -14400000,
            "Chile/EasterIsland" to -21600000,
            "Cuba" to -18000000,
            "EET" to 7200000,
            "EST5EDT" to -18000000,
            "Egypt" to 7200000,
            "Eire" to 0,
            "Etc/GMT" to 0,
            "Etc/GMT+0" to 0,
            "Etc/GMT+1" to -3600000,
            "Etc/GMT+10" to -36000000,
            "Etc/GMT+11" to -39600000,
            "Etc/GMT+12" to -43200000,
            "Etc/GMT+2" to -7200000,
            "Etc/GMT+3" to -10800000,
            "Etc/GMT+4" to -14400000,
            "Etc/GMT+5" to -18000000,
            "Etc/GMT+6" to -21600000,
            "Etc/GMT+7" to -25200000,
            "Etc/GMT+8" to -28800000,
            "Etc/GMT+9" to -32400000,
            "Etc/GMT-0" to 0,
            "Etc/GMT-1" to 3600000,
            "Etc/GMT-10" to 36000000,
            "Etc/GMT-11" to 39600000,
            "Etc/GMT-12" to 43200000,
            "Etc/GMT-13" to 46800000,
            "Etc/GMT-14" to 50400000,
            "Etc/GMT-2" to 7200000,
            "Etc/GMT-3" to 10800000,
            "Etc/GMT-4" to 14400000,
            "Etc/GMT-5" to 18000000,
            "Etc/GMT-6" to 21600000,
            "Etc/GMT-7" to 25200000,
            "Etc/GMT-8" to 28800000,
            "Etc/GMT-9" to 32400000,
            "Etc/GMT0" to 0,
            "Etc/Greenwich" to 0,
            "Etc/UCT" to 0,
            "Etc/UTC" to 0,
            "Etc/Universal" to 0,
            "Etc/Zulu" to 0,
            "Europe/Amsterdam" to 3600000,
            "Europe/Andorra" to 3600000,
            "Europe/Astrakhan" to 14400000,
            "Europe/Athens" to 7200000,
            "Europe/Belfast" to 0,
            "Europe/Belgrade" to 3600000,
            "Europe/Berlin" to 3600000,
            "Europe/Bratislava" to 3600000,
            "Europe/Brussels" to 3600000,
            "Europe/Bucharest" to 7200000,
            "Europe/Budapest" to 3600000,
            "Europe/Busingen" to 3600000,
            "Europe/Chisinau" to 7200000,
            "Europe/Copenhagen" to 3600000,
            "Europe/Dublin" to 0,
            "Europe/Gibraltar" to 3600000,
            "Europe/Guernsey" to 0,
            "Europe/Helsinki" to 7200000,
            "Europe/Isle_of_Man" to 0,
            "Europe/Istanbul" to 10800000,
            "Europe/Jersey" to 0,
            "Europe/Kaliningrad" to 7200000,
            "Europe/Kiev" to 7200000,
            "Europe/Kirov" to 10800000,
            "Europe/Lisbon" to 0,
            "Europe/Ljubljana" to 3600000,
            "Europe/London" to 0,
            "Europe/Luxembourg" to 3600000,
            "Europe/Madrid" to 3600000,
            "Europe/Malta" to 3600000,
            "Europe/Mariehamn" to 7200000,
            "Europe/Minsk" to 10800000,
            "Europe/Monaco" to 3600000,
            "Europe/Moscow" to 10800000,
            "Europe/Nicosia" to 7200000,
            "Europe/Oslo" to 3600000,
            "Europe/Paris" to 3600000,
            "Europe/Podgorica" to 3600000,
            "Europe/Prague" to 3600000,
            "Europe/Riga" to 7200000,
            "Europe/Rome" to 3600000,
            "Europe/Samara" to 14400000,
            "Europe/San_Marino" to 3600000,
            "Europe/Sarajevo" to 3600000,
            "Europe/Saratov" to 14400000,
            "Europe/Simferopol" to 10800000,
            "Europe/Skopje" to 3600000,
            "Europe/Sofia" to 7200000,
            "Europe/Stockholm" to 3600000,
            "Europe/Tallinn" to 7200000,
            "Europe/Tirane" to 3600000,
            "Europe/Tiraspol" to 7200000,
            "Europe/Ulyanovsk" to 14400000,
            "Europe/Uzhgorod" to 7200000,
            "Europe/Vaduz" to 3600000,
            "Europe/Vatican" to 3600000,
            "Europe/Vienna" to 3600000,
            "Europe/Vilnius" to 7200000,
            "Europe/Volgograd" to 10800000,
            "Europe/Warsaw" to 3600000,
            "Europe/Zagreb" to 3600000,
            "Europe/Zaporozhye" to 7200000,
            "Europe/Zurich" to 3600000,
            "GB" to 0,
            "GB-Eire" to 0,
            "GMT" to 0,
            "GMT0" to 0,
            "Greenwich" to 0,
            "Hongkong" to 28800000,
            "Iceland" to 0,
            "Indian/Antananarivo" to 10800000,
            "Indian/Chagos" to 21600000,
            "Indian/Christmas" to 25200000,
            "Indian/Cocos" to 23400000,
            "Indian/Comoro" to 10800000,
            "Indian/Kerguelen" to 18000000,
            "Indian/Mahe" to 14400000,
            "Indian/Maldives" to 18000000,
            "Indian/Mauritius" to 14400000,
            "Indian/Mayotte" to 10800000,
            "Indian/Reunion" to 14400000,
            "Iran" to 12600000,
            "Israel" to 7200000,
            "Jamaica" to -18000000,
            "Japan" to 32400000,
            "Kwajalein" to 43200000,
            "Libya" to 7200000,
            "MET" to 3600000,
            "MST7MDT" to -25200000,
            "Mexico/BajaNorte" to -28800000,
            "Mexico/BajaSur" to -25200000,
            "Mexico/General" to -21600000,
            "NZ" to 43200000,
            "NZ-CHAT" to 45900000,
            "Navajo" to -25200000,
            "PRC" to 28800000,
            "PST8PDT" to -28800000,
            "Pacific/Apia" to 46800000,
            "Pacific/Auckland" to 43200000,
            "Pacific/Bougainville" to 39600000,
            "Pacific/Chatham" to 45900000,
            "Pacific/Chuuk" to 36000000,
            "Pacific/Easter" to -21600000,
            "Pacific/Efate" to 39600000,
            "Pacific/Enderbury" to 46800000,
            "Pacific/Fakaofo" to 46800000,
            "Pacific/Fiji" to 43200000,
            "Pacific/Funafuti" to 43200000,
            "Pacific/Galapagos" to -21600000,
            "Pacific/Gambier" to -32400000,
            "Pacific/Guadalcanal" to 39600000,
            "Pacific/Guam" to 36000000,
            "Pacific/Honolulu" to -36000000,
            "Pacific/Johnston" to -36000000,
            "Pacific/Kiritimati" to 50400000,
            "Pacific/Kosrae" to 39600000,
            "Pacific/Kwajalein" to 43200000,
            "Pacific/Majuro" to 43200000,
            "Pacific/Marquesas" to -34200000,
            "Pacific/Midway" to -39600000,
            "Pacific/Nauru" to 43200000,
            "Pacific/Niue" to -39600000,
            "Pacific/Norfolk" to 39600000,
            "Pacific/Noumea" to 39600000,
            "Pacific/Pago_Pago" to -39600000,
            "Pacific/Palau" to 32400000,
            "Pacific/Pitcairn" to -28800000,
            "Pacific/Pohnpei" to 39600000,
            "Pacific/Ponape" to 39600000,
            "Pacific/Port_Moresby" to 36000000,
            "Pacific/Rarotonga" to -36000000,
            "Pacific/Saipan" to 36000000,
            "Pacific/Samoa" to -39600000,
            "Pacific/Tahiti" to -36000000,
            "Pacific/Tarawa" to 43200000,
            "Pacific/Tongatapu" to 46800000,
            "Pacific/Truk" to 36000000,
            "Pacific/Wake" to 43200000,
            "Pacific/Wallis" to 43200000,
            "Pacific/Yap" to 36000000,
            "Poland" to 3600000,
            "Portugal" to 0,
            "ROK" to 32400000,
            "Singapore" to 28800000,
            "SystemV/AST4" to -14400000,
            "SystemV/AST4ADT" to -14400000,
            "SystemV/CST6" to -21600000,
            "SystemV/CST6CDT" to -21600000,
            "SystemV/EST5" to -18000000,
            "SystemV/EST5EDT" to -18000000,
            "SystemV/HST10" to -36000000,
            "SystemV/MST7" to -25200000,
            "SystemV/MST7MDT" to -25200000,
            "SystemV/PST8" to -28800000,
            "SystemV/PST8PDT" to -28800000,
            "SystemV/YST9" to -32400000,
            "SystemV/YST9YDT" to -32400000,
            "Turkey" to 10800000,
            "UCT" to 0,
            "US/Alaska" to -32400000,
            "US/Aleutian" to -36000000,
            "US/Arizona" to -25200000,
            "US/Central" to -21600000,
            "US/East-Indiana" to -18000000,
            "US/Eastern" to -18000000,
            "US/Hawaii" to -36000000,
            "US/Indiana-Starke" to -21600000,
            "US/Michigan" to -18000000,
            "US/Mountain" to -25200000,
            "US/Pacific" to -28800000,
            "US/Samoa" to -39600000,
            "UTC" to 0,
            "Universal" to 0,
            "W-SU" to 10800000,
            "WET" to 0,
            "Zulu" to 0,
            "EST" to -18000000,
            "HST" to -36000000,
            "MST" to -25200000,
            "ACT" to 34200000,
            "AET" to 36000000,
            "AGT" to -10800000,
            "ART" to 7200000,
            "AST" to -32400000,
            "BET" to -10800000,
            "BST" to 21600000,
            "CAT" to 7200000,
            "CNT" to -12600000,
            "CST" to -21600000,
            "CTT" to 28800000,
            "EAT" to 10800000,
            "ECT" to 3600000,
            "IET" to -18000000,
            "IST" to 19800000,
            "JST" to 32400000,
            "MIT" to 46800000,
            "NET" to 14400000,
            "NST" to 43200000,
            "PLT" to 18000000,
            "PNT" to -25200000,
            "PRT" to -14400000,
            "PST" to -28800000,
            "SST" to 39600000,
            "VST" to 25200000,
        ) }
        /**
         * This is a best approximation of [java.util.TimeZone.getRawOffset], which returns the offset,
         * regardless of daylight savings time - possibly even with historic accuracy.
         * The current implementation uses a map of id to raw offset taken from the above java method.
         * */
        val TimeZone.rawOffset: Int get() = mapOfTimeZoneIdToRawOffsetInMillis[id]!!

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