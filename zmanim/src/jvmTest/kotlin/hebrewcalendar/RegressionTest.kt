package hebrewcalendar

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toJavaInstant
import kotlinx.datetime.toJavaZoneId
import kotlinx.datetime.toKotlinInstant
import kotlinx.datetime.toKotlinLocalDate
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.until
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Test
import io.github.kdroidfilter.kosherkotlin.ComplexZmanimCalendar
import io.github.kdroidfilter.kosherkotlin.Zman
import io.github.kdroidfilter.kosherkotlin.hebrewcalendar.Daf
import io.github.kdroidfilter.kosherkotlin.hebrewcalendar.HebrewDateFormatter
import io.github.kdroidfilter.kosherkotlin.hebrewcalendar.HebrewLocalDate
import io.github.kdroidfilter.kosherkotlin.hebrewcalendar.JewishDate
import io.github.kdroidfilter.kosherkotlin.metadata.ZmanCalculationMethod
import io.github.kdroidfilter.kosherkotlin.metadata.ZmanDefinition
import io.github.kdroidfilter.kosherkotlin.metadata.ZmanType
import io.github.kdroidfilter.kosherkotlin.util.GeoLocation
import io.github.kdroidfilter.kosherkotlin.util.GeoLocation.Companion.rawOffset
import java.time.temporal.ChronoUnit
import java.util.Calendar
import java.util.Date
import java.util.stream.Collectors
import java.util.stream.LongStream

class RegressionTest {
    companion object {
        fun Instant.toDate(): Date = this.let { Date.from(it.toJavaInstant()) }

        val YEAR_6000 = HebrewLocalDate(6000, io.github.kdroidfilter.kosherkotlin.hebrewcalendar.HebrewMonth.TISHREI, 1).toLocalDateGregorian()
        val YEAR_6000_INSTANT = YEAR_6000.atStartOfDayIn(TimeZone.UTC)

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

    val javaLocation = com.kosherjava.zmanim.util.GeoLocation(
        kotlinLocation.locationName,
        kotlinLocation.latitude,
        kotlinLocation.longitude,
        kotlinLocation.elevation,
        java.util.TimeZone.getTimeZone(kotlinLocation.timeZone.toJavaZoneId())
    )

    @Test
    fun testComplexZmanimCalendarForAllLocations() {
        val startingDateGregorian = LocalDate(1900, Month.JANUARY, 1)

        val kotlinDate = startingDateGregorian.atStartOfDayIn(TimeZone.UTC)
        val javaDate = java.time.LocalDate.of(
            startingDateGregorian.year,
            startingDateGregorian.month,
            startingDateGregorian.dayOfMonth
        )
        val allDays = LongStream
            .range(0, kotlinDate.until(YEAR_6000_INSTANT, DateTimeUnit.DAY, TimeZone.UTC))
            .parallel()
            .mapToObj {
                javaDate.plusDays(it) to kotlinDate.plus(it, DateTimeUnit.DAY, TimeZone.UTC)
                    .toLocalDateTime(TimeZone.UTC)
            }
            .collect(Collectors.toList())
        for ((kotlin, javaLoc) in TestHelper.allLocations.zip(TestHelper.allJavaLocations)) {
            println("Testing ${kotlin.locationName}")
            allDays.parallelStream().forEach { (javaDate, kotlinDate) ->
                testComplexZmanimCalendar(kotlin, javaLoc, javaDate, kotlinDate.date)
            }
        }
    }

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
        val jd = JewishDate(year, io.github.kdroidfilter.kosherkotlin.hebrewcalendar.HebrewMonth.getMonthForValue(month), day)
        assertEquals(year, jd.hebrewLocalDate.year.toInt())
        assertEquals(month, jd.hebrewLocalDate.month.value)
        assertEquals(day, jd.jewishDayOfMonth)
    }

    @Test
    fun assertSetYearWorks() {
        val year = mYear + 1
        val jd = JewishDate(mYear, io.github.kdroidfilter.kosherkotlin.hebrewcalendar.HebrewMonth.getMonthForValue(mMonth), mDay)
        jd.jewishYear = year.toLong()
        assertEquals(year, jd.hebrewLocalDate.year.toInt())
        assertEquals(mMonth, jd.hebrewLocalDate.month.value)
        assertEquals(mDay, jd.jewishDayOfMonth)
    }

