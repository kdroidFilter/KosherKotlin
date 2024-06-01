/*
 * Zmanim Java API
 * Copyright (C) 2004-2022 Eliyahu Hershfeld
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
 * or connect to: https://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 */
package sternbach.software.kosherkotlin

import sternbach.software.kosherkotlin.hebrewcalendar.JewishCalendar
import sternbach.software.kosherkotlin.metadata.ZmanAuthority
import sternbach.software.kosherkotlin.metadata.ZmanCalculationMethod
import sternbach.software.kosherkotlin.metadata.ZmanDefinition
import sternbach.software.kosherkotlin.metadata.ZmanType
import sternbach.software.kosherkotlin.util.GeoLocation
import kotlinx.datetime.Instant
import sternbach.software.kosherkotlin.metadata.UsesElevation
import sternbach.software.kosherkotlin.metadata.ZmanCalculationMethod.Companion.degrees
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes


/**
 * The ZmanimCalendar is a specialized calendar that can calculate sunrise, sunset and Jewish *zmanim*
 * (religious times) for prayers and other Jewish religious duties. This class contains the main functionality of the
 * *Zmanim* library. For a much more extensive list of *zmanim*, use the [ComplexZmanimCalendar] that
 * extends this class. See documentation for the [ComplexZmanimCalendar] and [AstronomicalCalendar] for
 * simple examples on using the API.
 * **Elevation based *zmanim* (even sunrise and sunset) should not be used *lekula* without the guidance
 * of a *posek***. According to Rabbi Dovid Yehudah Bursztyn in his
 * [Zmanim Kehilchasam, 7th edition](https://www.worldcat.org/oclc/1158574217) chapter 2, section 7 (pages 181-182)
 * and section 9 (pages 186-187), no *zmanim* besides sunrise and sunset should use elevation. However, Rabbi Yechiel
 * Avrahom Zilber in the [Birur Halacha Vol. 6](https://hebrewbooks.org/51654) Ch. 58 Pages
 * [&amp;34](https://hebrewbooks.org/pdfpager.aspx?req=51654&amp;pgnum=42) and
 * [&amp;42](https://hebrewbooks.org/pdfpager.aspx?req=51654&amp;pgnum=50) is of the opinion that elevation should be
 * accounted for in *zmanim* calculations. Related to this, Rabbi Yaakov Karp in [Shimush Zekeinim](https://www.worldcat.org/oclc/919472094), Ch. 1, page 17 states that obstructing horizons should
 * be factored into *zmanim* calculations. The setting defaults to false (elevation will not be used for
 * *zmanim* calculations besides sunrise and sunset), unless the setting is changed to true in [isUseElevation]. This will impact sunrise and sunset-based *zmanim* such as [sunrise],
 * [sunset], [sofZmanShmaGRA], *alos*-based *zmanim* such as [sofZmanShmaMGA]
 * that are based on a fixed offset of sunrise or sunset and *zmanim* based on a percentage of the day such as
 * [ComplexZmanimCalendar.sofZmanShmaMGA90MinutesZmanis] that are based on sunrise and sunset. Even when set to
 * true it will not impact *zmanim* that are a degree-based offset of sunrise and sunset, such as [][ComplexZmanimCalendar.sofZmanShmaMGA16Point1Degrees] or [ComplexZmanimCalendar.getSofZmanShmaBaalHatanya] since
 * these *zmanim* are not linked to sunrise or sunset times (the calculations are based on the astronomical definition of
 * sunrise and sunset calculated in a vacuum with the solar radius above the horizon), and are therefore not impacted by the use
 * of elevation.
 * For additional information on the *halachic* impact of elevation on *zmanim* see:
 *
 *  * [Zmanei Halacha Lema'aseh](https://www.nli.org.il/en/books/NNL_ALEPH002542826/NLI) 4th edition by [Rabbi Yedidya Manat](http://beinenu.com/rabbis/%D7%94%D7%A8%D7%91-%D7%99%D7%93%D7%99%D7%93%D7%99%D7%94-%D7%9E%D7%A0%D7%AA).
 * See section 1, pages 11-12 for a very concise write-up, with details in section 2, pages 37 - 63 and 133 - 151.
 *  * [Zmanim Kehilchasam](https://www.worldcat.org/oclc/1158574217) 7th edition, by Rabbi Dovid Yehuda Burstein,  vol 1,
 * chapter 2, pages 95 - 188.
 *  * [Hazmanim Bahalacha](https://www.worldcat.org/oclc/36089452) by Rabbi Chaim Banish , perek 7, pages 53 - 63.
 *
 *
 *
 * **Note:** It is important to read the technical notes on top of the [AstronomicalCalendar] documentation
 * before using this code.
 *
 * I would like to thank [Rabbi Yaakov Shakow](https://www.worldcat.org/search?q=au%3AShakow%2C+Yaakov), the
 * author of Luach Ikvei Hayom who spent a considerable amount of time reviewing, correcting and making suggestions on the
 * documentation in this library.
 * <h2>Disclaimer:</h2> I did my best to get accurate results, but please double-check before relying on these
 * *zmanim* for *halacha lema'aseh*.
 *
 * **Glossary**
 * *Alos* - Dawn. The time when the sun is 16.1&deg; below the eastern geometric horizon before sunrise.
 * Sunrise - Netz/Haneitz
 * Sunset - Shkiah
 * Dusk/Nightfall:  This is the same as *Tzais*.
 * Candle lighting - The time when Shabbos candles are lit. This is usually 18 minutes before sunset.
 * *Chatzos* - Midday/Midnight. The time when the sun is at its highest point in the sky. This is usually 6 hours after *Alos*.
 *
 *
 * @author  Eliyahu Hershfeld 2004 - 2022
 */
