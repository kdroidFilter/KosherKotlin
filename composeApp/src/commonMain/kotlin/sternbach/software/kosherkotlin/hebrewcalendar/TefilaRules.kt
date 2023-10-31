/*
 * Zmanim Java API
 * Copyright (C) 2019 - 2022 Eliyahu Hershfeld
 * Copyright (C) 2019 - 2021 Y Paritcher
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
package sternbach.software.kosherkotlin.hebrewcalendar

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek

/**
 * Tefila Rules is a utility class that covers the various *halachos* and *minhagim* regarding
 * changes to daily *tefila* / prayers, based on the Jewish calendar. This is mostly useful for use in
 * developing *siddur* type applications, but it is also valuable for *shul* calendars that set
 * *tefila* times based on if [*tachanun*](https://en.wikipedia.org/wiki/Tachanun) is
 * recited that day. There are many settings in this class to cover the vast majority of *minhagim*, but
 * there are likely some not covered here. The source for many of the *chasidishe minhagim* can be found
 * in the [Minhag Yisrael Torah](https://www.nli.org.il/he/books/NNL_ALEPH001141272/NLI) on Orach
 * Chaim 131.
 * Dates used in specific communities such as specific *yahrzeits* or a holidays like Purim Mezhbizh
 * (Medzhybizh) celebrated on 11 [*Teves*][HebrewMonth.TEVES] or [Purim Saragossa](https://en.wikipedia.org/wiki/Second_Purim#Purim_Saragossa_(18_Shevat)) celebrated on
 * the (17th or) 18th of [*Shevat*][HebrewMonth.SHEVAT] are not (and likely will not be) supported by
 * this class.
 *
 * Sample code:
 * <pre style="background: #FEF0C9; display: inline-block;">
 * TefilaRules tr = new TefilaRules();
 * JewishCalendar jewishCalendar = new JewishCalendar();
 * HebrewDateFormatter hdf = new HebrewDateFormatter();
 * jewishCalendar.setJewishDate(5783, HebrewMonth.TISHREI, 1); // Rosh Hashana
 * System.out.println(hdf.format(jewishCalendar) + ": " + tr.isTachanunRecitedShacharis(jd));
 * jewishCalendar.setJewishDate(5783, HebrewMonth.ADAR, 17);
 * System.out.println(hdf.format(jewishCalendar) + ": " + tr.isTachanunRecitedShacharis(jewishCalendar));
 * tr.setTachanunRecitedWeekOfPurim(false);
 * System.out.println(hdf.format(jewishCalendar) + ": " + tr.isTachanunRecitedShacharis(jewishCalendar));</pre>
 *
 * @author  Y. Paritcher 2019 - 2021
 * @author  Eliyahu Hershfeld 2019 - 2022
 *
 * @todo The following items may be added at a future date.
 *
 *  1. *Lamnatzaiach*
 *  1. *Mizmor Lesoda*
 *  1. *Behab*
 *  1. *Selichos*
 *  1. ...
 *
 */
class TefilaRules {
    /**
     * Is *tachanun* recited at the end Of [*Tishrei*][HebrewMonth.TISHREI].The Magen Avraham 669:1 and the Pri
     * Chadash 131:7 state that some places to not recite *tachanun* during this period. The Sh"UT Chasam Sofer on Choshen
     * Mishpat 77 writes that this is the *minhag* in Ashkenaz. The Shaarei Teshuva 131:19 quotes the Sheyarie Kneses
     * Hagdola who also states that it should not be recited. The Aderes wanted to institute saying *tachanun* during this
     * period, but was dissuaded from this by Rav Shmuel Salant who did not want to change the *minhag* in Yerushalayim.
     * The Aruch Hashulchan is of the opinion that that this *minhag* is incorrect, and it should be recited, and The Chazon
     * Ish also recited *tachanun* during this period. See the Dirshu edition of the Mishna Berurah for details.
     * @return If *tachanun* is set to be recited at the end of [*Tishrei*][HebrewMonth.TISHREI].
     */
    var isTachanunRecitedEndOfTishrei: Boolean = true

