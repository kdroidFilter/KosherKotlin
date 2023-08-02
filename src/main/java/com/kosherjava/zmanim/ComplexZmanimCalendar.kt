/*
 * Zmanim Java API
 * Copyright (C) 2004-2023 Eliyahu Hershfeld
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General
 * Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful,but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more
 * details.
 * You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to
 * the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA,
 * or connect to: https://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 */
package com.kosherjava.zmanim

import com.kosherjava.zmanim.hebrewcalendar.JewishCalendar
import com.kosherjava.zmanim.util.AstronomicalCalculator
import com.kosherjava.zmanim.util.GeoLocation
import com.kosherjava.zmanim.util.GeoLocation.Companion.rawOffset
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.math.roundToLong


/**
 *
 * This class extends ZmanimCalendar and provides many more *zmanim* than available in the ZmanimCalendar. The basis
 * for most *zmanim* in this class are from the *sefer* **[Yisroel
 * Vehazmanim](https://hebrewbooks.org/9765)** by **[Rabbi Yisrael Dovid Harfenes](https://en.wikipedia.org/wiki/Yisroel_Dovid_Harfenes)**.
 * As an example of the number of different *zmanim* made available by this class, there are methods to return 18
 * different calculations for *alos* (dawn), 18 for *plag hamincha* and 29 for *tzais* available in this
 * API. The real power of this API is the ease in calculating *zmanim* that are not part of the library. The methods for
 * *zmanim* calculations not present in this class or it's superclass  [ZmanimCalendar] are contained in the
 * [AstronomicalCalendar], the base class of the calendars in our API since they are generic methods for calculating
 * time based on degrees or time before or after [sunrise] and [sunset] and are of interest
 * for calculation beyond *zmanim* calculations. Here are some examples.
 *
 * First create the Calendar for the location you would like to calculate:
 *
 * <pre style="background: #FEF0C9; display: inline-block;\">
 * String locationName = &quot;Lakewood, NJ&quot;;
 * double latitude = 40.0828; // Lakewood, NJ
 * double longitude = -74.2094; // Lakewood, NJ
 * double elevation = 20; // optional elevation correction in Meters
 * // the String parameter in timeZone has to be a valid timezone listed in
 * // [java.util.TimeZone.getAvailableIDs]
 * TimeZone timeZone = TimeZone.getTimeZone(&quot;America/New_York&quot;);
 * GeoLocation location = new GeoLocation(locationName, latitude, longitude, elevation, timeZone);
 * ComplexZmanimCalendar czc = new ComplexZmanimCalendar(location);
 * // Optionally set the date or it will default to today's date
 * czc.calendar.set(Calendar.MONTH, Calendar.FEBRUARY);
 * czc.calendar.set(Calendar.DAY_OF_MONTH, 8);</pre>
 *
 *
 * **Note:** For locations such as Israel where the beginning and end of daylight savings time can fluctuate from
 * year to year, if your version of Java does not have an [up to date timezone database](https://www.oracle.com/java/technologies/tzdata-versions.html), create a
 * [java.util.SimpleTimeZone] with the known start and end of DST.
 * To get *alos* calculated as 14 below the horizon (as calculated in the calendars published in Montreal),
 * add [AstronomicalCalendar.GEOMETRIC_ZENITH] (90) to the 14 offset to get the desired time:
 * <br></br><br></br>
 * <pre style="background: #FEF0C9; display: inline-block;\">
 * Date alos14 = czc.getSunriseOffsetByDegrees([AstronomicalCalendar.GEOMETRIC_ZENITH] + 14);</pre>
 *
 *
 * To get *mincha gedola* calculated based on the [Magen Avraham (MGA)](https://en.wikipedia.org/wiki/Avraham_Gombinern) using a *shaah zmanis* based on the day starting
 * 16.1 below the horizon (and ending 16.1 after sunset) the following calculation can be used:
 *
 * <pre style="background: #FEF0C9; display: inline-block;\">
 * Date minchaGedola = czc.getTimeOffset(czc.getAlos16point1Degrees(), czc.getShaahZmanis16Point1Degrees() * 6.5);</pre>
 *
 *
 * or even simpler using the included convenience methods
 * <pre style="background: #FEF0C9; display: inline-block;\">
 * Date minchaGedola = czc.getMinchaGedola(czc.getAlos16point1Degrees(), czc.getShaahZmanis16Point1Degrees());</pre>
 *
 *
 * A little more complex example would be calculating *zmanim* that rely on a *shaah zmanis* that is
 * not present in this library. While a drop more complex, it is still rather easy. An example would be to calculate
 * the [Trumas Hadeshen](https://en.wikipedia.org/wiki/Israel_Isserlein)'s *alos* to
 * *tzais* based *plag hamincha* as calculated in the Machzikei Hadass calendar in Manchester, England.
 * A number of this calendar's *zmanim* are calculated based on a day starting at *alos* of 12 before
 * sunrise and ending at *tzais* of 7.083 after sunset. Be aware that since the *alos* and *tzais*
 * do not use identical degree-based offsets, this leads to *chatzos* being at a time other than the
 * [solar transit][sunTransit] (solar midday). To calculate this *zman*, use the following steps. Note
 * that *plag hamincha* is 10.75 hours after the start of the day, and the following steps are all that it takes.
 * <br></br>
 * <pre style="background: #FEF0C9; display: inline-block;\">
 * Date plag = czc.getPlagHamincha(czc.getSunriseOffsetByDegrees([AstronomicalCalendar.GEOMETRIC_ZENITH] + 12),
 * czc.getSunsetOffsetByDegrees([AstronomicalCalendar.GEOMETRIC_ZENITH] + ZENITH_7_POINT_083));</pre>
 *
 *
 * Something a drop more challenging, but still simple, would be calculating a *zman* using the same "complex"
 * offset day used in the above mentioned Manchester calendar, but for a *shaos zmaniyos* based *zman* not
 * supported by this library, such as calculating the point that one should be *makpid*
 * not to eat on *erev Shabbos* or *erev Yom Tov*. This is 9 *shaos zmaniyos* into the day.
 *
 *  1. Calculate the *shaah zmanis* in milliseconds for this day
 *  1. Add 9 of these *shaos zmaniyos* to *alos* starting at 12
 *
 * <br></br>
 * <pre style="background: #FEF0C9; display: inline-block;\">
 * long shaahZmanis = czc.getTemporalHour(czc.getSunriseOffsetByDegrees([AstronomicalCalendar.GEOMETRIC_ZENITH] + 12),
 * czc.getSunsetOffsetByDegrees([AstronomicalCalendar.GEOMETRIC_ZENITH] + ZENITH_7_POINT_083));
 * Date sofZmanAchila = getTimeOffset(czc.getSunriseOffsetByDegrees([AstronomicalCalendar.GEOMETRIC_ZENITH] + 12),
 * shaahZmanis * 9);</pre>
 *
 *
 * Calculating this *sof zman achila* according to the [GRA](https://en.wikipedia.org/wiki/Vilna_Gaon)
 * is simplicity itself.
 * <pre style="background: #FEF0C9; display: inline-block;\">
 * Date sofZamnAchila = czc.getTimeOffset(czc.getSunrise(), czc.getShaahZmanisGra() * 9);</pre>
 *
 * <h2>Documentation from the [ZmanimCalendar] parent class</h2>
 * {@inheritDoc}
 *
 * @author  Eliyahu Hershfeld 2004 - 2023
 */
class ComplexZmanimCalendar(location: GeoLocation = GeoLocation()) : ZmanimCalendar(location) {
    /**
     * Returns the offset in minutes after sunset used to calculate *tzais* based on the calculations of
     * *Chacham* Yosef Harari-Raful of Yeshivat Ateret Torah calculations. The default value is 40 minutes.
     * This affects most *zmanim*, since almost all zmanim use subset as part of their calculation.
     *
     * @return the number of minutes after sunset for *Tzait*.
     * @see .setAteretTorahSunsetOffset
     */
    /**
     * Allows setting the offset in minutes after sunset for the Ateret Torah *zmanim*. The default if unset is
     * 40 minutes. *Chacham* Yosef Harari-Raful of Yeshivat Ateret Torah uses 40 minutes globally with the exception
     * of Israel where a 25 minute offset is used. This 40 minute (or any other) offset can be overridden by this method.
     * This offset impacts all Ateret Torah *zmanim*.
     *
     * @param ateretTorahSunsetOffset
     * the number of minutes after sunset to use as an offset for the Ateret Torah *tzais*
     * @see .getAteretTorahSunsetOffset
     */
    /**
     * The offset in minutes (defaults to 40) after sunset used for *tzeit* based on calculations of
     * *Chacham* Yosef Harari-Raful of Yeshivat Ateret Torah.
     * @see .getTzaisAteretTorah
     * @see .getAteretTorahSunsetOffset
     * @see .setAteretTorahSunsetOffset
     */
    var ateretTorahSunsetOffset: Double = 40.0

    /**
     * Method to return a *shaah zmanis* (temporal hour) calculated using a 19.8 dip. This calculation
     * divides the day based on the opinion of the [Magen
 * Avraham (MGA)](https://en.wikipedia.org/wiki/Avraham_Gombinern) that the day runs from dawn to dusk. Dawn for this calculation is when the sun is 19.8
     * below the eastern geometric horizon before sunrise. Dusk for this is when the sun is 19.8 below the western
     * geometric horizon after sunset. This day is split into 12 equal parts with each part being a *shaah zmanis*.
     *
     * @return the `long` millisecond length of a *shaah zmanis*. If the calculation can't be computed
     * such as northern and southern locations even south of the Arctic Circle and north of the Antarctic Circle
     * where the sun may not reach low enough below the horizon for this calculation, a [Long.MIN_VALUE]
     * will be returned. See detailed explanation on top of the [AstronomicalCalendar] documentation.
     */
    val shaahZmanis19Point8Degrees: Long
        get() = getTemporalHour(alos19Point8Degrees, tzais19Point8Degrees)

    /**
     * Method to return a *shaah zmanis* (temporal hour) calculated using a 18 dip. This calculation divides
     * the day based on the opinion of the [Magen Avraham
 * (MGA)](https://en.wikipedia.org/wiki/Avraham_Gombinern) that the day runs from dawn to dusk. Dawn for this calculation is when the sun is 18 below the
     * eastern geometric horizon before sunrise. Dusk for this is when the sun is 18 below the western geometric
     * horizon after sunset. This day is split into 12 equal parts with each part being a *shaah zmanis*.
     *
     * @return the `long` millisecond length of a *shaah zmanis*. If the calculation can't be computed
     * such as northern and southern locations even south of the Arctic Circle and north of the Antarctic Circle
     * where the sun may not reach low enough below the horizon for this calculation, a [Long.MIN_VALUE]
     * will be returned. See detailed explanation on top of the [AstronomicalCalendar] documentation.
     */
    val shaahZmanis18Degrees: Long
        get() = getTemporalHour(alos18Degrees, tzais18Degrees)

    /**
     * Method to return a *shaah zmanis* (temporal hour) calculated using a dip of 26. This calculation
     * divides the day based on the opinion of the [Magen
 * Avraham (MGA)](https://en.wikipedia.org/wiki/Avraham_Gombinern) that the day runs from dawn to dusk. Dawn for this calculation is when the sun is
     * [26&amp;deg;][alos26Degrees] below the eastern geometric horizon before sunrise. Dusk for this is when
     * the sun is [26&amp;deg;][tzais26Degrees] below the western geometric horizon after sunset. This day is
     * split into 12 equal parts with each part being a *shaah zmanis*. Since *zmanim* that use this
     * method are extremely late or early and at a point when the sky is a long time past the 18 point where the
     * darkest point is reached, *zmanim* that use this should only be used *lechumra*, such as
     * delaying the start of nighttime *mitzvos*.
     *
     * @return the `long` millisecond length of a *shaah zmanis*. If the calculation can't be computed
     * such as northern and southern locations even south of the Arctic Circle and north of the Antarctic Circle
     * where the sun may not reach low enough below the horizon for this calculation, a [Long.MIN_VALUE]
     * will be returned. See detailed explanation on top of the [AstronomicalCalendar] documentation.
     * @see .getShaahZmanis120Minutes
     */
    val shaahZmanis26Degrees: Long
        get() = getTemporalHour(alos26Degrees, tzais26Degrees)

    /**
     * Method to return a *shaah zmanis* (temporal hour) calculated using a dip of 16.1. This calculation
     * divides the day based on the opinion that the day runs from dawn to dusk. Dawn for this calculation is when the
     * sun is 16.1 below the eastern geometric horizon before sunrise and dusk is when the sun is 16.1 below
     * the western geometric horizon after sunset. This day is split into 12 equal parts with each part being a
     * *shaah zmanis*.
     *
     * @return the `long` millisecond length of a *shaah zmanis*. If the calculation can't be computed
     * such as northern and southern locations even south of the Arctic Circle and north of the Antarctic Circle
     * where the sun may not reach low enough below the horizon for this calculation, a [Long.MIN_VALUE]
     * will be returned. See detailed explanation on top of the [AstronomicalCalendar] documentation.
     *
     * @see .getAlos16Point1Degrees
     * @see .getTzais16Point1Degrees
     * @see .getSofZmanShmaMGA16Point1Degrees
     * @see .getSofZmanTfilaMGA16Point1Degrees
     * @see .getMinchaGedola16Point1Degrees
     * @see .getMinchaKetana16Point1Degrees
     * @see .getPlagHamincha16Point1Degrees
     */
    val shaahZmanis16Point1Degrees: Long
        get() = getTemporalHour(alos16Point1Degrees, tzais16Point1Degrees)

    /**
     * Method to return a *shaah zmanis* (solar hour) according to the opinion of the [Magen Avraham (MGA)](https://en.wikipedia.org/wiki/Avraham_Gombinern). This calculation
     * divides the day based on the opinion of the MGA that the day runs from dawn to dusk. Dawn for this calculation is
     * 60 minutes before sunrise and dusk is 60 minutes after sunset. This day is split into 12 equal parts with each
     * part being a *shaah zmanis*. Alternate methods of calculating a *shaah zmanis* are available in the
     * subclass [ComplexZmanimCalendar].
     *
     * @return the `long` millisecond length of a *shaah zmanis*. If the calculation can't be computed
     * such as in the Arctic Circle where there is at least one day a year where the sun does not rise, and one
     * where it does not set, a [Long.MIN_VALUE] will be returned. See detailed explanation on top of the
     * [AstronomicalCalendar] documentation.
     *
     * @see .getAlos60
     * @see .getTzais60
     * @see .getPlagHamincha60Minutes
     */
    val shaahZmanis60Minutes: Long
        get() = getTemporalHour(alos60, tzais60)

    /**
     * Method to return a *shaah zmanis* (solar hour) according to the opinion of the [Magen Avraham (MGA)](https://en.wikipedia.org/wiki/Avraham_Gombinern). This calculation divides the day
     * based on the opinion of the MGA that the day runs from dawn to dusk. Dawn for this calculation is 72 minutes
     * before sunrise and dusk is 72 minutes after sunset. This day is split into 12 equal parts with each part
     * being a *shaah zmanis*. Alternate methods of calculating a *shaah zmanis* are available in the
     * subclass [ComplexZmanimCalendar].
     *
     * @return the `long` millisecond length of a *shaah zmanis*. If the calculation can't be computed
     * such as in the Arctic Circle where there is at least one day a year where the sun does not rise, and one
     * where it does not set, a [Long.MIN_VALUE] will be returned. See detailed explanation on top of the
     * [AstronomicalCalendar] documentation.
     */
    val shaahZmanis72Minutes: Long get() = shaahZmanisMGA

    /**
     * Method to return a *shaah zmanis* (temporal hour) according to the opinion of the [Magen Avraham (MGA)](https://en.wikipedia.org/wiki/Avraham_Gombinern) based on *alos* being
     * [72][alos72Zmanis] minutes *zmaniyos* before [sunrise][.getSunrise]. This calculation
     * divides the day based on the opinion of the MGA that the day runs from dawn to dusk. Dawn for this calculation
     * is 72 minutes *zmaniyos* before sunrise and dusk is 72 minutes *zmaniyos* after sunset. This day
     * is split into 12 equal parts with each part being a *shaah zmanis*. This is identical to 1/10th of the day
     * from [sunrise] to [sunset].
     *
     * @return the `long` millisecond length of a *shaah zmanis*. If the calculation can't be computed
     * such as in the Arctic Circle where there is at least one day a year where the sun does not rise, and one
     * where it does not set, a [Long.MIN_VALUE] will be returned. See detailed explanation on top of the
     * [AstronomicalCalendar] documentation.
     * @see .getAlos72Zmanis
     * @see .getTzais72Zmanis
     */
    val shaahZmanis72MinutesZmanis: Long
        get() = getTemporalHour(alos72Zmanis, tzais72Zmanis)

    /**
     * Method to return a *shaah zmanis* (temporal hour) calculated using a dip of 90 minutes. This calculation
     * divides the day based on the opinion of the [Magen
 * Avraham (MGA)](https://en.wikipedia.org/wiki/Avraham_Gombinern) that the day runs from dawn to dusk. Dawn for this calculation is 90 minutes before sunrise
     * and dusk is 90 minutes after sunset. This day is split into 12 equal parts with each part being a *shaah zmanis*.
     *
     * @return the `long` millisecond length of a *shaah zmanis*. If the calculation can't be computed
     * such as in the Arctic Circle where there is at least one day a year where the sun does not rise, and one
     * where it does not set, a [Long.MIN_VALUE] will be returned. See detailed explanation on top of the
     * [AstronomicalCalendar] documentation.
     */
    val shaahZmanis90Minutes: Long
        get() = getTemporalHour(alos90, tzais90)

    /**
     * Method to return a *shaah zmanis* (temporal hour) according to the opinion of the [Magen Avraham (MGA)](https://en.wikipedia.org/wiki/Avraham_Gombinern) based on *alos* being
     * [90][alos90Zmanis] minutes *zmaniyos* before [sunrise]. This calculation divides
     * the day based on the opinion of the MGA that the day runs from dawn to dusk. Dawn for this calculation is 90 minutes
     * *zmaniyos* before sunrise and dusk is 90 minutes *zmaniyos* after sunset. This day is split into 12 equal
     * parts with each part being a *shaah zmanis*. This is 1/8th of the day from [sunrise] to
     * [sunset][.getSunset].
     *
     * @return the `long` millisecond length of a *shaah zmanis*. If the calculation can't be computed
     * such as in the Arctic Circle where there is at least one day a year where the sun does not rise, and one
     * where it does not set, a [Long.MIN_VALUE] will be returned. See detailed explanation on top of the
     * [AstronomicalCalendar] documentation.
     * @see .getAlos90Zmanis
     * @see .getTzais90Zmanis
     */
    val shaahZmanis90MinutesZmanis: Long
        get() = getTemporalHour(alos90Zmanis, tzais90Zmanis)

    /**
     * Method to return a *shaah zmanis* (temporal hour) according to the opinion of the [Magen Avraham (MGA)](https://en.wikipedia.org/wiki/Avraham_Gombinern) based on *alos* being [alos96Zmanis] minutes *zmaniyos* before [sunrise]. This calculation divides the
     * day based on the opinion of the MGA that the day runs from dawn to dusk. Dawn for this calculation is 96 minutes
     * *zmaniyos* before sunrise and dusk is 96 minutes *zmaniyos* after sunset. This day is split into 12
     * equal parts with each part being a *shaah zmanis*. This is identical to 1/7.5th of the day from
     * [sunrise] to [sunset].
     *
     * @return the `long` millisecond length of a *shaah zmanis*. If the calculation can't be computed
     * such as in the Arctic Circle where there is at least one day a year where the sun does not rise, and one
     * where it does not set, a [Long.MIN_VALUE] will be returned. See detailed explanation on top of the
     * [AstronomicalCalendar] documentation.
     * @see .getAlos96Zmanis
     * @see .getTzais96Zmanis
     */
    val shaahZmanis96MinutesZmanis: Long
        get() = getTemporalHour(alos96Zmanis, tzais96Zmanis)

    /**
     * Method to return a *shaah zmanis* (temporal hour) according to the opinion of the
     * *Chacham* Yosef Harari-Raful of Yeshivat Ateret Torah calculated with *alos* being 1/10th
     * of sunrise to sunset day, or [72][alos72Zmanis] minutes *zmaniyos* of such a day before
     * [sunrise], and *tzais* is usually calculated as [40][tzaisAteretTorah] (configurable to any offset via [.setAteretTorahSunsetOffset]) after [ sunset][.getSunset]. This day is split into 12 equal parts with each part being a *shaah zmanis*. Note that with this
     * system, *chatzos* (mid-day) will not be the point that the sun is [halfway across][sunTransit].
     *
     * @return the `long` millisecond length of a *shaah zmanis*. If the calculation can't be computed
     * such as in the Arctic Circle where there is at least one day a year where the sun does not rise, and one
     * where it does not set, a [Long.MIN_VALUE] will be returned. See detailed explanation on top of the
     * [AstronomicalCalendar] documentation.
     * @see .getAlos72Zmanis
     * @see .getTzaisAteretTorah
     * @see .getAteretTorahSunsetOffset
     * @see .setAteretTorahSunsetOffset
     */
    val shaahZmanisAteretTorah: Long
        get() = getTemporalHour(alos72Zmanis, tzaisAteretTorah)

    /**
     * Method to return a *shaah zmanis* (temporal hour) used by some *zmanim* according to the opinion of
     * [Rabbi Yaakov Moshe Hillel](https://en.wikipedia.org/wiki/Yaakov_Moshe_Hillel) as published in the
     * *luach* of the Bais Horaah of Yeshivat Chevrat Ahavat Shalom that is based on a day starting 72 minutes before
     * sunrise in degrees [<em>alos</em> 16.1&amp;deg;][alos16Point1Degrees] and ending 14 minutes after sunset in
     * degrees [<em>tzais</em> 3.8&amp;deg;][.getTzaisGeonim3Point8Degrees]. This day is split into 12 equal parts with
     * each part being a *shaah zmanis*. Note that with this system, *chatzos* (mid-day) will not be the point
     * that the sun is [halfway across the sky][sunTransit]. These *shaos zmaniyos* are used for *Mincha
     * Ketana* and *Plag Hamincha*. The 14 minutes are based on 3/4 of an 18 minute *mil*, with half a minute
     * added for Rav Yosi.
     *
     * @return the `long` millisecond length of a *shaah zmanis*. If the calculation can't be computed
     * such as in the Arctic Circle where there is at least one day a year where the sun does not rise, and one
     * where it does not set, a [Long.MIN_VALUE] will be returned. See detailed explanation on top of the
     * [AstronomicalCalendar] documentation.
     *
     * @see .getMinchaKetanaAhavatShalom
     * @see .getPlagAhavatShalom
     */
    val shaahZmanisAlos16Point1ToTzais3Point8: Long
        get() = getTemporalHour(alos16Point1Degrees, tzaisGeonim3Point8Degrees)

    /**
     * Method to return a *shaah zmanis* (temporal hour) used by some *zmanim* according to the opinion of
     * [Rabbi Yaakov Moshe Hillel](https://en.wikipedia.org/wiki/Yaakov_Moshe_Hillel) as published in the
     * *luach* of the Bais Horaah of Yeshivat Chevrat Ahavat Shalom that is based on a day starting 72 minutes before
     * sunrise in degrees [<em>alos</em> 16.1&amp;deg;][.getAlos16Point1Degrees] and ending 13.5 minutes after sunset in
     * degrees [<em>tzais</em> 3.7&amp;deg;][tzaisGeonim3Point7Degrees]. This day is split into 12 equal parts with
     * each part being a *shaah zmanis*. Note that with this system, *chatzos* (mid-day) will not be the point
     * that the sun is [halfway across the sky][.getSunTransit]. These *shaos zmaniyos* are used for *Mincha
     * Gedola* calculation.
     *
     * @return the `long` millisecond length of a *shaah zmanis*. If the calculation can't be computed
     * such as in the Arctic Circle where there is at least one day a year where the sun does not rise, and one
     * where it does not set, a [Long.MIN_VALUE] will be returned. See detailed explanation on top of the
     * [AstronomicalCalendar] documentation.
     *
     * @see .getMinchaGedolaAhavatShalom
     */
    val shaahZmanisAlos16Point1ToTzais3Point7: Long
        get() = getTemporalHour(alos16Point1Degrees, tzaisGeonim3Point7Degrees)

    /**
     * Method to return a *shaah zmanis* (temporal hour) calculated using a dip of 96 minutes. This calculation
     * divides the day based on the opinion of the [Magen
 * Avraham (MGA)](https://en.wikipedia.org/wiki/Avraham_Gombinern) that the day runs from dawn to dusk. Dawn for this calculation is 96 minutes before sunrise
     * and dusk is 96 minutes after sunset. This day is split into 12 equal parts with each part being a *shaah
     * zmanis*.
     *
     * @return the `long` millisecond length of a *shaah zmanis*. If the calculation can't be computed
     * such as in the Arctic Circle where there is at least one day a year where the sun does not rise, and one
     * where it does not set, a [Long.MIN_VALUE] will be returned. See detailed explanation on top of the
     * [AstronomicalCalendar] documentation.
     */
    val shaahZmanis96Minutes: Long
        get() = getTemporalHour(alos96, tzais96)

    /**
     * Method to return a *shaah zmanis* (temporal hour) calculated using a dip of 120 minutes. This calculation
     * divides the day based on the opinion of the [Magen
 * Avraham (MGA)](https://en.wikipedia.org/wiki/Avraham_Gombinern) that the day runs from dawn to dusk. Dawn for this calculation is 120 minutes before sunrise and
     * dusk is 120 minutes after sunset. This day is split into 12 equal parts with each part being a *shaah zmanis*.
     * Since *zmanim* that use this method are extremely late or early and at a point when the sky is a long time
     * past the 18 point where the darkest point is reached, *zmanim* that use this should only be used
     * *lechumra* only, such as delaying the start of nighttime *mitzvos*.
     *
     * @return the `long` millisecond length of a *shaah zmanis*. If the calculation can't be computed
     * such as in the Arctic Circle where there is at least one day a year where the sun does not rise, and one
     * where it does not set, a [Long.MIN_VALUE] will be returned. See detailed explanation on top of the
     * [AstronomicalCalendar] documentation.
     * @see .getShaahZmanis26Degrees
     */
    val shaahZmanis120Minutes: Long
        get() = getTemporalHour(alos120, tzais120)

    /**
     * Method to return a *shaah zmanis* (temporal hour) according to the opinion of the [Magen Avraham (MGA)](https://en.wikipedia.org/wiki/Avraham_Gombinern) based on *alos* being [alos120Zmanis] minutes *zmaniyos* before [sunrise]. This calculation divides
     * the day based on the opinion of the MGA that the day runs from dawn to dusk. Dawn for this calculation is
     * 120 minutes *zmaniyos* before sunrise and dusk is 120 minutes *zmaniyos* after sunset. This day is
     * split into 12 equal parts with each part being a *shaah zmanis*. This is identical to 1/6th of the day from
     * [sunrise] to [sunset]. Since *zmanim* that use this method are
     * extremely late or early and at a point when the sky is a long time past the 18 point where the darkest point
     * is reached, *zmanim* that use this should only be used *lechumra* such as delaying the start of
     * nighttime *mitzvos*.
     *
     * @return the `long` millisecond length of a *shaah zmanis*. If the calculation can't be computed
     * such as in the Arctic Circle where there is at least one day a year where the sun does not rise, and one
     * where it does not set, a [Long.MIN_VALUE] will be returned. See detailed explanation on top of the
     * [AstronomicalCalendar] documentation.
     * @see .getAlos120Zmanis
     * @see .getTzais120Zmanis
     */
    val shaahZmanis120MinutesZmanis: Long
        get() = getTemporalHour(alos120Zmanis, tzais120Zmanis)

    /**
     * This method should be used *lechumra* only and returns the time of *plag hamincha* based on sunrise
     * being 120 minutes *zmaniyos* or 1/6th of the day before sunrise. This is calculated as 10.75 hours after
     * [dawn][alos120Zmanis]. The formula used is 10.75 * [shaahZmanis120MinutesZmanis] after
     * [dawn][alos120Zmanis]. Since the *zman* based on an extremely early *alos* and a very
     * late *tzais*, it should only be used *lechumra*.
     *
     * @return the `Date` of the time of *plag hamincha*. If the calculation can't be computed such as
     * in the Arctic Circle where there is at least one day a year where the sun does not rise, and one where it
     * does not set, a null will be returned. See detailed explanation on top of the
     * [AstronomicalCalendar] documentation.
     *
     * @see .getShaahZmanis120MinutesZmanis
     * @see .getAlos120
     * @see .getTzais120
     * @see .getPlagHamincha26Degrees
     * @see .getPlagHamincha120Minutes
     */  // (forRemoval=false) // add back once Java 9 is the minimum supported version
    @get:Deprecated("This method should be used <em>lechumra</em> only since it returns a very late time, and if used\n" + "	          <em>lekula</em> can result in <em>chillul Shabbos</em> etc. There is no current plan to remove this\n" + "	          method from the API, and this deprecation is intended to alert developers of the danger of using it.\n" + "	  \n" + "	  ")
    val plagHamincha120MinutesZmanis: Instant?
        get() = getPlagHamincha(alos120Zmanis, tzais120Zmanis)

    /**
     * This method should be used *lechumra* only and returns the time of *plag hamincha* according to the
     * Magen Avraham with the day starting 120 minutes before sunrise and ending 120 minutes after sunset. This is
     * calculated as 10.75 hours after [dawn 120 minutes][alos120]. The formula used is 10.75 [shaahZmanis120Minutes] after [.getAlos120]. Since the *zman* based on an extremely early
     * *alos* and a very late *tzais*, it should only be used *lechumra*.
     *
     * @return the `Date` of the time of *plag hamincha*. If the calculation can't be computed such as
     * in the Arctic Circle where there is at least one day a year where the sun does not rise, and one where it
     * does not set, a null will be returned. See detailed explanation on top of the
     * [AstronomicalCalendar] documentation.
     *
     * @see .getShaahZmanis120Minutes
     * @see .getPlagHamincha26Degrees
     */  // (forRemoval=false) // add back once Java 9 is the minimum supported version
    @get:Deprecated("This method should be used <em>lechumra</em> only since it returns a very late time, and if used\n" + "	          <em>lekula</em> can result in <em>chillul Shabbos</em> etc. There is no current plan to remove this\n" + "	          method from the API, and this deprecation is intended to alert developers of the danger of using it.\n" + "	  \n" + "	  ")
    val plagHamincha120Minutes: Instant?
        get() = getPlagHamincha(alos120, tzais120)

