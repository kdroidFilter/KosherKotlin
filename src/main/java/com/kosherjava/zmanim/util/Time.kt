/*
 * Zmanim Java API
 * Copyright (C) 2004-2020 Eliyahu Hershfeld
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
package com.kosherjava.zmanim.util


import kotlinx.datetime.TimeZone
import kotlin.math.abs

/**
 * A class that represents a numeric time. Times that represent a time of day are stored as [java.util.Date]s in
 * this API. The time class is used to represent numeric time such as the time in hours, minutes, seconds and
 * milliseconds of a [temporal hour][com.kosherjava.zmanim.AstronomicalCalendar.getTemporalHour].
 *
 * This is a data class so that it inherits the [copy] method, and to semantically signify it is a data holder.
 * @author  Eliyahu Hershfeld 2004 - 2020
 */
data class Time(
    var hours: Int = 0,
    var minutes: Int = 0,
    var seconds: Int = 0,
    var milliseconds: Int = 0,
    /**
     * Does the time represent a negative time 9such as using this to subtract time from another Time.
     * True if the time is negative.
     */
    var isNegative: Boolean = false
) {

    /**
     * Constructor with a parameter for milliseconds. This constructor casts the milliseconds to an int and
     * calls a secondary constructo
     * @param millis the milliseconds to set the object with.
     */
    constructor(millis: Double) : this(millis.toInt())

    /**
     * A constructor that sets the time by milliseconds. The milliseconds are converted to hours, minutes, seconds
     * and milliseconds. If the milliseconds are negative it will set [isNegative] to true.
     * @param millis the milliseconds to set.
     */
    constructor(millis: Int) : this() {
        var adjustedMillis = millis
        if (adjustedMillis < 0) {
            isNegative = true
            adjustedMillis = abs(adjustedMillis)
        }
        hours = adjustedMillis / HOUR_MILLIS
        adjustedMillis -= hours * HOUR_MILLIS
        minutes = adjustedMillis / MINUTE_MILLIS
        adjustedMillis -= minutes * MINUTE_MILLIS
        seconds = adjustedMillis / SECOND_MILLIS
        adjustedMillis -= seconds * SECOND_MILLIS
        milliseconds = adjustedMillis
    }

    /**
     * Returns the time in milliseconds by converting hours, minutes and seconds into milliseconds.
     * @return the time in milliseconds
     */
    val time: Double
        get() = (hours * HOUR_MILLIS + minutes * MINUTE_MILLIS + seconds * SECOND_MILLIS + milliseconds).toDouble()

    /**
     * @see Object.toString
     */
//    override fun toString(): String {
//        return ZmanimFormatter(TimeZone.UTC).format(this)
//    }

    companion object {
        /** milliseconds in a second.  */
        private const val SECOND_MILLIS = 1000

        /** milliseconds in a minute.  */
        private const val MINUTE_MILLIS = SECOND_MILLIS * 60

        /** milliseconds in an hour.  */
        private const val HOUR_MILLIS = MINUTE_MILLIS * 60
    }
}