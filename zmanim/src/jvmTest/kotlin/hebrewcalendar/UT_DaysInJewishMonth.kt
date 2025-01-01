/*
 * Copyright (c) 2011. Jay R. Gindin
 */
package hebrewcalendar

import org.junit.Assert
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import io.github.kdroidfilter.kosherkotlin.hebrewcalendar.JewishDate

/**
 * Validate the days in a Hebrew month (in various types of years) are correct.
 */
class UT_DaysInJewishMonth {

    @Test
    fun daysInMonthsInHaserYear() {
        assertHaser(5773)
        assertHaser(5777)
        assertHaser(5781)
        assertHaserLeap(5784)
        assertHaserLeap(5790)
        assertHaserLeap(5793)
    }

    @Test
    fun daysInMonthsInQesidrahYear() {
        assertQesidrah(5769)
        assertQesidrah(5772)
        assertQesidrah(5778)
        assertQesidrah(5786)
        assertQesidrah(5789)
        assertQesidrah(5792)
        assertQesidrahLeap(5782)
    }

    @Test
    fun daysInMonthsInShalemYear() {
        assertShalem(5770)
        assertShalem(5780)
        assertShalem(5783)
        assertShalem(5785)
        assertShalem(5788)
        assertShalem(5791)
        assertShalem(5794)
        assertShalemLeap(5771)
        assertShalemLeap(5774)
        assertShalemLeap(5776)
        assertShalemLeap(5779)
        assertShalemLeap(5787)
        assertShalemLeap(5795)
    }

    private fun assertHaser(year: Int) {
        val jewishDate = JewishDate(year, 1, 1)
        assertEquals(year, jewishDate.hebrewLocalDate.year.toInt())
        assertFalse(jewishDate.isCheshvanLong)
        assertTrue(jewishDate.isKislevShort)
    }

    private fun assertHaserLeap(year: Int) {
        val jewishDate = JewishDate(year, 1, 1)
        assertEquals(year, jewishDate.hebrewLocalDate.year.toInt())
        assertHaser(year)
        assertTrue(jewishDate.isJewishLeapYear)
    }

    private fun assertQesidrah(year: Int) {
        val jewishDate = JewishDate(year, 1, 1)
        assertEquals(year, jewishDate.hebrewLocalDate.year.toInt())
        assertFalse(jewishDate.isCheshvanLong)
        assertFalse(jewishDate.isKislevShort)
    }

    private fun assertQesidrahLeap(year: Int) {
        val jewishDate = JewishDate(year, 1, 1)
        assertEquals(year, jewishDate.hebrewLocalDate.year.toInt())
        assertQesidrah(year)
        assertTrue(jewishDate.isJewishLeapYear)
    }

    private fun assertShalem(year: Int) {
        val jewishDate = JewishDate(year, 1, 1)
        assertEquals(year, jewishDate.hebrewLocalDate.year.toInt())
        assertTrue(jewishDate.isCheshvanLong)
        assertFalse(jewishDate.isKislevShort)
    }

    private fun assertShalemLeap(year: Int) {
        val jewishDate = JewishDate(year, 1, 1)
        assertEquals(year, jewishDate.hebrewLocalDate.year.toInt())
        assertShalem(year)
        assertTrue(jewishDate.isJewishLeapYear)
    }
} // End of UT_DaysInJewishMonth class
