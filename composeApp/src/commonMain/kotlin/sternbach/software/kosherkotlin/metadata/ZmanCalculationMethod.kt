package sternbach.software.kosherkotlin.metadata

import sternbach.software.kosherkotlin.ComplexZmanimCalendar
import sternbach.software.kosherkotlin.ZmanDescriptionFormatter
import kotlin.math.absoluteValue
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

/**
 * An instance of this class represents a description for how a zman is calculated. The description may rely on other
 * information provided in the [ZmanDefinition] (e.g. whether the calculation changes depending on the [ComplexZmanimCalendar.isUseElevation] setting).
 *
 * *Why are there so many ways to calculate a zman?*
 * The length of a shaah zmanis (halachic/solar hour) is calculated based on the length of the day.
 * The definition of day has various opinions, and thus the length of a shaah zmanis varies.
 * One such definition is dusk to dawn.
 * Dawn can either be calculated as:
 * 1. a fixed number of minutes before sunrise,
 * 2. or as the moment that the sun reaches a number of degrees above the eastern geometric horizon - which happens before sunrise.
 *
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
 *    - etc.
 * see [DayDefinition]
 *
 *
 * */
sealed class ZmanCalculationMethod<T>(val value: T) {
    companion object {
        val Duration.zmaniyos get() = ZmaniyosDuration(this)
        val Float.degrees get() = Degrees(this)
        val Int.degrees get() = Degrees(this.toFloat())
        val Double.degrees get() = Degrees(this.toFloat())
    }

    /* One or both of these must be implemented! */
    open fun format(): String = format(true)
    open fun format(inEnglish: Boolean): String = format()

    open fun format(subjectZman:String, zmanRelativeTo: String) = format()
    open fun valueToString() = format()
    open fun shortDescription() = valueToString()

