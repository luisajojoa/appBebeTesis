package com.tesis.bebeappble.UI


import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.tesis.bebeappble.R
import com.tesis.bebeappble.bluetooth.BluetoothCommunication
import com.tesis.bebeappble.bluetooth.MessageSender
import com.tesis.bebeappble.bluetooth.MessagesReceivedManager
import com.tesis.bebeappble.sensors.AbruptMovementsDetector
import com.tesis.bebeappble.vibration.HearRateVibration
import java.math.BigInteger


class MainActivity : AppCompatActivity() {

    private lateinit var editTextMessage: EditText
    private lateinit var btnSendMessage: Button
    private lateinit var mediaPlayer : MediaPlayer
    private lateinit var videoView : VideoView
    private lateinit var btnNoseBaby : Button
    private lateinit var btnHeadBaby : Button
    private lateinit var path1 : String
    private lateinit var path2 : String
    private lateinit var sliderTemp: SeekBar
    private lateinit var imageBabe : ConstraintLayout
    private var enableVideo =0
    private lateinit var termometerIcon : ImageButton
    private lateinit var heartIcon : ImageButton
    private lateinit var hearRateVibration :HearRateVibration
    private lateinit var measurementsDialog: MeasurementsDialog
    private lateinit  var messageSender :MessageSender
    private lateinit var babyAppearanceModifier : BabyAppearanceModifier


    companion object{
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
        messageSender = MessageSender(BluetoothCommunication)
        babyAppearanceModifier = BabyAppearanceModifier()

        // ESTE ES EL CALLBACK DEL ABRUPTMOVEMENTS
        AbruptMovementsDetector(this).addMovementsListener {
            Log.i("lajm", "Movimiento abrupto! cuidado con Victoria ")
            mediaPlayer.start()
        }

    }

    private fun retrieveViews() {
        videoView  = findViewById(R.id.videoViewBebe)
        btnNoseBaby = findViewById(R.id.btnNose)
        btnHeadBaby = findViewById(R.id.btnHead)
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
                TEMPERATURE_MESSAGE))
        }

        heartIcon.setOnClickListener {
            val hearRateMessage = MessagesReceivedManager.getMessage(HEART_RATE_MESSAGE)
            hearRateVibration.start(hearRateMessage?.value)
            measurementsDialog.showMeasure(R.drawable.ic_heart_rate_mesure, hearRateMessage){
                hearRateVibration.stop()
            }
        }


        btnHeadBaby.setOnLongClickListener {
//            Log.i("video", "videoview is visible? : ${videoView.isVisible}")
            val msj: BigInteger = 400.toBigInteger()
            messageSender.send(msj)

            return@setOnLongClickListener true
        }

        btnNoseBaby.setOnClickListener {
            /// AGREGAR INFO DE QUE SE TOCÓ LA NARIZ ENVIAR

        }

        var startPoint : Int?= null
        var endPoint :Int ?= null
        sliderTemp.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // progress es el valor que necesito ENVIAR

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
        }
        //playingVideo(videoView, path1)
        babyAppearanceModifier.change(imageBabe)
        MessagesReceivedManager.babyCrying(mediaPlayer)
    }


    override fun onStop() {
        super.onStop()
        BluetoothCommunication.stopAdvertising()
        MessagesReceivedManager.stopListeningMessages()

        //mediaPlayer.stop()
    }
}