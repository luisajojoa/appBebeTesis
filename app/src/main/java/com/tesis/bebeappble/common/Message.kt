package com.tesis.bebeappble.common

sealed class Message(val value: Int, var type: Type) {
    enum class Type{
        HEAR_RATE,BREATHING_RATE,TEMPERATURE, CRY
    }
    class HeartRateMessage(value: Int, type : Type = Type.HEAR_RATE): Message(value, type)
    class BreathingRateMessage(value: Int, type : Type = Type.BREATHING_RATE ): Message(value, type)
    class TemperatureMessage(value: Int, type : Type = Type.TEMPERATURE): Message(value, type)
    class CryMessage(value: Int, type : Type = Type.CRY): Message(value, type)
}