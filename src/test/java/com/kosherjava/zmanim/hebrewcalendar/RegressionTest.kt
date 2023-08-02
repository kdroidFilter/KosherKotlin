package com.kosherjava.zmanim.hebrewcalendar

import com.kosherjava.zmanim.AstronomicalCalendar
import com.kosherjava.zmanim.ComplexZmanimCalendar
import com.kosherjava.zmanim.Zman
import com.kosherjava.zmanim.util.GeoLocation
import com.kosherjava.zmanim.util.GeoLocation.Companion.rawOffset
import kotlinx.datetime.*
import kotlinx.datetime.TimeZone
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Test

class RegressionTest {
    companion object {
        fun Instant.toDate(): java.util.Date = this.let { java.util.Date.from(it.toJavaInstant()) }
        fun Zman.DateBased.toDate(): java.util.Date? = this.momentOfOccurrence?.let { java.util.Date.from(it.toJavaInstant()) }
    }
    val hdf = HebrewDateFormatter()

    val mYear = 3762
    val mMonth = 1
    val mDay = 1
    val kotlinLocation = GeoLocation(
        "Lakewood, NJ",
        40.096,
        -74.222,
        29.02,
        TimeZone.of("America/New_York")
    )

    val javaLocation = com.kosherjava.zmanim.java.zmanim.util.GeoLocation(
        kotlinLocation.locationName,
        kotlinLocation.latitude,
        kotlinLocation.longitude,
        kotlinLocation.elevation,
        java.util.TimeZone.getTimeZone(kotlinLocation.timeZone.toJavaZoneId())
    )

    fun Int.toHrMinSec(): Triple<Int, Int, Int> {
        var hour = 0
        var minute = 0
        var second = this
        minute += (second / 60)
        hour += (minute / 60)
        second %= 60
        minute %= 60
        return Triple(hour, minute, second)
    }
    @Test
    fun assertConstructorWorks() {
        val year = mYear + 1
        val month = mMonth + 1
        val day = mDay + 1
        val jd = JewishDate(year, HebrewMonth.getMonthForValue(month), day)
        assertEquals(year, jd.hebrewLocalDate.year.toInt())
        assertEquals(month, jd.hebrewLocalDate.month.value)
        assertEquals(day, jd.jewishDayOfMonth)
    }

    @Test
    fun assertSetYearWorks() {
        val year = mYear + 1
        val jd = JewishDate(mYear, HebrewMonth.getMonthForValue(mMonth), mDay)
        jd.jewishYear = year.toLong()
        assertEquals(year, jd.hebrewLocalDate.year.toInt())
        assertEquals(mMonth, jd.hebrewLocalDate.month.value)
        assertEquals(mDay, jd.jewishDayOfMonth)
    }
    @Test
    fun assertSetMonthWorks() {
        val month = mMonth + 1
        val jd = JewishDate(mYear, HebrewMonth.getMonthForValue(mMonth), mDay)
        jd.setJewishMonth(HebrewMonth.getMonthForValue(month))
        assertEquals(mYear, jd.hebrewLocalDate.year.toInt())
        assertEquals(month, jd.hebrewLocalDate.month.value)
        assertEquals(mDay, jd.jewishDayOfMonth)
    }

    @Test
    fun testRawOffset() {
        assertEquals(javaLocation.timeZone.rawOffset, kotlinLocation.timeZone.rawOffset)
    }

    @Test
    fun testJewishCalendar() {
        val kotlin = JewishCalendar()
        val java = com.kosherjava.zmanim.java.zmanim.hebrewcalendar.JewishCalendar()
        kotlin.chalakimSinceMoladTohu
        kotlin.cheshvanKislevKviah
        kotlin.dafYomiBavli
        kotlin.dafYomiYerushalmi
        kotlin.dayOfChanukah
        kotlin.dayOfOmer
        kotlin.daysInJewishMonth
        kotlin.daysInJewishYear
        kotlin.daysSinceStartOfJewishYear
        kotlin.gregorianDayOfMonth
        kotlin.gregorianMonth
        kotlin.gregorianYear
        kotlin.hasCandleLighting

        kotlin.inIsrael

        kotlin.isAseresYemeiTeshuva
        kotlin.isAssurBemelacha
        kotlin.isBeHaB
        kotlin.isBirkasHachamah
        kotlin.isChanukah
        kotlin.isCheshvanLong
        kotlin.isCholHamoed
        kotlin.isCholHamoedPesach
        kotlin.isCholHamoedSuccos
        kotlin.isErevRoshChodesh
        kotlin.isErevYomTov
        kotlin.isErevYomTovSheni
        kotlin.isHoshanaRabba
        kotlin.isIsruChag
        kotlin.isJewishLeapYear
        kotlin.isKislevShort
        kotlin.isMacharChodesh
        kotlin.isMukafChoma
        kotlin.isPesach
        kotlin.isPurim
        kotlin.isRoshChodesh
        kotlin.isRoshHashana
        kotlin.isShabbosMevorchim
        kotlin.isShavuos
        kotlin.isShminiAtzeres
        kotlin.isSimchasTorah
        kotlin.isSuccos
        kotlin.isTaanis
        kotlin.isTaanisBechoros
        kotlin.isTishaBav
        kotlin.isTomorrowShabbosOrYomTov

        kotlin.isUseModernHolidays

        kotlin.isYomKippur
        kotlin.isYomKippurKatan
        kotlin.isYomTov
        kotlin.isYomTovAssurBemelacha
        kotlin.jewishDayOfMonth
        kotlin.jewishMonth
        kotlin.jewishYear
        kotlin.molad
        kotlin.moladAsInstant
        kotlin.moladChalakim
        kotlin.moladHours
        kotlin.moladMinutes
        kotlin.parshah
        kotlin.sofZmanKidushLevana15Days
        kotlin.sofZmanKidushLevanaBetweenMoldos
        kotlin.specialShabbos
        kotlin.tchilasZmanKidushLevana3Days
        kotlin.tchilasZmanKidushLevana7Days
        kotlin.tekufasTishreiElapsedDays
        kotlin.upcomingParshah
        kotlin.yomTovIndex
    }

