package com.github.cwramirezg.micredito.home.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "simulaciones")
data class SimulacionEntity(
    @PrimaryKey val id: String = java.util.UUID.randomUUID().toString(),
    val clienteId: String,
    val monto: Double,
    val plazo: Int,
    val tasaInteres: Double,
    val cuotaMensual: Double,
    val interesTotal: Double,
    val montoTotal: Double,
    val fechaSimulacion: Long,
    val esFavorita: Boolean = false
)
