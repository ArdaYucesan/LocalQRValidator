package com.ssidglobal.qrreader

import QRReaderTheme
import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.navigation
import com.ssidglobal.qrreader.presentation.HomeSections
import com.ssidglobal.qrreader.presentation.addHomeGraph
import com.ssidglobal.qrreader.navigation.MainDestinations
import com.ssidglobal.qrreader.navigation.rememberQRReaderNavController

@Composable
fun QRReaderApp() {
    QRReaderTheme {
        val jetsnackNavController = rememberQRReaderNavController()
        NavHost(
            navController = jetsnackNavController.navController,
            startDestination = MainDestinations.HOME_ROUTE
        ) {
            jetsnackNavGraph(
                onNavigateToRoute = jetsnackNavController::navigateToBottomBarRoute
            )
        }
    }
}

private fun NavGraphBuilder.jetsnackNavGraph(
    onNavigateToRoute: (String) -> Unit
) {
    navigation(
        route = MainDestinations.HOME_ROUTE,
        startDestination = HomeSections.QRSCREEN.route
    ) {
        addHomeGraph(onNavigateToRoute)
    }
}