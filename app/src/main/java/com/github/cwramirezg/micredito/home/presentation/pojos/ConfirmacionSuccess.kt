package com.github.cwramirezg.micredito.home.presentation.pojos

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