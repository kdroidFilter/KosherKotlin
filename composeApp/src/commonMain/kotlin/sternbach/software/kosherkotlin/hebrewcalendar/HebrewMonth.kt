package sternbach.software.kosherkotlin.hebrewcalendar;

import sternbach.software.kosherkotlin.hebrewcalendar.JewishDate.Companion.isJewishLeapYear
import kotlin.math.absoluteValue

enum class HebrewMonth(val value: Int) : Comparable<HebrewMonth> {
    /**
     * Value of the month field indicating Nissan, the first numeric month of the year in the Jewish calendar. With the
     * year starting at [TISHREI], it would actually be the 7th (or 8th in a [leap][Int.isJewishLeapYear]) month of the year.
     */
    NISSAN(1),

    /**
     * Value of the month field indicating Iyar, the second numeric month of the year in the Jewish calendar. With the
     * year starting at [TISHREI], it would actually be the 8th (or 9th in a [leap][Int.isJewishLeapYear]) month of the year.
     */
    IYAR(2),

    /**
     * Value of the month field indicating Sivan, the third numeric month of the year in the Jewish calendar. With the
     * year starting at [TISHREI], it would actually be the 9th (or 10th in a [leap][Int.isJewishLeapYear]) month of the year.
     */
    SIVAN(3),

    /**
     * Value of the month field indicating Tammuz, the fourth numeric month of the year in the Jewish calendar. With the
     * year starting at [TISHREI], it would actually be the 10th (or 11th in a [leap][Int.isJewishLeapYear]) month of the year.
     */
    TAMMUZ(4),

    /**
     * Value of the month field indicating Av, the fifth numeric month of the year in the Jewish calendar. With the year
     * starting at [TISHREI], it would actually be the 11th (or 12th in a [leap year][Int.isJewishLeapYear])
     * month of the year.
     */
    AV(5),

    /**
     * Value of the month field indicating Elul, the sixth numeric month of the year in the Jewish calendar. With the
     * year starting at [TISHREI], it would actually be the 12th (or 13th in a [leap][Int.isJewishLeapYear]) month of the year.
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
     * Value of the month field indicating Adar (or Adar I in a [leap year][Int.isJewishLeapYear]), the twelfth
     * numeric month of the year in the Jewish calendar. With the year starting at [TISHREI], it would actually
     * be the 6th month of the year.
     */
    ADAR(12),

    /**
     * Value of the month field indicating Adar II, the leap (intercalary or embolismic) thirteenth (Undecimber) numeric
     * month of the year added in Jewish [leap year][Int.isJewishLeapYear]). The leap years are years 3, 6, 8, 11,
     * 14, 17 and 19 of a 19 year cycle. With the year starting at [TISHREI], it would actually be the 7th month
     * of the year.
     */
    ADAR_II(13);

    /**
     * Returns the [HebrewMonth] which came before [this] month, with the first being [NISSAN] and last being [ADAR_II].
     * If [this] is the first month, this will wrap around and return the last month (which would actually be part of the previous year).
     * **Note:** this does not account for leap years and the presence of [ADAR_II] - it blindly gets the next month.
     * In order to properly determine the previous month accounting for leap years and without wrapping, use [getPreviousMonthInYear].
     * */
    val previousMonth get() = getMonthForValue(value - 1)

    /**
     * Returns the [HebrewMonth] which follows [this] month, with the first being [NISSAN] and last being [ADAR_II].
     * If [this] is the last month, this will wrap around and return the first month (which would actually be part of the next year).
     * **Note:** this does not account for leap years and the presence of [ADAR_II] - it blindly gets the next month.
     * In order to properly determine the next month accounting for leap years and without wrapping, use [getNextMonthInYear].
     * */
    val nextMonth get() = getMonthForValue(value + 1)

    fun isLastMonthInYear(jewishYear: Long, tishreiBased: Boolean = true) =
        if(tishreiBased) this == ELUL
        else
            if(jewishYear.isJewishLeapYear) this == ADAR_II
            else this == ADAR

    fun isFirstMonthInYear(tishreiBased: Boolean = true) =
        tishreiBased && this == TISHREI || this == NISSAN

    /**
     * Returns the next month in the [jewishYear] (with [TISHREI] being the first if [tishreiBased] is true,
     * otherwise [NISSAN]), or null if [this] is the last month of the year.
     * This method will not return [ADAR_II] if [jewishYear] is not a leap year.
     * @return next jewish month, or null if [this] is the last jewish month of [jewishYear]
     * */
    fun getNextMonthInYear(jewishYear: Long, tishreiBased: Boolean = true) =
        if (this.isLastMonthInYear(
                jewishYear,
                tishreiBased
            )
        ) null /*get next month after last month?! we would spill over to the next year*/
        else
            if (this == ADAR)
                if(jewishYear.isJewishLeapYear) ADAR_II
                else NISSAN
            else nextMonth

    /**
     * Returns the number of days in this month for the given [jewishYear].
     * */
    fun getNumDaysInMonthForYear(jewishYear: Long) = HebrewLocalDate.getNumDaysInHebrewMonth(this, jewishYear)

    fun getTishreiBasedValueInYear(jewishYear: Long) = getTishreiBasedValue(value, jewishYear)
    fun toTishreiBasedMonthInYear(jewishYear: Int) = getTishreiBasedMonth(this, jewishYear)

    /**
     * Returns the previous month in the [jewishYear] (with [TISHREI] being the first if [tishreiBased] is true,
     * otherwise [NISSAN]), or null if [this] is the first month of the year.
     * This method will not return [ADAR_II] if [jewishYear] is not a leap year.
     * @return next jewish month, or null if [this] is the first jewish month of the year.
     * */
    fun getPreviousMonthInYear(tishreiBased: Boolean = true) =
        if (this.isFirstMonthInYear(tishreiBased)) null /*get previous month before first month?! we would spill over to the previous year*/
        else previousMonth

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

        /**
         * Converts the [NISSAN] based constants used by this class to numeric month starting from
         * [TISHREI]. This is required for Molad claculations.
         *
         * @param jewishYear
         * The Jewish year
         * @param this@toTishreiBasedMonthValue
         * The Jewish Month
         * @return the Jewish month of the year starting with Tishrei
         */
        fun getTishreiBasedValue(nissanBasedValue: Int, jewishYear: Int): Int =
            getTishreiBasedValue(nissanBasedValue, jewishYear.toLong())
        fun getTishreiBasedValue(nissanBasedValue: Int, jewishYear: Long): Int =
            1 +
                    if(jewishYear.isJewishLeapYear) (nissanBasedValue + 6) % 13
                    else (nissanBasedValue + 5) % 12
        /**
         * Interpolates this [month]
         * */
        fun getTishreiBasedMonth(month: HebrewMonth, jewishYear: Int) = getTishreiBasedMonth(month, jewishYear.toLong())
        fun getTishreiBasedMonth(month: HebrewMonth, jewishYear: Long) = getMonthForValue(getTishreiBasedValue(month.value, jewishYear))
    }
}