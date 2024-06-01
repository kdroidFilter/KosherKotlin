package sternbach.software.kosherkotlin

import sternbach.software.kosherkotlin.metadata.UsesElevation
import sternbach.software.kosherkotlin.metadata.ZmanAuthority
import sternbach.software.kosherkotlin.metadata.ZmanCalculationMethod
import sternbach.software.kosherkotlin.metadata.ZmanDefinition
import sternbach.software.kosherkotlin.metadata.ZmanType

class ZmanDescriptionFormatter {
    fun formatShortDescription(zman: Zman<*>, includeElevationDescription: Boolean): String {
        val definition = zman.definition
        val result = StringBuilder(getShortCalculationDescription(definition))
//        "Alos-Tzais - 19.8˚ - affected by elevation"
//        addShortDayDefinition(definition.calculationMethod as? ZmanCalculationMethod.DayDefinition, result, rules, includeElevationDescription)
        /*if (definition.zmanToCalcMethodUsed == null && definition.mainCalculationMethodUsed == null) {
            addShortDayDefinition(definition.definitionOfDayUsed!!, result, definition)
        } else if (definition.mainCalculationMethodUsed != null && definition.zmanToCalcMethodUsed == null) {
            if (definition.definitionOfDayUsed != null) {
                addShortDayDefinition(definition.definitionOfDayUsed, result, definition)
            }
        }*/
        return result.toString()
    }
    fun formatShortDescription(zman: ZmanDefinition) = getShortCalculationDescription(zman)

    private fun addShortDayDefinition(
        mainCalculationMethodUsed: ZmanCalculationMethod.DayDefinition?,
        result: StringBuilder,
        definition: ZmanDefinition,
        includeElevationDescription: Boolean
    ) {
        /*
        *
        *
        val shaahZmanisAteretTorah: Zman.ValueBased
        get() = Zman.ValueBased(
            ZmanType.SHAA_ZMANIS,
            getTemporalHour(alos72Zmanis.momentOfOccurrence, tzaisAteretTorah.momentOfOccurrence).milliseconds,
            ZmanDefinition(
                ZmanAuthority.AteretTorah,
                mapOf(
                    ZmanType.ALOS to ZmanCalculationMethod.ZmaniyosDuration._72,
                    ZmanType.TZAIS to ZmanAuthority.AteretTorah,
                ),
                UsesElevation.IF_SET
            )
        )
    val shaahZmanisAlos16Point1ToTzais3Point8: Zman.ValueBased
        get() = Zman.ValueBased(
            ZmanType.SHAA_ZMANIS,
            getTemporalHour(
                alos16Point1Degrees.momentOfOccurrence,
                tzaisGeonim3Point8Degrees.momentOfOccurrence
            ).milliseconds,
            ZmanDefinition(
                ZmanAuthority.AHAVAT_SHALOM,
                mapOf(
                    ZmanType.ALOS to 16.1F.degrees,
                    ZmanType.TZAIS to 3.8F.degrees,
                ),
                UsesElevation.ALWAYS
            )
        )
        * */
        val startZman = mainCalculationMethodUsed?.dayStart?.type
        val endZman = mainCalculationMethodUsed?.dayEnd?.type
        val mainCalculationMethod =
            {
                if (definition.calculationMethod != null) "${if (result.isNotBlank()) " - " else ""}${definition.calculationMethod.valueToString()}"
                else null
            }

        result.apply {
            if (startZman != null && endZman != null) {
                append(startZman.friendlyNameEnglish)
                val startCalcMethod = mainCalculationMethodUsed.dayStart.calculationMethod
                if (startCalcMethod != null && startCalcMethod != ZmanCalculationMethod.Unspecified) {
                    append("(")
                    append(startCalcMethod.valueToString())
                    append(")")
                }
                append("-")
                append(endZman.friendlyNameEnglish)
                val endCalcMethod = mainCalculationMethodUsed.dayEnd.calculationMethod
                if (endCalcMethod != null && endCalcMethod != ZmanCalculationMethod.Unspecified) {
                    append("(")
                    append(endCalcMethod.valueToString())
                    append(")")
                }
            }
            val string = mainCalculationMethod()
            if (string != null) append(string)
            if (includeElevationDescription) {
                append(" - ")
                append(
                    when (definition.isElevationUsed) {
                        UsesElevation.IF_SET -> "affected by elevation if set"
                        UsesElevation.NEVER -> "unaffected by elevation"
                        UsesElevation.ALWAYS -> "affected by elevation"
                        UsesElevation.UNSPECIFIED -> "affect by elevation unspecified"
                    }
                )
            }
        }
    }

