package sternbach.software.kosherkotlin.util

import kotlinx.datetime.LocalDate

object WeekFormat {
    fun interface Formatter {
        fun format(input: LocalDate): String
    }
    val long: Formatter = Formatter { it.dayName }
    val short: Formatter = Formatter { it.dayName.take(3) }

    val LocalDate.dayName get() = dayOfWeek.name
}