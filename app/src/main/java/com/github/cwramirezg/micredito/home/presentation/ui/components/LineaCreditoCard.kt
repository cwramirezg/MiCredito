package com.github.cwramirezg.micredito.home.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.cwramirezg.micredito.core.presentation.utils.toCurrency
import com.github.cwramirezg.micredito.core.presentation.utils.toPercentage
import com.github.cwramirezg.micredito.home.domain.entities.EstadoLineaCredito
import com.github.cwramirezg.micredito.home.domain.entities.LineaCredito

@Composable
fun LineaCreditoCard(
    lineaCredito: LineaCredito,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Tu línea de crédito",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                InfoItem(
                    label = "Monto disponible",
                    valor = "${lineaCredito.montoMinimo.toCurrency()} - ${lineaCredito.montoMaximo.toCurrency()}"
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                InfoItem(
                    label = "Tasa de interés",
                    valor = lineaCredito.tasaInteres.toPercentage(),
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(16.dp))

                InfoItem(
                    label = "Plazos disponibles",
                    valor = "${lineaCredito.plazoMinimo} - ${lineaCredito.plazoMaximo} meses",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun InfoItem(
    label: String,
    valor: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
        )
        Text(
            text = valor,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LineaCreditoCardPreview() {
    val lineaCredito = LineaCredito(
        id = "",
        createdAt = 876567567 ,
        updatedAt = 34534534734,
        clienteId = "4234234",
        montoMaximo = 100000.0,
        montoMinimo = 1000.0,
        tasaInteres = 17.0,
        plazoMaximo = 60,
        plazoMinimo = 24,
        estado = EstadoLineaCredito.ACTIVA,
        fechaVencimiento = 234234234
    )
    LineaCreditoCard(lineaCredito)
}