package com.github.cwramirezg.micredito.home.data.repository

import com.github.cwramirezg.micredito.core.data.network.NetworkResult
import com.github.cwramirezg.micredito.core.data.repository.RepositoryResult
import com.github.cwramirezg.micredito.home.data.local.datasource.CreditoLocalDataSource
import com.github.cwramirezg.micredito.home.data.local.entities.SimulacionEntity
import com.github.cwramirezg.micredito.home.data.local.entities.SolicitudPendienteEntity
import com.github.cwramirezg.micredito.home.data.mappers.ClienteMapper
import com.github.cwramirezg.micredito.home.data.mappers.LineaCreditoMapper
import com.github.cwramirezg.micredito.home.data.mappers.SolicitudMapper
import com.github.cwramirezg.micredito.home.data.remote.datasource.CreditoRemoteDataSource
import com.github.cwramirezg.micredito.home.data.remote.dto.SolicitudCreditoRequestDto
import com.github.cwramirezg.micredito.home.domain.entities.Cliente
import com.github.cwramirezg.micredito.home.domain.entities.Confirmacion
import com.github.cwramirezg.micredito.home.domain.entities.LineaCredito
import com.github.cwramirezg.micredito.home.domain.entities.SimulacionCredito
import com.github.cwramirezg.micredito.home.domain.entities.SolicitudCredito
import com.github.cwramirezg.micredito.home.domain.entities.SolicitudCreditoRequest
import com.github.cwramirezg.micredito.home.domain.repository.CreditoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject

