package com.kosherjava.zmanim

import com.kosherjava.zmanim.hebrewcalendar.HebrewLocalDate.Companion.toHebrewDate
import com.kosherjava.zmanim.hebrewcalendar.JewishCalendar
import com.kosherjava.zmanim.util.DateUtils.now
//import com.kosherjava.zmanim.util.DateUtils.toDate
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toJavaInstant
import java.util.*

fun main() {
    println("Calendar: ${Calendar.getInstance()}")
    println(com.kosherjava.zmanim.java.zmanim.hebrewcalendar.JewishCalendar().sofZmanKidushLevana15Days.time)
//    println(JewishCalendar().sofZmanKidushLevana15Days.toDate()!!.time)

}