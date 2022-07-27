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
package com.kosherjava.zmanim.hebrewcalendar;

import java.util.Calendar;

/**
 * Tefila Rules is a utility class that covers the various <em>halachos</em> and <em>minhagim</em> regarding
 * changes to daily <em>tefila</em> / prayers, based on the Jewish calendar. This is mostly useful for use in
 * developing <em></em>, but it is also valuble for <em>shul</em> calendars that set <em>tefila</em> time based
 * on if <em>tachanun</em> is recited that day.
 * 
 * @author &copy; Y. Paritcher 2019 - 2021
 * @author &copy; Eliyahu Hershfeld 2019 - 2022
 * 
 * @todo The following items may be added.
 * <ol>
 * <li>Lamnatzaiach</li>
 * </ol>
 */
public class TefilaRules {
	/**
	 * The default value is <code>true</code>.
	 * @see #isTachanunRecitedEndOfTishrei()
	 * @see #setTachanunRecitedEndOfTishrei(boolean)
	 */
	private boolean tachanunRecitedEndOfTishrei = true;
	
	/**
	 * The default value is <code>false</code>.
	 * @see #isTachanunRecitedWeekAfterShavuos()
	 * @see #setTachanunRecitedWeekAfterShavuos(boolean)
	 */
	private boolean tachanunRecitedWeekAfterShavuos = false;
	
	/**
	 * The default value is <code>true</code>.
	 * @see #isTachanunRecited13SivanOutOfIsrael()
	 * @see #setTachanunRecited13SivanOutOfIsrael(boolean)
	 */
	private boolean tachanunRecited13SivanOutOfIsrael = true;
	
	/**
	 * The default value is <code>false</code>.
	 * @see #isTachanunRecitedPesachSheni()
	 * @see #setTachanunRecitedPesachSheni(boolean)
	 */
	private boolean tachanunRecitedPesachSheni = false;
	
	/**
	 * The default value is <code>true</code>.
	 * @see #isTachanunRecited15IyarOutOfIsrael()
	 * @see #setTachanunRecited15IyarOutOfIsrael(boolean)
	 */
	private boolean tachanunRecited15IyarOutOfIsrael = true;
	
	/**
	 * The default value is <code>false</code>.
	 * @see #isTachanunRecitedMinchaErevLagBaomer()
	 * @see #setTachanunRecitedMinchaErevLagBaomer(boolean)
	 */
	private boolean tachanunRecitedMinchaErevLagBaomer = false;
	
	/**
	 * The default value is <code>true</code>.
	 * @see #isTachanunRecitedShivasYemeiHamiluim()
	 * @see #setTachanunRecitedShivasYemeiHamiluim(boolean)
	 */
	private boolean tachanunRecitedShivasYemeiHamiluim = true;
	
	/**
	 * The default value is <code>true</code>.
	 * @see #isTachanunRecitedWeekOfHod()
	 * @see #setTachanunRecitedWeekOfHod(boolean)
	 */
	private boolean tachanunRecitedWeekOfHod = true;
	
	/**
	 * The default value is <code>true</code>.
	 * @see #isTachanunRecitedWeekOfPurim()
	 * @see #setTachanunRecitedWeekOfPurim(boolean)
	 */
	private boolean tachanunRecitedWeekOfPurim = true;
	
	/**
	 * The default value is <code>true</code>.
	 * @see #isTachanunRecitedFridays()
	 * @see #setTachanunRecitedFridays(boolean)
	 */
	private boolean tachanunRecitedFridays = true;
	
	/**
	 * The default value is <code>true</code>.
	 * @see #isTachanunRecitedSundays()
	 * @see #setTachanunRecitedSundays(boolean)
	 */
	private boolean tachanunRecitedSundays = true;
	
	/**
	 * The default value is <code>true</code>.
	 * @see #isTachanunRecitedMinchaAllYear()
	 * @see #setTachanunRecitedMinchaAllYear(boolean)
	 */
	private boolean tachanunRecitedMinchaAllYear = true;

