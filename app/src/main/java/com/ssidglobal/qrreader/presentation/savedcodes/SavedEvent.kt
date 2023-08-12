package com.ssidglobal.qrreader.presentation.savedcodes

import com.ssidglobal.qrreader.domain.model.QRData

sealed class SavedEvent {
    data class DeleteQR(val qrData : QRData) : SavedEvent()
    object RestoreQR : SavedEvent()
}