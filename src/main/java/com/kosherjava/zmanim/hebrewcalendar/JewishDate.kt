/*
 * Zmanim Java API
 * Copyright (C) 2011 - 2021 Eliyahu Hershfeld
 * Copyright (C) September 2002 Avrom Finkelstien
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General
 * Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful,but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more
 * details.
 * You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to
 * the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA,
 * or connect to: http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 */
package com.kosherjava.zmanim.hebrewcalendar

import com.kosherjava.zmanim.hebrewcalendar.HebrewLocalDate.Companion.toHebrewDate
import com.kosherjava.zmanim.util.DateUtils.now
import kotlinx.datetime.*

/**
 * The JewishDate is the base calendar class, that supports maintenance of a [LocalDateTime]
 * instance along with the corresponding Jewish date. This class can use the standard Java (TODO change for kotlin) Date and Calendar
 * classes for setting and maintaining the dates, but it does not subclass these classes or use them internally
 * in any calculations. This class also does not have a concept of a time (which the Date class does). Please
 * note that the calendar does not currently support dates prior to 1/1/1 Gregorian. Also keep in mind that the
 * Gregorian calendar started on October 15, 1582, so any calculations prior to that are suspect (at least from
 * a Gregorian perspective). While 1/1/1 Gregorian and forward are technically supported, any calculations prior to [Hillel II's (Hakatan's](http://en.wikipedia.org/wiki/Hillel_II)) calendar (4119 in the Jewish Calendar / 359
 * CE Julian as recorded by [Rav Hai Gaon](http://en.wikipedia.org/wiki/Hai_Gaon)) would be just an
 * approximation.
 *
 * This open source Java code was written by [Avrom Finkelstien](http://www.facebook.com/avromf) from his C++
 * code. It was refactored to fit the KosherJava Zmanim API with simplification of the code, enhancements and some bug
 * fixing.
 *
 * Some of Avrom's original C++ code was translated from
 * [C/C++ code](https://web.archive.org/web/20120124134148/http://emr.cs.uiuc.edu/~reingold/calendar.C) in
 * [Calendrical Calculations](http://www.calendarists.com) by Nachum Dershowitz and Edward M.
 * Reingold, Software-- Practice &amp; Experience, vol. 20, no. 9 (September, 1990), pp. 899- 928. Any method with the mark
 * "ND+ER" indicates that the method was taken from this source with minor modifications.
 *
 * If you are looking for a class that implements a Jewish calendar version of the Calendar class, one is available from
 * the [ICU (International Components for Unicode)](http://site.icu-project.org/) project, formerly part of
 * IBM's DeveloperWorks.
 *
 * @see JewishCalendar
 *
 * @see HebrewDateFormatter
 *
 * @see Date
 *
 * @see java.util.Calendar
 *
 * @author  Avrom Finkelstien 2002
 * @author  Eliyahu Hershfeld 2011 - 2021
 */
open class JewishDate : Comparable<JewishDate> {

    /**
     * Constructor that creates a JewishDate based on a molad passed in. The molad would be the number of chalakim/parts
     * starting at the beginning of Sunday prior to the molad Tohu BeHaRaD (Be = Monday, Ha= 5 hours and Rad =204
     * chalakim/parts) - prior to the start of the Jewish calendar. BeHaRaD is 23:11:20 on Sunday night(5 hours 204/1080
     * chalakim after sunset on Sunday evening).
     *
     * @param molad the number of chalakim since the beginning of Sunday prior to BaHaRaD
     */
    constructor(molad: Long) {
        absDateToDate(moladToAbsDate(molad))
        // long chalakimSince = getChalakimSinceMoladTohu(year, TISHREI);// tishrei
        val conjunctionDay = (molad / CHALAKIM_PER_DAY.toLong()).toInt()
        val conjunctionParts = (molad - conjunctionDay * CHALAKIM_PER_DAY.toLong()).toInt()
        setMoladTime(conjunctionParts)
    }
    /**
     * Creates a Jewish date based on a Jewish year, month and day of month.
     *
     * @param hebrewLocalDate.year
     * the Jewish year
     * @param hebrewLocalDate.month
     * the Jewish month. The method expects a 1 for Nissan ... 12 for Adar and 13 for Adar II. Use the
     * constants [NISSAN] ... [ADAR] (or [ADAR_II] for a leap year Adar II) to avoid any
     * confusion.
     * @param hebrewDayOfMonth
     * the Jewish day of month. If 30 is passed in for a month with only 29 days (for example [IYAR],
     * or [KISLEV] in a year that [isKislevShort]), the 29th (last valid date of the month)
     * will be set
     * @throws IllegalArgumentException
     * if the day of month is < 1 or > 30, or a year of < 0 is passed in.
     */
    constructor(hebrewYear: Int, hebrewMonth: HebrewMonth, hebrewDayOfMonth: Int) : this(hebrewYear.toLong(), hebrewMonth, hebrewDayOfMonth)
    constructor(hebrewYear: Long, hebrewMonth: HebrewMonth, hebrewDayOfMonth: Int) {
        setJewishDate(hebrewYear, hebrewMonth, hebrewDayOfMonth)
    }
    constructor(hebrewYear: Int, hebrewMonth: Int, hebrewDayOfMonth: Int) : this(hebrewYear, HebrewMonth.getMonthForValue(hebrewMonth), hebrewDayOfMonth)
    constructor(localDate: LocalDate) {
        setDate(localDate)
    }

    /**
     * Default constructor will set a default date to the current system date.
     */
    constructor() {
        resetDate()
    }

    /**
     * The molad hours. Only a JewishDate object populated with [molad],
     * [setJewishDate] or [moladHours] will have this field
     * populated. A regular JewishDate object will have this field set to 0.
     *
     * @see molad
     * @see setJewishDate
     */
    var moladHours = 0

    /**
     * The molad minutes. Only an object populated with [molad],
     * [setJewishDate] or [moladMinutes] will have these fields
     * populated. A regular JewishDate object will have this field set to 0.
     *
     * When setting the molad minutes, the expectation is that the traditional minute-less chalakim will be broken out to
     * minutes and [chalakim/parts][moladChalakim] , so 793 (TaShTZaG) parts would have the minutes set to
     * 44 and chalakim to 1.
     * @see moladMinutes
     * @see moladChalakim
     * @see setJewishDate
     */
    var moladMinutes = 0

