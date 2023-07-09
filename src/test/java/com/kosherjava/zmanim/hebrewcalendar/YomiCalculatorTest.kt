package com.kosherjava.zmanim.hebrewcalendar

import org.junit.Assert
import org.junit.Test

class YomiCalculatorTest {
    private val calc = YomiCalculator
    @Test
    fun testCorrectDaf1() {
        val jewishCalendar = JewishCalendar(5685, JewishDate.KISLEV, 12)
        val daf = calc.getDafYomiBavli(jewishCalendar)
        Assert.assertEquals(5, daf!!.masechtaNumber.toLong())
        Assert.assertEquals(2, daf.daf.toLong())
        println(hdf.formatDafYomiBavli(jewishCalendar.dafYomiBavli!!))
    }

    @Test
    fun testCorrectDaf2() {
        val jewishCalendar = JewishCalendar(5736, JewishDate.ELUL, 26)
        val daf = calc.getDafYomiBavli(jewishCalendar)
        Assert.assertEquals(4, daf!!.masechtaNumber.toLong())
        Assert.assertEquals(14, daf.daf.toLong())
        println(hdf.formatDafYomiBavli(jewishCalendar.dafYomiBavli!!))
    }

    @Test
    fun testCorrectDaf3() {
        val jewishCalendar = JewishCalendar(5777, JewishDate.ELUL, 10)
        val daf = calc.getDafYomiBavli(jewishCalendar)
        Assert.assertEquals(23, daf!!.masechtaNumber.toLong())
        Assert.assertEquals(47, daf.daf.toLong())
        println(hdf.formatDafYomiBavli(jewishCalendar.dafYomiBavli!!))
    }

    companion object {
        private val hdf = HebrewDateFormatter()

        init {
            hdf.isHebrewFormat = true
        }
    }
}