    /**
     * Is *tachanun* recited during the week after *Shavuos*. This is the opinion of the Pri Megadim
     * quoted by the Mishna Berurah. This is since *karbanos* of *Shavuos* have *tashlumim* for
     * 7 days, it is still considered like a Yom Tov. The Chazon Ish quoted in the Orchos Rabainu vol. 1 page 68
     * recited *tachanun* during this week.
     *
     * @return If *tachanun* is set to be recited during the week after Shavuos.
     */
    var isTachanunRecitedWeekAfterShavuos: Boolean = false

    /**
     * Is *tachanun* is recited on the 13th of [*Sivan*][HebrewMonth.SIVAN] ([*Yom Tov Sheni shel Galuyos*](https://en.wikipedia.org/wiki/Yom_tov_sheni_shel_galuyot) of the 7th
     * day) outside Israel. This is brought down by the Shaarie Teshuva 131:19 quoting the [Sheyarei Kneses Hagedola 131:12](https://hebrewbooks.org/pdfpager.aspx?req=41295&st=&pgnum=39)that
     * *tachanun* should not be recited on this day. Rav Shlomo Zalman Orbach in Halichos Shlomo on
     * Shavuos 12:16:25 is of the opinion that even in *chutz laaretz* it should be recited since the *yemei
     * Tashlumin* are counted based on Israel since that is where the *karbanos* are brought. Both
     * [isTachanunRecitedShacharis] and [isTachanunRecitedMincha]
     * only return false if the location is not set to [Israel][JewishCalendar.getInIsrael] and both
     * [isTachanunRecitedWeekAfterShavuos] and [isTachanunRecited13SivanOutOfIsrael] are set to false.
     *
     * @return If *tachanun* is set to be recited on the 13th of [*Sivan*][HebrewMonth.SIVAN] out of Israel.
     * @see isTachanunRecited13SivanOutOfIsrael
     * @see isTachanunRecitedWeekAfterShavuos
     */
    var isTachanunRecited13SivanOutOfIsrael: Boolean = true

    /**
     * Is *tachanun* recited on [*Pesach Sheni*][JewishCalendar.PESACH_SHENI]. The Pri Chadash 131:7 states
     * that *tachanun* should not be recited. The Aruch Hashulchan states that this is the minhag of the *sephardim*.
     * the Shaarei Efraim 10:27 also mentions that it is not recited, as does the Siddur Yaavetz (Shaar Hayesod, Chodesh Iyar).
     * The Pri Megadim (Mishbetzes Hazahav 131:15) and the Chazon Ish (Erev Pesahc Shchal Beshabos, page 203 in [Rav Sheraya
 * Devlitzky's](https://he.wikipedia.org/wiki/%D7%A9%D7%A8%D7%99%D7%94_%D7%93%D7%91%D7%9C%D7%99%D7%A6%D7%A7%D7%99) comments).
     *
     * @return If *tachanun* is recited on [*Pesach Sheni*][JewishCalendar.PESACH_SHENI].
     */
    var isTachanunRecitedPesachSheni: Boolean = false

    /**
     * Is *tachanun* recited on 15 [*Iyar*][HebrewMonth.IYAR] (*sfaika deyoma* of [*Pesach Sheni*][JewishCalendar.PESACH_SHENI]) out of Israel. If [isTachanunRecitedPesachSheni] is `true` this will be
     * ignored even if `false`.
     *
     * @return if *tachanun* is recited on 15 [*Iyar*][HebrewMonth.IYAR]  (*sfaika deyoma* of [*Pesach Sheni*][JewishCalendar.PESACH_SHENI] out of Israel. If [isTachanunRecitedPesachSheni]
     * is `true` this will be ignored even if `false`.
     * @see isTachanunRecited15IyarOutOfIsrael
     * @see isTachanunRecitedPesachSheni
     * @see isTachanunRecitedPesachSheni
     */
    var isTachanunRecited15IyarOutOfIsrael: Boolean = true

