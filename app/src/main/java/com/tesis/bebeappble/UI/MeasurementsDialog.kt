package com.tesis.bebeappble.UI

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.tesis.bebeappble.R
import com.tesis.bebeappble.common.Message
import java.lang.IllegalArgumentException

class MeasurementsDialog(val activity: AppCompatActivity) {
    val view = activity.layoutInflater.inflate(R.layout.layout_show_measurements, null, false)
    val imageIcon :ImageView = view.findViewById<ImageView>(R.id.imgIcon)
    val txtMeasurementsValue: TextView = view.findViewById<TextView>(R.id.txtMesure)
    var typeMessage: Message.Type? = null
    var heartRateValue : Int = 0
    var temperatureValue : Int = 0


    val dialog = Dialog(activity).apply {
        window?.requestFeature(Window.FEATURE_NO_TITLE)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    fun showMeasure(icon: Int, message: Message? , onDismissDialog: () -> Unit = {}) {
        val icon = activity.getDrawable(icon)
        if(message != null){
            typeMessage = message.type
            when(typeMessage) {
                Message.Type.HEAR_RATE -> txtMeasurementsValue.text ="${message.value} bpm"
                Message.Type.TEMPERATURE -> txtMeasurementsValue.text ="${message.value.toDouble()/10} ºC"
            }
        }
        imageIcon.setImageDrawable(icon)
        dialog.setContentView(view)
        dialog.setOnDismissListener { onDismissDialog.invoke()}
        dialog.show()
    }
/*
    fun updateMeasurements(message: Message) {
       // if(dialog.isShowing && typeMessage== message.type){
            when(message.type) {
                Message.Type.HEAR_RATE -> heartRateValue = message.value
                Message.Type.TEMPERATURE -> temperatureValue = message.value
            }
        //}
    }*/
/*
    fun getHeartMeasurement() : Int{
        return heartRateValue
    }*/

}