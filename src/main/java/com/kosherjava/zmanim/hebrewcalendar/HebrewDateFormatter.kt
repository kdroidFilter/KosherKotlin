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

import com.kosherjava.zmanim.util.WeekFormat
import com.kosherjava.zmanim.hebrewcalendar.JewishCalendar.Companion.toJewishDayOfWeek
import com.kosherjava.zmanim.hebrewcalendar.JewishCalendar.Parsha
import kotlin.text.StringBuilder

/**
 * The HebrewDateFormatter class formats a [JewishDate].
 *
 * The class formats Jewish dates, numbers, *Daf Yomi* (*Bavli* and *Yerushalmi*), the *Omer*,
 * *Parshas Hashavua* (including the special *parshiyos* of *Shekalim*, *Zachor*, *Parah*
 * and *Hachodesh*), Yomim Tovim and the Molad (experimental) in Hebrew or Latin chars, and has various settings.
 * Sample full date output includes (using various options):
 *
 *  * 21 Shevat, 5729
 *  * כא שבט תשכט
 *  * כ״א שבט ה׳תשכ״ט
 *  * כ״א שבט תש״פ or
 * כ״א שבט תש״ף
 *  * כ׳ שבט ו׳ אלפים
 *
 *
 * @see JewishDate
 *
 * @see JewishCalendar
 *
 *
 * @author  Eliyahu Hershfeld 2011 - 2023
 */
class HebrewDateFormatter {
    /**
     * Returns if the formatter is set to use Hebrew formatting in the various formatting methods.
     *
     * @return the hebrewFormat
     * @see format
     * @see formatDayOfWeek
     * @see formatMonth
     * @see formatOmer
     * @see formatYomTov
     */
    var isHebrewFormat: Boolean = false

    /**
     * Returns whether the class is set to use the thousand's digit when formatting: true if set to use the thousand's
     * digit when formatting Hebrew dates and numbers.
     *
     * When formatting a Hebrew Year, traditionally the thousands digit is omitted and output for a year such as 5729
     * (1969 Gregorian) would be calculated for 729 and format as &#x5EA;&#x5E9;&#x5DB;&#x5F4;&#x5D8;.
     * When set to true the long format year such as &#x5D4;&#x5F3; &#x5EA;&#x5E9;&#x5DB;&#x5F4;&#x5D8; for 5729/1969 is returned.
     */
    var isUseLongHebrewYears: Boolean = false

    /**
     * Returns whether the class is set to use the Geresh ׳ and Gershayim ״ in formatting Hebrew dates and
     * numbers. When true and output would look like כ״א שבט תש״כ
     * (or כ״א שבט תש״ך). When set to false, this output
     * would display as כא שבט תשכ.
     *
     * @return true if set to use the Geresh ׳ and Gershayim ״ in formatting Hebrew dates and numbers.
     */
    /**
     * Sets whether to use the Geresh ׳ and Gershayim ״ in formatting Hebrew dates and numbers. The default
     * value is true and output would look like כ״א שבט תש״כ
     * (or כ״א שבט תש״ך). When set to false, this output would
     * display as כא שבט תשכ (or
     * כא שבט תשך). Single digit days or month or years such as כ׳
     * שבט ו׳ אלפים show the use of the Geresh.
     *
     * Set to false to omit the Geresh ׳ and Gershayim ״ in formatting
     */
    var isUseGershGershayim: Boolean = true
    /**
     * Returns if the [formatDayOfWeek] will use the long format such as
     * ראשון or short such as א when formatting the day of week in
     * [Hebrew][isHebrewFormat].
     *
     * @return the longWeekFormat
     * @see .setLongWeekFormat
     * @see .formatDayOfWeek
     */
    /**
     * Setting to control if the [formatDayOfWeek] will use the long format such as
     * ראשון or short such as א when formatting the day of week in
     * [Hebrew][isHebrewFormat].
     *
     */
    var isLongWeekFormat: Boolean = true
        set(longWeekFormat) {
            field = longWeekFormat
            weekFormat = if (longWeekFormat) WeekFormat.long else WeekFormat.short
        }
    /**
     * Returns whether the class is set to use the מנצפ״ך letters when
     * formatting years ending in 20, 40, 50, 80 and 90 to produce תש״פ if false or
     * or תש״ף if true. Traditionally non-final form letters are used, so the year
     * 5780 would be formatted as תש״פ if the default false is used here. If this returns
     * true, the format תש״ף would be used.
     *
     * @return true if set to use final form letters when formatting Hebrew years. The default value is false.
     */
    /**
     * When formatting a Hebrew Year, traditionally years ending in 20, 40, 50, 80 and 90 are formatted using non-final
     * form letters for example תש״פ for the year 5780. Setting this to true (the default
     * is false) will use the final form letters for מנצפ״ך and will format
     * the year 5780 as תש״ף.
     *
     * Set this to true to use final form letters when formatting Hebrew years.
     */
    var isUseFinalFormLetters: Boolean = false

