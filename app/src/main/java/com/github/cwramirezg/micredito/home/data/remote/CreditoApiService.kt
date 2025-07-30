package com.github.cwramirezg.micredito.home.data.remote

import com.github.cwramirezg.micredito.home.data.remote.dto.ApiResponse
import com.github.cwramirezg.micredito.home.data.remote.dto.ClienteDto
import com.github.cwramirezg.micredito.home.data.remote.dto.EstadoSolicitudDto
import com.github.cwramirezg.micredito.home.data.remote.dto.LineaCreditoDto
import com.github.cwramirezg.micredito.home.data.remote.dto.SolicitudCreditoDto
import com.github.cwramirezg.micredito.home.data.remote.dto.SolicitudCreditoRequestDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface CreditoApiService {

    @GET("clientes/{clienteId}")
    suspend fun obtenerCliente(
        @Path("clienteId") clienteId: String
    ): Response<ApiResponse<ClienteDto>>

    @GET("clientes/{clienteId}/linea-credito")
    suspend fun obtenerLineaCredito(
        @Path("clienteId") clienteId: String
    ): Response<ApiResponse<List<LineaCreditoDto>>>

    @POST("solicitudes-credito")
    suspend fun enviarSolicitudCredito(
        @Body solicitud: SolicitudCreditoRequestDto
    ): Response<ApiResponse<SolicitudCreditoDto>>

    @GET("clientes/{clienteId}/solicitudes")
    suspend fun obtenerHistorialSolicitudes(
        @Path("clienteId") clienteId: String
    ): Response<ApiResponse<List<SolicitudCreditoDto>>>

    @GET("solicitudes/{solicitudId}/estado")
    suspend fun consultarEstadoSolicitud(
        @Path("solicitudId") solicitudId: String
    ): Response<ApiResponse<EstadoSolicitudDto>>
}
