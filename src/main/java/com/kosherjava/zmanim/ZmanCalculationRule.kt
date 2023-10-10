package com.kosherjava.zmanim

interface ZmanCalculationRule {
    

    interface DuskDefinitionRule : ZmanCalculationRule {
        object AteretTorahOffset : DuskDefinitionRule

        object _3_8_Degrees : DuskDefinitionRule
        object _3_7_Degrees : DuskDefinitionRule
    }

    interface DayDefinitionRule : ZmanCalculationRule {
        /**
         * Dawn is when the sun is X degrees below the eastern geometric horizon before sunrise.
         * Dusk is when the sun is X degrees below the western geometric horizon after sunset.
         * */
        object _16_1_Degrees : DayDefinitionRule
        object _26_Degrees : DayDefinitionRule
        object _10_2_Degrees : DayDefinitionRule
        object _7_65_Degrees : DayDefinitionRule
        object _9_5_Degrees : DayDefinitionRule
        object _11_Degrees : DayDefinitionRule
        object _11_5_Degrees : DayDefinitionRule
        object _19_Degrees : DayDefinitionRule
        object _19_8_Degrees : DayDefinitionRule
        object _18_Degrees : DayDefinitionRule

        object _72_Zmaniyos_Minutes : DayDefinitionRule
        object _90_Zmaniyos_Minutes : DayDefinitionRule
        object _96_Zmaniyos_Minutes : DayDefinitionRule
        object _120_Zmaniyos_Minutes : DayDefinitionRule

    }

    interface DayStartRule : ZmanCalculationRule {
        object DAWN : DayStartRule
        object SUNRISE : DayStartRule
    }

    interface DayEndRule : ZmanCalculationRule {
        object DUSK : DayEndRule
        object SUNSET : DayEndRule
    }

    interface MilLengthRule : ZmanCalculationRule {
        /**
         * @see ZmanAuthority.CHAVAS_YAIR
         * @see ZmanAuthority.DIVREI_MALKIEL
         * @see ZmanAuthority.MAHARIK
         * @see ZmanAuthority.HISACHDUS_HARABONIM
         * */
        object _15_Minutes : MilLengthRule

        /**
         * @see ZmanAuthority.RAAVAN (disputed - see [ComplexZmanimCalendar.alos60])
         * */
        object _18_Minutes : MilLengthRule
        object _22_5_Minutes : MilLengthRule
        object _24_Minutes : MilLengthRule
    }

    data class OpinionRule<T>(val opinion: ZmanCalculationMethod<T>) : ZmanCalculationRule
}