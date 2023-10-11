package com.kosherjava.zmanim

import kotlin.math.absoluteValue
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

/**
 * The length of a shaah zmanis (halachic/solar hour) is calculated based on the length of the day.
 * The definition of day has various opinions, and thus the length of a shaah zmanis varies.
 * One such definition is dusk to dawn.
 * Dawn can either be calculated as:
 *      1. a fixed number of minutes before sunrise,
 *      2. or as the moment that the sun reaches a number of degrees above the eastern geometric horizon - which happens before sunrise.
 * Sunrise is an astronomical phenomenon, not subject to varying opinions (save for whether to adjust for elevation).
 * Dusk is often calculated as the inverse of dawn (i.e. after sunrise, and below the western geometric horizon),
 * though some opinions place it orthogonally to their definiition of dawn (e.g. [ZmanAuthority.AteretTorah] in [ComplexZmanimCalendar.shaahZmanisAteretTorah]).
 * The number of fixed minutes for sunrise/sunset is determined by the time it takes to walk the distance of a [*mil*][https://en.wikipedia.org/wiki/Biblical_mile].
 * Some interpret those fixed minutes (let M = minutes) as measured by the position of the sun in degrees relative to
 * the eastern geometric horizon M minutes before sunrise in Jerusalem [around the equinox / equilux](https://kosherjava.com/2022/01/12/equinox-vs-equilux-zmanim-calculations/).
 *
 * This is the distinction between the two aforementioned ways of calculating dawn.
 * Some measure by the length of time it takes to walk 4 mil, while others measure using 5 mil.
 * This is but one divergence of opinion in the calculation of shaah zmanis. Here are all of them:
 *  1. Fixed minutes or degrees?
 *      - Measure in fixed minutes or zmaniyos minutes?
 *  2. How many minutes does it take to walk a mil?
 *  3. Should a full mil be used to measure, or 3/4 of a mil?
 *  4. Multiply by 4 or 5 mil?
 *  5. Account for elevation or not?
 *  6. When does day start/end?
 *    - Dawn/dusk
 *    - sunrise/sunset (which are astronomical phenomena)
 *    - dawn 16.1/sea level sunset
 * see [ZmanDefinition.DayDefinition]
 *
 *  TODO it seems this class conflates two ideas: how a zman is calculated, and the sources for the calculation's legitamacy/the authorities who hold by it ("supporting authorities"). This suggests the need to split the class up.
 * */
sealed class ZmanCalculationMethod<T>(val value: T) {

    /* One or both of these must be implemented! */
    open fun format(): String = format(true)
    open fun format(inEnglish: Boolean): String = format()

    open fun format(subjectZman:String, zmanRelativeTo: String) = format()
    open fun valueToString() = format()

    object Unspecified : ZmanCalculationMethod<Unit>(Unit) {
        override fun format(): String = "Unspecified"
    }

    /**
     * @see ComplexZmanimCalendar.fixedLocalChatzos
     * */
    object FixedLocalChatzos : ZmanCalculationMethod<Unit>(Unit) {
        override fun format(): String = "Fixed Local Chatzos"
    }

