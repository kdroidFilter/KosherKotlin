package com.kosherjava.zmanim

import com.kosherjava.zmanim.metadata.ZmanCalculationMethod

fun main(args: Array<String>) {
    val zmanim = ComplexZmanimCalendar()
//    println("TimeZone.UTC.id = ${TimeZone.UTC.id}")
//    println(zmanim.minchaKetanaGRAFixedLocalChatzosToSunset.rules.mainCalculationMethodUsed?.format())
//    println(zmanim.minchaKetanaGRAFixedLocalChatzosToSunset.rules.mainCalculationMethodUsed?.valueToString())
    val formatter = ZmanDescriptionFormatter()
    val includeElevationDescription = false
    val allZmanim = zmanim.allZmanim
    listOf(
        zmanim.allShaosZmaniyos,
        allZmanim
    ).flatten()
        .filter {
            val fromZman = (it.rules.mainCalculationMethodUsed as? ZmanCalculationMethod.FixedDuration)?.fromZman
            it.rules.mainCalculationMethodUsed is ZmanCalculationMethod.FixedDuration &&
                    fromZman == null
        }
        .forEach {
            println("Index in allZmanim = ${allZmanim.indexOf(it)}")
        }
//    println("Shaos zmaniyos:")
//    zmanim.allShaosZmaniyos.mapIndexed { index, it -> "$index(${it.rules.type}): " + formatter.formatShortDescription(it, includeElevationDescription) }.forEach { println(it) }
//    println("Zmanim:")
//    zmanim.allZmanim.mapIndexed { index, it -> "$index(${it.rules.type}): " + runCatching{ formatter.formatShortDescription(it, includeElevationDescription) }.getOrNull() }.forEach { println(it) }
//    println()
//    zmanim.allZmanim.mapIndexed { index, it -> "$index(${it.rules.type}) - ${it.rules}: " + runCatching{ formatter.formatShortDescription(it, includeElevationDescription) }.getOrNull() }.forEach { println(it) }
}