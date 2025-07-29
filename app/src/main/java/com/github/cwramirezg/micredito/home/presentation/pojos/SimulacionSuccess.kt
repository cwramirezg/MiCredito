package com.github.cwramirezg.micredito.home.presentation.pojos

import kotlinx.serialization.Serializable

@Serializable
data class SimulacionSuccess(
    val monto: Double,
    val plazo: Int,
    val cuotaMensual: Double,
    val interesTotal: Double,
    val montoTotal: Double
)
