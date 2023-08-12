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

package com.ssidglobal.qrreader.presentation.scanner

import QRReaderTheme
import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role.Companion.Image
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ssidglobal.qrreader.R
import com.ssidglobal.qrreader.presentation.HomeSections
import com.ssidglobal.qrreader.presentation.HomeTopAppBar
import com.ssidglobal.qrreader.presentation.QRReaderBottomBar
import com.ssidglobal.qrreader.presentation.add.CustomSnackbarVisuals
import com.ssidglobal.qrreader.presentation.savedcodes.QRCodeItem
import com.ssidglobal.qrreader.presentation.savedcodes.SavedEvent
import com.ssidglobal.qrreader.presentation.savedcodes.SavedViewModel
import com.ssidglobal.qrreader.ui.theme.md_theme_light_error
import com.ssidglobal.qrreader.ui.theme.md_theme_light_onError
import com.ssidglobal.qrreader.ui.theme.md_theme_light_onTertiary
import com.ssidglobal.qrreader.ui.theme.md_theme_light_tertiary
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QRScannerScreenContainer(
    onNavigateToRoute: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: QRScannerViewModel = hiltViewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is QRScannerViewModel.UiEvent.ShowSnackbar -> {
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
    QRCodeScreen(
        onNavigateToRoute = onNavigateToRoute,
        state = viewModel.scannerState.value,
        snackbarHostState = snackbarHostState,
        onEvent = { viewModel.onEvent(it) })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QRCodeScreen(
    onNavigateToRoute: (String) -> Unit,
    modifier: Modifier = Modifier,
    state: QRScannerScreenState,
    snackbarHostState: SnackbarHostState,
    onEvent: (QRScannerEvent) -> Unit
) {
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
        topBar = { HomeTopAppBar(title = "QR Kontrol") },
        bottomBar = {
            QRReaderBottomBar(
                tabs = HomeSections.values(),
                currentRoute = HomeSections.QRSCREEN.route,
                navigateToRoute = onNavigateToRoute
            )
        },
        modifier = modifier,

        ) { paddingValues ->
        Surface(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(modifier=Modifier.padding(top = 10.dp),text = "Kabul Edilenler")
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(15.dp)
                        .border(1.dp, color = Color.Black, shape = RoundedCornerShape(10.dp))
                        .weight(2f), contentAlignment = Alignment.Center
                ) {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(state.validQRCodes) { code ->
                            Log.d("__SavedContainer", "SavedQRCodeScreen items ${code.qrOwner} ")
                            QRCodeItem(
                                modifier = Modifier.fillMaxWidth(),
                                isDeletable = false,
                                qrOwner = code.qrOwner,
                                qrValue = code.value,
                                id = code.id,
                                onDeleteClick = {
                                }
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(0.5f), contentAlignment = Alignment.Center
                ) {
                    OutlinedButton(
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.inversePrimary),
                        onClick = { onEvent(QRScannerEvent.onQRReadClick) }) {
                        Text(
                            text = "QR TARA",
                            color = Color.Black,
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                }
            }
        }
    }
}

@Preview("default")
@Composable
fun HomePreview() {
    QRReaderTheme {
        QRScannerScreenContainer(onNavigateToRoute = { })
    }
}
