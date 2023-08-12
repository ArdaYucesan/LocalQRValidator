package com.ssidglobal.qrreader.data.Repositories

import com.google.gson.JsonObject
import com.ssidglobal.qrreader.domain.model.QRData
import com.ssidglobal.qrreader.domain.model.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface RetrofitApi {
    @POST("qrcode")
    suspend fun validateQRCode(@Body body : JsonObject) : Response<QRData>

    @GET("qrcode")
    suspend fun getSavedQRCodes() : Response<List<QRData>>

    @POST("qrcode")
    suspend fun addQRCode(@Body body : JsonObject) : Response<QRData>

}