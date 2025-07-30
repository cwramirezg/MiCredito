package com.github.cwramirezg.micredito.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.github.cwramirezg.micredito.home.presentation.ui.screens.ConfirmacionScreen
import com.github.cwramirezg.micredito.home.presentation.ui.screens.CreditoHomeScreen
import com.github.cwramirezg.micredito.home.presentation.ui.screens.SimulacionScreen

fun NavGraphBuilder.homeNavGraph(navigationManager: NavigationManager) {
    navigation<HomeNavGraph>(
        startDestination = NavigationDestination.CreditoHome
    ) {
        composable<NavigationDestination.CreditoHome> {
            CreditoHomeScreen {
            }
        }
        composable<NavigationDestination.Simulacion> {
            SimulacionScreen()
        }
        composable<NavigationDestination.Confirmacion> {

        }
    }
}