	/**
	 * Returns if <em>tachanun</em> is recited during <em>shacharis</em> on the day in question. See the many
	 * <em>minhag</em> based settings that are available in this class.
	 * @param jewishCalendar the Jewish calendar day.
	 * @return if <em>tachanun</em> is recited during <em>shacharis</em>.
	 */
	public boolean isTachanunRecitedShacharis(JewishCalendar jewishCalendar) {
		int holidayIndex = jewishCalendar.getYomTovIndex();
		int day = jewishCalendar.getJewishDayOfMonth();
		int month = jewishCalendar.getJewishMonth();

		if (jewishCalendar.getDayOfWeek() == Calendar.SATURDAY
				|| (!tachanunRecitedSundays && jewishCalendar.getDayOfWeek() == Calendar.SUNDAY)
				|| (!tachanunRecitedFridays && jewishCalendar.getDayOfWeek() == Calendar.FRIDAY)
				|| month == JewishDate.NISSAN
				|| (month == JewishDate.TISHREI && ((!tachanunRecitedEndOfTishrei && day > 8)
				|| (tachanunRecitedEndOfTishrei && (day > 8 && day < 22))))
				|| (month == JewishDate.SIVAN && (tachanunRecitedWeekAfterShavuos && day < 7
						|| !tachanunRecitedWeekAfterShavuos && day < (!jewishCalendar.getInIsrael()
								&& !tachanunRecited13SivanOutOfIsrael ? 14: 13)))
				|| (jewishCalendar.isYomTov() && (! jewishCalendar.isTaanis()
						|| (!tachanunRecitedPesachSheni && holidayIndex == JewishCalendar.PESACH_SHENI))) // Erev YT is included in isYomTov()
				|| (!jewishCalendar.getInIsrael() && !tachanunRecitedPesachSheni && !tachanunRecited15IyarOutOfIsrael
						&& jewishCalendar.getJewishMonth() == JewishDate.IYAR && day == 15)
				|| holidayIndex == JewishCalendar.TISHA_BEAV || jewishCalendar.isIsruChag()
				|| jewishCalendar.isRoshChodesh()
				|| (!tachanunRecitedShivasYemeiHamiluim &&
						((!jewishCalendar.isJewishLeapYear() && month == JewishDate.ADAR)
								|| (jewishCalendar.isJewishLeapYear() && month == JewishDate.ADAR_II)) && day > 22)
				|| (!tachanunRecitedWeekOfPurim &&
						((!jewishCalendar.isJewishLeapYear() && month == JewishDate.ADAR)
								|| (jewishCalendar.isJewishLeapYear() && month == JewishDate.ADAR_II)) && day > 10 && day < 18)
				|| (jewishCalendar.isUseModernHolidays()
						&& (holidayIndex == JewishCalendar.YOM_HAATZMAUT || holidayIndex == JewishCalendar.YOM_YERUSHALAYIM))
				|| (!tachanunRecitedWeekOfHod && month == JewishDate.IYAR && day > 13 && day < 21)) {
			return false;
		}
		return true;
	}

	/**
	 * Returns if <em>tachanun</em> is recited during <em>mincha</em>.
	 * 
	 * @param jewishCalendar the Jewish calendar day.
	 * 
	 * @return if <em>tachanun</em> is recited during <em>mincha</em>.
	 */
	public boolean isTachanunRecitedMincha(JewishCalendar jewishCalendar) {
		JewishCalendar tomorrow = new JewishCalendar();
		tomorrow = (JewishCalendar) jewishCalendar.clone();
		tomorrow.forward(Calendar.DATE, 1);
		
		if (!tachanunRecitedMinchaAllYear
					|| jewishCalendar.getDayOfWeek() == Calendar.FRIDAY
					|| ! isTachanunRecitedShacharis(jewishCalendar) 
					|| (! isTachanunRecitedShacharis(tomorrow) && 
							!(tomorrow.getYomTovIndex() == JewishCalendar.EREV_ROSH_HASHANA) &&
							!(tomorrow.getYomTovIndex() == JewishCalendar.EREV_YOM_KIPPUR) &&
							!(tomorrow.getYomTovIndex() == JewishCalendar.PESACH_SHENI))
					|| ! tachanunRecitedMinchaErevLagBaomer && tomorrow.getYomTovIndex() == JewishCalendar.LAG_BAOMER) {
			return false;
		}
		return true;
	}
	
