/*
 * Zmanim Java API
 * Copyright (C) 2011 - 2023 Eliyahu Hershfeld
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

import java.lang.IllegalArgumentException
import java.lang.StringBuilder
import java.text.SimpleDateFormat
import com.kosherjava.zmanim.hebrewcalendar.JewishCalendar.Parsha
import java.util.EnumMap

/**
 * The HebrewDateFormatter class formats a [JewishDate].
 *
 * The class formats Jewish dates, numbers, *Daf Yomi* (*Bavli* and *Yerushalmi*), the *Omer*,
 * *Parshas Hashavua* (including the special *parshiyos* of *Shekalim*, *Zachor*, *Parah*
 * and *Hachodesh*), Yomim Tovim and the Molad (experimental) in Hebrew or Latin chars, and has various settings.
 * Sample full date output includes (using various options):
 *
 *  * 21 Shevat, 5729
 *  * &#x5DB;&#x5D0; &#x5E9;&#x5D1;&#x5D8; &#x5EA;&#x5E9;&#x5DB;&#x5D8;
 *  * &#x5DB;&#x5F4;&#x5D0; &#x5E9;&#x5D1;&#x5D8; &#x5D4;&#x5F3;&#x5EA;&#x5E9;&#x5DB;&#x5F4;&#x5D8;
 *  * &#x5DB;&#x5F4;&#x5D0; &#x5E9;&#x5D1;&#x5D8; &#x5EA;&#x5E9;&#x5F4;&#x05E4; or
 * &#x5DB;&#x5F4;&#x5D0; &#x5E9;&#x5D1;&#x5D8; &#x5EA;&#x5E9;&#x5F4;&#x05E3;
 *  * &#x05DB;&#x05F3; &#x05E9;&#x05D1;&#x05D8; &#x05D5;&#x05F3; &#x05D0;&#x05DC;&#x05E4;&#x05D9;&#x05DD;
 *
 *
 * @see JewishDate
 *
 * @see JewishCalendar
 *
 *
 * @author  Eliyahu Hershfeld 2011 - 2023
 */
class HebrewDateFormatter constructor() {
    /**
     * Returns if the formatter is set to use Hebrew formatting in the various formatting methods.
     *
     * @return the hebrewFormat
     * @see .setHebrewFormat
     * @see .format
     * @see .formatDayOfWeek
     * @see .formatMonth
     * @see .formatOmer
     * @see .formatYomTov
     */
    /**
     * Sets the formatter to format in Hebrew in the various formatting methods.
     *
     * @param hebrewFormat
     * the hebrewFormat to set
     * @see .isHebrewFormat
     * @see .format
     * @see .formatDayOfWeek
     * @see .formatMonth
     * @see .formatOmer
     * @see .formatYomTov
     */
    /**
     * See [.isHebrewFormat] and [.setHebrewFormat].
     */
    var isHebrewFormat: Boolean = false
    /**
     * Returns whether the class is set to use the thousands digit when formatting. When formatting a Hebrew Year,
     * traditionally the thousands digit is omitted and output for a year such as 5729 (1969 Gregorian) would be
     * calculated for 729 and format as &#x5EA;&#x5E9;&#x5DB;&#x5F4;&#x5D8;. When set to true the long format year such
     * as &#x5D4;&#x5F3; &#x5EA;&#x5E9;&#x5DB;&#x5F4;&#x5D8; for 5729/1969 is returned.
     *
     * @return true if set to use the thousands digit when formatting Hebrew dates and numbers.
     */
    /**
     * When formatting a Hebrew Year, traditionally the thousands digit is omitted and output for a year such as 5729
     * (1969 Gregorian) would be calculated for 729 and format as &#x5EA;&#x5E9;&#x5DB;&#x5F4;&#x5D8;. This method
     * allows setting this to true to return the long format year such as &#x5D4;&#x5F3;
     * &#x5EA;&#x5E9;&#x5DB;&#x5F4;&#x5D8; for 5729/1969.
     *
     * @param useLongHebrewYears
     * Set this to true to use the long formatting
     */
    /**
     * See [.isUseLongHebrewYears] and [.setUseLongHebrewYears].
     */
    var isUseLongHebrewYears: Boolean = false
    /**
     * Returns whether the class is set to use the Geresh &#x5F3; and Gershayim &#x5F4; in formatting Hebrew dates and
     * numbers. When true and output would look like &#x5DB;&#x5F4;&#x5D0; &#x5E9;&#x5D1;&#x5D8; &#x5EA;&#x5E9;&#x5F4;&#x5DB;
     * (or &#x5DB;&#x5F4;&#x5D0; &#x5E9;&#x5D1;&#x5D8; &#x5EA;&#x5E9;&#x5F4;&#x5DA;). When set to false, this output
     * would display as &#x5DB;&#x5D0; &#x5E9;&#x5D1;&#x5D8; &#x5EA;&#x5E9;&#x5DB;.
     *
     * @return true if set to use the Geresh &#x5F3; and Gershayim &#x5F4; in formatting Hebrew dates and numbers.
     */
    /**
     * Sets whether to use the Geresh &#x5F3; and Gershayim &#x5F4; in formatting Hebrew dates and numbers. The default
     * value is true and output would look like &#x5DB;&#x5F4;&#x5D0; &#x5E9;&#x5D1;&#x5D8; &#x5EA;&#x5E9;&#x5F4;&#x5DB;
     * (or &#x5DB;&#x5F4;&#x5D0; &#x5E9;&#x5D1;&#x5D8; &#x5EA;&#x5E9;&#x5F4;&#x5DA;). When set to false, this output would
     * display as &#x5DB;&#x5D0; &#x5E9;&#x5D1;&#x5D8; &#x5EA;&#x5E9;&#x5DB; (or
     * &#x5DB;&#x5D0; &#x5E9;&#x5D1;&#x5D8; &#x5EA;&#x5E9;&#x5DA;). Single digit days or month or years such as &#x05DB;&#x05F3;
     * &#x05E9;&#x05D1;&#x05D8; &#x05D5;&#x05F3; &#x05D0;&#x05DC;&#x05E4;&#x05D9;&#x05DD; show the use of the Geresh.
     *
     * @param useGershGershayim
     * set to false to omit the Geresh &#x5F3; and Gershayim &#x5F4; in formatting
     */
    /**
     * See [.isUseGershGershayim] and [.setUseGershGershayim].
     */
    var isUseGershGershayim: Boolean = true
    /**
     * Returns if the [.formatDayOfWeek] will use the long format such as
     * &#x05E8;&#x05D0;&#x05E9;&#x05D5;&#x05DF; or short such as &#x05D0; when formatting the day of week in
     * [Hebrew][.isHebrewFormat].
     *
     * @return the longWeekFormat
     * @see .setLongWeekFormat
     * @see .formatDayOfWeek
     */
    /**
     * Setting to control if the [.formatDayOfWeek] will use the long format such as
     * &#x05E8;&#x05D0;&#x05E9;&#x05D5;&#x05DF; or short such as &#x05D0; when formatting the day of week in
     * [Hebrew][.isHebrewFormat].
     *
     * @param longWeekFormat
     * the longWeekFormat to set
     */
    /**
     * See [.isLongWeekFormat] and [.setLongWeekFormat].
     */
    var isLongWeekFormat: Boolean = true
        set(longWeekFormat) {
            field = longWeekFormat
            if (longWeekFormat) {
                weekFormat = SimpleDateFormat("EEEE")
            } else {
                weekFormat = SimpleDateFormat("EEE")
            }
        }
    /**
     * Returns whether the class is set to use the &#x05DE;&#x05E0;&#x05E6;&#x05E4;&#x05F4;&#x05DA; letters when
     * formatting years ending in 20, 40, 50, 80 and 90 to produce &#x05EA;&#x05E9;&#x05F4;&#x05E4; if false or
     * or &#x05EA;&#x05E9;&#x05F4;&#x05E3; if true. Traditionally non-final form letters are used, so the year
     * 5780 would be formatted as &#x05EA;&#x05E9;&#x05F4;&#x05E4; if the default false is used here. If this returns
     * true, the format &#x05EA;&#x05E9;&#x05F4;&#x05E3; would be used.
     *
     * @return true if set to use final form letters when formatting Hebrew years. The default value is false.
     */
    /**
     * When formatting a Hebrew Year, traditionally years ending in 20, 40, 50, 80 and 90 are formatted using non-final
     * form letters for example &#x05EA;&#x05E9;&#x05F4;&#x05E4; for the year 5780. Setting this to true (the default
     * is false) will use the final form letters for &#x05DE;&#x05E0;&#x05E6;&#x05E4;&#x05F4;&#x05DA; and will format
     * the year 5780 as &#x05EA;&#x05E9;&#x05F4;&#x05E3;.
     *
     * @param useFinalFormLetters
     * Set this to true to use final form letters when formatting Hebrew years.
     */
    /**
     * See [.isUseFinalFormLetters] and [.setUseFinalFormLetters].
     */
    var isUseFinalFormLetters: Boolean = false

