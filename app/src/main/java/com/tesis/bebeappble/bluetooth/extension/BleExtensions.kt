package com.tesis.bebeappble.bluetooth.extension

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import java.util.*

fun BluetoothGatt.getCharacteristic(
        uuidService: UUID,
        uuidCharacteristic: UUID
) : BluetoothGattCharacteristic? {
    return this.getService(uuidService)?.getCharacteristic(uuidCharacteristic)
}