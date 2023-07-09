/*
 * Zmanim Java API
 * Copyright (C) 2004-2022 Eliyahu Hershfeld
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
 * or connect to: https://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 */
package com.kosherjava.zmanim.util

import com.kosherjava.zmanim.AstronomicalCalendar
import java.lang.Exception
import java.lang.reflect.Method
import java.text.DateFormat
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * A class used to format both non [Date] times generated by the Zmanim package as well as Dates. For
 * example the [AstronomicalCalendar.getTemporalHour] returns the length of the hour in
 * milliseconds. This class can format this time.
 *
 * @author  Eliyahu Hershfeld 2004 - 2022
 */
class ZmanimFormatter constructor(format: Int, dateFormat: SimpleDateFormat, timeZone: TimeZone?) {
    /**
     * Setting to prepent a zero to single digit hours.
     * @see .setSettings
     */
    private var prependZeroHours: Boolean = false

    /**
     * @see .setSettings
     */
    private var useSeconds: Boolean = false

    /**
     * @see .setSettings
     */
    private var useMillis: Boolean = false

    /**
     * the formatter for hours.
     */
    private val hourNF: DecimalFormat

    lateinit var dateFormat: SimpleDateFormat
    /**
     * @return the timeZone
     */
    /**
     * @param timeZone
     * the timeZone to set
     */
    /**
     * @see .setTimeZone
     */
    var timeZone: TimeZone? = null // TimeZone.getTimeZone("UTC");
    // private DecimalFormat decimalNF;
    /**
     * Defaults to [.SEXAGESIMAL_XSD_FORMAT].
     * @see .setTimeFormat
     */
    private var timeFormat: Int = SEXAGESIMAL_XSD_FORMAT

    /**
     * constructor that defaults to this will use the format "h:mm:ss" for dates and 00.00.00.0 for [Time].
     * @param timeZone the TimeZone Object
     */
    constructor(timeZone: TimeZone?) : this(0, SimpleDateFormat("h:mm:ss"), timeZone) {}
    // public ZmanimFormatter() {
    // this(0, new SimpleDateFormat("h:mm:ss"), TimeZone.getTimeZone("UTC"));
    // }
    /**
     * ZmanimFormatter constructor using a formatter
     *
     * @param format
     * int The formatting style to use. Using ZmanimFormatter.SEXAGESIMAL_SECONDS_FORMAT will format the time
     * time of 90*60*1000 + 1 as 1:30:00
     * @param dateFormat the SimpleDateFormat Object
     * @param timeZone the TimeZone Object
     */
    init {
        var hourFormat: String? = "0"
        if (prependZeroHours) {
            hourFormat = "00"
        }
        hourNF = DecimalFormat(hourFormat)
        setTimeFormat(format)
        dateFormat.timeZone = timeZone
        this.dateFormat = dateFormat
    }

    /**
     * Sets the format to use for formatting.
     *
     * @param format
     * int the format constant to use.
     */
    fun setTimeFormat(format: Int) {
        timeFormat = format
        when (format) {
            SEXAGESIMAL_XSD_FORMAT -> setSettings(true, true, true)
            SEXAGESIMAL_FORMAT -> setSettings(false, false, false)
            SEXAGESIMAL_SECONDS_FORMAT -> setSettings(false, true, false)
            SEXAGESIMAL_MILLIS_FORMAT -> setSettings(false, true, true)
        }
    }

    /**
     * Sets various format settings.
     * @param prependZeroHours  if to prepend a zero for single digit hours (so that 1 'oclock is displayed as 01)
     * @param useSeconds should seconds be used in the time format
     * @param useMillis should milliseconds be used informatting time.
     */
    private fun setSettings(prependZeroHours: Boolean, useSeconds: Boolean, useMillis: Boolean) {
        this.prependZeroHours = prependZeroHours
        this.useSeconds = useSeconds
        this.useMillis = useMillis
    }