    @Test
    fun testComplexZmanimCalendar() {
        val calc = ComplexZmanimCalendar(kotlinLocation)
        val javaCalc = com.kosherjava.zmanim.java.zmanim.ComplexZmanimCalendar(javaLocation)
        assertEquals(javaCalc.solarMidnight.time, calc.solarMidnight?.toDate()?.time)
        assertEquals(
            javaCalc.getUTCSunrise(AstronomicalCalendar.GEOMETRIC_ZENITH),
            calc.getUTCSunrise(AstronomicalCalendar.GEOMETRIC_ZENITH),
            0.0
        )
        assertEquals(javaCalc.sunrise, calc.sunrise?.toDate())
        assertEquals(javaCalc.alos60, calc.alos60.toDate())
        assertEquals(javaCalc.tzais60, calc.tzais60.toDate())
        assertEquals(javaCalc.alos90Zmanis, calc.alos90Zmanis.toDate())
        assertEquals(javaCalc.tzais90Zmanis, calc.tzais90Zmanis.toDate())
        assertEquals(com.kosherjava.zmanim.java.zmanim.hebrewcalendar.JewishCalendar().moladAsDate.time, JewishCalendar().moladAsInstant.toDate()!!.time)
        assertEquals(com.kosherjava.zmanim.java.zmanim.hebrewcalendar.JewishCalendar().sofZmanKidushLevana15Days.time, JewishCalendar().sofZmanKidushLevana15Days.toDate()!!.time)
        assertEquals(javaCalc.sofZmanKidushLevanaBetweenMoldos?.time?.toDouble() ?: 0.0, calc.sofZmanKidushLevanaBetweenMoldos?.toDate()?.time?.toDouble() ?: 0.0, 0.0)
        assertEquals(javaCalc.sofZmanKidushLevana15Days?.time?.toDouble() ?: 0.0, (calc.sofZmanKidushLevana15Days?.toDate()?.time?.toDouble() ?: 0.0), 0.0)
        assertEquals(
            javaCalc.getTemporalHour(javaCalc.alos90Zmanis, javaCalc.tzais90Zmanis),
            calc.getTemporalHour(calc.alos90Zmanis.momentOfOccurrence, calc.tzais90Zmanis.momentOfOccurrence)
        )
        calc.apply {
            val values = listOf(
                Triple(javaCalc.shaahZmanis19Point8Degrees, shaahZmanis19Point8Degrees, "shaahZmanis19Point8Degrees"),
                Triple(javaCalc.shaahZmanis18Degrees, shaahZmanis18Degrees, "shaahZmanis18Degrees"),
                Triple(javaCalc.shaahZmanis26Degrees, shaahZmanis26Degrees, "shaahZmanis26Degrees"),
                Triple(javaCalc.shaahZmanis16Point1Degrees, shaahZmanis16Point1Degrees, "shaahZmanis16Point1Degrees"),
                Triple(javaCalc.shaahZmanis60Minutes, shaahZmanis60Minutes, "shaahZmanis60Minutes"),
                Triple(javaCalc.shaahZmanis72MinutesZmanis, shaahZmanis72MinutesZmanis, "shaahZmanis72MinutesZmanis"),
                Triple(javaCalc.shaahZmanis90Minutes, shaahZmanis90Minutes, "shaahZmanis90Minutes"),
                Triple(javaCalc.shaahZmanis90MinutesZmanis, shaahZmanis90MinutesZmanis, "shaahZmanis90MinutesZmanis"),
                Triple(javaCalc.shaahZmanis96MinutesZmanis, shaahZmanis96MinutesZmanis, "shaahZmanis96MinutesZmanis"),
                Triple(javaCalc.shaahZmanisAteretTorah, shaahZmanisAteretTorah, "shaahZmanisAteretTorah"),
                Triple(javaCalc.shaahZmanisAlos16Point1ToTzais3Point8, shaahZmanisAlos16Point1ToTzais3Point8, "shaahZmanisAlos16Point1ToTzais3Point8"),
                Triple(javaCalc.shaahZmanisAlos16Point1ToTzais3Point7, shaahZmanisAlos16Point1ToTzais3Point7, "shaahZmanisAlos16Point1ToTzais3Point7"),
                Triple(javaCalc.shaahZmanis96Minutes, shaahZmanis96Minutes, "shaahZmanis96Minutes"),
                Triple(javaCalc.shaahZmanis120Minutes, shaahZmanis120Minutes, "shaahZmanis120Minutes"),
                Triple(javaCalc.shaahZmanis120MinutesZmanis, shaahZmanis120MinutesZmanis, "shaahZmanis120MinutesZmanis"),
            );

            val listOfZmanim = listOf(
                Triple(javaCalc.plagHamincha120MinutesZmanis, plagHamincha120MinutesZmanis, "plagHamincha120MinutesZmanis"),
                Triple(javaCalc.plagHamincha120Minutes, plagHamincha120Minutes, "plagHamincha120Minutes"),
                Triple(javaCalc.alos60, alos60, "alos60"),
                Triple(javaCalc.alos72Zmanis, alos72Zmanis, "alos72Zmanis"),
                Triple(javaCalc.alos96, alos96, "alos96"),
                Triple(javaCalc.alos90Zmanis, alos90Zmanis, "alos90Zmanis"),
                Triple(javaCalc.alos96Zmanis, alos96Zmanis, "alos96Zmanis"),
                Triple(javaCalc.alos90, alos90, "alos90"),
                Triple(javaCalc.alos120, alos120, "alos120"),
                Triple(javaCalc.alos120Zmanis, alos120Zmanis, "alos120Zmanis"),
                Triple(javaCalc.alos26Degrees, alos26Degrees, "alos26Degrees"),
                Triple(javaCalc.alos18Degrees, alos18Degrees, "alos18Degrees"),
                Triple(javaCalc.alos19Degrees, alos19Degrees, "alos19Degrees"),
                Triple(javaCalc.alos19Point8Degrees, alos19Point8Degrees, "alos19Point8Degrees"),
                Triple(javaCalc.alos16Point1Degrees, alos16Point1Degrees, "alos16Point1Degrees"),
                Triple(javaCalc.misheyakir11Point5Degrees, misheyakir11Point5Degrees, "misheyakir11Point5Degrees"),
                Triple(javaCalc.misheyakir11Degrees, misheyakir11Degrees, "misheyakir11Degrees"),
                Triple(javaCalc.misheyakir10Point2Degrees, misheyakir10Point2Degrees, "misheyakir10Point2Degrees"),
                Triple(javaCalc.misheyakir7Point65Degrees, misheyakir7Point65Degrees, "misheyakir7Point65Degrees"),
                Triple(javaCalc.misheyakir9Point5Degrees, misheyakir9Point5Degrees, "misheyakir9Point5Degrees"),
                Triple(javaCalc.sofZmanShmaMGA19Point8Degrees, sofZmanShmaMGA19Point8Degrees, "sofZmanShmaMGA19Point8Degrees"),
                Triple(javaCalc.sofZmanShmaMGA16Point1Degrees, sofZmanShmaMGA16Point1Degrees, "sofZmanShmaMGA16Point1Degrees"),
                Triple(javaCalc.sofZmanShmaMGA18Degrees, sofZmanShmaMGA18Degrees, "sofZmanShmaMGA18Degrees"),
                Triple(javaCalc.sofZmanShmaMGA72MinutesZmanis, sofZmanShmaMGA72MinutesZmanis, "sofZmanShmaMGA72MinutesZmanis"),
                Triple(javaCalc.sofZmanShmaMGA90Minutes, sofZmanShmaMGA90Minutes, "sofZmanShmaMGA90Minutes"),
                Triple(javaCalc.sofZmanShmaMGA90MinutesZmanis, sofZmanShmaMGA90MinutesZmanis, "sofZmanShmaMGA90MinutesZmanis"),
                Triple(javaCalc.sofZmanShmaMGA96Minutes, sofZmanShmaMGA96Minutes, "sofZmanShmaMGA96Minutes"),
                Triple(javaCalc.sofZmanShmaMGA96MinutesZmanis, sofZmanShmaMGA96MinutesZmanis, "sofZmanShmaMGA96MinutesZmanis"),
                Triple(javaCalc.sofZmanShma3HoursBeforeChatzos, sofZmanShma3HoursBeforeChatzos, "sofZmanShma3HoursBeforeChatzos"),
                Triple(javaCalc.sofZmanShmaMGA120Minutes, sofZmanShmaMGA120Minutes, "sofZmanShmaMGA120Minutes"),
                Triple(javaCalc.sofZmanShmaAlos16Point1ToSunset, sofZmanShmaAlos16Point1ToSunset, "sofZmanShmaAlos16Point1ToSunset"),
                Triple(javaCalc.sofZmanShmaAlos16Point1ToTzaisGeonim7Point083Degrees, sofZmanShmaAlos16Point1ToTzaisGeonim7Point083Degrees, "sofZmanShmaAlos16Point1ToTzaisGeonim7Point083Degrees"),
                Triple(javaCalc.sofZmanTfilaMGA19Point8Degrees, sofZmanTfilaMGA19Point8Degrees, "sofZmanTfilaMGA19Point8Degrees"),
                Triple(javaCalc.sofZmanTfilaMGA16Point1Degrees, sofZmanTfilaMGA16Point1Degrees, "sofZmanTfilaMGA16Point1Degrees"),
                Triple(javaCalc.sofZmanTfilaMGA18Degrees, sofZmanTfilaMGA18Degrees, "sofZmanTfilaMGA18Degrees"),
                Triple(javaCalc.sofZmanTfilaMGA72MinutesZmanis, sofZmanTfilaMGA72MinutesZmanis, "sofZmanTfilaMGA72MinutesZmanis"),
                Triple(javaCalc.sofZmanTfilaMGA90MinutesZmanis, sofZmanTfilaMGA90MinutesZmanis, "sofZmanTfilaMGA90MinutesZmanis"),
                Triple(javaCalc.sofZmanTfilaMGA96MinutesZmanis, sofZmanTfilaMGA96MinutesZmanis, "sofZmanTfilaMGA96MinutesZmanis"),
                Triple(javaCalc.sofZmanTfila2HoursBeforeChatzos, sofZmanTfila2HoursBeforeChatzos, "sofZmanTfila2HoursBeforeChatzos"),
                Triple(javaCalc.minchaGedola30Minutes, minchaGedola30Minutes, "minchaGedola30Minutes"),
                Triple(javaCalc.minchaGedola16Point1Degrees, minchaGedola16Point1Degrees, "minchaGedola16Point1Degrees"),
                Triple(javaCalc.minchaGedolaAhavatShalom, minchaGedolaAhavatShalom, "minchaGedolaAhavatShalom"),
                Triple(javaCalc.minchaGedolaGreaterThan30, minchaGedolaGreaterThan30, "minchaGedolaGreaterThan30"),
                Triple(javaCalc.minchaKetana16Point1Degrees, minchaKetana16Point1Degrees, "minchaKetana16Point1Degrees"),
                Triple(javaCalc.minchaKetanaAhavatShalom, minchaKetanaAhavatShalom, "minchaKetanaAhavatShalom"),
                Triple(javaCalc.minchaKetana72Minutes, minchaKetana72Minutes, "minchaKetana72Minutes"),
                Triple(javaCalc.plagHamincha60Minutes, plagHamincha60Minutes, "plagHamincha60Minutes"),
                Triple(javaCalc.plagHamincha72Minutes, plagHamincha72Minutes, "plagHamincha72Minutes"),
                Triple(javaCalc.plagHamincha90Minutes, plagHamincha90Minutes, "plagHamincha90Minutes"),
                Triple(javaCalc.plagHamincha96Minutes, plagHamincha96Minutes, "plagHamincha96Minutes"),
                Triple(javaCalc.plagHamincha96MinutesZmanis, plagHamincha96MinutesZmanis, "plagHamincha96MinutesZmanis"),
                Triple(javaCalc.plagHamincha90MinutesZmanis, plagHamincha90MinutesZmanis, "plagHamincha90MinutesZmanis"),
                Triple(javaCalc.plagHamincha72MinutesZmanis, plagHamincha72MinutesZmanis, "plagHamincha72MinutesZmanis"),
                Triple(javaCalc.plagHamincha16Point1Degrees, plagHamincha16Point1Degrees, "plagHamincha16Point1Degrees"),
                Triple(javaCalc.plagHamincha19Point8Degrees, plagHamincha19Point8Degrees, "plagHamincha19Point8Degrees"),
                Triple(javaCalc.plagHamincha26Degrees, plagHamincha26Degrees, "plagHamincha26Degrees"),
                Triple(javaCalc.plagHamincha18Degrees, plagHamincha18Degrees, "plagHamincha18Degrees"),
                Triple(javaCalc.plagAlosToSunset, plagAlosToSunset, "plagAlosToSunset"),
                Triple(javaCalc.plagAlos16Point1ToTzaisGeonim7Point083Degrees, plagAlos16Point1ToTzaisGeonim7Point083Degrees, "plagAlos16Point1ToTzaisGeonim7Point083Degrees"),
                Triple(javaCalc.plagAhavatShalom, plagAhavatShalom, "plagAhavatShalom"),
                Triple(javaCalc.bainHashmashosRT13Point24Degrees, bainHashmashosRT13Point24Degrees, "bainHashmashosRT13Point24Degrees"),
                Triple(javaCalc.bainHashmashosRT58Point5Minutes, bainHashmashosRT58Point5Minutes, "bainHashmashosRT58Point5Minutes"),
                Triple(javaCalc.bainHashmashosRT13Point5MinutesBefore7Point083Degrees, bainHashmashosRT13Point5MinutesBefore7Point083Degrees, "bainHashmashosRT13Point5MinutesBefore7Point083Degrees"),
                Triple(javaCalc.bainHashmashosRT2Stars, bainHashmashosRT2Stars, "bainHashmashosRT2Stars"),
                Triple(javaCalc.bainHashmashosYereim18Minutes, bainHashmashosYereim18Minutes, "bainHashmashosYereim18Minutes"),
                Triple(javaCalc.bainHashmashosYereim3Point05Degrees, bainHashmashosYereim3Point05Degrees, "bainHashmashosYereim3Point05Degrees"),
                Triple(javaCalc.bainHashmashosYereim16Point875Minutes, bainHashmashosYereim16Point875Minutes, "bainHashmashosYereim16Point875Minutes"),
                Triple(javaCalc.bainHashmashosYereim2Point8Degrees, bainHashmashosYereim2Point8Degrees, "bainHashmashosYereim2Point8Degrees"),
                Triple(javaCalc.bainHashmashosYereim13Point5Minutes, bainHashmashosYereim13Point5Minutes, "bainHashmashosYereim13Point5Minutes"),
                Triple(javaCalc.bainHashmashosYereim2Point1Degrees, bainHashmashosYereim2Point1Degrees, "bainHashmashosYereim2Point1Degrees"),
                Triple(javaCalc.tzaisGeonim3Point7Degrees, tzaisGeonim3Point7Degrees, "tzaisGeonim3Point7Degrees"),
                Triple(javaCalc.tzaisGeonim3Point8Degrees, tzaisGeonim3Point8Degrees, "tzaisGeonim3Point8Degrees"),
                Triple(javaCalc.tzaisGeonim5Point95Degrees, tzaisGeonim5Point95Degrees, "tzaisGeonim5Point95Degrees"),
                Triple(javaCalc.tzaisGeonim3Point65Degrees, tzaisGeonim3Point65Degrees, "tzaisGeonim3Point65Degrees"),
                Triple(javaCalc.tzaisGeonim3Point676Degrees, tzaisGeonim3Point676Degrees, "tzaisGeonim3Point676Degrees"),
                Triple(javaCalc.tzaisGeonim4Point61Degrees, tzaisGeonim4Point61Degrees, "tzaisGeonim4Point61Degrees"),
                Triple(javaCalc.tzaisGeonim4Point37Degrees, tzaisGeonim4Point37Degrees, "tzaisGeonim4Point37Degrees"),
                Triple(javaCalc.tzaisGeonim5Point88Degrees, tzaisGeonim5Point88Degrees, "tzaisGeonim5Point88Degrees"),
                Triple(javaCalc.tzaisGeonim4Point8Degrees, tzaisGeonim4Point8Degrees, "tzaisGeonim4Point8Degrees"),
                Triple(javaCalc.tzaisGeonim6Point45Degrees, tzaisGeonim6Point45Degrees, "tzaisGeonim6Point45Degrees"),
                Triple(javaCalc.tzaisGeonim7Point083Degrees, tzaisGeonim7Point083Degrees, "tzaisGeonim7Point083Degrees"),
                Triple(javaCalc.tzaisGeonim7Point67Degrees, tzaisGeonim7Point67Degrees, "tzaisGeonim7Point67Degrees"),
                Triple(javaCalc.tzaisGeonim8Point5Degrees, tzaisGeonim8Point5Degrees, "tzaisGeonim8Point5Degrees"),
                Triple(javaCalc.tzaisGeonim9Point3Degrees, tzaisGeonim9Point3Degrees, "tzaisGeonim9Point3Degrees"),
                Triple(javaCalc.tzaisGeonim9Point75Degrees, tzaisGeonim9Point75Degrees, "tzaisGeonim9Point75Degrees"),
                Triple(javaCalc.tzais60, tzais60, "tzais60"),
                Triple(javaCalc.tzaisAteretTorah, tzaisAteretTorah, "tzaisAteretTorah"),
                Triple(javaCalc.tzais90Zmanis, tzais90Zmanis, "tzais90Zmanis"),
                Triple(javaCalc.tzais96Zmanis, tzais96Zmanis, "tzais96Zmanis"),
                Triple(javaCalc.tzais90, tzais90, "tzais90"),
                Triple(javaCalc.tzais120, tzais120, "tzais120"),
                Triple(javaCalc.tzais120Zmanis, tzais120Zmanis, "tzais120Zmanis"),
                Triple(javaCalc.tzais16Point1Degrees, tzais16Point1Degrees, "tzais16Point1Degrees"),
                Triple(javaCalc.tzais26Degrees, tzais26Degrees, "tzais26Degrees"),
                Triple(javaCalc.tzais18Degrees, tzais18Degrees, "tzais18Degrees"),
                Triple(javaCalc.tzais19Point8Degrees, tzais19Point8Degrees, "tzais19Point8Degrees"),
                Triple(javaCalc.tzais96, tzais96, "tzais96"),
                Triple(javaCalc.fixedLocalChatzos, fixedLocalChatzos, "fixedLocalChatzos"),
                Triple(javaCalc.sofZmanKidushLevanaBetweenMoldos, sofZmanKidushLevanaBetweenMoldos, "sofZmanKidushLevanaBetweenMoldos"),
                Triple(javaCalc.sofZmanKidushLevana15Days, sofZmanKidushLevana15Days, "sofZmanKidushLevana15Days"),
                Triple(javaCalc.zmanMolad, zmanMolad, "zmanMolad"),
                Triple(javaCalc.sofZmanBiurChametzGRA, sofZmanBiurChametzGRA, "sofZmanBiurChametzGRA"),
                Triple(javaCalc.sofZmanBiurChametzMGA72Minutes, sofZmanBiurChametzMGA72Minutes, "sofZmanBiurChametzMGA72Minutes"),
                Triple(javaCalc.sofZmanBiurChametzMGA16Point1Degrees, sofZmanBiurChametzMGA16Point1Degrees, "sofZmanBiurChametzMGA16Point1Degrees"),
                Triple(javaCalc.solarMidnight, solarMidnight, "solarMidnight"),
                Triple(javaCalc.sofZmanShmaBaalHatanya, sofZmanShmaBaalHatanya, "sofZmanShmaBaalHatanya"),
                Triple(javaCalc.sofZmanTfilaBaalHatanya, sofZmanTfilaBaalHatanya, "sofZmanTfilaBaalHatanya"),
                Triple(javaCalc.sofZmanBiurChametzBaalHatanya, sofZmanBiurChametzBaalHatanya, "sofZmanBiurChametzBaalHatanya"),
                Triple(javaCalc.minchaGedolaBaalHatanya, minchaGedolaBaalHatanya, "minchaGedolaBaalHatanya"),
                Triple(javaCalc.minchaGedolaBaalHatanyaGreaterThan30, minchaGedolaBaalHatanyaGreaterThan30, "minchaGedolaBaalHatanyaGreaterThan30"),
                Triple(javaCalc.minchaKetanaBaalHatanya, minchaKetanaBaalHatanya, "minchaKetanaBaalHatanya"),
                Triple(javaCalc.plagHaminchaBaalHatanya, plagHaminchaBaalHatanya, "plagHaminchaBaalHatanya"),
                Triple(javaCalc.tzaisBaalHatanya, tzaisBaalHatanya, "tzaisBaalHatanya"),
                Triple(javaCalc.sofZmanShmaMGA18DegreesToFixedLocalChatzos, sofZmanShmaMGA18DegreesToFixedLocalChatzos, "sofZmanShmaMGA18DegreesToFixedLocalChatzos"),
                Triple(javaCalc.sofZmanShmaMGA16Point1DegreesToFixedLocalChatzos, sofZmanShmaMGA16Point1DegreesToFixedLocalChatzos, "sofZmanShmaMGA16Point1DegreesToFixedLocalChatzos"),
                Triple(javaCalc.sofZmanShmaMGA90MinutesToFixedLocalChatzos, sofZmanShmaMGA90MinutesToFixedLocalChatzos, "sofZmanShmaMGA90MinutesToFixedLocalChatzos"),
                Triple(javaCalc.sofZmanShmaMGA72MinutesToFixedLocalChatzos, sofZmanShmaMGA72MinutesToFixedLocalChatzos, "sofZmanShmaMGA72MinutesToFixedLocalChatzos"),
                Triple(javaCalc.sofZmanShmaGRASunriseToFixedLocalChatzos, sofZmanShmaGRASunriseToFixedLocalChatzos, "sofZmanShmaGRASunriseToFixedLocalChatzos"),
                Triple(javaCalc.sofZmanTfilaGRASunriseToFixedLocalChatzos, sofZmanTfilaGRASunriseToFixedLocalChatzos, "sofZmanTfilaGRASunriseToFixedLocalChatzos"),
                Triple(javaCalc.minchaGedolaGRAFixedLocalChatzos30Minutes, minchaGedolaGRAFixedLocalChatzos30Minutes, "minchaGedolaGRAFixedLocalChatzos30Minutes"),
                Triple(javaCalc.minchaKetanaGRAFixedLocalChatzosToSunset, minchaKetanaGRAFixedLocalChatzosToSunset, "minchaKetanaGRAFixedLocalChatzosToSunset"),
                Triple(javaCalc.plagHaminchaGRAFixedLocalChatzosToSunset, plagHaminchaGRAFixedLocalChatzosToSunset, "plagHaminchaGRAFixedLocalChatzosToSunset"),
                Triple(javaCalc.tzais50, tzais50, "tzais50"),
                Triple(javaCalc.samuchLeMinchaKetanaGRA, samuchLeMinchaKetanaGRA, "samuchLeMinchaKetanaGRA"),
                Triple(javaCalc.samuchLeMinchaKetana16Point1Degrees, samuchLeMinchaKetana16Point1Degrees, "samuchLeMinchaKetana16Point1Degrees")
            ).map { Triple(it.first?.time?.toDouble(), it.second?.toDate()?.time?.toDouble(), it.third) }
            testValues(values, transformActual = { it.duration.inWholeMilliseconds })
            testValues(listOfZmanim, 0.0)
        }
    }