    /**
     * Is *tachanun* recited on *mincha* on *erev [Lag Baomer][JewishCalendar.LAG_BAOMER]*.
     * @return if *tachanun* is recited in *mincha* on *erev*
     * [*Lag Baomer*][JewishCalendar.LAG_BAOMER].
     * @see setTachanunRecitedMinchaErevLagBaomer
     */
    var isTachanunRecitedMinchaErevLagBaomer: Boolean = false

    /**
     * Is *tachanun* recited during the *Shivas Yemei Hamiluim*, from the 23 of [HebrewMonth.ADAR] on a non-leap-year or [*Adar II*][HebrewMonth.ADAR_II] on a
     * leap year to the end of the month. Some *chasidishe* communities do not say *tachanun*
     * during this week. See [Darkei Chaim Veshalom 191](https://hebrewbooks.org/pdfpager.aspx?req=4692&st=&pgnum=70).
     * @return if *tachanun* is recited during the *Shivas Yemei Hamiluim*, from the 23 of [HebrewMonth.ADAR] on a non-leap-year or [*Adar II*][HebrewMonth.ADAR_II]
     * on a leap year to the end of the month.
     * @see isTachanunRecitedShivasYemeiHamiluim
     */
    var isTachanunRecitedShivasYemeiHamiluim: Boolean = true

