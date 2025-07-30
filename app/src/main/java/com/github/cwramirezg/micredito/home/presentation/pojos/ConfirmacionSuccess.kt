package com.github.cwramirezg.micredito.home.presentation.pojos

import kotlinx.serialization.Serializable

@Serializable
data class ConfirmacionSuccess(
    val montoSolicitado: Double,
    val tasa: Double,
    val plazo: Int,
    val cuotaMensual: Double,
    val estado: EstadoConfirmacion
)

enum class EstadoConfirmacion {
    POR_ENVIAR,
    ENVIADO,
    RECHAZADO
}