package com.tesis.bebeappble


import android.content.Context
import android.graphics.drawable.Drawable
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
import kotlin.properties.Delegates


class MainActivity : AppCompatActivity() {

    private lateinit var editTextMessage: EditText
    private lateinit var btnSendMessage: Button
    private lateinit var btnBebe: ImageButton
    private lateinit var mediaPlayer : MediaPlayer
    private lateinit var videoView : VideoView
    private lateinit var btnVideo : Button
    private lateinit var path1 : String
    private lateinit var path2 : String
    private lateinit var sliderTemp: SeekBar
    private var temperature : Int ?=null
    private var imagenBebe : Drawable?=null
    private lateinit var context : Context


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        context = this
        mediaPlayer = MediaPlayer.create(this, R.raw.bebellorando )

        retrieveViews()
        addListeners()

        path1 = "android.resource://" + packageName + "/" + R.raw.bebevideo
        path2 = "android.resource://" + packageName + "/" + R.raw.video1


        BluetoothCommunication.startBLE(this)
    }

    private fun retrieveViews() {
        btnSendMessage = findViewById(R.id.btnSendMessaje)
        editTextMessage = findViewById(R.id.editTextMsj)
        btnBebe = findViewById(R.id.btnBebe)
        videoView  = findViewById(R.id.videoViewBebe)
        btnVideo = findViewById(R.id.btnPlay)
        sliderTemp = findViewById(R.id.temperaturaSlider)

    }

    private fun addListeners() {
        btnSendMessage.setOnClickListener {
            val msj = editTextMessage.text.toString()
            BluetoothCommunication.sendMessage(msj)
        }
       /* btnBebe.setOnClickListener{
            var imageBebe = ContextCompat.getDrawable(this,R.drawable.bebeas)
            btnBebe.setImageDrawable(imageBebe)
            //mediaPlayer.start()
            playingVideo(videoView, path2)
        }*/
        btnVideo.setOnClickListener {
            playingVideo(videoView, path1)
        }
        var startPoint : Int?= null
        var endPoint :Int ?= null
        sliderTemp.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                when(progress){
                    in 30..32 -> imagenBebe = ContextCompat.getDrawable(context,R.drawable.bebeas)
                    in 33..35 -> imagenBebe = ContextCompat.getDrawable(context,R.drawable.redbaby)
                    in 36..38 -> imagenBebe = ContextCompat.getDrawable(context,R.drawable.redbaby1)
                    else -> imagenBebe = ContextCompat.getDrawable(context,R.drawable.redbaby2)
                }
                btnBebe.setImageDrawable(imagenBebe)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                startPoint = seekBar?.progress
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                endPoint = seekBar?.progress
            }
        })

    }
    private fun playingVideo(videoView: VideoView, path:String){
        videoView.setVideoURI(Uri.parse(path))
        val isPlaying = videoView.isPlaying
        val msg = getString(if (isPlaying) R.string.paused else R.string.playing)
        Toast.makeText(this@MainActivity, msg, Toast.LENGTH_SHORT).show()
        if (isPlaying) {
            videoView.pause()
        } else {
            videoView.start()
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