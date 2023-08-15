package com.akash.mybarcodescanner.di

import com.ssidglobal.qrreader.data.repositories.ScannerRepoImpl
import com.akash.mybarcodescanner.domain.repo.ScannerRepo
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped


@Module
@InstallIn(ViewModelComponent::class)
abstract class RepositoryModule {

    @Binds
    @ViewModelScoped
    abstract fun bindMainRepo(
        mainRepoImpl : ScannerRepoImpl
    ) : ScannerRepo

}