package com.github.cwramirezg.micredito.home.presentation.states

sealed class SolicitudUiState {
    object Idle : SolicitudUiState()
    object Loading : SolicitudUiState()

    data class Success(
        val mensaje: String,
        val solicitudId: String
    ) : SolicitudUiState()

    data class Error(
        val message: String,
        val isOffline: Boolean = false,
        val idSolicitud: String = ""
    ) : SolicitudUiState()
}
