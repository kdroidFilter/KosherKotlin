package com.kosherjava.zmanim.hebrewcalendar

import com.kosherjava.zmanim.hebrewcalendar.HebrewLocalDate.Companion.toHebrewDate
import com.kosherjava.zmanim.hebrewcalendar.JewishDate.Companion.daysInJewishYear
import com.kosherjava.zmanim.util.DateUtils.now
import kotlinx.datetime.*
import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.File
import java.time.Instant
import java.util.*

class ConvertBetweenGregorianAndHebrewTest {

    // Simple cases, after starting date

    @Test
    fun `gregorian to hebrew - one day after start date`() {
        val date = HebrewLocalDate.STARTING_DATE_GREGORIAN.plus(DatePeriod(days = 1))
        val hebrew = HebrewLocalDate.STARTING_DATE_HEBREW
        assertEquals(HebrewLocalDate(hebrew.year, hebrew.month, hebrew.dayOfMonth + 1), date.toHebrewDate())
    }

    @Test
    fun `gregorian to hebrew - one month after start date`() {
        val date = HebrewLocalDate.STARTING_DATE_GREGORIAN.plus(DatePeriod(days = 30))
        val hebrew = HebrewLocalDate.STARTING_DATE_HEBREW
        assertEquals(HebrewLocalDate(hebrew.year, hebrew.month.nextMonth, hebrew.dayOfMonth), date.toHebrewDate())
    }

    @Test
    fun `gregorian to hebrew - one year after start date`() {
        val hebrew = HebrewLocalDate.STARTING_DATE_HEBREW
        val date = HebrewLocalDate.STARTING_DATE_GREGORIAN.plus(DatePeriod(days = hebrew.year.daysInJewishYear))
        assertEquals(HebrewLocalDate(hebrew.year + 1, hebrew.month, hebrew.dayOfMonth), date.toHebrewDate())
    }

    @Test
    fun `gregorian to hebrew - less than month after start date`() {
        val date = HebrewLocalDate.STARTING_DATE_GREGORIAN.plus(DatePeriod(days = 3))
        val hebrew = HebrewLocalDate.STARTING_DATE_HEBREW
        assertEquals(HebrewLocalDate(hebrew.year, hebrew.month, hebrew.dayOfMonth + 3), date.toHebrewDate())
    }

    @Test
    fun `gregorian to hebrew - more than month after start date`() {
        val date = HebrewLocalDate.STARTING_DATE_GREGORIAN.plus(DatePeriod(days = 32))
        val hebrew = HebrewLocalDate.STARTING_DATE_HEBREW
        assertEquals(HebrewLocalDate(hebrew.year, hebrew.month.nextMonth, 3), date.toHebrewDate())
    }

    @Test
    fun `gregorian to hebrew - more than year after start date`() {
        val hebrew = HebrewLocalDate.STARTING_DATE_HEBREW
        val date = HebrewLocalDate.STARTING_DATE_GREGORIAN.plus(DatePeriod(days = hebrew.year.daysInJewishYear + 1))
        println("Num days in year")
        assertEquals(HebrewLocalDate(hebrew.year + 1, hebrew.month, hebrew.dayOfMonth + 1), date.toHebrewDate())
    }

    //two-way conversion


    @Test
    fun `hebrew to gregorian - one day after start date`() {
        val startingDateGregorian = HebrewLocalDate.STARTING_DATE_GREGORIAN
        println("Starting gregorian date: $startingDateGregorian")
        val date = startingDateGregorian.plus(DatePeriod(days = 1))
        val hebrew = HebrewLocalDate.STARTING_DATE_HEBREW
        assertEquals(date, HebrewLocalDate(hebrew.year, hebrew.month, hebrew.dayOfMonth + 1).toLocalDateGregorian())
    }

    @Test
    fun `hebrew to gregorian - one month after start date`() {
        val date = HebrewLocalDate.STARTING_DATE_GREGORIAN.plus(DatePeriod(days = 30))
        val hebrew = HebrewLocalDate.STARTING_DATE_HEBREW
        assertEquals(
            date,
            HebrewLocalDate(hebrew.year, hebrew.month.nextMonth, hebrew.dayOfMonth).toLocalDateGregorian()
        )
    }

    @Test
    fun `hebrew to gregorian - one year after start date`() {
        val hebrew = HebrewLocalDate.STARTING_DATE_HEBREW
        val date = HebrewLocalDate.STARTING_DATE_GREGORIAN + DatePeriod(days = hebrew.year.daysInJewishYear)
        assertEquals(date, HebrewLocalDate(hebrew.year + 1, hebrew.month, hebrew.dayOfMonth).toLocalDateGregorian())
    }

