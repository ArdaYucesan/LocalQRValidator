package com.ssidglobal.qrreader.presentation.savedcodes

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssidglobal.qrreader.domain.model.QRData
import com.ssidglobal.qrreader.domain.use_case.DeleteUseCase
import com.ssidglobal.qrreader.domain.use_case.GetAllUseCase
import com.ssidglobal.qrreader.domain.use_case.SaveUseCase
import com.ssidglobal.qrreader.presentation.add.AddQRScreenState
import com.ssidglobal.qrreader.presentation.add.AddQRViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SavedViewModel @Inject constructor(
    private val saveQrUseCase: SaveUseCase,
    private val getAllUseCase: GetAllUseCase,
    private val deleteQrUseCase: DeleteUseCase,
) : ViewModel() {

    val TAG = "__SavedQRCodesViewModel"

    var state = mutableStateOf(SavedQRScreenState())
        private set

    var eventFlow = MutableSharedFlow<SavedViewModel.UiEvent>()
        private set

    private var recentlyDeletedCode : QRData? = null

//    recentlyDeletedNote = event.note


    private var getCodesJob: Job? = null;

    init {
        getCodes()
    }

    fun onEvent(event: SavedEvent) {
        when (event) {
            is SavedEvent.DeleteQR -> {
                viewModelScope.launch {
                    deleteQrUseCase.invoke(event.qrData)
                    recentlyDeletedCode = event.qrData
                }
            }

            is SavedEvent.RestoreQR -> {
                viewModelScope.launch {
                    saveQrUseCase.invoke(recentlyDeletedCode?:return@launch)
                    recentlyDeletedCode = null
                }
            }
        }
    }

    private fun getCodes() {
        getCodesJob?.cancel()

        getCodesJob = getAllUseCase.invoke().onEach { codes ->
            state.value = state.value.copy(
                qrCodes = codes
            )

        }.launchIn(viewModelScope)
    }


    sealed class UiEvent {
        data class ShowSnackbar(val message: String, val isError: Boolean = false) : UiEvent()
    }
}