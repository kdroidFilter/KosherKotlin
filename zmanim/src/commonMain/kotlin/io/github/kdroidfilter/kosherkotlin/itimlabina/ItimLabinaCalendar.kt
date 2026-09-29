package io.github.kdroidfilter.kosherkotlin.itimlabina

import io.github.kdroidfilter.kosherkotlin.util.GeoLocation
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.sin
import kotlin.math.PI
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant

/**
 * Zmanim following the method of the עתים לבינה luach.
 *
 * - Every angle is the **geocentric** altitude of the sun's **center** (no parallax), from [SolarPosition].
 * - [Refraction.NONE]: the angle is used as is (e.g. 6.45° tzeis, 11.5° misheyakir).
 * - [Refraction.STANDARD]: 50' below the angle.
 * - [Refraction.GLOYBERMAN]: the angle is measured from the refracted sea horizon of the day: semidiameter plus the
 *   horizon refraction of [HorizonRefraction], driven by [climate]. Sunrise and sunset "במישור", and most
 *   degree-based alos/tzeis (72 or 90 "במעלות", 18 minutes "במעלות"...), use it.
 * - From elevation ("מהגובה"): the horizon dip of [HorizonRefraction]. Eye height is ignored.
 * - Chatzos halayla: noon − 12 h minus whole seconds.
 * - When the sun never reaches the angle, the time falls back to solar midnight.
 *
 * Not covered: the visible sunrise and sunset (הנץ/שקיעה הנראית), which need terrain data.
 */
