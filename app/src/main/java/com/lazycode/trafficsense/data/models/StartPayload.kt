package com.lazycode.trafficsense.data.models

import com.google.gson.annotations.SerializedName

data class StartPayload(

    @field:SerializedName("sensors")
    val sensors: List<SensorsItemPayload?>? = null,

    @field:SerializedName("distance")
    val distance: Double? = null,

    @field:SerializedName("bbox")
    val bbox: List<Double?>? = null,

    @field:SerializedName("points")
    val points: Points? = null,

    @field:SerializedName("carbon_monoxide")
    val carbonMonoxide: Double? = null,

    @field:SerializedName("arrive_info")
    val arriveInfo: String? = null,

    @field:SerializedName("departure_info")
    val departureInfo: String? = null
)

data class SensorsItemPayload(
    @field:SerializedName("id")
    val id: Int? = null
)
