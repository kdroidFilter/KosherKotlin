package app.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.spring
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin
import kotlin.random.Random

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
    /** Width of the rail floating over the hero: the sky runs under it, the text does not. */
    startInset: Dp = 0.dp,
    /** Desktop widget: fill the overlay window and skip pointer-tilt, so a click can open the app. */
    widget: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val colors = LuachTheme.colors
    val pulse = rememberPulse()

    // A Canvas has no layout direction of its own, so the sky ran left to right inside an
    // otherwise RTL app. Mirrored, the day reads like the text: sunrise right, sunset left.
    val mirrored = LocalLayoutDirection.current == LayoutDirection.Rtl

    val sky = remember(day.nowMinuteOfDay, day.sunriseMinuteOfDay, day.sunsetMinuteOfDay) {
        travel(day)
    }
    // The warm end of the sky rides under the sun all day, and once the sun is down it stays
    // on the horizon it set behind rather than following the moon.
    val glow = bezierAt(if (sky.night) 1f else sky.progress).x / ArcViewWidth
    val glowX = if (mirrored) 1f - glow else glow
    // Stars hold through dawn and dusk, and are gone by full daylight.
    val starAlpha = colors.starAlpha * (1f - sky.daylight())

    // The sky leans away from whatever the platform can tell us about the viewer: the phone's
    // own tilt where there is an accelerometer, the pointer where there is not.
    val motion = rememberDeviceTilt()
    val pointer = rememberPointerTilt()
    val scope = rememberCoroutineScope()
    val lean: () -> Offset = remember(motion, pointer) {
        if (motion != null) ({ motion.value }) else ({ pointer.value })
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (widget) Modifier.fillMaxSize()
                else Modifier.heightIn(min = if (compact) 400.dp else 470.dp),
            )
            .then(
                if (motion == null && !widget) Modifier.pointerTiltSource(pointer, scope) else Modifier,
            )
            .drawBehind {
                // CSS: radial-gradient(120% 150% at 78% 118%, …). Compose only has circular
                // radial gradients, so match the centre and reach instead of the ellipse.
                drawRect(
                    Brush.radialGradient(
                        colors = colors.skyStops,
                        // Furthest layer, so it leans least.
                        center = Offset(
                            size.width * glowX - lean().x * 12.dp.toPx(),
                            size.height * 1.18f - lean().y * 8.dp.toPx(),
                        ),
                        radius = max(size.width, size.height) * 1.35f,
                    )
                )
            }
    ) {
        if (starAlpha > 0.01f) {
            Stars(alpha = starAlpha, pulse = pulse, lean = lean, modifier = Modifier.fillMaxSize())
        }
        Box(
            Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .height(if (widget) 72.dp else 200.dp)
                .background(Brush.verticalGradient(listOf(Color.Transparent, colors.skyVeil)))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = startInset + when {
                        widget -> 14.dp
                        compact -> 20.dp
                        else -> 46.dp
                    },
                    end = when {
                        widget -> 14.dp
                        compact -> 20.dp
                        else -> 46.dp
                    },
                    top = topInset + when {
                        widget -> 12.dp
                        compact -> 24.dp
                        else -> 40.dp
                    },
                    bottom = if (widget) 8.dp else 16.dp,
                ),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            HeroHeader(day, compact, widget, pulse)
            SkyArc(
                day = day,
                sky = sky,
                pulse = pulse,
                mirrored = mirrored,
                widget = widget,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(
                        when {
                            widget -> 88.dp
                            compact -> 150.dp
                            else -> 212.dp
                        },
                    )
                    // Nearest layer, so it leans most. A layer-phase read: no recomposition.
                    .graphicsLayer {
                        translationX = -lean().x * 16.dp.toPx()
                        translationY = -lean().y * 9.dp.toPx()
                    },
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun HeroHeader(day: DaySnapshot, compact: Boolean, widget: Boolean, pulse: () -> Float) {
    val colors = LuachTheme.colors
    val fonts = LuachTheme.fonts
    val clockSize = when {
        widget -> 40.sp
        compact -> 76.sp
        else -> 130.sp
    }
    val clockLine = when {
        widget -> 38.sp
        compact -> 68.sp
        else -> 112.sp
    }
    val dateSize = when {
        widget -> 15.sp
        compact -> 22.sp
        else -> 30.sp
    }

    // FlowRow, not Row: beside a 130sp clock the pill was squeezed down to a couple of glyphs
    // per line. Now it drops onto its own line as soon as the two no longer fit side by side.
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalArrangement = Arrangement.spacedBy(if (widget) 6.dp else 16.dp),
        itemVerticalAlignment = if (widget) Alignment.CenterVertically else Alignment.Top,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(if (widget) 2.dp else 12.dp)) {
            // A content swap, not a value animation: the minute rolls up and out of the way.
            AnimatedContent(
                targetState = day.clock,
                transitionSpec = {
                    (slideInVertically { it / 4 } + fadeIn()) togetherWith
                        (slideOutVertically { -it / 4 } + fadeOut())
                },
                label = "clock",
            ) { clock ->
                Text(
                    text = clock,
                    fontFamily = fonts.display,
                    fontSize = clockSize,
                    lineHeight = clockLine,
                    letterSpacing = if (widget) (-1.5).sp else (-4).sp,
                    color = colors.heroInk,
                )
            }
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(if (widget) 8.dp else 14.dp),
            ) {
                Text(
                    text = day.hebrewDate,
                    fontFamily = fonts.display,
                    fontSize = dateSize,
                    color = colors.heroGold,
                )
                Text(
                    text = if (widget) day.gregorianDate else "${day.gregorianDate} · ${day.parsha}",
                    fontFamily = fonts.body,
                    fontSize = if (widget) 11.sp else 13.sp,
                    color = colors.heroSub,
                )
            }
        }

        day.next?.let { NextZmanPill(it, pulse, widget) }
    }
}

