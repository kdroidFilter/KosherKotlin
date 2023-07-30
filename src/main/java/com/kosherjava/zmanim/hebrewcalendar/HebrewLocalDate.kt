package com.kosherjava.zmanim.hebrewcalendar

import com.kosherjava.zmanim.hebrewcalendar.JewishDate.Companion.daysInJewishYear
import com.kosherjava.zmanim.hebrewcalendar.JewishDate.Companion.isJewishLeapYear
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus

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
): Comparable<HebrewLocalDate> {
    init {
//        require(year > 0) { "year must be positive: $year" } // leaving this out to make the calendar proleptic
        require(dayOfMonth in 1..30) { "dayOfMonth must be between 1 and 30: $dayOfMonth" }
    }


    //-----------------------------------------------------------------------
    /**
     * Compares this date to another date.
     *
     *
     * The comparison is primarily based on the date, from earliest to latest.
     * It is "consistent with equals", as defined by [Comparable].
     *
     * @param other  the other date to compare to
     * @return the comparator value, negative if less, positive if greater
     */

    override fun compareTo(otherDate: HebrewLocalDate): Int {
        var cmp = year - otherDate.year
        if (cmp == 0) {
            cmp = month.value - otherDate.month.value
            if (cmp == 0) {
                cmp = dayOfMonth - otherDate.dayOfMonth
            }
        }
        return cmp
    }

    /**
     * Returns this object with the [dayOfMonth] set to the given [newDayOfMonth].
     *
     * **Note:** This method does not change the month or year (e.g. passing in a value of 32 does not increment the month).
     * */
    fun withDayOfMonth(newDayOfMonth: Int) = copy(dayOfMonth = newDayOfMonth)

    /**
     * Returns this object with the [month] set to the given [newMonth].
     *
     * **Note:** This method does not change the day or year (e.g. if the [dayOfMonth] is 30 and [newMonth] only has 29 days, [dayOfMonth] will remain 30).
     * */
    fun withMonth(newMonth: HebrewMonth) = copy(month = newMonth)

    /**
     *
     * Returns this object with the [year] set to the given [newYear].
     *
     * **Note:** This method does not change the day or month (e.g. if the [month] was the leap month [HebrewMonth.ADAR_II], and [newYear] is not a leap year, the month will remain [HebrewMonth.ADAR_II]).
     * */
    fun withYear(newYear: Int) = copy(year = newYear)

    /**
     * Returns the absolute date of Jewish date. ND+ER
     *
     * @param year
     * the Jewish year. The year can't be negative
     * @param month
     * the Jewish month starting with Nisan. Nisan expects a value of 1 etc till Adar with a value of 12. For
     * a leap year, 13 will be the expected value for Adar II. Use the constants [HebrewMonth.NISSAN]
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

    val isJewishLeapYear get() = year.isJewishLeapYear

    /**
     * Computes the Gregorian [LocalDate] of a given [HebrewLocalDate]
     */
    fun toLocalDateGregorian(): LocalDate = toPairOfHebrewAndGregorianLocalDate(targetHebrewDate = this).second

    companion object {

        /**
         * Computes the [HebrewLocalDate] of a given Gregorian date.
         * @param this the gregorian date to convert to a [HebrewLocalDate]
         */
        fun LocalDate.toHebrewDate(): HebrewLocalDate = toPairOfHebrewAndGregorianLocalDate(targetGregorianDate = this).first
        /**
         * Computes the [HebrewLocalDate] of a given Gregorian date.
         * @param this the gregorian date to convert to a [HebrewLocalDate]
         */
        internal fun toPairOfHebrewAndGregorianLocalDate(
            targetGregorianDate: LocalDate? = null,
            targetHebrewDate: HebrewLocalDate? = null,
        ): Pair<HebrewLocalDate, LocalDate> {
            /*
             * The algorithm for converting a Gregorian date to a hebrew date is as follows:
             * Let targetGregorianDate = date that client wants to convert to hebrew date.
             * Let currentGregorianDate = the intermediate representation of the gregorian date which will be mutated until equal to targetGregorianDate
             * Let currentHebrewDate = the intermediate representation of the hebrew date which will be mutated until currentGregorianDate is equal to targetGregorianDate
             * Let hebrewStartingPoint = the beginning of the inception of the hebrew calendar
             * Let gregorianStartingPoint = the known corresponding Gregorian date to hebrewStartingPoint.
             * 1. Init variables:
             *   a. hebrewStartingPoint = Tishrei 1, 1
             *   b. gregorianStartingPoint = September 7, 3761 B.C.E.
             *   c. currentHebrewDate = hebrewStartingPoint
             *   d. currentGregorianDate = gregorianStartingPoint
             * 2. If gregorianStartingPoint is equal to targetGregorianDate: return hebrewStartingPoint.
             * 3. If gregorianStartingPoint is before targetGregorianDate:
             *   a. while(currentGregorianDate.year != targetGregorianDate.year):
             *     i. Let numDaysInHebrewYear = getNumDaysInHebrewYear(currentHebrewDate.year) //accounts for leap years
             *    ii. Let nextHebrewNewYear = currentGregorianDate.plusDays(numDaysInHebrewYear)
             *   iii. if(nextHebrewNewYear is after targetGregorianDate): break //we've overshot the target because it is between now and the next new year
             *    iv. else:
             *       1. currentGregorianDate = nextHebrewNewYear
             *       2. currentHebrewDate.year += 1
             *   b. while(currentGregorianDate.month != targetGregorianDate.month):
             *     i. Let numDaysInHebrewMonth = getNumDaysInHebrewMonth(currentHebrewDate.month)
             *    ii. Let nextHebrewNewMonth = currentGregorianDate.plusDays(numDaysInHebrewMonth)
             *   iii. if(nextHebrewNewMonth is after targetGregorianDate): break //we've overshot the target because it is between now and the next first-of-the-month
             *    iv. else:
             *       1. currentGregorianDate = nextHebrewNewYear
             *       2. currentHebrewDate.month += 1
             *   // currentHebrewDate is at the first day of the month of targetGregorianDate's hebrew equivalent.
             *   c. Let numDaysLeft = targetGregorianDate.dayOfMonth - currentGregorianDate.dayOfMonth
             *   d. let numDaysInHebrewMonth = getNumDaysInHebrewMonth(currentHebrewDate.month)
             *   e. if(numDaysLeft <= numDaysInHebrewMonth) currentHebrewDate.dayOfMonth += numDaysLeft //currentHebrewDate.dayOfMonth was 1
             *   f. else: //target is next month (e.g. numDaysLeft = 30 but numDaysInHebrewMonth = 29
             *     i. currentHebrewDate.month += 1
             *    ii. currentHebrewDate.dayOfMonth = numDaysLeft - numDaysInHebrewMonth + 1 //+1 because we did not start at 0, so we need to add the first day
             * 4. If gregorianStartingPoint is after targetGregorianDate: //wants to compute the hebrew date of a Gregorian date prior to the inception of the Hebrew calendar
             *   i. Repeat step 3, but subtract instead of add.
             * */
            if (STARTING_DATE_GREGORIAN == targetGregorianDate || STARTING_DATE_HEBREW == targetHebrewDate) return STARTING_DATE_HEBREW to STARTING_DATE_GREGORIAN
            require((targetGregorianDate != null) xor (targetHebrewDate != null)) { "Target date must not be null, and only one target can be chosen." }
            var currentHebrewDate = STARTING_DATE_HEBREW
            var currentGregorianDate = STARTING_DATE_GREGORIAN

            if(targetGregorianDate != null && STARTING_DATE_GREGORIAN > targetGregorianDate) { //STARTING_DATE_GREGORIAN > this - i.e. want to compute the hebrew date of a Gregorian date prior to the inception of the hebrew calendar
                throw IllegalArgumentException("Cannot compute dates before the inception of the Hebrew calendar: $STARTING_DATE_GREGORIAN Gregorian/$STARTING_DATE_HEBREW. If you have a reason you want to do this, please submit a feature request on GitHub.")
            }
            while (
                if(targetGregorianDate != null) currentGregorianDate.year != targetGregorianDate.year
                else currentHebrewDate.year != targetHebrewDate!!.year
            ) {
                val daysInYear = getNumDaysInHebrewYear(currentHebrewDate.year) //accounts for leap years
                println("Days in year ${currentHebrewDate.year}: $daysInYear")
                println("Before adding days: $currentGregorianDate")
                val newStartOfHebrewYear = currentGregorianDate.plus(DatePeriod(days = daysInYear))
                val newHebrewYear = currentHebrewDate.withYear(currentHebrewDate.year + 1)
                if (
                    if(targetGregorianDate != null) newStartOfHebrewYear > targetGregorianDate
                    else newHebrewYear > targetHebrewDate!!
                ) break //overshot, don't keep adding. Could have overshot because the date could be between the two Rosh Hashanahs.
                currentGregorianDate = newStartOfHebrewYear
                println("After adding days : $currentGregorianDate")
                println("Before adding year: $currentHebrewDate")
                currentHebrewDate = newHebrewYear
                println("After adding year : $currentHebrewDate")
            }
            // we have the year right, now we need to get the month right
            println("Current gregorian: $currentGregorianDate")
            println("Current hebrew: $currentHebrewDate")
            while (
                if(targetGregorianDate != null) currentGregorianDate.month != targetGregorianDate.month
                else currentHebrewDate.month != targetHebrewDate!!.month
            ) {
                val daysInMonth = getNumDaysInHebrewMonth(
                    currentHebrewDate.month,
                    currentHebrewDate.year
                )
                println("Days in month ${currentHebrewDate.month}: $daysInMonth")
                println("Before adding days: $currentGregorianDate")
                val newStartOfHebrewMonth = currentGregorianDate.plus(
                    DatePeriod(
                        days = daysInMonth
                    )
                )
                val newHebrewDate = currentHebrewDate.withMonth(currentHebrewDate.month.nextMonth)

                if (
                    if(targetGregorianDate != null) newStartOfHebrewMonth > targetGregorianDate
                    else newHebrewDate > targetHebrewDate!!
                ) {
                    println("New start of hebrew month $newStartOfHebrewMonth is after $targetGregorianDate")
                    break
                } //overshot, don't keep adding. Could have overshot because the date could be between the two months
                currentGregorianDate = newStartOfHebrewMonth

                println("After adding days : $currentGregorianDate")
                println("Before adding month: $currentHebrewDate")
                currentHebrewDate = newHebrewDate
                println("After adding month: $currentHebrewDate")
            }
            //gregorian month is right. Now we need to get the day right.
            if (currentGregorianDate == targetGregorianDate) return currentHebrewDate to currentGregorianDate // if day is already right, return

            println("Current gregorian: $currentGregorianDate")
            println("Current hebrew: $currentHebrewDate")
            // currentHebrewDate is at the first day of the month of targetGregorianDate's hebrew equivalent month.
            val numDaysLeftToAdd =
                if(targetGregorianDate != null) targetGregorianDate.dayOfMonth - currentGregorianDate.dayOfMonth
                else targetHebrewDate!!.dayOfMonth - currentHebrewDate.dayOfMonth
            val numDaysInHebrewMonth = getNumDaysInHebrewMonth(currentHebrewDate.month, currentHebrewDate.year)
            println("Num days left: $numDaysLeftToAdd, numDays in hebrew month: $numDaysInHebrewMonth")
            currentHebrewDate = if (numDaysLeftToAdd
                /*TODO what if this is equal to numDaysInHebrewMonth? 1 (current day) + 30 (num days) = 31...*/
                <= numDaysInHebrewMonth
            ) currentHebrewDate.withDayOfMonth(numDaysLeftToAdd + 1/*started on day 1, so need to add that*/)
            else //target is next month (e.g. numDaysLeft = 30 but numDaysInHebrewMonth = 29)
                currentHebrewDate
                    .withMonth(currentHebrewDate.month.nextMonth)
                    .withDayOfMonth(numDaysLeftToAdd - numDaysInHebrewMonth + 1/*started on day 1, so need to add that*/)
            return currentHebrewDate to currentGregorianDate
        }

        fun getNumDaysInHebrewYear(year: Int): Int = year.daysInJewishYear
        fun getNumDaysInHebrewMonth(month: HebrewMonth, year: Int): Int = JewishDate.getDaysInJewishMonth(month, year)

        /**
         * the Jewish epoch using the RD (Rata Die/Fixed Date or Reingold Dershowitz) day used in Calendrical Calculations.
         * Day 1 is January 1, 0001 Gregorian
         */
        const val JEWISH_EPOCH = -1_373_429

        /**
         * The start of the Hebrew calendar. Used as a reference point for converting between
         * Gregorian and Hebrew dates.
         * */
        internal val STARTING_DATE_HEBREW = HebrewLocalDate(1, HebrewMonth.TISHREI, 1)

        /**
         * @see STARTING_DATE_HEBREW; Does not account for The Gregorian Reformation.
         * */
        internal val STARTING_DATE_GREGORIAN = LocalDate(-3761, kotlinx.datetime.Month.SEPTEMBER, 7)
    }
}