	/**
	 * Returns if it is the Jewish day (starting the evening before) to start reciting <em>Vesein Tal Umatar Livracha</em>
	 * (<em>Sheailas Geshamim</em>). In Israel this is the 7th day of <em>Marcheshvan</em>. Outside Israel recitation
	 * starts on the evening of December 4th (or 5th if it is the year before a civil leap year) in the 21st century and
	 * shifts a day forward every century not evenly divisible by 400. This method will return true if <em>vesein tal
	 * umatar</em> on the current Jewish date that starts on the previous night, so Dec 5/6 will be returned by this method
	 * in the 21st century. <em>vesein tal umatar</em> is not recited on <em>Shabbos</em> and the start date will be
	 * delayed a day when the start day is on a <em>Shabbos</em> (this can only occur out of Israel).
	 * 
	 * @param jewishCalendar the Jewish calendar day.
	 * 
	 * @return true if it is the first Jewish day (starting the prior evening of reciting <em>Vesein Tal Umatar Livracha</em>
	 *         (<em>Sheailas Geshamim</em>).
	 * 
	 * @see #isVeseinTalUmatarStartingTonight(JewishCalendar)
	 * @see #isVeseinTalUmatarRecited(JewishCalendar)
	 */
	public boolean isVeseinTalUmatarStartDate(JewishCalendar jewishCalendar) {
		if (jewishCalendar.getInIsrael()) {
			 // The 7th Cheshvan can't occur on Shabbos, so always return true for 7 Cheshvan
			if (jewishCalendar.getJewishMonth() == JewishDate.CHESHVAN && jewishCalendar.getJewishDayOfMonth() == 7) {
				return true;
			}
		} else {
			if (jewishCalendar.getDayOfWeek() == Calendar.SATURDAY) { //Not recited on Friday night
				return false;
			}
			if(jewishCalendar.getDayOfWeek() == Calendar.SUNDAY) { // When starting on Sunday, it can be the start date or delayed from Shabbos
				return jewishCalendar.getTekufasTishreiElapsedDays() == 48 || jewishCalendar.getTekufasTishreiElapsedDays() == 47;
			} else {
				return jewishCalendar.getTekufasTishreiElapsedDays() == 47;
			}
		}
		return false; // keep the compiler happy
	}
	
	/**
	 * Returns if true if tonight is the first night to start reciting <em>Vesein Tal Umatar Livracha</em> (
	 * <em>Sheailas Geshamim</em>). In Israel this is the 7th day of <em>Marcheshvan</em> (so the 6th will return
	 * true). Outside Israel recitation starts on the evening of December 4th (or 5th if it is the year before a
	 * civil leap year) in the 21st century and shifts a day forward every century not evenly divisible by 400.
	 * <em>Vesein tal umatar</em> is not recited on <em>Shabbos</em> and the start date will be delayed a day when
	 * the start day is on a <em>Shabbos</em> (this can only occur out of Israel).
	 * 
	 * @param jewishCalendar the Jewish calendar day.
	 * 
	 * @return true if it is the first Jewish day (starting the prior evening of reciting <em>Vesein Tal Umatar
	 * Livracha</em> (<em>Sheailas Geshamim</em>).
	 * 
	 * @see #isVeseinTalUmatarStartDate(JewishCalendar)
	 * @see #isVeseinTalUmatarRecited(JewishCalendar)
	 */
	public boolean isVeseinTalUmatarStartingTonight(JewishCalendar jewishCalendar) {
		if (jewishCalendar.getInIsrael()) {
			// The 7th Cheshvan can't occur on Shabbos, so always return true for 6 Cheshvan
			if (jewishCalendar.getJewishMonth() == JewishDate.CHESHVAN && jewishCalendar.getJewishDayOfMonth() == 6) {
					return true;
			}
		} else {
			if (jewishCalendar.getDayOfWeek() == Calendar.FRIDAY) { //Not recited on Friday night
				return false;
			}
			if(jewishCalendar.getDayOfWeek() == Calendar.SATURDAY) { // When starting on motzai Shabbos, it can be the start date or delayed from Friday night
				return jewishCalendar.getTekufasTishreiElapsedDays() == 47 || jewishCalendar.getTekufasTishreiElapsedDays() == 46;
			} else {
				return jewishCalendar.getTekufasTishreiElapsedDays() == 46;
			}
		}
		return false;
	}

