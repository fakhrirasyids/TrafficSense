package com.lazycode.trafficsense.data.models

import com.google.gson.annotations.SerializedName

data class GetVehicleResponse(

	@field:SerializedName("payload")
	val payload: List<VehicleItem?>? = null,

	@field:SerializedName("success")
	val success: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)

data class VehicleImagesItem(

	@field:SerializedName("image")
	val image: String? = null,

	@field:SerializedName("image_url")
	val imageUrl: String? = null,

	@field:SerializedName("id")
	val id: Int? = null
)

data class VehicleItem(

	@field:SerializedName("vehicle_images")
	val vehicleImages: List<VehicleImagesItem?>? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("user_id")
	val userId: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("capacity")
	val capacity: String? = null
)