    @Test
    fun assertSetMonthWorks() {
        val month = mMonth + 1
        val jd = JewishDate(mYear, io.github.kdroidfilter.kosherkotlin.hebrewcalendar.HebrewMonth.getMonthForValue(mMonth), mDay)
        jd.setJewishMonth(io.github.kdroidfilter.kosherkotlin.hebrewcalendar.HebrewMonth.getMonthForValue(month))
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
        val gregorianEnd = LocalDate(2239, Month.SEPTEMBER, 30)
        val hebrewStart =
            HebrewLocalDate.STARTING_DATE_HEBREW //start of hillel hakatan's calender - java upstream doesn't support earlier
        val distantFutureJewishDate = JewishDate(gregorianEnd) //6000-1-1 hebrew

        val javaCurrentJewishCalendar =
            com.kosherjava.zmanim.hebrewcalendar.JewishCalendar(
                hebrewStart.year.toInt(),
                hebrewStart.month.value,
                hebrewStart.dayOfMonth
            )
        val javaCurrentJewishCalendarIsraeli =
            com.kosherjava.zmanim.hebrewcalendar.JewishCalendar(
                hebrewStart.year.toInt(),
                hebrewStart.month.value,
                hebrewStart.dayOfMonth,
                true
            )
        val javaCurrentJewishCalendarIsraeliUseModernHolidays =
            com.kosherjava.zmanim.hebrewcalendar.JewishCalendar(
                hebrewStart.year.toInt(),
                hebrewStart.month.value,
                hebrewStart.dayOfMonth,
                true
            ).apply {
                isUseModernHolidays = true
            }

        val kotlinCurrentJewishCalendar =
            io.github.kdroidfilter.kosherkotlin.hebrewcalendar.JewishCalendar(hebrewStart)
        val kotlinCurrentJewishCalendarIsraeli =
            io.github.kdroidfilter.kosherkotlin.hebrewcalendar.JewishCalendar(hebrewStart, true)
        val kotlinCurrentJewishCalendarIsraeliUseModernHolidays =
            io.github.kdroidfilter.kosherkotlin.hebrewcalendar.JewishCalendar(hebrewStart, true, true)

        while (
            javaCurrentJewishCalendar.jewishYear != distantFutureJewishDate.jewishYear.toInt() ||
            javaCurrentJewishCalendar.jewishMonth != distantFutureJewishDate.jewishMonth.value ||
            javaCurrentJewishCalendar.jewishDayOfMonth != distantFutureJewishDate.jewishDayOfMonth
        ) {
//            println("Calendar (java): $javaCurrentJewishCalendar")
//            println("Calendar (kotl): $kotlinCurrentJewishCalendar")
            assertAllValues(kotlinCurrentJewishCalendar, javaCurrentJewishCalendar)
            assertAllValues(kotlinCurrentJewishCalendarIsraeli, javaCurrentJewishCalendarIsraeli)
            assertAllValues(
                kotlinCurrentJewishCalendarIsraeliUseModernHolidays,
                javaCurrentJewishCalendarIsraeliUseModernHolidays
            )
            javaCurrentJewishCalendar.forward(java.util.Calendar.DATE, 1)
            javaCurrentJewishCalendarIsraeli.forward(java.util.Calendar.DATE, 1)
            javaCurrentJewishCalendarIsraeliUseModernHolidays.forward(java.util.Calendar.DATE, 1)
            kotlinCurrentJewishCalendar.forward(DateTimeUnit.DAY, 1)
            kotlinCurrentJewishCalendarIsraeli.forward(DateTimeUnit.DAY, 1)
            kotlinCurrentJewishCalendarIsraeliUseModernHolidays.forward(DateTimeUnit.DAY, 1)
        }
    }

