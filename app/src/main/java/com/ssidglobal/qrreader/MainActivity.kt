package com.ssidglobal.qrreader

import QRReaderTheme
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.google.android.gms.common.moduleinstall.ModuleInstallClient
import com.google.android.gms.common.moduleinstall.ModuleInstallRequest
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var moduleInstallClient: ModuleInstallClient
    @Inject
    lateinit var moduleInstallRequest: ModuleInstallRequest
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        moduleInstallClient
            .installModules(moduleInstallRequest)
            .addOnSuccessListener {response->
                if (response.areModulesAlreadyInstalled()) {
                    // Modules are already installed when the request is sent.
                    Log.e("__Module", "onCreate: already installed", )
                }
                Log.d("__Base", "onCreate: module installed")
            }
            .addOnFailureListener {
                // Handle failure…
                Log.e("__Base", "onCreate: failred ${it.message}", )
            }

        setContent {
            QRReaderApp()
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    QRReaderTheme {
        Greeting("Android")
    }
}