    /**
     * The internal DateFormat.
     * See [isLongWeekFormat].
     */
    private var weekFormat: WeekFormat.Formatter? = null
    /**
     * Retruns the list of transliterated parshiyos used by this formatter.
     *
     * @return the list of transliterated Parshios
     */
    /**
     * Setter method to allow overriding of the default list of parshiyos transliterated into Latin chars. The
     * default uses Ashkenazi American English transliteration.
     */
    /**
     * List of transliterated parshiyos using the default *Ashkenazi* pronunciation.&nbsp; The formatParsha method
     * uses this for transliterated *parsha* formatting.&nbsp; This list can be overridden (for *Sephardi*
     * English transliteration for example) by setting the [transliteratedParshiosList].&nbsp; The list
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
    var transliteratedParshiosList = mapOf(
        Parsha.NONE to "",
        Parsha.BERESHIS to "Bereshis",
        Parsha.NOACH to "Noach",
        Parsha.LECH_LECHA to "Lech Lecha",
        Parsha.VAYERA to "Vayera",
        Parsha.CHAYEI_SARA to "Chayei Sara",
        Parsha.TOLDOS to "Toldos",
        Parsha.VAYETZEI to "Vayetzei",
        Parsha.VAYISHLACH to "Vayishlach",
        Parsha.VAYESHEV to "Vayeshev",
        Parsha.MIKETZ to "Miketz",
        Parsha.VAYIGASH to "Vayigash",
        Parsha.VAYECHI to "Vayechi",
        Parsha.SHEMOS to "Shemos",
        Parsha.VAERA to "Vaera",
        Parsha.BO to "Bo",
        Parsha.BESHALACH to "Beshalach",
        Parsha.YISRO to "Yisro",
        Parsha.MISHPATIM to "Mishpatim",
        Parsha.TERUMAH to "Terumah",
        Parsha.TETZAVEH to "Tetzaveh",
        Parsha.KI_SISA to "Ki Sisa",
        Parsha.VAYAKHEL to "Vayakhel",
        Parsha.PEKUDEI to "Pekudei",
        Parsha.VAYIKRA to "Vayikra",
        Parsha.TZAV to "Tzav",
        Parsha.SHMINI to "Shmini",
        Parsha.TAZRIA to "Tazria",
        Parsha.METZORA to "Metzora",
        Parsha.ACHREI_MOS to "Achrei Mos",
        Parsha.KEDOSHIM to "Kedoshim",
        Parsha.EMOR to "Emor",
        Parsha.BEHAR to "Behar",
        Parsha.BECHUKOSAI to "Bechukosai",
        Parsha.BAMIDBAR to "Bamidbar",
        Parsha.NASSO to "Nasso",
        Parsha.BEHAALOSCHA to "Beha'aloscha",
        Parsha.SHLACH to "Sh'lach",
        Parsha.KORACH to "Korach",
        Parsha.CHUKAS to "Chukas",
        Parsha.BALAK to "Balak",
        Parsha.PINCHAS to "Pinchas",
        Parsha.MATOS to "Matos",
        Parsha.MASEI to "Masei",
        Parsha.DEVARIM to "Devarim",
        Parsha.VAESCHANAN to "Vaeschanan",
        Parsha.EIKEV to "Eikev",
        Parsha.REEH to "Re'eh",
        Parsha.SHOFTIM to "Shoftim",
        Parsha.KI_SEITZEI to "Ki Seitzei",
        Parsha.KI_SAVO to "Ki Savo",
        Parsha.NITZAVIM to "Nitzavim",
        Parsha.VAYEILECH to "Vayeilech",
        Parsha.HAAZINU to "Ha'Azinu",
        Parsha.VZOS_HABERACHA to "Vezos Habracha",
        Parsha.VAYAKHEL_PEKUDEI to "Vayakhel Pekudei",
        Parsha.TAZRIA_METZORA to "Tazria Metzora",
        Parsha.ACHREI_MOS_KEDOSHIM to "Achrei Mos Kedoshim",
        Parsha.BEHAR_BECHUKOSAI to "Behar Bechukosai",
        Parsha.CHUKAS_BALAK to "Chukas Balak",
        Parsha.MATOS_MASEI to "Matos Masei",
        Parsha.NITZAVIM_VAYEILECH to "Nitzavim Vayeilech",
        Parsha.SHKALIM to "Shekalim",
        Parsha.ZACHOR to "Zachor",
        Parsha.PARA to "Parah",
        Parsha.HACHODESH to "Hachodesh",
        Parsha.SHUVA to "Shuva",
        Parsha.SHIRA to "Shira",
        Parsha.HAGADOL to "Hagadol",
        Parsha.CHAZON to "Chazon",
        Parsha.NACHAMU to "Nachamu"
    )

    /**
     * Unicode [EnumMap] of Hebrew *parshiyos*.  The list includes double and special *parshiyos* and
     * contains `"בראשית, נח, לך לך,
     * וירא, חיי שרה,
     * תולדות, ויצא, וישלח,
     * וישב, מקץ, ויגש, ויחי,
     * שמות, וארא, בא, בשלח,
     * יתרו, משפטים, תרומה,
     * תצוה, כי תשא, ויקהל,
     * פקודי, ויקרא, צו,
     * שמיני, תזריע, מצרע,
     * אחרי מות, קדושים,
     * אמור, בהר, בחקתי,
     * במדבר, נשא, בהעלתך,
     * שלח לך, קרח, חוקת, בלק,
     * פינחס, מטות, מסעי,
     * דברים, ואתחנן, עקב,
     * ראה, שופטים, כי תצא,
     * כי תבוא, נצבים, וילך,
     * האזינו, וזאת הברכה,
     * ויקהל פקודי, תזריע
     * מצרע, אחרי מות
     * קדושים, בהר בחקתי,
     * חוקת בלק, מטות מסעי,
     * נצבים וילך, שקלים,
     * זכור, פרה, החדש,
     * שובה,שירה,הגדול,
     * חזון,נחמו"`
     */
    private val hebrewParshaMap = mapOf(
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

    /**
     * Returns the list of months transliterated into Latin chars. The default list of months uses Ashkenazi
     * pronunciation in typical American English spelling. This list has a length of 14 with 3 variations for Adar -
     * "Adar", "Adar II", "Adar I"
     *
     * @return the list of months beginning in Nissan and ending in in "Adar", "Adar II", "Adar I". The default list is
     * currently ["Nissan", "Iyar", "Sivan", "Tammuz", "Av", "Elul", "Tishrei", "Cheshvan", "Kislev", "Teves",
     * "Shevat", "Adar", "Adar II", "Adar I"].
     */
    var transliteratedMonthList: Array<String> = arrayOf(
        "Nissan", "Iyar", "Sivan", "Tammuz", "Av", "Elul", "Tishrei", "Cheshvan",
        "Kislev", "Teves", "Shevat", "Adar", "Adar II", "Adar I"
    )
    /**
     * Returns the Hebrew Omer prefix.  By default it is the letter ב producing
     * בעומר, but it can be set to ל to produce
     * לעומר (or any other prefix) using the setter for [hebrewOmerPrefix].
     *
     * @return the [hebrewOmerPrefix]
     *
     * @see formatOmer
     */
    /**
     * Method to set the Hebrew Omer prefix.  By default it is the letter ב producing
     * בעומר, but it can be set to ל to produce
     * לעומר (or any other prefix)
     * @param hebrewOmerPrefix
     * the hebrewOmerPrefix to set. You can use the Unicode \u05DC to set it to ל.
     * @see formatOmer
     */
    /**
     * The Hebrew omer prefix character. It defaults to ב producing בעומר,
     * but can be set to ל to produce לעומר (or any other prefix).
     */
    var hebrewOmerPrefix: String = "ב"

    /**
     * Returns the day of Shabbos transliterated into Latin chars. The default uses Ashkenazi pronunciation "Shabbos".
     * This can be overwritten using the [transliteratedShabbosDayOfWeek] to alternate spellings such as "Shabbat" used by
     * the [formatDayOfWeek]
     *
     * @return the transliteratedShabbos. The default list of months uses Ashkenazi pronunciation "Shabbos".
     * @see formatDayOfWeek
     */
    var transliteratedShabbosDayOfWeek: String = "Shabbos"

    /**
     * Returns the list of holidays transliterated into Latin chars. This is used by the
     * [formatYomTov] when formatting the Yom Tov String. The default list of months uses
     * Ashkenazi pronunciation in typical American English spelling.
     *
     * When setting, ensure that the sequence exactly matches the list returned by the
     * default
     *
     * @return the list of transliterated holidays. The default list is currently ["Erev Pesach", "Pesach",
     * "Chol Hamoed Pesach", "Pesach Sheni", "Erev Shavuos", "Shavuos", "Seventeenth of Tammuz", "Tishah B'Av",
     * "Tu B'Av", "Erev Rosh Hashana", "Rosh Hashana", "Fast of Gedalyah", "Erev Yom Kippur", "Yom Kippur",
     * "Erev Succos", "Succos", "Chol Hamoed Succos", "Hoshana Rabbah", "Shemini Atzeres", "Simchas Torah",
     * "Erev Chanukah", "Chanukah", "Tenth of Teves", "Tu B'Shvat", "Fast of Esther", "Purim", "Shushan Purim",
     * "Purim Katan", "Rosh Chodesh", "Yom HaShoah", "Yom Hazikaron", "Yom Ha'atzmaut", "Yom Yerushalayim",
     * "Lag B'Omer","Shushan Purim Katan","Isru Chag"].
     *
     * @see transliteratedMonthList
     * @see formatYomTov
     * @see isHebrewFormat
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
     * Hebrew holiday array in the following format.<br></br>`["ערב פסח",
     * "פסח", "חול המועד
     * פסח", "פסח שני", "ערב
     * שבועות", "שבועות",
     * "שבעה עשר בתמוז",
     * "תשעה באב",
     * "ט״ו באב",
     * "ערב ראש השנה",
     * "ראש השנה",
     * "צום גדליה",
     * "ערב יום כיפור",
     * "יום כיפור",
     * "ערב סוכות",
     * "סוכות",
     * "חול המועד סוכות",
     * "הושענא רבה",
     * "שמיני עצרת",
     * "שמחת תורה",
     * "ערב חנוכה",
     * "חנוכה", "עשרה בטבת",
     * "ט״ו בשבט",
     * "תענית אסתר",
     * "פורים",
     * "פורים שושן",
     * "פורים קטן",
     * "ראש חודש",
     * "יום השואה",
     * "יום הזיכרון",
     * "יום העצמאות",
     * "יום ירושלים",
     * "ל״ג בעומר",
     * "פורים שושן קטן"]`
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
     * @see isHebrewFormat
     */
    fun formatYomTov(jewishCalendar: JewishCalendar): String {
        val index = jewishCalendar.yomTovIndex
        if (index == JewishCalendar.CHANUKAH) {
            val dayOfChanukah = jewishCalendar.dayOfChanukah
            return if (isHebrewFormat) "${formatHebrewNumber(dayOfChanukah)} ${hebrewHolidays[index]}"
            else "${transliteratedHolidayList[index]} $dayOfChanukah"
        }
        return if (index == -1) ""
        else if (isHebrewFormat) hebrewHolidays[index]
        else transliteratedHolidayList[index]
    }

    /**
     * Formats a day as Rosh Chodesh in the format of in the format of ראש
     * חודש שבט or Rosh Chodesh Shevat. If it
     * is not Rosh Chodesh, an empty `String` will be returned.
     * @param jewishCalendar the JewishCalendar
     * @return The formatted `String` in the format of ראש
     * חודש שבט or Rosh Chodesh Shevat. If it
     * is not Rosh Chodesh, an empty `String` will be returned.
     */
    fun formatRoshChodesh(jewishCalendar: JewishCalendar): String {
        if (!jewishCalendar.isRoshChodesh) {
            return ""
        }
        var month = jewishCalendar.jewishMonth
        if (jewishCalendar.jewishDayOfMonth == 30) {
            if (month < JewishDate.ADAR || (month == JewishDate.ADAR && jewishCalendar.isJewishLeapYear)) {
                month++
            } else { // roll to Nissan
                month = JewishDate.NISSAN
            }
        }

        // This method is only about formatting, so we shouldn't make any changes to the params passed in...
        val copy = jewishCalendar.copy(inIsrael = jewishCalendar.inIsrael) //force JewishCalendar.copy, not JewishDate.copy
        copy.setJewishMonth(month)
        return "${
            if (isHebrewFormat) hebrewHolidays[JewishCalendar.ROSH_CHODESH]
            else transliteratedHolidayList[JewishCalendar.ROSH_CHODESH]
        } ${formatMonth(jewishCalendar)}"
    }

