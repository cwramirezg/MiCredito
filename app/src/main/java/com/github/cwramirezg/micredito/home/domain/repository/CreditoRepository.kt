package com.github.cwramirezg.micredito.home.domain.repository

import com.github.cwramirezg.micredito.core.data.network.NetworkResult
import com.github.cwramirezg.micredito.home.domain.entities.Cliente
import com.github.cwramirezg.micredito.home.domain.entities.Confirmacion
import com.github.cwramirezg.micredito.home.domain.entities.LineaCredito
import com.github.cwramirezg.micredito.home.domain.entities.SimulacionCredito
import com.github.cwramirezg.micredito.home.domain.entities.SolicitudCredito
import com.github.cwramirezg.micredito.home.domain.entities.SolicitudCreditoRequest
import com.github.cwramirezg.micredito.home.presentation.pojos.ConfirmacionSuccess
import kotlinx.coroutines.flow.Flow

interface CreditoRepository {
    fun obtenerLineaCredito(clienteId: String): Flow<NetworkResult<List<LineaCredito>>>

    suspend fun obtenerDatosCliente(clienteId: String): Flow<NetworkResult<Cliente>>

    suspend fun enviarSolicitudCredito(
        solicitud: SolicitudCreditoRequest
    ): Flow<NetworkResult<SolicitudCredito>>

    suspend fun obtenerHistorialSolicitudes(
        clienteId: String
    ): Flow<NetworkResult<List<SolicitudCredito>>>

    suspend fun obtenerSolicitudCredito(
        solicitudId: String
    ): Flow<NetworkResult<Confirmacion>>

    suspend fun guardarSimulacionTemporal(simulacion: SimulacionCredito): Flow<NetworkResult<Unit>>

    suspend fun obtenerSimulacionesGuardadas(): Flow<NetworkResult<List<SimulacionCredito>>>

    suspend fun reintentarSolicitudesPendientes(): Flow<NetworkResult<List<SolicitudCredito>>>
}
