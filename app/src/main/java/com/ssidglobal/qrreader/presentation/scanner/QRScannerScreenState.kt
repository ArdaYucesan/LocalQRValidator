package com.ssidglobal.qrreader.presentation.scanner

import com.ssidglobal.qrreader.domain.model.QRData

data class QRScannerScreenState(
    var guestName: String? = "",
    val errorMessage: String? = null,
    var readedQRCode : QRData? = null,
    var validQRCodes : List<QRData> = emptyList()
)
