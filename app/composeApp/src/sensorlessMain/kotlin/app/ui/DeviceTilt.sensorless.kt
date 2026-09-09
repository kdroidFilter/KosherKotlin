package app.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.geometry.Offset

/** Desktop and the browser have a pointer instead, which is the better instrument anyway. */
@Composable
actual fun rememberDeviceTilt(): State<Offset>? = null