	/**
	 * Returns if <em>Vesein Tal Umatar Livracha</em> (<em>Sheailas Geshamim</em>) is recited. This will return
	 * true for the entire season, even on <em>Shabbos</em> when it is not recited.
	 * 
	 * @param jewishCalendar the Jewish calendar day.
	 * 
	 * @return true if <em>Vesein Tal Umatar Livracha</em> (<em>Sheailas Geshamim</em>) is recited.
	 * 
	 * @see #isVeseinTalUmatarStartDate(JewishCalendar)
	 * @see #isVeseinTalUmatarStartingTonight(JewishCalendar)
	 */
	public boolean isVeseinTalUmatarRecited(JewishCalendar jewishCalendar) {
		if (jewishCalendar.getJewishMonth() == JewishDate.NISSAN && jewishCalendar.getJewishDayOfMonth() < 15) {
			return true;
		}
		if (jewishCalendar.getJewishMonth() < JewishDate.CHESHVAN) {
			return false;
		}
		if (jewishCalendar.getInIsrael()) {
			return jewishCalendar.getJewishMonth() != JewishDate.CHESHVAN || jewishCalendar.getJewishDayOfMonth() >= 7;
		} else {
			return jewishCalendar.getTekufasTishreiElapsedDays() >= 47;
		}
	}
	
	/**
	 * Returns if <em>Vesein Beracha</em> is recited. It is recited from 15 <em>Nissan</em> to the point that {@link
	 * #isVeseinTalUmatarRecited(JewishCalendar) <em>vesein tal umatar</em> is recited}.
	 * 
	 * @param jewishCalendar the Jewish calendar day.
	 * 
	 * @return true if <em>Vesein Beracha</em> is recited.
	 * 
	 * @see #isVeseinTalUmatarRecited(JewishCalendar)
	 */
	public boolean isVeseinBerachaRecited(JewishCalendar jewishCalendar) {
		return ! isVeseinTalUmatarRecited(jewishCalendar);
	}

	/**
	 * Returns if the date is the start date for reciting <em>Mashiv Haruach Umorid Hageshem</em>. The date is 22 <em>Tishrei</em>.
	 * 
	 * @param jewishCalendar the Jewish calendar day.
	 * 
	 * @return true if the date is the start date for reciting <em>Mashiv Haruach Umorid Hageshem</em>.
	 * 
	 * @see #isMashivHaruachEndDate(JewishCalendar)
	 * @see #isMashivHaruachRecited(JewishCalendar)
	 */
	public boolean isMashivHaruachStartDate(JewishCalendar jewishCalendar) {
		return jewishCalendar.getJewishMonth() == JewishDate.TISHREI && jewishCalendar.getJewishDayOfMonth() == 22;
	}

	/**
	 * Returns if the date is the end date for reciting <em>Mashiv Haruach Umorid Hageshem</em>. The date is 15 <em>Nissan</em>.
	 * 
	 * @param jewishCalendar the Jewish calendar day.
	 * 
	 * @return true if the date is the end date for reciting <em>Mashiv Haruach Umorid Hageshem</em>.
	 * 
	 * @see #isMashivHaruachStartDate(JewishCalendar)
	 * @see #isMashivHaruachRecited(JewishCalendar)
	 */
	public boolean isMashivHaruachEndDate(JewishCalendar jewishCalendar) {
		return jewishCalendar.getJewishMonth() == JewishDate.NISSAN && jewishCalendar.getJewishDayOfMonth() == 15;
	}

