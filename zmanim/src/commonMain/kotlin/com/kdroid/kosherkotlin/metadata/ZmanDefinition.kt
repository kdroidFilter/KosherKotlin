package com.kdroid.kosherkotlin.metadata

/**
 *
 * */
data class ZmanDefinition(
    val type: ZmanType,
    val calculationMethod: ZmanCalculationMethod,
    val isElevationUsed: UsesElevation = UsesElevation.UNSPECIFIED,
    val supportingAuthorities: List<ZmanAuthority> = listOf()
)