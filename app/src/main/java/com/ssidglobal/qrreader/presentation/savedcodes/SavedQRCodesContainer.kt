package com.ssidglobal.qrreader.presentation.savedcodes

import QRReaderTheme
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ssidglobal.qrreader.presentation.HomeSections
import com.ssidglobal.qrreader.presentation.HomeTopAppBar
import com.ssidglobal.qrreader.presentation.QRReaderBottomBar
import com.ssidglobal.qrreader.presentation.add.AddQREvent
import com.ssidglobal.qrreader.presentation.add.AddQRScreenState
import com.ssidglobal.qrreader.presentation.add.AddQRViewModel
import com.ssidglobal.qrreader.presentation.add.CustomSnackbarVisuals
import com.ssidglobal.qrreader.ui.theme.md_theme_light_error
import com.ssidglobal.qrreader.ui.theme.md_theme_light_onError
import com.ssidglobal.qrreader.ui.theme.md_theme_light_onTertiary
import com.ssidglobal.qrreader.ui.theme.md_theme_light_tertiary
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedQRCodesContainer(
    onNavigateToRoute: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SavedViewModel = hiltViewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is SavedViewModel.UiEvent.ShowSnackbar -> {
//                    snackbarHostState.showSnackbar(event.message)
                    if (event.isError) {

                        snackbarHostState.showSnackbar(
                            CustomSnackbarVisuals(
                                message = event.message,
                                containerColor = md_theme_light_error,
                                contentColor = md_theme_light_onError,
                            )
                        )
                    } else {
                        snackbarHostState.showSnackbar(
                            CustomSnackbarVisuals(
                                message = event.message,
                                containerColor = md_theme_light_tertiary,
                                contentColor = md_theme_light_onTertiary,
                            )
                        )
                    }
                }
            }
        }
    }

    SavedQRCodeScreen(
        onNavigateToRoute = onNavigateToRoute,
        onEvent = { viewModel.onEvent(it) },
        snackbarHostState = snackbarHostState,
        state = viewModel.state.value
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun SavedQRCodeScreen(
    modifier: Modifier = Modifier,
    onNavigateToRoute: (String) -> Unit,
    onEvent: (SavedEvent) -> Unit,
    snackbarHostState: SnackbarHostState,
    state: SavedQRScreenState
) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState, snackbar = { snackbarData: SnackbarData ->
                val customVisuals = snackbarData.visuals as? CustomSnackbarVisuals
                if (customVisuals != null) {
                    Snackbar(
                        snackbarData = snackbarData,
                        contentColor = customVisuals.contentColor,
                        containerColor = customVisuals.containerColor,
                        actionColor = customVisuals.actionColor
                    )
                } else {
                    Snackbar(snackbarData = snackbarData)
                }
            })
        },
        topBar = {
            HomeTopAppBar(title = "Kayıtlı Biletler")
        },
        bottomBar = {
            QRReaderBottomBar(
                tabs = HomeSections.values(),
                currentRoute = HomeSections.SAVEDCODES.route,
                navigateToRoute = onNavigateToRoute
            )
        }, modifier = modifier
    ) { paddingValues ->
        Surface(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(state.qrCodes) { code ->
                    Log.d("__SavedContainer", "SavedQRCodeScreen items ${code.qrOwner} ")
                    QRCodeItem(
                        modifier = Modifier.fillMaxWidth(),
                        qrOwner = code.qrOwner,
                        qrValue = code.value,
                        id = code.id,
                        onDeleteClick = {
                            onEvent(SavedEvent.DeleteQR(code))
                            scope.launch {
                                val result = snackbarHostState.showSnackbar(
                                    message = "QR Silindi",
                                    actionLabel = "Geri al",
                                    duration = SnackbarDuration.Long
                                )
                                if (result == SnackbarResult.ActionPerformed) {
                                    onEvent(SavedEvent.RestoreQR)
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun QRCodeItem(
    modifier: Modifier = Modifier,
    qrOwner: String,
    qrValue: String,
    isDeletable: Boolean = true,
    id: Int,
    onDeleteClick: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(3f)
                    .fillMaxHeight()
                    .padding(horizontal = 15.dp, vertical = 15.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = qrOwner)
                Divider(modifier = Modifier.padding(vertical = 5.dp))
                Text(text = "QR : ${qrValue}", style = MaterialTheme.typography.bodySmall)
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(start = 20.dp),
                verticalArrangement = Arrangement.Center
            ) {
                IconButton(
                    onClick = onDeleteClick,
                ) {
                    if (isDeletable) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "delete code"
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.VerifiedUser,
                            contentDescription = "validuser",
                            tint = MaterialTheme.colorScheme.tertiary
                        )
                    }
                }
            }
        }
    }
}

@Preview("default")
@Composable
private fun SavedQRCodePreview() {
    QRReaderTheme {
        SavedQRCodeScreen(
            onNavigateToRoute = {},
            onEvent = {},
            snackbarHostState = SnackbarHostState(),
            state = SavedQRScreenState()
        )
    }
}
