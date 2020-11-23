package com.tesis.bebeappble


import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.tesis.bebeappble.bluetooth.BluetoothCommunication
import com.tesis.bebeappble.bluetooth.TAG
import com.tesis.bebeappble.common.Message
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private lateinit var editTextMessage: EditText
    private lateinit var btnSendMessage: Button
    private lateinit var btnBebe: ImageButton
    private lateinit var mediaPlayer : MediaPlayer
    private lateinit var videoView : VideoView
    private lateinit var btnVideo : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mediaPlayer = MediaPlayer.create(this, R.raw.bebellorando )

        retrieveViews()
        addListeners()

        val path = "android.resource://" + packageName + "/" + R.raw.bebevideo
        videoView.setVideoURI(Uri.parse(path))


        BluetoothCommunication.startBLE(this)
    }

    private fun retrieveViews() {
        btnSendMessage = findViewById(R.id.btnSendMessaje)
        editTextMessage = findViewById(R.id.editTextMsj)
        btnBebe = findViewById(R.id.btnBebe)
        videoView  = findViewById(R.id.videoViewBebe)
        btnVideo = findViewById(R.id.btnPlay)

    }

    private fun addListeners() {
        btnSendMessage.setOnClickListener {
            val msj = editTextMessage.text.toString()
            BluetoothCommunication.sendMessage(msj)
        }
        btnBebe.setOnClickListener{
            var imageBebe = ContextCompat.getDrawable(this,R.drawable.bebeas)
            btnBebe.setImageDrawable(imageBebe)
            mediaPlayer.start()
        }
        btnVideo.setOnClickListener {
            val isPlaying = videoView.isPlaying
            btnPlay.setText(if (isPlaying) R.string.play else R.string.pause)

            val msg = getString(if (isPlaying) R.string.paused else R.string.playing)
            Toast.makeText(this@MainActivity, msg, Toast.LENGTH_SHORT).show()
            if (isPlaying) {
                videoView.pause()
            } else {
                videoView.start()
            }
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
        //mediaPlayer.stop()
    }
}