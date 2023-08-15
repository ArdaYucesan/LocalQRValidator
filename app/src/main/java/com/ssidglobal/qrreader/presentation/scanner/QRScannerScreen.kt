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
import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.util.Log
import android.util.Size
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.ssidglobal.qrreader.presentation.HomeSections
import com.ssidglobal.qrreader.presentation.HomeTopAppBar
import com.ssidglobal.qrreader.presentation.QRReaderBottomBar
import com.ssidglobal.qrreader.presentation.add.CustomSnackbarVisuals
import com.ssidglobal.qrreader.presentation.savedcodes.QRCodeItem
import com.ssidglobal.qrreader.ui.theme.md_theme_light_error
import com.ssidglobal.qrreader.ui.theme.md_theme_light_onError
import com.ssidglobal.qrreader.ui.theme.md_theme_light_onTertiary
import com.ssidglobal.qrreader.ui.theme.md_theme_light_tertiary
import kotlinx.coroutines.flow.collectLatest

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
//                3f8e662791ca1312e350756c3121448626acbd9f462aff03d387e2f6ac053049


                Text(modifier = Modifier.padding(top = 10.dp), text = "Kabul Edilenler")

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(15.dp)
                        .weight(5f),
                    contentAlignment = Alignment.Center
                ) {

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .border(1.dp, color = Color.Black, shape = RoundedCornerShape(10.dp)),
                    ) {
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
                        .weight(0.7f)
                        .padding(top = 15.dp)
                ) {
//                    readerPreview(onEvent)
                    OutlinedButton(onClick = { onEvent(QRScannerEvent.onQRReadClick) }) {
                        Text(text = "QR TARA", style = MaterialTheme.typography.titleLarge)
                    }
                }
            }
        }
    }
}

@Composable
fun readerPreview(onEvent: (QRScannerEvent) -> Unit) {
    var oldCode by remember {
        mutableStateOf("")
    }
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember {
        ProcessCameraProvider.getInstance(context)
    }
    var hasCamPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            hasCamPermission = granted
        }
    )
    val previewView by remember { mutableStateOf(PreviewView(context)) }

    val preview = Preview.Builder().build()

    LaunchedEffect(key1 = true) {
        launcher.launch(Manifest.permission.CAMERA)
    }

    if (hasCamPermission) {
        AndroidView(
            update = {
                preview.setSurfaceProvider(previewView.surfaceProvider)
            },
            factory = { aaa ->
                val previewView = previewView
//                val preview = Preview.Builder().build()
                val selector = CameraSelector.Builder()
                    .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                    .build()
                preview.setSurfaceProvider(previewView.surfaceProvider)
                val imageAnalysis = ImageAnalysis.Builder()
                    .setTargetResolution(
                        Size(
                            previewView.width,
                            previewView.height
                        )
                    )
                    .setBackpressureStrategy(STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                imageAnalysis.setAnalyzer(
                    ContextCompat.getMainExecutor(context),
                    QRCodeAnalyzer { result ->
                        if (oldCode != result) {
                            onEvent(QRScannerEvent.onQRRead(result))
                            oldCode = result
                        }
                    }
                )
                try {
                    cameraProviderFuture.get().bindToLifecycle(
                        lifecycleOwner,
                        selector,
                        preview,
                        imageAnalysis
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                previewView
            },
        )
    }
}