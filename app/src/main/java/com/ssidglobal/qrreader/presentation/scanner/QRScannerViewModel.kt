package com.ssidglobal.qrreader.presentation.scanner

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.akash.mybarcodescanner.domain.repo.ScannerRepo
import com.ssidglobal.qrreader.domain.model.InvalidQRDataException
import com.ssidglobal.qrreader.domain.model.QRData
import com.ssidglobal.qrreader.domain.model.Resource
import com.ssidglobal.qrreader.domain.use_case.GetAllUseCase
import com.ssidglobal.qrreader.domain.use_case.SaveUseCase
import com.ssidglobal.qrreader.domain.use_case.ValidateUseCase
import com.ssidglobal.qrreader.presentation.add.AddQRViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class QRScannerViewModel @Inject constructor(
    private val repo: ScannerRepo,
    private val validateUseCase: ValidateUseCase,
    private val saveUseCase: SaveUseCase,
    private val getAllUseCase: GetAllUseCase
) : ViewModel() {

    var scannerState = mutableStateOf(QRScannerScreenState())
        private set

    var eventFlow = MutableSharedFlow<UiEvent>()
        private set

    private var validateJob: Job? = null;

    init {
        getValidQR()
    }

    fun onEvent(event: QRScannerEvent) {
        when (event) {
            is QRScannerEvent.onQRReadClick -> {
                startScanning()
            }

            is QRScannerEvent.onQRRead -> {
                viewModelScope.launch {

                    val code = validateUseCase(event.readValue)

                    if (code != null) {

                        Log.d("__", "onEvent: data owner : ${code.qrOwner}")
                        saveUseCase(code)
                        eventFlow.emit(UiEvent.ShowSnackbar("Kayıtlı QR Kod Sahibi : ${code.qrOwner}."))

                    } else {
                        eventFlow.emit(
                            UiEvent.ShowSnackbar(
                                message = "Geçersiz QR Kod !",
                                isError = true
                            )
                        )
                        Log.d("__", "onEvent: data owner : null")

                    }
                }
            }

            is QRScannerEvent.onQRTest -> {
                viewModelScope.launch {
                    saveUseCase(QRData("baba", "arda", true))
                }
            }
        }
    }

    private fun startScanning() {
        viewModelScope.launch {
            repo.startScanning().collect { readingValue ->
                if (!readingValue.isNullOrBlank()) {
                    Log.d("__", "startScanningoutside : ${readingValue}")

                    onEvent(QRScannerEvent.onQRRead(readingValue))

                } else {
                    //TODO : burada boş qr error snackbarı göster
                }
            }
        }
    }

    private fun getValidQR() {
        validateJob?.cancel()

        validateJob = getAllUseCase.invoke().onEach { codes ->
            scannerState.value = scannerState.value.copy(
                validQRCodes = codes.filter { it.isValid == true }
            )

        }.launchIn(viewModelScope)
    }

    sealed class UiEvent {
        data class ShowSnackbar(val message: String, val isError: Boolean = false) : UiEvent()
    }
}
