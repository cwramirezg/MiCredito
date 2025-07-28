package com.github.cwramirezg.micredito.home.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun PlazoSelector(
    plazoSeleccionado: Int,
    plazos: List<Int>,
    onPlazoChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = "Plazo (meses)",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            plazos.forEach { plazo ->
                FilterChip(
                    onClick = { onPlazoChange(plazo) },
                    label = { Text("$plazo meses") },
                    selected = plazo == plazoSeleccionado,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PlazoSelectorPreview() {
    PlazoSelector(
        plazoSeleccionado = 12,
        plazos = listOf(6, 12, 24, 36, 48, 60),
        onPlazoChange = {}
    )
}