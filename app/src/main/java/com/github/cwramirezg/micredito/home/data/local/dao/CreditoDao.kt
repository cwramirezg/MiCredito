package com.github.cwramirezg.micredito.home.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.github.cwramirezg.micredito.home.data.local.entities.LineaCreditoEntity
import com.github.cwramirezg.micredito.home.data.local.entities.SolicitudPendienteEntity

@Dao
interface CreditoDao {

    @Query("SELECT * FROM lineas_credito WHERE clienteId = :clienteId AND estado = 'ACTIVA'")
    suspend fun obtenerLineaCreditoActiva(clienteId: String): LineaCreditoEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarLineaCredito(lineaCredito: LineaCreditoEntity)

    @Query("SELECT * FROM solicitudes_pendientes WHERE estadoLocal = 'PENDIENTE_ENVIO'")
    suspend fun obtenerSolicitudesPendientes(): List<SolicitudPendienteEntity>

    @Insert
    suspend fun insertarSolicitudPendiente(solicitud: SolicitudPendienteEntity)

    @Delete
    suspend fun eliminarSolicitudPendiente(solicitud: SolicitudPendienteEntity)

    @Update
    suspend fun actualizarSolicitudPendiente(solicitud: SolicitudPendienteEntity)
}
