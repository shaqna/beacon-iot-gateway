package com.maou.beaconiotgateway.data.source.response

import com.maou.beaconiotgateway.data.source.request.Item
import com.squareup.moshi.Json

data class GeneralResponse(
    val code: Int,
    val message: String,
    val res: List<ResObject>
)

data class ResObject(
    @field:Json(name = "Item")
    val item: Item,

    @field:Json(name ="TableName")
    val tableName: String
)
