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

    @Test
    fun hallelOnTheSeventhIsOnlyForShavuosOutsideIsrael() {
        val abroad = { month: HebrewMonth, d: Int -> day(month, d).apply { inIsrael = false } }
        assertFalse(rules.isHallelRecited(abroad(HebrewMonth.CHESHVAN, 7)))
        assertFalse(rules.isHallelShalemRecited(abroad(HebrewMonth.CHESHVAN, 7)))
        assertTrue(rules.isHallelRecited(abroad(HebrewMonth.SIVAN, 7)))
        assertFalse(rules.isHallelRecited(day(HebrewMonth.SIVAN, 7).apply { inIsrael = true }))
    }

    @Test
    fun tachanunIsNotRecitedOnErevRoshHashana() {
        // 28-29 Elul 5784 are a Tuesday and a Wednesday
        assertFalse(rules.isTachanunRecitedShacharis(JewishCalendar(5784, HebrewMonth.ELUL, 29)))
        assertTrue(rules.isTachanunRecitedShacharis(JewishCalendar(5784, HebrewMonth.ELUL, 28)))
        assertTrue(rules.isTachanunRecitedMincha(JewishCalendar(5784, HebrewMonth.ELUL, 28)))
    }
}
