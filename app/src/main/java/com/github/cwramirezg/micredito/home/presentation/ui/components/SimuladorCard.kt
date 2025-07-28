package com.github.cwramirezg.micredito.home.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.cwramirezg.micredito.home.domain.entities.EstadoLineaCredito
import com.github.cwramirezg.micredito.home.domain.entities.LineaCredito

@Composable
fun SimuladorCard(
    lineaCredito: LineaCredito,
    montoSeleccionado: Double,
    plazoSeleccionado: Int,
    onMontoChange: (Double) -> Unit,
    onPlazoChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Simula tu crédito",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            MontoSlider(
                valor = montoSeleccionado,
                minimo = lineaCredito.montoMinimo,
                maximo = lineaCredito.montoMaximo,
                onValueChange = onMontoChange
            )

            PlazoSelector(
                plazoSeleccionado = plazoSeleccionado,
                plazos = generatePlazos(lineaCredito.plazoMinimo, lineaCredito.plazoMaximo),
                onPlazoChange = onPlazoChange
            )
        }
    }
}

private fun generatePlazos(minimo: Int, maximo: Int): List<Int> {
    return when {
        maximo <= 12 -> listOf(6, 9, 12).filter { it in minimo..maximo }
        maximo <= 24 -> listOf(6, 12, 18, 24).filter { it in minimo..maximo }
        else -> listOf(6, 12, 18, 24, 36).filter { it in minimo..maximo }
    }
}

@Preview(showBackground = true)
@Composable
fun SimuladorCardPreview() {
    val lineaCredito = LineaCredito(
        id = "1",
        createdAt = 24234234,
        updatedAt = 24234234,
        clienteId = "c123",
        montoMaximo = 100000.0,
        montoMinimo = 10000.0,
        tasaInteres = 0.0,
        plazoMinimo = 24,
        plazoMaximo = 60,
        estado = EstadoLineaCredito.ACTIVA,
        fechaVencimiento = 23123123
    )
    SimuladorCard(
        lineaCredito = lineaCredito,
        montoSeleccionado = 50000.0,
        plazoSeleccionado = 12,
        onMontoChange = {},
        onPlazoChange = {}
    )
}