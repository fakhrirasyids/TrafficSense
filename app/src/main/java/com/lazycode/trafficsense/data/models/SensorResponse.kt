package com.lazycode.trafficsense.data.models

import com.google.gson.annotations.SerializedName

data class SensorResponse(

	@field:SerializedName("payload")
	val payload: List<SensorItem?>? = null,

	@field:SerializedName("success")
	val success: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)

data class SensorItem(

	@field:SerializedName("code")
	val code: String? = null,

	@field:SerializedName("latitude")
	val latitude: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("radius")
	val radius: String? = null,

	@field:SerializedName("longitude")
	val longitude: String? = null,

	@field:SerializedName("carbon_monoxide")
	val carbonMonoxide: Double? = null
)
