/*
 * Zmanim Java API
 * Copyright (C) 2004-2020 Eliyahu Hershfeld
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
 * or connect to: http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 */
package com.kosherjava.zmanim.util

import com.kosherjava.zmanim.util.AstronomicalCalculator.Companion.toDegrees
import com.kosherjava.zmanim.util.AstronomicalCalculator.Companion.toRadians
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.log
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.tan

/**
 * A class for various location calculations
 * Most of the code in this class is ported from [Chris Veness'](http://www.movable-type.co.uk/)
 * [LGPL](http://www.fsf.org/licensing/licenses/lgpl.html) Javascript Implementation
 *
 * @author  Eliyahu Hershfeld 2009 - 2020
 */
@Deprecated("All methods in this call are available in the {@link GeoLocation} class, and this class that duplicates that\n" + "  code will be removed in release 3.0.")
object GeoLocationUtils {
    /**
     * Constant for a distance type calculation.
     * @see .getGeodesicDistance
     */
    private val DISTANCE: Int = 0

    /**
     * Constant for a initial bearing type calculation.
     * @see .getGeodesicInitialBearing
     */
    private val INITIAL_BEARING: Int = 1

    /**
     * Constant for a final bearing type calculation.
     * @see .getGeodesicFinalBearing
     */
    private val FINAL_BEARING: Int = 2

    /**
     * Calculate the [geodesic](http://en.wikipedia.org/wiki/Great_circle) initial bearing between this Object and
     * a second Object passed to this method using [Thaddeus
 * Vincenty's](http://en.wikipedia.org/wiki/Thaddeus_Vincenty) inverse formula See T Vincenty, "[Direct and
 * Inverse Solutions of Geodesics on the Ellipsoid with application of nested equations](http://www.ngs.noaa.gov/PUBS_LIB/inverse.pdf)", Survey Review, vol XXII
     * no 176, 1975.
     *
     * @param location
     * the initial location
     * @param destination
     * the destination location
     * @return the geodesic bearing
     */
    fun getGeodesicInitialBearing(location: GeoLocation, destination: GeoLocation): Double {
        return vincentyFormula(location, destination, INITIAL_BEARING)
    }

    /**
     * Calculate the [geodesic](http://en.wikipedia.org/wiki/Great_circle) final bearing between this Object
     * and a second Object passed to this method using [Thaddeus Vincenty's](http://en.wikipedia.org/wiki/Thaddeus_Vincenty)
     * inverse formula See T Vincenty, "[Direct and Inverse Solutions of Geodesics
 * on the Ellipsoid with application of nested equations](http://www.ngs.noaa.gov/PUBS_LIB/inverse.pdf)", Survey Review, vol XXII no 176, 1975.
     *
     * @param location
     * the initial location
     * @param destination
     * the destination location
     * @return the geodesic bearing
     */
    fun getGeodesicFinalBearing(location: GeoLocation, destination: GeoLocation): Double {
        return vincentyFormula(location, destination, FINAL_BEARING)
    }

    /**
     * Calculate [geodesic distance](http://en.wikipedia.org/wiki/Great-circle_distance) in Meters
     * between this Object and a second Object passed to this method using [Thaddeus Vincenty's](http://en.wikipedia.org/wiki/Thaddeus_Vincenty) inverse formula See T Vincenty,
     * "[Direct and Inverse Solutions of Geodesics on the
 * Ellipsoid with application of nested equations](http://www.ngs.noaa.gov/PUBS_LIB/inverse.pdf)", Survey Review, vol XXII no 176, 1975. This uses the
     * WGS-84 geodetic model.
     *
     * @param location
     * the initial location
     * @param destination
     * the destination location
     * @return the geodesic distance in Meters
     */
    fun getGeodesicDistance(location: GeoLocation, destination: GeoLocation): Double {
        return vincentyFormula(location, destination, DISTANCE)
    }

