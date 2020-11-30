package com.tesis.bebeappble.bluetooth

import com.tesis.bebeappble.common.Message

object MessagesReceivedManager {


    private var newMessageCallback : ((Message) -> Unit)? = null
    val tempMessages = HashMap<String, Message>()

    fun listenNewMessages(callback: (Message) -> Unit){
        this.newMessageCallback = callback
    }

    fun reportNewMessage(message: Message) {
        val lastMessage = tempMessages[message.javaClass.simpleName]
        if (lastMessage != null) {
            if (lastMessage.value != message.value) {
                tempMessages[message.javaClass.simpleName] = message
                newMessageCallback?.invoke(message)
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
    }
}