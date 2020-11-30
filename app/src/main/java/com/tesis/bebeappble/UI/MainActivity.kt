package com.tesis.bebeappble.UI


import android.graphics.drawable.Drawable
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Message
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintHelper
import androidx.constraintlayout.widget.ConstraintLayout
import com.tesis.bebeappble.R
import com.tesis.bebeappble.bluetooth.BluetoothCommunication
import com.tesis.bebeappble.bluetooth.MessagesReceivedManager
import com.tesis.bebeappble.sensors.AbruptMovementsDetector
import com.tesis.bebeappble.vibration.HearRateVibration


class MainActivity : AppCompatActivity() {

    private lateinit var editTextMessage: EditText
    private lateinit var btnSendMessage: Button

    private lateinit var mediaPlayer : MediaPlayer
    private lateinit var videoView : VideoView
    private lateinit var btnVideo : Button
    private lateinit var path1 : String
    private lateinit var path2 : String
    private lateinit var sliderTemp: SeekBar
    private var imageBaby : Drawable?=null
    private lateinit var imageBabe : ConstraintLayout
    private var enableVideo =0
    private lateinit var termometerIcon : ImageButton
    private lateinit var heartIcon : ImageButton
    private lateinit var hearRateVibration :HearRateVibration
    private lateinit var measurementsDialog: MeasurementsDialog


    companion object{
        const val BABY_CRYING = 1
        const val BABY_NOT_CRYING = 0
        const val HEART_RATE_MESSAGE = "HeartRateMessage"
        const val BREATHING_RATE_MESSAGE = "BreathingRateMessage"
        const val CRY_MESSAGE = "CryMessage"
        const val TEMPERATURE_MESSAGE = "TemperatureMessage"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mediaPlayer = MediaPlayer.create(this,
            R.raw.bebellorando
        )

        hearRateVibration = HearRateVibration(this )

        retrieveViews()
        addListeners()
        measurementsDialog = MeasurementsDialog(this)



        path1 = "android.resource://" + packageName + "/" + R.raw.videotermometro
        // path2 = "android.resource://" + packageName + "/" + R.raw.video1
        videoView.setVideoURI(Uri.parse(path1))

        BluetoothCommunication.startBLE(this)

        // ESTE ES EL CALLBACK DEL ABRUPTMOVEMENTS
        AbruptMovementsDetector(this).addMovementsListener {
            Log.i("lajm", "Movimiento abrupto! cuidado con Victoria ")
            mediaPlayer.start()
        }

    }

    private fun retrieveViews() {
        videoView  = findViewById(R.id.videoViewBebe)
        btnVideo = findViewById(R.id.btnPlay)
        sliderTemp = findViewById(R.id.temperaturaSlider)
        termometerIcon = findViewById(R.id.imgBtnTermometro)
        heartIcon = findViewById(R.id.buttonHeartIconb)
        imageBabe = findViewById(R.id.constraintBaby)
    }

