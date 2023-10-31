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
package sternbach.software.kosherkotlin.util

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

import sternbach.software.kosherkotlin.hebrewcalendar.*
import kotlin.math.*

/**
 * Implementation of sunrise and sunset methods to calculate astronomical times based on the [NOAA](https://noaa.gov) algorithm. This calculator uses the Java algorithm based on the implementation by [NOAA - National Oceanic and Atmospheric Administration](https://noaa.gov)'s [Surface Radiation Research Branch](https://www.srrb.noaa.gov/highlights/sunrise/sunrise.html). NOAA's [implementation](https://www.srrb.noaa.gov/highlights/sunrise/solareqns.PDF) is based on equations from [Astronomical Algorithms](https://www.amazon.com/Astronomical-Table-Sun-Moon-Planets/dp/1942675038/) by [Jean Meeus](https://en.wikipedia.org/wiki/Jean_Meeus). Added to the algorithm is an adjustment of the zenith
 * to account for elevation. The algorithm can be found in the [Wikipedia Sunrise Equation](https://en.wikipedia.org/wiki/Sunrise_equation) article.
 *
 * @author  Eliyahu Hershfeld 2011 - 2023
 */
class NOAACalculator : AstronomicalCalculator() {
    override fun copy(): AstronomicalCalculator = NOAACalculator()
    /**
     * @see com.kosherjava.zmanim.util.AstronomicalCalculator.getCalculatorName
     */
    override val calculatorName: String
        get() = "US National Oceanic and Atmospheric Administration Algorithm"

    /**
     * @see com.kosherjava.zmanim.util.AstronomicalCalculator.getUTCSunrise
     */
    override fun getUTCSunrise(
        LocalDate: LocalDate,
        geoLocation: GeoLocation,
        zenith: Double,
        adjustForElevation: Boolean
    ): Double {
        val elevation = if (adjustForElevation) geoLocation.elevation else 0.0
        val adjustedZenith: Double = adjustZenith(zenith, elevation)
        var sunrise: Double = getSunriseUTC(
            DateUtils.getJulianDay(LocalDate), geoLocation.latitude, -geoLocation.longitude,
            adjustedZenith
        )
//        println("kelevation: $elevation, adjustedZenith: $adjustedZenith, sunrise: $sunrise")
        sunrise /= 60

        // ensure that the time is >= 0 and < 24
        while (sunrise < 0.0) sunrise += 24.0
        while (sunrise >= 24.0) sunrise -= 24.0
        return sunrise
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
        val elevation: Double = if (adjustForElevation) geoLocation.elevation else 0.0
        val adjustedZenith: Double = adjustZenith(zenith, elevation)
        var sunset: Double = getSunsetUTC(
            DateUtils.getJulianDay(LocalDate), geoLocation.latitude, -geoLocation.longitude,
            adjustedZenith
        )
        sunset /= 60

        // ensure that the time is >= 0 and < 24
        while (sunset < 0.0) sunset += 24.0
        while (sunset >= 24.0) sunset -= 24.0
        return sunset
    }

    /**
     * Return the [Universal Coordinated Time](https://en.wikipedia.org/wiki/Universal_Coordinated_Time) (UTC)
     * of [solar noon](https://en.wikipedia.org/wiki/Noon#Solar_noon) for the given day at the given location
     * on earth. This implementation returns true solar noon as opposed to the time halfway between sunrise and sunset.
     * Other calculators may return a more simplified calculation of halfway between sunrise and sunset. See [The Definition of *Chatzos*](https://kosherjava.com/2020/07/02/definition-of-chatzos/) for details on
     * solar noon calculations.
     * @see com.kosherjava.zmanim.util.AstronomicalCalculator.getUTCNoon
     * @see solarNoonUTC
     * @param calendar
     * The Calendar representing the date to calculate solar noon for
     * @param geoLocation
     * The location information used for astronomical calculating sun times. This class uses only requires
     * the longitude for calculating noon since it is the same time anywhere along the longitude line.
     * @return the time in minutes from zero UTC
     */
    override fun getUTCNoon(date: LocalDate, geoLocation: GeoLocation): Double {
        val julianDay: Double = DateUtils.getJulianDay(date)
        val julianCenturies: Double = getJulianCenturiesFromJulianDay(julianDay)
        var noon: Double = getSolarNoonUTC(julianCenturies, -geoLocation.longitude)
//        println("getUTCNoon($date, ${geoLocation.locationName})")
//        println("JulianDay: $julianDay julianCenturies: $julianCenturies noon: $noon")
        noon /= 60

        // ensure that the time is >= 0 and < 24
        while (noon < 0.0) noon += 24.0
        while (noon >= 24.0) noon -= 24.0
        return noon
    }

