package com.github.cwramirezg.micredito.home.data.remote.datasource

import com.github.cwramirezg.micredito.core.data.repository.RepositoryResult
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

    suspend fun obtenerCliente(clienteId: String): Flow<RepositoryResult<ClienteDto>> = flow {
        emit(RepositoryResult.Loading())
        try {
            val response = apiService.obtenerCliente(clienteId)
            emit(handleApiResponse(response))
        } catch (e: Exception) {
            emit(RepositoryResult.Error("Error de conexión: ${e.message}"))
        }
    }

    suspend fun obtenerLineaCredito(clienteId: String): RepositoryResult<List<LineaCreditoDto>> =
        try {
            Timber.d("Obteniendo datos de API para clienteId: $clienteId")
            val response = apiService.obtenerLineaCredito(clienteId)
            val result = handleApiResponse(response)
            Timber.d("Resultado de API: $result")
            result
        } catch (e: Exception) {
            Timber.e("Error en API: ${e.message}")
            RepositoryResult.Error("Error de red: ${e.message}")
        }

    suspend fun enviarSolicitudCredito(
        solicitud: SolicitudCreditoRequestDto
    ): Flow<RepositoryResult<SolicitudCreditoDto>> = flow {
        emit(RepositoryResult.Loading())
        try {
            val response = apiService.enviarSolicitudCredito(solicitud)
            emit(handleApiResponse(response))
        } catch (e: Exception) {
            emit(RepositoryResult.Error("Error al enviar solicitud: ${e.message}"))
        }
    }

    suspend fun obtenerHistorialSolicitudes(
        clienteId: String
    ): Flow<RepositoryResult<List<SolicitudCreditoDto>>> = flow {
        emit(RepositoryResult.Loading())
        try {
            val response = apiService.obtenerHistorialSolicitudes(clienteId)
            emit(handleApiResponse(response))
        } catch (e: Exception) {
            emit(RepositoryResult.Error("Error al obtener historial: ${e.message}"))
        }
    }

    private fun <T> handleApiResponse(response: Response<ApiResponse<T>>): RepositoryResult<T> {
        return when {
            response.isSuccessful -> {
                val apiResponse = response.body()
                when {
                    apiResponse?.success == true && apiResponse.data != null -> {
                        RepositoryResult.Success(apiResponse.data)
                    }

                    else -> RepositoryResult.Error(
                        apiResponse?.message ?: "Error desconocido"
                    )
                }
            }

            response.code() == 401 -> RepositoryResult.Error("Sesión expirada")
            response.code() == 404 -> RepositoryResult.Error("Información no encontrada")
            response.code() >= 500 -> RepositoryResult.Error("Error del servidor")
            else -> RepositoryResult.Error("Error en la solicitud: ${response.code()}")
        }
    }
}