@Composable
private fun NextZmanPill(next: app.domain.NextZman, pulse: () -> Float, widget: Boolean) {
    val colors = LuachTheme.colors
    val fonts = LuachTheme.fonts
    val nameSize = if (widget) 14.sp else 22.sp

    Column(
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(if (widget) 0.dp else 8.dp),
    ) {
        Row(
            modifier = Modifier
                .background(colors.glassFill, CircleShape)
                .border(1.dp, colors.glassLine, CircleShape)
                .padding(
                    horizontal = if (widget) 10.dp else 18.dp,
                    vertical = if (widget) 6.dp else 12.dp,
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(if (widget) 8.dp else 14.dp),
        ) {
            Box(
                Modifier
                    .size(if (widget) 5.dp else 7.dp)
                    .drawBehind { drawCircle(colors.heroGold, alpha = 0.35f + 0.65f * pulse()) }
            )
            if (!widget) {
                Text("הזמן הבא", fontFamily = fonts.body, fontSize = 13.sp, color = colors.heroSub)
            }
            Text(
                text = next.name,
                fontFamily = fonts.display,
                fontSize = nameSize,
                color = colors.heroInk,
                maxLines = 1,
            )
            Text(
                text = next.time,
                fontFamily = fonts.display,
                fontSize = nameSize,
                color = colors.heroGold,
                maxLines = 1,
            )
        }
        if (!widget) {
            Text(
                text = "בעוד ${next.inLabel} · ${next.opinion}",
                fontFamily = fonts.body,
                fontSize = 12.5.sp,
                color = colors.heroSub,
            )
        }
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
private fun Stars(alpha: Float, pulse: () -> Float, lean: () -> Offset, modifier: Modifier = Modifier) {
    val color = LuachTheme.colors.sunCore
    val meteor = rememberMeteor()

    Canvas(modifier) {
        val progress = pulse()
        val shift = lean()
        StarSeeds.forEach { star ->
            // No extra randomness: the seeded field already varies radius, and a bigger star
            // reads as a nearer one, so its own size is the depth the parallax needs.
            val depth = ((star.radius - StarMinRadius) / StarRadiusSpread).coerceIn(0f, 1f)
            val push = 6.dp.toPx() + depth * 22.dp.toPx()
            // One animated value drives all 78 stars; the phase offset makes them independent.
            val twinkle = 0.35f + 0.65f * abs(sin((progress + star.phase) * 2f * PI_F))
            drawCircle(
                color = color,
                radius = star.radius.dp.toPx(),
                center = Offset(
                    star.x * size.width - shift.x * push,
                    star.y * size.height - shift.y * push,
                ),
                alpha = star.alpha * alpha * twinkle,
            )
        }

        val travelled = meteor.progress.value
        if (travelled < 1f) {
            val head = Offset(
                (meteor.origin.x - MeteorTravel * travelled) * size.width,
                (meteor.origin.y + MeteorTravel * 0.5f * travelled) * size.height,
            )
            val tail = Offset(head.x + 0.09f * size.width, head.y - 0.045f * size.height)
            drawLine(
                brush = Brush.linearGradient(listOf(Color.Transparent, color), tail, head),
                start = tail,
                end = head,
                strokeWidth = 1.6.dp.toPx(),
                cap = StrokeCap.Round,
                // Fade in and out again so it never blinks on or off mid-flight.
                alpha = alpha * sin(travelled * PI_F),
            )
        }
    }
}

private const val StarMinRadius = 0.4f
private const val StarRadiusSpread = 1.1f
private const val MeteorTravel = 0.3f

/** An occasional streak across the upper sky. Parked at 1, where nothing is drawn. */
private class MeteorState {
    val progress = Animatable(1f)
    var origin by mutableStateOf(Offset(0.8f, 0.1f))
}

/**
 * Lives with the star field, so it only runs while there is a night sky to cross. Each pass
 * picks a fresh entry point — a streak on the same line every time reads as a machine.
 */
@Composable
private fun rememberMeteor(): MeteorState {
    val meteor = remember { MeteorState() }
    LaunchedEffect(meteor) {
        val random = Random(29)
        while (true) {
            delay(7_000L + random.nextLong(11_000L))
            meteor.origin = Offset(0.4f + random.nextFloat() * 0.55f, 0.03f + random.nextFloat() * 0.24f)
            meteor.progress.snapTo(0f)
            meteor.progress.animateTo(1f, tween(1_200, easing = LinearEasing))
        }
    }
    return meteor
}

/**
 * Where the pointer is inside the hero, as -1..1 from the centre, sprung so the sky lags the
 * cursor instead of snapping to it. Read it from draw or layer lambdas, never in composition.
 */
@Composable
private fun rememberPointerTilt(): Animatable<Offset, *> =
    remember { Animatable(Offset.Zero, Offset.VectorConverter) }

/**
 * Feeds [rememberPointerTilt] without consuming anything, so clicks and scrolling still pass
 * through. Not attached on the platforms that lean from their own accelerometer instead.
 */
private fun Modifier.pointerTiltSource(tilt: Animatable<Offset, *>, scope: CoroutineScope): Modifier =
    pointerInput(tilt) {
        awaitPointerEventScope {
            while (true) {
                val event = awaitPointerEvent(PointerEventPass.Initial)
                val target = if (event.type == PointerEventType.Exit) {
                    Offset.Zero
                } else {
                    val at = event.changes.last().position
                    Offset(
                        ((at.x / size.width - 0.5f) * 2f).coerceIn(-1f, 1f),
                        ((at.y / size.height - 0.5f) * 2f).coerceIn(-1f, 1f),
                    )
                }
                scope.launch {
                    tilt.animateTo(target, spring(0.9f, Spring.StiffnessLow))
                }
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
    sky: SkyTravel,
    pulse: () -> Float,
    mirrored: Boolean,
    widget: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val colors = LuachTheme.colors
    val fonts = LuachTheme.fonts
    val measurer = rememberTextMeasurer()

    val labelStyle = remember(fonts, colors) {
        TextStyle(
            fontFamily = fonts.body,
            fontSize = 12.5.sp,
            color = colors.arcLabel,
            textAlign = TextAlign.Center,
        )
    }
    val marks = remember(day, sky.night) {
        if (sky.night) listOf(0f to day.sunsetLabel, 0.5f to day.midnightLabel, 1f to day.sunriseLabel)
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
        if (sky.progress > 0f) {
            val travelled = Path()
            PathMeasure().apply {
                setPath(path, false)
                getSegment(0f, length * sky.progress, travelled, true)
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

        if (!widget) {
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
        }

        val body = project(bezierAt(sky.progress))
        // The arc canvas is shorter in the widget, so the body is scaled up or it reads as a speck.
        val bodyScale = if (widget) 1.7f else 1f
        if (sky.night) {
            drawMoon(body, 11f * scaleY * bodyScale, day.moonPhase)
        } else {
            // A low sun reddens. The bezier stands in for altitude: highest at mid-travel.
            val low = 1f - sin(sky.progress * PI_F)
            drawSun(body, 9f * scaleY * bodyScale, low * low, pulse(), colors.sunCore, colors.sunHalo)
        }
    }
}

// --- Sun and moon --------------------------------------------------------------------------

/** Deep amber the disc and its corona sink towards on the horizon. */
private val SunLow = Color(0xFFE8823A)

/** The moon is not warm. Its colour is a fact about the moon, not about the theme. */
private val MoonLight = Color(0xFFEDEFF5)
private val MoonSea = Color(0xFF6E7486)

/** Maria, in units of the moon's radius: centre offset to patch radius. */
private val MoonMaria = listOf(
    Offset(-0.28f, -0.30f) to 0.30f,
    Offset(0.18f, -0.12f) to 0.22f,
    Offset(-0.05f, 0.34f) to 0.26f,
    Offset(0.36f, 0.28f) to 0.16f,
    Offset(-0.46f, 0.12f) to 0.14f,
)

/**
 * A white-hot core reddening towards the limb, inside a corona whose falloff dies out before
 * its own edge — a flat disc with a hard rim was the cartoon part. [warmth] is 0 at the top of
 * the arc and 1 on the horizon.
 */
private fun DrawScope.drawSun(
    center: Offset,
    radius: Float,
    warmth: Float,
    pulse: Float,
    core: Color,
    halo: Color,
) {
    val glow = lerp(halo, SunLow, warmth)
    val corona = radius * (5.8f + 0.6f * pulse)
    drawCircle(
        brush = Brush.radialGradient(
            0.00f to glow.copy(alpha = 0.50f),
            0.20f to glow.copy(alpha = 0.26f),
            0.50f to glow.copy(alpha = 0.07f),
            1.00f to Color.Transparent,
            center = center,
            radius = corona,
        ),
        radius = corona,
        center = center,
    )
    drawCircle(
        brush = Brush.radialGradient(
            0.00f to Color.White,
            0.55f to lerp(core, SunLow, warmth * 0.7f),
            1.00f to lerp(core, SunLow, warmth),
            center = center,
            radius = radius,
        ),
        radius = radius,
        center = center,
    )
}

/**
 * The moon at [phase] of the lunar cycle. The lit limb is half the disc; the terminator is the
 * half-ellipse that narrows to a straight line at the quarters and reopens the other way, so
 * one shape covers crescent, quarter and gibbous without special cases.
 *
 * What sells it as a sphere rather than a white chip: earthshine on the unlit side, maria, and
 * a limb that darkens — all clipped to the lit shape, so they vanish with it.
 */
private fun DrawScope.drawMoon(center: Offset, radius: Float, phase: Float) {
    // Full moons light the sky; a new moon lights nothing.
    val illumination = (1f - cos(2f * PI_F * phase)) / 2f
    val glow = radius * 3.6f
    drawCircle(
        brush = Brush.radialGradient(
            0.00f to MoonLight.copy(alpha = 0.26f * illumination),
            0.45f to MoonLight.copy(alpha = 0.08f * illumination),
            1.00f to Color.Transparent,
            center = center,
            radius = glow,
        ),
        radius = glow,
        center = center,
    )

    drawCircle(MoonLight.copy(alpha = 0.13f), radius, center)

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
    drawPath(path, MoonLight)

    clipPath(path) {
        MoonMaria.forEach { (at, size) ->
            drawCircle(
                color = MoonSea,
                radius = size * radius,
                center = Offset(center.x + at.x * radius, center.y + at.y * radius),
                alpha = 0.17f,
            )
        }
        drawCircle(
            brush = Brush.radialGradient(
                0.55f to Color.Transparent,
                1.00f to MoonSea.copy(alpha = 0.38f),
                center = center,
                radius = radius,
            ),
            radius = radius,
            center = center,
        )
    }
}

// --- Moon ------------------------------------------------------------------------------

@Immutable
private class SkyTravel(val night: Boolean, val progress: Float) {
    /** 0 while the sun is down, ramping to 1 over the first and last [DaylightFade] of the day. */
    fun daylight(): Float =
        if (night) 0f else (minOf(progress, 1f - progress) / DaylightFade).coerceIn(0f, 1f)
}

/** Fraction of the daylight span the stars take to fade out at dawn, and back in at dusk. */
private const val DaylightFade = 0.08f

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
