package com.github.cwramirezg.micredito.home.data.remote.datasource

import com.github.cwramirezg.micredito.core.data.network.NetworkResult
import com.github.cwramirezg.micredito.home.data.remote.CreditoApiService
import com.github.cwramirezg.micredito.home.data.remote.dto.ApiResponse
import com.github.cwramirezg.micredito.home.data.remote.dto.ClienteDto
import com.github.cwramirezg.micredito.home.data.remote.dto.LineaCreditoDto
import com.github.cwramirezg.micredito.home.data.remote.dto.SolicitudCreditoDto
import com.github.cwramirezg.micredito.home.data.remote.dto.SolicitudCreditoRequestDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response
import timber.log.Timber
import javax.inject.Inject

class CreditoRemoteDataSource @Inject constructor(
    private val apiService: CreditoApiService
) {

    suspend fun obtenerCliente(clienteId: String): Flow<NetworkResult<ClienteDto>> = flow {
        emit(NetworkResult.Loading())
        try {
            val response = apiService.obtenerCliente(clienteId)
            emit(handleApiResponse(response))
        } catch (e: Exception) {
            emit(NetworkResult.Error("Error de conexión: ${e.message}"))
        }
    }

    suspend fun obtenerLineaCredito(clienteId: String): NetworkResult<List<LineaCreditoDto>> =
        try {
            Timber.d("Obteniendo datos de API para clienteId: $clienteId")
            val response = apiService.obtenerLineaCredito(clienteId)
            val result = handleApiResponse(response)
            Timber.d("Resultado de API: $result")
            result
        } catch (e: Exception) {
            Timber.e("Error en API: ${e.message}")
            NetworkResult.Error("Error de red: ${e.message}")
        }

    suspend fun enviarSolicitudCredito(
        solicitud: SolicitudCreditoRequestDto
    ): Flow<NetworkResult<SolicitudCreditoDto>> = flow {
        emit(NetworkResult.Loading())
        try {
            val response = apiService.enviarSolicitudCredito(solicitud)
            emit(handleApiResponse(response))
        } catch (e: Exception) {
            emit(NetworkResult.Error("Error al enviar solicitud: ${e.message}"))
        }
    }

    suspend fun obtenerHistorialSolicitudes(
        clienteId: String
    ): Flow<NetworkResult<List<SolicitudCreditoDto>>> = flow {
        emit(NetworkResult.Loading())
        try {
            val response = apiService.obtenerHistorialSolicitudes(clienteId)
            emit(handleApiResponse(response))
        } catch (e: Exception) {
            emit(NetworkResult.Error("Error al obtener historial: ${e.message}"))
        }
    }

    private fun <T> handleApiResponse(response: Response<ApiResponse<T>>): NetworkResult<T> {
        return when {
            response.isSuccessful -> {
                val apiResponse = response.body()
                when {
                    apiResponse?.success == true && apiResponse.data != null -> {
                        NetworkResult.Success(apiResponse.data)
                    }

                    else -> NetworkResult.Error(
                        apiResponse?.message ?: "Error desconocido"
                    )
                }
            }

            response.code() == 401 -> NetworkResult.Error("Sesión expirada")
            response.code() == 404 -> NetworkResult.Error("Información no encontrada")
            response.code() >= 500 -> NetworkResult.Error("Error del servidor")
            else -> NetworkResult.Error("Error en la solicitud: ${response.code()}")
        }
    }
}
