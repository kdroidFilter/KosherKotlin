/*
 * Copyright (c) 2011. Jay R. Gindin
 */
package com.kosherjava.zmanim.hebrewcalendar

import org.junit.Assert
import org.junit.Before
import org.junit.Test

/**
 *
 */
class UT_JewishDateNavigation {
    lateinit var jewishDate: JewishDate

    @Before
    fun setupProps() {
        jewishDate = JewishDate()
    }

    @Test
    fun jewishForwardMonthToMonth() {
        jewishDate.setJewishDate(5771, 1, 1)
        Assert.assertEquals(5, jewishDate.gregorianDayOfMonth)
        Assert.assertEquals(3, jewishDate.gregorianMonth)
        Assert.assertEquals(2011, jewishDate.gregorianYear)
    }

    @Test
    fun computeRoshHashana5771() {

        // At one point, this test was failing as the JewishDate class spun through a never-ending loop...
        jewishDate.setJewishDate(5771, 7, 1)
        Assert.assertEquals(9, jewishDate.gregorianDayOfMonth)
        Assert.assertEquals(8, jewishDate.gregorianMonth)
        Assert.assertEquals(2010, jewishDate.gregorianYear)
    }
} // End of UT_JewishDateNavigation class
