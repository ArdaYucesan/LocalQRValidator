package com.ssidglobal.qrreader.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date

@Entity
data class QRData(
    val qrOwner : String,
    val value : String,
    val isValid : Boolean,
    val updatedAt : LocalDateTime = LocalDateTime.now(),
    @PrimaryKey(autoGenerate = true) val id: Int = 0
){
    val createdDateFormatted : String
        get() =updatedAt.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"))
}

class InvalidQRDataException(message: String): Exception(message)
