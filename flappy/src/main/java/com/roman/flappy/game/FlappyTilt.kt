package com.roman.flappy.game

import android.content.Context
import android.content.Context.SENSOR_SERVICE
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

/**
 *
 * Author: romanvysotsky
 * Created: 13.02.24
 */

class FlappyTilt(
    private val context: Context,
    private val onEvent: (x: Float, y: Float) -> Unit,
): SensorEventListener {

    private var sensorManager: SensorManager? = null
    private var accelerometer: Sensor? = null

    private var filteredX = 0f
    private var filteredY = 0f
    private val alpha = 0.1f  // Smoothing factor, adjust as needed
    private val naturalPhoneTilt = 0.4f // So that the user doesn't have to hold the phone weirdly; better ux for the car control


    fun start() {
        val sensorManager = context.getSystemService(SENSOR_SERVICE) as SensorManager
        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME)
        this.sensorManager = sensorManager
        this.accelerometer = accelerometer
    }

    fun stop() {
        sensorManager?.unregisterListener(this)
        this.sensorManager = null
        this.accelerometer = null
    }


    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            val x = event.values[0]
            val y = event.values[1]

            // Apply low-pass filter
            val filteredX = alpha * x + (1 - alpha) * filteredX
            val filteredY = alpha * y + (1 - alpha) * filteredY

            // Update object's position based on filtered values
            onEvent(filteredX, filteredY - naturalPhoneTilt)
        }
    }

    override fun onAccuracyChanged(p0: Sensor, p1: Int) {
        //nop
    }


}
