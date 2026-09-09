package app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.domain.City
import app.domain.DefaultCities
import app.domain.LuachSettings
import app.domain.SettingsStore
import app.domain.ZmanimRepository
import dev.zacsweers.metro.Inject
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Instant

/**
 * MVVM shell, MVI core: intents fold into [inputs], and the rendered [state] is derived from
 * those inputs plus a minute clock. Nothing here touches Compose, so it is testable as a
 * plain class.
 */
@Inject
class LuachViewModel(
    private val repository: ZmanimRepository,
    private val settingsStore: SettingsStore,
) : ViewModel() {

    /** The only mutable state in the screen. Intents reduce into it; nothing else writes. */
    private val inputs = MutableStateFlow(Inputs(settings = settingsStore.load()))

    val state: StateFlow<LuachUiState> =
        combine(inputs, minuteClock) { current, now -> render(current, now) }
            .flowOn(Dispatchers.Default)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS),
                // Computed up front so the first frame already shows real zmanim instead of
                // a placeholder that would flash for one recomposition.
                initialValue = render(inputs.value, Clock.System.now()),
            )

    fun onIntent(intent: LuachIntent) {
        val before = inputs.value.settings
        val after = inputs.updateAndGet { reduce(it, intent) }.settings
        // Settings are the only durable part of the state, so persist exactly when they change.
        if (after != before) settingsStore.save(after)
    }

    private fun reduce(current: Inputs, intent: LuachIntent): Inputs =
        when (intent) {
            is LuachIntent.SelectCity -> current.copy(city = intent.city)
            is LuachIntent.Search -> current.copy(query = intent.query)
            LuachIntent.PreviousMonth -> current.copy(monthOffset = current.monthOffset - 1)
            LuachIntent.NextMonth -> current.copy(monthOffset = current.monthOffset + 1)
            is LuachIntent.SetThemeMode -> current.withSettings { copy(themeMode = intent.mode) }
            is LuachIntent.SetElevation -> current.withSettings { copy(useElevation = intent.enabled) }
            is LuachIntent.SetCandleOffset -> current.withSettings { copy(candleLightingOffset = intent.minutes) }
            is LuachIntent.SetCalculator -> current.withSettings { copy(calculator = intent.calculator) }
        }

    private fun render(inputs: Inputs, now: Instant): LuachUiState {
        val today = now.toLocalDateTime(TimeZone.of(inputs.city.timeZoneId)).date
        return LuachUiState(
            city = inputs.city,
            visibleCities = DefaultCities.matching(inputs.query),
            settings = inputs.settings,
            query = inputs.query,
            monthOffset = inputs.monthOffset,
            day = repository.day(inputs.city, today, now, inputs.settings),
            month = repository.month(today, inputs.monthOffset, inputs.city),
            events = repository.events(inputs.city, today, inputs.settings),
            limud = repository.limud(today, inputs.city),
        )
    }

    private data class Inputs(
        val city: City = DefaultCities.first(),
        val query: String = "",
        val monthOffset: Int = 0,
        val settings: LuachSettings = LuachSettings(),
    )

    private inline fun Inputs.withSettings(block: LuachSettings.() -> LuachSettings) =
        copy(settings = settings.block())

    private companion object {
        const val STOP_TIMEOUT_MS = 5_000L

        /**
         * Emits on every wall-clock minute boundary. Cold on purpose: `stateIn` owns the one
         * collection, so the ticker stops with the last subscriber instead of outliving the UI.
         */
        val minuteClock: Flow<Instant> = flow {
            while (true) {
                val now = Clock.System.now()
                emit(now)
                delay(MINUTE_MS - now.toEpochMilliseconds().mod(MINUTE_MS))
            }
        }

        const val MINUTE_MS = 60_000L
    }
}

private fun ImmutableList<City>.matching(query: String): ImmutableList<City> {
    val trimmed = query.trim()
    if (trimmed.isEmpty()) return this
    return filter {
        it.hebrewName.contains(trimmed) || it.latinName.contains(trimmed, ignoreCase = true)
    }.toImmutableList()
}
