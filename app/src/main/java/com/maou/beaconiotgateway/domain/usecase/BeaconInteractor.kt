package com.maou.beaconiotgateway.domain.usecase

import com.maou.beaconiotgateway.data.BaseResult
import com.maou.beaconiotgateway.domain.model.BleDevice
import com.maou.beaconiotgateway.domain.repository.BeaconRepository
import kotlinx.coroutines.flow.Flow

class BeaconInteractor(private val beaconRepository: BeaconRepository) : BeaconUseCase{
    override suspend fun sendBeaconData(bleDevice: BleDevice): Flow<BaseResult<String, String>> {
        return beaconRepository.sendBeaconData(bleDevice)
    }
}