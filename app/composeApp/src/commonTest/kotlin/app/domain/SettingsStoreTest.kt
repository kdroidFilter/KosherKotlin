package app.domain

import com.russhwolf.settings.MapSettings
import kotlin.test.Test
import kotlin.test.assertEquals

class SettingsStoreTest {

    @Test
    fun defaultsApplyToAnEmptyStore() {
        assertEquals(LuachSettings(), SettingsStore(MapSettings()).load())
    }

    @Test
    fun everySettingSurvivesARoundTrip() {
        val backing = MapSettings()
        val changed = LuachSettings(
            useElevation = true,
            candleLightingOffset = 40,
            calculator = SunCalculator.SUN_TIMES,
            themeMode = ThemeMode.LIGHT,
            desktopWidget = true,
            desktopWidgetX = 120.5f,
            desktopWidgetY = 80f,
        )

        SettingsStore(backing).save(changed)

        // A fresh store over the same backing data is what a restart looks like.
        assertEquals(changed, SettingsStore(backing).load())
    }

    @Test
    fun anUnknownStoredEnumFallsBackInsteadOfCrashing() {
        val backing = MapSettings(
            "luach.calculator" to "REMOVED_IN_A_LATER_VERSION",
            "luach.themeMode" to "SEPIA",
        )
        val loaded = SettingsStore(backing).load()

        assertEquals(LuachSettings().calculator, loaded.calculator)
        assertEquals(LuachSettings().themeMode, loaded.themeMode)
    }

    @Test
    fun theDefaultThemeFollowsTheSystem() {
        assertEquals(ThemeMode.SYSTEM, LuachSettings().themeMode)
    }

    @Test
    fun aMissingWidgetPositionStaysUnset() {
        assertEquals(null, LuachSettings().desktopWidgetX)
        assertEquals(null, LuachSettings().desktopWidgetY)
        val loaded = SettingsStore(MapSettings()).load()
        assertEquals(null, loaded.desktopWidgetX)
        assertEquals(null, loaded.desktopWidgetY)
    }
}
