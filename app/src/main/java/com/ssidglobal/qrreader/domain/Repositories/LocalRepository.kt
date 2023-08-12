package com.ssidglobal.qrreader.domain.Repositories

import com.ssidglobal.qrreader.domain.model.QRData
import kotlinx.coroutines.flow.Flow

interface LocalRepository {
    fun getQRCodes(): Flow<List<QRData>>

    suspend fun getQRCodeId(id: Int): QRData?

    suspend fun insertQRData(qrCode: QRData)

    suspend fun deleteQRData(qrCode: QRData)
}