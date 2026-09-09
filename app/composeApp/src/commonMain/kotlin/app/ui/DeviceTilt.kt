package app.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.geometry.Offset

/**
 * How far the device is leaning, as -1..1 from level on each axis, or `null` on the platforms
 * that have no motion sensor — there the hero falls back to following the pointer.
 *
 * Read the value from a draw or layer lambda: it updates at sensor rate and has no business
 * recomposing anything.
 */
@Composable
expect fun rememberDeviceTilt(): State<Offset>?
