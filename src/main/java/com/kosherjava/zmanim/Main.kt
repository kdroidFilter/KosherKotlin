package com.kosherjava.zmanim

import kotlinx.datetime.TimeZone

fun main(args: Array<String>) {
    val zmanim = ComplexZmanimCalendar()
//    println("TimeZone.UTC.id = ${TimeZone.UTC.id}")
    println(zmanim.minchaKetanaGRAFixedLocalChatzosToSunset.rules.mainCalculationMethodUsed?.format())
    println(zmanim.minchaKetanaGRAFixedLocalChatzosToSunset.rules.mainCalculationMethodUsed?.valueToString())
    val formatter = ZmanDescriptionFormatter()
    val includeElevationDescription = false
    listOf(zmanim.allShaosZmaniyos,
            zmanim.allZmanim).flatten().filter { it.rules.mainCalculationMethodUsed !is ZmanCalculationMethod.DayDefinition }.forEach {
        println(it)
    }
//    println("Shaos zmaniyos:")
//    zmanim.allShaosZmaniyos.mapIndexed { index, it -> "$index(${it.rules.type}): " + formatter.formatShortDescription(it, includeElevationDescription) }.forEach { println(it) }
//    println("Zmanim:")
//    zmanim.allZmanim.mapIndexed { index, it -> "$index(${it.rules.type}): " + runCatching{ formatter.formatShortDescription(it, includeElevationDescription) }.getOrNull() }.forEach { println(it) }
//    println()
//    zmanim.allZmanim.mapIndexed { index, it -> "$index(${it.rules.type}) - ${it.rules}: " + runCatching{ formatter.formatShortDescription(it, includeElevationDescription) }.getOrNull() }.forEach { println(it) }
}