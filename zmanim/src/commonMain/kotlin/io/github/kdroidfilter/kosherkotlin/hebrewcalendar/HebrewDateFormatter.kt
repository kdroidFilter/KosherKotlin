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
package io.github.kdroidfilter.kosherkotlin.hebrewcalendar

import com.kdroid.gematria.converter.toHebrewNumeral
import io.github.kdroidfilter.kosherkotlin.hebrewcalendar.HebrewMonth.*
import io.github.kdroidfilter.kosherkotlin.util.WeekFormat
import io.github.kdroidfilter.kosherkotlin.hebrewcalendar.JewishCalendar.Companion.toJewishDayOfWeek
import io.github.kdroidfilter.kosherkotlin.hebrewcalendar.JewishCalendar.Parsha
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
     * Unicode of Hebrew *parshiyos* names.
     */

    private val hebrewParshaMap = mapOf(
        Parsha.NONE to "",
        Parsha.BERESHIS to "בראשית",
        Parsha.NOACH to "נח",
        Parsha.LECH_LECHA to "לך לך",
        Parsha.VAYERA to "וירא",
        Parsha.CHAYEI_SARA to "חיי שרה",
        Parsha.TOLDOS to "תולדות",
        Parsha.VAYETZEI to "ויצא",
        Parsha.VAYISHLACH to "וישלח",
        Parsha.VAYESHEV to "וישב",
        Parsha.MIKETZ to "מקץ",
        Parsha.VAYIGASH to "ויגש",
        Parsha.VAYECHI to "ויחי",
        Parsha.SHEMOS to "שמות",
        Parsha.VAERA to "וארא",
        Parsha.BO to "בא",
        Parsha.BESHALACH to "בשלח",
        Parsha.YISRO to "יתרו",
        Parsha.MISHPATIM to "משפטים",
        Parsha.TERUMAH to "תרומה",
        Parsha.TETZAVEH to "תצוה",
        Parsha.KI_SISA to "כי תשא",
        Parsha.VAYAKHEL to "ויקהל",
        Parsha.PEKUDEI to "פקודי",
        Parsha.VAYIKRA to "ויקרא",
        Parsha.TZAV to "צו",
        Parsha.SHMINI to "שמיני",
        Parsha.TAZRIA to "תזריע",
        Parsha.METZORA to "מצרע",
        Parsha.ACHREI_MOS to "אחרי מות",
        Parsha.KEDOSHIM to "קדושים",
        Parsha.EMOR to "אמור",
        Parsha.BEHAR to "בהר",
        Parsha.BECHUKOSAI to "בחקותי",
        Parsha.BAMIDBAR to "במדבר",
        Parsha.NASSO to "נשא",
        Parsha.BEHAALOSCHA to "בהעלותך",
        Parsha.SHLACH to "שלח לך",
        Parsha.KORACH to "קרח",
        Parsha.CHUKAS to "חוקת",
        Parsha.BALAK to "בלק",
        Parsha.PINCHAS to "פנחס",
        Parsha.MATOS to "מטות",
        Parsha.MASEI to "מסעי",
        Parsha.DEVARIM to "דברים",
        Parsha.VAESCHANAN to "ואתחנן",
        Parsha.EIKEV to "עקב",
        Parsha.REEH to "ראה",
        Parsha.SHOFTIM to "שופטים",
        Parsha.KI_SEITZEI to "כי תצא",
        Parsha.KI_SAVO to "כי תבוא",
        Parsha.NITZAVIM to "נצבים",
        Parsha.VAYEILECH to "וילך",
        Parsha.HAAZINU to "האזינו",
        Parsha.VZOS_HABERACHA to "וזאת הברכה",
        Parsha.VAYAKHEL_PEKUDEI to "ויקהל פקודי",
        Parsha.TAZRIA_METZORA to "תזריע מצרע",
        Parsha.ACHREI_MOS_KEDOSHIM to "אחרי מות קדושים",
        Parsha.BEHAR_BECHUKOSAI to "בהר בחקותי",
        Parsha.CHUKAS_BALAK to "חוקת בלק",
        Parsha.MATOS_MASEI to "מטות מסעי",
        Parsha.NITZAVIM_VAYEILECH to "נצבים וילך",
        Parsha.SHKALIM to "שקלים",
        Parsha.ZACHOR to "זכור",
        Parsha.PARA to "פרה",
        Parsha.HACHODESH to "החדש",
        Parsha.SHUVA to "שובה",
        Parsha.SHIRA to "שירה",
        Parsha.HAGADOL to "הגדול",
        Parsha.CHAZON to "חזון",
        Parsha.NACHAMU to "נחמו"
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

    private val hebrewHolidays = arrayOf(
        "ערב פסח", "פסח",
        "חול המועד פסח", "פסח שני", "ערב שבועות",
        "שבועות", "שבעה עשר בתמוז",
        "תשעה באב", "ט׳ באב",
        "ערב ראש השנה", "ראש השנה",
        "צום גדליה", "ערב יום כיפור",
        "יום כיפור", "ערב סוכות",
        "סוכות", "חול המועד סוכות",
        "הושענא רבה", "שמיני עצרת",
        "שמחת תורה", "ערב חנוכה",
        "חנוכה", "עשרה בטבת",
        "ט׳ בשבט", "תענית אסתר",
        "פורים", "פורים שושן",
        "פורים קטן", "ראש חודש",
        "יום השואה", "יום הזיכרון",
        "יום העצמאות", "יום ירושלים",
        "ל״ג בעומר", "פורים שושן קטן",
        "אסרו חג"
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
        var month = jewishCalendar.hebrewLocalDate.month
        if (jewishCalendar.jewishDayOfMonth == 30) {
            month = if (month < ADAR || (month == ADAR && jewishCalendar.isJewishLeapYear)) {
                month.nextMonth
            } else { // roll to Nissan
                NISSAN
            }
        }

        // This method is only about formatting, so we shouldn't make any changes to the params passed in...
        val copy = jewishCalendar.copy(jewishMonth = jewishCalendar.hebrewLocalDate.month, inIsrael = jewishCalendar.inIsrael) //force JewishCalendar.copy, not JewishDate.copy
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
                else formatHebrewNumber(jewishDayOfWeek.toLong())
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
        if (isHebrewFormat) "${formatHebrewNumber(jewishDate.jewishDayOfMonth.toLong())} ${formatMonth(jewishDate)} ${
            formatHebrewNumber(
                jewishDate.hebrewLocalDate.year
            )
        }"
        else "${jewishDate.jewishDayOfMonth} ${formatMonth(jewishDate)}, ${jewishDate.hebrewLocalDate.year}"

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
        val month = jewishDate.hebrewLocalDate.month
        return if (isHebrewFormat)
            if (jewishDate.isJewishLeapYear && month == ADAR) "${hebrewMonths[13]}${if (isUseGershGershayim) GERESH else ""}" // return Adar I, not Adar in a leap year
            else if (jewishDate.isJewishLeapYear && month == ADAR_II) "${hebrewMonths[12]}${if (isUseGershGershayim) GERESH else ""}"
            else hebrewMonths[month.value - 1]
        else
            if (jewishDate.isJewishLeapYear && month == ADAR) transliteratedMonthList[13] // return Adar I, not Adar in a leap year
            else transliteratedMonthList[month.value - 1]
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

        return when {
            isHebrewFormat -> "${formatHebrewNumber(omer.toLong())} $hebrewOmerPrefix עומר"
            omer == 33 -> transliteratedHolidayList[33] // Lag B'Omer
            else -> "Omer $omer"
        }
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
    fun getFormattedKviah(jewishYear: Int): String = getFormattedKviah(jewishYear.toLong())

    fun getFormattedKviah(jewishYear: Long): String {
        // Set the date to Rosh Hashana of the given Jewish year
        val jewishDate = JewishDate(jewishYear, TISHREI, 1)

        // Determine the kviah (calendar type: Chaserim, Shelaimim, or Kesidrah)
        val kviah = jewishDate.cheshvanKislevKviah

        // Get the day of the week for Rosh Hashana
        val roshHashanaDayOfweek = jewishDate.gregorianLocalDate.dayOfWeek.toJewishDayOfWeek()

        // Build the formatted kviah string
        val returnValue = StringBuilder(formatHebrewNumber(roshHashanaDayOfweek.toLong()))
        returnValue.append(
            when (kviah) {
                JewishDate.CHASERIM -> "ח" // Chaserim (deficient year)
                JewishDate.SHELAIMIM -> "ש" // Shelaimim (complete year)
                else -> "כ" // Kesidrah (regular year)
            }
        )

        // Set the date to Pesach of the given Jewish year
        jewishDate.setJewishDate(jewishYear, NISSAN, 15)

        // Get the day of the week for Pesach
        val pesachDayOfweek = jewishDate.gregorianLocalDate.dayOfWeek.toJewishDayOfWeek()
        returnValue.append(formatHebrewNumber(pesachDayOfweek.toLong()))

        // Remove the Geresh character from the formatted kviah string
        return returnValue.replace(GERESH.toRegex(), "")
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
            "${daf.masechta} ${formatHebrewNumber(daf.daf.toLong())}"
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
            return if (isHebrewFormat) Daf.yerushalmiMasechtos[39]
            else Daf.yerushalmiMasechtosTransliterated[39]
        return if (isHebrewFormat) "${daf.yerushalmiMasechta} ${formatHebrewNumber(daf.daf)}"
        else "${daf.yerushalmiMasechtaTransliterated} ${daf.daf}"
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
    fun formatHebrewNumber(number: Int): String = formatHebrewNumber(number.toLong())

    fun formatHebrewNumber(number: Long): String {
       return number.toInt().toHebrewNumeral( includeGeresh = isUseGershGershayim)
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

        private const val GERESH: String = "׳"

        /**
         * The Gershayim character ("״") is similar to a double quote and is used in formatting Hebrew numbers.
         */
        private const val GERSHAYIM: String = "״"

        /**
         * List of Hebrew months in their standard format using Hebrew characters.
         */
        private val hebrewMonths: Array<String> = arrayOf(
            "ניסן", "אייר", "סיון", "תמוז", "אב", "אלול",
            "תשרי", "חשון", "כסלו", "טבת", "שבט", "אדר", "אדר ב", "אדר א"
        )

        /**
         * List of Hebrew days of the week using Hebrew characters.
         * The days are ordered starting from Sunday (first day) to Shabbat (seventh day).
         */
        private val hebrewDaysOfWeek: Array<String> = arrayOf(
            "ראשון", "שני", "שלישי", "רביעי", "חמישי", "שישי", "שבת"
        )


        private const val MINUTE_CHALAKIM = 18
        private const val HOUR_CHALAKIM = 1080
        private const val DAY_CHALAKIM = 24 * HOUR_CHALAKIM

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