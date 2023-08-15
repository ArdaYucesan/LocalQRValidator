/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ssidglobal.qrreader.presentation.add

import QRReaderTheme
import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Scanner
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
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
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ssidglobal.qrreader.presentation.HomeSections
import com.ssidglobal.qrreader.presentation.HomeTopAppBar
import com.ssidglobal.qrreader.presentation.QRReaderBottomBar
import com.ssidglobal.qrreader.presentation.scanner.QRScannerEvent
import com.ssidglobal.qrreader.presentation.scanner.QRScannerViewModel
import com.ssidglobal.qrreader.ui.theme.md_theme_light_error
import com.ssidglobal.qrreader.ui.theme.md_theme_light_onError
import com.ssidglobal.qrreader.ui.theme.md_theme_light_onPrimary
import com.ssidglobal.qrreader.ui.theme.md_theme_light_onSurface
import com.ssidglobal.qrreader.ui.theme.md_theme_light_onTertiary
import com.ssidglobal.qrreader.ui.theme.md_theme_light_primary
import com.ssidglobal.qrreader.ui.theme.md_theme_light_surface
import com.ssidglobal.qrreader.ui.theme.md_theme_light_tertiary
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class CustomSnackbarVisuals(
    override val message: String,
    override val duration: SnackbarDuration = SnackbarDuration.Short,
    val containerColor: Color = Color.White,
    val contentColor: Color = Color.Red,
    val actionColor: Color = Color.Blue,
    override val actionLabel: String? = null,
    override val withDismissAction: Boolean = false
) : SnackbarVisuals

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddQRCodeContainer(
    onNavigateToRoute: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AddQRViewModel = hiltViewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is AddQRViewModel.UiEvent.ShowSnackbar -> {
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

    AddQRCode(
        snackbarHostState = snackbarHostState,
        onNavigateToRoute = onNavigateToRoute,
        onEvent = { viewModel.onEvent(it) },
        state = viewModel.state.value
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun AddQRCode(
    modifier: Modifier = Modifier,
    onNavigateToRoute: (String) -> Unit,
    onEvent: (AddQREvent) -> Unit,
    snackbarHostState: SnackbarHostState,
    state: AddQRScreenState
) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

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
            HomeTopAppBar(title = "Bilet Kayıt")
        },
        bottomBar = {
            QRReaderBottomBar(
                tabs = HomeSections.values(),
                currentRoute = HomeSections.ADDCODE.route,
                navigateToRoute = onNavigateToRoute
            )
        }, modifier = modifier
    ) { paddingValues ->
        Surface(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),

                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {

                        Row(verticalAlignment = Alignment.CenterVertically) {

                            OutlinedTextField(
                                modifier = Modifier.padding(10.dp),
                                value = state.qrOwner,
                                onValueChange = { value ->
                                    onEvent(AddQREvent.QRCodeOwnerEntered(value))
                                },
                                label = { Text("Bilet Sahibi") },
                                trailingIcon = {
                                    when {
                                        state.qrOwner.isNotEmpty() -> IconButton(onClick = {
                                            onEvent(AddQREvent.QRCodeOwnerEntered(""))
                                        }) {
                                            Icon(
                                                imageVector = Icons.Filled.Close,
                                                contentDescription = "Clear"
                                            )
                                        }
                                    }
                                },
                                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                                keyboardOptions = KeyboardOptions.Default.copy(
                                    imeAction = ImeAction.Done,
                                    keyboardType = KeyboardType.Text
                                ),
                            )
                            IconButton(onClick = { }) {

                                Icon(
                                    modifier = Modifier
                                        .width(30.dp)
                                        .height(30.dp),
                                    imageVector = Icons.Default.VerifiedUser,
                                    contentDescription = "scanner"
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {

                            OutlinedTextField(
                                modifier = Modifier.padding(10.dp),
                                value = state.qrValue,
                                onValueChange = { value ->
                                    onEvent(AddQREvent.QRCodeValueEntered(value))
                                },
                                label = { Text("QR Kod") },
                                trailingIcon = {
                                    when {
                                        state.qrValue.isNotEmpty() -> IconButton(onClick = {
                                            onEvent(AddQREvent.QRCodeValueEntered(""))
                                        }) {
                                            Icon(
                                                imageVector = Icons.Filled.Clear,
                                                contentDescription = "Clear"
                                            )
                                        }
                                    }
                                },
                                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                                keyboardOptions = KeyboardOptions.Default.copy(
                                    imeAction = ImeAction.Done,
                                    keyboardType = KeyboardType.Text
                                ),
                            )
                            IconButton(onClick = { onEvent(AddQREvent.onQRReadClicked) }) {

                                Icon(
                                    modifier = Modifier
                                        .width(30.dp)
                                        .height(30.dp),
                                    imageVector = Icons.Default.QrCodeScanner,
                                    contentDescription = "scanner"
                                )
                            }
                        }
                        OutlinedButton(
                            onClick = {
                                focusManager.clearFocus()
                                onEvent(
                                    AddQREvent.QRCodeSaved
                                )
                            }
                        ) {
                            Icon(imageVector = Icons.Filled.Add, contentDescription = "SaveQR")
                            Text(text = "Kaydet")
                        }
                    }
                }

            }
        }
    }
}


@Preview("default")
@Composable
private fun SearchBarPreview() {
    QRReaderTheme {
        AddQRCode(
            onNavigateToRoute = {},
            onEvent = {},
            snackbarHostState = SnackbarHostState(),
            state = AddQRScreenState()
        )
    }
}
