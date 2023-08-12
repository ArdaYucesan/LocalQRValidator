package com.ssidglobal.qrreader.domain.use_case

import com.ssidglobal.qrreader.domain.Repositories.LocalRepository
import com.ssidglobal.qrreader.domain.model.QRData
import kotlinx.coroutines.flow.Flow

class GetAllUseCase(
    private val repository : LocalRepository
) {
    operator fun invoke(): Flow<List<QRData>> {
        return repository.getQRCodes()
    }
}