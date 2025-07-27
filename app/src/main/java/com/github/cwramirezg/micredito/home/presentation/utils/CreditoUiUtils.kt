package com.github.cwramirezg.micredito.home.presentation.utils

import androidx.compose.ui.graphics.Color
import com.github.cwramirezg.micredito.home.domain.entities.EstadoLineaCredito
import com.github.cwramirezg.micredito.home.domain.entities.EstadoSolicitud
import com.github.cwramirezg.micredito.home.domain.entities.TipoCliente

object CreditoUiUtils {

    fun getColorForEstadoLineaCredito(estado: EstadoLineaCredito): Color {
        return when (estado) {
            EstadoLineaCredito.ACTIVA -> Color(0xFF4CAF50)
            EstadoLineaCredito.SUSPENDIDA -> Color(0xFFFF9800)
            EstadoLineaCredito.VENCIDA -> Color(0xFFF44336)
            EstadoLineaCredito.CANCELADA -> Color(0xFF9E9E9E)
        }
    }

    fun getDescripcionEstado(estado: EstadoSolicitud): String {
        return when (estado) {
            EstadoSolicitud.PENDIENTE -> "Pendiente de evaluación"
            EstadoSolicitud.EN_EVALUACION -> "En proceso de evaluación"
            EstadoSolicitud.APROBADA -> "¡Aprobada!"
            EstadoSolicitud.RECHAZADA -> "No aprobada"
            EstadoSolicitud.DESEMBOLSADA -> "Desembolsada"
        }
    }

    fun getBeneficiosTipoCliente(tipo: TipoCliente): List<String> {
        return when (tipo) {
            TipoCliente.NUEVO -> listOf(
                "Tasa preferencial para nuevos clientes",
                "Sin comisiones el primer mes"
            )

            TipoCliente.RECURRENTE -> listOf(
                "Tasa reducida por historial",
                "Aprobación más rápida",
                "Mayor línea de crédito"
            )

            TipoCliente.PREMIUM -> listOf(
                "Mejor tasa del mercado",
                "Aprobación inmediata",
                "Línea de crédito premium",
                "Atención personalizada"
            )
        }
    }
}
