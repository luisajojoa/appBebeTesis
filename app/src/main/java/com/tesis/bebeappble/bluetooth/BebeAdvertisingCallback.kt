package com.tesis.bebeappble.bluetooth

import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseSettings
import android.util.Log

class BebeAdvertisingCallback : AdvertiseCallback() {
    override fun onStartSuccess(settingsInEffect: AdvertiseSettings?) {
        super.onStartSuccess(settingsInEffect)
        Log.i("BebeAdvertisingCallback", "Advertising success")
    }

    override fun onStartFailure(errorCode: Int) {
        super.onStartFailure(errorCode)
        Log.e("BebeAdvertisingCallback", "Advertising failed: $errorCode")
    }
}