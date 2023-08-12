package com.ssidglobal.qrreader.presentation.savedcodes

import com.ssidglobal.qrreader.domain.model.QRData

data class SavedQRScreenState(var qrCodes: List<QRData> = listOf(
    QRData("Arda Yücesan","P091328PJXYZY091328PJXYZY091328PJXYZY091328PJXYZ",isValid = false),
    QRData("Serkan Yücesan","PF091328PJXYY091328PJXYZZ",isValid = false),
    QRData("Ahmet Cerit","Y091328PJXYZ",isValid = false),
))