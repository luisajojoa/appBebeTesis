package com.tesis.bebeappble.bluetooth

import android.media.MediaPlayer
import com.tesis.bebeappble.common.Message

object MessagesReceivedManager {

    private var newMessageCallback : ((Message) -> Unit)? = null
    private var newTemperatureMessageCallback : ((Message) -> Unit)? = null
    private var newCryingMessageCallback : ((Message) -> Unit)? = null
    private val tempMessages = HashMap<String, Message>()
    private const val BABY_CRYING = 1
    private const val BABY_NOT_CRYING = 0
    const val HEART_RATE_MESSAGE = "HeartRateMessage"
    const val BREATHING_RATE_MESSAGE = "BreathingRateMessage"
    const val CRY_MESSAGE = "CryMessage"
    const val TEMPERATURE_MESSAGE = "TemperatureMessage"

    fun listenNewMessages(callback: (Message) -> Unit){
        this.newMessageCallback = callback

    }
    fun listenNewTemperature(callback: (Message) -> Unit){
        this.newTemperatureMessageCallback = callback
    }

    private fun listenNewCrying(callback: (Message) -> Unit){
        this.newCryingMessageCallback = callback
    }

    fun reportNewMessage(message: Message) {
        val lastMessage = tempMessages[message.javaClass.simpleName]
        if (lastMessage != null) {
            if (lastMessage.value != message.value) {
                tempMessages[message.javaClass.simpleName] = message
                newMessageCallback?.invoke(message)
                if(message.type == Message.Type.TEMPERATURE){
                    newTemperatureMessageCallback?.invoke(message)
                }else if (message.type == Message.Type.CRY){
                    newCryingMessageCallback?.invoke(message)
                }

            }
        } else {
            tempMessages[message.javaClass.simpleName] = message
            newMessageCallback?.invoke(message)
        }
    }

    fun getMessage(messageType : String ): Message?{
        return tempMessages[messageType]
    }


    fun stopListeningMessages(){
        //no reportar un nuevo mensaje
        newMessageCallback = null
        newCryingMessageCallback = null
        newTemperatureMessageCallback = null
    }

    fun babyCrying(mediaPlayer : MediaPlayer){
        listenNewCrying { message ->
            when(message.value){
                BABY_NOT_CRYING -> mediaPlayer.pause()
                BABY_CRYING -> mediaPlayer.start()
            }

        }
    }
}