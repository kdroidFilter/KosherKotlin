package com.kosherjava.zmanim.hebrewcalendar;

import com.kosherjava.zmanim.hebrewcalendar.JewishDate.Companion.isCheshvanLong
import com.kosherjava.zmanim.hebrewcalendar.JewishDate.Companion.isJewishLeapYear
import com.kosherjava.zmanim.hebrewcalendar.JewishDate.Companion.isKislevShort
import kotlin.math.absoluteValue

enum class HebrewMonth(val value: Int) : Comparable<HebrewMonth> {
    /**
     * Value of the month field indicating Nissan, the first numeric month of the year in the Jewish calendar. With the
     * year starting at [TISHREI], it would actually be the 7th (or 8th in a [leap][isJewishLeapYear]) month of the year.
     */
    NISSAN(1),

    /**
     * Value of the month field indicating Iyar, the second numeric month of the year in the Jewish calendar. With the
     * year starting at [TISHREI], it would actually be the 8th (or 9th in a [leap][isJewishLeapYear]) month of the year.
     */
    IYAR(2),

    /**
     * Value of the month field indicating Sivan, the third numeric month of the year in the Jewish calendar. With the
     * year starting at [TISHREI], it would actually be the 9th (or 10th in a [leap][isJewishLeapYear]) month of the year.
     */
    SIVAN(3),

    /**
     * Value of the month field indicating Tammuz, the fourth numeric month of the year in the Jewish calendar. With the
     * year starting at [TISHREI], it would actually be the 10th (or 11th in a [leap][isJewishLeapYear]) month of the year.
     */
    TAMMUZ(4),

    /**
     * Value of the month field indicating Av, the fifth numeric month of the year in the Jewish calendar. With the year
     * starting at [TISHREI], it would actually be the 11th (or 12th in a [leap year][isJewishLeapYear])
     * month of the year.
     */
    AV(5),

    /**
     * Value of the month field indicating Elul, the sixth numeric month of the year in the Jewish calendar. With the
     * year starting at [TISHREI], it would actually be the 12th (or 13th in a [leap][isJewishLeapYear]) month of the year.
     */
    ELUL(6),

    /**
     * Value of the month field indicating Tishrei, the seventh numeric month of the year in the Jewish calendar. With
     * the year starting at this month, it would actually be the 1st month of the year.
     */
    TISHREI(7),

    /**
     * Value of the month field indicating Cheshvan/marcheshvan, the eighth numeric month of the year in the Jewish
     * calendar. With the year starting at [TISHREI], it would actually be the 2nd month of the year.
     */
    CHESHVAN(8),

    /**
     * Value of the month field indicating Kislev, the ninth numeric month of the year in the Jewish calendar. With the
     * year starting at [TISHREI], it would actually be the 3rd month of the year.
     */
    KISLEV(9),

    /**
     * Value of the month field indicating Teves, the tenth numeric month of the year in the Jewish calendar. With the
     * year starting at [TISHREI], it would actually be the 4th month of the year.
     */
    TEVES(10),

    /**
     * Value of the month field indicating Shevat, the eleventh numeric month of the year in the Jewish calendar. With
     * the year starting at [TISHREI], it would actually be the 5th month of the year.
     */
    SHEVAT(11),

    /**
     * Value of the month field indicating Adar (or Adar I in a [leap year][isJewishLeapYear]), the twelfth
     * numeric month of the year in the Jewish calendar. With the year starting at [TISHREI], it would actually
     * be the 6th month of the year.
     */
    ADAR(12),

    /**
     * Value of the month field indicating Adar II, the leap (intercalary or embolismic) thirteenth (Undecimber) numeric
     * month of the year added in Jewish [leap year][isJewishLeapYear]). The leap years are years 3, 6, 8, 11,
     * 14, 17 and 19 of a 19 year cycle. With the year starting at [TISHREI], it would actually be the 7th month
     * of the year.
     */
    ADAR_II(13);

    /**
     * Returns the HebrewMonth which came before [this] month, with the first being [NISSAN] and last being [ADAR_II].
     * If [this] is the first month, this will wrap around and return the last month.
     * */
    val previousMonth get() = getMonthForValue(value - 1)

    /**
     * Returns the HebrewMonth which follows [this] month, with the first being [NISSAN] and last being [ADAR_II].
     * If [this] is the last month, this will wrap around and return the first month.
     * */
    val nextMonth get() = getMonthForValue(value + 1)


    /**
     * Converts the [NISSAN] based constants used by this class to numeric month starting from
     * [TISHREI]. This is required for Molad claculations.
     *
     * @param year
     * The Jewish year
     * @param this@toTishreiBasedMonthValue
     * The Jewish Month
     * @return the Jewish month of the year starting with Tishrei
     */
    fun toTishreiBasedMonthValue(year: Int): Int {
        val isLeapYear = year.isJewishLeapYear
        return (value + (if (isLeapYear) 6 else 5)) % (if (isLeapYear) 13 else 12) + 1
    }
    fun daysInJewishMonthForYear(year: Int): Int {
        return when(this)  {
            AV -> 29
            TISHREI, SHEVAT, ADAR_II -> 30
            NISSAN, IYAR, SIVAN, TAMMUZ, ELUL -> 29
            TEVES, ADAR -> if (isJewishLeapYear(year)) 30 else 29
            KISLEV -> if (year.isKislevShort) 29 else 30
            else -> if(year.isCheshvanLong) 30 else 29
        }
    }

    fun isJewishLeapYear(year: Int): Boolean = (7 * year + 1) % 19 < 7

    infix fun until(other: HebrewMonth): HebrewMonthRange = HebrewMonthRange(this, other.previousMonth)
    operator fun rangeTo(other: HebrewMonth): HebrewMonthRange = HebrewMonthRange(this, other)

    class HebrewMonthRange(
        override val start: HebrewMonth,
        override val endInclusive: HebrewMonth
    ) : ClosedRange<HebrewMonth>, Iterable<HebrewMonth> {
        override fun iterator(): Iterator<HebrewMonth> = object : Iterator<HebrewMonth> {
            private var next = start
            private var hasNext = start <= endInclusive
            override fun hasNext(): Boolean = hasNext
            override fun next(): HebrewMonth {
                val value = next
                if (value == endInclusive) {
                    hasNext = false
                } else {
                    next = value.nextMonth
                }
                return value
            }
        }
    }

    companion object {
        /**
         * Returns the HebrewMonth for the given value, with 1 being [NISSAN] and 13 being [ADAR_II].
         * @param value from 1 to 13. Values outside this range will be wrapped.
         * */
        fun getMonthForValue(value: Int): HebrewMonth {
            val values = values()
            return values[((value - 1) % values.size).absoluteValue]
        }
    }
}