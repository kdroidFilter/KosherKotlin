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

import com.kosherjava.zmanim.hebrewcalendar.JewishCalendar.Parsha.NONE
import com.kosherjava.zmanim.util.GeoLocation
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

import kotlin.math.floor
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

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
 * @see java.util.Calendar
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
     * are [*Yom HaShoah*][.YOM_HASHOAH], [*Yom Hazikaron*][.YOM_HAZIKARON], [YOM_HAATZMAUT] and [*Yom Yerushalayim*][.YOM_YERUSHALAYIM].
     *
     * @return the useModernHolidays true if set to return modern Israeli national holidays
     *
     * @see .setUseModernHolidays
     */
    /**
     * Sets the calendar to return modern Israeli national holidays. By default this value is false. The holidays are:
     * [*Yom HaShoah*][.YOM_HASHOAH], [*Yom Hazikaron*][.YOM_HAZIKARON], [][.YOM_HAATZMAUT] and [*Yom Yerushalayim*][.YOM_YERUSHALAYIM].
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
     * List of *parshiyos* or special *Shabasos*. [NONE] indicates a week without a *parsha*, while the enum for
     * the *parsha* of [.VZOS_HABERACHA] exists for consistency, but is not currently used. The special *Shabasos* of
     * Shekalim, Zachor, Para, Hachodesh, as well as Shabbos Shuva, Shira, Hagadol, Chazon and Nachamu are also represented in this collection
     * of *parshiyos*.
     * @see specialShabbos
     * @see parshah
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
    constructor() : super()

    /**
     * A constructor that initializes the date to the [Date] parameter.
     *
     * @param date
     * the `Date` to set the calendar to
     */
    constructor(date: Instant, tz: TimeZone) : super(date.toLocalDateTime(tz).date)

    /**
     * A constructor that initializes the date to the [date] parameter.
     *
     * @param date
     * the `LocalDate` to set the [gregorianLocalDate] to
     */
    constructor(date: LocalDate) : super(date)

    /**
     * A constructor that initializes the hebrew date to the [date] parameter.
     *
     * @param date
     * the `LocalDate` to set the [gregorianLocalDate] to
     */
    constructor(date: HebrewLocalDate) : super(date)

    /**
     * A constructor that initializes the date to the [date] parameter.
     *
     * @param date
     * the `LocalDate` to set the [gregorianLocalDate] to
     * @param isInIsrael whether this class should calculate religious events with the rules of someone in israel
     */
    constructor(date: LocalDate, isInIsrael: Boolean) : super(date) {
        inIsrael = isInIsrael
    }

    /**
     * A constructor that initializes the date to the [date] parameter.
     *
     * @param date
     * the `LocalDate` to set the [gregorianLocalDate] to
     * @param isInIsrael whether this class should calculate religious events with the rules of someone in israel
     */
    constructor(date: LocalDate, isInIsrael: Boolean, shouldUseModernHolidays: Boolean) : super(date) {
        isUseModernHolidays = shouldUseModernHolidays
        inIsrael = isInIsrael
    }

    /**
     * A constructor that initializes the hebrew date to the [date] parameter.
     *
     * @param date
     * the `LocalDate` to set the [gregorianLocalDate] to
     * @param isInIsrael whether this class should calculate religious events with the rules of someone in israel
     */
    constructor(date: HebrewLocalDate, isInIsrael: Boolean) : super(date) {
        inIsrael = isInIsrael
    }

    /**
     * A constructor that initializes the heberew date to the [date] parameter.
     *
     * @param date
     * the `LocalDate` to set the [gregorianLocalDate] to
     * @param isInIsrael whether this class should calculate religious events with the rules of someone in israel
     */
    constructor(date: HebrewLocalDate, isInIsrael: Boolean, shouldUseModernHolidays: Boolean) : super(date) {
        isUseModernHolidays = shouldUseModernHolidays
        inIsrael = isInIsrael
    }

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
     * if the day of month is < 1 or > 30, or a year of < 0 is passed in.
     */
    constructor(jewishYear: Int, jewishMonth: Int, jewishDayOfMonth: Int) : super(
        jewishYear,
        HebrewMonth.getMonthForValue(jewishMonth),
        jewishDayOfMonth
    )

    constructor(jewishYear: Long, jewishMonth: Int, jewishDayOfMonth: Int) : super(
        jewishYear,
        HebrewMonth.getMonthForValue(jewishMonth),
        jewishDayOfMonth
    )

    constructor(hebrewYear: Int, hebrewMonth: HebrewMonth, hebrewDayOfMonth: Int) : super(
        hebrewYear,
        hebrewMonth,
        hebrewDayOfMonth
    )

    constructor(hebrewYear: Long, hebrewMonth: HebrewMonth, hebrewDayOfMonth: Int) : super(
        hebrewYear,
        hebrewMonth,
        hebrewDayOfMonth
    )

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
    constructor(
        jewishYear: Int,
        jewishMonth: Int,
        jewishDayOfMonth: Int,
        inIsrael: Boolean,
    ) : this(jewishYear.toLong(), jewishMonth, jewishDayOfMonth, inIsrael)

    constructor(
        jewishYear: Long,
        jewishMonth: Int,
        jewishDayOfMonth: Int,
        inIsrael: Boolean,
    ) : super(
        jewishYear,
        HebrewMonth.getMonthForValue(jewishMonth),
        jewishDayOfMonth
    ) {
        this.inIsrael = inIsrael
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
    constructor(
        jewishYear: Int,
        jewishMonth: HebrewMonth,
        jewishDayOfMonth: Int,
        inIsrael: Boolean,
    ) : this(jewishYear.toLong(), jewishMonth, jewishDayOfMonth, inIsrael)

    constructor(
        jewishYear: Long,
        jewishMonth: HebrewMonth,
        jewishDayOfMonth: Int,
        inIsrael: Boolean,
    ) : super(
        jewishYear,
        jewishMonth,
        jewishDayOfMonth
    ) {
        this.inIsrael = inIsrael
    }// 28 years of 365.25 days + the offset from molad tohu mentioned above//elapsed days since molad ToHu
    //elapsed days to the current LocalDate date

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
            var elapsedDays = getJewishCalendarElapsedDays(hebrewLocalDate.year) //elapsed days since molad ToHu
            elapsedDays += daysSinceStartOfJewishYear //elapsed days to the current LocalDate date

            /* Molad Nissan year 1 was 177 days after molad tohu of Tishrei. We multiply 29.5 days * 6 months from Tishrei
              * to Nissan = 177. Subtract 7 days since tekufas Nissan was 7 days and 9 hours before the molad as stated in the Rambam
              * and we are now at 170 days. Because getJewishCalendarElapsedDays and getDaysSinceStartOfJewishYear use the value for
              * Rosh Hashana as 1, we have to add 1 day for a total of 171. To this add a day since the tekufah is on a Tuesday
              * night and we push off the bracha to Wednesday AM resulting in the 172 used in the calculation.
              */
            val elapsedDaysModTwentyEightYears = elapsedDays % (28 * 365.25)
            //println("Elapsed days ($elapsedDays) mod 28 years kotlin: $elapsedDaysModTwentyEightYears")
            return elapsedDaysModTwentyEightYears == 172.0 // 28 years of 365.25 days + the offset from molad tohu mentioned above
        }

    /**
     * Return the type of year for *parsha* calculations. The algorithm follows the
     * [&amp;&amp;Luach Arba'ah Shearim](http://hebrewbooks.org/pdfpager.aspx?req=14268&amp;st=&amp;pgnum=222) in the Tur Ohr Hachaim.
     * @return the type of year for *parsha* calculations.
     */
    private val parshaYearType: Int
        get() {
            var roshHashanaDayOfWeek =
                ((getJewishCalendarElapsedDays(hebrewLocalDate.year) + 1) % 7).toInt() // plus one to the original Rosh Hashana of year 1 to get a week starting on Sunday
            if (roshHashanaDayOfWeek == 0) {
                roshHashanaDayOfWeek = 7 // convert 0 to 7 for Shabbos for readability
            }
            if (isJewishLeapYear) {
                when (roshHashanaDayOfWeek) {
                    MONDAY -> {
                        if (isKislevShort) //BaCh
                            return if (inIsrael) 14 else 6
                        else if (isCheshvanLong) //BaSh
                            return if (inIsrael) 15 else 7
                    }

                    TUESDAY -> {
                        return if (inIsrael) 15 else 7
                    }

                    THURSDAY -> {
                        if (isKislevShort) return 8 //HaCh
                        if (isCheshvanLong) return 9 //HaSh
                    }

                    SATURDAY -> {
                        if (isKislevShort) return 10 //ZaCh
                        if (isCheshvanLong) //ZaSh
                            return if (inIsrael) 16 else 11
                    }
                }
            } else { //not a leap year
                when (roshHashanaDayOfWeek) {
                    MONDAY -> {
                        if (isKislevShort) return 0 //BaCh
                        if (isCheshvanLong) return if (inIsrael) 12 else 1 //BaSh
                    }

                    TUESDAY -> {
                        return if (inIsrael) 12 else 1
                    }

                    THURSDAY -> {
                        if (isCheshvanLong) return 3 //HaSh
                        if (!isKislevShort) return if (inIsrael) 13 else 2 //Hak
                    }

                    SATURDAY -> {
                        if (isKislevShort) return 4 //ZaCh
                        if (isCheshvanLong) return 5 //ZaSh
                    }
                }
            }
            return -1 //keep the compiler happy
        }// negative year should be impossible, but let's cover all bases
    //keep the compiler happy
    /**
     * Returns this week's [*Parsha*][Parsha] if it is *Shabbos*. It returns [Parsha.NONE] if the date
     * is a weekday or if there is no *parsha* that week (for example *Yom Tov* that falls on a *Shabbos*).
     *
     * @return the current *parsha*.
     */
    val parshah: Parsha
        get() {
            if (gregorianLocalDate.dayOfWeek != DayOfWeek.SATURDAY) {
                return NONE
            }
            val yearType = parshaYearType
            val roshHashanaDayOfWeek = getJewishCalendarElapsedDays(hebrewLocalDate.year) % 7
            val day = (roshHashanaDayOfWeek + daysSinceStartOfJewishYear).toInt()
            if (yearType >= 0) { // negative year should be impossible, but let's cover all bases
                return parshalist[yearType][day / 7]
            }
            return NONE //keep the compiler happy
        }//Yom Kippur / Sukkos or Pesach with 2 potential non-parsha Shabbosim in a row

    /**
     * Returns the upcoming [*Parsha*][Parsha] regardless of if it is the weekday or *Shabbos* (where next
     * Shabbos's *Parsha* will be returned. This is unlike [.getParshah] that returns [Parsha.NONE] if
     * the date is not *Shabbos*. If the upcoming Shabbos is a *Yom Tov* and has no *Parsha*, the
     * following week's *Parsha* will be returned.
     *
     * @return the upcoming *parsha*.
     */
    val upcomingParshah: Parsha
        get() {
            val copy = copy(
                jewishMonth = hebrewLocalDate.month,
                inIsrael = inIsrael
            ) //force JewishCalendar.copy, not JewishDate.copy
            val daysToShabbos =
                (DayOfWeek.SATURDAY.toJewishDayOfWeek() - copy.gregorianLocalDate.dayOfWeek.toJewishDayOfWeek() + 7) % 7
            //println("Days to shabbos kotlin: $daysToShabbos, gregorianLocalDate(${copy.gregorianLocalDate}).dayOfWeek = ${copy.gregorianLocalDate.dayOfWeek}")
            if (copy.gregorianLocalDate.dayOfWeek != DayOfWeek.SATURDAY) {
                copy.forward(DateTimeUnit.DAY, daysToShabbos)
            } else {
                copy.forward(DateTimeUnit.DAY, 7)
            }
            //println("New gregorianLocalDate(${copy.gregorianLocalDate}).dayOfWeek = ${copy.gregorianLocalDate.dayOfWeek}")

            while (copy.parshah == NONE) { //Yom Kippur / Sukkos or Pesach with 2 potential non-parsha Shabbosim in a row
                copy.forward(DateTimeUnit.DAY, 7)
                //println("New copy: ${copy.gregorianLocalDate}")
            }
            return copy.parshah
        }

    /**
     * Returns a [*Parsha*][Parsha] enum if the *Shabbos* is one of the four *parshiyos* of [Parsha.SHKALIM], [*Zachor*][Parsha.ZACHOR], [*Para*][Parsha.PARA], [Parsha.HACHODESH] or [Parsha.NONE] for a regular *Shabbos* (or any weekday).
     *
     * @return one of the four *parshiyos* of [*Shkalim*][Parsha.SHKALIM] [*Zachor*][Parsha.ZACHOR], [*Para*][Parsha.PARA], [*Hachdesh*][Parsha.HACHODESH] or [Parsha.NONE].
     */
    val specialShabbos: Parsha
        get() {
            if (gregorianLocalDate.dayOfWeek == DayOfWeek.SATURDAY) {
                if ((hebrewLocalDate.month == HebrewMonth.SHEVAT && !isJewishLeapYear) || (hebrewLocalDate.month == HebrewMonth.ADAR && isJewishLeapYear)) {
                    if ((jewishDayOfMonth == 25) || (jewishDayOfMonth == 27) || (jewishDayOfMonth == 29)) {
                        return Parsha.SHKALIM
                    }
                }
                if ((hebrewLocalDate.month == HebrewMonth.ADAR && !isJewishLeapYear) || hebrewLocalDate.month == HebrewMonth.ADAR_II) {
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
                if (hebrewLocalDate.month == HebrewMonth.NISSAN) {
                    if (jewishDayOfMonth == 1) {
                        return Parsha.HACHODESH
                    }
                    if (jewishDayOfMonth >= 8 && jewishDayOfMonth <= 14) {
                        return Parsha.HAGADOL
                    }
                }
                if (hebrewLocalDate.month == HebrewMonth.AV) {
                    if (jewishDayOfMonth >= 4 && jewishDayOfMonth <= 9) {
                        return Parsha.CHAZON
                    }
                    if (jewishDayOfMonth >= 10 && jewishDayOfMonth <= 16) {
                        return Parsha.NACHAMU
                    }
                }
                if (hebrewLocalDate.month == HebrewMonth.TISHREI) {
                    if (jewishDayOfMonth >= 3 && jewishDayOfMonth <= 8) {
                        return Parsha.SHUVA
                    }
                }
                if (parshah == Parsha.BESHALACH) {
                    return Parsha.SHIRA
                }
            }
            return NONE
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
            val day = jewishDayOfMonth
            val dayOfWeek = gregorianLocalDate.dayOfWeek
            return when (hebrewLocalDate.month) {
                HebrewMonth.NISSAN -> {
                    when {
                        day == 14 -> EREV_PESACH
                        day == 15 || day == 21 || !inIsrael && (day == 16 || day == 22) -> PESACH
                        day in (if (inIsrael) 16 else 17)..20 -> CHOL_HAMOED_PESACH
                        day == if (inIsrael) 22 else 23 -> ISRU_CHAG
                        isUseModernHolidays && (
                                (
                                        (day == 26 && dayOfWeek == DayOfWeek.THURSDAY) ||
                                                (day == 28 && dayOfWeek == DayOfWeek.MONDAY) ||
                                                (day == 27 && dayOfWeek != DayOfWeek.SUNDAY && dayOfWeek != DayOfWeek.FRIDAY)
                                        )
                                ) -> YOM_HASHOAH

                        else -> NO_HOLIDAY
                    }

                }

                HebrewMonth.IYAR -> {

                    if (day == 14) PESACH_SHENI
                    else if (day == 18) LAG_BAOMER
                    else if (isUseModernHolidays) when {
                        (day in 2..3 && dayOfWeek === DayOfWeek.WEDNESDAY) ||
                                (day == 4 && dayOfWeek === DayOfWeek.TUESDAY) ||
                                (day == 5 && dayOfWeek === DayOfWeek.MONDAY) -> YOM_HAZIKARON
                        (day in 3..4 && dayOfWeek === DayOfWeek.THURSDAY) ||
                                (day == 5 && dayOfWeek === DayOfWeek.WEDNESDAY) ||
                                (day == 6 && dayOfWeek === DayOfWeek.TUESDAY) -> YOM_HAATZMAUT
                        day == 28 -> YOM_YERUSHALAYIM
                        else -> NO_HOLIDAY
                    }
                    else NO_HOLIDAY
                }

                HebrewMonth.SIVAN -> {
                    when {
                        day == 5 -> EREV_SHAVUOS
                        day == 6 || day == 7 && !inIsrael -> SHAVUOS
                        day == if(inIsrael) 7 else 8 -> ISRU_CHAG
                        else -> NO_HOLIDAY
                    }
                }

                HebrewMonth.TAMMUZ ->            // push off the fast day if it falls on Shabbos// if 13th Adar falls on Friday or Shabbos, push back to Thursday// else if a leap year// if 13th Adar falls on Friday or Shabbos, push back to Thursday// if (day == 24) {
                    // return EREV_CHANUKAH;
                    // } else
// push off Tzom Gedalia if it falls on Shabbos// if Tisha B'av falls on Shabbos, push off until Sunday
                    // if 5 Iyar falls on Wed, Yom Haatzmaut is that day. If it fal1s on Friday or Shabbos, it is moved back to
                    // Thursday. If it falls on Monday it is moved to Tuesday
                {
                    // push off the fast day if it falls on Shabbos
                    if (
                        (day == 17 && dayOfWeek != DayOfWeek.SATURDAY) ||
                        (day == 18 && dayOfWeek == DayOfWeek.SUNDAY)
                    ) SEVENTEEN_OF_TAMMUZ
                    else NO_HOLIDAY
                }

                HebrewMonth.AV -> {
                    // if Tisha B'av falls on Shabbos, push off until Sunday
                    when {
                        day == 10 && dayOfWeek == DayOfWeek.SUNDAY || day == 9 && dayOfWeek != DayOfWeek.SATURDAY -> TISHA_BEAV
                        day == 15 -> TU_BEAV
                        else -> NO_HOLIDAY
                    }
                }

                HebrewMonth.ELUL ->// if 13th Adar falls on Friday or Shabbos, push back to Thursday// else if a leap year// if 13th Adar falls on Friday or Shabbos, push back to Thursday// if (day == 24) {
                    // return EREV_CHANUKAH;
                    // } else
// push off Tzom Gedalia if it falls on Shabbos// if Tisha B'av falls on Shabbos, push off until Sunday// push off the fast day if it falls on Shabbos// push off the fast day if it falls on Shabbos// if 13th Adar falls on Friday or Shabbos, push back to Thursday// else if a leap year// if 13th Adar falls on Friday or Shabbos, push back to Thursday// if (day == 24) {
                    // return EREV_CHANUKAH;
                    // } else
// push off Tzom Gedalia if it falls on Shabbos// if Tisha B'av falls on Shabbos, push off until Sunday
                    // if 5 Iyar falls on Wed, Yom Haatzmaut is that day. If it fal1s on Friday or Shabbos, it is moved back to
                    // Thursday. If it falls on Monday it is moved to Tuesday
                    // if 5 Iyar falls on Wed, Yom Haatzmaut is that day. If it fal1s on Friday or Shabbos, it is moved back to
                    // Thursday. If it falls on Monday it is moved to Tuesday
                {
                    if (day == 29) return EREV_ROSH_HASHANA
                    else NO_HOLIDAY
                }

                HebrewMonth.TISHREI -> {
                    return when (day) {
                        1 -> ROSH_HASHANA
                        2 -> ROSH_HASHANA
                        9 -> EREV_YOM_KIPPUR
                        10 -> YOM_KIPPUR
                        14 -> EREV_SUCCOS
                        15 -> SUCCOS
                        16 -> if (inIsrael) CHOL_HAMOED_SUCCOS else SUCCOS
                        17, 18, 19, 20 -> CHOL_HAMOED_SUCCOS
                        21 -> HOSHANA_RABBA
                        22 -> SHEMINI_ATZERES
                        23 -> if (inIsrael) ISRU_CHAG else SIMCHAS_TORAH
                        else -> when (day) {
                            3 -> if (dayOfWeek != DayOfWeek.SATURDAY) FAST_OF_GEDALYAH else NO_HOLIDAY
                            4 -> if (dayOfWeek == DayOfWeek.SUNDAY) FAST_OF_GEDALYAH else NO_HOLIDAY// push off Tzom Gedalia if it falls on Shabbos
                            24 -> if (!inIsrael) ISRU_CHAG else NO_HOLIDAY
                            else -> NO_HOLIDAY
                        }
                    }
                }
                HebrewMonth.CHESHVAN -> NO_HOLIDAY // :(
                HebrewMonth.KISLEV ->            // if (day == 24) {
                    // return EREV_CHANUKAH;
                    // } else// if 13th Adar falls on Friday or Shabbos, push back to Thursday// else if a leap year// if 13th Adar falls on Friday or Shabbos, push back to Thursday// push off Tzom Gedalia if it falls on Shabbos// if 13th Adar falls on Friday or Shabbos, push back to Thursday// else if a leap year// if 13th Adar falls on Friday or Shabbos, push back to Thursday// if (day == 24) {
                    // return EREV_CHANUKAH;
                    // } else
// push off Tzom Gedalia if it falls on Shabbos// if Tisha B'av falls on Shabbos, push off until Sunday// push off the fast day if it falls on Shabbos// push off the fast day if it falls on Shabbos// if 13th Adar falls on Friday or Shabbos, push back to Thursday// else if a leap year// if 13th Adar falls on Friday or Shabbos, push back to Thursday// if (day == 24) {
                    // return EREV_CHANUKAH;
                    // } else
// push off Tzom Gedalia if it falls on Shabbos// if Tisha B'av falls on Shabbos, push off until Sunday
                    // if 5 Iyar falls on Wed, Yom Haatzmaut is that day. If it fal1s on Friday or Shabbos, it is moved back to
                    // Thursday. If it falls on Monday it is moved to Tuesday
                    // if 5 Iyar falls on Wed, Yom Haatzmaut is that day. If it fal1s on Friday or Shabbos, it is moved back to
                    // Thursday. If it falls on Monday it is moved to Tuesday
// if Tisha B'av falls on Shabbos, push off until Sunday// push off the fast day if it falls on Shabbos// push off the fast day if it falls on Shabbos// if 13th Adar falls on Friday or Shabbos, push back to Thursday// else if a leap year// if 13th Adar falls on Friday or Shabbos, push back to Thursday// if (day == 24) {
                    // return EREV_CHANUKAH;
                    // } else
// push off Tzom Gedalia if it falls on Shabbos// if Tisha B'av falls on Shabbos, push off until Sunday
                    // if 5 Iyar falls on Wed, Yom Haatzmaut is that day. If it fal1s on Friday or Shabbos, it is moved back to
                    // Thursday. If it falls on Monday it is moved to Tuesday
                    // if 5 Iyar falls on Wed, Yom Haatzmaut is that day. If it fal1s on Friday or Shabbos, it is moved back to
                    // Thursday. If it falls on Monday it is moved to Tuesday
                {
                    // if (day == 24) {
                    // return EREV_CHANUKAH;
                    // } else
                    if (day >= 25) CHANUKAH
                    else NO_HOLIDAY
                }

                HebrewMonth.TEVES -> {
                    when (day) {
                        in 1..(if (isKislevShort) 3 else 2) -> CHANUKAH//TODO use constant comparison
                        10 -> TENTH_OF_TEVES
                        else -> NO_HOLIDAY
                    }
                }

                HebrewMonth.SHEVAT ->// if 13th Adar falls on Friday or Shabbos, push back to Thursday// else if a leap year// if 13th Adar falls on Friday or Shabbos, push back to Thursday// if (day == 24) {
                    // return EREV_CHANUKAH;
                    // } else
// if (day == 24) {
                    // return EREV_CHANUKAH;
                    // } else// if 13th Adar falls on Friday or Shabbos, push back to Thursday// else if a leap year// if 13th Adar falls on Friday or Shabbos, push back to Thursday// push off Tzom Gedalia if it falls on Shabbos// if 13th Adar falls on Friday or Shabbos, push back to Thursday// else if a leap year// if 13th Adar falls on Friday or Shabbos, push back to Thursday// if (day == 24) {
                    // return EREV_CHANUKAH;
                    // } else
// push off Tzom Gedalia if it falls on Shabbos// if Tisha B'av falls on Shabbos, push off until Sunday// push off the fast day if it falls on Shabbos// push off the fast day if it falls on Shabbos// if 13th Adar falls on Friday or Shabbos, push back to Thursday// else if a leap year// if 13th Adar falls on Friday or Shabbos, push back to Thursday// if (day == 24) {
                    // return EREV_CHANUKAH;
                    // } else
// push off Tzom Gedalia if it falls on Shabbos// if Tisha B'av falls on Shabbos, push off until Sunday
                    // if 5 Iyar falls on Wed, Yom Haatzmaut is that day. If it fal1s on Friday or Shabbos, it is moved back to
                    // Thursday. If it falls on Monday it is moved to Tuesday
                    // if 5 Iyar falls on Wed, Yom Haatzmaut is that day. If it fal1s on Friday or Shabbos, it is moved back to
                    // Thursday. If it falls on Monday it is moved to Tuesday
// if Tisha B'av falls on Shabbos, push off until Sunday// push off the fast day if it falls on Shabbos// push off the fast day if it falls on Shabbos// if 13th Adar falls on Friday or Shabbos, push back to Thursday// else if a leap year// if 13th Adar falls on Friday or Shabbos, push back to Thursday// if (day == 24) {
                    // return EREV_CHANUKAH;
                    // } else
// push off Tzom Gedalia if it falls on Shabbos// if Tisha B'av falls on Shabbos, push off until Sunday
                    // if 5 Iyar falls on Wed, Yom Haatzmaut is that day. If it fal1s on Friday or Shabbos, it is moved back to
                    // Thursday. If it falls on Monday it is moved to Tuesday
                    // if 5 Iyar falls on Wed, Yom Haatzmaut is that day. If it fal1s on Friday or Shabbos, it is moved back to
                    // Thursday. If it falls on Monday it is moved to Tuesday
// push off Tzom Gedalia if it falls on Shabbos// if 13th Adar falls on Friday or Shabbos, push back to Thursday// else if a leap year// if 13th Adar falls on Friday or Shabbos, push back to Thursday// if (day == 24) {
                    // return EREV_CHANUKAH;
                    // } else
// push off Tzom Gedalia if it falls on Shabbos// if Tisha B'av falls on Shabbos, push off until Sunday// push off the fast day if it falls on Shabbos// push off the fast day if it falls on Shabbos// if 13th Adar falls on Friday or Shabbos, push back to Thursday// else if a leap year// if 13th Adar falls on Friday or Shabbos, push back to Thursday// if (day == 24) {
                    // return EREV_CHANUKAH;
                    // } else
// push off Tzom Gedalia if it falls on Shabbos// if Tisha B'av falls on Shabbos, push off until Sunday
                    // if 5 Iyar falls on Wed, Yom Haatzmaut is that day. If it fal1s on Friday or Shabbos, it is moved back to
                    // Thursday. If it falls on Monday it is moved to Tuesday
                    // if 5 Iyar falls on Wed, Yom Haatzmaut is that day. If it fal1s on Friday or Shabbos, it is moved back to
                    // Thursday. If it falls on Monday it is moved to Tuesday
// if Tisha B'av falls on Shabbos, push off until Sunday// push off the fast day if it falls on Shabbos// push off the fast day if it falls on Shabbos// if 13th Adar falls on Friday or Shabbos, push back to Thursday// else if a leap year// if 13th Adar falls on Friday or Shabbos, push back to Thursday// if (day == 24) {
                    // return EREV_CHANUKAH;
                    // } else
// push off Tzom Gedalia if it falls on Shabbos// if Tisha B'av falls on Shabbos, push off until Sunday
                    // if 5 Iyar falls on Wed, Yom Haatzmaut is that day. If it fal1s on Friday or Shabbos, it is moved back to
                    // Thursday. If it falls on Monday it is moved to Tuesday
                    // if 5 Iyar falls on Wed, Yom Haatzmaut is that day. If it fal1s on Friday or Shabbos, it is moved back to
                    // Thursday. If it falls on Monday it is moved to Tuesday
                {
                    if (day == 15) TU_BESHVAT
                    else NO_HOLIDAY
                }

                HebrewMonth.ADAR ->// if 13th Adar falls on Friday or Shabbos, push back to Thursday// if 13th Adar falls on Friday or Shabbos, push back to Thursday// else if a leap year// if 13th Adar falls on Friday or Shabbos, push back to Thursday// if (day == 24) {
                    // return EREV_CHANUKAH;
                    // } else
// if (day == 24) {
                    // return EREV_CHANUKAH;
                    // } else// if 13th Adar falls on Friday or Shabbos, push back to Thursday// else if a leap year// if 13th Adar falls on Friday or Shabbos, push back to Thursday// push off Tzom Gedalia if it falls on Shabbos// if 13th Adar falls on Friday or Shabbos, push back to Thursday// else if a leap year// if 13th Adar falls on Friday or Shabbos, push back to Thursday// if (day == 24) {
                    // return EREV_CHANUKAH;
                    // } else
// push off Tzom Gedalia if it falls on Shabbos// if Tisha B'av falls on Shabbos, push off until Sunday// push off the fast day if it falls on Shabbos// push off the fast day if it falls on Shabbos// if 13th Adar falls on Friday or Shabbos, push back to Thursday// else if a leap year// if 13th Adar falls on Friday or Shabbos, push back to Thursday// if (day == 24) {
                    // return EREV_CHANUKAH;
                    // } else
// push off Tzom Gedalia if it falls on Shabbos// if Tisha B'av falls on Shabbos, push off until Sunday
                    // if 5 Iyar falls on Wed, Yom Haatzmaut is that day. If it fal1s on Friday or Shabbos, it is moved back to
                    // Thursday. If it falls on Monday it is moved to Tuesday
                    // if 5 Iyar falls on Wed, Yom Haatzmaut is that day. If it fal1s on Friday or Shabbos, it is moved back to
                    // Thursday. If it falls on Monday it is moved to Tuesday
// if Tisha B'av falls on Shabbos, push off until Sunday// push off the fast day if it falls on Shabbos// push off the fast day if it falls on Shabbos// if 13th Adar falls on Friday or Shabbos, push back to Thursday// else if a leap year// if 13th Adar falls on Friday or Shabbos, push back to Thursday// if (day == 24) {
                    // return EREV_CHANUKAH;
                    // } else
// push off Tzom Gedalia if it falls on Shabbos// if Tisha B'av falls on Shabbos, push off until Sunday
                    // if 5 Iyar falls on Wed, Yom Haatzmaut is that day. If it fal1s on Friday or Shabbos, it is moved back to
                    // Thursday. If it falls on Monday it is moved to Tuesday
                    // if 5 Iyar falls on Wed, Yom Haatzmaut is that day. If it fal1s on Friday or Shabbos, it is moved back to
                    // Thursday. If it falls on Monday it is moved to Tuesday
// push off Tzom Gedalia if it falls on Shabbos// if 13th Adar falls on Friday or Shabbos, push back to Thursday// else if a leap year// if 13th Adar falls on Friday or Shabbos, push back to Thursday// if (day == 24) {
                    // return EREV_CHANUKAH;
                    // } else
// push off Tzom Gedalia if it falls on Shabbos// if Tisha B'av falls on Shabbos, push off until Sunday// push off the fast day if it falls on Shabbos// push off the fast day if it falls on Shabbos// if 13th Adar falls on Friday or Shabbos, push back to Thursday// else if a leap year// if 13th Adar falls on Friday or Shabbos, push back to Thursday// if (day == 24) {
                    // return EREV_CHANUKAH;
                    // } else
// push off Tzom Gedalia if it falls on Shabbos// if Tisha B'av falls on Shabbos, push off until Sunday
                    // if 5 Iyar falls on Wed, Yom Haatzmaut is that day. If it fal1s on Friday or Shabbos, it is moved back to
                    // Thursday. If it falls on Monday it is moved to Tuesday
                    // if 5 Iyar falls on Wed, Yom Haatzmaut is that day. If it fal1s on Friday or Shabbos, it is moved back to
                    // Thursday. If it falls on Monday it is moved to Tuesday
// if Tisha B'av falls on Shabbos, push off until Sunday// push off the fast day if it falls on Shabbos// push off the fast day if it falls on Shabbos// if 13th Adar falls on Friday or Shabbos, push back to Thursday// else if a leap year// if 13th Adar falls on Friday or Shabbos, push back to Thursday// if (day == 24) {
                    // return EREV_CHANUKAH;
                    // } else
// push off Tzom Gedalia if it falls on Shabbos// if Tisha B'av falls on Shabbos, push off until Sunday
                    // if 5 Iyar falls on Wed, Yom Haatzmaut is that day. If it fal1s on Friday or Shabbos, it is moved back to
                    // Thursday. If it falls on Monday it is moved to Tuesday
                    // if 5 Iyar falls on Wed, Yom Haatzmaut is that day. If it fal1s on Friday or Shabbos, it is moved back to
                    // Thursday. If it falls on Monday it is moved to Tuesday
// if (day == 24) {
                    // return EREV_CHANUKAH;
                    // } else
// if (day == 24) {
                    // return EREV_CHANUKAH;
                    // } else// if 13th Adar falls on Friday or Shabbos, push back to Thursday// else if a leap year// if 13th Adar falls on Friday or Shabbos, push back to Thursday// push off Tzom Gedalia if it falls on Shabbos// if 13th Adar falls on Friday or Shabbos, push back to Thursday// else if a leap year// if 13th Adar falls on Friday or Shabbos, push back to Thursday// if (day == 24) {
                    // return EREV_CHANUKAH;
                    // } else
// push off Tzom Gedalia if it falls on Shabbos// if Tisha B'av falls on Shabbos, push off until Sunday// push off the fast day if it falls on Shabbos// push off the fast day if it falls on Shabbos// if 13th Adar falls on Friday or Shabbos, push back to Thursday// else if a leap year// if 13th Adar falls on Friday or Shabbos, push back to Thursday// if (day == 24) {
                    // return EREV_CHANUKAH;
                    // } else
// push off Tzom Gedalia if it falls on Shabbos// if Tisha B'av falls on Shabbos, push off until Sunday
                    // if 5 Iyar falls on Wed, Yom Haatzmaut is that day. If it fal1s on Friday or Shabbos, it is moved back to
                    // Thursday. If it falls on Monday it is moved to Tuesday
                    // if 5 Iyar falls on Wed, Yom Haatzmaut is that day. If it fal1s on Friday or Shabbos, it is moved back to
                    // Thursday. If it falls on Monday it is moved to Tuesday
// if Tisha B'av falls on Shabbos, push off until Sunday// push off the fast day if it falls on Shabbos// push off the fast day if it falls on Shabbos// if 13th Adar falls on Friday or Shabbos, push back to Thursday// else if a leap year// if 13th Adar falls on Friday or Shabbos, push back to Thursday// if (day == 24) {
                    // return EREV_CHANUKAH;
                    // } else
// push off Tzom Gedalia if it falls on Shabbos// if Tisha B'av falls on Shabbos, push off until Sunday
                    // if 5 Iyar falls on Wed, Yom Haatzmaut is that day. If it fal1s on Friday or Shabbos, it is moved back to
                    // Thursday. If it falls on Monday it is moved to Tuesday
                    // if 5 Iyar falls on Wed, Yom Haatzmaut is that day. If it fal1s on Friday or Shabbos, it is moved back to
                    // Thursday. If it falls on Monday it is moved to Tuesday
// push off Tzom Gedalia if it falls on Shabbos// if 13th Adar falls on Friday or Shabbos, push back to Thursday// else if a leap year// if 13th Adar falls on Friday or Shabbos, push back to Thursday// if (day == 24) {
                    // return EREV_CHANUKAH;
                    // } else
// push off Tzom Gedalia if it falls on Shabbos// if Tisha B'av falls on Shabbos, push off until Sunday// push off the fast day if it falls on Shabbos// push off the fast day if it falls on Shabbos// if 13th Adar falls on Friday or Shabbos, push back to Thursday// else if a leap year// if 13th Adar falls on Friday or Shabbos, push back to Thursday// if (day == 24) {
                    // return EREV_CHANUKAH;
                    // } else
// push off Tzom Gedalia if it falls on Shabbos// if Tisha B'av falls on Shabbos, push off until Sunday
                    // if 5 Iyar falls on Wed, Yom Haatzmaut is that day. If it fal1s on Friday or Shabbos, it is moved back to
                    // Thursday. If it falls on Monday it is moved to Tuesday
                    // if 5 Iyar falls on Wed, Yom Haatzmaut is that day. If it fal1s on Friday or Shabbos, it is moved back to
                    // Thursday. If it falls on Monday it is moved to Tuesday
// if Tisha B'av falls on Shabbos, push off until Sunday// push off the fast day if it falls on Shabbos// push off the fast day if it falls on Shabbos// if 13th Adar falls on Friday or Shabbos, push back to Thursday// else if a leap year// if 13th Adar falls on Friday or Shabbos, push back to Thursday// if (day == 24) {
                    // return EREV_CHANUKAH;
                    // } else
// push off Tzom Gedalia if it falls on Shabbos// if Tisha B'av falls on Shabbos, push off until Sunday
                    // if 5 Iyar falls on Wed, Yom Haatzmaut is that day. If it fal1s on Friday or Shabbos, it is moved back to
                    // Thursday. If it falls on Monday it is moved to Tuesday
                    // if 5 Iyar falls on Wed, Yom Haatzmaut is that day. If it fal1s on Friday or Shabbos, it is moved back to
                    // Thursday. If it falls on Monday it is moved to Tuesday
                {
                    if (!isJewishLeapYear) {
                        // if 13th Adar falls on Friday or Shabbos, push back to Thursday
                        when (day) {
                            11, 12 -> if (dayOfWeek == DayOfWeek.THURSDAY) FAST_OF_ESTHER else NO_HOLIDAY
                            13 -> if (dayOfWeek !in DayOfWeek.FRIDAY..DayOfWeek.SATURDAY) FAST_OF_ESTHER else NO_HOLIDAY
                            14 -> PURIM
                            15 -> SHUSHAN_PURIM
                            else -> NO_HOLIDAY
                        }
                    } else { // else if a leap year
                        when (day) {
                            14 -> return PURIM_KATAN
                            15 -> return SHUSHAN_PURIM_KATAN
                            else -> NO_HOLIDAY
                        }
                    }
                }

                HebrewMonth.ADAR_II -> {
                    // if 13th Adar falls on Friday or Shabbos, push back to Thursday
                    when (day) {
                        11, 12 -> if (dayOfWeek == DayOfWeek.THURSDAY) FAST_OF_ESTHER else NO_HOLIDAY
                        13 -> if (dayOfWeek != DayOfWeek.FRIDAY && dayOfWeek != DayOfWeek.SATURDAY) FAST_OF_ESTHER else NO_HOLIDAY
                        14 -> PURIM
                        15 -> SHUSHAN_PURIM
                        else -> NO_HOLIDAY
                    }
                }
            }
        }


    /**
     * Returns true if the current day is *Yom Tov*. The method returns true even for holidays such as [CHANUKAH]
     * and minor ones such as [TU_BEAV] and [PESACH_SHENI]. *Erev Yom Tov* (with the exception of
     * [HOSHANA_RABBA], *erev* the second days of [PESACH]) returns false, as do [fast][isTaanis] besides [YOM_KIPPUR].
     * Use [isAssurBemelacha] to find the days that have a prohibition of work.
     *
     * @return true if the current day is a Yom Tov
     *
     * @see yomTovIndex
     * @see isErevYomTov
     * @see isErevYomTovSheni
     * @see isTaanis
     * @see isAssurBemelacha
     * @see isCholHamoed
     */
    val isYomTov: Boolean
        get() {
            val holidayIndex = yomTovIndex
            if (
                (
                        isErevYomTov && (
                                holidayIndex != HOSHANA_RABBA ||
                                        (holidayIndex == CHOL_HAMOED_PESACH && jewishDayOfMonth != 20)
                                )
                        ) ||
                (isTaanis && holidayIndex != YOM_KIPPUR) ||
                (holidayIndex == ISRU_CHAG)
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
        get() = yomTovIndex.let {
            it == PESACH ||
                    it == SHAVUOS ||
                    it == SUCCOS ||
                    it == SHEMINI_ATZERES ||
                    it == SIMCHAS_TORAH ||
                    it == ROSH_HASHANA ||
                    it == YOM_KIPPUR
        }

    /**
     * Returns true if it is *Shabbos* or if it is a *Yom Tov* day that has a *melacha* (work)  prohibition.
     *
     * @return if the day is a *Yom Tov* that is *assur bemlacha* or *Shabbos*
     */
    val isAssurBemelacha: Boolean
        get() = gregorianLocalDate.dayOfWeek == DayOfWeek.SATURDAY || isYomTovAssurBemelacha

    /**
     * Returns true if the day has candle lighting. This will return true on *Erev Shabbos*, *Erev Yom Tov*, the
     * first day of *Rosh Hashana* and the first days of *Yom Tov* out of Israel. It is identical
     * to calling [isTomorrowShabbosOrYomTov].
     *
     * @return if the day has candle lighting.
     *
     * @see isTomorrowShabbosOrYomTov
     */
    val hasCandleLighting: Boolean get() = isTomorrowShabbosOrYomTov

    /**
     * Returns true if tomorrow is *Shabbos* or *Yom Tov*. This will return true on *Erev Shabbos*,
     * *Erev Yom Tov*, the first day of *Rosh Hashana* and *erev* the first days of *Yom Tov*
     * out of Israel. It is identical to calling [hasCandleLighting].
     *
     * @return will return if the next day is *Shabbos* or *Yom Tov*.
     *
     * @see hasCandleLighting
     */
    val isTomorrowShabbosOrYomTov: Boolean
        get() = gregorianLocalDate.dayOfWeek == DayOfWeek.FRIDAY || isErevYomTov || isErevYomTovSheni

    /**
     * Returns true if the day is the second day of *Yom Tov*. This impacts the second day of *Rosh Hashana* everywhere and
     * the second days of Yom Tov in *chutz laaretz* (out of Israel).
     *
     * @return  if the day is the second day of *Yom Tov*.
     */
    val isErevYomTovSheni: Boolean
        get() = (hebrewLocalDate.month == HebrewMonth.TISHREI && jewishDayOfMonth == 1) ||
                (
                        !inIsrael && (
                                (
                                        (hebrewLocalDate.month == HebrewMonth.NISSAN && (jewishDayOfMonth == 15 || jewishDayOfMonth == 21)) ||
                                                (hebrewLocalDate.month == HebrewMonth.TISHREI && (jewishDayOfMonth == 15 || jewishDayOfMonth == 22)) ||
                                                (hebrewLocalDate.month == HebrewMonth.SIVAN && jewishDayOfMonth == 6)
                                        )
                                )
                        )


    /**
     * Returns true if the current day is *Aseres Yemei Teshuva*.
     *
     * @return if the current day is *Aseres Yemei Teshuvah*
     */
    val isAseresYemeiTeshuva: Boolean
        get() = hebrewLocalDate.month == HebrewMonth.TISHREI && jewishDayOfMonth <= 10

    /**
     * Returns true if the current day is *Pesach* (either  the *Yom Tov* of *Pesach* or*Chol Hamoed Pesach*).
     *
     * @return true if the current day is *Pesach* (either  the *Yom Tov* of *Pesach* or*Chol Hamoed Pesach*).
     * @see isYomTov
     * @see isCholHamoedPesach
     * @see PESACH
     *
     * @see CHOL_HAMOED_PESACH
     */
    val isPesach: Boolean
        get() = yomTovIndex.let {
            it == PESACH || it == CHOL_HAMOED_PESACH
        }

    /**
     * Returns true if the current day is *Chol Hamoed* of *Pesach*.
     *
     * @return true if the current day is *Chol Hamoed* of *Pesach*
     * @see isYomTov
     * @see isPesach
     * @see CHOL_HAMOED_PESACH
     */
    val isCholHamoedPesach: Boolean
        get() = yomTovIndex == CHOL_HAMOED_PESACH

    /**
     * Returns true if the current day is *Shavuos*.
     *
     * @return true if the current day is *Shavuos*.
     * @see isYomTov
     * @see SHAVUOS
     */
    val isShavuos: Boolean
        get() = yomTovIndex == SHAVUOS

    /**
     * Returns true if the current day is *Rosh Hashana*.
     *
     * @return true if the current day is *Rosh Hashana*.
     * @see isYomTov
     * @see ROSH_HASHANA
     */
    val isRoshHashana: Boolean
        get() = yomTovIndex == ROSH_HASHANA

    /**
     * Returns true if the current day is *Yom Kippur*.
     *
     * @return true if the current day is *Yom Kippur*.
     * @see isYomTov
     * @see YOM_KIPPUR
     */
    val isYomKippur: Boolean
        get() = yomTovIndex == YOM_KIPPUR

    /**
     * Returns true if the current day is *Succos* (either  the *Yom Tov* of *Succos* or*Chol Hamoed Succos*).
     * It will return false for [Shmini Atzeres][isShminiAtzeres] and [Simchas Torah][isSimchasTorah].
     *
     * @return true if the current day is *Succos* (either  the *Yom Tov* of *Succos* or*Chol Hamoed Succos*.
     * @see isYomTov
     * @see isCholHamoedSuccos
     * @see isHoshanaRabba
     * @see SUCCOS
     *
     * @see CHOL_HAMOED_SUCCOS
     *
     * @see HOSHANA_RABBA
     */
    val isSuccos: Boolean
        get() = yomTovIndex.let {
            it == SUCCOS || it == CHOL_HAMOED_SUCCOS || it == HOSHANA_RABBA
        }

    /**
     * Returns true if the current day is *Hoshana Rabba*.
     *
     * @return true true if the current day is *Hoshana Rabba*.
     * @see isYomTov
     * @see HOSHANA_RABBA
     */
    val isHoshanaRabba: Boolean
        get() = yomTovIndex == HOSHANA_RABBA

    /**
     * Returns true if the current day is *Shmini Atzeres*.
     *
     * @return true if the current day is *Shmini Atzeres*.
     * @see isYomTov
     * @see SHEMINI_ATZERES
     */
    val isShminiAtzeres: Boolean
        get() =
            yomTovIndex == SHEMINI_ATZERES//if in Israel, Holiday index of SIMCHAS_TORAH will not be returned by yomTovIndex

    /**
     * Returns true if the current day is *Simchas Torah*. This will always return false if [in Israel][.getInIsrael]
     *
     * @return true if the current day is *Shmini Atzeres*.
     * @see isYomTov
     * @see SIMCHAS_TORAH
     */
    val isSimchasTorah: Boolean
        get() = //if in Israel, Holiday index of SIMCHAS_TORAH will not be returned by yomTovIndex
            yomTovIndex == SIMCHAS_TORAH

    /**
     * Returns true if the current day is *Chol Hamoed* of *Succos*.
     *
     * @return true if the current day is *Chol Hamoed* of *Succos*
     * @see isYomTov
     * @see CHOL_HAMOED_SUCCOS
     */
    val isCholHamoedSuccos: Boolean
        get() = yomTovIndex.let {
            it == CHOL_HAMOED_SUCCOS || it == HOSHANA_RABBA
        }

    /**
     * Returns true if the current day is *Chol Hamoed* of *Pesach* or *Succos*.
     *
     * @return true if the current day is *Chol Hamoed* of *Pesach* or *Succos*
     * @see isYomTov
     * @see CHOL_HAMOED_PESACH
     *
     * @see CHOL_HAMOED_SUCCOS
     */
    val isCholHamoed: Boolean
        get() = isCholHamoedPesach || isCholHamoedSuccos

    /**
     * Returns true if the current day is *Erev Yom Tov*. The method returns true for *Erev* - *Pesach*
     * (first and last days), *Shavuos*, *Rosh Hashana*, *Yom Kippur*, *Succos* and *Hoshana
     * Rabba*.
     *
     * @return true if the current day is *Erev* - *Pesach*, *Shavuos*, *Rosh Hashana*, *Yom
     * Kippur*, *Succos* and *Hoshana Rabba*.
     * @see isYomTov
     * @see isErevYomTovSheni
     */
    val isErevYomTov: Boolean
        get() = yomTovIndex.let {
            it == EREV_PESACH ||
                    it == EREV_SHAVUOS ||
                    it == EREV_ROSH_HASHANA ||
                    it == EREV_YOM_KIPPUR ||
                    it == EREV_SUCCOS ||
                    it == HOSHANA_RABBA ||
                    it == CHOL_HAMOED_PESACH && jewishDayOfMonth == 20
        }// Erev Rosh Hashana is not Erev Rosh Chodesh.
    val isErevPesach: Boolean get() = jewishMonth == HebrewMonth.NISSAN && yomTovIndex == EREV_PESACH //include "2nd days"? (i.e. holiday == CHOL_HAMOED_PESACH and jewishDayOfMonth == 20). Not sure people call that "erev pesach", but thye might call it "erev yom tov" because it is assur bemelacha
    val isErevSuccos: Boolean get() = jewishMonth == HebrewMonth.TISHREI && yomTovIndex == EREV_SUCCOS
    val isErevSheminiAtzeres: Boolean get() = jewishMonth == HebrewMonth.TISHREI && yomTovIndex == HOSHANA_RABBA
    val isErevShavuos: Boolean get() = jewishMonth == HebrewMonth.SIVAN && yomTovIndex == EREV_SHAVUOS
    val isErevRoshHashana: Boolean get() = jewishMonth == HebrewMonth.ELUL && yomTovIndex == EREV_ROSH_HASHANA
    val isErevYomKippur: Boolean get() = jewishMonth == HebrewMonth.TISHREI && yomTovIndex == EREV_YOM_KIPPUR

    /**
     * Returns true if the current day is *Erev Rosh Chodesh*. Returns false for *Erev Rosh Hashana*.
     *
     * @return true if the current day is *Erev Rosh Chodesh*. Returns false for *Erev Rosh Hashana*.
     * @see isRoshChodesh
     */
    val isErevRoshChodesh: Boolean
        get() = // Erev Rosh Hashana is not Erev Rosh Chodesh.
            jewishDayOfMonth == 29 && hebrewLocalDate.month != HebrewMonth.ELUL

    /**
     * Returns true if the current day is *Yom Kippur Katan*. Returns false for *Erev Rosh Hashana*,
     * *Erev Rosh Chodesh Cheshvan*, *Teves* and *Iyyar*. If *Erev Rosh Chodesh* occurs
     * on a Friday or *Shabbos*, *Yom Kippur Katan* is moved back to Thursday.
     *
     * @return true if the current day is *Erev Rosh Chodesh*. Returns false for *Erev Rosh Hashana*.
     * @see isRoshChodesh
     */
    val isYomKippurKatan: Boolean
        get() {
            val dayOfWeek = gregorianLocalDate.dayOfWeek
            val month = hebrewLocalDate.month
            val day = jewishDayOfMonth
            return when {
                month == HebrewMonth.ELUL || month == HebrewMonth.TISHREI || month == HebrewMonth.KISLEV || month == HebrewMonth.NISSAN -> false
                day == 29 && dayOfWeek != DayOfWeek.FRIDAY && dayOfWeek != DayOfWeek.SATURDAY -> true
                day in 27..28 && dayOfWeek == DayOfWeek.THURSDAY -> true
                else -> false
            }
        }

    /**
     * The Monday, Thursday and Monday after the first *Shabbos* after [*Rosh Chodesh*][isRoshChodesh]
     * [*Cheshvan*][HebrewMonth.CHESHVAN] and [*Iyar*][HebrewMonth.IYAR] are [*BeHaB*](https://outorah.org/p/41334/) days.
     * If the last Monday of Iyar's BeHaB coincides with [PESACH_SHENI], the method currently considers it both *Pesach Sheni* and *BeHaB*.
     * As seen in an Ohr Sameach  article on the subject [The unknown Days: BeHaB Vs. Pesach Sheini?](https://ohr.edu/this_week/insights_into_halacha/9340)
     * there are some customs that delay the day to various points in the future.
     * @return true if the day is *BeHaB*.
     */
    val isBeHaB: Boolean
        get() {
            val dayOfWeek = gregorianLocalDate.dayOfWeek
            val month = hebrewLocalDate.month
            val day = jewishDayOfMonth
            return (month == HebrewMonth.CHESHVAN || month == HebrewMonth.IYAR) &&
                    (
                            dayOfWeek == DayOfWeek.MONDAY && day in 5..17/*4 < day < 18*/ ||
                                    dayOfWeek == DayOfWeek.THURSDAY && day in 8..13/*7 < day < 14*/
                            )
        }

    /**
     * Return true if the day is a Taanis (fast day). Return true for *17 of Tammuz*, *Tisha B'Av*,
     * *Yom Kippur*, *Fast of Gedalyah*, *10 of Teves* and the *Fast of Esther*.
     *
     * @return true if today is a fast day
     */
    val isTaanis: Boolean
        get() = yomTovIndex.let {
            it == SEVENTEEN_OF_TAMMUZ ||
                    it == TISHA_BEAV ||
                    it == YOM_KIPPUR ||
                    it == FAST_OF_GEDALYAH ||
                    it == TENTH_OF_TEVES ||
                    it == FAST_OF_ESTHER
        }// on 14 Nissan unless that is Shabbos where the fast is moved back to Thursday


    /**
     * Return true if the day is *Taanis Bechoros* (on *Erev Pesach*). It will return true for the 14th
     * of *Nissan* if it is not on *Shabbos*, or if the 12th of *Nissan* occurs on a Thursday.
     *
     * @return true if today is *Taanis Bechoros*.
     */
    val isTaanisBechoros: Boolean
        get() {
            val day = jewishDayOfMonth
            val dayOfWeek = gregorianLocalDate.dayOfWeek
            // on 14 Nissan unless that is Shabbos where the fast is moved back to Thursday
            return hebrewLocalDate.month == HebrewMonth.NISSAN && (
                    (day == 14 && dayOfWeek != DayOfWeek.SATURDAY) ||
                            (day == 12 && dayOfWeek == DayOfWeek.THURSDAY)
                    )
        }// teves

    /**
     * Returns the day of *Chanukah* or -1 if it is not *Chanukah*.
     *
     * @return the day of *Chanukah* or -1 if it is not *Chanukah*.
     * @see isChanukah
     */
    val dayOfChanukah: Int
        get() =
            if (isChanukah)
                if (hebrewLocalDate.month == HebrewMonth.KISLEV) jewishDayOfMonth - 24
                else /*teves*/ if (isKislevShort) jewishDayOfMonth + 5 else jewishDayOfMonth + 6
            else -1


    /**
     * Returns true if the current day is one of the 8 days of *Chanukah*.
     *
     * @return if the current day is one of the 8 days of *Chanukah*.
     *
     * @see dayOfChanukah
     */
    val isChanukah: Boolean
        get() = yomTovIndex == CHANUKAH

    /**
     * Returns if the day is Purim ([Shushan Purim](https://en.wikipedia.org/wiki/Purim#Shushan_Purim)
     * in a mukaf choma and regular Purim in a non-mukaf choma).
     * @return if the day is Purim (Shushan Purim in a mukaf choma and regular Purin in a non-mukaf choma)
     *
     * @see isMukafChoma
     */
    val isPurim: Boolean
        get() =
            if (isMukafChoma) yomTovIndex == SHUSHAN_PURIM
            else yomTovIndex == PURIM

    /**
     * Returns if the day is Rosh Chodesh. Rosh Hashana will return false
     *
     * @return true if it is Rosh Chodesh. Rosh Hashana will return false
     */
    val isRoshChodesh: Boolean
        get() = // Rosh Hashana is not rosh chodesh. Elul never has 30 days
            (jewishDayOfMonth == 1 && hebrewLocalDate.month != HebrewMonth.TISHREI) || jewishDayOfMonth == 30

    /**
     * Returns if the day is *Shabbos* and Sunday is *Rosh Chodesh* and the *haftorah* of Machar Chodesh is read.
     *
     * @return true if it is *Shabbos* and Sunday is *Rosh Chodesh* and the *haftorah* of Machar Chodesh is read.
     * @todo There is more to tweak in this method (it does not cover all cases and opinions), and it may be removed.
     */
    val isMacharChodesh: Boolean
        get() = gregorianLocalDate.dayOfWeek == DayOfWeek.SATURDAY && (jewishDayOfMonth == 30 || jewishDayOfMonth == 29)

    /**
     * Returns if the day is *Shabbos Mevorchim*.
     *
     * @return true if it is *Shabbos Mevorchim*.
     */
    val isShabbosMevorchim: Boolean
        get() = gregorianLocalDate.dayOfWeek == DayOfWeek.SATURDAY &&
                jewishDayOfMonth >= 23 &&
                jewishDayOfMonth <= 29 &&
                hebrewLocalDate.month != HebrewMonth.ELUL
    // if Sivan and before Shavuos// if Iyar// not a day of the Omer

    // if Nissan and second day of Pesach and on
    /**
     * Returns the int value of the *Omer* day or -1 if the day is not in the *Omer*.
     *
     * @return The *Omer* count as an int or -1 if it is not a day of the *Omer*.
     */
    val dayOfOmer: Int
        get() {
            val month = hebrewLocalDate.month
            val day = jewishDayOfMonth

            // if Nissan and second day of Pesach and on
            return if (month == HebrewMonth.NISSAN && day >= 16) day - 15
            else if (month == HebrewMonth.IYAR) day + 15             // if Iyar
            else if (month == HebrewMonth.SIVAN && day < 6) day + 44 // if Sivan and before Shavuos
            else -1 // not a day of the Omer
        }

    /**
     * Returns if the day is Tisha Be'Av (the 9th of Av).
     * @return if the day is Tisha Be'Av (the 9th of Av).
     */
    val isTishaBav: Boolean
        get() = yomTovIndex == TISHA_BEAV
    // Har Habayis longitude

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
    val moladAsInstant: Instant
        get() {
            val molad = super.molad
            val locationName = "Jerusalem, Israel"
            val latitude = 31.778 // Har Habayis latitude
            val longitude = 35.2354 // Har Habayis longitude

            // The raw molad Instant (point in time) must be generated using standard time. Using "Asia/Jerusalem" timezone will result in the time
            // being incorrectly off by an hour in the summer due to DST. Proper adjustment for the actual time in DST will be done by the date
            // formatter class used to display the Instant.
            val yerushalayimStandardTZ = TimeZone.of("Etc/GMT+2")
            val geo = GeoLocation(locationName, latitude, longitude, yerushalayimStandardTZ)
            val localDate = molad.gregorianLocalDate
            val moladSeconds = molad.moladChalakim * 10 / 3.0
            return LocalDateTime(
                localDate,
                LocalTime(
                    molad.moladHours,
                    molad.moladMinutes,
                    moladSeconds.toInt(),
                    (1000 * (moladSeconds - moladSeconds.toInt())).toInt().milliseconds.inWholeNanoseconds.toInt()
                )
            )
                .toInstant(yerushalayimStandardTZ)
                .plus((-1 * geo.localMeanTimeOffset.toInt()).milliseconds) // subtract local time difference of 20.94 minutes (20 minutes and 56.496 seconds) to get to Standard time
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
    val tchilasZmanKidushLevana3Days: Instant
        get() = moladAsInstant + 3.days// 3 days after the molad

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
    val tchilasZmanKidushLevana7Days: Instant
        get() = moladAsInstant + 7.days //7 days after the molad

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
     * @see sofZmanKidushLevana15Days
     * @see com.kosherjava.zmanim.ComplexZmanimCalendar.getSofZmanKidushLevanaBetweenMoldos
     * @see com.kosherjava.zmanim.ComplexZmanimCalendar.getSofZmanKidushLevanaBetweenMoldos
     */
    val sofZmanKidushLevanaBetweenMoldos: Instant
        /* add half the time between molad and molad (half of 29 days, 12 hours and 793 chalakim (44 minutes, 3.3
        * seconds), or 14 days, 18 hours, 22 minutes and 666 milliseconds). Add it as hours, not days, to avoid
        * DST/ST crossover issues.
        */
        get() = moladAsInstant + 14.days + 18.hours + 22.minutes + 1.seconds + 666.milliseconds

    /**
     * Returns the latest time of *Kiddush Levana* calculated as 15 days after the *molad.* This is the
     * opinion brought down in the Shulchan Aruch (Orach Chaim 426). It should be noted that some opinions hold that
     * the [Rema](http://en.wikipedia.org/wiki/Moses_Isserles) who brings down the the [Maharil's](http://en.wikipedia.org/wiki/Yaakov_ben_Moshe_Levi_Moelin) opinion of calculating it as
     * [half way between *molad* and *molad*][sofZmanKidushLevanaBetweenMoldos] is of the
     * opinion of the Mechaber as well. Also see the Aruch Hashulchan. For additional details on the subject, See Rabbi
     * Dovid Heber's very detailed writeup in Siman Daled (chapter 4) of [Shaarei Zmanim](http://www.worldcat.org/oclc/461326125). This method returns the time even if it is during
     * the day when *Kiddush Levana* can't be said. Callers of this method should consider displaying *alos*
     * before this time if the *zman* is between *alos* and *tzais*.
     *
     * @return the Date representing the moment 15 days after the *molad*.
     * @see sofZmanKidushLevanaBetweenMoldos
     * @see com.kosherjava.zmanim.ComplexZmanimCalendar.getSofZmanKidushLevana15Days
     * @see com.kosherjava.zmanim.ComplexZmanimCalendar.getSofZmanKidushLevana15Days
     */
    val sofZmanKidushLevana15Days: Instant
        get() = moladAsInstant + 15.days //15 days after the molad. Must be added as hours, not days, to avoid DST/ST crossover issues. However, as of kotlinx-datetime version 0.4.0, it converts it to seconds/nanoseconds, so it should be fine.

    /**
     * Returns the *Daf Yomi (Bavli)* for the date that the LocalDate is set to. See the
     * [HebrewDateFormatter.formatDafYomiBavli] for the ability to format the *daf* in
     * Hebrew or transliterated *masechta* names.
     *
     * @return the daf as a [Daf]
     */
    val dafYomiBavli: Daf?
        get() = YomiCalculator.getDafYomiBavli(this)

    /**
     * Returns the *Daf Yomi (Yerushalmi)* for the date that the LocalDate is set to. See the
     * [HebrewDateFormatter.formatDafYomiYerushalmi] for the ability to format the *daf*
     * in Hebrew or transliterated *masechta* names.
     *
     * @return the daf as a [Daf] or null if the date is on Tisha B'Av or Yom Kippur.
     */
    val dafYomiYerushalmi: Daf?
        get() =
            YerushalmiYomiCalculator.getDafYomiYerushalmi(this)// Days since Rosh Hashana year 1. Add 1/2 day as the first tekufas tishrei was 9 hours into the day. This allows all
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
     * @see isVeseinTalUmatarStartDate
     * @see isVeseinTalUmatarStartingTonight
     * @see isVeseinTalUmatarRecited
     */
    val tekufasTishreiElapsedDays: Int
        get() {
            // Days since Rosh Hashana year 1. Add 1/2 day as the first tekufas tishrei was 9 hours into the day. This allows all
            // 4 years of the secular leap year cycle to share 47 days. Truncate 47D and 9H to 47D for simplicity.
            val days: Double =
                getJewishCalendarElapsedDays(hebrewLocalDate.year) + (daysSinceStartOfJewishYear - 1) + 0.5
            // days of completed solar years
            val solar: Double = (hebrewLocalDate.year - 1) * 365.25
            return floor(days - solar).toInt()
        }// When starting on Sunday, it can be the start date or delayed from Shabbos
    // keep the compiler happy
//Not recited on Friday night// The 7th Cheshvan can't occur on Shabbos, so always return true for 7 Cheshvan

    /**
     * Returns true if the current day is *Isru Chag*. The method returns true for the day following *Pesach*
     * *Shavuos* and *Succos*. It utilizes {@see #inIsrael} to return the proper date.
     *
     * @return true if the current day is *Isru Chag*. The method returns true for the day following *Pesach*
     * *Shavuos* and *Succos*. It utilizes {@see #inIsrael} to return the proper date.
     */
    val isIsruChag: Boolean
        get() = yomTovIndex == ISRU_CHAG

    /**
     * Indicates whether some other object is "equal to" this one.
     * @see Object.equals
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is JewishCalendar) return false
        return gregorianLocalDate == other.gregorianLocalDate && inIsrael == other.inIsrael
    }

    /**
     * Overrides [Object.hashCode].
     * @see Object.hashCode
     */
    override fun hashCode(): Int {
        var result = 17
        result =
            37 * result + this::class.hashCode() // needed or this and subclasses will return identical hash
        result += (37 * result) + gregorianLocalDate.hashCode() + (if (inIsrael) 1 else 3)
        return result
    }

    fun copy(
        jewishYear: Long = this.hebrewLocalDate.year,
        jewishMonth: Int = this.hebrewLocalDate.month.value,
        jewishDayOfMonth: Int = this.jewishDayOfMonth,
        inIsrael: Boolean = this.inIsrael,
    ): JewishCalendar = JewishCalendar(jewishYear, jewishMonth, jewishDayOfMonth, inIsrael)

    fun copy(
        jewishYear: Long = this.hebrewLocalDate.year,
        jewishMonth: HebrewMonth = this.hebrewLocalDate.month,
        jewishDayOfMonth: Int = this.jewishDayOfMonth,
        inIsrael: Boolean = this.inIsrael,
    ): JewishCalendar = JewishCalendar(jewishYear, jewishMonth, jewishDayOfMonth, inIsrael)

    companion object {
        /** value returned by [yomTovIndex] to indicate no holiday. */
        const val NO_HOLIDAY = -1

        /** The 14th day of Nissan, the day before of Pesach (Passover). */
        const val EREV_PESACH = 0

        /** The holiday of Pesach (Passover) on the 15th (and 16th out of Israel) day of Nissan. */
        const val PESACH = 1

        /** Chol Hamoed (interim days) of Pesach (Passover) */
        const val CHOL_HAMOED_PESACH = 2

        /**Pesach Sheni, the 14th day of Iyar, a minor holiday. */
        const val PESACH_SHENI = 3

        /**Erev Shavuos (the day before Shavuos), the 5th of Sivan */
        const val EREV_SHAVUOS = 4

        /**Shavuos (Pentecost), the 6th of Sivan */
        const val SHAVUOS = 5

        /** The fast of the 17th day of Tamuz */
        const val SEVENTEEN_OF_TAMMUZ = 6

        /** The fast of the 9th of Av */
        const val TISHA_BEAV = 7

        /** The 15th day of Av, a minor holiday */
        const val TU_BEAV = 8

        /**Erev Rosh Hashana (the day before Rosh Hashana), the 29th of Elul */
        const val EREV_ROSH_HASHANA = 9

        /** Rosh Hashana, the first of Tishrei. */
        const val ROSH_HASHANA = 10

        /** The fast of Gedalyah, the 3rd of Tishrei. */
        const val FAST_OF_GEDALYAH = 11

        /** The 9th day of Tishrei, the day before of Yom Kippur. */
        const val EREV_YOM_KIPPUR = 12

        /** The holiday of Yom Kippur, the 10th day of Tishrei */
        const val YOM_KIPPUR = 13

        /** The 14th day of Tishrei, the day before of Succos/Sukkos (Tabernacles). */
        const val EREV_SUCCOS = 14

        /** The holiday of Succos/Sukkos (Tabernacles), the 15th (and 16th out of Israel) day of Tishrei  */
        const val SUCCOS = 15

        /** Chol Hamoed (interim days) of Succos/Sukkos (Tabernacles) */
        const val CHOL_HAMOED_SUCCOS = 16

        /** Hoshana Rabba, the 7th day of Succos/Sukkos that occurs on the 21st of Tishrei.  */
        const val HOSHANA_RABBA = 17

        /** Shmini Atzeres, the 8th day of Succos/Sukkos is an independent holiday that occurs on the 22nd of Tishrei.  */
        const val SHEMINI_ATZERES = 18

        /** Simchas Torah, the 9th day of Succos/Sukkos, or the second day of Shmini Atzeres that is celebrated
         * [out of Israel][.getInIsrael] on the 23rd of Tishrei.
         */
        const val SIMCHAS_TORAH = 19
        // public static final int EREV_CHANUKAH = 20;// probably remove this
        /** The holiday of Chanukah. 8 days starting on the 25th day Kislev. */
        const val CHANUKAH = 21

        /** The fast of the 10th day of Teves. */
        const val TENTH_OF_TEVES = 22

        /** Tu Bishvat on the 15th day of Shevat, a minor holiday. */
        const val TU_BESHVAT = 23

        /** The fast of Esther, usually on the 13th day of Adar (or Adar II on leap years). It is earlier on some years. */
        const val FAST_OF_ESTHER = 24

        /** The holiday of Purim on the 14th day of Adar (or Adar II on leap years). */
        const val PURIM = 25

        /** The holiday of Shushan Purim on the 15th day of Adar (or Adar II on leap years). */
        const val SHUSHAN_PURIM = 26

        /** The holiday of Purim Katan on the 14th day of Adar I on a leap year when Purim is on Adar II, a minor holiday. */
        const val PURIM_KATAN = 27

        /**
         * Rosh Chodesh, the new moon on the first day of the Jewish month, and the 30th day of the previous month in the
         * case of a month with 30 days.
         */
        const val ROSH_CHODESH = 28

        /** Yom HaShoah, Holocaust Remembrance Day, usually held on the 27th of Nissan. If it falls on a Friday, it is moved
         * to the 26th, and if it falls on a Sunday it is moved to the 28th. A [modern holiday][.isUseModernHolidays].
         */
        const val YOM_HASHOAH = 29

        /**
         * Yom HaZikaron, Israeli Memorial Day, held a day before Yom Ha'atzmaut.  A [modern holiday][.isUseModernHolidays].
         */
        const val YOM_HAZIKARON = 30

        /** Yom Ha'atzmaut, Israel Independence Day, the 5th of Iyar, but if it occurs on a Friday or Saturday, the holiday is
         * moved back to Thursday, the 3rd of 4th of Iyar, and if it falls on a Monday, it is moved forward to Tuesday the
         * 6th of Iyar.  A [modern holiday][.isUseModernHolidays]. */
        const val YOM_HAATZMAUT = 31

        /**
         * Yom Yerushalayim or Jerusalem Day, on 28 Iyar. A [modern holiday][.isUseModernHolidays].
         */
        const val YOM_YERUSHALAYIM = 32

        /** The 33rd day of the Omer, the 18th of Iyar, a minor holiday. */
        const val LAG_BAOMER = 33

        /** The holiday of Purim Katan on the 15th day of Adar I on a leap year when Purim is on Adar II, a minor holiday. */
        const val SHUSHAN_PURIM_KATAN = 34

        /** The day following the last day of Pesach, Shavuos and Sukkos. */
        const val ISRU_CHAG = 35

        /**
         * The day before *Rosh Chodesh* (moved to Thursday if *Rosh Chodesh* is on a Friday or *Shabbos*) in most months.
         * This constant is not actively in use.
         * @see .isYomKippurKatan
         */
        const val YOM_KIPPUR_KATAN = 36

        /**
         * The Monday, Thursday and Monday after the first *Shabbos* after *Rosh Chodesh Cheshvan* and *Iyar*) are BeHab
         * days. This constant is not actively in use.
         * @see .isBeHaB
         */
        const val BEHAB = 37

        /* maybe this will be useful some day...
        val holidays = intArrayOf(
            EREV_PESACH,
            PESACH,
            CHOL_HAMOED_PESACH,
            PESACH_SHENI,
            EREV_SHAVUOS,
            SHAVUOS,
            SEVENTEEN_OF_TAMMUZ,
            TISHA_BEAV,
            TU_BEAV,
            EREV_ROSH_HASHANA,
            ROSH_HASHANA,
            FAST_OF_GEDALYAH,
            EREV_YOM_KIPPUR,
            YOM_KIPPUR,
            EREV_SUCCOS,
            SUCCOS,
            CHOL_HAMOED_SUCCOS,
            HOSHANA_RABBA,
            SHEMINI_ATZERES,
            SIMCHAS_TORAH,
            CHANUKAH,
            TENTH_OF_TEVES,
            TU_BESHVAT,
            FAST_OF_ESTHER,
            PURIM,
            SHUSHAN_PURIM,
            PURIM_KATAN,
            ROSH_CHODESH,
            YOM_HASHOAH,
            YOM_HAZIKARON,
            YOM_HAATZMAUT,
            YOM_YERUSHALAYIM,
            LAG_BAOMER,
            SHUSHAN_PURIM_KATAN,
            ISRU_CHAG,
            YOM_KIPPUR_KATAN,
            BEHAB,
        )*/

        /**
         * An array of *parshiyos* in the 17 possible combinations.
         */
        val parshalist: Array<Array<Parsha>> = arrayOf(
            arrayOf(
                NONE,
                Parsha.VAYEILECH,
                Parsha.HAAZINU,
                NONE,
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
                NONE,
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
                NONE,
                Parsha.VAYEILECH,
                Parsha.HAAZINU,
                NONE,
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
                NONE,
                Parsha.SHMINI,
                Parsha.TAZRIA_METZORA,
                Parsha.ACHREI_MOS_KEDOSHIM,
                Parsha.EMOR,
                Parsha.BEHAR_BECHUKOSAI,
                Parsha.BAMIDBAR,
                NONE,
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
                NONE,
                Parsha.HAAZINU,
                NONE,
                NONE,
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
                NONE,
                NONE,
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
                NONE,
                Parsha.HAAZINU,
                NONE,
                NONE,
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
                NONE,
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
                NONE,
                NONE,
                Parsha.HAAZINU,
                NONE,
                NONE,
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
                NONE,
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
                NONE,
                NONE,
                Parsha.HAAZINU,
                NONE,
                NONE,
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
                NONE,
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
                NONE,
                Parsha.VAYEILECH,
                Parsha.HAAZINU,
                NONE,
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
                NONE,
                Parsha.ACHREI_MOS,
                Parsha.KEDOSHIM,
                Parsha.EMOR,
                Parsha.BEHAR,
                Parsha.BECHUKOSAI,
                Parsha.BAMIDBAR,
                NONE,
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
                NONE,
                Parsha.VAYEILECH,
                Parsha.HAAZINU,
                NONE,
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
                NONE,
                NONE,
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
                NONE,
                Parsha.HAAZINU,
                NONE,
                NONE,
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
                NONE,
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
                NONE,
                Parsha.HAAZINU,
                NONE,
                NONE,
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
                NONE,
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
                NONE,
                NONE,
                Parsha.HAAZINU,
                NONE,
                NONE,
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
                NONE,
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
                NONE,
                NONE,
                Parsha.HAAZINU,
                NONE,
                NONE,
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
                NONE,
                Parsha.ACHREI_MOS,
                Parsha.KEDOSHIM,
                Parsha.EMOR,
                Parsha.BEHAR,
                Parsha.BECHUKOSAI,
                Parsha.BAMIDBAR,
                NONE,
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
                NONE,
                Parsha.VAYEILECH,
                Parsha.HAAZINU,
                NONE,
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
                NONE,
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
                NONE,
                Parsha.HAAZINU,
                NONE,
                NONE,
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
                NONE,
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
                NONE,
                Parsha.VAYEILECH,
                Parsha.HAAZINU,
                NONE,
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
                NONE,
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
                NONE,
                Parsha.VAYEILECH,
                Parsha.HAAZINU,
                NONE,
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
                NONE,
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
                NONE,
                NONE,
                Parsha.HAAZINU,
                NONE,
                NONE,
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
                NONE,
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

        /**
         * Value of the [.DAY_OF_WEEK] field indicating
         * Sunday.
         */
        const val SUNDAY = 1

        /**
         * Value of the [.DAY_OF_WEEK] field indicating
         * Monday.
         */
        const val MONDAY = 2

        /**
         * Value of the [.DAY_OF_WEEK] field indicating
         * Tuesday.
         */
        const val TUESDAY = 3

        /**
         * Value of the [.DAY_OF_WEEK] field indicating
         * Wednesday.
         */
        const val WEDNESDAY = 4

        /**
         * Value of the [.DAY_OF_WEEK] field indicating
         * Thursday.
         */
        const val THURSDAY = 5

        /**
         * Value of the [.DAY_OF_WEEK] field indicating
         * Friday.
         */
        const val FRIDAY = 6

        /**
         * Value of the [.DAY_OF_WEEK] field indicating
         * Saturday.
         */
        const val SATURDAY = 7


        //TODO should this be the standard instead of using DayOfWeek?
        /**
         * [kotlinx.datetime.DayOfWeek] is ordered according to ISO-8601, where Monday is 1 and Sunday is 7.
         * [java.util.Calendar]'s day of week constants are ordered with Sunday is 1 and Saturday is 7.
         * This function converts between the two.
         * It is called [toJewishDayOfWeek] because the Torah describes Saturday as the 7th day.
         * **Note:** The caller should prefer to compare [DayOfWeek]s directly, instead of converting
         * to day numbers. This is to improve readability and possible future-proofing.
         * */
        fun DayOfWeek.toJewishDayOfWeek(): Int = when (this@toJewishDayOfWeek) {
            DayOfWeek.SUNDAY -> SUNDAY
            DayOfWeek.MONDAY -> MONDAY
            DayOfWeek.TUESDAY -> TUESDAY
            DayOfWeek.WEDNESDAY -> WEDNESDAY
            DayOfWeek.THURSDAY -> THURSDAY
            DayOfWeek.FRIDAY -> FRIDAY
            DayOfWeek.SATURDAY -> SATURDAY
            else -> throw IllegalArgumentException("Day of week must be SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, or SATURDAY")
        }
    }
}