package com.ssidglobal.qrreader.data.repositories

import com.google.gson.JsonObject
import com.ssidglobal.qrreader.domain.Repositories.ApiRepository
import com.ssidglobal.qrreader.domain.model.QRData
import com.ssidglobal.qrreader.domain.model.Response
import javax.inject.Inject

class ApiRepositoryImpl @Inject constructor(
    private val api: RetrofitApi
) : ApiRepository {

    override suspend fun validateQRCode(body: JsonObject): Response<QRData> {
        return api.validateQRCode(body)
    }

    override suspend fun getSavedQRCodes(): Response<List<QRData>> {
        return api.getSavedQRCodes()
    }

    override suspend fun addQRCode(body: JsonObject): Response<QRData> {
        return api.addQRCode(body)
    }
}