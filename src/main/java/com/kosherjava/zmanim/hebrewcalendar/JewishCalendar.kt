/*
 * Zmanim Java API
 * Copyright (C) 2011 - 2023 Eliyahu Hershfeld
 * Copyright (C) September 2002 Avrom Finkelstien
 * Copyright (C) 2019 - 2022 Y Paritcher
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

import com.kosherjava.zmanim.util.GeoLocation
import java.time.LocalDate
import java.util.*

/**
 * The JewishCalendar extends the JewishDate class and adds calendar methods.
 *
 * This open source Java code was originally ported by [Avrom Finkelstien](http://www.facebook.com/avromf)
 * from his C++ code. It was refactored to fit the KosherJava Zmanim API with simplification of the code, enhancements
 * and some bug fixing. The class allows setting whether the holiday and *parsha* scheme follows the Israel scheme
 * or outside Israel scheme. The default is the outside Israel scheme.
 * The parsha code was ported by Y. Paritcher from his [libzmanim](https://github.com/yparitcher/libzmanim) code.
 *
 * @todo Some do not belong in this class, but here is a partial list of what should still be implemented in some form:
 *
 *  1. Mishna yomis etc
 *
 *
 * @see Date
 *
 * @see Calendar
 *
 * @author  Y. Paritcher 2019 - 2022
 * @author  Avrom Finkelstien 2002
 * @author  Eliyahu Hershfeld 2011 - 2023
 */
class JewishCalendar : JewishDate {
    /**
     * Gets whether Israel holiday scheme is used or not. The default (if not set) is false.
     *
     * @return if the calendar is set to Israel
     *
     * @see .setInIsrael
     */
    /**
     * Sets whether to use Israel holiday scheme or not. Default is false.
     *
     * @param inIsrael
     * set to true for calculations for Israel
     *
     * @see .getInIsrael
     */
    /**
     * Is the calendar set to Israel, where some holidays have different rules.
     * @see .getInIsrael
     * @see .setInIsrael
     */
    var inIsrael: Boolean = false
    /**
     * Returns if the city is set as a city surrounded by a wall from the time of Yehoshua, and Shushan Purim
     * should be celebrated as opposed to regular Purim.
     * @return if the city is set as a city surrounded by a wall from the time of Yehoshua, and Shushan Purim
     * should be celebrated as opposed to regular Purim.
     * @see .setIsMukafChoma
     */
    /**
     * Sets if the location is surrounded by a wall from the time of Yehoshua, and Shushan Purim should be
     * celebrated as opposed to regular Purim. This should be set for Yerushalayim, Shushan and other cities.
     * @param isMukafChoma is the city surrounded by a wall from the time of Yehoshua.
     *
     * @see .getIsMukafChoma
     */
    /**
     * Is the calendar set to have Purim *demukafim*, where Purim is celebrated on Shushan Purim.
     * @see .getIsMukafChoma
     * @see .setIsMukafChoma
     */
    var isMukafChoma: Boolean = false
    /**
     * Is this calendar set to return modern Israeli national holidays. By default this value is false. The holidays
     * are [&lt;em&gt;Yom HaShoah&lt;/em&gt;][.YOM_HASHOAH], [&lt;em&gt;Yom Hazikaron&lt;/em&gt;][.YOM_HAZIKARON], [ ][.YOM_HAATZMAUT] and [&lt;em&gt;Yom Yerushalayim&lt;/em&gt;][.YOM_YERUSHALAYIM].
     *
     * @return the useModernHolidays true if set to return modern Israeli national holidays
     *
     * @see .setUseModernHolidays
     */
    /**
     * Sets the calendar to return modern Israeli national holidays. By default this value is false. The holidays are:
     * [&lt;em&gt;Yom HaShoah&lt;/em&gt;][.YOM_HASHOAH], [&lt;em&gt;Yom Hazikaron&lt;/em&gt;][.YOM_HAZIKARON], [ ][.YOM_HAATZMAUT] and [&lt;em&gt;Yom Yerushalayim&lt;/em&gt;][.YOM_YERUSHALAYIM].
     *
     * @param useModernHolidays
     * the useModernHolidays to set
     *
     * @see .isUseModernHolidays
     */
    /**
     * Is the calendar set to use modern Israeli holidays such as Yom Haatzmaut.
     * @see .isUseModernHolidays
     * @see .setUseModernHolidays
     */
    var isUseModernHolidays: Boolean = false

    /**
     * List of *parshiyos* or special *Shabasos*. [.NONE] indicates a week without a *parsha*, while the enum for
     * the *parsha* of [.VZOS_HABERACHA] exists for consistency, but is not currently used. The special *Shabasos* of
     * Shekalim, Zachor, Para, Hachodesh, as well as Shabbos Shuva, Shira, Hagadol, Chazon and Nachamu are also represented in this collection
     * of *parshiyos*.
     * @see .getSpecialShabbos
     * @see .getParshah
     */
    enum class Parsha {
        /**NONE A week without any *parsha* such as *Shabbos Chol Hamoed*  */
        NONE,

        /**BERESHIS */
        BERESHIS,

        /**NOACH */
        NOACH,

        /**LECH_LECHA */
        LECH_LECHA,

        /**VAYERA */
        VAYERA,

        /**CHAYEI_SARA */
        CHAYEI_SARA,

        /**TOLDOS */
        TOLDOS,

        /**VAYETZEI */
        VAYETZEI,

        /**VAYISHLACH */
        VAYISHLACH,

        /**VAYESHEV */
        VAYESHEV,

        /**MIKETZ */
        MIKETZ,

        /**VAYIGASH */
        VAYIGASH,

        /**VAYECHI */
        VAYECHI,

        /**SHEMOS */
        SHEMOS,

        /**VAERA */
        VAERA,

        /**BO */
        BO,

        /**BESHALACH */
        BESHALACH,

        /**YISRO */
        YISRO,

        /**MISHPATIM */
        MISHPATIM,

        /**TERUMAH */
        TERUMAH,

        /**TETZAVEH */
        TETZAVEH,

        /***KI_SISA */
        KI_SISA,

        /**VAYAKHEL */
        VAYAKHEL,

        /**PEKUDEI */
        PEKUDEI,

        /**VAYIKRA */
        VAYIKRA,

        /**TZAV */
        TZAV,

        /**SHMINI */
        SHMINI,

        /**TAZRIA */
        TAZRIA,

        /**METZORA */
        METZORA,

        /**ACHREI_MOS */
        ACHREI_MOS,

        /**KEDOSHIM */
        KEDOSHIM,

        /**EMOR */
        EMOR,

        /**BEHAR */
        BEHAR,

        /**BECHUKOSAI */
        BECHUKOSAI,

        /**BAMIDBAR */
        BAMIDBAR,

        /**NASSO */
        NASSO,

        /**BEHAALOSCHA */
        BEHAALOSCHA,

        /**SHLACH */
        SHLACH,

        /**KORACH */
        KORACH,

        /**CHUKAS */
        CHUKAS,

        /**BALAK */
        BALAK,

        /**PINCHAS */
        PINCHAS,

        /**MATOS */
        MATOS,

        /**MASEI */
        MASEI,

        /**DEVARIM */
        DEVARIM,

        /**VAESCHANAN */
        VAESCHANAN,

        /**EIKEV */
        EIKEV,

        /**REEH */
        REEH,

        /**SHOFTIM */
        SHOFTIM,

        /**KI_SEITZEI */
        KI_SEITZEI,

        /**KI_SAVO */
        KI_SAVO,

        /**NITZAVIM */
        NITZAVIM,

        /**VAYEILECH */
        VAYEILECH,

        /**HAAZINU */
        HAAZINU,

        /**VZOS_HABERACHA */
        VZOS_HABERACHA,

        /**The double parsha of Vayakhel &amp; Peudei */
        VAYAKHEL_PEKUDEI,

        /**The double *parsha* of Tazria
         * &amp; Metzora */
        TAZRIA_METZORA,

        /**The double *parsha* of Achrei Mos &amp; Kedoshim */
        ACHREI_MOS_KEDOSHIM,

        /**The double *parsha*
         * of Behar &amp; Bechukosai */
        BEHAR_BECHUKOSAI,

        /**The double *parsha* of Chukas &amp; Balak */
        CHUKAS_BALAK,

        /**The double
         * *parsha* of Matos &amp; Masei */
        MATOS_MASEI,

        /**The double *parsha* of Nitzavim &amp; Vayelech */
        NITZAVIM_VAYEILECH,

        /**The special *parsha* of Shekalim */
        SHKALIM,

        /** The special *parsha* of Zachor */
        ZACHOR,

        /**The special *parsha* of
         * Para */
        PARA,

        /** The special *parsha* of Hachodesh */
        HACHODESH,

        /***Shabbos* Shuva */
        SHUVA,

        /***Shabbos* Shira */
        SHIRA,

        /***Shabbos* Hagadol */
        HAGADOL,

        /***Shabbos* Chazon */
        CHAZON,

        /***Shabbos* Nachamu */
        NACHAMU
    }

    /**
     * Default constructor will set a default date to the current system date.
     */
    constructor() : super() {}

    /**
     * A constructor that initializes the date to the [Date] parameter.
     *
     * @param date
     * the `Date` to set the calendar to
     */
    constructor(date: Date?) : super(date) {}

    /**
     * A constructor that initializes the date to the [Calendar] parameter.
     *
     * @param calendar
     * the `Calendar` to set the calendar to
     */
    constructor(calendar: Calendar) : super(calendar)

    /**
     * A constructor that initializes the date to the [LocalDate] parameter.
     *
     * @param localDate
     * the `LocalDate` to set the calendar to
     */
    constructor(localDate: LocalDate) : super(localDate)

    /**
     * Creates a Jewish date based on a Jewish year, month and day of month.
     *
     * @param jewishYear
     * the Jewish year
     * @param jewishMonth
     * the Jewish month. The method expects a 1 for Nissan ... 12 for Adar and 13 for Adar II. Use the
     * constants [.NISSAN] ... [.ADAR] (or [.ADAR_II] for a leap year Adar II) to avoid any
     * confusion.
     * @param jewishDayOfMonth
     * the Jewish day of month. If 30 is passed in for a month with only 29 days (for example [.IYAR],
     * or [.KISLEV] in a year that [.isKislevShort]), the 29th (last valid date of the month)
     * will be set
     * @throws IllegalArgumentException
     * if the day of month is &lt; 1 or &gt; 30, or a year of &lt; 0 is passed in.
     */
    constructor(jewishYear: Int, jewishMonth: Int, jewishDayOfMonth: Int) : super(
        jewishYear,
        jewishMonth,
        jewishDayOfMonth
    ) {
    }

    /**
     * Creates a Jewish date based on a Jewish date and whether in Israel
     *
     * @param jewishYear
     * the Jewish year
     * @param jewishMonth
     * the Jewish month. The method expects a 1 for *Nissan* ... 12 for *Adar* and 13 for
     * *Adar II*. Use the constants [.NISSAN] ... [.ADAR] (or [.ADAR_II] for a
     * leap year Adar II) to avoid any confusion.
     * @param jewishDayOfMonth
     * the Jewish day of month. If 30 is passed in for a month with only 29 days (for example [.IYAR],
     * or [.KISLEV] in a year that [.isKislevShort]), the 29th (last valid date of the month)
     * will be set.
     * @param inIsrael
     * whether in Israel. This affects *Yom Tov* calculations
     */
    constructor(jewishYear: Int, jewishMonth: Int, jewishDayOfMonth: Int, inIsrael: Boolean) : super(
        jewishYear,
        jewishMonth,
        jewishDayOfMonth
    ) {
        this.inIsrael = inIsrael
    }// 28 years of 365.25 days + the offset from molad tohu mentioned above//elapsed days since molad ToHu
    //elapsed days to the current calendar date

