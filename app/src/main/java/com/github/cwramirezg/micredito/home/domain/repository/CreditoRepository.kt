package com.github.cwramirezg.micredito.home.domain.repository

import com.github.cwramirezg.micredito.core.data.network.NetworkResult
import com.github.cwramirezg.micredito.home.domain.entities.Cliente
import com.github.cwramirezg.micredito.home.domain.entities.LineaCredito
import com.github.cwramirezg.micredito.home.domain.entities.SimulacionCredito
import com.github.cwramirezg.micredito.home.domain.entities.SolicitudCredito
import com.github.cwramirezg.micredito.home.domain.entities.SolicitudCreditoRequest
import kotlinx.coroutines.flow.Flow

interface CreditoRepository {
    /**
     * Obtiene la línea de crédito activa del cliente
     */
    suspend fun obtenerLineaCredito(clienteId: String): Flow<NetworkResult<LineaCredito>>

    /**
     * Obtiene los datos del cliente autenticado
     */
    suspend fun obtenerDatosCliente(clienteId: String): Flow<NetworkResult<Cliente>>

    /**
     * Envía una solicitud de crédito para evaluación
     */
    suspend fun enviarSolicitudCredito(
        solicitud: SolicitudCreditoRequest
    ): Flow<NetworkResult<SolicitudCredito>>

    /**
     * Obtiene el historial de solicitudes del cliente
     */
    suspend fun obtenerHistorialSolicitudes(
        clienteId: String
    ): Flow<NetworkResult<List<SolicitudCredito>>>

    /**
     * Guarda una simulación temporalmente (offline)
     */
    suspend fun guardarSimulacionTemporal(simulacion: SimulacionCredito): Flow<NetworkResult<Unit>>

    /**
     * Obtiene simulaciones guardadas localmente
     */
    suspend fun obtenerSimulacionesGuardadas(): Flow<NetworkResult<List<SimulacionCredito>>>

    /**
     * Reintenta solicitudes pendientes (cuando hay conexión)
     */
    suspend fun reintentarSolicitudesPendientes(): Flow<NetworkResult<List<SolicitudCredito>>>
}
