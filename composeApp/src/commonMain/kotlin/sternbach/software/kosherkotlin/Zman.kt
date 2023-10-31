package sternbach.software.kosherkotlin

import sternbach.software.kosherkotlin.metadata.ZmanDefinition
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration

/**
 * This class represents a halachic moment in time or duration of time.
 * Some zmanim, like [sha'os zmaniyos][ZmanType.SHAA_ZMANIS], don't have a moment when they occur. Rather, they carry a value.
 * Others, like [sunset][ZmanType.SHKIAH] happen at a specific moment in time.
 * @param T the type of value that this zman represents ([Instant], [Duration], etc.). This is the type of the [value] property.
 * */
sealed class Zman<T>(
    open val rules: ZmanDefinition,
    protected val computeValue: () -> T,
): Comparable<Zman<T>> {
    val value: T by lazy { computeValue() }
    /**
     * This class represents a zman that has a moment in which it occurs.
     * @param momentOfOccurrence null if zman never occurs or does not apply (e.g. time to say kiddush levana after time
     * has passed). Some zmanim can occasionally not occur (e.g. sunrise in the Arctic Circle) - see [AstronomicalCalendar] for more details.
     * */
    data class DateBased(
        override val rules: ZmanDefinition,
        private val computeInstant: () -> Instant?,
    ) : Zman<Instant?>(rules, computeInstant) {
        val momentOfOccurrence: Instant? by lazy { value }
        override fun compareTo(other: Zman<Instant?>): Int {
            if(this === other) return 0
//            if(this.type != other.type) return this.type.compareTo(other.type)
            other as DateBased
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
     * *shaah zmanis* base times such as [*shaah Zmanis GRA*][com.kosherjava.zmanim.ZmanimCalendar.shaahZmanisGra] or
     * [*shaah Zmanis 16.1˚*][com.kosherjava.zmanim.ComplexZmanimCalendar.shaahZmanis16Point1Degrees]).
     * */
    data class ValueBased(
        override val rules: ZmanDefinition,
        private val computeDuration: () -> Duration,
    ) : Zman<Duration>(rules, computeDuration) {
        val duration: Duration by lazy { value }

        override fun compareTo(other: Zman<Duration>): Int {
            if(this === other) return 0
//            if(this.type != other.type) return this.type.compareTo(other.type)
//            if(other is DateBased) return -1  //not sure why DateBased should go after ValueBased, but why are you comparing them?
            other as ValueBased
            return when {
                duration.isInfinite() && other.duration.isInfinite() -> 0
                duration.isInfinite() && other.duration.isFinite() -> -1
                duration.isFinite()   && other.duration.isInfinite() -> 1
                else -> duration.compareTo(other.duration)
            }
        }
    }
    fun formatted(tz: TimeZone) = formatted(tz, rules.toString())
    fun formatted(tz: TimeZone, inEnglish: Boolean) = formatted(tz, rules.toString()/*.format(inEnglish)*/)
    fun formatted(tz: TimeZone, opinion: String) = opinion +
            if (this is ValueBased) this.duration.toString()
            else {
                (this as DateBased)
                    .momentOfOccurrence
                    ?.toLocalDateTime(tz)
                    ?.time
                    ?.let {
                        "${(if(it.hour == 0) 12 else if(it.hour <= 12) it.hour else it.hour - 12).pad()}:${it.minute.pad()}:${it.second.pad()} ${if (it.hour < 12) "AM" else "PM"}"
                    }
                    ?: "N/A"
            }
    private fun Int.pad() = toString().padStart(2,'0')
}