    fun formatLongDescription(zman: Zman<*>): String = formatLongDescription(zman.definition)
    fun formatLongDescription(zman: ZmanDefinition): String {
        val result = StringBuilder()
        result.append(getLongCalculationDescription(zman))
        if (zman.calculationMethod is ZmanCalculationMethod.DayDefinition) {
            /*val startZman = zman.calculationMethod.dayStart.type
            val endZman = zman.calculationMethod.dayEnd.type
            result.appendLine("Day starts at ${startZman?.friendlyNameEnglish} and ends at ${endZman?.friendlyNameEnglish}")
            result.appendLine("${startZman?.friendlyNameEnglish} is defined as")
            if (zman.calculationMethod.dayStart.calculationMethod != null) {
                result.append(zman.calculationMethod.dayStart.calculationMethod.format())
            } else {
                result.append(
                    zman.calculationMethod.format(
                        startZman.friendlyNameEnglish,
                        endZman.friendlyNameEnglish
                    )
                )
            }*/
            /*
            getTemporalHour(
                alos19Point8Degrees.momentOfOccurrence,
                tzais19Point8Degrees.momentOfOccurrence
            ).milliseconds,
            ZmanDefinition(
                19.8F.degrees,
                null,
                UsesElevation.ALWAYS,
                ZmanDefinition.DayDefinition.DAWN_TO_DUSK,
            )
        ) //Long description:
          // "Day is defined as the time between Alos and Tzais.
          // "Alos is defined as when the sun is 19.8˚ below the eastern geometric horizon and Tzais when it is 19.8˚ below the western geometric horizon."
          //Short description:
          // "Alos-Tzais - 19.8˚ - affected by elevation"
        */
//            result.append(zman.calculationMethod.format())
        }/* else {
            zman as Zman.ValueBased

            result.appendLine("Day is defined as when the sun is ")
        }*/
        if(zman.supportingAuthorities.isNotEmpty()) {
            result.append(" Supporting authorities: ${zman.supportingAuthorities.joinToString(", ", postfix = ".") { it.name } }")
        }
        when (zman.isElevationUsed) {
            UsesElevation.IF_SET -> result.append(" This zman is affected by elevation if set.")
            UsesElevation.NEVER -> result.append(" This zman is unaffected by elevation.")
            UsesElevation.ALWAYS -> result.append(" This zman is affected by elevation.")
            UsesElevation.UNSPECIFIED -> {}
        }
        return result.toString()
    }

