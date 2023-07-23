package com.maou.beaconiotgateway.domain.repository

import com.maou.beaconiotgateway.data.BaseResult
import com.maou.beaconiotgateway.domain.model.BleDevice
import kotlinx.coroutines.flow.Flow

interface BeaconRepository {

    suspend fun sendBeaconData(bleDevice: BleDevice): Flow<BaseResult<String, String>>

}