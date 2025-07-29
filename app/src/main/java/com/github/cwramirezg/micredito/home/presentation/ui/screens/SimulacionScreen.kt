package com.github.cwramirezg.micredito.home.presentation.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.cwramirezg.micredito.core.presentation.base.BaseScreen
import com.github.cwramirezg.micredito.home.presentation.pojos.SimulacionSuccess
import com.github.cwramirezg.micredito.home.presentation.states.SolicitudUiState
import com.github.cwramirezg.micredito.home.presentation.ui.components.LoadingButton
import com.github.cwramirezg.micredito.home.presentation.ui.components.ResumenCreditoCard
import com.github.cwramirezg.micredito.home.presentation.ui.components.SimuladorCard
import com.github.cwramirezg.micredito.home.presentation.viewmodel.SimulacionViewModel

@Composable
fun SimulacionScreen(
    viewModel: SimulacionViewModel = hiltViewModel(),
    onNavigateToConfirmacion: () -> Unit = {}
) {
    val uiState by viewModel.simulacionUiState.collectAsStateWithLifecycle()
    val solicitudUiState by viewModel.solicitudUiState.collectAsStateWithLifecycle()
    val montoSeleccionado by viewModel.montoSeleccionado.collectAsStateWithLifecycle()
    val plazoSeleccionado by viewModel.plazoSeleccionado.collectAsStateWithLifecycle()

    SimuladorCard(
        lineaCredito = viewModel.lineaCredito,
        montoSeleccionado = montoSeleccionado,
        plazoSeleccionado = plazoSeleccionado,
        onMontoChange = viewModel::actualizarMonto,
        onPlazoChange = viewModel::actualizarPlazo
    )
    BaseScreen(
        uiState = uiState,
        idleContent = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Seleccione un monto para simular",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    ) { state ->
        SimulacionContent(
            state = state,
            onSolicitarCredito = viewModel::solicitarCredito,
            solicitudUiState = solicitudUiState
        )
    }


}

@Composable
private fun SimulacionContent(
    state: SimulacionSuccess,
    onSolicitarCredito: () -> Unit,
    solicitudUiState: SolicitudUiState,
) {
    ResumenCreditoCard(
        monto = state.monto,
        plazo = state.plazo,
        cuotaMensual = state.cuotaMensual,
        interesTotal = state.interesTotal,
        montoTotal = state.montoTotal
    )
    val habilitado = solicitudUiState !is SolicitudUiState.Loading
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
