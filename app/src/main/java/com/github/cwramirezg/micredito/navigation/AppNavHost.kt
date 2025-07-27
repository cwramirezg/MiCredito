package com.github.cwramirezg.micredito.navigation

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.github.cwramirezg.micredito.home.presentation.ui.screens.ConfirmacionScreen
import com.github.cwramirezg.micredito.home.presentation.ui.screens.CreditoHomeScreen
import com.github.cwramirezg.micredito.home.presentation.ui.screens.SimulacionScreen
import com.github.cwramirezg.micredito.splash.presentation.ui.OnboardingScreen
import com.github.cwramirezg.micredito.splash.presentation.ui.SplashScreen
import timber.log.Timber

@Composable
fun AppNavHost(
    navController: NavHostController,
    navigationManager: NavigationManager,
    startDestination: Any
) {
    LaunchedEffect(navigationManager) {
        Timber.d("LaunchedEffect iniciado - navigationManager: $navigationManager")
        try {
            Timber.d("Iniciando collect del navigationEvent")
            navigationManager.navigationEvent.collect { event ->
                Timber.d("Evento de navegación recibido: $event")
                when (event) {
                    is NavigationEvent.Navigate -> {
                        Timber.d("Navegando a: ${event.destination}")
                        try {
                            navController.navigate(event.destination) {
                                // Limpiar back stack cuando venimos del splash
                                when (event.destination) {
                                    is NavigationDestination.Login,
                                    is NavigationDestination.Onboarding,
                                    is NavigationDestination.CreditoHome -> {
                                        Timber.d("Limpiando back stack desde splash")
                                        if (navController.currentDestination?.route?.contains("Splash") == true) {
                                            popUpTo(NavigationDestination.Splash) {
                                                inclusive = true
                                            }
                                        }
                                    }

                                    else -> {
                                        Timber.d("Navegación normal sin limpiar back stack")
                                    }
                                }
                                Timber.d("Navegación exitosa a: ${event.destination}")
                            }
                        } catch (e: Exception) {
                            Timber.e("Error al navegar a ${event.destination}: ${e.message}")
                        }
                    }

                    NavigationEvent.NavigateBack -> {
                        Timber.d("Navegando hacia atrás")
                        if (!navController.popBackStack()) {
                            Timber.d("No hay más pantallas, cerrando app")
                            (navController.context as? Activity)?.finish()
                        }
                    }

                    NavigationEvent.ClearBackStack -> {
                        Timber.d("Limpiando back stack completo")
                        navController.navigate(NavigationDestination.CreditoHome) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "Error en navegación: ${e.message}")
        }
    }

    LaunchedEffect(navController.currentDestination) {
        Timber.d("Destino actual: ${navController.currentDestination?.route}")
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable<NavigationDestination.Splash> {
            Timber.d("Renderizando SplashScreen")
            SplashScreen()
        }

        composable<NavigationDestination.Onboarding> {
            Timber.d("Renderizando OnboardingScreen")
            OnboardingScreen(
                onComplete = {
                    Timber.d("Onboarding completado")
                    navigationManager.navigateTo(NavigationDestination.Login)
                }
            )
        }

        composable<NavigationDestination.Login> {
            Timber.d("Renderizando LoginScreen")
            /* LoginScreen(
                 onNavigateToRegister = {
                     navigationManager.navigateTo(NavigationDestination.Register)
                 },
                 onLoginSuccess = {
                     navigationManager.navigateTo(NavigationDestination.CreditoHome)
                 }
             )*/
        }

        composable<NavigationDestination.Register> {
            /*  RegisterScreen(
                  onNavigateToLogin = {
                      navigationManager.navigateBack()
                  },
                  onRegisterSuccess = {
                      navigationManager.navigateTo(NavigationDestination.CreditoHome)
                  }
              )*/
        }

        composable<NavigationDestination.CreditoHome> {
            CreditoHomeScreen(
                /*  onNavigateToSimulacion = { clienteId ->
                      navigationManager.navigateTo(NavigationDestination.Simulacion(clienteId))
                  }*/
            )
        }

        composable<NavigationDestination.Simulacion> { backStackEntry ->
            SimulacionScreen(
                /* onNavigateToConfirmacion = { solicitudId ->
                     navigationManager.navigateTo(NavigationDestination.Confirmacion(solicitudId))
                 }*/
            )
        }

        composable<NavigationDestination.Confirmacion> { backStackEntry ->
            ConfirmacionScreen(
                /*  onNavigateToHome = {
                      navigationManager.clearBackStack()
                  }*/
            )
        }

        composable<NavigationDestination.Error> { backStackEntry ->
            /*  ErrorScreen(
                  onNavigateBack = {
                      navigationManager.navigateBack()
                  }
              )*/
        }
    }
}