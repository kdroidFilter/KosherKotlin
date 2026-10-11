package hebrewcalendar

import io.github.kdroidfilter.kosherkotlin.hebrewcalendar.HebrewDateFormatter
import io.github.kdroidfilter.kosherkotlin.hebrewcalendar.HebrewMonth
import io.github.kdroidfilter.kosherkotlin.hebrewcalendar.JewishCalendar
import org.junit.Assert
import org.junit.Test

class RoshChodeshFormatTest {
    private val hdf = HebrewDateFormatter().apply { isHebrewFormat = true }

    private fun format(year: Long, month: HebrewMonth, day: Int) =
        hdf.formatRoshChodesh(JewishCalendar(year, month, day))

    @Test
    fun thirtiethNamesTheComingMonth() {
        // 30 Tishrei 5787 is the first day of Rosh Chodesh Cheshvan
        Assert.assertEquals("ראש חודש חשון", format(5787, HebrewMonth.TISHREI, 30))
        Assert.assertEquals("ראש חודש חשון", format(5787, HebrewMonth.CHESHVAN, 1))
    }

    @Test
    fun thirtiethOfShevatNamesAdarOrAdarI() {
        // 5786 is a plain year, 5787 a leap year
        Assert.assertEquals("ראש חודש אדר", format(5786, HebrewMonth.SHEVAT, 30))
        Assert.assertEquals("ראש חודש אדר א׳", format(5787, HebrewMonth.SHEVAT, 30))
    }

    @Test
    fun thirtiethOfAdarINamesAdarII() {
        Assert.assertEquals("ראש חודש אדר ב׳", format(5787, HebrewMonth.ADAR, 30))
    }

    @Test
    fun doesNotMutateTheInput() {
        val calendar = JewishCalendar(5787, HebrewMonth.TISHREI, 30)
        hdf.formatRoshChodesh(calendar)
        Assert.assertEquals(HebrewMonth.TISHREI, calendar.hebrewLocalDate.month)
        Assert.assertEquals(30, calendar.jewishDayOfMonth)
    }
}
