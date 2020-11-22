package com.tesis.bebeappble.bluetooth

import android.bluetooth.*
import android.bluetooth.le.BluetoothLeAdvertiser
import android.content.Context
import android.util.Log
import com.tesis.bebeappble.bluetooth.extension.getCharacteristic
import com.tesis.bebeappble.common.Message
import java.math.BigInteger


object BluetoothCommunication {

    private lateinit var gattServer: BluetoothGattServer
    private var bleAdvertising: BluetoothLeAdvertiser? = null
    private val bleSettingsBuilder = BluetoothSettingsBuilder()
    private var newMessageCallback : ((Message) -> Unit)? = null
    private lateinit var context: Context
    private var gattClient: BluetoothGatt?= null

    // HashMap guarda una llave y un Valor, La llave es el tipo de msj y el valor es el ultimo valor recibido
    private val tempMessages = HashMap<String, String>()

    fun startBLE(context: Context) {
        this.context = context
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

    fun sendMessage(message: String){
        val characteristic = gattClient?.getCharacteristic(ConstantsBle.SERVICE_UUID, ConstantsBle.SENDER_CHARACTERISTIC_UUID)
        if(characteristic!=null) {
            characteristic.writeType = BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
            val messageByteArray = message.toByteArray()
            characteristic.value = messageByteArray
            //Al cambiar la caracteristica, se envia el mensaje
            val success = gattClient?.writeCharacteristic(characteristic) ?: false
            if (success){
                Log.i(TAG, "Mensaje enviado!! :)")
            }else{
                Log.i(TAG, "Error al enviar mensaje, BleGatt es : $gattClient")
            }
        }else{
            Log.i(TAG, "NULL characteristic sender $characteristic")
        }
    }

    fun listenNewMessages(callback: (Message) -> Unit){
        this.newMessageCallback = callback
    }

    fun reportNewMessage(message: Message) {
        val lastMessageValue = tempMessages[message.javaClass.simpleName]
        if (lastMessageValue != null) {
            if (lastMessageValue != message.value) {
                tempMessages[message.javaClass.simpleName] = message.value
                newMessageCallback?.invoke(message)
            }
        } else {
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
            //Cuando el estado cambie a conectado, entonces se instancia el BluetoothGatt (que se usa para enviar mensajes)

            if(newState==BluetoothProfile.STATE_CONNECTED){
                //conectarse a dispositivo remoto
                device?.connectGatt(context,true, object : BluetoothGattCallback(){
                    override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
                        super.onServicesDiscovered(gatt, status)
                        val characteristic = gatt?.getCharacteristic(ConstantsBle.SERVICE_UUID, ConstantsBle.SENDER_CHARACTERISTIC_UUID)
                        if (characteristic != null && gattClient == null) {
                            gattClient = gatt
                        }
                    }
                })
            }
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
                if (characteristic?.uuid == ConstantsBle.RECEIVER_CHARACTERISTIC_UUID) {
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
