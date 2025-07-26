package com.github.cwramirezg.micredito.home.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lineas_credito")
data class LineaCreditoEntity(
    @PrimaryKey val id: String,
    val clienteId: String,
    val montoMaximo: Double,
    val montoMinimo: Double,
    val tasaInteres: Double,
    val plazoMaximo: Int,
    val plazoMinimo: Int,
    val estado: String,
    val fechaVencimiento: Long,
    val createdAt: Long,
    val updatedAt: Long,
    val lastSyncAt: Long = System.currentTimeMillis()
)
