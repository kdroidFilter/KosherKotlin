package sternbach.software.kosherkotlin.metadata

import sternbach.software.kosherkotlin.Zman
import kotlin.math.absoluteValue

data class Occurrence(val subject: ZmanType, val calculationMethod: ZmanCalculationMethod) {

    infix fun after(zmanType: ZmanType): ZmanRelationship = ZmanRelationship(
        subject, +calculationMethod, zmanType
    )
    infix fun after(zmanDefinition: ZmanDefinition): ZmanRelationship = ZmanRelationship(
        subject, +calculationMethod, relativeToZman = zmanDefinition
    )
    infix fun before(zmanType: ZmanType): ZmanRelationship = ZmanRelationship(
        subject, -calculationMethod, zmanType
    )
    infix fun before(zmanDefinition: ZmanDefinition): ZmanRelationship = ZmanRelationship(
        subject, -calculationMethod, relativeToZman = zmanDefinition
    )

    /**
     * Syntactic sugar for [after] ([zman].[definition][Zman.definition]).
     * */
    infix fun after(zman: Zman<*>): ZmanRelationship = after(zman.definition)
    /**
     * Syntactic sugar for [before] ([zman].[definition][Zman.definition]).
     * */
    infix fun before(zman: Zman<*>): ZmanRelationship = before(zman.definition)

    /**
     * Returns a [ZmanCalculationMethod] with a [ZmanCalculationMethod.value] that is the negative of [this.value].
     * If [this.value] is already negative, it will remain negative, unlike a regular unary minus operator.
     * */
    private operator fun ZmanCalculationMethod.unaryMinus(): ZmanCalculationMethod =
        when (this) {
            is ZmanCalculationMethod.Degrees -> ZmanCalculationMethod.Degrees(-(degrees.absoluteValue))
            is ZmanCalculationMethod.FixedDuration -> ZmanCalculationMethod.FixedDuration(-(duration.absoluteValue))
            is ZmanCalculationMethod.ZmaniyosDuration -> ZmanCalculationMethod.ZmaniyosDuration(-(duration.absoluteValue))
            is ZmanCalculationMethod.FixedDuration.AteretTorah -> ZmanCalculationMethod.FixedDuration.AteretTorah(-(minutes.absoluteValue))
            is ZmanAuthority,
            is ZmanCalculationMethod.DayDefinition,
            is ZmanCalculationMethod.LaterOf,
            is ZmanCalculationMethod.Relationship,
            ZmanCalculationMethod.FixedLocalChatzos,
            ZmanCalculationMethod.Unspecified,-> this
        }
    private operator fun ZmanCalculationMethod.unaryPlus(): ZmanCalculationMethod =
        when (this) {
            is ZmanCalculationMethod.Degrees -> ZmanCalculationMethod.Degrees(degrees.absoluteValue)
            is ZmanCalculationMethod.FixedDuration -> ZmanCalculationMethod.FixedDuration(duration.absoluteValue)
            is ZmanCalculationMethod.ZmaniyosDuration -> ZmanCalculationMethod.ZmaniyosDuration(duration.absoluteValue)
            is ZmanCalculationMethod.FixedDuration.AteretTorah -> ZmanCalculationMethod.FixedDuration.AteretTorah(minutes.absoluteValue)
            is ZmanAuthority,
            is ZmanCalculationMethod.DayDefinition,
            is ZmanCalculationMethod.LaterOf,
            is ZmanCalculationMethod.Relationship,
            ZmanCalculationMethod.FixedLocalChatzos,
            ZmanCalculationMethod.Unspecified, -> this
        }
}
