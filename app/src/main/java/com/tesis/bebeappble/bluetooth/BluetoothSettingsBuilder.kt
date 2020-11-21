package com.tesis.bebeappble.bluetooth

import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertiseSettings
import android.os.ParcelUuid
import android.provider.SyncStateContract

class BluetoothSettingsBuilder {
    fun getBLEGatService(): BluetoothGattService {
        val service = BluetoothGattService(ConstantsBle.SERVICE_UUID, BluetoothGattService.SERVICE_TYPE_PRIMARY)
        val characteristic = BluetoothGattCharacteristic(
            ConstantsBle.CHARACTERISTIC_UUID,
            BluetoothGattCharacteristic.PROPERTY_WRITE,
            BluetoothGattCharacteristic.PERMISSION_WRITE
        )
        service.addCharacteristic(characteristic)
        return service
    }

    fun getAdvertisingSettings(): AdvertiseSettings {
        return AdvertiseSettings.Builder()
            .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
            .setTimeout(0)
            .build()

    }
    fun getAdvertisingData() : AdvertiseData {
        return AdvertiseData.Builder()
            .addServiceUuid(ParcelUuid(ConstantsBle.SERVICE_UUID))
            .setIncludeDeviceName(true)
            .build()
    }
}