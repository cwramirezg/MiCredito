package com.github.cwramirezg.micredito.home.domain.entities

import com.github.cwramirezg.micredito.home.presentation.pojos.ConfirmacionSuccess
import com.github.cwramirezg.micredito.home.presentation.pojos.EstadoConfirmacion

data class Confirmacion(
    val montoSolicitado: Double,
    val tasa: Double,
    val plazo: Int,
    val cuotaMensual: Double,
    val estado: EstadoConfirmacion
)

fun Confirmacion.toConfirmacionSuccess()= ConfirmacionSuccess(
    montoSolicitado = montoSolicitado,
    tasa = tasa,
    plazo = plazo,
    cuotaMensual = cuotaMensual,
    estado = estado
)