    /* Molad Nissan year 1 was 177 days after molad tohu of Tishrei. We multiply 29.5 days * 6 months from Tishrei
      * to Nissan = 177. Subtract 7 days since tekufas Nissan was 7 days and 9 hours before the molad as stated in the Rambam
      * and we are now at 170 days. Because getJewishCalendarElapsedDays and getDaysSinceStartOfJewishYear use the value for
      * Rosh Hashana as 1, we have to add 1 day for a total of 171. To this add a day since the tekufah is on a Tuesday
      * night and we push off the bracha to Wednesday AM resulting in the 172 used in the calculation.
      */
    /**
     * [Birkas Hachamah](https://en.wikipedia.org/wiki/Birkat_Hachama) is recited every 28 years based on
     * *Tekufas Shmuel* (Julian years) that a year is 365.25 days. The [Rambam](https://en.wikipedia.org/wiki/Maimonides) in [&amp;&amp;Hilchos Kiddush Hachodesh 9:3](http://hebrewbooks.org/pdfpager.aspx?req=14278&amp;st=&amp;pgnum=323)
     * states that *tekufas Nissan* of year 1 was 7 days + 9 hours before *molad Nissan*. This is calculated as every
     * 10,227 days (28 * 365.25).
     * @return true for a day that *Birkas Hachamah* is recited.
     */
    val isBirkasHachamah: Boolean
        get() {
            var elapsedDays: Int = getJewishCalendarElapsedDays(getJewishYear()) //elapsed days since molad ToHu
            elapsedDays += daysSinceStartOfJewishYear //elapsed days to the current calendar date

            /* Molad Nissan year 1 was 177 days after molad tohu of Tishrei. We multiply 29.5 days * 6 months from Tishrei
              * to Nissan = 177. Subtract 7 days since tekufas Nissan was 7 days and 9 hours before the molad as stated in the Rambam
              * and we are now at 170 days. Because getJewishCalendarElapsedDays and getDaysSinceStartOfJewishYear use the value for
              * Rosh Hashana as 1, we have to add 1 day for a total of 171. To this add a day since the tekufah is on a Tuesday
              * night and we push off the bracha to Wednesday AM resulting in the 172 used in the calculation.
              */if (elapsedDays % (28 * 365.25) == 172.0) { // 28 years of 365.25 days + the offset from molad tohu mentioned above
                return true
            }
            return false
        }//ZaSh
    //keep the compiler happy
//ZaCh//Hak//HaSh//BaSh//BaCh//not a leap year//ZaSh//ZaCh//HaSh//HaCh//BaSh//BaCh// convert 0 to 7 for Shabbos for readability// plus one to the original Rosh Hashana of year 1 to get a week starting on Sunday
    /**
     * Return the type of year for *parsha* calculations. The algorithm follows the
     * [&amp;&amp;Luach Arba'ah Shearim](http://hebrewbooks.org/pdfpager.aspx?req=14268&amp;st=&amp;pgnum=222) in the Tur Ohr Hachaim.
     * @return the type of year for *parsha* calculations.
     */
    private val parshaYearType: Int
        private get() {
            var roshHashanaDayOfWeek: Int =
                (getJewishCalendarElapsedDays(getJewishYear()) + 1) % 7 // plus one to the original Rosh Hashana of year 1 to get a week starting on Sunday
            if (roshHashanaDayOfWeek == 0) {
                roshHashanaDayOfWeek = 7 // convert 0 to 7 for Shabbos for readability
            }
            if (isJewishLeapYear) {
                when (roshHashanaDayOfWeek) {
                    Calendar.MONDAY -> {
                        if (isKislevShort) { //BaCh
                            if (inIsrael) {
                                return 14
                            }
                            return 6
                        }
                        if (isCheshvanLong) { //BaSh
                            if (inIsrael) {
                                return 15
                            }
                            return 7
                        }
                    }
                    Calendar.TUESDAY -> {
                        if (inIsrael) {
                            return 15
                        }
                        return 7
                    }
                    Calendar.THURSDAY -> {
                        if (isKislevShort) { //HaCh
                            return 8
                        }
                        if (isCheshvanLong) { //HaSh
                            return 9
                        }
                    }
                    Calendar.SATURDAY -> {
                        if (isKislevShort) { //ZaCh
                            return 10
                        }
                        if (isCheshvanLong) { //ZaSh
                            if (inIsrael) {
                                return 16
                            }
                            return 11
                        }
                    }
                }
            } else { //not a leap year
                when (roshHashanaDayOfWeek) {
                    Calendar.MONDAY -> {
                        if (isKislevShort) { //BaCh
                            return 0
                        }
                        if (isCheshvanLong) { //BaSh
                            if (inIsrael) {
                                return 12
                            }
                            return 1
                        }
                    }
                    Calendar.TUESDAY -> {
                        if (inIsrael) {
                            return 12
                        }
                        return 1
                    }
                    Calendar.THURSDAY -> {
                        if (isCheshvanLong) { //HaSh
                            return 3
                        }
                        if (!isKislevShort) { //Hak
                            if (inIsrael) {
                                return 13
                            }
                            return 2
                        }
                    }
                    Calendar.SATURDAY -> {
                        if (isKislevShort) { //ZaCh
                            return 4
                        }
                        if (isCheshvanLong) { //ZaSh
                            return 5
                        }
                    }
                }
            }
            return -1 //keep the compiler happy
        }// negative year should be impossible, but let's cover all bases
    //keep the compiler happy
    /**
     * Returns this week's [&lt;em&gt;Parsha&lt;/em&gt;][Parsha] if it is *Shabbos*. It returns [Parsha.NONE] if the date
     * is a weekday or if there is no *parsha* that week (for example *Yom Tov* that falls on a *Shabbos*).
     *
     * @return the current *parsha*.
     */
    val parshah: Parsha
        get() {
            if (dayOfWeek != Calendar.SATURDAY) {
                return Parsha.NONE
            }
            val yearType: Int = parshaYearType
            val roshHashanaDayOfWeek: Int = getJewishCalendarElapsedDays(getJewishYear()) % 7
            val day: Int = roshHashanaDayOfWeek + daysSinceStartOfJewishYear
            if (yearType >= 0) { // negative year should be impossible, but let's cover all bases
                return parshalist[yearType][day / 7]
            }
            return Parsha.NONE //keep the compiler happy
        }//Yom Kippur / Sukkos or Pesach with 2 potential non-parsha Shabbosim in a row

    /**
     * Returns the upcoming [&lt;em&gt;Parsha&lt;/em&gt;][Parsha] regardless of if it is the weekday or *Shabbos* (where next
     * Shabbos's *Parsha* will be returned. This is unlike [.getParshah] that returns [Parsha.NONE] if
     * the date is not *Shabbos*. If the upcoming Shabbos is a *Yom Tov* and has no *Parsha*, the
     * following week's *Parsha* will be returned.
     *
     * @return the upcoming *parsha*.
     */
    val upcomingParshah: Parsha
        get() {
            val clone: JewishCalendar = clone() as JewishCalendar
            val daysToShabbos: Int = (Calendar.SATURDAY - dayOfWeek + 7) % 7
            if (dayOfWeek != Calendar.SATURDAY) {
                clone.forward(Calendar.DATE, daysToShabbos)
            } else {
                clone.forward(Calendar.DATE, 7)
            }
            while (clone.parshah == Parsha.NONE) { //Yom Kippur / Sukkos or Pesach with 2 potential non-parsha Shabbosim in a row
                clone.forward(Calendar.DATE, 7)
            }
            return clone.parshah
        }

    /**
     * Returns a [&lt;em&gt;Parsha&lt;/em&gt;][Parsha] enum if the *Shabbos* is one of the four *parshiyos* of [ ][Parsha.SHKALIM], [&lt;em&gt;Zachor&lt;/em&gt;][Parsha.ZACHOR], [&lt;em&gt;Para&lt;/em&gt;][Parsha.PARA], [ ][Parsha.HACHODESH] or [Parsha.NONE] for a regular *Shabbos* (or any weekday).
     *
     * @return one of the four *parshiyos* of [&lt;em&gt;Shkalim&lt;/em&gt;][Parsha.SHKALIM] [         &lt;em&gt;Zachor&lt;/em&gt;][Parsha.ZACHOR], [&lt;em&gt;Para&lt;/em&gt;][Parsha.PARA], [&lt;em&gt;Hachdesh&lt;/em&gt;][Parsha.HACHODESH] or [         ][Parsha.NONE].
     */
    val specialShabbos: Parsha
        get() {
            if (dayOfWeek == Calendar.SATURDAY) {
                if ((getJewishMonth() == SHEVAT && !isJewishLeapYear) || (getJewishMonth() == ADAR && isJewishLeapYear)) {
                    if ((jewishDayOfMonth == 25) || (jewishDayOfMonth == 27) || (jewishDayOfMonth == 29)) {
                        return Parsha.SHKALIM
                    }
                }
                if ((getJewishMonth() == ADAR && !isJewishLeapYear) || getJewishMonth() == ADAR_II) {
                    if (jewishDayOfMonth == 1) {
                        return Parsha.SHKALIM
                    }
                    if ((jewishDayOfMonth == 8) || (jewishDayOfMonth == 9) || (jewishDayOfMonth == 11) || (jewishDayOfMonth == 13)) {
                        return Parsha.ZACHOR
                    }
                    if ((jewishDayOfMonth == 18) || (jewishDayOfMonth == 20) || (jewishDayOfMonth == 22) || (jewishDayOfMonth == 23)) {
                        return Parsha.PARA
                    }
                    if ((jewishDayOfMonth == 25) || (jewishDayOfMonth == 27) || (jewishDayOfMonth == 29)) {
                        return Parsha.HACHODESH
                    }
                }
                if (getJewishMonth() == NISSAN) {
                    if (jewishDayOfMonth == 1) {
                        return Parsha.HACHODESH
                    }
                    if (jewishDayOfMonth >= 8 && jewishDayOfMonth <= 14) {
                        return Parsha.HAGADOL
                    }
                }
                if (getJewishMonth() == AV) {
                    if (jewishDayOfMonth >= 4 && jewishDayOfMonth <= 9) {
                        return Parsha.CHAZON
                    }
                    if (jewishDayOfMonth >= 10 && jewishDayOfMonth <= 16) {
                        return Parsha.NACHAMU
                    }
                }
                if (getJewishMonth() == TISHREI) {
                    if (jewishDayOfMonth >= 3 && jewishDayOfMonth <= 8) {
                        return Parsha.SHUVA
                    }
                }
                if (parshah == Parsha.BESHALACH) {
                    return Parsha.SHIRA
                }
            }
            return Parsha.NONE
        }// if 13th Adar falls on Friday or Shabbos, push back to Thursday
    // if we get to this stage, then there are no holidays for the given date return -1
// else if a leap year// if 13th Adar falls on Friday or Shabbos, push back to Thursday// if (day == 24) {
    // return EREV_CHANUKAH;
    // } else
// push off Tzom Gedalia if it falls on Shabbos// if Tisha B'av falls on Shabbos, push off until Sunday// push off the fast day if it falls on Shabbos// if 5 Iyar falls on Wed, Yom Haatzmaut is that day. If it fal1s on Friday or Shabbos, it is moved back to
    // Thursday. If it falls on Monday it is moved to Tuesday
    /**
     * Returns an index of the Jewish holiday or fast day for the current day, or a -1 if there is no holiday for this day.
     * There are constants in this class representing each *Yom Tov*. Formatting of the *Yomim tovim* is done
     * in the [HebrewDateFormatter.formatYomTov].
     *
     * @todo Consider using enums instead of the constant ints.
     *
     * @return the index of the holiday such as the constant [.LAG_BAOMER] or [.YOM_KIPPUR] or a -1 if it is not a holiday.
     *
     * @see HebrewDateFormatter.formatYomTov
     */
    val yomTovIndex: Int
        get() {
            val day: Int = jewishDayOfMonth
            val dayOfWeek: Int = dayOfWeek
            when (getJewishMonth()) {
                NISSAN -> {
                    if (day == 14) {
                        return EREV_PESACH
                    }
                    if ((day == 15) || (day == 21
                                ) || (!inIsrael && (day == 16 || day == 22))
                    ) {
                        return PESACH
                    }
                    if ((day >= 17 && day <= 20
                                || (day == 16 && inIsrael))
                    ) {
                        return CHOL_HAMOED_PESACH
                    }
                    if ((day == 22 && inIsrael) || (day == 23 && !inIsrael)) {
                        return ISRU_CHAG
                    }
                    if ((isUseModernHolidays
                                && (((day == 26 && dayOfWeek == Calendar.THURSDAY)
                                || (day == 28 && dayOfWeek == Calendar.MONDAY)
                                || ((day == 27) && (dayOfWeek != Calendar.SUNDAY) && (dayOfWeek != Calendar.FRIDAY)))))
                    ) {
                        return YOM_HASHOAH
                    }
                }
                IYAR -> {
                    if ((isUseModernHolidays
                                && (((day == 4 && dayOfWeek == Calendar.TUESDAY)
                                || ((day == 3 || day == 2) && dayOfWeek == Calendar.WEDNESDAY) || (day == 5 && dayOfWeek == Calendar.MONDAY))))
                    ) {
                        return YOM_HAZIKARON
                    }
                    // if 5 Iyar falls on Wed, Yom Haatzmaut is that day. If it fal1s on Friday or Shabbos, it is moved back to
                    // Thursday. If it falls on Monday it is moved to Tuesday
                    if ((isUseModernHolidays
                                && (((day == 5 && dayOfWeek == Calendar.WEDNESDAY)
                                || ((day == 4 || day == 3) && dayOfWeek == Calendar.THURSDAY) || (day == 6 && dayOfWeek == Calendar.TUESDAY))))
                    ) {
                        return YOM_HAATZMAUT
                    }
                    if (day == 14) {
                        return PESACH_SHENI
                    }
                    if (day == 18) {
                        return LAG_BAOMER
                    }
                    if (isUseModernHolidays && day == 28) {
                        return YOM_YERUSHALAYIM
                    }
                }
                SIVAN -> {
                    if (day == 5) {
                        return EREV_SHAVUOS
                    }
                    if (day == 6 || (day == 7 && !inIsrael)) {
                        return SHAVUOS
                    }
                    if ((day == 7 && inIsrael) || (day == 8 && !inIsrael)) {
                        return ISRU_CHAG
                    }
                }
                TAMMUZ ->            // push off the fast day if it falls on Shabbos
                    if (((day == 17 && dayOfWeek != Calendar.SATURDAY)
                                || (day == 18 && dayOfWeek == Calendar.SUNDAY))
                    ) {
                        return SEVENTEEN_OF_TAMMUZ
                    }
                AV -> {
                    // if Tisha B'av falls on Shabbos, push off until Sunday
                    if (((dayOfWeek == Calendar.SUNDAY && day == 10)
                                || (dayOfWeek != Calendar.SATURDAY && day == 9))
                    ) {
                        return TISHA_BEAV
                    }
                    if (day == 15) {
                        return TU_BEAV
                    }
                }
                ELUL -> if (day == 29) {
                    return EREV_ROSH_HASHANA
                }
                TISHREI -> {
                    if (day == 1 || day == 2) {
                        return ROSH_HASHANA
                    }
                    if ((day == 3 && dayOfWeek != Calendar.SATURDAY) || (day == 4 && dayOfWeek == Calendar.SUNDAY)) {
                        // push off Tzom Gedalia if it falls on Shabbos
                        return FAST_OF_GEDALYAH
                    }
                    if (day == 9) {
                        return EREV_YOM_KIPPUR
                    }
                    if (day == 10) {
                        return YOM_KIPPUR
                    }
                    if (day == 14) {
                        return EREV_SUCCOS
                    }
                    if (day == 15 || (day == 16 && !inIsrael)) {
                        return SUCCOS
                    }
                    if (day >= 17 && day <= 20 || (day == 16 && inIsrael)) {
                        return CHOL_HAMOED_SUCCOS
                    }
                    if (day == 21) {
                        return HOSHANA_RABBA
                    }
                    if (day == 22) {
                        return SHEMINI_ATZERES
                    }
                    if (day == 23 && !inIsrael) {
                        return SIMCHAS_TORAH
                    }
                    if ((day == 23 && inIsrael) || (day == 24 && !inIsrael)) {
                        return ISRU_CHAG
                    }
                }
                KISLEV ->            // if (day == 24) {
                    // return EREV_CHANUKAH;
                    // } else
                    if (day >= 25) {
                        return CHANUKAH
                    }
                TEVES -> {
                    if ((day == 1) || (day == 2
                                ) || (day == 3 && isKislevShort)
                    ) {
                        return CHANUKAH
                    }
                    if (day == 10) {
                        return TENTH_OF_TEVES
                    }
                }
                SHEVAT -> if (day == 15) {
                    return TU_BESHVAT
                }
                ADAR -> if (!isJewishLeapYear) {
                    // if 13th Adar falls on Friday or Shabbos, push back to Thursday
                    if ((((day == 11 || day == 12) && dayOfWeek == Calendar.THURSDAY)
                                || (day == 13 && !(dayOfWeek == Calendar.FRIDAY || dayOfWeek == Calendar.SATURDAY)))
                    ) {
                        return FAST_OF_ESTHER
                    }
                    if (day == 14) {
                        return PURIM
                    }
                    if (day == 15) {
                        return SHUSHAN_PURIM
                    }
                } else { // else if a leap year
                    if (day == 14) {
                        return PURIM_KATAN
                    }
                    if (day == 15) {
                        return SHUSHAN_PURIM_KATAN
                    }
                }
                ADAR_II -> {
                    // if 13th Adar falls on Friday or Shabbos, push back to Thursday
                    if ((((day == 11 || day == 12) && dayOfWeek == Calendar.THURSDAY)
                                || (day == 13 && !(dayOfWeek == Calendar.FRIDAY || dayOfWeek == Calendar.SATURDAY)))
                    ) {
                        return FAST_OF_ESTHER
                    }
                    if (day == 14) {
                        return PURIM
                    }
                    if (day == 15) {
                        return SHUSHAN_PURIM
                    }
                }
            }
            // if we get to this stage, then there are no holidays for the given date return -1
            return -1
        }

