package com.lazycode.trafficsense.data.models

import com.google.gson.annotations.SerializedName

data class MapPlacesResponse(

	@field:SerializedName("hits")
	val hits: List<HitsItem?>? = null,

	@field:SerializedName("took")
	val took: Int? = null
)

data class Point(

	@field:SerializedName("lng")
	val lng: Double? = null,

	@field:SerializedName("lat")
	val lat: Double? = null
)

data class HitsItem(

	@field:SerializedName("osm_id")
	val osmId: Double? = null,

	@field:SerializedName("osm_type")
	val osmType: String? = null,

	@field:SerializedName("extent")
	val extent: List<Double?>? = null,

	@field:SerializedName("country")
	val country: String? = null,

	@field:SerializedName("osm_key")
	val osmKey: String? = null,

	@field:SerializedName("housenumber")
	val housenumber: String? = null,

	@field:SerializedName("city")
	val city: String? = null,

	@field:SerializedName("street")
	val street: String? = null,

	@field:SerializedName("osm_value")
	val osmValue: String? = null,

	@field:SerializedName("postcode")
	val postcode: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("point")
	val point: Point? = null
)
