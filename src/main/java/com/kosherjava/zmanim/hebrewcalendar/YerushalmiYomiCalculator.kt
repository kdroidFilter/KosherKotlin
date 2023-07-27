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

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.daysUntil
import kotlinx.datetime.toInstant
import kotlin.time.Duration.Companion.days


/**
 * This class calculates the [Talmud Yerusalmi](https://en.wikipedia.org/wiki/Jerusalem_Talmud) [Daf Yomi](https://en.wikipedia.org/wiki/Daf_Yomi) page ([Daf]) for the a given date.
 *
 * @author  elihaidv
 * @author  Eliyahu Hershfeld 2017 - 2023
 */
object YerushalmiYomiCalculator {
    var timeZone = TimeZone.currentSystemDefault()

    /**
     * The start date of the first Daf Yomi Yerushalmi cycle of February 2, 1980 / 15 Shevat, 5740.
     */
    private val DAF_YOMI_START_DAY: Instant =
        LocalDateTime(
            1980, Month.FEBRUARY, 2,
            0, 0, 0, 0
        )
            .toInstant(timeZone)


    /** The number of milliseconds in a day.  */
    private const val DAY_MILIS: Int = 1000 * 60 * 60 * 24

    /** The number of pages in the Talmud Yerushalmi. */
    private const val WHOLE_SHAS_DAFS: Int = 1554

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
        val requested = LocalDateTime(
            calendar.gregorianLocalDate,
            LocalTime(0, 0, 0)
        )
            .toInstant(timeZone)
        var masechta = 0
        var dafYomi: Daf? = null

        // There isn't Daf Yomi on Yom Kippur or Tisha B'Av.
        if (calendar.yomTovIndex == JewishCalendar.YOM_KIPPUR ||
            calendar.yomTovIndex == JewishCalendar.TISHA_BEAV
        ) {
            return null
        }
        require(requested >= DAF_YOMI_START_DAY) { "$requested is prior to organized Daf Yomi Yerushalmi cycles that started on $DAF_YOMI_START_DAY" }

        // Start to calculate current cycle. init the start day
        var nextCycle = DAF_YOMI_START_DAY
        var prevCycle = Clock.System.now()

        // Go cycle by cycle, until we get the next cycle
        while (requested > nextCycle) {
            prevCycle = nextCycle

            // Adds the number of whole shas dafs. and the number of days that not have daf.
            nextCycle += WHOLE_SHAS_DAFS.days
            nextCycle += getNumOfSpecialDays(prevCycle, nextCycle).days
        }

        // Get the number of days from cycle start until request.
        val dafNo = prevCycle.daysUntil(requested, timeZone)

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
    private fun getNumOfSpecialDays(start: Instant, end: Instant): Int {

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
            val timeZone = TimeZone.currentSystemDefault()
            val startToEnd = start..end
            if (yom_kippur.gregorianLocalDate.atStartOfDayIn(timeZone) in startToEnd) specialDays++
            if (tisha_beav.gregorianLocalDate.atStartOfDayIn(timeZone) in startToEnd) specialDays++
        }
        return specialDays
    }
}