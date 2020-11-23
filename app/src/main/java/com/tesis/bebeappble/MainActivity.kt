package com.tesis.bebeappble

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.tesis.bebeappble.bluetooth.BluetoothCommunication


import com.tesis.bebeappble.bluetooth.TAG
import com.tesis.bebeappble.common.Message

class MainActivity : AppCompatActivity() {

    private lateinit var editTextMessage: EditText
    private lateinit var btnSendMessage: Button
    private lateinit var btnBebe: ImageButton
    private lateinit var imgTermometer: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        retrieveViews()
        addListeners()
        BluetoothCommunication.startBLE(this)
    }

    private fun retrieveViews() {
        btnSendMessage = findViewById(R.id.btnSendMessaje)
        editTextMessage = findViewById(R.id.editTextMsj)
        btnBebe = findViewById(R.id.btnBebe)
        imgTermometer = findViewById(R.id.imgTermometer)
    }

    private fun addListeners() {
        btnSendMessage.setOnClickListener {
            val msj = editTextMessage.text.toString()
            BluetoothCommunication.sendMessage(msj)
        }
        btnBebe.setOnClickListener{
            var imageBebe = ContextCompat.getDrawable(this,R.drawable.bebeas)
            btnBebe.setImageDrawable(imageBebe)
        }
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
        BluetoothCommunication.stopListeningMessages()
    }
}