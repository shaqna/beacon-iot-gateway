package com.maou.beaconiotgateway.domain.controller

import com.maou.beaconiotgateway.domain.model.BleDevice
import kotlinx.coroutines.flow.StateFlow

interface BluetoothLeController {

    val scannedDevice: StateFlow<List<BleDevice>>

    fun startLeScanning()
    fun stopLeScanning()
}