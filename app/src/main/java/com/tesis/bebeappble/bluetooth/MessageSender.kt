package com.tesis.bebeappble.bluetooth

import java.math.BigInteger


class MessageSender(private val bluetoothCommunication : BluetoothCommunication) {
    private var nose = 0
    private var head = 0
    private var ambientTemperature = 0
    private var abruptMovement = 0
    val NOSE_TREATMENT = 0
    val HEAD_TREATMENT = 1
    val ABRUPT_MOVEMENT = 3
    val TEMPERATURE_INCUBATOR = 4

    fun send(message: Int, type : Int){
        when(type){
            NOSE_TREATMENT -> nose = message
            HEAD_TREATMENT -> head = message
            TEMPERATURE_INCUBATOR -> ambientTemperature = message
            ABRUPT_MOVEMENT -> abruptMovement = message
        }
        val sendingMessage = byteArrayOf(nose.toByte(), head.toByte(), ambientTemperature.toByte(), abruptMovement.toByte())
        BluetoothCommunication.sendMessage(sendingMessage)
    }

}