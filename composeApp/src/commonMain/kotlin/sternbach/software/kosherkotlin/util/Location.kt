package com.kosherjava.zmanim.util

import kotlinx.datetime.TimeZone

/**
 * Represents a coordinate on the globe. In contrast to [GeoLocation] which has an associated timezone and place name,
 * [Location] may not. This is to accommodate various platforms, which may not expose as rich information as [GeoLocation]
 * requires.
 *
 * @param timestamp epoch milliseconds when the location was last updated.
 * On some platforms (e.g. Kotlin/JS) this can be significantly delayed (I think e.g. if the user is offline, etc.)
 * compared to when the location flow is updated.
 * @param accuracy the margin of error in latitude and longitude. A smaller number means the [latitude] and [longitude]
 * are more accurate.
 * */
data class Location(
    val latitude: Double,
    val longitude: Double,
    val elevation: Double? = null,
    val accuracy: Double? = null,
    val timestamp: Long? = null,
    val tz: TimeZone? = null,
    val locationName: String? = null,
)