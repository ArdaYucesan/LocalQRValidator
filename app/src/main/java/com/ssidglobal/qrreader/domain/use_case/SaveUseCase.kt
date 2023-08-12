package com.ssidglobal.qrreader.domain.use_case

import android.util.Log
import com.ssidglobal.qrreader.domain.Repositories.LocalRepository
import com.ssidglobal.qrreader.domain.model.InvalidQRDataException
import com.ssidglobal.qrreader.domain.model.QRData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import kotlin.jvm.Throws

class SaveUseCase(
    private val repository: LocalRepository,
) {
    val TAG = "__SaveUseCase"

    @Throws(InvalidQRDataException::class)
    suspend operator fun invoke(qrCode : QRData) {
        Log.d(TAG, "invoke: ${qrCode.qrOwner}")

        if(qrCode.qrOwner.isBlank()) {
            Log.d(TAG, "invoke: ${qrCode.qrOwner}")
            throw InvalidQRDataException("QR Kod sahibi boş bırakılamaz !")
        }

        if(qrCode.value.isBlank()) {
            Log.d(TAG, "invoke: ${qrCode.value}")
            throw InvalidQRDataException("QR Kod değeri boş bırakalamaz !")
        }

        Log.d(TAG, "invoke: ${qrCode.value} ** ${qrCode.qrOwner}")


        repository.insertQRData(qrCode)

    }
}