    /**
     * Method to return *alos* (dawn) calculated as 60 minutes before sunrise. This is the time to walk the
     * distance of 4 *Mil* at 15 minutes a *Mil*. This seems to be the opinion of the [Chavas Yair](https://en.wikipedia.org/wiki/Yair_Bacharach) in the Mekor Chaim, Orach Chaim Ch.
     * 90, though  the Mekor Chaim in Ch. 58 and in the [Chut Hashani Cha 97](https://hebrewbooks.org/pdfpager.aspx?req=45193&pgnum=214) states that
     * a a person walks 3 and a 1/3 *mil* in an hour, or an 18-minute *mil*. Also see the [Divrei Malkiel](https://he.wikipedia.org/wiki/%D7%9E%D7%9C%D7%9B%D7%99%D7%90%D7%9C_%D7%A6%D7%91%D7%99_%D7%98%D7%A0%D7%A0%D7%91%D7%95%D7%99%D7%9D) [Vol. 4, Ch. 20, page 34](https://hebrewbooks.org/pdfpager.aspx?req=803&pgnum=33)) who
     * mentions the 15 minute *mil* lechumra by baking matzos. Also see the [Maharik](https://en.wikipedia.org/wiki/Joseph_Colon_Trabotto) [Ch. 173](https://hebrewbooks.org/pdfpager.aspx?req=1142&pgnum=216) where the questioner quoting the
     * [Ra'avan](https://en.wikipedia.org/wiki/Eliezer_ben_Nathan) is of the opinion that the time to walk a
     * *mil* is 15 minutes (5 *mil* in a little over an hour). There are many who believe that there is a
     * *ta'us sofer* (scribe's error) in the Ra'avan, and it should 4 *mil* in a little over an hour, or an
     * 18-minute *mil*. Time based offset calculations are based on the opinion of the
     * *[Rishonim](https://en.wikipedia.org/wiki/Rishonim)* who stated that the time of the *neshef*
     * (time between dawn and sunrise) does not vary by the time of year or location but purely depends on the time it takes to
     * walk the distance of 4* *mil*. [tzaisGeonim9Point75Degrees] is a related *zman* that is a
     * degree-based calculation based on 60 minutes.
     *
     * @return the `Date` representing the time. If the calculation can't be computed such as in the Arctic
     * Circle where there is at least one day a year where the sun does not rise, and one where it does not set,
     * a null will be returned. See detailed explanation on top of the [AstronomicalCalendar].
     * documentation.
     *
     * @see .getTzais60
     * @see .getPlagHamincha60Minutes
     * @see .getShaahZmanis60Minutes
     */
    val alos60: Instant?
        get() = getTimeOffset(
            sunrise,
            -60 * MINUTE_MILLIS
        )

    /**
     * Method to return *alos* (dawn) calculated using 72 minutes *zmaniyos* or 1/10th of the day before
     * sunrise. This is based on an 18-minute *Mil* so the time for 4 *Mil* is 72 minutes which is 1/10th
     * of a day (12 * 60 = 720) based on the a day being from [sea level sunrise][.getSeaLevelSunrise] to
     * [sea level sunset][seaLevelSunrise] or [sunrise] to [sunset]
     * (depending on the [isUseElevation] setting).
     * The actual calculation is [seaLevelSunrise] - ([shaahZmanisGra] * 1.2). This calculation
     * is used in the calendars published by the [Hisachdus Harabanim D'Artzos Habris Ve'Canada](https://en.wikipedia.org/wiki/Central_Rabbinical_Congress).
     *
     * @return the `Date` representing the time. If the calculation can't be computed such as in the Arctic
     * Circle where there is at least one day a year where the sun does not rise, and one where it does not set,
     * a null will be returned. See detailed explanation on top of the [AstronomicalCalendar]
     * documentation.
     * @see .getShaahZmanisGra
     */
    val alos72Zmanis: Instant?
        get() = getZmanisBasedOffset(-1.2)

    /**
     * Method to return *alos* (dawn) calculated using 96 minutes before before [sunrise][.getSunrise] or
     * [sea level sunrise][seaLevelSunrise] (depending on the [.isUseElevation] setting) that is based
     * on the time to walk the distance of 4 *Mil* at 24 minutes a *Mil*. Time based offset
     * calculations for *alos* are based on the opinion of the *[Rishonim](https://en.wikipedia.org/wiki/Rishonim)* who stated that the time of the *Neshef* (time between dawn and sunrise) does not vary
     * by the time of year or location but purely depends on the time it takes to walk the distance of 4 *Mil*.
     *
     * @return the `Date` representing the time. If the calculation can't be computed such as in the Arctic
     * Circle where there is at least one day a year where the sun does not rise, and one where it does not set,
     * a null will be returned. See detailed explanation on top of the [AstronomicalCalendar]
     * documentation.
     */
    val alos96: Instant?
        get() = getTimeOffset(
            elevationAdjustedSunrise,
            -96 * MINUTE_MILLIS
        )

    /**
     * Method to return *alos* (dawn) calculated using 90 minutes *zmaniyos* or 1/8th of the day before
     * [sunrise][.getSunrise] or [sea level sunrise][.getSeaLevelSunrise] (depending on the [isUseElevation] setting). This is based on a 22.5-minute *Mil* so the time for 4 *Mil* is 90
     * minutes which is 1/8th of a day (12 * 60) / 8 = 90
     * The day is calculated from [sea level sunrise][.getSeaLevelSunrise] to [sea level][.getSeaLevelSunrise] or [sunrise][.getSunrise] to [sunset][.getSunset] (depending on the [.isUseElevation].
     * The actual calculation used is [.getSunrise] - ([.getShaahZmanisGra] * 1.5).
     *
     * @return the `Date` representing the time. If the calculation can't be computed such as in the Arctic
     * Circle where there is at least one day a year where the sun does not rise, and one where it does not set,
     * a null will be returned. See detailed explanation on top of the [AstronomicalCalendar]
     * documentation.
     * @see .getShaahZmanisGra
     */
    val alos90Zmanis: Instant?
        get() = getZmanisBasedOffset(-1.5)

    /**
     * This method returns *alos* (dawn) calculated using 96 minutes *zmaniyos* or 1/7.5th of the day before
     * [sunrise][.getSunrise] or [sea level sunrise][.getSeaLevelSunrise] (depending on the [isUseElevation] setting). This is based on a 24-minute *Mil* so the time for 4 *Mil* is 96
     * minutes which is 1/7.5th of a day (12 * 60 / 7.5 = 96).
     * The day is calculated from [sea level sunrise][.getSeaLevelSunrise] to [sea level][.getSeaLevelSunrise] or [sunrise][.getSunrise] to [sunset][.getSunset] (depending on the [.isUseElevation].
     * The actual calculation used is [.getSunrise] - ([.getShaahZmanisGra] * 1.6).
     *
     * @return the `Date` representing the time. If the calculation can't be computed such as in the Arctic
     * Circle where there is at least one day a year where the sun does not rise, and one where it does not set,
     * a null will be returned. See detailed explanation on top of the [AstronomicalCalendar]
     * documentation.
     * @see .getShaahZmanisGra
     */
    val alos96Zmanis: Instant?
        get() = getZmanisBasedOffset(-1.6)

    /**
     * Method to return *alos* (dawn) calculated using 90 minutes before [sea level][.getSeaLevelSunrise] based on the time to walk the distance of 4 *Mil* at 22.5 minutes a *Mil*. Time based
     * offset calculations for *alos* are based on the opinion of the *[Rishonim](https://en.wikipedia.org/wiki/Rishonim)* who stated that the time of the *Neshef*
     * (time between dawn and sunrise) does not vary by the time of year or location but purely depends on the time it
     * takes to walk the distance of 4 *Mil*.
     *
     * @return the `Date` representing the time. If the calculation can't be computed such as in the Arctic
     * Circle where there is at least one day a year where the sun does not rise, and one where it does not set,
     * a null will be returned. See detailed explanation on top of the [AstronomicalCalendar]
     * documentation.
     */
    val alos90: Instant?
        get() = getTimeOffset(
            elevationAdjustedSunrise,
            -90 * MINUTE_MILLIS
        )

    /**
     * This method should be used *lechumra* only and returns *alos* (dawn) calculated using 120 minutes
     * before [sea level sunrise][.getSeaLevelSunrise] (no adjustment for elevation is made) based on the time
     * to walk the distance of 5 *Mil*(*Ula*) at 24 minutes a *Mil*. Time based offset calculations
     * for *alos* are based on the* opinion of the *[Rishonim](https://en.wikipedia.org/wiki/Rishonim)
     * *  who stated that the time of the *neshef* (time between dawn and sunrise) does not vary by the time of
     * year or location but purely depends on the time it takes to walk the distance of 5 *Mil*(*Ula*). Since
     * this time is extremely early, it should only be used *lechumra*, such as not eating after this time on a fast
     * day, and not as the start time for *mitzvos* that can only be performed during the day.
     *
     * @return the `Date` representing the time. If the calculation can't be computed such as in the Arctic
     * Circle where there is at least one day a year where the sun does not rise, and one where it does not set,
     * a null will be returned. See detailed explanation on top of the [AstronomicalCalendar]
     * documentation.
     *
     * @see .getTzais120
     * @see .getAlos26Degrees
     */  // (forRemoval=false) // add back once Java 9 is the minimum supported version
    @get:Deprecated("This method should be used <em>lechumra</em> only (such as stopping to eat at this time on a fast day),\n" + "	          since it returns a very early time, and if used <em>lekula</em> can result in doing <em>mitzvos hayom</em>\n" + "	          too early according to most opinions. There is no current plan to remove this method from the API, and this\n" + "	          deprecation is intended to alert developers of the danger of using it.\n" + "	  \n" + "	  ")
    val alos120: Instant?
        get() = getTimeOffset(
            elevationAdjustedSunrise,
            -120 * MINUTE_MILLIS
        )

    /**
     * This method should be used *lechumra* only and  method returns *alos* (dawn) calculated using
     * 120 minutes *zmaniyos* or 1/6th of the day before [sunrise][.getSunrise] or [seaLevelSunrise] (depending on the [isUseElevation] setting). This is based
     * on a 24-minute *Mil* so the time for 5 *Mil* is 120 minutes which is 1/6th of a day (12 * 60 /
     * 6 = 120). The day is calculated from [sea level sunrise][.getSeaLevelSunrise] to [seaLevelSunrise] or [sunrise] to [sunset] (depending
     * on the [isUseElevation]. The actual calculation used is [sunrise] - ([shaahZmanisGra] * 2). Since this time is extremely early, it should only be used *lechumra*, such
     * as not eating after this time on a fast day, and not as the start time for *mitzvos* that can only be
     * performed during the day.
     *
     * @return the `Date` representing the time. If the calculation can't be computed such as in the Arctic
     * Circle where there is at least one day a year where the sun does not rise, and one where it does not set,
     * a null will be returned. See detailed explanation on top of the [AstronomicalCalendar]
     * documentation.
     * @see .getAlos120
     * @see .getAlos26Degrees
     */  // (forRemoval=false) // add back once Java 9 is the minimum supported version
    @get:Deprecated("This method should be used <em>lechumra</em> only (such as stopping to eat at this time on a fast day),\n" + "	          since it returns a very early time, and if used <em>lekula</em> can result in doing <em>mitzvos hayom</em>\n" + "	          too early according to most opinions. There is no current plan to remove this method from the API, and this\n" + "	          deprecation is intended to alert developers of the danger of using it.\n" + "	  \n" + "	  ")
    val alos120Zmanis: Instant?
        get() = getZmanisBasedOffset(-2.0)

    /**
     * This method should be used *lechumra* only and returns *alos* (dawn) calculated when the sun is [ZENITH_26_DEGREES] below the eastern geometric horizon before sunrise. This calculation is based on the same
     * calculation of [120 minutes][.getAlos120] but uses a degree-based calculation instead of 120 exact minutes. This
     * calculation is based on the position of the sun 120 minutes before sunrise in Jerusalem [around the equinox / equilux](https://kosherjava.com/2022/01/12/equinox-vs-equilux-zmanim-calculations/), which
     * calculates to 26 below [geometric zenith][.GEOMETRIC_ZENITH]. Since this time is extremely early, it should
     * only be used *lechumra* only, such as not eating after this time on a fast day, and not as the start time for
     * *mitzvos* that can only be performed during the day.
     *
     * @return the `Date` representing *alos*. If the calculation can't be computed such as northern
     * and southern locations even south of the Arctic Circle and north of the Antarctic Circle where the sun
     * may not reach low enough below the horizon for this calculation, a null will be returned. See detailed
     * explanation on top of the [AstronomicalCalendar] documentation.
     * @see .ZENITH_26_DEGREES
     *
     * @see .getAlos120
     * @see .getTzais120
     * @see .getTzais26Degrees
     */  // (forRemoval=false) // add back once Java 9 is the minimum supported version
    @get:Deprecated("This method should be used <em>lechumra</em> only (such as stopping to eat at this time on a fast day),\n" + "	          since it returns a very early time, and if used <em>lekula</em> can result in doing <em>mitzvos hayom</em>\n" + "	          too early according to most opinions. There is no current plan to remove this  method from the API, and this\n" + "	          deprecation is intended to alert developers of the danger of using it.\n" + "	  \n" + "	  ")
    val alos26Degrees: Instant?
        get() = getSunriseOffsetByDegrees(ZENITH_26_DEGREES)

    /**
     * A method to return *alos* (dawn) calculated when the sun is [18&amp;deg;][.ASTRONOMICAL_ZENITH] below the
     * eastern geometric horizon before sunrise.
     *
     * @return the `Date` representing *alos*. If the calculation can't be computed such as northern
     * and southern locations even south of the Arctic Circle and north of the Antarctic Circle where the sun
     * may not reach low enough below the horizon for this calculation, a null will be returned. See detailed
     * explanation on top of the [AstronomicalCalendar] documentation.
     * @see .ASTRONOMICAL_ZENITH
     */
    val alos18Degrees: Instant?
        get() = getSunriseOffsetByDegrees(ASTRONOMICAL_ZENITH)

    /**
     * A method to return *alos* (dawn) calculated when the sun is [19&amp;deg;][.ZENITH_19_DEGREES] below the
     * eastern geometric horizon before sunrise. This is the [Rambam](https://en.wikipedia.org/wiki/Maimonides)'s *alos* according to Rabbi Moshe Kosower's [Maaglei Tzedek](https://www.worldcat.org/oclc/145454098), page 88, [Ayeles Hashachar Vol. I, page 12](https://hebrewbooks.org/pdfpager.aspx?req=33464&pgnum=13), [Yom Valayla Shel Torah, Ch. 34, p. 222](https://hebrewbooks.org/pdfpager.aspx?req=55960&pgnum=258) and
     * Rabbi Yaakov Shakow's [Luach Ikvei Hayom](https://www.worldcat.org/oclc/1043573513).
     *
     * @return the `Date` representing *alos*. If the calculation can't be computed such as northern
     * and southern locations even south of the Arctic Circle and north of the Antarctic Circle where the sun
     * may not reach low enough below the horizon for this calculation, a null will be returned. See detailed
     * explanation on top of the [AstronomicalCalendar] documentation.
     * @see .ASTRONOMICAL_ZENITH
     */
    val alos19Degrees: Instant?
        get() {
            return getSunriseOffsetByDegrees(ZENITH_19_DEGREES)
        }

    /**
     * Method to return *alos* (dawn) calculated when the sun is [19.8&amp;deg;][.ZENITH_19_POINT_8] below the
     * eastern geometric horizon before sunrise. This calculation is based on the same calculation of
     * [90 minutes][.getAlos90] but uses a degree-based calculation instead of 90 exact minutes. This calculation
     * is based on the position of the sun 90 minutes before sunrise in Jerusalem [around the equinox / equilux](https://kosherjava.com/2022/01/12/equinox-vs-equilux-zmanim-calculations/), which
     * calculates to 19.8 below [geometric zenith][.GEOMETRIC_ZENITH].
     *
     * @return the `Date` representing *alos*. If the calculation can't be computed such as northern
     * and southern locations even south of the Arctic Circle and north of the Antarctic Circle where the sun
     * may not reach low enough below the horizon for this calculation, a null will be returned. See detailed
     * explanation on top of the [AstronomicalCalendar] documentation.
     * @see .ZENITH_19_POINT_8
     *
     * @see .getAlos90
     */
    val alos19Point8Degrees: Instant?
        get() = getSunriseOffsetByDegrees(ZENITH_19_POINT_8)

    /**
     * Method to return *alos* (dawn) calculated when the sun is [16.1&amp;deg;][.ZENITH_16_POINT_1] below the
     * eastern geometric horizon before sunrise. This calculation is based on the same calculation of
     * [72 minutes][.getAlos72] but uses a degree-based calculation instead of 72 exact minutes. This calculation
     * is based on the position of the sun 72 minutes before sunrise in Jerusalem [around the equinox / equilux](https://kosherjava.com/2022/01/12/equinox-vs-equilux-zmanim-calculations/), which
     * calculates to 16.1 below [geometric zenith][.GEOMETRIC_ZENITH].
     *
     * @return the `Date` representing *alos*. If the calculation can't be computed such as northern
     * and southern locations even south of the Arctic Circle and north of the Antarctic Circle where the sun
     * may not reach low enough below the horizon for this calculation, a null will be returned. See detailed
     * explanation on top of the [AstronomicalCalendar] documentation.
     * @see .ZENITH_16_POINT_1
     *
     * @see .getAlos72
     */
    val alos16Point1Degrees: Instant?
        get() = getSunriseOffsetByDegrees(ZENITH_16_POINT_1)

    /**
     * This method returns *misheyakir* based on the position of the sun when it is [ 11.5&amp;deg;][.ZENITH_11_DEGREES] below [geometric zenith][.GEOMETRIC_ZENITH] (90). This calculation is used for calculating
     * *misheyakir* according to some opinions. This calculation is based on the position of the sun 52 minutes
     * before [sunrise][.getSunrise] in Jerusalem [around the equinox / equilux](https://kosherjava.com/2022/01/12/equinox-vs-equilux-zmanim-calculations/),
     * which calculates to 11.5 below [geometric zenith][.GEOMETRIC_ZENITH].
     * @todo recalculate.
     *
     * @return the `Date` of *misheyakir*. If the calculation can't be computed such as northern and
     * southern locations even south of the Arctic Circle and north of the Antarctic Circle where the sun may
     * not reach low enough below the horizon for this calculation, a null will be returned. See detailed
     * explanation on top of the [AstronomicalCalendar] documentation.
     * @see .ZENITH_11_POINT_5
     */
    val misheyakir11Point5Degrees: Instant?
        get() = getSunriseOffsetByDegrees(ZENITH_11_POINT_5)

    /**
     * This method returns *misheyakir* based on the position of the sun when it is [ 11&amp;deg;][.ZENITH_11_DEGREES] below [geometric zenith][.GEOMETRIC_ZENITH] (90). This calculation is used for calculating
     * *misheyakir* according to some opinions. This calculation is based on the position of the sun 48 minutes
     * before [sunrise][.getSunrise] in Jerusalem d[around the equinox / equilux](https://kosherjava.com/2022/01/12/equinox-vs-equilux-zmanim-calculations/),
     * which calculates to 11 below [geometric zenith][.GEOMETRIC_ZENITH].
     *
     * @return If the calculation can't be computed such as northern and southern locations even south of the Arctic
     * Circle and north of the Antarctic Circle where the sun may not reach low enough below the horizon for
     * this calculation, a null will be returned. See detailed explanation on top of the
     * [AstronomicalCalendar] documentation.
     * @see .ZENITH_11_DEGREES
     */
    val misheyakir11Degrees: Instant?
        get() = getSunriseOffsetByDegrees(ZENITH_11_DEGREES)

    /**
     * This method returns *misheyakir* based on the position of the sun when it is [ 10.2&amp;deg;][.ZENITH_10_POINT_2] below [geometric zenith][.GEOMETRIC_ZENITH] (90). This calculation is used for calculating
     * *misheyakir* according to some opinions. This calculation is based on the position of the sun 45 minutes
     * before [sunrise][.getSunrise] in Jerusalem [around the equinox](https://kosherjava.com/2022/01/12/equinox-vs-equilux-zmanim-calculations/) which calculates
     * to 10.2 below [geometric zenith][.GEOMETRIC_ZENITH].
     *
     * @return the `Date` of *misheyakir*. If the calculation can't be computed such as
     * northern and southern locations even south of the Arctic Circle and north of the Antarctic Circle where
     * the sun may not reach low enough below the horizon for this calculation, a null will be returned. See
     * detailed explanation on top of the [AstronomicalCalendar] documentation.
     * @see .ZENITH_10_POINT_2
     */
    val misheyakir10Point2Degrees: Instant?
        get() = getSunriseOffsetByDegrees(ZENITH_10_POINT_2)

    /**
     * This method returns *misheyakir* based on the position of the sun when it is [ 7.65&amp;deg;][.ZENITH_7_POINT_65] below [geometric zenith][.GEOMETRIC_ZENITH] (90). The degrees are based on a 35/36 minute
     * *zman* [around the
 * equinox / equilux](https://kosherjava.com/2022/01/12/equinox-vs-equilux-zmanim-calculations/), when the *neshef* (twilight) is the shortest. This time is based on [Rabbi Moshe Feinstein](https://en.wikipedia.org/wiki/Moshe_Feinstein) who writes in [Ohr Hachaim Vol. 4, Ch. 6](https://hebrewbooks.org/pdfpager.aspx?req=14677&pgnum=7))
     * that *misheyakir* in New York is 35-40 minutes before sunset, something that is a drop less than 8.
     * [Rabbi Yisroel Taplin](https://en.wikipedia.org/wiki/Yisroel_Taplin) in [Zmanei Yisrael](https://www.worldcat.org/oclc/889556744) (page 117) notes that [Rabbi Yaakov Kamenetsky](https://en.wikipedia.org/wiki/Yaakov_Kamenetsky) stated that it is not less than 36
     * minutes before sunrise (maybe it is 40 minutes). Sefer Yisrael Vehazmanim (p. 7) quotes the Tamar Yifrach
     * in the name of the [Satmar Rov](https://en.wikipedia.org/wiki/Joel_Teitelbaum) that one should be stringent
     * not consider *misheyakir* before 36 minutes. This is also the accepted [minhag](https://en.wikipedia.org/wiki/Minhag) in [Lakewood](https://en.wikipedia.org/wiki/Lakewood_Township,_New_Jersey) that is used in the [Yeshiva](https://en.wikipedia.org/wiki/Beth_Medrash_Govoha). This follows the opinion of [Rabbi Shmuel Kamenetsky](https://en.wikipedia.org/wiki/Shmuel_Kamenetsky) who provided the time of 35/36 minutes,
     * but did not provide a degree-based time. Since this *zman* depends on the level of light, Rabbi Yaakov Shakow
     * presented this degree-based calculations to Rabbi Kamenetsky who agreed to them.
     *
     * @return the `Date` of *misheyakir*. If the calculation can't be computed such as
     * northern and southern locations even south of the Arctic Circle and north of the Antarctic Circle where
     * the sun may not reach low enough below the horizon for this calculation, a null will be returned. See
     * detailed explanation on top of the [AstronomicalCalendar] documentation.
     *
     * @see .ZENITH_7_POINT_65
     *
     * @see .getMisheyakir9Point5Degrees
     */
    val misheyakir7Point65Degrees: Instant?
        get() = getSunriseOffsetByDegrees(ZENITH_7_POINT_65)

    /**
     * This method returns *misheyakir* based on the position of the sun when it is [ 9.5&amp;deg;][.ZENITH_9_POINT_5] below [geometric zenith][.GEOMETRIC_ZENITH] (90). This calculation is based on Rabbi Dovid Kronglass's
     * Calculation of 45 minutes in Baltimore as mentioned in [Divrei Chachamim No. 24](https://hebrewbooks.org/pdfpager.aspx?req=20287&pgnum=29) brought down by the [Birur Halacha, Tinyana, Ch. 18](https://hebrewbooks.org/pdfpager.aspx?req=50535&pgnum=87). This calculates to
     * 9.5. Also see [Rabbi Yaakov Yitzchok Neiman](https://en.wikipedia.org/wiki/Jacob_Isaac_Neiman) in Kovetz
     * Eitz Chaim Vol. 9, p. 202 that the Vya'an Yosef did not want to rely on times earlier than 45 minutes in New York. This
     * *zman* is also used in the calendars published by Rabbi Hershel Edelstein. As mentioned in Yisroel Vehazmanim,
     * Rabbi Edelstein who was given the 45 minute *zman* by Rabbi Bick. The calendars published by the *[Edot Hamizrach](https://en.wikipedia.org/wiki/Mizrahi_Jews)* communities also use this *zman*. This also
     * follows the opinion of [Rabbi Shmuel Kamenetsky](https://en.wikipedia.org/wiki/Shmuel_Kamenetsky) who provided
     * the time of 36 and 45 minutes, but did not provide a degree-based time. Since this *zman* depends on the level of
     * light, Rabbi Yaakov Shakow presented these degree-based times to Rabbi Shmuel Kamenetsky who agreed to them.
     *
     * @return the `Date` of *misheyakir*. If the calculation can't be computed such as
     * northern and southern locations even south of the Arctic Circle and north of the Antarctic Circle where
     * the sun may not reach low enough below the horizon for this calculation, a null will be returned. See
     * detailed explanation on top of the [AstronomicalCalendar] documentation.
     *
     * @see .ZENITH_9_POINT_5
     *
     * @see .getMisheyakir7Point65Degrees
     */
    val misheyakir9Point5Degrees: Instant?
        get() = getSunriseOffsetByDegrees(ZENITH_9_POINT_5)

    /**
     * This method returns the latest *zman krias shema* (time to recite Shema in the morning) according to the
     * opinion of the [Magen Avraham (MGA)](https://en.wikipedia.org/wiki/Avraham_Gombinern) based on
     * *alos* being [19.8&amp;deg;][.getAlos19Point8Degrees] before [sunrise][.getSunrise]. This
     * time is 3 [<em>shaos zmaniyos</em>][.getShaahZmanis19Point8Degrees] (solar hours) after [alos19Point8Degrees] based on the opinion of the MGA that the day is calculated from dawn to nightfall
     * with both being 19.8 below sunrise or sunset. This returns the time of 3 *
     * [.getShaahZmanis19Point8Degrees] after [dawn][.getAlos19Point8Degrees].
     *
     * @return the `Date` of the latest *zman krias shema*. If the calculation can't be computed such
     * as northern and southern locations even south of the Arctic Circle and north of the Antarctic Circle
     * where the sun may not reach low enough below the horizon for this calculation, a null will be returned.
     * See detailed explanation on top of the [AstronomicalCalendar] documentation.
     * @see .getShaahZmanis19Point8Degrees
     * @see .getAlos19Point8Degrees
     */
    val sofZmanShmaMGA19Point8Degrees: Instant?
        get() = getSofZmanShma(alos19Point8Degrees, tzais19Point8Degrees)

    /**
     * This method returns the latest *zman krias shema* (time to recite Shema in the morning) according to the
     * opinion of the [Magen Avraham (MGA)](https://en.wikipedia.org/wiki/Avraham_Gombinern) based
     * on *alos* being [16.1&amp;deg;][.getAlos16Point1Degrees] before [sunrise][.getSunrise]. This time
     * is 3 [<em>shaos zmaniyos</em>][.getShaahZmanis16Point1Degrees] (solar hours) after
     * [dawn][.getAlos16Point1Degrees] based on the opinion of the MGA that the day is calculated from
     * dawn to nightfall with both being 16.1 below sunrise or sunset. This returns the time of
     * 3 * [.getShaahZmanis16Point1Degrees] after [dawn][.getAlos16Point1Degrees].
     *
     * @return the `Date` of the latest *zman krias shema*. If the calculation can't be computed such
     * as northern and southern locations even south of the Arctic Circle and north of the Antarctic Circle
     * where the sun may not reach low enough below the horizon for this calculation, a null will be returned.
     * See detailed explanation on top of the [AstronomicalCalendar] documentation.
     * @see .getShaahZmanis16Point1Degrees
     * @see .getAlos16Point1Degrees
     */
    val sofZmanShmaMGA16Point1Degrees: Instant?
        get() = getSofZmanShma(alos16Point1Degrees, tzais16Point1Degrees)

    /**
     * This method returns the latest *zman krias shema* (time to recite Shema in the morning) according to the
     * opinion of the [Magen Avraham (MGA)](https://en.wikipedia.org/wiki/Avraham_Gombinern) based
     * on *alos* being [18&amp;deg;][.getAlos18Degrees] before [sunrise][.getSunrise]. This time is 3
     * [<em>shaos zmaniyos</em>][.getShaahZmanis18Degrees] (solar hours) after [dawn][.getAlos18Degrees]
     * based on the opinion of the MGA that the day is calculated from dawn to nightfall with both being 18
     * below sunrise or sunset. This returns the time of 3 * [.getShaahZmanis18Degrees] after
     * [dawn][.getAlos18Degrees].
     *
     * @return the `Date` of the latest *zman krias shema*. If the calculation can't be computed such
     * as northern and southern locations even south of the Arctic Circle and north of the Antarctic Circle
     * where the sun may not reach low enough below the horizon for this calculation, a null will be returned.
     * See detailed explanation on top of the [AstronomicalCalendar] documentation.
     * @see .getShaahZmanis18Degrees
     * @see .getAlos18Degrees
     */
    val sofZmanShmaMGA18Degrees: Instant?
        get() = getSofZmanShma(alos18Degrees, tzais18Degrees)

    /**
     * This method returns the latest *zman krias shema* (time to recite Shema in the morning) according to the
     * opinion of the [Magen Avraham (MGA)](https://en.wikipedia.org/wiki/Avraham_Gombinern) based on
     * *alos* being [72][.getAlos72] minutes before [sunrise][.getSunrise]. This time is 3 [shaahZmanis72Minutes] (solar hours) after [dawn][.getAlos72] based on the opinion
     * of the MGA that the day is calculated from a [dawn][.getAlos72] of 72 minutes before sunrise to
     * [nightfall][.getTzais72] of 72 minutes after sunset. This returns the time of 3 * [shaahZmanis72Minutes] after [dawn][.getAlos72]. This class returns an identical time to [ ][.getSofZmanShmaMGA] and is repeated here for clarity.
     *
     * @return the `Date` of the latest *zman krias shema*. If the calculation can't be computed such
     * as in the Arctic Circle where there is at least one day a year where the sun does not rise, and one where
     * it does not set, a null will be returned. See detailed explanation on top of the
     * [AstronomicalCalendar] documentation.
     * @see .getShaahZmanis72Minutes
     * @see .getAlos72
     * @see .getSofZmanShmaMGA
     */
    val sofZmanShmaMGA72Minutes: Instant? get() = sofZmanShmaMGA

    /**
     * This method returns the latest *zman krias shema* (time to recite *Shema* in the morning) according
     * to the opinion of the [Magen Avraham (MGA)](https://en.wikipedia.org/wiki/Avraham_Gombinern) based
     * on *alos* being [72][.getAlos72Zmanis] minutes *zmaniyos*, or 1/10th of the day before
     * [sunrise][.getSunrise]. This time is 3 [<em>shaos zmaniyos</em>][.getShaahZmanis90MinutesZmanis]
     * (solar hours) after [dawn][.getAlos72Zmanis] based on the opinion of the MGA that the day is calculated
     * from a [dawn][.getAlos72Zmanis] of 72 minutes *zmaniyos*, or 1/10th of the day before
     * [sea level sunrise][.getSeaLevelSunrise] to [nightfall][.getTzais72Zmanis] of 72 minutes
     * *zmaniyos* after [sea level sunset][.getSeaLevelSunset]. This returns the time of 3 *
     * [.getShaahZmanis72MinutesZmanis] after [dawn][.getAlos72Zmanis].
     *
     * @return the `Date` of the latest *zman krias shema*. If the calculation can't be computed such
     * as in the Arctic Circle where there is at least one day a year where the sun does not rise, and one where
     * it does not set, a null will be returned. See detailed explanation on top of the
     * [AstronomicalCalendar] documentation.
     * @see .getShaahZmanis72MinutesZmanis
     * @see .getAlos72Zmanis
     */
    val sofZmanShmaMGA72MinutesZmanis: Instant?
        get() = getSofZmanShma(alos72Zmanis, tzais72Zmanis)

    /**
     * This method returns the latest *zman krias shema* (time to recite *Shema* in the morning) according
     * to the opinion of the [Magen Avraham (MGA)](https://en.wikipedia.org/wiki/Avraham_Gombinern) based on
     * *alos* being [90][.getAlos90] minutes before [sunrise][.getSunrise]. This time is 3
     * [<em>shaos zmaniyos</em>][.getShaahZmanis90Minutes] (solar hours) after [dawn][.getAlos90] based on
     * the opinion of the MGA that the day is calculated from a [dawn][.getAlos90] of 90 minutes before sunrise to
     * [nightfall][.getTzais90] of 90 minutes after sunset. This returns the time of 3 *
     * [.getShaahZmanis90Minutes] after [dawn][.getAlos90].
     *
     * @return the `Date` of the latest *zman krias shema*. If the calculation can't be computed such
     * as in the Arctic Circle where there is at least one day a year where the sun does not rise, and one where
     * it does not set, a null will be returned. See detailed explanation on top of the
     * [AstronomicalCalendar] documentation.
     * @see .getShaahZmanis90Minutes
     * @see .getAlos90
     */
    val sofZmanShmaMGA90Minutes: Instant?
        get() = getSofZmanShma(alos90, tzais90)