    internal fun Duration.durationValueToString(halachic: Boolean = false) = absoluteValue.toComponents { hours, minutes, seconds, nanoseconds ->
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
    private fun Long.pluralSuffix() = if (absoluteValue > 1L) "s" else ""
    private fun Int.pluralSuffix() = if (absoluteValue > 1L) "s" else ""

    /**
     * A form of empty set/placeholder indicating that the calculation method was not specified/is irrelevant.
     * Almost exclusively used when defining a [ZmanDefinition] that is reused across zmanim.
     * */
    object Unspecified : ZmanCalculationMethod<Unit>(Unit) {
        override fun format(): String = "Unspecified"
    }
    /**
     * A method of calculation in which the zman is calculated relative to another zman (e.g. X occurs 10 minutes before Y).
     * If a [com.kosherjava.zmanim.Zman] uses this [ZmanCalculationMethod], the information on how it is calculated will be in the [relationship]
     *
     * A [ZmanRelationship] is a relationship between two [ZmanType]s, dictating when [ZmanRelationship.subject] occurs relative to [ZmanRelationship.relativeToZman].
     * ZmanType.TZAIS  occurs 45.minutes          after  ZmanType.SHKIAH
     * ZmanType.SHKIAH occurs 45.minutes          before ZmanType.TZAIS
     *
     * ZmanType.SHKIAH occurs 45.minutes.zmaniyos before ZmanType.TZAIS
     * ZmanType.SHKIAH occurs 16.1F.degrees       before ZmanType.TZAIS
     *
     * ZmanType.BAIN_HASHMASHOS occurs 13.5.minutes before tzaisGeonim7Point083Degrees.rules
     *
     *
     * @see ZmanRelationship
     * @see ZmanType.occurs
     * @see Occurence
     * */
    data class Relationship<T>(val relationship: ZmanRelationship<T>) : ZmanCalculationMethod<Unit>(Unit) {
        override fun format(): String = "Unspecified"
    }

    /**
     * A method of calculation in which the later of two zmanim is used.
     * Meaning that if [zman1] occurs at 6:00 AM and [zman2] occurs at 6:05 AM, this zman takes on the value of 6:00 AM.
     * */
    data class LaterOf(val zman1: ZmanDefinition, val zman2: ZmanDefinition): ZmanCalculationMethod<Unit>(Unit) {
        override fun format(): String = "Later of ${zman1.calculationMethod?.format()} or ${zman2.calculationMethod?.format()}"
    }

    /**
     * @see ComplexZmanimCalendar.fixedLocalChatzos
     * */
    object FixedLocalChatzos : ZmanCalculationMethod<Unit>(Unit) {
        override fun format(): String = "Fixed Local Chatzos"
    }

    /**
     * Dawn for this calculation is X zmaniyos minutes before sunrise.
     * Dusk is X zmaniyos minutes after sunset.
     * */
    data class ZmaniyosDuration(val duration: Duration): ZmanCalculationMethod<Duration>(duration) {

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
        override fun shortDescription(): String = duration.durationValueToString(true)//TODO add relative to zman
    }

    /**
     * Dawn is when the sun is x degrees below the eastern geometric horizon before sunrise.
     * Dusk is when the sun is x degrees below the western geometric horizon after sunset.
     * */
    data class Degrees(val degrees: Float) :
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
    /**
     * Dawn for this calculation is 60 minutes before sunrise.
     * Dusk is 60 minutes after sunset.
     *
     * @param duration if negative, this zman is [duration] before what this is relative to . If positive, after.
     * */
    data class FixedDuration(val duration: Duration) :
        ZmanCalculationMethod<Duration>(duration) {
        /**
         * Ateret torah (which defaults to 40 minutes)
         * */
        data class AteretTorah(val minutes: Double = ComplexZmanimCalendar.ATERET_TORAH_DEFAULT_OFFSET) :
            ZmanCalculationMethod<Duration>(minutes.minutes) {
            override fun shortDescription(): String = ZmanDescriptionFormatter.shortDescriptionAteretTorah(minutes)
            override fun valueToString(): String = ZmanAuthority.Strings.ATERET_TORAH
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

        override fun format() = format("Day", "sunrise/set")

        override fun format(subjectZman:String, zmanRelativeTo: String) = "$subjectZman is ${valueToString()} ${if (duration.isNegative()) "before" else "after"} $zmanRelativeTo"

        override fun valueToString(): String = duration.durationValueToString()
        override fun shortDescription(): String = duration.durationValueToString()//TODO add relative to zman
    }

    /**
     * - The [GR"A][ZmanAuthority.GRA] holds that the day starts at [sunrise][ZmanType.HANAITZ] and ends at [sunset][ZmanType.SHKIAH].
     * - The [MG"A][ZmanAuthority.MGA] holds that the day starts at [dawn][ZmanType.ALOS] and ends at [dusk][ZmanType.TZAIS].
     * - The [Raze"h/Menorah HaTehorah][ZmanAuthority.RAZEH] holds that the day starts at [*alos* 16.1˚][ComplexZmanimCalendar.alos16Point1Degrees] and ends at
     * [sea level sunset][ComplexZmanimCalendar.seaLevelSunset]
     * */
    data class DayDefinition(
        val dayStart: ZmanDefinition,
        val dayEnd: ZmanDefinition,
        val dayStartRelationship: ZmanRelationship<*>? = null,
        val dayEndRelationship: ZmanRelationship<*>? = null,
    ): ZmanCalculationMethod<Unit>(Unit) {
        override fun format(): String = "Day starts at $dayStart and ends at $dayEnd"//TODO add relationships
        companion object {

            fun fromCalculationMethod(
                dayStartCalculationMethod: ZmanCalculationMethod<*>,
                dayEndCalculationMethod: ZmanCalculationMethod<*> = dayStartCalculationMethod,
                dayStartsAtZman: ZmanType = ZmanType.ALOS,
                dayEndsAtZman: ZmanType = ZmanType.TZAIS,
                dayStartUsesElevation: ZmanDefinition.UsesElevation = ZmanDefinition.UsesElevation.UNSPECIFIED,
                dayEndUsesElevation: ZmanDefinition.UsesElevation = ZmanDefinition.UsesElevation.UNSPECIFIED,
            ) = DayDefinition(
                ZmanDefinition(
                    dayStartsAtZman, dayStartCalculationMethod, dayStartUsesElevation
                ),
                ZmanDefinition(
                    dayEndsAtZman, dayEndCalculationMethod, dayEndUsesElevation
                ),
            )
            fun dawnToDusk(startMethod: ZmanCalculationMethod<*>, endMethod: ZmanCalculationMethod<*> = startMethod) =
                DayDefinition(
                    ZmanDefinition(
                        ZmanType.ALOS,
                        startMethod,
                    ),
                    ZmanDefinition(
                        ZmanType.TZAIS,
                        endMethod,
                    )
                )
            fun sunriseToSunset(startMethod: ZmanCalculationMethod<*>, endMethod: ZmanCalculationMethod<*> = startMethod) = DayDefinition(
                ZmanDefinition(
                    ZmanType.HANAITZ,
                    startMethod,
                ), ZmanDefinition(
                    ZmanType.SHKIAH,
                    endMethod,
                )
            )
            fun dawn72ZmanisToDuskAteretTorah(offset: Double = ComplexZmanimCalendar.ATERET_TORAH_DEFAULT_OFFSET) =
                DayDefinition(
                    ZmanDefinition(
                        ZmanType.ALOS, Relationship(ZmanType.ALOS occurs 72.minutes.zmaniyos before ZmanType.HANAITZ), ZmanDefinition.UsesElevation.IF_SET

                    ), ZmanDefinition(
                        ZmanType.TZAIS, FixedDuration.AteretTorah(offset)

                    )
                )

            /*val DAWN_72_BEFORE_16_1_TO_DUSK_3_7 = DayDefinition(
                ZmanDefinition(
                    ZmanType.ALOS, Degrees._16_1, ZmanDefinition.UsesElevation.ALWAYS,

                    relationship = ZmanType.ALOS occurs 72.minutes before Zman.DateBased(null,ZmanDefinition(
                        ZmanType.ALOS, Degrees._16_1
                    ))
                ), ZmanDefinition(
                    ZmanType.TZAIS, Degrees._3_7, ZmanDefinition.UsesElevation.ALWAYS

                )
            )*/
            val DAWN_72_MINUTES_TO_FIXED_LOCAL_CHATZOS = DayDefinition(
                ZmanDefinition(
                    ZmanType.ALOS, Relationship(ZmanType.ALOS occurs 72.minutes before ZmanType.HANAITZ)

                ), ZmanDefinition(
                    ZmanType.TZAIS, FixedLocalChatzos, ZmanDefinition.UsesElevation.NEVER

                )
            )

            val SUNRISE_TO_SUNSET = DayDefinition(
                ZmanDefinition(
                    ZmanType.HANAITZ,
                    Unspecified,
                ), ZmanDefinition(
                    ZmanType.SHKIAH,
                    Unspecified,
                )
            )
            /**
             * @see ZmanAuthority.RAZEH
             * */
            val DAWN_16_1_TO_ELEVATION_ADJUSTED_SUNSET = DayDefinition(
                ZmanDefinition(
                    ZmanType.ALOS, Degrees._16_1, ZmanDefinition.UsesElevation.ALWAYS

                ), ZmanDefinition(
                    ZmanType.SHKIAH, ZmanAuthority.Unanimous, ZmanDefinition.UsesElevation.IF_SET
                )
            )
            /**
             * @see ComplexZmanimCalendar.sofZmanShmaAlos16Point1ToTzaisGeonim7Point083Degrees
             * day starts at [*alos* 16.1˚][alos16Point1Degrees] and ends at
             * [*tzais* 7.083˚][tzaisGeonim7Point083Degrees]
             * */
            val DAWN_16_1_TO_DUSK_7_083 = DayDefinition(
                ZmanDefinition(
                    ZmanType.ALOS, Degrees._16_1, ZmanDefinition.UsesElevation.ALWAYS

                ), ZmanDefinition(
                    ZmanType.TZAIS, Degrees._7_083, ZmanDefinition.UsesElevation.ALWAYS

                )
            )
            val DAWN_16_1_TO_DUSK_3_8 = DayDefinition(
                ZmanDefinition(
                    ZmanType.ALOS, Degrees._16_1, ZmanDefinition.UsesElevation.ALWAYS

                ), ZmanDefinition(
                    ZmanType.TZAIS, Degrees._3_8, ZmanDefinition.UsesElevation.ALWAYS

                )
            )
            val DAWN_90_MINUTES_TO_FIXED_LOCAL_CHATZOS = DayDefinition(
                ZmanDefinition(
                    ZmanType.ALOS, Relationship(ZmanType.ALOS occurs 90.minutes before ZmanType.HANAITZ)

                ), ZmanDefinition(
                    ZmanType.TZAIS, FixedLocalChatzos, ZmanDefinition.UsesElevation.NEVER

                )
            )
            val DAWN_16_1_TO_DUSK_3_7 = DayDefinition(
                ZmanDefinition(
                    ZmanType.ALOS, Degrees._16_1, ZmanDefinition.UsesElevation.ALWAYS

                ), ZmanDefinition(
                    ZmanType.TZAIS, Degrees._3_7, ZmanDefinition.UsesElevation.ALWAYS

                )
            )
            val SUNRISE_TO_FIXED_LOCAL_CHATZOS = DayDefinition(
                ZmanDefinition(
                    ZmanType.HANAITZ, ZmanAuthority.Unanimous, ZmanDefinition.UsesElevation.ALWAYS

                ), ZmanDefinition(
                    ZmanType.TZAIS, FixedLocalChatzos, ZmanDefinition.UsesElevation.NEVER

                )
            )
            val FIXED_LOCAL_CHATZOS_TO_SUNSET = DayDefinition(
                ZmanDefinition(
                    ZmanType.HANAITZ, FixedLocalChatzos, ZmanDefinition.UsesElevation.ALWAYS

                ), ZmanDefinition(
                    ZmanType.SHKIAH, ZmanAuthority.Unanimous, ZmanDefinition.UsesElevation.ALWAYS

                )
            )

            val DAWN_16_1_TO_FIXED_LOCAL_CHATZOS = DayDefinition(
                ZmanDefinition(
                    ZmanType.ALOS, Degrees._16_1, ZmanDefinition.UsesElevation.ALWAYS

                ), ZmanDefinition(
                    ZmanType.TZAIS, FixedLocalChatzos, ZmanDefinition.UsesElevation.NEVER

                )
            )
            val DAWN_18_TO_FIXED_LOCAL_CHATZOS = DayDefinition(
                ZmanDefinition(
                    ZmanType.ALOS, Degrees._18, ZmanDefinition.UsesElevation.ALWAYS

                ), ZmanDefinition(
                    ZmanType.TZAIS, FixedLocalChatzos, ZmanDefinition.UsesElevation.NEVER

                )
            )
        }
    }
}