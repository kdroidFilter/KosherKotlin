package com.kosherjava.zmanim

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlin.time.Duration

/**
 * This class represents a halachic moment in time or duration of time.
 * Some zmanim, like [sha'os zmaniyos][ZmanType.SHAA_ZMANIS], don't have a moment when they occur. Rather, they carry a value.
 * Others, like [sunset][ZmanType.SHKIAH] happen at a specific moment in time.
 * @param T the type of zman opinion which this zman was calculated using. See [ZmanOpinion] for options.
 * */
sealed class Zman<T: ZmanOpinion<A>, A>(
    open val type: ZmanType,
    open val opinion: T
) {
    /**
     * This class represents a zman that has a moment in which it occurs.
     * @param momentOfOccurrence null if zman never occurs or does not apply (e.g. time to say kiddush levana after time
     * has passed). Some zmanim can occasionally not occur (e.g. sunrise in the Arctic Circle).
     * */
    data class DateBased<T: ZmanOpinion<A>, A>(
        override val type: ZmanType,
        override val opinion: T,
        val momentOfOccurrence: Instant?
    ) : Zman<T, A>(type, opinion)

    /**
     * This class represents a zman which simply carries a datetime-less value.
     *
     * The duration of the *zman* is a [temporal hour][com.kosherjava.zmanim.AstronomicalCalendar.temporalHour] (or the various
     * *shaah zmanis* base times such as [<em>shaah Zmanis GRA</em>][com.kosherjava.zmanim.ZmanimCalendar.shaahZmanisGra] or
     * [<em>shaah Zmanis 16.1˚</em>][com.kosherjava.zmanim.ComplexZmanimCalendar.shaahZmanis16Point1Degrees]).
     * */
    data class ValueBased<T: ZmanOpinion<A>, A>(
        override val type: ZmanType,
        override val opinion: T,
        val duration: Duration
    ) : Zman<T, A>(type, opinion)

    companion object {
        fun <T: ZmanOpinion<A>, A> Zman<T, A>.formatted() = formatted(opinion.format())
        fun <T: ZmanOpinion<A>, A> Zman<T, A>.formatted(inEnglish: Boolean) = formatted(opinion.format(inEnglish))
        private fun <T: ZmanOpinion<A>, A> Zman<T, A>.formatted(opinion: String) = opinion +
                if (this is ValueBased) this.duration.toString()
                else {
                    (this as DateBased)
                        .momentOfOccurrence
                        ?.toString()
                        ?.let {
                            LocalDateTime
                                .parse(it)
                                .time
                                .let {
                                    "${it.hour}:${it.minute}:${it.second} ${if (it.hour >= 12) "PM" else "AM"}"
                                }
                        }
                        ?: "N/A"
                }
    }
}