    @Test
    fun testAstronomicalCalendar() {
        val javaAstroCal = com.kosherjava.zmanim.java.zmanim.AstronomicalCalendar(javaLocation)
        val kotlinAstroCal = AstronomicalCalendar(kotlinLocation)
        javaAstroCal.apply {
            assertEquals(sunrise, kotlinAstroCal.sunrise?.toDate())
            assertEquals(seaLevelSunrise, kotlinAstroCal.seaLevelSunrise?.toDate())
            assertEquals(beginCivilTwilight, kotlinAstroCal.beginCivilTwilight?.toDate())
            assertEquals(beginNauticalTwilight, kotlinAstroCal.beginNauticalTwilight?.toDate())
            assertEquals(beginAstronomicalTwilight, kotlinAstroCal.beginAstronomicalTwilight?.toDate())
            assertEquals(sunset, kotlinAstroCal.sunset?.toDate())
            assertEquals(seaLevelSunset, kotlinAstroCal.seaLevelSunset?.toDate())
            assertEquals(endCivilTwilight, kotlinAstroCal.endCivilTwilight?.toDate())
            assertEquals(endNauticalTwilight, kotlinAstroCal.endNauticalTwilight?.toDate())
            assertEquals(endAstronomicalTwilight, kotlinAstroCal.endAstronomicalTwilight?.toDate())
    //            getTimeOffset
    //            getTimeOffset
    //            getSunriseOffsetByDegrees()
    //            getSunsetOffsetByDegrees()
    //            getUTCSunrise()
    //            getUTCSeaLevelSunrise()
    //            getUTCSunset()
    //            getUTCSeaLevelSunset()
            assertEquals(temporalHour, kotlinAstroCal.temporalHour)
            getTemporalHour()
            assertEquals(sunTransit, kotlinAstroCal.sunTransit?.toDate())
            getSunTransit()
    //            getSunriseSolarDipFromOffset()
    //            getSunsetSolarDipFromOffset()
            assertEquals(
                calendar.toInstant().toKotlinInstant().toEpochMilliseconds(),
                kotlinAstroCal.localDateTime.toInstant(kotlinAstroCal.geoLocation.timeZone).toEpochMilliseconds()
            )
        }
    }