    /**
     * Is *tachanun* recited during the *sefira* week of *Hod* (14 - 20 [*Iyar*][HebrewMonth.IYAR],
     * or the 29th - 35th of the [*Omer*][JewishCalendar.getDayOfOmer]). Some *chasidishe* communities
     * do not recite *tachanun* during this week. See Minhag Yisrael Torah 131:Iyar.
     * @return If *tachanun* is set to be recited during the *sefira* week of *Hod* (14 - 20 [HebrewMonth.IYAR], or the 29th - 35th of the [*Omer*][JewishCalendar.dayOfOmer]).
     * @see isTachanunRecitedWeekOfHod
     */
    var isTachanunRecitedWeekOfHod: Boolean = true
    /**
     * Is *tachanun* recited during the week of Purim, from the 11th through the 17th of [HebrewMonth.ADAR] (on a non-leap year, or [*Adar II*][HebrewMonth.ADAR_II] on a leap year). Some
     * *chasidishe* communities do not recite *tachanun* during this period. See the [Minhag Yisrael Torah](https://www.nli.org.il/he/books/NNL_ALEPH001141272/NLI) 131 and [Darkei Chaim Veshalom 191](https://hebrewbooks.org/pdfpager.aspx?req=4692&st=&pgnum=70)who discuss the
     * *minhag* not to recite *tachanun*. Also see the [Mishmeres Shalom (Hadras Shalom)](https://hebrewbooks.org/pdfpager.aspx?req=8944&st=&pgnum=160) who discusses the
     * *minhag* of not reciting it on the 16th and 17th.
     * @return If *tachanun* is set to be recited during the week of Purim from the 11th through the 17th of [HebrewMonth.ADAR] (on a non-leap year, or [*Adar II*][HebrewMonth.ADAR_II] on a leap year).
     * @see .setTachanunRecitedWeekOfPurim
     */
    /**
     * Sets if *tachanun* should be recited during the week of Purim from the 11th through the 17th of [HebrewMonth.ADAR] (on a non-leap year), or [*Adar II*][HebrewMonth.ADAR_II] (on a leap year).
     * @param tachanunRecitedWeekOfPurim Sets if *tachanun* is to recited during the week of Purim from the 11th
     * through the 17th of [*Adar*][HebrewMonth.ADAR] (on a non-leap year), or [*Adar II*][HebrewMonth.ADAR_II] (on a leap year). Some *chasidishe* communities do not recite *tachanun*
     * during this period.
     * @see .isTachanunRecitedWeekOfPurim
     */
    /**
     * The default value is `true`.
     * @see .isTachanunRecitedWeekOfPurim
     * @see .setTachanunRecitedWeekOfPurim
     */
    var isTachanunRecitedWeekOfPurim: Boolean = true
    /**
     * Is *tachanun* recited on Fridays. Some *chasidishe* communities do not recite
     * *tachanun* on Fridays. See [Likutei
 * Maharich Vol 2 Seder Hanhagos Erev Shabbos](https://hebrewbooks.org/pdfpager.aspx?req=41190&st=&pgnum=10). This is also the *minhag* in Satmar.
     * @return if *tachanun* is recited on Fridays.
     * @see .setTachanunRecitedFridays
     */
    /**
     * Sets if *tachanun* should be recited on Fridays.
     * @param tachanunRecitedFridays sets if *tachanun* should be recited on Fridays. Some *chasidishe*
     * communities do not recite *tachanun* on Fridays.
     * @see .isTachanunRecitedFridays
     */
    /**
     * The default value is `true`.
     * @see .isTachanunRecitedFridays
     * @see .setTachanunRecitedFridays
     */
    var isTachanunRecitedFridays: Boolean = true
    /**
     * Is *tachanun* recited on Sundays. Some *chasidishe* communities do not recite
     * *tachanun* on Sundays. See [Likutei
 * Maharich Vol 2 Seder Hanhagos Erev Shabbos](https://hebrewbooks.org/pdfpager.aspx?req=41190&st=&pgnum=10).
     * @return if *tachanun* is recited on Sundays.
     * @see .setTachanunRecitedSundays
     */
    /**
     * Sets if *tachanun* should be recited on Sundays.
     * @param tachanunRecitedSundays sets if *tachanun* should be recited on Sundays. Some *chasidishe*
     * communities do not recite *tachanun* on Sundays.
     * @see .isTachanunRecitedSundays
     */
    /**
     * The default value is `true`.
     * @see .isTachanunRecitedSundays
     * @see .setTachanunRecitedSundays
     */
    var isTachanunRecitedSundays: Boolean = true
    /**
     * Is *tachanun* recited in *Mincha* the entire year. Some *chasidishe* communities do not recite
     * *tachanun* by *Mincha* all year round. See[Nemukei Orach Chaim 131:3](https://hebrewbooks.org/pdfpager.aspx?req=4751&st=&pgnum=105).
     * @return if *tachanun* is recited in *Mincha* the entire year.
     * @see .setTachanunRecitedMinchaAllYear
     */
    /**
     * Sets if *tachanun* should be recited in *Mincha* the entire year.
     * @param tachanunRecitedMinchaAllYear sets if *tachanun* should be recited by *mincha* all year. If set
     * to false, [.isTachanunRecitedMincha] will always return false. If set to true (the
     * default), it will use the regular rules.
     * @see .isTachanunRecitedMinchaAllYear
     */
    /**
     * The default value is `true`.
     * @see .isTachanunRecitedMinchaAllYear
     * @see .setTachanunRecitedMinchaAllYear
     */
    var isTachanunRecitedMinchaAllYear: Boolean = true

