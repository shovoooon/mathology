package com.shovon.mathology.network

import com.shovon.mathology.model.*
import retrofit2.Call
import retrofit2.http.*


interface Api {

    @GET("appinfo.php")
    fun appInfo():Call<AppInfoResponse>

    @FormUrlEncoded
    @POST("register.php")
    fun register(
        @Field("phone") phone:String
    ):Call<RegisterResponse>

    @FormUrlEncoded
    @POST("addbal.php")
    fun addBal(
        @Field("phone") phone:String
    ):Call<AddBalResponse>

    @FormUrlEncoded
    @POST("dashboard.php")
    fun dashboard(
        @Field("phone") phone:String
    ):Call<DashboardResponse>

    @FormUrlEncoded
    @POST("payout.php")
    fun payout(
        @Field("phone") phone:String,
        @Field("amount") amount:String,
        @Field("method") method:String
    ):Call<PayoutResponse>
}

