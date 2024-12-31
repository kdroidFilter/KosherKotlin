/**
 * This package contain classes that represent a [Jewish Date/Calendar](https://en.wikipedia.org/wiki/Hebrew_calendar),
 * and allows conversion between [Jewish][JewishDate] and [Gregorian dates][java.util.GregorianCalendar]. The main calendar
 * classes [JewishCalendar] and [JewishDate] are based on [Avrom Finkelstien's](http://www.facebook.com/avromf) code,
 * refactored to fit the Zmanim API. The parsha and season-based *tefila* change code was ported by Y. Paritcher from his
 * [libzmanim](https://github.com/yparitcher/libzmanim) code.
 *
 * <h2>Design:</h2>
 *
 *  * [JewishDate] is the base class, allowing the maintainance of an instance of a Gregorian date along with the corresponding Jewish date.
 *  * [JewishCalendar] extends JewishDate and adds some methods related to the calendar.
 *  * [TefilaRules] is a utility class for various calendar based *tefila* rules.
 *  * [HebrewDateFormatter] defines the basics for taking a JewishCalendar and formatting the dates.
 *  * [YomiCalculator] calculates the [Daf] Yomi Bavli for a given JewishCalendar and [YerushalmiYomiCalculator] does the same
 * for Yerushalmi Yomi.
 *
 * @author  Eliyahu Hershfeld 2011 - 2022
 */
package io.github.kdroidfilter.kosherkotlin.hebrewcalendar

import io.github.kdroidfilter.kosherkotlin.hebrewcalendar.Daf
import io.github.kdroidfilter.kosherkotlin.hebrewcalendar.HebrewDateFormatter
import io.github.kdroidfilter.kosherkotlin.hebrewcalendar.JewishCalendar
import io.github.kdroidfilter.kosherkotlin.hebrewcalendar.JewishDate
import io.github.kdroidfilter.kosherkotlin.hebrewcalendar.YerushalmiYomiCalculator
import io.github.kdroidfilter.kosherkotlin.hebrewcalendar.YomiCalculator
