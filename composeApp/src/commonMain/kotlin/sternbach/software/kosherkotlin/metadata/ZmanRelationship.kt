package com.kosherjava.zmanim.metadata
/**
 * A [ZmanRelationship] is a relationship between two [ZmanType]s, dictating when [subject] occurs relative to [relativeToZman].
 * ZmanType.TZAIS  occurs 45.minutes          after  ZmanType.SHKIAH
 * ZmanType.SHKIAH occurs 45.minutes          before ZmanType.TZAIS
 *
 * ZmanType.SHKIAH occurs 45.minutes.zmaniyos before ZmanType.TZAIS
 * ZmanType.SHKIAH occurs 16.1F.degrees       before ZmanType.TZAIS
 *
 * ZmanType.BAIN_HASHMASHOS occurs 13.5.minutes before tzaisGeonim7Point083Degrees.rules
 *
 * */
data class ZmanRelationship<T>(
    val subject: ZmanType,
    val calculation: ZmanCalculationMethod<T>,
    val relativeToZmanType: ZmanType? = null,
    val relativeToZman: ZmanDefinition? = null,
)
