package app.ui

import androidx.compose.runtime.Immutable
import app.domain.City
import app.domain.DaySnapshot
import app.domain.HolidayEvent
import app.domain.LimudCard
import app.domain.LuachSettings
import app.domain.MonthGrid
import app.domain.SunCalculator
import app.domain.ThemeMode
import kotlinx.collections.immutable.ImmutableList

/**
 * The single state object the whole screen renders from.
 *
 * Every field is either a primitive, an `@Immutable` value, or an `ImmutableList`, so the
 * Compose compiler can prove the screen skippable. See `compose_stability.conf` and
 * `-PcomposeReports=true`.
 */
@Immutable
data class LuachUiState(
    val city: City,
    val visibleCities: ImmutableList<City>,
    val settings: LuachSettings,
    val query: String,
    val monthOffset: Int,
    val day: DaySnapshot,
    val month: MonthGrid,
    val events: ImmutableList<HolidayEvent>,
    val limud: ImmutableList<LimudCard>,
)

/** Everything the user can do. The UI sends these; it never mutates state directly. */
sealed interface LuachIntent {
    data class SelectCity(val city: City) : LuachIntent
    data class Search(val query: String) : LuachIntent
    data object PreviousMonth : LuachIntent
    data object NextMonth : LuachIntent
    data class SetThemeMode(val mode: ThemeMode) : LuachIntent
    data class SetElevation(val enabled: Boolean) : LuachIntent
    data class SetCandleOffset(val minutes: Int) : LuachIntent
    data class SetCalculator(val calculator: SunCalculator) : LuachIntent
}
