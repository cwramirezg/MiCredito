package com.github.cwramirezg.micredito.home.presentation.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.cwramirezg.micredito.core.presentation.base.BaseScreenWithAppBar
import com.github.cwramirezg.micredito.core.presentation.states.UiState
import com.github.cwramirezg.micredito.home.domain.entities.LineaCredito
import com.github.cwramirezg.micredito.home.presentation.pojos.SimulacionSuccess
import com.github.cwramirezg.micredito.home.presentation.pojos.SolicitudSuccess
import com.github.cwramirezg.micredito.home.presentation.ui.components.LoadingButton
import com.github.cwramirezg.micredito.home.presentation.ui.components.ResumenCreditoCard
import com.github.cwramirezg.micredito.home.presentation.ui.components.SimuladorCard
import com.github.cwramirezg.micredito.home.presentation.viewmodel.SimulacionViewModel
import timber.log.Timber

@Composable
fun SimulacionScreen(
    viewModel: SimulacionViewModel = hiltViewModel(),
    onNavigateToConfirmacion: (String) -> Unit = {}
) {
    val uiState by viewModel.simulacionUiState.collectAsStateWithLifecycle()
    val solicitudUiState by viewModel.solicitudUiState.collectAsStateWithLifecycle()
    val montoSeleccionado by viewModel.montoSeleccionado.collectAsStateWithLifecycle()
    val plazoSeleccionado by viewModel.plazoSeleccionado.collectAsStateWithLifecycle()

    BaseScreenWithAppBar(
        uiState = uiState,
        title = "Simular crédito"
    ) { state ->
        SimulacionContent(
            state = state,
            lineaCredito = viewModel.lineaCredito,
            onSolicitarCredito = {
                Timber.d("Solicitando crédito: $solicitudUiState")
                when (solicitudUiState) {
                    is UiState.Error -> {
                        Timber.d("Solicitando crédito error")
                        if ((solicitudUiState as UiState.Error).isOffline) {
                            onNavigateToConfirmacion((solicitudUiState as UiState.Error).idSolicitud)
                        }
                    }

                    else -> {
                        Timber.d("Solicitando crédito else")
                        viewModel.solicitarCredito()
                    }
                }
            },
            solicitudUiState = solicitudUiState,
            montoSeleccionado = montoSeleccionado,
            plazoSeleccionado = plazoSeleccionado,
            onMontoChange = viewModel::actualizarMonto,
            onPlazoChange = viewModel::actualizarPlazo
        )
    }
}

@Composable
private fun SimulacionContent(
    state: SimulacionSuccess,
    lineaCredito: LineaCredito,
    onSolicitarCredito: () -> Unit,
    solicitudUiState: UiState<SolicitudSuccess>,
    montoSeleccionado: Double,
    plazoSeleccionado: Int,
    onMontoChange: (Double) -> Unit,
    onPlazoChange: (Int) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SimuladorCard(
            lineaCredito = lineaCredito,
            montoSeleccionado = montoSeleccionado,
            plazoSeleccionado = plazoSeleccionado,
            onMontoChange = onMontoChange,
            onPlazoChange = onPlazoChange
        )
        ResumenCreditoCard(
            monto = state.monto,
            plazo = state.plazo,
            cuotaMensual = state.cuotaMensual,
            interesTotal = state.interesTotal,
            montoTotal = state.montoTotal
        )
        val habilitado = solicitudUiState !is UiState.Loading
        LoadingButton(
            onClick = onSolicitarCredito,
            enabled = habilitado,
            loading = solicitudUiState is UiState.Loading,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text(
                text = when (solicitudUiState) {
                    is UiState.Loading -> "Enviando..."
                    is UiState.Error -> {
                        if (solicitudUiState.isOffline) "Guardar offline" else "Solicitar crédito"
                    }

                    else -> "Solicitar Crédito"
                },
                style = MaterialTheme.typography.titleMedium
            )
        }
        // Mensaje de error de solicitud
        if (solicitudUiState is UiState.Error) {
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