    @Test
    fun `hebrew to gregorian - less than one month after start date`() {
        val date = HebrewLocalDate.STARTING_DATE_GREGORIAN.plus(DatePeriod(days = 3))
        val hebrew = HebrewLocalDate.STARTING_DATE_HEBREW
        assertEquals(date, HebrewLocalDate(hebrew.year, hebrew.month, hebrew.dayOfMonth + 3).toLocalDateGregorian())
    }

    @Test
    fun `hebrew to gregorian - more than one month after start date`() {
        val date = HebrewLocalDate.STARTING_DATE_GREGORIAN.plus(DatePeriod(days = 32))
        val hebrew = HebrewLocalDate.STARTING_DATE_HEBREW
        assertEquals(date, HebrewLocalDate(hebrew.year, hebrew.month.nextMonth, 3).toLocalDateGregorian())
    }

    @Test
    fun `hebrew to gregorian - more than one year after start date`() {
        val hebrew = HebrewLocalDate.STARTING_DATE_HEBREW
        val date = HebrewLocalDate.STARTING_DATE_GREGORIAN.plus(DatePeriod(days = hebrew.year.daysInJewishYear + 1))
        println("Num days in year")
        assertEquals(date, HebrewLocalDate(hebrew.year + 1, hebrew.month, hebrew.dayOfMonth + 1).toLocalDateGregorian())
    }

    @Test
    fun `hebrew to gregorian - min and max date`() {
        assertEquals(
            HebrewLocalDate.STARTING_DATE_GREGORIAN,
            HebrewLocalDate.STARTING_DATE_HEBREW.toLocalDateGregorian()
        )
        val distantFuture = LocalDate(100_000, 1, 1)
        val distantFutureHebrew = HebrewLocalDate(103759, HebrewMonth.CHESHVAN, 22)
        assertEquals(distantFuture, distantFutureHebrew.toLocalDateGregorian())
    }

    @Test
    fun `gregorian to hebrew - min and max date`() {
        assertEquals(HebrewLocalDate.STARTING_DATE_HEBREW, HebrewLocalDate.STARTING_DATE_GREGORIAN.toHebrewDate())
        val distantFutureHebrew = HebrewLocalDate(103_759, HebrewMonth.CHESHVAN, 22)
        val distantFuture = LocalDate(100_000, 1, 1)
        assertEquals(distantFutureHebrew, distantFuture.toHebrewDate())
    }

    @Test
    fun yearZero() {
        val target = LocalDate(0, 1, 1)
        val hebrew = HebrewLocalDate(3761, HebrewMonth.TEVES, 18)
        assertEquals(hebrew, target.toHebrewDate())
        assertEquals(target, hebrew.toLocalDateGregorian())
    }

    @Test
    fun twoWayConversion() {
        val now = LocalDate.now() //probably bad practice to use a value which can change between tests
        assertEquals(now, now.toHebrewDate().toLocalDateGregorian())
    }