    /**
     * The internal DateFormat.&nbsp; See [.isLongWeekFormat] and [.setLongWeekFormat].
     */
    private var weekFormat: SimpleDateFormat? = null
    /**
     * Retruns the list of transliterated parshiyos used by this formatter.
     *
     * @return the list of transliterated Parshios
     */
    /**
     * Setter method to allow overriding of the default list of parshiyos transliterated into into Latin chars. The
     * default uses Ashkenazi American English transliteration.
     *
     * @param transliteratedParshaMap
     * the transliterated Parshios as an EnumMap to set
     * @see .getTransliteratedParshiosList
     */
    /**
     * List of transliterated parshiyos using the default *Ashkenazi* pronunciation.&nbsp; The formatParsha method
     * uses this for transliterated *parsha* formatting.&nbsp; This list can be overridden (for *Sephardi*
     * English transliteration for example) by setting the [.setTransliteratedParshiosList].&nbsp; The list
     * includes double and special *parshiyos* is set as "*Bereshis, Noach, Lech Lecha, Vayera, Chayei Sara,
     * Toldos, Vayetzei, Vayishlach, Vayeshev, Miketz, Vayigash, Vayechi, Shemos, Vaera, Bo, Beshalach, Yisro, Mishpatim,
     * Terumah, Tetzaveh, Ki Sisa, Vayakhel, Pekudei, Vayikra, Tzav, Shmini, Tazria, Metzora, Achrei Mos, Kedoshim, Emor,
     * Behar, Bechukosai, Bamidbar, Nasso, Beha'aloscha, Sh'lach, Korach, Chukas, Balak, Pinchas, Matos, Masei, Devarim,
     * Vaeschanan, Eikev, Re'eh, Shoftim, Ki Seitzei, Ki Savo, Nitzavim, Vayeilech, Ha'Azinu, Vezos Habracha,
     * Vayakhel Pekudei, Tazria Metzora, Achrei Mos Kedoshim, Behar Bechukosai, Chukas Balak, Matos Masei, Nitzavim Vayeilech,
     * Shekalim, Zachor, Parah, Hachodesh,Shuva, Shira, Hagadol, Chazon, Nachamu*".
     *
     * @see .formatParsha
     */
    var transliteratedParshiosList = EnumMap<Parsha, String>(Parsha::class.java)

