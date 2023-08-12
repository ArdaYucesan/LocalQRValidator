package com.ssidglobal.qrreader.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class QRData(
    val qrOwner : String,
    val value : String,
    val isValid : Boolean,
    @PrimaryKey(autoGenerate = true) val id: Int = 0
)

class InvalidQRDataException(message: String): Exception(message)
