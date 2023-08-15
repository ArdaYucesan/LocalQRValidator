package com.ssidglobal.qrreader.presentation.add

import com.ssidglobal.qrreader.presentation.scanner.QRScannerEvent

sealed class AddQREvent {
   object QRCodeSaved : AddQREvent()
    data class QRCodeValueEntered(val qrValue : String) : AddQREvent()
    data class QRCodeOwnerEntered(val qrOwner : String) : AddQREvent()
    data class onQRRead(val readValue : String) : AddQREvent()

    object onQRReadClicked : AddQREvent()
}