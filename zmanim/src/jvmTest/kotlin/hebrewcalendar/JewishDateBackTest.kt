package hebrewcalendar

import io.github.kdroidfilter.kosherkotlin.hebrewcalendar.HebrewMonth
import io.github.kdroidfilter.kosherkotlin.hebrewcalendar.JewishDate
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.minus
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Stepping back off the first of a month has to land on the last day of the month before, whose
 * length is its own — not the one being left.
 */
class JewishDateBackTest {

    @Test
    fun backFromTheFirstOfALongMonthLandsOnAShortOne() {
        // Av has 30 days, Tammuz 29: the pair that used to ask for a 30 Tammuz.
        val date = JewishDate(5786L, HebrewMonth.AV, 1).back()

        assertEquals(HebrewMonth.TAMMUZ, date.hebrewLocalDate.month)
        assertEquals(29, date.hebrewLocalDate.dayOfMonth)
    }

    @Test
    fun everyFirstOfTheMonthStepsBackOneDay() {
        // Walking a leap year and a plain one covers Adar I/II, short Cheshvan and short Kislev.
        for (year in 5784L..5790L) {
            val date = JewishDate(year, HebrewMonth.TISHREI, 1)
            repeat(400) {
                val before = date.gregorianLocalDate
                date.back()
                assertEquals(before.minus(1, DateTimeUnit.DAY), date.gregorianLocalDate)
            }
        }
    }
}