    /**
     * Returns if *tachanun* is recited during *shacharis* on the day in question. See the many
     * *minhag* based settings that are available in this class.
     *
     * @param jewishCalendar the Jewish calendar day.
     * @return if *tachanun* is recited during *shacharis*.
     * @see .isTachanunRecitedMincha
     */
    fun isTachanunRecitedShacharis(jewishCalendar: JewishCalendar): Boolean {
        val holidayIndex = jewishCalendar.yomTovIndex
        val day = jewishCalendar.jewishDayOfMonth
        val month = jewishCalendar.hebrewLocalDate.month
        return !(
                jewishCalendar.gregorianLocalDate.dayOfWeek == DayOfWeek.SATURDAY ||
                        !isTachanunRecitedSundays && jewishCalendar.gregorianLocalDate.dayOfWeek == DayOfWeek.SUNDAY ||
                        !isTachanunRecitedFridays && jewishCalendar.gregorianLocalDate.dayOfWeek == DayOfWeek.FRIDAY ||
                        month == HebrewMonth.NISSAN ||
                        month == HebrewMonth.TISHREI &&
                        (
                                !isTachanunRecitedEndOfTishrei && day > 8 ||
                                        isTachanunRecitedEndOfTishrei && day in 9..21/*8 < day < 22*/
                                ) ||
                        month == HebrewMonth.SIVAN && (
                        isTachanunRecitedWeekAfterShavuos && day < 7 ||
                                !isTachanunRecitedWeekAfterShavuos &&
                                day < if (!jewishCalendar.inIsrael && !isTachanunRecited13SivanOutOfIsrael) 14 else 13
                        ) ||
                        jewishCalendar.isYomTov &&
                        (
                                !jewishCalendar.isTaanis ||
                                        !isTachanunRecitedPesachSheni && holidayIndex == JewishCalendar.PESACH_SHENI
                                ) || // Erev YT is included in isYomTov
                        !jewishCalendar.inIsrael &&
                        !isTachanunRecitedPesachSheni &&
                        !isTachanunRecited15IyarOutOfIsrael &&
                        month == HebrewMonth.IYAR && day == 15 ||
                        holidayIndex == JewishCalendar.TISHA_BEAV ||
                        jewishCalendar.isIsruChag ||
                        jewishCalendar.isRoshChodesh ||
                        !isTachanunRecitedShivasYemeiHamiluim && (
                        !jewishCalendar.isJewishLeapYear && month == HebrewMonth.ADAR ||
                                jewishCalendar.isJewishLeapYear && month == HebrewMonth.ADAR_II
                        ) && day > 22 ||
                        !isTachanunRecitedWeekOfPurim &&
                        (!jewishCalendar.isJewishLeapYear && month == HebrewMonth.ADAR ||
                                jewishCalendar.isJewishLeapYear && month == HebrewMonth.ADAR_II
                                ) &&
                        day in 11..17/*10 < day < 18*/ ||
                        jewishCalendar.isUseModernHolidays &&
                        (
                                holidayIndex == JewishCalendar.YOM_HAATZMAUT ||
                                        holidayIndex == JewishCalendar.YOM_YERUSHALAYIM
                                ) ||
                        !isTachanunRecitedWeekOfHod &&
                        (month == HebrewMonth.IYAR) &&
                        day in 14..20/*13 < day < 21*/
                )
    }

    /**
     * Returns if *tachanun* is recited during *mincha* on the day in question.
     *
     * @param jewishCalendar the Jewish LocalDate day.
     * @return if *tachanun* is recited during *mincha*.
     * @see .isTachanunRecitedShacharis
     */
    fun isTachanunRecitedMincha(jewishCalendar: JewishCalendar): Boolean {
        val tomorrow = jewishCalendar.copy(jewishMonth = jewishCalendar.hebrewLocalDate.month, inIsrael = jewishCalendar.inIsrael) //force JewishCalendar.copy, not JewishDate.copy
        tomorrow.forward(DateTimeUnit.DAY, 1)
        val yomTovIndex = tomorrow.yomTovIndex
        return !(
                !isTachanunRecitedMinchaAllYear ||
                        jewishCalendar.gregorianLocalDate.dayOfWeek == DayOfWeek.FRIDAY ||
                        !isTachanunRecitedShacharis(jewishCalendar) ||
                        !isTachanunRecitedShacharis(tomorrow) &&
                        yomTovIndex != JewishCalendar.EREV_ROSH_HASHANA &&
                        yomTovIndex != JewishCalendar.EREV_YOM_KIPPUR &&
                        yomTovIndex != JewishCalendar.PESACH_SHENI ||
                        !isTachanunRecitedMinchaErevLagBaomer && yomTovIndex == JewishCalendar.LAG_BAOMER
                )
    }