    private fun assertAllValues(
        kotlin: io.github.kdroidfilter.kosherkotlin.hebrewcalendar.JewishCalendar,
        java: com.kosherjava.zmanim.hebrewcalendar.JewishCalendar
    ) {
        assertEquals(java.chalakimSinceMoladTohu, kotlin.chalakimSinceMoladTohu)
        assertEquals(java.cheshvanKislevKviah, kotlin.cheshvanKislevKviah)
        assertEquals(
            runCatching { java.dafYomiBavli.let { Daf(it.masechtaNumber, it.daf) } }.getOrNull(),
            runCatching { kotlin.dafYomiBavli }.getOrNull(),
        )
        assertEquals(java.dayOfChanukah, kotlin.dayOfChanukah)
        assertEquals(java.dayOfOmer, kotlin.dayOfOmer)
        assertEquals(java.daysInJewishMonth, kotlin.daysInJewishMonth)
        assertEquals(java.daysInJewishYear, kotlin.daysInJewishYear)
        assertEquals(java.gregorianDayOfMonth, kotlin.gregorianDayOfMonth)
        assertEquals(java.gregorianMonth + 1, kotlin.gregorianMonth)
        assertEquals(java.gregorianYear, kotlin.gregorianYear)
        assertEquals(java.hasCandleLighting(), kotlin.hasCandleLighting)
        assertEquals(java.inIsrael, kotlin.inIsrael)
        assertEquals(java.isAseresYemeiTeshuva, kotlin.isAseresYemeiTeshuva)
        assertEquals(java.isAssurBemelacha, kotlin.isAssurBemelacha)
        assertEquals(java.isBeHaB, kotlin.isBeHaB)
        assertEquals(java.isChanukah, kotlin.isChanukah)
        assertEquals(java.isCheshvanLong, kotlin.isCheshvanLong)
        assertEquals(java.isCholHamoed, kotlin.isCholHamoed)
        assertEquals(java.isCholHamoedPesach, kotlin.isCholHamoedPesach)
        assertEquals(java.isCholHamoedSuccos, kotlin.isCholHamoedSuccos)
        assertEquals(java.isErevRoshChodesh, kotlin.isErevRoshChodesh)
        assertEquals(java.isErevYomTov, kotlin.isErevYomTov)
        assertEquals(java.isErevYomTovSheni, kotlin.isErevYomTovSheni)
        assertEquals(java.isHoshanaRabba, kotlin.isHoshanaRabba)
        assertEquals(java.isIsruChag, kotlin.isIsruChag)
        assertEquals(java.isJewishLeapYear, kotlin.isJewishLeapYear)
        assertEquals(java.isKislevShort, kotlin.isKislevShort)
        assertEquals(java.isMacharChodesh, kotlin.isMacharChodesh)
        assertEquals(java.isMukafChoma, kotlin.isMukafChoma)
        assertEquals(java.isPesach, kotlin.isPesach)
        assertEquals(java.isPurim, kotlin.isPurim)
        assertEquals(java.isRoshChodesh, kotlin.isRoshChodesh)
        assertEquals(java.isRoshHashana, kotlin.isRoshHashana)
        assertEquals(java.isShabbosMevorchim, kotlin.isShabbosMevorchim)
        assertEquals(java.isShavuos, kotlin.isShavuos)
        assertEquals(java.isShminiAtzeres, kotlin.isShminiAtzeres)
        assertEquals(java.isSimchasTorah, kotlin.isSimchasTorah)
        assertEquals(java.isSuccos, kotlin.isSuccos)
        assertEquals(java.isTaanis, kotlin.isTaanis)
        assertEquals(java.isTaanisBechoros, kotlin.isTaanisBechoros)
        assertEquals(java.isTishaBav, kotlin.isTishaBav)
        assertEquals(java.isTomorrowShabbosOrYomTov, kotlin.isTomorrowShabbosOrYomTov)

        assertEquals(java.isUseModernHolidays, kotlin.isUseModernHolidays)

        assertEquals(java.isYomKippur, kotlin.isYomKippur)
        assertEquals(java.isYomKippurKatan, kotlin.isYomKippurKatan)
        assertEquals(java.isYomTov, kotlin.isYomTov)
        assertEquals(java.isYomTovAssurBemelacha, kotlin.isYomTovAssurBemelacha)
        assertEquals(java.jewishDayOfMonth, kotlin.jewishDayOfMonth)
        assertEquals(java.jewishMonth, kotlin.jewishMonth.value)
        assertEquals(java.jewishYear, kotlin.jewishYear.toInt())
        assertEquals(java.moladChalakim, kotlin.moladChalakim)
        assertEquals(java.moladHours, kotlin.moladHours)
        assertEquals(java.moladMinutes, kotlin.moladMinutes)
        assertEquals(java.specialShabbos.name, kotlin.specialShabbos.name)
        assertEquals(java.yomTovIndex, kotlin.yomTovIndex)

        //failing tests:

        //        assertEquals(runCatching { java.dafYomiYerushalmi.let { Daf(it.masechtaNumber, it.daf) } }.getOrNull(),runCatching { kotlin.dafYomiYerushalmi }.getOrNull(),) //fails for 11 iyar, 5744
        //assertEquals(java.daysSinceStartOfJewishYear,//kotlin.daysSinceStartOfJewishYear,)
//        assertEquals(java.isBirkasHachamah,kotlin.isBirkasHachamah,)
        //assertEquals(molad,//runCatching { kotlin.molad.gregorianLocalDate.atStartOfDayIn(kotlinLocation.timeZone).toLocalDateTime(kotlinLocation.timeZone).date }.getOrNull(),)
        //assertEquals(moladAsDate,//runCatching { kotlin.moladAsInstant.toLocalDateTime(kotlinLocation.timeZone) }.getOrNull(),)
        //assertEquals(java.parshah.name,//kotlin.parshah.name,)
        //assertEquals(if(molad == null) null else java.sofZmanKidushLevana15Days.toInstant().toKotlinInstant().toLocalDateTime(kotlinLocation.timeZone),//runCatching { kotlin.sofZmanKidushLevana15Days.toLocalDateTime(kotlinLocation.timeZone) }.getOrNull(),)
        //assertEquals(if(molad == null) null else java.sofZmanKidushLevanaBetweenMoldos.toInstant().toKotlinInstant().toLocalDateTime(kotlinLocation.timeZone),//runCatching { kotlin.sofZmanKidushLevanaBetweenMoldos.toLocalDateTime(kotlinLocation.timeZone) }.getOrNull(),)
        //assertEquals(if(molad == null) null else java.tchilasZmanKidushLevana3Days.toInstant().toKotlinInstant().toLocalDateTime(kotlinLocation.timeZone),//runCatching { kotlin.tchilasZmanKidushLevana3Days.toLocalDateTime(kotlinLocation.timeZone) }.getOrNull(),)
        //assertEquals(if(molad == null) null else java.tchilasZmanKidushLevana7Days.toInstant().toKotlinInstant().toLocalDateTime(kotlinLocation.timeZone),//runCatching { kotlin.tchilasZmanKidushLevana7Days.toLocalDateTime(kotlinLocation.timeZone) }.getOrNull(),)
        //assertEquals(java.tekufasTishreiElapsedDays,//kotlin.tekufasTishreiElapsedDays,)
        //assertEquals(java.upcomingParshah.name,//kotlin.upcomingParshah.name,)
    }

