package com.kosherjava.zmanim

import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.milliseconds

fun main(args: Array<String>) {
    val zmanim = ComplexZmanimCalendar()
//    println("TimeZone.UTC.id = ${TimeZone.UTC.id}")
//    println(zmanim.minchaKetanaGRAFixedLocalChatzosToSunset.rules.mainCalculationMethodUsed?.format())
//    println(zmanim.minchaKetanaGRAFixedLocalChatzosToSunset.rules.mainCalculationMethodUsed?.valueToString())
    val formatter = ZmanDescriptionFormatter()
    val includeElevationDescription = false
    val days = Int.MAX_VALUE * 1000L * 60L * 60L * 24L
    println(days.milliseconds.inWholeNanoseconds)
    println(Long.MAX_VALUE)

//    println(days.inWholeNanoseconds)
    listOf(
        zmanim.allShaosZmaniyos,
        zmanim.allZmanim
    ).flatten()
        .apply {
            println("Short descriptions:")
            forEach {
                println()
                println(formatter.formatShortDescription(it, includeElevationDescription))
            }

            println("Long descriptions:")
            forEach {
                println()
                println(formatter.formatLongDescription(it))
            }
//    println("Shaos zmaniyos:")
//    zmanim.allShaosZmaniyos.mapIndexed { index, it -> "$index(${it.rules.type}): " + formatter.formatShortDescription(it, includeElevationDescription) }.forEach { println(it) }
//    println("Zmanim:")
//    zmanim.allZmanim.mapIndexed { index, it -> "$index(${it.rules.type}): " + runCatching{ formatter.formatShortDescription(it, includeElevationDescription) }.getOrNull() }.forEach { println(it) }
//    println()
//    zmanim.allZmanim.mapIndexed { index, it -> "$index(${it.rules.type}) - ${it.rules}: " + runCatching{ formatter.formatShortDescription(it, includeElevationDescription) }.getOrNull() }.forEach { println(it) }
        }
}