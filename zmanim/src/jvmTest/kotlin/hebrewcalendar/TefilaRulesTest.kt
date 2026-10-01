package hebrewcalendar

import io.github.kdroidfilter.kosherkotlin.hebrewcalendar.HebrewMonth
import io.github.kdroidfilter.kosherkotlin.hebrewcalendar.JewishCalendar
import io.github.kdroidfilter.kosherkotlin.hebrewcalendar.TefilaRules
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TefilaRulesTest {
    private val rules = TefilaRules()

    private fun day(month: HebrewMonth, dayOfMonth: Int) = JewishCalendar(5787, month, dayOfMonth)

    @Test
    fun mashivHaruachIsRecitedFromAfterSheminiAtzeresToBeforePesach() {
        // Used to throw: the bounds were built with the month number as the year
        assertFalse(rules.isMashivHaruachRecited(day(HebrewMonth.TISHREI, 20)))
        assertFalse(rules.isMashivHaruachRecited(day(HebrewMonth.TISHREI, 22)))
        assertTrue(rules.isMashivHaruachRecited(day(HebrewMonth.TISHREI, 23)))
        assertTrue(rules.isMashivHaruachRecited(day(HebrewMonth.SHEVAT, 1)))
        assertTrue(rules.isMashivHaruachRecited(day(HebrewMonth.NISSAN, 14)))
        assertFalse(rules.isMashivHaruachRecited(day(HebrewMonth.NISSAN, 15)))
        assertFalse(rules.isMashivHaruachRecited(day(HebrewMonth.TAMMUZ, 1)))
    }

    @Test
    fun moridHatalIsRecitedInTheSummerAndOnBothChangeDays() {
        assertTrue(rules.isMoridHatalRecited(day(HebrewMonth.TAMMUZ, 1)))
        assertTrue(rules.isMoridHatalRecited(day(HebrewMonth.TISHREI, 22)))
        assertTrue(rules.isMoridHatalRecited(day(HebrewMonth.NISSAN, 15)))
        assertFalse(rules.isMoridHatalRecited(day(HebrewMonth.SHEVAT, 1)))
    }
}
