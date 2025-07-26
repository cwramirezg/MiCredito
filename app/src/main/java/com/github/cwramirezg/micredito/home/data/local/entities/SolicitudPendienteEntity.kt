package com.github.cwramirezg.micredito.home.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "solicitudes_pendientes")
data class SolicitudPendienteEntity(
    @PrimaryKey val id: String = java.util.UUID.randomUUID().toString(),
    val clienteId: String,
    val lineaCreditoId: String,
    val monto: Double,
    val plazo: Int,
    val intentos: Int = 0,
    val ultimoIntento: Long? = null,
    val fechaCreacion: Long = System.currentTimeMillis(),
    val estadoLocal: String = "PENDIENTE_ENVIO"
)