    /**
     * This method returns the latest *zman krias shema* (time to recite Shema in the morning) according to the
     * opinion of the [Magen Avraham (MGA)](https://en.wikipedia.org/wiki/Avraham_Gombinern) based
     * on *alos* being [90][.getAlos90Zmanis] minutes *zmaniyos* before [ sunrise][.getSunrise]. This time is 3 [<em>shaos zmaniyos</em>][.getShaahZmanis90MinutesZmanis] (solar hours) after
     * [dawn][.getAlos90Zmanis] based on the opinion of the MGA that the day is calculated from a [alos90Zmanis] of 90 minutes *zmaniyos* before sunrise to [nightfall][.getTzais90Zmanis]
     * of 90 minutes *zmaniyos* after sunset. This returns the time of 3 * [.getShaahZmanis90MinutesZmanis]
     * after [dawn][.getAlos90Zmanis].
     *
     * @return the `Date` of the latest *zman krias shema*. If the calculation can't be computed such
     * as in the Arctic Circle where there is at least one day a year where the sun does not rise, and one where
     * it does not set, a null will be returned. See detailed explanation on top of the
     * [AstronomicalCalendar] documentation.
     * @see .getShaahZmanis90MinutesZmanis
     * @see .getAlos90Zmanis
     */
    val sofZmanShmaMGA90MinutesZmanis: Instant?
        get() = getSofZmanShma(alos90Zmanis, tzais90Zmanis)

    /**
     * This method returns the latest *zman krias shema* (time to recite Shema in the morning) according to the
     * opinion of the [Magen Avraham (MGA)](https://en.wikipedia.org/wiki/Avraham_Gombinern) based
     * on *alos* being [96][.getAlos96] minutes before [sunrise][.getSunrise]. This time is 3
     * [<em>shaos zmaniyos</em>][.getShaahZmanis96Minutes] (solar hours) after [dawn][.getAlos96] based on
     * the opinion of the MGA that the day is calculated from a [dawn][.getAlos96] of 96 minutes before
     * sunrise to [nightfall][.getTzais96] of 96 minutes after sunset. This returns the time of 3 * [shaahZmanis96Minutes] after [dawn][.getAlos96].
     *
     * @return the `Date` of the latest *zman krias shema*. If the calculation can't be computed such
     * as in the Arctic Circle where there is at least one day a year where the sun does not rise, and one where
     * it does not set, a null will be returned. See detailed explanation on top of the
     * [AstronomicalCalendar] documentation.
     * @see .getShaahZmanis96Minutes
     * @see .getAlos96
     */
    val sofZmanShmaMGA96Minutes: Instant?
        get() = getSofZmanShma(alos96, tzais96)

    /**
     * This method returns the latest *zman krias shema* (time to recite Shema in the morning) according to the
     * opinion of the [Magen Avraham (MGA)](https://en.wikipedia.org/wiki/Avraham_Gombinern) based
     * on *alos* being [96][.getAlos90Zmanis] minutes *zmaniyos* before [ sunrise][.getSunrise]. This time is 3 [<em>shaos zmaniyos</em>][.getShaahZmanis96MinutesZmanis] (solar hours) after
     * [dawn][.getAlos96Zmanis] based on the opinion of the MGA that the day is calculated from a [alos96Zmanis] of 96 minutes *zmaniyos* before sunrise to [nightfall][.getTzais90Zmanis]
     * of 96 minutes *zmaniyos* after sunset. This returns the time of 3 * [.getShaahZmanis96MinutesZmanis]
     * after [dawn][.getAlos96Zmanis].
     *
     * @return the `Date` of the latest *zman krias shema*. If the calculation can't be computed such
     * as in the Arctic Circle where there is at least one day a year where the sun does not rise, and one where
     * it does not set, a null will be returned. See detailed explanation on top of the
     * [AstronomicalCalendar] documentation.
     * @see .getShaahZmanis96MinutesZmanis
     * @see .getAlos96Zmanis
     */
    val sofZmanShmaMGA96MinutesZmanis: Instant?
        get() = getSofZmanShma(alos96Zmanis, tzais96Zmanis)

    /**
     * This method returns the latest *zman krias shema* (time to recite *Shema* in the morning) calculated
     * as 3 hours (regular clock hours and not *sha'os zmaniyos*) before [ZmanimCalendar.getChatzos].
     * Generally known as part of the "Komarno" *zmanim* after [Rav Yitzchak Eizik of
 * Komarno](https://en.wikipedia.org/wiki/Komarno_(Hasidic_dynasty)#Rabbi_Yitzchak_Eisik_Safrin), a proponent of this calculation, it actually predates him a lot. It is the opinion of the
     * *Shach* in the Nekudas Hakesef (Yoreh Deah 184), [Rav Moshe Lifshitz](https://hebrewbooks.org/pdfpager.aspx?req=21638&st=&pgnum=30) in his commentary
     * [Lechem Mishneh on Brachos 1:2](https://hebrewbooks.org/pdfpager.aspx?req=21638&st=&pgnum=50). It is
     * next brought down about 100 years later by the [Yaavetz](https://en.wikipedia.org/wiki/Jacob_Emden)
     * (in his *siddur*, [Mor Uktziah Orach
 * Chaim 1](https://hebrewbooks.org/pdfpager.aspx?req=7920&st=&pgnum=6), [Lechem Shamayim, Brachos 1:2](https://hebrewbooks.org/pdfpager.aspx?req=22309&st=&pgnum=30)
     * and [She'elos Yaavetz vol. 1 no. 40](https://hebrewbooks.org/pdfpager.aspx?req=1408&st=&pgnum=69)),
     * Rav Yitzchak Eizik of Komarno in the Ma'aseh Oreg on Mishnayos Brachos 11:2, Shevus Yaakov, Chasan Sofer and others.
     * See Yisrael Vehazmanim [vol. 1 7:3, page 55 -
 * 62](https://hebrewbooks.org/pdfpager.aspx?req=9765&st=&pgnum=83). A variant of this calculation [.getSofZmanShmaFixedLocal] uses [fixed][.getFixedLocalChatzos] for calculating this type of *zman*.
     *
     * @return the `Date` of the latest *zman krias shema*. If the calculation can't be computed such
     * as in the Arctic Circle where there is at least one day a year where the sun does not rise, and one where
     * it does not set, a null will be returned. See detailed explanation on top of the
     * [AstronomicalCalendar] documentation.
     * @see ZmanimCalendar.getChatzos
     * @see .getSofZmanShmaFixedLocal
     * @see .getSofZmanTfila2HoursBeforeChatzos
     */
    val sofZmanShma3HoursBeforeChatzos: Instant?
        get() = getTimeOffset(
            chatzos,
            -180 * MINUTE_MILLIS
        )

    /**
     * This method returns the latest *zman krias shema* (time to recite Shema in the morning) according to the
     * opinion of the [Magen Avraham (MGA)](https://en.wikipedia.org/wiki/Avraham_Gombinern) based
     * on *alos* being [120][.getAlos120] minutes or 1/6th of the day before [sunrise][.getSunrise].
     * This time is 3 [<em>shaos zmaniyos</em>][.getShaahZmanis120Minutes] (solar hours) after [ dawn][.getAlos120] based on the opinion of the MGA that the day is calculated from a [dawn][.getAlos120] of 120 minutes
     * before sunrise to [nightfall][.getTzais120] of 120 minutes after sunset. This returns the time of 3
     * [.getShaahZmanis120Minutes] after [dawn][.getAlos120]. This is an extremely early *zman* that
     * is very much a *chumra*.
     *
     * @return the `Date` of the latest *zman krias shema*. If the calculation can't be computed such
     * as in the Arctic Circle where there is at least one day a year where the sun does not rise, and one where
     * it does not set, a null will be returned. See detailed explanation on top of the
     * [AstronomicalCalendar] documentation.
     * @see .getShaahZmanis120Minutes
     * @see .getAlos120
     */
    val sofZmanShmaMGA120Minutes: Instant?
        get() = getSofZmanShma(alos120, tzais120)

    /**
     * This method returns the latest *zman krias shema* (time to recite *Shema* in the morning) based
     * on the opinion that the day starts at [<em>alos</em> 16.1&amp;deg;][.getAlos16Point1Degrees] and ends at
     * [sea level sunset][.getSeaLevelSunset]. This is the opinion of the [\u05D7\u05D9\u05D3\u05D5\u05E9\u05D9
 * \u05D5\u05DB\u05DC\u05DC\u05D5\u05EA \u05D4\u05E8\u05D6\u05F4\u05D4](https://hebrewbooks.org/40357) and the [\u05DE\u05E0\u05D5\u05E8\u05D4 \u05D4\u05D8\u05D4\u05D5\u05E8\u05D4](https://hebrewbooks.org/14799) as
     * mentioned by Yisrael Vehazmanim [vol 1, sec. 7,
 * ch. 3 no. 16](https://hebrewbooks.org/pdfpager.aspx?req=9765&pgnum=81). Three *shaos zmaniyos* are calculated based on this day and added to [alos16Point1Degrees] to reach this time. This time is 3 *shaos zmaniyos* (solar hours)
     * after [dawn][.getAlos16Point1Degrees] based on the opinion that the day is calculated from a [alos16Point1Degrees] to [sea level sunset][.getSeaLevelSunset].
     * **Note:** Based on this calculation *chatzos* will not be at midday.
     *
     * @return the `Date` of the latest *zman krias shema* based on this day. If the calculation can't
     * be computed such as northern and southern locations even south of the Arctic Circle and north of the
     * Antarctic Circle where the sun may not reach low enough below the horizon for this calculation, a null
     * will be returned. See detailed explanation on top of the [AstronomicalCalendar] documentation.
     * @see .getAlos16Point1Degrees
     * @see .getSeaLevelSunset
     */
    val sofZmanShmaAlos16Point1ToSunset: Instant?
        get() = getSofZmanShma(alos16Point1Degrees, elevationAdjustedSunset)

    /**
     * This method returns the latest *zman krias shema* (time to recite Shema in the morning) based on the
     * opinion that the day starts at [<em>alos</em> 16.1&amp;deg;][.getAlos16Point1Degrees] and ends at
     * [<em>tzais</em> 7.083&amp;deg;][.getTzaisGeonim7Point083Degrees]. 3 *shaos zmaniyos* are calculated
     * based on this day and added to [<em>alos</em>][.getAlos16Point1Degrees] to reach this time. This time is 3
     * *shaos zmaniyos* (temporal hours) after [<em>alos</em> 16.1&amp;deg;][.getAlos16Point1Degrees] based on
     * the opinion that the day is calculated from a [<em>alos</em> 16.1&amp;deg;][.getAlos16Point1Degrees] to
     * [<em>tzais</em> 7.083&amp;deg;][.getTzaisGeonim7Point083Degrees].
     * **Note: ** Based on this calculation *chatzos* will not be at midday.
     *
     * @return the `Date` of the latest *zman krias shema* based on this calculation. If the
     * calculation can't be computed such as northern and southern locations even south of the Arctic Circle and
     * north of the Antarctic Circle where the sun may not reach low enough below the horizon for this
     * calculation, a null will be returned. See detailed explanation on top of the [AstronomicalCalendar]
     * documentation.
     * @see .getAlos16Point1Degrees
     * @see .getTzaisGeonim7Point083Degrees
     */
    val sofZmanShmaAlos16Point1ToTzaisGeonim7Point083Degrees: Instant?
        get() = getSofZmanShma(alos16Point1Degrees, tzaisGeonim7Point083Degrees)

    /**
     * This method returns the latest *zman tfila* (time to recite the morning prayers) according to the opinion
     * of the [Magen Avraham (MGA)](https://en.wikipedia.org/wiki/Avraham_Gombinern) based on
     * *alos* being [19.8&amp;deg;][.getAlos19Point8Degrees] before [sunrise][.getSunrise]. This time
     * is 4 [<em>shaos zmaniyos</em>][.getShaahZmanis19Point8Degrees] (solar hours) after [alos19Point8Degrees] based on the opinion of the MGA that the day is calculated from dawn to
     * nightfall with both being 19.8 below sunrise or sunset. This returns the time of 4 * [shaahZmanis19Point8Degrees] after [dawn][alos19Point8Degrees].
     *
     * @return the `Date` of the latest *zman krias shema*. If the calculation can't be computed such
     * as northern and southern locations even south of the Arctic Circle and north of the Antarctic Circle
     * where the sun may not reach low enough below the horizon for this calculation, a null will be returned.
     * See detailed explanation on top of the [AstronomicalCalendar] documentation.
     *
     * @see .getShaahZmanis19Point8Degrees
     * @see .getAlos19Point8Degrees
     */
    val sofZmanTfilaMGA19Point8Degrees: Instant?
        get() = getSofZmanTfila(alos19Point8Degrees, tzais19Point8Degrees)

    /**
     * This method returns the latest *zman tfila* (time to recite the morning prayers) according to the opinion
     * of the [Magen Avraham (MGA)](https://en.wikipedia.org/wiki/Avraham_Gombinern) based on
     * *alos* being [16.1&amp;deg;][.getAlos16Point1Degrees] before [sunrise][.getSunrise]. This time
     * is 4 [<em>shaos zmaniyos</em>][.getShaahZmanis16Point1Degrees] (solar hours) after [alos16Point1Degrees] based on the opinion of the MGA that the day is calculated from dawn to
     * nightfall with both being 16.1 below sunrise or sunset. This returns the time of 4 * [shaahZmanis16Point1Degrees] after [dawn][alos16Point1Degrees].
     *
     * @return the `Date` of the latest *zman krias shema*. If the calculation can't be computed such
     * as northern and southern locations even south of the Arctic Circle and north of the Antarctic Circle
     * where the sun may not reach low enough below the horizon for this calculation, a null will be returned.
     * See detailed explanation on top of the [AstronomicalCalendar] documentation.
     *
     * @see .getShaahZmanis16Point1Degrees
     * @see .getAlos16Point1Degrees
     */
    val sofZmanTfilaMGA16Point1Degrees: Instant?
        get() = getSofZmanTfila(alos16Point1Degrees, tzais16Point1Degrees)

    /**
     * This method returns the latest *zman tfila* (time to recite the morning prayers) according to the opinion
     * of the [Magen Avraham (MGA)](https://en.wikipedia.org/wiki/Avraham_Gombinern) based on
     * *alos* being [18&amp;deg;][.getAlos18Degrees] before [sunrise][.getSunrise]. This time is 4
     * [<em>shaos zmaniyos</em>][.getShaahZmanis18Degrees] (solar hours) after [dawn][.getAlos18Degrees]
     * based on the opinion of the MGA that the day is calculated from dawn to nightfall with both being 18
     * below sunrise or sunset. This returns the time of 4 * [.getShaahZmanis18Degrees] after
     * [dawn][.getAlos18Degrees].
     *
     * @return the `Date` of the latest *zman krias shema*. If the calculation can't be computed such
     * as northern and southern locations even south of the Arctic Circle and north of the Antarctic Circle
     * where the sun may not reach low enough below the horizon for this calculation, a null will be returned.
     * See detailed explanation on top of the [AstronomicalCalendar] documentation.
     *
     * @see .getShaahZmanis18Degrees
     * @see .getAlos18Degrees
     */
    val sofZmanTfilaMGA18Degrees: Instant?
        get() = getSofZmanTfila(alos18Degrees, tzais18Degrees)

    /**
     * This method returns the latest *zman tfila* (time to recite the morning prayers) according to the opinion
     * of the [Magen Avraham (MGA)](https://en.wikipedia.org/wiki/Avraham_Gombinern) based on
     * *alos* being [72][.getAlos72] minutes before [sunrise][.getSunrise]. This time is 4
     * [<em>shaos zmaniyos</em>][.getShaahZmanis72Minutes] (solar hours) after [dawn][.getAlos72] based on
     * the opinion of the MGA that the day is calculated from a [dawn][.getAlos72] of 72 minutes before
     * sunrise to [nightfall][.getTzais72] of 72 minutes after sunset. This returns the time of 4 *
     * [.getShaahZmanis72Minutes] after [dawn][.getAlos72]. This class returns an identical time to
     * [.getSofZmanTfilaMGA] and is repeated here for clarity.
     *
     * @return the `Date` of the latest *zman tfila*. If the calculation can't be computed such as in
     * the Arctic Circle where there is at least one day a year where the sun does not rise, and one where it
     * does not set, a null will be returned. See detailed explanation on top of the
     * [AstronomicalCalendar] documentation.
     * @see .getShaahZmanis72Minutes
     * @see .getAlos72
     * @see .getSofZmanShmaMGA
     */
    val sofZmanTfilaMGA72Minutes: Instant? get() = sofZmanTfilaMGA

    /**
     * This method returns the latest *zman tfila* (time to the morning prayers) according to the opinion of the
     * [Magen Avraham (MGA)](https://en.wikipedia.org/wiki/Avraham_Gombinern) based on *alos*
     * being [72][.getAlos72Zmanis] minutes *zmaniyos* before [sunrise][.getSunrise]. This time is 4
     * [<em>shaos zmaniyos</em>][.getShaahZmanis72MinutesZmanis] (solar hours) after [dawn][.getAlos72Zmanis]
     * based on the opinion of the MGA that the day is calculated from a [dawn][.getAlos72Zmanis] of 72
     * minutes *zmaniyos* before sunrise to [nightfall][.getTzais72Zmanis] of 72 minutes *zmaniyos*
     * after sunset. This returns the time of 4 * [.getShaahZmanis72MinutesZmanis] after [dawn][.getAlos72Zmanis].
     *
     * @return the `Date` of the latest *zman krias shema*. If the calculation can't be computed such
     * as in the Arctic Circle where there is at least one day a year where the sun does not rise, and one where
     * it does not set, a null will be returned. See detailed explanation on top of the
     * [AstronomicalCalendar] documentation.
     * @see .getShaahZmanis72MinutesZmanis
     * @see .getAlos72Zmanis
     */
    val sofZmanTfilaMGA72MinutesZmanis: Instant?
        get() = getSofZmanTfila(
            alos72Zmanis,
            tzais72Zmanis
        )

    /**
     * This method returns the latest *zman tfila* (time to recite the morning prayers) according to the opinion
     * of the [Magen Avraham (MGA)](https://en.wikipedia.org/wiki/Avraham_Gombinern) based on
     * *alos* being [90][.getAlos90] minutes before [sunrise][.getSunrise]. This time is 4
     * [<em>shaos zmaniyos</em>][.getShaahZmanis90Minutes] (solar hours) after [dawn][.getAlos90] based on
     * the opinion of the MGA that the day is calculated from a [dawn][.getAlos90] of 90 minutes before sunrise to
     * [nightfall][.getTzais90] of 90 minutes after sunset. This returns the time of 4 *
     * [.getShaahZmanis90Minutes] after [dawn][.getAlos90].
     *
     * @return the `Date` of the latest *zman tfila*. If the calculation can't be computed such as in
     * the Arctic Circle where there is at least one day a year where the sun does not rise, and one where it
     * does not set, a null will be returned. See detailed explanation on top of the
     * [AstronomicalCalendar] documentation.
     * @see .getShaahZmanis90Minutes
     * @see .getAlos90
     */
    val sofZmanTfilaMGA90Minutes: Instant? get() = getSofZmanTfila(alos90, tzais90)

    /**
     * This method returns the latest *zman tfila* (time to the morning prayers) according to the opinion of the
     * [Magen Avraham (MGA)](https://en.wikipedia.org/wiki/Avraham_Gombinern) based on *alos*
     * being [90][.getAlos90Zmanis] minutes *zmaniyos* before [sunrise][.getSunrise]. This time is
     * 4 [<em>shaos zmaniyos</em>][.getShaahZmanis90MinutesZmanis] (solar hours) after [ dawn][.getAlos90Zmanis] based on the opinion of the MGA that the day is calculated from a [dawn][.getAlos90Zmanis]
     * of 90 minutes *zmaniyos* before sunrise to [nightfall][.getTzais90Zmanis] of 90 minutes
     * *zmaniyos* after sunset. This returns the time of 4 * [.getShaahZmanis90MinutesZmanis] after
     * [dawn][.getAlos90Zmanis].
     *
     * @return the `Date` of the latest *zman krias shema*. If the calculation can't be computed such
     * as in the Arctic Circle where there is at least one day a year where the sun does not rise, and one where
     * it does not set, a null will be returned. See detailed explanation on top of the
     * [AstronomicalCalendar] documentation.
     * @see .getShaahZmanis90MinutesZmanis
     * @see .getAlos90Zmanis
     */
    val sofZmanTfilaMGA90MinutesZmanis: Instant?
        get() = getSofZmanTfila(
            alos90Zmanis,
            tzais90Zmanis
        )

    /**
     * This method returns the latest *zman tfila* (time to recite the morning prayers) according to the opinion
     * of the [Magen Avraham (MGA)](https://en.wikipedia.org/wiki/Avraham_Gombinern) based on
     * *alos* being [96][.getAlos96] minutes before [sunrise][.getSunrise]. This time is 4
     * [<em>shaos zmaniyos</em>][.getShaahZmanis96Minutes] (solar hours) after [dawn][.getAlos96] based on
     * the opinion of the MGA that the day is calculated from a [dawn][.getAlos96] of 96 minutes before
     * sunrise to [nightfall][.getTzais96] of 96 minutes after sunset. This returns the time of 4 *
     * [.getShaahZmanis96Minutes] after [dawn][.getAlos96].
     *
     * @return the `Date` of the latest *zman tfila*. If the calculation can't be computed such as in
     * the Arctic Circle where there is at least one day a year where the sun does not rise, and one where it
     * does not set, a null will be returned. See detailed explanation on top of the
     * [AstronomicalCalendar] documentation.
     * @see .getShaahZmanis96Minutes
     * @see .getAlos96
     */
    val sofZmanTfilaMGA96Minutes: Instant? get() = getSofZmanTfila(alos96, tzais96)

    /**
     * This method returns the latest *zman tfila* (time to the morning prayers) according to the opinion of the
     * [Magen Avraham (MGA)](https://en.wikipedia.org/wiki/Avraham_Gombinern) based on *alos*
     * being [96][.getAlos96Zmanis] minutes *zmaniyos* before [sunrise][.getSunrise]. This time is
     * 4 [<em>shaos zmaniyos</em>][.getShaahZmanis96MinutesZmanis] (solar hours) after [ dawn][.getAlos96Zmanis] based on the opinion of the MGA that the day is calculated from a [dawn][.getAlos96Zmanis]
     * of 96 minutes *zmaniyos* before sunrise to [nightfall][.getTzais96Zmanis] of 96 minutes
     * *zmaniyos* after sunset. This returns the time of 4 * [.getShaahZmanis96MinutesZmanis] after
     * [dawn][.getAlos96Zmanis].
     *
     * @return the `Date` of the latest *zman krias shema*. If the calculation can't be computed such
     * as in the Arctic Circle where there is at least one day a year where the sun does not rise, and one where
     * it does not set, a null will be returned. See detailed explanation on top of the
     * [AstronomicalCalendar] documentation.
     * @see .getShaahZmanis90MinutesZmanis
     * @see .getAlos90Zmanis
     */
    val sofZmanTfilaMGA96MinutesZmanis: Instant?
        get() = getSofZmanTfila(
            alos96Zmanis,
            tzais96Zmanis
        )

    /**
     * This method returns the latest *zman tfila* (time to recite the morning prayers) according to the opinion
     * of the [Magen Avraham (MGA)](https://en.wikipedia.org/wiki/Avraham_Gombinern) based on
     * *alos* being [120][.getAlos120] minutes before [sunrise][.getSunrise] . This time is 4
     * [<em>shaos zmaniyos</em>][.getShaahZmanis120Minutes] (solar hours) after [dawn][.getAlos120]
     * based on the opinion of the MGA that the day is calculated from a [dawn][.getAlos120] of 120
     * minutes before sunrise to [nightfall][.getTzais120] of 120 minutes after sunset. This returns the time of
     * 4 * [.getShaahZmanis120Minutes] after [dawn][.getAlos120]. This is an extremely early *zman*
     * that is very much a *chumra*.
     *
     * @return the `Date` of the latest *zman krias shema*. If the calculation can't be computed such
     * as in the Arctic Circle where there is at least one day a year where the sun does not rise, and one where
     * it does not set, a null will be returned. See detailed explanation on top of the
     * [AstronomicalCalendar] documentation.
     * @see .getShaahZmanis120Minutes
     * @see .getAlos120
     */
    val sofZmanTfilaMGA120Minutes: Instant? get() = getSofZmanTfila(alos120, tzais120)

    /**
     * This method returns the latest *zman tfila* (time to recite the morning prayers) calculated as 2 hours
     * before [ZmanimCalendar.getChatzos]. This is based on the opinions that calculate
     * *sof zman krias shema* as [.getSofZmanShma3HoursBeforeChatzos]. This returns the time of 2 hours
     * before [ZmanimCalendar.getChatzos].
     *
     * @return the `Date` of the latest *zman krias shema*. If the calculation can't be computed such
     * as in the Arctic Circle where there is at least one day a year where the sun does not rise, and one where
     * it does not set, a null will be returned. See detailed explanation on top of the
     * [AstronomicalCalendar] documentation.
     * @see ZmanimCalendar.getChatzos
     * @see .getSofZmanShma3HoursBeforeChatzos
     */
    val sofZmanTfila2HoursBeforeChatzos: Instant?
        get() = getTimeOffset(
            chatzos,
            -120 * MINUTE_MILLIS
        )

    /**
     * This method returns *mincha gedola* calculated as 30 minutes after [<em>chatzos</em>][.getChatzos]
     * and not 1/2 of a [<em>shaah zmanis</em>][.getShaahZmanisGra] after [<em>chatzos</em>][.getChatzos] as
     * calculated by [.getMinchaGedola]. Some use this time to delay the start of *mincha* in the winter when
     * 1/2 of a [<em>shaah zmanis</em>][.getShaahZmanisGra] is less than 30 minutes. See
     * [.getMinchaGedolaGreaterThan30] for a convenience method that returns the later of the 2 calculations. One
     * should not use this time to start *mincha* before the standard [<em>mincha gedola</em>][.getMinchaGedola].
     * See Shulchan Aruch [Orach Chayim 234:1](https://hebrewbooks.org/pdfpager.aspx?req=49624&st=&pgnum=291) and
     * the Shaar Hatziyon *seif katan ches*.
     *
     * @return the `Date` of 30 minutes after *chatzos*. If the calculation can't be computed such as
     * in the Arctic Circle where there is at least one day a year where the sun does not rise, and one where it
     * does not set, a null will be returned. See detailed explanation on top of the
     * [AstronomicalCalendar] documentation.
     * @see .getMinchaGedola
     * @see .getMinchaGedolaGreaterThan30
     */
    val minchaGedola30Minutes: Instant?
        get() = getTimeOffset(
            chatzos,
            MINUTE_MILLIS * 30
        )

    /**
     * This method returns the time of *mincha gedola* according to the Magen Avraham with the day starting 72
     * minutes before sunrise and ending 72 minutes after sunset. This is the earliest time to pray *mincha*. For
     * more information on this see the documentation on [<em>mincha gedola</em>][.getMinchaGedola]. This is
     * calculated as 6.5 [solar hours][.getTemporalHour] after *alos*. The calculation used is 6.5 *
     * [.getShaahZmanis72Minutes] after [<em>alos</em>][.getAlos72].
     *
     * @see .getAlos72
     * @see .getMinchaGedola
     * @see .getMinchaKetana
     * @see ZmanimCalendar.getMinchaGedola
     * @return the `Date` of the time of *mincha gedola*. If the calculation can't be computed such as
     * in the Arctic Circle where there is at least one day a year where the sun does not rise, and one where it
     * does not set, a null will be returned. See detailed explanation on top of the [AstronomicalCalendar]
     * documentation.
     */
    val minchaGedola72Minutes: Instant? get() = getMinchaGedola(alos72, tzais72)

    /**
     * This method returns the time of *mincha gedola* according to the Magen Avraham with the day starting and
     * ending 16.1 below the horizon. This is the earliest time to pray *mincha*. For more information on
     * this see the documentation on [<em>mincha gedola</em>][.getMinchaGedola]. This is calculated as 6.5
     * [solar hours][.getTemporalHour] after *alos*. The calculation used is 6.5 *
     * [.getShaahZmanis16Point1Degrees] after [<em>alos</em>][.getAlos16Point1Degrees].
     *
     * @see .getShaahZmanis16Point1Degrees
     * @see .getMinchaGedola
     * @see .getMinchaKetana
     * @return the `Date` of the time of *mincha gedola*. If the calculation can't be computed such as
     * northern and southern locations even south of the Arctic Circle and north of the Antarctic Circle where
     * the sun  may not reach low enough below the horizon for this calculation, a null will be returned. See
     * detailed explanation on top of the [AstronomicalCalendar] documentation.
     */
    val minchaGedola16Point1Degrees: Instant?
        get() = getMinchaGedola(alos16Point1Degrees, tzais16Point1Degrees)

    /**
     * This method returns the time of *mincha gedola* based on the opinion of [Rabbi Yaakov Moshe Hillel](https://en.wikipedia.org/wiki/Yaakov_Moshe_Hillel) as published in the *luach*
     * of the Bais Horaah of Yeshivat Chevrat Ahavat Shalom that *mincha gedola* is calculated as half a *shaah
     * zmanis* after *chatzos* with *shaos zmaniyos* calculated based on a day starting 72 minutes befoe sunrise
     * [<em>alos</em> 16.1&amp;deg;][.getAlos16Point1Degrees] and ending 13.5 minutes after sunset [tzaisGeonim3Point7Degrees]. *Mincha gedola* is the earliest time to pray *mincha*.
     * The later of this time or 30 clock minutes after *chatzos* is returned. See [minchaGedolaGreaterThan30]
     * (though that calculation is based on *mincha gedola* GRA).
     * For more information about *mincha gedola* see the documentation on [<em>mincha gedola</em>][.getMinchaGedola].
     *
     * @return the `Date` of the *mincha gedola*. If the calculation can't be computed such as northern and
     * southern locations even south of the Arctic Circle and north of the Antarctic Circle where the sun may not
     * reach low enough below the horizon for this calculation, a null will be returned. See detailed explanation
     * on top of the [AstronomicalCalendar] documentation.
     *
     * @see .getAlos16Point1Degrees
     * @see .getTzaisGeonim3Point7Degrees
     * @see .getShaahZmanisAlos16Point1ToTzais3Point7
     * @see .getMinchaGedolaGreaterThan30
     */
    val minchaGedolaAhavatShalom: Instant?
        get() =
            if (minchaGedola30Minutes == null || minchaGedola == null) null
            else if (minchaGedola30Minutes!! > getTimeOffset(
                    chatzos,
                    shaahZmanisAlos16Point1ToTzais3Point7 / 2
                )!!
            ) minchaGedola30Minutes
            else getTimeOffset(chatzos, shaahZmanisAlos16Point1ToTzais3Point7 / 2)