    /**
     * TODO this is duplicated with GeoLocation. This has a better javadoc. DRY up?
     * Calculates the initial [geodesic](http://en.wikipedia.org/wiki/Great_circle) bearing, final bearing or
     * [geodesic distance](http://en.wikipedia.org/wiki/Great-circle_distance) using [Thaddeus Vincenty's](http://en.wikipedia.org/wiki/Thaddeus_Vincenty) inverse formula See T Vincenty, "[Direct and Inverse Solutions of Geodesics on the Ellipsoid
 * with application of nested equations](http://www.ngs.noaa.gov/PUBS_LIB/inverse.pdf)", Survey Review, vol XXII no 176, 1975.
     *
     * @param location
     * the initial location
     * @param destination
     * the destination location
     * @param formula
     * This formula calculates initial bearing ([.INITIAL_BEARING]),
     * final bearing ([.FINAL_BEARING]) and distance ([.DISTANCE]).
     * @return
     * the geodesic distance, initial or final bearing (based on the formula passed in) between the location
     * and destination in Meters
     * @see .getGeodesicDistance
     * @see .getGeodesicInitialBearing
     * @see .getGeodesicFinalBearing
     */
    private fun vincentyFormula(location: GeoLocation, destination: GeoLocation, formula: Int): Double {
        val a: Double =
            6378137.0 // length of semi-major axis of the ellipsoid (radius at equator) in metres based on WGS-84
        val b: Double =
            6356752.3142 // length of semi-minor axis of the ellipsoid (radius at the poles) in meters based on WGS-84
        val f: Double = 1 / 298.257223563 // flattening of the ellipsoid based on WGS-84
        val L: Double =
            toRadians(destination.longitude - location.longitude) //difference in longitude of two points;
        val U1: Double =
            atan((1 - f) * tan(toRadians(location.latitude))) // reduced latitude (latitude on the auxiliary sphere)
        val U2: Double =
            atan((1 - f) * tan(toRadians(destination.latitude))) // reduced latitude (latitude on the auxiliary sphere)
        val sinU1: Double = sin(U1)
        val cosU1: Double = cos(U1)
        val sinU2: Double = sin(U2)
        val cosU2: Double = cos(U2)
        var lambda: Double = L
        var lambdaP: Double = 2 * PI
        var iterLimit: Double = 20.0
        var sinLambda: Double = 0.0
        var cosLambda: Double = 0.0
        var sinSigma: Double = 0.0
        var cosSigma: Double = 0.0
        var sigma: Double = 0.0
        var sinAlpha: Double = 0.0
        var cosSqAlpha: Double = 0.0
        var cos2SigmaM: Double = 0.0
        var C: Double
        while (abs(lambda - lambdaP) > 1e-12 && --iterLimit > 0) {
            sinLambda = sin(lambda)
            cosLambda = cos(lambda)
            sinSigma = sqrt(
                ((cosU2 * sinLambda) * (cosU2 * sinLambda)
                        + (cosU1 * sinU2 - sinU1 * cosU2 * cosLambda)
                        * (cosU1 * sinU2 - sinU1 * cosU2 * cosLambda))
            )
            if (sinSigma == 0.0) return 0.0 // co-incident points
            cosSigma = sinU1 * sinU2 + cosU1 * cosU2 * cosLambda
            sigma = atan2(sinSigma, cosSigma)
            sinAlpha = cosU1 * cosU2 * sinLambda / sinSigma
            cosSqAlpha = 1 - sinAlpha * sinAlpha
            cos2SigmaM = cosSigma - 2 * sinU1 * sinU2 / cosSqAlpha
            if (cos2SigmaM.isNaN()) cos2SigmaM = 0.0 // equatorial line: cosSqAlpha=0 (§6)
            C = (f / 16) * cosSqAlpha * (4 + f * (4 - 3 * cosSqAlpha))
            lambdaP = lambda
            lambda = (L
                    + ((1 - C)
                    * f
                    * sinAlpha
                    * (sigma + (C
                    * sinSigma
                    * (cos2SigmaM + (C * cosSigma
                    * (-1 + 2 * cos2SigmaM * cos2SigmaM)))))))
        }
        if (iterLimit == 0.0) return Double.NaN // formula failed to converge
        val uSq: Double = cosSqAlpha * (a * a - b * b) / (b * b)
        val A: Double = 1 + uSq / 16384 * (4096 + uSq * (-768 + uSq * (320 - 175 * uSq)))
        val B: Double = uSq / 1024 * (256 + uSq * (-128 + uSq * (74 - 47 * uSq)))
        val deltaSigma: Double = (B
                * sinSigma
                * (cos2SigmaM + B
                / 4
                * (cosSigma * (-1 + 2 * cos2SigmaM * cos2SigmaM) - (((B
                / 6)) * cos2SigmaM
                * (-3 + 4 * sinSigma * sinSigma)
                * (-3 + 4 * cos2SigmaM * cos2SigmaM)))))
        val distance: Double = b * A * (sigma - deltaSigma)

        // initial bearing
        val fwdAz: Double = toDegrees(
            atan2(
                cosU2 * sinLambda, cosU1
                        * sinU2 - sinU1 * cosU2 * cosLambda
            )
        )
        // final bearing
        val revAz: Double = toDegrees(
            atan2(
                cosU1 * sinLambda, -sinU1
                        * cosU2 + cosU1 * sinU2 * cosLambda
            )
        )
        if (formula == DISTANCE) {
            return distance
        } else if (formula == INITIAL_BEARING) {
            return fwdAz
        } else if (formula == FINAL_BEARING) {
            return revAz
        } else { // should never happen
            return Double.NaN
        }
    }

