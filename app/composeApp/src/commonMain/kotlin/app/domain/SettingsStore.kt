package app.domain

import com.russhwolf.settings.Settings
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn

/**
 * Persists [LuachSettings] with multiplatform-settings (SharedPreferences, NSUserDefaults,
 * Preferences, localStorage — whichever the target provides).
 *
 * Reads and writes are synchronous and tiny, so this exposes no coroutines and owns no scope:
 * the caller decides when to load and save.
 */
@SingleIn(AppScope::class)
@Inject
class SettingsStore(private val settings: Settings) {

    fun load(): LuachSettings {
        val defaults = LuachSettings()
        return LuachSettings(
            useElevation = settings.getBoolean(USE_ELEVATION, defaults.useElevation),
            candleLightingOffset = settings.getInt(CANDLE_OFFSET, defaults.candleLightingOffset),
            calculator = settings.getStringOrNull(CALCULATOR)
                ?.let { name -> SunCalculator.entries.firstOrNull { it.name == name } }
                ?: defaults.calculator,
            themeMode = settings.getStringOrNull(THEME_MODE)
                ?.let { name -> ThemeMode.entries.firstOrNull { it.name == name } }
                ?: defaults.themeMode,
            desktopWidget = settings.getBoolean(DESKTOP_WIDGET, defaults.desktopWidget),
            desktopWidgetX = settings.getFloatOrNull(DESKTOP_WIDGET_X),
            desktopWidgetY = settings.getFloatOrNull(DESKTOP_WIDGET_Y),
        )
    }

    fun save(value: LuachSettings) {
        settings.putBoolean(USE_ELEVATION, value.useElevation)
        settings.putInt(CANDLE_OFFSET, value.candleLightingOffset)
        settings.putString(CALCULATOR, value.calculator.name)
        settings.putString(THEME_MODE, value.themeMode.name)
        settings.putBoolean(DESKTOP_WIDGET, value.desktopWidget)
        value.desktopWidgetX?.let { settings.putFloat(DESKTOP_WIDGET_X, it) }
            ?: settings.remove(DESKTOP_WIDGET_X)
        value.desktopWidgetY?.let { settings.putFloat(DESKTOP_WIDGET_Y, it) }
            ?: settings.remove(DESKTOP_WIDGET_Y)
    }

    private companion object {
        const val USE_ELEVATION = "luach.useElevation"
        const val CANDLE_OFFSET = "luach.candleLightingOffset"
        const val CALCULATOR = "luach.calculator"
        const val THEME_MODE = "luach.themeMode"
        const val DESKTOP_WIDGET = "luach.desktopWidget"
        const val DESKTOP_WIDGET_X = "luach.desktopWidgetX"
        const val DESKTOP_WIDGET_Y = "luach.desktopWidgetY"
    }
}