    /**
     * Unicode [EnumMap] of Hebrew *parshiyos*.&nbsp; The list includes double and special *parshiyos* and
     * contains `"&#x05D1;&#x05E8;&#x05D0;&#x05E9;&#x05D9;&#x05EA;, &#x05E0;&#x05D7;, &#x05DC;&#x05DA; &#x05DC;&#x05DA;,
     * &#x05D5;&#x05D9;&#x05E8;&#x05D0;, &#x05D7;&#x05D9;&#x05D9; &#x05E9;&#x05E8;&#x05D4;,
     * &#x05EA;&#x05D5;&#x05DC;&#x05D3;&#x05D5;&#x05EA;, &#x05D5;&#x05D9;&#x05E6;&#x05D0;, &#x05D5;&#x05D9;&#x05E9;&#x05DC;&#x05D7;,
     * &#x05D5;&#x05D9;&#x05E9;&#x05D1;, &#x05DE;&#x05E7;&#x05E5;, &#x05D5;&#x05D9;&#x05D2;&#x05E9;, &#x05D5;&#x05D9;&#x05D7;&#x05D9;,
     * &#x05E9;&#x05DE;&#x05D5;&#x05EA;, &#x05D5;&#x05D0;&#x05E8;&#x05D0;, &#x05D1;&#x05D0;, &#x05D1;&#x05E9;&#x05DC;&#x05D7;,
     * &#x05D9;&#x05EA;&#x05E8;&#x05D5;, &#x05DE;&#x05E9;&#x05E4;&#x05D8;&#x05D9;&#x05DD;, &#x05EA;&#x05E8;&#x05D5;&#x05DE;&#x05D4;,
     * &#x05EA;&#x05E6;&#x05D5;&#x05D4;, &#x05DB;&#x05D9; &#x05EA;&#x05E9;&#x05D0;, &#x05D5;&#x05D9;&#x05E7;&#x05D4;&#x05DC;,
     * &#x05E4;&#x05E7;&#x05D5;&#x05D3;&#x05D9;, &#x05D5;&#x05D9;&#x05E7;&#x05E8;&#x05D0;, &#x05E6;&#x05D5;,
     * &#x05E9;&#x05DE;&#x05D9;&#x05E0;&#x05D9;, &#x05EA;&#x05D6;&#x05E8;&#x05D9;&#x05E2;, &#x05DE;&#x05E6;&#x05E8;&#x05E2;,
     * &#x05D0;&#x05D7;&#x05E8;&#x05D9; &#x05DE;&#x05D5;&#x05EA;, &#x05E7;&#x05D3;&#x05D5;&#x05E9;&#x05D9;&#x05DD;,
     * &#x05D0;&#x05DE;&#x05D5;&#x05E8;, &#x05D1;&#x05D4;&#x05E8;, &#x05D1;&#x05D7;&#x05E7;&#x05EA;&#x05D9;,
     * &#x05D1;&#x05DE;&#x05D3;&#x05D1;&#x05E8;, &#x05E0;&#x05E9;&#x05D0;, &#x05D1;&#x05D4;&#x05E2;&#x05DC;&#x05EA;&#x05DA;,
     * &#x05E9;&#x05DC;&#x05D7; &#x05DC;&#x05DA;, &#x05E7;&#x05E8;&#x05D7;, &#x05D7;&#x05D5;&#x05E7;&#x05EA;, &#x05D1;&#x05DC;&#x05E7;,
     * &#x05E4;&#x05D9;&#x05E0;&#x05D7;&#x05E1;, &#x05DE;&#x05D8;&#x05D5;&#x05EA;, &#x05DE;&#x05E1;&#x05E2;&#x05D9;,
     * &#x05D3;&#x05D1;&#x05E8;&#x05D9;&#x05DD;, &#x05D5;&#x05D0;&#x05EA;&#x05D7;&#x05E0;&#x05DF;, &#x05E2;&#x05E7;&#x05D1;,
     * &#x05E8;&#x05D0;&#x05D4;, &#x05E9;&#x05D5;&#x05E4;&#x05D8;&#x05D9;&#x05DD;, &#x05DB;&#x05D9; &#x05EA;&#x05E6;&#x05D0;,
     * &#x05DB;&#x05D9; &#x05EA;&#x05D1;&#x05D5;&#x05D0;, &#x05E0;&#x05E6;&#x05D1;&#x05D9;&#x05DD;, &#x05D5;&#x05D9;&#x05DC;&#x05DA;,
     * &#x05D4;&#x05D0;&#x05D6;&#x05D9;&#x05E0;&#x05D5;, &#x05D5;&#x05D6;&#x05D0;&#x05EA; &#x05D4;&#x05D1;&#x05E8;&#x05DB;&#x05D4;,
     * &#x05D5;&#x05D9;&#x05E7;&#x05D4;&#x05DC; &#x05E4;&#x05E7;&#x05D5;&#x05D3;&#x05D9;, &#x05EA;&#x05D6;&#x05E8;&#x05D9;&#x05E2;
     * &#x05DE;&#x05E6;&#x05E8;&#x05E2;, &#x05D0;&#x05D7;&#x05E8;&#x05D9; &#x05DE;&#x05D5;&#x05EA;
     * &#x05E7;&#x05D3;&#x05D5;&#x05E9;&#x05D9;&#x05DD;, &#x05D1;&#x05D4;&#x05E8; &#x05D1;&#x05D7;&#x05E7;&#x05EA;&#x05D9;,
     * &#x05D7;&#x05D5;&#x05E7;&#x05EA; &#x05D1;&#x05DC;&#x05E7;, &#x05DE;&#x05D8;&#x05D5;&#x05EA; &#x05DE;&#x05E1;&#x05E2;&#x05D9;,
     * &#x05E0;&#x05E6;&#x05D1;&#x05D9;&#x05DD; &#x05D5;&#x05D9;&#x05DC;&#x05DA;, &#x05E9;&#x05E7;&#x05DC;&#x05D9;&#x05DD;,
     * &#x05D6;&#x05DB;&#x05D5;&#x05E8;, &#x05E4;&#x05E8;&#x05D4;, &#x05D4;&#x05D7;&#x05D3;&#x05E9;,
     * &#x05E9;&#x05D5;&#x05D1;&#x05D4;,&#x05E9;&#x05D9;&#x05E8;&#x05D4;,&#x05D4;&#x05D2;&#x05D3;&#x05D5;&#x05DC;,
     * &#x05D7;&#x05D6;&#x05D5;&#x05DF;,&#x05E0;&#x05D7;&#x05DE;&#x05D5;"`
     */
    private val hebrewParshaMap = EnumMap(
        mutableMapOf(
            Parsha.NONE to "",
            Parsha.BERESHIS to "\u05D1\u05E8\u05D0\u05E9\u05D9\u05EA",
            Parsha.NOACH to "\u05E0\u05D7",
            Parsha.LECH_LECHA to "\u05DC\u05DA \u05DC\u05DA",
            Parsha.VAYERA to "\u05D5\u05D9\u05E8\u05D0",
            Parsha.CHAYEI_SARA to "\u05D7\u05D9\u05D9 \u05E9\u05E8\u05D4",
            Parsha.TOLDOS to "\u05EA\u05D5\u05DC\u05D3\u05D5\u05EA",
            Parsha.VAYETZEI to "\u05D5\u05D9\u05E6\u05D0",
            Parsha.VAYISHLACH to "\u05D5\u05D9\u05E9\u05DC\u05D7",
            Parsha.VAYESHEV to "\u05D5\u05D9\u05E9\u05D1",
            Parsha.MIKETZ to "\u05DE\u05E7\u05E5",
            Parsha.VAYIGASH to "\u05D5\u05D9\u05D2\u05E9",
            Parsha.VAYECHI to "\u05D5\u05D9\u05D7\u05D9",
            Parsha.SHEMOS to "\u05E9\u05DE\u05D5\u05EA",
            Parsha.VAERA to "\u05D5\u05D0\u05E8\u05D0",
            Parsha.BO to "\u05D1\u05D0",
            Parsha.BESHALACH to "\u05D1\u05E9\u05DC\u05D7",
            Parsha.YISRO to "\u05D9\u05EA\u05E8\u05D5",
            Parsha.MISHPATIM to "\u05DE\u05E9\u05E4\u05D8\u05D9\u05DD",
            Parsha.TERUMAH to "\u05EA\u05E8\u05D5\u05DE\u05D4",
            Parsha.TETZAVEH to "\u05EA\u05E6\u05D5\u05D4",
            Parsha.KI_SISA to "\u05DB\u05D9 \u05EA\u05E9\u05D0",
            Parsha.VAYAKHEL to "\u05D5\u05D9\u05E7\u05D4\u05DC",
            Parsha.PEKUDEI to "\u05E4\u05E7\u05D5\u05D3\u05D9",
            Parsha.VAYIKRA to "\u05D5\u05D9\u05E7\u05E8\u05D0",
            Parsha.TZAV to "\u05E6\u05D5",
            Parsha.SHMINI to "\u05E9\u05DE\u05D9\u05E0\u05D9",
            Parsha.TAZRIA to "\u05EA\u05D6\u05E8\u05D9\u05E2",
            Parsha.METZORA to "\u05DE\u05E6\u05E8\u05E2",
            Parsha.ACHREI_MOS to "\u05D0\u05D7\u05E8\u05D9 \u05DE\u05D5\u05EA",
            Parsha.KEDOSHIM to "\u05E7\u05D3\u05D5\u05E9\u05D9\u05DD",
            Parsha.EMOR to "\u05D0\u05DE\u05D5\u05E8",
            Parsha.BEHAR to "\u05D1\u05D4\u05E8",
            Parsha.BECHUKOSAI to "\u05D1\u05D7\u05E7\u05EA\u05D9",
            Parsha.BAMIDBAR to "\u05D1\u05DE\u05D3\u05D1\u05E8",
            Parsha.NASSO to "\u05E0\u05E9\u05D0",
            Parsha.BEHAALOSCHA to "\u05D1\u05D4\u05E2\u05DC\u05EA\u05DA",
            Parsha.SHLACH to "\u05E9\u05DC\u05D7 \u05DC\u05DA",
            Parsha.KORACH to "\u05E7\u05E8\u05D7",
            Parsha.CHUKAS to "\u05D7\u05D5\u05E7\u05EA",
            Parsha.BALAK to "\u05D1\u05DC\u05E7",
            Parsha.PINCHAS to "\u05E4\u05D9\u05E0\u05D7\u05E1",
            Parsha.MATOS to "\u05DE\u05D8\u05D5\u05EA",
            Parsha.MASEI to "\u05DE\u05E1\u05E2\u05D9",
            Parsha.DEVARIM to "\u05D3\u05D1\u05E8\u05D9\u05DD",
            Parsha.VAESCHANAN to "\u05D5\u05D0\u05EA\u05D7\u05E0\u05DF",
            Parsha.EIKEV to "\u05E2\u05E7\u05D1",
            Parsha.REEH to "\u05E8\u05D0\u05D4",
            Parsha.SHOFTIM to "\u05E9\u05D5\u05E4\u05D8\u05D9\u05DD",
            Parsha.KI_SEITZEI to "\u05DB\u05D9 \u05EA\u05E6\u05D0",
            Parsha.KI_SAVO to "\u05DB\u05D9 \u05EA\u05D1\u05D5\u05D0",
            Parsha.NITZAVIM to "\u05E0\u05E6\u05D1\u05D9\u05DD",
            Parsha.VAYEILECH to "\u05D5\u05D9\u05DC\u05DA",
            Parsha.HAAZINU to "\u05D4\u05D0\u05D6\u05D9\u05E0\u05D5",
            Parsha.VZOS_HABERACHA to "\u05D5\u05D6\u05D0\u05EA \u05D4\u05D1\u05E8\u05DB\u05D4 ",
            Parsha.VAYAKHEL_PEKUDEI to "\u05D5\u05D9\u05E7\u05D4\u05DC \u05E4\u05E7\u05D5\u05D3\u05D9",
            Parsha.TAZRIA_METZORA to "\u05EA\u05D6\u05E8\u05D9\u05E2 \u05DE\u05E6\u05E8\u05E2",
            Parsha.ACHREI_MOS_KEDOSHIM to "\u05D0\u05D7\u05E8\u05D9 \u05DE\u05D5\u05EA \u05E7\u05D3\u05D5\u05E9\u05D9\u05DD",
            Parsha.BEHAR_BECHUKOSAI to "\u05D1\u05D4\u05E8 \u05D1\u05D7\u05E7\u05EA\u05D9",
            Parsha.CHUKAS_BALAK to "\u05D7\u05D5\u05E7\u05EA \u05D1\u05DC\u05E7",
            Parsha.MATOS_MASEI to "\u05DE\u05D8\u05D5\u05EA \u05DE\u05E1\u05E2\u05D9",
            Parsha.NITZAVIM_VAYEILECH to "\u05E0\u05E6\u05D1\u05D9\u05DD \u05D5\u05D9\u05DC\u05DA",
            Parsha.SHKALIM to "\u05E9\u05E7\u05DC\u05D9\u05DD",
            Parsha.ZACHOR to "\u05D6\u05DB\u05D5\u05E8",
            Parsha.PARA to "\u05E4\u05E8\u05D4",
            Parsha.HACHODESH to "\u05D4\u05D7\u05D3\u05E9",
            Parsha.SHUVA to "\u05E9\u05D5\u05D1\u05D4",
            Parsha.SHIRA to "\u05E9\u05D9\u05E8\u05D4",
            Parsha.HAGADOL to "\u05D4\u05D2\u05D3\u05D5\u05DC",
            Parsha.CHAZON to "\u05D7\u05D6\u05D5\u05DF",
            Parsha.NACHAMU to "\u05E0\u05D7\u05DE\u05D5"
        )
    )
    /**
     * Returns the list of months transliterated into Latin chars. The default list of months uses Ashkenazi
     * pronunciation in typical American English spelling. This list has a length of 14 with 3 variations for Adar -
     * "Adar", "Adar II", "Adar I"
     *
     * @return the list of months beginning in Nissan and ending in in "Adar", "Adar II", "Adar I". The default list is
     * currently ["Nissan", "Iyar", "Sivan", "Tammuz", "Av", "Elul", "Tishrei", "Cheshvan", "Kislev", "Teves",
     * "Shevat", "Adar", "Adar II", "Adar I"].
     * @see .setTransliteratedMonthList
     */
    /**
     * Setter method to allow overriding of the default list of months transliterated into into Latin chars. The default
     * uses Ashkenazi American English transliteration.
     *
     * @param transliteratedMonths
     * an array of 14 month names that defaults to ["Nissan", "Iyar", "Sivan", "Tamuz", "Av", "Elul", "Tishrei",
     * "Heshvan", "Kislev", "Tevet", "Shevat", "Adar", "Adar II", "Adar I"].
     * @see .getTransliteratedMonthList
     */
    /**
     * Transliterated month names.&nbsp; Defaults to ["Nissan", "Iyar", "Sivan", "Tammuz", "Av", "Elul", "Tishrei", "Cheshvan",
     * "Kislev", "Teves", "Shevat", "Adar", "Adar II", "Adar I" ].
     * @see .getTransliteratedMonthList
     * @see .setTransliteratedMonthList
     */
    var transliteratedMonthList: Array<String> = arrayOf(
        "Nissan", "Iyar", "Sivan", "Tammuz", "Av", "Elul", "Tishrei", "Cheshvan",
        "Kislev", "Teves", "Shevat", "Adar", "Adar II", "Adar I"
    )
    /**
     * Returns the Hebrew Omer prefix.&nbsp; By default it is the letter &#x05D1; producing
     * &#x05D1;&#x05E2;&#x05D5;&#x05DE;&#x05E8;, but it can be set to &#x05DC; to produce
     * &#x05DC;&#x05E2;&#x05D5;&#x05DE;&#x05E8; (or any other prefix) using the [.setHebrewOmerPrefix].
     *
     * @return the hebrewOmerPrefix
     *
     * @see .hebrewOmerPrefix
     *
     * @see .setHebrewOmerPrefix
     * @see .formatOmer
     */
    /**
     * Method to set the Hebrew Omer prefix.&nbsp; By default it is the letter &#x05D1; producing
     * &#x05D1;&#x05E2;&#x05D5;&#x05DE;&#x05E8;, but it can be set to &#x05DC; to produce
     * &#x05DC;&#x05E2;&#x05D5;&#x05DE;&#x05E8; (or any other prefix)
     * @param hebrewOmerPrefix
     * the hebrewOmerPrefix to set. You can use the Unicode &#92;u05DC to set it to &#x5DC;.
     * @see .hebrewOmerPrefix
     *
     * @see .getHebrewOmerPrefix
     * @see .formatOmer
     */
    /**
     * The Hebrew omer prefix charachter. It defaults to &#x05D1; producing &#x05D1;&#x05E2;&#x05D5;&#x05DE;&#x05E8;,
     * but can be set to &#x05DC; to produce &#x05DC;&#x05E2;&#x05D5;&#x05DE;&#x05E8; (or any other prefix).
     * @see .getHebrewOmerPrefix
     * @see .setHebrewOmerPrefix
     */
    var hebrewOmerPrefix: String = "\u05D1"
    /**
     * Returns the day of Shabbos transliterated into Latin chars. The default uses Ashkenazi pronunciation "Shabbos".
     * This can be overwritten using the [.setTransliteratedShabbosDayOfWeek]
     *
     * @return the transliteratedShabbos. The default list of months uses Ashkenazi pronunciation "Shabbos".
     * @see .setTransliteratedShabbosDayOfWeek
     * @see .formatDayOfWeek
     */
    /**
     * Setter to override the default transliterated name of "Shabbos" to alternate spelling such as "Shabbat" used by
     * the [.formatDayOfWeek]
     *
     * @param transliteratedShabbos
     * the transliteratedShabbos to set
     *
     * @see .getTransliteratedShabbosDayOfWeek
     * @see .formatDayOfWeek
     */
    /**
     * The default value for formatting Shabbos (Saturday).&nbsp; Defaults to Shabbos.
     * @see .getTransliteratedShabbosDayOfWeek
     * @see .setTransliteratedShabbosDayOfWeek
     */
    var transliteratedShabbosDayOfWeek: String = "Shabbos"
    /**
     * Returns the list of holidays transliterated into Latin chars. This is used by the
     * [.formatYomTov] when formatting the Yom Tov String. The default list of months uses
     * Ashkenazi pronunciation in typical American English spelling.
     *
     * @return the list of transliterated holidays. The default list is currently ["Erev Pesach", "Pesach",
     * "Chol Hamoed Pesach", "Pesach Sheni", "Erev Shavuos", "Shavuos", "Seventeenth of Tammuz", "Tishah B'Av",
     * "Tu B'Av", "Erev Rosh Hashana", "Rosh Hashana", "Fast of Gedalyah", "Erev Yom Kippur", "Yom Kippur",
     * "Erev Succos", "Succos", "Chol Hamoed Succos", "Hoshana Rabbah", "Shemini Atzeres", "Simchas Torah",
     * "Erev Chanukah", "Chanukah", "Tenth of Teves", "Tu B'Shvat", "Fast of Esther", "Purim", "Shushan Purim",
     * "Purim Katan", "Rosh Chodesh", "Yom HaShoah", "Yom Hazikaron", "Yom Ha'atzmaut", "Yom Yerushalayim",
     * "Lag B'Omer","Shushan Purim Katan","Isru Chag"].
     *
     * @see .setTransliteratedMonthList
     * @see .formatYomTov
     * @see .isHebrewFormat
     */
    /**
     * Sets the list of holidays transliterated into Latin chars. This is used by the
     * [.formatYomTov] when formatting the Yom Tov String.
     *
     * @param transliteratedHolidays
     * the transliteratedHolidays to set. Ensure that the sequence exactly matches the list returned by the
     * default
     */
    /**
     * See [.getTransliteratedHolidayList] and [.setTransliteratedHolidayList].
     */
    var transliteratedHolidayList: Array<String> = arrayOf(
        "Erev Pesach", "Pesach", "Chol Hamoed Pesach", "Pesach Sheni",
        "Erev Shavuos", "Shavuos", "Seventeenth of Tammuz", "Tishah B'Av", "Tu B'Av", "Erev Rosh Hashana",
        "Rosh Hashana", "Fast of Gedalyah", "Erev Yom Kippur", "Yom Kippur", "Erev Succos", "Succos",
        "Chol Hamoed Succos", "Hoshana Rabbah", "Shemini Atzeres", "Simchas Torah", "Erev Chanukah", "Chanukah",
        "Tenth of Teves", "Tu B'Shvat", "Fast of Esther", "Purim", "Shushan Purim", "Purim Katan", "Rosh Chodesh",
        "Yom HaShoah", "Yom Hazikaron", "Yom Ha'atzmaut", "Yom Yerushalayim", "Lag B'Omer", "Shushan Purim Katan",
        "Isru Chag"
    )

