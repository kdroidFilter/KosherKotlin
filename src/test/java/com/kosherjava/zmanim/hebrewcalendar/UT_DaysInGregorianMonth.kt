/*
 * Copyright (c) 2011. Jay R. Gindin
 */
package com.kosherjava.zmanim.hebrewcalendar

import kotlinx.datetime.LocalDate
import kotlinx.datetime.toKotlinInstant
import kotlinx.datetime.toLocalDateTime
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.util.Calendar

/**
 * Verify the calculation of the number of days in a month. Not too hard...just the rules about when February
 * has 28 or 29 days...
 */
class UT_DaysInGregorianMonth {

    lateinit var hebrewDate: JewishDate
    lateinit var cal: Calendar

    @Before
    fun setupProps() {
        hebrewDate = JewishDate()
        cal = Calendar.getInstance()
    }

    @Test
    fun testDaysInMonth() {
        cal[Calendar.YEAR] = 2011
        cal[Calendar.MONTH] = Calendar.JANUARY
        hebrewDate.setDate(LocalDate(cal[Calendar.YEAR], cal[Calendar.MONTH] + 1, cal[Calendar.DAY_OF_MONTH]))
        hebrewDate.assertDaysInMonth(false)
    }

    @Test
    fun testDaysInMonthLeapYear() {
        cal[Calendar.YEAR] = 2012
        cal[Calendar.MONTH] = Calendar.JANUARY
        hebrewDate.setDate(LocalDate(cal[Calendar.YEAR], cal[Calendar.MONTH] + 1, cal[Calendar.DAY_OF_MONTH]))
        hebrewDate.assertDaysInMonth(true)
    }

    @Test
    fun testDaysInMonth100Year() {
        cal[Calendar.YEAR] = 2100
        cal[Calendar.MONTH] = Calendar.JANUARY
        hebrewDate.setDate(LocalDate(cal[Calendar.YEAR], cal[Calendar.MONTH] + 1, cal[Calendar.DAY_OF_MONTH]))
        hebrewDate.assertDaysInMonth(false)
    }

    @Test
    fun testDaysInMonth400Year() {
        cal[Calendar.YEAR] = 2000
        cal[Calendar.MONTH] = Calendar.JANUARY
        hebrewDate.setDate(LocalDate(cal[Calendar.YEAR], cal[Calendar.MONTH] + 1, cal[Calendar.DAY_OF_MONTH]))
        hebrewDate.assertDaysInMonth(true)
    }

    private fun JewishDate.assertDaysInMonth(
        febIsLeap: Boolean
    ) {
        Assert.assertEquals(31, 1.lastDayOfGregorianMonth.toLong())
        Assert.assertEquals(if (febIsLeap) 29 else 28.toLong(), 2.lastDayOfGregorianMonth.toLong())
        Assert.assertEquals(31, 3.lastDayOfGregorianMonth.toLong())
        Assert.assertEquals(30, 4.lastDayOfGregorianMonth.toLong())
        Assert.assertEquals(31, 5.lastDayOfGregorianMonth.toLong())
        Assert.assertEquals(30, 6.lastDayOfGregorianMonth.toLong())
        Assert.assertEquals(31, 7.lastDayOfGregorianMonth.toLong())
        Assert.assertEquals(31, 8.lastDayOfGregorianMonth.toLong())
        Assert.assertEquals(30, 9.lastDayOfGregorianMonth.toLong())
        Assert.assertEquals(31, 10.lastDayOfGregorianMonth.toLong())
        Assert.assertEquals(30, 11.lastDayOfGregorianMonth.toLong())
        Assert.assertEquals(31, 12.lastDayOfGregorianMonth.toLong())
    }
} // End of UT_DaysInGregorianMonth class
