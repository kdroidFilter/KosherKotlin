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

import kotlinx.datetime.Instant


import kotlin.Comparator

/**
 * A wrapper class for a astronomical times / *zmanim* that is mostly intended to allow sorting collections of astronomical times.
 * It has fields for both date/time and duration based *zmanim*, name / labels as well as a longer description or explanation of a
 * *zman*.
 *
 * Here is an example of various ways of sorting *zmanim*.
 *
 * First create the Calendar for the location you would like to calculate:
 *
 * <pre style="background: #FEF0C9; display: inline-block;">
 * String locationName = &quot;Lakewood, NJ&quot;;
 * double latitude = 40.0828; // Lakewood, NJ
 * double longitude = -74.2094; // Lakewood, NJ
 * double elevation = 20; // optional elevation correction in Meters
 * // the String parameter in timeZone has to be a valid timezone listed in [java.util.TimeZone.getAvailableIDs]
 * TimeZone timeZone = TimeZone.getTimeZone(&quot;America/New_York&quot;);
 * GeoLocation location = new GeoLocation(locationName, latitude, longitude, elevation, timeZone);
 * ComplexZmanimCalendar czc = new ComplexZmanimCalendar(location);
 * Zman sunset = new Zman(czc.getSunset(), "Sunset");
 * Zman shaah16 = new Zman(czc.getShaahZmanis16Point1Degrees(), "Shaah zmanis 16.1");
 * Zman sunrise = new Zman(czc.getSunrise(), "Sunrise");
 * Zman shaah = new Zman(czc.getShaahZmanisGra(), "Shaah zmanis GRA");
 * ArrayList<Zman> zl = new ArrayList<Zman>();
 * zl.add(sunset);
 * zl.add(shaah16);
 * zl.add(sunrise);
 * zl.add(shaah);
 * //will sort sunset, shaah 1.6, sunrise, shaah GRA
 * System.out.println(zl);
 * Collections.sort(zl, Zman.DATE_ORDER);
 * // will sort sunrise, sunset, shaah, shaah 1.6 (the last 2 are not in any specific order)
 * Collections.sort(zl, Zman.DURATION_ORDER);
 * // will sort sunrise, sunset (the first 2 are not in any specific order), shaah GRA, shaah 1.6
 * Collections.sort(zl, Zman.NAME_ORDER);
 * // will sort shaah 1.6, shaah GRA, sunrise, sunset
</pre> *
 *
 * @author  Eliyahu Hershfeld 2007-2020
 * @todo Add secondary sorting. As of now the `Comparator`s in this class do not sort by secondary order. This means that when sorting a
 * [java.util.Collection] of *zmanim* and using the [DATE_ORDER] `Comparator` will have the duration based *zmanim*
 * at the end, but they will not be sorted by duration. This should be N/A for label based sorting.
 */
