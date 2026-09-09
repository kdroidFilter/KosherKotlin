package app.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.domain.DaySnapshot
import app.ui.theme.LuachTheme
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin

/**
 * The sky panel: gradient, star field, clock, next-zman pill and the sun (or moon) arc.
 *
 * Every frame-rate value (the star twinkle, the sun halo) is read inside a draw lambda rather
 * than in composition, so the pulse redraws without recomposing anything.
 */
@Composable
fun Hero(
    day: DaySnapshot,
    compact: Boolean,
    topInset: Dp = 0.dp,
    modifier: Modifier = Modifier,
) {
    val colors = LuachTheme.colors
    val pulse = rememberPulse()

    // A Canvas has no layout direction of its own, so the sky ran left to right inside an
    // otherwise RTL app. Mirrored, the day reads like the text: sunrise right, sunset left.
    val mirrored = LocalLayoutDirection.current == LayoutDirection.Rtl

    Box(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = if (compact) 400.dp else 470.dp)
            .drawBehind {
                // CSS: radial-gradient(120% 150% at 78% 118%, …). Compose only has circular
                // radial gradients, so match the centre and reach instead of the ellipse.
                drawRect(
                    Brush.radialGradient(
                        colors = colors.skyStops,
                        // The warm end of the sky belongs over the horizon the sun sets on.
                        center = Offset(size.width * if (mirrored) 0.22f else 0.78f, size.height * 1.18f),
                        radius = max(size.width, size.height) * 1.35f,
                    )
                )
            }
    ) {
        if (colors.starAlpha > 0f) {
            Stars(alpha = colors.starAlpha, pulse = pulse, modifier = Modifier.fillMaxSize())
        }
        Box(
            Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .height(200.dp)
                .background(Brush.verticalGradient(listOf(Color.Transparent, colors.skyVeil)))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = if (compact) 20.dp else 46.dp,
                    end = if (compact) 20.dp else 46.dp,
                    top = topInset + if (compact) 24.dp else 40.dp,
                    bottom = 16.dp,
                ),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            HeroHeader(day, compact, pulse)
            SkyArc(
                day = day,
                pulse = pulse,
                mirrored = mirrored,
                modifier = Modifier.fillMaxWidth().height(if (compact) 150.dp else 212.dp),
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun HeroHeader(day: DaySnapshot, compact: Boolean, pulse: () -> Float) {
    val colors = LuachTheme.colors
    val fonts = LuachTheme.fonts

    // FlowRow, not Row: beside a 130sp clock the pill was squeezed down to a couple of glyphs
    // per line. Now it drops onto its own line as soon as the two no longer fit side by side.
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        itemVerticalAlignment = Alignment.Top,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = day.clock,
                fontFamily = fonts.display,
                fontSize = if (compact) 76.sp else 130.sp,
                lineHeight = if (compact) 68.sp else 112.sp,
                letterSpacing = (-4).sp,
                color = colors.heroInk,
            )
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Text(
                    text = day.hebrewDate,
                    fontFamily = fonts.display,
                    fontSize = if (compact) 22.sp else 30.sp,
                    color = colors.heroGold,
                )
                Text(
                    text = "${day.gregorianDate} · ${day.parsha}",
                    fontFamily = fonts.body,
                    fontSize = 13.sp,
                    color = colors.heroSub,
                )
            }
        }

        day.next?.let { NextZmanPill(it, pulse) }
    }
}

@Composable
private fun NextZmanPill(next: app.domain.NextZman, pulse: () -> Float) {
    val colors = LuachTheme.colors
    val fonts = LuachTheme.fonts

    Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier
                .background(colors.glassFill, CircleShape)
                .border(1.dp, colors.glassLine, CircleShape)
                .padding(horizontal = 18.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Box(
                Modifier
                    .size(7.dp)
                    .drawBehind { drawCircle(colors.heroGold, alpha = 0.35f + 0.65f * pulse()) }
            )
            Text("הזמן הבא", fontFamily = fonts.body, fontSize = 13.sp, color = colors.heroSub)
            Text(
                text = next.name,
                fontFamily = fonts.display,
                fontSize = 22.sp,
                color = colors.heroInk,
                maxLines = 1,
            )
            Text(
                text = next.time,
                fontFamily = fonts.display,
                fontSize = 22.sp,
                color = colors.heroGold,
                maxLines = 1,
            )
        }
        Text(
            text = "בעוד ${next.inLabel} · ${next.opinion}",
            fontFamily = fonts.body,
            fontSize = 12.5.sp,
            color = colors.heroSub,
        )
    }
}

// --- Star field -------------------------------------------------------------------------

