package com.lazycode.trafficsense.data.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class GetTripsResponse(

    @field:SerializedName("payload")
    val payload: TripsSavedPayload? = null,

    @field:SerializedName("success")
    val success: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null
)

@Parcelize
data class TripsSavedPayload(

    @field:SerializedName("arrive_info")
    val arriveInfo: String? = null,

    @field:SerializedName("user_id")
    val userId: String? = null,

    @field:SerializedName("departure_info")
    val departureInfo: String? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("detail")
    val detail: Detail? = null
) : Parcelable

@Parcelize
data class Detail(

    @field:SerializedName("arrive_info")
    val arriveInfo: String? = null,

    @field:SerializedName("distance")
    val distance: Double? = null,

    @field:SerializedName("bbox")
    val bbox: List<Double?>? = null,

    @field:SerializedName("departure_info")
    val departureInfo: String? = null,

    @field:SerializedName("points")
    val points: PointsSaved? = null,

    @field:SerializedName("carbon_monoxide")
    val carbonMonoxide: Double? = null
) : Parcelable

@Parcelize
data class PointsSaved(

    @field:SerializedName("coordinates")
    val coordinates: List<List<Double>?>? = null,

    @field:SerializedName("type")
    val type: String? = null
) : Parcelable