    /**
     * Returns true if the current day is *Yom Tov*. The method returns true even for holidays such as [.CHANUKAH]
     * and minor ones such as [.TU_BEAV] and [.PESACH_SHENI]. *Erev Yom Tov* (with the exception of
     * [.HOSHANA_RABBA], *erev* the second days of [.PESACH]) returns false, as do [fast][.isTaanis] besides [.YOM_KIPPUR]. Use [.isAssurBemelacha] to find the days that have a prohibition of work.
     *
     * @return true if the current day is a Yom Tov
     *
     * @see .getYomTovIndex
     * @see .isErevYomTov
     * @see .isErevYomTovSheni
     * @see .isTaanis
     * @see .isAssurBemelacha
     * @see .isCholHamoed
     */
    val isYomTov: Boolean
        get() {
            val holidayIndex: Int = yomTovIndex
            if (((isErevYomTov && (holidayIndex != HOSHANA_RABBA || (holidayIndex == CHOL_HAMOED_PESACH && jewishDayOfMonth != 20)))
                        || (isTaanis && holidayIndex != YOM_KIPPUR) || (holidayIndex == ISRU_CHAG))
            ) {
                return false
            }
            return yomTovIndex != -1
        }

    /**
     * Returns true if the *Yom Tov* day has a *melacha* (work)  prohibition. This method will return false for a
     * non-*Yom Tov* day, even if it is *Shabbos*.
     *
     * @return if the *Yom Tov* day has a *melacha* (work)  prohibition.
     */
    val isYomTovAssurBemelacha: Boolean
        get() {
            val holidayIndex: Int = yomTovIndex
            return (holidayIndex == PESACH) || (holidayIndex == SHAVUOS) || (holidayIndex == SUCCOS) || (holidayIndex == SHEMINI_ATZERES) || (
                    holidayIndex == SIMCHAS_TORAH) || (holidayIndex == ROSH_HASHANA) || (holidayIndex == YOM_KIPPUR)
        }

    /**
     * Returns true if it is *Shabbos* or if it is a *Yom Tov* day that has a *melacha* (work)  prohibition.
     *
     * @return if the day is a *Yom Tov* that is *assur bemlacha* or *Shabbos*
     */
    val isAssurBemelacha: Boolean
        get() {
            return dayOfWeek == Calendar.SATURDAY || isYomTovAssurBemelacha
        }

    /**
     * Returns true if the day has candle lighting. This will return true on *Erev Shabbos*, *Erev Yom Tov*, the
     * first day of *Rosh Hashana* and the first days of *Yom Tov* out of Israel. It is identical
     * to calling [.isTomorrowShabbosOrYomTov].
     *
     * @return if the day has candle lighting.
     *
     * @see .isTomorrowShabbosOrYomTov
     */
    fun hasCandleLighting(): Boolean {
        return isTomorrowShabbosOrYomTov
    }

    /**
     * Returns true if tomorrow is *Shabbos* or *Yom Tov*. This will return true on *Erev Shabbos*,
     * *Erev Yom Tov*, the first day of *Rosh Hashana* and *erev* the first days of *Yom Tov*
     * out of Israel. It is identical to calling [.hasCandleLighting].
     *
     * @return will return if the next day is *Shabbos* or *Yom Tov*.
     *
     * @see .hasCandleLighting
     */
    val isTomorrowShabbosOrYomTov: Boolean
        get() {
            return (dayOfWeek == Calendar.FRIDAY) || isErevYomTov || isErevYomTovSheni
        }

    /**
     * Returns true if the day is the second day of *Yom Tov*. This impacts the second day of *Rosh Hashana* everywhere and
     * the second days of Yom Tov in *chutz laaretz* (out of Israel).
     *
     * @return  if the day is the second day of *Yom Tov*.
     */
    val isErevYomTovSheni: Boolean
        get() {
            return ((getJewishMonth() == TISHREI && (jewishDayOfMonth == 1))
                    || (!inIsrael
                    && (((getJewishMonth() == NISSAN && (jewishDayOfMonth == 15 || jewishDayOfMonth == 21))
                    || (getJewishMonth() == TISHREI && (jewishDayOfMonth == 15 || jewishDayOfMonth == 22))
                    || (getJewishMonth() == SIVAN && jewishDayOfMonth == 6)))))
        }

    /**
     * Returns true if the current day is *Aseres Yemei Teshuva*.
     *
     * @return if the current day is *Aseres Yemei Teshuvah*
     */
    val isAseresYemeiTeshuva: Boolean
        get() {
            return getJewishMonth() == TISHREI && jewishDayOfMonth <= 10
        }

    /**
     * Returns true if the current day is *Pesach* (either  the *Yom Tov* of *Pesach* or*Chol Hamoed Pesach*).
     *
     * @return true if the current day is *Pesach* (either  the *Yom Tov* of *Pesach* or*Chol Hamoed Pesach*).
     * @see .isYomTov
     * @see .isCholHamoedPesach
     * @see .PESACH
     *
     * @see .CHOL_HAMOED_PESACH
     */
    val isPesach: Boolean
        get() {
            val holidayIndex: Int = yomTovIndex
            return holidayIndex == PESACH || holidayIndex == CHOL_HAMOED_PESACH
        }

    /**
     * Returns true if the current day is *Chol Hamoed* of *Pesach*.
     *
     * @return true if the current day is *Chol Hamoed* of *Pesach*
     * @see .isYomTov
     * @see .isPesach
     * @see .CHOL_HAMOED_PESACH
     */
    val isCholHamoedPesach: Boolean
        get() {
            val holidayIndex: Int = yomTovIndex
            return holidayIndex == CHOL_HAMOED_PESACH
        }

    /**
     * Returns true if the current day is *Shavuos*.
     *
     * @return true if the current day is *Shavuos*.
     * @see .isYomTov
     * @see .SHAVUOS
     */
    val isShavuos: Boolean
        get() {
            val holidayIndex: Int = yomTovIndex
            return holidayIndex == SHAVUOS
        }

    /**
     * Returns true if the current day is *Rosh Hashana*.
     *
     * @return true if the current day is *Rosh Hashana*.
     * @see .isYomTov
     * @see .ROSH_HASHANA
     */
    val isRoshHashana: Boolean
        get() {
            val holidayIndex: Int = yomTovIndex
            return holidayIndex == ROSH_HASHANA
        }

    /**
     * Returns true if the current day is *Yom Kippur*.
     *
     * @return true if the current day is *Yom Kippur*.
     * @see .isYomTov
     * @see .YOM_KIPPUR
     */
    val isYomKippur: Boolean
        get() {
            val holidayIndex: Int = yomTovIndex
            return holidayIndex == YOM_KIPPUR
        }

    /**
     * Returns true if the current day is *Succos* (either  the *Yom Tov* of *Succos* or*Chol Hamoed Succos*).
     * It will return false for [Shmini Atzeres][.isShminiAtzeres] and [Simchas Torah][.isSimchasTorah].
     *
     * @return true if the current day is *Succos* (either  the *Yom Tov* of *Succos* or*Chol Hamoed Succos*.
     * @see .isYomTov
     * @see .isCholHamoedSuccos
     * @see .isHoshanaRabba
     * @see .SUCCOS
     *
     * @see .CHOL_HAMOED_SUCCOS
     *
     * @see .HOSHANA_RABBA
     */
    val isSuccos: Boolean
        get() {
            val holidayIndex: Int = yomTovIndex
            return (holidayIndex == SUCCOS) || (holidayIndex == CHOL_HAMOED_SUCCOS) || (holidayIndex == HOSHANA_RABBA)
        }

    /**
     * Returns true if the current day is *Hoshana Rabba*.
     *
     * @return true true if the current day is *Hoshana Rabba*.
     * @see .isYomTov
     * @see .HOSHANA_RABBA
     */
    val isHoshanaRabba: Boolean
        get() {
            val holidayIndex: Int = yomTovIndex
            return holidayIndex == HOSHANA_RABBA
        }

    /**
     * Returns true if the current day is *Shmini Atzeres*.
     *
     * @return true if the current day is *Shmini Atzeres*.
     * @see .isYomTov
     * @see .SHEMINI_ATZERES
     */
    val isShminiAtzeres: Boolean
        get() {
            val holidayIndex: Int = yomTovIndex
            return holidayIndex == SHEMINI_ATZERES
        }//if in Israel, Holiday index of SIMCHAS_TORAH will not be returned by yomTovIndex

    /**
     * Returns true if the current day is *Simchas Torah*. This will always return false if [in Israel][.getInIsrael]
     *
     * @return true if the current day is *Shmini Atzeres*.
     * @see .isYomTov
     * @see .SIMCHAS_TORAH
     */
    val isSimchasTorah: Boolean
        get() {
            val holidayIndex: Int = yomTovIndex
            //if in Israel, Holiday index of SIMCHAS_TORAH will not be returned by yomTovIndex
            return holidayIndex == SIMCHAS_TORAH
        }

    /**
     * Returns true if the current day is *Chol Hamoed* of *Succos*.
     *
     * @return true if the current day is *Chol Hamoed* of *Succos*
     * @see .isYomTov
     * @see .CHOL_HAMOED_SUCCOS
     */
    val isCholHamoedSuccos: Boolean
        get() {
            val holidayIndex: Int = yomTovIndex
            return holidayIndex == CHOL_HAMOED_SUCCOS || holidayIndex == HOSHANA_RABBA
        }

    /**
     * Returns true if the current day is *Chol Hamoed* of *Pesach* or *Succos*.
     *
     * @return true if the current day is *Chol Hamoed* of *Pesach* or *Succos*
     * @see .isYomTov
     * @see .CHOL_HAMOED_PESACH
     *
     * @see .CHOL_HAMOED_SUCCOS
     */
    val isCholHamoed: Boolean
        get() {
            return isCholHamoedPesach || isCholHamoedSuccos
        }

    /**
     * Returns true if the current day is *Erev Yom Tov*. The method returns true for *Erev* - *Pesach*
     * (first and last days), *Shavuos*, *Rosh Hashana*, *Yom Kippur*, *Succos* and *Hoshana
     * Rabba*.
     *
     * @return true if the current day is *Erev* - *Pesach*, *Shavuos*, *Rosh Hashana*, *Yom
     * Kippur*, *Succos* and *Hoshana Rabba*.
     * @see .isYomTov
     * @see .isErevYomTovSheni
     */
    val isErevYomTov: Boolean
        get() {
            val holidayIndex: Int = yomTovIndex
            return (holidayIndex == EREV_PESACH) || (holidayIndex == EREV_SHAVUOS) || (holidayIndex == EREV_ROSH_HASHANA
                    ) || (holidayIndex == EREV_YOM_KIPPUR) || (holidayIndex == EREV_SUCCOS) || (holidayIndex == HOSHANA_RABBA
                    ) || (holidayIndex == CHOL_HAMOED_PESACH && jewishDayOfMonth == 20)
        }// Erev Rosh Hashana is not Erev Rosh Chodesh.

    /**
     * Returns true if the current day is *Erev Rosh Chodesh*. Returns false for *Erev Rosh Hashana*.
     *
     * @return true if the current day is *Erev Rosh Chodesh*. Returns false for *Erev Rosh Hashana*.
     * @see .isRoshChodesh
     */
    val isErevRoshChodesh: Boolean
        get() {
            // Erev Rosh Hashana is not Erev Rosh Chodesh.
            return (jewishDayOfMonth == 29 && getJewishMonth() != ELUL)
        }

