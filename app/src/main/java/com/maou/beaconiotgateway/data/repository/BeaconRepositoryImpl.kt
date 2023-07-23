package com.maou.beaconiotgateway.data.repository

import android.util.Log
import com.maou.beaconiotgateway.data.BaseResult
import com.maou.beaconiotgateway.data.source.request.BeaconRequest
import com.maou.beaconiotgateway.data.source.request.Item
import com.maou.beaconiotgateway.data.source.request.Payload
import com.maou.beaconiotgateway.data.source.service.ApiService
import com.maou.beaconiotgateway.domain.model.BleDevice
import com.maou.beaconiotgateway.domain.repository.BeaconRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.sql.Timestamp

class BeaconRepositoryImpl(
    private val apiService: ApiService
) : BeaconRepository {

    override suspend fun sendBeaconData(bleDevice: BleDevice): Flow<BaseResult<String, String>> =
        flow {
            val item = Item(
                timeStamp = bleDevice.timestamp,
                beaconAddress = bleDevice.deviceAddress,
                rssi = bleDevice.rssi
            )
            val response = apiService.sendBeaconData(BeaconRequest(payload = Payload(item)))
            Log.d("Repository", "response code ${response.code}")
            if (response.code != 200) {
                emit(BaseResult.Error(response.message))
            }

            emit(BaseResult.Success(response.message))

        }.catch { e ->
            emit(BaseResult.Error(e.message.toString()))
        }.flowOn(Dispatchers.IO)
}