package com.github.cwramirezg.micredito.home.domain.entities

import com.github.cwramirezg.micredito.core.domain.models.BaseEntity

data class Cliente(
    override val id: String,
    override val createdAt: Long,
    override val updatedAt: Long,
    val nombres: String,
    val apellidos: String,
    val dni: String,
    val telefono: String,
    val email: String,
    val tipoCliente: TipoCliente
) : BaseEntity()

enum class TipoCliente {
    NUEVO,
    RECURRENTE,
    PREMIUM
}

val Cliente.nombreCompleto: String
    get() = "$nombres $apellidos"