    /**
     * Dawn for this calculation is 60 minutes before sunrise.
     * Dusk is 60 minutes after sunset.
     *
     * @param duration if negative, this zman is [duration] before [fromZman]. If positive, after. If [Duration.ZERO], this zman is equal to [fromZman].
     * */
    open class FixedDuration(val duration: Duration, val fromZman: ZmanType? = null/*sunrise/set*/) :
        ZmanCalculationMethod<Duration>(duration) {
        /**
         * Ateret torah (which defaults to 40 minutes)
         * */
        data class AteretTorah(val minutes: Double = ComplexZmanimCalendar.ATERET_TORAH_DEFAULT_OFFSET) :
            FixedDuration(minutes.minutes) {
            override fun valueToString(): String = ZmanDescriptionFormatter.formatAteretTorah(minutes)
        }
        companion object {


            /**
             * A mil takes 15 minutes to walk; 4 mil * 15 minutes/mil = 60 minutes
             *
             * @see ZmanAuthority.CHAVAS_YAIR
             * @see ZmanAuthority.DIVREI_MALKIEL
             * @see ZmanAuthority.MAHARIK
             * @see ZmanAuthority.HISACHDUS_HARABONIM
             *
             * */
            val _30 = FixedDuration(30.minutes)
            val _60 = FixedDuration(60.minutes)

            /**
             * A mil takes 18 minutes to walk; 4 mil * 18 minutes/mil = 72 minutes
             * @see ZmanAuthority.RAAVAN (disputed - see [ComplexZmanimCalendar.alos60])
             * */
            val _72 = FixedDuration(72.minutes)

            /**
             * A mil takes 22.5 minutes to walk; 4 mil * 22.5 minutes/mil = 90 minutes
             * */
            val _90 = FixedDuration(90.minutes)

            /**
             * A mil takes 24 minutes to walk; 4 mil * 24 minutes/mil = 96 minutes
             * */
            val _96 = FixedDuration(96.minutes)

            /**
             * A mil takes 24 minutes to walk; 5 mil * 24 minutes/mil = 120 minutes
             * */
            val _120 = FixedDuration(120.minutes)
        }

        override fun format() = format("Day", fromZman?.toString() ?: "sunrise/set")

        override fun format(subjectZman:String, zmanRelativeTo: String) = "$subjectZman is ${valueToString()} ${if (duration.isNegative()) "before" else "after"} $zmanRelativeTo"

        override fun valueToString(): String = duration.durationValueToString()
    }

    internal fun Duration.durationValueToString(halachic: Boolean = false) = toComponents { hours, minutes, seconds, nanoseconds ->
        buildString {
            val totalMinutes = inWholeMinutes
            if(totalMinutes.absoluteValue <= 120) append("$totalMinutes${if(halachic) " Halachic "  else " "}minutes")
            else {
                append(if (hours != 0L) "$hours hour${hours.pluralSuffix()}" else "")
                append(if (minutes != 0) " $minutes minute${minutes.pluralSuffix()}" else "")
                if (halachic) append(" - Halachic time")
            }
        }
    }
    internal fun Long.pluralSuffix() = if (absoluteValue > 1L) "s" else ""
    internal fun Int.pluralSuffix() = if (absoluteValue > 1L) "s" else ""

    data class FixedMinutesFloat(val minutes: Float) : ZmanCalculationMethod<Float>(minutes) {
        override fun format() = "Day is $minutes minutes before sunrise / after sunset"
        override fun valueToString(): String = "$minutes minutes"
    }

    /**
     * Dawn for this calculation is X zmaniyos minutes before sunrise.
     * Dusk is X zmaniyos minutes after sunset.
     * */
    data class ZmaniyosDuration(val duration: Duration, val fromZman: ZmanType? = null) :
        ZmanCalculationMethod<Duration>(duration) {

        companion object {
            /**
             * A mil takes 18 minutes to walk
             *
             *
             * @see ZmanAuthority.RAAVAN (disputed - see [ComplexZmanimCalendar.alos60])
             * */
            val _72 = ZmaniyosDuration(72.minutes)
            val _90 = ZmaniyosDuration(90.minutes)
            val _96 = ZmaniyosDuration(96.minutes)
            val _120 = ZmaniyosDuration(120.minutes)
        }

        override fun format(inEnglish: Boolean) = buildString {
            append("Day is ")
            append(duration)
            append(
                if (inEnglish) "Halachic Minutes"
                else " זמניות "
            )
            append("minutes before sunrise / after sunset")
        }

        override fun valueToString(): String = duration.durationValueToString(true)
    }