    /**
     * This is a convenience method that returns the later of [.getMinchaGedola] and
     * [.getMinchaGedola30Minutes]. In the winter when 1/2 of a [<em>shaah zmanis</em>][.getShaahZmanisGra] is
     * less than 30 minutes [.getMinchaGedola30Minutes] will be returned, otherwise [.getMinchaGedola]
     * will be returned.
     *
     * @return the `Date` of the later of [.getMinchaGedola] and [.getMinchaGedola30Minutes].
     * If the calculation can't be computed such as in the Arctic Circle where there is at least one day a year
     * where the sun does not rise, and one where it does not set, a null will be returned. See detailed
     * explanation on top of the [AstronomicalCalendar] documentation.
     */
    val minchaGedolaGreaterThan30: Instant?
        get() =
            if (minchaGedola30Minutes == null || minchaGedola == null) null
            else if (minchaGedola30Minutes!! > minchaGedola!!) minchaGedola30Minutes
            else minchaGedola

    /**
     * This method returns the time of *mincha ketana* according to the Magen Avraham with the day starting and
     * ending 16.1 below the horizon. This is the preferred earliest time to pray *mincha* according to the
     * opinion of the [Rambam](https://en.wikipedia.org/wiki/Maimonides) and others. For more information on
     * this see the documentation on [<em>mincha gedola</em>][.getMinchaGedola]. This is calculated as 9.5
     * [solar hours][.getTemporalHour] after *alos*. The calculation used is 9.5 *
     * [.getShaahZmanis16Point1Degrees] after [<em>alos</em>][.getAlos16Point1Degrees].
     *
     * @see .getShaahZmanis16Point1Degrees
     * @see .getMinchaGedola
     * @see .getMinchaKetana
     * @return the `Date` of the time of *mincha ketana*. If the calculation can't be computed such
     * as northern and southern locations even south of the Arctic Circle and north of the Antarctic Circle
     * where the sun may not reach low enough below the horizon for this calculation, a null will be returned.
     * See detailed explanation on top of the [AstronomicalCalendar] documentation.
     */
    val minchaKetana16Point1Degrees: Instant?
        get() = getMinchaKetana(alos16Point1Degrees, tzais16Point1Degrees)

    /**
     * This method returns the time of *mincha ketana* based on the opinion of [Rabbi Yaakov Moshe Hillel](https://en.wikipedia.org/wiki/Yaakov_Moshe_Hillel) as published in the *luach*
     * of the Bais Horaah of Yeshivat Chevrat Ahavat Shalom that *mincha ketana* is calculated as 2.5 *shaos
     * zmaniyos* before [<em>tzais</em> 3.8&amp;deg;][.getTzaisGeonim3Point8Degrees] with *shaos zmaniyos*
     * calculated based on a day starting at [<em>alos</em> 16.1&amp;deg;][.getAlos16Point1Degrees] and ending at
     * *tzais* 3.8. *Mincha ketana* is the preferred earliest time to pray *mincha* according to
     * the opinion of the [Rambam](https://en.wikipedia.org/wiki/Maimonides) and others. For more information
     * on this see the documentation on [<em>mincha ketana</em>][.getMinchaKetana].
     *
     * @return the the `Date` of the time of *mincha ketana*. If the calculation can't be computed such as northern
     * and southern locations even south of the Arctic Circle and north of the Antarctic Circle where the sun may not
     * reach low enough below the horizon for this calculation, a null will be returned. See detailed explanation
     * on top of the [AstronomicalCalendar] documentation.
     *
     * @see .getShaahZmanisAlos16Point1ToTzais3Point8
     * @see .getMinchaGedolaAhavatShalom
     * @see .getPlagAhavatShalom
     */
    val minchaKetanaAhavatShalom: Instant?
        get() = getTimeOffset(
            tzaisGeonim3Point8Degrees,
            -shaahZmanisAlos16Point1ToTzais3Point8 * 2.5
        )

    /**
     * This method returns the time of *mincha ketana* according to the Magen Avraham with the day
     * starting 72 minutes before sunrise and ending 72 minutes after sunset. This is the preferred earliest time to pray
     * *mincha* according to the opinion of the [Rambam](https://en.wikipedia.org/wiki/Maimonides)
     * and others. For more information on this see the documentation on [<em>mincha gedola</em>][.getMinchaGedola].
     * This is calculated as 9.5 [.getShaahZmanis72Minutes] after *alos*. The calculation used is 9.5 *
     * [.getShaahZmanis72Minutes] after [<em>alos</em>][.getAlos72].
     *
     * @see .getShaahZmanis16Point1Degrees
     * @see .getMinchaGedola
     * @see .getMinchaKetana
     * @return the `Date` of the time of *mincha ketana*. If the calculation can't be computed such as
     * in the Arctic Circle where there is at least one day a year where the sun does not rise, and one where it
     * does not set, a null will be returned. See detailed explanation on top of the [AstronomicalCalendar]
     * documentation.
     */
    val minchaKetana72Minutes: Instant?
        get() = getMinchaKetana(alos72, tzais72)

    /**
     * This method returns the time of *plag hamincha* according to the Magen Avraham with the day starting 60
     * minutes before sunrise and ending 60 minutes after sunset. This is calculated as 10.75 hours after
     * [dawn][.getAlos60]. The formula used is 10.75 [.getShaahZmanis60Minutes] after [.getAlos60].
     *
     * @return the `Date` of the time of *plag hamincha*. If the calculation can't be computed such as
     * in the Arctic Circle where there is at least one day a year where the sun does not rise, and one where it
     * does not set, a null will be returned. See detailed explanation on top of the
     * [AstronomicalCalendar] documentation.
     *
     * @see .getShaahZmanis60Minutes
     * @see .getAlos60
     * @see .getTzais60
     */
    val plagHamincha60Minutes: Instant?
        get() = getPlagHamincha(alos60, tzais60)

    /**
     * This method should be used *lechumra* only and returns the time of *plag hamincha* according to the
     * Magen Avraham with the day starting 72 minutes before sunrise and ending 72 minutes after sunset. This is calculated
     * as 10.75 hours after [dawn][.getAlos72]. The formula used is 10.75 [.getShaahZmanis72Minutes] after
     * [.getAlos72]. Since *plag* by this calculation can occur after sunset, it should only be used
     * *lechumra*.
     *
     * @return the `Date` of the time of *plag hamincha*. If the calculation can't be computed such as
     * in the Arctic Circle where there is at least one day a year where the sun does not rise, and one where it
     * does not set, a null will be returned. See detailed explanation on top of the
     * [AstronomicalCalendar] documentation.
     *
     * @see .getShaahZmanis72Minutes
     */
    @Deprecated("This method should be used <em>lechumra</em> only since it returns a very late time, and if used\n" + "	          <em>lekula</em> can result in <em>chillul Shabbos</em> etc. There is no current plan to remove this\n" + "	          method from the API, and this deprecation is intended to alert developers of the danger of using it.\n" + "	  \n" + "	  ")  // (forRemoval=false) // add back once Java 9 is the minimum supported version
    val plagHamincha72Minutes: Instant?
        get() = getPlagHamincha(alos72, tzais72)

    /**
     * This method should be used *lechumra* only and returns the time of *plag hamincha* according to the
     * Magen Avraham with the day starting 90 minutes before sunrise and ending 90 minutes after sunset. This is calculated
     * as 10.75 hours after [dawn][.getAlos90]. The formula used is 10.75 [.getShaahZmanis90Minutes] after
     * [.getAlos90]. Since *plag* by this calculation can occur after sunset, it should only be used
     * *lechumra*.
     *
     * @return the `Date` of the time of *plag hamincha*. If the calculation can't be computed such as
     * in the Arctic Circle where there is at least one day a year where the sun does not rise, and one where it
     * does not set, a null will be returned. See detailed explanation on top of the
     * [AstronomicalCalendar] documentation.
     *
     * @see .getShaahZmanis90Minutes
     */
    @Deprecated("This method should be used <em>lechumra</em> only since it returns a very late time, and if used\n" + "	          <em>lekula</em> can result in <em>chillul Shabbos</em> etc. There is no current plan to remove this\n" + "	          method from the API, and this deprecation is intended to alert developers of the danger of using it.\n" + "	  \n" + "	  ")  // (forRemoval=false) // add back once Java 9 is the minimum supported version
    val plagHamincha90Minutes: Instant?
        get() = getPlagHamincha(alos90, tzais90)

    /**
     * This method should be used *lechumra* only and returns the time of *plag hamincha* according to the Magen
     * Avraham with the day starting 96 minutes before sunrise and ending 96 minutes after sunset. This is calculated as 10.75
     * hours after [dawn][.getAlos96]. The formula used is 10.75 [.getShaahZmanis96Minutes] after
     * [.getAlos96]. Since *plag* by this calculation can occur after sunset, it should only be used
     * *lechumra*.
     *
     * @return the `Date` of the time of *plag hamincha*. If the calculation can't be computed such as
     * in the Arctic Circle where there is at least one day a year where the sun does not rise, and one where it
     * does not set, a null will be returned. See detailed explanation on top of the
     * [AstronomicalCalendar] documentation.
     * @see .getShaahZmanis96Minutes
     */
    @Deprecated("This method should be used <em>lechumra</em> only since it returns a very late time, and if used\n" + "	          <em>lekula</em> can result in <em>chillul Shabbos</em> etc. There is no current plan to remove this\n" + "	          method from the API, and this deprecation is intended to alert developers of the danger of using it.\n" + "	  \n" + "	  ")  // (forRemoval=false) // add back once Java 9 is the minimum supported version
    val plagHamincha96Minutes: Instant?
        get() = getPlagHamincha(alos96, tzais96)

    /**
     * This method should be used *lechumra* only and returns the time of *plag hamincha*. This is calculated
     * as 10.75 hours after [dawn][.getAlos96Zmanis]. The formula used is 10.75 * [shaahZmanis96MinutesZmanis] after [dawn][.getAlos96Zmanis]. Since *plag* by this calculation can
     * occur after sunset, it should only be used *lechumra*.
     *
     * @return the `Date` of the time of *plag hamincha*. If the calculation can't be computed such as
     * in the Arctic Circle where there is at least one day a year where the sun does not rise, and one where it
     * does not set, a null will be returned. See detailed explanation on top of the
     * [AstronomicalCalendar] documentation.
     */
    @Deprecated("This method should be used <em>lechumra</em> only since it returns a very late time, and if used\n" + "	          <em>lekula</em> can result in <em>chillul Shabbos</em> etc. There is no current plan to remove this\n" + "	          method from the API, and this deprecation is intended to alert developers of the danger of using it.\n" + "	  \n" + "	  ")  // (forRemoval=false) // add back once Java 9 is the minimum supported version
    val plagHamincha96MinutesZmanis: Instant?
        get() = getPlagHamincha(alos96Zmanis, tzais96Zmanis)

    /**
     * This method should be used *lechumra* only and returns the time of *plag hamincha*. This is calculated
     * as 10.75 hours after [dawn][.getAlos90Zmanis]. The formula used is 10.75 * [shaahZmanis90MinutesZmanis] after [dawn][.getAlos90Zmanis]. Since *plag* by this calculation can
     * occur after sunset, it should only be used *lechumra*.
     *
     * @return the `Date` of the time of *plag hamincha*. If the calculation can't be computed such as
     * in the Arctic Circle where there is at least one day a year where the sun does not rise, and one where it
     * does not set, a null will be returned. See detailed explanation on top of the
     * [AstronomicalCalendar] documentation.
     */
    @Deprecated("This method should be used <em>lechumra</em> only since it returns a very late time, and if used\n" + "	          <em>lekula</em> can result in <em>chillul Shabbos</em> etc. There is no current plan to remove this\n" + "	          method from the API, and this deprecation is intended to alert developers of the danger of using it.\n" + "	  \n" + "	  ")  // (forRemoval=false) // add back once Java 9 is the minimum supported version
    val plagHamincha90MinutesZmanis: Instant?
        get() = getPlagHamincha(alos90Zmanis, tzais90Zmanis)

    /**
     * This method should be used *lechumra* only and returns the time of *plag hamincha*. This is calculated as
     * 10.75 hours after [.getAlos72Zmanis]. The formula used is 10.75 * [.getShaahZmanis72MinutesZmanis] after
     * [dawn][.getAlos72Zmanis]. Since *plag* by this calculation can occur after sunset, it should only be used
     * *lechumra*.
     *
     * @return the `Date` of the time of *plag hamincha*. If the calculation can't be computed such as
     * in the Arctic Circle where there is at least one day a year where the sun does not rise, and one where it
     * does not set, a null will be returned. See detailed explanation on top of the
     * [AstronomicalCalendar] documentation.
     */
    @Deprecated("This method should be used <em>lechumra</em> only since it returns a very late time, and if used\n" + "	          <em>lekula</em> can result in <em>chillul Shabbos</em> etc. There is no current plan to remove this\n" + "	          method from the API, and this deprecation is intended to alert developers of the danger of using it.\n" + "	  \n" + "	  ")  // (forRemoval=false) // add back once Java 9 is the minimum supported version
    val plagHamincha72MinutesZmanis: Instant?
        get() = getPlagHamincha(alos72Zmanis, tzais72Zmanis)

    /**
     * This method should be used *lechumra* only and returns the time of *plag hamincha* based on the
     * opinion that the day starts at [<em>alos</em> 16.1&amp;deg;][.getAlos16Point1Degrees] and ends at [ ][.getTzais16Point1Degrees]. This is calculated as 10.75 hours *zmaniyos*
     * after [dawn][.getAlos16Point1Degrees]. The formula used is 10.75 * [.getShaahZmanis16Point1Degrees]
     * after [.getAlos16Point1Degrees]. Since *plag* by this calculation can occur after sunset, it
     * should only be used *lechumra*.
     *
     * @return the `Date` of the time of *plag hamincha*. If the calculation can't be computed such as
     * northern and southern locations even south of the Arctic Circle and north of the Antarctic Circle where
     * the sun may not reach low enough below the horizon for this calculation, a null will be returned. See
     * detailed explanation on top of the [AstronomicalCalendar] documentation.
     *
     * @see .getShaahZmanis16Point1Degrees
     */
    @Deprecated("This method should be used <em>lechumra</em> only since it returns a very late time, and if used\n" + "	          <em>lekula</em> can result in <em>chillul Shabbos</em> etc. There is no current plan to remove this\n" + "	          method from the API, and this deprecation is intended to alert developers of the danger of using it.\n" + "	  \n" + "	  ")  // (forRemoval=false) // add back once Java 9 is the minimum supported version
    val plagHamincha16Point1Degrees: Instant?
        get() = getPlagHamincha(alos16Point1Degrees, tzais16Point1Degrees)

    /**
     * This method should be used *lechumra* only and returns the time of *plag hamincha* based on the
     * opinion that the day starts at [<em>alos</em> 19.8&amp;deg;][alos19Point8Degrees] and ends at [tzais19Point8Degrees]. This is calculated as 10.75 hours *zmaniyos*
     * after [dawn][.getAlos19Point8Degrees]. The formula used is 10.75 * [shaahZmanis19Point8Degrees] after [alos19Point8Degrees]. Since *plag* by this
     * calculation can occur after sunset, it should only be used *lechumra*.
     *
     * @return the `Date` of the time of *plag hamincha*. If the calculation can't be computed such as
     * northern and southern locations even south of the Arctic Circle and north of the Antarctic Circle where
     * the sun may not reach low enough below the horizon for this calculation, a null will be returned. See
     * detailed explanation on top of the [AstronomicalCalendar] documentation.
     *
     * @see .getShaahZmanis19Point8Degrees
     */
    @Deprecated("This method should be used <em>lechumra</em> only since it returns a very late time, and if used\n" + "	          <em>lekula</em> can result in <em>chillul Shabbos</em> etc. There is no current plan to remove this\n" + "	          method from the API, and this deprecation is intended to alert developers of the danger of using it.\n" + "	  \n" + "	  ")  // (forRemoval=false) // add back once Java 9 is the minimum supported version
    val plagHamincha19Point8Degrees: Instant?
        get() = getPlagHamincha(alos19Point8Degrees, tzais19Point8Degrees)

    /**
     * This method should be used *lechumra* only and returns the time of *plag hamincha* based on the
     * opinion that the day starts at [<em>alos</em> 26&amp;deg;][.getAlos26Degrees] and ends at [tzais26Degrees]. This is calculated as 10.75 hours *zmaniyos* after [ ][.getAlos26Degrees]. The formula used is 10.75 * [.getShaahZmanis26Degrees] after [ ][.getAlos26Degrees]. Since the *zman* based on an extremely early *alos* and a very late
     * *tzais*, it should only be used *lechumra*.
     *
     * @return the `Date` of the time of *plag hamincha*. If the calculation can't be computed such as
     * northern and southern locations even south of the Arctic Circle and north of the Antarctic Circle where
     * the sun may not reach low enough below the horizon for this calculation, a null will be returned. See
     * detailed explanation on top of the [AstronomicalCalendar] documentation.
     *
     * @see .getShaahZmanis26Degrees
     * @see .getPlagHamincha120Minutes
     */
    @Deprecated("This method should be used <em>lechumra</em> only since it returns a very late time, and if used\n" + "	          <em>lekula</em> can result in <em>chillul Shabbos</em> etc. There is no current plan to remove this\n" + "	          method from the API, and this deprecation is intended to alert developers of the danger of using it.\n" + "	  \n" + "	  ")  // (forRemoval=false) // add back once Java 9 is the minimum supported version
    val plagHamincha26Degrees: Instant?
        get() = getPlagHamincha(alos26Degrees, tzais26Degrees)

    /**
     * This method should be used *lechumra* only and returns the time of *plag hamincha* based on the
     * opinion that the day starts at [<em>alos</em> 18&amp;deg;][.getAlos18Degrees] and ends at [tzais18Degrees]. This is calculated as 10.75 hours *zmaniyos* after [ ][.getAlos18Degrees]. The formula used is 10.75 * [.getShaahZmanis18Degrees] after [ ][.getAlos18Degrees]. Since *plag* by this calculation can occur after sunset, it should only be used
     * *lechumra*.
     *
     * @return the `Date` of the time of *plag hamincha*. If the calculation can't be computed such as
     * northern and southern locations even south of the Arctic Circle and north of the Antarctic Circle where
     * the sun may not reach low enough below the horizon for this calculation, a null will be returned. See
     * detailed explanation on top of the [AstronomicalCalendar] documentation.
     *
     * @see .getShaahZmanis18Degrees
     */
    @Deprecated("This method should be used <em>lechumra</em> only since it returns a very late time, and if used\n" + "	          <em>lekula</em> can result in <em>chillul Shabbos</em> etc. There is no current plan to remove this\n" + "	          method from the API, and this deprecation is intended to alert developers of the danger of using it.\n" + "	  \n" + "	  ")  // (forRemoval=false) // add back once Java 9 is the minimum supported version
    val plagHamincha18Degrees: Instant?
        get() = getPlagHamincha(alos18Degrees, tzais18Degrees)

    /**
     * This method should be used *lechumra* only and returns the time of *plag hamincha* based on the opinion
     * that the day starts at [<em>alos</em> 16.1&amp;deg;][.getAlos16Point1Degrees] and ends at [sunset][.getSunset].
     * 10.75 *shaos zmaniyos* are calculated based on this day and added to [ <em>alos</em>][.getAlos16Point1Degrees] to reach this time. This time is 10.75 *shaos zmaniyos* (temporal hours) after [alos16Point1Degrees] based on the opinion that the day is calculated from a [ dawn][.getAlos16Point1Degrees] of 16.1 degrees before sunrise to [sea level sunset][.getSeaLevelSunset]. This returns the time of 10.75 *
     * the calculated *shaah zmanis* after [dawn][.getAlos16Point1Degrees]. Since *plag* by this
     * calculation can occur after sunset, it should only be used *lechumra*.
     *
     * @return the `Date` of the *plag*. If the calculation can't be computed such as northern and southern
     * locations even south of the Arctic Circle and north of the Antarctic Circle where the sun may not reach
     * low enough below the horizon for this calculation, a null will be returned. See detailed explanation on
     * top of the [AstronomicalCalendar] documentation.
     *
     * @see .getAlos16Point1Degrees
     * @see .getSeaLevelSunset
     */
    @Deprecated("This method should be used <em>lechumra</em> only since it returns a very late time, and if used\n" + "	          <em>lekula</em> can result in <em>chillul Shabbos</em> etc. There is no current plan to remove this\n" + "	          method from the API, and this deprecation is intended to alert developers of the danger of using it.\n" + "	  \n" + "	  ")  // (forRemoval=false) // add back once Java 9 is the minimum supported version
    val plagAlosToSunset: Instant?
        get() = getPlagHamincha(alos16Point1Degrees, elevationAdjustedSunset)

    /**
     * This method returns the time of *plag hamincha* based on the opinion that the day starts at
     * [<em>alos</em> 16.1&amp;deg;][.getAlos16Point1Degrees] and ends at [ <em>tzais</em>][.getTzaisGeonim7Point083Degrees]. 10.75 *shaos zmaniyos* are calculated based on this day and added to [ ][.getAlos16Point1Degrees] to reach this time. This time is 10.75 *shaos zmaniyos* (temporal
     * hours) after [dawn][.getAlos16Point1Degrees] based on the opinion that the day is calculated from a
     * [dawn][.getAlos16Point1Degrees] of 16.1 degrees before sunrise to
     * [<em>tzais</em>][.getTzaisGeonim7Point083Degrees] . This returns the time of 10.75 * the calculated
     * *shaah zmanis* after [dawn][.getAlos16Point1Degrees].
     *
     * @return the `Date` of the *plag*. If the calculation can't be computed such as northern and
     * southern locations even south of the Arctic Circle and north of the Antarctic Circle where the sun may not
     * reach low enough below the horizon for this calculation, a null will be returned. See detailed explanation
     * on top of the [AstronomicalCalendar] documentation.
     *
     * @see .getAlos16Point1Degrees
     * @see .getTzaisGeonim7Point083Degrees
     */
    val plagAlos16Point1ToTzaisGeonim7Point083Degrees: Instant?
        get() = getPlagHamincha(alos16Point1Degrees, tzaisGeonim7Point083Degrees)

    /**
     * This method returns the time of *plag hamincha* (the earliest time that Shabbos can be started) based on the
     * opinion of [Rabbi Yaakov Moshe Hillel](https://en.wikipedia.org/wiki/Yaakov_Moshe_Hillel) as published in
     * the *luach* of the Bais Horaah of Yeshivat Chevrat Ahavat Shalom that that *plag hamincha* is calculated
     * as 1.25 *shaos zmaniyos* before [<em>tzais</em> 3.8&amp;deg;][.getTzaisGeonim3Point8Degrees] with *shaos
     * zmaniyos* calculated based on a day starting at [<em>alos</em> 16.1&amp;deg;][.getAlos16Point1Degrees] and
     * ending at *tzais* 3.8.
     *
     * @return the `Date` of the *plag*. If the calculation can't be computed such as northern and
     * southern locations even south of the Arctic Circle and north of the Antarctic Circle where the sun may not
     * reach low enough below the horizon for this calculation, a null will be returned. See detailed explanation
     * on top of the [AstronomicalCalendar] documentation.
     *
     * @see .getShaahZmanisAlos16Point1ToTzais3Point8
     * @see .getMinchaGedolaAhavatShalom
     * @see .getMinchaKetanaAhavatShalom
     */
    val plagAhavatShalom: Instant?
        get() = getTimeOffset(
            tzaisGeonim3Point8Degrees,
            -shaahZmanisAlos16Point1ToTzais3Point8 * 1.25
        )

    /**
     * Method to return the beginning of *bain hashmashos* of Rabbeinu Tam calculated when the sun is
     * [13.24&amp;deg;][.ZENITH_13_POINT_24] below the western [geometric horizon][.GEOMETRIC_ZENITH] (90)
     * after sunset. This calculation is based on the same calculation of [ <em>bain hashmashos</em> Rabbeinu Tam 58.5 minutes][.getBainHashmashosRT58Point5Minutes] but uses a degree-based calculation instead of 58.5 exact
     * minutes. This calculation is based on the position of the sun 58.5 minutes after sunset in Jerusalem [around the equinox / equilux](https://kosherjava.com/2022/01/12/equinox-vs-equilux-zmanim-calculations/),
     * which calculates to 13.24 below [geometric zenith][.GEOMETRIC_ZENITH].
     * NOTE: As per Yisrael Vehazmanim Vol. III page 1028, No. 50, a dip of slightly less than 13 should be used.
     * Calculations show that the proper dip to be 13.2456 (truncated to 13.24 that provides about 1.5 second
     * earlier (*lechumra*) time) below the horizon at that time. This makes a difference of 1 minute and 10
     * seconds in Jerusalem during the Equinox, and 1 minute 29 seconds during the solstice as compared to the proper
     * 13.24 versus 13. For NY during the solstice, the difference is 1 minute 56 seconds.
     * @todo recalculate the above based on equilux/equinox calculations.
     *
     * @return the `Date` of the sun being 13.24 below [geometric zenith][.GEOMETRIC_ZENITH]
     * (90). If the calculation can't be computed such as northern and southern locations even south of the
     * Arctic Circle and north of the Antarctic Circle where the sun may not reach low enough below the horizon
     * for this calculation, a null will be returned. See detailed explanation on top of the
     * [AstronomicalCalendar] documentation.
     *
     * @see .ZENITH_13_POINT_24
     *
     * @see .getBainHashmashosRT58Point5Minutes
     */
    val bainHashmashosRT13Point24Degrees: Instant?
        get() = getSunsetOffsetByDegrees(ZENITH_13_POINT_24)

    /**
     * This method returns the beginning of *Bain hashmashos* of Rabbeinu Tam calculated as a 58.5
     * minute offset after sunset. *bain hashmashos* is 3/4 of a *Mil* before *tzais* or 3 1/4
     * *Mil* after sunset. With a *Mil* calculated as 18 minutes, 3.25 * 18 = 58.5 minutes.
     *
     * @return the `Date` of 58.5 minutes after sunset. If the calculation can't be computed such as in the
     * Arctic Circle where there is at least one day a year where the sun does not rise, and one where it does
     * not set, a null will be returned. See detailed explanation on top of the [AstronomicalCalendar]
     * documentation.
     */
    val bainHashmashosRT58Point5Minutes: Instant?
        get() = getTimeOffset(
            elevationAdjustedSunset,
            58.5 * MINUTE_MILLIS
        )

    /**
     * This method returns the beginning of *bain hashmashos* based on the calculation of 13.5 minutes (3/4 of an
     * 18-minute *Mil*) before *shkiah* calculated as [7.083&amp;deg;][.getTzaisGeonim7Point083Degrees].
     *
     * @return the `Date` of the *bain hashmashos* of Rabbeinu Tam in this calculation. If the
     * calculation can't be computed such as northern and southern locations even south of the Arctic Circle and
     * north of the Antarctic Circle where the sun may not reach low enough below the horizon for this
     * calculation, a null will be returned. See detailed explanation on top of the [AstronomicalCalendar]
     * documentation.
     * @see .getTzaisGeonim7Point083Degrees
     */
    val bainHashmashosRT13Point5MinutesBefore7Point083Degrees: Instant?
        get() = getTimeOffset(
            getSunsetOffsetByDegrees(ZENITH_7_POINT_083),
            -13.5 * MINUTE_MILLIS
        )

    /**
     * This method returns the beginning of *bain hashmashos* of Rabbeinu Tam calculated according to the
     * opinion of the *Divrei Yosef* (see Yisrael Vehazmanim) calculated 5/18th (27.77%) of the time between
     * *alos* (calculated as 19.8 before sunrise) and sunrise. This is added to sunset to arrive at the time
     * for *bain hashmashos* of Rabbeinu Tam.
     *
     * @return the `Date` of *bain hashmashos* of Rabbeinu Tam for this calculation. If the
     * calculation can't be computed such as northern and southern locations even south of the Arctic Circle and
     * north of the Antarctic Circle where the sun may not reach low enough below the horizon for this
     * calculation, a null will be returned. See detailed explanation on top of the [AstronomicalCalendar]
     * documentation.
     */
    val bainHashmashosRT2Stars: Instant?
        get() = alos19Point8Degrees?.let { alos19Point8 ->
            elevationAdjustedSunrise?.let { sunrise ->
                getTimeOffset(
                    elevationAdjustedSunset,
                    ((sunrise - alos19Point8) * (5 / 18.0)).inWholeMilliseconds
                )
            }
        }

    /**
     * This method returns the beginning of *bain hashmashos* (twilight) according to the [Yereim (Rabbi Eliezer of Metz)](https://en.wikipedia.org/wiki/Eliezer_ben_Samuel) calculated as 18 minutes
     * or 3/4 of a 24-minute *Mil* before sunset. According to the Yereim, *bain hashmashos* starts 3/4
     * of a *Mil* before sunset and *tzais* or nightfall starts at sunset.
     *
     * @return the `Date` of 18 minutes before sunset. If the calculation can't be computed such as in the
     * Arctic Circle where there is at least one day a year where the sun does not rise, and one where it does
     * not set, a null will be returned. See detailed explanation on top of the [AstronomicalCalendar]
     * documentation.
     * @see .getBainHashmashosYereim3Point05Degrees
     */
    val bainHashmashosYereim18Minutes: Instant?
        get() = getTimeOffset(
            elevationAdjustedSunset,
            -18 * MINUTE_MILLIS
        )

    /**
     * This method returns the beginning of *bain hashmashos* (twilight) according to the [Yereim (Rabbi Eliezer of Metz)](https://en.wikipedia.org/wiki/Eliezer_ben_Samuel) calculated as the sun's
     * position 3.05 above the horizon [around the equinox / equilux](https://kosherjava.com/2022/01/12/equinox-vs-equilux-zmanim-calculations/),
     * its position 18 minutes or 3/4 of an 24-minute *mil* before sunset. According to the Yereim, *bain
     * hashmashos* starts 3/4 of a *Mil* before sunset and *tzais* or nightfall starts at sunset.
     * Note that *lechumra* (of about 14 seconds) a refraction value of 0.5166 as opposed to the traditional
     * 0.566 is used. This is more inline with the actual refraction in *Eretz Yisrael* and is brought down
     * by [Rabbi
 * Yedidya Manet](http://beinenu.com/rabbis/%D7%94%D7%A8%D7%91-%D7%99%D7%93%D7%99%D7%93%D7%99%D7%94-%D7%9E%D7%A0%D7%AA) in his [Zmanei Halacha
 * Lema’aseh](https://www.nli.org.il/en/books/NNL_ALEPH002542826/NLI) (p. 11). That is the first source that I am aware of that calculates degree-based Yereim
     * *zmanim*. The 0.5166 refraction is also used by the [Luach Itim
 * Lebinah](https://zmanim.online/). Calculating the Yereim's *bain hashmashos* using 18-minute based degrees is also suggested
     * in the upcoming 8th edition of the zmanim Kehilchasam. For more details, see the article [The Yereim’s *Bein Hashmashos*](https://kosherjava.com/2020/12/07/the-yereims-bein-hashmashos/).
     *
     * @todo recalculate based on equinox/equilux
     * @return the `Date` of the sun's position 3.05 minutes before sunset. If the calculation can't
     * be computed such as in the Arctic Circle where there is at least one day a year where the sun does not
     * rise, and one where it does not set, a null will be returned. See detailed explanation on top of the
     * [AstronomicalCalendar] documentation.
     *
     * @see .ZENITH_MINUS_3_POINT_05
     *
     * @see .getBainHashmashosYereim18Minutes
     * @see .getBainHashmashosYereim2Point8Degrees
     * @see .getBainHashmashosYereim2Point1Degrees
     */
    val bainHashmashosYereim3Point05Degrees: Instant?
        get() = getSunsetOffsetByDegrees(ZENITH_MINUS_3_POINT_05)

