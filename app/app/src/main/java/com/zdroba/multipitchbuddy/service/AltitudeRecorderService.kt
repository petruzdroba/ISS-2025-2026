package com.zdroba.multipitchbuddy.service

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.zdroba.multipitchbuddy.entity.AltitudeReading
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.Instant
import kotlin.math.abs
import kotlin.math.pow

class AltitudeRecorderService(context: Context): IAltitudeRecorderService {

    private val _readings = MutableStateFlow<AltitudeReading?>(null)
    override val readings: Flow<AltitudeReading?> = _readings.asStateFlow()

    private var sensorEventListener: SensorEventListener? = null
    private var lastEmittedAltitude: Double? = null
    private var lastEmissionTime: Long = 0
    private var baselinePressure: Double? = null

    private val THROTTLE_INTERVAL_MS = 750L
    private val MIN_ALTITUDE_CHANGE = 0.25

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    override suspend fun start() {
        val pressureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE) ?: return

        if (sensorEventListener != null) return

        sensorEventListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                val pressure = event.values[0].toDouble()

                if (pressure <= 300 || pressure >= 1100) return

                if (baselinePressure == null) {
                    baselinePressure = pressure
                    _readings.value = AltitudeReading(0.0, Instant.now())
                    lastEmittedAltitude = 0.0
                    lastEmissionTime = System.currentTimeMillis()
                    return
                }

                val altitude = convertPressureToRelativeAltitude(pressure)
                val now = System.currentTimeMillis()
                val timeSinceLast = now - lastEmissionTime

                val shouldEmit = timeSinceLast >= THROTTLE_INTERVAL_MS ||
                        (lastEmittedAltitude != null &&
                                abs(altitude - lastEmittedAltitude!!) >= MIN_ALTITUDE_CHANGE)

                if (shouldEmit) {
                    lastEmittedAltitude = altitude
                    lastEmissionTime = now
                    _readings.value = AltitudeReading(altitude, Instant.now())
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        sensorManager.registerListener(
            sensorEventListener,
            pressureSensor,
            SensorManager.SENSOR_DELAY_NORMAL
        )
    }

    override suspend fun end() {
        sensorEventListener?.let {
            sensorManager.unregisterListener(it)
            sensorEventListener = null
        }

        _readings.value = null
        lastEmittedAltitude = null
        lastEmissionTime = 0
        baselinePressure = null
    }

    private fun convertPressureToRelativeAltitude(currentPressure: Double): Double {
        val baseline = baselinePressure ?: return 0.0
        return 44330.0 * (1.0 - (currentPressure / baseline).pow(1.0 / 5.255))
    }
}