package com.tesis.bebeappble.UI


import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.tesis.bebeappble.R
import com.tesis.bebeappble.bluetooth.BluetoothCommunication
import com.tesis.bebeappble.bluetooth.MessageSender
import com.tesis.bebeappble.bluetooth.MessagesReceivedManager
import com.tesis.bebeappble.common.Message
import com.tesis.bebeappble.sensors.AbruptMovementsDetector
import com.tesis.bebeappble.vibration.HearRateVibration


class MainActivity : AppCompatActivity() {

    private lateinit var mediaPlayer : MediaPlayer
    private lateinit var videoView : VideoView
    private lateinit var btnNoseBaby : Button
    private lateinit var btnHeadBaby : Button
    private lateinit var sliderTemp: SeekBar
    private lateinit var imageBabe : ConstraintLayout
    private lateinit var termometerIcon : ImageButton
    private lateinit var heartIcon : ImageButton
    private lateinit var hearRateVibration :HearRateVibration
    private lateinit var measurementsDialog: MeasurementsDialog
    private lateinit  var messageSender :MessageSender
    private lateinit var babyAppearanceModifier : BabyAppearanceModifier
    private lateinit var txtAmbientTemperature : TextView
    private var counterNose :Int =0
    var counterHead = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        retrieveViews()
        addListeners()
        BluetoothCommunication.startBLE(this){ success ->
            if (success){
                messageSender.send(sliderTemp.progress.toBigInteger(), MessageSender.TEMPERATURE_INCUBATOR)
            }else {
                Log.i("lajm", "No se pudo enviar el dato del slider de Temperatura")
            }
        }
        mediaPlayer = MediaPlayer.create(this, R.raw.bebellorando)
        hearRateVibration = HearRateVibration(this )
        measurementsDialog = MeasurementsDialog(this)
        messageSender = MessageSender(BluetoothCommunication)
        babyAppearanceModifier = BabyAppearanceModifier()
    }

    private fun retrieveViews() {
        videoView  = findViewById(R.id.videoViewBebe)
        btnNoseBaby = findViewById(R.id.btnNose)
        btnHeadBaby = findViewById(R.id.btnHead)
        sliderTemp = findViewById(R.id.temperaturaSlider)
        termometerIcon = findViewById(R.id.imgBtnTermometro)
        heartIcon = findViewById(R.id.buttonHeartIconb)
        imageBabe = findViewById(R.id.constraintBaby)
        txtAmbientTemperature = findViewById(R.id.ambientTemperatur)
        val ambientTemperature :Double= sliderTemp.progress/10.toDouble()
        txtAmbientTemperature.text= "$ambientTemperature ºC"
    }

    private fun addListeners() {

        termometerIcon.setOnClickListener{
            measurementsDialog.showMeasure(R.drawable.ic_temperature__mesure, MessagesReceivedManager.getMessage(
                MessagesReceivedManager.TEMPERATURE_MESSAGE))
        }

        heartIcon.setOnClickListener {
            val hearRateMessage = MessagesReceivedManager.getMessage(MessagesReceivedManager.HEART_RATE_MESSAGE)
            hearRateVibration.start(hearRateMessage?.value)
            measurementsDialog.showMeasure(R.drawable.ic_heart_rate_mesure, hearRateMessage){
                hearRateVibration.stop()
            }
        }

        btnHeadBaby.setOnLongClickListener {
//            Log.i("video", "videoview is visible? : ${videoView.isVisible}")
            counterHead = if(counterHead == 0){
                1
            }else{
                0
            }
            messageSender.send(counterHead.toBigInteger(), MessageSender.HEAD_TREATMENT)

            return@setOnLongClickListener true
        }

        btnNoseBaby.setOnClickListener {
            /// AGREGAR INFO DE QUE SE TOCÓ LA NARIZ ENVIAR
            counterNose += 1
            if(counterNose == 2){
                val temperature = MessagesReceivedManager.getMessage(MessagesReceivedManager.TEMPERATURE_MESSAGE)
                if( temperature!= null) {
                    if (temperature.value < 375) {
                        counterNose = 0
                    }
                }
            }else if(counterNose == 5){
                counterNose = 0;
            }
            messageSender.send(counterNose.toBigInteger(), MessageSender.NOSE_TREATMENT)
        }

        var startPoint : Int?= null
        var endPoint :Int ?= null
        sliderTemp.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // progress es el valor que necesito ENVIAR
                messageSender.send(progress.toBigInteger() , MessageSender.TEMPERATURE_INCUBATOR)
                val ambientTemperature = progress/10.toDouble()
                txtAmbientTemperature.text = "$ambientTemperature ºC"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                startPoint = seekBar?.progress
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                endPoint = seekBar?.progress
            }
        })
    }

    override fun onStart() {
        super.onStart()
        BluetoothCommunication.startAdvertising()
        MessagesReceivedManager.listenNewMessages { message ->
            Log.i("lajm","Llegó mensaje de tipo ${message.type} con el valor ${message.value}")
            //AQUI ESTAN VALORES LEIDOS POR BLUETOOTH
        }
        babyAppearanceModifier.change(imageBabe)
        MessagesReceivedManager.babyCrying(mediaPlayer)
        // ESTE ES EL CALLBACK DEL ABRUPTMOVEMENTS
        AbruptMovementsDetector(this).addMovementsListener {
            Log.i("lajm", "Movimiento abrupto! cuidado con Victoria ")
            messageSender.send(1.toBigInteger(), MessageSender.ABRUPT_MOVEMENT)
            mediaPlayer.start()
        }
    }

    override fun onStop() {
        super.onStop()
        BluetoothCommunication.stopAdvertising()
        MessagesReceivedManager.stopListeningMessages()
    }
}