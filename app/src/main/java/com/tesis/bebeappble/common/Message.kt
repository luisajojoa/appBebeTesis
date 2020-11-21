package com.tesis.bebeappble.common

sealed class Message(val value: String) {
    class HeartRateMessage(value: String): Message(value)
    class BreathingRateMessage(value: String): Message(value)
    class TemperatureMessage(value: String): Message(value)
}