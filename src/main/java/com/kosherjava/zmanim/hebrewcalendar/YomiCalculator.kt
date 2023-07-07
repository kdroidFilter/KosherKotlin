/*
 * Zmanim Java API
 * Copyright (C) 2011-2020 Eliyahu Hershfeld
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
 * This class calculates the Daf Yomi Bavli page (daf) for a given date. To calculate Daf Yomi Yerushalmi
 * use the [YerushalmiYomiCalculator]. The library may cover Mishna Yomi etc. at some point in the future.
 *
 * @author  Bob Newell (original C code)
 * @author  Eliyahu Hershfeld 2011 - 2020
 */
object YomiCalculator {
    /**
     * The start date of the first Daf Yomi Bavli cycle of September 11, 1923 / Rosh Hashana 5684.
     */
    private val dafYomiStartDay: Calendar = GregorianCalendar(1923, Calendar.SEPTEMBER, 11)

    /** The start date of the first Daf Yomi Bavli cycle in the Julian calendar. Used internally for claculations. */
    private val dafYomiJulianStartDay: Int = getJulianDay(dafYomiStartDay)

    /**
     * The date that the pagination for the Daf Yomi *Maseches Shekalim* changed to use the commonly used Vilna
     * Shas pagination from the no longer commonly available Zhitomir / Slavuta Shas used by Rabbi Meir Shapiro.
     */
    private val shekalimChangeDay: Calendar = GregorianCalendar(1975, Calendar.JUNE, 24)

    /** The Julian date that the cycle for Shekalim changed.
     * @see .getDafYomiBavli
     */
    private val shekalimJulianChangeDay: Int = getJulianDay(shekalimChangeDay)

    /**
     * Returns the [Daf Yomi](http://en.wikipedia.org/wiki/Daf_yomi) [Bavli](http://en.wikipedia.org/wiki/Talmud) [Daf] for a given date. The first Daf Yomi cycle
     * started on Rosh Hashana 5684 (September 11, 1923) and calculations prior to this date will result in an
     * IllegalArgumentException thrown. For historical calculations (supported by this method), it is important to note
     * that a change in length of the cycle was instituted starting in the eighth Daf Yomi cycle beginning on June 24,
     * 1975. The Daf Yomi Bavli cycle has a single masechta of the Talmud Yerushalmi - Shekalim as part of the cycle.
     * Unlike the Bavli where the number of daf per masechta was standardized since the original [Bomberg Edition](http://en.wikipedia.org/wiki/Daniel_Bomberg) published from 1520 - 1523, there is no
     * uniform page length in the Yerushalmi. The early cycles had the Yerushalmi Shekalim length of 13 days following the
     * [Slavuta/Zhytomyr](https://he.wikipedia.org/wiki/%D7%93%D7%A4%D7%95%D7%A1_%D7%A1%D7%9C%D7%90%D7%95%D7%95%D7%99%D7%98%D7%90)
     * Shas used by [Rabbi Meir Shapiro](http://en.wikipedia.org/wiki/Meir_Shapiro). With the start of the eighth Daf Yomi
     * cycle beginning on June 24, 1975 the length of the Yerushalmi Shekalim was changed from 13 to 22 daf to follow
     * the [Vilna Shas](https://en.wikipedia.org/wiki/Vilna_Edition_Shas) that is in common use today.
     *
     * @param jewishCalendar
     * The JewishCalendar date for calculation. TODO: this can be changed to use a regular GregorianCalendar since
     * there is nothing specific to the JewishCalendar in this class.
     * @return the [Daf].
     *
     * @throws IllegalArgumentException
     * if the date is prior to the September 11, 1923 start date of the first Daf Yomi cycle
     */
    fun getDafYomiBavli(jewishCalendar: JewishCalendar): Daf? {
        /*
		 * The number of daf per masechta. Since the number of blatt in Shekalim changed on the 8th Daf Yomi cycle
		 * beginning on June 24, 1975 from 13 to 22, the actual calculation for blattPerMasechta[4] will later be
		 * adjusted based on the cycle.
		 */
        val blattPerMasechta: IntArray = intArrayOf(
            64, 157, 105, 121, 22, 88, 56, 40, 35, 31, 32, 29, 27, 122, 112, 91, 66, 49, 90, 82,
            119, 119, 176, 113, 24, 49, 76, 14, 120, 110, 142, 61, 34, 34, 28, 22, 4, 9, 5, 73
        )
        val calendar: Calendar = jewishCalendar.gregorianCalendar
        var dafYomi: Daf? = null
        val julianDay: Int = getJulianDay(calendar)
        val cycleNo: Int
        val dafNo: Int
        if (calendar.before(dafYomiStartDay)) {
            // TODO: should we return a null or throw an IllegalArgumentException?
            throw IllegalArgumentException(
                (calendar.toString() + " is prior to organized Daf Yomi Bavli cycles that started on "
                        + dafYomiStartDay)
            )
        }
        if ((calendar == shekalimChangeDay) || calendar.after(shekalimChangeDay)) {
            cycleNo = 8 + ((julianDay - shekalimJulianChangeDay) / 2711)
            dafNo = ((julianDay - shekalimJulianChangeDay) % 2711)
        } else {
            cycleNo = 1 + ((julianDay - dafYomiJulianStartDay) / 2702)
            dafNo = ((julianDay - dafYomiJulianStartDay) % 2702)
        }
        var total: Int = 0
        var masechta: Int = -1
        var blatt: Int = 0

        // Fix Shekalim for old cycles.
        if (cycleNo <= 7) {
            blattPerMasechta[4] = 13
        } else {
            blattPerMasechta[4] = 22 // correct any change that may have been changed from a prior calculation
        }
        // Finally, find the daf.
        for (j in blattPerMasechta.indices) {
            masechta++
            total = total + blattPerMasechta[j] - 1
            if (dafNo < total) {
                blatt = 1 + blattPerMasechta[j] - (total - dafNo)
                // Fiddle with the weird ones near the end.
                when (masechta) {
                    36 -> blatt += 21
                    37 -> blatt += 24
                    38 -> blatt += 32
                }
                dafYomi = Daf(masechta, blatt)
                break
            }
        }
        return dafYomi
    }

    /**
     * Return the [Julian day](http://en.wikipedia.org/wiki/Julian_day) from a Java Calendar.
     *
     * @param calendar
     * The Java Calendar of the date to be calculated
     * @return the Julian day number corresponding to the date
     */
    private fun getJulianDay(calendar: Calendar?): Int {
        var year: Int = calendar!!.get(Calendar.YEAR)
        var month: Int = calendar.get(Calendar.MONTH) + 1
        val day: Int = calendar.get(Calendar.DAY_OF_MONTH)
        if (month <= 2) {
            year -= 1
            month += 12
        }
        val a: Int = year / 100
        val b: Int = 2 - a + a / 4
        return (Math.floor(365.25 * (year + 4716)) + Math.floor(30.6001 * (month + 1)) + day + b - 1524.5).toInt()
    }
}