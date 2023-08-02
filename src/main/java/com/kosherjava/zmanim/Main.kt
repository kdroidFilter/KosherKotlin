package com.kosherjava.zmanim

//import com.kosherjava.zmanim.util.DateUtils.toDate
import java.util.*
import kotlin.time.Duration.Companion.milliseconds

fun main() {
    println((1691082188516 - 1691038990714).milliseconds.inWholeHours)
    println("Calendar: ${Calendar.getInstance()}")
    println(com.kosherjava.zmanim.java.zmanim.hebrewcalendar.JewishCalendar().sofZmanKidushLevana15Days.time)
//    println(JewishCalendar().sofZmanKidushLevana15Days.toDate()!!.time)

}