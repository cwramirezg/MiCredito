package com.github.cwramirezg.micredito.home.data.local.datasource

import com.github.cwramirezg.micredito.core.data.network.NetworkResult
import com.github.cwramirezg.micredito.home.data.local.dao.CreditoDao
import com.github.cwramirezg.micredito.home.data.local.dao.SimulacionDao
import com.github.cwramirezg.micredito.home.data.local.entities.LineaCreditoEntity
import com.github.cwramirezg.micredito.home.data.local.entities.SimulacionEntity
import com.github.cwramirezg.micredito.home.data.local.entities.SolicitudPendienteEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CreditoLocalDataSource @Inject constructor(
    private val creditoDao: CreditoDao,
    private val simulacionDao: SimulacionDao
) {

    suspend fun obtenerLineaCreditoLocal(clienteId: String): Flow<NetworkResult<LineaCreditoEntity?>> =
        flow {
            try {
                val lineaCredito = creditoDao.obtenerLineaCreditoActiva(clienteId)
                emit(NetworkResult.Success(lineaCredito))
            } catch (e: Exception) {
                emit(NetworkResult.Error("Error al acceder a datos locales: ${e.message}"))
            }
        }

    suspend fun guardarLineaCredito(lineaCredito: LineaCreditoEntity) {
        creditoDao.insertarLineaCredito(lineaCredito)
    }

    suspend fun guardarSimulacion(simulacion: SimulacionEntity) {
        simulacionDao.insertarSimulacion(simulacion)
    }

    fun obtenerSimulacionesGuardadas(clienteId: String): Flow<List<SimulacionEntity>> {
        return simulacionDao.obtenerSimulacionesCliente(clienteId)
    }

    suspend fun guardarSolicitudPendiente(solicitud: SolicitudPendienteEntity) {
        creditoDao.insertarSolicitudPendiente(solicitud)
    }

    suspend fun obtenerSolicitudesPendientes(): List<SolicitudPendienteEntity> {
        return creditoDao.obtenerSolicitudesPendientes()
    }

    suspend fun eliminarSolicitudPendiente(solicitud: SolicitudPendienteEntity) {
        creditoDao.eliminarSolicitudPendiente(solicitud)
    }

    suspend fun actualizarIntentoSolicitud(solicitud: SolicitudPendienteEntity) {
        val solicitudActualizada = solicitud.copy(
            intentos = solicitud.intentos + 1,
            ultimoIntento = System.currentTimeMillis()
        )
        creditoDao.actualizarSolicitudPendiente(solicitudActualizada)
    }
}
