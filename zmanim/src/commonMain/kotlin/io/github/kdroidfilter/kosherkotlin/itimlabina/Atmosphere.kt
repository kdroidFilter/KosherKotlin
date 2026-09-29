package io.github.kdroidfilter.kosherkotlin.itimlabina

import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.floor
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * A layered model atmosphere: in each layer the temperature is linear in height and the pressure a power of the
 * temperature. Traces the horizontal ray that reaches an observer to get the astronomical refraction at the horizon.
 */
internal class Atmosphere(vararg layers: Layer) {

    class Layer(
        val bottomKm: Double,
        val temperature: Double,
        val pressure: Double,
        /** K/km. */
        val lapse: Double,
        /** P = P0 · (T / T0)^exponent. */
        val exponent: Double,
    )

    private val layers = layers.toList()
    private val refractionCache = HashMap<Int, Double>()

    /** Temperature (K) at [heightM] meters. */
    fun temperature(heightM: Double): Double = at(heightM).first

    /** Pressure (mbar) at [heightM] meters. */
    fun pressure(heightM: Double): Double = at(heightM).second

    private fun at(heightM: Double): Pair<Double, Double> {
        val km = heightM / 1000
        val i = layers.indexOfLast { it.bottomKm <= km }.coerceAtLeast(0)
        val layer = layers[i]
        val t = layer.temperature + layer.lapse * (km - layer.bottomKm)
        val p = if (layer.lapse != 0.0) {
            layer.pressure * (t / layer.temperature).pow(layer.exponent)
        } else {
            layer.pressure * exp(-(km - layer.bottomKm) * HYDROSTATIC / layer.temperature)
        }
        return t to p
    }

    /**
     * Refraction (degrees) of the ray reaching an observer at [observerM] meters horizontally, traced up to 70 km;
     * the height is floored to 100 m and each height traced once.
     */
    fun refraction(observerM: Double): Double {
        val floored = (floor(observerM / 100) * 100).toInt()
        return refractionCache.getOrPut(floored) { trace(floored.toDouble()) }
    }

    /** ∫ −(dn/dh)/n · tan z dh with n·r·sin z constant, over u = √(h − h0) to tame the start of the ray. */
    private fun trace(h0: Double): Double {
        val du = 0.02
        val count = (sqrt(TOP_M - h0) / du).toInt()
        val n0 = index(h0)
        val r0 = EARTH_RADIUS + h0
        var sum = 0.0
        var previous = Double.NaN
        for (k in 0 until count) {
            val u = (k + 0.5) * du
            val h = h0 + u * u
            val n = index(h)
            val dndh = (index(h + 0.5) - index(h - 0.5))
            val s = n0 * r0 / (n * (EARTH_RADIUS + h))
            val tanZ = s / sqrt((1 - s * s).coerceAtLeast(1e-30))
            val integrand = -dndh / n * tanZ * 2 * u
            if (!previous.isNaN()) sum += (previous + integrand) / 2 * du
            previous = integrand
        }
        return sum * 180 / PI
    }

    private fun index(heightM: Double): Double {
        val (t, p) = at(heightM)
        return 1 + REFRACTIVITY * p / t
    }

    private companion object {
        const val TOP_M = 70_000.0
        const val EARTH_RADIUS = 6_371_000.0
        const val HYDROSTATIC = 34.16

        /** (n − 1)·T/P for visible light (λ = 0.6 µm): (77.46 + 0.459/λ²)·10⁻⁶. */
        const val REFRACTIVITY = (77.46 + 0.459 / 0.36) * 1e-6
    }
}
