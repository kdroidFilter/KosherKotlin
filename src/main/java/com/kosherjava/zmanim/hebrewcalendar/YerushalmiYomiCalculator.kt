/*
 * Zmanim Java API
 * Copyright (C) 2017 - 2023 Eliyahu Hershfeld
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General
 * Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful,but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to
 * the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA,
 * or connect to: http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 */
package com.kosherjava.zmanim.hebrewcalendar

import com.kosherjava.zmanim.util.ZmanimFormatter
import java.util.TimeZone
import java.lang.StringBuffer
import com.kosherjava.zmanim.util.Zman
import java.lang.IllegalArgumentException
import com.kosherjava.zmanim.util.GeoLocation
import java.lang.StringBuilder
import java.lang.CloneNotSupportedException
import com.kosherjava.zmanim.util.AstronomicalCalculator
import java.util.Calendar
import com.kosherjava.zmanim.util.NOAACalculator
import java.text.SimpleDateFormat
import java.text.DecimalFormat
import java.text.DateFormat
import java.util.Collections
import com.kosherjava.zmanim.util.GeoLocationUtils
import com.kosherjava.zmanim.util.SunTimesCalculator
import com.kosherjava.zmanim.hebrewcalendar.Daf
import com.kosherjava.zmanim.hebrewcalendar.JewishDate
import java.time.LocalDate
import java.util.GregorianCalendar
import com.kosherjava.zmanim.hebrewcalendar.HebrewDateFormatter
import com.kosherjava.zmanim.hebrewcalendar.JewishCalendar
import com.kosherjava.zmanim.hebrewcalendar.JewishCalendar.Parsha
import com.kosherjava.zmanim.hebrewcalendar.YomiCalculator
import com.kosherjava.zmanim.hebrewcalendar.YerushalmiYomiCalculator
import java.util.EnumMap
import com.kosherjava.zmanim.AstronomicalCalendar
import com.kosherjava.zmanim.ZmanimCalendar
import java.math.BigDecimal
import com.kosherjava.zmanim.ComplexZmanimCalendar

/**
 * This class calculates the [Talmud Yerusalmi](https://en.wikipedia.org/wiki/Jerusalem_Talmud) [Daf Yomi](https://en.wikipedia.org/wiki/Daf_Yomi) page ([Daf]) for the a given date.
 *
 * @author  elihaidv
 * @author  Eliyahu Hershfeld 2017 - 2023
 */
object YerushalmiYomiCalculator {
    /**
     * The start date of the first Daf Yomi Yerushalmi cycle of February 2, 1980 / 15 Shevat, 5740.
     */
    private val DAF_YOMI_START_DAY: Calendar = GregorianCalendar(1980, Calendar.FEBRUARY, 2)

    /** The number of milliseconds in a day.  */
    private val DAY_MILIS: Int = 1000 * 60 * 60 * 24

    /** The number of pages in the Talmud Yerushalmi. */
    private val WHOLE_SHAS_DAFS: Int = 1554

    /** The number of pages per *masechta* (tractate). */
    private val BLATT_PER_MASECHTA: IntArray = intArrayOf(
        68, 37, 34, 44, 31, 59, 26, 33, 28, 20, 13, 92, 65, 71, 22, 22, 42, 26, 26, 33, 34, 22,
        19, 85, 72, 47, 40, 47, 54, 48, 44, 37, 34, 44, 9, 57, 37, 19, 13
    )

