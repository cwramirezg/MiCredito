package com.github.cwramirezg.micredito.home.presentation.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.cwramirezg.micredito.core.presentation.base.BaseScreen
import com.github.cwramirezg.micredito.core.presentation.states.UiState
import com.github.cwramirezg.micredito.home.presentation.states.CreditoUiState
import com.github.cwramirezg.micredito.home.presentation.ui.components.ClienteCard
import com.github.cwramirezg.micredito.home.presentation.ui.components.LineaCreditoCard
import com.github.cwramirezg.micredito.home.presentation.viewmodel.CreditoViewModel

@Composable
fun CreditoHomeScreen(
    viewModel: CreditoViewModel = hiltViewModel(),
    onNavigateToSimulacion: () -> Unit = {}
) {
    val creditoUiState by viewModel.creditoUiState.collectAsStateWithLifecycle()

    BaseScreen(
        uiState = when (creditoUiState) {
            is CreditoUiState.Idle -> UiState.Idle
            is CreditoUiState.Loading -> UiState.Loading
            is CreditoUiState.Success -> UiState.Success(creditoUiState)
            is CreditoUiState.Error -> UiState.Error((creditoUiState as CreditoUiState.Error).message)
        },
        onRetry = viewModel::reintentar
    ) { creditoState ->
        CreditoContent(
            creditoState = creditoState as CreditoUiState.Success,
            onNavigateToSimulacion = onNavigateToSimulacion
        )
    }
}

@Composable
private fun CreditoContent(
    creditoState: CreditoUiState.Success,
    onNavigateToSimulacion: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ClienteCard(
            cliente = creditoState.cliente,
            onHistoryClick = onNavigateToSimulacion
        )
        LineaCreditoCard(
            lineaCredito = creditoState.lineaCredito
        )
    }
}