    /**
     * A method that formats milliseconds into a time format.
     *
     * @param milliseconds
     * The time in milliseconds.
     * @return String The formatted `String`
     */
    fun format(milliseconds: Double): String {
        return format(milliseconds.toInt())
    }

    /**
     * A method that formats milliseconds into a time format.
     *
     * @param millis
     * The time in milliseconds.
     * @return String The formatted `String`
     */
    fun format(millis: Int): String {
        return format(Time(millis))
    }

    /**
     * A method that formats [Time]objects.
     *
     * @param time
     * The time `Object` to be formatted.
     * @return String The formatted `String`
     */
    fun format(time: Time): String {
        if (timeFormat == XSD_DURATION_FORMAT) {
            return formatXSDDurationTime(time)
        }
        val sb: StringBuilder = StringBuilder()
        sb.append(hourNF.format(time.hours.toLong()))
        sb.append(":")
        sb.append(minuteSecondNF.format(time.minutes.toLong()))
        if (useSeconds) {
            sb.append(":")
            sb.append(minuteSecondNF.format(time.seconds.toLong()))
        }
        if (useMillis) {
            sb.append(".")
            sb.append(milliNF.format(time.milliseconds.toLong()))
        }
        return sb.toString()
    }

    /**
     * Formats a date using this classe's [date format][.getDateFormat].
     *
     * @param dateTime
     * the date to format
     * @param calendar
     * the [Calendar] used to help format based on the Calendar's DST and other
     * settings.
     * @return the formatted String
     */
    fun formatDateTime(dateTime: Date?, calendar: Calendar): String {
        dateFormat.calendar = calendar
        return if ((dateFormat.toPattern() == "yyyy-MM-dd'T'HH:mm:ss")) getXSDateTime(dateTime, calendar)
        else dateFormat.format(dateTime)
    }

    /**
     * The date:date-time function returns the current date and time as a date/time string. The date/time string that's
     * returned must be a string in the format defined as the lexical representation of xs:dateTime in [[3.3.8 dateTime]](http://www.w3.org/TR/xmlschema11-2/#dateTime) of [[XML Schema 1.1 Part 2: Datatypes]](http://www.w3.org/TR/xmlschema11-2/). The date/time format is
     * basically CCYY-MM-DDThh:mm:ss, although implementers should consult [[XML Schema 1.1 Part 2: Datatypes]](http://www.w3.org/TR/xmlschema11-2/) and [[ISO 8601]](http://www.iso.ch/markete/8601.pdf) for details. The date/time string format must include a
     * time zone, either a Z to indicate Coordinated Universal Time or a + or - followed by the difference between the
     * difference from UTC represented as hh:mm.
     * @param dateTime the Date Object
     * @param calendar Calendar Object
     * @return the XSD dateTime
     */
    fun getXSDateTime(dateTime: Date?, calendar: Calendar): String {
        val xsdDateTimeFormat: String = "yyyy-MM-dd'T'HH:mm:ss"
        /*
		 * if (xmlDateFormat == null || xmlDateFormat.trim().equals("")) { xmlDateFormat = xsdDateTimeFormat; }
		 */
        val dateFormat: SimpleDateFormat = SimpleDateFormat(xsdDateTimeFormat)
        dateFormat.setTimeZone(timeZone)
        val sb: StringBuilder = StringBuilder(dateFormat.format(dateTime))
        // Must also include offset from UTF.
        val offset =
            calendar.get(Calendar.ZONE_OFFSET) + calendar.get(Calendar.DST_OFFSET) // Get the offset (in milliseconds)
        // If there is no offset, we have "Coordinated Universal Time"
        if (offset == 0) sb.append("Z") else {
            // Convert milliseconds to hours and minutes
            val hrs = offset / (60 * 60 * 1000)
            // In a few cases, the time zone may be +/-hh:30.
            val min = offset % (60 * 60 * 1000)
            val posneg: Char = if (hrs < 0) '-' else '+'
            sb.append(posneg.toString() + formatDigits(hrs) + ':' + formatDigits(min))
        }
        return sb.toString()
    }

