package com.ssidglobal.qrreader.data.data_source

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ssidglobal.qrreader.domain.model.QRData
import kotlinx.coroutines.flow.Flow

@Dao
interface QRCodeDao {

    @Query("SELECT * FROM  qrdata")
    fun getQRCodes(): Flow<List<QRData>>

    @Query("SELECT * FROM qrdata WHERE id = :id")
    suspend fun getQRCodeById(id: Int): QRData?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQRCode(qrCode: QRData)

    @Delete
    suspend fun deleteQRCode(qrCode: QRData)
}