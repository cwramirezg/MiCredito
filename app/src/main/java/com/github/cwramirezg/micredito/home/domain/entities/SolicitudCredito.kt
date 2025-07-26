package com.github.cwramirezg.micredito.home.domain.entities

import com.github.cwramirezg.micredito.core.domain.models.BaseEntity

data class SolicitudCredito(
    override val id: String,
    override val createdAt: Long,
    override val updatedAt: Long,
    val clienteId: String,
    val lineaCreditoId: String,
    val simulacion: SimulacionCredito,
    val estado: EstadoSolicitud,
    val observaciones: String? = null
) : BaseEntity()

enum class EstadoSolicitud {
    PENDIENTE,
    EN_EVALUACION,
    APROBADA,
    RECHAZADA,
    DESEMBOLSADA
}

data class SolicitudCreditoRequest(
    val clienteId: String,
    val lineaCreditoId: String,
    val monto: Double,
    val plazo: Int
)