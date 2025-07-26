package com.github.cwramirezg.micredito.home.data.mappers

import com.github.cwramirezg.micredito.home.data.remote.dto.SolicitudCreditoDto
import com.github.cwramirezg.micredito.home.domain.entities.EstadoSolicitud
import com.github.cwramirezg.micredito.home.domain.entities.SimulacionCredito
import com.github.cwramirezg.micredito.home.domain.entities.SolicitudCredito
import java.text.SimpleDateFormat
import java.util.Locale

object SolicitudMapper {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())

    fun fromDtoToDomain(dto: SolicitudCreditoDto): SolicitudCredito {
        val simulacion = SimulacionCredito(
            monto = dto.monto,
            plazo = dto.plazo,
            tasaInteres = dto.tasaInteres,
            cuotaMensual = dto.cuotaMensual,
            interesTotal = dto.interesTotal,
            montoTotal = dto.montoTotal,
            fechaSimulacion = parseDateToTimestamp(dto.createdAt)
        )

        return SolicitudCredito(
            id = dto.id,
            clienteId = dto.clienteId,
            lineaCreditoId = dto.lineaCreditoId,
            simulacion = simulacion,
            estado = mapEstadoFromString(dto.estado),
            observaciones = dto.observaciones,
            createdAt = parseDateToTimestamp(dto.createdAt),
            updatedAt = parseDateToTimestamp(dto.updatedAt)
        )
    }

    private fun mapEstadoFromString(estado: String): EstadoSolicitud {
        return when (estado.uppercase()) {
            "PENDIENTE" -> EstadoSolicitud.PENDIENTE
            "EN_EVALUACION" -> EstadoSolicitud.EN_EVALUACION
            "APROBADA" -> EstadoSolicitud.APROBADA
            "RECHAZADA" -> EstadoSolicitud.RECHAZADA
            "DESEMBOLSADA" -> EstadoSolicitud.DESEMBOLSADA
            else -> EstadoSolicitud.PENDIENTE
        }
    }

    private fun parseDateToTimestamp(dateString: String): Long {
        return try {
            dateFormat.parse(dateString)?.time ?: System.currentTimeMillis()
        } catch (e: Exception) {
            System.currentTimeMillis()
        }
    }
}