    /**
     * Returns the [Daf Yomi](https://en.wikipedia.org/wiki/Daf_Yomi)
     * [Yerusalmi](https://en.wikipedia.org/wiki/Jerusalem_Talmud) page ([Daf]) for a given date.
     * The first Daf Yomi cycle started on 15 Shevat (Tu Bishvat), 5740 (February, 2, 1980) and calculations
     * prior to this date will result in an IllegalArgumentException thrown. A null will be returned on Tisha B'Av or
     * Yom Kippur.
     *
     * @param calendar
     * the calendar date for calculation
     * @return the [Daf] or null if the date is on Tisha B'Av or Yom Kippur.
     *
     * @throws IllegalArgumentException
     * if the date is prior to the February 2, 1980, the start of the first Daf Yomi Yerushalmi cycle
     */
    fun getDafYomiYerushalmi(calendar: JewishCalendar): Daf? {
        val nextCycle: Calendar = GregorianCalendar()
        val prevCycle: Calendar = GregorianCalendar()
        val requested: Calendar = calendar.gregorianCalendar
        var masechta = 0
        var dafYomi: Daf? = null

        // There isn't Daf Yomi on Yom Kippur or Tisha B'Av.
        if (calendar.yomTovIndex == JewishCalendar.YOM_KIPPUR ||
            calendar.yomTovIndex == JewishCalendar.TISHA_BEAV
        ) {
            return null
        }
        require(!requested.before(DAF_YOMI_START_DAY)) { "$requested is prior to organized Daf Yomi Yerushalmi cycles that started on $DAF_YOMI_START_DAY" }

        // Start to calculate current cycle. init the start day
        nextCycle.time = DAF_YOMI_START_DAY.time

        // Go cycle by cycle, until we get the next cycle
        while (requested.after(nextCycle)) {
            prevCycle.time = nextCycle.time

            // Adds the number of whole shas dafs. and the number of days that not have daf.
            nextCycle.add(Calendar.DAY_OF_MONTH, WHOLE_SHAS_DAFS)
            nextCycle.add(Calendar.DAY_OF_MONTH, getNumOfSpecialDays(prevCycle, nextCycle))
        }

        // Get the number of days from cycle start until request.
        val dafNo = getDiffBetweenDays(prevCycle, requested).toInt()

        // Get the number of special day to subtract
        val specialDays = getNumOfSpecialDays(prevCycle, requested)
        var total = dafNo - specialDays

        // Finally find the daf.
        for (j in BLATT_PER_MASECHTA.indices) {
            if (total < BLATT_PER_MASECHTA[j]) {
                dafYomi = Daf(masechta, total + 1)
                break
            }
            total -= BLATT_PER_MASECHTA[j]
            masechta++
        }
        return dafYomi
    }

    /**
     * Return the number of special days (Yom Kippur and Tisha Beav) That there is no Daf in this days.
     * From the last given number of days until given date
     *
     * @param start start date to calculate
     * @param end end date to calculate
     * @return the number of special days
     */
    private fun getNumOfSpecialDays(start: Calendar, end: Calendar): Int {

        // Find the start and end Jewish years
        val startYear = JewishCalendar(start).jewishYear
        val endYear = JewishCalendar(end).jewishYear

        // Value to return
        var specialDays = 0

        //Instant of special Dates
        val yom_kippur = JewishCalendar(5770, 7, 10)
        val tisha_beav = JewishCalendar(5770, 5, 9)

        // Go over the years and find special dates
        for (i in startYear..endYear) {
            yom_kippur.setJewishYear(i)
            tisha_beav.setJewishYear(i)
            if (isBetween(start, yom_kippur.gregorianCalendar, end)) {
                specialDays++
            }
            if (isBetween(start, tisha_beav.gregorianCalendar, end)) {
                specialDays++
            }
        }
        return specialDays
    }

    /**
     * Return if the date is between two dates
     *
     * @param start the start date
     * @param date the date being compared
     * @param end the end date
     * @return if the date is between the start and end dates
     */
    private fun isBetween(start: Calendar, date: Calendar?, end: Calendar?): Boolean {
        return start.before(date) && end!!.after(date)
    }

    /**
     * Return the number of days between the dates passed in
     * @param start the start date
     * @param end the end date
     * @return the number of days between the start and end dates
     */
    private fun getDiffBetweenDays(start: Calendar, end: Calendar): Long {
        return (end.timeInMillis - start.timeInMillis) / DAY_MILIS
    }
}