package com.tesis.bebeappble.bluetooth

import java.math.BigInteger


class MessageSender(private val bluetoothCommunication : BluetoothCommunication) {
    private var nose = 0.toBigInteger()
    private var head = 0.toBigInteger()
    private var ambientTemperature = 0.toBigInteger()
    private var abruptMovement = 0.toBigInteger()
    val msj = ByteArray(5)
    companion object {
        const val NOSE_TREATMENT = 0
        const val HEAD_TREATMENT = 1
        const val ABRUPT_MOVEMENT = 3
        const val TEMPERATURE_INCUBATOR = 4
    }

    fun send(message: BigInteger, type : Int){
        when(type){
            NOSE_TREATMENT -> nose = message
            HEAD_TREATMENT -> head = message
            TEMPERATURE_INCUBATOR -> ambientTemperature = message
            ABRUPT_MOVEMENT -> abruptMovement = message
        }
        val sendingMessage =byteArrayOf(head.toByte(), nose.toByte(), abruptMovement.toByte())
        val sendingTemperature = ambientTemperature.toByteArray()
        System.arraycopy(sendingMessage,0, msj, 0, sendingMessage.size )
        System.arraycopy(sendingTemperature, 0, msj, sendingMessage.size, sendingTemperature.size )
        BluetoothCommunication.sendMessage(msj)
    }

}