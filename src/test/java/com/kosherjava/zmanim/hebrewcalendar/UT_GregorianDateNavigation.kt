/*
 * Copyright (c) 2011. Jay R. Gindin
 */
package com.kosherjava.zmanim.hebrewcalendar

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
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
        hebrewDate = JewishDate(LocalDate(cal[Calendar.YEAR], cal[Calendar.MONTH] + 1, cal[Calendar.DAY_OF_MONTH]))
        assertProperties(
            null,
            null,
            5771 to { jewishYear.toInt() },
            11 to { jewishMonth.getTishreiBasedValueInYear(jewishYear) },
            26 to { jewishDayOfMonth },
            moveDateForwardBeforeAssert = false,
            setDateBeforeAssert = false,
        )
        assertProperties(
            null,
            null,
            1 to { gregorianLocalDate.monthNumber - 1 },
            1 to { gregorianLocalDate.dayOfMonth },
            11 to { jewishMonth.getTishreiBasedValueInYear(jewishYear) },
            27 to { jewishDayOfMonth },
            moveDateForwardBeforeAssert = true,
            setDateBeforeAssert = false,
        )
        assertProperties(
            Calendar.FEBRUARY,
            28,
            1 to { gregorianLocalDate.monthNumber - 1 },
            28 to { gregorianLocalDate.dayOfMonth },
            12 to { jewishMonth.getTishreiBasedValueInYear(jewishYear) },
            24 to { jewishDayOfMonth },
            moveDateForwardBeforeAssert = false
        )
        assertProperties(
            null,
            null,
            2 to { gregorianLocalDate.monthNumber - 1 },
            1 to { gregorianLocalDate.dayOfMonth },
            12 to { jewishMonth.getTishreiBasedValueInYear(jewishYear) },
            25 to { jewishDayOfMonth },
            setDateBeforeAssert = false
        )

        assertProperties(
            Calendar.MARCH,
            31,
            3 to { gregorianLocalDate.monthNumber - 1 },
            1 to { gregorianLocalDate.dayOfMonth },
            13 to { jewishMonth.getTishreiBasedValueInYear(jewishYear) },
            26 to { jewishDayOfMonth },
        )
        assertProperties(
            Calendar.APRIL,
            30,
            4 to { gregorianLocalDate.monthNumber - 1 },
            1 to { gregorianLocalDate.dayOfMonth },
            1 to { jewishMonth.getTishreiBasedValueInYear(jewishYear) },
            27 to { jewishDayOfMonth },
        )
        assertProperties(
            Calendar.MAY,
            31,
            5 to { gregorianLocalDate.monthNumber - 1 },
            1 to { gregorianLocalDate.dayOfMonth },
            2 to { jewishMonth.getTishreiBasedValueInYear(jewishYear) },
            28 to { jewishDayOfMonth },
        )
        assertProperties(
            Calendar.JUNE,
            30,
            6 to { gregorianLocalDate.monthNumber - 1 },
            1 to { gregorianLocalDate.dayOfMonth },
            3 to { jewishMonth.getTishreiBasedValueInYear(jewishYear) },
            29 to { jewishDayOfMonth },
        )
        assertProperties(
            Calendar.JULY,
            31,
            7 to { gregorianLocalDate.monthNumber - 1 },
            1 to { gregorianLocalDate.dayOfMonth },
            5 to { jewishMonth.getTishreiBasedValueInYear(jewishYear) },
            1 to { jewishDayOfMonth },
        )
        assertProperties(
            Calendar.AUGUST,
            31,
            8 to { gregorianLocalDate.monthNumber - 1 },
            1 to { gregorianLocalDate.dayOfMonth },
            6 to { jewishMonth.getTishreiBasedValueInYear(jewishYear) },
            2 to { jewishDayOfMonth },
        )
        assertProperties(
            Calendar.SEPTEMBER,
            30,
            9 to { gregorianLocalDate.monthNumber - 1 },
            1 to { gregorianLocalDate.dayOfMonth },
            7 to { jewishMonth.getTishreiBasedValueInYear(jewishYear) },
            3 to { jewishDayOfMonth }
        )
        assertProperties(
            Calendar.OCTOBER,
            31,
            5772 to { jewishYear.toInt() },
            10 to { gregorianLocalDate.monthNumber - 1 },
            1 to { gregorianLocalDate.dayOfMonth },
            8 to { jewishMonth.getTishreiBasedValueInYear(jewishYear) },
            4 to { jewishDayOfMonth }
        )
        assertProperties(
            Calendar.NOVEMBER,
            30,
            11 to { gregorianLocalDate.monthNumber - 1 },
            1 to { gregorianLocalDate.dayOfMonth },
            9 to { jewishMonth.getTishreiBasedValueInYear(jewishYear) },
            5 to { jewishDayOfMonth }
        )
        assertProperties(
            Calendar.DECEMBER,
            31,
            2012 to { gregorianLocalDate.year },
            0 to { gregorianLocalDate.monthNumber - 1 },
            1 to { gregorianLocalDate.dayOfMonth },
            10 to { jewishMonth.getTishreiBasedValueInYear(jewishYear) },
            6 to { jewishDayOfMonth }
        )
    }

    @Test
    fun gregorianBackwardMonthToMonth() {
        cal = Calendar.getInstance(TimeZone.getTimeZone(ZoneId.of("America/New_York")))
        cal[Calendar.YEAR] = 2011
        cal[Calendar.MONTH] = Calendar.JANUARY
        cal[Calendar.DATE] = 1
        hebrewDate = JewishDate(LocalDate(cal[Calendar.YEAR], cal[Calendar.MONTH] + 1, cal[Calendar.DAY_OF_MONTH]))
        assertProperties(
            null,
            null,
            2010 to { gregorianLocalDate.year },
            11 to { gregorianLocalDate.monthNumber - 1 },
            31 to { gregorianLocalDate.dayOfMonth },
            10 to { jewishMonth.getTishreiBasedValueInYear(jewishYear) },
            24 to { jewishDayOfMonth },
            moveDateForwardBeforeAssert = false,
            moveDateBackwardBeforeAssert = true
        )

        cal[Calendar.YEAR] = 2010
        assertProperties(
            Calendar.DECEMBER,
            1,
            10 to { gregorianLocalDate.monthNumber - 1 },
            30 to { gregorianLocalDate.dayOfMonth },
            9 to { jewishMonth.getTishreiBasedValueInYear(jewishYear) },
            23 to { jewishDayOfMonth },
            moveDateForwardBeforeAssert = false,
            moveDateBackwardBeforeAssert = true
        )
        assertProperties(
            Calendar.NOVEMBER,
            1,
            9 to { gregorianLocalDate.monthNumber - 1 },
            31 to { gregorianLocalDate.dayOfMonth },
            8 to { jewishMonth.getTishreiBasedValueInYear(jewishYear) },
            23 to { jewishDayOfMonth },
            moveDateForwardBeforeAssert = false,
            moveDateBackwardBeforeAssert = true
        )
        assertProperties(
            Calendar.OCTOBER,
            1,
            8 to { gregorianLocalDate.monthNumber - 1 },
            30 to { gregorianLocalDate.dayOfMonth },
            7 to { jewishMonth.getTishreiBasedValueInYear(jewishYear) },
            22 to { jewishDayOfMonth },
            moveDateForwardBeforeAssert = false,
            moveDateBackwardBeforeAssert = true
        )
        assertProperties(
            Calendar.SEPTEMBER,
            1,
            7 to { gregorianLocalDate.monthNumber - 1 },
            31 to { gregorianLocalDate.dayOfMonth },
            5770 to { jewishYear.toInt() },
            6 to { jewishMonth.getTishreiBasedValueInYear(jewishYear) },
            moveDateForwardBeforeAssert = false,
            moveDateBackwardBeforeAssert = true
        )
        assertProperties(
            Calendar.AUGUST,
            1,
            6 to { gregorianLocalDate.monthNumber - 1 },
            31 to { gregorianLocalDate.dayOfMonth },
            5 to { jewishMonth.getTishreiBasedValueInYear(jewishYear) },
            20 to { jewishDayOfMonth },
            moveDateForwardBeforeAssert = false,
            moveDateBackwardBeforeAssert = true
        )
        assertProperties(
            Calendar.JULY,
            1,
            5 to { gregorianLocalDate.monthNumber - 1 },
            30 to { gregorianLocalDate.dayOfMonth },
            4 to { jewishMonth.getTishreiBasedValueInYear(jewishYear) },
            18 to { jewishDayOfMonth },
            moveDateForwardBeforeAssert = false,
            moveDateBackwardBeforeAssert = true
        )
        assertProperties(
            Calendar.JUNE,
            1,
            4 to { gregorianLocalDate.monthNumber - 1 },
            31 to { gregorianLocalDate.dayOfMonth },
            3 to { jewishMonth.getTishreiBasedValueInYear(jewishYear) },
            18 to { jewishDayOfMonth },
            moveDateForwardBeforeAssert = false,
            moveDateBackwardBeforeAssert = true
        )

        assertProperties(
            Calendar.MAY,
            1,
            3 to { gregorianLocalDate.monthNumber - 1 },
            30 to { gregorianLocalDate.dayOfMonth },
            2 to { jewishMonth.getTishreiBasedValueInYear(jewishYear) },
            16 to { jewishDayOfMonth },
            moveDateForwardBeforeAssert = false,
            moveDateBackwardBeforeAssert = true
        )

        assertProperties(
            Calendar.APRIL,
            1,
            2 to { gregorianLocalDate.monthNumber - 1 },
            31 to { gregorianLocalDate.dayOfMonth },
            1 to { jewishMonth.getTishreiBasedValueInYear(jewishYear) },
            16 to { jewishDayOfMonth },
            moveDateForwardBeforeAssert = false,
            moveDateBackwardBeforeAssert = true
        )
        assertProperties(
            Calendar.MARCH,
            1,
            1 to { gregorianLocalDate.monthNumber - 1 },
            28 to { gregorianLocalDate.dayOfMonth },
            12 to { jewishMonth.getTishreiBasedValueInYear(jewishYear) },
            14 to { jewishDayOfMonth },
            moveDateForwardBeforeAssert = false,
            moveDateBackwardBeforeAssert = true
        )
        assertProperties(
            Calendar.FEBRUARY,
            1,
            0 to { gregorianLocalDate.monthNumber - 1 },
            31 to { gregorianLocalDate.dayOfMonth },
            11 to { jewishMonth.getTishreiBasedValueInYear(jewishYear) },
            16 to { jewishDayOfMonth },
            moveDateForwardBeforeAssert = false,
            moveDateBackwardBeforeAssert = true
        )
    }

    @JvmName("assertPropertiesInt")
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
        if (setDateBeforeAssert) hebrewDate.setDate(LocalDate(cal[Calendar.YEAR], cal[Calendar.MONTH] + 1, cal[Calendar.DAY_OF_MONTH]))
        if (moveDateForwardBeforeAssert) hebrewDate.forward(DateTimeUnit.DAY, 1)
        if (moveDateBackwardBeforeAssert) hebrewDate.back()
        for ((expected, actual) in expectedToActual) Assert.assertEquals(expected, hebrewDate.actual())
        if (moveDateForwardAfterAssert) hebrewDate.forward(DateTimeUnit.DAY, 1)
    }
    private fun assertProperties(
        month: Int? = null,
        day: Int? = null,
        vararg expectedToActual: Pair<Long, JewishDate.() -> Int>,
        moveDateForwardBeforeAssert: Boolean = true,
        setDateBeforeAssert: Boolean = true,
        moveDateForwardAfterAssert: Boolean = false,
        moveDateBackwardBeforeAssert: Boolean = false
    ) {
        if (month != null) cal[Calendar.MONTH] = month
        if (day != null) cal[Calendar.DATE] = day
        if (setDateBeforeAssert) hebrewDate.setDate(LocalDate(cal[Calendar.YEAR], cal[Calendar.MONTH] + 1, cal[Calendar.DAY_OF_MONTH]))
        if (moveDateForwardBeforeAssert) hebrewDate.forward(DateTimeUnit.DAY, 1)
        if (moveDateBackwardBeforeAssert) hebrewDate.back()
        for ((expected, actual) in expectedToActual) Assert.assertEquals(expected, hebrewDate.actual())
        if (moveDateForwardAfterAssert) hebrewDate.forward(DateTimeUnit.DAY, 1)
    }
} // End of UT_GregorianDateNavigation class
