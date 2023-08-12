package com.ssidglobal.qrreader.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScanner
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import com.ssidglobal.qrreader.common.Constants
import com.ssidglobal.qrreader.data.Repositories.ApiRepositoryImpl
import com.ssidglobal.qrreader.data.Repositories.LocalRepositoryImpl
import com.ssidglobal.qrreader.data.Repositories.RetrofitApi
import com.ssidglobal.qrreader.data.data_source.QRCodeDatabase
import com.ssidglobal.qrreader.domain.Repositories.ApiRepository
import com.ssidglobal.qrreader.domain.Repositories.LocalRepository
import com.ssidglobal.qrreader.domain.use_case.DeleteUseCase
import com.ssidglobal.qrreader.domain.use_case.GetAllUseCase
import com.ssidglobal.qrreader.domain.use_case.SaveUseCase
import com.ssidglobal.qrreader.domain.use_case.ValidateUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideQRCodeApi(): RetrofitApi{
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RetrofitApi::class.java)
    }

    @Provides
    @Singleton
    fun provideApiRepository(api: RetrofitApi): ApiRepository {
        return ApiRepositoryImpl(api)
    }

    @Provides
    @Singleton
    fun provideQRCodeDatabase(app: Application): QRCodeDatabase {

        return Room.databaseBuilder(
            app,
            QRCodeDatabase::class.java,
            QRCodeDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideLocalRepository(db: QRCodeDatabase): LocalRepository {
        return LocalRepositoryImpl(db.qrDao)
    }

    @Singleton
    @Provides
    fun provideContext(app:Application):Context{
        return app.applicationContext
    }

    @Provides
    @Singleton
    fun provideValidateUseCase(repository: LocalRepository): ValidateUseCase{
        return ValidateUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideSaveUseCase(repository: LocalRepository): SaveUseCase{
        return SaveUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideDeleteUseCase(repository: LocalRepository): DeleteUseCase{
        return DeleteUseCase(repository)
    }
    @Provides
    @Singleton
    fun provideGetAllUseCase(repository: LocalRepository): GetAllUseCase{
        return GetAllUseCase(repository)
    }

    @Singleton
    @Provides
    fun provideBarCodeOptions() : GmsBarcodeScannerOptions{
        return GmsBarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
            .build()
    }

    @Singleton
    @Provides
    fun provideBarCodeScanner(context: Context,options: GmsBarcodeScannerOptions):GmsBarcodeScanner{
        return GmsBarcodeScanning.getClient(context, options)
    }
}