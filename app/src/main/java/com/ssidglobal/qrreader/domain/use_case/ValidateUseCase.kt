package com.ssidglobal.qrreader.domain.use_case

import android.util.Log
import com.google.gson.JsonObject
import com.ssidglobal.qrreader.common.Constants
import com.ssidglobal.qrreader.common.Helper
import com.ssidglobal.qrreader.domain.Repositories.ApiRepository
import com.ssidglobal.qrreader.domain.Repositories.LocalRepository
import com.ssidglobal.qrreader.domain.model.QRData
import com.ssidglobal.qrreader.domain.model.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.takeWhile
import java.io.IOException
import javax.inject.Inject

class ValidateUseCase @Inject constructor(
    private val repository: LocalRepository,
) {

    suspend operator fun invoke(qrValue: String): QRData? {
        Log.d("__Validate", " readed value : $qrValue")

        val codesList = repository.getQRCodes().firstOrNull()

        val validatedQR = codesList?.find { it.value == qrValue }



        if (validatedQR != null) {
            return QRData(
                qrOwner = validatedQR.qrOwner,
                value = validatedQR.value,
                id = validatedQR.id,
                isValid = true
            )
        } else {
            return null
        }

    }
}