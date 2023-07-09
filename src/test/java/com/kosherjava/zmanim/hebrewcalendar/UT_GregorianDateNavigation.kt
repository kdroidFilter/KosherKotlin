/*
 * Copyright (c) 2011. Jay R. Gindin
 */
package com.kosherjava.zmanim.hebrewcalendar

import org.junit.Assert
import org.junit.Test
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
            1 to { gregorianMonth },
            1 to { gregorianDayOfMonth },
            11 to { jewishMonth },
            27 to { jewishDayOfMonth },
            moveDateForwardBeforeAssert = true,
            setDateBeforeAssert = false,
        )
        assertProperties(
            Calendar.FEBRUARY,
            28,
            1 to { gregorianMonth },
            28 to { gregorianDayOfMonth },
            12 to { jewishMonth },
            24 to { jewishDayOfMonth },
            moveDateForwardBeforeAssert = false
        )
        assertProperties(
            null,
            null,
            2 to { gregorianMonth },
            1 to { gregorianDayOfMonth },
            12 to { jewishMonth },
            25 to { jewishDayOfMonth },
            setDateBeforeAssert = false
        )

        assertProperties(
            Calendar.MARCH,
            31,
            3 to { gregorianMonth },
            1 to { gregorianDayOfMonth },
            13 to { jewishMonth },
            26 to { jewishDayOfMonth },
        )
        assertProperties(
            Calendar.APRIL,
            30,
            4 to { gregorianMonth },
            1 to { gregorianDayOfMonth },
            1 to { jewishMonth },
            27 to { jewishDayOfMonth },
        )
        assertProperties(
            Calendar.MAY,
            31,
            5 to { gregorianMonth },
            1 to { gregorianDayOfMonth },
            2 to { jewishMonth },
            28 to { jewishDayOfMonth },
        )
        assertProperties(
            Calendar.JUNE,
            30,
            6 to { gregorianMonth },
            1 to { gregorianDayOfMonth },
            3 to { jewishMonth },
            29 to { jewishDayOfMonth },
        )
        assertProperties(
            Calendar.JULY,
            31,
            7 to { gregorianMonth },
            1 to { gregorianDayOfMonth },
            5 to { jewishMonth },
            1 to { jewishDayOfMonth },
        )
        assertProperties(
            Calendar.AUGUST,
            31,
            8 to { gregorianMonth },
            1 to { gregorianDayOfMonth },
            6 to { jewishMonth },
            2 to { jewishDayOfMonth },
        )
        assertProperties(
            Calendar.SEPTEMBER,
            30,
            9 to { gregorianMonth },
            1 to { gregorianDayOfMonth },
            7 to { jewishMonth },
            3 to { jewishDayOfMonth }
        )
        assertProperties(
            Calendar.OCTOBER,
            31,
            5772 to { jewishYear },
            10 to { gregorianMonth },
            1 to { gregorianDayOfMonth },
            8 to { jewishMonth },
            4 to { jewishDayOfMonth }
        )
        assertProperties(
            Calendar.NOVEMBER,
            30,
            11 to { gregorianMonth },
            1 to { gregorianDayOfMonth },
            9 to { jewishMonth },
            5 to { jewishDayOfMonth }
        )
        assertProperties(
            Calendar.DECEMBER,
            31,
            2012 to { gregorianYear },
            0 to { gregorianMonth },
            1 to { gregorianDayOfMonth },
            10 to { jewishMonth },
            6 to { jewishDayOfMonth }
        )
    }

    private fun assertProperties(
        month: Int? = null,
        day: Int? = null,
        vararg expectedToActual: Pair<Int, JewishDate.() -> Int>,
        moveDateForwardBeforeAssert: Boolean = true,
        setDateBeforeAssert: Boolean = true,
        moveDateForwardAfterAssert: Boolean = false,
    ) {
        if (month != null) cal[Calendar.MONTH] = month
        if (day != null) cal[Calendar.DATE] = day
        if(setDateBeforeAssert) hebrewDate.setDate(cal)
        if(moveDateForwardBeforeAssert) hebrewDate.forward(Calendar.DATE, 1)
        for((expected, actual) in expectedToActual) Assert.assertEquals(expected, hebrewDate.actual())
        if(moveDateForwardAfterAssert) hebrewDate.forward(Calendar.DATE, 1)
    }

    @Test
    fun gregorianBackwardMonthToMonth() {
        val cal = Calendar.getInstance()
        cal[Calendar.YEAR] = 2011
        cal[Calendar.MONTH] = Calendar.JANUARY
        cal[Calendar.DATE] = 1
        val hebrewDate = JewishDate(cal)
        hebrewDate.back()
        Assert.assertEquals(2010, hebrewDate.gregorianYear)
        Assert.assertEquals(11, hebrewDate.gregorianMonth)
        Assert.assertEquals(31, hebrewDate.gregorianDayOfMonth)
        Assert.assertEquals(10, hebrewDate.jewishMonth)
        Assert.assertEquals(24, hebrewDate.jewishDayOfMonth)
        cal[Calendar.DATE] = 1
        cal[Calendar.MONTH] = Calendar.DECEMBER
        cal[Calendar.YEAR] = 2010
        hebrewDate.setDate(cal)
        hebrewDate.back()
        Assert.assertEquals(10, hebrewDate.gregorianMonth)
        Assert.assertEquals(30, hebrewDate.gregorianDayOfMonth)
        Assert.assertEquals(9, hebrewDate.jewishMonth)
        Assert.assertEquals(23, hebrewDate.jewishDayOfMonth)
        cal[Calendar.DATE] = 1
        cal[Calendar.MONTH] = Calendar.NOVEMBER
        hebrewDate.setDate(cal)
        hebrewDate.back()
        Assert.assertEquals(9, hebrewDate.gregorianMonth)
        Assert.assertEquals(31, hebrewDate.gregorianDayOfMonth)
        Assert.assertEquals(8, hebrewDate.jewishMonth)
        Assert.assertEquals(23, hebrewDate.jewishDayOfMonth)
        cal[Calendar.DATE] = 1
        cal[Calendar.MONTH] = Calendar.OCTOBER
        hebrewDate.setDate(cal)
        hebrewDate.back()
        Assert.assertEquals(8, hebrewDate.gregorianMonth)
        Assert.assertEquals(30, hebrewDate.gregorianDayOfMonth)
        Assert.assertEquals(7, hebrewDate.jewishMonth)
        Assert.assertEquals(22, hebrewDate.jewishDayOfMonth)
        cal[Calendar.DATE] = 1
        cal[Calendar.MONTH] = Calendar.SEPTEMBER
        hebrewDate.setDate(cal)
        hebrewDate.back()
        Assert.assertEquals(7, hebrewDate.gregorianMonth)
        Assert.assertEquals(31, hebrewDate.gregorianDayOfMonth)
        Assert.assertEquals(5770, hebrewDate.jewishYear)
        Assert.assertEquals(6, hebrewDate.jewishMonth)
        Assert.assertEquals(21, hebrewDate.jewishDayOfMonth)
        cal[Calendar.DATE] = 1
        cal[Calendar.MONTH] = Calendar.AUGUST
        hebrewDate.setDate(cal)
        hebrewDate.back()
        Assert.assertEquals(6, hebrewDate.gregorianMonth)
        Assert.assertEquals(31, hebrewDate.gregorianDayOfMonth)
        Assert.assertEquals(5, hebrewDate.jewishMonth)
        Assert.assertEquals(20, hebrewDate.jewishDayOfMonth)
        cal[Calendar.DATE] = 1
        cal[Calendar.MONTH] = Calendar.JULY
        hebrewDate.setDate(cal)
        hebrewDate.back()
        Assert.assertEquals(5, hebrewDate.gregorianMonth)
        Assert.assertEquals(30, hebrewDate.gregorianDayOfMonth)
        Assert.assertEquals(4, hebrewDate.jewishMonth)
        Assert.assertEquals(18, hebrewDate.jewishDayOfMonth)
        cal[Calendar.DATE] = 1
        cal[Calendar.MONTH] = Calendar.JUNE
        hebrewDate.setDate(cal)
        hebrewDate.back()
        Assert.assertEquals(4, hebrewDate.gregorianMonth)
        Assert.assertEquals(31, hebrewDate.gregorianDayOfMonth)
        Assert.assertEquals(3, hebrewDate.jewishMonth)
        Assert.assertEquals(18, hebrewDate.jewishDayOfMonth)
        cal[Calendar.DATE] = 1
        cal[Calendar.MONTH] = Calendar.MAY
        hebrewDate.setDate(cal)
        hebrewDate.back()
        Assert.assertEquals(3, hebrewDate.gregorianMonth)
        Assert.assertEquals(30, hebrewDate.gregorianDayOfMonth)
        Assert.assertEquals(2, hebrewDate.jewishMonth)
        Assert.assertEquals(16, hebrewDate.jewishDayOfMonth)
        cal[Calendar.DATE] = 1
        cal[Calendar.MONTH] = Calendar.APRIL
        hebrewDate.setDate(cal)
        hebrewDate.back()
        Assert.assertEquals(2, hebrewDate.gregorianMonth)
        Assert.assertEquals(31, hebrewDate.gregorianDayOfMonth)
        Assert.assertEquals(1, hebrewDate.jewishMonth)
        Assert.assertEquals(16, hebrewDate.jewishDayOfMonth)
        cal[Calendar.DATE] = 1
        cal[Calendar.MONTH] = Calendar.MARCH
        hebrewDate.setDate(cal)
        hebrewDate.back()
        Assert.assertEquals(1, hebrewDate.gregorianMonth)
        Assert.assertEquals(28, hebrewDate.gregorianDayOfMonth)
        Assert.assertEquals(12, hebrewDate.jewishMonth)
        Assert.assertEquals(14, hebrewDate.jewishDayOfMonth)
        cal[Calendar.DATE] = 1
        cal[Calendar.MONTH] = Calendar.FEBRUARY
        hebrewDate.setDate(cal)
        hebrewDate.back()
        Assert.assertEquals(0, hebrewDate.gregorianMonth)
        Assert.assertEquals(31, hebrewDate.gregorianDayOfMonth)
        Assert.assertEquals(11, hebrewDate.jewishMonth)
        Assert.assertEquals(16, hebrewDate.jewishDayOfMonth)
    }
} // End of UT_GregorianDateNavigation class
