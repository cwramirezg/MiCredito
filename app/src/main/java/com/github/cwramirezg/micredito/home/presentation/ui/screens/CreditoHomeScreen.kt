package com.github.cwramirezg.micredito.home.presentation.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.cwramirezg.micredito.core.presentation.base.BaseScreenWithAppBar
import com.github.cwramirezg.micredito.core.presentation.states.UiState
import com.github.cwramirezg.micredito.home.presentation.states.CreditoUiState
import com.github.cwramirezg.micredito.home.presentation.ui.components.ClienteCard
import com.github.cwramirezg.micredito.home.presentation.ui.components.LineaCreditoCard
import com.github.cwramirezg.micredito.home.presentation.viewmodel.CreditoViewModel

@Composable
fun CreditoHomeScreen(
    viewModel: CreditoViewModel = hiltViewModel(),
    onNavigateToHistory: () -> Unit = {},
    onNavigateToSimulacion: (String) -> Unit = {}
) {
    val creditoUiState by viewModel.creditoUiState.collectAsStateWithLifecycle()

    BaseScreenWithAppBar(
        uiState = when (creditoUiState) {
            is CreditoUiState.Idle -> UiState.Idle
            is CreditoUiState.Loading -> UiState.Loading
            is CreditoUiState.Success -> UiState.Success(creditoUiState)
            is CreditoUiState.Error -> UiState.Error((creditoUiState as CreditoUiState.Error).message)
        },
        title = "Mis lineas de crédito",
        onRetry = viewModel::reintentar
    ) { creditoState ->
        CreditoContent(
            creditoState = creditoState as CreditoUiState.Success,
            onNavigateToHistory = onNavigateToHistory,
            onNavigateToSimulacion = onNavigateToSimulacion
        )
    }
}

@Composable
private fun CreditoContent(
    creditoState: CreditoUiState.Success,
    onNavigateToHistory: () -> Unit = {},
    onNavigateToSimulacion: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            ClienteCard(
                cliente = creditoState.cliente,
                onHistoryClick = onNavigateToHistory
            )
        }
        items(creditoState.lineaCreditos) { linea ->
            LineaCreditoCard(
                lineaCredito = linea,
                onClick = onNavigateToSimulacion
            )
        }
    }
}
