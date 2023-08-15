package com.ssidglobal.qrreader.presentation.add

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.akash.mybarcodescanner.domain.repo.ScannerRepo
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import com.ssidglobal.qrreader.domain.model.InvalidQRDataException
import com.ssidglobal.qrreader.domain.model.QRData
import com.ssidglobal.qrreader.domain.use_case.SaveUseCase
import com.ssidglobal.qrreader.presentation.scanner.QRScannerEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddQRViewModel @Inject constructor(
    private val saveUseCase: SaveUseCase,
//    private val repo : ScannerRepo
    context: Context,
    options: GmsBarcodeScannerOptions
) : ViewModel() {

    val TAG = "__AddQRViewModel"
    val scanner = GmsBarcodeScanning.getClient(context, options)


    var state = mutableStateOf(AddQRScreenState())
        private set

    var eventFlow = MutableSharedFlow<UiEvent>()
        private set

    fun onEvent(event: AddQREvent) {
        when (event) {
            is AddQREvent.QRCodeOwnerEntered -> {
                state.value = state.value.copy(
                    qrOwner = event.qrOwner
                )
            }

            is AddQREvent.QRCodeValueEntered -> {
                state.value = state.value.copy(
                    qrValue = event.qrValue
                )
            }

            is AddQREvent.QRCodeSaved -> {
                viewModelScope.launch {
                    try {
                        Log.d(TAG, "onEvent:${state.value.qrOwner} - ${state.value.qrValue}")
                        saveUseCase.invoke(
                            QRData(
                                qrOwner = state.value.qrOwner,
                                value = state.value.qrValue,
                                isValid = false
                            )
                        )
                        eventFlow.emit(UiEvent.ShowSnackbar("Kayıt Başarılı."))
                        state.value = state.value.copy(
                            qrValue = "",
                            qrOwner = ""
                        )

                    } catch (e: InvalidQRDataException) {
                        eventFlow.emit(
                            UiEvent.ShowSnackbar(
                                message = e.message ?: "Beklenmedik bir hata oluştu !",
                                isError = true
                            )
                        )
                    }
                }
            }

            is AddQREvent.onQRReadClicked -> {
                startScanning()
            }

            is AddQREvent.onQRRead -> {
                state.value = state.value.copy(qrValue = event.readValue)
            }
        }
    }

    private fun startScanning() {
        viewModelScope.launch {
            scanner.startScan()
                .addOnSuccessListener {
//                        launch {
//                            send(getDetails(it))
//                        }
                    Log.d(TAG, "startScanning: ${it.rawValue.toString()} ")
                    onEvent(AddQREvent.onQRRead(it.rawValue.toString()))
                }
                .addOnFailureListener { e: Exception ->
                    Log.e(
                        "__Vision",
                        "startScanning: error : ${e.message}"
                    )
                }
                .addOnCanceledListener {
                    Log.e("__Vision", "startScanning: canceled")
                }
//            scanner.startScanning().collect { readingValue ->
//                if (!readingValue.isNullOrBlank()) {
//                    Log.d("__", "startScanningoutside : ${readingValue}")
//
//                    onEvent(AddQREvent.onQRRead(readingValue))
//
//                } else {
//                    //TODO : burada boş qr error snackbarı göster
//                }
//            }
        }
    }

    private fun getDetails(barcode: Barcode): String {
        return when (barcode.valueType) {
            Barcode.TYPE_WIFI -> {
                val ssid = barcode.wifi!!.ssid
                val password = barcode.wifi!!.password
                val type = barcode.wifi!!.encryptionType
                "ssid : $ssid, password : $password, type : $type"
            }

            Barcode.TYPE_URL -> {
                "url : ${barcode.url!!.url}"
            }

            Barcode.TYPE_PRODUCT -> {
                "productType : ${barcode.displayValue}"
            }

            Barcode.TYPE_EMAIL -> {
                "email : ${barcode.email}"
            }

            Barcode.TYPE_CONTACT_INFO -> {
                "contact : ${barcode.contactInfo}"
            }

            Barcode.TYPE_PHONE -> {
                "phone : ${barcode.phone}"
            }

            Barcode.TYPE_CALENDAR_EVENT -> {
                "calender event : ${barcode.calendarEvent}"
            }

            Barcode.TYPE_GEO -> {
                "geo point : ${barcode.geoPoint}"
            }

            Barcode.TYPE_ISBN -> {
                "isbn : ${barcode.displayValue}"
            }

            Barcode.TYPE_DRIVER_LICENSE -> {
                "driving license : ${barcode.driverLicense}"
            }

            Barcode.TYPE_SMS -> {
                "sms : ${barcode.sms}"
            }

            Barcode.TYPE_TEXT -> {
                return barcode.rawValue ?: ""
            }

            Barcode.TYPE_UNKNOWN -> {
                "unknown : ${barcode.rawValue}"
            }

            else -> {
                "Couldn't determine"
            }
        }

    }

    sealed class UiEvent {
        data class ShowSnackbar(val message: String, val isError: Boolean = false) : UiEvent()
    }
}
