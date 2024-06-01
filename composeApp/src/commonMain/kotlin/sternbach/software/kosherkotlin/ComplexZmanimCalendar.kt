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
package sternbach.software.kosherkotlin

import kotlinx.datetime.*
import sternbach.software.kosherkotlin.hebrewcalendar.JewishCalendar
import sternbach.software.kosherkotlin.metadata.UsesElevation
import sternbach.software.kosherkotlin.metadata.ZmanAuthority
import sternbach.software.kosherkotlin.metadata.ZmanAuthority.DIVREI_YOSEF
import sternbach.software.kosherkotlin.metadata.ZmanAuthority.RABEINU_TAM
import sternbach.software.kosherkotlin.metadata.ZmanCalculationMethod
import sternbach.software.kosherkotlin.metadata.ZmanCalculationMethod.Companion.degrees
import sternbach.software.kosherkotlin.metadata.ZmanCalculationMethod.Companion.fixed
import sternbach.software.kosherkotlin.metadata.ZmanCalculationMethod.Companion.zmaniyos
import sternbach.software.kosherkotlin.metadata.ZmanDefinition
import sternbach.software.kosherkotlin.metadata.ZmanType
import sternbach.software.kosherkotlin.util.AstronomicalCalculator
import sternbach.software.kosherkotlin.util.GeoLocation
import sternbach.software.kosherkotlin.util.GeoLocation.Companion.rawOffset
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes


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
 * long shaahZmanis = czc.temporalHour(czc.getSunriseOffsetByDegrees([AstronomicalCalendar.GEOMETRIC_ZENITH] + 12),
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
class ComplexZmanimCalendar(
    location: GeoLocation
) : ZmanimCalendar(location) {

    constructor(
        location: GeoLocation = GeoLocation(),
        date: LocalDate = Clock.System.todayIn(location.timeZone),
        useElevation: Boolean = false,
        ateretTorahSunsetOffset: Double = ATERET_TORAH_DEFAULT_OFFSET,
        candleLightingOffset: Double = 18.0,
    ) : this(location) {
        localDateTime = LocalDateTime(date, localDateTime.time)
        this.isUseElevation = useElevation
        this.ateretTorahSunsetOffset = ateretTorahSunsetOffset
        this.candleLightingOffset = candleLightingOffset
    }

    override var localDateTime: LocalDateTime =
        Clock.System.now().toLocalDateTime(geoLocation.timeZone)
        set(value) {
            field = value
            if (value.date != jewishCalendar.gregorianLocalDate) {
                jewishCalendar = JewishCalendar(value.date)
            }
        }
    var jewishCalendar: JewishCalendar = JewishCalendar(localDateTime.date)
        set(value) {
            field = value
            if (value.gregorianLocalDate != localDateTime.date) {
                localDateTime = LocalDateTime(value.gregorianLocalDate, localDateTime.time)
            }
        }

    /**
     * The offset in minutes after sunset used to calculate *tzais* based on the calculations of
     * *Chacham* Yosef Harari-Raful of Yeshivat Ateret Torah calculations. The default value is 40 minutes.
     * This affects most *zmanim*, since almost all zmanim use subset as part of their calculation.
     *
     * This is a `public var` to allow overriding the default and adjusting the offset used in calculating tzais.
     * *Chacham* Yosef Harari-Raful of Yeshivat Ateret Torah uses 40 minutes globally except for in Israel where a
     * 25-minute offset is used.
     * This offset impacts all Ateret Torah *zmanim*.
     *
     * @return the number of minutes after sunset for *Tzait*.
     */
    var ateretTorahSunsetOffset: Double = ATERET_TORAH_DEFAULT_OFFSET

    /**
     * Method to return a *shaah zmanis* (temporal hour) calculated using a 19.8 dip. This calculation
     * divides the day based on the opinion of the [Magen Avraham (MGA)](https://en.wikipedia.org/wiki/Avraham_Gombinern)
     * that the day runs from dawn to dusk.
     *
     * Dawn for this calculation is when the sun is 19.8 below the eastern geometric horizon before sunrise.
     * Dusk for this is when the sun is 19.8 below the western geometric horizon after sunset.
     * This day is split into 12 equal parts with each part being a *shaah zmanis*.
     *
     * @return the `long` millisecond length of a *shaah zmanis*. If the calculation can't be computed
     * such as northern and southern locations even south of the Arctic Circle and north of the Antarctic Circle
     * where the sun may not reach low enough below the horizon for this calculation, a [Long.MIN_VALUE]
     * will be returned. See detailed explanation on top of the [AstronomicalCalendar] documentation.
     */
    val shaahZmanis19Point8Degrees: Zman.ValueBased
        get() = Zman.ValueBased(
            ZmanDefinition(
                ZmanType.SHAA_ZMANIS,
                ZmanCalculationMethod.DayDefinition.dawnToDusk(19.8F.degrees),
                UsesElevation.ALWAYS,
            )
        ) {
            getTemporalHour(
                alos19Point8Degrees.momentOfOccurrence, tzais19Point8Degrees.momentOfOccurrence
            ).milliseconds
        }

    /**
     * Method to return a *shaah zmanis* (temporal hour) calculated using a 18 dip. This calculation divides
     * the day based on the opinion of the [Magen Avraham (MGA)](https://en.wikipedia.org/wiki/Avraham_Gombinern)
     * that the day runs from dawn to dusk.
     *
     * Dawn for this calculation is when the sun is 18 below the eastern geometric horizon before sunrise.
     * Dusk for this is when the sun is 18 below the western geometric horizon after sunset.
     * This day is split into 12 equal parts with each part being a *shaah zmanis*.
     *
     * @return the `long` millisecond length of a *shaah zmanis*. If the calculation can't be computed
     * such as northern and southern locations even south of the Arctic Circle and north of the Antarctic Circle
     * where the sun may not reach low enough below the horizon for this calculation, a [Long.MIN_VALUE]
     * will be returned. See detailed explanation on top of the [AstronomicalCalendar] documentation.
     */
    val shaahZmanis18Degrees: Zman.ValueBased
        get() = Zman.ValueBased(
            ZmanDefinition(
                ZmanType.SHAA_ZMANIS,
                ZmanCalculationMethod.DayDefinition.dawnToDusk(18F.degrees),
                UsesElevation.ALWAYS,
            )
        ) {
            getTemporalHour(
                alos18Degrees.momentOfOccurrence, tzais18Degrees.momentOfOccurrence
            ).milliseconds
        }

    /**
     * Method to return a *shaah zmanis* (temporal hour) calculated using a dip of 26. This calculation
     * divides the day based on the opinion of the [Magen Avraham (MGA)](https://en.wikipedia.org/wiki/Avraham_Gombinern)
     * that the day runs from dawn to dusk.
     *
     * Dawn for this calculation is when the sun is [26˚][alos26Degrees] below the eastern geometric horizon before sunrise.
     * Dusk for this is when the sun is [26˚][tzais26Degrees] below the western geometric horizon after sunset.
     * This day is split into 12 equal parts with each part being a *shaah zmanis*.
     *
     * Since *zmanim* that use this method are extremely late or early and at a point when the sky is a long time past
     * the 18 point where the darkest point is reached, *zmanim* that use this should only be used *lechumra*, such as
     * delaying the start of nighttime *mitzvos*.
     *
     * @return the `long` millisecond length of a *shaah zmanis*. If the calculation can't be computed
     * such as northern and southern locations even south of the Arctic Circle and north of the Antarctic Circle
     * where the sun may not reach low enough below the horizon for this calculation, a [Long.MIN_VALUE]
     * will be returned. See detailed explanation on top of the [AstronomicalCalendar] documentation.
     * @see shaahZmanis120Minutes
     */
    val shaahZmanis26Degrees: Zman.ValueBased
        get() = Zman.ValueBased(
            ZmanDefinition(
                ZmanType.SHAA_ZMANIS,
                ZmanCalculationMethod.DayDefinition.dawnToDusk(18F.degrees),
                UsesElevation.ALWAYS,
            )
        ) {
            getTemporalHour(
                alos26Degrees.momentOfOccurrence, tzais26Degrees.momentOfOccurrence
            ).milliseconds
        }

    /**
     * Method to return a *shaah zmanis* (temporal hour) calculated using a dip of 16.1. This calculation
     * divides the day based on the opinion of the [Magen Avraham (MGA)](https://en.wikipedia.org/wiki/Avraham_Gombinern) that the day runs from dawn to dusk.
     *
     * Dawn for this calculation is when the sun is 16.1 below the eastern geometric horizon before sunrise.
     * Dusk is when the sun is 16.1 below the western geometric horizon after sunset.
     * This day is split into 12 equal parts with each part being a *shaah zmanis*.
     *
     * @return the `long` millisecond length of a *shaah zmanis*. If the calculation can't be computed
     * such as northern and southern locations even south of the Arctic Circle and north of the Antarctic Circle
     * where the sun may not reach low enough below the horizon for this calculation, a [Long.MIN_VALUE]
     * will be returned. See detailed explanation on top of the [AstronomicalCalendar] documentation.
     *
     * @see alos16Point1Degrees
     * @see tzais16Point1Degrees
     * @see sofZmanShmaMGA16Point1Degrees
     * @see sofZmanTfilaMGA16Point1Degrees
     * @see minchaGedola16Point1Degrees
     * @see minchaKetana16Point1Degrees
     * @see plagHamincha16Point1Degrees
     */
    val shaahZmanis16Point1Degrees: Zman.ValueBased
        get() = Zman.ValueBased(
            ZmanDefinition(
                ZmanType.SHAA_ZMANIS,
                ZmanCalculationMethod.DayDefinition.dawnToDusk(16.1F.degrees),
                UsesElevation.ALWAYS,
            )
        ) {
            getTemporalHour(
                alos16Point1Degrees.momentOfOccurrence, tzais16Point1Degrees.momentOfOccurrence
            ).milliseconds
        }

    /**
     * Method to return a *shaah zmanis* (solar hour) according to the opinion of the [Magen Avraham (MGA)](https://en.wikipedia.org/wiki/Avraham_Gombinern). This calculation
     * divides the day based on the opinion of the MGA that the day runs from dawn to dusk.
     *
     * Dawn for this calculation is 60 minutes before sunrise.
     * Dusk is 60 minutes after sunset.
     * This day is split into 12 equal parts with each part being a *shaah zmanis*.
     *
     * @return the `long` millisecond length of a *shaah zmanis*. If the calculation can't be computed
     * such as in the Arctic Circle where there is at least one day a year where the sun does not rise, and one
     * where it does not set, a [Long.MIN_VALUE] will be returned. See detailed explanation on top of the
     * [AstronomicalCalendar] documentation.
     *
     * @see alos60
     * @see tzais60
     * @see plagHamincha60Minutes
     */
    val shaahZmanis60Minutes: Zman.ValueBased
        get() = Zman.ValueBased(
            ZmanDefinition(
                ZmanType.SHAA_ZMANIS,
                ZmanCalculationMethod.DayDefinition.dawnToDusk(60.minutes.fixed),
                UsesElevation.ALWAYS,
            )
        ) {
            getTemporalHour(alos60.momentOfOccurrence, tzais60.momentOfOccurrence).milliseconds
        }

    /**
     * Method to return a *shaah zmanis* (solar hour) according to the opinion of the [Magen Avraham (MGA)](https://en.wikipedia.org/wiki/Avraham_Gombinern). This calculation divides the day
     * based on the opinion of the MGA that the day runs from dawn to dusk.
     *
     * Dawn for this calculation is 72 minutes before sunrise.
     * Dusk is 72 minutes after sunset.
     * This day is split into 12 equal parts with each part being a *shaah zmanis*.
     *
     * @return the `long` millisecond length of a *shaah zmanis*. If the calculation can't be computed
     * such as in the Arctic Circle where there is at least one day a year where the sun does not rise, and one
     * where it does not set, a [Long.MIN_VALUE] will be returned. See detailed explanation on top of the
     * [AstronomicalCalendar] documentation.
     */
    val shaahZmanis72Minutes: Zman.ValueBased
        get() = shaahZmanisMGA

    /**
     * Method to return a *shaah zmanis* (temporal hour) according to the opinion of the [Magen Avraham (MGA)](https://en.wikipedia.org/wiki/Avraham_Gombinern) based on *alos* being
     * [72][alos72Zmanis] minutes *zmaniyos* before [sunrise]. This calculation
     * divides the day based on the opinion of the MGA that the day runs from dawn to dusk.
     *
     * Dawn for this calculation is 72 minutes *zmaniyos* before sunrise.
     * Dusk is 72 minutes *zmaniyos* after sunset.
     * This day is split into 12 equal parts with each part being a *shaah zmanis*.
     * This is identical to 1/10th of the day from [sunrise] to [sunset].
     *
     * @return the `long` millisecond length of a *shaah zmanis*. If the calculation can't be computed
     * such as in the Arctic Circle where there is at least one day a year where the sun does not rise, and one
     * where it does not set, a [Long.MIN_VALUE] will be returned. See detailed explanation on top of the
     * [AstronomicalCalendar] documentation.
     * @see alos72Zmanis
     * @see tzais72Zmanis
     */
    val shaahZmanis72MinutesZmanis: Zman.ValueBased
        get() = Zman.ValueBased(
            ZmanDefinition(
                ZmanType.SHAA_ZMANIS,
                ZmanCalculationMethod.DayDefinition.dawnToDusk(ZmanCalculationMethod.ZmaniyosDuration._72),
                UsesElevation.IF_SET,
            )
        ) {
            getTemporalHour(
                alos72Zmanis.momentOfOccurrence, tzais72Zmanis.momentOfOccurrence
            ).milliseconds
        }

    /**
     * Method to return a *shaah zmanis* (temporal hour) calculated using a dip of 90 minutes. This calculation
     * divides the day based on the opinion of the [Magen Avraham (MGA)](https://en.wikipedia.org/wiki/Avraham_Gombinern) that the day runs from dawn to dusk.
     *
     * Dawn for this calculation is 90 minutes before sunrise.
     * Dusk is 90 minutes after sunset.
     * This day is split into 12 equal parts with each part being a *shaah zmanis*.
     *
     * @return the `long` millisecond length of a *shaah zmanis*. If the calculation can't be computed
     * such as in the Arctic Circle where there is at least one day a year where the sun does not rise, and one
     * where it does not set, a [Long.MIN_VALUE] will be returned. See detailed explanation on top of the
     * [AstronomicalCalendar] documentation.
     */
    val shaahZmanis90Minutes: Zman.ValueBased
        get() = Zman.ValueBased(
            ZmanDefinition(
                ZmanType.SHAA_ZMANIS,
                ZmanCalculationMethod.DayDefinition.dawnToDusk(ZmanCalculationMethod.FixedDuration._90),
                UsesElevation.IF_SET,
            )
        ) {
            getTemporalHour(alos90.momentOfOccurrence, tzais90.momentOfOccurrence).milliseconds
        }

    /**
     * Method to return a *shaah zmanis* (temporal hour) according to the opinion of the [Magen Avraham (MGA)](https://en.wikipedia.org/wiki/Avraham_Gombinern) based on *alos* being
     * [90][alos90Zmanis] minutes *zmaniyos* before [sunrise]. This calculation divides
     * the day based on the opinion of the MGA that the day runs from dawn to dusk.
     *
     * Dawn for this calculation is 90 minutes *zmaniyos* before sunrise.
     * Dusk is 90 minutes *zmaniyos* after sunset.
     * This day is split into 12 equal parts with each part being a *shaah zmanis*.
     * This is 1/8th of the day from [sunrise] to [sunset].
     *
     * @return the `long` millisecond length of a *shaah zmanis*. If the calculation can't be computed
     * such as in the Arctic Circle where there is at least one day a year where the sun does not rise, and one
     * where it does not set, a [Long.MIN_VALUE] will be returned. See detailed explanation on top of the
     * [AstronomicalCalendar] documentation.
     * @see alos90Zmanis
     * @see tzais90Zmanis
     */
    val shaahZmanis90MinutesZmanis: Zman.ValueBased
        get() = Zman.ValueBased(
            ZmanDefinition(
                ZmanType.SHAA_ZMANIS,
                ZmanCalculationMethod.DayDefinition.dawnToDusk(ZmanCalculationMethod.ZmaniyosDuration._90),
                UsesElevation.IF_SET,
            )
        ) {
            getTemporalHour(
                alos90Zmanis.momentOfOccurrence, tzais90Zmanis.momentOfOccurrence
            ).milliseconds
        }

    /**
     * Method to return a *shaah zmanis* (temporal hour) according to the opinion of the [Magen Avraham (MGA)](https://en.wikipedia.org/wiki/Avraham_Gombinern) based on *alos* being
     * [96][alos96Zmanis] minutes *zmaniyos* before [sunrise]. This calculation divides the
     * day based on the opinion of the MGA that the day runs from dawn to dusk.
     *
     * Dawn for this calculation is 96 minutes *zmaniyos* before sunrise.
     * Dusk is 96 minutes *zmaniyos* after sunset.
     * This day is split into 12 equal parts with each part being a *shaah zmanis*.
     * This is identical to 1/7.5th of the day from [sunrise] to [sunset].
     *
     * @return the `long` millisecond length of a *shaah zmanis*. If the calculation can't be computed
     * such as in the Arctic Circle where there is at least one day a year where the sun does not rise, and one
     * where it does not set, a [Long.MIN_VALUE] will be returned. See detailed explanation on top of the
     * [AstronomicalCalendar] documentation.
     * @see alos96Zmanis
     * @see tzais96Zmanis
     */
    val shaahZmanis96MinutesZmanis: Zman.ValueBased
        get() = Zman.ValueBased(
            ZmanDefinition(
                ZmanType.SHAA_ZMANIS,
                ZmanCalculationMethod.DayDefinition.dawnToDusk(ZmanCalculationMethod.ZmaniyosDuration._96),
                UsesElevation.IF_SET,
            )
        ) {
            getTemporalHour(
                alos96Zmanis.momentOfOccurrence, tzais96Zmanis.momentOfOccurrence
            ).milliseconds
        }

    /**
     * Method to return a *shaah zmanis* (temporal hour) used by some *zmanim* according to the opinion of
     * [Rabbi Yaakov Moshe Hillel](https://en.wikipedia.org/wiki/Yaakov_Moshe_Hillel) as published in the
     * *luach* of the Bais Horaah of Yeshivat Chevrat Ahavat Shalom.
     *
     * Dawn for this calculation is 72 minutes before sunrise in degrees [*alos* 16.1˚][alos16Point1Degrees]
     * Dusk for this calculation is 14 minutes after sunset in degrees [*tzais* 3.8˚][tzaisGeonim3Point8Degrees].
     * This day is split into 12 equal parts with each part being a *shaah zmanis*.
     *
     * Note that with this system, *chatzos* (mid-day) will not be the point that the sun is [halfway across the sky][sunTransit].
     * These *shaos zmaniyos* are used for *Mincha Ketana* and *Plag Hamincha*.
     * The 14 minutes are based on 3/4 of an 18 minute *mil*, with half a minute added for Rav Yosi.
     *
     * @return the `long` millisecond length of a *shaah zmanis*. If the calculation can't be computed
     * such as in the Arctic Circle where there is at least one day a year where the sun does not rise, and one
     * where it does not set, a [Long.MIN_VALUE] will be returned. See detailed explanation on top of the
     * [AstronomicalCalendar] documentation.
     *
     * @see minchaKetanaAhavatShalom
     * @see plagAhavatShalom
     */
    val shaahZmanisAlos16Point1ToTzais3Point8: Zman.ValueBased
        get() = Zman.ValueBased(
            ZmanDefinition(
                ZmanType.SHAA_ZMANIS,
                ZmanCalculationMethod.DayDefinition.DAWN_16_1_TO_DUSK_3_8,
                UsesElevation.ALWAYS,
                listOf(
                    ZmanAuthority.AHAVAT_SHALOM
                )
            )
        ) {
            getTemporalHour(
                alos16Point1Degrees.momentOfOccurrence, tzaisGeonim3Point8Degrees.momentOfOccurrence
            ).milliseconds
        }

    /**
     * Method to return a *shaah zmanis* (temporal hour) according to the opinion of the
     * *Chacham* Yosef Harari-Raful of Yeshivat Ateret Torah.
     *
     * Dawn for this calculation is 1/10th of sunrise to sunset day, or [72][alos72Zmanis] minutes *zmaniyos* of such a day before [sunrise].
     * Dusk is usually calculated as [40][tzaisAteretTorah] (configurable to any offset via setting [ateretTorahSunsetOffset]) after [sunset].
     * This day is split into 12 equal parts with each part being a *shaah zmanis*.
     *
     * Note that with this system, *chatzos* (mid-day) will not be the point that the sun is [halfway across][sunTransit].
     *
     * @return the `long` millisecond length of a *shaah zmanis*. If the calculation can't be computed
     * such as in the Arctic Circle where there is at least one day a year where the sun does not rise, and one
     * where it does not set, a [Long.MIN_VALUE] will be returned. See detailed explanation on top of the
     * [AstronomicalCalendar] documentation.
     * @see alos72Zmanis
     * @see tzaisAteretTorah
     * @see ateretTorahSunsetOffset
     * @see ateretTorahSunsetOffset
     */
    val shaahZmanisAteretTorah: Zman.ValueBased
        get() = Zman.ValueBased(
            ZmanDefinition(
                ZmanType.SHAA_ZMANIS,
                ZmanCalculationMethod.DayDefinition.dawn72ZmanisToDuskAteretTorah(
                    ateretTorahSunsetOffset
                ),
                UsesElevation.IF_SET,
            )
        ) {
            getTemporalHour(
                alos72Zmanis.momentOfOccurrence, tzaisAteretTorah.momentOfOccurrence
            ).milliseconds
        }

    /**
     * Method to return a *shaah zmanis* (temporal hour) used by some *zmanim* according to the opinion of
     * [Rabbi Yaakov Moshe Hillel](https://en.wikipedia.org/wiki/Yaakov_Moshe_Hillel) as published in the
     * *luach* of the Bais Horaah of Yeshivat Chevrat Ahavat Shalom.
     *
     * Day in this calculation day starts 72 minutes before sunrise in degrees [*alos* 16.1˚][alos16Point1Degrees]
     * and ending 13.5 minutes after sunset in degrees [*tzais* 3.7˚][tzaisGeonim3Point7Degrees].
     * This day is split into 12 equal parts with each part being a *shaah zmanis*.
     *
     * Note that with this system, *chatzos* (mid-day) will not be the point that the sun is [halfway across the sky][sunTransit].
     * These *shaos zmaniyos* are used for *Mincha Gedola* calculation.
     *
     * @return the `long` millisecond length of a *shaah zmanis*. If the calculation can't be computed
     * such as in the Arctic Circle where there is at least one day a year where the sun does not rise, and one
     * where it does not set, a [Long.MIN_VALUE] will be returned. See detailed explanation on top of the
     * [AstronomicalCalendar] documentation.
     *
     * @see minchaGedolaAhavatShalom
     */
    val shaahZmanisAlos16Point1ToTzais3Point7: Zman.ValueBased
        get() = Zman.ValueBased(
            ZmanDefinition(
                ZmanType.SHAA_ZMANIS,
                ZmanCalculationMethod.DayDefinition.DAWN_16_1_TO_DUSK_3_7,
                UsesElevation.ALWAYS,
                listOf(
                    ZmanAuthority.AHAVAT_SHALOM
                )
            )
        ) {
            getTemporalHour(
                alos16Point1Degrees.momentOfOccurrence, tzaisGeonim3Point7Degrees.momentOfOccurrence
            ).milliseconds
        }

    /**
     * Method to return a *shaah zmanis* (temporal hour) calculated using a dip of 96 minutes. This calculation
     * divides the day based on the opinion of the [Magen Avraham (MGA)](https://en.wikipedia.org/wiki/Avraham_Gombinern) that the day runs from dawn to dusk.
     *
     * Dawn for this calculation is 96 minutes before sunrise
     * Dusk is 96 minutes after sunset.
     * This day is split into 12 equal parts with each part being a *shaah zmanis*.
     *
     * @return the `long` millisecond length of a *shaah zmanis*. If the calculation can't be computed
     * such as in the Arctic Circle where there is at least one day a year where the sun does not rise, and one
     * where it does not set, a [Long.MIN_VALUE] will be returned. See detailed explanation on top of the
     * [AstronomicalCalendar] documentation.
     */
    val shaahZmanis96Minutes: Zman.ValueBased
        get() = Zman.ValueBased(
            ZmanDefinition(
                ZmanType.SHAA_ZMANIS,
                ZmanCalculationMethod.DayDefinition.dawnToDusk(ZmanCalculationMethod.FixedDuration._96),
                UsesElevation.IF_SET,
            )
        ) {
            getTemporalHour(alos96.momentOfOccurrence, tzais96.momentOfOccurrence).milliseconds
        }

    /**
     * Method to return a *shaah zmanis* (temporal hour) calculated using a dip of 120 minutes. This calculation
     * divides the day based on the opinion of the [Magen Avraham (MGA)](https://en.wikipedia.org/wiki/Avraham_Gombinern) that the day runs from dawn to dusk.
     *
     * Dawn for this calculation is 120 minutes before sunrise.
     * Dusk is 120 minutes after sunset.
     * This day is split into 12 equal parts with each part being a *shaah zmanis*.
     *
     * Since *zmanim* that use this method are extremely late or early and at a point when the sky is a long time
     * past the 18 point where the darkest point is reached, *zmanim* that use this should only be used
     * *lechumra* only, such as delaying the start of nighttime *mitzvos*.
     *
     * @return the `long` millisecond length of a *shaah zmanis*. If the calculation can't be computed
     * such as in the Arctic Circle where there is at least one day a year where the sun does not rise, and one
     * where it does not set, a [Long.MIN_VALUE] will be returned. See detailed explanation on top of the
     * [AstronomicalCalendar] documentation.
     * @see shaahZmanis26Degrees
     */
    val shaahZmanis120Minutes: Zman.ValueBased
        get() = Zman.ValueBased(
            ZmanDefinition(
                ZmanType.SHAA_ZMANIS,
                ZmanCalculationMethod.DayDefinition.dawnToDusk(ZmanCalculationMethod.FixedDuration._120),
                UsesElevation.IF_SET,
            )
        ) {
            getTemporalHour(alos120.momentOfOccurrence, tzais120.momentOfOccurrence).milliseconds
        }

    /**
     * Method to return a *shaah zmanis* (temporal hour) according to the opinion of the [Magen Avraham (MGA)](https://en.wikipedia.org/wiki/Avraham_Gombinern) based on *alos* being
     * [120][alos120Zmanis] minutes *zmaniyos* before [sunrise].
     * This calculation divides the day based on the opinion of the MGA that the day runs from dawn to dusk.
     *
     * Dawn for this calculation is 120 minutes *zmaniyos* before sunrise.
     * Dusk is 120 minutes *zmaniyos* after sunset.
     * This day is split into 12 equal parts with each part being a *shaah zmanis*.
     * This is identical to 1/6th of the day from [sunrise] to [sunset].
     *
     * Since *zmanim* that use this method are extremely late or early and at a point when the sky is a long time past
     * the 18 point where the darkest point is reached, *zmanim* that use this should only be used *lechumra* such as
     * delaying the start of  nighttime *mitzvos*.
     *
     * @return the `long` millisecond length of a *shaah zmanis*. If the calculation can't be computed
     * such as in the Arctic Circle where there is at least one day a year where the sun does not rise, and one
     * where it does not set, a [Long.MIN_VALUE] will be returned. See detailed explanation on top of the
     * [AstronomicalCalendar] documentation.
     * @see alos120Zmanis
     * @see tzais120Zmanis
     */
    val shaahZmanis120MinutesZmanis: Zman.ValueBased
        get() = Zman.ValueBased(
            ZmanDefinition(
                ZmanType.SHAA_ZMANIS,
                ZmanCalculationMethod.DayDefinition.dawnToDusk(ZmanCalculationMethod.ZmaniyosDuration._120),
                UsesElevation.IF_SET,
            )
        ) {
            getTemporalHour(
                alos120Zmanis.momentOfOccurrence, tzais120Zmanis.momentOfOccurrence
            ).milliseconds
        }

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
     * @see shaahZmanis120MinutesZmanis
     * @see alos120
     * @see tzais120
     * @see plagHamincha26Degrees
     * @see plagHamincha120Minutes
     */  // (forRemoval=false) // add back once Java 9 is the minimum supported version
    @get:Deprecated("This method should be used <em>lechumra</em> only since it returns a very late time, and if used\n" + "	          <em>lekula</em> can result in <em>chillul Shabbos</em> etc. There is no current plan to remove this\n" + "	          method from the API, and this deprecation is intended to alert developers of the danger of using it.\n" + "	  \n" + "	  ")
    val plagHamincha120MinutesZmanis: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.PLAG_HAMINCHA,
                ZmanCalculationMethod.DayDefinition.dawnToDusk(ZmanCalculationMethod.ZmaniyosDuration._120),
                UsesElevation.IF_SET,
            )
        ) {
            getPlagHamincha(alos120Zmanis.momentOfOccurrence, tzais120Zmanis.momentOfOccurrence)
        }

    /**
     * This method should be used *lechumra* only and returns the time of *plag hamincha* according to the
     * [Magen Avraham (MGA)](https://en.wikipedia.org/wiki/Avraham_Gombinern).
     *
     * Day in this calculation starts 120 minutes before sunrise and ends 120 minutes after sunset.
     * This is calculated as 10.75 hours after [dawn 120 minutes][alos120].
     * The formula used is 10.75 [shaahZmanis120Minutes] after [alos120].
     * Since the *zman* based on an extremely early *alos* and a very late *tzais*, it should only be used *lechumra*.
     *
     * @return the `Date` of the time of *plag hamincha*. If the calculation can't be computed such as
     * in the Arctic Circle where there is at least one day a year where the sun does not rise, and one where it
     * does not set, a null will be returned. See detailed explanation on top of the
     * [AstronomicalCalendar] documentation.
     *
     * @see shaahZmanis120Minutes
     * @see .getPlagHamincha26Degrees
     */  // (forRemoval=false) // add back once Java 9 is the minimum supported version
    @get:Deprecated("This method should be used <em>lechumra</em> only since it returns a very late time, and if used\n" + "	          <em>lekula</em> can result in <em>chillul Shabbos</em> etc. There is no current plan to remove this\n" + "	          method from the API, and this deprecation is intended to alert developers of the danger of using it.\n" + "	  \n" + "	  ")
    val plagHamincha120Minutes
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.PLAG_HAMINCHA,
                ZmanCalculationMethod.DayDefinition.dawnToDusk(120.minutes.fixed),
                UsesElevation.IF_SET,
            )
        ) {
            getPlagHamincha(alos120.momentOfOccurrence, tzais120.momentOfOccurrence)
        }

    /**
     * Method to return *alos* (dawn) calculated as 60 minutes before sunrise.
     *
     * This is the time to walk the distance of 4 *Mil* at 15 minutes per *Mil* (4 * 15 = 60).
     *
     * **Opinions on how long it takes to walk a *mil*:**
     *
     * - This (15 minutes per *mil*) seems to be the opinion of the [Chavas Yair](https://en.wikipedia.org/wiki/Yair_Bacharach) in
     * the Mekor Chaim, Orach Chaim Ch. 90, though the Mekor Chaim in Ch. 58 and in the [Chut Hashani Ch. 97](https://hebrewbooks.org/pdfpager.aspx?req=45193&pgnum=214)
     * states that a person walks 3 and a 1/3 *mil* in an hour, or an 18-minute *mil*.
     *
     * - Also see the [Divrei Malkiel](https://he.wikipedia.org/wiki/%D7%9E%D7%9C%D7%9B%D7%99%D7%90%D7%9C_%D7%A6%D7%91%D7%99_%D7%98%D7%A0%D7%A0%D7%91%D7%95%D7%99%D7%9D) [Vol. 4, Ch. 20, page 34](https://hebrewbooks.org/pdfpager.aspx?req=803&pgnum=33))
     * who mentions the 15 minute *mil* lechumra by baking matzos.
     *
     * - Also see the [Maharik](https://en.wikipedia.org/wiki/Joseph_Colon_Trabotto) [Ch. 173](https://hebrewbooks.org/pdfpager.aspx?req=1142&pgnum=216)
     * where the questioner quoting the [Ra'avan](https://en.wikipedia.org/wiki/Eliezer_ben_Nathan) is of the opinion
     * that the time to walk a *mil* is 15 minutes (5 *mil* in a little over an hour).
     *
     *     - There are many who believe that there is a *ta'us sofer* (scribe's error) in the Ra'avan, and it should 4 *mil*
     * in a little over an hour, or an 18-minute *mil*.
     *
     * Time based offset calculations are based on the opinion of the *[Rishonim](https://en.wikipedia.org/wiki/Rishonim)*
     * who stated that the time of the *neshef* (time between dawn and sunrise) does not vary by the time of year or location
     * but purely depends on the time it takes to walk the distance of 4 *mil*.
     *
     * [tzaisGeonim9Point75Degrees] is a related *zman* that is a degree-based calculation based on 60 minutes.
     *
     * @return the [Instant] representing the time. If the calculation can't be computed such as in the Arctic
     * Circle where there is at least one day a year where the sun does not rise, and one where it does not set,
     * a null will be returned. See detailed explanation on top of the [AstronomicalCalendar].
     * documentation.
     *
     * @see tzais60
     * @see plagHamincha60Minutes
     * @see shaahZmanis60Minutes
     */
    val alos60
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.ALOS,
                ZmanCalculationMethod.Relationship(ZmanType.ALOS occurs 60.minutes before ZmanType.HANAITZ),
                UsesElevation.IF_SET,
            )
        ) {
            getTimeOffset(
                sunrise, -60 * MINUTE_MILLIS
            )
        }

    /**
     * Method to return *alos* (dawn) calculated using 72 minutes *zmaniyos* or 1/10th of the day before
     * sunrise.
     *
     * This is based on an 18-minute *Mil* so the time for 4 *Mil* is 72 minutes.
     *
     * This is 1/10th of a day (12 * 60 = 720), by the calculation that a day starts at [sea level sunrise][seaLevelSunrise]
     * and ends at [sea level sunset][seaLevelSunrise] (or [sunrise] to [sunset], depending on the [isUseElevation] setting).
     *
     * The actual calculation is [seaLevelSunrise] - ([shaahZmanisGra] * 1.2). This calculation
     * is used in the calendars published by the [Hisachdus Harabanim D'Artzos Habris Ve'Canada](https://en.wikipedia.org/wiki/Central_Rabbinical_Congress).
     *
     * @return the `Date` representing the time. If the calculation can't be computed such as in the Arctic
     * Circle where there is at least one day a year where the sun does not rise, and one where it does not set,
     * a null will be returned. See detailed explanation on top of the [AstronomicalCalendar]
     * documentation.
     * @see shaahZmanisGra
     */
    val alos72Zmanis: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.ALOS,
                ZmanCalculationMethod.Relationship(ZmanType.ALOS occurs 72.minutes.zmaniyos before ZmanType.HANAITZ),
                UsesElevation.IF_SET,
                listOf(
                    ZmanAuthority.HISACHDUS_HARABONIM
                ),
            )
        ) {
            getZmanisBasedOffset(-1.2)
        }

    /**
     * Method to return *alos* (dawn) calculated using 96 minutes before [sunrise] or
     * [sea level sunrise][seaLevelSunrise] (depending on the [isUseElevation] setting) that is based
     * on the time to walk the distance of 4 *Mil* at 24 minutes a *Mil*.
     *
     * Time based offset calculations for *alos* are based on the opinion of the *[Rishonim](https://en.wikipedia.org/wiki/Rishonim)*
     * who stated that the time of the *Neshef* (time between dawn and sunrise) does not vary by the time of year or location
     * but purely depends on the time it takes to walk the distance of 4 *Mil*.
     *
     * @return the `Date` representing the time. If the calculation can't be computed such as in the Arctic
     * Circle where there is at least one day a year where the sun does not rise, and one where it does not set,
     * a null will be returned. See detailed explanation on top of the [AstronomicalCalendar]
     * documentation.
     */
    val alos96: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.ALOS,
                ZmanCalculationMethod.Relationship(ZmanType.ALOS occurs 96.minutes before ZmanType.HANAITZ),
                UsesElevation.IF_SET,
            )
        ) {
            getTimeOffset(elevationAdjustedSunrise, -96 * MINUTE_MILLIS)
        }

    /**
     * Method to return *alos* (dawn) calculated using 90 minutes *zmaniyos* or 1/8th of the day before
     * [sunrise] or [sea level sunrise][seaLevelSunrise] (depending on the [isUseElevation] setting).
     *
     * This is based on a 22.5-minute *Mil* so the time for 4 *Mil* is 90 minutes which is 1/8th of a day (12 * 60) / 8 = 90.
     *
     * The day is calculated from [sea level sunrise][seaLevelSunrise] to [sea level sunset][seaLevelSunset] or [sunrise][sunrise] to [sunset][sunset] (depending on the [isUseElevation].
     * The actual calculation used is [sunrise] - ([shaahZmanisGra] * 1.5).
     *
     * @return the `Date` representing the time. If the calculation can't be computed such as in the Arctic
     * Circle where there is at least one day a year where the sun does not rise, and one where it does not set,
     * a null will be returned. See detailed explanation on top of the [AstronomicalCalendar]
     * documentation.
     * @see shaahZmanisGra
     */
    val alos90Zmanis: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.ALOS,
                ZmanCalculationMethod.Relationship(ZmanType.ALOS occurs 90.minutes.zmaniyos before ZmanType.HANAITZ),
                UsesElevation.IF_SET,
            )
        ) {
            getZmanisBasedOffset(-1.5)
        }

    /**
     * This method returns *alos* (dawn) calculated using 96 minutes *zmaniyos* or 1/7.5th of the day before
     * [sunrise][sunrise] or [sea level sunrise][seaLevelSunrise] (depending on the [isUseElevation] setting).
     *
     * This is based on a 24-minute *Mil* so the time for 4 *Mil* is 96 minutes which is 1/7.5th of a day (12 * 60 / 7.5 = 96).
     * The day is calculated from [sea level sunrise][seaLevelSunrise] to [sea level][seaLevelSunrise] or [sunrise][sunrise] to [sunset][sunset] (depending on the [isUseElevation].
     * The actual calculation used is [sunrise] - ([shaahZmanisGra] * 1.6).
     *
     * @return the [Instant] representing the time. If the calculation can't be computed such as in the Arctic
     * Circle where there is at least one day a year where the sun does not rise, and one where it does not set,
     * a null will be returned. See detailed explanation on top of the [AstronomicalCalendar]
     * documentation.
     * @see shaahZmanisGra
     */
    val alos96Zmanis: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.ALOS,
                ZmanCalculationMethod.Relationship(ZmanType.ALOS occurs 96.minutes.zmaniyos before ZmanType.HANAITZ),
                UsesElevation.IF_SET,
            )
        ) {
            getZmanisBasedOffset(-1.6)
        }

    /**
     * Method to return *alos* (dawn) calculated using 90 minutes before [sea level][seaLevelSunrise] based on the time to walk the distance of 4 *Mil* at 22.5 minutes a *Mil*.
     *
     * Time based offset calculations for *alos* are based on the opinion of the *[Rishonim](https://en.wikipedia.org/wiki/Rishonim)* who stated that the time of the *Neshef*
     * (time between dawn and sunrise) does not vary by the time of year or location but purely depends on the time it
     * takes to walk the distance of 4 *Mil*.
     *
     * @return the `Date` representing the time. If the calculation can't be computed such as in the Arctic
     * Circle where there is at least one day a year where the sun does not rise, and one where it does not set,
     * a null will be returned. See detailed explanation on top of the [AstronomicalCalendar]
     * documentation.
     */
    val alos90: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.ALOS,
                ZmanCalculationMethod.Relationship(ZmanType.ALOS occurs 90.minutes before ZmanType.HANAITZ),
                UsesElevation.IF_SET,
            )
        ) {
            getTimeOffset(elevationAdjustedSunrise, -90 * MINUTE_MILLIS)
        }

    /**
     * This method should be used *lechumra* only and returns *alos* (dawn) calculated using 120 minutes
     * before [sea level sunrise][seaLevelSunrise] based on the time
     * to walk the distance of 5 *Mil*(*Ula*) at 24 minutes a *Mil*.
     *
     * Time based offset calculations
     * for *alos* are based on the* opinion of the *[Rishonim](https://en.wikipedia.org/wiki/Rishonim)
     * who stated that the time of the *neshef* (time between dawn and sunrise) does not vary by the time of
     * year or location but purely depends on the time it takes to walk the distance of 5 *Mil*(*Ula*). Since
     * this time is extremely early, it should only be used *lechumra*, such as not eating after this time on a fast
     * day, and not as the start time for *mitzvos* that can only be performed during the day.
     *
     * @return the `Date` representing the time. If the calculation can't be computed such as in the Arctic
     * Circle where there is at least one day a year where the sun does not rise, and one where it does not set,
     * a null will be returned. See detailed explanation on top of the [AstronomicalCalendar]
     * documentation.
     *
     * @see tzais120
     * @see alos26Degrees
     */  // (forRemoval=false) // add back once Java 9 is the minimum supported version
    @get:Deprecated("This method should be used <em>lechumra</em> only (such as stopping to eat at this time on a fast day),\n" + "	          since it returns a very early time, and if used <em>lekula</em> can result in doing <em>mitzvos hayom</em>\n" + "	          too early according to most opinions. There is no current plan to remove this method from the API, and this\n" + "	          deprecation is intended to alert developers of the danger of using it.\n" + "	  \n" + "	  ")
    val alos120: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.ALOS,
                ZmanCalculationMethod.Relationship(ZmanType.ALOS occurs 120.minutes before ZmanType.HANAITZ),
                UsesElevation.IF_SET,
            )
        ) {
            getTimeOffset(elevationAdjustedSunrise, -120 * MINUTE_MILLIS)
        }

    /**
     * This method should be used *lechumra* only and  method returns *alos* (dawn) calculated using
     * 120 minutes *zmaniyos* or 1/6th of the day before [sunrise] or [seaLevelSunrise] (depending on the [isUseElevation] setting). This is based
     * on a 24-minute *Mil* so the time for 5 *Mil* is 120 minutes which is 1/6th of a day (12 * 60 /
     * 6 = 120). The day is calculated from [sea level sunrise][seaLevelSunrise] to [seaLevelSunrise] or [sunrise] to [sunset] (depending
     * on the [isUseElevation]. The actual calculation used is [sunrise] - ([shaahZmanisGra] * 2). Since this time is extremely early, it should only be used *lechumra*, such
     * as not eating after this time on a fast day, and not as the start time for *mitzvos* that can only be
     * performed during the day.
     *
     * @return the `Date` representing the time. If the calculation can't be computed such as in the Arctic
     * Circle where there is at least one day a year where the sun does not rise, and one where it does not set,
     * a null will be returned. See detailed explanation on top of the [AstronomicalCalendar]
     * documentation.
     * @see alos120
     * @see alos26Degrees
     */  // (forRemoval=false) // add back once Java 9 is the minimum supported version
    @get:Deprecated("This method should be used <em>lechumra</em> only (such as stopping to eat at this time on a fast day),\n" + "	          since it returns a very early time, and if used <em>lekula</em> can result in doing <em>mitzvos hayom</em>\n" + "	          too early according to most opinions. There is no current plan to remove this method from the API, and this\n" + "	          deprecation is intended to alert developers of the danger of using it.\n" + "	  \n" + "	  ")
    val alos120Zmanis: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.ALOS,
                ZmanCalculationMethod.Relationship(ZmanType.ALOS occurs 120.minutes.zmaniyos before ZmanType.HANAITZ),
                UsesElevation.IF_SET,
            )
        ) {
            getZmanisBasedOffset(-2.0)
        }

    /**
     * This method should be used *lechumra* only and returns *alos* (dawn) calculated when the sun is [ZENITH_26_DEGREES] below the eastern geometric horizon before sunrise.
     * This calculation is based on the same calculation of [120 minutes][alos120] but uses a degree-based calculation instead of 120 exact minutes.
     * This calculation is based on the position of the sun 120 minutes before sunrise in Jerusalem [around the equinox / equilux](https://kosherjava.com/2022/01/12/equinox-vs-equilux-zmanim-calculations/),
     * which calculates to 26 below [geometric zenith][AstronomicalCalendar.GEOMETRIC_ZENITH].
     *
     * Since this time is extremely early, it should only be used *lechumra* only, such as not eating after this time
     * on a fast day, and not as the start time for *mitzvos* that can only be performed during the day.
     *
     * @return the `Date` representing *alos*. If the calculation can't be computed such as northern
     * and southern locations even south of the Arctic Circle and north of the Antarctic Circle where the sun
     * may not reach low enough below the horizon for this calculation, a null will be returned. See detailed
     * explanation on top of the [AstronomicalCalendar] documentation.
     * @see ZENITH_26_DEGREES
     *
     * @see alos120
     * @see tzais120
     * @see tzais26Degrees
     */  // (forRemoval=false) // add back once Java 9 is the minimum supported version
    @get:Deprecated("This method should be used <em>lechumra</em> only (such as stopping to eat at this time on a fast day),\n" + "	          since it returns a very early time, and if used <em>lekula</em> can result in doing <em>mitzvos hayom</em>\n" + "	          too early according to most opinions. There is no current plan to remove this  method from the API, and this\n" + "	          deprecation is intended to alert developers of the danger of using it.\n" + "	  \n" + "	  ")
    val alos26Degrees: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.ALOS,
                ZmanCalculationMethod.Relationship(ZmanType.ALOS occurs 26.degrees before ZmanType.HANAITZ),
                UsesElevation.ALWAYS,
            )
        ) {
            getSunriseOffsetByDegrees(ZENITH_26_DEGREES)
        }

    /**
     * A method to return *alos* (dawn) calculated when the sun is [18˚][AstronomicalCalendar.ASTRONOMICAL_ZENITH] below the
     * eastern geometric horizon before sunrise.
     *
     * @return the `Date` representing *alos*. If the calculation can't be computed such as northern
     * and southern locations even south of the Arctic Circle and north of the Antarctic Circle where the sun
     * may not reach low enough below the horizon for this calculation, a null will be returned. See detailed
     * explanation on top of the [AstronomicalCalendar] documentation.
     * @see ASTRONOMICAL_ZENITH
     */
    val alos18Degrees: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.ALOS,
                18F.degrees,
                UsesElevation.ALWAYS
            )
        ) {
            getSunriseOffsetByDegrees(ASTRONOMICAL_ZENITH)
        }

    /**
     * A method to return *alos* (dawn) calculated when the sun is [19˚][ZENITH_19_DEGREES] below the
     * eastern geometric horizon before sunrise.
     * **Sources:**
     * - This is the [Rambam](https://en.wikipedia.org/wiki/Maimonides) 's *alos* according to
     * - Rabbi Moshe Kosower's [Maaglei Tzedek](https://www.worldcat.org/oclc/145454098), page 88,
     * - Rabbi Dovid Shor's [Ayeles Hashachar Vol. I, page 12](https://hebrewbooks.org/pdfpager.aspx?req=33464&pgnum=13),
     * - Rabbi Yaakov Gershon Weiss's [Yom Valayla Shel Torah, Ch. 34, p. 222](https://hebrewbooks.org/pdfpager.aspx?req=55960&pgnum=258) and
     * - Rabbi Yaakov Shakow's [Luach Ikvei Hayom](https://www.worldcat.org/oclc/1043573513).
     *
     * @return the `Date` representing *alos*. If the calculation can't be computed such as northern
     * and southern locations even south of the Arctic Circle and north of the Antarctic Circle where the sun
     * may not reach low enough below the horizon for this calculation, a null will be returned. See detailed
     * explanation on top of the [AstronomicalCalendar] documentation.
     * @see ASTRONOMICAL_ZENITH
     */
    val alos19Degrees: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.ALOS,
                19F.degrees,
                UsesElevation.ALWAYS,
                listOf(
                    ZmanAuthority.RAMBAM,
                    ZmanAuthority.MAAGALEI_TZEDEK,
                    ZmanAuthority.SHOR,
                    ZmanAuthority.WEISS,
                    ZmanAuthority.SHAKOW,
                )
            )
        ) {
            getSunriseOffsetByDegrees(ZENITH_19_DEGREES)
        }

    /**
     * Method to return *alos* (dawn) calculated when the sun is [19.8˚][ZENITH_19_POINT_8] below the
     * eastern geometric horizon before sunrise. This calculation is based on the same calculation of
     * [90 minutes][alos90] but uses a degree-based calculation instead of 90 exact minutes. This calculation
     * is based on the position of the sun 90 minutes before sunrise in Jerusalem [around the equinox / equilux](https://kosherjava.com/2022/01/12/equinox-vs-equilux-zmanim-calculations/), which
     * calculates to 19.8 below [geometric zenith][AstronomicalCalendar.GEOMETRIC_ZENITH].
     *
     * @return the `Date` representing *alos*. If the calculation can't be computed such as northern
     * and southern locations even south of the Arctic Circle and north of the Antarctic Circle where the sun
     * may not reach low enough below the horizon for this calculation, a null will be returned. See detailed
     * explanation on top of the [AstronomicalCalendar] documentation.
     * @see ZENITH_19_POINT_8
     *
     * @see alos90
     */
    val alos19Point8Degrees: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.ALOS,
                19.8F.degrees,
                UsesElevation.ALWAYS,
            )
        ) {
            getSunriseOffsetByDegrees(ZENITH_19_POINT_8)
        }

    /**
     * Method to return *alos* (dawn) calculated when the sun is [16.1˚][ZENITH_16_POINT_1] below the
     * eastern geometric horizon before sunrise.
     *
     * This calculation is based on the same calculation of [72 minutes][alos72],
     * but uses a degree-based calculation instead of 72 exact minutes.
     * This calculation is based on the position of the sun 72 minutes before sunrise in Jerusalem [around the equinox / equilux](https://kosherjava.com/2022/01/12/equinox-vs-equilux-zmanim-calculations/),
     * which calculates to 16.1 below [geometric zenith][AstronomicalCalendar.GEOMETRIC_ZENITH].
     *
     * @return the `Date` representing *alos*. If the calculation can't be computed such as northern
     * and southern locations even south of the Arctic Circle and north of the Antarctic Circle where the sun
     * may not reach low enough below the horizon for this calculation, a null will be returned. See detailed
     * explanation on top of the [AstronomicalCalendar] documentation.
     * @see ZENITH_16_POINT_1
     *
     * @see alos72
     */
    val alos16Point1Degrees: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.ALOS,
                16.1F.degrees,
                UsesElevation.ALWAYS,
            )
        ) {
            getSunriseOffsetByDegrees(ZENITH_16_POINT_1)
        }

    /**
     * This method returns *misheyakir* based on the position of the sun when it is [11.5˚][ZENITH_11_DEGREES] below [geometric zenith][AstronomicalCalendar.GEOMETRIC_ZENITH] (90).
     * This calculation is used for calculating *misheyakir* according to some opinions.
     * This calculation is based on the position of the sun 52 minutes before [sunrise] in Jerusalem [around the equinox / equilux](https://kosherjava.com/2022/01/12/equinox-vs-equilux-zmanim-calculations/),
     * which calculates to 11.5 below [geometric zenith][AstronomicalCalendar.GEOMETRIC_ZENITH].
     * @todo recalculate.
     *
     * @return the `Date` of *misheyakir*. If the calculation can't be computed such as northern and
     * southern locations even south of the Arctic Circle and north of the Antarctic Circle where the sun may
     * not reach low enough below the horizon for this calculation, a null will be returned. See detailed
     * explanation on top of the [AstronomicalCalendar] documentation.
     * @see ZENITH_11_POINT_5
     */
    val misheyakir11Point5Degrees: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.MISHEYAKIR,
                11.5F.degrees,
                UsesElevation.ALWAYS,
            )
        ) {
            getSunriseOffsetByDegrees(ZENITH_11_POINT_5)
        }

    /**
     * This method returns *misheyakir* based on the position of the sun when it is [11˚][ZENITH_11_DEGREES] below [geometric zenith][AstronomicalCalendar.GEOMETRIC_ZENITH] (90). This calculation is used for calculating
     * *misheyakir* according to some opinions. This calculation is based on the position of the sun 48 minutes
     * before [sunrise] in Jerusalem [around the equinox / equilux](https://kosherjava.com/2022/01/12/equinox-vs-equilux-zmanim-calculations/),
     * which calculates to 11 below [geometric zenith][AstronomicalCalendar.GEOMETRIC_ZENITH].
     *
     * @return If the calculation can't be computed such as northern and southern locations even south of the Arctic
     * Circle and north of the Antarctic Circle where the sun may not reach low enough below the horizon for
     * this calculation, a null will be returned. See detailed explanation on top of the
     * [AstronomicalCalendar] documentation.
     * @see ZENITH_11_DEGREES
     */
    val misheyakir11Degrees: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.MISHEYAKIR,
                11F.degrees,
                UsesElevation.ALWAYS,
            )
        ) {
            getSunriseOffsetByDegrees(ZENITH_11_DEGREES)
        }

    /**
     * This method returns *misheyakir* based on the position of the sun when it is [10.2˚][ZENITH_10_POINT_2] below [geometric zenith][AstronomicalCalendar.GEOMETRIC_ZENITH] (90). This calculation is used for calculating
     * *misheyakir* according to some opinions. This calculation is based on the position of the sun 45 minutes
     * before [sunrise] in Jerusalem [around the equinox](https://kosherjava.com/2022/01/12/equinox-vs-equilux-zmanim-calculations/) which calculates
     * to 10.2 below [geometric zenith][AstronomicalCalendar.GEOMETRIC_ZENITH].
     *
     * @return the `Date` of *misheyakir*. If the calculation can't be computed such as
     * northern and southern locations even south of the Arctic Circle and north of the Antarctic Circle where
     * the sun may not reach low enough below the horizon for this calculation, a null will be returned. See
     * detailed explanation on top of the [AstronomicalCalendar] documentation.
     * @see ZENITH_10_POINT_2
     */
    val misheyakir10Point2Degrees: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.MISHEYAKIR,
                10.2F.degrees,
                UsesElevation.ALWAYS,
            )
        ) {
            getSunriseOffsetByDegrees(ZENITH_10_POINT_2)
        }

    /**
     * This method returns *misheyakir* based on the position of the sun when it is [7.65˚][ZENITH_7_POINT_65] below [geometric zenith][AstronomicalCalendar.GEOMETRIC_ZENITH] (90).
     * The degrees are based on a 35/36 minute *zman* [around the equinox / equilux](https://kosherjava.com/2022/01/12/equinox-vs-equilux-zmanim-calculations/),
     * when the *neshef* (twilight) is the shortest.
     *
     * **Sources:**
     * - This time is based on [Rabbi Moshe Feinstein](https://en.wikipedia.org/wiki/Moshe_Feinstein) who writes in [Ohr Hachaim Vol. 4, Ch. 6](https://hebrewbooks.org/pdfpager.aspx?req=14677&pgnum=7))
     * that *misheyakir* in New York is 35-40 minutes before sunset, something that is a drop less than 8.
     * - [Rabbi Yisroel Taplin](https://en.wikipedia.org/wiki/Yisroel_Taplin) in [Zmanei Yisrael](https://www.worldcat.org/oclc/889556744) (page 117) notes that [Rabbi Yaakov Kamenetsky](https://en.wikipedia.org/wiki/Yaakov_Kamenetsky) stated that it is not less than 36
     * minutes before sunrise (maybe it is 40 minutes).
     * - Sefer Yisrael Vehazmanim (p. 7) quotes the Tamar Yifrach in the name of the [Satmar Rov](https://en.wikipedia.org/wiki/Joel_Teitelbaum) that one should be stringent
     * not consider *misheyakir* before 36 minutes.
     * - This is also the accepted [minhag](https://en.wikipedia.org/wiki/Minhag) in [Lakewood](https://en.wikipedia.org/wiki/Lakewood_Township,_New_Jersey) that is used in the [Yeshiva](https://en.wikipedia.org/wiki/Beth_Medrash_Govoha).
     * - This follows the opinion of [Rabbi Shmuel Kamenetsky](https://en.wikipedia.org/wiki/Shmuel_Kamenetsky) who provided the time of 35/36 minutes,
     * but did not provide a degree-based time.
     *
     * Since this *zman* depends on the level of light, Rabbi Yaakov Shakow presented this degree-based calculations to Rabbi Kamenetsky who agreed to them.
     *
     * @return the `Date` of *misheyakir*. If the calculation can't be computed such as
     * northern and southern locations even south of the Arctic Circle and north of the Antarctic Circle where
     * the sun may not reach low enough below the horizon for this calculation, a null will be returned. See
     * detailed explanation on top of the [AstronomicalCalendar] documentation.
     *
     * @see ZENITH_7_POINT_65
     *
     * @see misheyakir9Point5Degrees
     */
    val misheyakir7Point65Degrees: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.MISHEYAKIR,
                7.65F.degrees,
                UsesElevation.ALWAYS,
                listOf(
                    ZmanAuthority.FEINSTEIN,
                    ZmanAuthority.KAMENETSKY,
                    ZmanAuthority.SHAKOW,
                    ZmanAuthority.SATMER,
                )
            )
        ) {
            getSunriseOffsetByDegrees(ZENITH_7_POINT_65)
        }

    /**
     * This method returns *misheyakir* based on the position of the sun when it is [9.5˚][ZENITH_9_POINT_5] below [geometric zenith][AstronomicalCalendar.GEOMETRIC_ZENITH] (90).
     *
     * **Sources:**
     * This calculation is based on
     * - Rabbi Dovid Kronglass's calculation of 45 minutes in Baltimore as mentioned in [Divrei Chachamim No. 24](https://hebrewbooks.org/pdfpager.aspx?req=20287&pgnum=29)
     * - brought down by Rabbi Yechiel Avraham Zilber's [Birur Halacha, Tinyana, Ch. 18](https://hebrewbooks.org/pdfpager.aspx?req=50535&pgnum=87).
     *     - This calculates to 9.5.
     * - Also see [Rabbi Yaakov Yitzchok Neiman](https://en.wikipedia.org/wiki/Jacob_Isaac_Neiman)
     * in Kovetz Eitz Chaim Vol. 9, p. 202 that the Vya'an Yosef (Rebbe of Pupa - [ZmanAuthority.GREENWALD]) did not want to rely on times earlier than 45 minutes in New York.
     * - This *zman* is also used in the calendars published by Rabbi Hershel Edelstein.
     * - As mentioned in Yisroel Vehazmanim, Rabbi Edelstein who was given the 45 minute *zman* by Rabbi Avraham Yehoshua Bick.
     * - The calendars published by the *[Edot Hamizrach](https://en.wikipedia.org/wiki/Mizrahi_Jews)* communities also use this *zman*.
     * - This also follows the opinion of [Rabbi Shmuel Kamenetsky](https://en.wikipedia.org/wiki/Shmuel_Kamenetsky) who provided
     * the time of 36 and 45 minutes, but did not provide a degree-based time.
     *
     * Since this *zman* depends on the level of light, Rabbi Yaakov Shakow presented these degree-based times to Rabbi Shmuel Kamenetsky who agreed to them.
     *
     * @return the `Date` of *misheyakir*. If the calculation can't be computed such as
     * northern and southern locations even south of the Arctic Circle and north of the Antarctic Circle where
     * the sun may not reach low enough below the horizon for this calculation, a null will be returned. See
     * detailed explanation on top of the [AstronomicalCalendar] documentation.
     *
     * @see ZENITH_9_POINT_5
     *
     * @see misheyakir7Point65Degrees
     */
    val misheyakir9Point5Degrees: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.MISHEYAKIR,
                9.5F.degrees,
                UsesElevation.ALWAYS,
                listOf(
                    ZmanAuthority.KRONGLASS,
                    ZmanAuthority.ZILBER,
                    ZmanAuthority.GREENWALD,
                    ZmanAuthority.EDELSTEIN,
                    ZmanAuthority.BICK,
                    ZmanAuthority.EIDOT_HAMIZRACH,
                    ZmanAuthority.KAMENETSKY,
                    ZmanAuthority.SHAKOW,
                )
            )
        ) {
            getSunriseOffsetByDegrees(ZENITH_9_POINT_5)
        }

    /**
     * This method returns the latest *zman krias shema* (time to recite Shema in the morning) according to the
     * opinion of the [Magen Avraham (MGA)](https://en.wikipedia.org/wiki/Avraham_Gombinern) based on
     * *alos* being [19.8˚][alos19Point8Degrees] before [sunrise].
     *
     * This time is 3 *[shaos zmaniyos][shaahZmanis19Point8Degrees]* (solar hours) after [alos19Point8Degrees],
     * based on the opinion of the MGA that the day is calculated from dawn to dusk, with both being 19.8 below
     * sunrise or sunset.
     *
     * This returns the time of 3 * [shaahZmanis19Point8Degrees] after [dawn][alos19Point8Degrees].
     *
     * @return the `Date` of the latest *zman krias shema*. If the calculation can't be computed such
     * as northern and southern locations even south of the Arctic Circle and north of the Antarctic Circle
     * where the sun may not reach low enough below the horizon for this calculation, a null will be returned.
     * See detailed explanation on top of the [AstronomicalCalendar] documentation.
     * @see shaahZmanis19Point8Degrees
     * @see alos19Point8Degrees
     */
    val sofZmanShmaMGA19Point8Degrees: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.SOF_ZMAN_KRIAS_SHEMA,
                ZmanCalculationMethod.DayDefinition.dawnToDusk(19.8F.degrees),
                UsesElevation.ALWAYS,
            )
        ) {
            getSofZmanShma(
                alos19Point8Degrees.momentOfOccurrence, tzais19Point8Degrees.momentOfOccurrence
            )
        }

    /**
     * This method returns the latest *zman krias shema* (time to recite Shema in the morning) according to the
     * opinion of the [Magen Avraham (MGA)](https://en.wikipedia.org/wiki/Avraham_Gombinern) based
     * on *alos* being [16.1˚][alos16Point1Degrees] before [sunrise].
     *
     * This time is 3 [*shaos zmaniyos*][shaahZmanis16Point1Degrees] (solar hours) after
     * [dawn][alos16Point1Degrees] based on the opinion of the MGA that the day is calculated from
     * dawn to nightfall with both being 16.1 below sunrise or sunset. This returns the time of
     * 3 * [shaahZmanis16Point1Degrees] after [dawn][alos16Point1Degrees].
     *
     * @return the `Date` of the latest *zman krias shema*. If the calculation can't be computed such
     * as northern and southern locations even south of the Arctic Circle and north of the Antarctic Circle
     * where the sun may not reach low enough below the horizon for this calculation, a null will be returned.
     * See detailed explanation on top of the [AstronomicalCalendar] documentation.
     * @see shaahZmanis16Point1Degrees
     * @see alos16Point1Degrees
     */
    val sofZmanShmaMGA16Point1Degrees: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.SOF_ZMAN_KRIAS_SHEMA,
                ZmanCalculationMethod.DayDefinition.dawnToDusk(16.1F.degrees),
                UsesElevation.ALWAYS,
            )
        ) {
            getSofZmanShma(
                alos16Point1Degrees.momentOfOccurrence, tzais16Point1Degrees.momentOfOccurrence
            )
        }

    /**
     * This method returns the latest *zman krias shema* (time to recite Shema in the morning) according to the
     * opinion of the [Magen Avraham (MGA)](https://en.wikipedia.org/wiki/Avraham_Gombinern) based
     * on *alos* being [18˚][alos18Degrees] before [sunrise]. This time is 3
     * [*shaos zmaniyos*][shaahZmanis18Degrees] (solar hours) after [dawn][alos18Degrees]
     * based on the opinion of the MGA that the day is calculated from dawn to nightfall with both being 18
     * below sunrise or sunset. This returns the time of 3 * [shaahZmanis18Degrees] after [dawn][alos18Degrees].
     *
     * @return the `Date` of the latest *zman krias shema*. If the calculation can't be computed such
     * as northern and southern locations even south of the Arctic Circle and north of the Antarctic Circle
     * where the sun may not reach low enough below the horizon for this calculation, a null will be returned.
     * See detailed explanation on top of the [AstronomicalCalendar] documentation.
     * @see shaahZmanis18Degrees
     * @see alos18Degrees
     */
    val sofZmanShmaMGA18Degrees: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.SOF_ZMAN_KRIAS_SHEMA,
                ZmanCalculationMethod.DayDefinition.dawnToDusk(18F.degrees),
                UsesElevation.ALWAYS,
            )
        ) {
            getSofZmanShma(alos18Degrees.momentOfOccurrence, tzais18Degrees.momentOfOccurrence)
        }

    /**
     * This method returns the latest *zman krias shema* (time to recite Shema in the morning) according to the
     * opinion of the [Magen Avraham (MGA)](https://en.wikipedia.org/wiki/Avraham_Gombinern) based on
     * *alos* being [72][alos72] minutes before [sunrise]. This time is 3 [shaahZmanis72Minutes] (solar hours) after [dawn][alos72] based on the opinion
     * of the MGA that the day is calculated from a [dawn][alos72] of 72 minutes before sunrise to
     * [nightfall][tzais72] of 72 minutes after sunset. This returns the time of 3 * [shaahZmanis72Minutes] after [dawn][alos72].
     *
     * This class returns an identical time to [sofZmanShmaMGA] and is repeated here for clarity.
     *
     * @return the `Date` of the latest *zman krias shema*. If the calculation can't be computed such
     * as in the Arctic Circle where there is at least one day a year where the sun does not rise, and one where
     * it does not set, a null will be returned. See detailed explanation on top of the
     * [AstronomicalCalendar] documentation.
     * @see shaahZmanis72Minutes
     * @see alos72
     * @see sofZmanShmaMGA
     */
    val sofZmanShmaMGA72Minutes: Zman.DateBased
        get() = sofZmanShmaMGA

    /**
     * This method returns the latest *zman krias shema* (time to recite *Shema* in the morning) according
     * to the opinion of the [Magen Avraham (MGA)](https://en.wikipedia.org/wiki/Avraham_Gombinern) based
     * on *alos* being [72][alos72Zmanis] minutes *zmaniyos*, or 1/10th of the day before
     * [sunrise]. This time is 3 [*shaos zmaniyos*][shaahZmanis90MinutesZmanis]
     * (solar hours) after [dawn][alos72Zmanis] based on the opinion of the MGA that the day is calculated
     * from a [dawn][alos72Zmanis] of 72 minutes *zmaniyos*, or 1/10th of the day before
     * [sea level sunrise][seaLevelSunrise] to [nightfall][tzais72Zmanis] of 72 minutes
     * *zmaniyos* after [sea level sunset][seaLevelSunset]. This returns the time of 3 *
     * [shaahZmanis72MinutesZmanis] after [dawn][alos72Zmanis].
     *
     * @return the `Date` of the latest *zman krias shema*. If the calculation can't be computed such
     * as in the Arctic Circle where there is at least one day a year where the sun does not rise, and one where
     * it does not set, a null will be returned. See detailed explanation on top of the
     * [AstronomicalCalendar] documentation.
     * @see shaahZmanis72MinutesZmanis
     * @see alos72Zmanis
     */
    val sofZmanShmaMGA72MinutesZmanis: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.SOF_ZMAN_KRIAS_SHEMA,
                ZmanCalculationMethod.DayDefinition.dawnToDusk(ZmanCalculationMethod.ZmaniyosDuration._72),
                UsesElevation.IF_SET,
            )
        ) {
            getSofZmanShma(alos72Zmanis.momentOfOccurrence, tzais72Zmanis.momentOfOccurrence)
        }

    /**
     * This method returns the latest *zman krias shema* (time to recite *Shema* in the morning) according
     * to the opinion of the [Magen Avraham (MGA)](https://en.wikipedia.org/wiki/Avraham_Gombinern) based on
     * *alos* being [90][alos90] minutes before [sunrise]. This time is 3
     * [*shaos zmaniyos*][shaahZmanis90Minutes] (solar hours) after [dawn][alos90] based on
     * the opinion of the MGA that the day is calculated from a [dawn][alos90] of 90 minutes before sunrise to
     * [nightfall][tzais90] of 90 minutes after sunset. This returns the time of 3 *
     * [shaahZmanis90Minutes] after [dawn][alos90].
     *
     * @return the `Date` of the latest *zman krias shema*. If the calculation can't be computed such
     * as in the Arctic Circle where there is at least one day a year where the sun does not rise, and one where
     * it does not set, a null will be returned. See detailed explanation on top of the
     * [AstronomicalCalendar] documentation.
     * @see shaahZmanis90Minutes
     * @see alos90
     */
    val sofZmanShmaMGA90Minutes: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.SOF_ZMAN_KRIAS_SHEMA,
                ZmanCalculationMethod.DayDefinition.dawnToDusk(ZmanCalculationMethod.FixedDuration._90),
                UsesElevation.IF_SET,
            )
        ) {
            getSofZmanShma(alos90.momentOfOccurrence, tzais90.momentOfOccurrence)
        }

    /**
     * This method returns the latest *zman krias shema* (time to recite Shema in the morning) according to the
     * opinion of the [Magen Avraham (MGA)](https://en.wikipedia.org/wiki/Avraham_Gombinern) based
     * on *alos* being [90][alos90Zmanis] minutes *zmaniyos* before [sunrise][sunrise]. This time is 3 [*shaos zmaniyos*][shaahZmanis90MinutesZmanis] (solar hours) after
     * [dawn][alos90Zmanis] based on the opinion of the MGA that the day is calculated from a [alos90Zmanis] of 90 minutes *zmaniyos* before sunrise to [nightfall][tzais90Zmanis]
     * of 90 minutes *zmaniyos* after sunset. This returns the time of 3 * [shaahZmanis90MinutesZmanis]
     * after [dawn][alos90Zmanis].
     *
     * @return the `Date` of the latest *zman krias shema*. If the calculation can't be computed such
     * as in the Arctic Circle where there is at least one day a year where the sun does not rise, and one where
     * it does not set, a null will be returned. See detailed explanation on top of the
     * [AstronomicalCalendar] documentation.
     * @see shaahZmanis90MinutesZmanis
     * @see alos90Zmanis
     */
    val sofZmanShmaMGA90MinutesZmanis: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.SOF_ZMAN_KRIAS_SHEMA,
                ZmanCalculationMethod.DayDefinition.dawnToDusk(ZmanCalculationMethod.ZmaniyosDuration._90),
                UsesElevation.IF_SET,
            )
        ) {
            getSofZmanShma(alos90Zmanis.momentOfOccurrence, tzais90Zmanis.momentOfOccurrence)
        }

    /**
     * This method returns the latest *zman krias shema* (time to recite Shema in the morning) according to the
     * opinion of the [Magen Avraham (MGA)](https://en.wikipedia.org/wiki/Avraham_Gombinern) based
     * on *alos* being [96][alos96] minutes before [sunrise]. This time is 3
     * [*shaos zmaniyos*][shaahZmanis96Minutes] (solar hours) after [dawn][alos96] based on
     * the opinion of the MGA that the day is calculated from a [dawn][alos96] of 96 minutes before
     * sunrise to [nightfall][tzais96] of 96 minutes after sunset. This returns the time of 3 * [shaahZmanis96Minutes] after [dawn][alos96].
     *
     * @return the `Date` of the latest *zman krias shema*. If the calculation can't be computed such
     * as in the Arctic Circle where there is at least one day a year where the sun does not rise, and one where
     * it does not set, a null will be returned. See detailed explanation on top of the
     * [AstronomicalCalendar] documentation.
     * @see shaahZmanis96Minutes
     * @see alos96
     */
    val sofZmanShmaMGA96Minutes: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.SOF_ZMAN_KRIAS_SHEMA,
                ZmanCalculationMethod.DayDefinition.dawnToDusk(ZmanCalculationMethod.FixedDuration._96),
                UsesElevation.IF_SET,
            )
        ) {
            getSofZmanShma(alos96.momentOfOccurrence, tzais96.momentOfOccurrence)
        }

    /**
     * This method returns the latest *zman krias shema* (time to recite Shema in the morning) according to the
     * opinion of the [Magen Avraham (MGA)](https://en.wikipedia.org/wiki/Avraham_Gombinern) based
     * on *alos* being [96][alos90Zmanis] minutes *zmaniyos* before [sunrise][sunrise]. This time is 3 [*shaos zmaniyos*][shaahZmanis96MinutesZmanis] (solar hours) after
     * [dawn][alos96Zmanis] based on the opinion of the MGA that the day is calculated from a [alos96Zmanis] of 96 minutes *zmaniyos* before sunrise to [nightfall][tzais90Zmanis]
     * of 96 minutes *zmaniyos* after sunset. This returns the time of 3 * [shaahZmanis96MinutesZmanis]
     * after [dawn][alos96Zmanis].
     *
     * @return the `Date` of the latest *zman krias shema*. If the calculation can't be computed such
     * as in the Arctic Circle where there is at least one day a year where the sun does not rise, and one where
     * it does not set, a null will be returned. See detailed explanation on top of the
     * [AstronomicalCalendar] documentation.
     * @see shaahZmanis96MinutesZmanis
     * @see alos96Zmanis
     */
    val sofZmanShmaMGA96MinutesZmanis: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.SOF_ZMAN_KRIAS_SHEMA,
                ZmanCalculationMethod.DayDefinition.dawnToDusk(ZmanCalculationMethod.ZmaniyosDuration._96),
                UsesElevation.IF_SET,
            )
        ) {
            getSofZmanShma(alos96Zmanis.momentOfOccurrence, tzais96Zmanis.momentOfOccurrence)
        }

    /**
     * This method returns the latest *zman krias shema* (time to recite *Shema* in the morning) calculated
     * as 3 hours (regular clock hours and not *sha'os zmaniyos*) before [ZmanimCalendar.chatzos].
     *
     *
     * Generally known as part of the "Komarno" *zmanim* after [Rav Yitzchak Eizik of Komarno](https://en.wikipedia.org/wiki/Komarno_(Hasidic_dynasty)#Rabbi_Yitzchak_Eisik_Safrin),
     * a proponent of this calculation, it actually predates him a lot.
     *
     * It is the opinion of
     * - the *Shach* in the Nekudas Hakesef (Yoreh Deah 184),
     * - [Rav Moshe Lifshitz](https://hebrewbooks.org/pdfpager.aspx?req=21638&st=&pgnum=30) in his commentary
     * [Lechem Mishneh on Brachos 1:2](https://hebrewbooks.org/pdfpager.aspx?req=21638&st=&pgnum=50).
     * - It is next brought down about 100 years later by the [Yaavetz](https://en.wikipedia.org/wiki/Jacob_Emden)
     *     - (in his *siddur*, [Mor Uktziah Orach Chaim 1](https://hebrewbooks.org/pdfpager.aspx?req=7920&st=&pgnum=6),
     *     - [Lechem Shamayim, Brachos 1:2](https://hebrewbooks.org/pdfpager.aspx?req=22309&st=&pgnum=30)
     *     - and [She'elos Yaavetz vol. 1 no. 40](https://hebrewbooks.org/pdfpager.aspx?req=1408&st=&pgnum=69)),
     * - Rav Yitzchak Eizik of Komarno in the Ma'aseh Oreg on Mishnayos Brachos 11:2,
     * - Shevus Yaakov,
     * - Chasan Sofer
     * - and others.
     * - See Yisrael Vehazmanim [vol. 1 7:3, page 55 - 62](https://hebrewbooks.org/pdfpager.aspx?req=9765&st=&pgnum=83).
     *
     * A variant of this calculation ([sofZmanShmaGRASunriseToFixedLocalChatzos]) uses [fixed][fixedLocalChatzos] for calculating this type of *zman*.
     *
     * @return the `Date` of the latest *zman krias shema*. If the calculation can't be computed such
     * as in the Arctic Circle where there is at least one day a year where the sun does not rise, and one where
     * it does not set, a null will be returned. See detailed explanation on top of the
     * [AstronomicalCalendar] documentation.
     * @see ZmanimCalendar.getChatzos
     * @see sofZmanShmaFixedLocal
     * @see sofZmanTfila2HoursBeforeChatzos
     */
    val sofZmanShma3HoursBeforeChatzos: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.SOF_ZMAN_KRIAS_SHEMA,
                ZmanCalculationMethod.Relationship(ZmanType.SOF_ZMAN_KRIAS_SHEMA occurs 3.hours before ZmanType.CHATZOS_HAYOM),
                UsesElevation.NEVER,
                listOf(
                    ZmanAuthority.KOMARNO,
                    ZmanAuthority.SHACH,
                    ZmanAuthority.LECHEM_MISHNAH,
                    ZmanAuthority.YAAVETZ,
                    ZmanAuthority.SHVUS_YAAKOV,
                    ZmanAuthority.CHASSAN_SOFER,
                ),
            )
        ) {
            getTimeOffset(chatzos.momentOfOccurrence, -180 * MINUTE_MILLIS)
        }

    /**
     * This method returns the latest *zman krias shema* (time to recite Shema in the morning) according to the
     * opinion of the [Magen Avraham (MGA)](https://en.wikipedia.org/wiki/Avraham_Gombinern) based
     * on *alos* being [120][alos120] minutes or 1/6th of the day before [sunrise].
     * This time is 3 [*shaos zmaniyos*][shaahZmanis120Minutes] (solar hours) after [dawn][alos120] based on the opinion of the MGA that the day is calculated from a [dawn][alos120] of 120 minutes
     * before sunrise to [nightfall][tzais120] of 120 minutes after sunset. This returns the time of 3
     * [shaahZmanis120Minutes] after [dawn][alos120]. This is an extremely early *zman* that
     * is very much a *chumra*.
     *
     * @return the `Date` of the latest *zman krias shema*. If the calculation can't be computed such
     * as in the Arctic Circle where there is at least one day a year where the sun does not rise, and one where
     * it does not set, a null will be returned. See detailed explanation on top of the
     * [AstronomicalCalendar] documentation.
     * @see shaahZmanis120Minutes
     * @see alos120
     */
    val sofZmanShmaMGA120Minutes: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.SOF_ZMAN_KRIAS_SHEMA,
                ZmanCalculationMethod.DayDefinition.dawnToDusk(ZmanCalculationMethod.FixedDuration._120),
                UsesElevation.IF_SET,
            )
        ) {
            getSofZmanShma(alos120.momentOfOccurrence, tzais120.momentOfOccurrence)
        }

    /**
     * This method returns the latest *zman krias shema* (time to recite *Shema* in the morning) based
     * on the opinion that the day starts at [*alos* 16.1˚][alos16Point1Degrees] and ends at
     * [sea level sunset][seaLevelSunset].
     *
     * This is the opinion of the [חידושי וכללות הרז״ה](https://hebrewbooks.org/40357) and the [מנורה הטהורה](https://hebrewbooks.org/14799)
     * as mentioned by Yisrael Vehazmanim [vol 1, sec. 7, ch. 3 no. 16](https://hebrewbooks.org/pdfpager.aspx?req=9765&pgnum=81).
     *
     * Three *shaos zmaniyos* are calculated based on this day and added to [alos16Point1Degrees] to reach this time.
     * This time is 3 *shaos zmaniyos* (solar hours) after [dawn][alos16Point1Degrees] based on the opinion that
     * the day is calculated from a [alos16Point1Degrees] to [sea level sunset][seaLevelSunset].
     *
     * **Note:** Based on this calculation *chatzos* will not be at midday.
     *
     * @return the `Date` of the latest *zman krias shema* based on this day. If the calculation can't
     * be computed such as northern and southern locations even south of the Arctic Circle and north of the
     * Antarctic Circle where the sun may not reach low enough below the horizon for this calculation, a null
     * will be returned. See detailed explanation on top of the [AstronomicalCalendar] documentation.
     * @see alos16Point1Degrees
     * @see seaLevelSunset
     */
    val sofZmanShmaAlos16Point1ToSunset: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.SOF_ZMAN_KRIAS_SHEMA,
                ZmanCalculationMethod.DayDefinition.DAWN_16_1_TO_ELEVATION_ADJUSTED_SUNSET,
                UsesElevation.IF_SET,
                listOf(ZmanAuthority.RAZEH)
            )
        ) {
            getSofZmanShma(alos16Point1Degrees.momentOfOccurrence, elevationAdjustedSunset)
        }

    /**
     * This method returns the latest *zman krias shema* (time to recite Shema in the morning) based on the
     * opinion that the day starts at [*alos* 16.1˚][alos16Point1Degrees] and ends at
     * [*tzais* 7.083˚][tzaisGeonim7Point083Degrees].
     *
     * 3 *shaos zmaniyos* are calculated based on this day and added to [*alos*][alos16Point1Degrees] to reach this time.
     *
     * This time is 3 *shaos zmaniyos* (temporal hours) after [*alos* 16.1˚][alos16Point1Degrees] based on
     * the opinion that the day is calculated from a [*alos* 16.1˚][alos16Point1Degrees] to
     * [*tzais* 7.083˚][tzaisGeonim7Point083Degrees].
     *
     * **Note: ** Based on this calculation *chatzos* will not be at midday.
     *
     * @return the `Date` of the latest *zman krias shema* based on this calculation. If the
     * calculation can't be computed such as northern and southern locations even south of the Arctic Circle and
     * north of the Antarctic Circle where the sun may not reach low enough below the horizon for this
     * calculation, a null will be returned. See detailed explanation on top of the [AstronomicalCalendar]
     * documentation.
     * @see alos16Point1Degrees
     * @see tzaisGeonim7Point083Degrees
     */
    val sofZmanShmaAlos16Point1ToTzaisGeonim7Point083Degrees: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.SOF_ZMAN_KRIAS_SHEMA,
                ZmanCalculationMethod.DayDefinition.DAWN_16_1_TO_DUSK_7_083,
                UsesElevation.ALWAYS,
            )
        ) {
            getSofZmanShma(
                alos16Point1Degrees.momentOfOccurrence,
                tzaisGeonim7Point083Degrees.momentOfOccurrence
            )
        }

    /**
     * This method returns the latest *zman tfila* (time to recite the morning prayers) according to the opinion
     * of the [Magen Avraham (MGA)](https://en.wikipedia.org/wiki/Avraham_Gombinern) based on
     * *alos* being [19.8˚][alos19Point8Degrees] before [sunrise].
     *
     * This time is 4 [*shaos zmaniyos*][shaahZmanis19Point8Degrees] (solar hours) after [alos19Point8Degrees] based on the opinion of the MGA that the day is calculated from dawn to
     * nightfall with both being 19.8 below sunrise or sunset.
     *
     * This returns the time of 4 * [shaahZmanis19Point8Degrees] after [dawn][alos19Point8Degrees].
     *
     * @return the `Date` of the latest *zman krias shema*. If the calculation can't be computed such
     * as northern and southern locations even south of the Arctic Circle and north of the Antarctic Circle
     * where the sun may not reach low enough below the horizon for this calculation, a null will be returned.
     * See detailed explanation on top of the [AstronomicalCalendar] documentation.
     *
     * @see shaahZmanis19Point8Degrees
     * @see alos19Point8Degrees
     */
    val sofZmanTfilaMGA19Point8Degrees: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.SOF_ZMAN_TEFILLAH,
                ZmanCalculationMethod.DayDefinition.dawnToDusk(19.8F.degrees),
                UsesElevation.ALWAYS,
            )
        ) {
            getSofZmanTfila(
                alos19Point8Degrees.momentOfOccurrence, tzais19Point8Degrees.momentOfOccurrence
            )
        }

    /**
     * This method returns the latest *zman tfila* (time to recite the morning prayers) according to the opinion
     * of the [Magen Avraham (MGA)](https://en.wikipedia.org/wiki/Avraham_Gombinern) based on
     * *alos* being [16.1˚][alos16Point1Degrees] before [sunrise].
     *
     * This time is 4 [*shaos zmaniyos*][shaahZmanis16Point1Degrees] (solar hours) after [alos16Point1Degrees]
     * based on the opinion of the MGA that the day is calculated from dawn to nightfall,
     * with both being 16.1 below sunrise or sunset.
     *
     * This returns the time of 4 * [shaahZmanis16Point1Degrees] after [dawn][alos16Point1Degrees].
     *
     * @return the `Date` of the latest *zman krias shema*. If the calculation can't be computed such
     * as northern and southern locations even south of the Arctic Circle and north of the Antarctic Circle
     * where the sun may not reach low enough below the horizon for this calculation, a null will be returned.
     * See detailed explanation on top of the [AstronomicalCalendar] documentation.
     *
     * @see shaahZmanis16Point1Degrees
     * @see alos16Point1Degrees
     */
    val sofZmanTfilaMGA16Point1Degrees: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.SOF_ZMAN_TEFILLAH,
                ZmanCalculationMethod.DayDefinition.dawnToDusk(16.1F.degrees),
                UsesElevation.ALWAYS,
            )
        ) {
            getSofZmanTfila(
                alos16Point1Degrees.momentOfOccurrence, tzais16Point1Degrees.momentOfOccurrence
            )
        }

    /**
     * This method returns the latest *zman tfila* (time to recite the morning prayers) according to the opinion
     * of the [Magen Avraham (MGA)](https://en.wikipedia.org/wiki/Avraham_Gombinern) based on
     * *alos* being [18˚][alos18Degrees] before [sunrise]. This time is 4
     * [*shaos zmaniyos*][shaahZmanis18Degrees] (solar hours) after [dawn][alos18Degrees]
     * based on the opinion of the MGA that the day is calculated from dawn to nightfall with both being 18
     * below sunrise or sunset. This returns the time of 4 * [shaahZmanis18Degrees] after
     * [dawn][alos18Degrees].
     *
     * @return the `Date` of the latest *zman krias shema*. If the calculation can't be computed such
     * as northern and southern locations even south of the Arctic Circle and north of the Antarctic Circle
     * where the sun may not reach low enough below the horizon for this calculation, a null will be returned.
     * See detailed explanation on top of the [AstronomicalCalendar] documentation.
     *
     * @see shaahZmanis18Degrees
     * @see alos18Degrees
     */
    val sofZmanTfilaMGA18Degrees: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.SOF_ZMAN_TEFILLAH,
                ZmanCalculationMethod.DayDefinition.dawnToDusk(18F.degrees),
                UsesElevation.ALWAYS,
            )
        ) {
            getSofZmanTfila(alos18Degrees.momentOfOccurrence, tzais18Degrees.momentOfOccurrence)
        }

    /**
     * This method returns the latest *zman tfila* (time to recite the morning prayers) according to the opinion
     * of the [Magen Avraham (MGA)](https://en.wikipedia.org/wiki/Avraham_Gombinern) based on
     * *alos* being [72][alos72] minutes before [sunrise]. This time is 4
     * [*shaos zmaniyos*][shaahZmanis72Minutes] (solar hours) after [dawn][alos72] based on
     * the opinion of the MGA that the day is calculated from a [dawn][alos72] of 72 minutes before
     * sunrise to [nightfall][tzais72] of 72 minutes after sunset. This returns the time of 4 *
     * [shaahZmanis72Minutes] after [dawn][alos72]. This class returns an identical time to
     * [sofZmanTfilaMGA] and is repeated here for clarity.
     *
     * @return the `Date` of the latest *zman tfila*. If the calculation can't be computed such as in
     * the Arctic Circle where there is at least one day a year where the sun does not rise, and one where it
     * does not set, a null will be returned. See detailed explanation on top of the
     * [AstronomicalCalendar] documentation.
     * @see shaahZmanis72Minutes
     * @see alos72
     * @see sofZmanShmaMGA
     */
    val sofZmanTfilaMGA72Minutes: Zman.DateBased
        get() = sofZmanTfilaMGA

    /**
     * This method returns the latest *zman tfila* (time to the morning prayers) according to the opinion of the
     * [Magen Avraham (MGA)](https://en.wikipedia.org/wiki/Avraham_Gombinern) based on *alos*
     * being [72][alos72Zmanis] minutes *zmaniyos* before [sunrise]. This time is 4
     * [*shaos zmaniyos*][shaahZmanis72MinutesZmanis] (solar hours) after [dawn][alos72Zmanis]
     * based on the opinion of the MGA that the day is calculated from a [dawn][alos72Zmanis] of 72
     * minutes *zmaniyos* before sunrise to [nightfall][tzais72Zmanis] of 72 minutes *zmaniyos*
     * after sunset. This returns the time of 4 * [shaahZmanis72MinutesZmanis] after [dawn][alos72Zmanis].
     *
     * @return the `Date` of the latest *zman krias shema*. If the calculation can't be computed such
     * as in the Arctic Circle where there is at least one day a year where the sun does not rise,ZmanCalculationMethod.FixedDuration(72), and one where
     * it does not set, a null will be returned. See detailed explanation on top of the
     * [AstronomicalCalendar] documentation.
     * @see shaahZmanis72MinutesZmanis
     * @see alos72Zmanis
     */
    val sofZmanTfilaMGA72MinutesZmanis: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.SOF_ZMAN_TEFILLAH,
                ZmanCalculationMethod.DayDefinition.dawnToDusk(ZmanCalculationMethod.ZmaniyosDuration._72),
                UsesElevation.IF_SET,
            )
        ) {
            getSofZmanTfila(alos72Zmanis.momentOfOccurrence, tzais72Zmanis.momentOfOccurrence)
        }

    /**
     * This method returns the latest *zman tfila* (time to recite the morning prayers) according to the opinion
     * of the [Magen Avraham (MGA)](https://en.wikipedia.org/wiki/Avraham_Gombinern) based on
     * *alos* being [90][alos90] minutes before [sunrise]. This time is 4
     * [*shaos zmaniyos*][shaahZmanis90Minutes] (solar hours) after [dawn][alos90] based on
     * the opinion of the MGA that the day is calculated from a [dawn][alos90] of 90 minutes before sunrise to
     * [nightfall][tzais90] of 90 minutes after sunset. This returns the time of 4 *
     * [shaahZmanis90Minutes] after [dawn][alos90].
     *
     * @return the `Date` of the latest *zman tfila*. If the calculation can't be computed such as in
     * the Arctic Circle where there is at least one day a year where the sun does not rise, and one where it
     * does not set, a null will be returned. See detailed explanation on top of the
     * [AstronomicalCalendar] documentation.
     * @see shaahZmanis90Minutes
     * @see alos90
     */
    val sofZmanTfilaMGA90Minutes: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.SOF_ZMAN_TEFILLAH,
                ZmanCalculationMethod.DayDefinition.dawnToDusk(ZmanCalculationMethod.FixedDuration._90),
                UsesElevation.IF_SET,
            )
        ) {
            getSofZmanTfila(alos90.momentOfOccurrence, tzais90.momentOfOccurrence)
        }

    /**
     * This method returns the latest *zman tfila* (time to the morning prayers) according to the opinion of the
     * [Magen Avraham (MGA)](https://en.wikipedia.org/wiki/Avraham_Gombinern) based on *alos*
     * being [90][alos90Zmanis] minutes *zmaniyos* before [sunrise]. This time is
     * 4 [*shaos zmaniyos*][shaahZmanis90MinutesZmanis] (solar hours) after [dawn][alos90Zmanis] based on the opinion of the MGA that the day is calculated from a [dawn][alos90Zmanis]
     * of 90 minutes *zmaniyos* before sunrise to [nightfall][tzais90Zmanis] of 90 minutes
     * *zmaniyos* after sunset. This returns the time of 4 * [shaahZmanis90MinutesZmanis] after
     * [dawn][alos90Zmanis].
     *
     * @return the `Date` of the latest *zman krias shema*. If the calculation can't be computed such
     * as in the Arctic Circle where there is at least one day a year where the sun does not rise, and one where
     * it does not set, a null will be returned. See detailed explanation on top of the
     * [AstronomicalCalendar] documentation.
     * @see shaahZmanis90MinutesZmanis
     * @see alos90Zmanis
     */
    val sofZmanTfilaMGA90MinutesZmanis: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.SOF_ZMAN_TEFILLAH,
                ZmanCalculationMethod.DayDefinition.dawnToDusk(ZmanCalculationMethod.ZmaniyosDuration._90),
                UsesElevation.IF_SET,
            )
        ) {
            getSofZmanTfila(alos90Zmanis.momentOfOccurrence, tzais90Zmanis.momentOfOccurrence)
        }

    /**
     * This method returns the latest *zman tfila* (time to recite the morning prayers) according to the opinion
     * of the [Magen Avraham (MGA)](https://en.wikipedia.org/wiki/Avraham_Gombinern) based on
     * *alos* being [96][alos96] minutes before [sunrise]. This time is 4
     * [*shaos zmaniyos*][shaahZmanis96Minutes] (solar hours) after [dawn][alos96] based on
     * the opinion of the MGA that the day is calculated from a [dawn][alos96] of 96 minutes before
     * sunrise to [nightfall][tzais96] of 96 minutes after sunset. This returns the time of 4 *
     * [shaahZmanis96Minutes] after [dawn][alos96].
     *
     * @return the `Date` of the latest *zman tfila*. If the calculation can't be computed such as in
     * the Arctic Circle where there is at least one day a year where the sun does not rise, and one where it
     * does not set, a null will be returned. See detailed explanation on top of the
     * [AstronomicalCalendar] documentation.
     * @see shaahZmanis96Minutes
     * @see alos96
     */
    val sofZmanTfilaMGA96Minutes: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.SOF_ZMAN_TEFILLAH,
                ZmanCalculationMethod.DayDefinition.dawnToDusk(ZmanCalculationMethod.FixedDuration._96),
                UsesElevation.IF_SET,
            )
        ) {
            getSofZmanTfila(alos96.momentOfOccurrence, tzais96.momentOfOccurrence)
        }

    /**
     * This method returns the latest *zman tfila* (time to the morning prayers) according to the opinion of the
     * [Magen Avraham (MGA)](https://en.wikipedia.org/wiki/Avraham_Gombinern) based on *alos*
     * being [96][alos96Zmanis] minutes *zmaniyos* before [sunrise]. This time is
     * 4 [*shaos zmaniyos*][shaahZmanis96MinutesZmanis] (solar hours) after [dawn][alos96Zmanis] based on the opinion of the MGA that the day is calculated from a [dawn][alos96Zmanis]
     * of 96 minutes *zmaniyos* before sunrise to [nightfall][tzais96Zmanis] of 96 minutes
     * *zmaniyos* after sunset. This returns the time of 4 * [shaahZmanis96MinutesZmanis] after
     * [dawn][alos96Zmanis].
     *
     * @return the `Date` of the latest *zman krias shema*. If the calculation can't be computed such
     * as in the Arctic Circle where there is at least one day a year where the sun does not rise, and one where
     * it does not set, a null will be returned. See detailed explanation on top of the
     * [AstronomicalCalendar] documentation.
     * @see shaahZmanis90MinutesZmanis
     * @see alos90Zmanis
     */
    val sofZmanTfilaMGA96MinutesZmanis: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.SOF_ZMAN_TEFILLAH,
                ZmanCalculationMethod.DayDefinition.dawnToDusk(ZmanCalculationMethod.ZmaniyosDuration._96),
                UsesElevation.IF_SET,
            )
        ) {
            getSofZmanTfila(alos96Zmanis.momentOfOccurrence, tzais96Zmanis.momentOfOccurrence)
        }

    /**
     * This method returns the latest *zman tfila* (time to recite the morning prayers) according to the opinion
     * of the [Magen Avraham (MGA)](https://en.wikipedia.org/wiki/Avraham_Gombinern) based on
     * *alos* being [120][alos120] minutes before [sunrise] . This time is 4
     * [*shaos zmaniyos*][shaahZmanis120Minutes] (solar hours) after [dawn][alos120]
     * based on the opinion of the MGA that the day is calculated from a [dawn][alos120] of 120
     * minutes before sunrise to [nightfall][tzais120] of 120 minutes after sunset. This returns the time of
     * 4 * [shaahZmanis120Minutes] after [dawn][alos120]. This is an extremely early *zman*
     * that is very much a *chumra*.
     *
     * @return the `Date` of the latest *zman krias shema*. If the calculation can't be computed such
     * as in the Arctic Circle where there is at least one day a year where the sun does not rise, and one where
     * it does not set, a null will be returned. See detailed explanation on top of the
     * [AstronomicalCalendar] documentation.
     * @see shaahZmanis120Minutes
     * @see alos120
     */
    val sofZmanTfilaMGA120Minutes: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.SOF_ZMAN_TEFILLAH,
                ZmanCalculationMethod.DayDefinition.dawnToDusk(ZmanCalculationMethod.FixedDuration._120),
                UsesElevation.IF_SET,
            )
        ) {
            getSofZmanTfila(alos120.momentOfOccurrence, tzais120.momentOfOccurrence)
        }

    /**
     * This method returns the latest *zman tfila* (time to recite the morning prayers) calculated as 2 hours
     * before [ZmanimCalendar.getChatzos]. This is based on the opinions that calculate
     * *sof zman krias shema* as [sofZmanShma3HoursBeforeChatzos]. This returns the time of 2 hours
     * before [ZmanimCalendar.getChatzos].
     *
     * @return the `Date` of the latest *zman krias shema*. If the calculation can't be computed such
     * as in the Arctic Circle where there is at least one day a year where the sun does not rise, and one where
     * it does not set, a null will be returned. See detailed explanation on top of the
     * [AstronomicalCalendar] documentation.
     * @see ZmanimCalendar.getChatzos
     * @see sofZmanShma3HoursBeforeChatzos
     */
    val sofZmanTfila2HoursBeforeChatzos: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.SOF_ZMAN_TEFILLAH,
                ZmanCalculationMethod.Relationship(ZmanType.SOF_ZMAN_TEFILLAH occurs 2.hours before ZmanType.CHATZOS_HAYOM),
                UsesElevation.NEVER,
                listOf(
                    ZmanAuthority.KOMARNO,
                ),
            )
        ) {
            getTimeOffset(chatzos.momentOfOccurrence, -120 * MINUTE_MILLIS)
        }

    /**
     * This method returns *mincha gedola* calculated as 30 minutes after [*chatzos*][chatzos]
     * and not 1/2 of a [*shaah zmanis*][shaahZmanisGra] after [*chatzoschatzos] as
     * calculated by [minchaGedola].
     *
     * Some use this time to delay the start of *mincha* in the winter when 1/2 of a [*shaah zmanis*][shaahZmanisGra]
     * is less than 30 minutes.
     *
     * See [minchaGedolaGreaterThan30] for a convenience method that returns the later of the 2 calculations.
     *
     * One should not use this time to start *mincha* before the standard [*mincha gedola*][minchaGedola].
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
    val minchaGedola30Minutes: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.MINCHA_GEDOLAH,
                ZmanCalculationMethod.Relationship(ZmanType.MINCHA_GEDOLAH occurs 30.minutes after ZmanType.CHATZOS_HAYOM),
                UsesElevation.NEVER,
            ),
        ) {
            getTimeOffset(chatzos.momentOfOccurrence, MINUTE_MILLIS * 30)
        }

    /**
     * This method returns the time of *mincha gedola* according to the Magen Avraham with the day starting 72
     * minutes before sunrise and ending 72 minutes after sunset. This is the earliest time to pray *mincha*. For
     * more information on this see the documentation on [*mincha gedola*][.getMinchaGedola]. This is
     * calculated as 6.5 [solar hours][temporalHour] after *alos*. The calculation used is 6.5 *
     * [shaahZmanis72Minutes] after [*alos*][alos72].
     *
     * @see alos72
     * @see .getMinchaGedola
     * @see .getMinchaKetana
     * @see ZmanimCalendar.getMinchaGedola
     * @return the `Date` of the time of *mincha gedola*. If the calculation can't be computed such as
     * in the Arctic Circle where there is at least one day a year where the sun does not rise, and one where it
     * does not set, a null will be returned. See detailed explanation on top of the [AstronomicalCalendar]
     * documentation.
     */
    val minchaGedola72Minutes: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.MINCHA_GEDOLAH,
                ZmanCalculationMethod.DayDefinition.dawnToDusk(ZmanCalculationMethod.FixedDuration._72),
                UsesElevation.IF_SET,
            )
        ) {
            getMinchaGedola(alos72.momentOfOccurrence, tzais72.momentOfOccurrence)
        }

    /**
     * This method returns the time of *mincha gedola* according to the Magen Avraham with the day starting and
     * ending 16.1 below the horizon. This is the earliest time to pray *mincha*. For more information on
     * this see the documentation on [*mincha gedola*][.getMinchaGedola]. This is calculated as 6.5
     * [solar hours][temporalHour] after *alos*. The calculation used is 6.5 *
     * [shaahZmanis16Point1Degrees] after [*alos*][alos16Point1Degrees].
     *
     * @see shaahZmanis16Point1Degrees
     * @see .getMinchaGedola
     * @see .getMinchaKetana
     * @return the `Date` of the time of *mincha gedola*. If the calculation can't be computed such as
     * northern and southern locations even south of the Arctic Circle and north of the Antarctic Circle where
     * the sun  may not reach low enough below the horizon for this calculation, a null will be returned. See
     * detailed explanation on top of the [AstronomicalCalendar] documentation.
     */
    val minchaGedola16Point1Degrees: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.MINCHA_GEDOLAH,
                ZmanCalculationMethod.DayDefinition.dawnToDusk(16.1F.degrees),
                UsesElevation.ALWAYS,
            )
        ) {
            getMinchaGedola(
                alos16Point1Degrees.momentOfOccurrence, tzais16Point1Degrees.momentOfOccurrence
            )
        }

    /**
     * This method returns the time of *mincha gedola* based on the opinion of [Rabbi Yaakov Moshe Hillel](https://en.wikipedia.org/wiki/Yaakov_Moshe_Hillel) as published in the *luach*
     * of the Bais Horaah of Yeshivat Chevrat Ahavat Shalom.
     *
     * It will return the later time of the following two options:
     * - half a *shaah zmanis* after *chatzos*
     *     - with *shaos zmaniyos* calculated based on:
     *          - a day starting 72 minutes before sunrise [*alos* 16.1˚][alos16Point1Degrees] and
     *          - ending 13.5 minutes after sunset [tzaisGeonim3Point7Degrees].
     * - 30 clock minutes after *chatzos*
     *
     * See [minchaGedolaGreaterThan30] (though that calculation is based on *mincha gedola* GRA).
     * *Mincha gedola* is the earliest time to pray *mincha*. For more information about *mincha gedola* see the documentation on [*mincha gedola*][minchaGedola].
     *
     * @return the `Date` of the *mincha gedola*. If the calculation can't be computed such as northern and
     * southern locations even south of the Arctic Circle and north of the Antarctic Circle where the sun may not
     * reach low enough below the horizon for this calculation, a null will be returned. See detailed explanation
     * on top of the [AstronomicalCalendar] documentation.
     *
     * @see alos16Point1Degrees
     * @see tzaisGeonim3Point7Degrees
     * @see shaahZmanisAlos16Point1ToTzais3Point7
     * @see minchaGedolaGreaterThan30
     */
    val minchaGedolaAhavatShalom: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.MINCHA_GEDOLAH,
                ZmanCalculationMethod.LaterOf(
                    minchaGedola30Minutes.definition,
                    shaahZmanisAlos16Point1ToTzais3Point7.definition.copy(
                        type = ZmanType.MINCHA_GEDOLAH,
                        calculationMethod = ZmanCalculationMethod.Relationship(
                            ZmanType.MINCHA_GEDOLAH occurs 30.minutes.zmaniyos after ZmanType.CHATZOS_HAYOM
                        )
                    ),
                ),
                UsesElevation.ALWAYS,
                supportingAuthorities = listOf(ZmanAuthority.AHAVAT_SHALOM),
            )
        ) {
            maxOf(
                minchaGedola30Minutes.momentOfOccurrence,/*minchaGedola30MinutesZmanis.momentOfOccurence = */
                getTimeOffset(
                    chatzos.momentOfOccurrence,
                    shaahZmanisAlos16Point1ToTzais3Point7.duration.div(2).inWholeMilliseconds
                )
            )
        }

    /**
     * This is a convenience method that returns the later of [minchaGedola] and
     * [minchaGedola30Minutes]. In the winter when 1/2 of a [*shaah zmanis*][shaahZmanisGra] is
     * less than 30 minutes [minchaGedola30Minutes] will be returned, otherwise [minchaGedola]
     * will be returned.
     *
     * @return the `Date` of the later of [.getMinchaGedola] and [.getMinchaGedola30Minutes].
     * If the calculation can't be computed such as in the Arctic Circle where there is at least one day a year
     * where the sun does not rise, and one where it does not set, a null will be returned. See detailed
     * explanation on top of the [AstronomicalCalendar] documentation.
     */
    val minchaGedolaGreaterThan30: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.MINCHA_GEDOLAH, ZmanCalculationMethod.LaterOf(
                    minchaGedola30Minutes.definition,
                    minchaGedola.definition,
                ), UsesElevation.IF_SET
            )
        ) {
            maxOf(minchaGedola30Minutes.momentOfOccurrence, minchaGedola.momentOfOccurrence)
        }

    /**
     * This method returns the time of *mincha ketana* according to the Magen Avraham with the day starting and
     * ending 16.1 below the horizon. This is the preferred earliest time to pray *mincha* according to the
     * opinion of the [Rambam](https://en.wikipedia.org/wiki/Maimonides) and others. For more information on
     * this see the documentation on [*mincha gedola*][.getMinchaGedola]. This is calculated as 9.5
     * [solar hours][temporalHour] after *alos*. The calculation used is 9.5 *
     * [shaahZmanis16Point1Degrees] after [*alos*][alos16Point1Degrees].
     *
     * @see shaahZmanis16Point1Degrees
     * @see .getMinchaGedola
     * @see .getMinchaKetana
     * @return the `Date` of the time of *mincha ketana*. If the calculation can't be computed such
     * as northern and southern locations even south of the Arctic Circle and north of the Antarctic Circle
     * where the sun may not reach low enough below the horizon for this calculation, a null will be returned.
     * See detailed explanation on top of the [AstronomicalCalendar] documentation.
     */
    val minchaKetana16Point1Degrees: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.MINCHA_KETANAH,
                ZmanCalculationMethod.DayDefinition.dawnToDusk(16.1F.degrees),
                UsesElevation.ALWAYS,
            )
        ) {
            getMinchaKetana(
                alos16Point1Degrees.momentOfOccurrence, tzais16Point1Degrees.momentOfOccurrence
            )
        }

    /**
     * This method returns the time of *mincha ketana* based on the opinion of [Rabbi Yaakov Moshe Hillel](https://en.wikipedia.org/wiki/Yaakov_Moshe_Hillel) as published in the *luach*
     * of the Bais Horaah of Yeshivat Chevrat Ahavat Shalom that *mincha ketana* is calculated as 2.5 *shaos
     * zmaniyos* before [*tzais* 3.8˚][tzaisGeonim3Point8Degrees] with *shaos zmaniyos*
     * calculated based on a day starting at [*alos* 16.1˚][alos16Point1Degrees] and ending at
     * *tzais* 3.8. *Mincha ketana* is the preferred earliest time to pray *mincha* according to
     * the opinion of the [Rambam](https://en.wikipedia.org/wiki/Maimonides) and others. For more information
     * on this see the documentation on [*mincha ketana*][.getMinchaKetana].
     *
     * @return the the `Date` of the time of *mincha ketana*. If the calculation can't be computed such as northern
     * and southern locations even south of the Arctic Circle and north of the Antarctic Circle where the sun may not
     * reach low enough below the horizon for this calculation, a null will be returned. See detailed explanation
     * on top of the [AstronomicalCalendar] documentation.
     *
     * @see shaahZmanisAlos16Point1ToTzais3Point8
     * @see .getMinchaGedolaAhavatShalom
     * @see .getPlagAhavatShalom
     */
    val minchaKetanaAhavatShalom: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.MINCHA_KETANAH,
                ZmanCalculationMethod.DayDefinition.DAWN_16_1_TO_DUSK_3_8,
                UsesElevation.ALWAYS,
                listOf(
                    ZmanAuthority.AHAVAT_SHALOM,
                ),
            ) // TODO is this relationship just a day definition? ZmanType.MINCHA_KETANAH occurs 2.5.hours.zmaniyos before ZmanType.TZAIS

        ) {
            getTimeOffset(
                tzaisGeonim3Point8Degrees.momentOfOccurrence,
                (shaahZmanisAlos16Point1ToTzais3Point8.duration * -2.5).inWholeMilliseconds
            )
        }

    /**
     * This method returns the time of *mincha ketana* according to the Magen Avraham with the day
     * starting 72 minutes before sunrise and ending 72 minutes after sunset. This is the preferred earliest time to pray
     * *mincha* according to the opinion of the [Rambam](https://en.wikipedia.org/wiki/Maimonides)
     * and others. For more information on this see the documentation on [*mincha gedola*][.getMinchaGedola].
     * This is calculated as 9.5 [shaahZmanis72Minutes] after *alos*. The calculation used is 9.5 *
     * [shaahZmanis72Minutes] after [*alos*][alos72].
     *
     * @see shaahZmanis16Point1Degrees
     * @see .getMinchaGedola
     * @see .getMinchaKetana
     * @return the `Date` of the time of *mincha ketana*. If the calculation can't be computed such as
     * in the Arctic Circle where there is at least one day a year where the sun does not rise, and one where it
     * does not set, a null will be returned. See detailed explanation on top of the [AstronomicalCalendar]
     * documentation.
     */
    val minchaKetana72Minutes: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.MINCHA_KETANAH,
                ZmanCalculationMethod.DayDefinition.dawnToDusk(ZmanCalculationMethod.FixedDuration._72),
                UsesElevation.IF_SET,
            )
        ) {
            getMinchaKetana(alos72.momentOfOccurrence, tzais72.momentOfOccurrence)
        }

    /**
     * This method returns the time of *plag hamincha* according to the Magen Avraham with the day starting 60
     * minutes before sunrise and ending 60 minutes after sunset. This is calculated as 10.75 hours after
     * [dawn][alos60]. The formula used is 10.75 [shaahZmanis60Minutes] after [alos60].
     *
     * @return the `Date` of the time of *plag hamincha*. If the calculation can't be computed such as
     * in the Arctic Circle where there is at least one day a year where the sun does not rise, and one where it
     * does not set, a null will be returned. See detailed explanation on top of the
     * [AstronomicalCalendar] documentation.
     *
     * @see shaahZmanis60Minutes
     * @see alos60
     * @see tzais60
     */
    val plagHamincha60Minutes: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.PLAG_HAMINCHA,
                ZmanCalculationMethod.DayDefinition.dawnToDusk(60.minutes.fixed),
                UsesElevation.IF_SET,
            )
        ) {
            getPlagHamincha(alos60.momentOfOccurrence, tzais60.momentOfOccurrence)
        }

    /**
     * This method should be used *lechumra* only and returns the time of *plag hamincha* according to the
     * Magen Avraham with the day starting 72 minutes before sunrise and ending 72 minutes after sunset. This is calculated
     * as 10.75 hours after [dawn][alos72]. The formula used is 10.75 [shaahZmanis72Minutes] after
     * [alos72]. Since *plag* by this calculation can occur after sunset, it should only be used
     * *lechumra*.
     *
     * @return the `Date` of the time of *plag hamincha*. If the calculation can't be computed such as
     * in the Arctic Circle where there is at least one day a year where the sun does not rise, and one where it
     * does not set, a null will be returned. See detailed explanation on top of the
     * [AstronomicalCalendar] documentation.
     *
     * @see shaahZmanis72Minutes
     */
    @Deprecated("This method should be used <em>lechumra</em> only since it returns a very late time, and if used\n" + "	          <em>lekula</em> can result in <em>chillul Shabbos</em> etc. There is no current plan to remove this\n" + "	          method from the API, and this deprecation is intended to alert developers of the danger of using it.\n" + "	  \n" + "	  ")  // (forRemoval=false) // add back once Java 9 is the minimum supported version
    val plagHamincha72Minutes: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.PLAG_HAMINCHA,
                ZmanCalculationMethod.DayDefinition.dawnToDusk(ZmanCalculationMethod.FixedDuration._72),
                UsesElevation.IF_SET,
            )
        ) {
            getPlagHamincha(alos72.momentOfOccurrence, tzais72.momentOfOccurrence)
        }

    /**
     * This method should be used *lechumra* only and returns the time of *plag hamincha* according to the
     * Magen Avraham with the day starting 90 minutes before sunrise and ending 90 minutes after sunset. This is calculated
     * as 10.75 hours after [dawn][alos90]. The formula used is 10.75 [shaahZmanis90Minutes] after
     * [alos90]. Since *plag* by this calculation can occur after sunset, it should only be used
     * *lechumra*.
     *
     * @return the `Date` of the time of *plag hamincha*. If the calculation can't be computed such as
     * in the Arctic Circle where there is at least one day a year where the sun does not rise, and one where it
     * does not set, a null will be returned. See detailed explanation on top of the
     * [AstronomicalCalendar] documentation.
     *
     * @see shaahZmanis90Minutes
     */
    @Deprecated("This method should be used <em>lechumra</em> only since it returns a very late time, and if used\n" + "	          <em>lekula</em> can result in <em>chillul Shabbos</em> etc. There is no current plan to remove this\n" + "	          method from the API, and this deprecation is intended to alert developers of the danger of using it.\n" + "	  \n" + "	  ")  // (forRemoval=false) // add back once Java 9 is the minimum supported version
    val plagHamincha90Minutes: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.PLAG_HAMINCHA,
                ZmanCalculationMethod.DayDefinition.dawnToDusk(ZmanCalculationMethod.FixedDuration._90),
                UsesElevation.IF_SET,
            )
        ) {
            getPlagHamincha(alos90.momentOfOccurrence, tzais90.momentOfOccurrence)
        }

    /**
     * This method should be used *lechumra* only and returns the time of *plag hamincha* according to the Magen
     * Avraham with the day starting 96 minutes before sunrise and ending 96 minutes after sunset. This is calculated as 10.75
     * hours after [dawn][alos96]. The formula used is 10.75 [shaahZmanis96Minutes] after
     * [alos96]. Since *plag* by this calculation can occur after sunset, it should only be used
     * *lechumra*.
     *
     * @return the `Date` of the time of *plag hamincha*. If the calculation can't be computed such as
     * in the Arctic Circle where there is at least one day a year where the sun does not rise, and one where it
     * does not set, a null will be returned. See detailed explanation on top of the
     * [AstronomicalCalendar] documentation.
     * @see shaahZmanis96Minutes
     */
    @Deprecated("This method should be used <em>lechumra</em> only since it returns a very late time, and if used\n" + "	          <em>lekula</em> can result in <em>chillul Shabbos</em> etc. There is no current plan to remove this\n" + "	          method from the API, and this deprecation is intended to alert developers of the danger of using it.\n" + "	  \n" + "	  ")  // (forRemoval=false) // add back once Java 9 is the minimum supported version
    val plagHamincha96Minutes: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.PLAG_HAMINCHA,
                ZmanCalculationMethod.DayDefinition.dawnToDusk(ZmanCalculationMethod.FixedDuration._96),
                UsesElevation.IF_SET,
            )
        ) {
            getPlagHamincha(alos96.momentOfOccurrence, tzais96.momentOfOccurrence)
        }

    /**
     * This method should be used *lechumra* only and returns the time of *plag hamincha*. This is calculated
     * as 10.75 hours after [dawn][alos96Zmanis]. The formula used is 10.75 * [shaahZmanis96MinutesZmanis] after [dawn][alos96Zmanis]. Since *plag* by this calculation can
     * occur after sunset, it should only be used *lechumra*.
     *
     * @return the `Date` of the time of *plag hamincha*. If the calculation can't be computed such as
     * in the Arctic Circle where there is at least one day a year where the sun does not rise, and one where it
     * does not set, a null will be returned. See detailed explanation on top of the
     * [AstronomicalCalendar] documentation.
     */
    @Deprecated("This method should be used <em>lechumra</em> only since it returns a very late time, and if used\n" + "	          <em>lekula</em> can result in <em>chillul Shabbos</em> etc. There is no current plan to remove this\n" + "	          method from the API, and this deprecation is intended to alert developers of the danger of using it.\n" + "	  \n" + "	  ")  // (forRemoval=false) // add back once Java 9 is the minimum supported version
    val plagHamincha96MinutesZmanis: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.PLAG_HAMINCHA,
                ZmanCalculationMethod.DayDefinition.dawnToDusk(ZmanCalculationMethod.ZmaniyosDuration._96),
                UsesElevation.IF_SET,
            )
        ) {
            getPlagHamincha(alos96Zmanis.momentOfOccurrence, tzais96Zmanis.momentOfOccurrence)
        }

    /**
     * This method should be used *lechumra* only and returns the time of *plag hamincha*. This is calculated
     * as 10.75 hours after [dawn][alos90Zmanis]. The formula used is 10.75 * [shaahZmanis90MinutesZmanis] after [dawn][alos90Zmanis]. Since *plag* by this calculation can
     * occur after sunset, it should only be used *lechumra*.
     *
     * @return the `Date` of the time of *plag hamincha*. If the calculation can't be computed such as
     * in the Arctic Circle where there is at least one day a year where the sun does not rise, and one where it
     * does not set, a null will be returned. See detailed explanation on top of the
     * [AstronomicalCalendar] documentation.
     */
    @Deprecated("This method should be used <em>lechumra</em> only since it returns a very late time, and if used\n" + "	          <em>lekula</em> can result in <em>chillul Shabbos</em> etc. There is no current plan to remove this\n" + "	          method from the API, and this deprecation is intended to alert developers of the danger of using it.\n" + "	  \n" + "	  ")  // (forRemoval=false) // add back once Java 9 is the minimum supported version
    val plagHamincha90MinutesZmanis: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.PLAG_HAMINCHA,
                ZmanCalculationMethod.DayDefinition.dawnToDusk(ZmanCalculationMethod.ZmaniyosDuration._90),
                UsesElevation.IF_SET,
            )
        ) {
            getPlagHamincha(alos90Zmanis.momentOfOccurrence, tzais90Zmanis.momentOfOccurrence)
        }

    /**
     * This method should be used *lechumra* only and returns the time of *plag hamincha*. This is calculated as
     * 10.75 hours after [alos72Zmanis]. The formula used is 10.75 * [shaahZmanis72MinutesZmanis] after
     * [dawn][alos72Zmanis]. Since *plag* by this calculation can occur after sunset, it should only be used
     * *lechumra*.
     *
     * @return the `Date` of the time of *plag hamincha*. If the calculation can't be computed such as
     * in the Arctic Circle where there is at least one day a year where the sun does not rise, and one where it
     * does not set, a null will be returned. See detailed explanation on top of the
     * [AstronomicalCalendar] documentation.
     */
    @Deprecated("This method should be used <em>lechumra</em> only since it returns a very late time, and if used\n" + "	          <em>lekula</em> can result in <em>chillul Shabbos</em> etc. There is no current plan to remove this\n" + "	          method from the API, and this deprecation is intended to alert developers of the danger of using it.\n" + "	  \n" + "	  ")  // (forRemoval=false) // add back once Java 9 is the minimum supported version
    val plagHamincha72MinutesZmanis: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.PLAG_HAMINCHA,
                ZmanCalculationMethod.DayDefinition.dawnToDusk(ZmanCalculationMethod.ZmaniyosDuration._72),
                UsesElevation.IF_SET,
            )
        ) {
            getPlagHamincha(alos72Zmanis.momentOfOccurrence, tzais72Zmanis.momentOfOccurrence)
        }

    /**
     * This method should be used *lechumra* only and returns the time of *plag hamincha* based on the
     * opinion that the day starts at [*alos* 16.1˚][alos16Point1Degrees] and ends at [][tzais16Point1Degrees]. This is calculated as 10.75 hours *zmaniyos*
     * after [dawn][alos16Point1Degrees]. The formula used is 10.75 * [shaahZmanis16Point1Degrees]
     * after [alos16Point1Degrees]. Since *plag* by this calculation can occur after sunset, it
     * should only be used *lechumra*.
     *
     * @return the `Date` of the time of *plag hamincha*. If the calculation can't be computed such as
     * northern and southern locations even south of the Arctic Circle and north of the Antarctic Circle where
     * the sun may not reach low enough below the horizon for this calculation, a null will be returned. See
     * detailed explanation on top of the [AstronomicalCalendar] documentation.
     *
     * @see shaahZmanis16Point1Degrees
     */
    @Deprecated("This method should be used <em>lechumra</em> only since it returns a very late time, and if used\n" + "	          <em>lekula</em> can result in <em>chillul Shabbos</em> etc. There is no current plan to remove this\n" + "	          method from the API, and this deprecation is intended to alert developers of the danger of using it.\n" + "	  \n" + "	  ")  // (forRemoval=false) // add back once Java 9 is the minimum supported version
    val plagHamincha16Point1Degrees: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.PLAG_HAMINCHA,
                ZmanCalculationMethod.DayDefinition.dawnToDusk(16.1F.degrees),
                UsesElevation.ALWAYS,
            )
        ) {
            getPlagHamincha(
                alos16Point1Degrees.momentOfOccurrence, tzais16Point1Degrees.momentOfOccurrence
            )
        }

    /**
     * This method should be used *lechumra* only and returns the time of *plag hamincha* based on the
     * opinion that the day starts at [*alos* 19.8˚][alos19Point8Degrees] and ends at [tzais19Point8Degrees]. This is calculated as 10.75 hours *zmaniyos*
     * after [dawn][alos19Point8Degrees]. The formula used is 10.75 * [shaahZmanis19Point8Degrees] after [alos19Point8Degrees]. Since *plag* by this
     * calculation can occur after sunset, it should only be used *lechumra*.
     *
     * @return the `Date` of the time of *plag hamincha*. If the calculation can't be computed such as
     * northern and southern locations even south of the Arctic Circle and north of the Antarctic Circle where
     * the sun may not reach low enough below the horizon for this calculation, a null will be returned. See
     * detailed explanation on top of the [AstronomicalCalendar] documentation.
     *
     * @see shaahZmanis19Point8Degrees
     */
    @Deprecated("This method should be used <em>lechumra</em> only since it returns a very late time, and if used\n" + "	          <em>lekula</em> can result in <em>chillul Shabbos</em> etc. There is no current plan to remove this\n" + "	          method from the API, and this deprecation is intended to alert developers of the danger of using it.\n" + "	  \n" + "	  ")  // (forRemoval=false) // add back once Java 9 is the minimum supported version
    val plagHamincha19Point8Degrees: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.PLAG_HAMINCHA,
                ZmanCalculationMethod.DayDefinition.dawnToDusk(19.8F.degrees),
                UsesElevation.ALWAYS,
            )
        ) {
            getPlagHamincha(
                alos19Point8Degrees.momentOfOccurrence, tzais19Point8Degrees.momentOfOccurrence
            )
        }

    /**
     * This method should be used *lechumra* only and returns the time of *plag hamincha* based on the
     * opinion that the day starts at [*alos* 26˚][alos26Degrees] and ends at [tzais26Degrees]. This is calculated as 10.75 hours *zmaniyos* after [][alos26Degrees]. The formula used is 10.75 * [shaahZmanis26Degrees] after [][alos26Degrees]. Since the *zman* based on an extremely early *alos* and a very late
     * *tzais*, it should only be used *lechumra*.
     *
     * @return the `Date` of the time of *plag hamincha*. If the calculation can't be computed such as
     * northern and southern locations even south of the Arctic Circle and north of the Antarctic Circle where
     * the sun may not reach low enough below the horizon for this calculation, a null will be returned. See
     * detailed explanation on top of the [AstronomicalCalendar] documentation.
     *
     * @see shaahZmanis26Degrees
     * @see .getPlagHamincha120Minutes
     */
    @Deprecated("This method should be used <em>lechumra</em> only since it returns a very late time, and if used\n" + "	          <em>lekula</em> can result in <em>chillul Shabbos</em> etc. There is no current plan to remove this\n" + "	          method from the API, and this deprecation is intended to alert developers of the danger of using it.\n" + "	  \n" + "	  ")  // (forRemoval=false) // add back once Java 9 is the minimum supported version
    val plagHamincha26Degrees: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.PLAG_HAMINCHA,
                ZmanCalculationMethod.DayDefinition.dawnToDusk(26F.degrees),
                UsesElevation.ALWAYS,
            )
        ) {
            getPlagHamincha(alos26Degrees.momentOfOccurrence, tzais26Degrees.momentOfOccurrence)
        }

    /**
     * This method should be used *lechumra* only and returns the time of *plag hamincha* based on the
     * opinion that the day starts at [*alos* 18˚][alos18Degrees] and ends at [tzais18Degrees]. This is calculated as 10.75 hours *zmaniyos* after [][alos18Degrees]. The formula used is 10.75 * [shaahZmanis18Degrees] after [][alos18Degrees]. Since *plag* by this calculation can occur after sunset, it should only be used
     * *lechumra*.
     *
     * @return the `Date` of the time of *plag hamincha*. If the calculation can't be computed such as
     * northern and southern locations even south of the Arctic Circle and north of the Antarctic Circle where
     * the sun may not reach low enough below the horizon for this calculation, a null will be returned. See
     * detailed explanation on top of the [AstronomicalCalendar] documentation.
     *
     * @see shaahZmanis18Degrees
     */
    @Deprecated("This method should be used <em>lechumra</em> only since it returns a very late time, and if used\n" + "	          <em>lekula</em> can result in <em>chillul Shabbos</em> etc. There is no current plan to remove this\n" + "	          method from the API, and this deprecation is intended to alert developers of the danger of using it.\n" + "	  \n" + "	  ")  // (forRemoval=false) // add back once Java 9 is the minimum supported version
    val plagHamincha18Degrees: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.PLAG_HAMINCHA,
                ZmanCalculationMethod.DayDefinition.dawnToDusk(18F.degrees),
                UsesElevation.ALWAYS,
            )
        ) {
            getPlagHamincha(alos18Degrees.momentOfOccurrence, tzais18Degrees.momentOfOccurrence)
        }

    /**
     * This method should be used *lechumra* only and returns the time of *plag hamincha* based on the opinion
     * that the day starts at [*alos* 16.1˚][alos16Point1Degrees] and ends at [sunset].
     * 10.75 *shaos zmaniyos* are calculated based on this day and added to [*alos*][alos16Point1Degrees] to reach this time. This time is 10.75 *shaos zmaniyos* (temporal hours) after [alos16Point1Degrees] based on the opinion that the day is calculated from a [dawn][alos16Point1Degrees] of 16.1 degrees before sunrise to [sea level sunset][seaLevelSunset]. This returns the time of 10.75 *
     * the calculated *shaah zmanis* after [dawn][alos16Point1Degrees]. Since *plag* by this
     * calculation can occur after sunset, it should only be used *lechumra*.
     *
     * @return the `Date` of the *plag*. If the calculation can't be computed such as northern and southern
     * locations even south of the Arctic Circle and north of the Antarctic Circle where the sun may not reach
     * low enough below the horizon for this calculation, a null will be returned. See detailed explanation on
     * top of the [AstronomicalCalendar] documentation.
     *
     * @see alos16Point1Degrees
     * @see .seaLevelSunset
     */
    @Deprecated("This method should be used <em>lechumra</em> only since it returns a very late time, and if used\n" + "	          <em>lekula</em> can result in <em>chillul Shabbos</em> etc. There is no current plan to remove this\n" + "	          method from the API, and this deprecation is intended to alert developers of the danger of using it.\n" + "	  \n" + "	  ")  // (forRemoval=false) // add back once Java 9 is the minimum supported version
    val plagAlosToSunset: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.PLAG_HAMINCHA,
                ZmanCalculationMethod.DayDefinition.DAWN_16_1_TO_ELEVATION_ADJUSTED_SUNSET,
                UsesElevation.ALWAYS,
            )
        ) {
            getPlagHamincha(alos16Point1Degrees.momentOfOccurrence, elevationAdjustedSunset)
        }

    /**
     * This method returns the time of *plag hamincha* based on the opinion that the day starts at
     * [*alos* 16.1˚][alos16Point1Degrees] and ends at [*tzais*][tzaisGeonim7Point083Degrees]. 10.75 *shaos zmaniyos* are calculated based on this day and added to [][alos16Point1Degrees] to reach this time. This time is 10.75 *shaos zmaniyos* (temporal
     * hours) after [dawn][alos16Point1Degrees] based on the opinion that the day is calculated from a
     * [dawn][alos16Point1Degrees] of 16.1 degrees before sunrise to
     * [*tzais*][tzaisGeonim7Point083Degrees] . This returns the time of 10.75 * the calculated
     * *shaah zmanis* after [dawn][alos16Point1Degrees].
     *
     * @return the `Date` of the *plag*. If the calculation can't be computed such as northern and
     * southern locations even south of the Arctic Circle and north of the Antarctic Circle where the sun may not
     * reach low enough below the horizon for this calculation, a null will be returned. See detailed explanation
     * on top of the [AstronomicalCalendar] documentation.
     *
     * @see alos16Point1Degrees
     * @see tzaisGeonim7Point083Degrees
     */
    val plagAlos16Point1ToTzaisGeonim7Point083Degrees: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.PLAG_HAMINCHA,
                ZmanCalculationMethod.DayDefinition.DAWN_16_1_TO_DUSK_7_083,
                UsesElevation.ALWAYS,
            )
        ) {
            getPlagHamincha(
                alos16Point1Degrees.momentOfOccurrence,
                tzaisGeonim7Point083Degrees.momentOfOccurrence
            )
        }

    /**
     * This method returns the time of *plag hamincha* (the earliest time that Shabbos can be started) based on the
     * opinion of [Rabbi Yaakov Moshe Hillel](https://en.wikipedia.org/wiki/Yaakov_Moshe_Hillel) as published in
     * the *luach* of the Bais Horaah of Yeshivat Chevrat Ahavat Shalom that that *plag hamincha* is calculated
     * as 1.25 *shaos zmaniyos* before [*tzais* 3.8˚][tzaisGeonim3Point8Degrees] with *shaos
     * zmaniyos* calculated based on a day starting at [*alos* 16.1˚][alos16Point1Degrees] and
     * ending at *tzais* 3.8.
     *
     * @return the `Date` of the *plag*. If the calculation can't be computed such as northern and
     * southern locations even south of the Arctic Circle and north of the Antarctic Circle where the sun may not
     * reach low enough below the horizon for this calculation, a null will be returned. See detailed explanation
     * on top of the [AstronomicalCalendar] documentation.
     *
     * @see shaahZmanisAlos16Point1ToTzais3Point8
     * @see .getMinchaGedolaAhavatShalom
     * @see .getMinchaKetanaAhavatShalom
     */
    val plagAhavatShalom: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.PLAG_HAMINCHA,
                ZmanCalculationMethod.DayDefinition.DAWN_16_1_TO_DUSK_3_8,
                UsesElevation.ALWAYS,
                listOf(
                    ZmanAuthority.AHAVAT_SHALOM
                )
            )
        ) {
            getTimeOffset(
                tzaisGeonim3Point8Degrees.momentOfOccurrence,
                (shaahZmanisAlos16Point1ToTzais3Point8.duration * -1.25).inWholeMilliseconds
            )
        }

    /**
     * Method to return the beginning of *bain hashmashos* of Rabbeinu Tam calculated when the sun is
     * [13.24˚][ZENITH_13_POINT_24] below the western [geometric horizon][AstronomicalCalendar.GEOMETRIC_ZENITH] (90)
     * after sunset. This calculation is based on the same calculation of [*bain hashmashos* Rabbeinu Tam 58.5 minutes][bainHashmashosRT58Point5Minutes] but uses a degree-based calculation instead of 58.5 exact
     * minutes. This calculation is based on the position of the sun 58.5 minutes after sunset in Jerusalem [around the equinox / equilux](https://kosherjava.com/2022/01/12/equinox-vs-equilux-zmanim-calculations/),
     * which calculates to 13.24 below [geometric zenith][AstronomicalCalendar.GEOMETRIC_ZENITH].
     * NOTE: As per Yisrael Vehazmanim Vol. III page 1028, No. 50, a dip of slightly less than 13 should be used.
     * Calculations show that the proper dip to be 13.2456 (truncated to 13.24 that provides about 1.5 second
     * earlier (*lechumra*) time) below the horizon at that time. This makes a difference of 1 minute and 10
     * seconds in Jerusalem during the Equinox, and 1 minute 29 seconds during the solstice as compared to the proper
     * 13.24 versus 13. For NY during the solstice, the difference is 1 minute 56 seconds.
     * @todo recalculate the above based on equilux/equinox calculations.
     *
     * @return the `Date` of the sun being 13.24 below [geometric zenith][AstronomicalCalendar.GEOMETRIC_ZENITH]
     * (90). If the calculation can't be computed such as northern and southern locations even south of the
     * Arctic Circle and north of the Antarctic Circle where the sun may not reach low enough below the horizon
     * for this calculation, a null will be returned. See detailed explanation on top of the
     * [AstronomicalCalendar] documentation.
     *
     * @see ZENITH_13_POINT_24
     *
     * @see bainHashmashosRT58Point5Minutes
     */
    val bainHashmashosRT13Point24Degrees: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.BAIN_HASHMASHOS,
                13.24F.degrees,
                UsesElevation.ALWAYS,
                listOf(
                    RABEINU_TAM
                )
            )
        ) {
            getSunsetOffsetByDegrees(ZENITH_13_POINT_24)
        }

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
    val bainHashmashosRT58Point5Minutes: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.BAIN_HASHMASHOS,
                ZmanCalculationMethod.Relationship(ZmanType.BAIN_HASHMASHOS occurs 58.5.minutes after ZmanType.SHKIAH),
                UsesElevation.IF_SET,
                listOf(
                    RABEINU_TAM
                ),
            )
        ) {
            getTimeOffset(elevationAdjustedSunset, 58.5 * MINUTE_MILLIS)
        }

    /**
     * This method returns the beginning of *bain hashmashos* based on the calculation of 13.5 minutes (3/4 of an
     * 18-minute *Mil*) before *shkiah* calculated as [7.083˚][tzaisGeonim7Point083Degrees].
     *
     * @return the `Date` of the *bain hashmashos* of Rabbeinu Tam in this calculation. If the
     * calculation can't be computed such as northern and southern locations even south of the Arctic Circle and
     * north of the Antarctic Circle where the sun may not reach low enough below the horizon for this
     * calculation, a null will be returned. See detailed explanation on top of the [AstronomicalCalendar]
     * documentation.
     * @see tzaisGeonim7Point083Degrees
     */
    val bainHashmashosRT13Point5MinutesBefore7Point083Degrees: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.BAIN_HASHMASHOS,
                ZmanCalculationMethod.Relationship(ZmanType.BAIN_HASHMASHOS occurs 13.5.minutes before tzaisGeonim7Point083Degrees),
                UsesElevation.ALWAYS,
            ),
        ) {
            getTimeOffset(
                tzaisGeonim7Point083Degrees.momentOfOccurrence, -13.5 * MINUTE_MILLIS
            )
        }

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
    val bainHashmashosRT2Stars: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.BAIN_HASHMASHOS,
                ZmanCalculationMethod.Unspecified,//TODO this hard to catalog. Revisit.
                UsesElevation.ALWAYS,
                listOf(RABEINU_TAM accordingTo DIVREI_YOSEF)
            )
        ) {
            alos19Point8Degrees.momentOfOccurrence?.let { alos19Point8 ->
                elevationAdjustedSunrise?.let { sunrise ->
                    getTimeOffset(
                        elevationAdjustedSunset,
                        ((sunrise - alos19Point8) * (5 / 18.0)).inWholeMilliseconds //(five-eighteenths of the time between alos and sunrise) after sunset
                    )
                }
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
     * @see bainHashmashosYereim3Point05Degrees
     */
    val bainHashmashosYereim18Minutes: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.BAIN_HASHMASHOS,
                ZmanCalculationMethod.Relationship(ZmanType.BAIN_HASHMASHOS occurs 18.minutes before ZmanType.SHKIAH),
                UsesElevation.IF_SET,
                listOf(ZmanAuthority.YEREIM),
            ),
        ) {
            getTimeOffset(
                elevationAdjustedSunset, -18 * MINUTE_MILLIS
            )
        }

    /**
     * This method returns the beginning of *bain hashmashos* (twilight) according to the [Yereim (Rabbi Eliezer of Metz)](https://en.wikipedia.org/wiki/Eliezer_ben_Samuel) calculated as the sun's
     * position 3.05 above the horizon [around the equinox / equilux](https://kosherjava.com/2022/01/12/equinox-vs-equilux-zmanim-calculations/),
     * its position 18 minutes or 3/4 of a 24-minute *mil* before sunset. According to the Yereim, *bain
     * hashmashos* starts 3/4 of a *Mil* before sunset, and *tzais* /nightfall starts at sunset.
     * Note that *lechumra* (of about 14 seconds) a refraction value of 0.5166 as opposed to the traditional
     * 0.566 is used. This is more inline with the actual refraction in *Eretz Yisrael* and is brought down
     * by [Rabbi Yedidya Manet](http://beinenu.com/rabbis/%D7%94%D7%A8%D7%91-%D7%99%D7%93%D7%99%D7%93%D7%99%D7%94-%D7%9E%D7%A0%D7%AA) in his [Zmanei Halacha Lema’aseh](https://www.nli.org.il/en/books/NNL_ALEPH002542826/NLI) (p. 11). That is the first source that I am aware of that calculates degree-based Yereim
     * *zmanim*. The 0.5166 refraction is also used by the [Luach Itim Lebinah](https://zmanim.online/). Calculating the Yereim's *bain hashmashos* using 18-minute based degrees is also suggested
     * in the upcoming 8th edition of the zmanim Kehilchasam. For more details, see the article [The Yereim’s *Bein Hashmashos*](https://kosherjava.com/2020/12/07/the-yereims-bein-hashmashos/).
     *
     * @todo recalculate based on equinox/equilux
     * @return the `Date` of the sun's position 3.05 minutes before sunset. If the calculation can't
     * be computed such as in the Arctic Circle where there is at least one day a year where the sun does not
     * rise, and one where it does not set, a null will be returned. See detailed explanation on top of the
     * [AstronomicalCalendar] documentation.
     *
     * @see ZENITH_MINUS_3_POINT_05
     *
     * @see bainHashmashosYereim18Minutes
     * @see bainHashmashosYereim2Point8Degrees
     * @see bainHashmashosYereim2Point1Degrees
     */
    val bainHashmashosYereim3Point05Degrees: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.BAIN_HASHMASHOS,
                ZmanCalculationMethod.Degrees(-3.05F),
                UsesElevation.ALWAYS
            ),
        ) {
            getSunsetOffsetByDegrees(ZENITH_MINUS_3_POINT_05)
        }

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
     * @see bainHashmashosYereim2Point8Degrees
     */
    val bainHashmashosYereim16Point875Minutes: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.BAIN_HASHMASHOS,
                ZmanCalculationMethod.Relationship(ZmanType.BAIN_HASHMASHOS occurs 16.875.minutes before ZmanType.SHKIAH),
                UsesElevation.IF_SET,
                listOf(ZmanAuthority.YEREIM),
            ),
        ) {
            getTimeOffset(
                elevationAdjustedSunset, -16.875 * MINUTE_MILLIS
            )
        }

    /**
     * This method returns the beginning of *bain hashmashos* (twilight) according to the [Yereim (Rabbi Eliezer of Metz)](https://en.wikipedia.org/wiki/Eliezer_ben_Samuel) calculated as the sun's
     * position 2.8 above the horizon [around the equinox / equilux](https://kosherjava.com/2022/01/12/equinox-vs-equilux-zmanim-calculations/),
     * its position 16.875 minutes or 3/4 of an 18-minute *Mil* before sunset. According to the Yereim, *bain
     * hashmashos* starts 3/4 of a *Mil* before sunset and *tzais* or nightfall starts at sunset.
     * Details, including how the degrees were calculated can be seen in the documentation of
     * [bainHashmashosYereim3Point05Degrees].
     *
     * @return the `Date` of the sun's position 2.8 minutes before sunset. If the calculation can't
     * be computed such as in the Arctic Circle where there is at least one day a year where the sun does not
     * rise, and one where it does not set, a null will be returned. See detailed explanation on top of the
     * [AstronomicalCalendar] documentation.
     *
     * @see ZENITH_MINUS_2_POINT_8
     *
     * @see bainHashmashosYereim16Point875Minutes
     * @see bainHashmashosYereim3Point05Degrees
     * @see bainHashmashosYereim2Point1Degrees
     */
    val bainHashmashosYereim2Point8Degrees: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.BAIN_HASHMASHOS,
                ZmanCalculationMethod.Degrees(2.8F),
                UsesElevation.ALWAYS
            )
        ) {
            getSunsetOffsetByDegrees(ZENITH_MINUS_2_POINT_8)
        }

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
     * @see bainHashmashosYereim2Point1Degrees
     */
    val bainHashmashosYereim13Point5Minutes: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.BAIN_HASHMASHOS,
                ZmanCalculationMethod.Relationship(ZmanType.BAIN_HASHMASHOS occurs 13.5.minutes before ZmanType.SHKIAH),
                UsesElevation.IF_SET,
                listOf(ZmanAuthority.YEREIM),
            )
        ) {
            getTimeOffset(
                elevationAdjustedSunset, -13.5 * MINUTE_MILLIS
            )
        }

    /**
     * This method returns the beginning of *bain hashmashos* according to the [Yereim (Rabbi Eliezer of Metz)](https://en.wikipedia.org/wiki/Eliezer_ben_Samuel)
     * calculated as the sun's position 2.1 above the horizon [around the equinox / equilux](https://kosherjava.com/2022/01/12/equinox-vs-equilux-zmanim-calculations/)
     * in Yerushalayim, its position 13.5 minutes or 3/4 of an 18-minute *Mil* before sunset. According to the Yereim,
     * *bain hashmashos* starts 3/4 of a *mil* before sunset and *tzais* or nightfall starts at sunset.
     * Details, including how the degrees were calculated can be seen in the documentation of
     * [bainHashmashosYereim3Point05Degrees].
     *
     * @return the `Date` of the sun's position 2.1 minutes before sunset. If the calculation can't
     * be computed such as in the Arctic Circle where there is at least one day a year where the sun does not
     * rise, and one where it does not set, a null will be returned. See detailed explanation on top of the
     * [AstronomicalCalendar] documentation.
     *
     * @see ZENITH_MINUS_2_POINT_1
     *
     * @see bainHashmashosYereim13Point5Minutes
     * @see bainHashmashosYereim2Point8Degrees
     * @see bainHashmashosYereim3Point05Degrees
     */
    val bainHashmashosYereim2Point1Degrees: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.BAIN_HASHMASHOS,
                ZmanCalculationMethod.Degrees(2.1F),
                UsesElevation.ALWAYS
            )
        ) {
            getSunsetOffsetByDegrees(ZENITH_MINUS_2_POINT_1) //TODO figure out how to catalog this
        }

    /**
     * This method returns the *tzais* (nightfall) based on the opinion of the *Geonim* calculated at the
     * sun's position at [3.7˚][ZENITH_3_POINT_7] below the western horizon.
     *
     * @return the `Date` representing the time when the sun is 3.7 below sea level.
     * @see ZENITH_3_POINT_7
     */
    val tzaisGeonim3Point7Degrees: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.TZAIS,
                ZmanCalculationMethod.Degrees(3.7F),
                UsesElevation.ALWAYS
            )
        ) {
            getSunsetOffsetByDegrees(ZENITH_3_POINT_7)
        }

    /**
     * This method returns the *tzais* (nightfall) based on the opinion of the *Geonim* calculated at the
     * sun's position at [3.8˚][ZENITH_3_POINT_8] below the western horizon.
     *
     * @return the `Date` representing the time when the sun is 3.8 below sea level.
     * @see ZENITH_3_POINT_8
     */
    val tzaisGeonim3Point8Degrees: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.TZAIS,
                3.8F.degrees,
                UsesElevation.ALWAYS
            )
        ) {
            getSunsetOffsetByDegrees(ZENITH_3_POINT_8)
        }

    /**
     * This method returns the *tzais* (nightfall) based on the opinion of the *Geonim* calculated at the
     * sun's position at [5.95˚][ZENITH_5_POINT_95] below the western horizon.
     *
     * @return the `Date` representing the time when the sun is 5.95 below sea level. If the calculation
     * can't be computed such as northern and southern locations even south of the Arctic Circle and north of
     * the Antarctic Circle where the sun may not reach low enough below the horizon for this calculation, a
     * null will be returned. See detailed explanation on top of the [AstronomicalCalendar] documentation.
     * @see ZENITH_5_POINT_95
     */
    val tzaisGeonim5Point95Degrees: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.TZAIS,
                ZmanCalculationMethod.Degrees(5.95F),
                UsesElevation.ALWAYS
            )
        ) {
            getSunsetOffsetByDegrees(ZENITH_5_POINT_95)
        }

    /**
     * This method returns the *tzais* (nightfall) based on the opinion of the *Geonim* calculated as 3/4
     * of a [Mil](https://en.wikipedia.org/wiki/Biblical_and_Talmudic_units_of_measurement) based on an 18
     * minute Mil, or 13.5 minutes. It is the sun's position at [3.65˚][ZENITH_3_POINT_65] below the western
     * horizon. This is a very early *zman* and should not be relied on without Rabbinical guidance.
     *
     * @return the `Date` representing the time when the sun is 3.65 below sea level. If the calculation
     * can't be computed such as northern and southern locations even south of the Arctic Circle and north of
     * the Antarctic Circle where the sun may not reach low enough below the horizon for this calculation, a
     * null will be returned. See detailed explanation on top of the [AstronomicalCalendar] documentation.
     * @see ZENITH_3_POINT_65
     */
    val tzaisGeonim3Point65Degrees: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.TZAIS,
                ZmanCalculationMethod.Degrees(3.65F),
                UsesElevation.ALWAYS
            )
        ) {
            getSunsetOffsetByDegrees(ZENITH_3_POINT_65)
        }

    /**
     * This method returns the *tzais* (nightfall) based on the opinion of the *Geonim* calculated as 3/4
     * of a [Mil](https://en.wikipedia.org/wiki/Biblical_and_Talmudic_units_of_measurement) based on an 18
     * minute Mil, or 13.5 minutes. It is the sun's position at [3.676˚][ZENITH_3_POINT_676] below the western
     * horizon based on the calculations of Stanley Fishkind. This is a very early *zman* and should not be
     * relied on without Rabbinical guidance.
     *
     * @return the `Date` representing the time when the sun is 3.676 below sea level. If the
     * calculation can't be computed such as northern and southern locations even south of the Arctic Circle and
     * north of the Antarctic Circle where the sun may not reach low enough below the horizon for this
     * calculation, a null will be returned. See detailed explanation on top of the [AstronomicalCalendar]
     * documentation.
     * @see ZENITH_3_POINT_676
     */
    val tzaisGeonim3Point676Degrees: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.TZAIS,
                ZmanCalculationMethod.Degrees(3.676F),
                UsesElevation.ALWAYS
            )
        ) {
            getSunsetOffsetByDegrees(ZENITH_3_POINT_676)
        }

    /**
     * This method returns the *tzais* (nightfall) based on the opinion of the *Geonim* calculated as 3/4
     * of a *[mil](https://en.wikipedia.org/wiki/Biblical_and_Talmudic_units_of_measurement)* based
     * on a 24-minute Mil, or 18 minutes. It is the sun's position at [4.61˚][ZENITH_4_POINT_61] below the
     * western horizon. This is a very early *zman* and should not be relied on without Rabbinical guidance.
     *
     * @return the `Date` representing the time when the sun is 4.61 below sea level. If the calculation
     * can't be computed such as northern and southern locations even south of the Arctic Circle and north of
     * the Antarctic Circle where the sun may not reach low enough below the horizon for this calculation, a
     * null will be returned. See detailed explanation on top of the [AstronomicalCalendar] documentation.
     * @see ZENITH_4_POINT_61
     */
    val tzaisGeonim4Point61Degrees: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.TZAIS,
                ZmanCalculationMethod.Degrees(4.61F),
                UsesElevation.ALWAYS
            )
        ) {
            getSunsetOffsetByDegrees(ZENITH_4_POINT_61)
        }

    /**
     * This method returns the *tzais* (nightfall) based on the opinion of the *Geonim* calculated as 3/4
     * of a [Mil](https://en.wikipedia.org/wiki/Biblical_and_Talmudic_units_of_measurement), based on a 22.5
     * minute Mil, or 16 7/8 minutes. It is the sun's position at [4.37˚][ZENITH_4_POINT_37] below the western
     * horizon. This is a very early *zman* and should not be relied on without Rabbinical guidance.
     *
     * @return the `Date` representing the time when the sun is 4.37 below sea level. If the calculation
     * can't be computed such as northern and southern locations even south of the Arctic Circle and north of
     * the Antarctic Circle where the sun may not reach low enough below the horizon for this calculation, a
     * null will be returned. See detailed explanation on top of the [AstronomicalCalendar] documentation.
     * @see ZENITH_4_POINT_37
     */
    val tzaisGeonim4Point37Degrees: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.TZAIS,
                ZmanCalculationMethod.Degrees(4.37F),
                UsesElevation.ALWAYS
            )
        ) {
            getSunsetOffsetByDegrees(ZENITH_4_POINT_37)
        }

    /**
     * This method returns the *tzais* (nightfall) based on the opinion of the *Geonim* calculated as 3/4
     * of a 24-minute *[Mil](https://en.wikipedia.org/wiki/Biblical_and_Talmudic_units_of_measurement)*,
     * based on a *Mil* being 24 minutes, and is calculated as 18 + 2 + 4 for a total of 24 minutes. It is the
     * sun's position at [5.88˚][ZENITH_5_POINT_88] below the western horizon. This is a very early
     * *zman* and should not be relied on without Rabbinical guidance.
     *
     * @todo Additional detailed documentation needed.
     * @return the `Date` representing the time when the sun is 5.88 below sea level. If the calculation
     * can't be computed such as northern and southern locations even south of the Arctic Circle and north of
     * the Antarctic Circle where the sun may not reach low enough below the horizon for this calculation, a
     * null will be returned. See detailed explanation on top of the [AstronomicalCalendar] documentation.
     * @see ZENITH_5_POINT_88
     */
    val tzaisGeonim5Point88Degrees: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.TZAIS,
                ZmanCalculationMethod.Degrees(5.88F),
                UsesElevation.ALWAYS
            )
        ) {
            getSunsetOffsetByDegrees(ZENITH_5_POINT_88)
        }

    /**
     * This method returns the *tzais* (nightfall) based on the opinion of the *Geonim* calculated as 3/4
     * of a [Mil](https://en.wikipedia.org/wiki/Biblical_and_Talmudic_units_of_measurement) based on the
     * sun's position at [4.8˚][ZENITH_4_POINT_8] below the western horizon. This is based on Rabbi Leo Levi's
     * calculations. This is the This is a very early *zman* and should not be relied on without Rabbinical guidance.
     * @todo Additional documentation needed.
     *
     * @return the `Date` representing the time when the sun is 4.8 below sea level. If the calculation
     * can't be computed such as northern and southern locations even south of the Arctic Circle and north of
     * the Antarctic Circle where the sun may not reach low enough below the horizon for this calculation, a
     * null will be returned. See detailed explanation on top of the [AstronomicalCalendar] documentation.
     * @see ZENITH_4_POINT_8
     */
    val tzaisGeonim4Point8Degrees: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.TZAIS,
                ZmanCalculationMethod.Degrees(4.8F),
                UsesElevation.ALWAYS
            )
        ) {
            getSunsetOffsetByDegrees(ZENITH_4_POINT_8)
        }

    /**
     * This method returns the *tzais* (nightfall) based on the opinion of the *Geonim* as calculated by
     * [Rabbi Yechiel Michel Tucazinsky](https://en.wikipedia.org/wiki/Yechiel_Michel_Tucazinsky). It is
     * based on of the position of the sun no later than [31 minutes][tzaisGeonim6Point45Degrees] after sunset
     * in Jerusalem the height of the summer solstice and is 28 minutes after *shkiah* [around the equinox / equilux](https://kosherjava.com/2022/01/12/equinox-vs-equilux-zmanim-calculations/). This
     * computes to 6.45 below the western horizon.
     * @todo Additional documentation details needed.
     *
     * @return the `Date` representing the time when the sun is 6.45 below sea level. If the
     * calculation can't be computed such as northern and southern locations even south of the Arctic Circle and
     * north of the Antarctic Circle where the sun may not reach low enough below the horizon for this
     * calculation, a null will be returned. See detailed explanation on top of the [AstronomicalCalendar]
     * documentation.
     * @see ZENITH_6_POINT_45
     */
    val tzaisGeonim6Point45Degrees: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.TZAIS,
                ZmanCalculationMethod.Degrees(6.45F),
                UsesElevation.ALWAYS
            )
        ) {
            getSunsetOffsetByDegrees(ZENITH_6_POINT_45)
        }

    /**
     * This method returns the *tzais* (nightfall) based on the opinion of the *Geonim* calculated when the
     * sun's position is [7.083˚ (or 7˚5′)][ZENITH_7_POINT_083]) below the western horizon.
     *
     * This is often referred to as 75' or 7 degrees and 5 minutes.
     *
     * This calculation is based on
     * - the observation of 3 medium sized stars by Dr. Baruch (Berthold) Cohn in his *luach* [Tabellen enthaltend die Zeitangaben für den Beginn der Nacht und des Tages für die Breitengrade + 66 bis -38](https://sammlungen.ub.uni-frankfurt.de/freimann/content/titleinfo/983088)
     * published in Strasbourg, France in 1899.
     *
     * This calendar was very popular in Europe, and many other calendars based their time on it.
     *
     * - [Rav Dovid Tzvi Hoffman](https://en.wikipedia.org/wiki/David_Zvi_Hoffmann) in his [Sh"Ut Melamed Leho'il](https://hebrewbooks.org/1053) in an exchange of letters with Baruch Cohn in [Orach Chaim 30](https://hebrewbooks.org/pdfpager.aspx?req=1053&st=&pgnum=37) agreed to this *zman* (page 36),
     * - as did the Sh"Ut Bnei Tziyon and
     * - the Tenuvas Sadeh.
     * - It is very close to the time of the [Mekor Chesed](https://hebrewbooks.org/22044) of the Sefer chasidim.
     *
     * It is close to the position of the sun 30 minutes after sunset in Jerusalem [around the equinox / equilux](https://kosherjava.com/2022/01/12/equinox-vs-equilux-zmanim-calculations/),
     * but not exactly. The actual position of the sun 30 minutes after sunset in Jerusalem at the equilux is 7.205 and 7.199
     * at the equinox.
     *
     * See Hazmanim Bahalacha vol 2, pages 520-521 for more details.
     *
     * @return the `Date` representing the time when the sun is 7.083 below sea level. If the
     * calculation can't be computed such as northern and southern locations even south of the Arctic Circle and
     * north of the Antarctic Circle where the sun may not reach low enough below the horizon for this
     * calculation, a null will be returned. See detailed explanation on top of the [AstronomicalCalendar]
     * documentation.
     * @see ZENITH_7_POINT_083
     */
    val tzaisGeonim7Point083Degrees: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.TZAIS,
                7.083F.degrees,
                UsesElevation.ALWAYS,
                listOf(ZmanAuthority.GEONIM)
            )
        ) {
            getSunsetOffsetByDegrees(ZENITH_7_POINT_083)
        }

    /**
     * This method returns *tzais* (nightfall) based on the opinion of the *Geonim* calculated as 45 minutes
     * after sunset during the summer solstice in New York, when the *neshef* (twilight) is the longest.
     *
     * The sun's position at this time computes to [7.75˚][ZENITH_7_POINT_67] below the western horizon.
     *
     * See
     * - [Igros Moshe Even Haezer 4, Ch. 4](https://hebrewbooks.org/pdfpager.aspx?req=921&pgnum=149) (regarding *tzais* for *krias Shema*).
     * - It is also mentioned in Rabbi Heber's [Shaarei Zmanim](https://hebrewbooks.org/53000) on in [chapter 10 (page 87)](https://hebrewbooks.org/pdfpager.aspx?req=53055&pgnum=101) and [chapter 12 (page 108)](https://hebrewbooks.org/pdfpager.aspx?req=53055&pgnum=122).
     * - Also see the time of 45 minutes in [Rabbi Simcha Bunim Cohen's](https://en.wikipedia.org/wiki/Simcha_Bunim_Cohen) [The radiance of Shabbos](https://www.worldcat.org/oclc/179728985) as the earliest *zman* for New York.
     * - This *zman* is also listed in the [Divrei Shalom Vol. III, chapter 75](https://hebrewbooks.org/pdfpager.aspx?req=1927&pgnum=90),
     * - and [Bais Av"i Vol. III, chapter 117](https://hebrewbooks.org/pdfpager.aspx?req=892&pgnum=431).
     * - This *zman* is also listed in the Divrei Shalom etc. chapter 177 (FIXME - could not be located).
     *
     * Since this *zman* depends on the level of light, Rabbi Yaakov Shakow presented this degree-based calculation to
     * Rabbi [Rabbi Shmuel Kamenetsky](https://en.wikipedia.org/wiki/Shmuel_Kamenetsky) who agreed to it.
     *
     * @todo add hyperlinks to source of Divrei Shalom.
     * @return the `Date` representing the time when the sun is 7.67 below sea level. If the
     * calculation can't be computed such as northern and southern locations even south of the Arctic Circle and
     * north of the Antarctic Circle where the sun may not reach low enough below the horizon for this
     * calculation, a null will be returned. See detailed explanation on top of the [AstronomicalCalendar]
     * documentation.
     * @see ZENITH_7_POINT_67
     */
    val tzaisGeonim7Point67Degrees: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.TZAIS,
                7.67F.degrees,
                UsesElevation.ALWAYS
            ),
        ) {
            getSunsetOffsetByDegrees(ZENITH_7_POINT_67)
        }

    /**
     * This method returns the *tzais* (nightfall) based on the opinion of the *Geonim* calculated at the
     * sun's position at [8.5˚][ZENITH_8_POINT_5] below the western horizon.
     *
     * @return the `Date` representing the time when the sun is 8.5 below sea level. If the calculation
     * can't be computed such as northern and southern locations even south of the Arctic Circle and north of
     * the Antarctic Circle where the sun may not reach low enough below the horizon for this calculation, a
     * null will be returned. See detailed explanation on top of the [AstronomicalCalendar] documentation.
     * @see ZENITH_8_POINT_5
     */
    val tzaisGeonim8Point5Degrees: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.TZAIS,
                8.5F.degrees,
                UsesElevation.ALWAYS
            ),
        ) {
            getSunsetOffsetByDegrees(ZENITH_8_POINT_5)
        }

    /**
     * This method returns the *tzais* (nightfall) based on the calculations used in the [Luach Itim Lebinah](https://www.worldcat.org/oclc/243303103) as the stringent time for *tzais*.  It is
     * calculated at the sun's position at [9.3˚][ZENITH_9_POINT_3] below the western horizon.
     *
     * @return the `Date` representing the time when the sun is 9.3 below sea level. If the calculation
     * can't be computed such as northern and southern locations even south of the Arctic Circle and north of
     * the Antarctic Circle where the sun may not reach low enough below the horizon for this calculation, a
     * null will be returned. See detailed explanation on top of the [AstronomicalCalendar] documentation.
     */
    val tzaisGeonim9Point3Degrees: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.TZAIS,
                9.3F.degrees,
                UsesElevation.ALWAYS
            ),
        ) {
            getSunsetOffsetByDegrees(ZENITH_9_POINT_3)
        }

    /**
     * This method returns the *tzais* (nightfall) based on the opinion of the *Geonim* calculated as 60
     * minutes after sunset [around the equinox / equilux](https://kosherjava.com/2022/01/12/equinox-vs-equilux-zmanim-calculations/), the
     * day that a solar hour is 60 minutes in New York. The sun's position at this time computes to
     * [9.75˚][ZENITH_9_POINT_75] below the western horizon. This is the opinion of [Rabbi Eliyahu Henkin](https://en.wikipedia.org/wiki/Yosef_Eliyahu_Henkin).  This also follows the opinion of
     * [Rabbi Shmuel Kamenetsky](https://en.wikipedia.org/wiki/Shmuel_Kamenetsky). Rabbi Yaakov Shakow presented
     * these degree-based times to Rabbi Shmuel Kamenetsky who agreed to them.
     *
     * @todo recalculate based on equinox / equilux.
     * @return the `Date` representing the time when the sun is 9.75 below sea level. If the calculation
     * can't be computed such as northern and southern locations even south of the Arctic Circle and north of
     * the Antarctic Circle where the sun may not reach low enough below the horizon for this calculation, a
     * null will be returned. See detailed explanation on top of the [AstronomicalCalendar] documentation.
     *
     * @see tzais60
     */
    val tzaisGeonim9Point75Degrees: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.TZAIS,
                9.75F.degrees,
                UsesElevation.ALWAYS
            ),
        ) {
            getSunsetOffsetByDegrees(ZENITH_9_POINT_75)
        }

    /**
     * This method returns the *tzais* (nightfall) based on the opinion of the [Chavas Yair](https://en.wikipedia.org/wiki/Yair_Bacharach) and [Divrei Malkiel](https://he.wikipedia.org/wiki/%D7%9E%D7%9C%D7%9B%D7%99%D7%90%D7%9C_%D7%A6%D7%91%D7%99_%D7%98%D7%A0%D7%A0%D7%91%D7%95%D7%99%D7%9D) that the time to walk the distance of a *Mil* is 15 minutes for a total of 60 minutes
     * for 4 *Mil* after [sea level sunset][seaLevelSunset]. See detailed documentation explaining the
     * 60 minute concept at [alos60].
     *
     * @return the `Date` representing 60 minutes after sea level sunset. If the calculation can't be
     * computed such as in the Arctic Circle where there is at least one day a year where the sun does not rise,
     * and one where it does not set, a null will be returned. See detailed explanation on top of the
     * [AstronomicalCalendar] documentation.
     *
     * @see alos60
     * @see .getPlagHamincha60Minutes
     * @see shaahZmanis60Minutes
     */
    val tzais60: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.TZAIS,
                ZmanCalculationMethod.Relationship(ZmanType.TZAIS occurs 60.minutes after ZmanType.SHKIAH),
                UsesElevation.ALWAYS,
                listOf(ZmanAuthority.CHAVAS_YAIR, ZmanAuthority.DIVREI_MALKIEL),
            ),
        ) {
            getTimeOffset(
                elevationAdjustedSunset, 60 * MINUTE_MILLIS
            )
        }

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
     * @see ateretTorahSunsetOffset
     * @see .setAteretTorahSunsetOffset
     */
    val tzaisAteretTorah: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.TZAIS,
                ZmanCalculationMethod.Relationship(ZmanType.TZAIS occurs ateretTorahSunsetOffset.minutes after ZmanType.SHKIAH),
                UsesElevation.NEVER,
                listOf(ZmanAuthority.AteretTorah(ateretTorahSunsetOffset)),
            ),
        ) {
            getTimeOffset(
                elevationAdjustedSunset, ateretTorahSunsetOffset * MINUTE_MILLIS
            )
        }

    /**
     * This method returns the latest *zman krias shema* (time to recite Shema in the morning) based on the
     * calculation of *Chacham* Yosef Harari-Raful of Yeshivat Ateret Torah, that the day starts
     * [1/10th of the day][alos72Zmanis] before sunrise and is usually calculated as ending
     * [40 minutes after sunset][tzaisAteretTorah] (configurable to any offset via
     * [.setAteretTorahSunsetOffset]). *shaos zmaniyos* are calculated based on this day and added
     * to [*alos*][alos72Zmanis] to reach this time. This time is 3
     * [*shaos zmaniyos*][shaahZmanisAteretTorah] (temporal hours) after
     * [*alos* 72 *zmaniyos*][alos72Zmanis]. **Note: ** Based on this calculation *chatzos*
     * will not be at midday.
     *
     * @return the `Date` of the latest *zman krias shema* based on this calculation. If the
     * calculation can't be computed such as in the Arctic Circle where there is at least one day a year where
     * the sun does not rise, and one where it does not set, a null will be returned. See detailed explanation
     * on top of the [AstronomicalCalendar] documentation.
     * @see alos72Zmanis
     * @see tzaisAteretTorah
     * @see ateretTorahSunsetOffset
     * @see .setAteretTorahSunsetOffset
     * @see shaahZmanisAteretTorah
     */
    val sofZmanShmaAteretTorah: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.SOF_ZMAN_KRIAS_SHEMA,
                ZmanCalculationMethod.DayDefinition.dawnToDusk(
                    ZmanCalculationMethod.ZmaniyosDuration._72,
                    ZmanAuthority.AteretTorah(ateretTorahSunsetOffset)
                ),
                UsesElevation.IF_SET,
                listOf(ZmanAuthority.AteretTorah(ateretTorahSunsetOffset)),
            )
        ) {
            getSofZmanShma(alos72Zmanis.momentOfOccurrence, tzaisAteretTorah.momentOfOccurrence)
        }

    /**
     * This method returns the latest *zman tfila* (time to recite the morning prayers) based on the calculation
     * of *Chacham* Yosef Harari-Raful of Yeshivat Ateret Torah, that the day starts [1/10th of the day][alos72Zmanis]
     * before sunrise and is usually calculated as ending [40 minutes][tzaisAteretTorah] (configurable to any offset via
     * setting [ateretTorahSunsetOffset]).
     *
     * *shaos zmaniyos* are calculated based on this day and added to [*alos*][alos72Zmanis] to reach this time. This time
     * is 4 * [*shaos zmaniyos*][shaahZmanisAteretTorah] (temporal hours) after
     * [*alos* 72 zmaniyos][alos72Zmanis].
     * **Note: ** Based on this calculation *chatzos* will not be at midday.
     *
     * @return the `Date` of the latest *zman krias shema* based on this calculation. If the
     * calculation can't be computed such as in the Arctic Circle where there is at least one day a year where
     * the sun does not rise, and one where it does not set, a null will be returned. See detailed explanation
     * on top of the [AstronomicalCalendar] documentation.
     * @see alos72Zmanis
     * @see tzaisAteretTorah
     * @see shaahZmanisAteretTorah
     * @see .setAteretTorahSunsetOffset
     */
    val sofZmanTfilahAteretTorah: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.SOF_ZMAN_TEFILLAH,
                ZmanCalculationMethod.DayDefinition.dawnToDusk(
                    ZmanCalculationMethod.ZmaniyosDuration._72,
                    ZmanAuthority.AteretTorah(ateretTorahSunsetOffset)
                ),
                UsesElevation.IF_SET,
                listOf(ZmanAuthority.AteretTorah(ateretTorahSunsetOffset)),
            )
        ) {
            getSofZmanTfila(alos72Zmanis.momentOfOccurrence, tzaisAteretTorah.momentOfOccurrence)
        }

    /**
     * This method returns the time of *mincha gedola* based on the calculation of *Chacham* Yosef
     * Harari-Raful of Yeshivat Ateret Torah, that the day starts [1/10th of the day][alos72Zmanis]
     * before sunrise and is usually calculated as ending [40 minutes after sunset][tzaisAteretTorah]
     * (configurable to any offset via [.setAteretTorahSunsetOffset]). This is the preferred earliest
     * time to pray *mincha* according to the opinion of the [Rambam](https://en.wikipedia.org/wiki/Maimonides) and others. For more information on this see the documentation on [*mincha][.getMinchaGedola]. This is calculated as 6.5 [solar hours][shaahZmanisAteretTorah] after *alos*. The
     * calculation used is 6.5 * [shaahZmanisAteretTorah] after [*alos*][alos72Zmanis].
     *
     * @see alos72Zmanis
     * @see tzaisAteretTorah
     * @see shaahZmanisAteretTorah
     * @see .getMinchaGedola
     * @see .getMinchaKetanaAteretTorah
     * @see ZmanimCalendar.getMinchaGedola
     * @see ateretTorahSunsetOffset
     * @see .setAteretTorahSunsetOffset
     * @return the `Date` of the time of *mincha gedola*. If the calculation can't be computed such as
     * in the Arctic Circle where there is at least one day a year where the sun does not rise, and one where it
     * does not set, a null will be returned. See detailed explanation on top of the [AstronomicalCalendar]
     * documentation.
     */
    val minchaGedolaAteretTorah: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.MINCHA_GEDOLAH,
                ZmanCalculationMethod.DayDefinition.dawnToDusk(
                    ZmanCalculationMethod.ZmaniyosDuration._72,
                    ZmanAuthority.AteretTorah(ateretTorahSunsetOffset)
                ),
                UsesElevation.IF_SET,
                listOf(ZmanAuthority.AteretTorah(ateretTorahSunsetOffset)),
            )
        ) {
            getMinchaGedola(alos72Zmanis.momentOfOccurrence, tzaisAteretTorah.momentOfOccurrence)
        }

    /**
     * This method returns the time of *mincha ketana* based on the calculation of
     * *Chacham* Yosef Harari-Raful of Yeshivat Ateret Torah, that the day starts
     * [1/10th of the day][alos72Zmanis] before sunrise and is usually calculated as ending
     * [40 minutes after sunset][tzaisAteretTorah] (configurable to any offset via
     * [.setAteretTorahSunsetOffset]). This is the preferred earliest time to pray *mincha*
     * according to the opinion of the [Rambam](https://en.wikipedia.org/wiki/Maimonides) and others.
     * For more information on this see the documentation on [*mincha gedola*][.getMinchaGedola]. This is
     * calculated as 9.5 [solar hours][shaahZmanisAteretTorah] after [*alos*][alos72Zmanis].
     * The calculation used is 9.5 * [shaahZmanisAteretTorah] after [*alos*][alos72Zmanis].
     *
     * @see alos72Zmanis
     * @see tzaisAteretTorah
     * @see shaahZmanisAteretTorah
     * @see ateretTorahSunsetOffset
     * @see .setAteretTorahSunsetOffset
     * @see .getMinchaGedola
     * @see .getMinchaKetana
     * @return the `Date` of the time of *mincha ketana*. If the calculation can't be computed such as
     * in the Arctic Circle where there is at least one day a year where the sun does not rise, and one where it
     * does not set, a null will be returned. See detailed explanation on top of the [AstronomicalCalendar]
     * documentation.
     */
    val minchaKetanaAteretTorah: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.MINCHA_KETANAH,
                ZmanCalculationMethod.DayDefinition.dawnToDusk(
                    ZmanCalculationMethod.ZmaniyosDuration._72,
                    ZmanAuthority.AteretTorah(ateretTorahSunsetOffset)
                ),
                UsesElevation.IF_SET,
                listOf(ZmanAuthority.AteretTorah(ateretTorahSunsetOffset)),
            )
        ) {
            getMinchaKetana(alos72Zmanis.momentOfOccurrence, tzaisAteretTorah.momentOfOccurrence)
        }

    /**
     * This method returns the time of *plag hamincha* based on the calculation of *Chacham* Yosef Harari-Raful
     * of Yeshivat Ateret Torah, that the day starts [1/10th of the day][alos72Zmanis] before sunrise and is
     * usually calculated as ending [40 minutes after sunset][tzaisAteretTorah] (configurable to any offset
     * via [.setAteretTorahSunsetOffset]). *shaos zmaniyos* are calculated based on this day and
     * added to [*alos*][alos72Zmanis] to reach this time. This time is 10.75
     * [*shaos zmaniyos*][shaahZmanisAteretTorah] (temporal hours) after [dawn][alos72Zmanis].
     *
     * @return the `Date` of the *plag*. If the calculation can't be computed such as in the Arctic Circle
     * where there is at least one day a year where the sun does not rise, and one where it does not set, a null
     * will be returned. See detailed explanation on top of the [AstronomicalCalendar] documentation.
     * @see alos72Zmanis
     * @see tzaisAteretTorah
     * @see shaahZmanisAteretTorah
     * @see .setAteretTorahSunsetOffset
     * @see ateretTorahSunsetOffset
     */
    val plagHaminchaAteretTorah: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.PLAG_HAMINCHA,
                ZmanCalculationMethod.DayDefinition.dawnToDusk(
                    ZmanCalculationMethod.ZmaniyosDuration._72,
                    ZmanAuthority.AteretTorah(ateretTorahSunsetOffset)
                ),
                UsesElevation.IF_SET,
                listOf(ZmanAuthority.AteretTorah(ateretTorahSunsetOffset)),
            )
        ) {
            getPlagHamincha(alos72Zmanis.momentOfOccurrence, tzaisAteretTorah.momentOfOccurrence)
        }
    /**
     * This method returns the time of *misheyakir* based on the common calculation of the Syrian community in NY
     * that the *alos* is a fixed minute offset from day starting [1/10th of the day][alos72Zmanis]
     * before sunrise. The common offsets are 6 minutes (based on the *Pri Megadim*, but not linked to the
     * calculation of *Alos* as 1/10th of the day), 8 and 18 minutes (possibly attributed to
     * [*Chacham* Baruch Ben Haim](https://en.wikipedia.org/wiki/Baruch_Ben_Haim)). Since there is no
     * universal accepted offset, the user of this API will have to specify one. *Chacham* Yosef Harari-Raful of
     * Yeshivat Ateret Torah does not supply any *zman* for *misheyakir* and does not endorse any specific
     * calculation for *misheyakir*. For that reason, this method is not a public method.
     *
     * @param minutes
     * the number of minutes after *alos* calculated as [1/10th of the day][alos72Zmanis]
     * @return the `Date` of *misheyakir*. If the calculation can't be computed such as in the Arctic
     * Circle where there is at least one day a year where the sun does not rise, and one where it does not set,
     * a null will be returned. See detailed explanation on top of the [AstronomicalCalendar]
     * documentation.
     * @see alos72Zmanis
     */
    // private Date getMesheyakirAteretTorah(double minutes) {
    // return getTimeOffset(getAlos72Zmanis(), minutes * MINUTE_MILLIS);
    // }
    /**
     * Method to return *tzais* (dusk) calculated as 72 minutes zmaniyos, or 1/10th of the day after
     * [sea level sunset][seaLevelSunset]. This is the way that the [Minchas Cohen](https://en.wikipedia.org/wiki/Abraham_Cohen_Pimentel) in Ma'amar 2:4 calculates Rebbeinu Tam's
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
     * @see alos72Zmanis
     */
    val tzais72Zmanis: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.TZAIS,
                ZmanCalculationMethod.Relationship(ZmanType.TZAIS occurs 72.minutes.zmaniyos after ZmanType.SHKIAH),
                UsesElevation.IF_SET,
                listOf(RABEINU_TAM accordingTo ZmanAuthority.MINCHAS_COHEN),
            ),
        ) {
            getZmanisBasedOffset(1.2)
        }

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
        val shaahZmanis =
            getTemporalHour(elevationAdjustedSunrise, elevationAdjustedSunset)//shaaZmanisGra
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
     * @see alos90Zmanis
     */
    val tzais90Zmanis: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.TZAIS,
                ZmanCalculationMethod.Relationship(ZmanType.TZAIS occurs 90.minutes.zmaniyos after ZmanType.SHKIAH),
                UsesElevation.IF_SET,
            ),
        ) {
            getZmanisBasedOffset(1.5)
        }

    /**
     * Method to return *tzais* (dusk) calculated using 96 minutes *zmaniyos* or 1/7.5 of the day after
     * [sea level sunset][seaLevelSunset].
     *
     * @return the `Date` representing the time. If the calculation can't be computed such as in the Arctic
     * Circle where there is at least one day a year where the sun does not rise, and one where it does not set,
     * a null will be returned. See detailed explanation on top of the [AstronomicalCalendar]
     * documentation.
     * @see alos96Zmanis
     */
    val tzais96Zmanis: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.TZAIS,
                ZmanCalculationMethod.Relationship(ZmanType.TZAIS occurs 96.minutes.zmaniyos after ZmanType.SHKIAH),
                UsesElevation.IF_SET,
            ),
        ) {
            getZmanisBasedOffset(1.6)
        }

    /**
     * Method to return *tzais* (dusk) calculated as 90 minutes after sea level sunset. This method returns
     * *tzais* (nightfall) based on the opinion of the Magen Avraham that the time to walk the distance of a
     * *Mil* according to the [Rambam](https://en.wikipedia.org/wiki/Maimonides)'s opinion
     * is 18 minutes for a total of 90 minutes based on the opinion of Ula who calculated *tzais* as 5
     * *Mil* after sea level *shkiah* (sunset). A similar calculation [tzais19Point8Degrees]
     * uses solar position calculations based on this time.
     *
     * @return the `Date` representing the time. If the calculation can't be computed such as in the Arctic
     * Circle where there is at least one day a year where the sun does not rise, and one where it does not set,
     * a null will be returned. See detailed explanation on top of the [AstronomicalCalendar]
     * documentation.
     * @see tzais19Point8Degrees
     * @see alos90
     */
    val tzais90: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.TZAIS,
                ZmanCalculationMethod.Relationship(ZmanType.TZAIS occurs 90.minutes after ZmanType.SHKIAH),
                UsesElevation.IF_SET,
            ),
        ) {
            getTimeOffset(
                elevationAdjustedSunset, 90 * MINUTE_MILLIS
            )
        }

    /**
     * This method should be used *lechumra* only and returns *tzais* (nightfall) based on the calculations
     * of [Rav Chaim Naeh](https://en.wikipedia.org/wiki/Avraham_Chaim_Naeh) that the time to walk the
     * distance of a *Mil* according to the [Rambam](https://en.wikipedia.org/wiki/Maimonides)'s opinion
     * is 2/5 of an hour (24 minutes) for a total of 120 minutes based on the opinion of *Ula* who calculated
     * *tzais* as 5 *Mil* after sea level *shkiah* (sunset). A similar calculation [][tzais26Degrees] uses degree-based calculations based on this 120 minute calculation. Since the *zman*
     * is extremely late and at a point that is long past the 18 point where the darkest point is
     * reached, it should only be used *lechumra*, such as delaying the start of nighttime *mitzvos*.
     *
     * @return the `Date` representing the time. If the calculation can't be computed such as in the Arctic
     * Circle where there is at least one day a year where the sun does not rise, and one where it does not set,
     * a null will be returned. See detailed explanation on top of the [AstronomicalCalendar].
     * documentation.
     * @see tzais26Degrees
     * @see alos120
     */
    @Deprecated("This method should be used <em>lechumra</em> only since it returns a very late time, and if used\n" + "	          <em>lekula</em> can result in <em>chillul Shabbos</em> etc. There is no current plan to remove this\n" + "	          method from the API, and this deprecation is intended to alert developers of the danger of using it.\n" + "	  \n" + "	  ")  // (forRemoval=false) // add back once Java 9 is the minimum supported version
    val tzais120: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.TZAIS,
                ZmanCalculationMethod.Relationship(ZmanType.TZAIS occurs 120.minutes after ZmanType.SHKIAH),
                UsesElevation.IF_SET,
            ),
        ) {
            getTimeOffset(
                elevationAdjustedSunset, 120 * MINUTE_MILLIS
            )
        }

    /**
     * This method should be used *lechumra* only and returns *tzais* (dusk) calculated using 120 minutes
     * *zmaniyos* after [sea level sunset][seaLevelSunset]. Since the *zman*
     * is extremely late and at a point when the it is long past the 18 point where the darkest point is
     * reached, it should only be used *lechumra*, such as delaying the start of nighttime *mitzvos*.
     *
     * @return the `Date` representing the time. If the calculation can't be computed such as in the Arctic
     * Circle where there is at least one day a year where the sun does not rise, and one where it does not set,
     * a null will be returned. See detailed explanation on top of the [AstronomicalCalendar]
     * documentation.
     * @see alos120Zmanis
     * @see tzais120
     * @see tzais26Degrees
     */
    @Deprecated("This method should be used <em>lechumra</em> only since it returns a very late time, and if used\n" + "	          <em>lekula</em> can result in <em>chillul Shabbos</em> etc. There is no current plan to remove this\n" + "	          method from the API, and this deprecation is intended to alert developers of the danger of using it.\n" + "	  \n" + "	  ")  // (forRemoval=false) // add back once Java 9 is the minimum supported version
    val tzais120Zmanis: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.TZAIS,
                ZmanCalculationMethod.Relationship(ZmanType.TZAIS occurs 120.minutes.zmaniyos after ZmanType.SHKIAH),
                UsesElevation.IF_SET,
            ),
        ) {
            getZmanisBasedOffset(2.0)
        }

    /**
     * This calculates the time of *tzais* at the point when the sun is 16.1 below the horizon. This is
     * the sun's dip below the horizon 72 minutes after sunset according Rabbeinu Tam's calculation of *tzais*
     * [around the equinox / equilux](https://kosherjava.com/2022/01/12/equinox-vs-equilux-zmanim-calculations/) in
     * Jerusalem. The question of equinox VS equilux is complex, with Rabbi Meir Posen in the [Ohr Meir](https://www.worldcat.org/oclc/956316270) of the opinion that the equilux should be used. See
     * Yisrael Vehazmanim vol I, 34:1:4. Rabbi Yedidya Manet in his [Zmanei Halacha Lema'aseh](https://www.nli.org.il/en/books/NNL_ALEPH002542826/NLI) (4th edition part 2, pages
     * and 22 and 24) and Rabbi Yonah Metzbuch (in a letter published by Rabbi Manet) are of the opinion that the
     * astronomical equinox should be used. The difference adds up to about 9 seconds, too trivial to make much of a
     * difference. For information on how this is calculated see the comments on [alos16Point1Degrees].
     *
     * @return the `Date` representing the time. If the calculation can't be computed such as northern and
     * southern locations even south of the Arctic Circle and north of the Antarctic Circle where the sun may
     * not reach low enough below the horizon for this calculation, a null will be returned. See detailed
     * explanation on top of the [AstronomicalCalendar] documentation.
     * @see tzais72
     * @see alos16Point1Degrees
     */
    val tzais16Point1Degrees: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.TZAIS,
                16.1F.degrees,
                UsesElevation.IF_SET,
            ),
        ) {
            getSunsetOffsetByDegrees(ZENITH_16_POINT_1)
        }

    /**
     * This method should be used *lechumra* only and returns *tzais* based on when the sun is 26
     * below the horizon.For information on how this is calculated see the comments on [alos26Degrees].
     * Since the *zman* is extremely late and at a point when it is long past the 18 point where the
     * darkest point is reached, it should only be used *lechumra* such as delaying the start of nighttime
     * *mitzvos*.
     *
     * @return the `Date` representing the time. If the calculation can't be computed such as northern and
     * southern locations even south of the Arctic Circle and north of the Antarctic Circle where the sun may
     * not reach low enough below the horizon for this calculation, a null will be returned. See detailed
     * explanation on top of the [AstronomicalCalendar] documentation.
     * @see tzais120
     * @see alos26Degrees
     */
    @Deprecated("This method should be used <em>lechumra</em> only since it returns a very late time, and if used\n" + "	          <em>lekula</em> can result in <em>chillul Shabbos</em> etc. There is no current plan to remove this\n" + "	          method from the API, and this deprecation is intended to alert developers of the danger of using it.\n" + "	  \n" + "	  ")  // (forRemoval=false) // add back once Java 9 is the minimum supported version
    val tzais26Degrees: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.TZAIS,
                26F.degrees,
                UsesElevation.IF_SET,
            ),
        ) {
            getSunsetOffsetByDegrees(ZENITH_26_DEGREES)
        }

    /**
     * For information on how this is calculated see the comments on [alos18Degrees]
     *
     * @return the `Date` representing the time. If the calculation can't be computed such as northern and
     * southern locations even south of the Arctic Circle and north of the Antarctic Circle where the sun may
     * not reach low enough below the horizon for this calculation, a null will be returned. See detailed
     * explanation on top of the [AstronomicalCalendar] documentation.
     * @see alos18Degrees
     */
    val tzais18Degrees: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.TZAIS,
                18F.degrees,
                UsesElevation.IF_SET,
            ),
        ) {
            getSunsetOffsetByDegrees(ASTRONOMICAL_ZENITH)
        }

    /**
     * For information on how this is calculated see the comments on [alos19Point8Degrees]
     *
     * @return the `Date` representing the time. If the calculation can't be computed such as northern and
     * southern locations even south of the Arctic Circle and north of the Antarctic Circle where the sun may
     * not reach low enough below the horizon for this calculation, a null will be returned. See detailed
     * explanation on top of the [AstronomicalCalendar] documentation.
     * @see tzais90
     * @see alos19Point8Degrees
     */
    val tzais19Point8Degrees: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.TZAIS,
                19.8F.degrees,
                UsesElevation.IF_SET,
            ),
        ) {
            getSunsetOffsetByDegrees(ZENITH_19_POINT_8)
        }

    /**
     * A method to return *tzais* (dusk) calculated as 96 minutes after sea level sunset. For information on how
     * this is calculated see the comments on [alos96].
     *
     * @return the `Date` representing the time. If the calculation can't be computed such as in the Arctic
     * Circle where there is at least one day a year where the sun does not rise, and one where it does not set,
     * a null will be returned. See detailed explanation on top of the [AstronomicalCalendar]
     * documentation.
     * @see alos96
     */
    val tzais96: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.TZAIS,
                ZmanCalculationMethod.Relationship(ZmanType.TZAIS occurs 96.minutes after ZmanType.SHKIAH),
                UsesElevation.IF_SET,
            ),
        ) {
            getTimeOffset(
                elevationAdjustedSunset, 96 * MINUTE_MILLIS
            )
        }

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
    val fixedLocalChatzos: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.CHATZOS_HAYOM, ZmanCalculationMethod.FixedLocalChatzos,//TODO
                UsesElevation.NEVER, listOf(
                    ZmanAuthority.ARUCH_HASHULCHAN,
                    ZmanAuthority.FEINSTEIN,
                )
            )
        ) {
            getTimeOffset(
                getDateFromTime(
                    12.0 - geoLocation.timeZone.rawOffset / HOUR_MILLIS.toDouble(), true
                ), -geoLocation.localMeanTimeOffset
            )
        }

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
     * @see sofZmanKidushLevanaBetweenMoldos
     * @see sofZmanKidushLevana15Days
     * @see JewishCalendar.getSofZmanKidushLevanaBetweenMoldos
     */
    fun getSofZmanKidushLevanaBetweenMoldos(alos: Instant?, tzais: Instant?): Instant? {

        // Do not calculate for impossible dates, but account for extreme cases. In the extreme case of Rapa Iti in French
        // Polynesia on Dec 2027 when kiddush Levana 3 days can be said on *Rosh Chodesh*, the sof zman Kiddush Levana
        // will be on the 12th of the Teves. In the case of Anadyr, Russia on Jan, 2071, sof zman Kiddush Levana between the
        // moldos will occur is on the night of 17th of Shevat. See Rabbi Dovid Heber's Shaarei Zmanim chapter 4 (pages 28 and 32).
        if (jewishCalendar.jewishDayOfMonth < 11 || jewishCalendar.jewishDayOfMonth > 16) {
            return null
        }
        return getMoladBasedTime(
            jewishCalendar.sofZmanKidushLevanaBetweenMoldos, alos, tzais, false
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
        return if (!(moladBasedTime < midnightLastNight.toInstant(tz) || moladBasedTime > midnightTonight.toInstant(
                tz
            ))
        ) if (alos != null || tzais != null) if (techila && !(tzais != null && moladBasedTime < tzais || alos != null && moladBasedTime > alos)) tzais
        else alos
        else moladBasedTime
        else null
    }

    /**
     * Returns the latest time of Kiddush Levana according to the [Maharil's](https://en.wikipedia.org/wiki/Yaakov_ben_Moshe_Levi_Moelin) opinion that it is calculated as
     * halfway between *molad* and *molad*. This adds half the 29 days, 12 hours and 793 chalakim time between
     * *molad* and *molad* (14 days, 18 hours, 22 minutes and 666 milliseconds) to the month's *molad*.
     * The *sof zman Kiddush Levana* will be returned even if it occurs during the day. To limit the time to between
     * *tzais* and *alos*, see [JewishCalendar.sofZmanKidushLevanaBetweenMoldos].
     *
     * @return the Date representing the moment halfway between molad and molad. If the time occurs between
     * *alos* and *tzais*, *alos* will be returned
     * @see sofZmanKidushLevanaBetweenMoldos
     * @see sofZmanKidushLevana15Days
     * @see JewishCalendar.sofZmanKidushLevanaBetweenMoldos
     */
    val sofZmanKidushLevanaBetweenMoldos: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.SOF_ZMAN_KIDDUSH_LEVANA, ZmanAuthority.MAHARIL
            )
        ) {
            getSofZmanKidushLevanaBetweenMoldos(null, null)
        }

    /**
     * Returns the latest time of *Kiddush Levana* calculated as 15 days after the *molad*. This is the
     * opinion brought down in the Shulchan Aruch (Orach Chaim 426). It should be noted that some opinions hold that the
     * [Rema](https://en.wikipedia.org/wiki/Moses_Isserles) who brings down the opinion of the [Maharil's](https://en.wikipedia.org/wiki/Yaakov_ben_Moshe_Levi_Moelin) of calculating
     * [half way between *molad* and *molad*][sofZmanKidushLevanaBetweenMoldos] is of
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
     * @see sofZmanKidushLevanaBetweenMoldos
     * @see JewishCalendar.getSofZmanKidushLevana15Days
     */
    fun getSofZmanKidushLevana15Days(alos: Instant?, tzais: Instant?): Instant? {
        // Do not calculate for impossible dates, but account for extreme cases. In the extreme case of Rapa Iti in
        // French Polynesia on Dec 2027 when kiddush Levana 3 days can be said on *Rosh Chodesh*, the sof zman Kiddush
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
     * [half way between *molad* and *molad*][sofZmanKidushLevanaBetweenMoldos] is of
     * the opinion that the Mechaber agrees to his opinion. Also see the Aruch Hashulchan. For additional details on the subject,
     * See Rabbi Dovid Heber's very detailed write-up in Siman Daled (chapter 4) of [Shaarei
 * Zmanim](https://hebrewbooks.org/53000). The *sof zman Kiddush Levana* will be returned even if it occurs during the day. To limit the time to
     * between *tzais* and *alos*, see [sofZmanKidushLevana15Days].
     *
     * @return the Date representing the moment 15 days after the *molad*. If the time occurs between
     * *alos* and *tzais*, *alos* will be returned
     *
     * @see getSofZmanKidushLevana15Days
     * @see getSofZmanKidushLevanaBetweenMoldos
     * @see JewishCalendar.sofZmanKidushLevana15Days
     */
    val sofZmanKidushLevana15Days: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.SOF_ZMAN_KIDDUSH_LEVANA,
                ZmanCalculationMethod.Relationship(ZmanType.SOF_ZMAN_KIDDUSH_LEVANA occurs 15.days after ZmanType.MOLAD),
                supportingAuthorities = listOf(ZmanAuthority.SHULCHAN_ARUCH),
            )
        ) {
            getSofZmanKidushLevana15Days(null, null)
        }

    /**
     * Returns the earliest time of *Kiddush Levana* according to [Rabbeinu Yonah](https://en.wikipedia.org/wiki/Yonah_Gerondi)'s opinion that it can be said 3 days after the
     * *molad*. The time will be returned even if it occurs during the day when *Kiddush Levana* can't be said.
     * Use [getTchilasZmanKidushLevana3Days] if you want to limit the time to night hours.
     *
     * @return the Date representing the moment 3 days after the molad.
     * @see getTchilasZmanKidushLevana3Days
     * @see getTchilasZmanKidushLevana7Days
     * @see JewishCalendar.tchilasZmanKidushLevana3Days
     */
    val tchilasZmanKidushLevana3Days: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.EARLIEST_KIDDUSH_LEVANA,
                ZmanCalculationMethod.Relationship(ZmanType.EARLIEST_KIDDUSH_LEVANA occurs 3.days after ZmanType.MOLAD),
                supportingAuthorities = listOf(ZmanAuthority.RABEINU_YONAH),
            )
        ) {
            getTchilasZmanKidushLevana3Days(null, null)
        }

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
        // Do not calculate for impossible dates, but account for extreme cases. Tchilas zman kiddush Levana 3 days for
        // the extreme case of Rapa Iti in French Polynesia on Dec 2027 when kiddush Levana 3 days can be said on the evening
        // of the 30th, the second night of Rosh Chodesh. The 3rd day after the *molad* will be on the 4th of the month.
        // In the case of Anadyr, Russia on Jan, 2071, when sof zman kiddush levana is on the 17th of the month, the 3rd day
        // from the molad will be on the 5th day of Shevat. See Rabbi Dovid Heber's Shaarei Zmanim chapter 4 (pages 28 and 32).
        if (jewishCalendar.jewishDayOfMonth in 6..29) {
            return null
        }
        var zman: Instant? =
            getMoladBasedTime(jewishCalendar.tchilasZmanKidushLevana3Days, alos, tzais, true)

        //Get the following month's zman kiddush Levana for the extreme case of Rapa Iti in French Polynesia on Dec 2027 when
        // kiddush Levana can be said on Rosh Chodesh (the evening of the 30th). See Rabbi Dovid Heber's Shaarei Zmanim chapter 4 (page 32)
        if (zman == null && jewishCalendar.jewishDayOfMonth == 30) {
            jewishCalendar.forward(DateTimeUnit.MONTH, 1)
            zman = getMoladBasedTime(jewishCalendar.tchilasZmanKidushLevana3Days, null, null, true)
        }
        return zman
    }

    /**
     * Returns the point in time of *Molad*. For the traditional day of week, hour,
     * minute and chalakim, [JewishCalendar.moladAsInstant] and the not yet completed
     * [com.kosherjava.zmanim.hebrewcalendar.HebrewDateFormatter] that will have formatting for this.
     *
     * @return the Date representing the moment of the molad. If the molad does not occur on this day, a null will be returned.
     *
     * @see .getTchilasZmanKidushLevana3Days
     * @see .getTchilasZmanKidushLevana7Days
     * @see JewishCalendar.moladAsInstant
     */
    val zmanMolad: Zman.DateBased
        get() {
            // Optimize to not calculate for impossible dates, but account for extreme cases. The molad in the extreme case of Rapa
            // Iti in French Polynesia on Dec 2027 occurs on the night of the 27th of Kislev. In the case of Anadyr, Russia on
            // Jan 2071, the molad will be on the 2nd day of Shevat. See Rabbi Dovid Heber's Shaarei Zmanim chapter 4 (pages 28 and 32).
            if (jewishCalendar.jewishDayOfMonth in 3..26) {
                return Zman.DateBased(
                    ZmanDefinition(
                        ZmanType.MOLAD, ZmanAuthority.Unanimous
                    )
                ) { null }
            }
            var molad: Instant? = getMoladBasedTime(jewishCalendar.moladAsInstant, null, null, true)

            // deal with molad that happens on the end of the previous month
            if (molad == null && jewishCalendar.jewishDayOfMonth > 26) {
                jewishCalendar.forward(DateTimeUnit.MONTH, 1)
                molad = getMoladBasedTime(jewishCalendar.moladAsInstant, null, null, true)
            }
            return Zman.DateBased(ZmanDefinition(ZmanType.MOLAD, ZmanAuthority.Unanimous)) { molad }
        }

    /**
     * Used by Molad based *zmanim* to determine if *zmanim* occur during the current day.
     * @see moladBasedTime
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
            localDateTime.date + DatePeriod(days = 1)/*roll to tonight*/, MIDNIGHT
        )

    /**
     * Returns the earliest time of *Kiddush Levana* according to the opinions that it should not be said until 7
     * days after the *molad*. If the time of *tchilas zman Kiddush Levana* occurs during the day (between
     * [*alos*][ZmanimCalendar.getAlos72] and [*tzais*][ZmanimCalendar.getTzais72]) it
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
     * can't be recited. Use [getTchilasZmanKidushLevana7Days] if you want to limit the time to night hours.
     *
     * @return the Date representing the moment 7 days after the molad regardless of it is day or night.
     * @see getTchilasZmanKidushLevana7Days
     * @see JewishCalendar.getTchilasZmanKidushLevana7Days
     * @see getTchilasZmanKidushLevana3Days
     */
    val tchilasZmanKidushLevana7Days: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.EARLIEST_KIDDUSH_LEVANA,
                ZmanCalculationMethod.Relationship(ZmanType.EARLIEST_KIDDUSH_LEVANA occurs 7.days after ZmanType.MOLAD),
            )
        ) {
            getTchilasZmanKidushLevana7Days(null, null)
        }

    /**
     * This method returns the latest time one is allowed eating *chametz* on *Erev Pesach* according to
     * the opinion of the[GRA](https://en.wikipedia.org/wiki/Vilna_Gaon). This time is identical to the [sofZmanTfilaGRA] and is provided as a convenience method for those who are
     * unaware how this *zman* is calculated. This time is 4 hours into the day based on the opinion of the
     * [GRA](https://en.wikipedia.org/wiki/Vilna_Gaon) that the day is calculated from sunrise to sunset.
     * This returns the time 4 * [shaahZmanisGra] after [sea level sunrise][seaLevelSunrise].
     *
     * @see ZmanimCalendar.getShaahZmanisGra
     * @see ZmanimCalendar.getSofZmanTfilaGRA
     * @return the `Date` one is allowed eating *chametz* on *Erev Pesach*. If the calculation
     * can't be computed such as in the Arctic Circle where there is at least one day a year where the sun does
     * not rise, and one where it does not set, a null will be returned. See detailed explanation on top of the
     * [AstronomicalCalendar] documentation.
     */
    val sofZmanAchilasChametzGRA: Zman.DateBased
        get() = Zman.DateBased(
            sofZmanTfilaGRA.definition.copy(type = ZmanType.SOF_ZMAN_ACHILAS_CHAMETZ)
        ) {
            if (jewishCalendar.isErevPesach) sofZmanTfilaGRA.momentOfOccurrence
            else null
        }

    /**
     * This method returns the latest time one is allowed eating *chametz* on *Erev Pesach* according to the
     * opinion of the [Magen Avraham (MGA)](https://en.wikipedia.org/wiki/Avraham_Gombinern) based on *alos*
     * being [72][alos72] minutes before [sunrise]. This time is identical to the
     * [*Sof zman tfilah* MGA 72 minutes][sofZmanTfilaMGA72Minutes]. This time is 4 [*shaos zmaniyos*][shaahZmanisMGA] (temporal hours) after [dawn][alos72] based on the opinion of the MGA that the day is
     * calculated from a [dawn][alos72] of 72 minutes before sunrise to [nightfall][tzais72] of 72 minutes
     * after sunset. This returns the time of 4 * [shaahZmanisMGA] after [dawn][alos72].
     *
     * @return the `Date` of the latest time of eating *chametz*. If the calculation can't be computed such as
     * in the Arctic Circle where there is at least one day a year where the sun does not rise, and one where it
     * does not set), a null will be returned. See detailed explanation on top of the
     * [AstronomicalCalendar] documentation.
     * @see shaahZmanisMGA
     * @see alos72
     * @see sofZmanTfilaMGA72Minutes
     */
    val sofZmanAchilasChametzMGA72Minutes: Zman.DateBased
        get() = Zman.DateBased(
            sofZmanTfilaMGA72Minutes.definition.copy(type = ZmanType.SOF_ZMAN_ACHILAS_CHAMETZ)
        ) {
            if (jewishCalendar.isErevPesach) sofZmanTfilaMGA72Minutes.momentOfOccurrence
            else null
        }

    /**
     * This method returns the latest time one is allowed eating *chametz* on *Erev Pesach* according to the
     * opinion of the[Magen Avraham (MGA)](https://en.wikipedia.org/wiki/Avraham_Gombinern) based on *alos*
     * being [16.1˚][alos16Point1Degrees] before [sunrise]. This time is 4 [][shaahZmanis16Point1Degrees] (solar hours) after [dawn][alos16Point1Degrees]
     * based on the opinion of the MGA that the day is calculated from dawn to nightfall with both being 16.1
     * below sunrise or sunset. This returns the time of 4 [shaahZmanis16Point1Degrees] after
     * [dawn][alos16Point1Degrees].
     *
     * @return the `Date` of the latest time of eating *chametz*. If the calculation can't be computed such
     * as northern and southern locations even south of the Arctic Circle and north of the Antarctic Circle where
     * the sun may not reach low enough below the horizon for this calculation,ZmanCalculationMethod.FixedDuration(72), a null will be returned. See
     * detailed explanation on top of the [AstronomicalCalendar] documentation.
     *
     * @see shaahZmanis16Point1Degrees
     * @see alos16Point1Degrees
     * @see sofZmanTfilaMGA16Point1Degrees
     */
    val sofZmanAchilasChametzMGA16Point1Degrees: Zman.DateBased
        get() = Zman.DateBased(
            sofZmanTfilaMGA16Point1Degrees.definition.copy(type = ZmanType.SOF_ZMAN_ACHILAS_CHAMETZ)
        ) {
            if (jewishCalendar.isErevPesach) sofZmanTfilaMGA16Point1Degrees.momentOfOccurrence
            else null
        }

    /**
     * This method returns the latest time for burning *chametz* on *Erev Pesach* according to the opinion
     * of the [GRA](https://en.wikipedia.org/wiki/Vilna_Gaon). This time is 5 hours into the day based on the
     * opinion of the [GRA](https://en.wikipedia.org/wiki/Vilna_Gaon) that the day is calculated from
     * sunrise to sunset. This returns the time 5 * [shaahZmanisGra] after [sea][seaLevelSunrise].
     *
     * @see ZmanimCalendar.getShaahZmanisGra
     * @return the `Date` of the latest time for burning *chametz* on *Erev Pesach*. If the
     * calculation can't be computed such as in the Arctic Circle where there is at least one day a year where
     * the sun does not rise, and one where it does not set, a null will be returned. See detailed explanation on
     * top of the [AstronomicalCalendar] documentation.
     */
    val sofZmanBiurChametzGRA: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.SOF_ZMAN_BIUR_CHAMETZ,
                ZmanCalculationMethod.Relationship(ZmanType.SOF_ZMAN_BIUR_CHAMETZ occurs 5.hours.zmaniyos after ZmanType.HANAITZ),
                supportingAuthorities = listOf(ZmanAuthority.GRA),
            )
        ) {
            if (jewishCalendar.isErevPesach) getTimeOffset(
                elevationAdjustedSunrise, (shaahZmanisGra.duration * 5).inWholeMilliseconds
            )
            else null
        }

    /**
     * This method returns the latest time for burning *chametz* on *Erev Pesach* according to the opinion of
     * the [Magen Avraham (MGA)](https://en.wikipedia.org/wiki/Avraham_Gombinern) based on *alos*
     * being [72][alos72] minutes before [sunrise]. This time is 5 [shaahZmanisMGA] (temporal hours) after [dawn][alos72] based on the opinion of
     * the MGA that the day is calculated from a [dawn][alos72] of 72 minutes before sunrise to [][tzais72] of 72 minutes after sunset. This returns the time of 5 * [shaahZmanisMGA] after
     * [dawn][alos72].
     *
     * @return the `Date` of the latest time for burning *chametz* on *Erev Pesach*. If the calculation
     * can't be computed such as in the Arctic Circle where there is at least one day a year where the sun does not rise,
     * and one where it does not set), a null will be returned. See detailed explanation on top of the
     * [AstronomicalCalendar] documentation.
     * @see shaahZmanisMGA
     * @see alos72
     */
    val sofZmanBiurChametzMGA72Minutes: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.SOF_ZMAN_BIUR_CHAMETZ,
                ZmanCalculationMethod.DayDefinition.dawnToDusk(ZmanCalculationMethod.FixedDuration._72),
                UsesElevation.NEVER,
                listOf(ZmanAuthority.MGA),
            )
        ) {
            if (jewishCalendar.isErevPesach) getTimeOffset(
                alos72.momentOfOccurrence, (shaahZmanisMGA.duration * 5).inWholeMilliseconds
            )
            else null
        }

    /**
     * This method returns the latest time for burning *chametz* on *Erev Pesach* according to the opinion
     * of the [Magen Avraham (MGA)](https://en.wikipedia.org/wiki/Avraham_Gombinern) based on *alos*
     * being [16.1˚][alos16Point1Degrees] before [sunrise]. This time is 5
     * [*shaos zmaniyos*][shaahZmanis16Point1Degrees] (solar hours) after [dawn][alos16Point1Degrees] based on the opinion of the MGA that the day is calculated from dawn to nightfall with both being 16.1
     * below sunrise or sunset. This returns the time of 5 [shaahZmanis16Point1Degrees] after
     * [dawn][alos16Point1Degrees].
     *
     * @return the `Date` of the latest time for burning *chametz* on *Erev Pesach*. If the calculation
     * can't be computed such as northern and southern locations even south of the Arctic Circle and north of the
     * Antarctic Circle where the sun may not reach low enough below the horizon for this calculation, a null
     * will be returned. See detailed explanation on top of the [AstronomicalCalendar] documentation.
     *
     * @see shaahZmanis16Point1Degrees
     * @see alos16Point1Degrees
     */
    val sofZmanBiurChametzMGA16Point1Degrees: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.SOF_ZMAN_BIUR_CHAMETZ,
                ZmanCalculationMethod.DayDefinition.dawnToDusk(16.1F.degrees),
                UsesElevation.ALWAYS,
                listOf(ZmanAuthority.MGA)
            )
        ) {
            if (jewishCalendar.isErevPesach) getTimeOffset(
                alos16Point1Degrees.momentOfOccurrence,
                (shaahZmanis16Point1Degrees.duration * 5).inWholeMilliseconds
            )
            else null
        }

    /**
     * A method that returns "solar" midnight, or the time when the sun is at its [nadir](https://en.wikipedia.org/wiki/Nadir).
     * **Note:** this method is experimental and might be removed.
     *
     * @return the `Date` of Solar Midnight (chatzos layla). If the calculation can't be computed such as in
     * the Arctic Circle where there is at least one day a year where the sun does not rise, and one where it
     * does not set, a null will be returned. See detailed explanation on top of the
     * [AstronomicalCalendar] documentation.
     */
    val solarMidnight: Zman.DateBased
        get() {
            val clonedCal = ComplexZmanimCalendar(geoLocation)
            val tz = geoLocation.timeZone
            clonedCal.localDateTime =
                this.localDateTime.toInstant(tz).plus(DatePeriod(days = 1), tz).toLocalDateTime(tz)
            val tomorrowChatzos = clonedCal.chatzos
            val thisChatzos = this.chatzos
            //println("Date: ${clonedCal.localDateTime}, tomorrowChatzos: ${tomorrowChatzos.momentOfOccurrence}, thisChatzos: ${thisChatzos.momentOfOccurrence}")
            return Zman.DateBased(
                ZmanDefinition(
                    ZmanType.CHATZOS_HALAYLAH, ZmanAuthority.Unanimous
                )
            ) {
                if (tomorrowChatzos.momentOfOccurrence == null || thisChatzos.momentOfOccurrence == null) null
                else thisChatzos.momentOfOccurrence?.let {
                    tomorrowChatzos.momentOfOccurrence?.minus(it)
                        ?.div(2)?.inWholeMilliseconds?.let { it1 ->
                            getTimeOffset(
                                it, it1
                            )
                        }
                }
            }
        }

    /**
     * A method that returns the [Baal Hatanya](https://en.wikipedia.org/wiki/Shneur_Zalman_of_Liadi) 's
     * *netz amiti* (sunrise) without [elevation adjustment][AstronomicalCalculator.getElevationAdjustment]. This forms the base for the Baal Hatanya's dawn-based calculations that are
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
     * [About Our *Zmanim* Calculations @ Chabad.org](https://www.chabad.org/library/article_cdo/aid/3209349/jewish/About-Our-Zmanim-Calculations.htm).
     *
     * Note: *netz amiti* is used only for calculating certain *zmanim*, and is intentionally unpublished. For
     * practical purposes, daytime *mitzvos* like *shofar* and *lulav* should not be done until after the
     * published time for *netz* / sunrise.
     *
     * @return the `Date` representing the exact sea-level *netz amiti* (sunrise) time. If the calculation can't be
     * computed such as in the Arctic Circle where there is at least one day a year where the sun does not rise, and one
     * where it does not set, a null will be returned. See detailed explanation on top of the page.
     *
     * @see sunrise
     * @see seaLevelSunrise
     * @see sunsetBaalHatanya
     * @see ZENITH_1_POINT_583
     */
    private val sunriseBaalHatanya: Instant? get() = getSunriseOffsetByDegrees(ZENITH_1_POINT_583)

    /**
     * A method that returns the [Baal Hatanya](https://en.wikipedia.org/wiki/Shneur_Zalman_of_Liadi)'s
     * *shkiah amiti* (sunset) without [elevation adjustment][AstronomicalCalculator.getElevationAdjustment]. This forms the base for the Baal Hatanya's dusk-based calculations that are calculated
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
        get() = getSunsetOffsetByDegrees(ZENITH_1_POINT_583)

    /**
     * A method that returns the [Baal Hatanya](https://en.wikipedia.org/wiki/Shneur_Zalman_of_Liadi)'s
     * a *shaah zmanis* ([temporal hour][temporalHour]). This forms the base for the
     * Baal Hatanya's  day  based calculations that are calculated as a 1.583 dip below the horizon after sunset.
     * According to the Baal Hatanya, *shkiah amiti*, true (halachic) sunset, is when the top of the
     * sun's disk disappears from view at an elevation similar to the mountains of Eretz Yisrael.
     * This time is calculated as the point at which the center of the sun's disk is 1.583 degrees below the horizon.
     * A method that returns a *shaah zmanis* ([temporal hour][temporalHour]) calculated
     * based on the [Baal Hatanya](https://en.wikipedia.org/wiki/Shneur_Zalman_of_Liadi)'s *netz
     * amiti* and *shkiah amiti* using a dip of 1.583 below the sea level horizon. This calculation divides
     * the day based on the opinion of the Baal Hatanya that the day runs from [netz amiti][sunriseBaalHatanya]
     * to [*shkiah amiti*][sunsetBaalHatanya]. The calculations are based on a day from [sunriseBaalHatanya] to [sea level *shkiah amiti*][sunsetBaalHatanya].
     * The day is split into 12 equal parts with each one being a *shaah zmanis*. This method is similar to [][temporalHour], but all calculations are based on a sea level sunrise and sunset.
     * @return the `long` millisecond length of a *shaah zmanis* calculated from
     * [*netz amiti* (sunrise)][sunriseBaalHatanya] to [*shkiah amiti*][sunsetBaalHatanya]. If the calculation can't be computed such as in the Arctic Circle where there is at least one day a
     * year where the sun does not rise, and one where it does not set, [Long.MIN_VALUE] will be returned. See
     * detailed explanation on top of the [AstronomicalCalendar] documentation.
     *
     * @see .temporalHour
     * @see sunriseBaalHatanya
     * @see sunsetBaalHatanya
     * @see ZENITH_1_POINT_583
     */
    val shaahZmanisBaalHatanya: Long get() = getTemporalHour(sunriseBaalHatanya, sunsetBaalHatanya)

    /**
     * Returns the [Baal Hatanya](https://en.wikipedia.org/wiki/Shneur_Zalman_of_Liadi) 's *alos*
     * (dawn) calculated as the time when the sun is 16.9 below the eastern [geometric horizon][AstronomicalCalendar.GEOMETRIC_ZENITH]
     * before [sunrise]. For more information the source of 16.9 see [ZENITH_16_POINT_9].
     *
     * @see ZENITH_16_POINT_9
     *
     * @return The `Date` of dawn. If the calculation can't be computed such as northern and southern
     * locations even south of the Arctic Circle and north of the Antarctic Circle where the sun may not reach
     * low enough below the horizon for this calculation, a null will be returned. See detailed explanation on
     * top of the [AstronomicalCalendar] documentation.
     */
    val alosBaalHatanya: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.ALOS,
                ZmanCalculationMethod.Degrees(16.9F),
                supportingAuthorities = listOf(ZmanAuthority.BAAL_HATANYA)
            )
        ) {
            getSunriseOffsetByDegrees(ZENITH_16_POINT_9)
        }

    /**
     * This method returns the latest *zman krias shema* (time to recite Shema in the morning). This time is 3
     * [*shaos zmaniyos*][shaahZmanisBaalHatanya] (solar hours) after [*netz amiti* (sunrise)][sunriseBaalHatanya] based on the opinion of the Baal Hatanya that the day is calculated from
     * sunrise to sunset. This returns the time 3 * [shaahZmanisBaalHatanya] after [*netz amiti* (sunrise)][sunriseBaalHatanya].
     *
     * @see ZmanimCalendar.getSofZmanShma
     * @see shaahZmanisBaalHatanya
     * @return the `Date` of the latest *zman shema* according to the Baal Hatanya. If the calculation
     * can't be computed such as in the Arctic Circle where there is at least one day a year where the sun does
     * not rise, and one where it does not set, a null will be returned. See detailed explanation on top of the
     * [AstronomicalCalendar] documentation.
     */
    val sofZmanShmaBaalHatanya: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.SOF_ZMAN_KRIAS_SHEMA, ZmanCalculationMethod.DayDefinition.sunriseToSunset(
                    ZmanCalculationMethod.Degrees(
                        1.583F
                    )
                ), UsesElevation.ALWAYS, listOf(ZmanAuthority.BAAL_HATANYA)
            )
        ) {
            getSofZmanShma(
                sunriseBaalHatanya, sunsetBaalHatanya
            )
        }

    /**
     * This method returns the latest *zman tfilah* (time to recite the morning prayers). This time is 4
     * hours into the day based on the opinion of the Baal Hatanya that the day is
     * calculated from sunrise to sunset. This returns the time 4 * [shaahZmanisBaalHatanya] after
     * [*netz amiti* (sunrise)][sunriseBaalHatanya].
     *
     * @see ZmanimCalendar.getSofZmanTfila
     * @see shaahZmanisBaalHatanya
     * @return the `Date` of the latest *zman tfilah*. If the calculation can't be computed such as in
     * the Arctic Circle where there is at least one day a year where the sun does not rise, and one where it does
     * not set, a null will be returned. See detailed explanation on top of the [AstronomicalCalendar]
     * documentation.
     */
    val sofZmanTfilaBaalHatanya: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.SOF_ZMAN_TEFILLAH, ZmanCalculationMethod.DayDefinition.sunriseToSunset(
                    ZmanCalculationMethod.Degrees(
                        1.583F
                    )
                ), UsesElevation.ALWAYS, listOf(ZmanAuthority.BAAL_HATANYA)
            )
        ) {
            getSofZmanTfila(
                sunriseBaalHatanya, sunsetBaalHatanya
            )
        }

    /**
     * This method returns the latest time one is allowed eating *chametz* on *Erev Pesach* according to the
     * opinion of the Baal Hatanya. This time is identical to the [*Sof zman tfilah*][sofZmanTfilaBaalHatanya]. This time is 4 hours into the day based on the opinion of the Baal Hatanya that the day is calculated
     * from sunrise to sunset. This returns the time 4 [shaahZmanisBaalHatanya] after
     * [*netz amiti* (sunrise)][sunriseBaalHatanya].
     *
     * @see shaahZmanisBaalHatanya
     * @see sofZmanTfilaBaalHatanya
     * @return the `Date` one is allowed eating *chametz* on *Erev Pesach*. If the calculation can't
     * be computed such as in the Arctic Circle where there is at least one day a year where the sun does not rise,
     * and one where it does not set, a null will be returned. See detailed explanation on top of the
     * [AstronomicalCalendar] documentation.
     */
    val sofZmanAchilasChametzBaalHatanya: Zman.DateBased
        get() = Zman.DateBased(
            sofZmanTfilaBaalHatanya.definition.copy(type = ZmanType.SOF_ZMAN_ACHILAS_CHAMETZ)
        ) {
            if (jewishCalendar.isErevPesach) sofZmanTfilaBaalHatanya.momentOfOccurrence
            else null
        }

    /**
     * This method returns the latest time for burning *chametz* on *Erev Pesach* according to the opinion of
     * the Baal Hatanya. This time is 5 hours into the day based on the opinion of the Baal Hatanya that the day is calculated
     * from sunrise to sunset. This returns the time 5 * [shaahZmanisBaalHatanya] after
     * [*netz amiti* (sunrise)][sunriseBaalHatanya].
     *
     * @see shaahZmanisBaalHatanya
     * @return the `Date` of the latest time for burning *chametz* on *Erev Pesach*. If the calculation
     * can't be computed such as in the Arctic Circle where there is at least one day a year where the sun does not rise,
     * and one where it does not set, a null will be returned. See detailed explanation on top of the
     * [AstronomicalCalendar] documentation.
     */
    val sofZmanBiurChametzBaalHatanya: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.SOF_ZMAN_BIUR_CHAMETZ, ZmanCalculationMethod.DayDefinition.sunriseToSunset(
                    ZmanCalculationMethod.Degrees(
                        1.583F
                    )
                ), UsesElevation.ALWAYS, listOf(ZmanAuthority.BAAL_HATANYA)
            )
        ) {
            if (jewishCalendar.isErevPesach) getTimeOffset(
                sunriseBaalHatanya, shaahZmanisBaalHatanya * 5
            )
            else null
        }

    /**
     * This method returns the time of *mincha gedola*. *Mincha gedola* is the earliest time one can pray
     * *mincha*. The [Rambam](https://en.wikipedia.org/wiki/Maimonides) is of the opinion that it is
     * better to delay *mincha* until [*mincha ketana*][.getMinchaKetanaBaalHatanya] while the
     * [Ra"sh](https://en.wikipedia.org/wiki/Asher_ben_Jehiel),
     * [Tur](https://en.wikipedia.org/wiki/Jacob_ben_Asher), [GRA](https://en.wikipedia.org/wiki/Vilna_Gaon) and others are of the opinion that *mincha* can be prayed
     * *lechatchila* starting at *mincha gedola*. This is calculated as 6.5 [sea level solar hours][shaahZmanisBaalHatanya] after [*netz amiti* (sunrise)][sunriseBaalHatanya]. This calculation is based
     * on the opinion of the Baal Hatanya that the day is calculated from sunrise to sunset. This returns the time 6.5
     * * [shaahZmanisBaalHatanya] after [*netz amiti* (&quot;real&quot; sunrise)][sunriseBaalHatanya].
     *
     * @see .getMinchaGedola
     * @see shaahZmanisBaalHatanya
     * @see .getMinchaKetanaBaalHatanya
     * @return the `Date` of the time of *mincha gedola*. If the calculation can't be computed such as
     * in the Arctic Circle where there is at least one day a year where the sun does not rise, and one where it
     * does not set, a null will be returned. See detailed explanation on top of the [AstronomicalCalendar]
     * documentation.
     */
    val minchaGedolaBaalHatanya: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.MINCHA_GEDOLAH, ZmanCalculationMethod.DayDefinition.sunriseToSunset(
                    ZmanCalculationMethod.Degrees(
                        1.583F
                    )
                ), UsesElevation.ALWAYS, listOf(ZmanAuthority.BAAL_HATANYA)
            )
        ) {
            getMinchaGedola(sunriseBaalHatanya, sunsetBaalHatanya)
        }

    /**
     * This is a convenience method that returns the later of [minchaGedolaBaalHatanya] and
     * [minchaGedola30Minutes]. In the winter when 1/2 of a [*shaah zmanis*][shaahZmanisBaalHatanya] is less than 30 minutes [minchaGedola30Minutes] will be returned, otherwise
     * [minchaGedolaBaalHatanya] will be returned.
     *
     * @return the `Date` of the later of [minchaGedolaBaalHatanya] and [minchaGedola30Minutes].
     * If the calculation can't be computed such as in the Arctic Circle where there is at least one day a year
     * where the sun does not rise, and one where it does not set, a null will be returned. See detailed
     * explanation on top of the [AstronomicalCalendar] documentation.
     */
    val minchaGedolaBaalHatanyaGreaterThan30: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.MINCHA_GEDOLAH, ZmanCalculationMethod.LaterOf(
                    minchaGedola30Minutes.definition, minchaGedolaBaalHatanya.definition
                ), UsesElevation.ALWAYS, listOf(ZmanAuthority.BAAL_HATANYA)
            )
        ) {
            maxOf(
                minchaGedola30Minutes.momentOfOccurrence, minchaGedolaBaalHatanya.momentOfOccurrence
            )
        }

    /**
     * This method returns the time of *mincha ketana*. This is the preferred earliest time to pray
     * *mincha* in the opinion of the [Rambam](https://en.wikipedia.org/wiki/Maimonides) and others.
     * For more information on this see the documentation on [*mincha gedola*][.getMinchaGedolaBaalHatanya].
     * This is calculated as 9.5 [sea level solar hours][shaahZmanisBaalHatanya] after [*netz amiti* (sunrise)][sunriseBaalHatanya]. This calculation is calculated based on the opinion of the Baal Hatanya that the
     * day is calculated from sunrise to sunset. This returns the time 9.5 * [shaahZmanisBaalHatanya] after [sunriseBaalHatanya].
     *
     * @see .getMinchaKetana
     * @see shaahZmanisBaalHatanya
     * @see .getMinchaGedolaBaalHatanya
     * @return the `Date` of the time of *mincha ketana*. If the calculation can't be computed such as
     * in the Arctic Circle where there is at least one day a year where the sun does not rise, and one where it
     * does not set, a null will be returned. See detailed explanation on top of the [AstronomicalCalendar]
     * documentation.
     */
    val minchaKetanaBaalHatanya: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.MINCHA_KETANAH, ZmanCalculationMethod.DayDefinition.sunriseToSunset(
                    ZmanCalculationMethod.Degrees(
                        1.583F
                    )
                ), UsesElevation.ALWAYS, listOf(ZmanAuthority.BAAL_HATANYA)
            )
        ) {
            getMinchaKetana(sunriseBaalHatanya, sunsetBaalHatanya)
        }

    /**
     * This method returns the time of *plag hamincha*. This is calculated as 10.75 hours after sunrise. This
     * calculation is based on the opinion of the Baal Hatanya that the day is calculated
     * from sunrise to sunset. This returns the time 10.75 * [shaahZmanisBaalHatanya] after
     * [*netz amiti* (sunrise)][sunriseBaalHatanya].
     *
     * @see .getPlagHamincha
     * @return the `Date` of the time of *plag hamincha*. If the calculation can't be computed such as
     * in the Arctic Circle where there is at least one day a year where the sun does not rise, and one where it
     * does not set, a null will be returned. See detailed explanation on top of the
     * [AstronomicalCalendar] documentation.
     */
    val plagHaminchaBaalHatanya: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.PLAG_HAMINCHA, ZmanCalculationMethod.DayDefinition.sunriseToSunset(
                    ZmanCalculationMethod.Degrees(
                        1.583F
                    )
                ), UsesElevation.ALWAYS, listOf(ZmanAuthority.BAAL_HATANYA)
            )
        ) {
            getPlagHamincha(sunriseBaalHatanya, sunsetBaalHatanya)
        }

    /**
     * A method that returns *tzais* (nightfall) when the sun is 6 below the western geometric horizon
     * (90) after [sunset][sunset]. For information on the source of this calculation see
     * [ZENITH_6_DEGREES].
     *
     * @return The `Date` of nightfall. If the calculation can't be computed such as northern and southern
     * locations even south of the Arctic Circle and north of the Antarctic Circle where the sun may not reach
     * low enough below the horizon for this calculation, a null will be returned. See detailed explanation on
     * top of the [AstronomicalCalendar] documentation.
     * @see ZENITH_6_DEGREES
     */
    val tzaisBaalHatanya: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.TZAIS,
                ZmanCalculationMethod.Degrees(6F),
                UsesElevation.ALWAYS,
                listOf(ZmanAuthority.BAAL_HATANYA)
            )
        ) {
            getSunsetOffsetByDegrees(ZENITH_6_DEGREES)
        }

    /**
     * A utility methos to calculate zmanim based on [Rav Moshe Feinstein](https://en.wikipedia.org/wiki/Moshe_Feinstein)
     * as calculated in [MTJ](https://en.wikipedia.org/wiki/Mesivtha_Tifereth_Jerusalem), [Yeshiva of Staten Island](https://en.wikipedia.org/wiki/Mesivtha_Tifereth_Jerusalem), and Camp Yeshiva
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
     * @see ComplexZmanimCalendar.fixedLocalChatzos
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
     * at *alos* defined as [18˚][alos18Degrees] and ends at [fixed local]fixedLocalChatzos]. *Sof Zman Shema* is 3 *shaos zmaniyos* (solar hours) after *alos* or half of this half-day.
     *
     * @return the `Date` of the latest *zman krias shema*. If the calculation can't be computed such
     * as northern and southern locations even south of the Arctic Circle and north of the Antarctic Circle
     * where the sun may not reach low enough below the horizon for this calculation, a null will be returned.
     * See detailed explanation on top of the [AstronomicalCalendar] documentation.
     * @see alos18Degrees
     * @see fixedLocalChatzos
     * @see .getFixedLocalChatzosBasedZmanim
     */
    val sofZmanShmaMGA18DegreesToFixedLocalChatzos: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.SOF_ZMAN_KRIAS_SHEMA,
                ZmanCalculationMethod.DayDefinition.DAWN_18_TO_FIXED_LOCAL_CHATZOS,
                UsesElevation.ALWAYS,
                listOf(ZmanAuthority.MGA accordingTo ZmanAuthority.FEINSTEIN)
            ),
        ) {
            getFixedLocalChatzosBasedZmanim(
                alos18Degrees.momentOfOccurrence, fixedLocalChatzos.momentOfOccurrence, 3.0
            )
        }

    /**
     * This method returns [Rav Moshe Feinstein's](https://en.wikipedia.org/wiki/Moshe_Feinstein) opinion of the
     * calculation of *sof zman krias shema* (latest time to recite *Shema* in the morning) according to the
     * opinion of the [Magen Avraham (MGA)](https://en.wikipedia.org/wiki/Avraham_Gombinern) that the
     * day is calculated from dawn to nightfall, but calculated using the first half of the day only. The half a day starts
     * at *alos* defined as [16.1˚][alos16Point1Degrees] and ends at [fixed local]fixedLocalChatzos]. *Sof Zman Shema* is 3 *shaos zmaniyos* (solar hours) after this *alos* or half of this half-day.
     *
     * @return the `Date` of the latest *zman krias shema*. If the calculation can't be computed such
     * as northern and southern locations even south of the Arctic Circle and north of the Antarctic Circle
     * where the sun may not reach low enough below the horizon for this calculation, a null will be returned.
     * See detailed explanation on top of the [AstronomicalCalendar] documentation.
     * @see alos16Point1Degrees
     * @see fixedLocalChatzos
     * @see .getFixedLocalChatzosBasedZmanim
     */
    val sofZmanShmaMGA16Point1DegreesToFixedLocalChatzos: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.SOF_ZMAN_KRIAS_SHEMA,
                ZmanCalculationMethod.DayDefinition.DAWN_16_1_TO_FIXED_LOCAL_CHATZOS,
                UsesElevation.ALWAYS,
                listOf(ZmanAuthority.MGA accordingTo ZmanAuthority.FEINSTEIN)
            )
        ) {
            getFixedLocalChatzosBasedZmanim(
                alos16Point1Degrees.momentOfOccurrence, fixedLocalChatzos.momentOfOccurrence, 3.0
            )
        }

    /**
     * This method returns [Rav Moshe Feinstein's](https://en.wikipedia.org/wiki/Moshe_Feinstein) opinion of the
     * calculation of *sof zman krias shema* (latest time to recite *Shema* in the morning) according to the
     * opinion of the [Magen Avraham (MGA)](https://en.wikipedia.org/wiki/Avraham_Gombinern) that the
     * day is calculated from dawn to nightfall, but calculated using the first half of the day only. The half a day starts
     * at *alos* defined as [90 minutes before sunrise][alos90] and ends at [fixed local chatzos]fixedLocalChatzos]. *Sof Zman Shema* is 3 *shaos zmaniyos* (solar hours) after this *alos* or
     * half of this half-day.
     *
     * @return the `Date` of the latest *zman krias shema*. If the calculation can't be computed such
     * as northern and southern locations even south of the Arctic Circle and north of the Antarctic Circle
     * where the sun may not reach low enough below the horizon for this calculation, a null will be returned.
     * See detailed explanation on top of the [AstronomicalCalendar] documentation.
     * @see alos90
     * @see fixedLocalChatzos
     * @see .getFixedLocalChatzosBasedZmanim
     */
    val sofZmanShmaMGA90MinutesToFixedLocalChatzos: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.SOF_ZMAN_KRIAS_SHEMA,
                ZmanCalculationMethod.DayDefinition.DAWN_90_MINUTES_TO_FIXED_LOCAL_CHATZOS,
                UsesElevation.ALWAYS,
                listOf(ZmanAuthority.MGA accordingTo ZmanAuthority.FEINSTEIN)
            )
        ) {
            getFixedLocalChatzosBasedZmanim(
                alos90.momentOfOccurrence, fixedLocalChatzos.momentOfOccurrence, 3.0
            )
        }

    /**
     * This method returns [Rav Moshe Feinstein's](https://en.wikipedia.org/wiki/Moshe_Feinstein) opinion of the
     * calculation of *sof zman krias shema* (latest time to recite *Shema* in the morning) according to the
     * opinion of the [Magen Avraham (MGA)](https://en.wikipedia.org/wiki/Avraham_Gombinern) that the
     * day is calculated from dawn to nightfall, but calculated using the first half of the day only. The half a day starts
     * at *alos* defined as [72 minutes before sunrise][alos72] and ends at [fixed local chatzos]fixedLocalChatzos]. *Sof Zman Shema* is 3 *shaos zmaniyos* (solar hours) after this *alos* or
     * half of this half-day.
     *
     * @return the `Date` of the latest *zman krias shema*. If the calculation can't be computed such
     * as northern and southern locations even south of the Arctic Circle and north of the Antarctic Circle
     * where the sun may not reach low enough below the horizon for this calculation, a null will be returned.
     * See detailed explanation on top of the [AstronomicalCalendar] documentation.
     * @see alos72
     * @see fixedLocalChatzos
     * @see .getFixedLocalChatzosBasedZmanim
     */
    val sofZmanShmaMGA72MinutesToFixedLocalChatzos: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.SOF_ZMAN_KRIAS_SHEMA,
                ZmanCalculationMethod.DayDefinition.DAWN_72_MINUTES_TO_FIXED_LOCAL_CHATZOS,
                UsesElevation.ALWAYS,
                listOf(ZmanAuthority.MGA accordingTo ZmanAuthority.FEINSTEIN)
            )
        ) {
            getFixedLocalChatzosBasedZmanim(
                alos72.momentOfOccurrence, fixedLocalChatzos.momentOfOccurrence, 3.0
            )
        }

    /**
     * This method returns [Rav Moshe Feinstein's](https://en.wikipedia.org/wiki/Moshe_Feinstein) opinion of the
     * calculation of *sof zman krias shema* (latest time to recite *Shema* in the morning) according to the
     * opinion of the [GRA](https://en.wikipedia.org/wiki/Vilna_Gaon) that the day is calculated from
     * sunrise to sunset, but calculated using the first half of the day only. The half a day starts at [sunrise][sunrise] and ends at [fixed local chatzos]fixedLocalChatzos]. *Sof zman Shema* is 3 *shaos
     * zmaniyos* (solar hours) after sunrise or half of this half-day.
     *
     * @return the `Date` of the latest *zman krias shema*. If the calculation can't be computed such
     * as northern and southern locations even south of the Arctic Circle and north of the Antarctic Circle
     * where the sun may not reach low enough below the horizon for this calculation, a null will be returned.
     * See detailed explanation on top of the [AstronomicalCalendar] documentation.
     * @see sunrise
     * @see fixedLocalChatzos
     * @see .getFixedLocalChatzosBasedZmanim
     */
    val sofZmanShmaGRASunriseToFixedLocalChatzos: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.SOF_ZMAN_KRIAS_SHEMA,
                ZmanCalculationMethod.DayDefinition.SUNRISE_TO_FIXED_LOCAL_CHATZOS,
                UsesElevation.ALWAYS,
                listOf(ZmanAuthority.GRA accordingTo ZmanAuthority.FEINSTEIN)
            )
        ) {
            getFixedLocalChatzosBasedZmanim(sunrise, fixedLocalChatzos.momentOfOccurrence, 3.0)
        }

    /**
     * This method returns [Rav Moshe Feinstein's](https://en.wikipedia.org/wiki/Moshe_Feinstein) opinion of the
     * calculation of *sof zman tfila* (*zman tfilah* (the latest time to recite the morning prayers))
     * according to the opinion of the [GRA](https://en.wikipedia.org/wiki/Vilna_Gaon) that the day is
     * calculated from sunrise to sunset, but calculated using the first half of the day only. The half a day starts at
     * [sunrise] and ends at [fixed local chatzos]fixedLocalChatzos]. *Sof zman tefila*
     * is 4 *shaos zmaniyos* (solar hours) after sunrise or 2/3 of this half-day.
     *
     * @return the `Date` of the latest *zman krias shema*. If the calculation can't be computed such
     * as northern and southern locations even south of the Arctic Circle and north of the Antarctic Circle
     * where the sun may not reach low enough below the horizon for this calculation, a null will be returned.
     * See detailed explanation on top of the [AstronomicalCalendar] documentation.
     * @see sunrise
     * @see fixedLocalChatzos
     * @see .getFixedLocalChatzosBasedZmanim
     */
    val sofZmanTfilaGRASunriseToFixedLocalChatzos: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.SOF_ZMAN_TEFILLAH,
                ZmanCalculationMethod.DayDefinition.SUNRISE_TO_FIXED_LOCAL_CHATZOS,
                UsesElevation.ALWAYS,
                listOf(ZmanAuthority.GRA accordingTo ZmanAuthority.FEINSTEIN)
            )
        ) {
            getFixedLocalChatzosBasedZmanim(sunrise, fixedLocalChatzos.momentOfOccurrence, 4.0)
        }

    /**
     * This method returns [Rav Moshe Feinstein's](https://en.wikipedia.org/wiki/Moshe_Feinstein) opinion of
     * the calculation of *mincha gedola*, the earliest time one can pray *mincha* [GRA](https://en.wikipedia.org/wiki/Vilna_Gaon) that is 30 minutes after [fixed][fixedLocalChatzos].
     *
     * @return the `Date` of the time of *mincha gedola*. If the calculation can't be computed such as
     * in the Arctic Circle where there is at least one day a year where the sun does not rise, and one where it
     * does not set, a null will be returned. See detailed explanation on top of the [AstronomicalCalendar]
     * documentation.
     *
     * @see .getMinchaGedola
     * @see fixedLocalChatzos
     * @see .getMinchaKetanaGRAFixedLocalChatzosToSunset
     */
    val minchaGedolaGRAFixedLocalChatzos30Minutes: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.MINCHA_GEDOLAH,
                ZmanCalculationMethod.Relationship(ZmanType.MINCHA_GEDOLAH occurs 30.minutes after fixedLocalChatzos),
                UsesElevation.ALWAYS,
                supportingAuthorities = listOf(ZmanAuthority.GRA accordingTo ZmanAuthority.FEINSTEIN),
            ),
        ) {
            getTimeOffset(fixedLocalChatzos.momentOfOccurrence, MINUTE_MILLIS * 30)
        }

    /**
     * This method returns [Rav Moshe Feinstein's](https://en.wikipedia.org/wiki/Moshe_Feinstein) opinion
     * of the calculation of *mincha ketana* (the preferred time to recite the *mincha prayers* according to
     * the opinion of the [Rambam](https://en.wikipedia.org/wiki/Maimonides) and others) calculated according
     * to the [GRA](https://en.wikipedia.org/wiki/Vilna_Gaon) that is 3.5 *shaos zmaniyos* (solar
     * hours) after [fixed local chatzos][fixedLocalChatzos].
     *
     * @return the `Date` of the time of *mincha gedola*. If the calculation can't be computed such as
     * in the Arctic Circle where there is at least one day a year where the sun does not rise, and one where it
     * does not set, a null will be returned. See detailed explanation on top of the [AstronomicalCalendar]
     * documentation.
     *
     * @see .getMinchaGedola
     * @see fixedLocalChatzos
     * @see .getMinchaGedolaGRAFixedLocalChatzos30Minutes
     */
    val minchaKetanaGRAFixedLocalChatzosToSunset: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.MINCHA_KETANAH,
                ZmanCalculationMethod.DayDefinition.FIXED_LOCAL_CHATZOS_TO_SUNSET,
                UsesElevation.ALWAYS,
                listOf(
                    ZmanAuthority.GRA accordingTo ZmanAuthority.FEINSTEIN
                ),
                //TODO is this relationship just day definition? ZmanType.MINCHA_KETANAH occurs 3.5.hours after fixedLocalChatzos
            )
        ) {
            getFixedLocalChatzosBasedZmanim(fixedLocalChatzos.momentOfOccurrence, sunset, 3.5)
        }

    /**
     * This method returns [Rav Moshe Feinstein's](https://en.wikipedia.org/wiki/Moshe_Feinstein) opinion
     * of the calculation of *plag hamincha*. This method returns *plag hamincha* calculated according to the
     * [GRA](https://en.wikipedia.org/wiki/Vilna_Gaon) that the day ends at sunset and is 4.75 *shaos
     * zmaniyos* (solar hours) after [fixed local chatzos][fixedLocalChatzos].
     *
     * @return the `Date` of the time of *mincha gedola*. If the calculation can't be computed such as
     * in the Arctic Circle where there is at least one day a year where the sun does not rise, and one where it
     * does not set, a null will be returned. See detailed explanation on top of the [AstronomicalCalendar]
     * documentation.
     *
     * @see .getPlagHamincha
     * @see fixedLocalChatzos
     * @see .getMinchaKetanaGRAFixedLocalChatzosToSunset
     *
     * @see .getMinchaGedolaGRAFixedLocalChatzos30Minutes
     */
    val plagHaminchaGRAFixedLocalChatzosToSunset: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.PLAG_HAMINCHA,
                ZmanCalculationMethod.DayDefinition.FIXED_LOCAL_CHATZOS_TO_SUNSET,
                UsesElevation.ALWAYS,
                listOf(
                    ZmanAuthority.GRA accordingTo ZmanAuthority.FEINSTEIN
                )
            )
        ) {
            getFixedLocalChatzosBasedZmanim(fixedLocalChatzos.momentOfOccurrence, sunset, 4.75)
        }

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
    val tzais50: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.TZAIS,
                ZmanCalculationMethod.Relationship(ZmanType.TZAIS occurs 50.minutes after ZmanType.SHKIAH),
                UsesElevation.IF_SET,
                listOf(
                    ZmanAuthority.FEINSTEIN
                ),
            )
        ) {
            getTimeOffset(
                elevationAdjustedSunset, 50 * MINUTE_MILLIS
            )
        }

    /**
     * A method for calculating *samuch lemincha ketana*, / near *mincha ketana* time that is half an hour before
     * [.getMinchaKetana] or is 9 * [*shaos zmaniyos*][shaahZmanisGra] (solar hours) after [][sunrise] or [sea level sunrise][seaLevelSunrise] (depending on the [isUseElevation]
     * setting), calculated according to the [GRA](https://en.wikipedia.org/wiki/Vilna_Gaon) using a day starting at
     * sunrise and ending at sunset. This is the time that eating or other activity can't begin prior to praying *mincha*.
     * The calculation used is 9 * [shaahZmanis16Point1Degrees] after [*alos*][alos16Point1Degrees]. See the [Mechaber and Mishna Berurah
 * 232](https://hebrewbooks.org/pdfpager.aspx?req=60387&st=&pgnum=294) for details.
     *
     * @see shaahZmanisGra
     * @see samuchLeMinchaKetana16Point1Degrees
     * @return the `Date` of the time of *samuch lemincha ketana*. If the calculation can't be computed such
     * as northern and southern locations even south of the Arctic Circle and north of the Antarctic Circle
     * where the sun may not reach low enough below the horizon for this calculation, a null will be returned.
     * See detailed explanation on top of the [AstronomicalCalendar] documentation.
     */
    val samuchLeMinchaKetanaGRA: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.SAMUCH_LEMINCHA_KETANA,
                ZmanCalculationMethod.DayDefinition.SUNRISE_TO_SUNSET,
                UsesElevation.IF_SET,
                listOf(ZmanAuthority.GRA)
            ),
        ) {
            getSamuchLeMinchaKetana(elevationAdjustedSunrise, elevationAdjustedSunset)
        }

    /**
     * A method for calculating *samuch lemincha ketana*, / near *mincha ketana* time that is half an hour before
     * [minchaGedola16Point1Degrees]  or 9 * *shaos zmaniyos* (temporal hours) after the start of the day,
     * calculated using a day starting and ending 16.1 below the horizon. This is the time that eating or other activity
     * can't begin prior to praying *mincha*. The calculation used is 9 * [shaahZmanis16Point1Degrees] after
     * [*alos* 16.1˚][alos16Point1Degrees]. See the [Mechaber and Mishna Berurah 232](https://hebrewbooks.org/pdfpager.aspx?req=60387&st=&pgnum=294).
     *
     * @see shaahZmanis16Point1Degrees
     * @return the `Date` of the time of *samuch lemincha ketana*. If the calculation can't be computed such
     * as northern and southern locations even south of the Arctic Circle and north of the Antarctic Circle
     * where the sun may not reach low enough below the horizon for this calculation, a null will be returned.
     * See detailed explanation on top of the [AstronomicalCalendar] documentation.
     */
    val samuchLeMinchaKetana16Point1Degrees: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.SAMUCH_LEMINCHA_KETANA,
                ZmanCalculationMethod.DayDefinition.dawnToDusk(16.1F.degrees),
                UsesElevation.ALWAYS,
            )
        ) {
            getSamuchLeMinchaKetana(
                alos16Point1Degrees.momentOfOccurrence, tzais16Point1Degrees.momentOfOccurrence
            )
        }

    /**
     * A method for calculating *samuch lemincha ketana*, / near *mincha ketana* time that is half an hour before
     * [minchaKetana72Minutes]  or 9 * *shaos zmaniyos* (temporal hours) after the start of the day,
     * calculated using a day starting 72 minutes before sunrise and ending 72 minutes after sunset. This is the time that eating
     * or other activity can't begin prior to praying *mincha*. The calculation used is 9 * [shaahZmanis16Point1Degrees] after [*alos* 16.1˚][alos16Point1Degrees]. See the [Mechaber and Mishna Berurah 232](https://hebrewbooks.org/pdfpager.aspx?req=60387&st=&pgnum=294).
     *
     * @see shaahZmanis16Point1Degrees
     * @return the `Date` of the time of *samuch lemincha ketana*. If the calculation can't be computed such
     * as northern and southern locations even south of the Arctic Circle and north of the Antarctic Circle
     * where the sun may not reach low enough below the horizon for this calculation, a null will be returned.
     * See detailed explanation on top of the [AstronomicalCalendar] documentation.
     */
    val samuchLeMinchaKetana72Minutes: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.SAMUCH_LEMINCHA_KETANA,
                ZmanCalculationMethod.DayDefinition.dawnToDusk(ZmanCalculationMethod.FixedDuration._72),
                UsesElevation.IF_SET,
            )
        ) {
            getSamuchLeMinchaKetana(alos72.momentOfOccurrence, tzais72.momentOfOccurrence)
        }

    operator fun get(zmanType: ZmanType) = if (zmanType == ZmanType.SHAA_ZMANIS) allShaosZmaniyos
    else allZmanim.filter { it.definition.type == zmanType }

    operator fun get(zmanCalculationMethod: ZmanCalculationMethod): List<Zman<*>> =
        (allShaosZmaniyos + allZmanim).filter {
            it.definition.calculationMethod == zmanCalculationMethod
        }

    /**
     * An unsorted list of all opinions for the length of a sha'ah zmanis (given the time and place this class holds).
     * */
    override val allShaosZmaniyos by lazy {
        (super.allShaosZmaniyos + listOf(
            shaahZmanis19Point8Degrees,
            shaahZmanis18Degrees,
            shaahZmanis26Degrees,
            shaahZmanis16Point1Degrees,
            shaahZmanis60Minutes,
            shaahZmanis72MinutesZmanis,
            shaahZmanis90Minutes,
            shaahZmanis90MinutesZmanis,
            shaahZmanis96MinutesZmanis,
            shaahZmanisAteretTorah,
            shaahZmanisAlos16Point1ToTzais3Point8,
            shaahZmanisAlos16Point1ToTzais3Point7,
            shaahZmanis96Minutes,
            shaahZmanis120Minutes,
            shaahZmanis120MinutesZmanis
        )).distinct()
    }
    val typeToZman by lazy {
        mapOf(
            ZmanType.ALOS to listOf(
                alos60,
                alos72Zmanis,
                alos96,
                alos90Zmanis,
                alos96Zmanis,
                alos90,
                alos120,
                alos120Zmanis,
                alos26Degrees,
                alos18Degrees,
                alos19Degrees,
                alos19Point8Degrees,
                alos16Point1Degrees,
            )
        )
    }

    /**
     * An unsorted list of all zmanim this class exposes. Zmanim are only computed when list is accessed.
     */
    override val allZmanim by lazy {
        (super.allZmanim + listOf(
            plagHamincha120MinutesZmanis,
            plagHamincha120Minutes,
            alos60,
            alos72Zmanis,
            alos96,
            alos90Zmanis,
            alos96Zmanis,
            alos90,
            alos120,
            alos120Zmanis,
            alos26Degrees,
            alos18Degrees,
            alos19Degrees,
            alos19Point8Degrees,
            alos16Point1Degrees,
            misheyakir11Point5Degrees,
            misheyakir11Degrees,
            misheyakir10Point2Degrees,
            misheyakir7Point65Degrees,
            misheyakir9Point5Degrees,
            sofZmanShmaMGA19Point8Degrees,
            sofZmanShmaMGA16Point1Degrees,
            sofZmanShmaMGA18Degrees,
            sofZmanShmaMGA72Minutes,
            sofZmanShmaMGA72MinutesZmanis,
            sofZmanShmaMGA90Minutes,
            sofZmanShmaMGA90MinutesZmanis,
            sofZmanShmaMGA96Minutes,
            sofZmanShmaMGA96MinutesZmanis,
            sofZmanShma3HoursBeforeChatzos,
            sofZmanShmaMGA120Minutes,
            sofZmanShmaAlos16Point1ToSunset,
            sofZmanShmaAlos16Point1ToTzaisGeonim7Point083Degrees,
            sofZmanTfilaMGA19Point8Degrees,
            sofZmanTfilaMGA16Point1Degrees,
            sofZmanTfilaMGA18Degrees,
            sofZmanTfilaMGA72Minutes,
            sofZmanTfilaMGA72MinutesZmanis,
            sofZmanTfilaMGA90Minutes,
            sofZmanTfilaMGA90MinutesZmanis,
            sofZmanTfilaMGA96Minutes,
            sofZmanTfilaMGA96MinutesZmanis,
            sofZmanTfilaMGA120Minutes,
            sofZmanTfila2HoursBeforeChatzos,
            minchaGedola30Minutes,
            minchaGedola72Minutes,
            minchaGedola16Point1Degrees,
            minchaGedolaAhavatShalom,
            minchaGedolaGreaterThan30,
            minchaKetana16Point1Degrees,
            minchaKetanaAhavatShalom,
            minchaKetana72Minutes,
            plagHamincha60Minutes,
            plagHamincha72Minutes,
            plagHamincha90Minutes,
            plagHamincha96Minutes,
            plagHamincha96MinutesZmanis,
            plagHamincha90MinutesZmanis,
            plagHamincha72MinutesZmanis,
            plagHamincha16Point1Degrees,
            plagHamincha19Point8Degrees,
            plagHamincha26Degrees,
            plagHamincha18Degrees,
            plagAlosToSunset,
            plagAlos16Point1ToTzaisGeonim7Point083Degrees,
            plagAhavatShalom,
            bainHashmashosRT13Point24Degrees,
            bainHashmashosRT58Point5Minutes,
            bainHashmashosRT13Point5MinutesBefore7Point083Degrees,
            bainHashmashosRT2Stars,
            bainHashmashosYereim18Minutes,
            bainHashmashosYereim3Point05Degrees,
            bainHashmashosYereim16Point875Minutes,
            bainHashmashosYereim2Point8Degrees,
            bainHashmashosYereim13Point5Minutes,
            bainHashmashosYereim2Point1Degrees,
            Zman.DateBased(
                ZmanDefinition(
                    ZmanType.SHKIAH,
                    ZmanAuthority.Unanimous,
                    UsesElevation.IF_SET
                )
            ) { sunset },
            Zman.DateBased(
                ZmanDefinition(
                    ZmanType.SHKIAH, ZmanAuthority.Unanimous, UsesElevation.NEVER
                )
            ) { seaLevelSunset },
            Zman.DateBased(
                ZmanDefinition(
                    ZmanType.HANAITZ,
                    ZmanAuthority.Unanimous,
                    UsesElevation.IF_SET
                )
            ) { sunrise },
            Zman.DateBased(
                ZmanDefinition(
                    ZmanType.HANAITZ, ZmanAuthority.Unanimous, UsesElevation.NEVER
                )
            ) { seaLevelSunrise },
            tzaisGeonim3Point7Degrees,
            tzaisGeonim3Point8Degrees,
            tzaisGeonim5Point95Degrees,
            tzaisGeonim3Point65Degrees,
            tzaisGeonim3Point676Degrees,
            tzaisGeonim4Point61Degrees,
            tzaisGeonim4Point37Degrees,
            tzaisGeonim5Point88Degrees,
            tzaisGeonim4Point8Degrees,
            tzaisGeonim6Point45Degrees,
            tzaisGeonim7Point083Degrees,
            tzaisGeonim7Point67Degrees,
            tzaisGeonim8Point5Degrees,
            tzaisGeonim9Point3Degrees,
            tzaisGeonim9Point75Degrees,
            tzais60,
            tzaisAteretTorah,
            sofZmanShmaAteretTorah,
            sofZmanTfilahAteretTorah,
            minchaGedolaAteretTorah,
            minchaKetanaAteretTorah,
            plagHaminchaAteretTorah,
            tzais72Zmanis,
            tzais90Zmanis,
            tzais96Zmanis,
            tzais90,
            tzais120,
            tzais120Zmanis,
            tzais16Point1Degrees,
            tzais26Degrees,
            tzais18Degrees,
            tzais19Point8Degrees,
            tzais96,
            fixedLocalChatzos,
            sofZmanKidushLevanaBetweenMoldos,
            sofZmanKidushLevana15Days,
            tchilasZmanKidushLevana3Days,
            zmanMolad,
            tchilasZmanKidushLevana7Days,
            sofZmanAchilasChametzGRA,
            sofZmanAchilasChametzMGA72Minutes,
            sofZmanAchilasChametzMGA16Point1Degrees,
            sofZmanBiurChametzGRA,
            sofZmanBiurChametzMGA72Minutes,
            sofZmanBiurChametzMGA16Point1Degrees,
            solarMidnight,
            alosBaalHatanya,
            sofZmanShmaBaalHatanya,
            sofZmanTfilaBaalHatanya,
            sofZmanAchilasChametzBaalHatanya,
            sofZmanBiurChametzBaalHatanya,
            minchaGedolaBaalHatanya,
            minchaGedolaBaalHatanyaGreaterThan30,
            minchaKetanaBaalHatanya,
            plagHaminchaBaalHatanya,
            tzaisBaalHatanya,
            sofZmanShmaMGA18DegreesToFixedLocalChatzos,
            sofZmanShmaMGA16Point1DegreesToFixedLocalChatzos,
            sofZmanShmaMGA90MinutesToFixedLocalChatzos,
            sofZmanShmaMGA72MinutesToFixedLocalChatzos,
            sofZmanShmaGRASunriseToFixedLocalChatzos,
            sofZmanTfilaGRASunriseToFixedLocalChatzos,
            minchaGedolaGRAFixedLocalChatzos30Minutes,
            minchaKetanaGRAFixedLocalChatzosToSunset,
            plagHaminchaGRAFixedLocalChatzosToSunset,
            tzais50,
            samuchLeMinchaKetanaGRA,
            samuchLeMinchaKetana16Point1Degrees,
            samuchLeMinchaKetana72Minutes
        )).distinct()
    }

    companion object {

        const val ATERET_TORAH_DEFAULT_OFFSET = 40.0

        fun <T> maxOf(a: T?, b: T?): T? where T : Comparable<T> = if (a == null && b == null) null
        else if (a == null) b
        else if (b == null) a
        else if (a > b) a
        else b

        val MIDNIGHT = LocalTime(0, 0, 0)


        /**
         * The zenith of 3.7 below [geometric zenith][AstronomicalCalendar.GEOMETRIC_ZENITH] (90). This calculation is used for
         * calculating *tzais* (nightfall) based on the opinion of the *Geonim* that *tzais* is the
         * time it takes to walk 3/4 of a *Mil* at 18 minutes a *Mil*, or 13.5 minutes after sunset. The sun
         * is 3.7 below [geometric zenith][AstronomicalCalendar.GEOMETRIC_ZENITH] at this time in Jerusalem [around the equinox / equilux](https://kosherjava.com/2022/01/12/equinox-vs-equilux-zmanim-calculations/).
         *
         * @see tzaisGeonim3Point7Degrees
         */
        const val ZENITH_3_POINT_7: Double = GEOMETRIC_ZENITH + 3.7

        /**
         * The zenith of 3.8 below [geometric zenith][AstronomicalCalendar.GEOMETRIC_ZENITH] (90). This calculation is used for
         * calculating *tzais* (nightfall) based on the opinion of the *Geonim* that *tzais* is the
         * time it takes to walk 3/4 of a *Mil* at 18 minutes a *Mil*, or 13.5 minutes after sunset. The sun
         * is 3.8 below [geometric zenith][AstronomicalCalendar.GEOMETRIC_ZENITH] at this time in Jerusalem [around the equinox / equilux](https://kosherjava.com/2022/01/12/equinox-vs-equilux-zmanim-calculations/).
         *
         * @see tzaisGeonim3Point8Degrees
         */
        const val ZENITH_3_POINT_8: Double = GEOMETRIC_ZENITH + 3.8

        /**
         * The zenith of 5.95 below [geometric zenith][AstronomicalCalendar.GEOMETRIC_ZENITH] (90). This calculation is used for
         * calculating *tzais* (nightfall) according to some opinions. This calculation is based on the position of
         * the sun 24 minutes after sunset in Jerusalem [around the equinox / equilux](https://kosherjava.com/2022/01/12/equinox-vs-equilux-zmanim-calculations/),
         * which calculates to 5.95 below [geometric zenith][AstronomicalCalendar.GEOMETRIC_ZENITH].
         *
         * @see tzaisGeonim5Point95Degrees
         */
        const val ZENITH_5_POINT_95: Double = GEOMETRIC_ZENITH + 5.95

        /**
         * The zenith of 7.083 below [geometric zenith][AstronomicalCalendar.GEOMETRIC_ZENITH] (90). This is often referred to as
         * 75' or 7 and 5 minutes. This calculation is used for calculating *alos* (dawn) and
         * *tzais* (nightfall) according to some opinions. This calculation is based on observation of 3 medium sized
         * stars by Dr. Baruch Cohen in his calendar published in in 1899 in Strasbourg, France. This calculates to
         * 7.0833333 below [geometric zenith][AstronomicalCalendar.GEOMETRIC_ZENITH]. The [Sh"Ut
 * Melamed Leho'il](https://hebrewbooks.org/1053) in Orach Chaim 30 agreed to this *zman*, as did the Sh"Ut Bnei Tziyon, Tenuvas Sadeh and
         * it is very close to the time of the [Mekor Chesed](https://hebrewbooks.org/22044) of the Sefer chasidim.
         * It is close to the position of the sun 30 minutes after sunset in Jerusalem [around the equinox / equilux](https://kosherjava.com/2022/01/12/equinox-vs-equilux-zmanim-calculations/), but not
         * Exactly. The actual position of the sun 30 minutes after sunset in Jerusalem at the equilux is 7.205 and
         * 7.199 at the equinox. See Hazmanim Bahalacha vol 2, pages 520-521 for details.
         * @todo Hyperlink the proper sources.
         *
         * @see tzaisGeonim7Point083Degrees
         * @see bainHashmashosRT13Point5MinutesBefore7Point083Degrees
         */
        const val ZENITH_7_POINT_083: Double = GEOMETRIC_ZENITH + 7 + (5.0 / 60)

        /**
         * The zenith of 10.2 below [geometric zenith][AstronomicalCalendar.GEOMETRIC_ZENITH] (90). This calculation is used for
         * calculating *misheyakir* according to some opinions. This calculation is based on the position of the sun
         * 45 minutes before [sunrise] in Jerusalem [around the equinox / equilux](https://kosherjava.com/2022/01/12/equinox-vs-equilux-zmanim-calculations/) which
         * calculates to 10.2 below [geometric zenith][AstronomicalCalendar.GEOMETRIC_ZENITH].
         *
         * @see .getMisheyakir10Point2Degrees
         */
        const val ZENITH_10_POINT_2: Double = GEOMETRIC_ZENITH + 10.2

        /**
         * The zenith of 11 below [geometric zenith][AstronomicalCalendar.GEOMETRIC_ZENITH] (90). This calculation is used for
         * calculating *misheyakir* according to some opinions. This calculation is based on the position of the sun
         * 48 minutes before [sunrise] in Jerusalem [around the equinox / equilux](https://kosherjava.com/2022/01/12/equinox-vs-equilux-zmanim-calculations/), which
         * calculates to 11 below [geometric zenith][AstronomicalCalendar.GEOMETRIC_ZENITH].
         *
         * @see .getMisheyakir11Degrees
         */
        const val ZENITH_11_DEGREES: Double = GEOMETRIC_ZENITH + 11

        /**
         * The zenith of 11.5 below [geometric zenith][AstronomicalCalendar.GEOMETRIC_ZENITH] (90). This calculation is used for
         * calculating *misheyakir* according to some opinions. This calculation is based on the position of the sun
         * 52 minutes before [sunrise] in Jerusalem [around the equinox / equilux](https://kosherjava.com/2022/01/12/equinox-vs-equilux-zmanim-calculations/), which
         * calculates to 11.5 below [geometric zenith][AstronomicalCalendar.GEOMETRIC_ZENITH].
         *
         * @see .getMisheyakir11Point5Degrees
         */
        const val ZENITH_11_POINT_5: Double = GEOMETRIC_ZENITH + 11.5

        /**
         * The zenith of 13.24 below [geometric zenith][AstronomicalCalendar.GEOMETRIC_ZENITH] (90). This calculation is used for
         * calculating Rabbeinu Tam's *bain hashmashos* according to some opinions.
         * NOTE: See comments on [bainHashmashosRT13Point24Degrees] for additional details about the degrees.
         *
         * @see bainHashmashosRT13Point24Degrees
         */
        const val ZENITH_13_POINT_24: Double = GEOMETRIC_ZENITH + 13.24

        /**
         * The zenith of 19 below [geometric zenith][AstronomicalCalendar.GEOMETRIC_ZENITH] (90). This calculation is used for
         * calculating *alos* according to some opinions.
         *
         * @see alos19Degrees
         * @see ZENITH_19_POINT_8
         */
        const val ZENITH_19_DEGREES: Double = GEOMETRIC_ZENITH + 19

        /**
         * The zenith of 19.8 below [geometric zenith][AstronomicalCalendar.GEOMETRIC_ZENITH] (90). This calculation is used for
         * calculating *alos* (dawn) and *tzais* (nightfall) according to some opinions. This calculation is
         * based on the position of the sun 90 minutes after sunset in Jerusalem [around the equinox / equilux](https://kosherjava.com/2022/01/12/equinox-vs-equilux-zmanim-calculations/) which
         * calculates to 19.8 below [geometric zenith][AstronomicalCalendar.GEOMETRIC_ZENITH].
         *
         * @see tzais19Point8Degrees
         * @see alos19Point8Degrees
         * @see alos90
         * @see tzais90
         * @see ZENITH_19_DEGREES
         */
        const val ZENITH_19_POINT_8: Double = GEOMETRIC_ZENITH + 19.8

        /**
         * The zenith of 26 below [geometric zenith][AstronomicalCalendar.GEOMETRIC_ZENITH] (90). This calculation is used for
         * calculating *alos* (dawn) and *tzais* (nightfall) according to some opinions. This calculation is
         * based on the position of the sun [120 minutes][alos120] after sunset in Jerusalem o[around the equinox / equilux](https://kosherjava.com/2022/01/12/equinox-vs-equilux-zmanim-calculations/) which
         * calculates to 26 below [geometric zenith][AstronomicalCalendar.GEOMETRIC_ZENITH]. Since the level of darkness when the sun is
         * 26 and at a point when the level of darkness is long past the 18 point where the darkest point is reached,
         * it should only be used *lechumra* such as delaying the start of nighttime *mitzvos* or avoiding eating
         * this early on a fast day.
         *
         * @see alos26Degrees
         * @see tzais26Degrees
         * @see alos120
         * @see tzais120
         */
        const val ZENITH_26_DEGREES: Double = GEOMETRIC_ZENITH + 26.0

        /**
         * The zenith of 4.37 below [geometric zenith][AstronomicalCalendar.GEOMETRIC_ZENITH] (90). This calculation is used for
         * calculating *tzais* (nightfall) according to some opinions. This calculation is based on the position of
         * the sun [16 7/8 minutes][tzaisGeonim4Point37Degrees] after sunset (3/4 of a 22.5-minute *Mil*)
         * in Jerusalem [around the equinox / equilux](https://kosherjava.com/2022/01/12/equinox-vs-equilux-zmanim-calculations/),
         * which calculates to 4.37 below [geometric zenith][AstronomicalCalendar.GEOMETRIC_ZENITH].
         *
         * @see tzaisGeonim4Point37Degrees
         */
        const val ZENITH_4_POINT_37: Double = GEOMETRIC_ZENITH + 4.37

        /**
         * The zenith of 4.61 below [geometric zenith][AstronomicalCalendar.GEOMETRIC_ZENITH] (90). This calculation is used for
         * calculating *tzais* (nightfall) according to some opinions. This calculation is based on the position of
         * the sun [18 minutes][tzaisGeonim4Point37Degrees] after sunset (3/4 of a 24-minute *Mil*) in
         * Jerusalem [around the equinox
 * / equilux](https://kosherjava.com/2022/01/12/equinox-vs-equilux-zmanim-calculations/), which calculates to 4.61 below [geometric zenith][AstronomicalCalendar.GEOMETRIC_ZENITH].
         * @todo add documentation links
         *
         * @see tzaisGeonim4Point61Degrees
         */
        const val ZENITH_4_POINT_61: Double = GEOMETRIC_ZENITH + 4.61

        /**
         * The zenith of 4.8 below [geometric zenith][AstronomicalCalendar.GEOMETRIC_ZENITH] (90).
         * @todo Add more documentation.
         * @see tzaisGeonim4Point8Degrees
         */
        const val ZENITH_4_POINT_8: Double = GEOMETRIC_ZENITH + 4.8

        /**
         * The zenith of 3.65 below [geometric zenith][AstronomicalCalendar.GEOMETRIC_ZENITH] (90). This calculation is used for
         * calculating *tzais* (nightfall) according to some opinions. This calculation is based on the position of
         * the sun [13.5 minutes][tzaisGeonim3Point65Degrees] after sunset (3/4 of an 18-minute *Mil*)
         * in Jerusalem [around the equinox / equilux](https://kosherjava.com/2022/01/12/equinox-vs-equilux-zmanim-calculations/) which
         * calculates to 3.65 below [geometric zenith][AstronomicalCalendar.GEOMETRIC_ZENITH].
         *
         * @see tzaisGeonim3Point65Degrees
         */
        const val ZENITH_3_POINT_65: Double = GEOMETRIC_ZENITH + 3.65

        /**
         * The zenith of 3.676 below [geometric zenith][AstronomicalCalendar.GEOMETRIC_ZENITH] (90).
         * @todo Add more documentation.
         */
        const val ZENITH_3_POINT_676: Double = GEOMETRIC_ZENITH + 3.676

        /**
         * The zenith of 5.88 below [geometric zenith][AstronomicalCalendar.GEOMETRIC_ZENITH] (90).
         * @todo Add more documentation.
         */
        const val ZENITH_5_POINT_88: Double = GEOMETRIC_ZENITH + 5.88

        /**
         * The zenith of 1.583 below [geometric zenith][AstronomicalCalendar.GEOMETRIC_ZENITH] (90). This calculation is used for
         * calculating *netz amiti* (sunrise) and *shkiah amiti* (sunset) based on the opinion of the
         * [Baal Hatanya](https://en.wikipedia.org/wiki/Shneur_Zalman_of_Liadi).
         *
         * @see sunriseBaalHatanya
         * @see sunsetBaalHatanya
         */
        const val ZENITH_1_POINT_583: Double = GEOMETRIC_ZENITH + 1.583

        /**
         * The zenith of 16.9 below geometric zenith (90). This calculation is used for determining *alos*
         * (dawn) based on the opinion of the Baal Hatanya. It is based on the calculation that the time between dawn
         * and *netz amiti* (sunrise) is 72 minutes, the time that is takes to walk 4 *mil* at 18 minutes
         * a *mil* ([Rambam](https://en.wikipedia.org/wiki/Maimonides) and others). The sun's position at 72
         * minutes before [*netz amiti* (sunrise)][sunriseBaalHatanya] in Jerusalem [around the equinox / equilux](https://kosherjava.com/2022/01/12/equinox-vs-equilux-zmanim-calculations/) is
         * 16.9 below [geometric zenith][AstronomicalCalendar.GEOMETRIC_ZENITH].
         *
         * @see alosBaalHatanya
         */
        const val ZENITH_16_POINT_9: Double = GEOMETRIC_ZENITH + 16.9

        /**
         * The zenith of 6 below [geometric zenith][AstronomicalCalendar.GEOMETRIC_ZENITH] (90). This calculation is used for
         * calculating *tzais* / nightfall based on the opinion of the Baal Hatanya. This calculation is based on the
         * position of the sun 24 minutes after [sunset][sunset] in Jerusalem [around the equinox / equilux](https://kosherjava.com/2022/01/12/equinox-vs-equilux-zmanim-calculations/), which
         * is 6 below [geometric zenith][AstronomicalCalendar.GEOMETRIC_ZENITH].
         *
         * @see tzaisBaalHatanya
         */
        const val ZENITH_6_DEGREES: Double = GEOMETRIC_ZENITH + 6

        /**
         * The zenith of 6.45 below [geometric zenith][AstronomicalCalendar.GEOMETRIC_ZENITH] (90). This calculation is used for
         * calculating *tzais* (nightfall) according to some opinions. This is based on the calculations of [Rabbi Yechiel Michel Tucazinsky](https://en.wikipedia.org/wiki/Yechiel_Michel_Tucazinsky) of the position of
         * the sun no later than [31 minutes][tzaisGeonim6Point45Degrees] after sunset in Jerusalem, and at the
         * height of the summer solstice, this *zman* is 28 minutes after *shkiah*. This computes to 6.45
         * below [geometric zenith][AstronomicalCalendar.GEOMETRIC_ZENITH]. This calculation is found in the [Birur Halacha Yoreh Deah 262](https://hebrewbooks.org/pdfpager.aspx?req=50536&st=&pgnum=51) it the commonly
         * used *zman* in Israel. It should be noted that this differs from the 6.1/6.2 calculation for
         * Rabbi Tucazinsky's time as calculated by the Hazmanim Bahalacha Vol II chapter 50:7 (page 515).
         *
         * @see tzaisGeonim6Point45Degrees
         */
        const val ZENITH_6_POINT_45: Double = GEOMETRIC_ZENITH + 6.45

        /**
         * The zenith of 7.65 below [geometric zenith][AstronomicalCalendar.GEOMETRIC_ZENITH] (90). This calculation is used for
         * calculating *misheyakir* according to some opinions.
         *
         * @see .getMisheyakir7Point65Degrees
         */
        const val ZENITH_7_POINT_65: Double = GEOMETRIC_ZENITH + 7.65

        /**
         * The zenith of 7.67 below [geometric zenith][AstronomicalCalendar.GEOMETRIC_ZENITH] (90). This calculation is used for
         * calculating *tzais* according to some opinions.
         *
         * @see tzaisGeonim7Point67Degrees
         */
        const val ZENITH_7_POINT_67: Double = GEOMETRIC_ZENITH + 7.67

        /**
         * The zenith of 9.3 below [geometric zenith][AstronomicalCalendar.GEOMETRIC_ZENITH] (90). This calculation is used for
         * calculating *tzais* (nightfall) according to some opinions.
         *
         * @see tzaisGeonim9Point3Degrees
         */
        const val ZENITH_9_POINT_3: Double = GEOMETRIC_ZENITH + 9.3

        /**
         * The zenith of 9.5 below [geometric zenith][AstronomicalCalendar.GEOMETRIC_ZENITH] (90). This calculation is used for
         * calculating *misheyakir* according to some opinions.
         *
         * @see .getMisheyakir9Point5Degrees
         */
        const val ZENITH_9_POINT_5: Double = GEOMETRIC_ZENITH + 9.5

        /**
         * The zenith of 9.75 below [geometric zenith][AstronomicalCalendar.GEOMETRIC_ZENITH] (90). This calculation is used for
         * calculating *alos* (dawn) and *tzais* (nightfall) according to some opinions.
         *
         * @see tzaisGeonim9Point75Degrees
         */
        const val ZENITH_9_POINT_75: Double = GEOMETRIC_ZENITH + 9.75

        /**
         * The zenith of 2.1 above [geometric zenith][AstronomicalCalendar.GEOMETRIC_ZENITH] (90). This calculation is used for
         * calculating the start of *bain hashmashos* (twilight) of 13.5 minutes before sunset converted to degrees
         * according to the Yereim. As is traditional with degrees below the horizon, this is calculated without refraction
         * and from the center of the sun. It would be 0.833 less without this.
         *
         * @see bainHashmashosYereim2Point1Degrees
         */
        const val ZENITH_MINUS_2_POINT_1: Double = GEOMETRIC_ZENITH - 2.1

        /**
         * The zenith of 2.8 above [geometric zenith][AstronomicalCalendar.GEOMETRIC_ZENITH] (90). This calculation is used for
         * calculating the start of *bain hashmashos* (twilight) of 16.875 minutes before sunset converted to degrees
         * according to the Yereim. As is traditional with degrees below the horizon, this is calculated without refraction
         * and from the center of the sun. It would be 0.833 less without this.
         *
         * @see bainHashmashosYereim2Point8Degrees
         */
        const val ZENITH_MINUS_2_POINT_8: Double = GEOMETRIC_ZENITH - 2.8

        /**
         * The zenith of 3.05 above [geometric zenith][AstronomicalCalendar.GEOMETRIC_ZENITH] (90). This calculation is used for
         * calculating the start of *bain hashmashos* (twilight) of 18 minutes before sunset converted to degrees
         * according to the Yereim. As is traditional with degrees below the horizon, this is calculated without refraction
         * and from the center of the sun. It would be 0.833 less without this.
         *
         * @see bainHashmashosYereim3Point05Degrees
         */
        const val ZENITH_MINUS_3_POINT_05: Double = GEOMETRIC_ZENITH - 3.05
    }
}