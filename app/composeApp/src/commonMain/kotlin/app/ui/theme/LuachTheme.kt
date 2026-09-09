package app.ui.theme

import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kosherkotlin.app.composeapp.generated.resources.FrankRuhlLibre_VariableFont_wght
import kosherkotlin.app.composeapp.generated.resources.NotoSansHebrew_VariableFont_wdth_wght
import kosherkotlin.app.composeapp.generated.resources.Res
import org.jetbrains.compose.resources.Font

/**
 * The design's CSS custom properties, one Kotlin field each.
 *
 * `@Immutable` is truthful here: every field is a `Color` value and the whole palette is
 * replaced (never mutated) when the theme flips.
 */
@Immutable
data class LuachColors(
    val background: Color,
    /** Bottom stop of the page gradient; equal to [background] for a flat fill. */
    val backgroundEnd: Color,
    val surface: Color,
    val surfaceRaised: Color,
    val line: Color,
    val lineStrong: Color,
    val ink: Color,
    val muted: Color,
    val dim: Color,
    val gold: Color,
    val goldBright: Color,
    val goldSoft: Color,
    val railTop: Color,
    val railBottom: Color,
    val railInk: Color,
    val railMuted: Color,
    val railDim: Color,
    val railLine: Color,
    val railActive: Color,
    val railHover: Color,
    val skyStops: ImmutableList<Color>,
    val skyVeil: Color,
    val heroInk: Color,
    val heroGold: Color,
    val heroSub: Color,
    val heroKicker: Color,
    val glassFill: Color,
    val glassLine: Color,
    val starAlpha: Float,
    val arcStart: Color,
    val arcMid: Color,
    val arcEnd: Color,
    val arcBase: Color,
    val arcTick: Color,
    val arcLabel: Color,
    val sunCore: Color,
    val sunHalo: Color,
)

private val DarkColors = LuachColors(
    background = Color(0xFF0A0B10),
    backgroundEnd = Color(0xFF0A0B10),
    surface = Color(0xFF12141C),
    surfaceRaised = Color(0xFF191D27),
    line = Color(0xFFFFFFFF).copy(alpha = 0.09f),
    lineStrong = Color(0xFFFFFFFF).copy(alpha = 0.17f),
    ink = Color(0xFFF2EDE4),
    muted = Color(0xFFA6ABBA),
    dim = Color(0xFF9096A6),
    gold = Color(0xFFD8B06A),
    goldBright = Color(0xFFF0D9A8),
    goldSoft = Color(0xFFD8B06A).copy(alpha = 0.12f),
    railTop = Color(0xFF0E1018),
    railBottom = Color(0xFF0A0B10),
    railInk = Color(0xFFF2EDE4),
    railMuted = Color(0xFFA6ABBA),
    railDim = Color(0xFF9096A6),
    railLine = Color(0xFFFFFFFF).copy(alpha = 0.14f),
    railActive = Color(0xFFFFFFFF).copy(alpha = 0.07f),
    railHover = Color(0xFFFFFFFF).copy(alpha = 0.04f),
    skyStops = persistentListOf(
        Color(0xFFD98F3A), Color(0xFFA05A3C), Color(0xFF4C3A58),
        Color(0xFF1D2340), Color(0xFF0B0E1C), Color(0xFF070810),
    ),
    skyVeil = Color(0xFF070810).copy(alpha = 0.55f),
    heroInk = Color(0xFFFDF9F1),
    heroGold = Color(0xFFF0D9A8),
    heroSub = Color(0xFFF5F1E9).copy(alpha = 0.82f),
    heroKicker = Color(0xFFF0D9A8).copy(alpha = 0.85f),
    glassFill = Color(0xFF0A0B10).copy(alpha = 0.36f),
    glassLine = Color(0xFFF0D9A8).copy(alpha = 0.34f),
    starAlpha = 0.8f,
    arcStart = Color(0xFF8FA6D4).copy(alpha = 0.5f),
    arcMid = Color(0xFFF0D9A8),
    arcEnd = Color(0xFFD98F3A),
    arcBase = Color(0xFFF6F1E6).copy(alpha = 0.16f),
    arcTick = Color(0xFFF6F1E6).copy(alpha = 0.35f),
    arcLabel = Color(0xFFF6F1E6).copy(alpha = 0.8f),
    sunCore = Color(0xFFFDF3DD),
    sunHalo = Color(0xFFFDEBC4).copy(alpha = 0.85f),
)