    companion object {
        /**
         * The [Julian day](https://en.wikipedia.org/wiki/Julian_day) of January 1, 2000, known as
         * [J2000.0](https://en.wikipedia.org/wiki/Epoch_(astronomy)#J2000).
         */
        private const val JULIAN_DAY_JAN_1_2000: Double = 2451545.0

        /**
         * Julian days per century.
         */
        private const val JULIAN_DAYS_PER_CENTURY: Double = 36525.0

        /**
         * Convert [Julian day](https://en.wikipedia.org/wiki/Julian_day) to centuries since [J2000.0](https://en.wikipedia.org/wiki/Epoch_(astronomy)#J2000).
         *
         * @param julianDay
         * the Julian Day to convert
         * @return the centuries since 2000 Julian corresponding to the Julian Day
         */
        private fun getJulianCenturiesFromJulianDay(julianDay: Double): Double =
            (julianDay - JULIAN_DAY_JAN_1_2000) / JULIAN_DAYS_PER_CENTURY

        /**
         * Convert centuries since [J2000.0](https://en.wikipedia.org/wiki/Epoch_(astronomy)#J2000) to
         * [Julian day](https://en.wikipedia.org/wiki/Julian_day).
         *
         * @param julianCenturies
         * the number of Julian centuries since [J2000.0](https://en.wikipedia.org/wiki/Epoch_(astronomy)#J2000).
         * @return the Julian Day corresponding to the Julian centuries passed in
         */
        private fun getJulianDayFromJulianCenturies(julianCenturies: Double): Double =
            julianCenturies * JULIAN_DAYS_PER_CENTURY + JULIAN_DAY_JAN_1_2000

        /**
         * Returns the Geometric [Mean Longitude](https://en.wikipedia.org/wiki/Mean_longitude) of the Sun.
         *
         * @param julianCenturies
         * the number of Julian centuries since [J2000.0](https://en.wikipedia.org/wiki/Epoch_(astronomy)#J2000).
         * @return the Geometric Mean Longitude of the Sun in degrees
         */
        private fun getSunGeometricMeanLongitude(julianCenturies: Double): Double {
            var longitude: Double = 280.46646 + julianCenturies * (36000.76983 + 0.0003032 * julianCenturies)
            while (longitude > 360.0) longitude -= 360.0
            while (longitude < 0.0) longitude += 360.0
            return longitude // in degrees
        }

        /**
         * Returns the Geometric [Mean Anomaly](https://en.wikipedia.org/wiki/Mean_anomaly) of the Sun.
         *
         * @param julianCenturies
         * the number of Julian centuries since [J2000.0](https://en.wikipedia.org/wiki/Epoch_(astronomy)#J2000).
         * @return the Geometric Mean Anomaly of the Sun in degrees
         */
        private fun getSunGeometricMeanAnomaly(julianCenturies: Double): Double =
            357.52911 + julianCenturies * (35999.05029 - 0.0001537 * julianCenturies) // in degrees

        /**
         * Return the [eccentricity of earth's orbit](https://en.wikipedia.org/wiki/Eccentricity_%28orbit%29).
         *
         * @param julianCenturies
         * the number of Julian centuries since [J2000.0](https://en.wikipedia.org/wiki/Epoch_(astronomy)#J2000).
         * @return the unitless eccentricity
         */
        private fun getEarthOrbitEccentricity(julianCenturies: Double): Double =
            0.016708634 - julianCenturies * (0.000042037 + 0.0000001267 * julianCenturies) // unitless

        /**
         * Returns the [equation of center](https://en.wikipedia.org/wiki/Equation_of_the_center) for the sun.
         *
         * @param julianCenturies
         * the number of Julian centuries since [J2000.0](https://en.wikipedia.org/wiki/Epoch_(astronomy)#J2000).
         * @return the equation of center for the sun in degrees
         */
        private fun getSunEquationOfCenter(julianCenturies: Double): Double {
            val m: Double = getSunGeometricMeanAnomaly(julianCenturies)
            val mrad: Double = toRadians(m)
            val sinm: Double = sin(mrad)
            val sin2m: Double = sin(mrad + mrad)
            val sin3m: Double = sin(mrad + mrad + mrad)
            return (sinm * (1.914602 - julianCenturies * (0.004817 + 0.000014 * julianCenturies))) + ((sin2m
                    * (0.019993 - 0.000101 * julianCenturies))) + (sin3m * 0.000289) // in degrees
        }

        /**
         * Return the [true longitude](https://en.wikipedia.org/wiki/True_longitude) of the sun.
         *
         * @param julianCenturies
         * the number of Julian centuries since [J2000.0](https://en.wikipedia.org/wiki/Epoch_(astronomy)#J2000).
         * @return the sun's true longitude in degrees
         */
        private fun getSunTrueLongitude(julianCenturies: Double): Double {
            val sunLongitude: Double = getSunGeometricMeanLongitude(julianCenturies)
            val center: Double = getSunEquationOfCenter(julianCenturies)
            return sunLongitude + center // in degrees
        }
        // /**
        // * Returns the <a href="https://en.wikipedia.org/wiki/True_anomaly">true anamoly</a> of the sun.
        // *
        // * @param julianCenturies
        // * the number of Julian centuries since J2000.0
        // * @return the sun's true anamoly in degrees
        // */
        // private static double getSunTrueAnomaly(double julianCenturies) {
        // double meanAnomaly = getSunGeometricMeanAnomaly(julianCenturies);
        // double equationOfCenter = getSunEquationOfCenter(julianCenturies);
        //
        // return meanAnomaly + equationOfCenter; // in degrees
        // }
        /**
         * Return the [apparent longitude](https://en.wikipedia.org/wiki/Apparent_longitude) of the sun.
         *
         * @param julianCenturies
         * the number of Julian centuries since [J2000.0](https://en.wikipedia.org/wiki/Epoch_(astronomy)#J2000).
         * @return sun's apparent longitude in degrees
         */
        private fun getSunApparentLongitude(julianCenturies: Double): Double {
            val sunTrueLongitude: Double = getSunTrueLongitude(julianCenturies)
            val omega: Double = 125.04 - 1934.136 * julianCenturies
            val lambda: Double = sunTrueLongitude - 0.00569 - (0.00478 * sin(toRadians(omega)))
            return lambda // in degrees
        }

        /**
         * Returns the mean [obliquity of the ecliptic](https://en.wikipedia.org/wiki/Axial_tilt) (Axial tilt).
         *
         * @param julianCenturies
         * the number of Julian centuries since [J2000.0](https://en.wikipedia.org/wiki/Epoch_(astronomy)#J2000).
         * @return the mean obliquity in degrees
         */
        private fun getMeanObliquityOfEcliptic(julianCenturies: Double): Double {
            val seconds: Double =
                21.448 - julianCenturies * (46.8150 + julianCenturies * (0.00059 - julianCenturies * (0.001813)))
            return 23.0 + (26.0 + (seconds / 60.0)) / 60.0 // in degrees
        }

        /**
         * Returns the corrected [obliquity of the ecliptic](https://en.wikipedia.org/wiki/Axial_tilt) (Axial
         * tilt).
         *
         * @param julianCenturies
         * the number of Julian centuries since [J2000.0](https://en.wikipedia.org/wiki/Epoch_(astronomy)#J2000).
         * @return the corrected obliquity in degrees
         */
        private fun getObliquityCorrection(julianCenturies: Double): Double {
            val obliquityOfEcliptic: Double = getMeanObliquityOfEcliptic(julianCenturies)
            val omega: Double = 125.04 - 1934.136 * julianCenturies
            return obliquityOfEcliptic + 0.00256 * cos(toRadians(omega)) // in degrees
        }

        /**
         * Return the [declination](https://en.wikipedia.org/wiki/Declination) of the sun.
         *
         * @param julianCenturies
         * the number of Julian centuries since [J2000.0](https://en.wikipedia.org/wiki/Epoch_(astronomy)#J2000).
         * @return
         * the sun's declination in degrees
         */
        private fun getSunDeclination(julianCenturies: Double): Double {
            val obliquityCorrection: Double = getObliquityCorrection(julianCenturies)
            val lambda: Double = getSunApparentLongitude(julianCenturies)
            val sint: Double = sin(toRadians(obliquityCorrection)) * sin(toRadians(lambda))
            val theta: Double = toDegrees(asin(sint))
            return theta // in degrees
        }

        /**
         * Return the [Equation of Time](https://en.wikipedia.org/wiki/Equation_of_time) - the difference between
         * true solar time and mean solar time
         *
         * @param julianCenturies
         * the number of Julian centuries since [J2000.0](https://en.wikipedia.org/wiki/Epoch_(astronomy)#J2000).
         * @return equation of time in minutes of time
         */
        private fun getEquationOfTime(julianCenturies: Double): Double {
            val epsilon: Double = getObliquityCorrection(julianCenturies)
            val geomMeanLongSun: Double = getSunGeometricMeanLongitude(julianCenturies)
            val eccentricityEarthOrbit: Double = getEarthOrbitEccentricity(julianCenturies)
            val geomMeanAnomalySun: Double = getSunGeometricMeanAnomaly(julianCenturies)
            var y: Double = tan(toRadians(epsilon) / 2.0)
            y *= y
            val sin2l0: Double = sin(2.0 * toRadians(geomMeanLongSun))
            val sinm: Double = sin(toRadians(geomMeanAnomalySun))
            val cos2l0: Double = cos(2.0 * toRadians(geomMeanLongSun))
            val sin4l0: Double = sin(4.0 * toRadians(geomMeanLongSun))
            val sin2m: Double = sin(2.0 * toRadians(geomMeanAnomalySun))
            val equationOfTime: Double =
                (y * sin2l0 - 2.0 * eccentricityEarthOrbit * sinm + (4.0 * eccentricityEarthOrbit * y
                        * sinm * cos2l0)) - (0.5 * y * y * sin4l0) - (1.25 * eccentricityEarthOrbit * eccentricityEarthOrbit * sin2m)
            return toDegrees(equationOfTime) * 4.0 // in minutes of time
        }

        /**
         * Return the [hour angle](https://en.wikipedia.org/wiki/Hour_angle) of the sun in
         * [radians](https://en.wikipedia.org/wiki/Radian) at sunrise for the latitude.
         *
         * @param lat
         * the latitude of observer in degrees
         * @param solarDec
         * the declination angle of sun in degrees
         * @param zenith
         * the zenith
         * @return hour angle of sunrise in [radians](https://en.wikipedia.org/wiki/Radian)
         */
        private fun getSunHourAngleAtSunrise(lat: Double, solarDec: Double, zenith: Double): Double {
            val latRad: Double = toRadians(lat)
            val sdRad: Double = toRadians(solarDec)
            return acos(
                cos(toRadians(zenith)) / (cos(latRad) * cos(sdRad)) - tan(latRad)
                        * tan(sdRad)
            ) // in radians
        }

        /**
         * Returns the [hour angle](https://en.wikipedia.org/wiki/Hour_angle) of the sun in [radians](https://en.wikipedia.org/wiki/Radian)at sunset for the latitude.
         * @todo use - [sunHourAngleAtSunrise] implementation to avoid
         * duplication of code.
         *
         * @param lat
         * the latitude of observer in degrees
         * @param solarDec
         * the declination angle of sun in degrees
         * @param zenith
         * the zenith
         * @return the hour angle of sunset in [radians](https://en.wikipedia.org/wiki/Radian)
         */
        private fun getSunHourAngleAtSunset(lat: Double, solarDec: Double, zenith: Double): Double {
            val latRad: Double = toRadians(lat)
            val sdRad: Double = toRadians(solarDec)
            val hourAngle: Double = acos(
                cos(toRadians(zenith)) / (cos(latRad) * cos(sdRad))
                                        - tan(latRad) * tan(sdRad)
            )
            return -hourAngle // in radians
        }

        /**
         * Return the [Solar Elevation](https://en.wikipedia.org/wiki/Celestial_coordinate_system) for the
         * horizontal coordinate system at the given location at the given time. Can be negative if the sun is below the
         * horizon. Not corrected for altitude.
         *
         * @param cal
         * time of calculation
         * @param lat
         * latitude of location for calculation
         * @param lon
         * longitude of location for calculation
         * @return solar elevation in degrees - horizon is 0 degrees, civil twilight is -6 degrees
         */
        fun getSolarElevation(cal: LocalDateTime, lat: Double, lon: Double): Double {
            val (hourAngle_rad, lat_rad, dec_rad) = getHourAngleLatAndDec(cal, lat, lon)
            return toDegrees(
                asin(
                    sin(lat_rad) * sin(dec_rad)
                            + cos(lat_rad) * cos(dec_rad) * cos(hourAngle_rad)
                )
            )
        }

        /**
         * Return the [Solar Azimuth](https://en.wikipedia.org/wiki/Celestial_coordinate_system) for the
         * horizontal coordinate system at the given location at the given time. Not corrected for altitude. True south is 0
         * degrees.
         *
         * @param cal
         * time of calculation
         * @param lat
         * latitude of location for calculation
         * @param lon
         * longitude of location for calculation
         * @return the solar azimuth
         */
        fun getSolarAzimuth(cal: LocalDateTime, lat: Double, lon: Double): Double {
            val (hourAngle_rad, dec_rad, lat_rad) = getHourAngleLatAndDec(cal, lat, lon)
            return toDegrees(
                atan(
                    sin(hourAngle_rad)
                            / (
                            cos(hourAngle_rad) * sin(lat_rad) -
                                    tan(dec_rad) * cos(lat_rad)
                            )
                )
            ) + 180
        }

        fun getHourAngleLatAndDec(cal: LocalDateTime, lat: Double, lon: Double): Triple<Double, Double, Double> {
            val julianDay: Double = DateUtils.getJulianDay(cal.date)
            val julianCenturies: Double = getJulianCenturiesFromJulianDay(julianDay)
            val eot: Double = getEquationOfTime(julianCenturies)
            var longitude: Double = ((cal.hour + 12.0)
                    + (cal.minute + eot + (cal.second / 60.0)) / 60.0)
            longitude = -(longitude * 360.0 / 24.0) % 360.0
            val hourAngle_rad: Double = toRadians(lon - longitude)
            val declination: Double = getSunDeclination(julianCenturies)
            val dec_rad: Double = toRadians(declination)
            val lat_rad: Double = toRadians(lat)
            return Triple(hourAngle_rad, lat_rad, dec_rad)
        }

        /**
         * Return the [Universal Coordinated Time](https://en.wikipedia.org/wiki/Universal_Coordinated_Time) (UTC)
         * of sunrise for the given day at the given location on earth.
         *
         * @param julianDay
         * the Julian day
         * @param latitude
         * the latitude of observer in degrees
         * @param longitude
         * the longitude of observer in degrees
         * @param zenith
         * the zenith
         * @return the time in minutes from zero UTC
         */
        private fun getSunriseUTC(julianDay: Double, latitude: Double, longitude: Double, zenith: Double): Double {
            val julianCenturies: Double = getJulianCenturiesFromJulianDay(julianDay)

            // Find the time of solar noon at the location, and use that declination. This is better than start of the
            // Julian day
            val noonmin: Double = getSolarNoonUTC(julianCenturies, longitude)
            val tnoon: Double = getJulianCenturiesFromJulianDay(julianDay + noonmin / 1440.0)

            // First pass to approximate sunrise (using solar noon)
            var eqTime: Double = getEquationOfTime(tnoon)
            var solarDec: Double = getSunDeclination(tnoon)
            var hourAngle: Double = getSunHourAngleAtSunrise(latitude, solarDec, zenith)
            var delta: Double = longitude - toDegrees(hourAngle)
            var timeDiff: Double = 4 * delta // in minutes of time
            var timeUTC: Double = 720 + timeDiff - eqTime // in minutes

            // Second pass includes fractional Julian Day in gamma calc
            val newt: Double = getJulianCenturiesFromJulianDay(
                getJulianDayFromJulianCenturies(julianCenturies) + timeUTC
                        / 1440.0
            )
            eqTime = getEquationOfTime(newt)
            solarDec = getSunDeclination(newt)
            hourAngle = getSunHourAngleAtSunrise(latitude, solarDec, zenith)
            delta = longitude - toDegrees(hourAngle)
            timeDiff = 4 * delta
            timeUTC = 720 + timeDiff - eqTime // in minutes
            return timeUTC
        }

        /**
         * Return the [Universal Coordinated Time](https://en.wikipedia.org/wiki/Universal_Coordinated_Time) (UTC)
         * of of [solar noon](http://en.wikipedia.org/wiki/Noon#Solar_noon) for the given day at the given location
         * on earth.
         *
         * @param julianCenturies
         * the number of Julian centuries since [J2000.0](https://en.wikipedia.org/wiki/Epoch_(astronomy)#J2000).
         * @param longitude
         * the longitude of observer in degrees
         *
         * @return the time in minutes from zero UTC
         *
         * @see com.kosherjava.zmanim.util.AstronomicalCalculator.getUTCNoon
         * @see .getUTCNoon
         */
        private fun getSolarNoonUTC(julianCenturies: Double, longitude: Double): Double {
            // First pass uses approximate solar noon to calculate equation of time
            val tnoon: Double = getJulianCenturiesFromJulianDay(
                getJulianDayFromJulianCenturies(julianCenturies) + longitude
                        / 360.0
            )
            var eqTime: Double = getEquationOfTime(tnoon)
            val solNoonUTC: Double = 720 + (longitude * 4) - eqTime // min
            val newt: Double = getJulianCenturiesFromJulianDay(
                (getJulianDayFromJulianCenturies(julianCenturies) - 0.5
                        + solNoonUTC / 1440.0)
            )
            eqTime = getEquationOfTime(newt)
            return 720 + (longitude * 4) - eqTime // min
        }

        /**
         * Return the [Universal Coordinated Time](https://en.wikipedia.org/wiki/Universal_Coordinated_Time) (UTC)
         * of sunset for the given day at the given location on earth.
         *
         * @param julianDay
         * the Julian day
         * @param latitude
         * the latitude of observer in degrees
         * @param longitude
         * longitude of observer in degrees
         * @param zenith
         * zenith
         * @return the time in minutes from zero Universal Coordinated Time (UTC)
         */
        private fun getSunsetUTC(julianDay: Double, latitude: Double, longitude: Double, zenith: Double): Double {
            val julianCenturies: Double = getJulianCenturiesFromJulianDay(julianDay)

            // Find the time of solar noon at the location, and use that declination. This is better than start of the
            // Julian day
            val noonmin: Double = getSolarNoonUTC(julianCenturies, longitude)
            val tnoon: Double = getJulianCenturiesFromJulianDay(julianDay + noonmin / 1440.0)

            // First calculates sunrise and approx length of day
            var eqTime: Double = getEquationOfTime(tnoon)
            var solarDec: Double = getSunDeclination(tnoon)
            var hourAngle: Double = getSunHourAngleAtSunset(latitude, solarDec, zenith)
            var delta: Double = longitude - toDegrees(hourAngle)
            var timeDiff: Double = 4 * delta
            var timeUTC: Double = 720 + timeDiff - eqTime

            // Second pass includes fractional Julian Day in gamma calc
            val newt: Double = getJulianCenturiesFromJulianDay(
                getJulianDayFromJulianCenturies(julianCenturies) + timeUTC
                        / 1440.0
            )
            eqTime = getEquationOfTime(newt)
            solarDec = getSunDeclination(newt)
            hourAngle = getSunHourAngleAtSunset(latitude, solarDec, zenith)
            delta = longitude - toDegrees(hourAngle)
            timeDiff = 4 * delta
            timeUTC = 720 + timeDiff - eqTime // in minutes
            return timeUTC
        }
    }
}