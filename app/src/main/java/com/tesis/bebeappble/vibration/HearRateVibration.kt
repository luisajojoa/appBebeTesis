package com.tesis.bebeappble.vibration

import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator
import java.lang.IllegalArgumentException

class HearRateVibration (context: Context) {

    var vibrationManager = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    fun start(frequency : Int?){
        val timing : LongArray
        if(frequency != null){
            val timeOff = (60000/frequency)-100
            timing = longArrayOf(timeOff.toLong(), 150)
        }else{
            timing= longArrayOf(0,0)
        }
        val vibrationEffect = VibrationEffect.createWaveform(timing,0)
        vibrationManager.vibrate(vibrationEffect)
    }
    fun stop(){
        vibrationManager.cancel()
    }
}