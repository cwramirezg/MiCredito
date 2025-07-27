package com.github.cwramirezg.micredito.home.presentation.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.github.cwramirezg.micredito.home.presentation.ui.screens.CreditoHomeScreen

sealed class CreditoDestination(val route: String) {
    object Home : CreditoDestination("credito_home")
    object Historia : CreditoDestination("credito_historia")
    object Confirmacion : CreditoDestination("credito_confirmacion/{solicitudId}") {
        fun createRoute(solicitudId: String) = "credito_confirmacion/$solicitudId"
    }
}

@Composable
fun CreditoNavigation(
    navController: NavHostController = rememberNavController(),
    startDestination: String = CreditoDestination.Home.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(CreditoDestination.Home.route) {
            CreditoHomeScreen(
                onNavigateToHistory = {
                    navController.navigate(CreditoDestination.Historia.route)
                }
            )
        }

        composable(CreditoDestination.Historia.route) {
            // HistorialScreen implementation
            // En este reto nos enfocaremos en la pantalla principal
        }

        composable(CreditoDestination.Confirmacion.route) { backStackEntry ->
            val solicitudId = backStackEntry.arguments?.getString("solicitudId") ?: ""
            // ConfirmacionScreen implementation
        }
    }
}