    /**
     * the internal count of *molad* *chalakim* / parts. Only an object populated with [molad],
     * [setJewishDate] or [moladChalakim] will have these fields
     * populated. A regular JewishDate object will have this field set to 0.
     *
     * When setting the molad chalakim, the expectation is that the traditional minute-less chalakim will be broken out to
     * [minutes][moladMinutes] and chalakim, so 793 (TaShTZaG) parts would have the minutes set to 44 and
     * chalakim to 1.
     * */
    var moladChalakim = 0

    /**
     * Computes the Gregorian date from the absolute date. ND+ER
     * @param absDate the absolute date
     */
    private fun absDateToDate(absDate: Int) {
        var year = absDate / 366 // Search forward year by year from approximate year
        while (absDate >= gregorianDateToAbsDate(LocalDate(year + 1, 1, 1))) year++
        var month = 1 // Search forward month by month from January
        while (absDate > gregorianDateToAbsDate(LocalDate(year, month, getLastDayOfGregorianMonth(month, year)))) month++
        val dayOfMonth = absDate - gregorianDateToAbsDate(LocalDate(year, month, 1)) + 1
        setGregorianDate(year, month, dayOfMonth)
    }

    /**
     * Returns if the year the calendar is set to is a Jewish leap year. Years 3, 6, 8, 11, 14, 17 and 19 in the 19 year
     * cycle are leap years.
     *
     * @return true if it is a leap year
     * @see isJewishLeapYear
     */
    val isJewishLeapYear: Boolean
        get() = hebrewLocalDate.year.isJewishLeapYear

    /**
     * Returns the number of chalakim (parts - 1080 to the hour) from the original hypothetical Molad Tohu to the Jewish
     * year and month that this Object is set to.
     *
     * @return the number of chalakim (parts - 1080 to the hour) from the original hypothetical Molad Tohu
     */
    val chalakimSinceMoladTohu: Long
        get() = getChalakimSinceMoladTohu(hebrewLocalDate.year, hebrewLocalDate.month)

    /**
     * Returns the number of days for the current year that the calendar is set to.
     *
     * @return the number of days for the Object's current Jewish year.
     * @see isCheshvanLong
     * @see isKislevShort
     * @see isJewishLeapYear
     */
    val daysInJewishYear: Int
        get() = hebrewLocalDate.year.daysInJewishYear

    /**
     * Returns if Cheshvan is long (30 days VS 29 days) for the current year that the calendar is set to. The method
     * name isLong is done since in a Kesidran (ordered) year Cheshvan is short.
     *
     * @return true if Cheshvan is long for the current year that the calendar is set to
     * @see isCheshvanLong
     */
    val isCheshvanLong: Boolean
        get() = hebrewLocalDate.year.isCheshvanLong

    /**
     * Returns if the Kislev is short for the year that this class is set to. The method name isShort is done since in a
     * Kesidran (ordered) year Kislev is long.
     *
     * @return true if Kislev is short for the year that this class is set to
     */
    val isKislevShort: Boolean
        get() = hebrewLocalDate.year.isKislevShort

    /**
     * Returns the Cheshvan and Kislev kviah (whether a Jewish year is short, regular or long). It will return
     * [SHELAIMIM] if both cheshvan and kislev are 30 days, [KESIDRAN] if Cheshvan is 29 days and Kislev
     * is 30 days and [CHASERIM] if both are 29 days.
     *
     * @return [SHELAIMIM] if both cheshvan and kislev are 30 days, [KESIDRAN] if Cheshvan is 29 days and
     * Kislev is 30 days and [CHASERIM] if both are 29 days.
     * @see isCheshvanLong
     * @see isKislevShort
     */
    val cheshvanKislevKviah: Int
        get() = if (isCheshvanLong && !isKislevShort) SHELAIMIM
        else if (!isCheshvanLong && isKislevShort) CHASERIM
        else KESIDRAN

    /**
     * Returns the number of days of the Jewish month that the calendar is currently set to.
     *
     * @return the number of days for the Jewish month that the calendar is currently set to.
     */
    val daysInJewishMonth: Int
        get() = getDaysInJewishMonth(hebrewLocalDate.month, hebrewLocalDate.year)

    /**
     * Returns the molad for a given year and month. Returns a JewishDate [Object] set to the date of the molad
     * with the [hours][moladHours], [minutes][moladMinutes] and [chalakim][moladChalakim] set.
     * In the current implementation, it sets the molad time based on a midnight date rollover. This
     * means that Rosh Chodesh Adar II, 5771 with a molad of 7 chalakim past midnight on Shabbos 29 Adar I / March 5,
     * 2011 12:00 AM and 7 chalakim, will have the following values: hours: 0, minutes: 0, Chalakim: 7.
     *
     * @return a JewishDate [Object] set to the date of the molad with the [hours][moladHours],
     * [minutes][moladMinutes] and [chalakim][moladChalakim] set.
     */
    val molad: JewishDate
        get() {
            val moladDate = JewishDate(chalakimSinceMoladTohu)
            if (moladDate.moladHours >= 6) moladDate.forward(DateTimeUnit.DAY, 1)
            moladDate.moladHours = (moladDate.moladHours + 18) % 24
            return moladDate
        }

    /**
     * Sets the date based on a [LocalDateTime] object. Modifies the Jewish date as well.
     *
     * @param dateTime
     * the `LocalDateTime` to set the LocalDate to
     */
    fun setDate(date: LocalDate): JewishDate {
//        require(LocalDate.get(LocalDate.ERA) != GregorianLocalDate.BC) { ("LocalDates with a BC era are not supported. The year ${LocalDate.get(LocalDate.YEAR)} BC is invalid.") } //TODO how should this be dealt with?
        gregorianLocalDate = date
        hebrewLocalDate = date.toHebrewDate()
        return this
    }

