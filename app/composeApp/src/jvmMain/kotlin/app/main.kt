package app

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberWindowState
import app.di.AppGraph
import app.domain.LuachSettings
import app.ui.CompactBreakpoint
import app.ui.HeroWidget
import app.ui.LuachIntent
import app.ui.theme.LuachTheme
import dev.nucleusframework.application.DecoratedWindow
import dev.nucleusframework.application.NucleusBackend
import dev.nucleusframework.application.NucleusWindow
import dev.nucleusframework.application.nucleusApplication
import dev.nucleusframework.autolaunch.AutoLaunch
import dev.nucleusframework.core.runtime.ExecutableRuntime
import dev.nucleusframework.energymanager.EnergyManager
import dev.nucleusframework.window.DecoratedWindowScope
import dev.nucleusframework.window.TitleBarPlacement
import dev.nucleusframework.window.WindowScaffold
import dev.nucleusframework.window.macOSLargeCornerRadius
import dev.nucleusframework.window.material.MaterialDecoratedWindow
import dev.nucleusframework.window.material.MaterialTitleBar
import dev.nucleusframework.window.newFullscreenControls
import dev.nucleusframework.window.styling.LocalTitleBarStyle
import dev.zacsweers.metro.createGraph
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.mapNotNull

fun main(args: Array<String>) = nucleusApplication(
    args = args,
    backend = NucleusBackend.Tao,
    // Login-start with only the widget (hiddenFromDock) must not plant a Dock icon.
    dockIconFollowsWindows = true,
) {
    val graph = remember { createGraph<AppGraph>() }
    val viewModel = remember { graph.luachViewModel }
    val state by viewModel.state.collectAsState()

    val widgetOnlyLaunch = remember {
        startedAsWidgetOnly(args) && viewModel.state.value.settings.desktopWidget
    }
    var appVisible by remember { mutableStateOf(!widgetOnlyLaunch) }
    var widgetLocked by remember { mutableStateOf(false) }
    var mainWindow by remember { mutableStateOf<NucleusWindow?>(null) }

    // macOS: the login AppleEvent is delivered after NSApp.run(), so the first
    // wasStartedAtLogin() in main() is usually false. Poll once composition is up.
    LaunchedEffect(Unit) {
        if (AutoLaunch.wasStartedAtLogin(args) && state.settings.desktopWidget) {
            appVisible = false
        }
    }

    LaunchedEffect(state.settings.desktopWidget) {
        syncAutoLaunch(enabled = state.settings.desktopWidget)
    }

    val mainState = rememberWindowState(
        position = WindowPosition.Aligned(Alignment.Center),
        width = 1280.dp,
        height = 900.dp,
    )

    // Widget-only or minimized: drop the process into Nucleus efficiency mode
    // (EcoQoS / Darwin BG / nice+ioprio). Restore when the main window is
    // shown again, and on dispose so a quit does not leave it set.
    val saveEnergy = !appVisible || mainState.isMinimized
    LaunchedEffect(saveEnergy) {
        if (saveEnergy) EnergyManager.enableEfficiencyMode()
        else EnergyManager.disableEfficiencyMode()
    }
    DisposableEffect(Unit) {
        onDispose { EnergyManager.disableEfficiencyMode() }
    }

    fun revealApp() {
        appVisible = true
        mainWindow?.apply {
            setMinimized(false)
            show()
            toFront()
            requestFocus()
        }
    }

    fun disableWidget() {
        if (!appVisible) revealApp()
        viewModel.onIntent(LuachIntent.SetDesktopWidget(false))
    }

    // The window chrome is themed out here, outside App(), so it can only follow the OS theme —
    // the in-app light/dark override lives behind the settings store App() owns.
    val chromeDark = isSystemInDarkTheme()

    // Themed twice on purpose: MaterialDecoratedWindow reads MaterialTheme at the call site to
    // colour the chrome, and Tao gives each window its own ComposeScene, so the locals provided
    // out here do not reach the content inside.
    LuachTheme(dark = chromeDark) {
        MaterialDecoratedWindow(
            onCloseRequest = {
                if (state.settings.desktopWidget) appVisible = false else exitApplication()
            },
            state = mainState,
            visible = appVisible,
            title = "Luach",
            // Never narrow enough to fold the rail away: the desktop window stops at the
            // compact breakpoint, so the side menu is always there.
            minimumSize = DpSize(CompactBreakpoint, 640.dp),
        ) {
            LaunchedEffect(nucleusWindow) { mainWindow = nucleusWindow }
            LuachTheme(dark = chromeDark) {
                WindowScaffold(
                    // Marker modifier: opts the window into the macOS 26 large corner radius.
                    modifier = Modifier.macOSLargeCornerRadius(),
                    titleBar = { LuachTitleBar() },
                    // Overlay, not Docked: the sky runs to the top edge of the window and the
                    // bar floats on it, instead of the app starting under a separate strip.
                    // The bar stays composed in fullscreen — auto-hiding it takes the traffic
                    // lights with it, and there is then no way out of fullscreen from the app.
                    titleBarPlacement = TitleBarPlacement.Overlay(autoHideInFullscreen = false),
                ) { chrome ->
                    LuachApp(state = state, onIntent = viewModel::onIntent, topInset = chrome.calculateTopPadding())
                }
            }
        }
    }

    if (state.settings.desktopWidget) {
        val widgetState = rememberWindowState(
            position = restoredWidgetPosition(state.settings),
            size = DpSize(480.dp, 260.dp),
        )
        LaunchedEffect(widgetState) {
            snapshotFlow { widgetState.position }
                .mapNotNull { it as? WindowPosition.Absolute }
                .distinctUntilChanged()
                .debounce(250)
                .collect { pos ->
                    viewModel.onIntent(LuachIntent.SetDesktopWidgetPosition(pos.x.value, pos.y.value))
                }
        }
        DisposableEffect(widgetState) {
            onDispose {
                val pos = widgetState.position
                if (pos is WindowPosition.Absolute) {
                    viewModel.onIntent(LuachIntent.SetDesktopWidgetPosition(pos.x.value, pos.y.value))
                }
            }
        }
        DecoratedWindow(
            onCloseRequest = ::disableWidget,
            state = widgetState,
            title = "Luach",
            resizable = false,
            undecorated = true,
            transparent = true,
            hiddenFromDock = true,
            nativeContextMenu = true,
            visibleOnAllWorkspaces = true,
            forceX11 = true,
            alwaysOnBottom = true,
        ) {
            HeroWidget(
                day = state.day,
                themeMode = state.settings.themeMode,
                taoWindow = nucleusWindow.unsafe.taoWindow,
                locked = widgetLocked,
                onOpenApp = ::revealApp,
                onToggleLock = { widgetLocked = !widgetLocked },
                onRemove = ::disableWidget,
                onQuit = ::exitApplication,
            )
        }
    }
}

