package com.tesis.bebeappble

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.tesis.bebeappble.bluetooth.BluetoothCommunication

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        BluetoothCommunication.startBLE(this)
    }

    override fun onStart() {
        super.onStart()
        BluetoothCommunication.startAdvertising()
    }

    override fun onStop() {
        super.onStop()
        BluetoothCommunication.stopAdvertising()
    }
}