    /**
     * Sets the molad time (hours minutes and chalakim) based on the number of chalakim since the start of the day.
     *
     * @param chalakim
     * the number of chalakim since the start of the day.
     */
    private fun setMoladTime(chalakim: Int) {
        var adjustedChalakim = chalakim
        moladHours = adjustedChalakim / CHALAKIM_PER_HOUR
        adjustedChalakim -= (moladHours * CHALAKIM_PER_HOUR)
        moladMinutes = adjustedChalakim / CHALAKIM_PER_MINUTE
        moladChalakim = adjustedChalakim - moladMinutes * CHALAKIM_PER_MINUTE
    }

    /**
     * returns the number of days from Rosh Hashana of the date passed in, to the full date passed in.
     *
     * @return the number of days
     */
    val daysSinceStartOfJewishYear: Int
        get() = getDaysSinceStartOfJewishYear(hebrewLocalDate.year, hebrewLocalDate.month, hebrewLocalDate.dayOfMonth)

    /**
     * Sets the Gregorian Date, and updates the Jewish date accordingly. Like the Java Calendar A value of 0 is expected
     * for January.
     *
     * @param year
     * the Gregorian year
     * @param month
     * the Gregorian month. Between 1 and 12.
     * @param dayOfMonth
     * the Gregorian day of month - 1 for January, 12 for December. If this is > the number of days in the month/year, the last valid date of
     * the month will be set
     * @throws IllegalArgumentException
     * if a year of < 1, a month < 0 or > 11 or a day of month < 1 is passed in.
     *
     * **Note:** This setter will correct the [dayOfMonth] if passed in an invalid number given the [year] and [month]
     * (e.g. February 29 in a leap year)
     */
    fun setGregorianDate(year: Int, month: Int, dayOfMonth: Int): JewishDate {
        gregorianLocalDate = LocalDate(year, month, dayOfMonth.coerceAtMost(getLastDayOfGregorianMonth(month, year)))
        hebrewLocalDate = gregorianLocalDate.toHebrewDate()
        return this
    }

    /**
     * Sets the Jewish Date and updates the Gregorian date accordingly.
     *
     * @param year
     * the Jewish year. The year can't be negative
     * @param month
     * the Jewish month starting with Nisan. A value of 1 is expected for Nissan ... 12 for Adar and 13 for
     * Adar II. Use the constants [NISSAN] ... [ADAR] (or [ADAR_II] for a leap year Adar
     * II) to avoid any confusion.
     * @param dayOfMonth
     * the Jewish day of month. valid values are 1-30. If the day of month is set to 30 for a month that only
     * has 29 days, the day will be set as 29.
     * @throws IllegalArgumentException
     * if a A Jewish date earlier than 18 Teves, 3761 (1/1/1 Gregorian), a month < 1 or > 12 (or 13 on a
     * leap year) or the day of month is < 1 or > 30 is passed in
     */
    fun setJewishDate(year: Long, month: HebrewMonth, dayOfMonth: Int): JewishDate {
        setJewishDate(year, month, dayOfMonth, 0, 0, 0)
        return this
    }

    /**
     * Sets the Jewish Date and updates the Gregorian date accordingly.
     *
     * @param year
     * the Jewish year. The year can't be negative
     * @param month
     * the Jewish month starting with Nisan. A value of 1 is expected for Nissan ... 12 for Adar and 13 for
     * Adar II. Use the constants [NISSAN] ... [ADAR] (or [ADAR_II] for a leap year Adar
     * II) to avoid any confusion.
     * @param dayOfMonth
     * the Jewish day of month. valid values are 1-30. If the day of month is set to 30 for a month that only
     * has 29 days, the day will be set as 29.
     *
     * @param hours
     * the hour of the day. Used for Molad calculations
     * @param minutes
     * the minutes. Used for Molad calculations
     * @param chalakim
     * the chalakim/parts. Used for Molad calculations. The chalakim should not exceed 17. Minutes should be
     * used for larger numbers.
     *
     * @throws IllegalArgumentException
     * if a A Jewish date earlier than 18 Teves, 3761 (1/1/1 Gregorian), a month < 1 or > 12 (or 13 on a
     * leap year), the day of month is < 1 or > 30, an hour < 0 or > 23, a minute < 0 > 59 or chalakim < 0 >
     * 17. For larger a larger number of chalakim such as 793 (TaShTzaG) break the chalakim into minutes (18
     * chalakim per minutes, so it would be 44 minutes and 1 chelek in the case of 793 (TaShTzaG).
     */
    fun setJewishDate(year: Long, month: HebrewMonth, dayOfMonth: Int, hours: Int, minutes: Int, chalakim: Int): JewishDate {
        validateJewishDate(hours, minutes, chalakim)
        val date = HebrewLocalDate(
            year,
            month,
            dayOfMonth
                .coerceAtMost(

                    // if 30 is passed for a month that only has 29 days (for example by rolling the month from a month that had 30
                    // days to a month that only has 29) set the date to 29th
                    getDaysInJewishMonth(month, year)
                )
        )
        hebrewLocalDate = date
        moladHours = hours
        moladMinutes = minutes
        moladChalakim = chalakim
        gregorianLocalDate = date.toLocalDateGregorian() 
        return this
    }

    /**
     * Returns this object's date as a [LocalDate] object.
     *
     * @return The [LocalDate]
     */
    lateinit var hebrewLocalDate: HebrewLocalDate
    lateinit var gregorianLocalDate: LocalDate

    /**
     * Resets this date to the current system date.
     */
    fun resetDate(): JewishDate {
        setDate(LocalDate.now())
        return this
    }

    /**
     * Returns a string containing the Jewish date in the form, "day Month, year" e.g. "21 Shevat, 5729". For more
     * complex formatting, use the formatter classes.
     *
     * @return the Jewish date in the form "day Month, year" e.g. "21 Shevat, 5729"
     * @see HebrewDateFormatter.format
     */
    override fun toString() = HebrewDateFormatter().format(this)

