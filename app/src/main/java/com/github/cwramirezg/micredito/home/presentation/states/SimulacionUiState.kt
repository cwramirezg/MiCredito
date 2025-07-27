package com.github.cwramirezg.micredito.home.presentation.states

sealed class SimulacionUiState {
    object Idle : SimulacionUiState()
    object Calculating : SimulacionUiState()

    data class Success(
        val monto: Double,
        val plazo: Int,
        val cuotaMensual: Double,
        val interesTotal: Double,
        val montoTotal: Double
    ) : SimulacionUiState()

    data class Error(
        val message: String
    ) : SimulacionUiState()
}
