package com.github.cwramirezg.micredito.home.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.cwramirezg.micredito.core.presentation.utils.toCurrency

@Composable
fun ResumenCreditoCard(
    monto: Double,
    plazo: Int,
    cuotaMensual: Double,
    interesTotal: Double,
    montoTotal: Double,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Resumen de tu crédito",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            ResumenItem(
                label = "Monto solicitado",
                valor = monto.toCurrency()
            )

            ResumenItem(
                label = "Plazo",
                valor = "$plazo meses"
            )

            HorizontalDivider(
                Modifier,
                DividerDefaults.Thickness,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
            )

            ResumenItem(
                label = "Cuota mensual",
                valor = cuotaMensual.toCurrency(),
                destacado = true
            )

            ResumenItem(
                label = "Interés total",
                valor = interesTotal.toCurrency()
            )

            ResumenItem(
                label = "Monto total a pagar",
                valor = montoTotal.toCurrency(),
                destacado = true
            )
        }
    }
}

@Composable
private fun ResumenItem(
    label: String,
    valor: String,
    destacado: Boolean = false,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = if (destacado) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            fontWeight = if (destacado) FontWeight.Bold else FontWeight.Normal
        )

        Text(
            text = valor,
            style = if (destacado) MaterialTheme.typography.titleLarge else MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ResumenCreditoCardPreview() {
    ResumenCreditoCard(
        monto = 10000.0,
        plazo = 12,
        cuotaMensual = 833.33,
        interesTotal = 8333.33,
        montoTotal = 108333.33
    )
}
