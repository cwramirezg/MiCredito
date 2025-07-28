package com.github.cwramirezg.micredito.home.data.repository

import com.github.cwramirezg.micredito.core.data.network.NetworkResult
import com.github.cwramirezg.micredito.home.data.local.datasource.CreditoLocalDataSource
import com.github.cwramirezg.micredito.home.data.local.entities.SimulacionEntity
import com.github.cwramirezg.micredito.home.data.local.entities.SolicitudPendienteEntity
import com.github.cwramirezg.micredito.home.data.mappers.ClienteMapper
import com.github.cwramirezg.micredito.home.data.mappers.LineaCreditoMapper
import com.github.cwramirezg.micredito.home.data.mappers.SolicitudMapper
import com.github.cwramirezg.micredito.home.data.remote.datasource.CreditoRemoteDataSource
import com.github.cwramirezg.micredito.home.data.remote.dto.SolicitudCreditoRequestDto
import com.github.cwramirezg.micredito.home.domain.entities.Cliente
import com.github.cwramirezg.micredito.home.domain.entities.LineaCredito
import com.github.cwramirezg.micredito.home.domain.entities.SimulacionCredito
import com.github.cwramirezg.micredito.home.domain.entities.SolicitudCredito
import com.github.cwramirezg.micredito.home.domain.entities.SolicitudCreditoRequest
import com.github.cwramirezg.micredito.home.domain.repository.CreditoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject

class CreditoRepositoryImpl @Inject constructor(
    private val remoteDataSource: CreditoRemoteDataSource,
    private val localDataSource: CreditoLocalDataSource
) : CreditoRepository {

    override fun obtenerLineaCredito(clienteId: String): Flow<NetworkResult<LineaCredito>> =
        flow {
            Timber.d("=== Iniciando obtención de línea de crédito ===")
            emit(NetworkResult.Loading())
            try {
                val localData = localDataSource.obtenerLineaCreditoLocal(clienteId)
                if (localData is NetworkResult.Success && localData.data != null) {
                    Timber.d("Datos encontrados en cache local")
                    emit(NetworkResult.Success(LineaCreditoMapper.fromEntityToDomain(localData.data)))
                }

                // 2. Siempre intentar obtener datos frescos de la red
                Timber.d("Obteniendo datos de la red para clienteId: $clienteId")
                val remoteData = remoteDataSource.obtenerLineaCredito(clienteId)

                when (remoteData) {
                    is NetworkResult.Success -> {
                        Timber.d("Datos obtenidos exitosamente de la red")
                        val lineaCredito = LineaCreditoMapper.fromDtoToDomain(remoteData.data)

                        // Guardar en cache local
                        val lineaCreditoEntity = LineaCreditoMapper.fromDtoToEntity(remoteData.data)
                        localDataSource.guardarLineaCredito(lineaCreditoEntity)

                        emit(NetworkResult.Success(lineaCredito))
                    }

                    is NetworkResult.Error -> {
                        Timber.d("Error de red: ${remoteData.message}")
                        // Si ya emitimos datos del cache, no emitir error
                        if (localData !is NetworkResult.Success) {
                            emit(NetworkResult.Error("No hay conexión y no se encontraron datos locales"))
                        }
                    }

                    is NetworkResult.Loading -> {
                        // No emitir loading adicional
                    }
                }
            } catch (e: Exception) {
                Timber.e("Exception en obtenerLineaCredito: ${e.message}")
                emit(NetworkResult.Error("Error inesperado: ${e.message}"))
            }
        }

    override suspend fun obtenerDatosCliente(clienteId: String): Flow<NetworkResult<Cliente>> {
        return remoteDataSource.obtenerCliente(clienteId).map { result ->
            when (result) {
                is NetworkResult.Success -> NetworkResult.Success(
                    ClienteMapper.fromDtoToDomain(
                        result.data
                    )
                )

                is NetworkResult.Error -> NetworkResult.Error(result.message ?: "Unknown error")
                is NetworkResult.Loading -> NetworkResult.Loading()
            }
        }
    }

    override suspend fun enviarSolicitudCredito(
        solicitud: SolicitudCreditoRequest
    ): Flow<NetworkResult<SolicitudCredito>> = flow {
        emit(NetworkResult.Loading())

        val solicitudDto = SolicitudCreditoRequestDto(
            clienteId = solicitud.clienteId,
            lineaCreditoId = solicitud.lineaCreditoId,
            monto = solicitud.monto,
            plazo = solicitud.plazo
        )

        remoteDataSource.enviarSolicitudCredito(solicitudDto).collect { result ->
            when (result) {
                is NetworkResult.Success -> {
                    val solicitudCredito = SolicitudMapper.fromDtoToDomain(result.data)
                    emit(NetworkResult.Success(solicitudCredito))
                }

                is NetworkResult.Error -> {
                    // Guardar para reintento offline
                    val solicitudPendiente = SolicitudPendienteEntity(
                        clienteId = solicitud.clienteId,
                        lineaCreditoId = solicitud.lineaCreditoId,
                        monto = solicitud.monto,
                        plazo = solicitud.plazo
                    )
                    localDataSource.guardarSolicitudPendiente(solicitudPendiente)
                    emit(NetworkResult.Error("Sin conexión. La solicitud se enviará automáticamente cuando haya internet."))
                }

                is NetworkResult.Loading -> emit(NetworkResult.Loading())
            }
        }
    }

    override suspend fun obtenerHistorialSolicitudes(
        clienteId: String
    ): Flow<NetworkResult<List<SolicitudCredito>>> {
        return remoteDataSource.obtenerHistorialSolicitudes(clienteId).map { result ->
            when (result) {
                is NetworkResult.Success -> {
                    val solicitudes = result.data.map { SolicitudMapper.fromDtoToDomain(it) }
                    NetworkResult.Success(solicitudes)
                }

                is NetworkResult.Error -> NetworkResult.Error(result.message ?: "Unknown error")
                is NetworkResult.Loading -> NetworkResult.Loading()
            }
        }
    }

    override suspend fun guardarSimulacionTemporal(
        simulacion: SimulacionCredito
    ): Flow<NetworkResult<Unit>> = flow {
        try {
            val simulacionEntity = SimulacionEntity(
                clienteId = "current_user", // Obtener del PreferencesManager
                monto = simulacion.monto,
                plazo = simulacion.plazo,
                tasaInteres = simulacion.tasaInteres,
                cuotaMensual = simulacion.cuotaMensual,
                interesTotal = simulacion.interesTotal,
                montoTotal = simulacion.montoTotal,
                fechaSimulacion = simulacion.fechaSimulacion
            )

            localDataSource.guardarSimulacion(simulacionEntity)
            emit(NetworkResult.Success(Unit))
        } catch (e: Exception) {
            emit(NetworkResult.Error("Error al guardar simulación: ${e.message}"))
        }
    }

    override suspend fun obtenerSimulacionesGuardadas(): Flow<NetworkResult<List<SimulacionCredito>>> =
        flow {
            try {
                localDataSource.obtenerSimulacionesGuardadas("current_user").collect { entidades ->
                    val simulaciones = entidades.map { entity ->
                        SimulacionCredito(
                            monto = entity.monto,
                            plazo = entity.plazo,
                            tasaInteres = entity.tasaInteres,
                            cuotaMensual = entity.cuotaMensual,
                            interesTotal = entity.interesTotal,
                            montoTotal = entity.montoTotal,
                            fechaSimulacion = entity.fechaSimulacion
                        )
                    }
                    emit(NetworkResult.Success(simulaciones))
                }
            } catch (e: Exception) {
                emit(NetworkResult.Error("Error al obtener simulaciones: ${e.message}"))
            }
        }

    override suspend fun reintentarSolicitudesPendientes(): Flow<NetworkResult<List<SolicitudCredito>>> =
        flow {
            emit(NetworkResult.Loading())

            try {
                val solicitudesPendientes = localDataSource.obtenerSolicitudesPendientes()
                val solicitudesEnviadas = mutableListOf<SolicitudCredito>()

                solicitudesPendientes.forEach { solicitudPendiente ->
                    // Limitar reintentos
                    if (solicitudPendiente.intentos < 3) {
                        val solicitudDto = SolicitudCreditoRequestDto(
                            clienteId = solicitudPendiente.clienteId,
                            lineaCreditoId = solicitudPendiente.lineaCreditoId,
                            monto = solicitudPendiente.monto,
                            plazo = solicitudPendiente.plazo
                        )

                        remoteDataSource.enviarSolicitudCredito(solicitudDto).collect { result ->
                            when (result) {
                                is NetworkResult.Success -> {
                                    val solicitudCredito =
                                        SolicitudMapper.fromDtoToDomain(result.data)
                                    solicitudesEnviadas.add(solicitudCredito)
                                    localDataSource.eliminarSolicitudPendiente(solicitudPendiente)
                                }

                                is NetworkResult.Error -> {
                                    localDataSource.actualizarIntentoSolicitud(solicitudPendiente)
                                }

                                is NetworkResult.Loading -> { /* Ignorar */
                                }
                            }
                        }
                    }
                }

                emit(NetworkResult.Success(solicitudesEnviadas))
            } catch (e: Exception) {
                emit(NetworkResult.Error("Error al reintentar solicitudes: ${e.message}"))
            }
        }
}
