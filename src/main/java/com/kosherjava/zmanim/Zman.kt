package com.kosherjava.zmanim

import kotlinx.datetime.Instant
import kotlin.time.Duration

/**
 * This class represents a halachic moment in time or duration of time.
 * Some zmanim, like [sha'os zmaniyos][ZmanType.SHAA_ZMANIS], don't have a moment when they occur. Rather, they carry a value.
 * Others, like [sunset][ZmanType.SHKIAH] happen at a specific moment in time.
 * */
sealed class Zman(open val type: ZmanType) {
    /**
     * This class represents a zman that has a moment in which it occurs.
     * @param momentOfOccurrence null if zman never occurs or does not apply (e.g. time to say kiddush levana after time
     * has passed). Some zmanim can occasionally not occur (e.g. sunrise in the Arctic Circle).
     * */
    data class DateBased(
        override val type: ZmanType,
        val momentOfOccurrence: Instant?
    ): Zman(type)
    /**
     * This class represents a zman which simply carries a datetime-less value.
     *
     * The duration of the *zman* is a [temporal hour][com.kosherjava.zmanim.AstronomicalCalendar.temporalHour] (or the various
     * *shaah zmanis* base times such as [<em>shaah Zmanis GRA</em>][com.kosherjava.zmanim.ZmanimCalendar.shaahZmanisGra] or
     * [<em>shaah Zmanis 16.1&amp;deg;</em>][com.kosherjava.zmanim.ComplexZmanimCalendar.shaahZmanis16Point1Degrees]).
     * */
    data class ValueBased(
        override val type: ZmanType,
        val duration: Duration
    ): Zman(type)
}