    /**
     * This method returns the beginning of *bain hashmashos* (twilight) according to the [Yereim (Rabbi Eliezer of Metz)](https://en.wikipedia.org/wiki/Eliezer_ben_Samuel) calculated as 16.875
     * minutes or 3/4 of a 22.5-minute *Mil* before sunset. According to the Yereim, *bain hashmashos*
     * starts 3/4 of a *Mil* before sunset and *tzais* or nightfall starts at sunset.
     *
     * @return the `Date` of 16.875 minutes before sunset. If the calculation can't be computed such as in the
     * Arctic Circle where there is at least one day a year where the sun does not rise, and one where it does
     * not set, a null will be returned. See detailed explanation on top of the [AstronomicalCalendar]
     * documentation.
     *
     * @see .getBainHashmashosYereim2Point8Degrees
     */
    val bainHashmashosYereim16Point875Minutes: Instant?
        get() = getTimeOffset(
            elevationAdjustedSunset,
            -16.875 * MINUTE_MILLIS
        )

    /**
     * This method returns the beginning of *bain hashmashos* (twilight) according to the [Yereim (Rabbi Eliezer of Metz)](https://en.wikipedia.org/wiki/Eliezer_ben_Samuel) calculated as the sun's
     * position 2.8 above the horizon [around the equinox / equilux](https://kosherjava.com/2022/01/12/equinox-vs-equilux-zmanim-calculations/),
     * its position 16.875 minutes or 3/4 of an 18-minute *Mil* before sunset. According to the Yereim, *bain
     * hashmashos* starts 3/4 of a *Mil* before sunset and *tzais* or nightfall starts at sunset.
     * Details, including how the degrees were calculated can be seen in the documentation of
     * [.getBainHashmashosYereim3Point05Degrees].
     *
     * @return the `Date` of the sun's position 2.8 minutes before sunset. If the calculation can't
     * be computed such as in the Arctic Circle where there is at least one day a year where the sun does not
     * rise, and one where it does not set, a null will be returned. See detailed explanation on top of the
     * [AstronomicalCalendar] documentation.
     *
     * @see .ZENITH_MINUS_2_POINT_8
     *
     * @see .getBainHashmashosYereim16Point875Minutes
     * @see .getBainHashmashosYereim3Point05Degrees
     * @see .getBainHashmashosYereim2Point1Degrees
     */
    val bainHashmashosYereim2Point8Degrees: Instant?
        get() = getSunsetOffsetByDegrees(ZENITH_MINUS_2_POINT_8)

    /**
     * This method returns the beginning of *bain hashmashos* (twilight) according to the [Yereim (Rabbi Eliezer of Metz)](https://en.wikipedia.org/wiki/Eliezer_ben_Samuel) calculated as 13.5 minutes
     * or 3/4 of an 18-minute *Mil* before sunset. According to the Yereim, *bain hashmashos* starts 3/4 of
     * a *Mil* before sunset and *tzais* or nightfall starts at sunset.
     *
     * @return the `Date` of 13.5 minutes before sunset. If the calculation can't be computed such as in the
     * Arctic Circle where there is at least one day a year where the sun does not rise, and one where it does
     * not set, a null will be returned. See detailed explanation on top of the [AstronomicalCalendar]
     * documentation.
     *
     * @see .getBainHashmashosYereim2Point1Degrees
     */
    val bainHashmashosYereim13Point5Minutes: Instant?
        get() = getTimeOffset(
            elevationAdjustedSunset,
            -13.5 * MINUTE_MILLIS
        )

    /**
     * This method returns the beginning of *bain hashmashos* according to the [Yereim (Rabbi Eliezer of Metz)](https://en.wikipedia.org/wiki/Eliezer_ben_Samuel) calculated as the sun's
     * position 2.1 above the horizon [around the equinox / equilux](https://kosherjava.com/2022/01/12/equinox-vs-equilux-zmanim-calculations/) in
     * Yerushalayim, its position 13.5 minutes or 3/4 of an 18-minute *Mil* before sunset. According to the Yereim,
     * *bain hashmashos* starts 3/4 of a *mil* before sunset and *tzais* or nightfall starts at sunset.
     * Details, including how the degrees were calculated can be seen in the documentation of
     * [.getBainHashmashosYereim3Point05Degrees].
     *
     * @return the `Date` of the sun's position 2.1 minutes before sunset. If the calculation can't
     * be computed such as in the Arctic Circle where there is at least one day a year where the sun does not
     * rise, and one where it does not set, a null will be returned. See detailed explanation on top of the
     * [AstronomicalCalendar] documentation.
     *
     * @see .ZENITH_MINUS_2_POINT_1
     *
     * @see .getBainHashmashosYereim13Point5Minutes
     * @see .getBainHashmashosYereim2Point8Degrees
     * @see .getBainHashmashosYereim3Point05Degrees
     */
    val bainHashmashosYereim2Point1Degrees: Instant?
        get() = getSunsetOffsetByDegrees(ZENITH_MINUS_2_POINT_1)

    /**
     * This method returns the *tzais* (nightfall) based on the opinion of the *Geonim* calculated at the
     * sun's position at [3.7&amp;deg;][.ZENITH_3_POINT_7] below the western horizon.
     *
     * @return the `Date` representing the time when the sun is 3.7 below sea level.
     * @see .ZENITH_3_POINT_7
     */
    val tzaisGeonim3Point7Degrees: Instant?
        get() = getSunsetOffsetByDegrees(ZENITH_3_POINT_7)

    /**
     * This method returns the *tzais* (nightfall) based on the opinion of the *Geonim* calculated at the
     * sun's position at [3.8&amp;deg;][.ZENITH_3_POINT_8] below the western horizon.
     *
     * @return the `Date` representing the time when the sun is 3.8 below sea level.
     * @see .ZENITH_3_POINT_8
     */
    val tzaisGeonim3Point8Degrees: Instant?
        get() = getSunsetOffsetByDegrees(ZENITH_3_POINT_8)

    /**
     * This method returns the *tzais* (nightfall) based on the opinion of the *Geonim* calculated at the
     * sun's position at [5.95&amp;deg;][.ZENITH_5_POINT_95] below the western horizon.
     *
     * @return the `Date` representing the time when the sun is 5.95 below sea level. If the calculation
     * can't be computed such as northern and southern locations even south of the Arctic Circle and north of
     * the Antarctic Circle where the sun may not reach low enough below the horizon for this calculation, a
     * null will be returned. See detailed explanation on top of the [AstronomicalCalendar] documentation.
     * @see .ZENITH_5_POINT_95
     */
    val tzaisGeonim5Point95Degrees: Instant?
        get() = getSunsetOffsetByDegrees(ZENITH_5_POINT_95)

    /**
     * This method returns the *tzais* (nightfall) based on the opinion of the *Geonim* calculated as 3/4
     * of a [Mil](https://en.wikipedia.org/wiki/Biblical_and_Talmudic_units_of_measurement) based on an 18
     * minute Mil, or 13.5 minutes. It is the sun's position at [3.65&amp;deg;][.ZENITH_3_POINT_65] below the western
     * horizon. This is a very early *zman* and should not be relied on without Rabbinical guidance.
     *
     * @return the `Date` representing the time when the sun is 3.65 below sea level. If the calculation
     * can't be computed such as northern and southern locations even south of the Arctic Circle and north of
     * the Antarctic Circle where the sun may not reach low enough below the horizon for this calculation, a
     * null will be returned. See detailed explanation on top of the [AstronomicalCalendar] documentation.
     * @see .ZENITH_3_POINT_65
     */
    val tzaisGeonim3Point65Degrees: Instant?
        get() = getSunsetOffsetByDegrees(ZENITH_3_POINT_65)

    /**
     * This method returns the *tzais* (nightfall) based on the opinion of the *Geonim* calculated as 3/4
     * of a [Mil](https://en.wikipedia.org/wiki/Biblical_and_Talmudic_units_of_measurement) based on an 18
     * minute Mil, or 13.5 minutes. It is the sun's position at [3.676&amp;deg;][.ZENITH_3_POINT_676] below the western
     * horizon based on the calculations of Stanley Fishkind. This is a very early *zman* and should not be
     * relied on without Rabbinical guidance.
     *
     * @return the `Date` representing the time when the sun is 3.676 below sea level. If the
     * calculation can't be computed such as northern and southern locations even south of the Arctic Circle and
     * north of the Antarctic Circle where the sun may not reach low enough below the horizon for this
     * calculation, a null will be returned. See detailed explanation on top of the [AstronomicalCalendar]
     * documentation.
     * @see .ZENITH_3_POINT_676
     */
    val tzaisGeonim3Point676Degrees: Instant?
        get() = getSunsetOffsetByDegrees(ZENITH_3_POINT_676)

    /**
     * This method returns the *tzais* (nightfall) based on the opinion of the *Geonim* calculated as 3/4
     * of a *[mil](https://en.wikipedia.org/wiki/Biblical_and_Talmudic_units_of_measurement)* based
     * on a 24-minute Mil, or 18 minutes. It is the sun's position at [4.61&amp;deg;][.ZENITH_4_POINT_61] below the
     * western horizon. This is a very early *zman* and should not be relied on without Rabbinical guidance.
     *
     * @return the `Date` representing the time when the sun is 4.61 below sea level. If the calculation
     * can't be computed such as northern and southern locations even south of the Arctic Circle and north of
     * the Antarctic Circle where the sun may not reach low enough below the horizon for this calculation, a
     * null will be returned. See detailed explanation on top of the [AstronomicalCalendar] documentation.
     * @see .ZENITH_4_POINT_61
     */
    val tzaisGeonim4Point61Degrees: Instant?
        get() = getSunsetOffsetByDegrees(ZENITH_4_POINT_61)

    /**
     * This method returns the *tzais* (nightfall) based on the opinion of the *Geonim* calculated as 3/4
     * of a [Mil](https://en.wikipedia.org/wiki/Biblical_and_Talmudic_units_of_measurement), based on a 22.5
     * minute Mil, or 16 7/8 minutes. It is the sun's position at [4.37&amp;deg;][.ZENITH_4_POINT_37] below the western
     * horizon. This is a very early *zman* and should not be relied on without Rabbinical guidance.
     *
     * @return the `Date` representing the time when the sun is 4.37 below sea level. If the calculation
     * can't be computed such as northern and southern locations even south of the Arctic Circle and north of
     * the Antarctic Circle where the sun may not reach low enough below the horizon for this calculation, a
     * null will be returned. See detailed explanation on top of the [AstronomicalCalendar] documentation.
     * @see .ZENITH_4_POINT_37
     */
    val tzaisGeonim4Point37Degrees: Instant?
        get() = getSunsetOffsetByDegrees(ZENITH_4_POINT_37)

    /**
     * This method returns the *tzais* (nightfall) based on the opinion of the *Geonim* calculated as 3/4
     * of a 24-minute *[Mil](https://en.wikipedia.org/wiki/Biblical_and_Talmudic_units_of_measurement)*,
     * based on a *Mil* being 24 minutes, and is calculated as 18 + 2 + 4 for a total of 24 minutes. It is the
     * sun's position at [5.88&amp;deg;][.ZENITH_5_POINT_88] below the western horizon. This is a very early
     * *zman* and should not be relied on without Rabbinical guidance.
     *
     * @todo Additional detailed documentation needed.
     * @return the `Date` representing the time when the sun is 5.88 below sea level. If the calculation
     * can't be computed such as northern and southern locations even south of the Arctic Circle and north of
     * the Antarctic Circle where the sun may not reach low enough below the horizon for this calculation, a
     * null will be returned. See detailed explanation on top of the [AstronomicalCalendar] documentation.
     * @see .ZENITH_5_POINT_88
     */
    val tzaisGeonim5Point88Degrees: Instant?
        get() = getSunsetOffsetByDegrees(ZENITH_5_POINT_88)

    /**
     * This method returns the *tzais* (nightfall) based on the opinion of the *Geonim* calculated as 3/4
     * of a [Mil](https://en.wikipedia.org/wiki/Biblical_and_Talmudic_units_of_measurement) based on the
     * sun's position at [4.8&amp;deg;][.ZENITH_4_POINT_8] below the western horizon. This is based on Rabbi Leo Levi's
     * calculations. This is the This is a very early *zman* and should not be relied on without Rabbinical guidance.
     * @todo Additional documentation needed.
     *
     * @return the `Date` representing the time when the sun is 4.8 below sea level. If the calculation
     * can't be computed such as northern and southern locations even south of the Arctic Circle and north of
     * the Antarctic Circle where the sun may not reach low enough below the horizon for this calculation, a
     * null will be returned. See detailed explanation on top of the [AstronomicalCalendar] documentation.
     * @see .ZENITH_4_POINT_8
     */
    val tzaisGeonim4Point8Degrees: Instant?
        get() = getSunsetOffsetByDegrees(ZENITH_4_POINT_8)

    /**
     * This method returns the *tzais* (nightfall) based on the opinion of the *Geonim* as calculated by
     * [Rabbi Yechiel Michel Tucazinsky](https://en.wikipedia.org/wiki/Yechiel_Michel_Tucazinsky). It is
     * based on of the position of the sun no later than [31 minutes][.getTzaisGeonim6Point45Degrees] after sunset
     * in Jerusalem the height of the summer solstice and is 28 minutes after *shkiah* [around the equinox / equilux](https://kosherjava.com/2022/01/12/equinox-vs-equilux-zmanim-calculations/). This
     * computes to 6.45 below the western horizon.
     * @todo Additional documentation details needed.
     *
     * @return the `Date` representing the time when the sun is 6.45 below sea level. If the
     * calculation can't be computed such as northern and southern locations even south of the Arctic Circle and
     * north of the Antarctic Circle where the sun may not reach low enough below the horizon for this
     * calculation, a null will be returned. See detailed explanation on top of the [AstronomicalCalendar]
     * documentation.
     * @see .ZENITH_6_POINT_45
     */
    val tzaisGeonim6Point45Degrees: Instant?
        get() = getSunsetOffsetByDegrees(ZENITH_6_POINT_45)

    /**
     * This method returns the *tzais* (nightfall) based on the opinion of the *Geonim* calculated when the
     * sun's position [7.083&amp;deg; (or 7&amp;deg; 5\u2032][.ZENITH_7_POINT_083]) below the western horizon. This is often
     * referred to as 75' or 7 and 5 minutes. This calculation is based on the observation of 3 medium sized
     * stars by Dr. Baruch (Berthold) Cohn in his *luach* [Tabellen enthaltend die Zeitangaben für
 * den Beginn der Nacht und des Tages für die Breitengrade + 66 bis -38](https://sammlungen.ub.uni-frankfurt.de/freimann/content/titleinfo/983088) published in Strasbourg, France in 1899.
     * This calendar was very popular in Europe, and many other calendars based their time on it. [Rav Dovid Tzvi Hoffman](https://en.wikipedia.org/wiki/David_Zvi_Hoffmann) in his
     * [Sh"Ut Melamed Leho'il](https://hebrewbooks.org/1053) in an exchange of letters with Baruch Cohn in [Orach Chaim 30](https://hebrewbooks.org/pdfpager.aspx?req=1053&st=&pgnum=37) agreed to this *zman* (page 36),
     * as did the Sh"Ut Bnei Tziyon and the Tenuvas Sadeh. It is very close to the time of the [Mekor Chesed](https://hebrewbooks.org/22044) of the Sefer chasidim. It is close to the position of the sun 30 minutes
     * after sunset in Jerusalem [around the equinox / equilux](https://kosherjava.com/2022/01/12/equinox-vs-equilux-zmanim-calculations/), but not
     * Exactly. The actual position of the sun 30 minutes after sunset in Jerusalem at the equilux is 7.205 and 7.199
     * at the equinox. See Hazmanim Bahalacha vol 2, pages 520-521 for more details.
     *
     * @return the `Date` representing the time when the sun is 7.083 below sea level. If the
     * calculation can't be computed such as northern and southern locations even south of the Arctic Circle and
     * north of the Antarctic Circle where the sun may not reach low enough below the horizon for this
     * calculation, a null will be returned. See detailed explanation on top of the [AstronomicalCalendar]
     * documentation.
     * @see .ZENITH_7_POINT_083
     */
    val tzaisGeonim7Point083Degrees: Instant?
        get() = getSunsetOffsetByDegrees(ZENITH_7_POINT_083)

    /**
     * This method returns *tzais* (nightfall) based on the opinion of the *Geonim* calculated as 45 minutes
     * after sunset during the summer solstice in New York, when the *neshef* (twilight) is the longest. The sun's
     * position at this time computes to [7.75&amp;deg;][.ZENITH_7_POINT_67] below the western horizon. See [Igros Moshe Even Haezer 4, Ch. 4](https://hebrewbooks.org/pdfpager.aspx?req=921&pgnum=149) (regarding
     * *tzais* for *krias Shema*). It is also mentioned in Rabbi Heber's [Shaarei Zmanim](https://hebrewbooks.org/53000) on in
     * [chapter 10 (page 87)](https://hebrewbooks.org/pdfpager.aspx?req=53055&pgnum=101) and
     * [chapter 12 (page 108)](https://hebrewbooks.org/pdfpager.aspx?req=53055&pgnum=122). Also see the
     * time of 45 minutes in [Rabbi Simcha Bunim Cohen's](https://en.wikipedia.org/wiki/Simcha_Bunim_Cohen) [The radiance of Shabbos](https://www.worldcat.org/oclc/179728985) as the earliest *zman* for New York.
     * This *zman* is also listed in the [Divrei
 * Shalom Vol. III, chapter 75](https://hebrewbooks.org/pdfpager.aspx?req=1927&pgnum=90), and [Bais Av"i
 * Vol. III, chapter 117](https://hebrewbooks.org/pdfpager.aspx?req=892&pgnum=431). This *zman* is also listed in the Divrei Shalom etc. chapter 177 (FIXME - could not
     * be located). Since this *zman* depends on the level of light, Rabbi Yaakov Shakow presented this degree-based
     * calculation to Rabbi [Rabbi Shmuel Kamenetsky](https://en.wikipedia.org/wiki/Shmuel_Kamenetsky) who agreed
     * to it.
     * @todo add hyperlinks to source of Divrei Shalom.
     * @return the `Date` representing the time when the sun is 7.67 below sea level. If the
     * calculation can't be computed such as northern and southern locations even south of the Arctic Circle and
     * north of the Antarctic Circle where the sun may not reach low enough below the horizon for this
     * calculation, a null will be returned. See detailed explanation on top of the [AstronomicalCalendar]
     * documentation.
     * @see .ZENITH_7_POINT_67
     */
    val tzaisGeonim7Point67Degrees: Instant?
        get() = getSunsetOffsetByDegrees(ZENITH_7_POINT_67)

    /**
     * This method returns the *tzais* (nightfall) based on the opinion of the *Geonim* calculated at the
     * sun's position at [8.5&amp;deg;][.ZENITH_8_POINT_5] below the western horizon.
     *
     * @return the `Date` representing the time when the sun is 8.5 below sea level. If the calculation
     * can't be computed such as northern and southern locations even south of the Arctic Circle and north of
     * the Antarctic Circle where the sun may not reach low enough below the horizon for this calculation, a
     * null will be returned. See detailed explanation on top of the [AstronomicalCalendar] documentation.
     * @see .ZENITH_8_POINT_5
     */
    val tzaisGeonim8Point5Degrees: Instant?
        get() = getSunsetOffsetByDegrees(ZENITH_8_POINT_5)

    /**
     * This method returns the *tzais* (nightfall) based on the calculations used in the [Luach Itim Lebinah](https://www.worldcat.org/oclc/243303103) as the stringent time for *tzais*.  It is
     * calculated at the sun's position at [9.3&amp;deg;][.ZENITH_9_POINT_3] below the western horizon.
     *
     * @return the `Date` representing the time when the sun is 9.3 below sea level. If the calculation
     * can't be computed such as northern and southern locations even south of the Arctic Circle and north of
     * the Antarctic Circle where the sun may not reach low enough below the horizon for this calculation, a
     * null will be returned. See detailed explanation on top of the [AstronomicalCalendar] documentation.
     */
    val tzaisGeonim9Point3Degrees: Instant?
        get() = getSunsetOffsetByDegrees(ZENITH_9_POINT_3)

    /**
     * This method returns the *tzais* (nightfall) based on the opinion of the *Geonim* calculated as 60
     * minutes after sunset [around the equinox / equilux](https://kosherjava.com/2022/01/12/equinox-vs-equilux-zmanim-calculations/), the
     * day that a solar hour is 60 minutes in New York. The sun's position at this time computes to
     * [9.75&amp;deg;][.ZENITH_9_POINT_75] below the western horizon. This is the opinion of [Rabbi Eliyahu Henkin](https://en.wikipedia.org/wiki/Yosef_Eliyahu_Henkin).  This also follows the opinion of
     * [Rabbi Shmuel Kamenetsky](https://en.wikipedia.org/wiki/Shmuel_Kamenetsky). Rabbi Yaakov Shakow presented
     * these degree-based times to Rabbi Shmuel Kamenetsky who agreed to them.
     *
     * @todo recalculate based on equinox / equilux.
     * @return the `Date` representing the time when the sun is 9.75 below sea level. If the calculation
     * can't be computed such as northern and southern locations even south of the Arctic Circle and north of
     * the Antarctic Circle where the sun may not reach low enough below the horizon for this calculation, a
     * null will be returned. See detailed explanation on top of the [AstronomicalCalendar] documentation.
     *
     * @see .getTzais60
     */
    val tzaisGeonim9Point75Degrees: Instant?
        get() = getSunsetOffsetByDegrees(ZENITH_9_POINT_75)

    /**
     * This method returns the *tzais* (nightfall) based on the opinion of the [Chavas Yair](https://en.wikipedia.org/wiki/Yair_Bacharach) and [Divrei Malkiel](https://he.wikipedia.org/wiki/%D7%9E%D7%9C%D7%9B%D7%99%D7%90%D7%9C_%D7%A6%D7%91%D7%99_%D7%98%D7%A0%D7%A0%D7%91%D7%95%D7%99%D7%9D) that the time to walk the distance of a *Mil* is 15 minutes for a total of 60 minutes
     * for 4 *Mil* after [sea level sunset][.getSeaLevelSunset]. See detailed documentation explaining the
     * 60 minute concept at [.getAlos60].
     *
     * @return the `Date` representing 60 minutes after sea level sunset. If the calculation can't be
     * computed such as in the Arctic Circle where there is at least one day a year where the sun does not rise,
     * and one where it does not set, a null will be returned. See detailed explanation on top of the
     * [AstronomicalCalendar] documentation.
     *
     * @see .getAlos60
     * @see .getPlagHamincha60Minutes
     * @see .getShaahZmanis60Minutes
     */
    val tzais60: Instant?
        get() = getTimeOffset(
            elevationAdjustedSunset,
            60 * MINUTE_MILLIS
        )

    /**
     * This method returns *tzais* usually calculated as 40 minutes (configurable to any offset via
     * [.setAteretTorahSunsetOffset]) after sunset. Please note that *Chacham* Yosef Harari-Raful
     * of Yeshivat Ateret Torah who uses this time, does so only for calculating various other *zmanai hayom*
     * such as *Sof Zman Krias Shema* and *Plag Hamincha*. His calendars do not publish a *zman*
     * for *Tzais*. It should also be noted that *Chacham* Harari-Raful provided a 25 minute *zman*
     * for Israel. This API uses 40 minutes year round in any place on the globe by default. This offset can be change
     * by calling [.setAteretTorahSunsetOffset].
     *
     * @return the `Date` representing 40 minutes (configurable via [.setAteretTorahSunsetOffset])
     * after sea level sunset. If the calculation can't be computed such as in the Arctic Circle where there is
     * at least one day a year where the sun does not rise, and one where it does not set, a null will be
     * returned. See detailed explanation on top of the [AstronomicalCalendar] documentation.
     * @see .getAteretTorahSunsetOffset
     * @see .setAteretTorahSunsetOffset
     */
    val tzaisAteretTorah: Instant?
        get() = getTimeOffset(
            elevationAdjustedSunset,
            ateretTorahSunsetOffset * MINUTE_MILLIS
        )

    /**
     * This method returns the latest *zman krias shema* (time to recite Shema in the morning) based on the
     * calculation of *Chacham* Yosef Harari-Raful of Yeshivat Ateret Torah, that the day starts
     * [1/10th of the day][.getAlos72Zmanis] before sunrise and is usually calculated as ending
     * [40 minutes after sunset][.getTzaisAteretTorah] (configurable to any offset via
     * [.setAteretTorahSunsetOffset]). *shaos zmaniyos* are calculated based on this day and added
     * to [<em>alos</em>][.getAlos72Zmanis] to reach this time. This time is 3
     * [<em>shaos zmaniyos</em>][.getShaahZmanisAteretTorah] (temporal hours) after
     * [<em>alos</em> 72 <em>zmaniyos</em>][.getAlos72Zmanis]. **Note: ** Based on this calculation *chatzos*
     * will not be at midday.
     *
     * @return the `Date` of the latest *zman krias shema* based on this calculation. If the
     * calculation can't be computed such as in the Arctic Circle where there is at least one day a year where
     * the sun does not rise, and one where it does not set, a null will be returned. See detailed explanation
     * on top of the [AstronomicalCalendar] documentation.
     * @see .getAlos72Zmanis
     * @see .getTzaisAteretTorah
     * @see .getAteretTorahSunsetOffset
     * @see .setAteretTorahSunsetOffset
     * @see .getShaahZmanisAteretTorah
     */
    val sofZmanShmaAteretTorah: Instant? get() = getSofZmanShma(alos72Zmanis, tzaisAteretTorah)

    /**
     * This method returns the latest *zman tfila* (time to recite the morning prayers) based on the calculation
     * of *Chacham* Yosef Harari-Raful of Yeshivat Ateret Torah, that the day starts [ 1/10th of the day][.getAlos72Zmanis] before sunrise and is usually calculated as ending [40 minutes][.getTzaisAteretTorah] (configurable to any offset via [.setAteretTorahSunsetOffset]). *shaos zmaniyos*
     * are calculated based on this day and added to [<em>alos</em>][.getAlos72Zmanis] to reach this time. This time
     * is 4 * [<em>shaos zmaniyos</em>][.getShaahZmanisAteretTorah] (temporal hours) after
     * [<em>alos</em> 72 zmaniyos][.getAlos72Zmanis].
     * **Note: ** Based on this calculation *chatzos* will not be at midday.
     *
     * @return the `Date` of the latest *zman krias shema* based on this calculation. If the
     * calculation can't be computed such as in the Arctic Circle where there is at least one day a year where
     * the sun does not rise, and one where it does not set, a null will be returned. See detailed explanation
     * on top of the [AstronomicalCalendar] documentation.
     * @see .getAlos72Zmanis
     * @see .getTzaisAteretTorah
     * @see .getShaahZmanisAteretTorah
     * @see .setAteretTorahSunsetOffset
     */
    val sofZmanTfilahAteretTorah: Instant? get() = getSofZmanTfila(alos72Zmanis, tzaisAteretTorah)

    /**
     * This method returns the time of *mincha gedola* based on the calculation of *Chacham* Yosef
     * Harari-Raful of Yeshivat Ateret Torah, that the day starts [1/10th of the day][.getAlos72Zmanis]
     * before sunrise and is usually calculated as ending [40 minutes after sunset][.getTzaisAteretTorah]
     * (configurable to any offset via [.setAteretTorahSunsetOffset]). This is the preferred earliest
     * time to pray *mincha* according to the opinion of the [Rambam](https://en.wikipedia.org/wiki/Maimonides) and others. For more information on this see the documentation on [<em>mincha][.getMinchaGedola]. This is calculated as 6.5 [solar hours][.getShaahZmanisAteretTorah] after *alos*. The
     * calculation used is 6.5 * [.getShaahZmanisAteretTorah] after [<em>alos</em>][.getAlos72Zmanis].
     *
     * @see .getAlos72Zmanis
     * @see .getTzaisAteretTorah
     * @see .getShaahZmanisAteretTorah
     * @see .getMinchaGedola
     * @see .getMinchaKetanaAteretTorah
     * @see ZmanimCalendar.getMinchaGedola
     * @see .getAteretTorahSunsetOffset
     * @see .setAteretTorahSunsetOffset
     * @return the `Date` of the time of *mincha gedola*. If the calculation can't be computed such as
     * in the Arctic Circle where there is at least one day a year where the sun does not rise, and one where it
     * does not set, a null will be returned. See detailed explanation on top of the [AstronomicalCalendar]
     * documentation.
     */
    val minchaGedolaAteretTorah: Instant? get() = getMinchaGedola(alos72Zmanis, tzaisAteretTorah)

    /**
     * This method returns the time of *mincha ketana* based on the calculation of
     * *Chacham* Yosef Harari-Raful of Yeshivat Ateret Torah, that the day starts
     * [1/10th of the day][.getAlos72Zmanis] before sunrise and is usually calculated as ending
     * [40 minutes after sunset][.getTzaisAteretTorah] (configurable to any offset via
     * [.setAteretTorahSunsetOffset]). This is the preferred earliest time to pray *mincha*
     * according to the opinion of the [Rambam](https://en.wikipedia.org/wiki/Maimonides) and others.
     * For more information on this see the documentation on [<em>mincha gedola</em>][.getMinchaGedola]. This is
     * calculated as 9.5 [solar hours][.getShaahZmanisAteretTorah] after [<em>alos</em>][.getAlos72Zmanis].
     * The calculation used is 9.5 * [.getShaahZmanisAteretTorah] after [<em>alos</em>][.getAlos72Zmanis].
     *
     * @see .getAlos72Zmanis
     * @see .getTzaisAteretTorah
     * @see .getShaahZmanisAteretTorah
     * @see .getAteretTorahSunsetOffset
     * @see .setAteretTorahSunsetOffset
     * @see .getMinchaGedola
     * @see .getMinchaKetana
     * @return the `Date` of the time of *mincha ketana*. If the calculation can't be computed such as
     * in the Arctic Circle where there is at least one day a year where the sun does not rise, and one where it
     * does not set, a null will be returned. See detailed explanation on top of the [AstronomicalCalendar]
     * documentation.
     */
    val minchaKetanaAteretTorah: Instant? get() = getMinchaKetana(alos72Zmanis, tzaisAteretTorah)

