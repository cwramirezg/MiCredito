package com.github.cwramirezg.micredito.home.domain.repository

import com.github.cwramirezg.micredito.core.data.repository.RepositoryResult
import com.github.cwramirezg.micredito.home.domain.entities.Cliente
import com.github.cwramirezg.micredito.home.domain.entities.Confirmacion
import com.github.cwramirezg.micredito.home.domain.entities.LineaCredito
import com.github.cwramirezg.micredito.home.domain.entities.SimulacionCredito
import com.github.cwramirezg.micredito.home.domain.entities.SolicitudCredito
import com.github.cwramirezg.micredito.home.domain.entities.SolicitudCreditoRequest
import kotlinx.coroutines.flow.Flow

interface CreditoRepository {
    fun obtenerLineaCredito(clienteId: String): Flow<RepositoryResult<List<LineaCredito>>>

    suspend fun obtenerDatosCliente(clienteId: String): Flow<RepositoryResult<Cliente>>

    suspend fun enviarSolicitudCredito(
        solicitud: SolicitudCreditoRequest
    ): Flow<RepositoryResult<SolicitudCredito>>

    suspend fun obtenerHistorialSolicitudes(
        clienteId: String
    ): Flow<RepositoryResult<List<SolicitudCredito>>>

    suspend fun obtenerSolicitudCredito(
        solicitudId: String
    ): Flow<RepositoryResult<Confirmacion>>

    suspend fun guardarSimulacionTemporal(simulacion: SimulacionCredito): Flow<RepositoryResult<Unit>>

    suspend fun obtenerSimulacionesGuardadas(): Flow<RepositoryResult<List<SimulacionCredito>>>

    suspend fun reintentarSolicitudesPendientes(): Flow<RepositoryResult<List<SolicitudCredito>>>
}
