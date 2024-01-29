package com.lazycode.trafficsense.data.models

import com.google.gson.annotations.SerializedName

data class GetDriverPassengerResponse(

	@field:SerializedName("payload")
	val payload: List<DriverPassengerItem?>? = null,

	@field:SerializedName("success")
	val success: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)

data class DriverPassengerItem(

	@field:SerializedName("passanger_data")
	val passangerData: PassangerData? = null,

	@field:SerializedName("drop_info")
	val dropInfo: String? = null,

	@field:SerializedName("passage_count")
	val passageCount: String? = null,

	@field:SerializedName("code")
	val code: String? = null,

	@field:SerializedName("pick_type")
	val pickType: String? = null,

	@field:SerializedName("passanger_id")
	val passangerId: String? = null,

	@field:SerializedName("carpooling_id")
	val carpoolingId: String? = null,

	@field:SerializedName("pick_latitude")
	val pickLatitude: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("pick_longitude")
	val pickLongitude: String? = null,

	@field:SerializedName("status_label")
	val statusLabel: String? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("price")
	val price: Double? = null,

	@field:SerializedName("is_approved")
	val isApproved: String? = null,

	@field:SerializedName("phone_number")
	val phoneNumber: String? = null,

	@field:SerializedName("drop_latitude")
	val dropLatitude: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("pick_info")
	val pickInfo: String? = null,

	@field:SerializedName("drop_longitude")
	val dropLongitude: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)

//data class PassangerData(
//
//	@field:SerializedName("role")
//	val role: String? = null,
//
//	@field:SerializedName("updated_at")
//	val updatedAt: String? = null,
//
//	@field:SerializedName("is_document_verified")
//	val isDocumentVerified: String? = null,
//
//	@field:SerializedName("profile_picture_url")
//	val profilePictureUrl: String? = null,
//
//	@field:SerializedName("name")
//	val name: String? = null,
//
//	@field:SerializedName("created_at")
//	val createdAt: String? = null,
//
//	@field:SerializedName("profile_picture")
//	val profilePicture: String? = null,
//
//	@field:SerializedName("phone_number")
//	val phoneNumber: String? = null,
//
//	@field:SerializedName("id")
//	val id: Int? = null,
//
//	@field:SerializedName("email")
//	val email: String? = null
//)