    /**
     * This method returns the time of *plag hamincha* based on the calculation of *Chacham* Yosef Harari-Raful
     * of Yeshivat Ateret Torah, that the day starts [1/10th of the day][.getAlos72Zmanis] before sunrise and is
     * usually calculated as ending [40 minutes after sunset][.getTzaisAteretTorah] (configurable to any offset
     * via [.setAteretTorahSunsetOffset]). *shaos zmaniyos* are calculated based on this day and
     * added to [<em>alos</em>][.getAlos72Zmanis] to reach this time. This time is 10.75
     * [<em>shaos zmaniyos</em>][.getShaahZmanisAteretTorah] (temporal hours) after [ dawn][.getAlos72Zmanis].
     *
     * @return the `Date` of the *plag*. If the calculation can't be computed such as in the Arctic Circle
     * where there is at least one day a year where the sun does not rise, and one where it does not set, a null
     * will be returned. See detailed explanation on top of the [AstronomicalCalendar] documentation.
     * @see .getAlos72Zmanis
     * @see .getTzaisAteretTorah
     * @see .getShaahZmanisAteretTorah
     * @see .setAteretTorahSunsetOffset
     * @see .getAteretTorahSunsetOffset
     */
    val plagHaminchaAteretTorah: Instant? get() = getPlagHamincha(alos72Zmanis, tzaisAteretTorah)
    /**
     * This method returns the time of *misheyakir* based on the common calculation of the Syrian community in NY
     * that the *alos* is a fixed minute offset from day starting [1/10th of the day][.getAlos72Zmanis]
     * before sunrise. The common offsets are 6 minutes (based on the *Pri Megadim*, but not linked to the
     * calculation of *Alos* as 1/10th of the day), 8 and 18 minutes (possibly attributed to
     * [*Chacham* Baruch Ben Haim](https://en.wikipedia.org/wiki/Baruch_Ben_Haim)). Since there is no
     * universal accepted offset, the user of this API will have to specify one. *Chacham* Yosef Harari-Raful of
     * Yeshivat Ateret Torah does not supply any *zman* for *misheyakir* and does not endorse any specific
     * calculation for *misheyakir*. For that reason, this method is not a public method.
     *
     * @param minutes
     * the number of minutes after *alos* calculated as [1/10th of the day][.getAlos72Zmanis]
     * @return the `Date` of *misheyakir*. If the calculation can't be computed such as in the Arctic
     * Circle where there is at least one day a year where the sun does not rise, and one where it does not set,
     * a null will be returned. See detailed explanation on top of the [AstronomicalCalendar]
     * documentation.
     * @see .getAlos72Zmanis
     */
    // private Date getMesheyakirAteretTorah(double minutes) {
    // return getTimeOffset(getAlos72Zmanis(), minutes * MINUTE_MILLIS);
    // }
    /**
     * Method to return *tzais* (dusk) calculated as 72 minutes zmaniyos, or 1/10th of the day after
     * [sea level sunset][.getSeaLevelSunset]. This is the way that the [Minchas Cohen](https://en.wikipedia.org/wiki/Abraham_Cohen_Pimentel) in Ma'amar 2:4 calculates Rebbeinu Tam's
     * time of *tzeis*. It should be noted that this calculation results in the shortest time from sunset to
     * *tzais* being during the winter solstice, the longest at the summer solstice and 72 clock minutes at the
     * equinox. This does not match reality, since there is no direct relationship between the length of the day and
     * twilight. The shortest twilight is during the equinox, the longest is during the the summer solstice, and in the
     * winter with the shortest daylight, the twilight period is longer than during the equinoxes.
     *
     * @return the `Date` representing the time. If the calculation can't be computed such as in the Arctic
     * Circle where there is at least one day a year where the sun does not rise, and one where it does not set,
     * a null will be returned. See detailed explanation on top of the [AstronomicalCalendar]
     * documentation.
     * @see .getAlos72Zmanis
     */
    val tzais72Zmanis: Instant? get() = getZmanisBasedOffset(1.2)

    /**
     * A utility method to return *alos* (dawn) or *tzais* (dusk) based on a fractional day offset.
     * @param hours the number of *shaaos zmaniyos* (temporal hours) before sunrise or after sunset that defines dawn
     * or dusk. If a negative number is passed in, it will return the time of *alos* (dawn) (subtracting the
     * time from sunrise) and if a positive number is passed in, it will return the time of *tzais* (dusk)
     * (adding the time to sunset). If 0 is passed in, a null will be returned (since we can't tell if it is sunrise
     * or sunset based).
     * @return the `Date` representing the time. If the calculation can't be computed such as in the Arctic
     * Circle where there is at least one day a year where the sun does not rise, and one where it does not set,
     * a null will be returned. A null will also be returned if 0 is passed in, since we can't tell if it is sunrise
     * or sunset based. See detailed explanation on top of the [AstronomicalCalendar] documentation.
     */
    private fun getZmanisBasedOffset(hours: Double): Instant? {
        val shaahZmanis: Long = shaahZmanisGra
        if (shaahZmanis == Long.MIN_VALUE || hours == 0.0) {
            return null
        }
        return getTimeOffset(
            if (hours > 0) elevationAdjustedSunset else elevationAdjustedSunrise,
            (shaahZmanis * hours).toLong()
        )
    }

    /**
     * Method to return *tzais* (dusk) calculated using 90 minutes zmaniyos or 1/8th of the day after [seaLevelSunset]. This time is known in Yiddish as the *achtel* (an eighth)
     * *zman*.
     *
     * @return the `Date` representing the time. If the calculation can't be computed such as in the Arctic
     * Circle where there is at least one day a year where the sun does not rise, and one where it does not set,
     * a null will be returned. See detailed explanation on top of the [AstronomicalCalendar]
     * documentation.
     * @see .getAlos90Zmanis
     */
    val tzais90Zmanis: Instant?
        get() = getZmanisBasedOffset(1.5)

    /**
     * Method to return *tzais* (dusk) calculated using 96 minutes *zmaniyos* or 1/7.5 of the day after
     * [sea level sunset][.getSeaLevelSunset].
     *
     * @return the `Date` representing the time. If the calculation can't be computed such as in the Arctic
     * Circle where there is at least one day a year where the sun does not rise, and one where it does not set,
     * a null will be returned. See detailed explanation on top of the [AstronomicalCalendar]
     * documentation.
     * @see .getAlos96Zmanis
     */
    val tzais96Zmanis: Instant?
        get() = getZmanisBasedOffset(1.6)

    /**
     * Method to return *tzais* (dusk) calculated as 90 minutes after sea level sunset. This method returns
     * *tzais* (nightfall) based on the opinion of the Magen Avraham that the time to walk the distance of a
     * *Mil* according to the [Rambam](https://en.wikipedia.org/wiki/Maimonides)'s opinion
     * is 18 minutes for a total of 90 minutes based on the opinion of Ula who calculated *tzais* as 5
     * *Mil* after sea level *shkiah* (sunset). A similar calculation [.getTzais19Point8Degrees]
     * uses solar position calculations based on this time.
     *
     * @return the `Date` representing the time. If the calculation can't be computed such as in the Arctic
     * Circle where there is at least one day a year where the sun does not rise, and one where it does not set,
     * a null will be returned. See detailed explanation on top of the [AstronomicalCalendar]
     * documentation.
     * @see .getTzais19Point8Degrees
     * @see .getAlos90
     */
    val tzais90: Instant?
        get() = getTimeOffset(
            elevationAdjustedSunset,
            90 * MINUTE_MILLIS
        )

    /**
     * This method should be used *lechumra* only and returns *tzais* (nightfall) based on the calculations
     * of [Rav Chaim Naeh](https://en.wikipedia.org/wiki/Avraham_Chaim_Naeh) that the time to walk the
     * distance of a *Mil* according to the [Rambam](https://en.wikipedia.org/wiki/Maimonides)'s opinion
     * is 2/5 of an hour (24 minutes) for a total of 120 minutes based on the opinion of *Ula* who calculated
     * *tzais* as 5 *Mil* after sea level *shkiah* (sunset). A similar calculation [ ][.getTzais26Degrees] uses degree-based calculations based on this 120 minute calculation. Since the *zman*
     * is extremely late and at a point that is long past the 18 point where the darkest point is
     * reached, it should only be used *lechumra*, such as delaying the start of nighttime *mitzvos*.
     *
     * @return the `Date` representing the time. If the calculation can't be computed such as in the Arctic
     * Circle where there is at least one day a year where the sun does not rise, and one where it does not set,
     * a null will be returned. See detailed explanation on top of the [AstronomicalCalendar].
     * documentation.
     * @see .getTzais26Degrees
     * @see .getAlos120
     */
    @Deprecated("This method should be used <em>lechumra</em> only since it returns a very late time, and if used\n" + "	          <em>lekula</em> can result in <em>chillul Shabbos</em> etc. There is no current plan to remove this\n" + "	          method from the API, and this deprecation is intended to alert developers of the danger of using it.\n" + "	  \n" + "	  ")  // (forRemoval=false) // add back once Java 9 is the minimum supported version
    val tzais120: Instant?
        get() = getTimeOffset(
            elevationAdjustedSunset,
            120 * MINUTE_MILLIS
        )

    /**
     * This method should be used *lechumra* only and returns *tzais* (dusk) calculated using 120 minutes
     * *zmaniyos* after [sea level sunset][.getSeaLevelSunset]. Since the *zman*
     * is extremely late and at a point when the it is long past the 18 point where the darkest point is
     * reached, it should only be used *lechumra*, such as delaying the start of nighttime *mitzvos*.
     *
     * @return the `Date` representing the time. If the calculation can't be computed such as in the Arctic
     * Circle where there is at least one day a year where the sun does not rise, and one where it does not set,
     * a null will be returned. See detailed explanation on top of the [AstronomicalCalendar]
     * documentation.
     * @see .getAlos120Zmanis
     * @see .getTzais120
     * @see .getTzais26Degrees
     */
    @Deprecated("This method should be used <em>lechumra</em> only since it returns a very late time, and if used\n" + "	          <em>lekula</em> can result in <em>chillul Shabbos</em> etc. There is no current plan to remove this\n" + "	          method from the API, and this deprecation is intended to alert developers of the danger of using it.\n" + "	  \n" + "	  ")  // (forRemoval=false) // add back once Java 9 is the minimum supported version
    val tzais120Zmanis: Instant?
        get() = getZmanisBasedOffset(2.0)

    /**
     * This calculates the time of *tzais* at the point when the sun is 16.1 below the horizon. This is
     * the sun's dip below the horizon 72 minutes after sunset according Rabbeinu Tam's calculation of *tzais*
     * [around the equinox / equilux](https://kosherjava.com/2022/01/12/equinox-vs-equilux-zmanim-calculations/) in
     * Jerusalem. The question of equinox VS equilux is complex, with Rabbi Meir Posen in the [Ohr Meir](https://www.worldcat.org/oclc/956316270) of the opinion that the equilux should be used. See
     * Yisrael Vehazmanim vol I, 34:1:4. Rabbi Yedidya Manet in his [Zmanei Halacha Lema'aseh](https://www.nli.org.il/en/books/NNL_ALEPH002542826/NLI) (4th edition part 2, pages
     * and 22 and 24) and Rabbi Yonah Metzbuch (in a letter published by Rabbi Manet) are of the opinion that the
     * astronomical equinox should be used. The difference adds up to about 9 seconds, too trivial to make much of a
     * difference. For information on how this is calculated see the comments on [.getAlos16Point1Degrees].
     *
     * @return the `Date` representing the time. If the calculation can't be computed such as northern and
     * southern locations even south of the Arctic Circle and north of the Antarctic Circle where the sun may
     * not reach low enough below the horizon for this calculation, a null will be returned. See detailed
     * explanation on top of the [AstronomicalCalendar] documentation.
     * @see .getTzais72
     * @see .getAlos16Point1Degrees
     */
    val tzais16Point1Degrees: Instant?
        get() = getSunsetOffsetByDegrees(ZENITH_16_POINT_1)

    /**
     * This method should be used *lechumra* only and returns *tzais* based on when the sun is 26
     * below the horizon.For information on how this is calculated see the comments on [.getAlos26Degrees].
     * Since the *zman* is extremely late and at a point when it is long past the 18 point where the
     * darkest point is reached, it should only be used *lechumra* such as delaying the start of nighttime
     * *mitzvos*.
     *
     * @return the `Date` representing the time. If the calculation can't be computed such as northern and
     * southern locations even south of the Arctic Circle and north of the Antarctic Circle where the sun may
     * not reach low enough below the horizon for this calculation, a null will be returned. See detailed
     * explanation on top of the [AstronomicalCalendar] documentation.
     * @see .getTzais120
     * @see .getAlos26Degrees
     */
    @Deprecated("This method should be used <em>lechumra</em> only since it returns a very late time, and if used\n" + "	          <em>lekula</em> can result in <em>chillul Shabbos</em> etc. There is no current plan to remove this\n" + "	          method from the API, and this deprecation is intended to alert developers of the danger of using it.\n" + "	  \n" + "	  ")  // (forRemoval=false) // add back once Java 9 is the minimum supported version
    val tzais26Degrees: Instant?
        get() = getSunsetOffsetByDegrees(ZENITH_26_DEGREES)

    /**
     * For information on how this is calculated see the comments on [.getAlos18Degrees]
     *
     * @return the `Date` representing the time. If the calculation can't be computed such as northern and
     * southern locations even south of the Arctic Circle and north of the Antarctic Circle where the sun may
     * not reach low enough below the horizon for this calculation, a null will be returned. See detailed
     * explanation on top of the [AstronomicalCalendar] documentation.
     * @see .getAlos18Degrees
     */
    val tzais18Degrees: Instant?
        get() = getSunsetOffsetByDegrees(ASTRONOMICAL_ZENITH)

    /**
     * For information on how this is calculated see the comments on [.getAlos19Point8Degrees]
     *
     * @return the `Date` representing the time. If the calculation can't be computed such as northern and
     * southern locations even south of the Arctic Circle and north of the Antarctic Circle where the sun may
     * not reach low enough below the horizon for this calculation, a null will be returned. See detailed
     * explanation on top of the [AstronomicalCalendar] documentation.
     * @see .getTzais90
     * @see .getAlos19Point8Degrees
     */
    val tzais19Point8Degrees: Instant?
        get() = getSunsetOffsetByDegrees(ZENITH_19_POINT_8)

    /**
     * A method to return *tzais* (dusk) calculated as 96 minutes after sea level sunset. For information on how
     * this is calculated see the comments on [.getAlos96].
     *
     * @return the `Date` representing the time. If the calculation can't be computed such as in the Arctic
     * Circle where there is at least one day a year where the sun does not rise, and one where it does not set,
     * a null will be returned. See detailed explanation on top of the [AstronomicalCalendar]
     * documentation.
     * @see .getAlos96
     */
    val tzais96: Instant?
        get() = getTimeOffset(
            elevationAdjustedSunset,
            96 * MINUTE_MILLIS
        )

    /**
     * A method that returns the local time for fixed *chatzos*. This time is noon and midnight adjusted from
     * standard time to account for the local latitude. The 360 of the globe divided by 24 calculates to 15
     * per hour with 4 minutes per degree, so at a longitude of 0 , 15, 30 etc... *Chatzos* is at exactly 12:00
     * noon. This is the time of *chatzos* according to the [Aruch Hashulchan](https://en.wikipedia.org/wiki/Aruch_HaShulchan) in [Orach Chaim 233:14](https://hebrewbooks.org/pdfpager.aspx?req=7705&pgnum=426) and [Rabbi Moshe Feinstein](https://en.wikipedia.org/wiki/Moshe_Feinstein) in Igros Moshe [Orach Chaim 1:24](https://hebrewbooks.org/pdfpager.aspx?req=916&st=&pgnum=67) and [2:20](https://hebrewbooks.org/pdfpager.aspx?req=14675&pgnum=191).
     * Lakewood, N.J., with a longitude of -74.2094, is 0.7906 away from the closest multiple of 15 at -75. This
     * is multiplied by 4 to yield 3 minutes and 10 seconds for a *chatzos* of 11:56:50. This method is not tied
     * to the theoretical 15 timezones, but will adjust to the actual timezone and [Daylight saving time](https://en.wikipedia.org/wiki/Daylight_saving_time).
     *
     * @return the Date representing the local *chatzos*, or null if [geoLocation].[timeZone][GeoLocation.timeZone] is null
     * @see GeoLocation.localMeanTimeOffset
     */
    val fixedLocalChatzos: Instant?
        get() = getTimeOffset(
            getDateFromTime(
                12.0 - geoLocation.timeZone.rawOffset
                        / HOUR_MILLIS.toDouble(), true
            ), -geoLocation.localMeanTimeOffset
        )

    /**
     * Returns the latest time of *Kidush Levana* according to the [Maharil's](https://en.wikipedia.org/wiki/Yaakov_ben_Moshe_Levi_Moelin) opinion that it is calculated as
     * halfway between *molad* and *molad*. This adds half the 29 days, 12 hours and 793 chalakim time
     * between *molad* and *molad* (14 days, 18 hours, 22 minutes and 666 milliseconds) to the month's *molad*.
     * If the time of *sof zman Kiddush Levana* occurs during the day (between the *alos* and *tzais* passed in
     * as parameters), it returns the *alos* passed in. If a null *alos* or *tzais* are passed to this method,
     * the non-daytime adjusted time will be returned.
     *
     * @param alos
     * the beginning of the Jewish day. If *Kidush Levana* occurs during the day (starting at *alos* and
     * ending at *tzais*), the time returned will be *alos*. If either the *alos* or *tzais*
     * parameters are null, no daytime adjustment will be made.
     * @param tzais
     * the end of the Jewish day. If Kidush Levana occurs during the day (starting at *alos* and ending at
     * *tzais*), the time returned will be *alos*. If either the *alos* or *tzais* parameter
     * are null, no daytime adjustment will be made.
     * @return null if the [jewish day of the month][JewishCalendar.jewishDayOfMonth] is < 11 or > 16. Otherwise, the Date representing the moment halfway between molad and molad. If the time occurs between
     * *alos* and *tzais*, *alos* will be returned.
     * @see .getSofZmanKidushLevanaBetweenMoldos
     * @see .getSofZmanKidushLevana15Days
     * @see JewishCalendar.getSofZmanKidushLevanaBetweenMoldos
     */
    fun getSofZmanKidushLevanaBetweenMoldos(alos: Instant?, tzais: Instant?): Instant? {
        val jewishCalendar = JewishCalendar(localDateTime.date)

        // Do not calculate for impossible dates, but account for extreme cases. In the extreme case of Rapa Iti in French
        // Polynesia on Dec 2027 when kiddush Levana 3 days can be said on <em>Rosh Chodesh</em>, the sof zman Kiddush Levana
        // will be on the 12th of the Teves. In the case of Anadyr, Russia on Jan, 2071, sof zman Kiddush Levana between the
        // moldos will occur is on the night of 17th of Shevat. See Rabbi Dovid Heber's Shaarei Zmanim chapter 4 (pages 28 and 32).
        if (jewishCalendar.jewishDayOfMonth < 11 || jewishCalendar.jewishDayOfMonth > 16) {
            return null
        }
        return getMoladBasedTime(
            jewishCalendar.sofZmanKidushLevanaBetweenMoldos,
            alos,
            tzais,
            false
        )
    }

    /**
     * Returns the Date of the *molad* based time if it occurs on the current date. Since *Kiddush Levana*
     * can only be said during the day, there are parameters to limit it to between *alos* and *tzais*. If
     * the time occurs between *alos* and *tzais*, *tzais* will be returned.
     *
     * @param moladBasedTime
     * the *molad* based time such as *molad*, *tchilas* and *sof zman Kiddush Levana*
     * @param alos
     * optional start of day to limit *molad* times to the end of the night before or beginning of the next night.
     * Ignored if either *alos* or *tzais* are null.
     * @param tzais
     * optional end of day to limit *molad* times to the end of the night before or beginning of the next night.
     * Ignored if either *tzais* or *alos* are null
     * @param techila
     * is it the start of *Kiddush Levana* time or the end? If it is start roll it to the next *tzais*, and
     * and if it is the end, return the end of the previous night (*alos* passed in). Ignored if either
     * *alos* or *tzais* are null.
     * @return the *molad* based time. If the *zman* does not occur during the current date, null will be returned.
     */
    private fun getMoladBasedTime(
        moladBasedTime: Instant,
        alos: Instant?,
        tzais: Instant?,
        techila: Boolean,
    ): Instant? {
        val tz = geoLocation.timeZone
        return if (!(moladBasedTime < midnightLastNight.toInstant(tz) || moladBasedTime > midnightTonight.toInstant(tz)))
            if (alos != null || tzais != null)
                if (techila && !(tzais != null && moladBasedTime < tzais || alos != null && moladBasedTime > alos)) tzais
                else alos
            else moladBasedTime
        else null
    }

    /**
     * Returns the latest time of Kiddush Levana according to the [Maharil's](https://en.wikipedia.org/wiki/Yaakov_ben_Moshe_Levi_Moelin) opinion that it is calculated as
     * halfway between *molad* and *molad*. This adds half the 29 days, 12 hours and 793 chalakim time between
     * *molad* and *molad* (14 days, 18 hours, 22 minutes and 666 milliseconds) to the month's *molad*.
     * The *sof zman Kiddush Levana* will be returned even if it occurs during the day. To limit the time to between
     * *tzais* and *alos*, see [.getSofZmanKidushLevanaBetweenMoldos].
     *
     * @return the Date representing the moment halfway between molad and molad. If the time occurs between
     * *alos* and *tzais*, *alos* will be returned
     * @see .getSofZmanKidushLevanaBetweenMoldos
     * @see .getSofZmanKidushLevana15Days
     * @see JewishCalendar.getSofZmanKidushLevanaBetweenMoldos
     */
    val sofZmanKidushLevanaBetweenMoldos: Instant?
        get() = getSofZmanKidushLevanaBetweenMoldos(null, null)

    /**
     * Returns the latest time of *Kiddush Levana* calculated as 15 days after the *molad*. This is the
     * opinion brought down in the Shulchan Aruch (Orach Chaim 426). It should be noted that some opinions hold that the
     * [Rema](https://en.wikipedia.org/wiki/Moses_Isserles) who brings down the opinion of the [Maharil's](https://en.wikipedia.org/wiki/Yaakov_ben_Moshe_Levi_Moelin) of calculating
     * [half way between <em>molad</em> and <em>molad</em>][.getSofZmanKidushLevanaBetweenMoldos] is of
     * the opinion that the Mechaber agrees to his opinion. Also see the Aruch Hashulchan. For additional details on the subject,
     * see Rabbi Dovid Heber's very detailed write-up in *Siman Daled* (chapter 4) of [Shaarei Zmanim](https://hebrewbooks.org/53000). If the time of *sof zman Kiddush Levana* occurs during
     * the day (between the *alos* and *tzais* passed in as parameters), it returns the *alos* passed in. If a
     * null *alos* or *tzais* are passed to this method, the non-daytime adjusted time will be returned.
     *
     * @param alos
     * the beginning of the Jewish day. If *Kidush Levana* occurs during the day (starting at *alos* and
     * ending at *tzais*), the time returned will be *alos*. If either the *alos* or *tzais*
     * parameters are null, no daytime adjustment will be made.
     * @param tzais
     * the end of the Jewish day. If *Kidush Levana* occurs during the day (starting at *alos* and ending at
     * *tzais*), the time returned will be *alos*. If either the *alos* or *tzais* parameters
     * are null, no daytime adjustment will be made.
     *
     * @return the Date representing the moment 15 days after the molad. If the time occurs between *alos* and
     * *tzais*, *alos* will be returned
     *
     * @see .getSofZmanKidushLevanaBetweenMoldos
     * @see JewishCalendar.getSofZmanKidushLevana15Days
     */
    fun getSofZmanKidushLevana15Days(alos: Instant?, tzais: Instant?): Instant? {
        val jewishCalendar = JewishCalendar(localDateTime.date)
        // Do not calculate for impossible dates, but account for extreme cases. In the extreme case of Rapa Iti in
        // French Polynesia on Dec 2027 when kiddush Levana 3 days can be said on <em>Rosh Chodesh</em>, the sof zman Kiddush
        // Levana will be on the 12th of the Teves. in the case of Anadyr, Russia on Jan, 2071, sof zman kiddush levana will
        // occur after midnight on the 17th of Shevat. See Rabbi Dovid Heber's Shaarei Zmanim chapter 4 (pages 28 and 32).
        if (jewishCalendar.jewishDayOfMonth < 11 || jewishCalendar.jewishDayOfMonth > 17) {
            return null
        }
        return getMoladBasedTime(jewishCalendar.sofZmanKidushLevana15Days, alos, tzais, false)
    }

    /**
     * Returns the latest time of *Kiddush Levana* calculated as 15 days after the molad. This is the opinion of
     * the Shulchan Aruch (Orach Chaim 426). It should be noted that some opinions hold that the
     * [Rema](https://en.wikipedia.org/wiki/Moses_Isserles) who brings down the opinion of the [Maharil's](https://en.wikipedia.org/wiki/Yaakov_ben_Moshe_Levi_Moelin) of calculating
     * [half way between <em>molad</em> and <em>molad</em>][.getSofZmanKidushLevanaBetweenMoldos] is of
     * the opinion that the Mechaber agrees to his opinion. Also see the Aruch Hashulchan. For additional details on the subject,
     * See Rabbi Dovid Heber's very detailed write-up in Siman Daled (chapter 4) of [Shaarei
 * Zmanim](https://hebrewbooks.org/53000). The *sof zman Kiddush Levana* will be returned even if it occurs during the day. To limit the time to
     * between *tzais* and *alos*, see [.getSofZmanKidushLevana15Days].
     *
     * @return the Date representing the moment 15 days after the *molad*. If the time occurs between
     * *alos* and *tzais*, *alos* will be returned
     *
     * @see getSofZmanKidushLevana15Days
     * @see getSofZmanKidushLevanaBetweenMoldos
     * @see JewishCalendar.sofZmanKidushLevana15Days
     */
    val sofZmanKidushLevana15Days: Instant?
        get() = getSofZmanKidushLevana15Days(null, null)

    /**
     * Returns the earliest time of *Kiddush Levana* according to [Rabbeinu Yonah](https://en.wikipedia.org/wiki/Yonah_Gerondi)'s opinion that it can be said 3 days after the
     * *molad*. The time will be returned even if it occurs during the day when *Kiddush Levana* can't be said.
     * Use [.getTchilasZmanKidushLevana3Days] if you want to limit the time to night hours.
     *
     * @return the Date representing the moment 3 days after the molad.
     * @see .getTchilasZmanKidushLevana3Days
     * @see .getTchilasZmanKidushLevana7Days
     * @see JewishCalendar.tchilasZmanKidushLevana3Days
     */
    val tchilasZmanKidushLevana3Days: Instant? get() = getTchilasZmanKidushLevana3Days(null, null)

    /**
     * Returns the earliest time of *Kiddush Levana* according to [Rabbeinu Yonah](https://en.wikipedia.org/wiki/Yonah_Gerondi)'s opinion that it can be said 3 days after the *molad*.
     * If the time of *tchilas zman Kiddush Levana* occurs during the day (between *alos* and *tzais* passed to
     * this method) it will return the following *tzais*. If null is passed for either *alos* or *tzais*, the actual
     * *tchilas zman Kiddush Levana* will be returned, regardless of if it is during the day or not.
     *
     * @param alos
     * the beginning of the Jewish day. If Kidush Levana occurs during the day (starting at *alos* and ending
     * at *tzais*), the time returned will be *tzais*. If either the *alos* or *tzais* parameters
     * are null, no daytime adjustment will be made.
     * @param tzais
     * the end of the Jewish day. If *Kidush Levana* occurs during the day (starting at *alos* and ending at
     * *tzais*), the time returned will be *tzais*. If either the *alos* or *tzais* parameters
     * are null, no daytime adjustment will be made.
     *
     * @return the Date representing the moment 3 days after the molad. If the time occurs between *alos* and
     * *tzais*, *tzais* will be returned
     * @see .getTchilasZmanKidushLevana3Days
     * @see .getTchilasZmanKidushLevana7Days
     * @see JewishCalendar.getTchilasZmanKidushLevana3Days
     */
    fun getTchilasZmanKidushLevana3Days(alos: Instant?, tzais: Instant?): Instant? {
        val jewishCalendar = JewishCalendar(localDateTime.date)
        // Do not calculate for impossible dates, but account for extreme cases. Tchilas zman kiddush Levana 3 days for
        // the extreme case of Rapa Iti in French Polynesia on Dec 2027 when kiddush Levana 3 days can be said on the evening
        // of the 30th, the second night of Rosh Chodesh. The 3rd day after the <em>molad</em> will be on the 4th of the month.
        // In the case of Anadyr, Russia on Jan, 2071, when sof zman kiddush levana is on the 17th of the month, the 3rd day
        // from the molad will be on the 5th day of Shevat. See Rabbi Dovid Heber's Shaarei Zmanim chapter 4 (pages 28 and 32).
        if (jewishCalendar.jewishDayOfMonth > 5 && jewishCalendar.jewishDayOfMonth < 30) {
            return null
        }
        var zman: Instant? =
            getMoladBasedTime(jewishCalendar.tchilasZmanKidushLevana3Days, alos, tzais, true)

        //Get the following month's zman kiddush Levana for the extreme case of Rapa Iti in French Polynesia on Dec 2027 when
        // kiddush Levana can be said on Rosh Chodesh (the evening of the 30th). See Rabbi Dovid Heber's Shaarei Zmanim chapter 4 (page 32)
        if (zman == null && jewishCalendar.jewishDayOfMonth === 30) {
            jewishCalendar.forward(DateTimeUnit.MONTH, 1)
            zman = getMoladBasedTime(jewishCalendar.tchilasZmanKidushLevana3Days, null, null, true)
        }
        return zman
    }

    /**
     * Returns the point in time of *Molad* as a `Date` Object. For the traditional day of week, hour,
     * minute and chalakim, [JewishCalendar.moladAsInstant] and the not yet completed
     * [com.kosherjava.zmanim.hebrewcalendar.HebrewDateFormatter] that will have formatting for this.
     *
     * @return the Date representing the moment of the molad. If the molad does not occur on this day, a null will be returned.
     *
     * @see .getTchilasZmanKidushLevana3Days
     * @see .getTchilasZmanKidushLevana7Days
     * @see JewishCalendar.moladAsInstant
     */
    val zmanMolad: Instant?
        get() {
            val jewishCalendar: JewishCalendar = JewishCalendar()
            jewishCalendar.setGregorianDate(
                localDateTime.year, localDateTime.monthNumber,
                localDateTime.dayOfMonth
            )

            // Optimize to not calculate for impossible dates, but account for extreme cases. The molad in the extreme case of Rapa
            // Iti in French Polynesia on Dec 2027 occurs on the night of the 27th of Kislev. In the case of Anadyr, Russia on
            // Jan 2071, the molad will be on the 2nd day of Shevat. See Rabbi Dovid Heber's Shaarei Zmanim chapter 4 (pages 28 and 32).
            if (jewishCalendar.jewishDayOfMonth > 2 && jewishCalendar.jewishDayOfMonth < 27) {
                return null
            }
            var molad: Instant? = getMoladBasedTime(jewishCalendar.moladAsInstant, null, null, true)

            // deal with molad that happens on the end of the previous month
            if (molad == null && jewishCalendar.jewishDayOfMonth > 26) {
                jewishCalendar.forward(DateTimeUnit.MONTH, 1)
                molad = getMoladBasedTime(jewishCalendar.moladAsInstant, null, null, true)
            }
            return molad
        }

    /**
     * Used by Molad based *zmanim* to determine if *zmanim* occur during the current day.
     * @see .getMoladBasedTime
     * @return previous midnight
     */
    private val midnightLastNight: LocalDateTime
        get() = LocalDateTime(localDateTime.date, MIDNIGHT)

    /**
     * Used by Molad based *zmanim* to determine if *zmanim* occur during the current day.
     * @see moladBasedTime
     * @return following midnight
     */
    private val midnightTonight: LocalDateTime
        get() = LocalDateTime(
            localDateTime.date + DatePeriod(days = 1)/*roll to tonight*/,
            MIDNIGHT
        )

    /**
     * Returns the earliest time of *Kiddush Levana* according to the opinions that it should not be said until 7
     * days after the *molad*. If the time of *tchilas zman Kiddush Levana* occurs during the day (between
     * [<em>alos</em>][ZmanimCalendar.getAlos72] and [<em>tzais</em>][ZmanimCalendar.getTzais72]) it
     * return the next *tzais*.
     *
     * @param alos
     * the beginning of the Jewish day. If *Kidush Levana* occurs during the day (starting at *alos*
     * and ending at *tzais*), the time returned will be *tzais*. If either the *alos* or
     * *tzais* parameters are null, no daytime adjustment will be made.
     * @param tzais
     * the end of the Jewish day. If *Kidush Levana* occurs during the day (starting at *alos* and
     * ending at *tzais*), the time returned will be *tzais*. If either the *alos* or
     * *tzais* parameters are null, no daytime adjustment will be made.
     *
     * @return the Date representing the moment 7 days after the molad. If the time occurs between *alos* and
     * *tzais*, *tzais* will be returned
     * @see .getTchilasZmanKidushLevana3Days
     * @see .getTchilasZmanKidushLevana7Days
     * @see JewishCalendar.getTchilasZmanKidushLevana7Days
     */
    fun getTchilasZmanKidushLevana7Days(alos: Instant?, tzais: Instant?): Instant? {
        val jewishCalendar = JewishCalendar(localDateTime.date)

        // Optimize to not calculate for impossible dates, but account for extreme cases. Tchilas zman kiddush Levana 7 days for
        // the extreme case of Rapa Iti in French Polynesia on Jan 2028 (when kiddush Levana 3 days can be said on the evening
        // of the 30th, the second night of Rosh Chodesh), the 7th day after the molad will be on the 4th of the month.
        // In the case of Anadyr, Russia on Jan, 2071, when sof zman kiddush levana is on the 17th of the month, the 7th day
        // from the molad will be on the 9th day of Shevat. See Rabbi Dovid Heber's Shaarei Zmanim chapter 4 (pages 28 and 32).
        if (jewishCalendar.jewishDayOfMonth < 4 || jewishCalendar.jewishDayOfMonth > 9) {
            return null
        }
        return getMoladBasedTime(jewishCalendar.tchilasZmanKidushLevana7Days, alos, tzais, true)
    }

