package com.github.cwramirezg.micredito.home.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EstadoSolicitudDto(
    @SerialName("solicitud_id") val solicitudId: String,
    @SerialName("estado") val estado: String,
    @SerialName("observaciones") val observaciones: String?
)
