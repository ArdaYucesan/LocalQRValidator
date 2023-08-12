package com.ssidglobal.qrreader.presentation.add

sealed class AddQREvent {
   object QRCodeSaved : AddQREvent()
    data class QRCodeValueEntered(val qrValue : String) : AddQREvent()
    data class QRCodeOwnerEntered(val qrOwner : String) : AddQREvent()
}