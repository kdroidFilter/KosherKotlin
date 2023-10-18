package com.kosherjava.zmanim.metadata

data class ZmanRelationship<T>(
    val subject: ZmanType,
    val calculation: ZmanCalculationMethod<T>,
    val relativeToZmanType: ZmanType? = null,
    val relativeToZman: ZmanDefinition? = null,
)