    /**
     * This returns the xml representation of an xsd:duration object.
     *
     * @param millis
     * the duration in milliseconds
     * @return the xsd:duration formatted String
     */
    fun formatXSDDurationTime(millis: Long): String {
        return formatXSDDurationTime(Time(millis.toDouble()))
    }

    /**
     * This returns the xml representation of an xsd:duration object.
     *
     * @param time
     * the duration as a Time object
     * @return the xsd:duration formatted String
     */
    fun formatXSDDurationTime(time: Time): String {
        val duration: StringBuilder = StringBuilder()
        if ((time.hours != 0) || (time.minutes != 0) || (time.seconds != 0) || (time.milliseconds != 0)) {
            duration.append("P")
            duration.append("T")
            if (time.hours != 0) duration.append(time.hours.toString() + "H")
            if (time.minutes != 0) duration.append(time.minutes.toString() + "M")
            if (time.seconds != 0 || time.milliseconds != 0) {
                duration.append(time.seconds.toString() + "." + milliNF.format(time.milliseconds.toLong()))
                duration.append("S")
            }
            if (duration.length == 1) // zero seconds
                duration.append("T0S")
            if (time.isNegative) duration.insert(0, "-")
        }
        return duration.toString()
    }

    companion object {
        /**
         * the formatter for minutes as seconds.
         */
        private val minuteSecondNF: DecimalFormat = DecimalFormat("00")

        /**
         * the formatter for minutes as milliseconds.
         */
        private val milliNF: DecimalFormat = DecimalFormat("000")

        /**
         * Format using hours, minutes, seconds and milliseconds using the xsd:time format. This format will return
         * 00.00.00.0 when formatting 0.
         */
        val SEXAGESIMAL_XSD_FORMAT = 0

        /**
         * Format using standard decimal format with 5 positions after the decimal.
         */
        val DECIMAL_FORMAT = 1

        /** Format using hours and minutes.  */
        val SEXAGESIMAL_FORMAT = 2

        /** Format using hours, minutes and seconds.  */
        val SEXAGESIMAL_SECONDS_FORMAT = 3

        /** Format using hours, minutes, seconds and milliseconds.  */
        val SEXAGESIMAL_MILLIS_FORMAT = 4

        /** constant for milliseconds in a minute (60,000)  */
        val MINUTE_MILLIS: Long = (60 * 1000).toLong()

        /** constant for milliseconds in an hour (3,600,000)  */
        val HOUR_MILLIS: Long = MINUTE_MILLIS * 60

        /**
         * Format using the XSD Duration format. This is in the format of PT1H6M7.869S (P for period (duration), T for time,
         * H, M and S indicate hours, minutes and seconds.
         */
        val XSD_DURATION_FORMAT = 5

        /**
         * Represent the hours and minutes with two-digit strings.
         *
         * @param digits
         * hours or minutes.
         * @return two-digit String representation of hrs or minutes.
         */
        private fun formatDigits(digits: Int): String {
            val dd: String = Math.abs(digits).toString()
            return if (dd.length == 1) '0'.toString() + dd else dd
        }

        /**
         * A method that returns an XML formatted `String` representing the serialized `Object`. The
         * format used is:
         *
         * <pre>
         * &lt;AstronomicalTimes date=&quot;1969-02-08&quot; type=&quot;com.kosherjava.zmanim.AstronomicalCalendar algorithm=&quot;US Naval Almanac Algorithm&quot; location=&quot;Lakewood, NJ&quot; latitude=&quot;40.095965&quot; longitude=&quot;-74.22213&quot; elevation=&quot;31.0&quot; timeZoneName=&quot;Eastern Standard Time&quot; timeZoneID=&quot;America/New_York&quot; timeZoneOffset=&quot;-5&quot;&gt;
         * &lt;Sunrise&gt;2007-02-18T06:45:27-05:00&lt;/Sunrise&gt;
         * &lt;TemporalHour&gt;PT54M17.529S&lt;/TemporalHour&gt;
         * ...
         * &lt;/AstronomicalTimes&gt;
        </pre> *
         *
         * Note that the output uses the [xsd:dateTime](http://www.w3.org/TR/xmlschema11-2/#dateTime) format for
         * times such as sunrise, and [xsd:duration](http://www.w3.org/TR/xmlschema11-2/#duration) format for
         * times that are a duration such as the length of a
         * [temporal hour][AstronomicalCalendar.getTemporalHour]. The output of this method is
         * returned by the [toString][.toString].
         *
         * @param astronomicalCalendar the AstronomicalCalendar Object
         *
         * @return The XML formatted `String`. The format will be:
         *
         * <pre>
         * &lt;AstronomicalTimes date=&quot;1969-02-08&quot; type=&quot;com.kosherjava.zmanim.AstronomicalCalendar algorithm=&quot;US Naval Almanac Algorithm&quot; location=&quot;Lakewood, NJ&quot; latitude=&quot;40.095965&quot; longitude=&quot;-74.22213&quot; elevation=&quot;31.0&quot; timeZoneName=&quot;Eastern Standard Time&quot; timeZoneID=&quot;America/New_York&quot; timeZoneOffset=&quot;-5&quot;&gt;
         * &lt;Sunrise&gt;2007-02-18T06:45:27-05:00&lt;/Sunrise&gt;
         * &lt;TemporalHour&gt;PT54M17.529S&lt;/TemporalHour&gt;
         * ...
         * &lt;/AstronomicalTimes&gt;
        </pre> *
         *
         * @todo Add proper schema, and support for nulls. XSD duration (for solar hours), should probably return nil and not P.
         */
        fun toXML(astronomicalCalendar: AstronomicalCalendar): String {
            val geoLocation = astronomicalCalendar.geoLocation
            val tz = geoLocation?.timeZone!!
            val formatter: ZmanimFormatter = ZmanimFormatter(
                XSD_DURATION_FORMAT, SimpleDateFormat(
                    "yyyy-MM-dd'T'HH:mm:ss"
                ), tz
            )
            val df: DateFormat = SimpleDateFormat("yyyy-MM-dd")
            df.timeZone = tz
            val date: Date = astronomicalCalendar.calendar.time
            val daylight: Boolean = tz.useDaylightTime() && tz.inDaylightTime(date)
            val sb: StringBuilder = StringBuilder("<")
            val clazz = astronomicalCalendar::class.java
            val className = clazz.name
            when(className) {
                 "com.kosherjava.zmanim.AstronomicalCalendar" -> {
                    sb.append("AstronomicalTimes")
                    // TODO: use proper schema ref, and maybe build a real schema.
                    // output += "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" ";
                    // output += xsi:schemaLocation="http://www.kosherjava.com/zmanim astronomical.xsd"
                }
                "com.kosherjava.zmanim.ComplexZmanimCalendar" -> {
                    sb.append("Zmanim")
                    // TODO: use proper schema ref, and maybe build a real schema.
                    // output += "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" ";
                    // output += xsi:schemaLocation="http://www.kosherjava.com/zmanim zmanim.xsd"
                }
                "com.kosherjava.zmanim.ZmanimCalendar" -> {
                    sb.append("BasicZmanim")
                    // TODO: use proper schema ref, and maybe build a real schema.
                    // output += "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" ";
                    // output += xsi:schemaLocation="http://www.kosherjava.com/zmanim basicZmanim.xsd"
                }
            }
            sb.append(" date=\"").append(df.format(date)).append("\"")
            sb.append(" type=\"").append(className).append("\"")
            sb.append(" algorithm=\"").append(astronomicalCalendar.astronomicalCalculator.calculatorName)
                .append("\"")
            sb.append(" location=\"").append(geoLocation.locationName).append("\"")
            sb.append(" latitude=\"").append(geoLocation.latitude).append("\"")
            sb.append(" longitude=\"").append(geoLocation.longitude).append("\"")
            sb.append(" elevation=\"").append(geoLocation.elevation).append("\"")
            sb.append(" timeZoneName=\"").append(tz.getDisplayName(daylight, TimeZone.LONG)).append("\"")
            sb.append(" timeZoneID=\"").append(tz.getID()).append("\"")
            sb.append(" timeZoneOffset=\"")
                .append((tz.getOffset(astronomicalCalendar.calendar.getTimeInMillis()) / (HOUR_MILLIS.toDouble())))
                .append("\"")
            sb.append(">\n")
            val theMethods: Array<Method> = clazz.methods
            var tagName: String
            var value: Any?
            val dateList: MutableList<Zman> = ArrayList()
            val durationList: MutableList<Zman> = ArrayList()
            val otherList: MutableList<String> = ArrayList()
            for (i in theMethods.indices) {
                if (includeMethod(theMethods[i])) {
                    tagName = theMethods[i].name.substring(3)
                    // String returnType = theMethods[i].getReturnType().getName();
                    try {
                        value = theMethods[i].invoke(astronomicalCalendar, null as Array<Any?>?)
                        if (value == null) { // TODO: Consider using reflection to determine the return type, not the value
                            otherList.add("<$tagName>N/A</$tagName>")
                            // TODO: instead of N/A, consider return proper xs:nil.
                            // otherList.add("<" + tagName + " xs:nil=\"true\" />");
                        } else if (value is Date) {
                            dateList.add(Zman(value as Date?, tagName))
                        } else if (value is Long || value is Int) { // shaah zmanis
                            if ((value as Long).toLong() == Long.MIN_VALUE) {
                                otherList.add("<$tagName>N/A</$tagName>")
                                // TODO: instead of N/A, consider return proper xs:nil.
                                // otherList.add("<" + tagName + " xs:nil=\"true\" />");
                            } else {
                                durationList.add(Zman(value.toLong(), tagName))
                            }
                        } else { // will probably never enter this block, but is present to be future proof
                            otherList.add("<$tagName>$value</$tagName>")
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            var zman: Zman
            Collections.sort(dateList, Zman.DATE_ORDER)
            for (i in dateList.indices) {
                zman = dateList[i]
                sb.append("\t<").append(zman.getLabel()).append(">")
                sb.append(formatter.formatDateTime(zman.zman, astronomicalCalendar.calendar))
                sb.append("</").append(zman.getLabel()).append(">\n")
            }
            Collections.sort(durationList, Zman.DURATION_ORDER)
            for (i in durationList.indices) {
                zman = durationList.get(i)
                sb.append("\t<" + zman.getLabel()).append(">")
                sb.append(formatter.format(zman.duration.toInt())).append("</").append(zman.getLabel())
                    .append(">\n")
            }
            for (i in otherList.indices) { // will probably never enter this block
                sb.append("\t").append(otherList.get(i)).append("\n")
            }
            when (className) {
                "com.kosherjava.zmanim.AstronomicalCalendar" -> {
                    sb.append("</AstronomicalTimes>")
                }
                "com.kosherjava.zmanim.ComplexZmanimCalendar" -> {
                    sb.append("</Zmanim>")
                }
                "com.kosherjava.zmanim.ZmanimCalendar" -> {
                    sb.append("</BasicZmanim>")
                }
            }
            return sb.toString()
        }

        /**
         * A method that returns a JSON formatted `String` representing the serialized `Object`. The
         * format used is:
         * <pre>
         * {
         * &quot;metadata&quot;:{
         * &quot;date&quot;:&quot;1969-02-08&quot;,
         * &quot;type&quot;:&quot;com.kosherjava.zmanim.AstronomicalCalendar&quot;,
         * &quot;algorithm&quot;:&quot;US Naval Almanac Algorithm&quot;,
         * &quot;location&quot;:&quot;Lakewood, NJ&quot;,
         * &quot;latitude&quot;:&quot;40.095965&quot;,
         * &quot;longitude&quot;:&quot;-74.22213&quot;,
         * &quot;elevation:&quot;31.0&quot;,
         * &quot;timeZoneName&quot;:&quot;Eastern Standard Time&quot;,
         * &quot;timeZoneID&quot;:&quot;America/New_York&quot;,
         * &quot;timeZoneOffset&quot;:&quot;-5&quot;},
         * &quot;AstronomicalTimes&quot;:{
         * &quot;Sunrise&quot;:&quot;2007-02-18T06:45:27-05:00&quot;,
         * &quot;TemporalHour&quot;:&quot;PT54M17.529S&quot;
         * ...
         * }
         * }
        </pre> *
         *
         * Note that the output uses the [xsd:dateTime](http://www.w3.org/TR/xmlschema11-2/#dateTime) format for
         * times such as sunrise, and [xsd:duration](http://www.w3.org/TR/xmlschema11-2/#duration) format for
         * times that are a duration such as the length of a
         * [temporal hour][AstronomicalCalendar.getTemporalHour].
         *
         * @param astronomicalCalendar the AstronomicalCalendar Object
         *
         * @return The JSON formatted `String`. The format will be:
         * <pre>
         * {
         * &quot;metadata&quot;:{
         * &quot;date&quot;:&quot;1969-02-08&quot;,
         * &quot;type&quot;:&quot;com.kosherjava.zmanim.AstronomicalCalendar&quot;,
         * &quot;algorithm&quot;:&quot;US Naval Almanac Algorithm&quot;,
         * &quot;location&quot;:&quot;Lakewood, NJ&quot;,
         * &quot;latitude&quot;:&quot;40.095965&quot;,
         * &quot;longitude&quot;:&quot;-74.22213&quot;,
         * &quot;elevation:&quot;31.0&quot;,
         * &quot;timeZoneName&quot;:&quot;Eastern Standard Time&quot;,
         * &quot;timeZoneID&quot;:&quot;America/New_York&quot;,
         * &quot;timeZoneOffset&quot;:&quot;-5&quot;},
         * &quot;AstronomicalTimes&quot;:{
         * &quot;Sunrise&quot;:&quot;2007-02-18T06:45:27-05:00&quot;,
         * &quot;TemporalHour&quot;:&quot;PT54M17.529S&quot;
         * ...
         * }
         * }
        </pre> *
         */
        fun toJSON(astronomicalCalendar: AstronomicalCalendar): String {
            val geoLocation = astronomicalCalendar.geoLocation
            val tz = geoLocation?.timeZone!!
            val formatter = ZmanimFormatter(
                XSD_DURATION_FORMAT, SimpleDateFormat(
                    "yyyy-MM-dd'T'HH:mm:ss"
                ), tz
            )
            val df: DateFormat = SimpleDateFormat("yyyy-MM-dd")
            df.timeZone = geoLocation.timeZone
            val date: Date = astronomicalCalendar.calendar.time
            val daylight: Boolean = tz.useDaylightTime() && tz.inDaylightTime(date)
            val sb: StringBuilder = StringBuilder("{\n\"metadata\":{\n")
            sb.append("\t\"date\":\"").append(df.format(date)).append("\",\n")
            val clazz = astronomicalCalendar::class.java
            val className = clazz.name
            sb.append("\t\"type\":\"").append(className).append("\",\n")
            sb.append("\t\"algorithm\":\"").append(astronomicalCalendar.astronomicalCalculator.calculatorName)
                .append("\",\n")
            sb.append("\t\"location\":\"").append(geoLocation.locationName)
                .append("\",\n")
            sb.append("\t\"latitude\":\"").append(geoLocation.latitude).append("\",\n")
            sb.append("\t\"longitude\":\"").append(geoLocation.longitude).append("\",\n")
            sb.append("\t\"elevation\":\"").append(geoLocation.elevation).append("\",\n")
            sb.append("\t\"timeZoneName\":\"").append(tz.getDisplayName(daylight, TimeZone.LONG)).append("\",\n")
            sb.append("\t\"timeZoneID\":\"").append(tz.getID()).append("\",\n")
            sb.append("\t\"timeZoneOffset\":\"")
                .append((tz.getOffset(astronomicalCalendar.calendar.getTimeInMillis()) / (HOUR_MILLIS.toDouble())))
                .append("\"")
            sb.append("},\n\"")
            when (className) {
                "com.kosherjava.zmanim.AstronomicalCalendar" -> {
                    sb.append("AstronomicalTimes")
                }
                "com.kosherjava.zmanim.ComplexZmanimCalendar" -> {
                    sb.append("Zmanim")
                }
                "com.kosherjava.zmanim.ZmanimCalendar" -> {
                    sb.append("BasicZmanim")
                }
            }
            sb.append("\":{\n")
            val theMethods: Array<Method> = clazz.methods
            var tagName: String
            var value: Any?
            val dateList: MutableList<Zman> = ArrayList()
            val durationList: MutableList<Zman> = ArrayList()
            val otherList: MutableList<String> = ArrayList()
            for (i in theMethods.indices) {
                if (includeMethod(theMethods[i])) {
                    tagName = theMethods[i].name.substring(3)
                    // String returnType = theMethods[i].getReturnType().getName();
                    try {
                        value = theMethods[i].invoke(astronomicalCalendar, null as Array<Any?>?)
                        if (value == null) { // TODO: Consider using reflection to determine the return type, not the value
                            otherList.add("\"$tagName\":\"N/A\",")
                        } else if (value is Date) {
                            dateList.add(Zman(value as Date?, tagName))
                        } else if (value is Long || value is Int) { // shaah zmanis
                            if ((value as Long).toLong() == Long.MIN_VALUE) {
                                otherList.add("\"$tagName\":\"N/A\"")
                            } else {
                                durationList.add(Zman(value.toLong(), tagName))
                            }
                        } else { // will probably never enter this block, but is present to be future proof
                            otherList.add("\"$tagName\":\"$value\",")
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            var zman: Zman
            Collections.sort(dateList, Zman.DATE_ORDER)
            for (i in dateList.indices) {
                zman = dateList.get(i)
                sb.append("\t\"").append(zman.getLabel()).append("\":\"")
                sb.append(formatter.formatDateTime(zman.zman, astronomicalCalendar.calendar))
                sb.append("\",\n")
            }
            Collections.sort(durationList, Zman.DURATION_ORDER)
            for (i in durationList.indices) {
                zman = durationList[i]
                sb.append("\t\"" + zman.getLabel()).append("\":\"")
                sb.append(formatter.format(zman.duration.toInt())).append("\",\n")
            }
            for (i in otherList.indices) { // will probably never enter this block
                sb.append("\t").append(otherList[i]).append("\n")
            }
            sb.setLength(sb.length - 2)
            sb.append("}\n}")
            return sb.toString()
        }

        /**
         * Determines if a method should be output by the [.toXML]
         *
         * @param method the method in question
         * @return if the method should be included in serialization
         */
        private fun includeMethod(method: Method): Boolean {
            val methodWhiteList: List<String> = ArrayList()
            // methodWhiteList.add("getName");
            val methodBlackList: List<String> = ArrayList()
            // methodBlackList.add("getGregorianChange");
            if (methodWhiteList.contains(method.name)) return true
            if (methodBlackList.contains(method.name)) return false
            if (method.parameterTypes.isNotEmpty()) return false // Skip get methods with parameters since we do not know what value to pass
            if (!method.name.startsWith("get")) return false
            if (method.returnType.name.endsWith("Date") || method.returnType.name
                    .endsWith("long")
            ) {
                return true
            }
            return false
        }
    }
}