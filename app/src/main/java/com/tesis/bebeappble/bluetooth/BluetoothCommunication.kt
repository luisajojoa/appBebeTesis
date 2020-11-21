package com.tesis.bebeappble.bluetooth

import android.bluetooth.*
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.BluetoothLeAdvertiser
import android.content.Context
import android.util.Log


object BluetoothCommunication {

    private lateinit var gattServer: BluetoothGattServer
    private var bleAdvertising: BluetoothLeAdvertiser? = null
    private val bleSettingsBuilder = BluetoothSettingsBuilder()

    fun startBLE(context: Context) {
        // Para usar API bluetooth de Android
        val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        // Creando un GattServer
        gattServer = bluetoothManager.openGattServer(context, BebeGattServerCallback())
        // crear sevicio y añadirlo al Gatt
        gattServer.addService(bleSettingsBuilder.getBLEGatService())
    }

    fun startAdvertising(){
        if (bleAdvertising == null){
            val bleAdapter = BluetoothAdapter.getDefaultAdapter()
            bleAdvertising = bleAdapter.bluetoothLeAdvertiser
        }
        bleAdvertising?.startAdvertising(bleSettingsBuilder.getAdvertisingSettings(),
            bleSettingsBuilder.getAdvertisingData(), BebeAdvertisingCallback())

    }
    fun stopAdvertising(){
        bleAdvertising?.stopAdvertising(BebeAdvertisingCallback())
    }

    fun sendMessage(message: String){}

    fun listenNewMessages(newMessageCallback: (String) -> Unit){}

    fun stopBLE(){}

    private class BebeGattServerCallback() : BluetoothGattServerCallback(){
        override fun onConnectionStateChange(device: BluetoothDevice?, status: Int, newState: Int) {
            super.onConnectionStateChange(device, status, newState)
        }

        override fun onCharacteristicWriteRequest(
            device: BluetoothDevice?,
            requestId: Int,
            characteristic: BluetoothGattCharacteristic?,
            preparedWrite: Boolean,
            responseNeeded: Boolean,
            offset: Int,
            value: ByteArray?
        ) {
            super.onCharacteristicWriteRequest(
                device,
                requestId,
                characteristic,
                preparedWrite,
                responseNeeded,
                offset,
                value
            )
        }
    }
}
