package hebrewcalendar

import org.junit.Assert
import org.junit.Test
import io.github.kdroidfilter.kosherkotlin.hebrewcalendar.HebrewDateFormatter
import io.github.kdroidfilter.kosherkotlin.hebrewcalendar.HebrewMonth
import io.github.kdroidfilter.kosherkotlin.hebrewcalendar.JewishCalendar
import io.github.kdroidfilter.kosherkotlin.hebrewcalendar.YomiCalculator

class YomiCalculatorTest {
    private val calc = YomiCalculator
    @Test
    fun testCorrectDaf1() {
        val jewishCalendar = JewishCalendar(5685, io.github.kdroidfilter.kosherkotlin.hebrewcalendar.HebrewMonth.KISLEV, 12)
        val daf = calc.getDafYomiBavli(jewishCalendar)
        Assert.assertEquals(5, daf!!.masechtaNumber)
        Assert.assertEquals(2, daf.daf)
        println(hdf.formatDafYomiBavli(jewishCalendar.dafYomiBavli!!))
    }

    @Test
    fun testCorrectDaf2() {
        val jewishCalendar = JewishCalendar(5736, io.github.kdroidfilter.kosherkotlin.hebrewcalendar.HebrewMonth.ELUL, 26)
        val daf = calc.getDafYomiBavli(jewishCalendar)
        Assert.assertEquals(4, daf!!.masechtaNumber)
        Assert.assertEquals(14, daf.daf)
        println(hdf.formatDafYomiBavli(jewishCalendar.dafYomiBavli!!))
    }

    @Test
    fun testCorrectDaf3() {
        val jewishCalendar = JewishCalendar(5777, io.github.kdroidfilter.kosherkotlin.hebrewcalendar.HebrewMonth.ELUL, 10)
        val daf = calc.getDafYomiBavli(jewishCalendar)
        Assert.assertEquals(23, daf!!.masechtaNumber)
        Assert.assertEquals(47, daf.daf)
        println(hdf.formatDafYomiBavli(jewishCalendar.dafYomiBavli!!))
    }

    companion object {
        private val hdf = HebrewDateFormatter()

        init {
            hdf.isHebrewFormat = true
        }
    }
}