    private fun getLongCalculationDescription(method: ZmanDefinition): String =
        when (method.calculationMethod) {
            is ZmanCalculationMethod.Degrees -> dayStartEndString(method.calculationMethod.degrees, method.type in hashSetOf(ZmanType.ALOS, ZmanType.HANAITZ, ZmanType.SHAA_ZMANIS), method.type in hashSetOf(ZmanType.TZAIS, ZmanType.SHKIAH, ZmanType.SHAA_ZMANIS))
            is ZmanAuthority -> "${method.type.friendlyNameEnglish} as calculated according to ${method.calculationMethod.name}."
            is ZmanCalculationMethod.DayDefinition -> {
                val shaosZmaniyos = ZmanType.shaosZmaniyosIntoDay[method.type]
                var dayDefString = "${method.type.friendlyNameEnglish} as calculated based on the definition of when day starts and ends. Day starts at ${method.calculationMethod.dayStart.type.friendlyNameEnglish} and ends at ${method.calculationMethod.dayEnd.type.friendlyNameEnglish}."
                dayDefString += if(method.calculationMethod.dayStart.calculationMethod != method.calculationMethod.dayEnd.calculationMethod) {
                    " ${method.calculationMethod.dayStart.type.friendlyNameEnglish} is defined as \"${getLongCalculationDescription(method.calculationMethod.dayStart)}\"; and ${method.calculationMethod.dayEnd.type.friendlyNameEnglish} is defined as \"${getLongCalculationDescription(method.calculationMethod.dayEnd)}\"."
                } else " ${method.calculationMethod.dayStart.type.friendlyNameEnglish} and ${method.calculationMethod.dayEnd.type.friendlyNameEnglish} are defined as follows: \"${getLongCalculationDescription(method.calculationMethod.dayStart)}\"."
                if(shaosZmaniyos != null)
                    dayDefString += "${method.type.friendlyNameEnglish} is $shaosZmaniyos Shao'os Zmaniyos (halachic hours) into the day. A Shaa Zmanis is 1/12th of the day, using the previously mentioned definition of day."
                ;dayDefString
            }
            is ZmanCalculationMethod.LaterOf -> "${method.type.friendlyNameEnglish} occurs at the later of either of the following zmanim: ${getLongCalculationDescription(method.calculationMethod.zman1)}; or ${getLongCalculationDescription(method.calculationMethod.zman2)}."
            is ZmanCalculationMethod.Relationship -> "${method.type.friendlyNameEnglish} is calculated as follows: ${method.calculationMethod.relationship.subject.friendlyNameEnglish} occurs ${method.calculationMethod.relationship.calculation.valueToString()} ${if(method.calculationMethod.relationship.calculation.let { it is ZmanCalculationMethod.FixedDuration && it.duration.isNegative() || it is ZmanCalculationMethod.ZmaniyosDuration && it.duration.isNegative()}) "before" else "after"} ${method.calculationMethod.relationship.relativeToZmanType?.friendlyNameEnglish ?: getLongCalculationDescription(method.calculationMethod.relationship.relativeToZman!!)}."
            ZmanCalculationMethod.FixedLocalChatzos -> {
                val shaosZmaniyos = ZmanType.shaosZmaniyosIntoDay[method.type]
                var dayDefString = "${method.type.friendlyNameEnglish} is calculated based on the opinion of R' Moshe Feinstein that chatzos is calculated using what is known as \"Fixed Local Chatzos\". The 360˚ of the globe divided by 24 (hours) calculates to 15˚ per hour with 4 minutes per degree, so at a longitude of 0˚, 15˚, 30˚, etc., Chatzos is at exactly 12:00 noon."
                if(shaosZmaniyos != null)
                    dayDefString += "${method.type.friendlyNameEnglish} is $shaosZmaniyos Shao'os Zmaniyos (halachic hours) into the day. A Shaa Zmanis is 1/12th of the day, using the previously mentioned definition of day."
                ;dayDefString

            }
            ZmanCalculationMethod.Unspecified -> "The calculation method for ${method.type.friendlyNameEnglish} is unspecified."

            //never happens
            is ZmanCalculationMethod.FixedDuration -> method.calculationMethod.valueToString()
            is ZmanCalculationMethod.ZmaniyosDuration -> method.calculationMethod.valueToString()
            is ZmanCalculationMethod.FixedDuration.AteretTorah -> method.calculationMethod.shortDescription()
        }

    private fun dayStartEndString(degrees: Float, mentionStart: Boolean = false, mentionEnd: Boolean = false) =
        when {
            mentionStart && mentionEnd -> "Day starts when the sun is $degrees˚ below the eastern geometric horizon and ends when it is $degrees˚ below the western geometric horizon."
            mentionStart && !mentionEnd -> "Day starts when the sun is $degrees˚ below the eastern geometric horizon."
            else -> "Day ends when it is $degrees˚ below the western geometric horizon."
        }

