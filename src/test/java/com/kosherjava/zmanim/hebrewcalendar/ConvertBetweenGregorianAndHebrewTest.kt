package com.kosherjava.zmanim.hebrewcalendar

import com.kosherjava.zmanim.hebrewcalendar.HebrewLocalDate.Companion.toHebrewDate
import com.kosherjava.zmanim.hebrewcalendar.JewishDate.Companion.daysInJewishYear
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import org.junit.Assert.assertEquals
import org.junit.Test

class ConvertBetweenGregorianAndHebrewTest {

    // Simple cases, after starting date

    @Test
    fun oneDayAfterStartingDate() {
        val date = HebrewLocalDate.STARTING_DATE_GREGORIAN.plus(DatePeriod(days = 1))
        val hebrew = HebrewLocalDate.STARTING_DATE_HEBREW
        assertEquals(HebrewLocalDate(hebrew.year, hebrew.month, hebrew.dayOfMonth + 1), date.toHebrewDate())
    }

    @Test
    fun oneMonthAfterStartingDate() {
        val date = HebrewLocalDate.STARTING_DATE_GREGORIAN.plus(DatePeriod(days = 30))
        val hebrew = HebrewLocalDate.STARTING_DATE_HEBREW
        assertEquals(HebrewLocalDate(hebrew.year, hebrew.month.nextMonth, hebrew.dayOfMonth), date.toHebrewDate())
    }

    @Test
    fun oneYearAfterStartingDate() {
        val hebrew = HebrewLocalDate.STARTING_DATE_HEBREW
        val date = HebrewLocalDate.STARTING_DATE_GREGORIAN.plus(DatePeriod(days = hebrew.year.daysInJewishYear))
        assertEquals(HebrewLocalDate(hebrew.year + 1, hebrew.month, hebrew.dayOfMonth), date.toHebrewDate())
    }

    @Test
    fun lessThanMonthAfterStartingDate() {
        val date = HebrewLocalDate.STARTING_DATE_GREGORIAN.plus(DatePeriod(days = 3))
        val hebrew = HebrewLocalDate.STARTING_DATE_HEBREW
        assertEquals(HebrewLocalDate(hebrew.year, hebrew.month, hebrew.dayOfMonth + 3), date.toHebrewDate())
    }

    @Test
    fun moreThanMonthAfterStartingDate() {
        val date = HebrewLocalDate.STARTING_DATE_GREGORIAN.plus(DatePeriod(days = 32))
        val hebrew = HebrewLocalDate.STARTING_DATE_HEBREW
        assertEquals(HebrewLocalDate(hebrew.year, hebrew.month.nextMonth, 3), date.toHebrewDate())
    }

    @Test
    fun moreThanYearAfterStartingDate() {
        val hebrew = HebrewLocalDate.STARTING_DATE_HEBREW
        val date = HebrewLocalDate.STARTING_DATE_GREGORIAN.plus(DatePeriod(days = hebrew.year.daysInJewishYear + 1))
        println("Num days in year")
        assertEquals(HebrewLocalDate(hebrew.year + 1, hebrew.month, hebrew.dayOfMonth + 1), date.toHebrewDate())
    }

    //two-way conversion


    @Test
    fun bi_oneDayAfterStartingDate() {
        val startingDateGregorian = HebrewLocalDate.STARTING_DATE_GREGORIAN
        println("Starting gregorian date: $startingDateGregorian")
        val date = startingDateGregorian.plus(DatePeriod(days = 1))
        val hebrew = HebrewLocalDate.STARTING_DATE_HEBREW
        assertEquals(date, HebrewLocalDate(hebrew.year, hebrew.month, hebrew.dayOfMonth + 1).toLocalDateGregorian())
    }

    @Test
    fun bi_oneMonthAfterStartingDate() {
        val date = HebrewLocalDate.STARTING_DATE_GREGORIAN.plus(DatePeriod(days = 30))
        val hebrew = HebrewLocalDate.STARTING_DATE_HEBREW
        assertEquals(date, HebrewLocalDate(hebrew.year, hebrew.month.nextMonth, hebrew.dayOfMonth).toLocalDateGregorian())
    }

    @Test
    fun bi_oneYearAfterStartingDate() {
        val hebrew = HebrewLocalDate.STARTING_DATE_HEBREW
        val date = HebrewLocalDate.STARTING_DATE_GREGORIAN.plus(DatePeriod(days = hebrew.year.daysInJewishYear))
        assertEquals(date, HebrewLocalDate(hebrew.year + 1, hebrew.month, hebrew.dayOfMonth).toLocalDateGregorian())
    }

    @Test
    fun bi_lessThanMonthAfterStartingDate() {
        val date = HebrewLocalDate.STARTING_DATE_GREGORIAN.plus(DatePeriod(days = 3))
        val hebrew = HebrewLocalDate.STARTING_DATE_HEBREW
        assertEquals(date, HebrewLocalDate(hebrew.year, hebrew.month, hebrew.dayOfMonth + 3).toLocalDateGregorian())
    }

    @Test
    fun bi_moreThanMonthAfterStartingDate() {
        val date = HebrewLocalDate.STARTING_DATE_GREGORIAN.plus(DatePeriod(days = 32))
        val hebrew = HebrewLocalDate.STARTING_DATE_HEBREW
        assertEquals(date, HebrewLocalDate(hebrew.year, hebrew.month.nextMonth, 3).toLocalDateGregorian())
    }

    @Test
    fun bi_moreThanYearAfterStartingDate() {
        val hebrew = HebrewLocalDate.STARTING_DATE_HEBREW
        val date = HebrewLocalDate.STARTING_DATE_GREGORIAN.plus(DatePeriod(days = hebrew.year.daysInJewishYear + 1))
        println("Num days in year")
        assertEquals(date, HebrewLocalDate(hebrew.year + 1, hebrew.month, hebrew.dayOfMonth + 1).toLocalDateGregorian())
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