    /**
     * Rolls the date, month or year forward by the amount passed in. It modifies both the Gregorian and Jewish dates accordingly.
     * If manipulation beyond the fields supported here is required, use the [Calendar] class [Calendar.add]
     * or [Calendar.roll] methods in the following manner.
     *
     * <pre>
     * `
     * Calendar cal = jewishDate.getTime(); // get a java.util.Calendar representation of the JewishDate
     * cal.add(Calendar.MONTH, 3); // add 3 Gregorian months
     * jewishDate.setDate(cal); // set the updated calendar back to this class
    ` *
    </pre> *
     *
     * @param field the calendar field to be forwarded. The must be [Calendar.DATE], [Calendar.MONTH] or [Calendar.YEAR]
     * @param amount the positive amount to move forward
     * @throws IllegalArgumentException if the field is anything besides [Calendar.DATE], [Calendar.MONTH] or [Calendar.YEAR]
     * or if the amount is less than 1
     *
     * @see back
     * @see Calendar.add
     * @see Calendar.roll
     */
    fun forward(field: DateTimeUnit, amount: Int): JewishDate {
        require(field == DateTimeUnit.DAY || field == DateTimeUnit.MONTH || field == DateTimeUnit.YEAR) { "Unsupported field was passed to Forward. Only DateTimeUnit.DATE, DateTimeUnit.MONTH or DateTimeUnit.YEAR are supported." }
        require(amount >= 1) { "JewishDate.forward() does not support amounts less than 1. See JewishDate.back()" }
        when (field) {
            DateTimeUnit.DAY -> {
                // Change Gregorian date
                for (i in 0 until amount) {
                    gregorianLocalDate = gregorianLocalDate.plus(1, DateTimeUnit.DAY)

                    // Change the Jewish Date
                    hebrewLocalDate = if (hebrewLocalDate.dayOfMonth == daysInJewishMonth) {
                        // if it last day of elul (i.e. last day of Jewish year)
                        when (hebrewLocalDate.month) {
                            HebrewMonth.ELUL -> HebrewLocalDate(hebrewLocalDate.year + 1, hebrewLocalDate.month.nextMonth/*wraps*/, 1)
                            getLastMonthOfJewishYear(hebrewLocalDate.year) -> HebrewLocalDate(hebrewLocalDate.year, HebrewMonth.NISSAN, 1)
                            else -> HebrewLocalDate(hebrewLocalDate.year, hebrewLocalDate.month.nextMonth, 1)
                        }
                    } else { // if not last date of month
                        hebrewLocalDate.withDayOfMonth(hebrewLocalDate.dayOfMonth + 1)
                    }
                }
            }
            DateTimeUnit.MONTH -> forwardJewishMonth(amount)
            DateTimeUnit.YEAR -> jewishYear = hebrewLocalDate.year + amount
            else -> {
                throw IllegalArgumentException("Unsupported field was passed to Forward. Only DateTimeUnit.DATE, DateTimeUnit.MONTH or DateTimeUnit.YEAR are supported.")
            }
        }
        return this
    }

    /**
     * Forward the Jewish date by the number of months passed in.
     * FIXME: Deal with forwarding a date such as 30 Nisan by a month. 30 Iyar does not exist. This should be dealt with similar to
     * the way that the Java Calendar behaves (not that simple since there is a difference between [Calendar.add] or [Calendar.roll].
     *
     * @throws IllegalArgumentException if the amount is less than 1
     * @param amount the number of months to roll the month forward
     */
    private fun forwardJewishMonth(amount: Int) {
        require(amount >= 1) { "the amount of months to forward has to be greater than zero." }
        for (i in 0 until amount) {
            if (hebrewLocalDate.month == HebrewMonth.ELUL) {
                setJewishMonth(HebrewMonth.TISHREI)
                jewishYear = hebrewLocalDate.year + 1
            } else if (
                (!isJewishLeapYear && hebrewLocalDate.month == HebrewMonth.ADAR) ||
                (isJewishLeapYear && hebrewLocalDate.month == HebrewMonth.ADAR_II)
            ) setJewishMonth(HebrewMonth.NISSAN)
            else setJewishMonth(hebrewLocalDate.month.nextMonth)
        }
    }