    /**
     * Returns the earliest time of *Kiddush Levana* according to the opinions that it should not be said until 7
     * days after the *molad*. The time will be returned even if it occurs during the day when *Kiddush Levana*
     * can't be recited. Use [.getTchilasZmanKidushLevana7Days] if you want to limit the time to night hours.
     *
     * @return the Date representing the moment 7 days after the molad regardless of it is day or night.
     * @see .getTchilasZmanKidushLevana7Days
     * @see JewishCalendar.getTchilasZmanKidushLevana7Days
     * @see .getTchilasZmanKidushLevana3Days
     */
    val tchilasZmanKidushLevana7Days: Instant? get() = getTchilasZmanKidushLevana7Days(null, null)

    /**
     * This method returns the latest time one is allowed eating *chametz* on *Erev Pesach* according to
     * the opinion of the[GRA](https://en.wikipedia.org/wiki/Vilna_Gaon). This time is identical to the [sofZmanTfilaGRA] and is provided as a convenience method for those who are
     * unaware how this *zman* is calculated. This time is 4 hours into the day based on the opinion of the
     * [GRA](https://en.wikipedia.org/wiki/Vilna_Gaon) that the day is calculated from sunrise to sunset.
     * This returns the time 4 * [.getShaahZmanisGra] after [sea level sunrise][.getSeaLevelSunrise].
     *
     * @see ZmanimCalendar.getShaahZmanisGra
     * @see ZmanimCalendar.getSofZmanTfilaGRA
     * @return the `Date` one is allowed eating *chametz* on *Erev Pesach*. If the calculation
     * can't be computed such as in the Arctic Circle where there is at least one day a year where the sun does
     * not rise, and one where it does not set, a null will be returned. See detailed explanation on top of the
     * [AstronomicalCalendar] documentation.
     */
    val getSofZmanAchilasChametzGRA: Instant? get() = sofZmanTfilaGRA

    /**
     * This method returns the latest time one is allowed eating *chametz* on *Erev Pesach* according to the
     * opinion of the [Magen Avraham (MGA)](https://en.wikipedia.org/wiki/Avraham_Gombinern) based on *alos*
     * being [72][.getAlos72] minutes before [sunrise][.getSunrise]. This time is identical to the
     * [<em>Sof zman tfilah</em> MGA 72 minutes][.getSofZmanTfilaMGA72Minutes]. This time is 4 [ <em>shaos zmaniyos</em>][.getShaahZmanisMGA] (temporal hours) after [dawn][.getAlos72] based on the opinion of the MGA that the day is
     * calculated from a [dawn][.getAlos72] of 72 minutes before sunrise to [nightfall][.getTzais72] of 72 minutes
     * after sunset. This returns the time of 4 * [.getShaahZmanisMGA] after [dawn][.getAlos72].
     *
     * @return the `Date` of the latest time of eating *chametz*. If the calculation can't be computed such as
     * in the Arctic Circle where there is at least one day a year where the sun does not rise, and one where it
     * does not set), a null will be returned. See detailed explanation on top of the
     * [AstronomicalCalendar] documentation.
     * @see .getShaahZmanisMGA
     * @see .getAlos72
     * @see .getSofZmanTfilaMGA72Minutes
     */
    val sofZmanAchilasChametzMGA72Minutes: Instant? get() = sofZmanTfilaMGA72Minutes

    /**
     * This method returns the latest time one is allowed eating *chametz* on *Erev Pesach* according to the
     * opinion of the[Magen Avraham (MGA)](https://en.wikipedia.org/wiki/Avraham_Gombinern) based on *alos*
     * being [16.1&amp;deg;][.getAlos16Point1Degrees] before [sunrise][.getSunrise]. This time is 4 [ ][.getShaahZmanis16Point1Degrees] (solar hours) after [dawn][.getAlos16Point1Degrees]
     * based on the opinion of the MGA that the day is calculated from dawn to nightfall with both being 16.1
     * below sunrise or sunset. This returns the time of 4 [.getShaahZmanis16Point1Degrees] after
     * [dawn][.getAlos16Point1Degrees].
     *
     * @return the `Date` of the latest time of eating *chametz*. If the calculation can't be computed such
     * as northern and southern locations even south of the Arctic Circle and north of the Antarctic Circle where
     * the sun may not reach low enough below the horizon for this calculation, a null will be returned. See
     * detailed explanation on top of the [AstronomicalCalendar] documentation.
     *
     * @see .getShaahZmanis16Point1Degrees
     * @see .getAlos16Point1Degrees
     * @see .getSofZmanTfilaMGA16Point1Degrees
     */
    val sofZmanAchilasChametzMGA16Point1Degrees: Instant? get() = sofZmanTfilaMGA16Point1Degrees

    /**
     * This method returns the latest time for burning *chametz* on *Erev Pesach* according to the opinion
     * of the [GRA](https://en.wikipedia.org/wiki/Vilna_Gaon). This time is 5 hours into the day based on the
     * opinion of the [GRA](https://en.wikipedia.org/wiki/Vilna_Gaon) that the day is calculated from
     * sunrise to sunset. This returns the time 5 * [.getShaahZmanisGra] after [sea][.getSeaLevelSunrise].
     *
     * @see ZmanimCalendar.getShaahZmanisGra
     * @return the `Date` of the latest time for burning *chametz* on *Erev Pesach*. If the
     * calculation can't be computed such as in the Arctic Circle where there is at least one day a year where
     * the sun does not rise, and one where it does not set, a null will be returned. See detailed explanation on
     * top of the [AstronomicalCalendar] documentation.
     */
    val sofZmanBiurChametzGRA: Instant?
        get() = getTimeOffset(
            elevationAdjustedSunrise,
            shaahZmanisGra * 5
        )

    /**
     * This method returns the latest time for burning *chametz* on *Erev Pesach* according to the opinion of
     * the [Magen Avraham (MGA)](https://en.wikipedia.org/wiki/Avraham_Gombinern) based on *alos*
     * being [72][.getAlos72] minutes before [sunrise][.getSunrise]. This time is 5 [shaahZmanisMGA] (temporal hours) after [dawn][.getAlos72] based on the opinion of
     * the MGA that the day is calculated from a [dawn][.getAlos72] of 72 minutes before sunrise to [ ][.getTzais72] of 72 minutes after sunset. This returns the time of 5 * [.getShaahZmanisMGA] after
     * [dawn][.getAlos72].
     *
     * @return the `Date` of the latest time for burning *chametz* on *Erev Pesach*. If the calculation
     * can't be computed such as in the Arctic Circle where there is at least one day a year where the sun does not rise,
     * and one where it does not set), a null will be returned. See detailed explanation on top of the
     * [AstronomicalCalendar] documentation.
     * @see .getShaahZmanisMGA
     * @see .getAlos72
     */
    val sofZmanBiurChametzMGA72Minutes: Instant?
        get() = getTimeOffset(
            alos72,
            shaahZmanisMGA * 5
        )

    /**
     * This method returns the latest time for burning *chametz* on *Erev Pesach* according to the opinion
     * of the [Magen Avraham (MGA)](https://en.wikipedia.org/wiki/Avraham_Gombinern) based on *alos*
     * being [16.1&amp;deg;][.getAlos16Point1Degrees] before [sunrise][.getSunrise]. This time is 5
     * [<em>shaos zmaniyos</em>][.getShaahZmanis16Point1Degrees] (solar hours) after [ dawn][.getAlos16Point1Degrees] based on the opinion of the MGA that the day is calculated from dawn to nightfall with both being 16.1
     * below sunrise or sunset. This returns the time of 5 [.getShaahZmanis16Point1Degrees] after
     * [dawn][.getAlos16Point1Degrees].
     *
     * @return the `Date` of the latest time for burning *chametz* on *Erev Pesach*. If the calculation
     * can't be computed such as northern and southern locations even south of the Arctic Circle and north of the
     * Antarctic Circle where the sun may not reach low enough below the horizon for this calculation, a null
     * will be returned. See detailed explanation on top of the [AstronomicalCalendar] documentation.
     *
     * @see .getShaahZmanis16Point1Degrees
     * @see .getAlos16Point1Degrees
     */
    val sofZmanBiurChametzMGA16Point1Degrees: Instant?
        get() =
            getTimeOffset(alos16Point1Degrees, shaahZmanis16Point1Degrees * 5)

    /**
     * A method that returns "solar" midnight, or the time when the sun is at its [nadir](https://en.wikipedia.org/wiki/Nadir).
     * **Note:** this method is experimental and might be removed.
     *
     * @return the `Date` of Solar Midnight (chatzos layla). If the calculation can't be computed such as in
     * the Arctic Circle where there is at least one day a year where the sun does not rise, and one where it
     * does not set, a null will be returned. See detailed explanation on top of the
     * [AstronomicalCalendar] documentation.
     */
    val solarMidnight: Instant?
        get() {
            val clonedCal = ZmanimCalendar(geoLocation)
            val tz = geoLocation.timeZone
            clonedCal.localDateTime = this.localDateTime
                    .toInstant(tz)
                    .plus(DatePeriod(days = 1), tz)
                    .toLocalDateTime(tz)
            val tomorrowChatzos = clonedCal.chatzos ?: return null
            val thisChatzos = this.chatzos ?: return null
            return getTimeOffset(
                thisChatzos,
                (tomorrowChatzos - thisChatzos).div(2).inWholeMilliseconds
            )
        }

    /**
     * A method that returns the [Baal Hatanya](https://en.wikipedia.org/wiki/Shneur_Zalman_of_Liadi)'s
     * *netz amiti* (sunrise) without [ elevation adjustment][AstronomicalCalculator.getElevationAdjustment]. This forms the base for the Baal Hatanya's dawn-based calculations that are
     * calculated as a dip below the horizon before sunrise.
     *
     * According to the Baal Hatanya, *netz amiti*, or true (halachic) sunrise, is when the top of the sun's
     * disk is visible at an elevation similar to the mountains of Eretz Yisrael. The time is calculated as the point at which
     * the center of the sun's disk is 1.583 below the horizon. This degree-based calculation can be found in Rabbi Shalom
     * DovBer Levine's commentary on The [Baal
 * Hatanya's Seder Hachnasas Shabbos](https://www.chabadlibrary.org/books/pdf/Seder-Hachnosas-Shabbos.pdf). From an elevation of 546 meters, the top of [Har Hacarmel](https://en.wikipedia.org/wiki/Mount_Carmel), the sun disappears when it is 1 35' or 1.583
     * below the sea level horizon. This in turn is based on the Gemara [Shabbos 35a](https://hebrewbooks.org/shas.aspx?mesechta=2&daf=35). There are other opinions brought down by
     * Rabbi Levine, including Rabbi Yosef Yitzchok Feigelstock who calculates it as the degrees below the horizon 4 minutes after
     * sunset in Yerushalayim (on the equinox). That is brought down as 1.583. This is identical to the 1 35' *zman*
     * and is probably a typo and should be 1.683. These calculations are used by most [Chabad](https://en.wikipedia.org/wiki/Chabad) calendars that use the Baal Hatanya's *zmanim*. See
     * [About Our
 * *Zmanim* Calculations @ Chabad.org](https://www.chabad.org/library/article_cdo/aid/3209349/jewish/About-Our-Zmanim-Calculations.htm).
     *
     * Note: *netz amiti* is used only for calculating certain *zmanim*, and is intentionally unpublished. For
     * practical purposes, daytime *mitzvos* like *shofar* and *lulav* should not be done until after the
     * published time for *netz* / sunrise.
     *
     * @return the `Date` representing the exact sea-level *netz amiti* (sunrise) time. If the calculation can't be
     * computed such as in the Arctic Circle where there is at least one day a year where the sun does not rise, and one
     * where it does not set, a null will be returned. See detailed explanation on top of the page.
     *
     * @see .getSunrise
     * @see .getSeaLevelSunrise
     * @see .getSunsetBaalHatanya
     * @see .ZENITH_1_POINT_583
     */
    private val sunriseBaalHatanya: Instant? get() = getSunriseOffsetByDegrees(ZENITH_1_POINT_583)

    /**
     * A method that returns the [Baal Hatanya](https://en.wikipedia.org/wiki/Shneur_Zalman_of_Liadi)'s
     * *shkiah amiti* (sunset) without [ elevation adjustment][AstronomicalCalculator.getElevationAdjustment]. This forms the base for the Baal Hatanya's dusk-based calculations that are calculated
     * as a dip below the horizon after sunset.
     *
     * According to the Baal Hatanya, *shkiah amiti*, true (*halachic*) sunset, is when the top of the
     * sun's disk disappears from view at an elevation similar to the mountains of *Eretz Yisrael*.
     * This time is calculated as the point at which the center of the sun's disk is 1.583 degrees below the horizon.
     *
     * Note: *shkiah amiti* is used only for calculating certain *zmanim*, and is intentionally unpublished. For
     * practical purposes, all daytime mitzvos should be completed before the published time for *shkiah* / sunset.
     *
     * For further explanation of the calculations used for the Baal Hatanya's *zmanim* in this library, see
     * [About Our
 * *Zmanim* Calculations @ Chabad.org](https://www.chabad.org/library/article_cdo/aid/3209349/jewish/About-Our-Zmanim-Calculations.htm).
     *
     * @return the `Date` representing the exact sea-level *shkiah amiti* (sunset) time. If the calculation
     * can't be computed such as in the Arctic Circle where there is at least one day a year where the sun does not
     * rise, and one where it does not set, a null will be returned. See detailed explanation on top of the
     * [AstronomicalCalendar] documentation.
     *
     * @see sunset
     * @see seaLevelSunset
     * @see sunriseBaalHatanya
     * @see ZENITH_1_POINT_583
     */
    private val sunsetBaalHatanya: Instant?
        get() =
            getSunsetOffsetByDegrees(ZENITH_1_POINT_583)

    /**
     * A method that returns the [Baal Hatanya](https://en.wikipedia.org/wiki/Shneur_Zalman_of_Liadi)'s
     * a *shaah zmanis* ([temporal hour][.getTemporalHour]). This forms the base for the
     * Baal Hatanya's  day  based calculations that are calculated as a 1.583 dip below the horizon after sunset.
     * According to the Baal Hatanya, *shkiah amiti*, true (halachic) sunset, is when the top of the
     * sun's disk disappears from view at an elevation similar to the mountains of Eretz Yisrael.
     * This time is calculated as the point at which the center of the sun's disk is 1.583 degrees below the horizon.
     * A method that returns a *shaah zmanis* ([temporal hour][.getTemporalHour]) calculated
     * based on the [Baal Hatanya](https://en.wikipedia.org/wiki/Shneur_Zalman_of_Liadi)'s *netz
     * amiti* and *shkiah amiti* using a dip of 1.583 below the sea level horizon. This calculation divides
     * the day based on the opinion of the Baal Hatanya that the day runs from [netz amiti][.getSunriseBaalHatanya]
     * to [<em>shkiah amiti</em>][.getSunsetBaalHatanya]. The calculations are based on a day from [sunriseBaalHatanya] to [sea level <em>shkiah amiti</em>][.getSunsetBaalHatanya].
     * The day is split into 12 equal parts with each one being a *shaah zmanis*. This method is similar to [ ][.getTemporalHour], but all calculations are based on a sea level sunrise and sunset.
     * @return the `long` millisecond length of a *shaah zmanis* calculated from
     * [<em>netz amiti</em> (sunrise)][.getSunriseBaalHatanya] to [<em>shkiah amiti</em>][.getSunsetBaalHatanya]. If the calculation can't be computed such as in the Arctic Circle where there is at least one day a
     * year where the sun does not rise, and one where it does not set, [Long.MIN_VALUE] will be returned. See
     * detailed explanation on top of the [AstronomicalCalendar] documentation.
     *
     * @see .getTemporalHour
     * @see .getSunriseBaalHatanya
     * @see .getSunsetBaalHatanya
     * @see .ZENITH_1_POINT_583
     */
    val shaahZmanisBaalHatanya: Long get() = getTemporalHour(sunriseBaalHatanya, sunsetBaalHatanya)

    /**
     * Returns the [Baal Hatanya](https://en.wikipedia.org/wiki/Shneur_Zalman_of_Liadi)'s *alos*
     * (dawn) calculated as the time when the sun is 16.9 below the eastern [geometric horizon][.GEOMETRIC_ZENITH]
     * before [sunrise][.getSunrise]. For more information the source of 16.9 see [.ZENITH_16_POINT_9].
     *
     * @see .ZENITH_16_POINT_9
     *
     * @return The `Date` of dawn. If the calculation can't be computed such as northern and southern
     * locations even south of the Arctic Circle and north of the Antarctic Circle where the sun may not reach
     * low enough below the horizon for this calculation, a null will be returned. See detailed explanation on
     * top of the [AstronomicalCalendar] documentation.
     */
    val alosBaalHatanya: Instant? get() = getSunriseOffsetByDegrees(ZENITH_16_POINT_9)

    /**
     * This method returns the latest *zman krias shema* (time to recite Shema in the morning). This time is 3
     * [<em>shaos zmaniyos</em>][.getShaahZmanisBaalHatanya] (solar hours) after [ <em>netz amiti</em> (sunrise)][.getSunriseBaalHatanya] based on the opinion of the Baal Hatanya that the day is calculated from
     * sunrise to sunset. This returns the time 3 * [.getShaahZmanisBaalHatanya] after [ <em>netz amiti</em> (sunrise)][.getSunriseBaalHatanya].
     *
     * @see ZmanimCalendar.getSofZmanShma
     * @see .getShaahZmanisBaalHatanya
     * @return the `Date` of the latest *zman shema* according to the Baal Hatanya. If the calculation
     * can't be computed such as in the Arctic Circle where there is at least one day a year where the sun does
     * not rise, and one where it does not set, a null will be returned. See detailed explanation on top of the
     * [AstronomicalCalendar] documentation.
     */
    val sofZmanShmaBaalHatanya: Instant?
        get() = getSofZmanShma(
            sunriseBaalHatanya,
            sunsetBaalHatanya
        )

    /**
     * This method returns the latest *zman tfilah* (time to recite the morning prayers). This time is 4
     * hours into the day based on the opinion of the Baal Hatanya that the day is
     * calculated from sunrise to sunset. This returns the time 4 * [.getShaahZmanisBaalHatanya] after
     * [<em>netz amiti</em> (sunrise)][.getSunriseBaalHatanya].
     *
     * @see ZmanimCalendar.getSofZmanTfila
     * @see .getShaahZmanisBaalHatanya
     * @return the `Date` of the latest *zman tfilah*. If the calculation can't be computed such as in
     * the Arctic Circle where there is at least one day a year where the sun does not rise, and one where it does
     * not set, a null will be returned. See detailed explanation on top of the [AstronomicalCalendar]
     * documentation.
     */
    val sofZmanTfilaBaalHatanya: Instant?
        get() = getSofZmanTfila(
            sunriseBaalHatanya,
            sunsetBaalHatanya
        )

    /**
     * This method returns the latest time one is allowed eating *chametz* on *Erev Pesach* according to the
     * opinion of the Baal Hatanya. This time is identical to the [<em>Sof zman tfilah</em>][.getSofZmanTfilaBaalHatanya]. This time is 4 hours into the day based on the opinion of the Baal Hatanya that the day is calculated
     * from sunrise to sunset. This returns the time 4 [.getShaahZmanisBaalHatanya] after
     * [<em>netz amiti</em> (sunrise)][.getSunriseBaalHatanya].
     *
     * @see .getShaahZmanisBaalHatanya
     * @see .getSofZmanTfilaBaalHatanya
     * @return the `Date` one is allowed eating *chametz* on *Erev Pesach*. If the calculation can't
     * be computed such as in the Arctic Circle where there is at least one day a year where the sun does not rise,
     * and one where it does not set, a null will be returned. See detailed explanation on top of the
     * [AstronomicalCalendar] documentation.
     */
    val sofZmanAchilasChametzBaalHatanya: Instant? get() = sofZmanTfilaBaalHatanya

    /**
     * This method returns the latest time for burning *chametz* on *Erev Pesach* according to the opinion of
     * the Baal Hatanya. This time is 5 hours into the day based on the opinion of the Baal Hatanya that the day is calculated
     * from sunrise to sunset. This returns the time 5 * [.getShaahZmanisBaalHatanya] after
     * [<em>netz amiti</em> (sunrise)][.getSunriseBaalHatanya].
     *
     * @see .getShaahZmanisBaalHatanya
     * @return the `Date` of the latest time for burning *chametz* on *Erev Pesach*. If the calculation
     * can't be computed such as in the Arctic Circle where there is at least one day a year where the sun does not rise,
     * and one where it does not set, a null will be returned. See detailed explanation on top of the
     * [AstronomicalCalendar] documentation.
     */
    val sofZmanBiurChametzBaalHatanya: Instant?
        get() = getTimeOffset(
            sunriseBaalHatanya,
            shaahZmanisBaalHatanya * 5
        )

    /**
     * This method returns the time of *mincha gedola*. *Mincha gedola* is the earliest time one can pray
     * *mincha*. The [Rambam](https://en.wikipedia.org/wiki/Maimonides) is of the opinion that it is
     * better to delay *mincha* until [<em>mincha ketana</em>][.getMinchaKetanaBaalHatanya] while the
     * [Ra"sh](https://en.wikipedia.org/wiki/Asher_ben_Jehiel),
     * [Tur](https://en.wikipedia.org/wiki/Jacob_ben_Asher), [GRA](https://en.wikipedia.org/wiki/Vilna_Gaon) and others are of the opinion that *mincha* can be prayed
     * *lechatchila* starting at *mincha gedola*. This is calculated as 6.5 [ sea level solar hours][.getShaahZmanisBaalHatanya] after [<em>netz amiti</em> (sunrise)][.getSunriseBaalHatanya]. This calculation is based
     * on the opinion of the Baal Hatanya that the day is calculated from sunrise to sunset. This returns the time 6.5
     * * [.getShaahZmanisBaalHatanya] after [<em>netz amiti</em> (&quot;real&quot; sunrise)][.getSunriseBaalHatanya].
     *
     * @see .getMinchaGedola
     * @see .getShaahZmanisBaalHatanya
     * @see .getMinchaKetanaBaalHatanya
     * @return the `Date` of the time of *mincha gedola*. If the calculation can't be computed such as
     * in the Arctic Circle where there is at least one day a year where the sun does not rise, and one where it
     * does not set, a null will be returned. See detailed explanation on top of the [AstronomicalCalendar]
     * documentation.
     */
    val minchaGedolaBaalHatanya: Instant?
        get() = getMinchaGedola(sunriseBaalHatanya, sunsetBaalHatanya)

    /**
     * This is a convenience method that returns the later of [.getMinchaGedolaBaalHatanya] and
     * [.getMinchaGedola30Minutes]. In the winter when 1/2 of a [ <em>shaah zmanis</em>][.getShaahZmanisBaalHatanya] is less than 30 minutes [.getMinchaGedola30Minutes] will be returned, otherwise
     * [.getMinchaGedolaBaalHatanya] will be returned.
     *
     * @return the `Date` of the later of [.getMinchaGedolaBaalHatanya] and [.getMinchaGedola30Minutes].
     * If the calculation can't be computed such as in the Arctic Circle where there is at least one day a year
     * where the sun does not rise, and one where it does not set, a null will be returned. See detailed
     * explanation on top of the [AstronomicalCalendar] documentation.
     */
    val minchaGedolaBaalHatanyaGreaterThan30: Instant?
        get() = minchaGedola30Minutes?.let { mg30 ->
            minchaGedolaBaalHatanya?.let { mgBH ->
                maxOf(mg30, mgBH)
                //if (_minchaGedola30Minutes!! > _minchaGedolaBaalHatanya) _minchaGedola30Minutes else _minchaGedolaBaalHatanya
            }
        }

    /**
     * This method returns the time of *mincha ketana*. This is the preferred earliest time to pray
     * *mincha* in the opinion of the [Rambam](https://en.wikipedia.org/wiki/Maimonides) and others.
     * For more information on this see the documentation on [<em>mincha gedola</em>][.getMinchaGedolaBaalHatanya].
     * This is calculated as 9.5 [sea level solar hours][.getShaahZmanisBaalHatanya] after [ <em>netz amiti</em> (sunrise)][.getSunriseBaalHatanya]. This calculation is calculated based on the opinion of the Baal Hatanya that the
     * day is calculated from sunrise to sunset. This returns the time 9.5 * [.getShaahZmanisBaalHatanya] after [sunriseBaalHatanya].
     *
     * @see .getMinchaKetana
     * @see .getShaahZmanisBaalHatanya
     * @see .getMinchaGedolaBaalHatanya
     * @return the `Date` of the time of *mincha ketana*. If the calculation can't be computed such as
     * in the Arctic Circle where there is at least one day a year where the sun does not rise, and one where it
     * does not set, a null will be returned. See detailed explanation on top of the [AstronomicalCalendar]
     * documentation.
     */
    val minchaKetanaBaalHatanya: Instant?
        get() = getMinchaKetana(sunriseBaalHatanya, sunsetBaalHatanya)

    /**
     * This method returns the time of *plag hamincha*. This is calculated as 10.75 hours after sunrise. This
     * calculation is based on the opinion of the Baal Hatanya that the day is calculated
     * from sunrise to sunset. This returns the time 10.75 * [.getShaahZmanisBaalHatanya] after
     * [<em>netz amiti</em> (sunrise)][.getSunriseBaalHatanya].
     *
     * @see .getPlagHamincha
     * @return the `Date` of the time of *plag hamincha*. If the calculation can't be computed such as
     * in the Arctic Circle where there is at least one day a year where the sun does not rise, and one where it
     * does not set, a null will be returned. See detailed explanation on top of the
     * [AstronomicalCalendar] documentation.
     */
    val plagHaminchaBaalHatanya: Instant?
        get() = getPlagHamincha(sunriseBaalHatanya, sunsetBaalHatanya)

    /**
     * A method that returns *tzais* (nightfall) when the sun is 6 below the western geometric horizon
     * (90) after [sunset][.getSunset]. For information on the source of this calculation see
     * [.ZENITH_6_DEGREES].
     *
     * @return The `Date` of nightfall. If the calculation can't be computed such as northern and southern
     * locations even south of the Arctic Circle and north of the Antarctic Circle where the sun may not reach
     * low enough below the horizon for this calculation, a null will be returned. See detailed explanation on
     * top of the [AstronomicalCalendar] documentation.
     * @see .ZENITH_6_DEGREES
     */
    val tzaisBaalHatanya: Instant?
        get() = getSunsetOffsetByDegrees(ZENITH_6_DEGREES)

    /**
     * A utility methos to calculate zmanim based on [Rav Moshe
 * Feinstein](https://en.wikipedia.org/wiki/Moshe_Feinstein) as calculated in [MTJ](https://en.wikipedia.org/wiki/Mesivtha_Tifereth_Jerusalem), [Yeshiva of Staten Island](https://en.wikipedia.org/wiki/Mesivtha_Tifereth_Jerusalem), and Camp Yeshiva
     * of Staten Island. The day is split in two, from *alos* / sunrise to fixed local *chatzos*, and the
     * second half of the day, from fixed local *chatzos* to sunset / *tzais*. Morning based times are calculated
     * based on the first 6 hours, and afternoon times based on the second half of the day.
     *
     * @param startOfHalfDay
     * The start of the half day. This would be *alos* or sunrise for morning based times and fixed
     * local *chatzos* for the second half of the day.
     * @param endOfHalfDay
     * The end of the half day. This would be fixed local *chatzos* for morning based times and sunset
     * or *tzais* for afternoon based times.
     * @param hours
     * the number of hours to offset the beginning of the first or second half of the day
     *
     * @return the `Date` of the later of [.getMinchaGedolaBaalHatanya] and [.getMinchaGedola30Minutes].
     * If the calculation can't be computed such as in the Arctic Circle where there is at least one day a year
     * where the sun does not rise, and one where it does not set, a null will be returned. See detailed
     * explanation on top of the [AstronomicalCalendar] documentation.
     *
     * @see ComplexZmanimCalendar.getFixedLocalChatzos
     */
    fun getFixedLocalChatzosBasedZmanim(
        startOfHalfDay: Instant?,
        endOfHalfDay: Instant?,
        hours: Double,
    ): Instant? {
        if (startOfHalfDay == null || endOfHalfDay == null) {
            return null
        }
        val startInEpochMilli = startOfHalfDay.toEpochMilliseconds()
        val endMinusStart = endOfHalfDay.toEpochMilliseconds() - startInEpochMilli
        val shaahZmanis = endMinusStart.div(6)
        return Instant.fromEpochMilliseconds((startInEpochMilli + shaahZmanis * hours).toLong())
    }

    /**
     * This method returns [Rav Moshe Feinstein's](https://en.wikipedia.org/wiki/Moshe_Feinstein) opinion of the
     * calculation of *sof zman krias shema* (latest time to recite *Shema* in the morning) according to the
     * opinion of the [Magen Avraham (MGA)](https://en.wikipedia.org/wiki/Avraham_Gombinern) that the
     * day is calculated from dawn to nightfall, but calculated using the first half of the day only. The half a day starts
     * at *alos* defined as [18&amp;deg;][.getAlos18Degrees] and ends at [fixed local][.getFixedLocalChatzos]. *Sof Zman Shema* is 3 *shaos zmaniyos* (solar hours) after *alos* or half of this half-day.
     *
     * @return the `Date` of the latest *zman krias shema*. If the calculation can't be computed such
     * as northern and southern locations even south of the Arctic Circle and north of the Antarctic Circle
     * where the sun may not reach low enough below the horizon for this calculation, a null will be returned.
     * See detailed explanation on top of the [AstronomicalCalendar] documentation.
     * @see .getAlos18Degrees
     * @see .getFixedLocalChatzos
     * @see .getFixedLocalChatzosBasedZmanim
     */
    val sofZmanShmaMGA18DegreesToFixedLocalChatzos: Instant?
        get() =
            getFixedLocalChatzosBasedZmanim(alos18Degrees, fixedLocalChatzos, 3.0)

    /**
     * This method returns [Rav Moshe Feinstein's](https://en.wikipedia.org/wiki/Moshe_Feinstein) opinion of the
     * calculation of *sof zman krias shema* (latest time to recite *Shema* in the morning) according to the
     * opinion of the [Magen Avraham (MGA)](https://en.wikipedia.org/wiki/Avraham_Gombinern) that the
     * day is calculated from dawn to nightfall, but calculated using the first half of the day only. The half a day starts
     * at *alos* defined as [16.1&amp;deg;][.getAlos16Point1Degrees] and ends at [fixed local][.getFixedLocalChatzos]. *Sof Zman Shema* is 3 *shaos zmaniyos* (solar hours) after this *alos* or half of this half-day.
     *
     * @return the `Date` of the latest *zman krias shema*. If the calculation can't be computed such
     * as northern and southern locations even south of the Arctic Circle and north of the Antarctic Circle
     * where the sun may not reach low enough below the horizon for this calculation, a null will be returned.
     * See detailed explanation on top of the [AstronomicalCalendar] documentation.
     * @see .getAlos16Point1Degrees
     * @see .getFixedLocalChatzos
     * @see .getFixedLocalChatzosBasedZmanim
     */
    val sofZmanShmaMGA16Point1DegreesToFixedLocalChatzos: Instant?
        get() =
            getFixedLocalChatzosBasedZmanim(alos16Point1Degrees, fixedLocalChatzos, 3.0)

