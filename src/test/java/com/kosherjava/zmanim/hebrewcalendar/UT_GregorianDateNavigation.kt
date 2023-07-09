/*
 * Copyright (c) 2011. Jay R. Gindin
 */
package com.kosherjava.zmanim.hebrewcalendar

import org.junit.Assert
import org.junit.Test
import java.time.ZoneId
import java.util.*

/**
 * Checks that we can roll forward & backward the gregorian dates...
 */
class UT_GregorianDateNavigation {

    lateinit var cal: Calendar
    lateinit var hebrewDate: JewishDate

    @Test
    fun gregorianForwardMonthToMonth() {
        cal = Calendar.getInstance()
        cal[Calendar.YEAR] = 2011
        cal[Calendar.MONTH] = Calendar.JANUARY
        cal[Calendar.DATE] = 31
        hebrewDate = JewishDate(cal)
        assertProperties(
            null,
            null,
            5771 to { jewishYear },
            11 to { jewishMonth },
            26 to { jewishDayOfMonth },
            moveDateForwardBeforeAssert = false,
            setDateBeforeAssert = false,
        )
        assertProperties(
            null,
            null,
            1 to { gregorianMonthZeroBased },
            1 to { gregorianDayOfMonth },
            11 to { jewishMonth },
            27 to { jewishDayOfMonth },
            moveDateForwardBeforeAssert = true,
            setDateBeforeAssert = false,
        )
        assertProperties(
            Calendar.FEBRUARY,
            28,
            1 to { gregorianMonthZeroBased },
            28 to { gregorianDayOfMonth },
            12 to { jewishMonth },
            24 to { jewishDayOfMonth },
            moveDateForwardBeforeAssert = false
        )
        assertProperties(
            null,
            null,
            2 to { gregorianMonthZeroBased },
            1 to { gregorianDayOfMonth },
            12 to { jewishMonth },
            25 to { jewishDayOfMonth },
            setDateBeforeAssert = false
        )

        assertProperties(
            Calendar.MARCH,
            31,
            3 to { gregorianMonthZeroBased },
            1 to { gregorianDayOfMonth },
            13 to { jewishMonth },
            26 to { jewishDayOfMonth },
        )
        assertProperties(
            Calendar.APRIL,
            30,
            4 to { gregorianMonthZeroBased },
            1 to { gregorianDayOfMonth },
            1 to { jewishMonth },
            27 to { jewishDayOfMonth },
        )
        assertProperties(
            Calendar.MAY,
            31,
            5 to { gregorianMonthZeroBased },
            1 to { gregorianDayOfMonth },
            2 to { jewishMonth },
            28 to { jewishDayOfMonth },
        )
        assertProperties(
            Calendar.JUNE,
            30,
            6 to { gregorianMonthZeroBased },
            1 to { gregorianDayOfMonth },
            3 to { jewishMonth },
            29 to { jewishDayOfMonth },
        )
        assertProperties(
            Calendar.JULY,
            31,
            7 to { gregorianMonthZeroBased },
            1 to { gregorianDayOfMonth },
            5 to { jewishMonth },
            1 to { jewishDayOfMonth },
        )
        assertProperties(
            Calendar.AUGUST,
            31,
            8 to { gregorianMonthZeroBased },
            1 to { gregorianDayOfMonth },
            6 to { jewishMonth },
            2 to { jewishDayOfMonth },
        )
        assertProperties(
            Calendar.SEPTEMBER,
            30,
            9 to { gregorianMonthZeroBased },
            1 to { gregorianDayOfMonth },
            7 to { jewishMonth },
            3 to { jewishDayOfMonth }
        )
        assertProperties(
            Calendar.OCTOBER,
            31,
            5772 to { jewishYear },
            10 to { gregorianMonthZeroBased },
            1 to { gregorianDayOfMonth },
            8 to { jewishMonth },
            4 to { jewishDayOfMonth }
        )
        assertProperties(
            Calendar.NOVEMBER,
            30,
            11 to { gregorianMonthZeroBased },
            1 to { gregorianDayOfMonth },
            9 to { jewishMonth },
            5 to { jewishDayOfMonth }
        )
        assertProperties(
            Calendar.DECEMBER,
            31,
            2012 to { gregorianYear },
            0 to { gregorianMonthZeroBased },
            1 to { gregorianDayOfMonth },
            10 to { jewishMonth },
            6 to { jewishDayOfMonth }
        )
    }

