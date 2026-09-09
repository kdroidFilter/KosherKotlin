package app.ui

import androidx.compose.foundation.ContextMenuArea
import androidx.compose.foundation.ContextMenuItem
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.isPrimaryPressed
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import app.domain.DaySnapshot
import app.domain.ThemeMode
import app.ui.theme.LuachTheme
import dev.nucleusframework.window.tao.TaoWindow

private const val WidgetDragThresholdPx = 6f

/**
 * The hero, clipped to a rounded card, living in the always-on-bottom overlay.
 *
 * A primary click (press + release without a drag) opens the main window. When unlocked, a
 * press that then moves past a small threshold hands the move to the compositor
 * ([TaoWindow.dragWindow]). Right-click is left for the context menu.
 */
@Composable
internal fun HeroWidget(
    day: DaySnapshot,
    themeMode: ThemeMode,
    taoWindow: TaoWindow?,
    locked: Boolean,
    onOpenApp: () -> Unit,
    onToggleLock: () -> Unit,
    onRemove: () -> Unit,
    onQuit: () -> Unit,
) {
    val dark = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
    }
    val openApp by rememberUpdatedState(onOpenApp)

    LuachTheme(dark = dark) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            ContextMenuArea(
                items = {
                    listOf(
                        ContextMenuItem("פתח את האפליקציה", onOpenApp),
                        ContextMenuItem(if (locked) "שחרר מיקום" else "נעל מיקום", onToggleLock),
                        ContextMenuItem("הסר יישומון", onRemove),
                        ContextMenuItem("יציאה", onQuit),
                    )
                },
            ) {
                Hero(
                    day = day,
                    compact = true,
                    widget = true,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(20.dp))
                        .pointerHoverIcon(PointerIcon.Hand)
                        .pointerInput(taoWindow, locked) {
                            awaitPointerEventScope {
                                var pendingClick = false
                                var pressPosition = Offset.Zero
                                while (true) {
                                    val event = awaitPointerEvent(PointerEventPass.Main)
                                    when (event.type) {
                                        PointerEventType.Press -> {
                                            if (!event.buttons.isPrimaryPressed) continue
                                            pendingClick = true
                                            pressPosition = event.changes.first().position
                                        }
                                        PointerEventType.Move -> {
                                            if (!pendingClick || locked) continue
                                            val distance =
                                                (event.changes.first().position - pressPosition).getDistance()
                                            if (distance > WidgetDragThresholdPx) {
                                                taoWindow?.dragWindow()
                                                pendingClick = false
                                            }
                                        }
                                        PointerEventType.Release -> {
                                            if (pendingClick) openApp()
                                            pendingClick = false
                                        }
                                        else -> Unit
                                    }
                                }
                            }
                        },
                )
            }
        }
    }
}
