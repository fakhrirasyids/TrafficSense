package com.lazycode.trafficsense.data.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class NavigatorResponse(

    @field:SerializedName("payload")
    val payload: NavigatorPayload? = null,

    @field:SerializedName("success")
    val success: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null
) : Parcelable

@Parcelize
data class Hints(

    @field:SerializedName("visited_nodes.average")
    val visitedNodesAverage: Int? = null,

    @field:SerializedName("visited_nodes.sum")
    val visitedNodesSum: Int? = null
) : Parcelable

@Parcelize
data class Points(

    @field:SerializedName("coordinates")
    val coordinates: List<List<Double>?>? = null,

    @field:SerializedName("type")
    val type: String? = null
) : Parcelable

@Parcelize
data class SnappedWaypoints(

    @field:SerializedName("coordinates")
    val coordinates: List<List<Double>?>? = null,

    @field:SerializedName("type")
    val type: String? = null
) : Parcelable

@Parcelize
data class Details(
    val any: @RawValue Any? = null
) : Parcelable

@Parcelize
data class PathsItem(
    @field:SerializedName("carbon_monoxide")
    val co: Double? = null,

    @field:SerializedName("descend")
    val descend: Double? = null,

    @field:SerializedName("ascend")
    val ascend: Double? = null,

    @field:SerializedName("distance")
    val distance: Double? = null,

    @field:SerializedName("transfers")
    val transfers: Int? = null,

    @field:SerializedName("bbox")
    val bbox: List<Double?>? = null,

    @field:SerializedName("legs")
    val legs: @RawValue List<Any?>? = null,

    @field:SerializedName("weight")
    val weight: Double? = null,

    @field:SerializedName("details")
    val details: Details? = null,

    @field:SerializedName("time")
    val time: Int? = null,

    @field:SerializedName("is_selected")
    val isSelected: Boolean? = null,

    @field:SerializedName("points_encoded")
    val pointsEncoded: Boolean? = null,

    @field:SerializedName("points")
    val points: Points? = null,

    @field:SerializedName("snapped_waypoints")
    val snappedWaypoints: SnappedWaypoints? = null,

    @field:SerializedName("sensors")
    val sensors: List<SensorItemNavigator?>? = null,

    ) : Parcelable

@Parcelize
data class NavigatorPayload(

    @field:SerializedName("hints")
    val hints: Hints? = null,

    @field:SerializedName("paths")
    var paths: List<PathsItem?>? = null,

    @field:SerializedName("info")
    val info: Info? = null
) : Parcelable

@Parcelize
data class Info(

    @field:SerializedName("took")
    val took: Int? = null,

    @field:SerializedName("copyrights")
    val copyrights: List<String?>? = null,

    @field:SerializedName("road_data_timestamp")
    val roadDataTimestamp: String? = null
) : Parcelable

@Parcelize
data class SensorItemNavigator(
    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("carbon_monoxide")
    val carbonMonoxide: Double? = null
) : Parcelable

