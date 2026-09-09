package app.domain

import io.github.kdroidfilter.kosherkotlin.metadata.ZmanAuthority
import io.github.kdroidfilter.kosherkotlin.metadata.ZmanCalculationMethod
import io.github.kdroidfilter.kosherkotlin.metadata.ZmanDefinition
import kotlin.time.Duration

/**
 * Renders the *opinion* line under a zman name in Hebrew.
 *
 * The library's own [io.github.kdroidfilter.kosherkotlin.ZmanDescriptionFormatter] produces
 * English prose; the design needs the short Hebrew form ("גר״א", "16.1°", "מג״א 72"), so this
 * reads the same [ZmanDefinition] metadata and renders it in Hebrew instead.
 */
fun ZmanDefinition.hebrewOpinion(): String {
    val authority = supportingAuthorities.firstOrNull()?.hebrewName().orEmpty()
    val method = calculationMethod.hebrewValue()
    // An authority whose value repeats the method ("גר״א" / "גר״א") should only be shown once.
    val parts = if (authority.isNotBlank() && method != authority) listOf(authority, method)
    else listOf(authority.ifBlank { method })
    return parts.filter { it.isNotBlank() }.joinToString(" ")
}

private fun ZmanCalculationMethod.hebrewValue(): String = when (this) {
    is ZmanCalculationMethod.FixedDuration.AteretTorah -> "עטרת תורה ${minutes.trimNumber()} דקות"
    is ZmanAuthority.AccordingTo -> accordingTo.hebrewName()
    is ZmanAuthority -> hebrewName()
    is ZmanCalculationMethod.Degrees -> "${degrees.trimNumber()}°"
    is ZmanCalculationMethod.FixedDuration -> duration.hebrewMinutes()
    is ZmanCalculationMethod.ZmaniyosDuration -> duration.hebrewZmaniyos()
    is ZmanCalculationMethod.Relationship -> relationship.calculation.hebrewValue()
    is ZmanCalculationMethod.DayDefinition -> dayStart.calculationMethod.hebrewValue()
    is ZmanCalculationMethod.LaterOf ->
        "המאוחר מבין ${zman1.calculationMethod.hebrewValue()} ל${zman2.calculationMethod.hebrewValue()}"
    ZmanCalculationMethod.FixedLocalChatzos -> "חצות מקומי קבוע"
    ZmanCalculationMethod.Unspecified -> ""
}

private fun ZmanAuthority.hebrewName(): String = when (this) {
    ZmanAuthority.Unanimous -> ""
    ZmanAuthority.GRA -> "גר״א"
    ZmanAuthority.MGA -> "מג״א"
    ZmanAuthority.GEONIM -> "גאונים"
    ZmanAuthority.RABEINU_TAM -> "רבנו תם"
    ZmanAuthority.BAAL_HATANYA -> "בעל התניא"
    ZmanAuthority.ARUCH_HASHULCHAN -> "ערוך השולחן"
    ZmanAuthority.SHULCHAN_ARUCH -> "שולחן ערוך"
    ZmanAuthority.RAMBAM -> "רמב״ם"
    ZmanAuthority.FEINSTEIN -> "ר׳ משה פיינשטיין"
    ZmanAuthority.YEREIM -> "יראים"
    ZmanAuthority.EIDOT_HAMIZRACH -> "עדות המזרח"
    is ZmanAuthority.AteretTorah -> "עטרת תורה"
    is ZmanAuthority.AccordingTo -> accordingTo.hebrewName()
    // Long tail: the library only carries an English name for these.
    else -> name
}

private fun Duration.hebrewMinutes(): String {
    val minutes = inWholeMinutes
    return if (minutes == 0L) "" else "${kotlin.math.abs(minutes)} דקות"
}

private fun Duration.hebrewZmaniyos(): String {
    val minutes = kotlin.math.abs(inWholeMinutes)
    if (minutes == 0L) return ""
    if (minutes % 60L == 0L) return "${minutes / 60} שעות זמניות"
    val hours = minutes / 60.0
    return "${hours.trimNumber()} שעות זמניות"
}

/** "16.1" rather than "16.1000004", and "72" rather than "72.0". */
internal fun Number.trimNumber(): String {
    val value = toDouble()
    val rounded = kotlin.math.round(value * 100) / 100
    return if (rounded % 1.0 == 0.0) rounded.toLong().toString() else rounded.toString()
}
