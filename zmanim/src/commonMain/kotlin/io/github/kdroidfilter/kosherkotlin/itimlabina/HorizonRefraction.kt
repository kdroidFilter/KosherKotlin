package io.github.kdroidfilter.kosherkotlin.itimlabina

import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.ln
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Monthly climatology of a place (°C, January first), which drives the refraction of the horizon: minimum and
 * maximum temperatures for sunrise and sunset, mean for the dip seen from an elevation, measured at [elevation]
 * meters. WorldClim 2.1 normals on a 2.5' grid, with the grid's elevation, are a good source. [STANDARD] is the
 * 15 °C standard atmosphere at sea level, every month.
 */
class ItimLabinaClimate(
    val minimum: DoubleArray,
    val maximum: DoubleArray,
    val mean: DoubleArray,
    val elevation: Double = 0.0,
) {
    init {
        require(minimum.size == 12 && maximum.size == 12 && mean.size == 12) { "One value per month" }
    }

    companion object {
        val STANDARD = ItimLabinaClimate(DoubleArray(12) { 15.0 }, DoubleArray(12) { 15.0 }, DoubleArray(12) { 15.0 })
    }
}

/**
 * Refraction of the horizon ("gloyberman"): the horizontal ray is traced through a model atmosphere (Dr. Menat's
 * winter or summer atmosphere in Israel, the AFGL/LOWTRAN ones elsewhere) from the observer's height, floored to
 * 100 m, then scaled by the model's temperature over the place's, both at the observer's height:
 *
 *     R = k · R_model(h) · T_model(h) / (T + L · (h_climate − h))
 *
 * T being the month's minimum temperature at sunrise, and at sunset the minimum plus 59 % of the daily range.
 *
 * The dip of the horizon seen from an elevation is the geometric dip over the WGS84 geocentric radius, deepened by
 * C(h) / T², T being the month's mean temperature rounded to the degree.
 */
internal object HorizonRefraction {

    /** Surface correction: [lapse] in K/m, k morning and evening, and the share of the daily range at sunset. */
    private class Correction(val lapse: Double, val morningScale: Double, val eveningScale: Double, val eveningShare: Double)

    private val ISRAEL = Correction(0.00367, 0.98878, 0.99227, 0.5927)
    private val ELSEWHERE = Correction(0.0063, 0.99141, 0.99491, 0.5927)

    /** The dip's refraction numerator C(h), in K², sampled over ln h; it drops by 3 % above 1000 m. */
    private val DIP_LOW_H = doubleArrayOf(50.0, 100.0, 200.0, 300.0, 500.0, 700.0, 800.0, 900.0, 1000.0)
    private val DIP_LOW_C = doubleArrayOf(
        6993.222, 6985.416, 6977.447, 6971.939, 6965.847, 6961.634, 6960.139, 6958.717, 6957.381,
    )
    private val DIP_HIGH_H = doubleArrayOf(1100.0, 1200.0, 1300.0, 1400.0, 1500.0, 1600.0, 2000.0, 2500.0, 3000.0, 3500.0, 4000.0)
    private val DIP_HIGH_C = doubleArrayOf(
        6743.439, 6740.706, 6737.905, 6735.475, 6733.212, 6730.913, 6723.462, 6716.106, 6709.922, 6704.669, 6700.149,
    )

    /** Horizon refraction in degrees, without the semidiameter. [month] is 0-based. */
    fun degrees(
        latitude: Double,
        longitude: Double,
        climate: ItimLabinaClimate,
        month: Int,
        morning: Boolean,
        elevation: Double,
    ): Double {
        val israel = isInIsrael(latitude, longitude)
        val atmosphere = atmosphere(latitude, israel, isWinter(latitude, month))
        val correction = if (israel) ISRAEL else ELSEWHERE
        val min = climate.minimum[month]
        val celsius = if (morning) min else min + correction.eveningShare * (climate.maximum[month] - min)
        val observed = celsius + 273.15 + correction.lapse * (climate.elevation - elevation)
        val scale = if (morning) correction.morningScale else correction.eveningScale
        return scale * atmosphere.refraction(elevation) * atmosphere.temperature(floor(elevation / 100) * 100) / observed
    }

    /** Dip of the horizon seen from [elevation], in degrees. */
    fun dip(latitude: Double, climate: ItimLabinaClimate, month: Int, elevation: Double): Double {
        if (elevation <= 0) return 0.0
        val radius = geocentricRadius(latitude)
        val geometric = acos(radius / (radius + elevation)) * 180 / PI
        val kelvin = floor(climate.mean[month] + 0.5) + 273.15
        return geometric * (1 + dipNumerator(elevation) / (kelvin * kelvin))
    }

    private fun atmosphere(latitude: Double, israel: Boolean, winter: Boolean): Atmosphere = when {
        israel -> if (winter) AtmosphereModels.MENAT_WINTER else AtmosphereModels.MENAT_SUMMER
        abs(latitude) < 15 -> AtmosphereModels.TROPICAL
        abs(latitude) >= 60 -> if (winter) AtmosphereModels.SUBARCTIC_WINTER else AtmosphereModels.SUBARCTIC_SUMMER
        else -> if (winter) AtmosphereModels.MIDLATITUDE_WINTER else AtmosphereModels.MIDLATITUDE_SUMMER
    }

    // ponytail: winter by month; the luach picks the model closest to the place's weather, a few places differ.
    /** November to April north of the equator, May to October south of it. */
    private fun isWinter(latitude: Double, month: Int): Boolean {
        val northern = if (latitude >= 0) month else (month + 6) % 12
        return northern >= 10 || northern <= 3
    }

    private fun dipNumerator(elevation: Double): Double =
        if (elevation <= 1000) interpolateLn(DIP_LOW_H, DIP_LOW_C, elevation)
        else interpolateLn(DIP_HIGH_H, DIP_HIGH_C, elevation)

    /** Linear in ln h between samples, extrapolated from the end segments. */
    private fun interpolateLn(hs: DoubleArray, cs: DoubleArray, h: Double): Double {
        val i = hs.indexOfLast { it <= h }.coerceIn(0, hs.size - 2)
        val t = (ln(h) - ln(hs[i])) / (ln(hs[i + 1]) - ln(hs[i]))
        return cs[i] + t * (cs[i + 1] - cs[i])
    }

    /** WGS84 distance from the center of the Earth at [latitude], in meters. */
    private fun geocentricRadius(latitude: Double): Double {
        val a = 6_378_137.0
        val b = a * (1 - 1 / 298.257223563)
        val c = cos(latitude * PI / 180)
        val s = sin(latitude * PI / 180)
        return sqrt(((a * a * c) * (a * a * c) + (b * b * s) * (b * b * s)) / ((a * c) * (a * c) + (b * s) * (b * s)))
    }

    // ponytail: a bounding box; the few km it overlaps with Sinai, Jordan and Lebanon share Israel's atmosphere.
    private fun isInIsrael(latitude: Double, longitude: Double) = latitude in 29.4..33.4 && longitude in 34.2..35.9
}
