package com.lazycode.trafficsense.data.models

import com.google.gson.annotations.SerializedName

data class UpdateStatusResponse(

	@field:SerializedName("success")
	val success: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)