/** Traffic lights and a drag area, on nothing: no plate, no divider, no title — just the sky. */
@Composable
private fun DecoratedWindowScope.LuachTitleBar() {
    val style = LocalTitleBarStyle.current

    MaterialTitleBar(
        // Read by the backend's TitleBar: the bar rides the menu bar in native fullscreen
        // instead of staying pinned to the layout. A tag only on Tao, which animates it itself.
        modifier = Modifier.newFullscreenControls(),
        style = style.copy(
            colors = style.colors.copy(
                background = Color.Transparent,
                inactiveBackground = Color.Transparent,
                border = Color.Transparent,
                content = LuachTheme.colors.heroInk,
            ),
        ),
    )
}

/**
 * Login-launched by Nucleus AutoLaunch, or the same CLI marker it writes into the
 * startup entry (`--nucleus-autostart`) — the latter also lets a packaged-dev run
 * be tested with `run --args="--nucleus-autostart"`.
 */
private fun startedAsWidgetOnly(args: Array<String>): Boolean =
    AutoLaunch.wasStartedAtLogin(args) || args.contains("--nucleus-autostart")

/**
 * Keep the OS login item in lockstep with the widget setting. Gradle/IDE launches
 * are [ExecutableRuntime.isDev]: registering those would put `java` on the user's
 * startup list, so they are skipped.
 */
private fun syncAutoLaunch(enabled: Boolean) {
    if (ExecutableRuntime.isDev()) return
    if (enabled) AutoLaunch.enable() else AutoLaunch.disable()
}

private fun restoredWidgetPosition(settings: LuachSettings): WindowPosition {
    val x = settings.desktopWidgetX
    val y = settings.desktopWidgetY
    return if (x != null && y != null) {
        WindowPosition.Absolute(x.dp, y.dp)
    } else {
        WindowPosition.Aligned(Alignment.BottomEnd)
    }
}