    @Test
    fun regressionTest() {
        val success = LinkedList<String>()
        val DISTANT_FUTURE = Instant.ofEpochSecond(3093527980800L);
        val distantFutureDate = Date.from(DISTANT_FUTURE);
        val distantFutureLocalDate = DISTANT_FUTURE
        val kotlinDistantFutureJewishDate = JewishDate(
            kotlinx.datetime.LocalDate(
                distantFutureDate.year,
                distantFutureDate.month + 1,
                distantFutureDate.date
            )
        )
        val distantFutureJewishDate = com.kosherjava.zmanim.java.zmanim.hebrewcalendar.JewishDate(distantFutureDate);
        var kotlinCurrentJewishDate = HebrewLocalDate(3761, HebrewMonth.TEVES, 18)
        val javaCurrentJewishDate = com.kosherjava.zmanim.java.zmanim.hebrewcalendar.JewishDate(
            3761,
            HebrewMonth.TEVES.value,
            18
        ); //start of hillel hazaken's calender

        while (
            javaCurrentJewishDate.getJewishYear() != distantFutureJewishDate.getJewishYear() ||
            javaCurrentJewishDate.getJewishMonth() != distantFutureJewishDate.getJewishMonth() ||
            javaCurrentJewishDate.getJewishDayOfMonth() != distantFutureJewishDate.getJewishDayOfMonth()
        ) {
            try {
                val kotlinGregorian = kotlinCurrentJewishDate.toLocalDateGregorian()
                if (
                    javaCurrentJewishDate.getJewishYear() == kotlinCurrentJewishDate.year.toInt() &&
                    javaCurrentJewishDate.getJewishMonth() == kotlinCurrentJewishDate.month.value &&
                    javaCurrentJewishDate.getJewishDayOfMonth() == kotlinCurrentJewishDate.dayOfMonth
                ) {
                    success.add("${javaCurrentJewishDate.jewishYear}-${javaCurrentJewishDate.jewishMonth}-${javaCurrentJewishDate.jewishDayOfMonth}")
                } else println("Failure: $javaCurrentJewishDate")
                if (
                    (javaCurrentJewishDate.getGregorianYear() == kotlinGregorian.year) &&
                    ((javaCurrentJewishDate.getGregorianMonth() + 1) == kotlinGregorian.month.value) &&
                    (javaCurrentJewishDate.getGregorianDayOfMonth() == kotlinGregorian.dayOfMonth)
                ){} else println("Failure gregorian: $javaCurrentJewishDate")
                javaCurrentJewishDate.forward(Calendar.DATE, 1);
                val prev = kotlinCurrentJewishDate.copy()
                kotlinCurrentJewishDate = kotlinCurrentJewishDate.plusDays(1)
                if(prev.year != kotlinCurrentJewishDate.year) println("New year: ${kotlinCurrentJewishDate.year}")
                /*val toHebrewDate = runCatching {
                    localDate.toHebrewDate()
                }.getOrNull()
                if (
                    hebrewLocalDate == toHebrewDate
                ) {
                    success.add(line)
                    counter++
                } else if (counter > 1) println("Failure: $javaCurrentJewishDate")*/
            } catch (t: Throwable) {
                println("Throwable at $javaCurrentJewishDate: ${t.stackTraceToString()}")
//            System.out.println("Current: " + current);
            }
        }
        println("Success: (${success.size}): \n${success.joinToString("\n")}")
        /*File("output.csv").useLines {
            var counter = 0
            for (line in it) {
                val (hebrew, gregorian) = line.split(",")
                val (hebrewYear, hebrewMonth, hebrewDayOfMonth) = hebrew.split("-")
                val hebrewLocalDate =
                    HebrewLocalDate(
                        hebrewYear.toInt(),
                        HebrewMonth.getMonthForValue(hebrewMonth.toInt()),
                        hebrewDayOfMonth.toInt()
                    )
                val gregorianYearIsNegative = gregorian.first() == '-'
                val (gregorianYear, gregorianMonth, gregorianDayOfMonth) = (if (gregorianYearIsNegative) gregorian.drop(
                    1
                ) else gregorian).split("-")
                val localDate = LocalDate(
                    gregorianYear.toInt() * if (gregorianYearIsNegative) -1 else 1,
                    gregorianMonth.toInt(),
                    gregorianDayOfMonth.toInt()
                )
//                println("Target gregorian: $localDate")
                val toHebrewDate = runCatching {
                    localDate.toHebrewDate()
                }.getOrNull()
                if (
                    hebrewLocalDate == toHebrewDate
                ) {
                    success.add(line)
                    counter++
                } else if (counter > 1) println("Failure: $line")
            }
        }*/
        println("Success: (${success.size}): \n${success.joinToString("\n")}")
    }

    @Test
    fun `6th month doesn't work (for some reason)`() {
        val hebrew = HebrewLocalDate(4483, HebrewMonth.getMonthForValue(6), 29)
        val gregorian = LocalDate(723, 9, 9)
        assertEquals(hebrew, gregorian.toHebrewDate())
        assertEquals(gregorian, hebrew.toLocalDateGregorian())
    }

    @Test
    fun `adding days to hebrew date works`() {
        val hebrew = HebrewLocalDate.STARTING_DATE_HEBREW
        assertEquals(hebrew.plusDays(1), HebrewLocalDate(hebrew.year, hebrew.month, hebrew.dayOfMonth + 1))
        assertEquals(hebrew.plusDays(2), HebrewLocalDate(hebrew.year, hebrew.month, hebrew.dayOfMonth + 2))
        assertEquals(hebrew.plusDays(32).month, hebrew.month.getNextMonthInYear(hebrew.year))
        val plusYear = hebrew.plusDays(hebrew.year.daysInJewishYear.toLong())
        println(hebrew)
        println(plusYear)
        assertEquals(hebrew.year + 1, plusYear.year)
    }


