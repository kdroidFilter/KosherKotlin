package io.github.kdroidfilter.kosherkotlin.util

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus
import kotlin.math.PI
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.sin

/**
 * The calculator of the עתים לבינה luach. On top of the [SpaCalculator]'s sun:
 *
 * - The horizon of sunrise and sunset ([horizonZenith]) is the sun's semidiameter plus a refraction traced through a
 *   model atmosphere and corrected by the place's [climate] (see [HorizonRefraction]), for the month, morning or
 *   evening, at the observer's elevation; seen from an elevation the dip of the horizon is added. The fixed
 *   [refraction] and [solarRadius] are not used.
 * - Solar midnight ([getUTCMidnight]) is noon − 12 h minus whole seconds, found by stepping from noon − 12 h past the
 *   lower transit.
 *
 * Its degree-based zmanim ("72 במעלות"…) are measured from that horizon: see
 * [AstronomicalCalendar.getSunriseOffsetByDegreesBelowHorizon][io.github.kdroidfilter.kosherkotlin.AstronomicalCalendar.getSunriseOffsetByDegreesBelowHorizon]
 * and the `ItimLabina` zmanim of [ComplexZmanimCalendar][io.github.kdroidfilter.kosherkotlin.ComplexZmanimCalendar].
 */
class ItimLabinaCalculator(
    /** Monthly temperatures of the place; refracted times depend on them by a few seconds. */
    val climate: ItimLabinaClimate = ItimLabinaClimate.STANDARD,
) : SpaCalculator() {

    override val calculatorName: String get() = "עתים לבינה (Itim Labina)"

    override fun copy(): AstronomicalCalculator = ItimLabinaCalculator(climate).also { copyTo(it) }

    override fun horizonZenith(date: LocalDate, geoLocation: GeoLocation, isSunrise: Boolean, adjustForElevation: Boolean): Double {
        val month = date.month.ordinal
        val semidiameter = SolarPosition.sun(SolarDay(date, geoLocation.longitude).noon).semidiameter
        val refraction = HorizonRefraction.degrees(
            geoLocation.latitude, geoLocation.longitude, climate, month, isSunrise, geoLocation.elevation,
        )
        val dip = if (adjustForElevation) HorizonRefraction.dip(geoLocation.latitude, climate, month, geoLocation.elevation) else 0.0
        return 90 + semidiameter + refraction + dip
    }

    override fun getUTCMidnight(date: LocalDate, geoLocation: GeoLocation): Double {
        val tomorrow = date.plus(1, DateTimeUnit.DAY)
        val noon = SolarDay(tomorrow, geoLocation.longitude).noon
        val lowerTransit = SolarDay(date, geoLocation.longitude).midnightAfter
        val lead = (noon - lowerTransit) * 86_400 - 43_200
        val w = 2 * PI * (tomorrow.dayOfYear - 1) / 365.25
        val lagged = lead + MIDNIGHT_LAG[0] + MIDNIGHT_LAG[1] * cos(w) + MIDNIGHT_LAG[2] * sin(w) +
            MIDNIGHT_LAG[3] * cos(2 * w) + MIDNIGHT_LAG[4] * sin(2 * w)
        val seconds = if (lagged > 0) ceil(lagged) else floor(lagged)
        return utcHours(date, noon - 0.5 - seconds / 86_400)
    }

    private companion object {
        /** The lower transit is taken this early (seconds): constant, cos, sin, cos 2, sin 2 of the day of the year. */
        val MIDNIGHT_LAG = doubleArrayOf(0.1963, 0.0318, 0.0009, 0.0180, -0.0073)
    }
}
