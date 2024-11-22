package com.example.wwbinspectionapp.api

import com.example.wwbinspectionapp.grantList.GrantAcceptRejectResponse
import com.example.wwbinspectionapp.grantList.GrantListResponse
import com.example.wwbinspectionapp.model.LoginModel.LoginResponce
import com.example.wwbinspectionapp.model.UserRequest
import com.example.wwbinspectionapp.model.factoryList.FactoryListResponce
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query


interface UserApi {
    @POST("auth/login")
    suspend fun signin(@Body userRequest: UserRequest): Response<LoginResponce>

    @GET("factory/list")
    suspend fun getFactoryList(
        @Header("x-access-token") authToken: String
    ): Response<FactoryListResponce>


    @GET("inspection/grant-list-wwb")
    suspend fun getGrantList(
        @Header("x-access-token") authToken: String,
        @Query("grant_type") grantType: String,
        @Query("factory_id") factoryId: Int
    ): Response<GrantListResponse>

    @POST("inspection/grant-approve-wwb")
    @FormUrlEncoded
    suspend fun approveGrant(
        @Header("x-access-token") authtoken: String,
        @Field("grant_type") grantType: String,
        @Field("app_status") appStatus: String,
        @Field("grant_id") grantId: Int,
        @Field("factory_worker_id") factoryWorkerId: Int
    ): Response<GrantAcceptRejectResponse>
}
