package io.github.kdroidfilter.kosherkotlin.util

import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn

/**
 * A calculator after the NREL Solar Position Algorithm (Reda & Andreas, 2004): VSOP87 Earth terms, IAU 1980 nutation
 * and aberration, geocentric sun. About 0.0003° on the sun's position, well under a second of sunrise time, where
 * the [NOAACalculator] drifts by up to 0.01° (3–4 s). Times are solved for, not interpolated: the sun's altitude is
 * bisected between the lower transit and solar noon.
 *
 * Sunrise and sunset (a zenith of exactly 90°) use [horizonZenith]; any other zenith is geometric, as elsewhere in
 * KosherKotlin.
 */
open class SpaCalculator : AstronomicalCalculator() {

    override val calculatorName: String get() = "NREL Solar Position Algorithm"

    override fun copy(): AstronomicalCalculator = SpaCalculator().also { copyTo(it) }

    protected fun copyTo(other: AstronomicalCalculator) {
        other.refraction = refraction
        other.solarRadius = solarRadius
        other.earthRadius = earthRadius
    }

    override fun getUTCSunrise(LocalDate: LocalDate, geoLocation: GeoLocation, zenith: Double, adjustForElevation: Boolean): Double =
        event(LocalDate, geoLocation, zenith, adjustForElevation, isSunrise = true)

    override fun getUTCSunset(LocalDate: LocalDate, geoLocation: GeoLocation, zenith: Double, adjustForElevation: Boolean): Double =
        event(LocalDate, geoLocation, zenith, adjustForElevation, isSunrise = false)

    override fun getUTCNoon(date: LocalDate, geoLocation: GeoLocation): Double =
        utcHours(date, SolarDay(date, geoLocation.longitude).noon)

    private fun event(date: LocalDate, geoLocation: GeoLocation, zenith: Double, adjustForElevation: Boolean, isSunrise: Boolean): Double {
        val target = if (zenith == 90.0) horizonZenith(date, geoLocation, isSunrise, adjustForElevation) else zenith
        val jd = SolarDay(date, geoLocation.longitude).crossing(geoLocation.latitude, 90 - target, isSunrise)
        return if (jd.isNaN()) Double.NaN else utcHours(date, jd)
    }

    /** Hours after 0:00 UTC of [date], in [0, 24), as the calendars expect. */
    protected fun utcHours(date: LocalDate, julianDay: Double): Double {
        val midnight = SolarPosition.julianDay(date.atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds())
        return (((julianDay - midnight) * 24) % 24 + 24) % 24
    }

    /** Solar noon and the neighbouring lower transits of [date] at [longitude], as Julian days. */
    internal class SolarDay(date: LocalDate, private val longitude: Double) {

        /** Midday of [date] in local mean solar time: anchors the day whatever the time zone. */
        private val anchor = SolarPosition.julianDay(date.atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds()) + 0.5 - longitude / 360

        val noon: Double by lazy { hourAngleCrossing(anchor, 0.0) }
        val midnightBefore: Double by lazy { hourAngleCrossing(noon - 0.5, 180.0) }
        val midnightAfter: Double by lazy { hourAngleCrossing(noon + 0.5, 180.0) }

        /**
         * When the sun's geocentric altitude crosses [altitude], rising between the previous lower transit and noon or
         * setting between noon and the next one; NaN when it never gets there.
         */
        fun crossing(latitude: Double, altitude: Double, rising: Boolean): Double {
            fun f(jd: Double) = SolarPosition.sun(jd).altitude(latitude, longitude) - altitude
            var low = if (rising) midnightBefore else noon
            var high = if (rising) noon else midnightAfter
            val fLow = f(low)
            if (fLow * f(high) > 0) return Double.NaN
            val increasing = fLow < 0
            // ponytail: plain bisection, ~40 ephemeris evaluations per zman; switch to secant if it ever shows up in a profile.
            repeat(40) {
                val mid = (low + high) / 2
                if ((f(mid) < 0) == increasing) low = mid else high = mid
            }
            return (low + high) / 2
        }

        /** Refines the moment the local hour angle equals [target] (0: noon, 180: midnight), starting near [guess]. */
        private fun hourAngleCrossing(guess: Double, target: Double): Double {
            var jd = guess
            repeat(4) {
                val h = SolarPosition.sun(jd).hourAngle(longitude)
                jd -= (((h - target) % 360 + 540) % 360 - 180) / 360
            }
            return jd
        }
    }
}
