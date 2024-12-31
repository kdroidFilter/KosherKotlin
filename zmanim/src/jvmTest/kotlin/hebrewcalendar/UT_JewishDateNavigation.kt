/*
 * Copyright (c) 2011. Jay R. Gindin
 */
package hebrewcalendar

import org.junit.Assert
import org.junit.Test
import com.kdroid.kosherkotlin.hebrewcalendar.HebrewMonth
import com.kdroid.kosherkotlin.hebrewcalendar.JewishDate

/**
 *
 */
class UT_JewishDateNavigation {

    @Test
    fun jewishForwardMonthToMonth() {
        val jewishDate = JewishDate(5771, HebrewMonth.getMonthForValue(1), 1)
        Assert.assertEquals(5, jewishDate.gregorianLocalDate.dayOfMonth)
        Assert.assertEquals(3, jewishDate.gregorianLocalDate.monthNumber - 1)
        Assert.assertEquals(2011, jewishDate.gregorianLocalDate.year)
    }

    @Test
    fun computeRoshHashana5771() {

        // At one point, this test was failing as the JewishDate class spun through a never-ending loop...
        val jewishDate = JewishDate(5771, HebrewMonth.getMonthForValue(7), 1)
        Assert.assertEquals(9, jewishDate.gregorianLocalDate.dayOfMonth)
        Assert.assertEquals(8, jewishDate.gregorianLocalDate.monthNumber - 1)
        Assert.assertEquals(2010, jewishDate.gregorianLocalDate.year)
    }
} // End of UT_JewishDateNavigation class
