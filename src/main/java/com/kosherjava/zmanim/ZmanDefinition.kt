package com.kosherjava.zmanim

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
    val isElevationUsed: UsesElevation = UsesElevation.UNSPECIFIED,
    val supportingAuthorities: List<ZmanAuthority> = listOf(),
    val relationship: ZmanRelationship<*>? = null,
) {

    enum class UsesElevation {
        IF_SET, NEVER, ALWAYS, UNSPECIFIED
    }

    companion object {
        val empty = ZmanDefinition(ZmanType.MOLAD, null, UsesElevation.UNSPECIFIED)
    }
}