    /**
     * Hebrew holiday array in the following format.<br></br>`["&#x05E2;&#x05E8;&#x05D1; &#x05E4;&#x05E1;&#x05D7;",
     * "&#x05E4;&#x05E1;&#x05D7;", "&#x05D7;&#x05D5;&#x05DC; &#x05D4;&#x05DE;&#x05D5;&#x05E2;&#x05D3;
     * &#x05E4;&#x05E1;&#x05D7;", "&#x05E4;&#x05E1;&#x05D7; &#x05E9;&#x05E0;&#x05D9;", "&#x05E2;&#x05E8;&#x05D1;
     * &#x05E9;&#x05D1;&#x05D5;&#x05E2;&#x05D5;&#x05EA;", "&#x05E9;&#x05D1;&#x05D5;&#x05E2;&#x05D5;&#x05EA;",
     * "&#x05E9;&#x05D1;&#x05E2;&#x05D4; &#x05E2;&#x05E9;&#x05E8; &#x05D1;&#x05EA;&#x05DE;&#x05D5;&#x05D6;",
     * "&#x05EA;&#x05E9;&#x05E2;&#x05D4; &#x05D1;&#x05D0;&#x05D1;",
     * "&#x05D8;&#x05F4;&#x05D5; &#x05D1;&#x05D0;&#x05D1;",
     * "&#x05E2;&#x05E8;&#x05D1; &#x05E8;&#x05D0;&#x05E9; &#x05D4;&#x05E9;&#x05E0;&#x05D4;",
     * "&#x05E8;&#x05D0;&#x05E9; &#x05D4;&#x05E9;&#x05E0;&#x05D4;",
     * "&#x05E6;&#x05D5;&#x05DD; &#x05D2;&#x05D3;&#x05DC;&#x05D9;&#x05D4;",
     * "&#x05E2;&#x05E8;&#x05D1; &#x05D9;&#x05D5;&#x05DD; &#x05DB;&#x05D9;&#x05E4;&#x05D5;&#x05E8;",
     * "&#x05D9;&#x05D5;&#x05DD; &#x05DB;&#x05D9;&#x05E4;&#x05D5;&#x05E8;",
     * "&#x05E2;&#x05E8;&#x05D1; &#x05E1;&#x05D5;&#x05DB;&#x05D5;&#x05EA;",
     * "&#x05E1;&#x05D5;&#x05DB;&#x05D5;&#x05EA;",
     * "&#x05D7;&#x05D5;&#x05DC; &#x05D4;&#x05DE;&#x05D5;&#x05E2;&#x05D3; &#x05E1;&#x05D5;&#x05DB;&#x05D5;&#x05EA;",
     * "&#x05D4;&#x05D5;&#x05E9;&#x05E2;&#x05E0;&#x05D0; &#x05E8;&#x05D1;&#x05D4;",
     * "&#x05E9;&#x05DE;&#x05D9;&#x05E0;&#x05D9; &#x05E2;&#x05E6;&#x05E8;&#x05EA;",
     * "&#x05E9;&#x05DE;&#x05D7;&#x05EA; &#x05EA;&#x05D5;&#x05E8;&#x05D4;",
     * "&#x05E2;&#x05E8;&#x05D1; &#x05D7;&#x05E0;&#x05D5;&#x05DB;&#x05D4;",
     * "&#x05D7;&#x05E0;&#x05D5;&#x05DB;&#x05D4;", "&#x05E2;&#x05E9;&#x05E8;&#x05D4; &#x05D1;&#x05D8;&#x05D1;&#x05EA;",
     * "&#x05D8;&#x05F4;&#x05D5; &#x05D1;&#x05E9;&#x05D1;&#x05D8;",
     * "&#x05EA;&#x05E2;&#x05E0;&#x05D9;&#x05EA; &#x05D0;&#x05E1;&#x05EA;&#x05E8;",
     * "&#x05E4;&#x05D5;&#x05E8;&#x05D9;&#x05DD;",
     * "&#x05E4;&#x05D5;&#x05E8;&#x05D9;&#x05DD; &#x05E9;&#x05D5;&#x05E9;&#x05DF;",
     * "&#x05E4;&#x05D5;&#x05E8;&#x05D9;&#x05DD; &#x05E7;&#x05D8;&#x05DF;",
     * "&#x05E8;&#x05D0;&#x05E9; &#x05D7;&#x05D5;&#x05D3;&#x05E9;",
     * "&#x05D9;&#x05D5;&#x05DD; &#x05D4;&#x05E9;&#x05D5;&#x05D0;&#x05D4;",
     * "&#x05D9;&#x05D5;&#x05DD; &#x05D4;&#x05D6;&#x05D9;&#x05DB;&#x05E8;&#x05D5;&#x05DF;",
     * "&#x05D9;&#x05D5;&#x05DD; &#x05D4;&#x05E2;&#x05E6;&#x05DE;&#x05D0;&#x05D5;&#x05EA;",
     * "&#x05D9;&#x05D5;&#x05DD; &#x05D9;&#x05E8;&#x05D5;&#x05E9;&#x05DC;&#x05D9;&#x05DD;",
     * "&#x05DC;&#x05F4;&#x05D2; &#x05D1;&#x05E2;&#x05D5;&#x05DE;&#x05E8;",
     * "&#x05E4;&#x05D5;&#x05E8;&#x05D9;&#x05DD; &#x05E9;&#x05D5;&#x05E9;&#x05DF; &#x05E7;&#x05D8;&#x05DF;"]`
     */
    private val hebrewHolidays: Array<String> = arrayOf(
        "\u05E2\u05E8\u05D1 \u05E4\u05E1\u05D7", "\u05E4\u05E1\u05D7",
        "\u05D7\u05D5\u05DC \u05D4\u05DE\u05D5\u05E2\u05D3 \u05E4\u05E1\u05D7",
        "\u05E4\u05E1\u05D7 \u05E9\u05E0\u05D9", "\u05E2\u05E8\u05D1 \u05E9\u05D1\u05D5\u05E2\u05D5\u05EA",
        "\u05E9\u05D1\u05D5\u05E2\u05D5\u05EA",
        "\u05E9\u05D1\u05E2\u05D4 \u05E2\u05E9\u05E8 \u05D1\u05EA\u05DE\u05D5\u05D6",
        "\u05EA\u05E9\u05E2\u05D4 \u05D1\u05D0\u05D1", "\u05D8\u05F4\u05D5 \u05D1\u05D0\u05D1",
        "\u05E2\u05E8\u05D1 \u05E8\u05D0\u05E9 \u05D4\u05E9\u05E0\u05D4",
        "\u05E8\u05D0\u05E9 \u05D4\u05E9\u05E0\u05D4", "\u05E6\u05D5\u05DD \u05D2\u05D3\u05DC\u05D9\u05D4",
        "\u05E2\u05E8\u05D1 \u05D9\u05D5\u05DD \u05DB\u05D9\u05E4\u05D5\u05E8",
        "\u05D9\u05D5\u05DD \u05DB\u05D9\u05E4\u05D5\u05E8", "\u05E2\u05E8\u05D1 \u05E1\u05D5\u05DB\u05D5\u05EA",
        "\u05E1\u05D5\u05DB\u05D5\u05EA",
        "\u05D7\u05D5\u05DC \u05D4\u05DE\u05D5\u05E2\u05D3 \u05E1\u05D5\u05DB\u05D5\u05EA",
        "\u05D4\u05D5\u05E9\u05E2\u05E0\u05D0 \u05E8\u05D1\u05D4",
        "\u05E9\u05DE\u05D9\u05E0\u05D9 \u05E2\u05E6\u05E8\u05EA",
        "\u05E9\u05DE\u05D7\u05EA \u05EA\u05D5\u05E8\u05D4", "\u05E2\u05E8\u05D1 \u05D7\u05E0\u05D5\u05DB\u05D4",
        "\u05D7\u05E0\u05D5\u05DB\u05D4", "\u05E2\u05E9\u05E8\u05D4 \u05D1\u05D8\u05D1\u05EA",
        "\u05D8\u05F4\u05D5 \u05D1\u05E9\u05D1\u05D8", "\u05EA\u05E2\u05E0\u05D9\u05EA \u05D0\u05E1\u05EA\u05E8",
        "\u05E4\u05D5\u05E8\u05D9\u05DD", "\u05E4\u05D5\u05E8\u05D9\u05DD \u05E9\u05D5\u05E9\u05DF",
        "\u05E4\u05D5\u05E8\u05D9\u05DD \u05E7\u05D8\u05DF", "\u05E8\u05D0\u05E9 \u05D7\u05D5\u05D3\u05E9",
        "\u05D9\u05D5\u05DD \u05D4\u05E9\u05D5\u05D0\u05D4",
        "\u05D9\u05D5\u05DD \u05D4\u05D6\u05D9\u05DB\u05E8\u05D5\u05DF",
        "\u05D9\u05D5\u05DD \u05D4\u05E2\u05E6\u05DE\u05D0\u05D5\u05EA",
        "\u05D9\u05D5\u05DD \u05D9\u05E8\u05D5\u05E9\u05DC\u05D9\u05DD",
        "\u05DC\u05F4\u05D2 \u05D1\u05E2\u05D5\u05DE\u05E8",
        "\u05E4\u05D5\u05E8\u05D9\u05DD \u05E9\u05D5\u05E9\u05DF \u05E7\u05D8\u05DF",
        "\u05D0\u05E1\u05E8\u05D5 \u05D7\u05D2"
    )

