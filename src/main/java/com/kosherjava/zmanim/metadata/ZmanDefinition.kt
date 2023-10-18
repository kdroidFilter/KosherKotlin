package com.kosherjava.zmanim.metadata

/**
 *
 * */
data class ZmanDefinition(
    val type: ZmanType,
    val mainCalculationMethodUsed: ZmanCalculationMethod<*>,
    val isElevationUsed: UsesElevation = UsesElevation.UNSPECIFIED,
    val supportingAuthorities: List<ZmanAuthority> = listOf()
) {

    enum class UsesElevation {
        IF_SET, NEVER, ALWAYS, UNSPECIFIED
    }
}