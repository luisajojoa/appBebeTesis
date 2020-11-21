package com.tesis.bebeappble

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.tesis.bebeappble.bluetooth.BluetoothCommunication
import com.tesis.bebeappble.common.Message
import com.tesis.bebeappble.bluetooth.TAG

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        BluetoothCommunication.startBLE(this)
    }

    override fun onStart() {
        super.onStart()
        BluetoothCommunication.startAdvertising()
        BluetoothCommunication.listenNewMessages { message ->
            when(message) {
                is Message.HeartRateMessage -> Log.i(TAG, "Heart Rate: ${message.value}")
                is Message.TemperatureMessage ->Log.i(TAG, "Temperature: ${message.value}")
                is Message.BreathingRateMessage -> Log.i(TAG, "Breathing Rate: ${message.value}")
            }
        }
    }

    override fun onStop() {
        super.onStop()
        BluetoothCommunication.stopAdvertising()
    }
}