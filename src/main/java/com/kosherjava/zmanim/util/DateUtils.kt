package com.kosherjava.zmanim.util

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

import kotlin.math.floor

/**
 * TODO this is debateably an anti-pattern. Figure out where to put these functinos.
 * */
object DateUtils {
    /**
     * Return the [Julian day](http://en.wikipedia.org/wiki/Julian_day) from a Java LocalDate.
     *
     * @param date
     * The Java LocalDate of the date to be calculated
     * @return the Julian day number corresponding to the date  Note: Number is returned for start of day. Fractional days
     * should be added later.
     */
    fun getJulianDay(date: LocalDate): Double {
        var year = date.year
        var month = date.monthNumber
        val day = date.dayOfMonth
        if (month <= 2) {
            year -= 1
            month += 12
        }
        val a = year / 100
        val b = 2 - a + a / 4
        return floor(365.25 * (year + 4716)) + floor(30.6001 * (month + 1)) + day + b - 1524.5
    }
    fun LocalDate.Companion.now() =
        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
}