    /**
     * Returns the [rhumb line](http://en.wikipedia.org/wiki/Rhumb_line)
     * bearing from the current location to the GeoLocation passed in.
     *
     * @param location
     * the initial location
     * @param destination
     * the destination location
     * @return the bearing in degrees
     */
    fun getRhumbLineBearing(location: GeoLocation, destination: GeoLocation): Double {
        var dLon: Double = toRadians(destination.longitude - location.longitude)
        val dPhi: Double = log(
            (tan(
                toRadians(destination.latitude)
                        / 2 + PI / 4
            )
                    / tan(toRadians(location.latitude) / 2 + PI / 4)),
            kotlin.math.E
        )
        if (abs(dLon) > PI) dLon = if (dLon > 0) -(2 * PI - dLon) else (2 * PI + dLon)
        return toDegrees(atan2(dLon, dPhi))
    }

    /**
     * Returns the [rhumb line](http://en.wikipedia.org/wiki/Rhumb_line) distance between two GeoLocations
     * passed in. Ported from [Chris Veness'](http://www.movable-type.co.uk/) Javascript Implementation.
     *
     * @param location
     * the initial location
     * @param destination
     * the destination location
     * @return the distance in Meters
     */
    fun getRhumbLineDistance(location: GeoLocation, destination: GeoLocation): Double {
        val earthRadius: Double = 6378137.0 // Earth's radius in meters (WGS-84)
        val dLat: Double = toRadians(location.latitude) - toRadians(destination.latitude)
        var dLon: Double =
            abs(toRadians(location.longitude) - toRadians(destination.longitude))
        val dPhi: Double = log(
            (tan(toRadians(location.latitude) / 2 + PI / 4)
                    / tan(toRadians(destination.latitude) / 2 + PI / 4)),
            kotlin.math.E
        )
        var q: Double = dLat / dPhi
        if (!(abs(q) <= Double.MAX_VALUE)) {
            q = cos(toRadians(destination.latitude))
        }
        // if dLon over 180° take shorter rhumb across 180° meridian:
        if (dLon > PI) {
            dLon = 2 * PI - dLon
        }
        val d: Double = sqrt(dLat * dLat + q * q * dLon * dLon)
        return d * earthRadius
    }
}