    /**
     * Formats the Yom Tov (holiday) in Hebrew or transliterated Latin characters.
     *
     * @param jewishCalendar the JewishCalendar
     * @return the formatted holiday or an empty String if the day is not a holiday.
     * @see .isHebrewFormat
     */
    fun formatYomTov(jewishCalendar: JewishCalendar): String {
        val index: Int = jewishCalendar.yomTovIndex
        if (index == JewishCalendar.CHANUKAH) {
            val dayOfChanukah: Int = jewishCalendar.dayOfChanukah
            return if (isHebrewFormat) (formatHebrewNumber(dayOfChanukah) + " " + hebrewHolidays.get(index)) else (transliteratedHolidayList.get(
                index
            ) + " " + dayOfChanukah)
        }
        return if (index == -1) "" else if (isHebrewFormat) hebrewHolidays.get(index) else transliteratedHolidayList.get(
            index
        )
    }

    /**
     * Formats a day as Rosh Chodesh in the format of in the format of &#x05E8;&#x05D0;&#x05E9;
     * &#x05D7;&#x05D5;&#x05D3;&#x05E9; &#x05E9;&#x05D1;&#x05D8; or Rosh Chodesh Shevat. If it
     * is not Rosh Chodesh, an empty `String` will be returned.
     * @param jewishCalendar the JewishCalendar
     * @return The formatted `String` in the format of &#x05E8;&#x05D0;&#x05E9;
     * &#x05D7;&#x05D5;&#x05D3;&#x05E9; &#x05E9;&#x05D1;&#x05D8; or Rosh Chodesh Shevat. If it
     * is not Rosh Chodesh, an empty `String` will be returned.
     */
    fun formatRoshChodesh(jewishCalendar: JewishCalendar): String {
        var jewishCalendar: JewishCalendar = jewishCalendar
        if (!jewishCalendar.isRoshChodesh) {
            return ""
        }
        var formattedRoshChodesh: String = ""
        var month: Int = jewishCalendar.getJewishMonth()
        if (jewishCalendar.jewishDayOfMonth == 30) {
            if (month < JewishDate.ADAR || (month == JewishDate.ADAR && jewishCalendar.isJewishLeapYear)) {
                month++
            } else { // roll to Nissan
                month = JewishDate.NISSAN
            }
        }

        // This method is only about formatting, so we shouldn't make any changes to the params passed in...
        jewishCalendar = jewishCalendar.clone() as JewishCalendar
        jewishCalendar.setJewishMonth(month)
        formattedRoshChodesh =
            if (isHebrewFormat) hebrewHolidays.get(JewishCalendar.ROSH_CHODESH) else transliteratedHolidayList.get(
                JewishCalendar.ROSH_CHODESH
            )
        formattedRoshChodesh += " " + formatMonth(jewishCalendar)
        return formattedRoshChodesh
    }