    @Test
    fun gregorianBackwardMonthToMonth() {
        cal = Calendar.getInstance(TimeZone.getTimeZone(ZoneId.of("America/New_York")))
        cal[Calendar.YEAR] = 2011
        cal[Calendar.MONTH] = Calendar.JANUARY
        cal[Calendar.DATE] = 1
        hebrewDate = JewishDate(cal)
        assertProperties(
            null,
            null,
            2010 to { gregorianYear },
            11 to { gregorianMonthZeroBased },
            31 to { gregorianDayOfMonth },
            10 to { jewishMonth },
            24 to { jewishDayOfMonth },
            moveDateForwardBeforeAssert = false,
            moveDateBackwardBeforeAssert = true
        )

        cal[Calendar.YEAR] = 2010
        assertProperties(
            Calendar.DECEMBER,
            1,
            10 to { gregorianMonthZeroBased },
            30 to { gregorianDayOfMonth },
            9 to { jewishMonth },
            23 to { jewishDayOfMonth },
            moveDateForwardBeforeAssert = false,
            moveDateBackwardBeforeAssert = true
        )
        assertProperties(
            Calendar.NOVEMBER,
            1,
            9 to { gregorianMonthZeroBased },
            31 to { gregorianDayOfMonth },
            8 to { jewishMonth },
            23 to { jewishDayOfMonth },
            moveDateForwardBeforeAssert = false,
            moveDateBackwardBeforeAssert = true
        )
        assertProperties(
            Calendar.OCTOBER,
            1,
            8 to { gregorianMonthZeroBased },
            30 to { gregorianDayOfMonth },
            7 to { jewishMonth },
            22 to { jewishDayOfMonth },
            moveDateForwardBeforeAssert = false,
            moveDateBackwardBeforeAssert = true
        )
        assertProperties(
            Calendar.SEPTEMBER,
            1,
            7 to { gregorianMonthZeroBased },
            31 to { gregorianDayOfMonth },
            5770 to { jewishYear },
            6 to { jewishMonth },
            moveDateForwardBeforeAssert = false,
            moveDateBackwardBeforeAssert = true
        )
        assertProperties(
            Calendar.AUGUST,
            1,
            6 to { gregorianMonthZeroBased },
            31 to { gregorianDayOfMonth },
            5 to { jewishMonth },
            20 to { jewishDayOfMonth },
            moveDateForwardBeforeAssert = false,
            moveDateBackwardBeforeAssert = true
        )
        assertProperties(
            Calendar.JULY,
            1,
            5 to { gregorianMonthZeroBased },
            30 to { gregorianDayOfMonth },
            4 to { jewishMonth },
            18 to { jewishDayOfMonth },
            moveDateForwardBeforeAssert = false,
            moveDateBackwardBeforeAssert = true
        )
        assertProperties(
            Calendar.JUNE,
            1,
            4 to { gregorianMonthZeroBased },
            31 to { gregorianDayOfMonth },
            3 to { jewishMonth },
            18 to { jewishDayOfMonth },
            moveDateForwardBeforeAssert = false,
            moveDateBackwardBeforeAssert = true
        )

        assertProperties(
            Calendar.MAY,
            1,
            3 to { gregorianMonthZeroBased },
            30 to { gregorianDayOfMonth },
            2 to { jewishMonth },
            16 to { jewishDayOfMonth },
            moveDateForwardBeforeAssert = false,
            moveDateBackwardBeforeAssert = true
        )

        assertProperties(
            Calendar.APRIL,
            1,
            2 to { gregorianMonthZeroBased },
            31 to { gregorianDayOfMonth },
            1 to { jewishMonth },
            16 to { jewishDayOfMonth },
            moveDateForwardBeforeAssert = false,
            moveDateBackwardBeforeAssert = true
        )
        assertProperties(
            Calendar.MARCH,
            1,
            1 to { gregorianMonthZeroBased },
            28 to { gregorianDayOfMonth },
            12 to { jewishMonth },
            14 to { jewishDayOfMonth },
            moveDateForwardBeforeAssert = false,
            moveDateBackwardBeforeAssert = true
        )
        assertProperties(
            Calendar.FEBRUARY,
            1,
            0 to { gregorianMonthZeroBased },
            31 to { gregorianDayOfMonth },
            11 to { jewishMonth },
            16 to { jewishDayOfMonth },
            moveDateForwardBeforeAssert = false,
            moveDateBackwardBeforeAssert = true
        )
    }

    private fun assertProperties(
        month: Int? = null,
        day: Int? = null,
        vararg expectedToActual: Pair<Int, JewishDate.() -> Int>,
        moveDateForwardBeforeAssert: Boolean = true,
        setDateBeforeAssert: Boolean = true,
        moveDateForwardAfterAssert: Boolean = false,
        moveDateBackwardBeforeAssert: Boolean = false
    ) {
        if (month != null) cal[Calendar.MONTH] = month
        if (day != null) cal[Calendar.DATE] = day
        if (setDateBeforeAssert) hebrewDate.setDate(cal)
        if (moveDateForwardBeforeAssert) hebrewDate.forward(Calendar.DATE, 1)
        if (moveDateBackwardBeforeAssert) hebrewDate.back()
        for ((expected, actual) in expectedToActual) Assert.assertEquals(expected, hebrewDate.actual())
        if (moveDateForwardAfterAssert) hebrewDate.forward(Calendar.DATE, 1)
    }
} // End of UT_GregorianDateNavigation class
