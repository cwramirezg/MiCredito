package com.github.cwramirezg.micredito.home.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.github.cwramirezg.micredito.home.data.local.entities.SimulacionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SimulacionDao {

    @Query("SELECT * FROM simulaciones WHERE clienteId = :clienteId ORDER BY fechaSimulacion DESC")
    fun obtenerSimulacionesCliente(clienteId: String): Flow<List<SimulacionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarSimulacion(simulacion: SimulacionEntity)

    @Query("SELECT * FROM simulaciones WHERE esFavorita = 1")
    suspend fun obtenerSimulacionesFavoritas(): List<SimulacionEntity>

    @Query("DELETE FROM simulaciones WHERE fechaSimulacion < :timestamp")
    suspend fun limpiarSimulacionesAntiguas(timestamp: Long)
}