@Immutable
private class Star(val x: Float, val y: Float, val radius: Float, val alpha: Float, val phase: Float)

/** The design's seeded LCG, so the app's sky is the same sky as the mock. */
private val StarSeeds: List<Star> = run {
    var seed = 7L
    fun next(): Float {
        seed = seed * 16807 % 2147483647
        return seed.toFloat() / 2147483647f
    }
    List(78) {
        Star(
            x = next(),
            y = next() * 0.62f,
            radius = 0.4f + next() * 1.1f,
            alpha = 0.12f + next() * 0.6f,
            phase = next(),
        )
    }
}

@Composable
private fun Stars(alpha: Float, pulse: () -> Float, modifier: Modifier = Modifier) {
    val color = LuachTheme.colors.sunCore
    Canvas(modifier) {
        val progress = pulse()
        StarSeeds.forEach { star ->
            // One animated value drives all 78 stars; the phase offset makes them independent.
            val twinkle = 0.35f + 0.65f * abs(sin((progress + star.phase) * 2f * PI_F))
            drawCircle(
                color = color,
                radius = star.radius.dp.toPx(),
                center = Offset(star.x * size.width, star.y * size.height),
                alpha = star.alpha * alpha * twinkle,
            )
        }
    }
}

// --- Sky arc ----------------------------------------------------------------------------

private const val PI_F = 3.1415927f
private const val ArcViewWidth = 900f
private const val ArcViewHeight = 212f

/** The design's cubic: `M40 178 C250 6 650 6 860 178`, in a 900×212 view box. */
private val ArcPoints = listOf(
    Offset(40f, 178f), Offset(250f, 6f), Offset(650f, 6f), Offset(860f, 178f),
)

private fun bezierAt(t: Float): Offset {
    val k = 1 - t
    val (p0, p1, p2, p3) = ArcPoints
    return Offset(
        k * k * k * p0.x + 3 * k * k * t * p1.x + 3 * k * t * t * p2.x + t * t * t * p3.x,
        k * k * k * p0.y + 3 * k * k * t * p1.y + 3 * k * t * t * p2.y + t * t * t * p3.y,
    )
}

@Composable
private fun SkyArc(
    day: DaySnapshot,
    pulse: () -> Float,
    mirrored: Boolean,
    modifier: Modifier = Modifier,
) {
    val colors = LuachTheme.colors
    val fonts = LuachTheme.fonts
    val measurer = rememberTextMeasurer()

    val travel = remember(day.nowMinuteOfDay, day.sunriseMinuteOfDay, day.sunsetMinuteOfDay) {
        travel(day)
    }

    val labelStyle = remember(fonts, colors) {
        TextStyle(
            fontFamily = fonts.body,
            fontSize = 12.5.sp,
            color = colors.arcLabel,
            textAlign = TextAlign.Center,
        )
    }
    val marks = remember(day, travel.night) {
        if (travel.night) listOf(0f to day.sunsetLabel, 0.5f to day.midnightLabel, 1f to day.sunriseLabel)
        else listOf(0f to day.sunriseLabel, 0.5f to day.chatzosLabel, 1f to day.sunsetLabel)
    }

    Canvas(modifier) {
        val scaleX = size.width / ArcViewWidth
        val scaleY = size.height / ArcViewHeight
        fun x(value: Float) = if (mirrored) size.width - value * scaleX else value * scaleX
        fun project(point: Offset) = Offset(x(point.x), point.y * scaleY)

        val (p0, p1, p2, p3) = ArcPoints
        val path = Path().apply {
            val start = project(p0)
            val c1 = project(p1)
            val c2 = project(p2)
            val end = project(p3)
            moveTo(start.x, start.y)
            cubicTo(c1.x, c1.y, c2.x, c2.y, end.x, end.y)
        }

        val baseline = 178f * scaleY
        drawLine(
            color = colors.arcBase,
            start = Offset(x(10f), baseline),
            end = Offset(x(890f), baseline),
            strokeWidth = 1.dp.toPx(),
        )
        drawPath(path, colors.arcBase, style = Stroke(width = 1.dp.toPx()))

        // Only the elapsed part of the arc, matching the design's strokeDashoffset reveal.
        if (travel.progress > 0f) {
            val travelled = Path()
            PathMeasure().apply {
                setPath(path, false)
                getSegment(0f, length * travel.progress, travelled, true)
            }
            drawPath(
                path = travelled,
                brush = if (mirrored) {
                    Brush.horizontalGradient(
                        0f to colors.arcEnd,
                        0.45f to colors.arcMid,
                        1f to colors.arcStart,
                    )
                } else {
                    Brush.horizontalGradient(
                        0f to colors.arcStart,
                        0.55f to colors.arcMid,
                        1f to colors.arcEnd,
                    )
                },
                style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round),
            )
        }

        marks.forEach { (u, label) ->
            val x = project(bezierAt(u)).x
            drawLine(
                color = colors.arcTick,
                start = Offset(x, baseline),
                end = Offset(x, 187f * scaleY),
                strokeWidth = 1.dp.toPx(),
            )
            val measured = measurer.measure(label, labelStyle)
            drawText(measured, topLeft = Offset(x - measured.size.width / 2f, 192f * scaleY))
        }

        val body = project(bezierAt(travel.progress))
        val glow = if (travel.night) colors.sunHalo.copy(alpha = 0.3f) else colors.sunHalo
        val halo = (if (travel.night) 42f else 62f) * scaleY
        drawCircle(
            brush = Brush.radialGradient(
                0f to glow,
                0.45f to glow.copy(alpha = glow.alpha * 0.33f),
                1f to Color.Transparent,
                center = body,
                radius = halo,
            ),
            radius = halo,
            center = body,
        )
        if (travel.night) {
            drawMoon(
                center = body,
                radius = 11f * scaleY,
                phase = day.moonPhase,
                lit = colors.sunCore,
                dark = colors.sunCore.copy(alpha = 0.14f),
            )
        } else {
            drawCircle(colors.sunCore, radius = 9f * scaleY, center = body)
            drawCircle(
                color = colors.sunHalo,
                radius = 9f * scaleY,
                center = body,
                alpha = 0.35f + 0.65f * pulse(),
                style = Stroke(width = 10f * scaleY),
            )
        }
    }
}

