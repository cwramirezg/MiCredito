package com.github.cwramirezg.micredito.home.presentation.states

import com.github.cwramirezg.micredito.home.domain.entities.Cliente
import com.github.cwramirezg.micredito.home.domain.entities.LineaCredito

sealed class CreditoUiState {
    object Loading : CreditoUiState()

    data class Success(
        val cliente: Cliente,
        val lineaCredito: LineaCredito
    ) : CreditoUiState()

    data class Error(
        val message: String,
        val canRetry: Boolean = true
    ) : CreditoUiState()

    object Idle : CreditoUiState()
}
