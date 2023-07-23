package com.maou.beaconiotgateway.data.controller

import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult

class BleScanCallback(
    private val onDeviceFound: (ScanResult) -> Unit
): ScanCallback() {
    override fun onScanResult(callbackType: Int, result: ScanResult?) {
        super.onScanResult(callbackType, result)
        result?.let {
            onDeviceFound(it)
        }
    }
}