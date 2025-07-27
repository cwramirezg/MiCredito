package com.github.cwramirezg.micredito.navigation

import kotlinx.serialization.Serializable

sealed class NavigationDestination {
    @Serializable
    object Login : NavigationDestination()

    @Serializable
    object Register : NavigationDestination()

    @Serializable
    object CreditoHome : NavigationDestination()

    @Serializable
    data class Simulacion(val clienteId: String) : NavigationDestination()

    @Serializable
    data class Confirmacion(val solicitudId: String) : NavigationDestination()

    @Serializable
    object Splash : NavigationDestination()

    @Serializable
    object Onboarding : NavigationDestination()

    @Serializable
    data class Error(val errorType: String) : NavigationDestination()
}

@Serializable
object AuthenticationNavGraph

@Serializable
object HomeNavGraph
