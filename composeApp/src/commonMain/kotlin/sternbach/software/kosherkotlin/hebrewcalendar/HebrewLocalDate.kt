package sternbach.software.kosherkotlin.hebrewcalendar

import sternbach.software.kosherkotlin.hebrewcalendar.JewishDate.Companion.daysInJewishYear
import sternbach.software.kosherkotlin.hebrewcalendar.JewishDate.Companion.isJewishLeapYear
import kotlinx.datetime.*
import kotlin.time.Duration.Companion.days

/**
 * A class representing a Hebrew local date. Hebrew analog to [java.time.LocalDate] or [kotlinx.datetime.LocalDate]
 * Although the Hebrew year actually starts in [HebrewMonth.TISHREI], the choice was made to assign [HebrewMonth.NISSAN]
 * with a value of 1. This may be because the year is colloquially said to start in Nissan (in accordance with the
 * opinion Maseches Rosh Hashana (TODO include source) that the world was created in Nissan).
 * **Note:** this class considers the Gregorian calendar to start at year 0, in accordance with ISO-8601.
 * @param year the Hebrew year Ano Mundi, e.g. 5783 (2023 Gregorian)
 * @param dayOfMonth the day of the month. This is a value between 1 and 30. Leap years can change the upper-bound of this number.
 * @param month the Hebrew month. This is a value between 1 and 13. The value of 13 represents Adar II on a leap year.
 * */