    private fun <E, A> testValues(
        values: List<Triple<E, A, String>>,
        transformExpected: (e: E) -> E = { it },
        transformActual: (a: A) -> E = { it as E },
    ) {
        for ((index, triple) in values.withIndex()) {
            val (expected, actual, label) = triple
            runCatching {
                assertEquals(transformExpected(expected), transformActual(actual))
            }.getOrElse {
                println("Error on $label index $index")
                throw it
            }
        }
    }
    private fun testValues(
        values: List<Triple<Double?, Double?, String>>,
        delta: Double
    ) {
        for ((index, pair) in values.withIndex()) {
            val (expected, actual, label) = pair
            runCatching {
                assertEquals(expected ?: 0.0, actual ?: 0.0, delta)
            }.getOrElse {
                println("Error on $label at index $index")
                throw it
            }
        }
    }

    /*fun testAllTimes() {
        val delimiter = "\t"
        val location = GeoLocation(
            "Lakewood, NJ",
            40.096,
            -74.222,
            29.02,
            TimeZone.of("America/New_York")
        )
        val calc = ComplexZmanimCalendar(location)
        val javaCalc = com.kosherjava.zmanim.java.zmanim.ComplexZmanimCalendar(
            com.kosherjava.zmanim.java.zmanim.util.GeoLocation(
                location.locationName,
                location.latitude,
                location.longitude,
                java.util.TimeZone.getTimeZone(location.timeZone.toJavaZoneId())
            )
        )

//        calc.isUseElevation = true
        println("Time zone of calc.calendar: ${calc.geoLocation.timeZone}")
        val lines = Files.readAllLines(File("./zmanim_Lakewood_NJ.tsv").toPath()).drop(1)
        var zoneOffset: UtcOffset? = null
        val pattern = DateTimeFormatter.ofPattern("MMM d, uuuu")

        for (line in lines) {
            val fields = line.split(delimiter)
            try {
                val quote = "\""
                val civilDate = java.time.LocalDate.parse(fields.first().removeSurrounding(quote), pattern).toKotlinLocalDate()
                val tz = calc.geoLocation.timeZone
                println("Zone id: $tz")
                val atStartOfDay = civilDate.atStartOfDayIn(tz)
                println("At start of day: $atStartOfDay")
                val offsetInSeconds = tz.rawOffset.milliseconds.inWholeSeconds.toInt()
                if (zoneOffset == null) {
                    val (hr, min, sec) = offsetInSeconds.toHrMinSec()
                    zoneOffset = UtcOffset(hr,min, sec)
                }
                calc.localDate = atStartOfDay.toLocalDateTime(tz)
                val jewishDate = JewishDate(calc.localDate.date)
                val jewishCalendar = JewishCalendar(calc.localDate.date)
                Assert.assertEquals(fields[1].removeSurrounding(quote), jewishDate.toString())
//                assertYomTovOrParshaMatches(jewishDate, jewishCalendar, fields, quote)
                val timeFormatter = DateTimeFormatter.ofPattern("h:mm:ss a")
                fun Instant?.fmt() =
                    this?.let { java.time.Instant.ofEpochSecond(it.epochSeconds)?.atOffset(ZoneOffset.ofTotalSeconds(offsetInSeconds))?.also { println(it) }?.format(timeFormatter) }
                var col = 4
                Assert.assertEquals(fields[col++], jewishCalendar.dafYomiBavli?.let { "${it.masechtaTransliterated} ${it.daf}" })
                Assert.assertEquals(fields[col++], calc.alos120.fmt())
                Assert.assertEquals(fields[col++], calc.alos120Zmanis.fmt())
                Assert.assertEquals(fields[col++], calc.alos26Degrees.fmt())
                Assert.assertEquals(fields[col++], calc.alos96.fmt())
                Assert.assertEquals(fields[col++], calc.alos96Zmanis.fmt())
                Assert.assertEquals(fields[col++], calc.alos19Point8Degrees.fmt())
                Assert.assertEquals(fields[col++], calc.alos19Degrees.fmt())
                Assert.assertEquals(fields[col++], calc.alos90.fmt())
                Assert.assertEquals(fields[col++], calc.alos90Zmanis.fmt())
                Assert.assertEquals(fields[col++], calc.alos18Degrees.fmt())
                Assert.assertEquals(fields[col++], calc.alosBaalHatanya.fmt())
                Assert.assertEquals(fields[col++], calc.alos16Point1Degrees.fmt())
                Assert.assertEquals(fields[col++], calc.alos72.fmt())
                Assert.assertEquals(fields[col++], calc.alos72Zmanis.fmt())
//                Assert.assertEquals(fields[col++], calc.alos60.fmt())
                Assert.assertEquals(fields[col++], calc.misheyakir11Point5Degrees.fmt())
                Assert.assertEquals(fields[col++], calc.misheyakir11Degrees.fmt())
                Assert.assertEquals(fields[col++], calc.misheyakir10Point2Degrees.fmt())
                Assert.assertEquals(fields[col++], calc.misheyakir9Point5Degrees.fmt())
                Assert.assertEquals(fields[col++], calc.misheyakir7Point65Degrees.fmt())
//                Assert.assertEquals(fields[col++], calc.sunrise.fmt())
                Assert.assertEquals(fields[col++], calc.seaLevelSunrise.fmt())
                Assert.assertEquals(fields[col++], calc.sofZmanShmaMGA120Minutes.fmt())
                Assert.assertEquals(fields[col++], calc.sofZmanShmaMGA16Point1Degrees.fmt())
                Assert.assertEquals(fields[col++], calc.sofZmanShmaMGA18Degrees.fmt())
                Assert.assertEquals(fields[col++], calc.sofZmanShmaMGA19Point8Degrees.fmt())
                Assert.assertEquals(fields[col++], calc.sofZmanShmaMGA72Minutes.fmt())
                Assert.assertEquals(fields[col++], calc.sofZmanShmaMGA72MinutesZmanis.fmt())
                Assert.assertEquals(fields[col++], calc.sofZmanShmaMGA90Minutes.fmt())
                Assert.assertEquals(fields[col++], calc.sofZmanShmaMGA90MinutesZmanis.fmt())
                Assert.assertEquals(fields[col++], calc.sofZmanShmaMGA96Minutes.fmt())
                Assert.assertEquals(fields[col++], calc.sofZmanShmaMGA96MinutesZmanis.fmt())
                Assert.assertEquals(fields[col++], calc.sofZmanShmaAlos16Point1ToSunset.fmt())
                Assert.assertEquals(fields[col++], calc.sofZmanShmaAlos16Point1ToTzaisGeonim7Point083Degrees.fmt())
                Assert.assertEquals(fields[col++], calc.sofZmanShmaMGA16Point1DegreesToFixedLocalChatzos.fmt())
                Assert.assertEquals(fields[col++], calc.sofZmanShmaMGA18DegreesToFixedLocalChatzos.fmt())
                Assert.assertEquals(fields[col++], calc.sofZmanShmaMGA90MinutesToFixedLocalChatzos.fmt())
                Assert.assertEquals(fields[col++], calc.sofZmanShmaMGA72MinutesToFixedLocalChatzos.fmt())
                Assert.assertEquals(fields[col++], calc.sofZmanShmaGRASunriseToFixedLocalChatzos.fmt())
                Assert.assertEquals(fields[col++], calc.sofZmanShma3HoursBeforeChatzos.fmt())
                Assert.assertEquals(fields[col++], calc.sofZmanShmaAteretTorah.fmt())
                Assert.assertEquals(fields[col++], calc.sofZmanShmaBaalHatanya.fmt())
                Assert.assertEquals(fields[col++], calc.sofZmanShmaGRA.fmt())
                Assert.assertEquals(fields[col++], calc.sofZmanTfilaMGA120Minutes.fmt())
                Assert.assertEquals(fields[col++], calc.sofZmanTfilaMGA16Point1Degrees.fmt())
                Assert.assertEquals(fields[col++], calc.sofZmanTfilaMGA18Degrees.fmt())
                Assert.assertEquals(fields[col++], calc.sofZmanTfilaMGA19Point8Degrees.fmt())
                Assert.assertEquals(fields[col++], calc.sofZmanTfilaMGA72Minutes.fmt())
                Assert.assertEquals(fields[col++], calc.sofZmanTfilaMGA72MinutesZmanis.fmt())
                Assert.assertEquals(fields[col++], calc.sofZmanTfilaMGA90Minutes.fmt())
                Assert.assertEquals(fields[col++], calc.sofZmanTfilaMGA90MinutesZmanis.fmt())
                Assert.assertEquals(fields[col++], calc.sofZmanTfilaMGA96Minutes.fmt())
                Assert.assertEquals(fields[col++], calc.sofZmanTfilaMGA96MinutesZmanis.fmt())
                Assert.assertEquals(fields[col++], calc.sofZmanTfilaGRASunriseToFixedLocalChatzos.fmt())
                Assert.assertEquals(fields[col++], calc.sofZmanTfila2HoursBeforeChatzos.fmt())
                Assert.assertEquals(fields[col++], calc.sofZmanTfilahAteretTorah.fmt())
                Assert.assertEquals(fields[col++], calc.sofZmanTfilaBaalHatanya.fmt())
                Assert.assertEquals(fields[col++], calc.sofZmanTfilaGRA.fmt())
                Assert.assertEquals(fields[col++], calc.chatzos.fmt())
                Assert.assertEquals(fields[col++], calc.fixedLocalChatzos.fmt())

                Assert.assertEquals(fields[col++], calc.minchaGedola.fmt()) // Mincha Gedola GRA in spreadsheet
                Assert.assertEquals(fields[col++], calc.minchaGedolaBaalHatanya.fmt())
                Assert.assertEquals(fields[col++], calc.minchaGedola72Minutes.fmt())
                Assert.assertEquals(fields[col++], calc.minchaGedola16Point1Degrees.fmt())
                Assert.assertEquals(fields[col++], calc.minchaGedolaAteretTorah.fmt())
                Assert.assertEquals(fields[col++], calc.minchaGedolaGreaterThan30.fmt())
                Assert.assertEquals(fields[col++], calc.minchaGedolaBaalHatanyaGreaterThan30.fmt())
                Assert.assertEquals(fields[col++], calc.minchaGedolaGRAFixedLocalChatzos30Minutes.fmt())
                Assert.assertEquals(fields[col++], calc.minchaGedolaAhavatShalom.fmt())

                Assert.assertEquals(fields[col++], calc.samuchLeMinchaKetanaGRA.fmt())

                Assert.assertEquals(fields[col++], calc.minchaKetana.fmt()) //gra?
                Assert.assertEquals(fields[col++], calc.minchaKetanaBaalHatanya.fmt())
                Assert.assertEquals(fields[col++], calc.samuchLeMinchaKetana16Point1Degrees.fmt())
                Assert.assertEquals(fields[col++], calc.minchaKetana16Point1Degrees.fmt())
                Assert.assertEquals(fields[col++], calc.samuchLeMinchaKetana72Minutes.fmt())
                Assert.assertEquals(fields[col++], calc.minchaKetana72Minutes.fmt())
                Assert.assertEquals(fields[col++], calc.minchaKetanaAteretTorah.fmt())
                Assert.assertEquals(fields[col++], calc.minchaKetanaGRAFixedLocalChatzosToSunset.fmt())
                Assert.assertEquals(fields[col++], calc.minchaKetanaAhavatShalom.fmt())

                Assert.assertEquals(fields[col++], calc.plagHamincha.fmt())
// Plag Hamincha
// Plag Hamincha Baal Hatanya
//Plag Hamincha 60 Minutes
//Plag Hamincha 72 Minutes
//Plag Hamincha 72 Minutes Zmaniyos
//Plag Hamincha 16.1°
//Plag Hamincha 18°
//Plag Hamincha 19.8°
//Plag Hamincha 90 Minutes
//Plag Hamincha 90 Minutes Zmaniyos
//Plag Hamincha 96 Minutes
//Plag Hamincha 96 Minutes Zmaniyos
//Plag Hamincha 120 Minutes
//Plag Hamincha 120 Minutes Zmaniyos
//Plag Hamincha Alos 16.1° To Sunset
// Plag Hamincha Ahavat Shalom
//Plag Hamincha Alos 16.1° To Tzais Geonim 7.083°
//Plag Hamincha 26°
// Plag Hamincha Ateret Torah
// Plag Hamincha Fixed Local Chatzos to Sunset
//Bain Hasmashos Yereim 3.05
//Bain Hasmashos Yereim 2.8
//Bain Hasmashos Yereim 18 Minutes
//Bain Hasmashos Yereim 16.875 Minutes
//Bain Hasmashos Yereim 2.1°
//Bain Hasmashos Yereim 13.5 Minutes
//Candle Lighting 18 Minutes
// Sunset (Sea Level)
//Sunset (25.58 Meters)
//Bain Hasmashos Rabainu Tam 13.5 Minutes Before 7.083°
//Bain Hasmashos Rabainu Tam 58.5 Minutes
//Bain Hasmashos Rabainu Tam 13.24°
//Bain Hasmashos Rabainu Tam 2 Stars
//Tzais Geonim 3.65°
//Tzais Geonim 3.676°
//Tzais Geonim 3.7°
//Tzais Geonim 3.8°
//Tzais Geonim 4.37°
//Tzais Geonim 4.61°
//Tzais Geonim 4.8°
//Tzais Geonim 5.88°
//Tzais Geonim 5.95°
// Tzais Baal Hatanya
//Tzais Geonim 6.45°
//Tzais Geonim 7.083°
//Tzais Geonim 7.67°
//Tzais 50 Minutes
//Tzais 60 Minutes
//Tzais Geonim 8.5°
//Tzais 72 Minutes
//Tzais Geonim 9.3°
//Tzais Geonim 9.75°
//Tzais 72 Minutes Zmaniyos
//Tzais 16.1°
//Tzais 90 Minutes
//Tzais 90 Minutes Zmaniyos
//Tzais 18°
//Tzais 19.8°
//Tzais 96 Minutes
//Tzais 96 Minutes Zmaniyos
//Tzais 26°
//Tzais 120 Minutes
//Tzais 120 Minutes Zmaniyos
// Chatzos Halayla
// Molad
//Techilas Zman Kiddush Levana 3 Days
//Techilas Zman Kiddush Levana 7 Days
// Sof Zman Kiddush Levana Between Moldos
//Sof Zman Kiddush Levana 15 Days
// Shaah Zmanis GRA
// Shaah Zmanis Baal Hatanya
//Shaah Zmanis 72 Minutes
//Shaah Zmanis 72 Minutes Zmaniyos
//Shaah Zmanis 16.1°
//Shaah Zmanis 90 Minutes
//Shaah Zmanis 90 Minutes Zmaniyos
//Shaah Zmanis 18°
//Shaah Zmanis 19.8°
//Shaah Zmanis 96 Minutes
//Shaah Zmanis 96 Minutes Zmaniyos
//Shaah Zmanis 26°
//Shaah Zmanis 120 Minutes
//Shaah Zmanis 120 Minutes Zmaniyos
// Shaah Zmanit Ateret Torah
// Shaah Zmanit Ahavat Shalom
// Tachanun Shacharis
// Tachanun Mincha
// Hallel
// Hallel Shalem
            } catch (t: Throwable) {
                if (t is AssertionError) throw t
                t.printStackTrace()
                continue
            }
        }
    }*/