class ItimLabinaCalendar(
    val geoLocation: GeoLocation,
    val date: LocalDate,
    /** Monthly temperatures of the place; the refracted times depend on them by a few seconds. */
    val climate: ItimLabinaClimate = ItimLabinaClimate.STANDARD,
) {

    enum class Refraction { NONE, STANDARD, GLOYBERMAN }

    private val latitude = geoLocation.latitude
    private val longitude = geoLocation.longitude
    private val elevation = geoLocation.elevation

    /** Midday of [date] in local mean solar time: anchors the whole day whatever the time zone. */
    private val anchor: Double = SolarPosition.julianDay(date.atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds()) +
        0.5 - longitude / 360

    private val transitJd: Double by lazy { hourAngleCrossing(anchor, 0.0) }
    private val midnightBeforeJd: Double by lazy { hourAngleCrossing(transitJd - 0.5, 180.0) }
    private val midnightAfterJd: Double by lazy { hourAngleCrossing(transitJd + 0.5, 180.0) }

    /** True solar noon, when the sun crosses the meridian (חצות היום). */
    val chatzos: Instant by lazy { transitJd.toInstant() }

    /** Solar midnight of the night before [date] (חצות הלילה). */
    val chatzosLayla: Instant by lazy { midnight(transitJd, midnightBeforeJd) }

    /** The midnight after [date]'s noon, i.e. the next day's [chatzosLayla]. */
    private val chatzosLaylaAfter: Instant by lazy { midnight(hourAngleCrossing(transitJd + 1, 0.0), midnightAfterJd) }

    /**
     * Midnight steps whole seconds from noon − 12 h until it passes the lower transit, so it keeps noon's
     * milliseconds and overshoots by up to a second; the lower transit is taken [MIDNIGHT_LAG] early.
     */
    private fun midnight(noonJd: Double, lowerTransitJd: Double): Instant {
        val lead = (noonJd - lowerTransitJd) * 86_400 - 43_200
        val w = 2 * PI * (date.dayOfYear - 1) / 365.25
        val lagged = lead + MIDNIGHT_LAG[0] + MIDNIGHT_LAG[1] * cos(w) + MIDNIGHT_LAG[2] * sin(w) +
            MIDNIGHT_LAG[3] * cos(2 * w) + MIDNIGHT_LAG[4] * sin(2 * w)
        val seconds = if (lagged > 0) ceil(lagged) else floor(lagged)
        return noonJd.toInstant() - 12.hours - seconds.seconds
    }

    // Sunrise and sunset

    /** הנץ החמה במישור. */
    val sunriseMishor: Instant by lazy { sunrise() }

    /** שקיעת החמה במישור. */
    val sunsetMishor: Instant by lazy { sunset() }

    /** הנץ החמה מהגובה, seen from [GeoLocation.elevation] over a flat horizon. */
    val sunriseFromElevation: Instant by lazy { sunrise(fromElevation = true) }

    /** שקיעת החמה מהגובה, seen from [GeoLocation.elevation] over a flat horizon. */
    val sunsetFromElevation: Instant by lazy { sunset(fromElevation = true) }

    /**
     * The moment the sun is [degrees] above (negative: below) the horizon in the morning, the horizon being defined
     * by [refraction]. [fromElevation] lowers the horizon by the dip seen from [GeoLocation.elevation]; it only
     * applies with [Refraction.GLOYBERMAN].
     */
    fun sunrise(
        degrees: Double = 0.0,
        refraction: Refraction = Refraction.GLOYBERMAN,
        fromElevation: Boolean = false,
    ): Instant = crossing(targetAltitude(degrees, refraction, fromElevation, morning = true), morning = true)

    /** Evening counterpart of [sunrise]. */
    fun sunset(
        degrees: Double = 0.0,
        refraction: Refraction = Refraction.GLOYBERMAN,
        fromElevation: Boolean = false,
    ): Instant = crossing(targetAltitude(degrees, refraction, fromElevation, morning = false), morning = false)

    // Shaos zmaniyos

    /** A twelfth of the time between [start] and [end]. */
    fun shaahZmanis(start: Instant, end: Instant): Duration = (end - start) / 12

    /** [hours] shaos zmaniyos after [start], the day running from [start] to [end]. */
    fun zmanis(start: Instant, end: Instant, hours: Double): Instant = start + shaahZmanis(start, end) * hours

    private fun gra(hours: Double) = zmanis(sunriseMishor, sunsetMishor, hours)

    /** שעה זמנית גר"א, from sunrise to sunset במישור. */
    val shaahZmanisGra: Duration by lazy { shaahZmanis(sunriseMishor, sunsetMishor) }
    val sofZmanShmaGra: Instant by lazy { gra(3.0) }
    val sofZmanTfilaGra: Instant by lazy { gra(4.0) }
    val sofZmanAchilasChametzGra: Instant by lazy { gra(4.0) }
    val sofZmanBiurChametzGra: Instant by lazy { gra(5.0) }
    val sofZmanMusafGra: Instant by lazy { gra(7.0) }
    val sofZmanSeudaGra: Instant by lazy { gra(9.0) }

    /** The later of half an hour after chatzos and 6.5 shaos zmaniyos. */
    val minchaGedola: Instant by lazy { maxOf(chatzos + 30.minutes, gra(6.5)) }
    val minchaKetana: Instant by lazy { gra(9.5) }
    val plagHamincha: Instant by lazy { gra(10.75) }

    // Dawn and nightfall: "72 במעלות" is 15.2193° below the refracted horizon, etc.

    val alos72Degrees: Instant by lazy { sunrise(-15.2193) }
    val alos90Degrees: Instant by lazy { sunrise(-18.9712) }
    val alos120Degrees: Instant by lazy { sunrise(-25.1347) }

    /** 18° below the geometric horizon, per the astronomers (ראב"ע). */
    val alos18Degrees: Instant by lazy { sunrise(-18.0, Refraction.NONE) }
    val misheyakir11Point5Degrees: Instant by lazy { sunrise(-11.5, Refraction.NONE) }
    val misheyakir10Point5Degrees: Instant by lazy { sunrise(-10.5, Refraction.NONE) }

    /** בין השמשות ליראים: 13.5 minutes before sunset, as an angle. */
    val beinHashmashosYereim: Instant by lazy { sunset(2.86674) }

    /** Tzeis of the Geonim: 18 minutes after sunset, as an angle. */
    val tzeisGeonim18Minutes: Instant by lazy { sunset(-3.8217) }
    val tzeis13Point5Minutes: Instant by lazy { sunset(-2.86674) }
    val tzeisRambam: Instant by lazy { sunset(-4.24604) }
    val tzeis6Point45Degrees: Instant by lazy { sunset(-6.45, Refraction.NONE) }
    val tzeisChazonIsh: Instant by lazy { sunset(-9.28, Refraction.NONE) }

    /** 20 fixed minutes after sunset במישור. */
    val tzeis20Minutes: Instant by lazy { sunsetMishor + 20.minutes }
    val tzeis72Degrees: Instant by lazy { sunset(-15.2193) }
    val tzeis90Degrees: Instant by lazy { sunset(-18.9712) }
    val tzeis120Degrees: Instant by lazy { sunset(-25.1347) }

    /** שעה זמנית מג"א, from alos to tzeis 90 במעלות. */
    val shaahZmanisMga90Degrees: Duration by lazy { shaahZmanis(alos90Degrees, tzeis90Degrees) }

    // Solver

    private fun targetAltitude(degrees: Double, refraction: Refraction, fromElevation: Boolean, morning: Boolean) =
        when (refraction) {
            Refraction.NONE -> degrees
            Refraction.STANDARD -> degrees - STANDARD_REFRACTION
            Refraction.GLOYBERMAN -> {
                val month = date.month.ordinal
                val horizon = SolarPosition.sun(transitJd).semidiameter +
                    HorizonRefraction.degrees(latitude, longitude, climate, month, morning, elevation)
                val dip = if (fromElevation) HorizonRefraction.dip(latitude, climate, month, elevation) else 0.0
                degrees - horizon - dip
            }
        }

    /**
     * When the sun's altitude crosses [target], rising between the previous midnight and noon or setting between
     * noon and the next midnight. Falls back to that midnight when the sun never gets there.
     */
    private fun crossing(target: Double, morning: Boolean): Instant {
        val midnight = if (morning) midnightBeforeJd else midnightAfterJd
        val fallback = if (morning) chatzosLayla else chatzosLaylaAfter
        fun f(jd: Double) = SolarPosition.sun(jd).altitude(latitude, longitude) - target
        // f rises from midnight to noon in the morning, falls from noon to midnight in the evening.
        var low = if (morning) midnight else transitJd
        var high = if (morning) transitJd else midnight
        val fLow = f(low)
        val fHigh = f(high)
        if (fLow * fHigh > 0) return fallback
        val rising = fLow < 0
        // ponytail: plain bisection, ~40 ephemeris evaluations per zman; switch to secant if it ever shows up in a profile.
        repeat(40) {
            val mid = (low + high) / 2
            if ((f(mid) < 0) == rising) low = mid else high = mid
        }
        return ((low + high) / 2).toInstant()
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

    private fun Double.toInstant(): Instant = Instant.fromEpochMilliseconds(SolarPosition.epochMillis(this))

    private companion object {
        const val STANDARD_REFRACTION = 50.0 / 60

        /** Seconds, as constant, cos, sin, cos 2, sin 2 of the day of the year. */
        val MIDNIGHT_LAG = doubleArrayOf(0.1963, 0.0318, 0.0009, 0.0180, -0.0073)
    }
}
