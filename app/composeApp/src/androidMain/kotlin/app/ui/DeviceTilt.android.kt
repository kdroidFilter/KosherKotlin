package app.ui

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalContext

/**
 * Full deflection at this much lean, in m/s². A quarter of gravity is about 15° of tilt, which
 * is as far as anyone rotates a phone they are still reading. Tune it here, not at the call site.
 */
private const val TiltRange = 2.5f

/** Fraction of each new reading that is let through. Accelerometers are noisy; this is the filter. */
private const val Smoothing = 0.12f

@Composable
actual fun rememberDeviceTilt(): State<Offset>? {
    val context = LocalContext.current
    val manager = remember(context) { context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager }
    val sensor = remember(manager) { manager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) }
    // No accelerometer (a TV, a Chromebook): say so, so the hero falls back to the pointer
    // rather than leaning on a reading that will never move.
    if (manager == null || sensor == null) return null

    val tilt = remember { mutableStateOf(Offset.Zero) }
    DisposableEffect(manager, sensor) {
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                // Device axes: +x towards the right edge, +y towards the top. Leaning the right
                // edge down should push the sky left, hence the sign on x.
                val target = Offset(
                    (-event.values[0] / TiltRange).coerceIn(-1f, 1f),
                    (event.values[1] / TiltRange).coerceIn(-1f, 1f),
                )
                val current = tilt.value
                tilt.value = current + (target - current) * Smoothing
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
        }

        manager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_UI)
        onDispose { manager.unregisterListener(listener) }
    }
    return tilt
}
