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
package com.kosherjava.zmanim.hebrewcalendar

import com.kosherjava.zmanim.util.ZmanimFormatter
import java.util.TimeZone
import java.lang.StringBuffer
import com.kosherjava.zmanim.util.Zman
import java.lang.IllegalArgumentException
import com.kosherjava.zmanim.util.GeoLocation
import java.lang.StringBuilder
import java.lang.CloneNotSupportedException
import com.kosherjava.zmanim.util.AstronomicalCalculator
import java.util.Calendar
import com.kosherjava.zmanim.util.NOAACalculator
import java.text.SimpleDateFormat
import java.text.DecimalFormat
import java.text.DateFormat
import java.util.Collections
import com.kosherjava.zmanim.util.GeoLocationUtils
import com.kosherjava.zmanim.util.SunTimesCalculator
import com.kosherjava.zmanim.hebrewcalendar.Daf
import com.kosherjava.zmanim.hebrewcalendar.JewishDate
import java.time.LocalDate
import java.util.GregorianCalendar
import com.kosherjava.zmanim.hebrewcalendar.HebrewDateFormatter
import com.kosherjava.zmanim.hebrewcalendar.JewishCalendar
import com.kosherjava.zmanim.hebrewcalendar.JewishCalendar.Parsha
import com.kosherjava.zmanim.hebrewcalendar.YomiCalculator
import com.kosherjava.zmanim.hebrewcalendar.YerushalmiYomiCalculator
import java.util.EnumMap
import com.kosherjava.zmanim.AstronomicalCalendar
import com.kosherjava.zmanim.ZmanimCalendar
import java.math.BigDecimal
import com.kosherjava.zmanim.ComplexZmanimCalendar