    /**
     * Default constructor sets the [EnumMap]s of Hebrew and default transliterated parshiyos.
     */
    init {
        transliteratedParshiosList.put(Parsha.NONE, "")
        transliteratedParshiosList.put(Parsha.BERESHIS, "Bereshis")
        transliteratedParshiosList.put(Parsha.NOACH, "Noach")
        transliteratedParshiosList.put(Parsha.LECH_LECHA, "Lech Lecha")
        transliteratedParshiosList.put(Parsha.VAYERA, "Vayera")
        transliteratedParshiosList.put(Parsha.CHAYEI_SARA, "Chayei Sara")
        transliteratedParshiosList.put(Parsha.TOLDOS, "Toldos")
        transliteratedParshiosList.put(Parsha.VAYETZEI, "Vayetzei")
        transliteratedParshiosList.put(Parsha.VAYISHLACH, "Vayishlach")
        transliteratedParshiosList.put(Parsha.VAYESHEV, "Vayeshev")
        transliteratedParshiosList.put(Parsha.MIKETZ, "Miketz")
        transliteratedParshiosList.put(Parsha.VAYIGASH, "Vayigash")
        transliteratedParshiosList.put(Parsha.VAYECHI, "Vayechi")
        transliteratedParshiosList.put(Parsha.SHEMOS, "Shemos")
        transliteratedParshiosList.put(Parsha.VAERA, "Vaera")
        transliteratedParshiosList.put(Parsha.BO, "Bo")
        transliteratedParshiosList.put(Parsha.BESHALACH, "Beshalach")
        transliteratedParshiosList.put(Parsha.YISRO, "Yisro")
        transliteratedParshiosList.put(Parsha.MISHPATIM, "Mishpatim")
        transliteratedParshiosList.put(Parsha.TERUMAH, "Terumah")
        transliteratedParshiosList.put(Parsha.TETZAVEH, "Tetzaveh")
        transliteratedParshiosList.put(Parsha.KI_SISA, "Ki Sisa")
        transliteratedParshiosList.put(Parsha.VAYAKHEL, "Vayakhel")
        transliteratedParshiosList.put(Parsha.PEKUDEI, "Pekudei")
        transliteratedParshiosList.put(Parsha.VAYIKRA, "Vayikra")
        transliteratedParshiosList.put(Parsha.TZAV, "Tzav")
        transliteratedParshiosList.put(Parsha.SHMINI, "Shmini")
        transliteratedParshiosList.put(Parsha.TAZRIA, "Tazria")
        transliteratedParshiosList.put(Parsha.METZORA, "Metzora")
        transliteratedParshiosList.put(Parsha.ACHREI_MOS, "Achrei Mos")
        transliteratedParshiosList.put(Parsha.KEDOSHIM, "Kedoshim")
        transliteratedParshiosList.put(Parsha.EMOR, "Emor")
        transliteratedParshiosList.put(Parsha.BEHAR, "Behar")
        transliteratedParshiosList.put(Parsha.BECHUKOSAI, "Bechukosai")
        transliteratedParshiosList.put(Parsha.BAMIDBAR, "Bamidbar")
        transliteratedParshiosList.put(Parsha.NASSO, "Nasso")
        transliteratedParshiosList.put(Parsha.BEHAALOSCHA, "Beha'aloscha")
        transliteratedParshiosList.put(Parsha.SHLACH, "Sh'lach")
        transliteratedParshiosList.put(Parsha.KORACH, "Korach")
        transliteratedParshiosList.put(Parsha.CHUKAS, "Chukas")
        transliteratedParshiosList.put(Parsha.BALAK, "Balak")
        transliteratedParshiosList.put(Parsha.PINCHAS, "Pinchas")
        transliteratedParshiosList.put(Parsha.MATOS, "Matos")
        transliteratedParshiosList.put(Parsha.MASEI, "Masei")
        transliteratedParshiosList.put(Parsha.DEVARIM, "Devarim")
        transliteratedParshiosList.put(Parsha.VAESCHANAN, "Vaeschanan")
        transliteratedParshiosList.put(Parsha.EIKEV, "Eikev")
        transliteratedParshiosList.put(Parsha.REEH, "Re'eh")
        transliteratedParshiosList.put(Parsha.SHOFTIM, "Shoftim")
        transliteratedParshiosList.put(Parsha.KI_SEITZEI, "Ki Seitzei")
        transliteratedParshiosList.put(Parsha.KI_SAVO, "Ki Savo")
        transliteratedParshiosList.put(Parsha.NITZAVIM, "Nitzavim")
        transliteratedParshiosList.put(Parsha.VAYEILECH, "Vayeilech")
        transliteratedParshiosList.put(Parsha.HAAZINU, "Ha'Azinu")
        transliteratedParshiosList.put(Parsha.VZOS_HABERACHA, "Vezos Habracha")
        transliteratedParshiosList.put(Parsha.VAYAKHEL_PEKUDEI, "Vayakhel Pekudei")
        transliteratedParshiosList.put(Parsha.TAZRIA_METZORA, "Tazria Metzora")
        transliteratedParshiosList.put(Parsha.ACHREI_MOS_KEDOSHIM, "Achrei Mos Kedoshim")
        transliteratedParshiosList.put(Parsha.BEHAR_BECHUKOSAI, "Behar Bechukosai")
        transliteratedParshiosList.put(Parsha.CHUKAS_BALAK, "Chukas Balak")
        transliteratedParshiosList.put(Parsha.MATOS_MASEI, "Matos Masei")
        transliteratedParshiosList.put(Parsha.NITZAVIM_VAYEILECH, "Nitzavim Vayeilech")
        transliteratedParshiosList.put(Parsha.SHKALIM, "Shekalim")
        transliteratedParshiosList.put(Parsha.ZACHOR, "Zachor")
        transliteratedParshiosList.put(Parsha.PARA, "Parah")
        transliteratedParshiosList.put(Parsha.HACHODESH, "Hachodesh")
        transliteratedParshiosList.put(Parsha.SHUVA, "Shuva")
        transliteratedParshiosList.put(Parsha.SHIRA, "Shira")
        transliteratedParshiosList.put(Parsha.HAGADOL, "Hagadol")
        transliteratedParshiosList.put(Parsha.CHAZON, "Chazon")
        transliteratedParshiosList.put(Parsha.NACHAMU, "Nachamu")

        hebrewParshaMap
    }

    /**
     * Formats the day of week. If [Hebrew formatting][.isHebrewFormat] is set, it will display in the format
     * &#x05E8;&#x05D0;&#x05E9;&#x05D5;&#x05DF; etc. If Hebrew formatting is not in use it will return it in the format
     * of Sunday etc. There are various formatting options that will affect the output.
     *
     * @param jewishDate the JewishDate Object
     * @return the formatted day of week
     * @see .isHebrewFormat
     * @see .isLongWeekFormat
     */
    fun formatDayOfWeek(jewishDate: JewishDate): String {
        if (isHebrewFormat) {
            if (isLongWeekFormat) {
                return hebrewDaysOfWeek.get(jewishDate.dayOfWeek - 1)
            } else {
                if (jewishDate.dayOfWeek == 7) {
                    return formatHebrewNumber(300)
                } else {
                    return formatHebrewNumber(jewishDate.dayOfWeek)
                }
            }
        } else {
            if (jewishDate.dayOfWeek == 7) {
                if (isLongWeekFormat) {
                    return transliteratedShabbosDayOfWeek
                } else {
                    return transliteratedShabbosDayOfWeek.substring(0, 3)
                }
            } else {
                return weekFormat!!.format(jewishDate.gregorianCalendar.time)
            }
        }
    }

    /**
     * Formats the Jewish date. If the formatter is set to Hebrew, it will format in the form, "day Month year" for
     * example &#x5DB;&#x5F4;&#x5D0; &#x5E9;&#x5D1;&#x5D8; &#x5EA;&#x5E9;&#x5DB;&#x5F4;&#x5D8;, and the format
     * "21 Shevat, 5729" if not.
     *
     * @param jewishDate
     * the JewishDate to be formatted
     * @return the formatted date. If the formatter is set to Hebrew, it will format in the form, "day Month year" for
     * example &#x5DB;&#x5F4;&#x5D0; &#x5E9;&#x5D1;&#x5D8; &#x5EA;&#x5E9;&#x5DB;&#x5F4;&#x5D8;, and the format
     * "21 Shevat, 5729" if not.
     */
    fun format(jewishDate: JewishDate): String {
        if (isHebrewFormat) {
            return (formatHebrewNumber(jewishDate.jewishDayOfMonth) + " " + formatMonth(jewishDate) + " "
                    + formatHebrewNumber(jewishDate.getJewishYear()))
        } else {
            return jewishDate.jewishDayOfMonth
                .toString() + " " + formatMonth(jewishDate) + ", " + jewishDate.getJewishYear()
        }
    }

    /**
     * Returns a string of the current Hebrew month such as "Tishrei". Returns a string of the current Hebrew month such
     * as "&#x5D0;&#x5D3;&#x5E8; &#x5D1;&#x5F3;".
     *
     * @param jewishDate
     * the JewishDate to format
     * @return the formatted month name
     * @see .isHebrewFormat
     * @see .setHebrewFormat
     * @see .getTransliteratedMonthList
     * @see .setTransliteratedMonthList
     */
    fun formatMonth(jewishDate: JewishDate): String {
        val month: Int = jewishDate.getJewishMonth()
        if (isHebrewFormat) {
            if (jewishDate.isJewishLeapYear && month == JewishDate.ADAR) {
                return hebrewMonths.get(13) + (if (isUseGershGershayim) GERESH else "") // return Adar I, not Adar in a leap year
            } else if (jewishDate.isJewishLeapYear && month == JewishDate.ADAR_II) {
                return hebrewMonths.get(12) + (if (isUseGershGershayim) GERESH else "")
            } else {
                return hebrewMonths.get(month - 1)
            }
        } else {
            if (jewishDate.isJewishLeapYear && month == JewishDate.ADAR) {
                return transliteratedMonthList.get(13) // return Adar I, not Adar in a leap year
            } else {
                return transliteratedMonthList.get(month - 1)
            }
        }
    }

