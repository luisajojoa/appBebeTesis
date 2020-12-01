package com.tesis.bebeappble.UI

import android.graphics.drawable.Drawable
import androidx.constraintlayout.widget.ConstraintLayout
import com.tesis.bebeappble.R
import com.tesis.bebeappble.bluetooth.MessagesReceivedManager

class BabyAppearanceModifier() {
    fun change(constraintLayout: ConstraintLayout){
        MessagesReceivedManager.listenNewTemperature { message ->
            when (message.value) {
                in 360..367 ->  constraintLayout.setBackgroundResource(R.drawable.bebesaludable)
                in 368..374 -> constraintLayout.setBackgroundResource(R.drawable.bebe_rojo_1)
                in 375..381 -> constraintLayout.setBackgroundResource(R.drawable.bebe_rojo_2)
                in 382..387 -> constraintLayout.setBackgroundResource(R.drawable.bebe_rojo_3)
                in 388..420 -> constraintLayout.setBackgroundResource(R.drawable.bebe_super_rojo)
                in 330..343 -> constraintLayout.setBackgroundResource(R.drawable.bluebaby)
                in 344..349 -> constraintLayout.setBackgroundResource(R.drawable.blue_baby_1)
                in 350..355 -> constraintLayout.setBackgroundResource(R.drawable.blue_baby_2)
                in 355..359 -> constraintLayout.setBackgroundResource(R.drawable.blue_baby_3)
                else -> constraintLayout.setBackgroundResource(R.drawable.bebesaludable)
            }
        }
    }
}