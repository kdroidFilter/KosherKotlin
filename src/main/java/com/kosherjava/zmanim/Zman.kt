package com.kosherjava.zmanim

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
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
): Comparable<Zman<T, A>> {
    /**
     * This class represents a zman that has a moment in which it occurs.
     * @param momentOfOccurrence null if zman never occurs or does not apply (e.g. time to say kiddush levana after time
     * has passed). Some zmanim can occasionally not occur (e.g. sunrise in the Arctic Circle).
     * */
    data class DateBased<T: ZmanOpinion<A>, A>(
        override val type: ZmanType,
        override val opinion: T,
        val momentOfOccurrence: Instant?
    ) : Zman<T, A>(type, opinion) {
        override fun compareTo(other: Zman<T, A>): Int {
            if(this === other) return 0
//            if(this.type != other.type) return this.type.compareTo(other.type)
            if(other is ValueBased<*, *>) return 1 //not sure why DateBased should go after ValueBased, but why are you comparing them?
            other as DateBased<*, *>
            return when {
                //null goes at the beginning
                this.momentOfOccurrence == null && other.momentOfOccurrence == null -> 0
                this.momentOfOccurrence == null && other.momentOfOccurrence != null -> -1
                this.momentOfOccurrence != null && other.momentOfOccurrence == null -> 1
                else -> this.momentOfOccurrence!!.compareTo(other.momentOfOccurrence!!)
            }
        }
    }

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
    ) : Zman<T, A>(type, opinion) {
        override fun compareTo(other: Zman<T, A>): Int {
            if(this === other) return 0
            if(this.type != other.type) return this.type.compareTo(other.type)
            if(other is DateBased<*, *>) return -1  //not sure why DateBased should go after ValueBased, but why are you comparing them?
            other as ValueBased<*, *>
            return when {
                duration.isInfinite() && other.duration.isInfinite() -> 0
                duration.isInfinite() && other.duration.isFinite() -> -1
                duration.isFinite()   && other.duration.isInfinite() -> 1
                else -> duration.compareTo(other.duration)
            }
        }
    }
    fun formatted(tz: TimeZone) = formatted(tz, opinion.format())
    fun formatted(tz: TimeZone, inEnglish: Boolean) = formatted(tz, opinion.format(inEnglish))
    private fun Int.pad() = toString().padStart(2,'0')
    fun formatted(tz: TimeZone, opinion: String) = opinion +
            if (this is ValueBased) this.duration.toString()
            else {
                (this as DateBased)
                    .momentOfOccurrence
                    ?.toLocalDateTime(tz)
                    ?.time
                    ?.let {
                        "${(if(it.hour <= 12) it.hour else it.hour - 12).pad()}:${it.minute.pad()}:${it.second.pad()} ${if (it.hour >= 12) "PM" else "AM"}"
                    }
                    ?: "N/A"
            }
}
