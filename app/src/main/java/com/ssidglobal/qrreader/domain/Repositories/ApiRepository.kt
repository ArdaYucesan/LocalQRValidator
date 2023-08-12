package com.ssidglobal.qrreader.domain.Repositories

import com.google.gson.JsonObject
import com.ssidglobal.qrreader.domain.model.QRData
import com.ssidglobal.qrreader.domain.model.Response
import retrofit2.http.Body

interface ApiRepository {
    suspend fun validateQRCode(body: JsonObject): Response<QRData>

    suspend fun getSavedQRCodes(): Response<List<QRData>>

    suspend fun addQRCode(body: JsonObject): Response<QRData>

}