package com.github.cwramirezg.micredito.home.presentation.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.cwramirezg.micredito.core.presentation.base.BaseScreen
import com.github.cwramirezg.micredito.core.presentation.states.UiState
import com.github.cwramirezg.micredito.home.presentation.states.CreditoUiState
import com.github.cwramirezg.micredito.home.presentation.states.SimulacionUiState
import com.github.cwramirezg.micredito.home.presentation.states.SolicitudUiState
import com.github.cwramirezg.micredito.home.presentation.ui.components.ClienteCard
import com.github.cwramirezg.micredito.home.presentation.ui.components.LineaCreditoCard
import com.github.cwramirezg.micredito.home.presentation.ui.components.LoadingButton
import com.github.cwramirezg.micredito.home.presentation.ui.components.ResumenCreditoCard
import com.github.cwramirezg.micredito.home.presentation.ui.components.SimuladorCard
import com.github.cwramirezg.micredito.home.presentation.viewmodel.CreditoViewModel

@Composable
fun CreditoHomeScreen(
    viewModel: CreditoViewModel = hiltViewModel(),
    onNavigateToSimulacion: () -> Unit = {}
) {
    val creditoUiState by viewModel.creditoUiState.collectAsStateWithLifecycle()
    val simulacionUiState by viewModel.simulacionUiState.collectAsStateWithLifecycle()
    val solicitudUiState by viewModel.solicitudUiState.collectAsStateWithLifecycle()

    val montoSeleccionado by viewModel.montoSeleccionado.collectAsStateWithLifecycle()
    val plazoSeleccionado by viewModel.plazoSeleccionado.collectAsStateWithLifecycle()

    // Manejo de efectos secundarios
    solicitudUiState.let { estado ->
        when (estado) {
            is SolicitudUiState.Success -> {
                LaunchedEffect(estado.solicitudId) {
                    // Mostrar snackbar de éxito
                    // En producción: mostrar dialog de confirmación
                    viewModel.limpiarEstadoSolicitud()
                }
            }

            else -> { /* No action needed */
            }
        }
    }

    BaseScreen(
        uiState = when (creditoUiState) {
            is CreditoUiState.Loading -> UiState.Loading
            is CreditoUiState.Success -> UiState.Success(creditoUiState)
            is CreditoUiState.Error -> UiState.Error((creditoUiState as CreditoUiState.Error).message)
            is CreditoUiState.Idle -> UiState.Idle
        },
        onRetry = viewModel::reintentar
    ) { creditoState ->
        CreditoContent(
            creditoState = creditoState as CreditoUiState.Success,
            simulacionUiState = simulacionUiState,
            solicitudUiState = solicitudUiState,
            montoSeleccionado = montoSeleccionado,
            plazoSeleccionado = plazoSeleccionado,
            onMontoChange = viewModel::actualizarMonto,
            onPlazoChange = viewModel::actualizarPlazo,
            onSolicitarCredito = viewModel::solicitarCredito,
            onNavigateToSimulacion = onNavigateToSimulacion
        )
    }
}

@Composable
private fun CreditoContent(
    creditoState: CreditoUiState.Success,
    simulacionUiState: SimulacionUiState,
    solicitudUiState: SolicitudUiState,
    montoSeleccionado: Double,
    plazoSeleccionado: Int,
    onMontoChange: (Double) -> Unit,
    onPlazoChange: (Int) -> Unit,
    onSolicitarCredito: () -> Unit,
    onNavigateToSimulacion: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header con datos del cliente
        ClienteCard(
            cliente = creditoState.cliente,
            onHistoryClick = onNavigateToSimulacion
        )

        // Card de línea de crédito
        LineaCreditoCard(
            lineaCredito = creditoState.lineaCredito
        )

        // Simulador de crédito
        SimuladorCard(
            lineaCredito = creditoState.lineaCredito,
            montoSeleccionado = montoSeleccionado,
            plazoSeleccionado = plazoSeleccionado,
            onMontoChange = onMontoChange,
            onPlazoChange = onPlazoChange
        )

        // Resumen de simulación
        when (simulacionUiState) {
            is SimulacionUiState.Success -> {
                ResumenCreditoCard(
                    monto = simulacionUiState.monto,
                    plazo = simulacionUiState.plazo,
                    cuotaMensual = simulacionUiState.cuotaMensual,
                    interesTotal = simulacionUiState.interesTotal,
                    montoTotal = simulacionUiState.montoTotal
                )
            }

            is SimulacionUiState.Calculating -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is SimulacionUiState.Error -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = simulacionUiState.message,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }

            else -> { /* Idle state */
            }
        }

        // Botón de solicitar crédito
        val habilitado = simulacionUiState is SimulacionUiState.Success &&
                solicitudUiState !is SolicitudUiState.Loading

        LoadingButton(
            onClick = onSolicitarCredito,
            enabled = habilitado,
            loading = solicitudUiState is SolicitudUiState.Loading,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text(
                text = when (solicitudUiState) {
                    is SolicitudUiState.Loading -> "Enviando..."
                    is SolicitudUiState.Error -> if (solicitudUiState.isOffline) "Guardar offline" else "Solicitar Crédito"
                    else -> "Solicitar Crédito"
                },
                style = MaterialTheme.typography.titleMedium
            )
        }

        // Mensaje de error de solicitud
        if (solicitudUiState is SolicitudUiState.Error) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (solicitudUiState.isOffline)
                        MaterialTheme.colorScheme.secondaryContainer
                    else
                        MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = solicitudUiState.message,
                        color = if (solicitudUiState.isOffline)
                            MaterialTheme.colorScheme.onSecondaryContainer
                        else
                            MaterialTheme.colorScheme.onErrorContainer
                    )

                    if (solicitudUiState.isOffline) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Tu solicitud se enviará automáticamente cuando tengas conexión.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }
        }
    }
}