	/**
	 * Returns if <em>Mashiv Haruach Umorid Hageshem</em> is recited. This period starts on 22 <em>Tishrei</em> and ends
	 * on the 15th day of <em>Nissan</em>.
	 * <em>Marcheshvan</em>. Outside of Israel recitation starts on December 4/5.
	 * 
	 * @param jewishCalendar the Jewish calendar day.
	 * 
	 * @return true if <em>Mashiv Haruach Umorid Hageshem</em> is recited.
	 * 
	 * @see #isMashivHaruachStartDate(JewishCalendar)
	 * @see #isMashivHaruachEndDate(JewishCalendar)
	 */
	public boolean isMashivHaruachRecited(JewishCalendar jewishCalendar) {
		JewishDate startDate = new JewishDate(jewishCalendar.getJewishYear(), JewishDate.TISHREI, 22);
		JewishDate endDate = new JewishDate(jewishCalendar.getJewishYear(), JewishDate.NISSAN, 15);
		return jewishCalendar.compareTo(startDate) > 0 && jewishCalendar.compareTo(endDate) < 0;
	}

	/**
	 * Returns if <em>Morid Hatal</em> (or the lack of reciting <em>Mashiv Haruach</em> following <em>nussach Ashkenaz</em>) is recited.
	 * This period starts on 22 <em>Tishrei</em> and ends on the 15th day of
	 * <em>Nissan</em>.
	 * 
	 * @param jewishCalendar the Jewish calendar day.
	 * 
	 * @return true if <em>Morid Hatal</em> (or the lack of reciting <em>Mashiv Haruach</em> following <em>nussach Ashkenaz</em>) is recited.
	 */
	public boolean isMoridHatalRecited(JewishCalendar jewishCalendar) {
		return !isMashivHaruachRecited(jewishCalendar) || isMashivHaruachStartDate(jewishCalendar) || isMashivHaruachEndDate(jewishCalendar);
	}
	
	/**
	 * @return If <em>tachanun</em> is set to be recited during the week of Purim from the 11th through the 17th of Adar
	 *         (on a non-leap year, or Adar II on a leap year). Some <em>Chasidish</em> communities do not recite
	 *         <em>tachanun</em> during this period.
	 * @see #setTachanunRecitedWeekOfPurim(boolean)
	 */
	public boolean isTachanunRecitedWeekOfPurim() {
		return tachanunRecitedWeekOfPurim;
	}

	/**
	 * @param tachanunRecitedWeekOfPurim Sets if <em>tachanun</em> is to recited during the week of Purim from the 11th
	 *         through the 17th of Adar (on a non-leap year), or Adar II (on a leap year). Some <em>Chasidish</em>
	 *         communities do not recite <em>tachanun</em> during this period.
	 * @see #isTachanunRecitedWeekOfPurim()
	 */
	public void setTachanunRecitedWeekOfPurim(boolean tachanunRecitedWeekOfPurim) {
		this.tachanunRecitedWeekOfPurim = tachanunRecitedWeekOfPurim;
	}

	/**
	 * @return If <em>tachanun</em> is set to be recited during the <em>sefira</em> week of <em>Hod</em> (14 - 20 Iyar,
	 *         or the 29th - 35th of the <em>Omer</em>). Some <em>Chasidish</em> communities do not recite
	 *         <em>tachanun</em> during this period.
	 * @see #setTachanunRecitedWeekOfHod(boolean)
	 */
	public boolean isTachanunRecitedWeekOfHod() {
		return tachanunRecitedWeekOfHod;
	}

	/**
	 * @param tachanunRecitedWeekOfHod Sets if <em>tachanun</em> should be recited during the <em>sefira</em> week of
	 * <em>Hod</em>.
	 * @see #isTachanunRecitedWeekOfHod()
	 */
	public void setTachanunRecitedWeekOfHod(boolean tachanunRecitedWeekOfHod) {
		this.tachanunRecitedWeekOfHod = tachanunRecitedWeekOfHod;
	}