    /**
     * Returns if it is the Jewish day (starting the evening before) to start reciting *Vesein Tal Umatar Livracha*
     * (*Sheailas Geshamim*). In Israel this is the 7th day of [*Marcheshvan*][HebrewMonth.CHESHVAN].
     * Outside Israel recitation starts on the evening of December 4th (or 5th if it is the year before a civil leap year)
     * in the 21st century and shifts a day forward every century not evenly divisible by 400. This method will return true
     * if *vesein tal umatar* on the current Jewish date that starts on the previous night, so Dec 5/6 will be
     * returned by this method in the 21st century. *vesein tal umatar* is not recited on *Shabbos* and the
     * start date will be delayed a day when the start day is on a *Shabbos* (this can only occur out of Israel).
     *
     * @param jewishCalendar the Jewish calendar day.
     *
     * @return true if it is the first Jewish day (starting the prior evening of reciting *Vesein Tal Umatar Livracha*
     * (*Sheailas Geshamim*).
     *
     * @see .isVeseinTalUmatarStartingTonight
     * @see .isVeseinTalUmatarRecited
     */
    fun isVeseinTalUmatarStartDate(jewishCalendar: JewishCalendar): Boolean =
        if (jewishCalendar.inIsrael) jewishCalendar.hebrewLocalDate.month == HebrewMonth.CHESHVAN && jewishCalendar.jewishDayOfMonth == 7 // The 7th Cheshvan can't occur on Shabbos, so always return true for 7 Cheshvan
        else when (jewishCalendar.gregorianLocalDate.dayOfWeek) {
            DayOfWeek.SATURDAY -> false //Not recited on Friday night
            DayOfWeek.SUNDAY -> jewishCalendar.tekufasTishreiElapsedDays == 48 || jewishCalendar.tekufasTishreiElapsedDays == 47 // When starting on Sunday, it can be the start date or delayed from Shabbos
            else -> jewishCalendar.tekufasTishreiElapsedDays == 47
        }

    /**
     * Returns if true if tonight is the first night to start reciting *Vesein Tal Umatar Livracha* (
     * *Sheailas Geshamim*). In Israel this is the 7th day of [*Marcheshvan*][HebrewMonth.CHESHVAN] (so the 6th will return true). Outside Israel recitation starts on the evening
     * of December 4th (or 5th if it is the year before a civil leap year) in the 21st century and shifts a
     * day forward every century not evenly divisible by 400. *Vesein tal umatar* is not recited on
     * *Shabbos* and the start date will be delayed a day when the start day is on a *Shabbos*
     * (this can only occur out of Israel).
     *
     * @param jewishCalendar the Jewish calendar day.
     *
     * @return true if it is the first Jewish day (starting the prior evening of reciting *Vesein Tal Umatar
     * Livracha* (*Sheailas Geshamim*).
     *
     * @see isVeseinTalUmatarStartDate TODO seems like these can be dried up
     * @see isVeseinTalUmatarRecited
     */
    fun isVeseinTalUmatarStartingTonight(jewishCalendar: JewishCalendar): Boolean =
        if (jewishCalendar.inIsrael)
            jewishCalendar.hebrewLocalDate.month == HebrewMonth.CHESHVAN && jewishCalendar.jewishDayOfMonth == 6 // The 7th Cheshvan can't occur on Shabbos, so always return true for 6 Cheshvan
        else {
            // When starting on motzai Shabbos, it can be the start date or delayed from Friday night
            when (jewishCalendar.gregorianLocalDate.dayOfWeek) {
                DayOfWeek.FRIDAY -> false //Not recited on Friday night
                DayOfWeek.SATURDAY ->
                    jewishCalendar.tekufasTishreiElapsedDays == 47 || jewishCalendar.tekufasTishreiElapsedDays == 46
                else -> jewishCalendar.tekufasTishreiElapsedDays == 46
            }
        }

    /**
     * Returns if *Vesein Tal Umatar Livracha* (*Sheailas Geshamim*) is recited. This will return
     * true for the entire season, even on *Shabbos* when it is not recited.
     *
     * @param jewishCalendar the Jewish calendar day.
     *
     * @return true if *Vesein Tal Umatar Livracha* (*Sheailas Geshamim*) is recited.
     *
     * @see .isVeseinTalUmatarStartDate
     * @see .isVeseinTalUmatarStartingTonight
     */
    fun isVeseinTalUmatarRecited(jewishCalendar: JewishCalendar): Boolean = when {
        jewishCalendar.hebrewLocalDate.month == HebrewMonth.NISSAN && jewishCalendar.jewishDayOfMonth < 15 -> true
        jewishCalendar.hebrewLocalDate.month < HebrewMonth.CHESHVAN -> false
        jewishCalendar.inIsrael -> jewishCalendar.hebrewLocalDate.month != HebrewMonth.CHESHVAN || jewishCalendar.jewishDayOfMonth >= 7
        else -> jewishCalendar.tekufasTishreiElapsedDays >= 47
    }

