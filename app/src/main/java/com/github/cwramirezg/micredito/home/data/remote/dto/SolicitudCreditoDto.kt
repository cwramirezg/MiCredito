package com.github.cwramirezg.micredito.home.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SolicitudCreditoDto(
    @SerialName("id") val id: String,
    @SerialName("cliente_id") val clienteId: String,
    @SerialName("linea_credito_id") val lineaCreditoId: String,
    @SerialName("monto") val monto: Double,
    @SerialName("plazo") val plazo: Int,
    @SerialName("tasa_interes") val tasaInteres: Double,
    @SerialName("cuota_mensual") val cuotaMensual: Double,
    @SerialName("interes_total") val interesTotal: Double,
    @SerialName("monto_total") val montoTotal: Double,
    @SerialName("estado") val estado: String,
    @SerialName("observaciones") val observaciones: String?,
    @SerialName("created_at") val createdAt: String,
    @SerialName("updated_at") val updatedAt: String
)