    private fun testComplexZmanimCalendar(
        kotlinLocation: GeoLocation,
        javaLocation: com.kosherjava.zmanim.util.GeoLocation,
        javaDate: java.time.LocalDate,
        kotlinDate: kotlinx.datetime.LocalDate
    ) {
        val javaCalendar = Calendar.getInstance(javaLocation.timeZone).apply {
            set(Calendar.YEAR, javaDate.year)
            set(Calendar.MONTH, javaDate.monthValue - 1)
            set(Calendar.DAY_OF_MONTH, javaDate.dayOfMonth)
        }
        /*println("java date:         $javaDate")
        println("java calend.inst:  ${javaCalendar.toInstant()}")

        println("cal to date:       ${
            java.time.LocalDate.of(
                javaCalendar.get(Calendar.YEAR),
                javaCalendar.get(Calendar.MONTH) + 1,
                javaCalendar.get(Calendar.DAY_OF_MONTH),
            )
        }"
        )
        println("kotlin date:       $kotlinDate")
        println("java calendar:     $javaCalendar")*/
        val calc = ComplexZmanimCalendar(kotlinLocation)
        val javaCalc = com.kosherjava.zmanim.ComplexZmanimCalendar(javaLocation)
        calc.localDateTime = LocalDateTime(kotlinDate, LocalTime(0, 0, 0))
        javaCalc.calendar = javaCalendar
        /**
         * Only compare time. Allow second to be off by 1. Don't check millis.
         * */
        fun assertEquals(date: Date?, instant: Zman.DateBased?) {
            val (javaHr, javaMin, javaSec) = date?.toInstant()?.toKotlinInstant()
                ?.toString()?.substringAfter('T')?.substringBefore('.')?.removeSuffix("Z")
                ?.split(":") ?: listOf("0", "0", "0")
            val (kotlinHour, kotlinMin, kotlinSec) =
                instant?.momentOfOccurrence?.toString()?.substringAfter('T')?.substringBefore('.')
                    ?.removeSuffix("Z")
                    ?.split(":") ?: listOf("0", "0", "0")
            try {
                assert(
                    java.time.LocalTime
                        .of(javaHr.toInt(), javaMin.toInt(), javaSec.toInt())
                        .until(
                            java.time.LocalTime
                                .of(kotlinHour.toInt(), kotlinMin.toInt(), kotlinSec.toInt()),
                            ChronoUnit.SECONDS
                        ) <= 1
                )
            } catch (t: Throwable) {
                println("Failed on ${instant?.definition}: expected $javaHr:$javaMin:$javaSec, but got $kotlinHour:$kotlinMin:$kotlinSec on input $date, ${instant?.momentOfOccurrence}")
                throw t
            }

        }

        assertEquals(javaCalc.solarMidnight, calc.solarMidnight)
        assertEquals(
            javaCalc.getUTCSunrise(io.github.kdroidfilter.kosherkotlin.AstronomicalCalendar.GEOMETRIC_ZENITH),
            calc.getUTCSunrise(io.github.kdroidfilter.kosherkotlin.AstronomicalCalendar.GEOMETRIC_ZENITH),
            0.0
        )
        assertEquals(
            javaCalc.sunrise,
            Zman.DateBased(
                ZmanDefinition(
                    ZmanType.HANAITZ,
                    ZmanCalculationMethod.Unspecified
                )
            ) { calc.sunrise })
        assertEquals(javaCalc.alos60, calc.alos60)
        assertEquals(javaCalc.tzais60, calc.tzais60)
        assertEquals(javaCalc.alos90Zmanis, calc.alos90Zmanis)
        assertEquals(javaCalc.tzais90Zmanis, calc.tzais90Zmanis)
        assertEquals(
            com.kosherjava.zmanim.hebrewcalendar.JewishCalendar().moladAsDate.time,
            io.github.kdroidfilter.kosherkotlin.hebrewcalendar.JewishCalendar().moladAsInstant.toDate()!!.time
        )
        assertEquals(
            com.kosherjava.zmanim.hebrewcalendar.JewishCalendar().sofZmanKidushLevana15Days.time,
            io.github.kdroidfilter.kosherkotlin.hebrewcalendar.JewishCalendar().sofZmanKidushLevana15Days.toDate()!!.time
        )
        assertEquals(
            javaCalc.sofZmanKidushLevanaBetweenMoldos,
            calc.sofZmanKidushLevanaBetweenMoldos,
        )
        assertEquals(
            javaCalc.sofZmanKidushLevana15Days,
            calc.sofZmanKidushLevana15Days
        )
        assertEquals(
            javaCalc.getTemporalHour(javaCalc.alos90Zmanis, javaCalc.tzais90Zmanis),
            calc.getTemporalHour(
                calc.alos90Zmanis.momentOfOccurrence,
                calc.tzais90Zmanis.momentOfOccurrence
            )
        )
        calc.apply {
            val values = listOf(
                Triple(
                    javaCalc.shaahZmanis19Point8Degrees,
                    shaahZmanis19Point8Degrees,
                    "shaahZmanis19Point8Degrees"
                ),
                Triple(javaCalc.shaahZmanis18Degrees, shaahZmanis18Degrees, "shaahZmanis18Degrees"),
                Triple(javaCalc.shaahZmanis26Degrees, shaahZmanis26Degrees, "shaahZmanis26Degrees"),
                Triple(
                    javaCalc.shaahZmanis16Point1Degrees,
                    shaahZmanis16Point1Degrees,
                    "shaahZmanis16Point1Degrees"
                ),
                Triple(javaCalc.shaahZmanis60Minutes, shaahZmanis60Minutes, "shaahZmanis60Minutes"),
                Triple(
                    javaCalc.shaahZmanis72MinutesZmanis,
                    shaahZmanis72MinutesZmanis,
                    "shaahZmanis72MinutesZmanis"
                ),
                Triple(javaCalc.shaahZmanis90Minutes, shaahZmanis90Minutes, "shaahZmanis90Minutes"),
                Triple(
                    javaCalc.shaahZmanis90MinutesZmanis,
                    shaahZmanis90MinutesZmanis,
                    "shaahZmanis90MinutesZmanis"
                ),
                Triple(
                    javaCalc.shaahZmanis96MinutesZmanis,
                    shaahZmanis96MinutesZmanis,
                    "shaahZmanis96MinutesZmanis"
                ),
                Triple(
                    javaCalc.shaahZmanisAteretTorah,
                    shaahZmanisAteretTorah,
                    "shaahZmanisAteretTorah"
                ),
                Triple(
                    javaCalc.shaahZmanisAlos16Point1ToTzais3Point8,
                    shaahZmanisAlos16Point1ToTzais3Point8,
                    "shaahZmanisAlos16Point1ToTzais3Point8"
                ),
                Triple(
                    javaCalc.shaahZmanisAlos16Point1ToTzais3Point7,
                    shaahZmanisAlos16Point1ToTzais3Point7,
                    "shaahZmanisAlos16Point1ToTzais3Point7"
                ),
                Triple(javaCalc.shaahZmanis96Minutes, shaahZmanis96Minutes, "shaahZmanis96Minutes"),
                Triple(
                    javaCalc.shaahZmanis120Minutes,
                    shaahZmanis120Minutes,
                    "shaahZmanis120Minutes"
                ),
                Triple(
                    javaCalc.shaahZmanis120MinutesZmanis,
                    shaahZmanis120MinutesZmanis,
                    "shaahZmanis120MinutesZmanis"
                ),
            );

            //            println("Ahavat shalom kotlin: $kotlinMGAhavatShalom")
//            println("Ahavat shalom java: $javaMGAhavatShalom")
            /*val listOfZmanim = */listOf(
            Triple(
                javaCalc.plagHamincha120MinutesZmanis,
                plagHamincha120MinutesZmanis,
                "plagHamincha120MinutesZmanis"
            ),
            Triple(
                javaCalc.plagHamincha120Minutes,
                plagHamincha120Minutes,
                "plagHamincha120Minutes"
            ),
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
            Triple(
                javaCalc.misheyakir11Point5Degrees,
                misheyakir11Point5Degrees,
                "misheyakir11Point5Degrees"
            ),
            Triple(javaCalc.misheyakir11Degrees, misheyakir11Degrees, "misheyakir11Degrees"),
            Triple(
                javaCalc.misheyakir10Point2Degrees,
                misheyakir10Point2Degrees,
                "misheyakir10Point2Degrees"
            ),
            Triple(
                javaCalc.misheyakir7Point65Degrees,
                misheyakir7Point65Degrees,
                "misheyakir7Point65Degrees"
            ),
            Triple(
                javaCalc.misheyakir9Point5Degrees,
                misheyakir9Point5Degrees,
                "misheyakir9Point5Degrees"
            ),
            Triple(
                javaCalc.sofZmanShmaMGA19Point8Degrees,
                sofZmanShmaMGA19Point8Degrees,
                "sofZmanShmaMGA19Point8Degrees"
            ),
            Triple(
                javaCalc.sofZmanShmaMGA16Point1Degrees,
                sofZmanShmaMGA16Point1Degrees,
                "sofZmanShmaMGA16Point1Degrees"
            ),
            Triple(
                javaCalc.sofZmanShmaMGA18Degrees,
                sofZmanShmaMGA18Degrees,
                "sofZmanShmaMGA18Degrees"
            ),
            Triple(
                javaCalc.sofZmanShmaMGA72MinutesZmanis,
                sofZmanShmaMGA72MinutesZmanis,
                "sofZmanShmaMGA72MinutesZmanis"
            ),
            Triple(
                javaCalc.sofZmanShmaMGA90Minutes,
                sofZmanShmaMGA90Minutes,
                "sofZmanShmaMGA90Minutes"
            ),
            Triple(
                javaCalc.sofZmanShmaMGA90MinutesZmanis,
                sofZmanShmaMGA90MinutesZmanis,
                "sofZmanShmaMGA90MinutesZmanis"
            ),
            Triple(
                javaCalc.sofZmanShmaMGA96Minutes,
                sofZmanShmaMGA96Minutes,
                "sofZmanShmaMGA96Minutes"
            ),
            Triple(
                javaCalc.sofZmanShmaMGA96MinutesZmanis,
                sofZmanShmaMGA96MinutesZmanis,
                "sofZmanShmaMGA96MinutesZmanis"
            ),
            Triple(
                javaCalc.sofZmanShma3HoursBeforeChatzos,
                sofZmanShma3HoursBeforeChatzos,
                "sofZmanShma3HoursBeforeChatzos"
            ),
            Triple(
                javaCalc.sofZmanShmaMGA120Minutes,
                sofZmanShmaMGA120Minutes,
                "sofZmanShmaMGA120Minutes"
            ),
            Triple(
                javaCalc.sofZmanShmaAlos16Point1ToSunset,
                sofZmanShmaAlos16Point1ToSunset,
                "sofZmanShmaAlos16Point1ToSunset"
            ),
            Triple(
                javaCalc.sofZmanShmaAlos16Point1ToTzaisGeonim7Point083Degrees,
                sofZmanShmaAlos16Point1ToTzaisGeonim7Point083Degrees,
                "sofZmanShmaAlos16Point1ToTzaisGeonim7Point083Degrees"
            ),
            Triple(
                javaCalc.sofZmanTfilaMGA19Point8Degrees,
                sofZmanTfilaMGA19Point8Degrees,
                "sofZmanTfilaMGA19Point8Degrees"
            ),
            Triple(
                javaCalc.sofZmanTfilaMGA16Point1Degrees,
                sofZmanTfilaMGA16Point1Degrees,
                "sofZmanTfilaMGA16Point1Degrees"
            ),
            Triple(
                javaCalc.sofZmanTfilaMGA18Degrees,
                sofZmanTfilaMGA18Degrees,
                "sofZmanTfilaMGA18Degrees"
            ),
            Triple(
                javaCalc.sofZmanTfilaMGA72MinutesZmanis,
                sofZmanTfilaMGA72MinutesZmanis,
                "sofZmanTfilaMGA72MinutesZmanis"
            ),
            Triple(
                javaCalc.sofZmanTfilaMGA90MinutesZmanis,
                sofZmanTfilaMGA90MinutesZmanis,
                "sofZmanTfilaMGA90MinutesZmanis"
            ),
            Triple(
                javaCalc.sofZmanTfilaMGA96MinutesZmanis,
                sofZmanTfilaMGA96MinutesZmanis,
                "sofZmanTfilaMGA96MinutesZmanis"
            ),
            Triple(
                javaCalc.sofZmanTfila2HoursBeforeChatzos,
                sofZmanTfila2HoursBeforeChatzos,
                "sofZmanTfila2HoursBeforeChatzos"
            ),
//            if (kotlinLocation == TestHelper.arcticNunavut) Triple(
//                javaCalc.minchaGedola30Minutes,
//                minchaGedola30Minutes,
//                "minchaGedola30Minutes"
//            ) else null,
//            if (kotlinLocation == TestHelper.arcticNunavut) Triple(
//                javaCalc.minchaGedola16Point1Degrees,
//                minchaGedola16Point1Degrees,
//                "minchaGedola16Point1Degrees"
//            ) else null,
//            if (kotlinLocation == TestHelper.arcticNunavut) Triple(
//                javaCalc.minchaGedolaAhavatShalom,
//                minchaGedolaAhavatShalom,
//                "minchaGedolaAhavatShalom"
//            ) else null,
//            if (kotlinLocation == TestHelper.arcticNunavut) Triple(
//                javaCalc.minchaGedolaGreaterThan30,
//                minchaGedolaGreaterThan30,
//                "minchaGedolaGreaterThan30"
//            ) else null,
//            if (kotlinLocation == TestHelper.arcticNunavut) Triple(
//                javaCalc.minchaKetana16Point1Degrees,
//                minchaKetana16Point1Degrees,
//                "minchaKetana16Point1Degrees"
//            ) else null,
//            if (kotlinLocation == TestHelper.arcticNunavut) Triple(
//                javaCalc.minchaKetanaAhavatShalom,
//                minchaKetanaAhavatShalom,
//                "minchaKetanaAhavatShalom"
//            ) else null,
//            if (kotlinLocation == TestHelper.arcticNunavut) Triple(
//                javaCalc.minchaKetana72Minutes,
//                minchaKetana72Minutes,
//                "minchaKetana72Minutes"
//            ) else null,
//            Triple(
//                javaCalc.plagHamincha60Minutes,
//                plagHamincha60Minutes,
//                "plagHamincha60Minutes"
//            ),
            Triple(
                javaCalc.plagHamincha72Minutes,
                plagHamincha72Minutes,
                "plagHamincha72Minutes"
            ),
            Triple(
                javaCalc.plagHamincha90Minutes,
                plagHamincha90Minutes,
                "plagHamincha90Minutes"
            ),
            Triple(
                javaCalc.plagHamincha96Minutes,
                plagHamincha96Minutes,
                "plagHamincha96Minutes"
            ),
            Triple(
                javaCalc.plagHamincha96MinutesZmanis,
                plagHamincha96MinutesZmanis,
                "plagHamincha96MinutesZmanis"
            ),
            Triple(
                javaCalc.plagHamincha90MinutesZmanis,
                plagHamincha90MinutesZmanis,
                "plagHamincha90MinutesZmanis"
            ),
            Triple(
                javaCalc.plagHamincha72MinutesZmanis,
                plagHamincha72MinutesZmanis,
                "plagHamincha72MinutesZmanis"
            ),
            Triple(
                javaCalc.plagHamincha16Point1Degrees,
                plagHamincha16Point1Degrees,
                "plagHamincha16Point1Degrees"
            ),
            Triple(
                javaCalc.plagHamincha19Point8Degrees,
                plagHamincha19Point8Degrees,
                "plagHamincha19Point8Degrees"
            ),
            Triple(
                javaCalc.plagHamincha26Degrees,
                plagHamincha26Degrees,
                "plagHamincha26Degrees"
            ),
            Triple(
                javaCalc.plagHamincha18Degrees,
                plagHamincha18Degrees,
                "plagHamincha18Degrees"
            ),
            Triple(javaCalc.plagAlosToSunset, plagAlosToSunset, "plagAlosToSunset"),
            Triple(
                javaCalc.plagAlos16Point1ToTzaisGeonim7Point083Degrees,
                plagAlos16Point1ToTzaisGeonim7Point083Degrees,
                "plagAlos16Point1ToTzaisGeonim7Point083Degrees"
            ),
            Triple(javaCalc.plagAhavatShalom, plagAhavatShalom, "plagAhavatShalom"),
            Triple(
                javaCalc.bainHashmashosRT13Point24Degrees,
                bainHashmashosRT13Point24Degrees,
                "bainHashmashosRT13Point24Degrees"
            ),
            Triple(
                javaCalc.bainHashmashosRT58Point5Minutes,
                bainHashmashosRT58Point5Minutes,
                "bainHashmashosRT58Point5Minutes"
            ),
            Triple(
                javaCalc.bainHashmashosRT13Point5MinutesBefore7Point083Degrees,
                bainHashmashosRT13Point5MinutesBefore7Point083Degrees,
                "bainHashmashosRT13Point5MinutesBefore7Point083Degrees"
            ),
            Triple(
                javaCalc.bainHashmashosRT2Stars,
                bainHashmashosRT2Stars,
                "bainHashmashosRT2Stars"
            ),
            Triple(
                javaCalc.bainHashmashosYereim18Minutes,
                bainHashmashosYereim18Minutes,
                "bainHashmashosYereim18Minutes"
            ),
            Triple(
                javaCalc.bainHashmashosYereim3Point05Degrees,
                bainHashmashosYereim3Point05Degrees,
                "bainHashmashosYereim3Point05Degrees"
            ),
            Triple(
                javaCalc.bainHashmashosYereim16Point875Minutes,
                bainHashmashosYereim16Point875Minutes,
                "bainHashmashosYereim16Point875Minutes"
            ),
            Triple(
                javaCalc.bainHashmashosYereim2Point8Degrees,
                bainHashmashosYereim2Point8Degrees,
                "bainHashmashosYereim2Point8Degrees"
            ),
            Triple(
                javaCalc.bainHashmashosYereim13Point5Minutes,
                bainHashmashosYereim13Point5Minutes,
                "bainHashmashosYereim13Point5Minutes"
            ),
            Triple(
                javaCalc.bainHashmashosYereim2Point1Degrees,
                bainHashmashosYereim2Point1Degrees,
                "bainHashmashosYereim2Point1Degrees"
            ),
            Triple(
                javaCalc.tzaisGeonim3Point7Degrees,
                tzaisGeonim3Point7Degrees,
                "tzaisGeonim3Point7Degrees"
            ),
            Triple(
                javaCalc.tzaisGeonim3Point8Degrees,
                tzaisGeonim3Point8Degrees,
                "tzaisGeonim3Point8Degrees"
            ),
            Triple(
                javaCalc.tzaisGeonim5Point95Degrees,
                tzaisGeonim5Point95Degrees,
                "tzaisGeonim5Point95Degrees"
            ),
            Triple(
                javaCalc.tzaisGeonim3Point65Degrees,
                tzaisGeonim3Point65Degrees,
                "tzaisGeonim3Point65Degrees"
            ),
            Triple(
                javaCalc.tzaisGeonim3Point676Degrees,
                tzaisGeonim3Point676Degrees,
                "tzaisGeonim3Point676Degrees"
            ),
            Triple(
                javaCalc.tzaisGeonim4Point61Degrees,
                tzaisGeonim4Point61Degrees,
                "tzaisGeonim4Point61Degrees"
            ),
            Triple(
                javaCalc.tzaisGeonim4Point37Degrees,
                tzaisGeonim4Point37Degrees,
                "tzaisGeonim4Point37Degrees"
            ),
            Triple(
                javaCalc.tzaisGeonim5Point88Degrees,
                tzaisGeonim5Point88Degrees,
                "tzaisGeonim5Point88Degrees"
            ),
            Triple(
                javaCalc.tzaisGeonim4Point8Degrees,
                tzaisGeonim4Point8Degrees,
                "tzaisGeonim4Point8Degrees"
            ),
            Triple(
                javaCalc.tzaisGeonim6Point45Degrees,
                tzaisGeonim6Point45Degrees,
                "tzaisGeonim6Point45Degrees"
            ),
            Triple(
                javaCalc.tzaisGeonim7Point083Degrees,
                tzaisGeonim7Point083Degrees,
                "tzaisGeonim7Point083Degrees"
            ),
            Triple(
                javaCalc.tzaisGeonim7Point67Degrees,
                tzaisGeonim7Point67Degrees,
                "tzaisGeonim7Point67Degrees"
            ),
            Triple(
                javaCalc.tzaisGeonim8Point5Degrees,
                tzaisGeonim8Point5Degrees,
                "tzaisGeonim8Point5Degrees"
            ),
            Triple(
                javaCalc.tzaisGeonim9Point3Degrees,
                tzaisGeonim9Point3Degrees,
                "tzaisGeonim9Point3Degrees"
            ),
            Triple(
                javaCalc.tzaisGeonim9Point75Degrees,
                tzaisGeonim9Point75Degrees,
                "tzaisGeonim9Point75Degrees"
            ),
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
            Triple(
                javaCalc.sofZmanKidushLevanaBetweenMoldos,
                sofZmanKidushLevanaBetweenMoldos,
                "sofZmanKidushLevanaBetweenMoldos"
            ),
            Triple(
                javaCalc.sofZmanKidushLevana15Days,
                sofZmanKidushLevana15Days,
                "sofZmanKidushLevana15Days"
            ),
            Triple(javaCalc.zmanMolad, zmanMolad, "zmanMolad"),
            nullIfKotlinNull(
                javaCalc.sofZmanBiurChametzGRA,
                sofZmanBiurChametzGRA,
                "sofZmanBiurChametzGRA"
            ),
            nullIfKotlinNull(
                javaCalc.sofZmanBiurChametzMGA72Minutes,
                sofZmanBiurChametzMGA72Minutes,
                "sofZmanBiurChametzMGA72Minutes"
            ),
            nullIfKotlinNull(
                javaCalc.sofZmanBiurChametzMGA16Point1Degrees,
                sofZmanBiurChametzMGA16Point1Degrees,
                "sofZmanBiurChametzMGA16Point1Degrees"
            ),
            Triple(javaCalc.solarMidnight, solarMidnight, "solarMidnight"),
            Triple(
                javaCalc.sofZmanShmaBaalHatanya,
                sofZmanShmaBaalHatanya,
                "sofZmanShmaBaalHatanya"
            ),
            Triple(
                javaCalc.sofZmanTfilaBaalHatanya,
                sofZmanTfilaBaalHatanya,
                "sofZmanTfilaBaalHatanya"
            ),
            nullIfKotlinNull(
                javaCalc.sofZmanBiurChametzBaalHatanya,
                sofZmanBiurChametzBaalHatanya,
                "sofZmanBiurChametzBaalHatanya"
            ),
            Triple(
                javaCalc.minchaGedolaBaalHatanya,
                minchaGedolaBaalHatanya,
                "minchaGedolaBaalHatanya"
            ),
            Triple(
                javaCalc.minchaGedolaBaalHatanyaGreaterThan30,
                minchaGedolaBaalHatanyaGreaterThan30,
                "minchaGedolaBaalHatanyaGreaterThan30"
            ),
            Triple(
                javaCalc.minchaKetanaBaalHatanya,
                minchaKetanaBaalHatanya,
                "minchaKetanaBaalHatanya"
            ),
            Triple(
                javaCalc.plagHaminchaBaalHatanya,
                plagHaminchaBaalHatanya,
                "plagHaminchaBaalHatanya"
            ),
            Triple(javaCalc.tzaisBaalHatanya, tzaisBaalHatanya, "tzaisBaalHatanya"),
            Triple(
                javaCalc.sofZmanShmaMGA18DegreesToFixedLocalChatzos,
                sofZmanShmaMGA18DegreesToFixedLocalChatzos,
                "sofZmanShmaMGA18DegreesToFixedLocalChatzos"
            ),
            Triple(
                javaCalc.sofZmanShmaMGA16Point1DegreesToFixedLocalChatzos,
                sofZmanShmaMGA16Point1DegreesToFixedLocalChatzos,
                "sofZmanShmaMGA16Point1DegreesToFixedLocalChatzos"
            ),
            Triple(
                javaCalc.sofZmanShmaMGA90MinutesToFixedLocalChatzos,
                sofZmanShmaMGA90MinutesToFixedLocalChatzos,
                "sofZmanShmaMGA90MinutesToFixedLocalChatzos"
            ),
            Triple(
                javaCalc.sofZmanShmaMGA72MinutesToFixedLocalChatzos,
                sofZmanShmaMGA72MinutesToFixedLocalChatzos,
                "sofZmanShmaMGA72MinutesToFixedLocalChatzos"
            ),
            Triple(
                javaCalc.sofZmanShmaGRASunriseToFixedLocalChatzos,
                sofZmanShmaGRASunriseToFixedLocalChatzos,
                "sofZmanShmaGRASunriseToFixedLocalChatzos"
            ),
            Triple(
                javaCalc.sofZmanTfilaGRASunriseToFixedLocalChatzos,
                sofZmanTfilaGRASunriseToFixedLocalChatzos,
                "sofZmanTfilaGRASunriseToFixedLocalChatzos"
            ),
            Triple(
                javaCalc.minchaGedolaGRAFixedLocalChatzos30Minutes,
                minchaGedolaGRAFixedLocalChatzos30Minutes,
                "minchaGedolaGRAFixedLocalChatzos30Minutes"
            ),
            Triple(
                javaCalc.minchaKetanaGRAFixedLocalChatzosToSunset,
                minchaKetanaGRAFixedLocalChatzosToSunset,
                "minchaKetanaGRAFixedLocalChatzosToSunset"
            ),
            Triple(
                javaCalc.plagHaminchaGRAFixedLocalChatzosToSunset,
                plagHaminchaGRAFixedLocalChatzosToSunset,
                "plagHaminchaGRAFixedLocalChatzosToSunset"
            ),
            Triple(javaCalc.tzais50, tzais50, "tzais50"),
            Triple(
                javaCalc.samuchLeMinchaKetanaGRA,
                samuchLeMinchaKetanaGRA,
                "samuchLeMinchaKetanaGRA"
            ),
            Triple(
                javaCalc.samuchLeMinchaKetana16Point1Degrees,
                samuchLeMinchaKetana16Point1Degrees,
                "samuchLeMinchaKetana16Point1Degrees"
            )
        )
            .filterNotNull()
            .forEach {
                assertEquals(
                    it.first,
                    it.second,
                )
            }
            testValues(values, transformActual = { it.duration.inWholeMilliseconds })
//            testValues(listOfZmanim, 1.0)
        }
    }

