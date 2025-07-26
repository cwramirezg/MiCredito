package com.github.cwramirezg.micredito.home.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LineaCreditoDto(
    @SerialName("id") val id: String,
    @SerialName("cliente_id") val clienteId: String,
    @SerialName("monto_maximo") val montoMaximo: Double,
    @SerialName("monto_minimo") val montoMinimo: Double,
    @SerialName("tasa_interes") val tasaInteres: Double,
    @SerialName("plazo_maximo") val plazoMaximo: Int,
    @SerialName("plazo_minimo") val plazoMinimo: Int,
    @SerialName("estado") val estado: String,
    @SerialName("fecha_vencimiento") val fechaVencimiento: String,
    @SerialName("created_at") val createdAt: String,
    @SerialName("updated_at") val updatedAt: String
)