data class HebrewLocalDate(
    val year: Long,
    val month: HebrewMonth,
    val dayOfMonth: Int
) : Comparable<HebrewLocalDate> {
    init {
        require(year != 0L) { "year must not be 0 - year was skipped" }
//        require(year > 0) { "year must be positive: $year" } // leaving this out to make the calendar proleptic
        require(dayOfMonth in 1..30) { "dayOfMonth must be between 1 and 30: $dayOfMonth" }
        if(month == HebrewMonth.ADAR_II) require(year.isJewishLeapYear) { "$year was not leap year - month cannot be set to $month" }
        val daysInJewishMonth = JewishDate.getDaysInJewishMonth(month, year)
        require(daysInJewishMonth >= dayOfMonth) { "Cannot set dayOfMonth to $dayOfMonth; $month only had $daysInJewishMonth in the year $year" }
    }

    constructor(year: Int, month: HebrewMonth, dayOfMonth: Int) : this(year.toLong(), month, dayOfMonth)


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

    override fun compareTo(other: HebrewLocalDate): Int {
        var cmp = year - other.year
        if (cmp == 0L) {
            cmp = (
                    HebrewMonth.getTishreiBasedValue(
                        month.value,
                        year
                    ) -
                            HebrewMonth.getTishreiBasedValue(other.month.value, other.year)
                    ).toLong()
            if (cmp == 0L) {
                cmp = (dayOfMonth - other.dayOfMonth).toLong()
            }
        }
        return cmp.toInt()
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
    fun withYear(newYear: Long) = copy(year = newYear)
    fun withYear(newYear: Int) = copy(year = newYear.toLong())

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
    fun toJewishEpochDays(): Long =
        // add elapsed days this year + Days in prior years + Days elapsed before absolute year 1
        JewishDate.getDaysSinceStartOfJewishYear(year, month, dayOfMonth) +
                JewishDate.getJewishCalendarElapsedDays(year) +
                JEWISH_EPOCH

    val isJewishLeapYear get() = year.isJewishLeapYear

    /**
     * Returns a copy of this [HebrewLocalDate] with the specified number of days added.
     *
     *
     * This method adds the specified amount to the days field incrementing the
     * month and year fields as necessary to ensure the result remains valid.
     * The result is only invalid if the maximum/minimum year is exceeded.
     *
     *
     * For example, 2008-12-31 plus one day would result in 2009-01-01.
     *
     *
     * This instance is immutable and unaffected by this method call.
     *
     * @param daysToAdd  the days to add, may be negative
     * @return a [HebrewLocalDate] based on this date with the days added, not null
     * @throws DateTimeException if the result exceeds the supported date range //TODO
     */
    fun plusDays(days: Long): HebrewLocalDate {
        var daysLeft = days
        var newDayOfMonth = dayOfMonth + days
        var newMonth = month
        var newYear = year
        while (daysLeft > 0) {
            val daysInMonth = getNumDaysInHebrewMonth(newMonth, newYear)
            daysLeft -= daysInMonth
            if (newDayOfMonth > daysInMonth) {
                val nextMonthInYear = newMonth.getNextMonthInYear(newYear)
                newMonth = if (nextMonthInYear == null) {
                    newYear++
                    HebrewMonth.TISHREI
                } else {
                    nextMonthInYear
                }
                newDayOfMonth -= daysInMonth
            } else break
        }
        return HebrewLocalDate(newYear, newMonth, newDayOfMonth.toInt())
    }

    /**
     * Computes the Gregorian [LocalDate] of a given [HebrewLocalDate].
     * **Note:** this class considers the Gregorian calendar to start at year 0, in accordance with ISO-8601.
     */
    fun toLocalDateGregorian(): LocalDate = toPairOfHebrewAndGregorianLocalDate(targetHebrewDate = this).second

    companion object {

        /**
         * Computes the [HebrewLocalDate] of a given Gregorian date.
         * @param this the gregorian date to convert to a [HebrewLocalDate]
         */
        fun LocalDate.withYear(year: Int): LocalDate = LocalDate(year, month, dayOfMonth)
        fun LocalDate.withMonth(month: Month): LocalDate = LocalDate(year, month, dayOfMonth)
        fun LocalDate.withDayOfMonth(dayOfMonth: Int): LocalDate = LocalDate(year, month, dayOfMonth)
        fun LocalDate.toHebrewDate(): HebrewLocalDate =
            toPairOfHebrewAndGregorianLocalDate(targetGregorianDate = this).first

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
            //ensure target after start so we don't have to deal with negative numbers and walking backwards
            if (targetGregorianDate != null) require(
                targetGregorianDate >= STARTING_DATE_GREGORIAN
            ) { "Target date ($targetGregorianDate) must be after $STARTING_DATE_GREGORIAN ($STARTING_DATE_HEBREW). This requirement will hopefully be removed in a future release." }
            else require(
                targetHebrewDate!! >= STARTING_DATE_HEBREW
            ) { "Target date ($targetHebrewDate) must be after $STARTING_DATE_HEBREW ($STARTING_DATE_GREGORIAN). This requirement will hopefully be removed in a future release." }
            /*if(targetGregorianDate != null){
                val daysUntilTarget = STARTING_DATE_GREGORIAN.daysUntil(targetGregorianDate).toLong()
                val hebrewDate = STARTING_DATE_HEBREW.plusDays(daysUntilTarget)
                return hebrewDate to targetGregorianDate
            } else {

            }*/
            var currentHebrewDate = STARTING_DATE_HEBREW
            val tz = TimeZone.UTC
            var currentGregorianDateInstant =
                STARTING_DATE_GREGORIAN.atStartOfDayIn(tz) //+ 1.days //+1 because we don't want to mutate the STARTING_DATE_GREGORIAN
            var currentGregorianDateTime = currentGregorianDateInstant.toLocalDateTime(tz)
            val targetGregorianDateInstant = targetGregorianDate?.atStartOfDayIn(tz)
            if (targetGregorianDate != null && STARTING_DATE_GREGORIAN > targetGregorianDate) { //STARTING_DATE_GREGORIAN > this - i.e. want to compute the hebrew date of a Gregorian date prior to the inception of the hebrew calendar
                throw IllegalArgumentException("Cannot compute dates before the inception of the Hebrew calendar: $STARTING_DATE_GREGORIAN Gregorian/$STARTING_DATE_HEBREW. If you have a reason you want to do this, please submit a feature request on GitHub.")
            }
            while (
                if (targetGregorianDate != null) currentGregorianDateTime.date.year != targetGregorianDate.year
                else currentHebrewDate.year != targetHebrewDate!!.year
            ) {
                if (currentGregorianDateTime.date.year == 0) { //if we're at the beginning of the Gregorian calendar, we need to skip year 0 - kotlinx-datetime/ISO-8601 uses year 0, but people normally don't - the world went from -1 BCE to 1 CE, not 0
                    val timeInYear1 = LocalDateTime(
                        LocalDate(
                            currentGregorianDateTime.date.year + 1,
                            currentGregorianDateTime.date.month,
                            currentGregorianDateTime.date.dayOfMonth
                        ), currentGregorianDateTime.time
                    )
                    if (
                        (targetGregorianDate != null && timeInYear1.date > targetGregorianDate)
                    ) { //we would overshoot the target because target is between the current date (in year 0) and the corresponding date in year 1 (LocalDate(1, month, day))
                        break // move forward the months and days, correcting for year
                    } else {
                        currentGregorianDateInstant = timeInYear1.toInstant(tz)
                        currentGregorianDateTime = timeInYear1

                    }
                    /*println("Was year 0 (gdate = $currentGregorianDateTime, hdate = $currentHebrewDate), jumping to ${STARTING_DATE_HEBREW.month} of year 1")
                    var gregorianMonth = currentGregorianDateTime.date.month
                    while(currentGregorianDateTime.date.year == 0 || currentHebrewDate.month != STARTING_DATE_HEBREW.month) {
                        println("Current hebrew date: $currentHebrewDate, current gregorian date: $currentGregorianDateTime")
                        val lastDayOfGregorianMonth =
                            JewishDate.getLastDayOfGregorianMonth(gregorianMonth.value, currentGregorianDateTime.year).toLong()
                        val sameMonth = gregorianMonth.value == currentGregorianDateTime.date.monthNumber
                        val daysToAdd =
                            if (sameMonth) lastDayOfGregorianMonth - currentGregorianDateTime.date.dayOfMonth*//*currentDayOfGregorianMonth*//*
                            else lastDayOfGregorianMonth
                        println("Days to add: $daysToAdd (same month: $sameMonth)")
                        currentHebrewDate = currentHebrewDate.plusDays(
                            daysToAdd
                        )
                        currentGregorianDateInstant += lastDayOfGregorianMonth.days
                        currentGregorianDateTime = currentGregorianDateInstant.toLocalDateTime(tz)
                        gregorianMonth = currentGregorianDateTime.date.month
                    }*/
                    continue
                }
                val daysInYear = getNumDaysInHebrewYear(currentHebrewDate.year) //accounts for leap years
                //if(currentGregorianDate.year == 0) currentGregorianDate = LocalDate(1, currentGregorianDate.monthNumber, currentGregorianDate.dayOfMonth)
                val newHebrewYear = currentHebrewDate.withYear(currentHebrewDate.year + 1)
                val newStartOfHebrewYear = currentGregorianDateInstant + daysInYear.days
                if (
                    if (targetGregorianDate != null) newStartOfHebrewYear > targetGregorianDateInstant!!
                    else newHebrewYear > targetHebrewDate!!
                ) break //overshot, don't keep adding. Could have overshot because the date could be between the two Rosh Hashanahs.
                currentGregorianDateInstant = newStartOfHebrewYear
                currentGregorianDateTime = currentGregorianDateInstant.toLocalDateTime(tz)
                currentHebrewDate = newHebrewYear
            }
            //year is either right, and now we only need to worry about the month and dayOfMonth,
            // or target is between current and current plus 1 year, but crosses the year boundary
            // (e.g. current is last day of month, and target is 1st day of next month).
            while (
                if (targetGregorianDate != null) currentGregorianDateTime.month != targetGregorianDate.month || currentGregorianDateTime.year != targetGregorianDate.year
                else currentHebrewDate.month != targetHebrewDate!!.month || currentHebrewDate.year != targetHebrewDate.year
            ) {
                val daysInMonth = getNumDaysInHebrewMonth(
                    currentHebrewDate.month,
                    currentHebrewDate.year
                )
                val gregorianDateOfNextRoshChodesh = currentGregorianDateInstant + daysInMonth.days
                val nextMonth = currentHebrewDate.month.getNextMonthInYear(currentHebrewDate.year)
                val newHebrewDate =
                    if (nextMonth == null/*cross year boundary*/) currentHebrewDate.withMonth(HebrewMonth.TISHREI)
                        .withYear(currentHebrewDate.year + 1)
                    else currentHebrewDate.withMonth(nextMonth)

                if (
                    if (targetGregorianDate != null) gregorianDateOfNextRoshChodesh > targetGregorianDateInstant!!
                    else newHebrewDate > targetHebrewDate!!
                ) {
                    break
                } //overshot, don't keep adding. Could have overshot because the date could be between the two months
                currentGregorianDateInstant = gregorianDateOfNextRoshChodesh
                currentGregorianDateTime = currentGregorianDateInstant.toLocalDateTime(tz)

                currentHebrewDate = newHebrewDate
            }
            //month is either right, and now we only need to worry about the dayOfMonth,
            // or target is between current and current plus 1 month but crosses the month boundary
            // (e.g. current is last day of month, and target is 1st day of next month).
            if (currentGregorianDateInstant == targetGregorianDateInstant) return currentHebrewDate to currentGregorianDateTime.date // if day is already right, return

            // currentHebrewDate is at the first day of the month of targetGregorianDate's hebrew equivalent month.
            val hebrewMonthIsCorrect by lazy { targetHebrewDate!!.month == currentHebrewDate.month }
            val numDaysLeftToAdd =
                if (targetGregorianDate != null) (targetGregorianDateInstant!! - currentGregorianDateInstant).inWholeDays.toInt()
                else {
                    if (hebrewMonthIsCorrect) targetHebrewDate!!.dayOfMonth - currentHebrewDate.dayOfMonth
                    else targetHebrewDate!!.dayOfMonth + getNumDaysInHebrewMonth(
                        targetHebrewDate.month,
                        targetHebrewDate.year
                    )
                }
            val numDaysInHebrewMonth = getNumDaysInHebrewMonth(currentHebrewDate.month, currentHebrewDate.year)
            currentHebrewDate = if (numDaysLeftToAdd
                /*TODO what if this is equal to numDaysInHebrewMonth? 1 (current day) + 30 (num days) = 31...*/
                < numDaysInHebrewMonth
            ) currentHebrewDate.withDayOfMonth(
                (numDaysLeftToAdd + 1/*started on day 1, so need to add that*/)
                //.coerceAtMost(numDaysInHebrewMonth/*don't go over the max days in the month*/)
            )
            else { //target is next month (e.g. numDaysLeft = 30 but numDaysInHebrewMonth = 29)
                val nextMonth = currentHebrewDate.month.getNextMonthInYear(currentHebrewDate.year)
                if (nextMonth == null) {
                    HebrewLocalDate(
                        currentHebrewDate.year + 1,
                        HebrewMonth.TISHREI,
                        (numDaysLeftToAdd + 1/*started on day 1, so need to add that*/) - numDaysInHebrewMonth
                    )
                } else currentHebrewDate
                    .withMonth(nextMonth)
                    .withDayOfMonth((numDaysLeftToAdd + 1/*started on day 1, so need to add that*/) - numDaysInHebrewMonth)
            }
            currentGregorianDateInstant += numDaysLeftToAdd.days/*don't need to account for month because datetime library will do the math*/
            currentGregorianDateTime = currentGregorianDateInstant.toLocalDateTime(tz)
            return currentHebrewDate to currentGregorianDateTime.date
        }

        fun getNumDaysInHebrewYear(year: Long): Int = year.daysInJewishYear
        fun getNumDaysInHebrewMonth(month: HebrewMonth, year: Long): Int = JewishDate.getDaysInJewishMonth(month, year)

        /**
         * the Jewish epoch using the RD (Rata Die/Fixed Date or Reingold Dershowitz) day used in Calendrical Calculations.
         * Day 1 is January 1, 0001 Gregorian
         */
        const val JEWISH_EPOCH = -1_373_429

        /**
         * The start of the Hebrew calendar. Used as a reference point for converting between
         * Gregorian and Hebrew dates.
         * This currently starts at Rosh Hashana of Gregorian year 1 so as not to deal with year 0 (1 B.C.E in kotlinx-datetime).
         * Certain edge cases related to year zero were not handled correctly by the library.
         * TODO reimplement to handle those cases.
         * */
        internal val STARTING_DATE_HEBREW = HebrewLocalDate(3762, HebrewMonth.TISHREI, 1)

        /**
         * @see STARTING_DATE_HEBREW; Does not account for The Gregorian Reformation.
         * */
        internal val STARTING_DATE_GREGORIAN = LocalDate(1, Month.SEPTEMBER, 6)
    }
}
