package com.ssidglobal.qrreader.domain.use_case

import com.ssidglobal.qrreader.domain.Repositories.LocalRepository
import com.ssidglobal.qrreader.domain.model.QRData

class DeleteUseCase(
    private val repository: LocalRepository
) {
    suspend operator fun invoke(qrData : QRData){
        repository.deleteQRData(qrData)
    }
}