    private fun getShortCalculationDescription(method: ZmanDefinition): String =
        when (method.calculationMethod) {
            is ZmanCalculationMethod.Degrees -> method.calculationMethod.valueToString()
            is ZmanAuthority -> method.calculationMethod.valueToString()
            is ZmanCalculationMethod.DayDefinition -> {
                if(method.calculationMethod.dayStart.calculationMethod != method.calculationMethod.dayEnd.calculationMethod) {
                    "${method.calculationMethod.dayStart.type.friendlyNameEnglish} (${getShortCalculationDescription(method.calculationMethod.dayStart)}) - ${method.calculationMethod.dayEnd.type.friendlyNameEnglish} (${getShortCalculationDescription(method.calculationMethod.dayEnd)})."
                } else "${method.calculationMethod.dayStart.type.friendlyNameEnglish} - ${method.calculationMethod.dayEnd.type.friendlyNameEnglish} (${getShortCalculationDescription(method.calculationMethod.dayStart)})."
            }
            is ZmanCalculationMethod.LaterOf -> "Later of: ${getShortCalculationDescription(method.calculationMethod.zman1)} or ${getShortCalculationDescription(method.calculationMethod.zman2)}."
            is ZmanCalculationMethod.Relationship -> "${method.calculationMethod.relationship.subject.friendlyNameEnglish} is ${method.calculationMethod.relationship.calculation.valueToString()} ${if(method.calculationMethod.relationship.calculation.let { it is ZmanCalculationMethod.FixedDuration && it.duration.isNegative() || it is ZmanCalculationMethod.ZmaniyosDuration && it.duration.isNegative() }) "before" else "after"} ${method.calculationMethod.relationship.relativeToZmanType?.friendlyNameEnglish ?: getShortCalculationDescription(method.calculationMethod.relationship.relativeToZman!!)}."
            ZmanCalculationMethod.FixedLocalChatzos -> "Fixed Local Chatzos"

            //never happens
            ZmanCalculationMethod.Unspecified -> "Unspecified"
            is ZmanCalculationMethod.FixedDuration -> method.calculationMethod.valueToString()
            is ZmanCalculationMethod.ZmaniyosDuration -> method.calculationMethod.valueToString()
            is ZmanCalculationMethod.FixedDuration.AteretTorah -> method.calculationMethod.shortDescription()
        }

    private fun tryAddDescription(start: ZmanDefinition?, result: StringBuilder) {
        val startPrefix = "Day starts at "
        val endPrefix = "and ends at"
        val tryAddDescription = { zman: ZmanType, string: StringBuilder, prefix: String ->
            //start?.type?.equals(zman) ?: null
            val startCalculationMethod = start?.calculationMethod
            val notNuln = startCalculationMethod != null
            if (notNuln) {
                string.append("$startPrefix${zman.friendlyNameEnglish} ${startCalculationMethod?.format()}")
            }
            notNuln
        }
        var startZman = ZmanType.ALOS
        var endZman = ZmanType.TZAIS

        if (!tryAddDescription(startZman, result, startPrefix)) {
            startZman = ZmanType.HANAITZ
            if (!tryAddDescription(startZman, result, startPrefix)) {
                startZman = ZmanType.CHATZOS_HAYOM
                tryAddDescription(startZman, result, startPrefix)
            }
        }
        if (!tryAddDescription(endZman, result, endPrefix)) {
            endZman = ZmanType.SHKIAH
            tryAddDescription(endZman, result, endPrefix)
        }
    }
    companion object {
        fun shortDescriptionAteretTorah(minuteOffset: Double) = "${ZmanAuthority.Strings.ATERET_TORAH} (${if(minuteOffset % 1 == 0.0) minuteOffset.toInt()/*don't add .0*/ else minuteOffset} minutes)"
    }
}