    /**
     * Rolls the date back by 1 day. It modifies both the Gregorian and Jewish dates accordingly. The API does not
     * currently offer the ability to forward more than one day at a time, or to forward by month or year. If such
     * manipulation is required use the [java.util.Calendar] class [java.util.Calendar.add] or
     * [java.util.Calendar.roll] methods in the following manner.
     *
     * <pre>
     * `
     * Calendar cal = jewishDate.getTime(); // get a java.util.Calendar representation of the JewishDate
     * cal.add(Calendar.MONTH, -3); // subtract 3 Gregorian months
     * jewishDate.setDate(cal); // set the updated calendar back to this class
    ` *
    </pre> *
     *
     * @see back
     * @see java.util.Calendar.add
     * @see java.util.Calendar.roll
     */
    fun back(): JewishDate {
        // Change Gregorian date
        gregorianLocalDate = gregorianLocalDate.minus(1, DateTimeUnit.DAY)
        // change Jewish date
        hebrewLocalDate = if (hebrewLocalDate.dayOfMonth == 1) { // if first day of the Jewish month
            when (hebrewLocalDate.month) {
                HebrewMonth.NISSAN -> HebrewLocalDate(hebrewLocalDate.year, getLastMonthOfJewishYear(hebrewLocalDate.year), daysInJewishMonth)
                HebrewMonth.TISHREI -> { // if Rosh Hashana
                    HebrewLocalDate(hebrewLocalDate.year - 1, hebrewLocalDate.month.previousMonth, daysInJewishMonth)
                }
                else -> HebrewLocalDate(hebrewLocalDate.year, hebrewLocalDate.month.previousMonth, daysInJewishMonth)
            }
        } else {
            hebrewLocalDate.withDayOfMonth(hebrewLocalDate.dayOfMonth - 1)
        }
        return this
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     * @see Object.equals
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is JewishDate) return false
        return gregorianLocalDate == other.gregorianLocalDate
    }

    /**
     * Compares two dates as per the compareTo() method in the Comparable interface. Returns a value less than 0 if this
     * date is "less than" (before) the date, greater than 0 if this date is "greater than" (after) the date, or 0 if
     * they are equal.
     */
    override fun compareTo(other: JewishDate): Int = gregorianLocalDate.compareTo(other.gregorianLocalDate)

    var jewishYear: Long
        /**
         * Returns the jewish year
         * */
        get() = hebrewLocalDate.year
        /**
         * Sets the Jewish year.
         *
         * @param value
         * the Jewish year
         * @throws IllegalArgumentException
         * (in the current implementation) if a year of < [HebrewLocalDate.STARTING_DATE_HEBREW]
         * (Tishrei 1, 0002/Sep. 6, 1 AD in current implementation) is passed in. TODO
         */
        set(value) { setJewishDate(value, hebrewLocalDate.month, hebrewLocalDate.dayOfMonth) }

    var jewishMonth: HebrewMonth
        set(value) { setJewishDate(hebrewLocalDate.year, value, hebrewLocalDate.dayOfMonth) }
        get() = hebrewLocalDate.month
    /**
     * The Jewish day of month.
     */
    var jewishDayOfMonth: Int
        get() = hebrewLocalDate.dayOfMonth
        /**
         * Sets the jewish day of month.
         * @param dayOfMonth
         * the Jewish day of month
         * @throws IllegalArgumentException
         * if the day of month is < 1 or > 30 is passed in.
         * **Note:** If [jewishMonth] does not have [dayOfMonth] days in the current [jewishYear], this setter will
         * correct the value instead of throwing an error.
         * */
        set(dayOfMonth) { setJewishDate(hebrewLocalDate.year, hebrewLocalDate.month, dayOfMonth) }

    var gregorianYear

        /**
         * sets the Gregorian year.
         *
         * @param value
         * the Gregorian year.
         * @throws IllegalArgumentException
         * (in current implementation) if a year of < [HebrewLocalDate.STARTING_DATE_GREGORIAN].year. TODO
         *
         * **Note:** This setter will correct the [dayOfMonth] if passed in an invalid number given the [year] and [month]
         * (e.g. February 29 in a leap year)
         */
        set(value) { setGregorianDate(value, gregorianLocalDate.monthNumber, gregorianLocalDate.dayOfMonth) }
        get() = gregorianLocalDate.year

    var gregorianMonth
        /**
         * sets the Gregorian month.
         *
         * @param value
         * the Gregorian Day of month.
         * @throws IllegalArgumentException
         * if the day of month of < 1 is passed in, or (in the current implementation) if a dayOfMonth of < [HebrewLocalDate.STARTING_DATE_GREGORIAN].dayOfMonth
         * (Sep. 6, 1 AD/Tishrei 1, 0002 in current implementation) is passed in. TODO
         *
         * **Note:** This setter will correct the [value] if passed in an invalid number given the [gregorianYear] and [gregorianMonth]
         * (e.g. February 29 in a leap year)
         */
        set(value) { setGregorianDate(gregorianLocalDate.year, value, gregorianLocalDate.dayOfMonth) }
        get() = gregorianLocalDate.monthNumber

    var gregorianDayOfMonth
        /**
         * sets the Gregorian Day of month.
         *
         * @param value
         * the Gregorian Day of month.
         * @throws IllegalArgumentException
         * if the day of month of < 1 is passed in, or (in the current implementation) if a dayOfMonth of < [HebrewLocalDate.STARTING_DATE_GREGORIAN].dayOfMonth
         * (Sep. 6, 1 AD/Tishrei 1, 0002 in current implementation) is passed in. TODO
         *
         * **Note:** This setter will correct the [value] if passed in an invalid number given the [gregorianYear] and [gregorianMonth]
         * (e.g. February 29 in a leap year)
         */
        set(value) { setGregorianDate(gregorianLocalDate.year, gregorianLocalDate.monthNumber, value) }
        get() = gregorianLocalDate.dayOfMonth


    /**
     * sets the Jewish month.
     *
     * @param month
     * the Jewish month from 1 to 12 (or 13 years in a leap year). The month count starts with 1 for Nisan
     * and goes to 13 for Adar II
     * @throws IllegalArgumentException
     * if a month < 1 or > 12 (or 13 on a leap year) is passed in
     */
    fun setJewishMonth(month: HebrewMonth): JewishDate { //can't make this a setter because it has side effects and would cause a recursive StackOverflow
        setJewishDate(hebrewLocalDate.year, month, hebrewLocalDate.dayOfMonth)
        return this
    }

    val Int.lastDayOfGregorianMonth get() = getLastDayOfGregorianMonth(this, gregorianLocalDate.year)

    /**
     * A method that creates a [deep copy](http://en.wikipedia.org/wiki/Object_copy#Deep_copy) of the object.
     *
     * @see Object.clone
     */
    /*public override fun clone(): Any {
        var clone: JewishDate? = null
        try {
            clone = super.clone() as JewishDate
        } catch (cnse: CloneNotSupportedException) {
            // Required by the compiler. Should never be reached since we implement clone()
        }
        clone!!.setInternalGregorianDate(gregorianYear, gregorianMonth, gregorianDayOfMonth)
        return clone
    }*/
    fun copy(
        hebrewYear: Long = hebrewLocalDate.year,
        month: HebrewMonth = hebrewLocalDate.month,
        hebrewDayOfMonth: Int = hebrewLocalDate.dayOfMonth
    ): JewishDate = JewishDate(hebrewYear, month, hebrewDayOfMonth)

    /**
     * Overrides [Object.hashCode].
     * @see Object.hashCode
     */
    override fun hashCode(): Int {
        var result = 17
        result = 37 * result + this::class.hashCode() // needed or this and subclasses will return identical hash
        result += 37 * result + gregorianLocalDate.hashCode()
        return result
    }

    companion object {

        /** The number  of *chalakim* (18) in a minute. */
        private const val CHALAKIM_PER_MINUTE = 18

        /** The number  of *chalakim* (1080) in an hour. */
        private const val CHALAKIM_PER_HOUR = 1080

        /** The number of *chalakim* (25,920) in a 24 hour day . */
        private const val CHALAKIM_PER_DAY = 25920 // 24 * 1080

        /** The number  of *chalakim* in an average Jewish month. A month has 29 days, 12 hours and 793
         * *chalakim* (44 minutes and 3.3 seconds) for a total of 765,433 *chalakim* */
        private const val CHALAKIM_PER_MONTH: Long = 765433 // (29 * 24 + 12) * 1080 + 793

        /**
         * Days from the beginning of Sunday till molad BaHaRaD. Calculated as 1 day, 5 hours and 204 chalakim = (24 + 5) *
         * 1080 + 204 = 31524
         */
        private const val CHALAKIM_MOLAD_TOHU = 31524

        /**
         * A short year where both [CHESHVAN] and [KISLEV] are 29 days.
         *
         * @see cheshvanKislevKviah
         * @see HebrewDateFormatter.getFormattedKviah
         */
        const val CHASERIM = 0

        /**
         * An ordered year where [CHESHVAN] is 29 days and [.KISLEV] is 30 days.
         *
         * @see cheshvanKislevKviah
         * @see HebrewDateFormatter.getFormattedKviah
         */
        const val KESIDRAN = 1

        /**
         * A long year where both [CHESHVAN] and [KISLEV] are 30 days.
         *
         * @see cheshvanKislevKviah
         * @see HebrewDateFormatter.getFormattedKviah
         */
        const val SHELAIMIM = 2

        /**
         * Returns the number of days in a given month in a given month and year.
         *
         * @param month
         * the month. As with other cases in this class, this is 1-based, not zero-based.
         * @param year
         * the year (only impacts February)
         * @return the number of days in the month in the given year
         */
        internal fun getLastDayOfGregorianMonth(month: Int, year: Int): Int = when (month) {
            2 -> if ((year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)) 29 else 28
            4, 6, 9, 11 -> 30
            else -> 31
        }

        /**
         * Computes the absolute date from a Gregorian date. ND+ER
         *
         * @param year
         * the Gregorian year
         * @param month
         * the Gregorian month. Unlike the Java Calendar where January has the value of 0,This expects a 1 for
         * January
         * @param dayOfMonth
         * the day of the month (1st, 2nd, etc...)
         * @return the absolute Gregorian day
         */
        private fun gregorianDateToAbsDate(date: LocalDate): Int {
            val year: Int = date.year
            val month: Int = date.monthNumber
            val dayOfMonth: Int = date.dayOfMonth
            var absDate = dayOfMonth
            for (m in month - 1 downTo 1) {
                absDate += getLastDayOfGregorianMonth(m, year) // days in prior months of the year
            }
            return (((absDate // days this year
                    + (365 * (year - 1) // days in previous years ignoring leap days
                    ) + ((year - 1) / 4)) // Julian leap days before this year
                    - (year - 1) / 100 // minus prior century years
                    + (year - 1) / 400)) // plus prior years divisible by 400
        }

        /**
         * Returns if the year is a Jewish leap year. Years 3, 6, 8, 11, 14, 17 and 19 in the 19 year cycle are leap years.
         *
         * @param year
         * the Jewish year.
         * @return true if it is a leap year
         * @see isJewishLeapYear
         */
        val Int.isJewishLeapYear get() = ((7 * this) + 1) % 19 < 7
        val Long.isJewishLeapYear get() = ((7 * this) + 1) % 19 < 7

        /**
         * Returns the last month of a given Jewish year. This will be 12 on a non [leap year][isJewishLeapYear]
         * or 13 on a leap year.
         *
         * @param year
         * the Jewish year.
         * @return 12 on a non leap year or 13 on a leap year
         * @see isJewishLeapYear
         */
        private fun getLastMonthOfJewishYear(year: Long): HebrewMonth = if (year.isJewishLeapYear) HebrewMonth.ADAR_II else HebrewMonth.ADAR

        /**
         * Returns the number of days elapsed from the Sunday prior to the start of the Jewish Calendar to the mean
         * conjunction of Tishri of the Jewish year.
         *
         * @param year
         * the Jewish year
         * @return the number of days elapsed from prior to the molad Tohu BaHaRaD (Be = Monday, Ha= 5 hours and Rad =204
         * chalakim/parts) prior to the start of the Jewish calendar, to the mean conjunction of Tishri of the
         * Jewish year. BeHaRaD is 23:11:20 on Sunday night(5 hours 204/1080 chalakim after sunset on Sunday
         * evening).
         */
        fun getJewishCalendarElapsedDays(year: Int): Int = getJewishCalendarElapsedDays(year.toLong()).toInt()
        fun getJewishCalendarElapsedDays(year: Long): Long {
            val chalakimSince = getChalakimSinceMoladTohu(year, HebrewMonth.TISHREI)
            val moladDay = (chalakimSince / CHALAKIM_PER_DAY.toLong()).toInt()
            val moladParts = (chalakimSince - moladDay * CHALAKIM_PER_DAY.toLong()).toInt()
            // delay Rosh Hashana for the 4 dechiyos
            return addDechiyos(year, moladDay.toLong(), moladParts)
        }
        // private static int getJewishCalendarElapsedDaysOLD(int year) {
        // // Jewish lunar month = 29 days, 12 hours and 793 chalakim
        // // Molad Tohu = BeHaRaD - Monday, 5 hours (11 PM) and 204 chalakim
        // final int chalakimTashTZag = 793; // chalakim in a lunar month
        // final int chalakimTohuRaD = 204; // chalakim from original molad Tohu BeHaRaD
        // final int hoursTohuHa = 5; // hours from original molad Tohu BeHaRaD
        // final int dayTohu = 1; // Monday (0 based)
        //
        // int monthsElapsed = (235 * ((year - 1) / 19)) // Months in complete 19 year lunar (Metonic) cycles so far
        // + (12 * ((year - 1) % 19)) // Regular months in this cycle
        // + ((7 * ((year - 1) % 19) + 1) / 19); // Leap months this cycle
        // // start with Molad Tohu BeHaRaD
        // // start with RaD of BeHaRaD and add TaShTzaG (793) chalakim plus elapsed chalakim
        // int partsElapsed = chalakimTohuRaD + chalakimTashTZag * (monthsElapsed % 1080);
        // // start with Ha hours of BeHaRaD, add 12 hour remainder of lunar month add hours elapsed
        // int hoursElapsed = hoursTohuHa + 12 * monthsElapsed + 793 * (monthsElapsed / 1080) + partsElapsed / 1080;
        // // start with Monday of BeHaRaD = 1 (0 based), add 29 days of the lunar months elapsed
        // int conjunctionDay = dayTohu + 29 * monthsElapsed + hoursElapsed / 24;
        // int conjunctionParts = 1080 * (hoursElapsed % 24) + partsElapsed % 1080;
        // return addDechiyos(year, conjunctionDay, conjunctionParts);
        // }
        /**
         * Adds the 4 dechiyos for molad Tishrei. These are:
         *
         *  1. Lo ADU Rosh - Rosh Hashana can't fall on a Sunday, Wednesday or Friday. If the molad fell on one of these
         * days, Rosh Hashana is delayed to the following day.
         *  1. Molad Zaken - If the molad of Tishrei falls after 12 noon, Rosh Hashana is delayed to the following day. If
         * the following day is ADU, it will be delayed an additional day.
         *  1. GaTRaD - If on a non leap year the molad of Tishrei falls on a Tuesday (Ga) on or after 9 hours (T) and 204
         * chalakim (TRaD) it is delayed till Thursday (one day delay, plus one day for Lo ADU Rosh)
         *  1. BeTuTaKFoT - if the year following a leap year falls on a Monday (Be) on or after 15 hours (Tu) and 589
         * chalakim (TaKFoT) it is delayed till Tuesday
         *
         *
         * @param year the year
         * @param moladDay the molad day
         * @param moladParts the molad parts
         * @return the number of elapsed days in the JewishCalendar adjusted for the 4 dechiyos.
         */
        private fun addDechiyos(year: Int, moladDay: Int, moladParts: Int): Int = addDechiyos(year.toLong(), moladDay.toLong(), moladParts).toInt()
        private fun addDechiyos(year: Long, moladDay: Long, moladParts: Int): Long {
            var roshHashanaDay = moladDay // if no dechiyos
            // delay Rosh Hashana for the dechiyos of the Molad - new moon 1 - Molad Zaken, 2- GaTRaD 3- BeTuTaKFoT
            if (((moladParts >= 19440) // Dechiya of Molad Zaken - molad is >= midday (18 hours * 1080 chalakim)
                        || ((((moladDay % 7L) == 2L) // start Dechiya of GaTRaD - Ga = is a Tuesday
                        && (moladParts >= 9924) // TRaD = 9 hours, 204 parts or later (9 * 1080 + 204)
                        && !year.isJewishLeapYear)) // of a non-leap year - end Dechiya of GaTRaD
                        || ((((moladDay % 7L) == 1L) // start Dechiya of BeTuTaKFoT - Be = is on a Monday
                        && (moladParts >= 16789) // TRaD = 15 hours, 589 parts or later (15 * 1080 + 589)
                        && (year - 1).isJewishLeapYear)))
            ) { // in a year following a leap year - end Dechiya of BeTuTaKFoT
                roshHashanaDay += 1 // Then postpone Rosh HaShanah one day
            }
            // start 4th Dechiya - Lo ADU Rosh - Rosh Hashana can't occur on A- sunday, D- Wednesday, U - Friday
            if ((((roshHashanaDay % 7L) == 0L) // If Rosh HaShanah would occur on Sunday,
                        || ((roshHashanaDay % 7L) == 3L) // or Wednesday,
                        || ((roshHashanaDay % 7L) == 5L))
            ) { // or Friday - end 4th Dechiya - Lo ADU Rosh
                roshHashanaDay += 1 // Then postpone it one (more) day
            }
            return roshHashanaDay
        }

        /**
         * Returns the number of chalakim (parts - 1080 to the hour) from the original hypothetical Molad Tohu to the year
         * and month passed in.
         *
         * @param year
         * the Jewish year
         * @param month
         * the Jewish month the Jewish month, with the month numbers starting from Nisan. Use the JewishDate
         * constants such as [HebrewMonth.TISHREI].
         * @return the number of chalakim (parts - 1080 to the hour) from the original hypothetical Molad Tohu
         */
        private fun getChalakimSinceMoladTohu(year: Long, month: HebrewMonth): Long {
            // Jewish lunar month = 29 days, 12 hours and 793 chalakim
            // chalakim since Molad Tohu BeHaRaD - 1 day, 5 hours and 204 chalakim
            val monthNumberOfYear = month.getTishreiBasedValueInYear(year)
            val monthsElapsed =
                ((235 * ((year - 1) / 19)) // Months in complete 19 year lunar (Metonic) cycles so far
                        + (12 * ((year - 1) % 19)) // Regular months in this cycle
                        + ((7 * ((year - 1) % 19) + 1) / 19) // Leap months this cycle
                        + (monthNumberOfYear - 1)) // add elapsed months till the start of the molad of the month
            // return chalakim prior to BeHaRaD + number of chalakim since
            return CHALAKIM_MOLAD_TOHU + (CHALAKIM_PER_MONTH * monthsElapsed)
        }

        /**
         * Validates the components of a Jewish date for validity. It will throw an [IllegalArgumentException] if the
         * Jewish date is earlier than 18 Teves, 3761 (1/1/1 Gregorian), a month < 1 or > 12 (or 13 on a
         * [leap year][isJewishLeapYear]), the day of month is < 1 or > 30, an hour < 0 or > 23, a minute < 0
         * or > 59 or chalakim < 0 or > 17. For larger a larger number of chalakim such as 793 (TaShTzaG) break the chalakim into
         * minutes (18 chalakim per minutes, so it would be 44 minutes and 1 chelek in the case of 793/TaShTzaG).
         *
         * @param year
         * the Jewish year to validate. It will reject any year <= 3761 (lower than the year 1 Gregorian).
         * @param month
         * the Jewish month to validate. It will reject a month < 1 or > 12 (or 13 on a leap year) .
         * @param dayOfMonth
         * the day of the Jewish month to validate. It will reject any value < 1 or > 30 TODO: check calling
         * methods to see if there is any reason that the class can validate that 30 is invalid for some months.
         * @param hours
         * the hours (for molad calculations). It will reject an hour < 0 or > 23
         * @param minutes
         * the minutes (for molad calculations). It will reject a minute < 0 or > 59
         * @param chalakim
         * the chalakim/parts (for molad calculations). It will reject a chalakim < 0 or > 17. For larger numbers
         * such as 793 (TaShTzaG) break the chalakim into minutes (18 chalakim per minutes, so it would be 44
         * minutes and 1 chelek in the case of 793/TaShTzaG)
         *
         * @throws IllegalArgumentException
         * if a A Jewish date earlier than 18 Teves, 3761 (1/1/1 Gregorian), a month < 1 or > 12 (or 13 on a
         * leap year), the day of month is < 1 or > 30, an hour < 0 or > 23, a minute < 0 or > 59 or
         * chalakim < 0 or > 17. For larger a larger number of chalakim such as 793 (TaShTzaG) break the chalakim
         * into minutes (18 chalakim per minutes, so it would be 44 minutes and 1 chelek in the case of 793 (TaShTzaG).
         */
        private fun validateJewishDate(
            hours: Int,
            minutes: Int,
            chalakim: Int
        ) {
            require(!(hours < 0 || hours > 23)) { "Hours < 0 or > 23 can't be set. $hours is invalid." }
            require(!(minutes < 0 || minutes > 59)) { "Minutes < 0 or > 59 can't be set. $minutes is invalid." }
            require(!(chalakim < 0 || chalakim > 17)) { "Chalakim/parts < 0 or > 17 can't be set. $chalakim is invalid. For larger numbers such as 793 (TaShTzaG) break the chalakim into minutes (18 chalakim per minutes, so it would be 44 minutes and 1 chelek in the case of 793 (TaShTzaG)" }
        }

        /**
         * Returns the number of days for a given Jewish year. ND+ER
         *
         * @param year
         * the Jewish year
         * @return the number of days for a given Jewish year.
         * @see isCheshvanLong
         * @see isKislevShort
         */
        val Int.daysInJewishYear get() = getJewishCalendarElapsedDays(this + 1) - getJewishCalendarElapsedDays(this)
        val Long.daysInJewishYear get() = (getJewishCalendarElapsedDays(this + 1) - getJewishCalendarElapsedDays(this)).toInt()

        /**
         * Returns if Cheshvan is long in a given Jewish year. The method name isLong is done since in a Kesidran (ordered)
         * year Cheshvan is short. ND+ER
         *
         * @param year
         * the year
         * @return true if Cheshvan is long in Jewish year.
         * @see isCheshvanLong
         * @see getCheshvanKislevKviah
         */
        val Int.isCheshvanLong get() = this.daysInJewishYear % 10 == 5
        val Long.isCheshvanLong get() = this.daysInJewishYear % 10L == 5L

        /**
         * Returns if Kislev is short (29 days VS 30 days) in a given Jewish year. The method name isShort is done since in
         * a Kesidran (ordered) year Kislev is long. ND+ER
         *
         * @param year
         * the Jewish year
         * @return true if Kislev is short for the given Jewish year.
         * @see isKislevShort
         * @see cheshvanKislevKviah
         */
        val Long.isKislevShort get() = this.daysInJewishYear % 10L == 3L
        val Int.isKislevShort get() = this.daysInJewishYear % 10 == 3

        /**
         * Returns the number of days of a Jewish month for a given month and year.
         *
         * @param month
         * the Jewish month
         * @param year
         * the Jewish Year
         * @return the number of days for a given Jewish month
         */
        internal fun getDaysInJewishMonth(month: HebrewMonth, year: Int): Int = getDaysInJewishMonth(month, year.toLong())
        internal fun getDaysInJewishMonth(month: HebrewMonth, year: Long): Int = if (
            (
                    (month == HebrewMonth.IYAR) ||
                            (month == HebrewMonth.TAMMUZ) ||
                            (month == HebrewMonth.ELUL) ||
                            (month == HebrewMonth.CHESHVAN && !year.isCheshvanLong) ||
                            (month == HebrewMonth.KISLEV && year.isKislevShort) ||
                            (month == HebrewMonth.TEVES) ||
                            (month == HebrewMonth.ADAR && !year.isJewishLeapYear) ||
                            (month == HebrewMonth.ADAR_II)
                    )
        ) 29 else 30

        /**
         * Returns the number of days from the Jewish epoch from the number of chalakim from the epoch passed in.
         *
         * @param chalakim
         * the number of chalakim since the beginning of Sunday prior to BaHaRaD
         * @return the number of days from the Jewish epoch
         */
        private fun moladToAbsDate(chalakim: Long): Int = (chalakim / CHALAKIM_PER_DAY).toInt() + HebrewLocalDate.JEWISH_EPOCH

        /**
         * returns the number of days from Rosh Hashana of the date passed in, to the full date passed in.
         *
         * @param year
         * the Jewish year
         * @param month
         * the Jewish month
         * @param dayOfMonth
         * the day in the Jewish month
         * @return the number of days
         */
        fun getDaysSinceStartOfJewishYear(year: Long, month: HebrewMonth, dayOfMonth: Int): Int {
            var elapsedDays = dayOfMonth
            // Before Tishrei (from Nissan to Tishrei), add days in prior months
            if (month < HebrewMonth.TISHREI) {
                // this year before and after Nisan.
                for (m in HebrewMonth.TISHREI..getLastMonthOfJewishYear(year)) elapsedDays += getDaysInJewishMonth(m, year)
                for (m in HebrewMonth.NISSAN until month) elapsedDays += getDaysInJewishMonth(m, year)
            } else { // Add days in prior months this year
                for (m in HebrewMonth.TISHREI until month) elapsedDays += getDaysInJewishMonth(m, year)
            }
            return elapsedDays
        }
    }
}