    /**
     * Returns true if the current day is *Yom Kippur Katan*. Returns false for *Erev Rosh Hashana*,
     * *Erev Rosh Chodesh Cheshvan*, *Teves* and *Iyyar*. If *Erev Rosh Chodesh* occurs
     * on a Friday or *Shabbos*, *Yom Kippur Katan* is moved back to Thursday.
     *
     * @return true if the current day is *Erev Rosh Chodesh*. Returns false for *Erev Rosh Hashana*.
     * @see .isRoshChodesh
     */
    val isYomKippurKatan: Boolean
        get() {
            val dayOfWeek: Int = dayOfWeek
            val month: Int = getJewishMonth()
            val day: Int = jewishDayOfMonth
            if ((month == ELUL) || (month == TISHREI) || (month == KISLEV) || (month == NISSAN)) {
                return false
            }
            if ((day == 29) && (dayOfWeek != Calendar.FRIDAY) && (dayOfWeek != Calendar.SATURDAY)) {
                return true
            }
            if ((day == 27 || day == 28) && dayOfWeek == Calendar.THURSDAY) {
                return true
            }
            return false
        }

    /**
     * The Monday, Thursday and Monday after the first *Shabbos* after [&lt;em&gt;Rosh Chodesh&lt;/em&gt;][.isRoshChodesh]
     * [&lt;em&gt;Cheshvan&lt;/em&gt;][JewishDate.CHESHVAN] and [&lt;em&gt;Iyar&lt;/em&gt;][JewishDate.IYAR] are [ *BeHaB*](https://outorah.org/p/41334/) days. If the last Monday of Iyar's BeHaB coincides with [ ][.PESACH_SHENI], the method currently considers it both *Pesach Sheni* and *BeHaB*.
     * As seen in an Ohr Sameach  article on the subject [The
 * unknown Days: BeHaB Vs. Pesach Sheini?](https://ohr.edu/this_week/insights_into_halacha/9340) there are some customs that delay the day to various points in the future.
     * @return true if the day is *BeHaB*.
     */
    val isBeHaB: Boolean
        get() {
            val dayOfWeek: Int = dayOfWeek
            val month: Int = getJewishMonth()
            val day: Int = jewishDayOfMonth
            if (month == CHESHVAN || month == IYAR) {
                if ((((dayOfWeek == Calendar.MONDAY) && (day > 4) && (day < 18))
                            || ((dayOfWeek == Calendar.THURSDAY) && (day > 7) && (day < 14)))
                ) {
                    return true
                }
            }
            return false
        }

    /**
     * Return true if the day is a Taanis (fast day). Return true for *17 of Tammuz*, *Tisha B'Av*,
     * *Yom Kippur*, *Fast of Gedalyah*, *10 of Teves* and the *Fast of Esther*.
     *
     * @return true if today is a fast day
     */
    val isTaanis: Boolean
        get() {
            val holidayIndex: Int = yomTovIndex
            return (holidayIndex == SEVENTEEN_OF_TAMMUZ) || (holidayIndex == TISHA_BEAV) || (holidayIndex == YOM_KIPPUR
                    ) || (holidayIndex == FAST_OF_GEDALYAH) || (holidayIndex == TENTH_OF_TEVES) || (holidayIndex == FAST_OF_ESTHER)
        }// on 14 Nissan unless that is Shabbos where the fast is moved back to Thursday

    /**
     * Return true if the day is *Taanis Bechoros* (on *Erev Pesach*). It will return true for the 14th
     * of *Nissan* if it is not on *Shabbos*, or if the 12th of *Nissan* occurs on a Thursday.
     *
     * @return true if today is *Taanis Bechoros*.
     */
    val isTaanisBechoros: Boolean
        get() {
            val day: Int = jewishDayOfMonth
            val dayOfWeek: Int = dayOfWeek
            // on 14 Nissan unless that is Shabbos where the fast is moved back to Thursday
            return getJewishMonth() == NISSAN && ((day == 14 && dayOfWeek != Calendar.SATURDAY) ||
                    (day == 12 && dayOfWeek == Calendar.THURSDAY))
        }// teves

    /**
     * Returns the day of *Chanukah* or -1 if it is not *Chanukah*.
     *
     * @return the day of *Chanukah* or -1 if it is not *Chanukah*.
     * @see .isChanukah
     */
    val dayOfChanukah: Int
        get() {
            val day: Int = jewishDayOfMonth
            return if (isChanukah)
                if (getJewishMonth() == KISLEV) day - 24
                else /*teves*/ if (isKislevShort) day + 5 else day + 6
            else -1
        }

    /**
     * Returns true if the current day is one of the 8 days of *Chanukah*.
     *
     * @return if the current day is one of the 8 days of *Chanukah*.
     *
     * @see .getDayOfChanukah
     */
    val isChanukah: Boolean
        get() {
            return yomTovIndex == CHANUKAH
        }

    /**
     * Returns if the day is Purim ([Shushan Purim](https://en.wikipedia.org/wiki/Purim#Shushan_Purim)
     * in a mukaf choma and regular Purim in a non-mukaf choma).
     * @return if the day is Purim (Shushan Purim in a mukaf choma and regular Purin in a non-mukaf choma)
     *
     * @see .getIsMukafChoma
     * @see .setIsMukafChoma
     */
    val isPurim: Boolean
        get() =
            if (isMukafChoma) yomTovIndex == SHUSHAN_PURIM
            else yomTovIndex == PURIM// Rosh Hashana is not rosh chodesh. Elul never has 30 days

    /**
     * Returns if the day is Rosh Chodesh. Rosh Hashana will return false
     *
     * @return true if it is Rosh Chodesh. Rosh Hashana will return false
     */
    val isRoshChodesh: Boolean
        get() = // Rosh Hashana is not rosh chodesh. Elul never has 30 days
            (jewishDayOfMonth == 1 && getJewishMonth() != TISHREI) || jewishDayOfMonth == 30

    /**
     * Returns if the day is *Shabbos* and Sunday is *Rosh Chodesh* and the *haftorah* of Machar Chodesh is read.
     *
     * @return true if it is *Shabbos* and Sunday is *Rosh Chodesh* and the *haftorah* of Machar Chodesh is read.
     * @todo There is more to tweak in this method (it does not cover all cases and opinions), and it may be removed.
     */
    val isMacharChodesh: Boolean
        get() = (dayOfWeek == Calendar.SATURDAY && (jewishDayOfMonth == 30 || jewishDayOfMonth == 29))

    /**
     * Returns if the day is *Shabbos Mevorchim*.
     *
     * @return true if it is *Shabbos Mevorchim*.
     */
    val isShabbosMevorchim: Boolean
        get() =
            ((dayOfWeek == Calendar.SATURDAY) && (jewishDayOfMonth >= 23) && (jewishDayOfMonth <= 29) && (getJewishMonth() != ELUL))// if Sivan and before Shavuos// if Iyar// not a day of the Omer

    // if Nissan and second day of Pesach and on
    /**
     * Returns the int value of the *Omer* day or -1 if the day is not in the *Omer*.
     *
     * @return The *Omer* count as an int or -1 if it is not a day of the *Omer*.
     */
    val dayOfOmer: Int
        get() {
            var omer: Int = -1 // not a day of the Omer
            val month: Int = getJewishMonth()
            val day: Int = jewishDayOfMonth

            // if Nissan and second day of Pesach and on
            if (month == NISSAN && day >= 16) omer = day - 15
            else if (month == IYAR) omer = day + 15             // if Iyar
            else if (month == SIVAN && day < 6) omer = day + 44 // if Sivan and before Shavuos
            return omer
        }

    /**
     * Returns if the day is Tisha Be'Av (the 9th of Av).
     * @return if the day is Tisha Be'Av (the 9th of Av).
     */
    val isTishaBav: Boolean
        get() {
            val holidayIndex: Int = yomTovIndex
            return holidayIndex == TISHA_BEAV
        }// Har Habayis longitude

    // The raw molad Date (point in time) must be generated using standard time. Using "Asia/Jerusalem" timezone will result in the time
    // being incorrectly off by an hour in the summer due to DST. Proper adjustment for the actual time in DST will be done by the date
    // formatter class used to display the Date.
    // subtract local time difference of 20.94 minutes (20 minutes and 56.496 seconds) to get to Standard time
// Har Habayis latitude
    /**
     * Returns the *molad* in Standard Time in Yerushalayim as a Date. The traditional calculation uses local time.
     * This method subtracts 20.94 minutes (20 minutes and 56.496 seconds) from the local time (of *Har Habayis*
     * with a longitude of 35.2354 is 5.2354 away from the %15 timezone longitude) to get to standard time. This
     * method intentionally uses standard time and not daylight savings time. Java (TODO correct for Kotlin) will implicitly format the time to the
     * default (or set) Timezone.
     *
     * @return the Date representing the moment of the *molad* in Yerushalayim standard time (GMT + 2)
     */
    val moladAsDate: Date
        get() {
            val molad = super.molad
            val locationName = "Jerusalem, Israel"
            val latitude = 31.778 // Har Habayis latitude
            val longitude = 35.2354 // Har Habayis longitude

            // The raw molad Date (point in time) must be generated using standard time. Using "Asia/Jerusalem" timezone will result in the time
            // being incorrectly off by an hour in the summer due to DST. Proper adjustment for the actual time in DST will be done by the date
            // formatter class used to display the Date.
            val yerushalayimStandardTZ: TimeZone = TimeZone.getTimeZone("GMT+2")
            val geo = GeoLocation(locationName, latitude, longitude, yerushalayimStandardTZ)
            val cal: Calendar = Calendar.getInstance(geo.timeZone)
            cal.clear()
            val moladSeconds: Double = molad.moladChalakim * 10 / 3.0
            cal.set(
                molad.getGregorianYear(), molad.getGregorianMonth(), molad.getGregorianDayOfMonth(),
                molad.moladHours, molad.moladMinutes, moladSeconds.toInt()
            )
            cal.set(Calendar.MILLISECOND, (1000 * (moladSeconds - moladSeconds.toInt())).toInt())
            // subtract local time difference of 20.94 minutes (20 minutes and 56.496 seconds) to get to Standard time
            cal.add(Calendar.MILLISECOND, -1 * geo.localMeanTimeOffset.toInt())
            return cal.time
        }// 3 days after the molad

    /**
     * Returns the earliest time of *Kiddush Levana* calculated as 3 days after the molad. This method returns the time
     * even if it is during the day when *Kiddush Levana* can't be said. Callers of this method should consider
     * displaying the next *tzais* if the *zman* is between *alos* and *tzais*.
     *
     * @return the Date representing the moment 3 days after the molad.
     *
     * @see com.kosherjava.zmanim.ComplexZmanimCalendar.getTchilasZmanKidushLevana3Days
     * @see com.kosherjava.zmanim.ComplexZmanimCalendar.getTchilasZmanKidushLevana3Days
     */
    val tchilasZmanKidushLevana3Days: Date
        get() {
            val molad: Date = moladAsDate
            val cal: Calendar = Calendar.getInstance()
            cal.time = molad
            cal.add(Calendar.HOUR, 72) // 3 days after the molad
            return cal.time
        }// 7 days after the molad

    /**
     * Returns the earliest time of *Kiddush Levana* calculated as 7 days after the *molad* as mentioned
     * by the [Mechaber](http://en.wikipedia.org/wiki/Yosef_Karo). See the [Bach's](http://en.wikipedia.org/wiki/Yoel_Sirkis) opinion on this time. This method returns the time
     * even if it is during the day when *Kiddush Levana* can't be said. Callers of this method should consider
     * displaying the next *tzais* if the *zman* is between *alos* and *tzais*.
     *
     * @return the Date representing the moment 7 days after the molad.
     *
     * @see com.kosherjava.zmanim.ComplexZmanimCalendar.getTchilasZmanKidushLevana7Days
     * @see com.kosherjava.zmanim.ComplexZmanimCalendar.getTchilasZmanKidushLevana7Days
     */
    val tchilasZmanKidushLevana7Days: Date
        get() {
            val molad: Date = moladAsDate
            val cal: Calendar = Calendar.getInstance()
            cal.setTime(molad)
            cal.add(Calendar.HOUR, 168) // 7 days after the molad
            return cal.getTime()
        }// add half the time between molad and molad (half of 29 days, 12 hours and 793 chalakim (44 minutes, 3.3
    // seconds), or 14 days, 18 hours, 22 minutes and 666 milliseconds). Add it as hours, not days, to avoid
    // DST/ST crossover issues.
    /**
     * Returns the latest time of Kiddush Levana according to the [Maharil's](http://en.wikipedia.org/wiki/Yaakov_ben_Moshe_Levi_Moelin) opinion that it is calculated as
     * halfway between *molad* and *molad*. This adds half the 29 days, 12 hours and 793 *chalakim*
     * time between *molad* and *molad* (14 days, 18 hours, 22 minutes and 666 milliseconds) to the month's
     * *molad*. This method returns the time even if it is during the day when *Kiddush Levana* can't be
     * recited. Callers of this method should consider displaying *alos* before this time if the *zman* is
     * between *alos* and *tzais*.
     *
     * @return the Date representing the moment halfway between *molad* and *molad*.
     *
     * @see .getSofZmanKidushLevana15Days
     * @see com.kosherjava.zmanim.ComplexZmanimCalendar.getSofZmanKidushLevanaBetweenMoldos
     * @see com.kosherjava.zmanim.ComplexZmanimCalendar.getSofZmanKidushLevanaBetweenMoldos
     */
    val sofZmanKidushLevanaBetweenMoldos: Date
        get() {
            val molad: Date = moladAsDate
            val cal: Calendar = Calendar.getInstance()
            cal.setTime(molad)
            // add half the time between molad and molad (half of 29 days, 12 hours and 793 chalakim (44 minutes, 3.3
            // seconds), or 14 days, 18 hours, 22 minutes and 666 milliseconds). Add it as hours, not days, to avoid
            // DST/ST crossover issues.
            cal.add(Calendar.HOUR, (24 * 14) + 18)
            cal.add(Calendar.MINUTE, 22)
            cal.add(Calendar.SECOND, 1)
            cal.add(Calendar.MILLISECOND, 666)
            return cal.getTime()
        }//15 days after the molad. Add it as hours, not days, to avoid DST/ST crossover issues.

