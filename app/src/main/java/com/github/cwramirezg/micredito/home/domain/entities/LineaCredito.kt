package com.github.cwramirezg.micredito.home.domain.entities

import com.github.cwramirezg.micredito.core.domain.models.BaseEntity
import kotlinx.serialization.Serializable
import timber.log.Timber

@Serializable
data class LineaCredito(
    override val id: String,
    override val createdAt: Long,
    override val updatedAt: Long,
    val clienteId: String,
    val montoMaximo: Double,
    val montoMinimo: Double,
    val tasaInteres: Double, // Tasa anual en porcentaje
    val plazoMaximo: Int, // En meses
    val plazoMinimo: Int, // En meses
    val estado: EstadoLineaCredito,
    val fechaVencimiento: Long
) : BaseEntity() {

    fun esValida(): Boolean {
        Timber.d("Fecha de vencimiento: ${fechaVencimiento}, Fecha actual: ${System.currentTimeMillis()}")
        return estado == EstadoLineaCredito.ACTIVA && System.currentTimeMillis() < fechaVencimiento
    }

    fun montoDisponible(montoSolicitado: Double): Boolean =
        montoSolicitado in montoMinimo..montoMaximo

    fun plazoValido(plazoSolicitado: Int): Boolean =
        plazoSolicitado in plazoMinimo..plazoMaximo
}

enum class EstadoLineaCredito {
    ACTIVA,
    SUSPENDIDA,
    VENCIDA,
    CANCELADA
}
