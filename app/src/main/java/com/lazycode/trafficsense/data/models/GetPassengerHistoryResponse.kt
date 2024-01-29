package com.lazycode.trafficsense.data.models

import com.google.gson.annotations.SerializedName

data class GetPassengerHistoryResponse(

	@field:SerializedName("payload")
	val payload: List<PayloadItemHistory?>? = null,

	@field:SerializedName("success")
	val success: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)

data class CarpoolingPassangersItemHistory(

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
	val price: String? = null,

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

data class PassangerDataHistory(

	@field:SerializedName("role")
	val role: String? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("is_document_verified")
	val isDocumentVerified: String? = null,

	@field:SerializedName("profile_picture_url")
	val profilePictureUrl: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("profile_picture")
	val profilePicture: String? = null,

	@field:SerializedName("phone_number")
	val phoneNumber: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("email")
	val email: String? = null
)

data class DriverHistory(

	@field:SerializedName("role")
	val role: String? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("is_document_verified")
	val isDocumentVerified: String? = null,

	@field:SerializedName("profile_picture_url")
	val profilePictureUrl: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("profile_picture")
	val profilePicture: String? = null,

	@field:SerializedName("phone_number")
	val phoneNumber: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("email")
	val email: String? = null
)

data class CarpoolingData(

	@field:SerializedName("note")
	val note: Any? = null,

	@field:SerializedName("driver_id")
	val driverId: String? = null,

	@field:SerializedName("arrive_info")
	val arriveInfo: String? = null,

	@field:SerializedName("distance")
	val distance: String? = null,

	@field:SerializedName("arrive_latitude")
	val arriveLatitude: String? = null,

	@field:SerializedName("arrive_estimation")
	val arriveEstimation: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("carpooling_passangers")
	val carpoolingPassangers: List<CarpoolingPassangersItemHistory?>? = null,

	@field:SerializedName("arrive_longitude")
	val arriveLongitude: String? = null,

	@field:SerializedName("capacity")
	val capacity: String? = null,

	@field:SerializedName("status_label")
	val statusLabel: String? = null,

	@field:SerializedName("is_mine")
	val isMine: Boolean? = null,

	@field:SerializedName("departure_longitude")
	val departureLongitude: String? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("driver")
	val driver: DriverHistory? = null,

	@field:SerializedName("departure_info")
	val departureInfo: String? = null,

	@field:SerializedName("phone_number")
	val phoneNumber: String? = null,

	@field:SerializedName("departure_at")
	val departureAt: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("vehicle_id")
	val vehicleId: String? = null,

	@field:SerializedName("departure_latitude")
	val departureLatitude: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class PayloadItemHistory(

	@field:SerializedName("passanger_data")
	val passangerData: PassangerDataHistory? = null,

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
	val price: String? = null,

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

	@field:SerializedName("carpooling_data")
	val carpoolingData: CarpoolingData? = null,

	@field:SerializedName("drop_longitude")
	val dropLongitude: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)