	/**
	 * Is <em>tachanun</em> to be recited at the end Of Tishrei. The Magen Avraham 669:1 and the Pri Chadash 131:7 state that some
	 * places to not recite <em>tachanun</em> during this period. The Sh"UT Chasam Sofer on Choshen Mishpat 77 writes that this is
	 * the <em>minhag</em> in Ashkenaz. The Shaarei Teshuva 131:19 quotes the Sheyarie Kneses Hagdola who also states that it should
	 * not be recited. The Aderes wanted to institute saying <em>tachanun</em> during this period, but was dissuaded from this by
	 * Rav Shmuel Salant who did not want to change the <em>minhag</em> in Yerushalayim. The Aruch Hashulchan is of the opinion that
	 * that this <em>minhag</em> is incorrect, and it should be recited, and The Chazon Ish also recited <em>tachanun</em> during this
	 * period. See the Dirshu edition of the Mishna Berurah for details.
	 * @return If <em>tachanun</em> is set to be recited at the end Of Tishrei.
	 * @see #setTachanunRecitedEndOfTishrei(tachanunRecitedEndOfTishrei)
	 */
	public boolean isTachanunRecitedEndOfTishrei() {
		return tachanunRecitedEndOfTishrei;
	}

	/**
	 * Sets if <em>tachanun</em> should be recited at the end of Tishrei.
	 * @param tachanunRecitedEndOfTishrei is <em>tachanun</em> recited at the end of Tishrei.
	 * @see #isTachanunRecitedEndOfTishrei()
	 */
	public void setTachanunRecitedEndOfTishrei(boolean tachanunRecitedEndOfTishrei) {
		this.tachanunRecitedEndOfTishrei = tachanunRecitedEndOfTishrei;
	}
	
	/**
	 * Is <em>tachanun</em> to be recited during the week after <em>Shavuos</em>. This is the opinion of the Pri Megadim
	 * quoted by the Mishna Berurah. This is since <em>karbanos</em> of Shavuos have <em>tashlumim</em> for 7 days, it is
	 * still considered like a Yom Tov. The Chazon Ish quoted in the Orchos Rabainu vol. 1 page 68 recited <em>tachanun</em>
	 * this week.
	 * 
	 * @return If <em>tachanun</em> is set to be recited during the week after Shavuos.
	 * @see #setTachanunRecitedWeekAfterShavuos(boolean)
	 */
	public boolean isTachanunRecitedWeekAfterShavuos() {
		return tachanunRecitedWeekAfterShavuos;
	}

	/**
	 * Sets if <em>tachanun</em> is set to be recited during the week after <em>Shavuos</em>.
	 * @param tachanunRecitedWeekAfterShavuos is <em>tachanun</em> recited during the week after Shavuos.
	 * @see #isTachanunRecitedWeekAfterShavuos()
	 */
	public void setTachanunRecitedWeekAfterShavuos(boolean tachanunRecitedWeekAfterShavuos) {
		this.tachanunRecitedWeekAfterShavuos = tachanunRecitedWeekAfterShavuos;
	}
	
	/**
	 * Returns If <em>tachanun</em> is set to be recited on the 13th of <em>Sivan</em> (<em>sfaika deyoma</em> of the
	 * 7th day) outside Israel. This is brought down by the Shaarie Teshuva 131:19 quoting the Sheyarei Kneses Hagedola
	 * that due to the <em>sfaika deyoma</em> it should not be said. Rav Shlomo Zalman Orbach in Halichos Shlomo on
	 * Shavuos 12:16:25 that even in <em>chutz laaretz</em> it should be recited since the <em>yemei Tashlumin</em> are
	 * counted as in Israel since that is where the <em>karbanos</em> are brought. Both
	 * {@link #isTachanunRecitedShacharis(JewishCalendar)} and {@link #isTachanunRecitedMincha(JewishCalendar)}
	 * only return false if the location is not set to {@link JewishCalendar#getInIsrael() Israel} and both
	 * {@link #tachanunRecitedWeekAfterShavuos} and {@link #setTachanunRecited13SivanOutOfIsrael} are set to false.
	 * 
	 * @return If <em>tachanun</em> is set to be recited on the 13th of Sivan out of Israel.
	 * @see #setTachanunRecited13SivanOutOfIsrael(isTachanunRecitedThirteenSivanOutOfIsrael)
	 * @see #isTachanunRecitedWeekAfterShavuos()
	 */
	public boolean isTachanunRecited13SivanOutOfIsrael() {
		return tachanunRecited13SivanOutOfIsrael;
	}