open class ZmanimCalendar(
    geoLocation: GeoLocation = GeoLocation(),
    /**
     * Is elevation above sea level calculated for times besides sunrise and sunset. According to Rabbi Dovid Yehuda
     * Bursztyn in his [Zmanim Kehilchasam (second edition published
     * in 2007)](https://www.worldcat.org/oclc/659793988) chapter 2 (pages 186-187) no *zmanim* besides sunrise and sunset should use elevation. However
     * Rabbi Yechiel Avrahom Zilber in the [Birur Halacha Vol. 6](https://hebrewbooks.org/51654) Ch. 58 Pages
     * [&amp;34](https://hebrewbooks.org/pdfpager.aspx?req=51654&amp;pgnum=42) and [&amp;42](https://hebrewbooks.org/pdfpager.aspx?req=51654&amp;pgnum=50) is of the opinion that elevation should be
     * accounted for in *zmanim* calculations. Related to this, Rabbi Yaakov Karp in [Shimush Zekeinim](https://www.worldcat.org/oclc/919472094), Ch. 1, page 17 states that obstructing horizons
     * should be factored into *zmanim* calculations.The setting defaults to false (elevation will not be used for
     * *zmanim* calculations), unless the setting is changed to true in [isUseElevation]. This will
     * impact sunrise and sunset based *zmanim* such as [sunrise], [sunset],
     * [sofZmanShmaGRA], alos based *zmanim* such as [sofZmanShmaMGA] that are based on a
     * fixed offset of sunrise or sunset and *zmanim* based on a percentage of the day such as [ComplexZmanimCalendar.sofZmanShmaMGA90MinutesZmanis] that are based on sunrise and sunset. It will not impact
     * *zmanim* that are a degree based offset of sunrise and sunset, such as
     * [ComplexZmanimCalendar.getSofZmanShmaMGA16Point1Degrees] or [ComplexZmanimCalendar.getSofZmanShmaBaalHatanya].
     *
     * @return if the use of elevation is active
     *
     * @see isUseElevation
     */
    /**
     * Sets whether elevation above sea level is factored into *zmanim* calculations for times besides sunrise and sunset.
     * See [isUseElevation] for more details.
     * @param useElevation set to true to use elevation in *zmanim* calculations
     */
    /**
     * Is elevation factored in for some zmanim (see [isUseElevation] for additional information).
     * @see isUseElevation
     */
    var isUseElevation: Boolean = false,
    /**
     * A method to get the offset in minutes before [sea level sunset][AstronomicalCalendar.seaLevelSunset] which
     * is used in calculating candle lighting time. The default time used is 18 minutes before sea level sunset. Some
     * calendars use 15 minutes, while the custom in Jerusalem is to use a 40 minute offset. Please check the local custom
     * for candle lighting time.
     *
     * @return Returns the currently set candle lighting offset in minutes.
     * @see .getCandleLighting
     * @see .setCandleLightingOffset
     */
    /**
     * A method to set the offset in minutes before [sea level sunset][AstronomicalCalendar.seaLevelSunset] that is
     * used in calculating candle lighting time. The default time used is 18 minutes before sunset. Some calendars use 15
     * minutes, while the custom in Jerusalem is to use a 40 minute offset.
     *
     * @param candleLightingOffset
     * The candle lighting offset to set in minutes.
     * @see .getCandleLighting
     * @see .getCandleLightingOffset
     */
    /**
     * The default *Shabbos* candle lighting offset is 18 minutes. This can be changed via the
     * [candleLightingOffset] and retrieved by the [candleLightingOffset].
     */
    var candleLightingOffset: Double = 18.0
) : AstronomicalCalendar(geoLocation) {


    /**
     * This method will return [sea level sunrise][seaLevelSunrise] if [isUseElevation] is false (the
     * default), or elevation adjusted [AstronomicalCalendar.getSunrise] if it is true. This allows relevant *zmanim*
     * in this and extending classes (such as the [ComplexZmanimCalendar]) to automatically adjust to the elevation setting.
     *
     * @return [seaLevelSunrise] if [isUseElevation] is false (the default), or elevation adjusted
     * [AstronomicalCalendar.getSunrise] if it is true.
     * @see com.kosherjava.zmanim.AstronomicalCalendar.getSunrise
     */
    protected val elevationAdjustedSunrise: Instant?
        get() = if (isUseElevation) super.sunrise else seaLevelSunrise

    /**
     * This method will return [sea level sunrise][seaLevelSunrise] if [isUseElevation] is false (the default),
     * or elevation adjusted [AstronomicalCalendar.getSunrise] if it is true. This allows relevant *zmanim*
     * in this and extending classes (such as the [ComplexZmanimCalendar]) to automatically adjust to the elevation setting.
     *
     * @return [seaLevelSunset] if [isUseElevation] is false (the default), or elevation adjusted
     * [AstronomicalCalendar.getSunset] if it is true.
     * @see com.kosherjava.zmanim.AstronomicalCalendar.getSunset
     */
    protected val elevationAdjustedSunset: Instant?
        get() = if (isUseElevation) super.sunset else seaLevelSunset

    /**
     * A method that returns *tzais* (nightfall) when the sun is [8.5˚][ZENITH_8_POINT_5] below the
     * [geometric horizon][AstronomicalCalendar.GEOMETRIC_ZENITH] (90) after [sunset][sunset], a time that Rabbi Meir
     * Posen in his the *[Ohr Meir](https://www.worldcat.org/oclc/29283612)* calculated that 3 small
     * stars are visible, which is later than the required 3 medium stars. See the [ZENITH_8_POINT_5] constant.
     *
     * @see ZENITH_8_POINT_5
     *
     *
     * @return The `Date` of nightfall. If the calculation can't be computed such as northern and southern
     * locations even south of the Arctic Circle and north of the Antarctic Circle where the sun may not reach
     * low enough below the horizon for this calculation, a null will be returned. See detailed explanation on
     * top of the [AstronomicalCalendar] documentation.
     * @see ZENITH_8_POINT_5
     * ComplexZmanimCalendar.getTzaisGeonim8Point5Degrees
     */
    val tzais: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.TZAIS,
                8.5F.degrees,
                UsesElevation.ALWAYS,
                supportingAuthorities = listOf(ZmanAuthority.POSEN)
            )
        ) {
            getSunsetOffsetByDegrees(ZENITH_8_POINT_5)
        }

    /**
     * Returns *alos* (dawn) based on the time when the sun is [16.1˚][ZENITH_16_POINT_1] below the
     * eastern [geometric horizon][AstronomicalCalendar.GEOMETRIC_ZENITH] before [sunrise]. This is based on the
     * calculation that the time between dawn and sunrise (and sunset to nightfall) is 72 minutes, the time that is
     * takes to walk 4 *mil* at 18 minutes a mil (*[Rambam](https://en.wikipedia.org/wiki/Maimonides)* and others). The sun's position at 72 minutes before [sunrise] in Jerusalem
     * on the [around the equinox /
 * equilux](https://kosherjava.com/2022/01/12/equinox-vs-equilux-zmanim-calculations/) is 16.1 below [AstronomicalCalendar.GEOMETRIC_ZENITH].
     *
     * @see ZENITH_16_POINT_1
     *
     * @see ComplexZmanimCalendar.getAlos16Point1Degrees
     * @return The `Date` of dawn. If the calculation can't be computed such as northern and southern
     * locations even south of the Arctic Circle and north of the Antarctic Circle where the sun may not reach
     * low enough below the horizon for this calculation, a null will be returned. See detailed explanation on
     * top of the [AstronomicalCalendar] documentation.
     */
    val alosHashachar: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.ALOS,
                16.1F.degrees,
                UsesElevation.ALWAYS
            )
        ) {
            getSunriseOffsetByDegrees(ZENITH_16_POINT_1)
        }

    /**
     * Method to return *alos* (dawn) calculated using 72 minutes before [sunrise] or
     * [sea level sunrise][seaLevelSunrise] (depending on the [isUseElevation] setting). This time
     * is based on the time to walk the distance of 4 *Mil* at 18 minutes a *Mil*. The 72 minute time (but
     * not the concept of fixed minutes) is based on the opinion that the time of the *Neshef* (twilight between
     * dawn and sunrise) does not vary by the time of year or location but depends on the time it takes to walk the
     * distance of 4 *Mil*.
     *
     * @return the `Date` representing the time. If the calculation can't be computed such as in the Arctic
     * Circle where there is at least one day a year where the sun does not rise, and one where it does not set,
     * a null will be returned. See detailed explanation on top of the [AstronomicalCalendar]
     * documentation.
     */
    val alos72: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.ALOS,
                ZmanCalculationMethod.Relationship(ZmanType.ALOS occurs 72.minutes before ZmanType.HANAITZ),
                UsesElevation.NEVER,
            )
        ) {
            getTimeOffset(elevationAdjustedSunrise, -72 * MINUTE_MILLIS)
        }

    /**
     * This method returns *chatzos* (midday) following most opinions that *chatzos* is the midpoint
     * between [sea level sunrise][seaLevelSunrise] and [sea level sunset][seaLevelSunset]. A day
     * starting at *alos* and ending at *tzais* using the same time or degree offset will also return
     * the same time. The returned value is identical to [sunTransit]. In reality due to lengthening or
     * shortening of day, this is not necessarily the exact midpoint of the day, but it is very close.
     *
     * @see AstronomicalCalendar.getSunTransit
     * @return the `Date` of chatzos. If the calculation can't be computed such as in the Arctic Circle
     * where there is at least one day where the sun does not rise, and one where it does not set, a null will
     * be returned. See detailed explanation on top of the [AstronomicalCalendar] documentation.
     */
    val chatzos: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.CHATZOS_HAYOM,
                ZmanAuthority.Unanimous,
                UsesElevation.NEVER
            )
        ) {
            sunTransit
        }

    /**
     * A generic method for calculating the latest *zman krias shema* (time to recite shema in the morning)
     * that is 3 * *shaos zmaniyos* (temporal hours) after the start of the day, calculated using the start and
     * end of the day passed to this method.
     * The time from the start of day to the end of day are divided into 12 *shaos zmaniyos* (temporal hours),
     * and the latest *zman krias shema* is calculated as 3 of those *shaos zmaniyos* after the beginning of
     * the day. As an example, passing [sunrise] and [sunset][sunset] or [sea level sunrise][seaLevelSunrise] and [sea level sunset][seaLevelSunset] (depending on the [isUseElevation]
     * elevation setting) to this method will return *sof zman krias shema* according to the opinion of the
     * *[GRA](https://en.wikipedia.org/wiki/Vilna_Gaon)*.
     *
     * @param startOfDay
     * the start of day for calculating *zman krias shema*. This can be sunrise or any *alos* passed
     * to this method.
     * @param endOfDay
     * the end of day for calculating *zman krias shema*. This can be sunset or any *tzais* passed to
     * this method.
     * @return the `Date` of the latest *zman shema* based on the start and end of day times passed to this
     * method. If the calculation can't be computed such as in the Arctic Circle where there is at least one day
     * a year where the sun does not rise, and one where it does not set, a null will be returned. See detailed
     * explanation on top of the [AstronomicalCalendar] documentation.
     */
    fun getSofZmanShma(startOfDay: Instant?, endOfDay: Instant?): Instant? =
        getShaahZmanisBasedZman(startOfDay, endOfDay, 3.0)

    /**
     * This method returns the latest *zman krias shema* (time to recite shema in the morning) that is 3 *
     * [*shaos zmaniyos*][shaahZmanisGra] (solar hours) after [sunrise] or
     * [sea level sunrise][seaLevelSunrise] (depending on the [isUseElevation] setting), according
     * to the [GRA](https://en.wikipedia.org/wiki/Vilna_Gaon).
     * The day is calculated from [sea level sunrise][seaLevelSunrise] to [sea level][seaLevelSunrise] or [sunrise] to [sunset][sunset] (depending on the [isUseElevation]
     * setting).
     *
     * @see sofZmanShma
     * @see shaahZmanisGra
     * @see .isUseElevation
     * @see ComplexZmanimCalendar.getSofZmanShmaBaalHatanya
     * @return the `Date` of the latest *zman shema* according to the GRA. If the calculation can't be
     * computed such as in the Arctic Circle where there is at least one day a year where the sun does not rise,
     * and one where it does not set, a null will be returned. See the detailed explanation on top of the [] documentation.
     */
    val sofZmanShmaGRA: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.SOF_ZMAN_KRIAS_SHEMA,
                ZmanCalculationMethod.DayDefinition.SUNRISE_TO_SUNSET,
                UsesElevation.IF_SET,
                listOf(ZmanAuthority.GRA)
            )
        ) {
            getSofZmanShma(elevationAdjustedSunrise, elevationAdjustedSunset)
        }

    /**
     * This method returns the latest *zman krias shema* (time to recite shema in the morning) that is 3 *
     * [*shaos zmaniyos*][shaahZmanisMGA] (solar hours) after [alos72], according to the
     * [Magen Avraham (MGA)](https://en.wikipedia.org/wiki/Avraham_Gombinern). The day is calculated
     * from 72 minutes before [sea level sunrise][seaLevelSunrise] to 72 minutes after [seaLevelSunrise] or from 72 minutes before [sunrise] to [sunset][sunset] (depending on the [isUseElevation] setting).
     *
     * @return the `Date` of the latest *zman shema*. If the calculation can't be computed such as in
     * the Arctic Circle where there is at least one day a year where the sun does not rise, and one where it
     * does not set, a null will be returned. See detailed explanation on top of the
     * [AstronomicalCalendar] documentation.
     * @see sofZmanShma
     * @see ComplexZmanimCalendar.getShaahZmanis72Minutes
     * @see ComplexZmanimCalendar.getAlos72
     * @see ComplexZmanimCalendar.getSofZmanShmaMGA72Minutes
     */
    val sofZmanShmaMGA: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.SOF_ZMAN_KRIAS_SHEMA,
                ZmanCalculationMethod.DayDefinition.dawnToDusk(ZmanCalculationMethod.FixedDuration._72),
                UsesElevation.IF_SET,
            )
        ) {
            getSofZmanShma(alos72.momentOfOccurrence, tzais72.momentOfOccurrence)
        }

    /**
     * This method returns the *tzais* (nightfall) based on the opinion of *Rabbeinu Tam* that
     * *tzais hakochavim* is calculated as 72 minutes, the time it takes to walk 4 *Mil* at 18 minutes
     * a *Mil*. According to the [Machtzis Hashekel](https://en.wikipedia.org/wiki/Samuel_Loew) in
     * Orach Chaim 235:3, the [Pri Megadim](https://en.wikipedia.org/wiki/Joseph_ben_Meir_Teomim) in Orach
     * Chaim 261:2 (see the Biur Halacha) and others (see Hazmanim Bahalacha 17:3 and 17:5) the 72 minutes are standard
     * clock minutes any time of the year in any location. Depending on the [isUseElevation] setting) a 72
     * minute offset from  either [sunset][sunset] or [sea level sunset][seaLevelSunset] is used.
     *
     * @see ComplexZmanimCalendar.getTzais16Point1Degrees
     * @return the `Date` representing 72 minutes after sunset. If the calculation can't be
     * computed such as in the Arctic Circle where there is at least one day a year where the sun does not rise,
     * and one where it does not set, a null will be returned See detailed explanation on top of the
     * [AstronomicalCalendar] documentation.
     */
    val tzais72: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.TZAIS,
                ZmanCalculationMethod.Relationship(ZmanType.TZAIS occurs 72.minutes after ZmanType.SHKIAH),
                UsesElevation.IF_SET,
                supportingAuthorities = listOf(
                    ZmanAuthority.MACHATZIS_HASHEKEL,
                    ZmanAuthority.PRI_MEGADIM,
                    ZmanAuthority.RABEINU_TAM
                ),
            )
        ) {
            getTimeOffset(elevationAdjustedSunset, 72 * MINUTE_MILLIS)
        }

    /**
     * A method to return candle lighting time, calculated as [.getCandleLightingOffset] minutes before
     * [sea level sunset][seaLevelSunset]. This will return the time for any day of the week, since it can be
     * used to calculate candle lighting time for *Yom Tov* (mid-week holidays) as well. Elevation adjustments
     * are intentionally not performed by this method, but you can calculate it by passing the elevation adjusted sunset
     * to [.getTimeOffset].
     *
     * @return candle lighting time. If the calculation can't be computed such as in the Arctic Circle where there is at
     * least one day a year where the sun does not rise, and one where it does not set, a null will be returned.
     * See detailed explanation on top of the [AstronomicalCalendar] documentation.
     *
     * @see .seaLevelSunset
     * @see .getCandleLightingOffset
     * @see .setCandleLightingOffset
     */
    val candleLighting: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.CANDLE_LIGHTING,
                ZmanCalculationMethod.Relationship(ZmanType.CANDLE_LIGHTING occurs candleLightingOffset.minutes before ZmanType.SHKIAH),
                UsesElevation.NEVER,
            )
        ) {
            getTimeOffset(seaLevelSunset, -candleLightingOffset * MINUTE_MILLIS)
        }

    /**
     * A generic method for calculating the latest *zman tfilah* (time to recite the morning prayers)
     * that is 4 * *shaos zmaniyos* (temporal hours) after the start of the day, calculated using the start and
     * end of the day passed to this method.
     * The time from the start of day to the end of day are divided into 12 *shaos zmaniyos* (temporal hours),
     * and *sof zman tfila* is calculated as 4 of those *shaos zmaniyos* after the beginning of the day.
     * As an example, passing [sunrise] and [sunset][sunset] or [sea level sunrise][seaLevelSunrise] and [sea level sunset][seaLevelSunset] (depending on the [isUseElevation]
     * elevation setting) to this method will return *zman tfilah* according to the opinion of the *[GRA](https://en.wikipedia.org/wiki/Vilna_Gaon)*.
     *
     * @param startOfDay
     * the start of day for calculating *zman tfilah*. This can be sunrise or any *alos* passed
     * to this method.
     * @param endOfDay
     * the end of day for calculating *zman tfilah*. This can be sunset or any *tzais* passed
     * to this method.
     * @return the `Date` of the latest *zman tfilah* based on the start and end of day times passed
     * to this method. If the calculation can't be computed such as in the Arctic Circle where there is at least
     * one day a year where the sun does not rise, and one where it does not set, a null will be returned. See
     * detailed explanation on top of the [AstronomicalCalendar] documentation.
     */
    fun getSofZmanTfila(startOfDay: Instant?, endOfDay: Instant?): Instant? =
        getShaahZmanisBasedZman(startOfDay, endOfDay, 4.0)

    /**
     * This method returns the latest *zman tfila* (time to recite shema in the morning) that is 4 *
     * [*shaos zmaniyos* ][shaahZmanisGra](solar hours) after [sunrise] or
     * [sea level sunrise][seaLevelSunrise] (depending on the [isUseElevation] setting), according
     * to the [GRA](https://en.wikipedia.org/wiki/Vilna_Gaon).
     * The day is calculated from [sea level sunrise][seaLevelSunrise] to [sea level][seaLevelSunrise] or [sunrise] to [sunset][sunset] (depending on the [isUseElevation]
     * setting).
     *
     * @see sofZmanTfila
     * @see shaahZmanisGra
     * @see ComplexZmanimCalendar.getSofZmanTfilaBaalHatanya
     * @return the `Date` of the latest *zman tfilah*. If the calculation can't be computed such as in
     * the Arctic Circle where there is at least one day a year where the sun does not rise, and one where it
     * does not set, a null will be returned. See detailed explanation on top of the [AstronomicalCalendar]
     * documentation.
     */
    val sofZmanTfilaGRA: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.SOF_ZMAN_TEFILLAH,
                ZmanCalculationMethod.DayDefinition.SUNRISE_TO_SUNSET,
                UsesElevation.IF_SET,
                listOf(ZmanAuthority.GRA)
            )
        ) {
            getSofZmanTfila(elevationAdjustedSunrise, elevationAdjustedSunset)
        }

    /**
     * This method returns the latest *zman tfila* (time to recite shema in the morning) that is 4 *
     * [*shaos zmaniyos*][shaahZmanisMGA] (solar hours) after [alos72], according to the
     * *[Magen Avraham (MGA)](https://en.wikipedia.org/wiki/Avraham_Gombinern)*. The day is calculated
     * from 72 minutes before [sea level sunrise][seaLevelSunrise] to 72 minutes after [][seaLevelSunrise] or from 72 minutes before [sunrise] to [sunset][sunset] (depending on the [isUseElevation] setting).
     *
     * @return the `Date` of the latest *zman tfila*. If the calculation can't be computed such as in
     * the Arctic Circle where there is at least one day a year where the sun does not rise, and one where it
     * does not set), a null will be returned. See detailed explanation on top of the [AstronomicalCalendar]
     * documentation.
     * @see sofZmanTfila
     * @see shaahZmanisMGA
     * @see alos72
     */
    val sofZmanTfilaMGA: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.SOF_ZMAN_TEFILLAH,
                ZmanCalculationMethod.DayDefinition.dawnToDusk(ZmanCalculationMethod.FixedDuration._72),
                UsesElevation.IF_SET,
            )
        ) {
            getSofZmanTfila(alos72.momentOfOccurrence, tzais72.momentOfOccurrence)
        }

    /**
     * A generic method for calculating the latest *mincha gedola* (the earliest time to recite the mincha  prayers)
     * that is 6.5 * *shaos zmaniyos* (temporal hours) after the start of the day, calculated using the start and end
     * of the day passed to this method.
     * The time from the start of day to the end of day are divided into 12 *shaos zmaniyos* (temporal hours), and
     * *mincha gedola* is calculated as 6.5 of those *shaos zmaniyos* after the beginning of the day. As an
     * example, passing [sunrise] and [sunset][sunset] or [sea level][seaLevelSunrise] and [sea level sunset][seaLevelSunset] (depending on the [isUseElevation] elevation
     * setting) to this method will return *mincha gedola* according to the opinion of the
     * *[GRA](https://en.wikipedia.org/wiki/Vilna_Gaon)*.
     *
     * @param startOfDay
     * the start of day for calculating *Mincha gedola*. This can be sunrise or any *alos* passed
     * to this method.
     * @param endOfDay
     * the end of day for calculating *Mincha gedola*. This can be sunset or any *tzais* passed
     * to this method.
     * @return the `Date` of the time of *Mincha gedola* based on the start and end of day times
     * passed to this method. If the calculation can't be computed such as in the Arctic Circle where there is
     * at least one day a year where the sun does not rise, and one where it does not set, a null will be
     * returned. See detailed explanation on top of the [AstronomicalCalendar] documentation.
     */
    fun getMinchaGedola(startOfDay: Instant?, endOfDay: Instant?): Instant? =
        getShaahZmanisBasedZman(startOfDay, endOfDay, 6.5)

    /**
     * This method returns the latest *mincha gedola*,the earliest time one can pray *mincha* that is 6.5 *
     * [*shaos zmaniyos*][shaahZmanisGra] (solar hours) after [sunrise] or
     * [sea level sunrise][seaLevelSunrise] (depending on the [isUseElevation] setting), according
     * to the *[GRA](https://en.wikipedia.org/wiki/Vilna_Gaon)*. *Mincha gedola* is the earliest
     * time one can pray *mincha*. The Ramba"m is of the opinion that it is better to delay *mincha* until
     * [*mincha ketana*][.getMinchaKetana] while the *Ra"sh, Tur, GRA* and others are of the
     * opinion that *mincha* can be prayed *lechatchila* starting at *mincha gedola*.
     * The day is calculated from [sea level sunrise][seaLevelSunrise] to [sea level][seaLevelSunrise] or [sunrise] to [sunset][sunset] (depending on the [isUseElevation]
     * setting).
     *
     * @see .getMinchaGedola
     * @see shaahZmanisGra
     * @see .getMinchaKetana
     * @see ComplexZmanimCalendar.getMinchaGedolaBaalHatanya
     * @return the `Date` of the time of mincha gedola. If the calculation can't be computed such as in the
     * Arctic Circle where there is at least one day a year where the sun does not rise, and one where it does
     * not set, a null will be returned. See detailed explanation on top of the [AstronomicalCalendar]
     * documentation.
     */
    val minchaGedola: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.MINCHA_GEDOLAH,
                ZmanCalculationMethod.DayDefinition.SUNRISE_TO_SUNSET,
                UsesElevation.IF_SET,
                listOf(ZmanAuthority.GRA)
            )
        ) {
            getMinchaGedola(elevationAdjustedSunrise, elevationAdjustedSunset)
        }

    /**
     * A generic method for calculating *samuch lemincha ketana*, / near *mincha ketana* time that is half
     * an hour before [.getMinchaKetana]  or 9 * *shaos zmaniyos* (temporal hours) after the
     * start of the day, calculated using the start and end of the day passed to this method.
     * The time from the start of day to the end of day are divided into 12 *shaos zmaniyos* (temporal hours), and
     * *samuch lemincha ketana* is calculated as 9 of those *shaos zmaniyos* after the beginning of the day.
     * For example, passing [sunrise] and [sunset][sunset] or [sea][seaLevelSunrise] and [sea level sunset][seaLevelSunset] (depending on the [isUseElevation] elevation
     * setting) to this method will return *samuch lemincha ketana* according to the opinion of the
     * [GRA](https://en.wikipedia.org/wiki/Vilna_Gaon).
     *
     * @param startOfDay
     * the start of day for calculating *samuch lemincha ketana*. This can be sunrise or any *alos*
     * passed to to this method.
     * @param endOfDay
     * the end of day for calculating *samuch lemincha ketana*. This can be sunset or any *tzais*
     * passed to this method.
     * @return the `Date` of the time of *Mincha ketana* based on the start and end of day times
     * passed to this method. If the calculation can't be computed such as in the Arctic Circle where there is
     * at least one day a year where the sun does not rise, and one where it does not set, a null will be
     * returned. See detailed explanation on top of the [AstronomicalCalendar] documentation.
     *
     * @see ComplexZmanimCalendar.getSamuchLeMinchaKetanaGRA
     * @see ComplexZmanimCalendar.getSamuchLeMinchaKetana16Point1Degrees
     * @see ComplexZmanimCalendar.getSamuchLeMinchaKetana72Minutes
     */
    fun getSamuchLeMinchaKetana(startOfDay: Instant?, endOfDay: Instant?): Instant? =
        getShaahZmanisBasedZman(startOfDay, endOfDay, 9.0)

    /**
     * A generic method for calculating *mincha ketana*, (the preferred time to recite the mincha prayers in
     * the opinion of the *[Rambam](https://en.wikipedia.org/wiki/Maimonides)* and others) that is
     * 9.5 * *shaos zmaniyos* (temporal hours) after the start of the day, calculated using the start and end
     * of the day passed to this method.
     * The time from the start of day to the end of day are divided into 12 *shaos zmaniyos* (temporal hours), and
     * *mincha ketana* is calculated as 9.5 of those *shaos zmaniyos* after the beginning of the day. As an
     * example, passing [sunrise] and [sunset][sunset] or [sea][seaLevelSunrise] and [sea level sunset][seaLevelSunset] (depending on the [isUseElevation]
     * elevation setting) to this method will return *mincha ketana* according to the opinion of the
     * [GRA](https://en.wikipedia.org/wiki/Vilna_Gaon).
     *
     * @param startOfDay
     * the start of day for calculating *Mincha ketana*. This can be sunrise or any *alos* passed
     * to this method.
     * @param endOfDay
     * the end of day for calculating *Mincha ketana*. This can be sunset or any *tzais* passed to
     * this method.
     * @return the `Date` of the time of *Mincha ketana* based on the start and end of day times
     * passed to this method. If the calculation can't be computed such as in the Arctic Circle where there is
     * at least one day a year where the sun does not rise, and one where it does not set, a null will be
     * returned. See detailed explanation on top of the [AstronomicalCalendar] documentation.
     */
    fun getMinchaKetana(startOfDay: Instant?, endOfDay: Instant?): Instant? =
        getShaahZmanisBasedZman(startOfDay, endOfDay, 9.5)

    /**
     * This method returns *mincha ketana*,the preferred earliest time to pray *mincha* in the
     * opinion of the *[Rambam](https://en.wikipedia.org/wiki/Maimonides)* and others, that is 9.5
     * * [*shaos zmaniyos*][shaahZmanisGra] (solar hours) after [sunrise] or
     * [sea level sunrise][seaLevelSunrise] (depending on the [isUseElevation] setting), according
     * to the [GRA](https://en.wikipedia.org/wiki/Vilna_Gaon). For more information on this see the
     * documentation on [*mincha gedola*][.getMinchaGedola].
     * The day is calculated from [sea level sunrise][seaLevelSunrise] to [sea level][seaLevelSunrise] or [sunrise] to [sunset][sunset] (depending on the [isUseElevation]
     * setting.
     *
     * @see .getMinchaKetana
     * @see shaahZmanisGra
     * @see .getMinchaGedola
     * @see ComplexZmanimCalendar.getMinchaKetanaBaalHatanya
     * @return the `Date` of the time of mincha ketana. If the calculation can't be computed such as in the
     * Arctic Circle where there is at least one day a year where the sun does not rise, and one where it does
     * not set, a null will be returned. See detailed explanation on top of the [AstronomicalCalendar]
     * documentation.
     */
    val minchaKetana: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.MINCHA_KETANAH,
                ZmanCalculationMethod.DayDefinition.SUNRISE_TO_SUNSET,
                UsesElevation.IF_SET,
                listOf(ZmanAuthority.GRA)
            )
        ) {
            getMinchaKetana(elevationAdjustedSunrise, elevationAdjustedSunset)
        }

    /**
     * A generic method for calculating *plag hamincha* (the earliest time that Shabbos can be started) that is
     * 10.75 hours after the start of the day, (or 1.25 hours before the end of the day) based on the start and end of
     * the day passed to the method.
     * The time from the start of day to the end of day are divided into 12 *shaos zmaniyos* (temporal hours), and
     * *plag hamincha* is calculated as 10.75 of those *shaos zmaniyos* after the beginning of the day. As an
     * example, passing [sunrise] and [sunset][sunset] or [sea level][seaLevelSunrise] and [sea level sunset][seaLevelSunset] (depending on the [isUseElevation] elevation
     * setting) to this method will return *plag mincha* according to the opinion of the
     * *[GRA](https://en.wikipedia.org/wiki/Vilna_Gaon)*.
     *
     * @param startOfDay
     * the start of day for calculating plag. This can be sunrise or any *alos* passed to this method.
     * @param endOfDay
     * the end of day for calculating plag. This can be sunset or any *tzais* passed to this method.
     * @return the `Date` of the time of *plag hamincha* based on the start and end of day times
     * passed to this method. If the calculation can't be computed such as in the Arctic Circle where there is
     * at least one day a year where the sun does not rise, and one where it does not set, a null will be
     * returned. See detailed explanation on top of the [AstronomicalCalendar] documentation.
     */
    fun getPlagHamincha(startOfDay: Instant?, endOfDay: Instant?): Instant? =
        getShaahZmanisBasedZman(startOfDay, endOfDay, 10.75)

    /**
     * This method returns *plag hamincha*, that is 10.75 * [*shaos zmaniyos*][shaahZmanisGra]
     * (solar hours) after [sunrise] or [sea level sunrise][seaLevelSunrise] (depending on
     * the [isUseElevation] setting), according to the *[GRA](https://en.wikipedia.org/wiki/Vilna_Gaon)*. Plag hamincha is the earliest time that *Shabbos* can be started.
     * The day is calculated from [sea level sunrise][seaLevelSunrise] to [sea level][seaLevelSunrise] or [sunrise] to [sunset][sunset] (depending on the [isUseElevation]
     *
     * @see .getPlagHamincha
     * @see ComplexZmanimCalendar.getPlagHaminchaBaalHatanya
     * @return the `Date` of the time of *plag hamincha*. If the calculation can't be computed such as
     * in the Arctic Circle where there is at least one day a year where the sun does not rise, and one where it
     * does not set, a null will be returned. See detailed explanation on top of the
     * [AstronomicalCalendar] documentation.
     */
    val plagHamincha: Zman.DateBased
        get() = Zman.DateBased(
            ZmanDefinition(
                ZmanType.PLAG_HAMINCHA,
                ZmanCalculationMethod.DayDefinition.SUNRISE_TO_SUNSET,
                UsesElevation.IF_SET,
                listOf(ZmanAuthority.GRA)
            )
        ) {
            getPlagHamincha(elevationAdjustedSunrise, elevationAdjustedSunset)
        }

    /**
     * A method that returns a *shaah zmanis* ([temporal hour][temporalHour]) according to
     * the opinion of the *[GRA](https://en.wikipedia.org/wiki/Vilna_Gaon)*. This calculation divides
     * the day based on the opinion of the *GRA* that the day runs from [sea level sunrise][seaLevelSunrise] to [sea level sunset][seaLevelSunset] or [sunrise] to
     * [sunset] (depending on the [isUseElevation] setting). The day is split into 12 equal
     * parts with each one being a *shaah zmanis*. This method is similar to [temporalHour], but can
     * account for elevation.
     *
     * @return the `long` millisecond length of a *shaah zmanis* calculated from sunrise to sunset.
     * If the calculation can't be computed such as in the Arctic Circle where there is at least one day a year
     * where the sun does not rise, and one where it does not set, [Long.MIN_VALUE] will be returned. See
     * detailed explanation on top of the [AstronomicalCalendar] documentation.
     * @see .temporalHour
     * @see seaLevelSunrise
     * @see .seaLevelSunset
     * @see ComplexZmanimCalendar.shaahZmanisBaalHatanya
     */
    val shaahZmanisGra: Zman.ValueBased
        get() = Zman.ValueBased(
            ZmanDefinition(
                ZmanType.SHAA_ZMANIS,
                ZmanCalculationMethod.DayDefinition.SUNRISE_TO_SUNSET,
                UsesElevation.IF_SET,
                listOf(ZmanAuthority.GRA)
            )
        ) {
            getTemporalHour(elevationAdjustedSunrise, elevationAdjustedSunset).milliseconds
        }

    /**
     * A method that returns a *shaah zmanis* (temporal hour) according to the opinion of the *[Magen Avraham (MGA)](https://en.wikipedia.org/wiki/Avraham_Gombinern)* based on a 72 minutes *alos*
     * and *tzais*. This calculation divides the day that runs from dawn to dusk (for *sof zman krias shema* and
     * *tfila*). Dawn for this calculation is 72 minutes before [sunrise] or [sea level sunrise][seaLevelSunrise] (depending on the [isUseElevation] elevation setting) and dusk is 72 minutes after [sunset] or [sea level sunset][seaLevelSunset] (depending on the [isUseElevation] elevation
     * setting). This day is split into 12 equal parts with each part being a *shaah zmanis*. Alternate methods of calculating
     * a *shaah zmanis* according to the Magen Avraham (MGA) are available in the subclass [ComplexZmanimCalendar].
     *
     * @return the `long` millisecond length of a *shaah zmanis*. If the calculation can't be computed
     * such as in the Arctic Circle where there is at least one day a year where the sun does not rise, and one
     * where it does not set, [Long.MIN_VALUE] will be returned. See detailed explanation on top of the
     * [AstronomicalCalendar] documentation.
     */
    val shaahZmanisMGA: Zman.ValueBased
        get() = Zman.ValueBased(
            ZmanDefinition(
                ZmanType.SHAA_ZMANIS,
                ZmanCalculationMethod.DayDefinition.dawnToDusk(ZmanCalculationMethod.FixedDuration._72),
                UsesElevation.IF_SET,
                listOf(ZmanAuthority.MGA)
            )
        ) {
            getTemporalHour(alos72.momentOfOccurrence, tzais72.momentOfOccurrence).milliseconds
        }

    /**
     * This is a utility method to determine if the current Date (date-time) passed in has a *melacha* (work) prohibition.
     * Since there are many opinions on the time of *tzais*, the *tzais* for the current day has to be passed to this
     * class. Sunset is the classes current day's [elevation adjusted sunset][.getElevationAdjustedSunset] that observes the
     * [isUseElevation] settings. The [JewishCalendar.getInIsrael] will be set by the inIsrael parameter.
     *
     * @param currentTime the current time
     * @param tzais the time of tzais
     * @param inIsrael whether to use Israel holiday scheme or not
     *
     * @return true if *melacha* is prohibited or false if it is not.
     *
     * @see JewishCalendar.isAssurBemelacha
     * @see JewishCalendar.hasCandleLighting
     * @see JewishCalendar.setInIsrael
     */
    fun isAssurBemlacha(currentTime: Instant, tzais: Instant, inIsrael: Boolean): Boolean =
        JewishCalendar(localDateTime.date, inIsrael)
            .run {
                hasCandleLighting && currentTime >= elevationAdjustedSunset!! || //erev shabbos, YT or YT sheni and after shkiah
                        isAssurBemelacha && currentTime <= tzais //is shabbos or YT and it is before tzais
            }

    /**
     * A generic utility method for calculating any *shaah zmanis* (temporal hour) based *zman* with the
     * day defined as the start and end of day (or night) and the number of *shaahos zmaniyos* passed to the
     * method. This simplifies the code in other methods such as [.getPlagHamincha] and cuts down on
     * code replication. As an example, passing [sunrise] and [sunset][sunset] or [][seaLevelSunrise] and [sea level sunset][seaLevelSunset] (depending on the
     * [isUseElevation] elevation setting) and 10.75 hours to this method will return *plag mincha*
     * according to the opinion of the *[GRA](https://en.wikipedia.org/wiki/Vilna_Gaon)*.
     *
     * @param startOfDay
     * the start of day for calculating the *zman*. This can be sunrise or any *alos* passed
     * to this method.
     * @param endOfDay
     * the end of day for calculating the *zman*. This can be sunset or any *tzais* passed to
     * this method.
     * @param hours
     * the number of *shaahos zmaniyos* (temporal hours) to offset from the start of day
     * @return the `Date` of the time of *zman* with the *shaahos zmaniyos* (temporal hours)
     * in the day offset from the start of day passed to this method. If the calculation can't be computed such
     * as in the Arctic Circle where there is at least one day a year where the sun does not rise, and one
     * where it does not set, a null will be  returned. See detailed explanation on top of the [] documentation.
     */
    fun getShaahZmanisBasedZman(startOfDay: Instant?, endOfDay: Instant?, hours: Double): Instant? =
        getTimeOffset(startOfDay, /*shaa zmanis:*/getTemporalHour(startOfDay, endOfDay) * hours)

    open val allZmanim by lazy {
        listOf(
            tzais,
            alosHashachar,
            alos72,
            chatzos,
            sofZmanShmaGRA,
            sofZmanShmaMGA,
            tzais72,
            candleLighting,
            sofZmanTfilaGRA,
            sofZmanTfilaMGA,
            minchaGedola,
            minchaKetana,
            plagHamincha
        )
    }

    open val allShaosZmaniyos by lazy {
        listOf(shaahZmanisGra, shaahZmanisMGA)
    }

    companion object {
        /**
         * The zenith of 16.1 below geometric zenith (90). This calculation is used for determining *alos*
         * (dawn) and *tzais* (nightfall) in some opinions. It is based on the calculation that the time between dawn
         * and sunrise (and sunset to nightfall) is 72 minutes, the time that is takes to walk 4 *mil* at 18 minutes
         * a mil (*[Rambam](https://en.wikipedia.org/wiki/Maimonides)* and others). The sun's position at
         * 72 minutes before [sunrise] in Jerusalem [around the equinox / equilux](https://kosherjava.com/2022/01/12/equinox-vs-equilux-zmanim-calculations/) is
         * 16.1 below [geometric zenith][AstronomicalCalendar.GEOMETRIC_ZENITH].
         *
         * @see alosHashachar
         * @see ComplexZmanimCalendar.getAlos16Point1Degrees
         * @see ComplexZmanimCalendar.getTzais16Point1Degrees
         * @see ComplexZmanimCalendar.getSofZmanShmaMGA16Point1Degrees
         * @see ComplexZmanimCalendar.getSofZmanTfilaMGA16Point1Degrees
         * @see ComplexZmanimCalendar.getMinchaGedola16Point1Degrees
         * @see ComplexZmanimCalendar.getMinchaKetana16Point1Degrees
         * @see ComplexZmanimCalendar.getPlagHamincha16Point1Degrees
         * @see ComplexZmanimCalendar.getPlagAlos16Point1ToTzaisGeonim7Point083Degrees
         * @see ComplexZmanimCalendar.getSofZmanShmaAlos16Point1ToSunset
         */
        const val ZENITH_16_POINT_1: Double = GEOMETRIC_ZENITH + 16.1

        /**
         * The zenith of 8.5 below geometric zenith (90). This calculation is used for calculating *alos*
         * (dawn) and *tzais* (nightfall) in some opinions. This calculation is based on the position of the sun 36
         * minutes after [sunset][sunset] in Jerusalem [around the equinox / equilux](https://kosherjava.com/2022/01/12/equinox-vs-equilux-zmanim-calculations/), which
         * is 8.5 below [geometric zenith][AstronomicalCalendar.GEOMETRIC_ZENITH]. The *[Ohr Meir](https://www.worldcat.org/oclc/29283612)* considers this the time that 3 small stars are visible,
         * which is later than the required 3 medium stars.
         *
         * @see tzais
         * @see ComplexZmanimCalendar.getTzaisGeonim8Point5Degrees
         */
        const val ZENITH_8_POINT_5: Double = GEOMETRIC_ZENITH + 8.5
    }
}