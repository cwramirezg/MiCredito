package com.github.cwramirezg.micredito.home.presentation.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.cwramirezg.micredito.core.presentation.base.BaseScreenWithAppBar
import com.github.cwramirezg.micredito.home.presentation.pojos.CreditoSuccess
import com.github.cwramirezg.micredito.home.presentation.ui.components.ClienteCard
import com.github.cwramirezg.micredito.home.presentation.ui.components.LineaCreditoCard
import com.github.cwramirezg.micredito.home.presentation.viewmodel.CreditoViewModel

@Composable
fun CreditoHomeScreen(
    viewModel: CreditoViewModel = hiltViewModel(),
    onNavigateToHistory: () -> Unit = {},
    onNavigateToSimulacion: (String) -> Unit = {}
) {
    val uiState by viewModel.creditoUiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.cargarDatosCredito()
    }

    BaseScreenWithAppBar(
        uiState = uiState,
        title = "Mis lineas de crédito",
        onRetry = viewModel::reintentar
    ) { success ->
        CreditoContent(
            creditoSuccess = success,
            onNavigateToHistory = onNavigateToHistory,
            onNavigateToSimulacion = onNavigateToSimulacion
        )
    }
}

@Composable
private fun CreditoContent(
    creditoSuccess: CreditoSuccess,
    onNavigateToHistory: () -> Unit = {},
    onNavigateToSimulacion: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        creditoSuccess.cliente?.let {
            item {
                ClienteCard(
                    cliente = it,
                    onHistoryClick = onNavigateToHistory
                )
            }
        }
        items(creditoSuccess.lineaCreditos) { linea ->
            LineaCreditoCard(
                lineaCredito = linea,
                onClick = onNavigateToSimulacion
            )
        }
    }
}
