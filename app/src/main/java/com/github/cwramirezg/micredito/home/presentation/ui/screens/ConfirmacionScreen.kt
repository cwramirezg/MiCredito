package com.github.cwramirezg.micredito.home.presentation.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.cwramirezg.micredito.core.presentation.base.BaseScreenWithAppBar
import com.github.cwramirezg.micredito.core.presentation.utils.toCurrency
import com.github.cwramirezg.micredito.core.presentation.utils.toPercentage
import com.github.cwramirezg.micredito.home.presentation.pojos.ConfirmacionSuccess
import com.github.cwramirezg.micredito.home.presentation.pojos.EstadoConfirmacion
import com.github.cwramirezg.micredito.home.presentation.ui.components.InfoItem
import com.github.cwramirezg.micredito.home.presentation.viewmodel.ConfirmacionViewModel

@Composable
fun ConfirmacionScreen(
    viewModel: ConfirmacionViewModel = hiltViewModel(),
    onNavigateToHome: () -> Unit
) {
    val uiState by viewModel.confirmacionUiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.obtenerSolicitud()
    }

    BaseScreenWithAppBar(
        uiState = uiState,
        title = "Confirmación de simulación",
    ) { state ->
        ConfirmacionContent(
            state = state,
            onNavigateToHome = onNavigateToHome
        )
    }
}

@Composable
private fun ConfirmacionContent(
    state: ConfirmacionSuccess,
    onNavigateToHome: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Resumen de tu crédito",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                InfoItem(
                    label = "Monto solicitado",
                    valor = state.montoSolicitado.toCurrency()
                )

                Spacer(modifier = Modifier.height(8.dp))

                InfoItem(
                    label = "Tasa de interés",
                    valor = state.tasa.toPercentage()
                )

                Spacer(modifier = Modifier.height(8.dp))

                InfoItem(
                    label = "Plazo",
                    valor = "${state.plazo} meses"
                )

                Spacer(modifier = Modifier.height(8.dp))

                InfoItem(
                    label = "Cuota mensual estimada",
                    valor = state.cuotaMensual.toCurrency()
                )

                Spacer(modifier = Modifier.height(8.dp))
                InfoItem(
                    label = "Estado",
                    valor = when (state.estado) {
                        EstadoConfirmacion.POR_ENVIAR -> "Por enviar"
                        EstadoConfirmacion.ENVIADO -> "Enviado"
                        EstadoConfirmacion.RECHAZADO -> "Rechazado"
                    },
                    valorColor = when (state.estado) {
                        EstadoConfirmacion.POR_ENVIAR -> Color.Gray
                        EstadoConfirmacion.ENVIADO -> Color(0xFF388E3C)
                        EstadoConfirmacion.RECHAZADO -> Color(0xFFD32F2F)
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onNavigateToHome,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Volver al inicio")
        }
    }
}
