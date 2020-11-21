package com.tesis.bebeappble.bluetooth

object BluetoothStateInterpreter {
    fun getReadableState(state: Int): String {
        return when(state){
            0 -> "DISCONNECTED"
            1 -> "CONNECTING"
            2 -> "CONNECTED"
            3 -> "DISCONNECTING"
            else -> "UNKNOWN STATE"
        }
    }
}