    /**
     * Returns the latest time of *Kiddush Levana* calculated as 15 days after the *molad.* This is the
     * opinion brought down in the Shulchan Aruch (Orach Chaim 426). It should be noted that some opinions hold that
     * the [Rema](http://en.wikipedia.org/wiki/Moses_Isserles) who brings down the the [Maharil's](http://en.wikipedia.org/wiki/Yaakov_ben_Moshe_Levi_Moelin) opinion of calculating it as
     * [half way between &lt;em&gt;molad&lt;/em&gt; and &lt;em&gt;molad&lt;/em&gt;][.getSofZmanKidushLevanaBetweenMoldos] is of the
     * opinion of the Mechaber as well. Also see the Aruch Hashulchan. For additional details on the subject, See Rabbi
     * Dovid Heber's very detailed writeup in Siman Daled (chapter 4) of [Shaarei Zmanim](http://www.worldcat.org/oclc/461326125). This method returns the time even if it is during
     * the day when *Kiddush Levana* can't be said. Callers of this method should consider displaying *alos*
     * before this time if the *zman* is between *alos* and *tzais*.
     *
     * @return the Date representing the moment 15 days after the *molad*.
     * @see .getSofZmanKidushLevanaBetweenMoldos
     * @see com.kosherjava.zmanim.ComplexZmanimCalendar.getSofZmanKidushLevana15Days
     * @see com.kosherjava.zmanim.ComplexZmanimCalendar.getSofZmanKidushLevana15Days
     */
    val sofZmanKidushLevana15Days: Date
        get() {
            val molad: Date = moladAsDate
            val cal: Calendar = Calendar.getInstance()
            cal.setTime(molad)
            cal.add(
                Calendar.HOUR,
                24 * 15
            ) //15 days after the molad. Add it as hours, not days, to avoid DST/ST crossover issues.
            return cal.getTime()
        }

    /**
     * Returns the *Daf Yomi (Bavli)* for the date that the calendar is set to. See the
     * [HebrewDateFormatter.formatDafYomiBavli] for the ability to format the *daf* in
     * Hebrew or transliterated *masechta* names.
     *
     * @return the daf as a [Daf]
     */
    val dafYomiBavli: Daf?
        get() {
            return YomiCalculator.getDafYomiBavli(this)
        }

    /**
     * Returns the *Daf Yomi (Yerushalmi)* for the date that the calendar is set to. See the
     * [HebrewDateFormatter.formatDafYomiYerushalmi] for the ability to format the *daf*
     * in Hebrew or transliterated *masechta* names.
     *
     * @return the daf as a [Daf]
     */
    val dafYomiYerushalmi: Daf?
        get() {
            return YerushalmiYomiCalculator.getDafYomiYerushalmi(this)
        }// Days since Rosh Hashana year 1. Add 1/2 day as the first tekufas tishrei was 9 hours into the day. This allows all
    // 4 years of the secular leap year cycle to share 47 days. Truncate 47D and 9H to 47D for simplicity.
    // days of completed solar years
    /**
     * Returns the elapsed days since *Tekufas Tishrei*. This uses *Tekufas Shmuel* (identical to the [Julian Year](https://en.wikipedia.org/wiki/Julian_year_(astronomy)) with a solar year length of 365.25 days).
     * The notation used below is D = days, H = hours and C = chalakim. *[Molad](https://en.wikipedia.org/wiki/Molad) BaHaRad* was 2D,5H,204C or 5H,204C from the start of *Rosh Hashana* year 1. For *molad
     * Nissan* add 177D, 4H and 438C (6 * 29D, 12H and 793C), or 177D,9H,642C after *Rosh Hashana* year 1.
     * *Tekufas Nissan* was 7D, 9H and 642C before *molad Nissan* according to the Rambam, or 170D, 0H and
     * 0C after *Rosh Hashana* year 1. *Tekufas Tishrei* was 182D and 3H (365.25 / 2) before *tekufas
     * Nissan*, or 12D and 15H before *Rosh Hashana* of year 1. Outside of Israel we start reciting *Tal
     * Umatar* in *Birkas Hashanim* from 60 days after *tekufas Tishrei*. The 60 days include the day of
     * the *tekufah* and the day we start reciting *Tal Umatar*. 60 days from the tekufah == 47D and 9H
     * from *Rosh Hashana* year 1.
     *
     * @return the number of elapsed days since *tekufas Tishrei*.
     *
     * @see .isVeseinTalUmatarStartDate
     * @see .isVeseinTalUmatarStartingTonight
     * @see .isVeseinTalUmatarRecited
     */
    val tekufasTishreiElapsedDays: Int
        get() {
            // Days since Rosh Hashana year 1. Add 1/2 day as the first tekufas tishrei was 9 hours into the day. This allows all
            // 4 years of the secular leap year cycle to share 47 days. Truncate 47D and 9H to 47D for simplicity.
            val days: Double = getJewishCalendarElapsedDays(getJewishYear()) + (daysSinceStartOfJewishYear - 1) + 0.5
            // days of completed solar years
            val solar: Double = (getJewishYear() - 1) * 365.25
            return Math.floor(days - solar).toInt()
        }// When starting on Sunday, it can be the start date or delayed from Shabbos
    // keep the compiler happy
//Not recited on Friday night// The 7th Cheshvan can't occur on Shabbos, so always return true for 7 Cheshvan
    /**
     * Returns if it is the Jewish day (starting the evening before) to start reciting *Vesein Tal Umatar
     * Livracha* (*Sheailas Geshamim*). In Israel this is the 7th day of *Marcheshvan*. Outside
     * Israel recitation starts on the evening of December 4th (or 5th if it is the year before a civil leap year)
     * in the 21st century and shifts a day forward every century not evenly divisible by 400. This method will
     * return true if *vesein tal umatar* on the current Jewish date that starts on the previous night, so
     * Dec 5/6 will be returned by this method in the 21st century. *vesein tal umatar* is not recited on
     * *Shabbos* and the start date will be delayed a day when the start day is on a *Shabbos* (this
     * can only occur out of Israel).
     *
     * @return true if it is the first Jewish day (starting the prior evening of reciting *Vesein Tal Umatar
     * Livracha* (*Sheailas Geshamim*)).
     *
     * @see .isVeseinTalUmatarStartingTonight
     * @see .isVeseinTalUmatarRecited
     */  // (forRemoval=true) // add back once Java 9 is the minimum supported version
    @get:Deprecated("Use {@link TefilaRules#isVeseinTalUmatarStartDate(JewishCalendar)} instead. This method will be\n" + "	          removed in the v3.0 release.\n" + "	  \n" + "	  ")
    val isVeseinTalUmatarStartDate: Boolean
        get() {
            if (inIsrael) {
                // The 7th Cheshvan can't occur on Shabbos, so always return true for 7 Cheshvan
                if (getJewishMonth() == CHESHVAN && jewishDayOfMonth == 7) {
                    return true
                }
            } else {
                return when (dayOfWeek) {
                    Calendar.SATURDAY -> false //Not recited on Friday night
                    Calendar.SUNDAY -> tekufasTishreiElapsedDays == 48 || tekufasTishreiElapsedDays == 47 // When starting on Sunday, it can be the start date or delayed from Shabbos
                    else -> tekufasTishreiElapsedDays == 47
                }
            }
            return false // keep the compiler happy
        }// When starting on motzai Shabbos, it can be the start date or delayed from Friday night//Not recited on Friday night// The 7th Cheshvan can't occur on Shabbos, so always return true for 6 Cheshvan

    /**
     * Returns true if tonight is the first night to start reciting *Vesein Tal Umatar Livracha* (
     * *Sheailas Geshamim*). In Israel this is the 7th day of *Marcheshvan* (so the 6th will return
     * true). Outside Israel recitation starts on the evening of December 4th (or 5th if it is the year before a
     * civil leap year) in the 21st century and shifts a day forward every century not evenly divisible by 400.
     * *Vesein tal umatar* is not recited on *Shabbos* and the start date will be delayed a day when
     * the start day is on a *Shabbos* (this can only occur out of Israel).
     *
     * @return true if it is the first Jewish day (starting the prior evening of reciting *Vesein Tal Umatar
     * Livracha* (*Sheailas Geshamim*)).
     *
     * @see .isVeseinTalUmatarStartDate
     * @see .isVeseinTalUmatarRecited
     */  // (forRemoval=true) // add back once Java 9 is the minimum supported version
    @get:Deprecated("Use {@link TefilaRules#isVeseinTalUmatarStartingTonight(JewishCalendar)} instead. This method\n" + "	          will be removed in the v3.0 release.\n" + "	  \n" + "	  ")
    val isVeseinTalUmatarStartingTonight: Boolean
        get() {
            if (inIsrael) {
                // The 7th Cheshvan can't occur on Shabbos, so always return true for 6 Cheshvan
                if (getJewishMonth() == CHESHVAN && jewishDayOfMonth == 6) {
                    return true
                }
            } else {
                return when (dayOfWeek) {
                    Calendar.FRIDAY -> false //Not recited on Friday night
                    Calendar.SATURDAY -> tekufasTishreiElapsedDays == 47 || tekufasTishreiElapsedDays == 46 // When starting on motzai Shabbos, it can be the start date or delayed from Friday night
                    else -> tekufasTishreiElapsedDays == 46
                }
            }
            return false
        }

    /**
     * Returns if *Vesein Tal Umatar Livracha* (*Sheailas Geshamim*) is recited. This will return
     * true for the entire season, even on *Shabbos* when it is not recited.
     *
     * @return true if *Vesein Tal Umatar Livracha* (*Sheailas Geshamim*) is recited.
     *
     * @see .isVeseinTalUmatarStartDate
     * @see .isVeseinTalUmatarStartingTonight
     */  // (forRemoval=true) // add back once Java 9 is the minimum supported version
    @get:Deprecated("Use {@link TefilaRules#isVeseinTalUmatarRecited(JewishCalendar)} instead. This method will\n" + "	          be removed in the v3.0 release.\n" + "	  \n" + "	  ")
    val isVeseinTalUmatarRecited: Boolean
        get() {
            if (getJewishMonth() == NISSAN && jewishDayOfMonth < 15) {
                return true
            }
            if (getJewishMonth() < CHESHVAN) {
                return false
            }
            if (inIsrael) {
                return getJewishMonth() != CHESHVAN || jewishDayOfMonth >= 7
            } else {
                return tekufasTishreiElapsedDays >= 47
            }
        }

    /**
     * Returns if *Vesein Beracha* is recited. It is recited from 15 *Nissan* to the point that [ ][.isVeseinTalUmatarRecited].
     *
     * @return true if *Vesein Beracha* is recited.
     *
     * @see .isVeseinTalUmatarRecited
     */  // (forRemoval=true) // add back once Java 9 is the minimum supported version
    @get:Deprecated("Use {@link TefilaRules#isVeseinBerachaRecited(JewishCalendar)} instead. This method will be\n" + "	          removed in the v3.0 release.\n" + "	  \n" + "	  ")
    val isVeseinBerachaRecited: Boolean
        get() {
            return !isVeseinTalUmatarRecited
        }

    /**
     * Returns if the date is the start date for reciting *Mashiv Haruach Umorid Hageshem*. The date is 22 *Tishrei*.
     *
     * @return true if the date is the start date for reciting *Mashiv Haruach Umorid Hageshem*.
     *
     * @see .isMashivHaruachEndDate
     * @see .isMashivHaruachRecited
     */  // (forRemoval=true) // add back once Java 9 is the minimum supported version
    @get:Deprecated("Use {@link TefilaRules#isMashivHaruachStartDate(JewishCalendar)} instead. This method will be\n" + "	          removed in the v3.0 release.\n" + "	  \n" + "	  ")
    val isMashivHaruachStartDate: Boolean
        get() {
            return getJewishMonth() == TISHREI && jewishDayOfMonth == 22
        }

    /**
     * Returns if the date is the end date for reciting *Mashiv Haruach Umorid Hageshem*. The date is 15 *Nissan*.
     *
     * @return true if the date is the end date for reciting *Mashiv Haruach Umorid Hageshem*.
     *
     * @see .isMashivHaruachStartDate
     * @see .isMashivHaruachRecited
     */  // (forRemoval=true) // add back once Java 9 is the minimum supported version
    @get:Deprecated("Use {@link TefilaRules#isMashivHaruachEndDate(JewishCalendar)} instead. This method will be\n" + "	          removed in the v3.0 release.\n" + "	  \n" + "	  ")
    val isMashivHaruachEndDate: Boolean
        get() {
            return getJewishMonth() == NISSAN && jewishDayOfMonth == 15
        }

    /**
     * Returns if *Mashiv Haruach Umorid Hageshem* is recited. This period starts on 22 *Tishrei* and ends
     * on the 15th day of *Nissan*.
     *
     * @return true if *Mashiv Haruach Umorid Hageshem* is recited.
     *
     * @see .isMashivHaruachStartDate
     * @see .isMashivHaruachEndDate
     */  // (forRemoval=true) // add back once Java 9 is the minimum supported version
    @get:Deprecated("Use {@link TefilaRules#isMashivHaruachRecited(JewishCalendar)} instead. This method will be\n" + "	          removed in the v3.0 release.\n" + "	  \n" + "	  ")
    val isMashivHaruachRecited: Boolean
        get() {
            val startDate: JewishDate = JewishDate(getJewishYear(), TISHREI, 22)
            val endDate: JewishDate = JewishDate(getJewishYear(), NISSAN, 15)
            return compareTo(startDate) > 0 && compareTo(endDate) < 0
        }