// --- Moon ------------------------------------------------------------------------------

@Immutable
private class SkyTravel(val night: Boolean, val progress: Float)

private const val MinutesPerDay = 24 * 60

/** Where the sun is between sunrise and sunset, or the moon between sunset and sunrise. */
private fun travel(day: DaySnapshot): SkyTravel {
    val sunrise = day.sunriseMinuteOfDay
    val sunset = day.sunsetMinuteOfDay
    if (sunrise == null || sunset == null || sunset <= sunrise) return SkyTravel(false, 0f)

    val now = day.nowMinuteOfDay
    if (now in sunrise until sunset) {
        return SkyTravel(false, (now - sunrise).toFloat() / (sunset - sunrise))
    }
    // ponytail: today's sunrise stands in for tomorrow's, a minute or two out on this arc.
    val elapsed = if (now >= sunset) now - sunset else MinutesPerDay - sunset + now
    val length = MinutesPerDay - sunset + sunrise
    return SkyTravel(true, (elapsed.toFloat() / length).coerceIn(0f, 1f))
}

/**
 * The moon at [phase] of the lunar cycle. The lit limb is half the disc; the terminator is the
 * half-ellipse that narrows to a straight line at the quarters and reopens the other way, so
 * one shape covers crescent, quarter and gibbous without special cases.
 */
private fun DrawScope.drawMoon(center: Offset, radius: Float, phase: Float, lit: Color, dark: Color) {
    drawCircle(dark, radius, center)

    val waxing = phase < 0.5f
    // Distance of the terminator from the centre, signed towards the lit limb.
    val terminator = radius * cos(2f * PI_F * phase)
    val disc = Rect(center.x - radius, center.y - radius, center.x + radius, center.y + radius)

    val path = Path().apply {
        // The lit limb: the right half of the disc while waxing, the left half while waning.
        arcTo(disc, if (waxing) -90f else 90f, 180f, true)
        if (abs(terminator) < 0.5f) {
            lineTo(center.x, if (waxing) center.y - radius else center.y + radius)
        } else {
            arcTo(
                rect = Rect(
                    center.x - abs(terminator), center.y - radius,
                    center.x + abs(terminator), center.y + radius,
                ),
                startAngleDegrees = if (waxing) 90f else -90f,
                sweepAngleDegrees = if (terminator >= 0f) -180f else 180f,
                forceMoveTo = false,
            )
        }
        close()
    }
    drawPath(path, lit)
}

/**
 * One shared 0→1 ramp for every pulsing element, handed out as a lambda so callers read it in
 * the draw phase instead of in composition.
 */
@Composable
private fun rememberPulse(): () -> Float {
    val transition = rememberInfiniteTransition(label = "pulse")
    val progress = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2400, easing = LinearEasing), RepeatMode.Restart),
        label = "pulseProgress",
    )
    return remember(progress) { { progress.value } }
}
