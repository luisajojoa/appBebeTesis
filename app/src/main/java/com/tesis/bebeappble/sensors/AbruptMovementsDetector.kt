package com.tesis.bebeappble.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import com.tesis.bebeappble.common.isInitialized
import kotlin.math.absoluteValue

class AbruptMovementsDetector (context: Context) : SensorEventListener {
    private var abruptMovementDetectedCallback : (()-> Unit )? = null
    private val sensorManager : SensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometerReading = FloatArray(3)
    private val newAccelerometerReading = FloatArray(3)

    init {
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.also { accelerometer ->
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI)
        }
    }

    fun addMovementsListener ( callback:() -> Unit){
        abruptMovementDetectedCallback = callback
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null ){
            return
        }

        if (!accelerometerReading.isInitialized()){
            System.arraycopy(event.values, 0, accelerometerReading,0, accelerometerReading.size)
        }else {
            System.arraycopy(event.values, 0, newAccelerometerReading,0, newAccelerometerReading.size)
            val difX=  (newAccelerometerReading[0]- accelerometerReading[0]).absoluteValue
            val difY=  (newAccelerometerReading[1]- accelerometerReading[1]).absoluteValue
            val difZ=  (newAccelerometerReading[2]- accelerometerReading[2]).absoluteValue
            val abruptMovementVelocity = 2.5
            if (difX >= abruptMovementVelocity || difY >=abruptMovementVelocity || difZ>= abruptMovementVelocity){
                abruptMovementDetectedCallback?.invoke()
            }
            System.arraycopy(newAccelerometerReading, 0, accelerometerReading,0, accelerometerReading.size)
        }
    }
}