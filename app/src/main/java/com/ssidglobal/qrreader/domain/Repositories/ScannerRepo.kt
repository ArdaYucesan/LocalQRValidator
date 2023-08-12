package com.akash.mybarcodescanner.domain.repo

import kotlinx.coroutines.flow.Flow


interface ScannerRepo {

    fun startScanning(): Flow<String?>
}