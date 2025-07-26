package com.github.cwramirezg.micredito.home.data.mappers

import com.github.cwramirezg.micredito.home.data.local.entities.LineaCreditoEntity
import com.github.cwramirezg.micredito.home.data.remote.dto.LineaCreditoDto
import com.github.cwramirezg.micredito.home.domain.entities.EstadoLineaCredito
import com.github.cwramirezg.micredito.home.domain.entities.LineaCredito
import java.text.SimpleDateFormat
import java.util.Locale

object LineaCreditoMapper {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())

    fun fromDtoToDomain(dto: LineaCreditoDto): LineaCredito {
        return LineaCredito(
            id = dto.id,
            clienteId = dto.clienteId,
            montoMaximo = dto.montoMaximo,
            montoMinimo = dto.montoMinimo,
            tasaInteres = dto.tasaInteres,
            plazoMaximo = dto.plazoMaximo,
            plazoMinimo = dto.plazoMinimo,
            estado = mapEstadoFromString(dto.estado),
            fechaVencimiento = parseDateToTimestamp(dto.fechaVencimiento),
            createdAt = parseDateToTimestamp(dto.createdAt),
            updatedAt = parseDateToTimestamp(dto.updatedAt)
        )
    }

    fun fromDtoToEntity(dto: LineaCreditoDto): LineaCreditoEntity {
        return LineaCreditoEntity(
            id = dto.id,
            clienteId = dto.clienteId,
            montoMaximo = dto.montoMaximo,
            montoMinimo = dto.montoMinimo,
            tasaInteres = dto.tasaInteres,
            plazoMaximo = dto.plazoMaximo,
            plazoMinimo = dto.plazoMinimo,
            estado = dto.estado,
            fechaVencimiento = parseDateToTimestamp(dto.fechaVencimiento),
            createdAt = parseDateToTimestamp(dto.createdAt),
            updatedAt = parseDateToTimestamp(dto.updatedAt)
        )
    }

    fun fromEntityToDomain(entity: LineaCreditoEntity): LineaCredito {
        return LineaCredito(
            id = entity.id,
            clienteId = entity.clienteId,
            montoMaximo = entity.montoMaximo,
            montoMinimo = entity.montoMinimo,
            tasaInteres = entity.tasaInteres,
            plazoMaximo = entity.plazoMaximo,
            plazoMinimo = entity.plazoMinimo,
            estado = mapEstadoFromString(entity.estado),
            fechaVencimiento = entity.fechaVencimiento,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }

    private fun mapEstadoFromString(estado: String): EstadoLineaCredito {
        return when (estado.uppercase()) {
            "ACTIVA" -> EstadoLineaCredito.ACTIVA
            "SUSPENDIDA" -> EstadoLineaCredito.SUSPENDIDA
            "VENCIDA" -> EstadoLineaCredito.VENCIDA
            "CANCELADA" -> EstadoLineaCredito.CANCELADA
            else -> EstadoLineaCredito.SUSPENDIDA
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
