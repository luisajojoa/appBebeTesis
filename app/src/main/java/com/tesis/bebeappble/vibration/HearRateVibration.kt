package com.tesis.bebeappble.vibration

import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator

class HearRateVibration (context: Context) {
    var vibrationManager = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    fun start(type : Int){
        var timeOff : Long
        val timing : LongArray
        when(type){
            1 -> timeOff = 420 -100
            2 -> timeOff = 335 -100
            else -> timeOff = 714 -100
        }
        timing = longArrayOf(timeOff, 150)
        val vibrationEffect = VibrationEffect.createWaveform(timing,0)
        vibrationManager.vibrate(vibrationEffect)
    }
    fun stop(){
        vibrationManager.cancel()
    }
}