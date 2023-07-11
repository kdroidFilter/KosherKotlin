package com.kosherjava.zmanim.hebrewcalendar

import java.util.*

/**
 * TODO this is debateably an anti-pattern. Figure out where to put these functinos.
 * */
object DateUtils {
    /**
     * Return the [Julian day](http://en.wikipedia.org/wiki/Julian_day) from a Java Calendar.
     *
     * @param calendar
     * The Java Calendar of the date to be calculated
     * @return the Julian day number corresponding to the date  Note: Number is returned for start of day. Fractional days
     * should be added later.
     */
    fun getJulianDay(calendar: Calendar?): Double {
        var year = calendar!!.get(Calendar.YEAR)
        var month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        if (month <= 2) {
            year -= 1
            month += 12
        }
        val a = year / 100
        val b = 2 - a + a / 4
        return Math.floor(365.25 * (year + 4716)) + Math.floor(30.6001 * (month + 1)) + day + b - 1524.5
    }
}