package com.ssidglobal.qrreader.data.repositories

import com.ssidglobal.qrreader.data.data_source.QRCodeDao
import com.ssidglobal.qrreader.domain.Repositories.LocalRepository
import com.ssidglobal.qrreader.domain.model.QRData
import kotlinx.coroutines.flow.Flow

class LocalRepositoryImpl(
    private val dao:QRCodeDao
) : LocalRepository {
    override fun getQRCodes(): Flow<List<QRData>> {
        return dao.getQRCodes()
    }

    override suspend fun getQRCodeId(id: Int): QRData? {
        return dao.getQRCodeById(id)
    }

    override suspend fun insertQRData(qrCode: QRData) {
        return dao.insertQRCode(qrCode)

    }

    override suspend fun deleteQRData(qrCode: QRData) {
        return dao.deleteQRCode(qrCode)
    }
}