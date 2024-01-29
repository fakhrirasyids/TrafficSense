package com.lazycode.trafficsense.data.remote.services

import com.lazycode.trafficsense.data.models.CarpoolingResponse
import com.lazycode.trafficsense.data.models.DeleteVehicleResponse
import com.lazycode.trafficsense.data.models.GetDocumentsResponse
import com.lazycode.trafficsense.data.models.GetDriverPassengerResponse
import com.lazycode.trafficsense.data.models.GetPassengerHistoryResponse
import com.lazycode.trafficsense.data.models.GetTripsResponse
import com.lazycode.trafficsense.data.models.GetVehicleResponse
import com.lazycode.trafficsense.data.models.LoginResponse
import com.lazycode.trafficsense.data.models.LogoutResponse
import com.lazycode.trafficsense.data.models.NavigatorResponse
import com.lazycode.trafficsense.data.models.PathsItem
import com.lazycode.trafficsense.data.models.SavedTripItem
import com.lazycode.trafficsense.data.models.SavedTripsResponse
import com.lazycode.trafficsense.data.models.SensorResponse
import com.lazycode.trafficsense.data.models.StartPayload
import com.lazycode.trafficsense.data.models.StartResponse
import com.lazycode.trafficsense.data.models.StoreDocumentResponse
import com.lazycode.trafficsense.data.models.StorePassengerResponse
import com.lazycode.trafficsense.data.models.UpdatePriceResponse
import com.lazycode.trafficsense.data.models.UpdateProfile
import com.lazycode.trafficsense.data.models.UpdateProfileResponse
import com.lazycode.trafficsense.data.models.UpdateStatusResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.http.*

interface MainApiService {
    @POST("refresh")
    suspend fun refreshToken(
    ): LoginResponse

    @POST("logout")
    suspend fun logoutUser(
    ): LogoutResponse

    @GET("sensor")
    suspend fun getSensor(
    ): SensorResponse

    @POST("trips/navigate")
    @FormUrlEncoded
    suspend fun navigateDestination(
        @Field("departure_latitude") departure_latitude: String,
        @Field("departure_longitude") departure_longitude: String,
        @Field("destination_latitude") destination_latitude: String,
        @Field("destination_longitude") destination_longitude: String,
    ): NavigatorResponse

    @POST("trips/start")
    suspend fun startRoute(
        @Body payload: StartPayload
    ): StartResponse

    @Multipart
    @POST("profile")
    suspend fun updateProfile(
        @Part("name") name: RequestBody,
        @Part("phone_number") phone_number: RequestBody,
        @Part profile_picture: MultipartBody.Part,
        @Part("_method") method: RequestBody = "PUT".toRequestBody(),
    ): UpdateProfileResponse

    @GET("profile/documents")
    suspend fun getDocuments(
    ): GetDocumentsResponse

    @Multipart
    @POST("profile/documents")
    suspend fun storeDocuments(
        @Part("documents[0][document_type]") KTP: RequestBody = "KTP".toRequestBody(),
        @Part ktpImage: MultipartBody.Part,
        @Part("documents[1][document_type]") SIM: RequestBody = "SIM".toRequestBody(),
        @Part simImage: MultipartBody.Part,
        @Part("documents[2][document_type]") STNK: RequestBody = "STNK".toRequestBody(),
        @Part stnkImage: MultipartBody.Part,
    ): StoreDocumentResponse

    @GET("profile/vehicles")
    suspend fun getVehicles(
    ): GetVehicleResponse

    @Multipart
    @POST("profile/vehicles")
    suspend fun storeVehicle(
        @Part("name") name: RequestBody,
        @Part("capacity") capacity: RequestBody,
        @Part vehicleImages: MultipartBody.Part,
    ): StoreDocumentResponse

    @DELETE("profile/vehicles/{id}")
    suspend fun deleteVehicle(
        @Path("id") id: Int,
    ): DeleteVehicleResponse

    @GET("carpoolings")
    suspend fun getAllCarpooling(): CarpoolingResponse

    @GET("carpoolings/mine")
    suspend fun getMyCarpooling(): CarpoolingResponse

    @POST("carpoolings")
    @FormUrlEncoded
    suspend fun storeCarpooling(
        @Field("departure_latitude") departure_latitude: String,
        @Field("departure_longitude") departure_longitude: String,
        @Field("arrive_latitude") destination_latitude: String,
        @Field("arrive_longitude") destination_longitude: String,
        @Field("capacity") capacity: Int,
        @Field("phone_number") phoneNumber: String,
        @Field("departure_info") departureInfo: String,
        @Field("arrive_info") arriveInfo: String,
        @Field("vehicle_id") vehicleId: Int,
        @Field("departure_at") departureAt: String,
        @Field("arrive_estimation") arriveEstimation: Int = 0,
        @Field("distance") distance: Int = 0,
        @Field("note") note: String = "",
    ): NavigatorResponse

    @GET("carpoolings/{id}/passangers")
    suspend fun getDriverPassengers(
        @Path("id") id: Int,
    ): GetDriverPassengerResponse

    @PUT("carpoolings/{id}/passangers/{passenger_id}/price")
    @FormUrlEncoded
    suspend fun updatePassengerPrice(
        @Path("id") id: Int,
        @Path("passenger_id") passengerId: Int,
        @Field("price") price: Int
    ): UpdatePriceResponse

    @POST("carpoolings/{id}/passangers")
    @FormUrlEncoded
    suspend fun applyPassenger(
        @Path("id") id: Int,
        @Field("pick_latitude") departure_latitude: String,
        @Field("pick_longitude") departure_longitude: String,
        @Field("drop_latitude") destination_latitude: String,
        @Field("drop_longitude") destination_longitude: String,
        @Field("pick_info") departureInfo: String,
        @Field("drop_info") arriveInfo: String,
        @Field("phone_number") phoneNumber: String,
        @Field("passage_count") passageCount: Int,
        @Field("pick_type") pickType: String = "JEMPUT"
    ): StorePassengerResponse

    @GET("carpoolings/passangers/history")
    suspend fun getHistoryPassengers(): GetPassengerHistoryResponse

    @PUT("carpoolings/{id}/passangers/{passenger_id}/status")
    @FormUrlEncoded
    suspend fun updatePassengerStatusDeal(
        @Path("id") id: Int,
        @Path("passenger_id") passengerId: Int,
        @Field("status") status: Int = 2
    ): UpdateStatusResponse

    @GET("trips")
    suspend fun getSavedTrips(
    ): SavedTripsResponse

    @GET("trips/{id}")
    suspend fun getRouteDetail(
        @Path("id") id: Int
    ): GetTripsResponse

    @PUT("carpoolings/{id}/status")
    @FormUrlEncoded
    suspend fun updateCarpoolStatus(
        @Path("id") id: Int,
        @Field("status") status: Int
    ): UpdateStatusResponse

}
