package com.maou.beaconiotgateway.data.source.request

import com.squareup.moshi.Json

data class BeaconRequest(
    @field:Json(name = "operation")
    val operation: String = "post-beacon",

    @field:Json(name = "payload")
    val payload: Payload
)

data class Payload(
    @field:Json(name = "Item")
    val item: Item
)

data class Item(
    @field:Json(name="time_stamp")
    val timeStamp: String,

    @field:Json(name = "beacon_address")
    val beaconAddress: String,

    @field:Json(name = "rssi")
    val rssi: Int
)