    /**
     * Returns a String of the Omer day in the form &#x5DC;&#x5F4;&#x5D2; &#x5D1;&#x05E2;&#x05D5;&#x05DE;&#x5E8; if
     * Hebrew Format is set, or "Omer X" or "Lag B'Omer" if not. An empty string if there is no Omer this day.
     *
     * @param jewishCalendar
     * the JewishCalendar to be formatted
     *
     * @return a String of the Omer day in the form or an empty string if there is no Omer this day. The default
     * formatting has a &#x5D1;&#x5F3; prefix that would output &#x5D1;&#x05E2;&#x05D5;&#x05DE;&#x5E8;, but this
     * can be set via the [.setHebrewOmerPrefix] method to use a &#x5DC; and output
     * &#x5DC;&#x5F4;&#x5D2; &#x5DC;&#x05E2;&#x05D5;&#x05DE;&#x5E8;.
     * @see .isHebrewFormat
     * @see .getHebrewOmerPrefix
     * @see .setHebrewOmerPrefix
     */
    fun formatOmer(jewishCalendar: JewishCalendar): String {
        val omer: Int = jewishCalendar.dayOfOmer
        if (omer == -1) {
            return ""
        }
        if (isHebrewFormat) {
            return formatHebrewNumber(omer) + " " + hebrewOmerPrefix + "\u05E2\u05D5\u05DE\u05E8"
        } else {
            if (omer == 33) { // if Lag B'Omer
                return transliteratedHolidayList.get(33)
            } else {
                return "Omer " + omer
            }
        }
    }

    /**
     * Returns the kviah in the traditional 3 letter Hebrew format where the first letter represents the day of week of
     * Rosh Hashana, the second letter represents the lengths of Cheshvan and Kislev ([ Shelaimim][JewishDate.SHELAIMIM] , [Kesidran][JewishDate.KESIDRAN] or [Chaserim][JewishDate.CHASERIM]) and the 3rd letter
     * represents the day of week of Pesach. For example 5729 (1969) would return &#x5D1;&#x5E9;&#x5D4; (Rosh Hashana on
     * Monday, Shelaimim, and Pesach on Thursday), while 5771 (2011) would return &#x5D4;&#x5E9;&#x5D2; (Rosh Hashana on
     * Thursday, Shelaimim, and Pesach on Tuesday).
     *
     * @param jewishYear
     * the Jewish year
     * @return the Hebrew String such as &#x5D1;&#x5E9;&#x5D4; for 5729 (1969) and &#x5D4;&#x5E9;&#x5D2; for 5771
     * (2011).
     */
    fun getFormattedKviah(jewishYear: Int): String {
        val jewishDate: JewishDate = JewishDate(jewishYear, JewishDate.TISHREI, 1) // set date to Rosh Hashana
        val kviah: Int = jewishDate.cheshvanKislevKviah
        val roshHashanaDayOfweek: Int = jewishDate.dayOfWeek
        var returnValue: String = formatHebrewNumber(roshHashanaDayOfweek)
        returnValue += (if (kviah == JewishDate.CHASERIM) "\u05D7" else if (kviah == JewishDate.SHELAIMIM) "\u05E9" else "\u05DB")
        jewishDate.setJewishDate(jewishYear, JewishDate.NISSAN, 15) // set to Pesach of the given year
        val pesachDayOfweek: Int = jewishDate.dayOfWeek
        returnValue += formatHebrewNumber(pesachDayOfweek)
        returnValue = returnValue.replace(GERESH.toRegex(), "") // geresh is never used in the kviah format
        // boolean isLeapYear = JewishDate.isJewishLeapYear(jewishYear);
        // for efficiency we can avoid the expensive recalculation of the pesach day of week by adding 1 day to Rosh
        // Hashana for a 353 day year, 2 for a 354 day year, 3 for a 355 or 383 day year, 4 for a 384 day year and 5 for
        // a 385 day year
        return returnValue
    }

    /**
     * Formats the [Daf Yomi](https://en.wikipedia.org/wiki/Daf_Yomi) Bavli in the format of
     * "&#x05E2;&#x05D9;&#x05E8;&#x05D5;&#x05D1;&#x05D9;&#x05DF; &#x05E0;&#x05F4;&#x05D1;" in [Hebrew][.isHebrewFormat],
     * or the transliterated format of "Eruvin 52".
     * @param daf the Daf to be formatted.
     * @return the formatted daf.
     */
    fun formatDafYomiBavli(daf: Daf): String = if (isHebrewFormat) {
        daf.masechta + " " + formatHebrewNumber(daf.daf)
    } else {
        daf.masechtaTransliterated + " " + daf.daf
    }

    /**
     * Formats the [Daf Yomi Yerushalmi](https://en.wikipedia.org/wiki/Jerusalem_Talmud#Daf_Yomi_Yerushalmi) in the format
     * of "&#x05E2;&#x05D9;&#x05E8;&#x05D5;&#x05D1;&#x05D9;&#x05DF; &#x05E0;&#x05F4;&#x05D1;" in [Hebrew][.isHebrewFormat], or
     * the transliterated format of "Eruvin 52".
     *
     * @param daf the Daf to be formatted.
     * @return the formatted daf.
     */
    fun formatDafYomiYerushalmi(daf: Daf?): String {
        if (daf == null) {
            return if (isHebrewFormat) Daf.yerushlmiMasechtos[39] else Daf.yerushlmiMasechtosTransliterated[39]
        }
        return if (isHebrewFormat) daf.yerushalmiMasechta + " " + formatHebrewNumber(daf.daf)
        else daf.yerushlmiMasechtaTransliterated + " " + daf.daf
    }

    /**
     * Returns a Hebrew formatted string of a number. The method can calculate from 0 - 9999.
     *
     *  * Single digit numbers such as 3, 30 and 100 will be returned with a &#x5F3; ([Geresh](http://en.wikipedia.org/wiki/Geresh)) appended as at the end. For example &#x5D2;&#x5F3;,
     * &#x5DC;&#x5F3; and &#x5E7;&#x5F3;
     *  * multi digit numbers such as 21 and 769 will be returned with a &#x5F4; ([Gershayim](http://en.wikipedia.org/wiki/Gershayim)) between the second to last and last letters. For
     * example &#x5DB;&#x5F4;&#x5D0;, &#x5EA;&#x5E9;&#x5DB;&#x5F4;&#x5D8;
     *  * 15 and 16 will be returned as &#x5D8;&#x5F4;&#x5D5; and &#x5D8;&#x5F4;&#x5D6;
     *  * Single digit numbers (years assumed) such as 6000 (%1000=0) will be returned as &#x5D5;&#x5F3;
     * &#x5D0;&#x5DC;&#x5E4;&#x5D9;&#x5DD;
     *  * 0 will return &#x5D0;&#x5E4;&#x05E1;
     *
     *
     * @param number
     * the number to be formatted. It will trow an IllegalArgumentException if the number is &lt; 0 or &gt; 9999.
     * @return the Hebrew formatted number such as &#x5EA;&#x5E9;&#x5DB;&#x5F4;&#x5D8;
     * @see .isUseFinalFormLetters
     * @see .isUseGershGershayim
     * @see .isHebrewFormat
     */
    fun formatHebrewNumber(number: Int): String {
        var number: Int = number
        if (number < 0) {
            throw IllegalArgumentException("negative numbers can't be formatted")
        } else if (number > 9999) {
            throw IllegalArgumentException("numbers > 9999 can't be formatted")
        }
        val ALAFIM: String = "\u05D0\u05DC\u05E4\u05D9\u05DD"
        val EFES: String = "\u05D0\u05E4\u05E1"
        val jHundreds: Array<String> = arrayOf(
            "", "\u05E7", "\u05E8", "\u05E9", "\u05EA", "\u05EA\u05E7", "\u05EA\u05E8",
            "\u05EA\u05E9", "\u05EA\u05EA", "\u05EA\u05EA\u05E7"
        )
        val jTens: Array<String> = arrayOf(
            "", "\u05D9", "\u05DB", "\u05DC", "\u05DE", "\u05E0", "\u05E1", "\u05E2",
            "\u05E4", "\u05E6"
        )
        val jTenEnds: Array<String> = arrayOf(
            "", "\u05D9", "\u05DA", "\u05DC", "\u05DD", "\u05DF", "\u05E1", "\u05E2",
            "\u05E3", "\u05E5"
        )
        val tavTaz: Array<String> = arrayOf("\u05D8\u05D5", "\u05D8\u05D6")
        val jOnes: Array<String> = arrayOf(
            "", "\u05D0", "\u05D1", "\u05D2", "\u05D3", "\u05D4", "\u05D5", "\u05D6",
            "\u05D7", "\u05D8"
        )
        if (number == 0) { // do we really need this? Should it be applicable to a date?
            return EFES
        }
        val shortNumber: Int = number % 1000 // discard thousands
        // next check for all possible single Hebrew digit years
        val singleDigitNumber: Boolean =
            ((shortNumber < 11) || (shortNumber < 100 && shortNumber % 10 == 0) || (shortNumber <= 400 && shortNumber % 100 == 0))
        val thousands: Int = number / 1000 // get # thousands
        val sb: StringBuilder = StringBuilder()
        // append thousands to String
        if (number % 1000 == 0) { // in year is 5000, 4000 etc
            sb.append(jOnes.get(thousands))
            if (isUseGershGershayim) {
                sb.append(GERESH)
            }
            sb.append(" ")
            sb.append(ALAFIM) // add # of thousands plus word thousand (overide alafim boolean)
            return sb.toString()
        } else if (isUseLongHebrewYears && number >= 1000) { // if alafim boolean display thousands
            sb.append(jOnes.get(thousands))
            if (isUseGershGershayim) {
                sb.append(GERESH) // append thousands quote
            }
            sb.append(" ")
        }
        number = number % 1000 // remove 1000s
        val hundreds: Int = number / 100 // # of hundreds
        sb.append(jHundreds.get(hundreds)) // add hundreds to String
        number = number % 100 // remove 100s
        if (number == 15) { // special case 15
            sb.append(tavTaz.get(0))
        } else if (number == 16) { // special case 16
            sb.append(tavTaz.get(1))
        } else {
            val tens: Int = number / 10
            if (number % 10 == 0) { // if evenly divisable by 10
                if (!singleDigitNumber) {
                    if (isUseFinalFormLetters) {
                        sb.append(jTenEnds.get(tens)) // years like 5780 will end with a final form &#x05E3;
                    } else {
                        sb.append(jTens.get(tens)) // years like 5780 will end with a regular &#x05E4;
                    }
                } else {
                    sb.append(jTens.get(tens)) // standard letters so years like 5050 will end with a regular nun
                }
            } else {
                sb.append(jTens.get(tens))
                number = number % 10
                sb.append(jOnes.get(number))
            }
        }
        if (isUseGershGershayim) {
            if (singleDigitNumber) {
                sb.append(GERESH) // append single quote
            } else { // append double quote before last digit
                sb.insert(sb.length - 1, GERSHAYIM)
            }
        }
        return sb.toString()
    }

