package com.ssidglobal.qrreader.presentation.add

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssidglobal.qrreader.domain.model.InvalidQRDataException
import com.ssidglobal.qrreader.domain.model.QRData
import com.ssidglobal.qrreader.domain.use_case.SaveUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddQRViewModel @Inject constructor(
    private val saveUseCase: SaveUseCase,
) : ViewModel() {

    val TAG = "__AddQRViewModel"

    var state = mutableStateOf(AddQRScreenState())
        private set

    var eventFlow = MutableSharedFlow<UiEvent>()
        private set

    private var AddQrJob: Job? = null;

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
                        saveUseCase.invoke(QRData(qrOwner = state.value.qrOwner, value = state.value.qrValue,isValid = false))
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
        }
    }

    sealed class UiEvent {
        data class ShowSnackbar(val message: String, val isError: Boolean = false) : UiEvent()
    }
}
