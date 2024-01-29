package com.lazycode.trafficsense.data.models

import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class SavedTripsResponse(

	@field:SerializedName("payload")
	val payload: List<SavedTripItem?>? = null,

	@field:SerializedName("success")
	val success: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)

data class SavedTripItem(

	@field:SerializedName("arrive_info")
	val arriveInfo: String? = null,

	@field:SerializedName("arrive_latitude")
	val arriveLatitude: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("co_total")
	val coTotal: String? = null,

	@field:SerializedName("arrive_longitude")
	val arriveLongitude: String? = null,

	@field:SerializedName("arrive_at")
	val arriveAt: String? = null,

	@field:SerializedName("departure_longitude")
	val departureLongitude: String? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("user_id")
	val userId: String? = null,

	@field:SerializedName("departure_info")
	val departureInfo: String? = null,

	@field:SerializedName("departure_at")
	val departureAt: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("departure_latitude")
	val departureLatitude: String? = null
)