    /**
     * Dawn is when the sun is x degrees below the eastern geometric horizon before sunrise.
     * Dusk is when the sun is x degrees below the western geometric horizon after sunset.
     * */
    data class Degrees(val degrees: Float, val fromZman: ZmanType? = null/*sunrise/set*/) :
        ZmanCalculationMethod<Float>(degrees) {
        companion object {


            /**
             * geonim/13.5 minutes as measured in degrees
             * */
            val _3_7 = Degrees(3.7F)

            /**
             * geonim/14 minutes as measured in degrees.
             * The 14 minutes are based on 3/4 of an 18 minute *mil*, with half a minute added for Rav Yosi.
             * */
            val _3_8 = Degrees(3.8F)

            /**
             * @see ComplexZmanimCalendar.tzaisGeonim7Point083Degrees
             * */
            val _7_083 = Degrees(7.083F)

            /**
             * 35/36 minutes as measured in degrees
             * @see [ComplexZmanimCalendar.misheyakir7Point65Degrees] for sources
             * */
            val _7_65 = Degrees(7.65F)

            val _7_67 = Degrees(7.67F)

            val _8_5 = Degrees(8.5F)
            val _9_3 = Degrees(9.3F)

            /**
             * 45 minutes as measured in degrees
             * @see [ComplexZmanimCalendar.misheyakir9Point5Degrees] for sources
             * */
            val _9_5 = Degrees(9.5F)

            val _9_75 = Degrees(9.75F)

            /**
             * 45 minutes before [sunrise] in Jerusalem [around the equinox / equilux](https://kosherjava.com/2022/01/12/equinox-vs-equilux-zmanim-calculations/)
             * */
            val _10_2 = Degrees(10.2F)

            /**
             * 48 minutes before [sunrise] in Jerusalem [around the equinox / equilux](https://kosherjava.com/2022/01/12/equinox-vs-equilux-zmanim-calculations/)
             * */
            val _11 = Degrees(11F)

            /**
             * 52 minutes before [sunrise] in Jerusalem [around the equinox / equilux](https://kosherjava.com/2022/01/12/equinox-vs-equilux-zmanim-calculations/)
             * */
            val _11_5 = Degrees(11.5F)
            val _13_24 = Degrees(13.24F)

            /**
             * Rabeinu tam/72 minutes as measured in degrees
             * */
            val _16_1 = Degrees(16.1F)
            val _18 = Degrees(18F)

            /**
             * - This is the [Rambam](https://en.wikipedia.org/wiki/Maimonides)'s *alos* according to
             * - Rabbi Moshe Kosower's [Maaglei Tzedek](https://www.worldcat.org/oclc/145454098), page 88,
             * - Rabbi Dovid Shor's [Ayeles Hashachar Vol. I, page 12](https://hebrewbooks.org/pdfpager.aspx?req=33464&pgnum=13),
             * - Rabbi Yaakov Gershon Weiss's [Yom Valayla Shel Torah, Ch. 34, p. 222](https://hebrewbooks.org/pdfpager.aspx?req=55960&pgnum=258) and
             * - Rabbi Yaakov Shakow's [Luach Ikvei Hayom](https://www.worldcat.org/oclc/1043573513).
             *
             * @see ZmanAuthority.RAMBAM
             * @see ZmanAuthority.MAAGALEI_TZEDEK
             * @see ZmanAuthority.SHOR
             * @see ZmanAuthority.WEISS
             * @see ZmanAuthority.SHAKOW
             * */
            val _19 = Degrees(19F)

            /**
             * 90 minutes as measured in degrees
             * */
            val _19_8 = Degrees(19.8F)

            val _26 = Degrees(26F)
        }

        override fun format(): String = "Day is ${degrees}˚ below sunrise / sunset"
        override fun format(subjectZman: String, zmanRelativeTo: String): String = "$subjectZman is $degrees˚ ${if (degrees < 0) "before" else "after"} $zmanRelativeTo"
        override fun valueToString(): String = "$degrees˚"
    }
}