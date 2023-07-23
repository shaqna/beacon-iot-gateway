package com.maou.beaconiotgateway.data.source.service

import com.maou.beaconiotgateway.data.source.request.BeaconRequest
import com.maou.beaconiotgateway.data.source.response.GeneralResponse
import com.maou.beaconiotgateway.domain.model.BleDevice
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("prod/bus")
    suspend fun sendBeaconData(
        @Body beaconRequest: BeaconRequest
    ): GeneralResponse

}