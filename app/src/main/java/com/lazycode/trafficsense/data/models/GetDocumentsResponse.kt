package com.lazycode.trafficsense.data.models

import com.google.gson.annotations.SerializedName

data class GetDocumentsResponse(

	@field:SerializedName("payload")
	val payload: List<DocumentsItem?>? = null,

	@field:SerializedName("success")
	val success: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)

data class DocumentsItem(

	@field:SerializedName("image")
	val image: String? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("user_id")
	val userId: String? = null,

	@field:SerializedName("image_url")
	val imageUrl: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("is_verified")
	val isVerified: String? = null,

	@field:SerializedName("document_type")
	val documentType: String? = null
)
