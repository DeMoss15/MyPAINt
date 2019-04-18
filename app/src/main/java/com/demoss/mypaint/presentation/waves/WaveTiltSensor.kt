package com.demoss.mypaint.presentation.waves

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

interface TiltListener {
    fun onTilt(pitch: Double, roll: Double)
}

interface TiltSensor {
    fun addListener(tiltListener: TiltListener)

    fun register()

    fun unregister()
}

class WaveTiltSensor(context: Context) : SensorEventListener, TiltSensor {

    private val sensorManager: SensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accSensor: Sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val magneticSensor: Sensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
    private val listeners = mutableListOf<TiltListener>()

    private val rotationMatrix = FloatArray(9)
    private val accelerometerValues = FloatArray(3)
    private val magneticValues = FloatArray(3)
    private val orientationAngles = FloatArray(3)

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // nothing to do here
    }

    override fun onSensorChanged(event: SensorEvent?) {
        fun SensorEvent.copyValues(values: FloatArray) =
            System.arraycopy(this.values, 0, values, 0, values.size)

        when (event?.sensor?.type) {
            Sensor.TYPE_ACCELEROMETER -> event.copyValues(accelerometerValues)
            Sensor.TYPE_MAGNETIC_FIELD -> event.copyValues(magneticValues)
        }

        SensorManager.getRotationMatrix(rotationMatrix, null, accelerometerValues, magneticValues)
        SensorManager.getOrientation(rotationMatrix, orientationAngles)

        val pitchInRad = orientationAngles[1].toDouble()
        val rollInRad = orientationAngles[2].toDouble()
        listeners.forEach { it.onTilt(pitchInRad, rollInRad) }
    }

    override fun addListener(tiltListener: TiltListener) {
        listeners.add(tiltListener)
    }

    override fun register() {
        sensorManager.registerListener(this, accSensor, SensorManager.SENSOR_DELAY_UI)
        sensorManager.registerListener(this, magneticSensor, SensorManager.SENSOR_DELAY_UI)
    }

    override fun unregister() {
        listeners.clear()
        sensorManager.unregisterListener(this, accSensor)
        sensorManager.unregisterListener(this, magneticSensor)
    }
}