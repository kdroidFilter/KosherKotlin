package com.kosherjava.zmanim

data class ZmanDefinition(
    val mainCalculationMethodUsed: ZmanCalculationMethod<*>? = null,
    val zmanToCalcMethodUsed: Map<ZmanType, ZmanCalculationMethod<*>>? = null,
    val isElevationUsed: UsesElevation = UsesElevation.UNSPECIFIED,
    val definitionOfDayUsed: DayDefinition? = null
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
                        null,
                        mapOf(dayStartsAtZman to dayStartCalculationMethod),
                        dayStartUsesElevation
                    ),
                    ZmanDefinition(
                        null,
                        mapOf(dayEndsAtZman to  dayEndCalculationMethod),
                        dayEndUsesElevation
                    ),
            )
            val DAWN_TO_DUSK = DayDefinition(
                ZmanDefinition(
                    null,
                    mapOf(
                        ZmanType.ALOS to ZmanCalculationMethod.Unspecified,
                    )
                ),
                ZmanDefinition(
                    null,
                    mapOf(
                        ZmanType.TZAIS to ZmanCalculationMethod.Unspecified,
                    )
                )
            )
            val SUNRISE_TO_SUNSET = DayDefinition(

                ZmanDefinition(
                    null,
                    mapOf(
                        ZmanType.HANAITZ to ZmanCalculationMethod.Unspecified,
                    )
                ),
                ZmanDefinition(
                    null,
                    mapOf(
                        ZmanType.SHKIAH to ZmanCalculationMethod.Unspecified,
                    )
                )
            )

            /**
             * @see ZmanAuthority.RAZEH
             * */
            val DAWN_16_1_TO_SEA_LEVEL_SUNSET = DayDefinition(
                ZmanDefinition(
                    null,
                    mapOf(
                        ZmanType.ALOS to ZmanCalculationMethod.Degrees._16_1
                    ),
                    UsesElevation.ALWAYS
                ),
                ZmanDefinition(
                    null,
                    mapOf(
                        ZmanType.SHKIAH to ZmanAuthority.Unanimous,
                    ),
                    UsesElevation.NEVER
                )
            )
            val DAWN_16_1_TO_ELEVATION_ADJUSTED_SUNSET = DayDefinition(
                ZmanDefinition(
                    null,
                    mapOf(
                        ZmanType.ALOS to ZmanCalculationMethod.Degrees._16_1
                    ),
                    UsesElevation.ALWAYS
                ),
                ZmanDefinition(
                    null,
                    mapOf(
                        ZmanType.SHKIAH to ZmanAuthority.Unanimous,
                    ),
                    UsesElevation.IF_SET
                )
            )

            /**
             * @see ComplexZmanimCalendar.sofZmanShmaAlos16Point1ToTzaisGeonim7Point083Degrees
             * day starts at [*alos* 16.1˚][alos16Point1Degrees] and ends at
             * [*tzais* 7.083˚][tzaisGeonim7Point083Degrees]
             * */
            val DAWN_16_1_TO_DUSK_7_083 = DayDefinition(
                ZmanDefinition(
                    null,
                    mapOf(
                        ZmanType.ALOS to ZmanCalculationMethod.Degrees._16_1
                    ),
                    UsesElevation.ALWAYS
                ),
                ZmanDefinition(
                    null,
                    mapOf(
                        ZmanType.TZAIS to ZmanCalculationMethod.Degrees._7_083,
                    ),
                    UsesElevation.ALWAYS
                )
            )
            val DAWN_16_1_TO_DUSK_3_8 = DayDefinition(
                ZmanDefinition(
                    null,
                    mapOf(
                        ZmanType.ALOS to ZmanCalculationMethod.Degrees._16_1
                    ),
                    UsesElevation.ALWAYS
                ),
                ZmanDefinition(
                    null,
                    mapOf(
                        ZmanType.TZAIS to ZmanCalculationMethod.Degrees._3_8,
                    ),
                    UsesElevation.ALWAYS
                )
            )
            val DAWN_16_1_TO_DUSK_3_7 = DayDefinition(
                ZmanDefinition(
                    null,
                    mapOf(
                        ZmanType.ALOS to ZmanCalculationMethod.Degrees._16_1
                    ),
                    UsesElevation.ALWAYS
                ),
                ZmanDefinition(
                    null,
                    mapOf(
                        ZmanType.TZAIS to ZmanCalculationMethod.Degrees._3_7,
                    ),
                    UsesElevation.ALWAYS
                )
            )
            val DAWN_72_MINUTES_TO_FIXED_LOCAL_CHATZOS = DayDefinition(
                ZmanDefinition(
                    null,
                    mapOf(
                        ZmanType.ALOS to ZmanCalculationMethod.FixedDuration._72
                    )
                ),
                ZmanDefinition(
                    null,
                    mapOf(
                        ZmanType.TZAIS to ZmanCalculationMethod.FixedLocalChatzos
                    ),
                    UsesElevation.NEVER
                )
            )
            val DAWN_90_MINUTES_TO_FIXED_LOCAL_CHATZOS = DayDefinition(
                ZmanDefinition(
                    null,
                    mapOf(
                        ZmanType.ALOS to ZmanCalculationMethod.FixedDuration._90
                    )
                ),
                ZmanDefinition(
                    null,
                    mapOf(
                        ZmanType.TZAIS to ZmanCalculationMethod.FixedLocalChatzos
                    ),
                    UsesElevation.NEVER
                )
            )
            val SUNRISE_TO_FIXED_LOCAL_CHATZOS = DayDefinition(
                ZmanDefinition(
                    null,
                    mapOf(
                        ZmanType.HANAITZ to ZmanCalculationMethod.Unspecified
                    ),
                    UsesElevation.ALWAYS
                ),
                ZmanDefinition(
                    null,
                    mapOf(
                        ZmanType.TZAIS to ZmanCalculationMethod.FixedLocalChatzos
                    ),
                    UsesElevation.NEVER
                )
            )
            val FIXED_LOCAL_CHATZOS_TO_SUNSET = DayDefinition(
                ZmanDefinition(
                    null,
                    mapOf(
                        ZmanType.HANAITZ to ZmanCalculationMethod.FixedLocalChatzos
                    ),
                    UsesElevation.ALWAYS
                ),
                ZmanDefinition(
                    null,
                    mapOf(
                        ZmanType.SHKIAH to ZmanCalculationMethod.Unspecified
                    ),
                    UsesElevation.ALWAYS
                )
            )
            val DAWN_16_1_TO_FIXED_LOCAL_CHATZOS = DayDefinition(
                ZmanDefinition(
                    null,
                    mapOf(
                        ZmanType.ALOS to ZmanCalculationMethod.Degrees._16_1
                    ),
                    UsesElevation.ALWAYS
                ),
                ZmanDefinition(
                    null,
                    mapOf(
                        ZmanType.TZAIS to ZmanCalculationMethod.FixedLocalChatzos
                    ),
                    UsesElevation.NEVER
                )
            )
            val DAWN_18_TO_FIXED_LOCAL_CHATZOS = DayDefinition(
                ZmanDefinition(
                    null,
                    mapOf(
                        ZmanType.ALOS to ZmanCalculationMethod.Degrees._18
                    ),
                    UsesElevation.ALWAYS
                ),
                ZmanDefinition(
                    null,
                    mapOf(
                        ZmanType.TZAIS to ZmanCalculationMethod.FixedLocalChatzos
                    ),
                    UsesElevation.NEVER
                )
            )
            val DAWN_72_ZMANIS_TO_DUSK_ATERET_TORAH = DayDefinition(
                ZmanDefinition(
                    null,
                    mapOf(
                        ZmanType.ALOS to ZmanCalculationMethod.ZmaniyosDuration._72
                    ),
                    UsesElevation.IF_SET
                ),
                ZmanDefinition(
                    null,
                    mapOf(
                        ZmanType.TZAIS to ZmanCalculationMethod.FixedDuration.AteretTorah() //TODO if the offset is changed, this format string will be wrong
                    )
                )
            )
        }
    }
    enum class UsesElevation {
        IF_SET,
        NEVER,
        ALWAYS,
        UNSPECIFIED
    }
    companion object {
        val empty = ZmanDefinition(null, null, UsesElevation.UNSPECIFIED)
    }
}