package io.github.kdroidfilter.kosherkotlin.itimlabina

import kotlin.math.PI
import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.tan

/**
 * Geocentric apparent position of the sun, after the NREL Solar Position Algorithm (Reda & Andreas, 2004):
 * VSOP87 Earth terms, IAU 1980 nutation and aberration. Accurate to about 0.0003°, i.e. well under a second of
 * sunrise time, where KosherKotlin's NOAA calculator is off by up to 0.01° (3–4 s at sunrise).
 *
 * Altitudes are geocentric: no parallax, no refraction.
 */
internal object SolarPosition {

    /** Julian day (UT) of a Unix epoch millisecond. */
    fun julianDay(epochMillis: Long): Double = epochMillis / 86_400_000.0 + 2_440_587.5

    fun epochMillis(julianDay: Double): Long = ((julianDay - 2_440_587.5) * 86_400_000.0).toLong()

    class Sun(
        /** Degrees. */
        val rightAscension: Double,
        /** Degrees. */
        val declination: Double,
        /** Apparent Greenwich sidereal time, degrees. */
        val siderealTime: Double,
        /** Earth–sun distance, AU. */
        val radius: Double,
    ) {
        /** Apparent solar semidiameter, degrees. */
        val semidiameter: Double get() = 959.63 / 3600 / radius

        fun hourAngle(longitude: Double): Double = normalize180(siderealTime + longitude - rightAscension)

        fun altitude(latitude: Double, longitude: Double): Double {
            val phi = latitude.rad
            val delta = declination.rad
            return asin(sin(phi) * sin(delta) + cos(phi) * cos(delta) * cos(hourAngle(longitude).rad)).deg
        }
    }

    fun sun(julianDay: Double): Sun {
        val jde = julianDay + deltaT(julianDay) / 86_400
        val jc = (julianDay - 2_451_545) / 36_525
        val jce = (jde - 2_451_545) / 36_525
        val jme = jce / 10

        val l = series(jme, SpaTables.L0, SpaTables.L1, SpaTables.L2, SpaTables.L3, SpaTables.L4, SpaTables.L5).deg
        val b = series(jme, SpaTables.B0, SpaTables.B1).deg
        val r = series(jme, SpaTables.R0, SpaTables.R1, SpaTables.R2, SpaTables.R3, SpaTables.R4)
        val theta = normalize360(l + 180)
        val beta = -b

        val x = doubleArrayOf(
            297.85036 + 445_267.111480 * jce - 0.0019142 * jce * jce + jce * jce * jce / 189_474,
            357.52772 + 35_999.050340 * jce - 0.0001603 * jce * jce - jce * jce * jce / 300_000,
            134.96298 + 477_198.867398 * jce + 0.0086972 * jce * jce + jce * jce * jce / 56_250,
            93.27191 + 483_202.017538 * jce - 0.0036825 * jce * jce + jce * jce * jce / 327_270,
            125.04452 - 1_934.136261 * jce + 0.0020708 * jce * jce + jce * jce * jce / 450_000,
        )
        var dPsi = 0.0
        var dEps = 0.0
        for (i in SpaTables.NUTATION_Y.indices) {
            val y = SpaTables.NUTATION_Y[i]
            val abcd = SpaTables.NUTATION_ABCD[i]
            val arg = (y[0] * x[0] + y[1] * x[1] + y[2] * x[2] + y[3] * x[3] + y[4] * x[4]).rad
            dPsi += (abcd[0] + abcd[1] * jce) * sin(arg)
            dEps += (abcd[2] + abcd[3] * jce) * cos(arg)
        }
        dPsi /= 36_000_000
        dEps /= 36_000_000

        val u = jme / 10
        val eps0 = 84_381.448 + u * (-4_680.93 + u * (-1.55 + u * (1_999.25 + u * (-51.38 + u * (-249.67 +
            u * (-39.05 + u * (7.12 + u * (27.87 + u * (5.79 + u * 2.45)))))))))
        val eps = eps0 / 3600 + dEps
        val lambda = theta + dPsi - 20.4898 / (3600 * r)

        val nu0 = normalize360(
            280.46061837 + 360.98564736629 * (julianDay - 2_451_545) + 0.000387933 * jc * jc - jc * jc * jc / 38_710_000,
        )
        val nu = nu0 + dPsi * cos(eps.rad)

        val alpha = normalize360(
            atan2(sin(lambda.rad) * cos(eps.rad) - tan(beta.rad) * sin(eps.rad), cos(lambda.rad)).deg,
        )
        val delta = asin(sin(beta.rad) * cos(eps.rad) + cos(beta.rad) * sin(eps.rad) * sin(lambda.rad)).deg
        return Sun(alpha, delta, nu, r)
    }

    /** Σ Tᵢ·τⁱ where Tᵢ = Σ A·cos(B + C·τ), scaled by 1e-8. */
    private fun series(tau: Double, vararg tables: Array<DoubleArray>): Double {
        var sum = 0.0
        var power = 1.0
        for (table in tables) {
            var term = 0.0
            for (row in table) term += row[0] * cos(row[1] + row[2] * tau)
            sum += term * power
            power *= tau
        }
        return sum / 1e8
    }

    /**
     * TT − UT in seconds, Espenak & Meeus polynomial for 2005–2050.
     * ponytail: one polynomial; a second of ΔT moves the sun by 0.00001°, so wider ranges need no more.
     */
    private fun deltaT(julianDay: Double): Double {
        val t = (julianDay - 2_451_544.5) / 365.25
        return 62.92 + 0.32217 * t + 0.005589 * t * t
    }

    private fun normalize360(degrees: Double): Double = ((degrees % 360) + 360) % 360
    private fun normalize180(degrees: Double): Double = normalize360(degrees + 180) - 180
    private val Double.rad: Double get() = this * PI / 180
    private val Double.deg: Double get() = this * 180 / PI
}