    /**
     * Formats the day of week. If [Hebrew formatting][.isHebrewFormat] is set, it will display in the format
     * ראשון etc. If Hebrew formatting is not in use it will return it in the format
     * of Sunday etc. There are various formatting options that will affect the output.
     *
     * @param jewishDate the JewishDate Object
     * @return the formatted day of week
     * @see .isHebrewFormat
     * @see .isLongWeekFormat
     */
    fun formatDayOfWeek(jewishDate: JewishDate): String {
        val jewishDayOfWeek = jewishDate.gregorianLocalDate.dayOfWeek.toJewishDayOfWeek()
        return if (isHebrewFormat)
            if (isLongWeekFormat)
                hebrewDaysOfWeek[jewishDayOfWeek - 1]
            else
                if (jewishDayOfWeek == 7) formatHebrewNumber(300)
                else formatHebrewNumber(jewishDayOfWeek)
        else
            if (jewishDayOfWeek == 7)
                if (isLongWeekFormat) transliteratedShabbosDayOfWeek
                else transliteratedShabbosDayOfWeek.substring(0, 3)
            else
                weekFormat!!.format(jewishDate.gregorianLocalDate)
    }

    /**
     * Formats the Jewish date. If the formatter is set to Hebrew, it will format in the form, "day Month year" for
     * example כ״א שבט תשכ״ט, and the format
     * "21 Shevat, 5729" if not.
     *
     * @param jewishDate
     * the JewishDate to be formatted
     * @return the formatted date. If the formatter is set to Hebrew, it will format in the form, "day Month year" for
     * example כ״א שבט תשכ״ט, and the format
     * "21 Shevat, 5729" if not.
     */
    fun format(jewishDate: JewishDate): String =
        if (isHebrewFormat) "${formatHebrewNumber(jewishDate.jewishDayOfMonth)} ${formatMonth(jewishDate)} ${
            formatHebrewNumber(
                jewishDate.jewishYear
            )
        }"
        else "${jewishDate.jewishDayOfMonth} ${formatMonth(jewishDate)}, ${jewishDate.jewishYear}"

    /**
     * Returns a string of the current Hebrew month such as "Tishrei". Returns a string of the current Hebrew month such
     * as "אדר ב׳".
     *
     * @param jewishDate
     * the JewishDate to format
     * @return the formatted month name
     * @see isHebrewFormat
     * @see transliteratedMonthList
     */
    fun formatMonth(jewishDate: JewishDate): String {
        val month = jewishDate.jewishMonth
        return if (isHebrewFormat)
            if (jewishDate.isJewishLeapYear && month == JewishDate.ADAR) "${hebrewMonths[13]}${if (isUseGershGershayim) GERESH else ""}" // return Adar I, not Adar in a leap year
            else if (jewishDate.isJewishLeapYear && month == JewishDate.ADAR_II) "${hebrewMonths[12]}${if (isUseGershGershayim) GERESH else ""}"
            else hebrewMonths[month - 1]
        else
            if (jewishDate.isJewishLeapYear && month == JewishDate.ADAR) transliteratedMonthList[13] // return Adar I, not Adar in a leap year
            else transliteratedMonthList[month - 1]
    }

    /**
     * Returns a String of the Omer day in the form ל״ג בעומר if
     * Hebrew Format is set, or "Omer X" or "Lag B'Omer" if not. An empty string if there is no Omer this day.
     *
     * @param jewishCalendar
     * the JewishCalendar to be formatted
     *
     * @return a String of the Omer day in the form or an empty string if there is no Omer this day. The default
     * formatting has a ב׳ prefix that would output בעומר, but this
     * can be set via the [hebrewOmerPrefix] method to use a ל and output
     * ל״ג לעומר.
     * @see isHebrewFormat
     * @see hebrewOmerPrefix
     */
    fun formatOmer(jewishCalendar: JewishCalendar): String {
        val omer = jewishCalendar.dayOfOmer
        if (omer == -1) return ""
        return if (isHebrewFormat)
            "${formatHebrewNumber(omer)} $hebrewOmerPrefix\u05E2\u05D5\u05DE\u05E8"
        else if (omer == 33) transliteratedHolidayList[33] // if Lag B'Omer
        else "Omer $omer"
    }

    /**
     * Returns the kviah in the traditional 3 letter Hebrew format where the first letter represents the day of week of
     * Rosh Hashana, the second letter represents the lengths of Cheshvan and Kislev ([Shelaimim][JewishDate.SHELAIMIM] , [Kesidran][JewishDate.KESIDRAN] or [Chaserim][JewishDate.CHASERIM]) and the 3rd letter
     * represents the day of week of Pesach. For example 5729 (1969) would return בשה (Rosh Hashana on
     * Monday, Shelaimim, and Pesach on Thursday), while 5771 (2011) would return השג (Rosh Hashana on
     * Thursday, Shelaimim, and Pesach on Tuesday).
     *
     * @param jewishYear
     * the Jewish year
     * @return the Hebrew String such as בשה for 5729 (1969) and השג for 5771
     * (2011).
     */
    fun getFormattedKviah(jewishYear: Int): String {
        val jewishDate = JewishDate(jewishYear, JewishDate.TISHREI, 1) // set date to Rosh Hashana
        val kviah = jewishDate.cheshvanKislevKviah
        val roshHashanaDayOfweek = jewishDate.gregorianLocalDate.dayOfWeek.toJewishDayOfWeek()
        val returnValue = StringBuilder(formatHebrewNumber(roshHashanaDayOfweek))
        returnValue.append(
            when (kviah) {
                JewishDate.CHASERIM -> "\u05D7"
                JewishDate.SHELAIMIM -> "\u05E9"
                else -> "\u05DB"
            }
        )
        jewishDate.setJewishDate(jewishYear, JewishDate.NISSAN, 15) // set to Pesach of the given year
        val pesachDayOfweek = jewishDate.gregorianLocalDate.dayOfWeek.toJewishDayOfWeek()
        returnValue.append(formatHebrewNumber(pesachDayOfweek))
        return returnValue.replace(GERESH.toRegex(), "") // geresh is never used in the kviah format
        // boolean isLeapYear = JewishDate.isJewishLeapYear(jewishYear);
        // for efficiency we can avoid the expensive recalculation of the pesach day of week by adding 1 day to Rosh
        // Hashana for a 353 day year, 2 for a 354 day year, 3 for a 355 or 383 day year, 4 for a 384 day year and 5 for
        // a 385 day year
    }

    /**
     * Formats the [Daf Yomi](https://en.wikipedia.org/wiki/Daf_Yomi) Bavli in the format of
     * "עירובין נ״ב" in [Hebrew][isHebrewFormat],
     * or the transliterated format of "Eruvin 52".
     * @param daf the Daf to be formatted.
     * @return the formatted daf.
     */
    fun formatDafYomiBavli(daf: Daf): String =
        if (isHebrewFormat)
            "${daf.masechta} ${formatHebrewNumber(daf.daf)}"
        else "${daf.masechtaTransliterated} ${daf.daf}"

    /**
     * Formats the [Daf Yomi Yerushalmi](https://en.wikipedia.org/wiki/Jerusalem_Talmud#Daf_Yomi_Yerushalmi) in the format
     * of "עירובין נ״ב" in [Hebrew][isHebrewFormat], or
     * the transliterated format of "Eruvin 52".
     *
     * @param daf the Daf to be formatted.
     * @return the formatted daf.
     */
    fun formatDafYomiYerushalmi(daf: Daf?): String {
        if (daf == null)
            return if (isHebrewFormat) Daf.yerushlmiMasechtos[39]
            else Daf.yerushlmiMasechtosTransliterated[39]
        return if (isHebrewFormat) "${daf.yerushalmiMasechta} ${formatHebrewNumber(daf.daf)}"
        else "${daf.yerushlmiMasechtaTransliterated} ${daf.daf}"
    }

    /**
     * Returns a Hebrew formatted string of a number. The method can calculate from 0 - 9999.
     *
     *  * Single digit numbers such as 3, 30 and 100 will be returned with a ׳ ([Geresh](http://en.wikipedia.org/wiki/Geresh)) appended as at the end. For example ג׳,
     * ל׳ and ק׳
     *  * multi digit numbers such as 21 and 769 will be returned with a ״ ([Gershayim](http://en.wikipedia.org/wiki/Gershayim)) between the second to last and last letters. For
     * example כ״א, תשכ״ט
     *  * 15 and 16 will be returned as ט״ו and ט״ז
     *  * Single digit numbers (years assumed) such as 6000 (%1000=0) will be returned as ו׳
     * אלפים
     *  * 0 will return אפס
     *
     *
     * @param number
     * the number to be formatted. It will trow an IllegalArgumentException if the number is < 0 or > 9999.
     * @return the Hebrew formatted number such as תשכ״ט
     * @see isUseFinalFormLetters
     * @see isUseGershGershayim
     * @see isHebrewFormat
     */
    fun formatHebrewNumber(number: Int): String {
        var num = number
        val range = 0..9999
        require(num in range) { "${if (num < range.first) "negative numbers" else "numbers > ${range.last}"} can't be formatted" }
        val ALAFIM = "\u05D0\u05DC\u05E4\u05D9\u05DD"
        val EFES = "\u05D0\u05E4\u05E1"
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
        if (num == 0) return EFES // do we really need this? Should it be applicable to a date?
        val shortNumber = num % 1000 // discard thousands
        // next check for all possible single Hebrew digit years
        val singleDigitNumber =
            shortNumber < 11 ||
                    (shortNumber < 100 && shortNumber % 10 == 0) ||
                    (shortNumber <= 400 && shortNumber % 100 == 0)
        val thousands = num / 1000 // get # thousands
        val sb = StringBuilder()
        // append thousands to String
        if (num % 1000 == 0) { // in year is 5000, 4000 etc
            sb.append(jOnes[thousands])
            if (isUseGershGershayim) sb.append(GERESH)
            sb.append(" ")
            sb.append(ALAFIM) // add # of thousands plus word thousand (overide alafim boolean)
            return sb.toString()
        } else if (isUseLongHebrewYears && num >= 1000) { // if alafim boolean display thousands
            sb.append(jOnes[thousands])
            if (isUseGershGershayim) sb.append(GERESH) // append thousands quote
            sb.append(" ")
        }
        num %= 1000 // remove 1000s
        val hundreds = num / 100 // # of hundreds
        sb.append(jHundreds[hundreds]) // add hundreds to String
        num %= 100 // remove 100s
        if (num == 15) sb.append(tavTaz[0])  // special case 15
        else if (num == 16) sb.append(tavTaz[1]) // special case 16
        else {
            val tens = num / 10
            if (num % 10 == 0) { // if evenly divisable by 10
                if (!singleDigitNumber) {
                    if (isUseFinalFormLetters) sb.append(jTenEnds[tens])  // years like 5780 will end with a final form &#x05E3;
                    else sb.append(jTens[tens]) // years like 5780 will end with a regular &#x05E4;
                } else sb.append(jTens[tens]) // standard letters so years like 5050 will end with a regular nun
            } else {
                sb.append(jTens[tens])
                num %= 10
                sb.append(jOnes[num])
            }
        }
        if (isUseGershGershayim) {
            if (singleDigitNumber) sb.append(GERESH) // append single quote
            else sb.insert(sb.length - 1, GERSHAYIM) // append double quote before last digit
        }
        return sb.toString()
    }

    /**
     * Returns a String with the name of the current parsha(ios). If the formatter is set to format in Hebrew, returns
     * a string of the current parsha(ios) in Hebrew for example בראשית or
     * נצבים וילך or an empty string if there
     * are none. If not set to Hebrew, it returns a string of the parsha(ios) transliterated into Latin chars. The
     * default uses Ashkenazi pronunciation in typical American English spelling, for example Bereshis or
     * Nitzavim Vayeilech or an empty string if there are none.
     *
     * @param jewishCalendar the JewishCalendar Object
     * @return today's parsha(ios) in Hebrew for example, if the formatter is set to format in Hebrew, returns a string
     * of the current parsha(ios) in Hebrew for example בראשית or
     * נצבים וילך or an empty string if
     * there are none. If not set to Hebrew, it returns a string of the parsha(ios) transliterated into Latin
     * chars. The default uses Ashkenazi pronunciation in typical American English spelling, for example
     * Bereshis or Nitzavim Vayeilech or an empty string if there are none.
     */
    fun formatParsha(jewishCalendar: JewishCalendar): String? =
        if (isHebrewFormat) hebrewParshaMap[jewishCalendar.parshah]
        else transliteratedParshiosList[jewishCalendar.parshah]

    /**
     * Returns a String with the name of the current special parsha of Shekalim, Zachor, Parah or Hachodesh or an
     * empty String for a non-special parsha. If the formatter is set to format in Hebrew, it returns a string of
     * the current special parsha in Hebrew, for example שקלים,
     * זכור, פרה or החדש. An empty
     * string if the date is not a special parsha. If not set to Hebrew, it returns a string of the special parsha
     * transliterated into Latin chars. The default uses Ashkenazi pronunciation in typical American English spelling
     * Shekalim, Zachor, Parah or Hachodesh.
     *
     * @param jewishCalendar the JewishCalendar Object
     * @return today's special parsha. If the formatter is set to format in Hebrew, returns a string
     * of the current special parsha  in Hebrew for in the format of שקלים,
     * זכור, פרה or החדש or an empty
     * string if there are none. If not set to Hebrew, it returns a string of the special parsha transliterated
     * into Latin chars. The default uses Ashkenazi pronunciation in typical American English spelling of Shekalim,
     * Zachor, Parah or Hachodesh. An empty string if there are none.
     */
    fun formatSpecialParsha(jewishCalendar: JewishCalendar): String? =
        if (isHebrewFormat) hebrewParshaMap[jewishCalendar.specialShabbos]
        else transliteratedParshiosList[jewishCalendar.specialShabbos]

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
        const val DAY_CHALAKIM = 24 * HOUR_CHALAKIM

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
            val hours = ((adjustedChalakim / HOUR_CHALAKIM)).toInt()
            if (hours >= 6) {
                days += 1
            }
            adjustedChalakim -= (hours * HOUR_CHALAKIM.toLong())
            val minutes = (adjustedChalakim / MINUTE_CHALAKIM).toInt()
            adjustedChalakim -= minutes * MINUTE_CHALAKIM.toLong()
            return "Day: " + (days % 7) + " hours: " + hours + ", minutes " + minutes + ", chalakim: " + adjustedChalakim
        }
    }
}