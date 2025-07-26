package com.github.cwramirezg.micredito.home.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SolicitudCreditoRequestDto(
    @SerialName("cliente_id") val clienteId: String,
    @SerialName("linea_credito_id") val lineaCreditoId: String,
    @SerialName("monto") val monto: Double,
    @SerialName("plazo") val plazo: Int
)