	/**
	 * @param tachanunRecitedThirteenSivanOutOfIsrael sets if <em>tachanun</em> should be recited on the 13th
	 *          of <em>Sivan</em> out of Israel.
	 * @see #isTachanunRecited13SivanOutOfIsrael()
	 */
	public void setTachanunRecited13SivanOutOfIsrael(boolean tachanunRecitedThirteenSivanOutOfIsrael) {
		this.tachanunRecited13SivanOutOfIsrael = tachanunRecitedThirteenSivanOutOfIsrael;
	}
	
	/**
	 * Is <em>tachanun</em> recited on {@link JewishCalendar#PESACH_SHENI Pesach Sheni}. The Pri Chadash 131:7 states that
	 * <em>tachanun</em> should not be recited. The Aruch Hashulchan states that this is the minhag of the <em>sephardim</em>.
	 * the Shaarei Efraim 10:27 also mentions that it is not recited, as does the Siddur Yaavetz (Shaar Hayesod, Chodesh Iyar).
	 * The Pri Megadim (Mishbetzes Hazahav 131:15) and the Chazon Ish (Erev Pesahc Shchal Beshabos, page 203 in Rav Sheraya
	 * Deblitzky's comment).
	 * @return If <em>tachanun</em> is recited on {@link JewishCalendar#PESACH_SHENI Pesach Sheni}.
	 * @see #setTachanunRecitedPesachSheni(boolean)
	 */
	public boolean isTachanunRecitedPesachSheni() {
		return tachanunRecitedPesachSheni;
	}

	/**
	 * @param tachanunRecitedPesachSheni sets if <em>tachanun</em> should be recited on <em>Pesach Sheni</em>.
	 * @see #isTachanunRecitedPesachSheni()
	 */
	public void setTachanunRecitedPesachSheni(boolean tachanunRecitedPesachSheni) {
		this.tachanunRecitedPesachSheni = tachanunRecitedPesachSheni;
	}
	
	/**
	 * <em>tachanun</em> is recited on 15 Iyar (<em>sfaika deyoma</em> of {@link JewishCalendar#PESACH_SHENI <em>Pesach Sheni</em>})
	 * out of Israel.
	 * @return if <em>tachanun</em> is recited on 15 Iyar (<em>sfaika deyoma</em> of {@link
	 *          JewishCalendar#PESACH_SHENI Pesach Sheni}) out of Israel.
	 * @see #setTachanunRecited15IyarOutOfIsrael(boolean)
	 */
	public boolean isTachanunRecited15IyarOutOfIsrael() {
		return tachanunRecited15IyarOutOfIsrael;
	}

	/**
	 * @param tachanunRecited15IyarOutOfIsrael if <em>tachanun</em> should be recited on the 15th of <em>Iyar</em>
	 *          (<em>sfaika deyoma</em> of {@link JewishCalendar#PESACH_SHENI <em>Pesach Sheni</em>}) out of Israel.
	 * @see #isTachanunRecited15IyarOutOfIsrael()
	 */
	public void setTachanunRecited15IyarOutOfIsrael(boolean tachanunRecited15IyarOutOfIsrael) {
		this.tachanunRecited15IyarOutOfIsrael = tachanunRecited15IyarOutOfIsrael;
	}
	
	/**
	 * @return if <em>tachanun</em> is recited on {@link JewishCalendar#LAG_BAOMER <em>Lag Baomer</em>}.
	 * @see #setTachanunRecitedMinchaErevLagBaomer(boolean)
	 */
	public boolean isTachanunRecitedMinchaErevLagBaomer() {
		return tachanunRecitedMinchaErevLagBaomer;
	}

