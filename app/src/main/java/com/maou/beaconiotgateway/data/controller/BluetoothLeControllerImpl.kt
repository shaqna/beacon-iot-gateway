package com.maou.beaconiotgateway.data.controller

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.util.Log
import com.maou.beaconiotgateway.domain.controller.BluetoothLeController
import com.maou.beaconiotgateway.domain.model.BleDevice
import com.maou.beaconiotgateway.presentation.MainActivity
import com.maou.beaconiotgateway.utils.TimeHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@SuppressLint("MissingPermission")
class BluetoothLeControllerImpl(
    private val context: Context,
) : BluetoothLeController {

    companion object {
        const val BEACON_ADDRESS_TARGET = "C1:43:C1:7D:B3:C4"
    }

    private val bluetoothAdapter: BluetoothAdapter by lazy {
        val bluetoothManager = context.getSystemService(BluetoothManager::class.java)
        bluetoothManager.adapter
    }

    private val bleScanner by lazy {
        bluetoothAdapter.bluetoothLeScanner
    }

    private val scanSettings = ScanSettings.Builder()
        .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
        .build()

    private val bleScanCallback = BleScanCallback { result ->
        if(result.device.address == BEACON_ADDRESS_TARGET) {
            _scannedDevice.update { bleDevices ->
                val newDevice = BleDevice(
                    deviceAddress = result.device.address,
                    rssi = result.rssi,
                    timestamp = result.timestampNanos
                )
                Log.d(MainActivity.TAG, newDevice.toString())
                bleDevices + newDevice
            }
            Log.d(MainActivity.TAG, _scannedDevice.value.size.toString())
        }

    }

    private val _scannedDevice = MutableStateFlow<List<BleDevice>>(emptyList())

    override val scannedDevice: StateFlow<List<BleDevice>>
        get() = _scannedDevice.asStateFlow()

    override fun startLeScanning() {
        _scannedDevice.update {
            emptyList()
        }
        bleScanner.startScan(null, scanSettings, bleScanCallback)
    }

    override fun stopLeScanning() {
        bleScanner.stopScan(bleScanCallback)

    }
}