    // Simple cases, before starting date
/*
    @Test
    fun lessThanMonthBeforeStartingDate() {
        val numDaysToSubtract = 3
        val date = HebrewLocalDate.STARTING_DATE_GREGORIAN.minus(DatePeriod(days = numDaysToSubtract))
        println("Date: $date")
        val hebrew = HebrewLocalDate.STARTING_DATE_HEBREW
        val expectedNewMonth = hebrew.month.previousMonth
        val expectedNewYear = hebrew.year - 1
        val daysInJewishMonth = JewishDate.getDaysInJewishMonth(expectedNewMonth, expectedNewYear)
        println("Days in jewish month: $daysInJewishMonth")
        assertEquals(
            HebrewLocalDate(
                expectedNewYear,
                expectedNewMonth,
                daysInJewishMonth - numDaysToSubtract + hebrew.dayOfMonth*//*include starting day of old year*//*
            ), date.toHebrewDate()
        )
    }

    @Test
    fun oneDayBeforeStartingDate() {
        val numDaysToSubtract = 1
        val date = HebrewLocalDate.STARTING_DATE_GREGORIAN.minus(DatePeriod(days = numDaysToSubtract))
        println("Date: $date")
        val hebrew = HebrewLocalDate.STARTING_DATE_HEBREW
        val expectedNewMonth = hebrew.month.previousMonth
        val expectedNewYear = hebrew.year - 1
        val daysInJewishMonth = JewishDate.getDaysInJewishMonth(expectedNewMonth, expectedNewYear)
        println("Days in jewish month: $daysInJewishMonth")
        assertEquals(
            HebrewLocalDate(
                expectedNewYear,
                expectedNewMonth,
                daysInJewishMonth - numDaysToSubtract + hebrew.dayOfMonth*//*include starting day of old year*//*
            ), date.toHebrewDate()
        )
    }

    @Test
    fun oneYearBeforeStartingDate() {
        val numDaysToSubtract = (HebrewLocalDate.STARTING_DATE_HEBREW.year - 1).daysInJewishYear
        val date = HebrewLocalDate.STARTING_DATE_GREGORIAN.minus(DatePeriod(days = numDaysToSubtract))
        println("Date: $date, num days in previous year: $numDaysToSubtract")
        val hebrew = HebrewLocalDate.STARTING_DATE_HEBREW
        val expectedNewYear = hebrew.year - 1
        assertEquals(
            HebrewLocalDate(
                expectedNewYear,
                hebrew.month,
                hebrew.dayOfMonth
            ),
            date.toHebrewDate()
        )
    }

    @Test
    fun moreThanMonthBeforeStartingDate() {
        val numDaysToSubtract = 32
        val date = HebrewLocalDate.STARTING_DATE_GREGORIAN.minus(DatePeriod(days = numDaysToSubtract))
        println("Date: $date")
        val hebrew = HebrewLocalDate.STARTING_DATE_HEBREW
        val expectedNewMonth = hebrew.month.previousMonth.previousMonth //elul is 29, starting on 1, will end up in av
        val expectedNewYear = hebrew.year - 1
        val daysInJewishMonths = JewishDate.getDaysInJewishMonth(
            expectedNewMonth,
            expectedNewYear
        ) + JewishDate.getDaysInJewishMonth(hebrew.month.previousMonth, expectedNewYear)
        println("Days in jewish month: $daysInJewishMonths")
        assertEquals(
            HebrewLocalDate(
                expectedNewYear,
                expectedNewMonth,
                daysInJewishMonths - numDaysToSubtract + hebrew.dayOfMonth *//*include starting day of old year*//*
            ), date.toHebrewDate()
        )
    }

    @Test
    fun moreThanYearBeforeStartingDate() {
        val numDaysToSubtract =
            355*//*num days in year before STARTING_DATE_HEBREW*//* + 1*//*first day of TISHREI*//* + 385*//*one year before that*//* + 1*//*first day of TISHREI*//*
        val date = HebrewLocalDate.STARTING_DATE_GREGORIAN.minus(DatePeriod(days = numDaysToSubtract))
        println("Date: $date")
        val hebrew = HebrewLocalDate.STARTING_DATE_HEBREW
        val expectedHebrew = HebrewLocalDate(hebrew.year - 2, hebrew.month, 3*//*3x 1'st of TISHREI*//*)
        println("Expected hebrew: $expectedHebrew")
        assertEquals(expectedHebrew, date.toHebrewDate())
    }*/
}