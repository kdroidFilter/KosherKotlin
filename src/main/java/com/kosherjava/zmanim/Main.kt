package com.kosherjava.zmanim

import kotlinx.datetime.TimeZone

fun main(args: Array<String>) {
    val zmanim = ComplexZmanimCalendar()
    println("TimeZone.UTC.id = ${TimeZone.UTC.id}")
    println(zmanim.minchaKetanaGRAFixedLocalChatzosToSunset.rules.mainCalculationMethodUsed?.format())
    println(zmanim.minchaKetanaGRAFixedLocalChatzosToSunset.rules.mainCalculationMethodUsed?.valueToString())
    val formatter = ZmanDescriptionFormatter()
    val includeElevationDescription = false
    println("Shaos zmaniyos:")
    zmanim.allShaosZmaniyos.mapIndexed { index, it -> "$index(${it.type}): " + formatter.formatShortDescription(it, includeElevationDescription) }.forEach { println(it) }
    println("Zmanim:")
    zmanim.allZmanim.mapIndexed { index, it -> "$index(${it.type}): " + runCatching{ formatter.formatShortDescription(it, includeElevationDescription) }.getOrNull() }.forEach { println(it) }
    println()
    zmanim.allZmanim.mapIndexed { index, it -> "$index(${it.type}) - ${it.rules}: " + runCatching{ formatter.formatShortDescription(it, includeElevationDescription) }.getOrNull() }.forEach { println(it) }
}