    /**
     * Returns a String with the name of the current parsha(ios). If the formatter is set to format in Hebrew, returns
     * a string of the current parsha(ios) in Hebrew for example &#x05D1;&#x05E8;&#x05D0;&#x05E9;&#x05D9;&#x05EA; or
     * &#x05E0;&#x05E6;&#x05D1;&#x05D9;&#x05DD; &#x05D5;&#x05D9;&#x05DC;&#x05DA; or an empty string if there
     * are none. If not set to Hebrew, it returns a string of the parsha(ios) transliterated into Latin chars. The
     * default uses Ashkenazi pronunciation in typical American English spelling, for example Bereshis or
     * Nitzavim Vayeilech or an empty string if there are none.
     *
     * @param jewishCalendar the JewishCalendar Object
     * @return today's parsha(ios) in Hebrew for example, if the formatter is set to format in Hebrew, returns a string
     * of the current parsha(ios) in Hebrew for example &#x05D1;&#x05E8;&#x05D0;&#x05E9;&#x05D9;&#x05EA; or
     * &#x05E0;&#x05E6;&#x05D1;&#x05D9;&#x05DD; &#x05D5;&#x05D9;&#x05DC;&#x05DA; or an empty string if
     * there are none. If not set to Hebrew, it returns a string of the parsha(ios) transliterated into Latin
     * chars. The default uses Ashkenazi pronunciation in typical American English spelling, for example
     * Bereshis or Nitzavim Vayeilech or an empty string if there are none.
     */
    fun formatParsha(jewishCalendar: JewishCalendar): String? {
        val parshah = jewishCalendar.parshah
        return if (isHebrewFormat) hebrewParshaMap[parshah]
        else transliteratedParshiosList[parshah]
    }

    /**
     * Returns a String with the name of the current special parsha of Shekalim, Zachor, Parah or Hachodesh or an
     * empty String for a non-special parsha. If the formatter is set to format in Hebrew, it returns a string of
     * the current special parsha in Hebrew, for example &#x05E9;&#x05E7;&#x05DC;&#x05D9;&#x05DD;,
     * &#x05D6;&#x05DB;&#x05D5;&#x05E8;, &#x05E4;&#x05E8;&#x05D4; or &#x05D4;&#x05D7;&#x05D3;&#x05E9;. An empty
     * string if the date is not a special parsha. If not set to Hebrew, it returns a string of the special parsha
     * transliterated into Latin chars. The default uses Ashkenazi pronunciation in typical American English spelling
     * Shekalim, Zachor, Parah or Hachodesh.
     *
     * @param jewishCalendar the JewishCalendar Object
     * @return today's special parsha. If the formatter is set to format in Hebrew, returns a string
     * of the current special parsha  in Hebrew for in the format of &#x05E9;&#x05E7;&#x05DC;&#x05D9;&#x05DD;,
     * &#x05D6;&#x05DB;&#x05D5;&#x05E8;, &#x05E4;&#x05E8;&#x05D4; or &#x05D4;&#x05D7;&#x05D3;&#x05E9; or an empty
     * string if there are none. If not set to Hebrew, it returns a string of the special parsha transliterated
     * into Latin chars. The default uses Ashkenazi pronunciation in typical American English spelling of Shekalim,
     * Zachor, Parah or Hachodesh. An empty string if there are none.
     */
    fun formatSpecialParsha(jewishCalendar: JewishCalendar): String? {
        val specialParsha: Parsha = jewishCalendar.specialShabbos
        return if (isHebrewFormat) hebrewParshaMap[specialParsha] else transliteratedParshiosList[specialParsha]
    }

    companion object {
        /**
         * The [gersh](https://en.wikipedia.org/wiki/Geresh#Punctuation_mark) character is the &#x05F3; char
         * that is similar to a single quote and is used in formatting Hebrew numbers.
         */
        private val GERESH: String = "\u05F3"

        /**
         * The [gershyim](https://en.wikipedia.org/wiki/Gershayim#Punctuation_mark) character is the &#x05F4; char
         * that is similar to a double quote and is used in formatting Hebrew numbers.
         */
        private val GERSHAYIM: String = "\u05F4"

        /**
         * Unicode list of Hebrew months in the following format `["\u05E0\u05D9\u05E1\u05DF","\u05D0\u05D9\u05D9\u05E8",
         * "\u05E1\u05D9\u05D5\u05DF","\u05EA\u05DE\u05D5\u05D6","\u05D0\u05D1","\u05D0\u05DC\u05D5\u05DC",
         * "\u05EA\u05E9\u05E8\u05D9","\u05D7\u05E9\u05D5\u05DF","\u05DB\u05E1\u05DC\u05D5","\u05D8\u05D1\u05EA",
         * "\u05E9\u05D1\u05D8","\u05D0\u05D3\u05E8","\u05D0\u05D3\u05E8 \u05D1","\u05D0\u05D3\u05E8 \u05D0"]`
         *
         * @see .formatMonth
         */
        private val hebrewMonths: Array<String> = arrayOf(
            "\u05E0\u05D9\u05E1\u05DF", "\u05D0\u05D9\u05D9\u05E8",
            "\u05E1\u05D9\u05D5\u05DF", "\u05EA\u05DE\u05D5\u05D6", "\u05D0\u05D1", "\u05D0\u05DC\u05D5\u05DC",
            "\u05EA\u05E9\u05E8\u05D9", "\u05D7\u05E9\u05D5\u05DF", "\u05DB\u05E1\u05DC\u05D5",
            "\u05D8\u05D1\u05EA", "\u05E9\u05D1\u05D8", "\u05D0\u05D3\u05E8", "\u05D0\u05D3\u05E8 \u05D1",
            "\u05D0\u05D3\u05E8 \u05D0"
        )

        /**
         * Unicode list of Hebrew days of week in the format of `["&#x05E8;&#x05D0;&#x05E9;&#x05D5;&#x05DF;",
         * "&#x05E9;&#x05E0;&#x05D9;","&#x05E9;&#x05DC;&#x05D9;&#x05E9;&#x05D9;","&#x05E8;&#x05D1;&#x05D9;&#x05E2;&#x05D9;",
         * "&#x05D7;&#x05DE;&#x05D9;&#x05E9;&#x05D9;","&#x05E9;&#x05E9;&#x05D9;","&#x05E9;&#x05D1;&#x05EA;"]`
         */
        private val hebrewDaysOfWeek: Array<String> = arrayOf(
            "\u05E8\u05D0\u05E9\u05D5\u05DF", "\u05E9\u05E0\u05D9",
            "\u05E9\u05DC\u05D9\u05E9\u05D9", "\u05E8\u05D1\u05D9\u05E2\u05D9", "\u05D7\u05DE\u05D9\u05E9\u05D9",
            "\u05E9\u05E9\u05D9", "\u05E9\u05D1\u05EA"
        )

        const val MINUTE_CHALAKIM = 18
        const val HOUR_CHALAKIM = 1080
        const val DAY_CHALAKIM: Int = 24 * HOUR_CHALAKIM

        /**
         * Formats a molad.
         * @todo Experimental and incomplete.
         *
         * @param moladChalakim the chalakim of the molad
         * @return the formatted molad. FIXME: define proper format in English and Hebrew.
         */
        private fun formatMolad(moladChalakim: Long): String {
            var adjustedChalakim: Long = moladChalakim
            var days: Long = adjustedChalakim / DAY_CHALAKIM
            adjustedChalakim -= (days * DAY_CHALAKIM)
            val hours: Int = ((adjustedChalakim / HOUR_CHALAKIM)).toInt()
            if (hours >= 6) {
                days += 1
            }
            adjustedChalakim -= (hours * HOUR_CHALAKIM.toLong())
            val minutes: Int = (adjustedChalakim / MINUTE_CHALAKIM).toInt()
            adjustedChalakim -= minutes * MINUTE_CHALAKIM.toLong()
            return "Day: " + (days % 7) + " hours: " + hours + ", minutes " + minutes + ", chalakim: " + adjustedChalakim
        }
    }
}