    private fun assertYomTovOrParshaMatches(
        jewishDate: JewishDate,
        jewishCalendar: JewishCalendar,
        fields: List<String>,
        quote: String
    ) {
        val formatted = hdf.format(jewishDate)
        println("Date format: $formatted, parsha/yom tov: ${getParshaOrYomTov(jewishCalendar)}")
        val escapedParshaOrYomTov = fields[3].removeSurrounding(quote)
        Assert.assertEquals(
            escapedParshaOrYomTov,
            getParshaOrYomTov(jewishCalendar)
        )
    }

    private fun getParshaOrYomTov(jewishCalendar: JewishCalendar) = hdf
        .transliteratedParshiosList[jewishCalendar.parshah]
        ?.let {
            val specialShabbos = jewishCalendar.specialShabbos
            if (specialShabbos != JewishCalendar.Parsha.NONE) "$it, ${hdf.transliteratedParshiosList[specialShabbos]}" else it
        }
        ?.ifBlank {
            jewishCalendar
                .yomTovIndex
                .takeIf { it >= 0 }
                ?.let { hdf.transliteratedHolidayList[it].takeIf { it !in arrayOf("Erev Pesach"/*, "Chol Hamoed Pesach"*/) } }
                ?: when {
                    jewishCalendar.isYomKippurKatan -> "Yom Kippur Katan"
                    jewishCalendar.isRoshChodesh -> {
                        "Rosh Chodesh ${
                            hdf.transliteratedMonthList[
                                    if (jewishCalendar.jewishDayOfMonth >= 29) jewishCalendar.hebrewLocalDate.month.value/*intentionally +1 month*/
                                    else jewishCalendar.hebrewLocalDate.month.value - 1
                            ]
                        }"
                    }
                    else -> null
                }
        } ?: ""
}