package com.lazycode.trafficsense.data.models

import com.google.gson.annotations.SerializedName

data class UpdateProfileResponse(

	@field:SerializedName("payload")
	val payload: UpdateProfile? = null,

	@field:SerializedName("success")
	val success: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)

data class UpdateProfile(

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