class CreditoRepositoryImpl @Inject constructor(
    private val remoteDataSource: CreditoRemoteDataSource,
    private val localDataSource: CreditoLocalDataSource
) : CreditoRepository {

    override fun obtenerLineaCredito(clienteId: String): Flow<RepositoryResult<List<LineaCredito>>> =
        flow {
            Timber.d("=== Iniciando obtención de línea de crédito ===")
            emit(RepositoryResult.Loading())
            try {
                val localData = localDataSource.obtenerLineaCreditoLocal(clienteId)
                if (localData is RepositoryResult.Success) {
                    Timber.d("Datos encontrados en cache local")
                    emit(RepositoryResult.Success(localData.data.map {
                        LineaCreditoMapper.fromEntityToDomain(
                            it
                        )
                    }))
                }

                // 2. Siempre intentar obtener datos frescos de la red
                Timber.d("Obteniendo datos de la red para clienteId: $clienteId")
                val remoteData = remoteDataSource.obtenerLineaCredito(clienteId)

                when (remoteData) {
                    is RepositoryResult.Success -> {
                        Timber.d("Datos obtenidos exitosamente de la red")
                        val lineaCreditos =
                            remoteData.data.map { LineaCreditoMapper.fromDtoToDomain(it) }

                        val lineaCreditoEntity =
                            remoteData.data.map { LineaCreditoMapper.fromDtoToEntity(it) }
                        localDataSource.guardarLineaCredito(lineaCreditoEntity)

                        emit(RepositoryResult.Success(lineaCreditos))
                    }

                    is RepositoryResult.Error -> {
                        Timber.d("Error de red: ${remoteData.message}")
                        if (localData !is RepositoryResult.Success) {
                            emit(RepositoryResult.Error("No hay conexión y no se encontraron datos locales"))
                        }
                    }

                    is RepositoryResult.Loading -> {
                        // No emitir loading adicional
                    }
                }
            } catch (e: Exception) {
                Timber.e("Exception en obtenerLineaCredito: ${e.message}")
                emit(RepositoryResult.Error("Error inesperado: ${e.message}"))
            }
        }

    override suspend fun obtenerDatosCliente(clienteId: String): Flow<RepositoryResult<Cliente>> {
        return remoteDataSource.obtenerCliente(clienteId).map { result ->
            when (result) {
                is RepositoryResult.Success -> RepositoryResult.Success(
                    ClienteMapper.fromDtoToDomain(
                        result.data
                    )
                )

                is RepositoryResult.Error -> RepositoryResult.Error(result.message ?: "Unknown error")
                is RepositoryResult.Loading -> RepositoryResult.Loading()
            }
        }
    }

    override suspend fun enviarSolicitudCredito(
        solicitud: SolicitudCreditoRequest
    ): Flow<RepositoryResult<SolicitudCredito>> = flow {
        emit(RepositoryResult.Loading())

        val solicitudDto = SolicitudCreditoRequestDto(
            clienteId = solicitud.clienteId,
            lineaCreditoId = solicitud.lineaCreditoId,
            monto = solicitud.monto,
            plazo = solicitud.plazo
        )

        remoteDataSource.enviarSolicitudCredito(solicitudDto).collect { result ->
            when (result) {
                is RepositoryResult.Success -> {
                    val solicitudCredito = SolicitudMapper.fromDtoToDomain(result.data)
                    emit(RepositoryResult.Success(solicitudCredito))
                }

                is RepositoryResult.Error -> {
                    val solicitudPendiente = SolicitudPendienteEntity(
                        clienteId = solicitud.clienteId,
                        lineaCreditoId = solicitud.lineaCreditoId,
                        monto = solicitud.monto,
                        plazo = solicitud.plazo
                    )
                    localDataSource.guardarSolicitudPendiente(solicitudPendiente)
                    emit(
                        value = RepositoryResult.Error(
                            message = "Sin conexión. La solicitud se enviará automáticamente cuando haya internet.",
                            data = solicitudPendiente.id
                        )
                    )
                }

                is RepositoryResult.Loading -> emit(RepositoryResult.Loading())
            }
        }
    }

    override suspend fun obtenerSolicitudCredito(solicitudId: String): Flow<RepositoryResult<Confirmacion>> =
        flow {
            emit(RepositoryResult.Loading())
            try {
                val solicitudEntity = localDataSource.obtenerSolicitudPendiente(solicitudId)
                val confirmacion = SolicitudMapper.fromEntityToDomain(solicitudEntity)
                emit(RepositoryResult.Success(confirmacion))
            } catch (e: Exception) {
                emit(RepositoryResult.Error("Error al obtener solicitud: ${e.message}"))
            }
        }

    override suspend fun obtenerHistorialSolicitudes(
        clienteId: String
    ): Flow<RepositoryResult<List<SolicitudCredito>>> {
        return remoteDataSource.obtenerHistorialSolicitudes(clienteId).map { result ->
            when (result) {
                is RepositoryResult.Success -> {
                    val solicitudes = result.data.map { SolicitudMapper.fromDtoToDomain(it) }
                    RepositoryResult.Success(solicitudes)
                }

                is RepositoryResult.Error -> RepositoryResult.Error(result.message ?: "Unknown error")
                is RepositoryResult.Loading -> RepositoryResult.Loading()
            }
        }
    }

    override suspend fun guardarSimulacionTemporal(
        simulacion: SimulacionCredito
    ): Flow<RepositoryResult<Unit>> = flow {
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
            emit(RepositoryResult.Success(Unit))
        } catch (e: Exception) {
            emit(RepositoryResult.Error("Error al guardar simulación: ${e.message}"))
        }
    }

    override suspend fun obtenerSimulacionesGuardadas(): Flow<RepositoryResult<List<SimulacionCredito>>> =
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
                    emit(RepositoryResult.Success(simulaciones))
                }
            } catch (e: Exception) {
                emit(RepositoryResult.Error("Error al obtener simulaciones: ${e.message}"))
            }
        }

    override suspend fun reintentarSolicitudesPendientes(): Flow<RepositoryResult<List<SolicitudCredito>>> =
        flow {
            emit(RepositoryResult.Loading())

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
                                is RepositoryResult.Success -> {
                                    val solicitudCredito =
                                        SolicitudMapper.fromDtoToDomain(result.data)
                                    solicitudesEnviadas.add(solicitudCredito)
                                    localDataSource.eliminarSolicitudPendiente(solicitudPendiente)
                                }

                                is RepositoryResult.Error -> {
                                    localDataSource.actualizarIntentoSolicitud(solicitudPendiente)
                                }

                                is RepositoryResult.Loading -> { /* Ignorar */
                                }
                            }
                        }
                    }
                }

                emit(RepositoryResult.Success(solicitudesEnviadas))
            } catch (e: Exception) {
                emit(RepositoryResult.Error("Error al reintentar solicitudes: ${e.message}"))
            }
        }
}
