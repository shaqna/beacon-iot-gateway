package com.maou.beaconiotgateway.domain.usecase

import com.maou.beaconiotgateway.data.BaseResult
import com.maou.beaconiotgateway.domain.model.BleDevice
import kotlinx.coroutines.flow.Flow

interface BeaconUseCase {

    suspend fun sendBeaconData(bleDevice: BleDevice): Flow<BaseResult<String, String>>

}