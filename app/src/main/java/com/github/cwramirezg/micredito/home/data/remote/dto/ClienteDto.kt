package com.github.cwramirezg.micredito.home.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ClienteDto(
    @SerialName("id") val id: String,
    @SerialName("nombres") val nombres: String,
    @SerialName("apellidos") val apellidos: String,
    @SerialName("dni") val dni: String,
    @SerialName("telefono") val telefono: String,
    @SerialName("email") val email: String,
    @SerialName("tipo_cliente") val tipoCliente: String,
    @SerialName("created_at") val createdAt: String,
    @SerialName("updated_at") val updatedAt: String
)