    private fun addListeners() {
        //ENVIO DE DATOS!!
        /*btnSendMessage.setOnClickListener {
            val msj = editTextMessage.text.toString()
            BluetoothCommunication.sendMessage(msj)
        }*/
       /* btnBebe.setOnClickListener{
            var imageBebe = ContextCompat.getDrawable(this,R.drawable.bebeas)
            btnBebe.setImageDrawable(imageBebe)
            //mediaPlayer.start()
            playingVideo(videoView, path2)
        }*/

        termometerIcon.setOnClickListener{
            measurementsDialog.showMeasure(R.drawable.ic_temperature__mesure, MessagesReceivedManager.getMessage(
                TEMPERATURE_MESSAGE)){}
        }

        heartIcon.setOnClickListener {
            val hearRateMessage = MessagesReceivedManager.getMessage(HEART_RATE_MESSAGE)
            hearRateVibration.start(hearRateMessage?.value)
            measurementsDialog.showMeasure(R.drawable.ic_heart_rate_mesure, hearRateMessage){
                hearRateVibration.stop()
            }
        }

        btnVideo.setOnLongClickListener {
            videoView.visibility = View.INVISIBLE
//            Log.i("video", "videoview is visible? : ${videoView.isVisible}")
            return@setOnLongClickListener true
        }

        btnVideo.setOnClickListener {

            if( enableVideo ==0){
                mediaPlayer.start()
                enableVideo =1

            }else{
                enableVideo= 0
                mediaPlayer.pause()

            }

        }

        var startPoint : Int?= null
        var endPoint :Int ?= null
        sliderTemp.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                /*when(progress){
                    in 30..32 -> imageBaby = ContextCompat.getDrawable(context,R.drawable.bebeas)
                    in 33..35 -> imageBaby = ContextCompat.getDrawable(context,R.drawable.redbaby)
                    in 36..38 -> imageBaby = ContextCompat.getDrawable(context,R.drawable.redbaby1)
                    else -> imageBaby = ContextCompat.getDrawable(context,R.drawable.redbaby2)
                }*/
                //btnBebe.setImageDrawable(imageBaby)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                startPoint = seekBar?.progress
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                endPoint = seekBar?.progress
            }
        })

    }
  /*  private fun playingVideo(videoView: VideoView, path:String){
        videoView.setVideoURI(Uri.parse(path))
        val isPlaying = videoView.isPlaying
        val msg = getString(if (isPlaying) R.string.paused else R.string.playing)
        Toast.makeText(this@MainActivity, msg, Toast.LENGTH_SHORT).show()
        if (isPlaying) {
            videoView.pause()
        } else {
            videoView.start()
        }
    }*/


    override fun onStart() {
        super.onStart()
        BluetoothCommunication.startAdvertising()
        MessagesReceivedManager.listenNewMessages { message ->
            Log.i("lajm","Llegó mensaje de tipo ${message.type} con el valor ${message.value}")
            //AQUI ESTAN VALORES LEIDOS POR BLUETOOTH
           // measurementsDialog.updateMeasurements(message)
            /*when(message.type){
                Message.Type.HEAR_RATE -> Log.i("lajm", "heart rate ${message.value}" )
                Message.Type.TEMPERATURE -> Log.i("lajm", "temperature ${message.value}" )
                Message.Type.BREATHING_RATE -> Log.i("lajm", "breathing rate ${message.value}" )
                Message.Type.CRY -> {Log.i("lajm", "isCrying? ${message.value}" )
                    when(message.value){
                        BABY_CRYING ->{
                            mediaPlayer.start()
                            Log.i("lajm", "si entró!!")
                        }
                        BABY_NOT_CRYING -> {
                            Log.i("lajm", "no llorando")
                            if(mediaPlayer.isPlaying) {mediaPlayer.pause()}
                        }
                    }
                }
                else -> throw IllegalArgumentException("Type recieved per bluetooth not valid")
            }*/
        }
        //playingVideo(videoView, path1)
        changingBabyAppearance()
    }

    private fun changingBabyAppearance(){
        MessagesReceivedManager.listenNewTemperature {message ->
            when (message.value) {
                in 360..367 -> imageBabe.setBackgroundResource(R.drawable.bebesaludable)
                in 368..374 -> imageBabe.setBackgroundResource(R.drawable.bebe_rojo_1)
                in 375..381 -> imageBabe.setBackgroundResource(R.drawable.bebe_rojo_2)
                in 382..387 -> imageBabe.setBackgroundResource(R.drawable.bebe_rojo_3)
                in 388..420 -> imageBabe.setBackgroundResource(R.drawable.bebe_super_rojo)
                in 330..343 -> imageBabe.setBackgroundResource(R.drawable.bluebaby)
                in 344..349 -> imageBabe.setBackgroundResource(R.drawable.blue_baby_1)
                in 350..355 -> imageBabe.setBackgroundResource(R.drawable.blue_baby_2)
                in 355..359 -> imageBabe.setBackgroundResource(R.drawable.blue_baby_3)
                else -> imageBabe.setBackgroundResource(R.drawable.bebesaludable)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        BluetoothCommunication.stopAdvertising()
        MessagesReceivedManager.stopListeningMessages()
        //mediaPlayer.stop()
    }
}