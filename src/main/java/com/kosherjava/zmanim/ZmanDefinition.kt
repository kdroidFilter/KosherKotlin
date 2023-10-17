package com.kosherjava.zmanim

import kotlin.time.Duration.Companion.minutes

/**
 *
 * @param relationship
 * ZmanType.TZAIS  occurs 45.minutes          after  ZmanType.SHKIAH
 * ZmanType.SHKIAH occurs 45.minutes          before ZmanType.TZAIS
 * ZmanType.SHKIAH occurs 45.minutes.zmaniyos before ZmanType.TZAIS
 * ZmanType.SHKIAH occurs 16.1F.degrees       before ZmanType.TZAIS
 * */
data class ZmanDefinition(
    val type: ZmanType,
    val mainCalculationMethodUsed: ZmanCalculationMethod<*>? = null,
    val zmanToCalcMethodUsed: Map<ZmanType, ZmanCalculationMethod<*>>? = null,
    val isElevationUsed: UsesElevation = UsesElevation.UNSPECIFIED,
    val definitionOfDayUsed: DayDefinition? = null,
    val supportingAuthorities: List<ZmanAuthority> = listOf(),
    val relationship: ZmanRelationship<*>? = null,
) {
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
    ) {
        companion object {
            fun fromCalculationMethod(
                dayStartCalculationMethod: ZmanCalculationMethod<*>,
                dayEndCalculationMethod: ZmanCalculationMethod<*> = dayStartCalculationMethod,
                dayStartsAtZman: ZmanType = ZmanType.ALOS,
                dayEndsAtZman: ZmanType = ZmanType.TZAIS,
                dayStartUsesElevation: UsesElevation = UsesElevation.UNSPECIFIED,
                dayEndUsesElevation: UsesElevation = UsesElevation.UNSPECIFIED,
            ) = DayDefinition(
                ZmanDefinition(
                    dayStartsAtZman, dayStartCalculationMethod, null, dayStartUsesElevation
                ),
                ZmanDefinition(
                    dayEndsAtZman, dayEndCalculationMethod, null, dayEndUsesElevation
                ),
            )

            fun DAWN_TO_DUSK(startMethod: ZmanCalculationMethod<*>, endMethod: ZmanCalculationMethod<*> = startMethod) =
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

            fun SUNRISE_TO_SUNSET(startMethod: ZmanCalculationMethod<*>, endMethod: ZmanCalculationMethod<*> = startMethod) = DayDefinition(
                ZmanDefinition(
                    ZmanType.HANAITZ,
                    startMethod,
                ), ZmanDefinition(
                    ZmanType.SHKIAH,
                    endMethod,
                )
            )
            val DAWN_TO_DUSK = DayDefinition(
                ZmanDefinition(
                    ZmanType.ALOS,
                    ZmanCalculationMethod.Unspecified,
                ), ZmanDefinition(
                    ZmanType.TZAIS,
                    ZmanCalculationMethod.Unspecified,
                )
            )
            val SUNRISE_TO_SUNSET = DayDefinition(
                ZmanDefinition(
                    ZmanType.HANAITZ,
                    ZmanCalculationMethod.Unspecified,
                ), ZmanDefinition(
                    ZmanType.SHKIAH,
                    ZmanCalculationMethod.Unspecified,
                )
            )

            /**
             * @see ZmanAuthority.RAZEH
             * */
            val DAWN_16_1_TO_SEA_LEVEL_SUNSET = DayDefinition(
                ZmanDefinition(
                    ZmanType.ALOS, ZmanCalculationMethod.Degrees._16_1, null, UsesElevation.ALWAYS

                ), ZmanDefinition(
                    ZmanType.SHKIAH, ZmanAuthority.Unanimous, null, UsesElevation.NEVER
                )
            )
            val DAWN_16_1_TO_ELEVATION_ADJUSTED_SUNSET = DayDefinition(
                ZmanDefinition(
                    ZmanType.ALOS, ZmanCalculationMethod.Degrees._16_1, null, UsesElevation.ALWAYS

                ), ZmanDefinition(
                    ZmanType.SHKIAH, ZmanAuthority.Unanimous, null, UsesElevation.IF_SET

                )
            )

            /**
             * @see ComplexZmanimCalendar.sofZmanShmaAlos16Point1ToTzaisGeonim7Point083Degrees
             * day starts at [*alos* 16.1˚][alos16Point1Degrees] and ends at
             * [*tzais* 7.083˚][tzaisGeonim7Point083Degrees]
             * */
            val DAWN_16_1_TO_DUSK_7_083 = DayDefinition(
                ZmanDefinition(
                    ZmanType.ALOS, ZmanCalculationMethod.Degrees._16_1, null, UsesElevation.ALWAYS

                ), ZmanDefinition(
                    ZmanType.TZAIS, ZmanCalculationMethod.Degrees._7_083, null, UsesElevation.ALWAYS

                )
            )
            val DAWN_16_1_TO_DUSK_3_8 = DayDefinition(
                ZmanDefinition(
                    ZmanType.ALOS, ZmanCalculationMethod.Degrees._16_1, null,

                    UsesElevation.ALWAYS

                ), ZmanDefinition(
                    ZmanType.TZAIS, ZmanCalculationMethod.Degrees._3_8, null,

                    UsesElevation.ALWAYS

                )
            )
            val DAWN_16_1_TO_DUSK_3_7 = DayDefinition(
                ZmanDefinition(
                    ZmanType.ALOS, ZmanCalculationMethod.Degrees._16_1, null,

                    UsesElevation.ALWAYS

                ), ZmanDefinition(
                    ZmanType.TZAIS, ZmanCalculationMethod.Degrees._3_7, null, UsesElevation.ALWAYS

                )
            )
            val DAWN_72_BEFORE_16_1_TO_DUSK_3_7 = DayDefinition(
                ZmanDefinition(
                    ZmanType.ALOS, ZmanCalculationMethod.Degrees._16_1, null,

                    UsesElevation.ALWAYS,

                    relationship = ZmanType.ALOS occurs 72.minutes before ZmanDefinition(
                        ZmanType.ALOS, ZmanCalculationMethod.Degrees._16_1
                    )
                ), ZmanDefinition(
                    ZmanType.TZAIS, ZmanCalculationMethod.Degrees._3_7, null, UsesElevation.ALWAYS

                )
            )
            val DAWN_72_MINUTES_TO_FIXED_LOCAL_CHATZOS = DayDefinition(
                ZmanDefinition(
                    ZmanType.ALOS, ZmanCalculationMethod.FixedDuration._72

                ), ZmanDefinition(
                    ZmanType.TZAIS, ZmanCalculationMethod.FixedLocalChatzos, null,

                    UsesElevation.NEVER

                )
            )
            val DAWN_90_MINUTES_TO_FIXED_LOCAL_CHATZOS = DayDefinition(
                ZmanDefinition(
                    ZmanType.ALOS, ZmanCalculationMethod.FixedDuration._90

                ), ZmanDefinition(
                    ZmanType.TZAIS, ZmanCalculationMethod.FixedLocalChatzos, null,

                    UsesElevation.NEVER

                )
            )
            val SUNRISE_TO_FIXED_LOCAL_CHATZOS = DayDefinition(
                ZmanDefinition(
                    ZmanType.HANAITZ, ZmanCalculationMethod.Unspecified, null,

                    UsesElevation.ALWAYS

                ), ZmanDefinition(
                    ZmanType.TZAIS, ZmanCalculationMethod.FixedLocalChatzos, null,

                    UsesElevation.NEVER

                )
            )
            val FIXED_LOCAL_CHATZOS_TO_SUNSET = DayDefinition(
                ZmanDefinition(
                    ZmanType.HANAITZ, ZmanCalculationMethod.FixedLocalChatzos, null,

                    UsesElevation.ALWAYS

                ), ZmanDefinition(
                    ZmanType.SHKIAH, ZmanCalculationMethod.Unspecified, null,

                    UsesElevation.ALWAYS

                )
            )
            val DAWN_16_1_TO_FIXED_LOCAL_CHATZOS = DayDefinition(
                ZmanDefinition(
                    ZmanType.ALOS, ZmanCalculationMethod.Degrees._16_1, null,

                    UsesElevation.ALWAYS

                ), ZmanDefinition(
                    ZmanType.TZAIS, ZmanCalculationMethod.FixedLocalChatzos, null,

                    UsesElevation.NEVER

                )
            )
            val DAWN_18_TO_FIXED_LOCAL_CHATZOS = DayDefinition(
                ZmanDefinition(
                    ZmanType.ALOS, ZmanCalculationMethod.Degrees._18, null,

                    UsesElevation.ALWAYS

                ), ZmanDefinition(
                    ZmanType.TZAIS, ZmanCalculationMethod.FixedLocalChatzos, null,

                    UsesElevation.NEVER

                )
            )

            fun DAWN_72_ZMANIS_TO_DUSK_ATERET_TORAH(offset: Double = ComplexZmanimCalendar.ATERET_TORAH_DEFAULT_OFFSET) =
                DayDefinition(
                    ZmanDefinition(
                        ZmanType.ALOS, ZmanCalculationMethod.ZmaniyosDuration._72, null,

                        UsesElevation.IF_SET

                    ), ZmanDefinition(
                        ZmanType.TZAIS, ZmanCalculationMethod.FixedDuration.AteretTorah(offset)

                    )
                )
        }

    }

    enum class UsesElevation {
        IF_SET, NEVER, ALWAYS, UNSPECIFIED
    }

    companion object {
        val empty = ZmanDefinition(ZmanType.MOLAD, null, null, UsesElevation.UNSPECIFIED)
    }
}