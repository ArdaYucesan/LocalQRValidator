package com.ssidglobal.qrreader.data.data_source

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ssidglobal.qrreader.domain.model.QRData

@Database(
    entities = [QRData::class],
    version = 1
)
abstract class QRCodeDatabase : RoomDatabase() {
    abstract  val qrDao: QRCodeDao

    companion object {
        const val DATABASE_NAME = "qrcode_db"
    }
}