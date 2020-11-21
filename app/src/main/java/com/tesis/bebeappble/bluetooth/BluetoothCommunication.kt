package com.tesis.bebeappble.bluetooth

import android.bluetooth.*
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.BluetoothLeAdvertiser
import android.content.Context
import android.util.Log
import com.tesis.bebeappble.common.Message
import java.math.BigInteger


object BluetoothCommunication {

    private lateinit var gattServer: BluetoothGattServer
    private var bleAdvertising: BluetoothLeAdvertiser? = null
    private val bleSettingsBuilder = BluetoothSettingsBuilder()
    private var newMessageCallback : ((Message) -> Unit)? = null

    private val tempMessages = HashMap<String, String>()

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

    fun listenNewMessages(callback: (Message) -> Unit){
        this.newMessageCallback = callback
    }

    fun reportNewMessage(message: Message) {
        val tempMessage = tempMessages[message.javaClass.simpleName]
        if (tempMessage != null && tempMessage != message.value) {
            tempMessages[message.javaClass.simpleName] = message.value
            newMessageCallback?.invoke(message)
        }
    }

    private class BebeGattServerCallback() : BluetoothGattServerCallback(){
        override fun onConnectionStateChange(device: BluetoothDevice?, status: Int, newState: Int) {
            super.onConnectionStateChange(device, status, newState)
            // Si el estado cambia se obtiene el siguiente estado, se traduce de INT a algo regible y se imprime
            val readableNewState = BluetoothStateInterpreter.getReadableState(newState)
            Log.i(TAG, "Connection State callback GattServer new state : $readableNewState device: $device" )
        }
            // AQUI LLEGAN MENSAJES!
        override fun onCharacteristicWriteRequest(
            device: BluetoothDevice?,
            requestId: Int,
            characteristic: BluetoothGattCharacteristic?,
            preparedWrite: Boolean,
            responseNeeded: Boolean,
            offset: Int,
            value: ByteArray?
        ) {
            super.onCharacteristicWriteRequest( device, requestId, characteristic, preparedWrite, responseNeeded, offset, value)
                if (characteristic?.uuid == ConstantsBle.CHARACTERISTIC_UUID) {
                    gattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, 0, null)
                    /* The data received comes backwards to its original order, so it's been reversed and then
                       the specific byte of each vital sign is taken out of the complete array  */
                    val reversedArray = value?.reversedArray()
                    val heartRate = BigInteger(reversedArray?.sliceArray(IntRange(0,1)))
                    val breathingRate = BigInteger(reversedArray?.sliceArray(IntRange(2,3)))
                    val temperature = BigInteger(reversedArray?.sliceArray(IntRange(4,5)))

                    reportNewMessage(Message.HeartRateMessage(heartRate.toString()))
                    reportNewMessage(Message.BreathingRateMessage(breathingRate.toString()))
                    reportNewMessage(Message.TemperatureMessage(temperature.toString()))
                }
        }
    }
}
