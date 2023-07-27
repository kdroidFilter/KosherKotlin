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
import kotlin.math.PI
import kotlin.math.acos
import kotlin.math.asin
import kotlin.math.atan
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.sin
import kotlin.math.tan


/**
 * Implementation of sunrise and sunset methods to calculate astronomical times. This calculator uses the Java algorithm
 * written by [Kevin
 * Boone](htts://web.archive.org/web/20090531215353/http://www.kevinboone.com/suntimes.html) that is based on the [US Naval Observatory's](https://aa.usno.navy.mil/)[Astronomical Almanac](https://aa.usno.navy.mil/publications/asa) and used with his permission. Added to Kevin's
 * code is adjustment of the zenith to account for elevation. This algorithm returns the same time every year and does not
 * account for leap years. It is not as accurate as the Jean Meeus based [NOAACalculator] that is the default calculator
 * use by the KosherJava *zmanim* library.
 *
 * @author  Eliyahu Hershfeld 2004 - 2023
 * @author  Kevin Boone 2000
 */
class SunTimesCalculator : AstronomicalCalculator() {
    override fun copy(): AstronomicalCalculator = SunTimesCalculator()
    /**
     * @see getCalculatorName
     */
    override val calculatorName: String
        get() {
            return "US Naval Almanac Algorithm"
        }

    /**
     * @see com.kosherjava.zmanim.util.AstronomicalCalculator.getUTCSunrise
     */
    override fun getUTCSunrise(
        LocalDate: LocalDate,
        geoLocation: GeoLocation,
        zenith: Double,
        adjustForElevation: Boolean
    ): Double {
        val doubleTime: Double
        val elevation: Double = if (adjustForElevation) geoLocation.elevation else 0.0
        val adjustedZenith: Double = adjustZenith(zenith, elevation)
        doubleTime = getTimeUTC(LocalDate, geoLocation, adjustedZenith, true)
        return doubleTime
    }

    /**
     * @see com.kosherjava.zmanim.util.AstronomicalCalculator.getUTCSunset
     */
    override fun getUTCSunset(
        LocalDate: LocalDate,
        geoLocation: GeoLocation,
        zenith: Double,
        adjustForElevation: Boolean
    ): Double {
        var doubleTime: Double = Double.NaN
        val elevation: Double = if (adjustForElevation) geoLocation.elevation else 0.0
        val adjustedZenith: Double = adjustZenith(zenith, elevation)
        doubleTime = getTimeUTC(LocalDate, geoLocation, adjustedZenith, false)
        return doubleTime
    }

    /**
     * Return the [Universal Coordinated Time](https://en.wikipedia.org/wiki/Universal_Coordinated_Time) (UTC)
     * of [solar noon](https://en.wikipedia.org/wiki/Noon#Solar_noon) for the given day at the given location
     * on earth. This implementation returns solar noon as the time halfway between sunrise and sunset.
     * Other calculators may return true solar noon. See [The Definition of Chatzos](https://kosherjava.com/2020/07/02/definition-of-chatzos/) for details on solar
     * noon calculations.
     * @see com.kosherjava.zmanim.util.AstronomicalCalculator.getUTCNoon
     * @see NOAACalculator
     *
     *
     * @param LocalDate
     * The LocalDate representing the date to calculate solar noon for
     * @param geoLocation
     * The location information used for astronomical calculating sun times.
     * @return the time in minutes from zero UTC
     */
    override fun getUTCNoon(LocalDate: LocalDate, geoLocation: GeoLocation): Double {
        val sunrise: Double = getUTCSunrise(LocalDate, geoLocation, 90.0, true)
        val sunset: Double = getUTCSunset(LocalDate, geoLocation, 90.0, true)
        return (sunrise + ((sunset - sunrise) / 2))
    }

