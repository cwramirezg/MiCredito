package com.github.cwramirezg.micredito.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation

fun NavGraphBuilder.authenticationNavGraph(navigationManager: NavigationManager) {
    navigation<AuthenticationNavGraph>(
        startDestination = NavigationDestination.Login
    ) {
        composable<NavigationDestination.Login> {

        }

        composable<NavigationDestination.Register> {

        }
    }
}