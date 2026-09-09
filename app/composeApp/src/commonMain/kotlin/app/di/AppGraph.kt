package app.di

import app.ui.LuachViewModel
import com.russhwolf.settings.Settings
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

/**
 * The application's Metro object graph.
 *
 * `ZmanimRepository` and `SettingsStore` are `@SingleIn(AppScope::class)`, so their formatters
 * and the platform preference store are built once; the ViewModel is unscoped, so
 * `viewModel { }` gets a fresh one per store owner.
 */
@DependencyGraph(AppScope::class)
interface AppGraph {
    val luachViewModel: LuachViewModel

    /** multiplatform-settings' no-arg factory: SharedPreferences, NSUserDefaults, … */
    @Provides
    @SingleIn(AppScope::class)
    fun provideSettings(): Settings = Settings()
}
