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
package io.github.kdroidfilter.kosherkotlin

import io.github.kdroidfilter.kosherkotlin.util.AstronomicalCalculator
import io.github.kdroidfilter.kosherkotlin.AstronomicalCalendar
import io.github.kdroidfilter.kosherkotlin.ZmanimCalendar
import io.github.kdroidfilter.kosherkotlin.ComplexZmanimCalendar