    /**
     * Returns if *Morid Hatal* (or the lack of reciting *Mashiv Haruach* following *nussach Ashkenaz*) is recited.
     * This period starts on 22 *Tishrei* and ends on the 15th day of
     * *Nissan*.
     *
     * @return true if *Morid Hatal* (or the lack of reciting *Mashiv Haruach* following *nussach Ashkenaz*) is recited.
     */  // (forRemoval=true) // add back once Java 9 is the minimum supported version
    @get:Deprecated("Use {@link TefilaRules#isMoridHatalRecited(JewishCalendar)} instead. This method will be\n" + "	          removed in the v3.0 release.\n" + "	  \n" + "	  ")
    val isMoridHatalRecited: Boolean
        get() {
            return !isMashivHaruachRecited || isMashivHaruachStartDate || isMashivHaruachEndDate
        }

    /**
     * Returns true if the current day is *Isru Chag*. The method returns true for the day following *Pesach*
     * *Shavuos* and *Succos*. It utilizes {@see #inIsrael} to return the proper date.
     *
     * @return true if the current day is *Isru Chag*. The method returns true for the day following *Pesach*
     * *Shavuos* and *Succos*. It utilizes {@see #inIsrael} to return the proper date.
     */
    val isIsruChag: Boolean
        get() {
            val holidayIndex: Int = yomTovIndex
            return holidayIndex == ISRU_CHAG
        }

    /**
     * Indicates whether some other object is "equal to" this one.
     * @see Object.equals
     */
    override fun equals(`object`: Any?): Boolean {
        if (this === `object`) {
            return true
        }
        if (`object` !is JewishCalendar) {
            return false
        }
        return absDate == `object`.absDate && inIsrael == `object`.inIsrael
    }

    /**
     * Overrides [Object.hashCode].
     * @see Object.hashCode
     */
    override fun hashCode(): Int {
        var result = 17
        result = 37 * result + javaClass.hashCode() // needed or this and subclasses will return identical hash
        result += (37 * result) + absDate + (if (inIsrael) 1 else 3)
        return result
    }

