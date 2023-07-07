/**
 * The [KosherJava](https://kosherjava.com) Zmanim library is an API for a specialized calendar that can calculate different
 * astronomical times including sunrise and sunset and Jewish *[zmanim](https://en.wikipedia.org/wiki/Zmanim)* or religious
 * times for prayers and other Jewish religious duties. These classes extend the [java.util.GregorianCalendar] and can therefore use the
 * standard Calendar functionality to change dates etc. For non religious astronomical / solar calculations such as [sunrise](https://en.wikipedia.org/wiki/Sunrise), [sunset](https://en.wikipedia.org/wiki/Sunset) and [twilight](https://en.wikipedia.org/wiki/Twilight), use the [AstronomicalCalendar]. The [ZmanimCalendar] contains the most
 * commonly used zmanim or religious time calculations. For a much more extensive list of *zmanim* use the [ComplexZmanimCalendar].
 * **Note:** It is important to read the technical notes on top of the [com.kosherjava.zmanim.util.AstronomicalCalculator] documentation.
 * <h2>Disclaimer:</h2> I did my best to get accurate results using standardized astronomical calculations. Please use care when using the library
 * since people rely on the zmanim calculations for *[halacha lemaaseh](https://en.wikipedia.org/wiki/Halakha)*.
 * @author  Eliyahu Hershfeld 2004 - 2022
 */
package com.kosherjava.zmanim

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