private val LightColors = LuachColors(
    background = Color(0xFFEDF2FA),
    backgroundEnd = Color(0xFFFAF4E6),
    surface = Color(0xFFFFFFFF),
    surfaceRaised = Color(0xFFEEEEF0),
    line = Color(0xFF1C1C1E).copy(alpha = 0.10f),
    lineStrong = Color(0xFF1C1C1E).copy(alpha = 0.18f),
    ink = Color(0xFF16171A),
    muted = Color(0xFF61636B),
    dim = Color(0xFF7A7C84),
    // ponytail: neutral greys everywhere, the accent is the only cool note — a quiet navy. The
    // `gold*` names stay so the dark palette and every call site keep working.
    gold = Color(0xFF33556F),
    goldBright = Color(0xFF234056),
    goldSoft = Color(0xFF33556F).copy(alpha = 0.09f),
    railTop = Color(0xFFFCFCFD),
    railBottom = Color(0xFFF1F1F3),
    railInk = Color(0xFF16171A),
    railMuted = Color(0xFF61636B),
    railDim = Color(0xFF7A7C84),
    railLine = Color(0xFF1C1C1E).copy(alpha = 0.16f),
    railActive = Color(0xFF1C1C1E).copy(alpha = 0.06f),
    railHover = Color(0xFF1C1C1E).copy(alpha = 0.04f),
    skyStops = persistentListOf(
        Color(0xFFFFD79C), Color(0xFFF3C48C), Color(0xFFDFC4B2),
        Color(0xFFC2D2EC), Color(0xFFA8C0E4), Color(0xFF98B3DE),
    ),
    skyVeil = Color(0xFFFFFFFF).copy(alpha = 0.34f),
    heroInk = Color(0xFF15192A),
    heroGold = Color(0xFF6D4712),
    heroSub = Color(0xFF15192A).copy(alpha = 0.78f),
    heroKicker = Color(0xFF6D4712).copy(alpha = 0.9f),
    glassFill = Color(0xFFFFFFFF).copy(alpha = 0.5f),
    glassLine = Color(0xFF6D4712).copy(alpha = 0.32f),
    starAlpha = 0f,
    arcStart = Color(0xFF47608F).copy(alpha = 0.55f),
    arcMid = Color(0xFFC98A2E),
    arcEnd = Color(0xFFA85C22),
    arcBase = Color(0xFF1E2234).copy(alpha = 0.20f),
    arcTick = Color(0xFF1E2234).copy(alpha = 0.40f),
    arcLabel = Color(0xFF181C2C).copy(alpha = 0.86f),
    sunCore = Color(0xFFFFF6DE),
    sunHalo = Color(0xFFFFF1CD).copy(alpha = 0.95f),
)

/** Display serif (numerals, headings) and the UI sans, both Hebrew-capable. */
@Immutable
data class LuachFonts(val display: FontFamily, val body: FontFamily)

private val LocalLuachColors = staticCompositionLocalOf { DarkColors }
private val LocalLuachFonts = staticCompositionLocalOf<LuachFonts> {
    error("LuachFonts not provided")
}

object LuachTheme {
    val colors: LuachColors
        @Composable @ReadOnlyComposable get() = LocalLuachColors.current

    val fonts: LuachFonts
        @Composable @ReadOnlyComposable get() = LocalLuachFonts.current
}

/**
 * The Material 3 scheme the design's own palette maps onto, so any Material component — and
 * Nucleus's title bar, which reads [androidx.compose.material3.MaterialTheme] at the call
 * site — lands in the luach's colours instead of Material's purple defaults.
 */
private fun LuachColors.materialScheme(dark: Boolean) = with(
    if (dark) darkColorScheme() else lightColorScheme()
) {
    copy(
        primary = gold,
        onPrimary = background,
        primaryContainer = goldSoft,
        onPrimaryContainer = gold,
        background = this@materialScheme.background,
        onBackground = ink,
        surface = this@materialScheme.surface,
        onSurface = ink,
        surfaceContainer = surfaceRaised,
        surfaceContainerHigh = surfaceRaised,
        onSurfaceVariant = muted,
        outline = lineStrong,
        outlineVariant = line,
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun LuachTheme(dark: Boolean, content: @Composable () -> Unit) {
    // ponytail: one face per family. Compose Resources' Font() cannot pass variation axes, so
    // the wght axis stays at its default and Compose synthesises bold. Register per-weight
    // static faces here if the design ever needs a true Light.
    val fonts = LuachFonts(
        display = FontFamily(Font(Res.font.FrankRuhlLibre_VariableFont_wght)),
        body = FontFamily(Font(Res.font.NotoSansHebrew_VariableFont_wdth_wght)),
    )
    val colors = remember(dark) { if (dark) DarkColors else LightColors }
    val scheme = remember(colors, dark) { colors.materialScheme(dark) }

    // Expressive brings the shape-morph and spatial motion the controls below rely on.
    MaterialExpressiveTheme(colorScheme = scheme) {
        CompositionLocalProvider(
            LocalLuachColors provides colors,
            LocalLuachFonts provides fonts,
            content = content,
        )
    }
}
