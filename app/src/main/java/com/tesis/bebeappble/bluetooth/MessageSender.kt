package com.tesis.bebeappble.bluetooth

import java.math.BigInteger


class MessageSender(private val bluetoothCommunication : BluetoothCommunication) {

    fun send(message: BigInteger){

        bluetoothCommunication.sendMessage(message)
    }

}