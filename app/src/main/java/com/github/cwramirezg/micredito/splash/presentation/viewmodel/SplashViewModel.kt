package com.github.cwramirezg.micredito.splash.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.github.cwramirezg.micredito.core.data.local.PreferencesManager
import com.github.cwramirezg.micredito.core.presentation.base.BaseViewModel
import com.github.cwramirezg.micredito.navigation.NavigationDestination
import com.github.cwramirezg.micredito.navigation.NavigationManager
import com.github.cwramirezg.micredito.splash.presentation.states.SplashUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager,
    private val navigationManager: NavigationManager
) : BaseViewModel() {

    private val _uiState = MutableStateFlow<SplashUiState>(SplashUiState.Loading)
    val uiState: StateFlow<SplashUiState> = _uiState.asStateFlow()

    init {
        Timber.d("SplashViewModel creado - navigationManager: $navigationManager")
        inicializarApp()
    }

    private fun inicializarApp() {
        Timber.d("Iniciando inicializarApp()")
        viewModelScope.launch {
            try {
                _uiState.value = SplashUiState.Loading
                Timber.d("Estado cambiado a Loading")

                delay(2000)
                Timber.d("Delay completado, evaluando condiciones")

                val isFirstLaunch = preferencesManager.checkAndMarkFirstLaunch()

                val hasCompletedOnboarding = preferencesManager.hasCompletedOnboarding().first()
                val isUserLoggedIn = preferencesManager.isUserLoggedIn().first()

                Timber.d("isFirstLaunch: $isFirstLaunch")
                Timber.d("hasCompletedOnboarding: $hasCompletedOnboarding")
                Timber.d("isUserLoggedIn: $isUserLoggedIn")

                when {
                    isFirstLaunch -> {
                        Timber.d("Caso: Primera vez - navegando a Onboarding")
                        _uiState.value = SplashUiState.NavigateToOnboarding
                        navigationManager.navigateTo(NavigationDestination.Onboarding)
                    }

                    !hasCompletedOnboarding -> {
                        Timber.d("Caso: Onboarding no completado - navegando a Onboarding")
                        _uiState.value = SplashUiState.NavigateToOnboarding
                        navigationManager.navigateTo(NavigationDestination.Onboarding)
                    }

                    !isUserLoggedIn -> {
                        Timber.d("Caso: Usuario no logueado - navegando a Login")
                        _uiState.value = SplashUiState.NavigateToAuth
                        navigationManager.navigateTo(NavigationDestination.Login)
                    }

                    else -> {
                        Timber.d("Caso: Usuario logueado - navegando a Home")
                        _uiState.value = SplashUiState.NavigateToHome
                        navigationManager.navigateTo(NavigationDestination.CreditoHome)
                    }
                }

            } catch (e: Exception) {
                Timber.e("Error en inicializarApp: ${e.message}")
                _uiState.value =
                    SplashUiState.Error("Error al inicializar la aplicación: ${e.message}")
            }
        }
    }

    fun retry() {
        Timber.d("Retry solicitado")
        _uiState.value = SplashUiState.Loading
        inicializarApp()
    }

    override fun onError(message: String) {
        _uiState.value = SplashUiState.Error(message)
    }
}
