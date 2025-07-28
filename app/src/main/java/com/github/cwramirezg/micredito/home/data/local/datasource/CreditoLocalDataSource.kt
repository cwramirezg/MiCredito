package com.github.cwramirezg.micredito.home.data.local.datasource

import com.github.cwramirezg.micredito.core.data.network.NetworkResult
import com.github.cwramirezg.micredito.home.data.local.dao.CreditoDao
import com.github.cwramirezg.micredito.home.data.local.dao.SimulacionDao
import com.github.cwramirezg.micredito.home.data.local.entities.LineaCreditoEntity
import com.github.cwramirezg.micredito.home.data.local.entities.SimulacionEntity
import com.github.cwramirezg.micredito.home.data.local.entities.SolicitudPendienteEntity
import kotlinx.coroutines.flow.Flow
import timber.log.Timber
import javax.inject.Inject

class CreditoLocalDataSource @Inject constructor(
    private val creditoDao: CreditoDao,
    private val simulacionDao: SimulacionDao
) {

    suspend fun obtenerLineaCreditoLocal(clienteId: String): NetworkResult<LineaCreditoEntity> =
        try {
            Timber.d("Obteniendo datos locales para clienteId: $clienteId")
            val entity = creditoDao.obtenerLineaCreditoActiva(clienteId)
            if (entity != null) {
                Timber.d("Datos encontrados en cache local")
                NetworkResult.Success(entity)
            } else {
                Timber.d("No hay datos en cache local")
                NetworkResult.Error("No hay datos locales")
            }
        } catch (e: Exception) {
            Timber.e("Error en cache local: ${e.message}")
            NetworkResult.Error("Error en cache local: ${e.message}")
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
