package hebrewcalendar

import io.github.kdroidfilter.kosherkotlin.hebrewcalendar.JewishCalendar
import io.github.kdroidfilter.kosherkotlin.hebrewcalendar.JewishCalendar.Parsha
import kotlinx.datetime.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test

class NissanParshaTest {
    private fun parsha(date: String, inIsrael: Boolean = false) = JewishCalendar(LocalDate.parse(date), inIsrael).parshah

    @Test
    fun nissanCountsFromRoshHashana() {
        // Used to add Nissan and Iyar again: NISSAN until NISSAN ran to IYAR
        assertEquals(223, JewishCalendar(LocalDate.parse("2019-04-20")).daysSinceStartOfJewishYear)
        assertEquals(1, JewishCalendar(LocalDate.parse("2024-10-03")).daysSinceStartOfJewishYear)
    }

    @Test
    fun theParshiyosOfNissan() {
        // Shabbat HaGadol 5785
        assertEquals(Parsha.TZAV, parsha("2025-04-12"))
        // Pesach's eighth day abroad, אחרי מות in Israel
        assertEquals(Parsha.NONE, parsha("2019-04-27"))
        assertEquals(Parsha.ACHREI_MOS, parsha("2019-04-27", inIsrael = true))
        assertEquals(Parsha.ACHREI_MOS, parsha("2019-05-04"))
        assertEquals(Parsha.KEDOSHIM, parsha("2019-05-04", inIsrael = true))
    }
}