    companion object {
        /** The 14th day of Nissan, the day before of Pesach (Passover). */
        val EREV_PESACH: Int = 0

        /** The holiday of Pesach (Passover) on the 15th (and 16th out of Israel) day of Nissan. */
        val PESACH: Int = 1

        /** Chol Hamoed (interim days) of Pesach (Passover) */
        val CHOL_HAMOED_PESACH: Int = 2

        /**Pesach Sheni, the 14th day of Iyar, a minor holiday. */
        val PESACH_SHENI: Int = 3

        /**Erev Shavuos (the day before Shavuos), the 5th of Sivan */
        val EREV_SHAVUOS: Int = 4

        /**Shavuos (Pentecost), the 6th of Sivan */
        val SHAVUOS: Int = 5

        /** The fast of the 17th day of Tamuz */
        val SEVENTEEN_OF_TAMMUZ: Int = 6

        /** The fast of the 9th of Av */
        val TISHA_BEAV: Int = 7

        /** The 15th day of Av, a minor holiday */
        val TU_BEAV: Int = 8

        /**Erev Rosh Hashana (the day before Rosh Hashana), the 29th of Elul */
        val EREV_ROSH_HASHANA: Int = 9

        /** Rosh Hashana, the first of Tishrei. */
        val ROSH_HASHANA: Int = 10

        /** The fast of Gedalyah, the 3rd of Tishrei. */
        val FAST_OF_GEDALYAH: Int = 11

        /** The 9th day of Tishrei, the day before of Yom Kippur. */
        val EREV_YOM_KIPPUR: Int = 12

        /** The holiday of Yom Kippur, the 10th day of Tishrei */
        val YOM_KIPPUR: Int = 13

        /** The 14th day of Tishrei, the day before of Succos/Sukkos (Tabernacles). */
        val EREV_SUCCOS: Int = 14

        /** The holiday of Succos/Sukkos (Tabernacles), the 15th (and 16th out of Israel) day of Tishrei  */
        val SUCCOS: Int = 15

        /** Chol Hamoed (interim days) of Succos/Sukkos (Tabernacles) */
        val CHOL_HAMOED_SUCCOS: Int = 16

        /** Hoshana Rabba, the 7th day of Succos/Sukkos that occurs on the 21st of Tishrei.  */
        val HOSHANA_RABBA: Int = 17

        /** Shmini Atzeres, the 8th day of Succos/Sukkos is an independent holiday that occurs on the 22nd of Tishrei.  */
        val SHEMINI_ATZERES: Int = 18

        /** Simchas Torah, the 9th day of Succos/Sukkos, or the second day of Shmini Atzeres that is celebrated
         * [out of Israel][.getInIsrael] on the 23rd of Tishrei.
         */
        val SIMCHAS_TORAH: Int = 19
        // public static final int EREV_CHANUKAH = 20;// probably remove this
        /** The holiday of Chanukah. 8 days starting on the 25th day Kislev. */
        val CHANUKAH: Int = 21

        /** The fast of the 10th day of Teves. */
        val TENTH_OF_TEVES: Int = 22

        /** Tu Bishvat on the 15th day of Shevat, a minor holiday. */
        val TU_BESHVAT: Int = 23

        /** The fast of Esther, usually on the 13th day of Adar (or Adar II on leap years). It is earlier on some years. */
        val FAST_OF_ESTHER: Int = 24

        /** The holiday of Purim on the 14th day of Adar (or Adar II on leap years). */
        val PURIM: Int = 25

        /** The holiday of Shushan Purim on the 15th day of Adar (or Adar II on leap years). */
        val SHUSHAN_PURIM: Int = 26

        /** The holiday of Purim Katan on the 14th day of Adar I on a leap year when Purim is on Adar II, a minor holiday. */
        val PURIM_KATAN: Int = 27

        /**
         * Rosh Chodesh, the new moon on the first day of the Jewish month, and the 30th day of the previous month in the
         * case of a month with 30 days.
         */
        val ROSH_CHODESH: Int = 28

        /** Yom HaShoah, Holocaust Remembrance Day, usually held on the 27th of Nissan. If it falls on a Friday, it is moved
         * to the 26th, and if it falls on a Sunday it is moved to the 28th. A [modern holiday][.isUseModernHolidays].
         */
        val YOM_HASHOAH: Int = 29

        /**
         * Yom HaZikaron, Israeli Memorial Day, held a day before Yom Ha'atzmaut.  A [modern holiday][.isUseModernHolidays].
         */
        val YOM_HAZIKARON: Int = 30

        /** Yom Ha'atzmaut, Israel Independence Day, the 5th of Iyar, but if it occurs on a Friday or Saturday, the holiday is
         * moved back to Thursday, the 3rd of 4th of Iyar, and if it falls on a Monday, it is moved forward to Tuesday the
         * 6th of Iyar.  A [modern holiday][.isUseModernHolidays]. */
        val YOM_HAATZMAUT: Int = 31

        /**
         * Yom Yerushalayim or Jerusalem Day, on 28 Iyar. A [modern holiday][.isUseModernHolidays].
         */
        val YOM_YERUSHALAYIM: Int = 32

        /** The 33rd day of the Omer, the 18th of Iyar, a minor holiday. */
        val LAG_BAOMER: Int = 33

        /** The holiday of Purim Katan on the 15th day of Adar I on a leap year when Purim is on Adar II, a minor holiday. */
        val SHUSHAN_PURIM_KATAN: Int = 34

        /** The day following the last day of Pesach, Shavuos and Sukkos. */
        val ISRU_CHAG: Int = 35

        /**
         * The day before *Rosh Chodesh* (moved to Thursday if *Rosh Chodesh* is on a Friday or *Shabbos*) in most months.
         * This constant is not actively in use.
         * @see .isYomKippurKatan
         */
        val YOM_KIPPUR_KATAN: Int = 36

        /**
         * The Monday, Thursday and Monday after the first *Shabbos* after *Rosh Chodesh Cheshvan* and *Iyar*) are BeHab
         * days. This constant is not actively in use.
         * @see .isBeHaB
         */
        val BEHAB: Int = 37

        /**
         * An array of *parshiyos* in the 17 possible combinations.
         */
        val parshalist: Array<Array<Parsha>> = arrayOf(
            arrayOf(
                Parsha.NONE,
                Parsha.VAYEILECH,
                Parsha.HAAZINU,
                Parsha.NONE,
                Parsha.BERESHIS,
                Parsha.NOACH,
                Parsha.LECH_LECHA,
                Parsha.VAYERA,
                Parsha.CHAYEI_SARA,
                Parsha.TOLDOS,
                Parsha.VAYETZEI,
                Parsha.VAYISHLACH,
                Parsha.VAYESHEV,
                Parsha.MIKETZ,
                Parsha.VAYIGASH,
                Parsha.VAYECHI,
                Parsha.SHEMOS,
                Parsha.VAERA,
                Parsha.BO,
                Parsha.BESHALACH,
                Parsha.YISRO,
                Parsha.MISHPATIM,
                Parsha.TERUMAH,
                Parsha.TETZAVEH,
                Parsha.KI_SISA,
                Parsha.VAYAKHEL_PEKUDEI,
                Parsha.VAYIKRA,
                Parsha.TZAV,
                Parsha.NONE,
                Parsha.SHMINI,
                Parsha.TAZRIA_METZORA,
                Parsha.ACHREI_MOS_KEDOSHIM,
                Parsha.EMOR,
                Parsha.BEHAR_BECHUKOSAI,
                Parsha.BAMIDBAR,
                Parsha.NASSO,
                Parsha.BEHAALOSCHA,
                Parsha.SHLACH,
                Parsha.KORACH,
                Parsha.CHUKAS,
                Parsha.BALAK,
                Parsha.PINCHAS,
                Parsha.MATOS_MASEI,
                Parsha.DEVARIM,
                Parsha.VAESCHANAN,
                Parsha.EIKEV,
                Parsha.REEH,
                Parsha.SHOFTIM,
                Parsha.KI_SEITZEI,
                Parsha.KI_SAVO,
                Parsha.NITZAVIM_VAYEILECH
            ),
            arrayOf(
                Parsha.NONE,
                Parsha.VAYEILECH,
                Parsha.HAAZINU,
                Parsha.NONE,
                Parsha.BERESHIS,
                Parsha.NOACH,
                Parsha.LECH_LECHA,
                Parsha.VAYERA,
                Parsha.CHAYEI_SARA,
                Parsha.TOLDOS,
                Parsha.VAYETZEI,
                Parsha.VAYISHLACH,
                Parsha.VAYESHEV,
                Parsha.MIKETZ,
                Parsha.VAYIGASH,
                Parsha.VAYECHI,
                Parsha.SHEMOS,
                Parsha.VAERA,
                Parsha.BO,
                Parsha.BESHALACH,
                Parsha.YISRO,
                Parsha.MISHPATIM,
                Parsha.TERUMAH,
                Parsha.TETZAVEH,
                Parsha.KI_SISA,
                Parsha.VAYAKHEL_PEKUDEI,
                Parsha.VAYIKRA,
                Parsha.TZAV,
                Parsha.NONE,
                Parsha.SHMINI,
                Parsha.TAZRIA_METZORA,
                Parsha.ACHREI_MOS_KEDOSHIM,
                Parsha.EMOR,
                Parsha.BEHAR_BECHUKOSAI,
                Parsha.BAMIDBAR,
                Parsha.NONE,
                Parsha.NASSO,
                Parsha.BEHAALOSCHA,
                Parsha.SHLACH,
                Parsha.KORACH,
                Parsha.CHUKAS_BALAK,
                Parsha.PINCHAS,
                Parsha.MATOS_MASEI,
                Parsha.DEVARIM,
                Parsha.VAESCHANAN,
                Parsha.EIKEV,
                Parsha.REEH,
                Parsha.SHOFTIM,
                Parsha.KI_SEITZEI,
                Parsha.KI_SAVO,
                Parsha.NITZAVIM_VAYEILECH
            ),
            arrayOf(
                Parsha.NONE,
                Parsha.HAAZINU,
                Parsha.NONE,
                Parsha.NONE,
                Parsha.BERESHIS,
                Parsha.NOACH,
                Parsha.LECH_LECHA,
                Parsha.VAYERA,
                Parsha.CHAYEI_SARA,
                Parsha.TOLDOS,
                Parsha.VAYETZEI,
                Parsha.VAYISHLACH,
                Parsha.VAYESHEV,
                Parsha.MIKETZ,
                Parsha.VAYIGASH,
                Parsha.VAYECHI,
                Parsha.SHEMOS,
                Parsha.VAERA,
                Parsha.BO,
                Parsha.BESHALACH,
                Parsha.YISRO,
                Parsha.MISHPATIM,
                Parsha.TERUMAH,
                Parsha.TETZAVEH,
                Parsha.KI_SISA,
                Parsha.VAYAKHEL_PEKUDEI,
                Parsha.VAYIKRA,
                Parsha.TZAV,
                Parsha.NONE,
                Parsha.NONE,
                Parsha.SHMINI,
                Parsha.TAZRIA_METZORA,
                Parsha.ACHREI_MOS_KEDOSHIM,
                Parsha.EMOR,
                Parsha.BEHAR_BECHUKOSAI,
                Parsha.BAMIDBAR,
                Parsha.NASSO,
                Parsha.BEHAALOSCHA,
                Parsha.SHLACH,
                Parsha.KORACH,
                Parsha.CHUKAS,
                Parsha.BALAK,
                Parsha.PINCHAS,
                Parsha.MATOS_MASEI,
                Parsha.DEVARIM,
                Parsha.VAESCHANAN,
                Parsha.EIKEV,
                Parsha.REEH,
                Parsha.SHOFTIM,
                Parsha.KI_SEITZEI,
                Parsha.KI_SAVO,
                Parsha.NITZAVIM
            ),
            arrayOf(
                Parsha.NONE,
                Parsha.HAAZINU,
                Parsha.NONE,
                Parsha.NONE,
                Parsha.BERESHIS,
                Parsha.NOACH,
                Parsha.LECH_LECHA,
                Parsha.VAYERA,
                Parsha.CHAYEI_SARA,
                Parsha.TOLDOS,
                Parsha.VAYETZEI,
                Parsha.VAYISHLACH,
                Parsha.VAYESHEV,
                Parsha.MIKETZ,
                Parsha.VAYIGASH,
                Parsha.VAYECHI,
                Parsha.SHEMOS,
                Parsha.VAERA,
                Parsha.BO,
                Parsha.BESHALACH,
                Parsha.YISRO,
                Parsha.MISHPATIM,
                Parsha.TERUMAH,
                Parsha.TETZAVEH,
                Parsha.KI_SISA,
                Parsha.VAYAKHEL,
                Parsha.PEKUDEI,
                Parsha.VAYIKRA,
                Parsha.TZAV,
                Parsha.NONE,
                Parsha.SHMINI,
                Parsha.TAZRIA_METZORA,
                Parsha.ACHREI_MOS_KEDOSHIM,
                Parsha.EMOR,
                Parsha.BEHAR_BECHUKOSAI,
                Parsha.BAMIDBAR,
                Parsha.NASSO,
                Parsha.BEHAALOSCHA,
                Parsha.SHLACH,
                Parsha.KORACH,
                Parsha.CHUKAS,
                Parsha.BALAK,
                Parsha.PINCHAS,
                Parsha.MATOS_MASEI,
                Parsha.DEVARIM,
                Parsha.VAESCHANAN,
                Parsha.EIKEV,
                Parsha.REEH,
                Parsha.SHOFTIM,
                Parsha.KI_SEITZEI,
                Parsha.KI_SAVO,
                Parsha.NITZAVIM
            ),
            arrayOf(
                Parsha.NONE,
                Parsha.NONE,
                Parsha.HAAZINU,
                Parsha.NONE,
                Parsha.NONE,
                Parsha.BERESHIS,
                Parsha.NOACH,
                Parsha.LECH_LECHA,
                Parsha.VAYERA,
                Parsha.CHAYEI_SARA,
                Parsha.TOLDOS,
                Parsha.VAYETZEI,
                Parsha.VAYISHLACH,
                Parsha.VAYESHEV,
                Parsha.MIKETZ,
                Parsha.VAYIGASH,
                Parsha.VAYECHI,
                Parsha.SHEMOS,
                Parsha.VAERA,
                Parsha.BO,
                Parsha.BESHALACH,
                Parsha.YISRO,
                Parsha.MISHPATIM,
                Parsha.TERUMAH,
                Parsha.TETZAVEH,
                Parsha.KI_SISA,
                Parsha.VAYAKHEL_PEKUDEI,
                Parsha.VAYIKRA,
                Parsha.TZAV,
                Parsha.NONE,
                Parsha.SHMINI,
                Parsha.TAZRIA_METZORA,
                Parsha.ACHREI_MOS_KEDOSHIM,
                Parsha.EMOR,
                Parsha.BEHAR_BECHUKOSAI,
                Parsha.BAMIDBAR,
                Parsha.NASSO,
                Parsha.BEHAALOSCHA,
                Parsha.SHLACH,
                Parsha.KORACH,
                Parsha.CHUKAS,
                Parsha.BALAK,
                Parsha.PINCHAS,
                Parsha.MATOS_MASEI,
                Parsha.DEVARIM,
                Parsha.VAESCHANAN,
                Parsha.EIKEV,
                Parsha.REEH,
                Parsha.SHOFTIM,
                Parsha.KI_SEITZEI,
                Parsha.KI_SAVO,
                Parsha.NITZAVIM
            ),
            arrayOf(
                Parsha.NONE,
                Parsha.NONE,
                Parsha.HAAZINU,
                Parsha.NONE,
                Parsha.NONE,
                Parsha.BERESHIS,
                Parsha.NOACH,
                Parsha.LECH_LECHA,
                Parsha.VAYERA,
                Parsha.CHAYEI_SARA,
                Parsha.TOLDOS,
                Parsha.VAYETZEI,
                Parsha.VAYISHLACH,
                Parsha.VAYESHEV,
                Parsha.MIKETZ,
                Parsha.VAYIGASH,
                Parsha.VAYECHI,
                Parsha.SHEMOS,
                Parsha.VAERA,
                Parsha.BO,
                Parsha.BESHALACH,
                Parsha.YISRO,
                Parsha.MISHPATIM,
                Parsha.TERUMAH,
                Parsha.TETZAVEH,
                Parsha.KI_SISA,
                Parsha.VAYAKHEL_PEKUDEI,
                Parsha.VAYIKRA,
                Parsha.TZAV,
                Parsha.NONE,
                Parsha.SHMINI,
                Parsha.TAZRIA_METZORA,
                Parsha.ACHREI_MOS_KEDOSHIM,
                Parsha.EMOR,
                Parsha.BEHAR_BECHUKOSAI,
                Parsha.BAMIDBAR,
                Parsha.NASSO,
                Parsha.BEHAALOSCHA,
                Parsha.SHLACH,
                Parsha.KORACH,
                Parsha.CHUKAS,
                Parsha.BALAK,
                Parsha.PINCHAS,
                Parsha.MATOS_MASEI,
                Parsha.DEVARIM,
                Parsha.VAESCHANAN,
                Parsha.EIKEV,
                Parsha.REEH,
                Parsha.SHOFTIM,
                Parsha.KI_SEITZEI,
                Parsha.KI_SAVO,
                Parsha.NITZAVIM_VAYEILECH
            ),
            arrayOf(
                Parsha.NONE,
                Parsha.VAYEILECH,
                Parsha.HAAZINU,
                Parsha.NONE,
                Parsha.BERESHIS,
                Parsha.NOACH,
                Parsha.LECH_LECHA,
                Parsha.VAYERA,
                Parsha.CHAYEI_SARA,
                Parsha.TOLDOS,
                Parsha.VAYETZEI,
                Parsha.VAYISHLACH,
                Parsha.VAYESHEV,
                Parsha.MIKETZ,
                Parsha.VAYIGASH,
                Parsha.VAYECHI,
                Parsha.SHEMOS,
                Parsha.VAERA,
                Parsha.BO,
                Parsha.BESHALACH,
                Parsha.YISRO,
                Parsha.MISHPATIM,
                Parsha.TERUMAH,
                Parsha.TETZAVEH,
                Parsha.KI_SISA,
                Parsha.VAYAKHEL,
                Parsha.PEKUDEI,
                Parsha.VAYIKRA,
                Parsha.TZAV,
                Parsha.SHMINI,
                Parsha.TAZRIA,
                Parsha.METZORA,
                Parsha.NONE,
                Parsha.ACHREI_MOS,
                Parsha.KEDOSHIM,
                Parsha.EMOR,
                Parsha.BEHAR,
                Parsha.BECHUKOSAI,
                Parsha.BAMIDBAR,
                Parsha.NONE,
                Parsha.NASSO,
                Parsha.BEHAALOSCHA,
                Parsha.SHLACH,
                Parsha.KORACH,
                Parsha.CHUKAS_BALAK,
                Parsha.PINCHAS,
                Parsha.MATOS_MASEI,
                Parsha.DEVARIM,
                Parsha.VAESCHANAN,
                Parsha.EIKEV,
                Parsha.REEH,
                Parsha.SHOFTIM,
                Parsha.KI_SEITZEI,
                Parsha.KI_SAVO,
                Parsha.NITZAVIM_VAYEILECH
            ),
            arrayOf(
                Parsha.NONE,
                Parsha.VAYEILECH,
                Parsha.HAAZINU,
                Parsha.NONE,
                Parsha.BERESHIS,
                Parsha.NOACH,
                Parsha.LECH_LECHA,
                Parsha.VAYERA,
                Parsha.CHAYEI_SARA,
                Parsha.TOLDOS,
                Parsha.VAYETZEI,
                Parsha.VAYISHLACH,
                Parsha.VAYESHEV,
                Parsha.MIKETZ,
                Parsha.VAYIGASH,
                Parsha.VAYECHI,
                Parsha.SHEMOS,
                Parsha.VAERA,
                Parsha.BO,
                Parsha.BESHALACH,
                Parsha.YISRO,
                Parsha.MISHPATIM,
                Parsha.TERUMAH,
                Parsha.TETZAVEH,
                Parsha.KI_SISA,
                Parsha.VAYAKHEL,
                Parsha.PEKUDEI,
                Parsha.VAYIKRA,
                Parsha.TZAV,
                Parsha.SHMINI,
                Parsha.TAZRIA,
                Parsha.METZORA,
                Parsha.NONE,
                Parsha.NONE,
                Parsha.ACHREI_MOS,
                Parsha.KEDOSHIM,
                Parsha.EMOR,
                Parsha.BEHAR,
                Parsha.BECHUKOSAI,
                Parsha.BAMIDBAR,
                Parsha.NASSO,
                Parsha.BEHAALOSCHA,
                Parsha.SHLACH,
                Parsha.KORACH,
                Parsha.CHUKAS,
                Parsha.BALAK,
                Parsha.PINCHAS,
                Parsha.MATOS_MASEI,
                Parsha.DEVARIM,
                Parsha.VAESCHANAN,
                Parsha.EIKEV,
                Parsha.REEH,
                Parsha.SHOFTIM,
                Parsha.KI_SEITZEI,
                Parsha.KI_SAVO,
                Parsha.NITZAVIM
            ),
            arrayOf(
                Parsha.NONE,
                Parsha.HAAZINU,
                Parsha.NONE,
                Parsha.NONE,
                Parsha.BERESHIS,
                Parsha.NOACH,
                Parsha.LECH_LECHA,
                Parsha.VAYERA,
                Parsha.CHAYEI_SARA,
                Parsha.TOLDOS,
                Parsha.VAYETZEI,
                Parsha.VAYISHLACH,
                Parsha.VAYESHEV,
                Parsha.MIKETZ,
                Parsha.VAYIGASH,
                Parsha.VAYECHI,
                Parsha.SHEMOS,
                Parsha.VAERA,
                Parsha.BO,
                Parsha.BESHALACH,
                Parsha.YISRO,
                Parsha.MISHPATIM,
                Parsha.TERUMAH,
                Parsha.TETZAVEH,
                Parsha.KI_SISA,
                Parsha.VAYAKHEL,
                Parsha.PEKUDEI,
                Parsha.VAYIKRA,
                Parsha.TZAV,
                Parsha.SHMINI,
                Parsha.TAZRIA,
                Parsha.METZORA,
                Parsha.ACHREI_MOS,
                Parsha.NONE,
                Parsha.KEDOSHIM,
                Parsha.EMOR,
                Parsha.BEHAR,
                Parsha.BECHUKOSAI,
                Parsha.BAMIDBAR,
                Parsha.NASSO,
                Parsha.BEHAALOSCHA,
                Parsha.SHLACH,
                Parsha.KORACH,
                Parsha.CHUKAS,
                Parsha.BALAK,
                Parsha.PINCHAS,
                Parsha.MATOS,
                Parsha.MASEI,
                Parsha.DEVARIM,
                Parsha.VAESCHANAN,
                Parsha.EIKEV,
                Parsha.REEH,
                Parsha.SHOFTIM,
                Parsha.KI_SEITZEI,
                Parsha.KI_SAVO,
                Parsha.NITZAVIM
            ),
            arrayOf(
                Parsha.NONE,
                Parsha.HAAZINU,
                Parsha.NONE,
                Parsha.NONE,
                Parsha.BERESHIS,
                Parsha.NOACH,
                Parsha.LECH_LECHA,
                Parsha.VAYERA,
                Parsha.CHAYEI_SARA,
                Parsha.TOLDOS,
                Parsha.VAYETZEI,
                Parsha.VAYISHLACH,
                Parsha.VAYESHEV,
                Parsha.MIKETZ,
                Parsha.VAYIGASH,
                Parsha.VAYECHI,
                Parsha.SHEMOS,
                Parsha.VAERA,
                Parsha.BO,
                Parsha.BESHALACH,
                Parsha.YISRO,
                Parsha.MISHPATIM,
                Parsha.TERUMAH,
                Parsha.TETZAVEH,
                Parsha.KI_SISA,
                Parsha.VAYAKHEL,
                Parsha.PEKUDEI,
                Parsha.VAYIKRA,
                Parsha.TZAV,
                Parsha.SHMINI,
                Parsha.TAZRIA,
                Parsha.METZORA,
                Parsha.ACHREI_MOS,
                Parsha.NONE,
                Parsha.KEDOSHIM,
                Parsha.EMOR,
                Parsha.BEHAR,
                Parsha.BECHUKOSAI,
                Parsha.BAMIDBAR,
                Parsha.NASSO,
                Parsha.BEHAALOSCHA,
                Parsha.SHLACH,
                Parsha.KORACH,
                Parsha.CHUKAS,
                Parsha.BALAK,
                Parsha.PINCHAS,
                Parsha.MATOS,
                Parsha.MASEI,
                Parsha.DEVARIM,
                Parsha.VAESCHANAN,
                Parsha.EIKEV,
                Parsha.REEH,
                Parsha.SHOFTIM,
                Parsha.KI_SEITZEI,
                Parsha.KI_SAVO,
                Parsha.NITZAVIM_VAYEILECH
            ),
            arrayOf(
                Parsha.NONE,
                Parsha.NONE,
                Parsha.HAAZINU,
                Parsha.NONE,
                Parsha.NONE,
                Parsha.BERESHIS,
                Parsha.NOACH,
                Parsha.LECH_LECHA,
                Parsha.VAYERA,
                Parsha.CHAYEI_SARA,
                Parsha.TOLDOS,
                Parsha.VAYETZEI,
                Parsha.VAYISHLACH,
                Parsha.VAYESHEV,
                Parsha.MIKETZ,
                Parsha.VAYIGASH,
                Parsha.VAYECHI,
                Parsha.SHEMOS,
                Parsha.VAERA,
                Parsha.BO,
                Parsha.BESHALACH,
                Parsha.YISRO,
                Parsha.MISHPATIM,
                Parsha.TERUMAH,
                Parsha.TETZAVEH,
                Parsha.KI_SISA,
                Parsha.VAYAKHEL,
                Parsha.PEKUDEI,
                Parsha.VAYIKRA,
                Parsha.TZAV,
                Parsha.SHMINI,
                Parsha.TAZRIA,
                Parsha.METZORA,
                Parsha.NONE,
                Parsha.ACHREI_MOS,
                Parsha.KEDOSHIM,
                Parsha.EMOR,
                Parsha.BEHAR,
                Parsha.BECHUKOSAI,
                Parsha.BAMIDBAR,
                Parsha.NASSO,
                Parsha.BEHAALOSCHA,
                Parsha.SHLACH,
                Parsha.KORACH,
                Parsha.CHUKAS,
                Parsha.BALAK,
                Parsha.PINCHAS,
                Parsha.MATOS_MASEI,
                Parsha.DEVARIM,
                Parsha.VAESCHANAN,
                Parsha.EIKEV,
                Parsha.REEH,
                Parsha.SHOFTIM,
                Parsha.KI_SEITZEI,
                Parsha.KI_SAVO,
                Parsha.NITZAVIM_VAYEILECH
            ),
            arrayOf(
                Parsha.NONE,
                Parsha.NONE,
                Parsha.HAAZINU,
                Parsha.NONE,
                Parsha.NONE,
                Parsha.BERESHIS,
                Parsha.NOACH,
                Parsha.LECH_LECHA,
                Parsha.VAYERA,
                Parsha.CHAYEI_SARA,
                Parsha.TOLDOS,
                Parsha.VAYETZEI,
                Parsha.VAYISHLACH,
                Parsha.VAYESHEV,
                Parsha.MIKETZ,
                Parsha.VAYIGASH,
                Parsha.VAYECHI,
                Parsha.SHEMOS,
                Parsha.VAERA,
                Parsha.BO,
                Parsha.BESHALACH,
                Parsha.YISRO,
                Parsha.MISHPATIM,
                Parsha.TERUMAH,
                Parsha.TETZAVEH,
                Parsha.KI_SISA,
                Parsha.VAYAKHEL,
                Parsha.PEKUDEI,
                Parsha.VAYIKRA,
                Parsha.TZAV,
                Parsha.SHMINI,
                Parsha.TAZRIA,
                Parsha.METZORA,
                Parsha.NONE,
                Parsha.ACHREI_MOS,
                Parsha.KEDOSHIM,
                Parsha.EMOR,
                Parsha.BEHAR,
                Parsha.BECHUKOSAI,
                Parsha.BAMIDBAR,
                Parsha.NONE,
                Parsha.NASSO,
                Parsha.BEHAALOSCHA,
                Parsha.SHLACH,
                Parsha.KORACH,
                Parsha.CHUKAS_BALAK,
                Parsha.PINCHAS,
                Parsha.MATOS_MASEI,
                Parsha.DEVARIM,
                Parsha.VAESCHANAN,
                Parsha.EIKEV,
                Parsha.REEH,
                Parsha.SHOFTIM,
                Parsha.KI_SEITZEI,
                Parsha.KI_SAVO,
                Parsha.NITZAVIM_VAYEILECH
            ),
            arrayOf(
                Parsha.NONE,
                Parsha.VAYEILECH,
                Parsha.HAAZINU,
                Parsha.NONE,
                Parsha.BERESHIS,
                Parsha.NOACH,
                Parsha.LECH_LECHA,
                Parsha.VAYERA,
                Parsha.CHAYEI_SARA,
                Parsha.TOLDOS,
                Parsha.VAYETZEI,
                Parsha.VAYISHLACH,
                Parsha.VAYESHEV,
                Parsha.MIKETZ,
                Parsha.VAYIGASH,
                Parsha.VAYECHI,
                Parsha.SHEMOS,
                Parsha.VAERA,
                Parsha.BO,
                Parsha.BESHALACH,
                Parsha.YISRO,
                Parsha.MISHPATIM,
                Parsha.TERUMAH,
                Parsha.TETZAVEH,
                Parsha.KI_SISA,
                Parsha.VAYAKHEL_PEKUDEI,
                Parsha.VAYIKRA,
                Parsha.TZAV,
                Parsha.NONE,
                Parsha.SHMINI,
                Parsha.TAZRIA_METZORA,
                Parsha.ACHREI_MOS_KEDOSHIM,
                Parsha.EMOR,
                Parsha.BEHAR_BECHUKOSAI,
                Parsha.BAMIDBAR,
                Parsha.NASSO,
                Parsha.BEHAALOSCHA,
                Parsha.SHLACH,
                Parsha.KORACH,
                Parsha.CHUKAS,
                Parsha.BALAK,
                Parsha.PINCHAS,
                Parsha.MATOS_MASEI,
                Parsha.DEVARIM,
                Parsha.VAESCHANAN,
                Parsha.EIKEV,
                Parsha.REEH,
                Parsha.SHOFTIM,
                Parsha.KI_SEITZEI,
                Parsha.KI_SAVO,
                Parsha.NITZAVIM_VAYEILECH
            ),
            arrayOf(
                Parsha.NONE,
                Parsha.HAAZINU,
                Parsha.NONE,
                Parsha.NONE,
                Parsha.BERESHIS,
                Parsha.NOACH,
                Parsha.LECH_LECHA,
                Parsha.VAYERA,
                Parsha.CHAYEI_SARA,
                Parsha.TOLDOS,
                Parsha.VAYETZEI,
                Parsha.VAYISHLACH,
                Parsha.VAYESHEV,
                Parsha.MIKETZ,
                Parsha.VAYIGASH,
                Parsha.VAYECHI,
                Parsha.SHEMOS,
                Parsha.VAERA,
                Parsha.BO,
                Parsha.BESHALACH,
                Parsha.YISRO,
                Parsha.MISHPATIM,
                Parsha.TERUMAH,
                Parsha.TETZAVEH,
                Parsha.KI_SISA,
                Parsha.VAYAKHEL_PEKUDEI,
                Parsha.VAYIKRA,
                Parsha.TZAV,
                Parsha.NONE,
                Parsha.SHMINI,
                Parsha.TAZRIA_METZORA,
                Parsha.ACHREI_MOS_KEDOSHIM,
                Parsha.EMOR,
                Parsha.BEHAR,
                Parsha.BECHUKOSAI,
                Parsha.BAMIDBAR,
                Parsha.NASSO,
                Parsha.BEHAALOSCHA,
                Parsha.SHLACH,
                Parsha.KORACH,
                Parsha.CHUKAS,
                Parsha.BALAK,
                Parsha.PINCHAS,
                Parsha.MATOS_MASEI,
                Parsha.DEVARIM,
                Parsha.VAESCHANAN,
                Parsha.EIKEV,
                Parsha.REEH,
                Parsha.SHOFTIM,
                Parsha.KI_SEITZEI,
                Parsha.KI_SAVO,
                Parsha.NITZAVIM
            ),
            arrayOf(
                Parsha.NONE,
                Parsha.VAYEILECH,
                Parsha.HAAZINU,
                Parsha.NONE,
                Parsha.BERESHIS,
                Parsha.NOACH,
                Parsha.LECH_LECHA,
                Parsha.VAYERA,
                Parsha.CHAYEI_SARA,
                Parsha.TOLDOS,
                Parsha.VAYETZEI,
                Parsha.VAYISHLACH,
                Parsha.VAYESHEV,
                Parsha.MIKETZ,
                Parsha.VAYIGASH,
                Parsha.VAYECHI,
                Parsha.SHEMOS,
                Parsha.VAERA,
                Parsha.BO,
                Parsha.BESHALACH,
                Parsha.YISRO,
                Parsha.MISHPATIM,
                Parsha.TERUMAH,
                Parsha.TETZAVEH,
                Parsha.KI_SISA,
                Parsha.VAYAKHEL,
                Parsha.PEKUDEI,
                Parsha.VAYIKRA,
                Parsha.TZAV,
                Parsha.SHMINI,
                Parsha.TAZRIA,
                Parsha.METZORA,
                Parsha.NONE,
                Parsha.ACHREI_MOS,
                Parsha.KEDOSHIM,
                Parsha.EMOR,
                Parsha.BEHAR,
                Parsha.BECHUKOSAI,
                Parsha.BAMIDBAR,
                Parsha.NASSO,
                Parsha.BEHAALOSCHA,
                Parsha.SHLACH,
                Parsha.KORACH,
                Parsha.CHUKAS,
                Parsha.BALAK,
                Parsha.PINCHAS,
                Parsha.MATOS_MASEI,
                Parsha.DEVARIM,
                Parsha.VAESCHANAN,
                Parsha.EIKEV,
                Parsha.REEH,
                Parsha.SHOFTIM,
                Parsha.KI_SEITZEI,
                Parsha.KI_SAVO,
                Parsha.NITZAVIM_VAYEILECH
            ),
            arrayOf(
                Parsha.NONE,
                Parsha.VAYEILECH,
                Parsha.HAAZINU,
                Parsha.NONE,
                Parsha.BERESHIS,
                Parsha.NOACH,
                Parsha.LECH_LECHA,
                Parsha.VAYERA,
                Parsha.CHAYEI_SARA,
                Parsha.TOLDOS,
                Parsha.VAYETZEI,
                Parsha.VAYISHLACH,
                Parsha.VAYESHEV,
                Parsha.MIKETZ,
                Parsha.VAYIGASH,
                Parsha.VAYECHI,
                Parsha.SHEMOS,
                Parsha.VAERA,
                Parsha.BO,
                Parsha.BESHALACH,
                Parsha.YISRO,
                Parsha.MISHPATIM,
                Parsha.TERUMAH,
                Parsha.TETZAVEH,
                Parsha.KI_SISA,
                Parsha.VAYAKHEL,
                Parsha.PEKUDEI,
                Parsha.VAYIKRA,
                Parsha.TZAV,
                Parsha.SHMINI,
                Parsha.TAZRIA,
                Parsha.METZORA,
                Parsha.NONE,
                Parsha.ACHREI_MOS,
                Parsha.KEDOSHIM,
                Parsha.EMOR,
                Parsha.BEHAR,
                Parsha.BECHUKOSAI,
                Parsha.BAMIDBAR,
                Parsha.NASSO,
                Parsha.BEHAALOSCHA,
                Parsha.SHLACH,
                Parsha.KORACH,
                Parsha.CHUKAS,
                Parsha.BALAK,
                Parsha.PINCHAS,
                Parsha.MATOS,
                Parsha.MASEI,
                Parsha.DEVARIM,
                Parsha.VAESCHANAN,
                Parsha.EIKEV,
                Parsha.REEH,
                Parsha.SHOFTIM,
                Parsha.KI_SEITZEI,
                Parsha.KI_SAVO,
                Parsha.NITZAVIM
            ),
            arrayOf(
                Parsha.NONE,
                Parsha.NONE,
                Parsha.HAAZINU,
                Parsha.NONE,
                Parsha.NONE,
                Parsha.BERESHIS,
                Parsha.NOACH,
                Parsha.LECH_LECHA,
                Parsha.VAYERA,
                Parsha.CHAYEI_SARA,
                Parsha.TOLDOS,
                Parsha.VAYETZEI,
                Parsha.VAYISHLACH,
                Parsha.VAYESHEV,
                Parsha.MIKETZ,
                Parsha.VAYIGASH,
                Parsha.VAYECHI,
                Parsha.SHEMOS,
                Parsha.VAERA,
                Parsha.BO,
                Parsha.BESHALACH,
                Parsha.YISRO,
                Parsha.MISHPATIM,
                Parsha.TERUMAH,
                Parsha.TETZAVEH,
                Parsha.KI_SISA,
                Parsha.VAYAKHEL,
                Parsha.PEKUDEI,
                Parsha.VAYIKRA,
                Parsha.TZAV,
                Parsha.SHMINI,
                Parsha.TAZRIA,
                Parsha.METZORA,
                Parsha.NONE,
                Parsha.ACHREI_MOS,
                Parsha.KEDOSHIM,
                Parsha.EMOR,
                Parsha.BEHAR,
                Parsha.BECHUKOSAI,
                Parsha.BAMIDBAR,
                Parsha.NASSO,
                Parsha.BEHAALOSCHA,
                Parsha.SHLACH,
                Parsha.KORACH,
                Parsha.CHUKAS,
                Parsha.BALAK,
                Parsha.PINCHAS,
                Parsha.MATOS_MASEI,
                Parsha.DEVARIM,
                Parsha.VAESCHANAN,
                Parsha.EIKEV,
                Parsha.REEH,
                Parsha.SHOFTIM,
                Parsha.KI_SEITZEI,
                Parsha.KI_SAVO,
                Parsha.NITZAVIM_VAYEILECH
            )
        )
    }
}