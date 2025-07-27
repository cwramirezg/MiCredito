package com.github.cwramirezg.micredito.splash.presentation.states

sealed class SplashUiState {
    object Loading : SplashUiState()
    object NavigateToOnboarding : SplashUiState()
    object NavigateToAuth : SplashUiState()
    object NavigateToHome : SplashUiState()
    data class Error(val message: String) : SplashUiState()
}