    private fun getAllValues(java: com.kosherjava.zmanim.hebrewcalendar.JewishCalendar): Array<Any?> {
        val moladAsKotlinLocalDate =
            java.molad.localDate.atStartOfDay(javaLocation.timeZone.toZoneId()).toLocalDate()
                .toKotlinLocalDate()
        val molad = if (moladAsKotlinLocalDate < HebrewLocalDate.STARTING_DATE_GREGORIAN
        ) null else moladAsKotlinLocalDate
        val moladAsKotlinLocalDateTime = java.moladAsDate.toInstant().toKotlinInstant()
            .toLocalDateTime(kotlinLocation.timeZone)
        val moladAsDate =
            if (moladAsKotlinLocalDateTime.date < HebrewLocalDate.STARTING_DATE_GREGORIAN
            ) null else moladAsKotlinLocalDateTime
        return arrayOf()
    }

    private fun nullIfKotlinNull(
        java: Date?,
        kotlin: Zman.DateBased,
        label: String
    ): Triple<Date?, Zman.DateBased, String> {
        return Triple(
            if (kotlin.momentOfOccurrence == null) null
            else java,
            kotlin,
            label,
        )
    }

    @Test
    fun testAstronomicalCalendar() {
        val kotlinAstroCal = io.github.kdroidfilter.kosherkotlin.AstronomicalCalendar(kotlinLocation).apply {
            localDateTime = LocalDateTime(localDateTime.date, LocalTime(1, 0, 0))
        }
        val javaAstroCal = com.kosherjava.zmanim.AstronomicalCalendar(
            javaLocation
        ).apply {
            val cal = Calendar.getInstance()
            cal.set(Calendar.YEAR, kotlinAstroCal.localDateTime.year)
            cal.set(Calendar.MONTH, kotlinAstroCal.localDateTime.monthNumber - 1)
            cal.set(Calendar.DATE, kotlinAstroCal.localDateTime.dayOfMonth)


            cal.set(Calendar.HOUR, 1)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            cal.timeZone = javaLocation.timeZone
            calendar = cal
        }
        javaAstroCal.apply {
            assertEquals(sunrise, kotlinAstroCal.sunrise?.toDate())
            assertEquals(seaLevelSunrise, kotlinAstroCal.seaLevelSunrise?.toDate())
            assertEquals(beginCivilTwilight, kotlinAstroCal.beginCivilTwilight?.toDate())
            assertEquals(beginNauticalTwilight, kotlinAstroCal.beginNauticalTwilight?.toDate())
            assertEquals(
                beginAstronomicalTwilight,
                kotlinAstroCal.beginAstronomicalTwilight?.toDate()
            )
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
                calendar.toInstant().toKotlinInstant()
                    .toLocalDateTime(kotlinAstroCal.geoLocation.timeZone).date,
                kotlinAstroCal.localDateTime.toInstant(kotlinAstroCal.geoLocation.timeZone)
                    .toLocalDateTime(kotlinAstroCal.geoLocation.timeZone).date
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
        jewishCalendar: io.github.kdroidfilter.kosherkotlin.hebrewcalendar.JewishCalendar,
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

    private fun getParshaOrYomTov(jewishCalendar: io.github.kdroidfilter.kosherkotlin.hebrewcalendar.JewishCalendar) =
        hdf
            .transliteratedParshiosList[jewishCalendar.parshah]
            ?.let {
                val specialShabbos = jewishCalendar.specialShabbos
                if (specialShabbos != io.github.kdroidfilter.kosherkotlin.hebrewcalendar.JewishCalendar.Parsha.NONE) "$it, ${hdf.transliteratedParshiosList[specialShabbos]}" else it
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