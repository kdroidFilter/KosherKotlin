package app.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.CoreMotion.CMMotionManager
import platform.Foundation.NSOperationQueue

/**
 * Full deflection at this much lean, as a fraction of gravity — CoreMotion reports gravity
 * normalised, unlike Android's raw accelerometer. A quarter g is about 15° of tilt.
 */
private const val TiltRange = 0.25f

/** Device motion is already sensor-fused, so this only takes the last edge off. */
private const val Smoothing = 0.2f

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun rememberDeviceTilt(): State<Offset>? {
    val tilt = remember { mutableStateOf(Offset.Zero) }

    DisposableEffect(Unit) {
        val manager = CMMotionManager()
        if (!manager.deviceMotionAvailable) return@DisposableEffect onDispose { }

        manager.deviceMotionUpdateInterval = 1.0 / 30.0
        manager.startDeviceMotionUpdatesToQueue(NSOperationQueue.mainQueue) { motion, _ ->
            motion?.gravity?.useContents {
                // Leaning the right edge down should push the sky left, hence the sign on x.
                val target = Offset(
                    (-x / TiltRange).toFloat().coerceIn(-1f, 1f),
                    (y / TiltRange).toFloat().coerceIn(-1f, 1f),
                )
                val current = tilt.value
                tilt.value = current + (target - current) * Smoothing
            }
        }
        onDispose { manager.stopDeviceMotionUpdates() }
    }
    return tilt
}