data class Zman(
    /**
     * The name / label of the *zman*, such as "*Sof Zman Krias Shema GRA*". 
     * There are no automatically set labels.
     */
    var label: String,
    /**
     * Returns the `Date` based *zman*.
     * @return the *zman*.
     * @see .setZman
     */
    /**
     * Sets a `Date` based *zman*.
     * @param date a `Date` based *zman*
     * @see .getZman
     */
    /**
     * The [Date] of the *zman*
     */
    var zman: Instant? = null,
    /**
     * Returns a duration based *zman* such as [temporal hour][com.kosherjava.zmanim.AstronomicalCalendar.getTemporalHour]
     * (or the various *shaah zmanis* times such as [<em>shaah zmanis GRA</em>][com.kosherjava.zmanim.ZmanimCalendar.getShaahZmanisGra]
     * or [<em>shaah zmanis 16.1&amp;deg;</em>][com.kosherjava.zmanim.ComplexZmanimCalendar.getShaahZmanis16Point1Degrees]).
     * @return the duration based *zman*.
     * @see .setDuration
     */
    /**
     * Sets a duration based *zman* such as [temporal hour][com.kosherjava.zmanim.AstronomicalCalendar.getTemporalHour]
     * (or the various *shaah zmanis* times as [<em>shaah zmanis GRA</em>][com.kosherjava.zmanim.ZmanimCalendar.getShaahZmanisGra] or
     * [<em>shaah zmanis 16.1&amp;deg;</em>][com.kosherjava.zmanim.ComplexZmanimCalendar.getShaahZmanis16Point1Degrees]).
     * @param duration duration based *zman* such as [com.kosherjava.zmanim.AstronomicalCalendar.getTemporalHour].
     * @see .getDuration
     */
    /**
     * The duration if the *zman* is a [temporal hour][com.kosherjava.zmanim.AstronomicalCalendar.temporalHour] (or the various
     * *shaah zmanis* base times such as [<em>shaah Zmanis GRA</em>][com.kosherjava.zmanim.ZmanimCalendar.shaahZmanisGra] or
     * [<em>shaah Zmanis 16.1&amp;deg;</em>][com.kosherjava.zmanim.ComplexZmanimCalendar.shaahZmanis16Point1Degrees]).
     */
    var duration: Long? = null,
    /**
     * Returns the longer description or explanation of a *zman*. There is no default value for this and it must be set using
     * [description]
     * @return the description or explanation of a *zman*.
     * @see description
     */
    /**
     * Sets the longer description or explanation of a *zman*.
     * @param description
     * the *zman* description to set.
     * @see .getDescription
     */
    /**
     * A longer description or explanation of a *zman*.
     */
    var description: String? = null,
) {
    init {
        require(duration != null || zman != null) { "Either zman or duration must be non-null." }
    }

    /**
     * The constructor setting a [Date] based *zman* and a label.
     * @param date the Date of the *zman*.
     * @param label the label of the  *zman* such as "*Sof Zman Krias Shema GRA*".
     * @see .Zman
     */
    /*constructor(date: Date?, label: String) {
        this.label = label
        zman = date
    }*/

    /**
     * The constructor setting a duration based *zman* such as
     * [temporal hour][com.kosherjava.zmanim.AstronomicalCalendar.getTemporalHour] (or the various *shaah zmanis* times such as
     * [<em>shaah zmanis GRA</em>][com.kosherjava.zmanim.ZmanimCalendar.getShaahZmanisGra] or
     * [<em>shaah Zmanis 16.1&amp;deg;</em>][com.kosherjava.zmanim.ComplexZmanimCalendar.getShaahZmanis16Point1Degrees]) and label.
     * @param duration a duration based *zman* such as ([com.kosherjava.zmanim.AstronomicalCalendar.getTemporalHour]
     * @param label the label of the  *zman* such as "*Shaah Zmanis GRA*".
     * @see .Zman
     */
    /*constructor(duration: Long, label: String) {
        this.label = label
        this.duration = duration
    }*/

    /**
     * @see Object.toString
     */
    override fun toString(): String {
        val sb = StringBuilder()
        sb.append("\nLabel:\t\t\t").append(label)
        sb.append("\nZman:\t\t\t").append(zman)
        sb.append("\nDuration:\t\t\t").append(duration)
        sb.append("\nDescription:\t\t\t").append(description)
        return sb.toString()
    }

    companion object {
        /**
         * A [Comparator] that will compare and sort *zmanim* by date/time order. Compares its two arguments by the zman's date/time
         * order. Returns a negative integer, zero, or a positive integer as the first argument is less than, equal to, or greater
         * than the second.
         * Please note that this class will handle cases where either the `Zman` is a null or [.getZman] returns a null.
         */
        val DATE_ORDER: Comparator<Zman?> = compareBy { it?.zman ?: Long.MAX_VALUE }

        /**
         * A [Comparator] that will compare and sort zmanim by zmanim label order. Compares its two arguments by the zmanim label
         * name order. Returns a negative integer, zero, or a positive integer as the first argument is less than, equal to, or greater
         * than the second.
         * Please note that this class will will sort cases where either the `Zman` is a null or [.label] returns a null
         * as empty `String`s.
         */
        val NAME_ORDER: Comparator<Zman?> = compareBy { it?.label ?: "" }

        /**
         * A [Comparator] that will compare and sort duration based *zmanim*  such as
         * [temporal hour][com.kosherjava.zmanim.AstronomicalCalendar.getTemporalHour] (or the various *shaah zmanis* times
         * such as *[shaah zmanis GRA][com.kosherjava.zmanim.ZmanimCalendar.getShaahZmanisGra]* or
         * [<em>shaah zmanis 16.1&amp;deg;</em>][com.kosherjava.zmanim.ComplexZmanimCalendar.getShaahZmanis16Point1Degrees]). Returns a negative
         * integer, zero, or a positive integer as the first argument is less than, equal to, or greater than the second.
         * Please note that this class will will sort cases where `Zman` is a null.
         */
        val DURATION_ORDER: Comparator<Zman?> = compareBy { it?.duration ?: Long.MAX_VALUE }
    }
}