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
    private var onBLEConnectedCallback : ((Message) -> Unit)? = null
    private lateinit var context: Context
    private var gattClient: BluetoothGatt?= null


    // HashMap guarda una llave y un Valor, La llave es el tipo de msj y el valor es el ultimo valor recibido
    // private val tempMessages = HashMap<String, Int>()

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

    fun sendMessage(message: ByteArray){
        // An object of type GattClient's characteristic is obtained trough the UIID of the send service and characteristic required
        val characteristic = gattClient?.getCharacteristic(ConstantsBle.SERVICE_SENDER_UUID, ConstantsBle.SENDER_CHARACTERISTIC_UUID)
        if(characteristic!=null) {
            //the characteristic's type is set up to write type for sending data
            characteristic.writeType = BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
            // The message to be sent is converted to an array of bytes
            // The array is sent to the remote device by changing the attribute "value" of the characteristic object
            characteristic.value = message
            // Then the delivery status is retrieved
            val success = gattClient?.writeCharacteristic(characteristic) ?: false
            if (success){
                Log.i(TAG, "Message sent!! :)")
            }else{
                Log.i(TAG, "Message sent failure, BleGattClient is : $gattClient")
            }
        }else{
            Log.i(TAG, "NULL characteristic sender")
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
                //intenta conectarse a dispositivo remoto. Asi comienza a buscar servicios nuevos en su conexion
                device?.connectGatt(context,false, object : BluetoothGattCallback(){

                    override fun onConnectionStateChange(
                        gatt: BluetoothGatt?,
                        status: Int,
                        newState: Int
                    ) {
                        super.onConnectionStateChange(gatt, status, newState)
                        val isSuccess = status == BluetoothGatt.GATT_SUCCESS
                        val isConnected = newState == BluetoothProfile.STATE_CONNECTED
                        Log.d(TAG, "onConnectionStateChange: Client $gatt  success: $isSuccess connected: $isConnected")
                        // try to send a message to the other device as a test
                        if (isSuccess && isConnected) {
                            // discover services
                            gatt?.discoverServices()
                        }
                    }
                    override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
                        super.onServicesDiscovered(gatt, status)
                        val characteristic = gatt?.getCharacteristic(ConstantsBle.SERVICE_SENDER_UUID, ConstantsBle.SENDER_CHARACTERISTIC_UUID)
                        if (characteristic != null && gattClient == null) {
                            // se crea gatt client para no tener que escanear
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
                    val babyCry = BigInteger(reversedArray?.sliceArray(IntRange(6,7)))

                    MessagesReceivedManager.reportNewMessage(Message.HeartRateMessage(heartRate.toInt()))
                    MessagesReceivedManager.reportNewMessage(Message.BreathingRateMessage(breathingRate.toInt()))
                    MessagesReceivedManager.reportNewMessage(Message.TemperatureMessage(temperature.toInt()))
                    MessagesReceivedManager.reportNewMessage(Message.CryMessage(babyCry.toInt()))
                }
        }
    }
}
