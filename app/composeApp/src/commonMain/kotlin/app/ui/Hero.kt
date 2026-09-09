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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.domain.DaySnapshot
import app.ui.theme.LuachTheme
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.sin

/**
 * The sky panel: gradient, star field, clock, next-zman pill and the sun arc.
 *
 * Every frame-rate value (the star twinkle, the sun halo) is read inside a draw lambda rather
 * than in composition, so the pulse redraws without recomposing anything.
 */
@Composable
fun Hero(
    day: DaySnapshot,
    cityName: String,
    sectionLabel: String,
    compact: Boolean,
    modifier: Modifier = Modifier,
) {
    val colors = LuachTheme.colors
    val pulse = rememberPulse()

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
                        center = Offset(size.width * 0.78f, size.height * 1.18f),
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
                    top = if (compact) 24.dp else 40.dp,
                    bottom = 16.dp,
                ),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            HeroHeader(day, cityName, sectionLabel, compact, pulse)
            SunArc(
                day = day,
                pulse = pulse,
                modifier = Modifier.fillMaxWidth().height(if (compact) 150.dp else 212.dp),
            )
        }
    }
}

@Composable
private fun HeroHeader(
    day: DaySnapshot,
    cityName: String,
    sectionLabel: String,
    compact: Boolean,
    pulse: () -> Float,
) {
    val colors = LuachTheme.colors
    val fonts = LuachTheme.fonts

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top,
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "$sectionLabel · $cityName",
                    fontFamily = fonts.body,
                    fontSize = 10.5.sp,
                    letterSpacing = 3.sp,
                    color = colors.heroKicker,
                )
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

            if (!compact) day.next?.let { NextZmanPill(it, pulse) }
        }

        if (compact) day.next?.let { NextZmanPill(it, pulse) }
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
            Text(next.name, fontFamily = fonts.display, fontSize = 22.sp, color = colors.heroInk)
            Text(next.time, fontFamily = fonts.display, fontSize = 22.sp, color = colors.heroGold)
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

// --- Sun arc ----------------------------------------------------------------------------

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
private fun SunArc(day: DaySnapshot, pulse: () -> Float, modifier: Modifier = Modifier) {
    val colors = LuachTheme.colors
    val fonts = LuachTheme.fonts
    val measurer = rememberTextMeasurer()

    val progress = remember(day.nowMinuteOfDay, day.sunriseMinuteOfDay, day.sunsetMinuteOfDay) {
        val sunrise = day.sunriseMinuteOfDay
        val sunset = day.sunsetMinuteOfDay
        if (sunrise == null || sunset == null || sunset <= sunrise) 0f
        else ((day.nowMinuteOfDay - sunrise).toFloat() / (sunset - sunrise)).coerceIn(0f, 1f)
    }

    val labelStyle = remember(fonts, colors) {
        TextStyle(
            fontFamily = fonts.body,
            fontSize = 12.5.sp,
            color = colors.arcLabel,
            textAlign = TextAlign.Center,
        )
    }
    val marks = remember(day.sunriseLabel, day.chatzosLabel, day.sunsetLabel) {
        listOf(0f to day.sunriseLabel, 0.5f to day.chatzosLabel, 1f to day.sunsetLabel)
    }

    Canvas(modifier) {
        val scaleX = size.width / ArcViewWidth
        val scaleY = size.height / ArcViewHeight
        fun project(point: Offset) = Offset(point.x * scaleX, point.y * scaleY)

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
            start = Offset(10f * scaleX, baseline),
            end = Offset(890f * scaleX, baseline),
            strokeWidth = 1.dp.toPx(),
        )
        drawPath(path, colors.arcBase, style = Stroke(width = 1.dp.toPx()))

        // Only the elapsed part of the arc, matching the design's strokeDashoffset reveal.
        if (progress > 0f) {
            val travelled = Path()
            PathMeasure().apply {
                setPath(path, false)
                getSegment(0f, length * progress, travelled, true)
            }
            drawPath(
                path = travelled,
                brush = Brush.horizontalGradient(
                    0f to colors.arcStart,
                    0.55f to colors.arcMid,
                    1f to colors.arcEnd,
                ),
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

        val sun = project(bezierAt(progress))
        val halo = 62f * scaleY
        drawCircle(
            brush = Brush.radialGradient(
                0f to colors.sunHalo,
                0.45f to colors.sunHalo.copy(alpha = 0.28f),
                1f to Color.Transparent,
                center = sun,
                radius = halo,
            ),
            radius = halo,
            center = sun,
        )
        drawCircle(colors.sunCore, radius = 9f * scaleY, center = sun)
        drawCircle(
            color = colors.sunHalo,
            radius = 9f * scaleY,
            center = sun,
            alpha = 0.35f + 0.65f * pulse(),
            style = Stroke(width = 10f * scaleY),
        )
    }
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
