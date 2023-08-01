package com.kosherjava.zmanim

import com.kosherjava.zmanim.hebrewcalendar.HebrewLocalDate.Companion.toHebrewDate
import com.kosherjava.zmanim.util.DateUtils.now
import kotlinx.datetime.LocalDate

fun main() {
    println(LocalDate(-1,1,1).toHebrewDate()) // HebrewLocalDate(year=3760, month=SHEVAT, dayOfMonth=8)
    println(LocalDate(0,1,1).toHebrewDate()) // HebrewLocalDate(year=3761, month=TEVES, dayOfMonth=17)
    println(LocalDate(1,1,1).toHebrewDate()) // HebrewLocalDate(year=3762, month=SHEVAT, dayOfMonth=1)
    val now = LocalDate.now()
    println(now) // 2023-08-01
    println(now.toHebrewDate()) // HebrewLocalDate(year=5783, month=AV, dayOfMonth=14)
}