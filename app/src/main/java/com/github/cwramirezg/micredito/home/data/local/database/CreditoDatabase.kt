package com.github.cwramirezg.micredito.home.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.github.cwramirezg.micredito.home.data.local.dao.CreditoDao
import com.github.cwramirezg.micredito.home.data.local.dao.SimulacionDao
import com.github.cwramirezg.micredito.home.data.local.entities.LineaCreditoEntity
import com.github.cwramirezg.micredito.home.data.local.entities.SimulacionEntity
import com.github.cwramirezg.micredito.home.data.local.entities.SolicitudPendienteEntity

@Database(
    entities = [
        LineaCreditoEntity::class,
        SimulacionEntity::class,
        SolicitudPendienteEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class CreditoDatabase : RoomDatabase() {

    abstract fun creditoDao(): CreditoDao
    abstract fun simulacionDao(): SimulacionDao

    companion object {
        const val DATABASE_NAME = "credito_database"
    }
}