    /**
     * Returns if *Vesein Beracha* is recited. It is recited from 15 [*Nissan*][HebrewMonth.NISSAN] to the
     * point that [*vesein tal umatar* is recited][.isVeseinTalUmatarRecited].
     *
     * @param jewishCalendar the Jewish calendar day.
     * @return true if *Vesein Beracha* is recited.
     * @see .isVeseinTalUmatarRecited
     */
    fun isVeseinBerachaRecited(jewishCalendar: JewishCalendar): Boolean = !isVeseinTalUmatarRecited(jewishCalendar)

    /**
     * Returns if the date is the start date for reciting *Mashiv Haruach Umorid Hageshem*. The date is 22
     * [*Tishrei*][HebrewMonth.TISHREI].
     *
     * @param jewishCalendar the Jewish calendar day.
     * @return true if the date is the start date for reciting *Mashiv Haruach Umorid Hageshem*.
     * @see .isMashivHaruachEndDate
     * @see .isMashivHaruachRecited
     */
    fun isMashivHaruachStartDate(jewishCalendar: JewishCalendar): Boolean =
        jewishCalendar.hebrewLocalDate.month == HebrewMonth.TISHREI && jewishCalendar.jewishDayOfMonth == 22

    /**
     * Returns if the date is the end date for reciting *Mashiv Haruach Umorid Hageshem*. The date is 15
     * [*Nissan*][HebrewMonth.NISSAN].
     *
     * @param jewishCalendar the Jewish calendar day.
     * @return true if the date is the end date for reciting *Mashiv Haruach Umorid Hageshem*.
     * @see isMashivHaruachStartDate
     * @see isMashivHaruachRecited
     */
    fun isMashivHaruachEndDate(jewishCalendar: JewishCalendar): Boolean =
        jewishCalendar.hebrewLocalDate.month == HebrewMonth.NISSAN && jewishCalendar.jewishDayOfMonth == 15

    /**
     * Returns if *Mashiv Haruach Umorid Hageshem* is recited. This period starts on 22 [HebrewMonth.TISHREI] and ends on the 15th day of [*Nissan*][HebrewMonth.NISSAN].
     *
     * @param jewishCalendar the Jewish calendar day.
     * @return true if *Mashiv Haruach Umorid Hageshem* is recited.
     * @see .isMashivHaruachStartDate
     * @see .isMashivHaruachEndDate
     */
    fun isMashivHaruachRecited(jewishCalendar: JewishCalendar): Boolean = jewishCalendar.isBetween(
        JewishDate(jewishCalendar.hebrewLocalDate.month.value, HebrewMonth.TISHREI, 22),
        JewishDate(jewishCalendar.hebrewLocalDate.month.value, HebrewMonth.NISSAN, 15)
    )

    fun JewishCalendar.isBetween(
        startDate: JewishDate,
        endDate: JewishDate
    ) = this > startDate && this < endDate

    /**
     * Returns if *Morid Hatal* (or the lack of reciting *Mashiv Haruach* following *nussach Ashkenaz*) is
     * recited. This period starts on the 15th day of [*Nissan*][HebrewMonth.NISSAN] and ends on 22 [HebrewMonth.TISHREI].
     *
     * @param jewishCalendar the Jewish calendar day.
     *
     * @return true if *Morid Hatal* (or the lack of reciting *Mashiv Haruach* following *nussach Ashkenaz*) is recited.
     */
    fun isMoridHatalRecited(jewishCalendar: JewishCalendar): Boolean = !isMashivHaruachRecited(jewishCalendar) ||
            isMashivHaruachStartDate(jewishCalendar) ||
            isMashivHaruachEndDate(jewishCalendar)