	/**
	 * @param tachanunRecitedMinchaErevLagBaomer sets if <em>tachanun</em> should be recited on <em>mincha</em>
	 *          of <em>erev {@link JewishCalendar#LAG_BAOMER Lag Baomer}</em>.
	 * @see #isTachanunRecitedMinchaErevLagBaomer()
	 */
	public void setTachanunRecitedMinchaErevLagBaomer(boolean tachanunRecitedMinchaErevLagBaomer) {
		this.tachanunRecitedMinchaErevLagBaomer = tachanunRecitedMinchaErevLagBaomer;
	}
	
	/**
	 * @return if <em>tachanun</em> is recited during the <em>Shivas Yemei Hamiluim</em>.
	 * @see #setTachanunRecitedShivasYemeiHamiluim(boolean)
	 */
	public boolean isTachanunRecitedShivasYemeiHamiluim() {
		return tachanunRecitedShivasYemeiHamiluim;
	}

	/**
	 * @param tachanunRecitedShivasYemeiHamiluim sets if <em>tachanun</em> should be recited during the
	 *          <em>Shivas Yemei Hamiluim</em>.
	 * @see #isTachanunRecitedShivasYemeiHamiluim()
	 */
	public void setTachanunRecitedShivasYemeiHamiluim(boolean tachanunRecitedShivasYemeiHamiluim) {
		this.tachanunRecitedShivasYemeiHamiluim = tachanunRecitedShivasYemeiHamiluim;
	}

	/**
	 * @return if <em>tachanun</em> is recited on Fridays Some <em>chasidish</em> communities do not
	 *          recite <em>tachanun</em> on Fridays.
	 * @see #setTachanunRecitedFridays(boolean)
	 */
	public boolean isTachanunRecitedFridays() {
		return tachanunRecitedFridays;
	}

	/**
	 * @param tachanunRecitedFridays sets if <em>tachanun</em> should be recited on Fridays. Some <em>chasidish</em>
	 *          communities do not recite <em>tachanun</em> on Fridays.
	 * @see #isTachanunRecitedFridays()
	 */
	public void setTachanunRecitedFridays(boolean tachanunRecitedFridays) {
		this.tachanunRecitedFridays = tachanunRecitedFridays;
	}

	/**
	 * @return if <em>tachanun</em> is recited on Sundays.  Some <em>chasidish</em> communities do not recite
	 *          <em>tachanun</em> on Fridays.
	 * @see #isTachanunRecitedSundays()
	 */
	public boolean isTachanunRecitedSundays() {
		return tachanunRecitedSundays;
	}

	/**
	 * @param tachanunRecitedSundays sets if <em>tachanun</em> should be recited on Sundays. Some <em>chasidish</em>
	 *          communities do not recite <em>tachanun</em> on Sundays.
	 * @see #isTachanunRecitedSundays()
	 */
	public void setTachanunRecitedSundays(boolean tachanunRecitedSundays) {
		this.tachanunRecitedSundays = tachanunRecitedSundays;
	}
	
	/**
	 * @return if <em>tachanun</em> is recited on <em>Mincha</em> the entire year.  Some <em>chasidish</em>
	 *          communities do not recite <em>tachanun</em> all year round by <em>Mincha</em>.
	 * @see #setTachanunRecitedMinchaAllYear(boolean)
	 */
	public boolean isTachanunRecitedMinchaAllYear() {
		return tachanunRecitedMinchaAllYear;
	}

	/**
	 * @param tachanunRecitedMinchaAllYear sets if <em>tachanun</em> should be recited by <em>mincha</em> all year. If set
	 *          to false, {@link #isTachanunRecitedMincha(JewishCalendar)} will always return false. If set to true (the
	 *          default), it will use the regular rules.
	 * @see #isTachanunRecitedMinchaAllYear()
	 */
	public void setTachanunRecitedMinchaAllYear(boolean tachanunRecitedMinchaAllYear) {
		this.tachanunRecitedMinchaAllYear = tachanunRecitedMinchaAllYear;
	}
}