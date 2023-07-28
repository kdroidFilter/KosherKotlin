package com.kosherjava.zmanim.hebrewcalendar

import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.daysUntil
import kotlinx.datetime.minus

/**
 * A class representing a Hebrew local date. Hebrew analog to [java.time.LocalDate] or [kotlinx.datetime.LocalDate]
 * Although the Hebrew year actually starts in [HebrewMonth.TISHREI], the choice was made to assign [HebrewMonth.NISSAN]
 * with a value of 1. This may be because the year is colloquially said to start in Nissan (in accordance with the
 * opinion Maseches Rosh Hashana (TODO include source) that the world was created in Nissan).
 * @param year the Hebrew year Ano Mundi, e.g. 5783 (2023 Gregorian)
 * @param dayOfMonth the day of the month. This is a value between 1 and 30. Leap years can change the upper-bound of this number.
 * @param month the Hebrew month. This is a value between 1 and 13. The value of 13 represents Adar II on a leap year.
 * */
data class HebrewLocalDate(
    val year: Int,
    val month: HebrewMonth,
    val dayOfMonth: Int,
) {
    init {
        require(year > 0) { "year must be positive" }
        require(dayOfMonth in 1..30) { "dayOfMonth must be between 1 and 30" }
        require(month in HebrewMonth.NISSAN..HebrewMonth.ADAR_II) { "month must be between NISSAN and ADAR_II" }
    }

    fun withDayOfMonth(dayOfMonth: Int) = copy(dayOfMonth = dayOfMonth)
    fun withMonth(month: HebrewMonth) = copy(month = month)
    fun withYear(year: Int) = copy(year = year)

    /**
     * Returns the absolute date of Jewish date. ND+ER
     *
     * @param year
     * the Jewish year. The year can't be negative
     * @param month
     * the Jewish month starting with Nisan. Nisan expects a value of 1 etc till Adar with a value of 12. For
     * a leap year, 13 will be the expected value for Adar II. Use the constants [JewishDate.NISSAN]
     * etc.
     * @param dayOfMonth
     * the Jewish day of month. valid values are 1-30. If the day of month is set to 30 for a month that only
     * has 29 days, the day will be set as 29.
     * @return the absolute date of the Jewish date.
     */
    fun toJewishEpochDays(): Int =
        // add elapsed days this year + Days in prior years + Days elapsed before absolute year 1
        JewishDate.getDaysSinceStartOfJewishYear(year, month, dayOfMonth) +
                JewishDate.getJewishCalendarElapsedDays(year) +
                JEWISH_EPOCH

    fun isJewishLeapYear(year: Int): Boolean = (7 * year + 1) % 19 < 7

    /**
     * Computes the Gregorian [LocalDate] of a given [HebrewLocalDate]
     */
    fun toLocalDateGregorian(): LocalDate {
        return LocalDate(1,1,1)
    }

    companion object {

        /**
         * Computes the [HebrewLocalDate] of a given Gregorian date.
         * @param this the gregorian date to convert to a [HebrewLocalDate]
         */
        fun LocalDate.toHebrewDate(): HebrewLocalDate {
            // Approximation from below
            fun computeEpoch(fromDate: LocalDate) = fromDate.daysUntil(HebrewLocalDate.JEWISH_EPOCH_LOCAL_DATE)
            val date = this
            var daysSinceEpoch = computeEpoch(this)
            var jewishYear = daysSinceEpoch / 366
            // Search forward for year from the approximation
            while (daysSinceEpoch >= HebrewLocalDate(jewishYear + 1, HebrewMonth.TISHREI, 1).toJewishEpochDays()) {
                jewishYear++
                daysSinceEpoch = computeEpoch(date)
            }
            // Search forward for month from either Tishri or Nisan.
            var jewishMonth =
                if (daysSinceEpoch < HebrewLocalDate(
                        jewishYear,
                        HebrewMonth.NISSAN,
                        1
                    ).toJewishEpochDays()
                ) HebrewMonth.TISHREI /*Start at Tishri*/
                else HebrewMonth.NISSAN /*Start at Nisan*/
            while (daysSinceEpoch > HebrewLocalDate(
                    jewishYear,
                    jewishMonth,
                    jewishMonth.daysInJewishMonthForYear(jewishYear)
                ).toJewishEpochDays()
            ) jewishMonth = jewishMonth.nextMonth
            // Calculate the day by subtraction
            val jewishDay = daysSinceEpoch - HebrewLocalDate(jewishYear, jewishMonth, 1).toJewishEpochDays() + 1
            return HebrewLocalDate(jewishYear, jewishMonth, jewishDay)
        }

        /**
         * the Jewish epoch using the RD (Rata Die/Fixed Date or Reingold Dershowitz) day used in Calendrical Calculations.
         * Day 1 is January 1, 0001 Gregorian
         */
        const val JEWISH_EPOCH = -1_373_429
        val JAN_01_0001 = LocalDate(1, 1, 1)
        val JEWISH_EPOCH_LOCAL_DATE = JAN_01_0001.minus(DatePeriod(days = 1_373_429))

    }
}