    /**
     * Returns if *Hallel* is recited on the day in question. This will return true for both *Hallel shalem*
     * and *Chatzi Hallel*. See [.isHallelShalemRecited] to know if the complete *Hallel*
     * is recited.
     *
     * @param jewishCalendar the Jewish calendar day.
     * @return if *Hallel* is recited.
     * @see isHallelShalemRecited
     */
    fun isHallelRecited(jewishCalendar: JewishCalendar): Boolean {
        val day = jewishCalendar.jewishDayOfMonth
        val month = jewishCalendar.hebrewLocalDate.month
        val holidayIndex = jewishCalendar.yomTovIndex
        val inIsrael: Boolean = jewishCalendar.inIsrael
        return jewishCalendar.isRoshChodesh || //RH returns false for RC
            jewishCalendar.isChanukah ||
                month == HebrewMonth.NISSAN && day >= 15 && (inIsrael && day <= 21 || !inIsrael && day <= 22) ||

                        month == HebrewMonth.IYAR &&
                        jewishCalendar.isUseModernHolidays &&
                        (holidayIndex == JewishCalendar.YOM_HAATZMAUT || holidayIndex == JewishCalendar.YOM_YERUSHALAYIM) ||

                        (month == HebrewMonth.SIVAN && day == 6 || !inIsrael && day == 7) ||
                        month == HebrewMonth.TISHREI && day >= 15 && (day <= 22 || !inIsrael && day <= 23)

    }

    /**
     * Returns if *hallel shalem* is recited on the day in question. This will always return false if [isHallelRecited] returns false.
     *
     * @param jewishCalendar the Jewish calendar day.
     * @return if *hallel shalem* is recited.
     * @see .isHallelRecited
     */
    fun isHallelShalemRecited(jewishCalendar: JewishCalendar): Boolean {
        val day = jewishCalendar.jewishDayOfMonth
        val month = jewishCalendar.hebrewLocalDate.month
        val inIsrael: Boolean = jewishCalendar.inIsrael
        return isHallelRecited(jewishCalendar) &&
                !(
                        jewishCalendar.isRoshChodesh && !jewishCalendar.isChanukah ||
                                month == HebrewMonth.NISSAN && day > if(inIsrael) 15 else 16
                        )
    }

    /**
     * Returns if [*Al HaNissim*](https://en.wikipedia.org/wiki/Al_HaNissim) is recited on the day in question.
     *
     * @param jewishCalendar the Jewish calendar day.
     * @return if *al hanissim* is recited.
     * @see JewishCalendar.isChanukah
     * @see JewishCalendar.isPurim
     * @see JewishCalendar.getIsMukafChoma
     */
    fun isAlHanissimRecited(jewishCalendar: JewishCalendar): Boolean =
        jewishCalendar.isPurim || jewishCalendar.isChanukah

    /**
     * Returns if *Yaaleh Veyavo* is recited on the day in question.
     *
     * @param jewishCalendar the Jewish calendar day.
     * @return if *Yaaleh Veyavo* is recited.
     * @see JewishCalendar.isPesach
     * @see JewishCalendar.isShavuos
     * @see JewishCalendar.isRoshHashana
     * @see JewishCalendar.isYomKippur
     * @see JewishCalendar.isSuccos
     * @see JewishCalendar.isShminiAtzeres
     * @see JewishCalendar.isSimchasTorah
     * @see JewishCalendar.isRoshChodesh
     */
    fun isYaalehVeyavoRecited(jewishCalendar: JewishCalendar): Boolean = jewishCalendar.isPesach ||
            jewishCalendar.isShavuos ||
            jewishCalendar.isRoshHashana ||
            jewishCalendar.isYomKippur ||
            jewishCalendar.isSuccos ||
            jewishCalendar.isShminiAtzeres ||
            jewishCalendar.isSimchasTorah ||
            jewishCalendar.isRoshChodesh
}