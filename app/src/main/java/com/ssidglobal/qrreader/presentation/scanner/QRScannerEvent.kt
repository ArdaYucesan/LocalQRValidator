package com.ssidglobal.qrreader.presentation.scanner

sealed class QRScannerEvent {
    object onQRReadClick : QRScannerEvent()
    object onQRTest : QRScannerEvent()
    data class onQRRead(val readValue : String) : QRScannerEvent()
}