    /**
     * This method returns [Rav Moshe Feinstein's](https://en.wikipedia.org/wiki/Moshe_Feinstein) opinion of the
     * calculation of *sof zman krias shema* (latest time to recite *Shema* in the morning) according to the
     * opinion of the [Magen Avraham (MGA)](https://en.wikipedia.org/wiki/Avraham_Gombinern) that the
     * day is calculated from dawn to nightfall, but calculated using the first half of the day only. The half a day starts
     * at *alos* defined as [90 minutes before sunrise][.getAlos90] and ends at [ fixed local chatzos][.getFixedLocalChatzos]. *Sof Zman Shema* is 3 *shaos zmaniyos* (solar hours) after this *alos* or
     * half of this half-day.
     *
     * @return the `Date` of the latest *zman krias shema*. If the calculation can't be computed such
     * as northern and southern locations even south of the Arctic Circle and north of the Antarctic Circle
     * where the sun may not reach low enough below the horizon for this calculation, a null will be returned.
     * See detailed explanation on top of the [AstronomicalCalendar] documentation.
     * @see .getAlos90
     * @see .getFixedLocalChatzos
     * @see .getFixedLocalChatzosBasedZmanim
     */
    val sofZmanShmaMGA90MinutesToFixedLocalChatzos: Instant?
        get() =
            getFixedLocalChatzosBasedZmanim(alos90, fixedLocalChatzos, 3.0)

    /**
     * This method returns [Rav Moshe Feinstein's](https://en.wikipedia.org/wiki/Moshe_Feinstein) opinion of the
     * calculation of *sof zman krias shema* (latest time to recite *Shema* in the morning) according to the
     * opinion of the [Magen Avraham (MGA)](https://en.wikipedia.org/wiki/Avraham_Gombinern) that the
     * day is calculated from dawn to nightfall, but calculated using the first half of the day only. The half a day starts
     * at *alos* defined as [72 minutes before sunrise][.getAlos72] and ends at [ fixed local chatzos][.getFixedLocalChatzos]. *Sof Zman Shema* is 3 *shaos zmaniyos* (solar hours) after this *alos* or
     * half of this half-day.
     *
     * @return the `Date` of the latest *zman krias shema*. If the calculation can't be computed such
     * as northern and southern locations even south of the Arctic Circle and north of the Antarctic Circle
     * where the sun may not reach low enough below the horizon for this calculation, a null will be returned.
     * See detailed explanation on top of the [AstronomicalCalendar] documentation.
     * @see .getAlos72
     * @see .getFixedLocalChatzos
     * @see .getFixedLocalChatzosBasedZmanim
     */
    val sofZmanShmaMGA72MinutesToFixedLocalChatzos: Instant?
        get() =
            getFixedLocalChatzosBasedZmanim(alos72, fixedLocalChatzos, 3.0)

    /**
     * This method returns [Rav Moshe Feinstein's](https://en.wikipedia.org/wiki/Moshe_Feinstein) opinion of the
     * calculation of *sof zman krias shema* (latest time to recite *Shema* in the morning) according to the
     * opinion of the [GRA](https://en.wikipedia.org/wiki/Vilna_Gaon) that the day is calculated from
     * sunrise to sunset, but calculated using the first half of the day only. The half a day starts at [ sunrise][.getSunrise] and ends at [fixed local chatzos][.getFixedLocalChatzos]. *Sof zman Shema* is 3 *shaos
     * zmaniyos* (solar hours) after sunrise or half of this half-day.
     *
     * @return the `Date` of the latest *zman krias shema*. If the calculation can't be computed such
     * as northern and southern locations even south of the Arctic Circle and north of the Antarctic Circle
     * where the sun may not reach low enough below the horizon for this calculation, a null will be returned.
     * See detailed explanation on top of the [AstronomicalCalendar] documentation.
     * @see .getSunrise
     * @see .getFixedLocalChatzos
     * @see .getFixedLocalChatzosBasedZmanim
     */
    val sofZmanShmaGRASunriseToFixedLocalChatzos: Instant?
        get() =
            getFixedLocalChatzosBasedZmanim(sunrise, fixedLocalChatzos, 3.0)

    /**
     * This method returns [Rav Moshe Feinstein's](https://en.wikipedia.org/wiki/Moshe_Feinstein) opinion of the
     * calculation of *sof zman tfila* (*zman tfilah* (the latest time to recite the morning prayers))
     * according to the opinion of the [GRA](https://en.wikipedia.org/wiki/Vilna_Gaon) that the day is
     * calculated from sunrise to sunset, but calculated using the first half of the day only. The half a day starts at
     * [sunrise][.getSunrise] and ends at [fixed local chatzos][.getFixedLocalChatzos]. *Sof zman tefila*
     * is 4 *shaos zmaniyos* (solar hours) after sunrise or 2/3 of this half-day.
     *
     * @return the `Date` of the latest *zman krias shema*. If the calculation can't be computed such
     * as northern and southern locations even south of the Arctic Circle and north of the Antarctic Circle
     * where the sun may not reach low enough below the horizon for this calculation, a null will be returned.
     * See detailed explanation on top of the [AstronomicalCalendar] documentation.
     * @see .getSunrise
     * @see .getFixedLocalChatzos
     * @see .getFixedLocalChatzosBasedZmanim
     */
    val sofZmanTfilaGRASunriseToFixedLocalChatzos: Instant?
        get() =
            getFixedLocalChatzosBasedZmanim(sunrise, fixedLocalChatzos, 4.0)

    /**
     * This method returns [Rav Moshe Feinstein's](https://en.wikipedia.org/wiki/Moshe_Feinstein) opinion of
     * the calculation of *mincha gedola*, the earliest time one can pray *mincha* [GRA](https://en.wikipedia.org/wiki/Vilna_Gaon)that is 30 minutes after [fixed][.getFixedLocalChatzos].
     *
     * @return the `Date` of the time of *mincha gedola*. If the calculation can't be computed such as
     * in the Arctic Circle where there is at least one day a year where the sun does not rise, and one where it
     * does not set, a null will be returned. See detailed explanation on top of the [AstronomicalCalendar]
     * documentation.
     *
     * @see .getMinchaGedola
     * @see .getFixedLocalChatzos
     * @see .getMinchaKetanaGRAFixedLocalChatzosToSunset
     */
    val minchaGedolaGRAFixedLocalChatzos30Minutes: Instant?
        get() = getTimeOffset(
            fixedLocalChatzos,
            MINUTE_MILLIS * 30
        )

    /**
     * This method returns [Rav Moshe Feinstein's](https://en.wikipedia.org/wiki/Moshe_Feinstein) opinion
     * of the calculation of *mincha ketana* (the preferred time to recite the *mincha prayers* according to
     * the opinion of the [Rambam](https://en.wikipedia.org/wiki/Maimonides) and others) calculated according
     * to the [GRA](https://en.wikipedia.org/wiki/Vilna_Gaon) that is 3.5 *shaos zmaniyos* (solar
     * hours) after [fixed local chatzos][.getFixedLocalChatzos].
     *
     * @return the `Date` of the time of *mincha gedola*. If the calculation can't be computed such as
     * in the Arctic Circle where there is at least one day a year where the sun does not rise, and one where it
     * does not set, a null will be returned. See detailed explanation on top of the [AstronomicalCalendar]
     * documentation.
     *
     * @see .getMinchaGedola
     * @see .getFixedLocalChatzos
     * @see .getMinchaGedolaGRAFixedLocalChatzos30Minutes
     */
    val minchaKetanaGRAFixedLocalChatzosToSunset: Instant?
        get() =
            getFixedLocalChatzosBasedZmanim(fixedLocalChatzos, sunset, 3.5)

    /**
     * This method returns [Rav Moshe Feinstein's](https://en.wikipedia.org/wiki/Moshe_Feinstein) opinion
     * of the calculation of *plag hamincha*. This method returns *plag hamincha* calculated according to the
     * [GRA](https://en.wikipedia.org/wiki/Vilna_Gaon) that the day ends at sunset and is 4.75 *shaos
     * zmaniyos* (solar hours) after [fixed local chatzos][.getFixedLocalChatzos].
     *
     * @return the `Date` of the time of *mincha gedola*. If the calculation can't be computed such as
     * in the Arctic Circle where there is at least one day a year where the sun does not rise, and one where it
     * does not set, a null will be returned. See detailed explanation on top of the [AstronomicalCalendar]
     * documentation.
     *
     * @see .getPlagHamincha
     * @see .getFixedLocalChatzos
     * @see .getMinchaKetanaGRAFixedLocalChatzosToSunset
     *
     * @see .getMinchaGedolaGRAFixedLocalChatzos30Minutes
     */
    val plagHaminchaGRAFixedLocalChatzosToSunset: Instant?
        get() =
            getFixedLocalChatzosBasedZmanim(fixedLocalChatzos, sunset, 4.75)

    /**
     * Method to return *tzais* (dusk) calculated as 50 minutes after sea level sunset. This method returns
     * *tzais* (nightfall) based on the opinion of Rabbi Moshe Feinstein for the New York area. This time should
     * not be used for latitudes different than the NY area.
     *
     * @return the `Date` representing the time. If the calculation can't be computed such as in the Arctic
     * Circle where there is at least one day a year where the sun does not rise, and one where it does not set,
     * a null will be returned. See detailed explanation on top of the [AstronomicalCalendar]
     * documentation.
     */
    val tzais50: Instant?
        get() = getTimeOffset(
            elevationAdjustedSunset,
            50 * MINUTE_MILLIS
        )

    /**
     * A method for calculating *samuch lemincha ketana*, / near *mincha ketana* time that is half an hour before
     * [.getMinchaKetana] or is 9 * [<em>shaos zmaniyos</em>][.getShaahZmanisGra] (solar hours) after [ ][.getSunrise] or [sea level sunrise][.getSeaLevelSunrise] (depending on the [.isUseElevation]
     * setting), calculated according to the [GRA](https://en.wikipedia.org/wiki/Vilna_Gaon) using a day starting at
     * sunrise and ending at sunset. This is the time that eating or other activity can't begin prior to praying *mincha*.
     * The calculation used is 9 * [.getShaahZmanis16Point1Degrees] after [<em>alos</em>][.getAlos16Point1Degrees]. See the [Mechaber and Mishna Berurah
 * 232](https://hebrewbooks.org/pdfpager.aspx?req=60387&st=&pgnum=294) for details.
     *
     * @see .getShaahZmanisGra
     * @see .getSamuchLeMinchaKetana16Point1Degrees
     * @return the `Date` of the time of *samuch lemincha ketana*. If the calculation can't be computed such
     * as northern and southern locations even south of the Arctic Circle and north of the Antarctic Circle
     * where the sun may not reach low enough below the horizon for this calculation, a null will be returned.
     * See detailed explanation on top of the [AstronomicalCalendar] documentation.
     */
    val samuchLeMinchaKetanaGRA: Instant?
        get() =
            getSamuchLeMinchaKetana(elevationAdjustedSunrise, elevationAdjustedSunset)

    /**
     * A method for calculating *samuch lemincha ketana*, / near *mincha ketana* time that is half an hour before
     * [.getMinchaGedola16Point1Degrees]  or 9 * *shaos zmaniyos* (temporal hours) after the start of the day,
     * calculated using a day starting and ending 16.1 below the horizon. This is the time that eating or other activity
     * can't begin prior to praying *mincha*. The calculation used is 9 * [.getShaahZmanis16Point1Degrees] after
     * [<em>alos</em> 16.1&amp;deg;][.getAlos16Point1Degrees]. See the [Mechaber and Mishna Berurah 232](https://hebrewbooks.org/pdfpager.aspx?req=60387&st=&pgnum=294).
     *
     * @see .getShaahZmanis16Point1Degrees
     * @return the `Date` of the time of *samuch lemincha ketana*. If the calculation can't be computed such
     * as northern and southern locations even south of the Arctic Circle and north of the Antarctic Circle
     * where the sun may not reach low enough below the horizon for this calculation, a null will be returned.
     * See detailed explanation on top of the [AstronomicalCalendar] documentation.
     */
    val samuchLeMinchaKetana16Point1Degrees: Instant?
        get() =
            getSamuchLeMinchaKetana(alos16Point1Degrees, tzais16Point1Degrees)

    /**
     * A method for calculating *samuch lemincha ketana*, / near *mincha ketana* time that is half an hour before
     * [.getMinchaKetana72Minutes]  or 9 * *shaos zmaniyos* (temporal hours) after the start of the day,
     * calculated using a day starting 72 minutes before sunrise and ending 72 minutes after sunset. This is the time that eating
     * or other activity can't begin prior to praying *mincha*. The calculation used is 9 * [shaahZmanis16Point1Degrees] after [<em>alos</em> 16.1&amp;deg;][.getAlos16Point1Degrees]. See the [Mechaber and Mishna Berurah 232](https://hebrewbooks.org/pdfpager.aspx?req=60387&st=&pgnum=294).
     *
     * @see .getShaahZmanis16Point1Degrees
     * @return the `Date` of the time of *samuch lemincha ketana*. If the calculation can't be computed such
     * as northern and southern locations even south of the Arctic Circle and north of the Antarctic Circle
     * where the sun may not reach low enough below the horizon for this calculation, a null will be returned.
     * See detailed explanation on top of the [AstronomicalCalendar] documentation.
     */
    val samuchLeMinchaKetana72Minutes: Instant? get() = getSamuchLeMinchaKetana(alos72, tzais72)

    companion object {
        val MIDNIGHT = LocalTime(0, 0, 0)

        /**
         * The zenith of 3.7 below [geometric zenith][.GEOMETRIC_ZENITH] (90). This calculation is used for
         * calculating *tzais* (nightfall) based on the opinion of the *Geonim* that *tzais* is the
         * time it takes to walk 3/4 of a *Mil* at 18 minutes a *Mil*, or 13.5 minutes after sunset. The sun
         * is 3.7 below [geometric zenith][.GEOMETRIC_ZENITH] at this time in Jerusalem [around the equinox / equilux](https://kosherjava.com/2022/01/12/equinox-vs-equilux-zmanim-calculations/).
         *
         * @see .getTzaisGeonim3Point7Degrees
         */
        const val ZENITH_3_POINT_7: Double = GEOMETRIC_ZENITH + 3.7

        /**
         * The zenith of 3.8 below [geometric zenith][.GEOMETRIC_ZENITH] (90). This calculation is used for
         * calculating *tzais* (nightfall) based on the opinion of the *Geonim* that *tzais* is the
         * time it takes to walk 3/4 of a *Mil* at 18 minutes a *Mil*, or 13.5 minutes after sunset. The sun
         * is 3.8 below [geometric zenith][.GEOMETRIC_ZENITH] at this time in Jerusalem [around the equinox / equilux](https://kosherjava.com/2022/01/12/equinox-vs-equilux-zmanim-calculations/).
         *
         * @see .getTzaisGeonim3Point8Degrees
         */
        const val ZENITH_3_POINT_8: Double = GEOMETRIC_ZENITH + 3.8

        /**
         * The zenith of 5.95 below [geometric zenith][.GEOMETRIC_ZENITH] (90). This calculation is used for
         * calculating *tzais* (nightfall) according to some opinions. This calculation is based on the position of
         * the sun 24 minutes after sunset in Jerusalem [around the equinox / equilux](https://kosherjava.com/2022/01/12/equinox-vs-equilux-zmanim-calculations/),
         * which calculates to 5.95 below [geometric zenith][.GEOMETRIC_ZENITH].
         *
         * @see .getTzaisGeonim5Point95Degrees
         */
        const val ZENITH_5_POINT_95: Double = GEOMETRIC_ZENITH + 5.95

        /**
         * The zenith of 7.083 below [geometric zenith][.GEOMETRIC_ZENITH] (90). This is often referred to as
         * 75' or 7 and 5 minutes. This calculation is used for calculating *alos* (dawn) and
         * *tzais* (nightfall) according to some opinions. This calculation is based on observation of 3 medium sized
         * stars by Dr. Baruch Cohen in his calendar published in in 1899 in Strasbourg, France. This calculates to
         * 7.0833333 below [geometric zenith][.GEOMETRIC_ZENITH]. The [Sh"Ut
 * Melamed Leho'il](https://hebrewbooks.org/1053) in Orach Chaim 30 agreed to this *zman*, as did the Sh"Ut Bnei Tziyon, Tenuvas Sadeh and
         * it is very close to the time of the [Mekor Chesed](https://hebrewbooks.org/22044) of the Sefer chasidim.
         * It is close to the position of the sun 30 minutes after sunset in Jerusalem [around the equinox / equilux](https://kosherjava.com/2022/01/12/equinox-vs-equilux-zmanim-calculations/), but not
         * Exactly. The actual position of the sun 30 minutes after sunset in Jerusalem at the equilux is 7.205 and
         * 7.199 at the equinox. See Hazmanim Bahalacha vol 2, pages 520-521 for details.
         * @todo Hyperlink the proper sources.
         *
         * @see .getTzaisGeonim7Point083Degrees
         * @see .getBainHashmashosRT13Point5MinutesBefore7Point083Degrees
         */
        const val ZENITH_7_POINT_083: Double = GEOMETRIC_ZENITH + 7 + (5.0 / 60)

        /**
         * The zenith of 10.2 below [geometric zenith][.GEOMETRIC_ZENITH] (90). This calculation is used for
         * calculating *misheyakir* according to some opinions. This calculation is based on the position of the sun
         * 45 minutes before [sunrise][.getSunrise] in Jerusalem [around the equinox / equilux](https://kosherjava.com/2022/01/12/equinox-vs-equilux-zmanim-calculations/) which
         * calculates to 10.2 below [geometric zenith][.GEOMETRIC_ZENITH].
         *
         * @see .getMisheyakir10Point2Degrees
         */
        const val ZENITH_10_POINT_2: Double = GEOMETRIC_ZENITH + 10.2

        /**
         * The zenith of 11 below [geometric zenith][.GEOMETRIC_ZENITH] (90). This calculation is used for
         * calculating *misheyakir* according to some opinions. This calculation is based on the position of the sun
         * 48 minutes before [sunrise][.getSunrise] in Jerusalem [around the equinox / equilux](https://kosherjava.com/2022/01/12/equinox-vs-equilux-zmanim-calculations/), which
         * calculates to 11 below [geometric zenith][.GEOMETRIC_ZENITH].
         *
         * @see .getMisheyakir11Degrees
         */
        const val ZENITH_11_DEGREES: Double = GEOMETRIC_ZENITH + 11

        /**
         * The zenith of 11.5 below [geometric zenith][.GEOMETRIC_ZENITH] (90). This calculation is used for
         * calculating *misheyakir* according to some opinions. This calculation is based on the position of the sun
         * 52 minutes before [sunrise][.getSunrise] in Jerusalem [around the equinox / equilux](https://kosherjava.com/2022/01/12/equinox-vs-equilux-zmanim-calculations/), which
         * calculates to 11.5 below [geometric zenith][.GEOMETRIC_ZENITH].
         *
         * @see .getMisheyakir11Point5Degrees
         */
        const val ZENITH_11_POINT_5: Double = GEOMETRIC_ZENITH + 11.5

        /**
         * The zenith of 13.24 below [geometric zenith][.GEOMETRIC_ZENITH] (90). This calculation is used for
         * calculating Rabbeinu Tam's *bain hashmashos* according to some opinions.
         * NOTE: See comments on [.getBainHashmashosRT13Point24Degrees] for additional details about the degrees.
         *
         * @see .getBainHashmashosRT13Point24Degrees
         */
        const val ZENITH_13_POINT_24: Double = GEOMETRIC_ZENITH + 13.24

        /**
         * The zenith of 19 below [geometric zenith][.GEOMETRIC_ZENITH] (90). This calculation is used for
         * calculating *alos* according to some opinions.
         *
         * @see .getAlos19Degrees
         * @see .ZENITH_19_POINT_8
         */
        const val ZENITH_19_DEGREES: Double = GEOMETRIC_ZENITH + 19

        /**
         * The zenith of 19.8 below [geometric zenith][.GEOMETRIC_ZENITH] (90). This calculation is used for
         * calculating *alos* (dawn) and *tzais* (nightfall) according to some opinions. This calculation is
         * based on the position of the sun 90 minutes after sunset in Jerusalem [around the equinox / equilux](https://kosherjava.com/2022/01/12/equinox-vs-equilux-zmanim-calculations/) which
         * calculates to 19.8 below [geometric zenith][.GEOMETRIC_ZENITH].
         *
         * @see .getTzais19Point8Degrees
         * @see .getAlos19Point8Degrees
         * @see .getAlos90
         * @see .getTzais90
         * @see .ZENITH_19_DEGREES
         */
        const val ZENITH_19_POINT_8: Double = GEOMETRIC_ZENITH + 19.8

        /**
         * The zenith of 26 below [geometric zenith][.GEOMETRIC_ZENITH] (90). This calculation is used for
         * calculating *alos* (dawn) and *tzais* (nightfall) according to some opinions. This calculation is
         * based on the position of the sun [120 minutes][.getAlos120] after sunset in Jerusalem o[around the equinox / equilux](https://kosherjava.com/2022/01/12/equinox-vs-equilux-zmanim-calculations/) which
         * calculates to 26 below [geometric zenith][.GEOMETRIC_ZENITH]. Since the level of darkness when the sun is
         * 26 and at a point when the level of darkness is long past the 18 point where the darkest point is reached,
         * it should only be used *lechumra* such as delaying the start of nighttime *mitzvos* or avoiding eating
         * this early on a fast day.
         *
         * @see .getAlos26Degrees
         * @see .getTzais26Degrees
         * @see .getAlos120
         * @see .getTzais120
         */
        const val ZENITH_26_DEGREES: Double = GEOMETRIC_ZENITH + 26.0

        /**
         * The zenith of 4.37 below [geometric zenith][.GEOMETRIC_ZENITH] (90). This calculation is used for
         * calculating *tzais* (nightfall) according to some opinions. This calculation is based on the position of
         * the sun [16 7/8 minutes][.getTzaisGeonim4Point37Degrees] after sunset (3/4 of a 22.5-minute *Mil*)
         * in Jerusalem [around the equinox / equilux](https://kosherjava.com/2022/01/12/equinox-vs-equilux-zmanim-calculations/),
         * which calculates to 4.37 below [geometric zenith][.GEOMETRIC_ZENITH].
         *
         * @see .getTzaisGeonim4Point37Degrees
         */
        const val ZENITH_4_POINT_37: Double = GEOMETRIC_ZENITH + 4.37

        /**
         * The zenith of 4.61 below [geometric zenith][.GEOMETRIC_ZENITH] (90). This calculation is used for
         * calculating *tzais* (nightfall) according to some opinions. This calculation is based on the position of
         * the sun [18 minutes][.getTzaisGeonim4Point37Degrees] after sunset (3/4 of a 24-minute *Mil*) in
         * Jerusalem [around the equinox
 * / equilux](https://kosherjava.com/2022/01/12/equinox-vs-equilux-zmanim-calculations/), which calculates to 4.61 below [geometric zenith][.GEOMETRIC_ZENITH].
         * @todo add documentation links
         *
         * @see .getTzaisGeonim4Point61Degrees
         */
        const val ZENITH_4_POINT_61: Double = GEOMETRIC_ZENITH + 4.61

        /**
         * The zenith of 4.8 below [geometric zenith][.GEOMETRIC_ZENITH] (90).
         * @todo Add more documentation.
         * @see .getTzaisGeonim4Point8Degrees
         */
        const val ZENITH_4_POINT_8: Double = GEOMETRIC_ZENITH + 4.8

        /**
         * The zenith of 3.65 below [geometric zenith][.GEOMETRIC_ZENITH] (90). This calculation is used for
         * calculating *tzais* (nightfall) according to some opinions. This calculation is based on the position of
         * the sun [13.5 minutes][.getTzaisGeonim3Point65Degrees] after sunset (3/4 of an 18-minute *Mil*)
         * in Jerusalem [around the equinox / equilux](https://kosherjava.com/2022/01/12/equinox-vs-equilux-zmanim-calculations/) which
         * calculates to 3.65 below [geometric zenith][.GEOMETRIC_ZENITH].
         *
         * @see .getTzaisGeonim3Point65Degrees
         */
        const val ZENITH_3_POINT_65: Double = GEOMETRIC_ZENITH + 3.65

        /**
         * The zenith of 3.676 below [geometric zenith][.GEOMETRIC_ZENITH] (90).
         * @todo Add more documentation.
         */
        const val ZENITH_3_POINT_676: Double = GEOMETRIC_ZENITH + 3.676

        /**
         * The zenith of 5.88 below [geometric zenith][.GEOMETRIC_ZENITH] (90).
         * @todo Add more documentation.
         */
        const val ZENITH_5_POINT_88: Double = GEOMETRIC_ZENITH + 5.88

        /**
         * The zenith of 1.583 below [geometric zenith][.GEOMETRIC_ZENITH] (90). This calculation is used for
         * calculating *netz amiti* (sunrise) and *shkiah amiti* (sunset) based on the opinion of the
         * [Baal Hatanya](https://en.wikipedia.org/wiki/Shneur_Zalman_of_Liadi).
         *
         * @see .getSunriseBaalHatanya
         * @see .getSunsetBaalHatanya
         */
        const val ZENITH_1_POINT_583: Double = GEOMETRIC_ZENITH + 1.583

        /**
         * The zenith of 16.9 below geometric zenith (90). This calculation is used for determining *alos*
         * (dawn) based on the opinion of the Baal Hatanya. It is based on the calculation that the time between dawn
         * and *netz amiti* (sunrise) is 72 minutes, the time that is takes to walk 4 *mil* at 18 minutes
         * a *mil* ([Rambam](https://en.wikipedia.org/wiki/Maimonides) and others). The sun's position at 72
         * minutes before [<em>netz amiti</em> (sunrise)][.getSunriseBaalHatanya] in Jerusalem [around the equinox / equilux](https://kosherjava.com/2022/01/12/equinox-vs-equilux-zmanim-calculations/) is
         * 16.9 below [geometric zenith][.GEOMETRIC_ZENITH].
         *
         * @see .getAlosBaalHatanya
         */
        const val ZENITH_16_POINT_9: Double = GEOMETRIC_ZENITH + 16.9

        /**
         * The zenith of 6 below [geometric zenith][.GEOMETRIC_ZENITH] (90). This calculation is used for
         * calculating *tzais* / nightfall based on the opinion of the Baal Hatanya. This calculation is based on the
         * position of the sun 24 minutes after [sunset][.getSunset] in Jerusalem [around the equinox / equilux](https://kosherjava.com/2022/01/12/equinox-vs-equilux-zmanim-calculations/), which
         * is 6 below [geometric zenith][.GEOMETRIC_ZENITH].
         *
         * @see .getTzaisBaalHatanya
         */
        const val ZENITH_6_DEGREES: Double = GEOMETRIC_ZENITH + 6

        /**
         * The zenith of 6.45 below [geometric zenith][.GEOMETRIC_ZENITH] (90). This calculation is used for
         * calculating *tzais* (nightfall) according to some opinions. This is based on the calculations of [Rabbi Yechiel Michel Tucazinsky](https://en.wikipedia.org/wiki/Yechiel_Michel_Tucazinsky) of the position of
         * the sun no later than [31 minutes][.getTzaisGeonim6Point45Degrees] after sunset in Jerusalem, and at the
         * height of the summer solstice, this *zman* is 28 minutes after *shkiah*. This computes to 6.45
         * below [geometric zenith][.GEOMETRIC_ZENITH]. This calculation is found in the [Birur Halacha Yoreh Deah 262](https://hebrewbooks.org/pdfpager.aspx?req=50536&st=&pgnum=51) it the commonly
         * used *zman* in Israel. It should be noted that this differs from the 6.1/6.2 calculation for
         * Rabbi Tucazinsky's time as calculated by the Hazmanim Bahalacha Vol II chapter 50:7 (page 515).
         *
         * @see .getTzaisGeonim6Point45Degrees
         */
        const val ZENITH_6_POINT_45: Double = GEOMETRIC_ZENITH + 6.45

        /**
         * The zenith of 7.65 below [geometric zenith][.GEOMETRIC_ZENITH] (90). This calculation is used for
         * calculating *misheyakir* according to some opinions.
         *
         * @see .getMisheyakir7Point65Degrees
         */
        const val ZENITH_7_POINT_65: Double = GEOMETRIC_ZENITH + 7.65

        /**
         * The zenith of 7.67 below [geometric zenith][.GEOMETRIC_ZENITH] (90). This calculation is used for
         * calculating *tzais* according to some opinions.
         *
         * @see .getTzaisGeonim7Point67Degrees
         */
        const val ZENITH_7_POINT_67: Double = GEOMETRIC_ZENITH + 7.67

        /**
         * The zenith of 9.3 below [geometric zenith][.GEOMETRIC_ZENITH] (90). This calculation is used for
         * calculating *tzais* (nightfall) according to some opinions.
         *
         * @see .getTzaisGeonim9Point3Degrees
         */
        const val ZENITH_9_POINT_3: Double = GEOMETRIC_ZENITH + 9.3

        /**
         * The zenith of 9.5 below [geometric zenith][.GEOMETRIC_ZENITH] (90). This calculation is used for
         * calculating *misheyakir* according to some opinions.
         *
         * @see .getMisheyakir9Point5Degrees
         */
        const val ZENITH_9_POINT_5: Double = GEOMETRIC_ZENITH + 9.5

        /**
         * The zenith of 9.75 below [geometric zenith][.GEOMETRIC_ZENITH] (90). This calculation is used for
         * calculating *alos* (dawn) and *tzais* (nightfall) according to some opinions.
         *
         * @see .getTzaisGeonim9Point75Degrees
         */
        const val ZENITH_9_POINT_75: Double = GEOMETRIC_ZENITH + 9.75

        /**
         * The zenith of 2.1 above [geometric zenith][.GEOMETRIC_ZENITH] (90). This calculation is used for
         * calculating the start of *bain hashmashos* (twilight) of 13.5 minutes before sunset converted to degrees
         * according to the Yereim. As is traditional with degrees below the horizon, this is calculated without refraction
         * and from the center of the sun. It would be 0.833 less without this.
         *
         * @see .getBainHashmashosYereim2Point1Degrees
         */
        const val ZENITH_MINUS_2_POINT_1: Double = GEOMETRIC_ZENITH - 2.1

        /**
         * The zenith of 2.8 above [geometric zenith][.GEOMETRIC_ZENITH] (90). This calculation is used for
         * calculating the start of *bain hashmashos* (twilight) of 16.875 minutes before sunset converted to degrees
         * according to the Yereim. As is traditional with degrees below the horizon, this is calculated without refraction
         * and from the center of the sun. It would be 0.833 less without this.
         *
         * @see .getBainHashmashosYereim2Point8Degrees
         */
        const val ZENITH_MINUS_2_POINT_8: Double = GEOMETRIC_ZENITH - 2.8

        /**
         * The zenith of 3.05 above [geometric zenith][.GEOMETRIC_ZENITH] (90). This calculation is used for
         * calculating the start of *bain hashmashos* (twilight) of 18 minutes before sunset converted to degrees
         * according to the Yereim. As is traditional with degrees below the horizon, this is calculated without refraction
         * and from the center of the sun. It would be 0.833 less without this.
         *
         * @see .getBainHashmashosYereim3Point05Degrees
         */
        const val ZENITH_MINUS_3_POINT_05: Double = GEOMETRIC_ZENITH - 3.05
    }
}