    companion object {
        /**
         * The number of degrees of longitude that corresponds to one hour time difference.
         */
        private val DEG_PER_HOUR: Double = 360.0 / 24.0

        /**
         * @param deg the degrees
         * @return sin of the angle in degrees
         */
        private fun sinDeg(deg: Double): Double {
            return sin(deg * 2.0 * PI / 360.0)
        }

        /**
         * @param x angle
         * @return acos of the angle in degrees
         */
        private fun acosDeg(x: Double): Double {
            return acos(x) * 360.0 / (2 * PI)
        }

        /**
         * @param x angle
         * @return asin of the angle in degrees
         */
        private fun asinDeg(x: Double): Double {
            return asin(x) * 360.0 / (2 * PI)
        }

        /**
         * @param deg degrees
         * @return tan of the angle in degrees
         */
        private fun tanDeg(deg: Double): Double {
            return tan(deg * 2.0 * PI / 360.0)
        }

        /**
         * Calculate cosine of the angle in degrees
         *
         * @param deg degrees
         * @return cosine of the angle in degrees
         */
        private fun cosDeg(deg: Double): Double {
            return cos(deg * 2.0 * PI / 360.0)
        }

        /**
         * Get time difference between location's longitude and the Meridian, in hours.
         *
         * @param longitude the longitude
         * @return time difference between the location's longitude and the Meridian, in hours. West of Meridian has a negative time difference
         */
        private fun getHoursFromMeridian(longitude: Double): Double {
            return longitude / DEG_PER_HOUR
        }

        /**
         * Calculate the approximate time of sunset or sunrise in days since midnight Jan 1st, assuming 6am and 6pm events. We
         * need this figure to derive the Sun's mean anomaly.
         *
         * @param dayOfYear the day of year
         * @param hoursFromMeridian hours from the meridian
         * @param isSunrise true for sunrise and false for sunset
         *
         * @return the approximate time of sunset or sunrise in days since midnight Jan 1st, assuming 6am and 6pm events. We
         * need this figure to derive the Sun's mean anomaly.
         */
        private fun getApproxTimeDays(dayOfYear: Int, hoursFromMeridian: Double, isSunrise: Boolean): Double {
            if (isSunrise) {
                return dayOfYear + ((6.0 - hoursFromMeridian) / 24)
            } else { // sunset
                return dayOfYear + ((18.0 - hoursFromMeridian) / 24)
            }
        }

        /**
         * Calculate the Sun's mean anomaly in degrees, at sunrise or sunset, given the longitude in degrees
         *
         * @param dayOfYear the day of the year
         * @param longitude longitude
         * @param isSunrise true for sunrise and false for sunset
         * @return the Sun's mean anomaly in degrees
         */
        private fun getMeanAnomaly(dayOfYear: Int, longitude: Double, isSunrise: Boolean): Double {
            return (0.9856 * getApproxTimeDays(dayOfYear, getHoursFromMeridian(longitude), isSunrise)) - 3.289
        }

        /**
         * @param sunMeanAnomaly the Sun's mean anomaly in degrees
         * @return the Sun's true longitude in degrees. The result is an angle >= 0 and <= 360.
         */
        private fun getSunTrueLongitude(sunMeanAnomaly: Double): Double {
            var l: Double =
                sunMeanAnomaly + (1.916 * sinDeg(sunMeanAnomaly)) + (0.020 * sinDeg(2 * sunMeanAnomaly)) + 282.634

            // get longitude into 0-360 degree range
            if (l >= 360.0) {
                l = l - 360.0
            }
            if (l < 0) {
                l = l + 360.0
            }
            return l
        }

        /**
         * Calculates the Sun's right ascension in hours.
         * @param sunTrueLongitude the Sun's true longitude in degrees > 0 and < 360.
         * @return the Sun's right ascension in hours in angles > 0 and < 360.
         */
        private fun getSunRightAscensionHours(sunTrueLongitude: Double): Double {
            val a: Double = 0.91764 * tanDeg(sunTrueLongitude)
            var ra: Double = 360.0 / (2.0 * PI) * atan(a)
            val lQuadrant: Double = floor(sunTrueLongitude / 90.0) * 90.0
            val raQuadrant: Double = floor(ra / 90.0) * 90.0
            ra = ra + (lQuadrant - raQuadrant)
            return ra / DEG_PER_HOUR // convert to hours
        }

        /**
         * Calculate the cosine of the Sun's local hour angle
         *
         * @param sunTrueLongitude the sun's true longitude
         * @param latitude the latitude
         * @param zenith the zenith
         * @return the cosine of the Sun's local hour angle
         */
        private fun getCosLocalHourAngle(sunTrueLongitude: Double, latitude: Double, zenith: Double): Double {
            val sinDec: Double = 0.39782 * sinDeg(sunTrueLongitude)
            val cosDec: Double = cosDeg(asinDeg(sinDec))
            return (cosDeg(zenith) - (sinDec * sinDeg(latitude))) / (cosDec * cosDeg(latitude))
        }

        /**
         * Calculate local mean time of rising or setting. By 'local' is meant the exact time at the location, assuming that
         * there were no time zone. That is, the time difference between the location and the Meridian depended entirely on
         * the longitude. We can't do anything with this time directly; we must convert it to UTC and then to a local time.
         *
         * @param localHour the local hour
         * @param sunRightAscensionHours the sun's right ascention in hours
         * @param approxTimeDays approximate time days
         *
         * @return the fractional number of hours since midnight as a double
         */
        private fun getLocalMeanTime(
            localHour: Double,
            sunRightAscensionHours: Double,
            approxTimeDays: Double
        ): Double {
            return (localHour + sunRightAscensionHours) - (0.06571 * approxTimeDays) - 6.622
        }

        /**
         * Get sunrise or sunset time in UTC, according to flag. This time is returned as
         * a double and is not adjusted for time-zone.
         *
         * @param LocalDate
         * the LocalDate object to extract the day of year for calculation
         * @param geoLocation
         * the GeoLocation object that contains the latitude and longitude
         * @param zenith
         * Sun's zenith, in degrees
         * @param isSunrise
         * True for sunrise and false for sunset.
         * @return the time as a double. If an error was encountered in the calculation
         * (expected behavior for some locations such as near the poles,
         * [Double.NaN] will be returned.
         */
        private fun getTimeUTC(
            LocalDate: LocalDate,
            geoLocation: GeoLocation,
            zenith: Double,
            isSunrise: Boolean
        ): Double {
            val dayOfYear = LocalDate.dayOfYear
            val sunMeanAnomaly: Double = getMeanAnomaly(dayOfYear, geoLocation.longitude, isSunrise)
            val sunTrueLong: Double = getSunTrueLongitude(sunMeanAnomaly)
            val sunRightAscensionHours: Double = getSunRightAscensionHours(sunTrueLong)
            val cosLocalHourAngle: Double = getCosLocalHourAngle(sunTrueLong, geoLocation.latitude, zenith)
            var localHourAngle: Double = 0.0
            if (isSunrise) {
                localHourAngle = 360.0 - acosDeg(cosLocalHourAngle)
            } else { // sunset
                localHourAngle = acosDeg(cosLocalHourAngle)
            }
            val localHour: Double = localHourAngle / DEG_PER_HOUR
            val localMeanTime: Double = getLocalMeanTime(
                localHour, sunRightAscensionHours,
                getApproxTimeDays(dayOfYear, getHoursFromMeridian(geoLocation.longitude), isSunrise)
            )
            var pocessedTime: Double = localMeanTime - getHoursFromMeridian(geoLocation.longitude)
            while (pocessedTime < 0.0) {
                pocessedTime += 24.0
            }
            while (pocessedTime >= 24.0) {
                pocessedTime -= 24.0
            }
            return pocessedTime
        }
    }
}