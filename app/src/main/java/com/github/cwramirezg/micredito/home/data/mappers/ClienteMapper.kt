package com.github.cwramirezg.micredito.home.data.mappers

import com.github.cwramirezg.micredito.home.data.remote.dto.ClienteDto
import com.github.cwramirezg.micredito.home.domain.entities.Cliente
import com.github.cwramirezg.micredito.home.domain.entities.TipoCliente
import java.text.SimpleDateFormat
import java.util.Locale

object ClienteMapper {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())

    fun fromDtoToDomain(dto: ClienteDto): Cliente {
        return Cliente(
            id = dto.id,
            nombres = dto.nombres,
            apellidos = dto.apellidos,
            dni = dto.dni,
            telefono = dto.telefono,
            email = dto.email,
            tipoCliente = mapTipoClienteFromString(dto.tipoCliente),
            createdAt = parseDateToTimestamp(dto.createdAt),
            updatedAt = parseDateToTimestamp(dto.updatedAt)
        )
    }

    private fun mapTipoClienteFromString(tipo: String): TipoCliente {
        return when (tipo.uppercase()) {
            "NUEVO" -> TipoCliente.NUEVO
            "RECURRENTE" -> TipoCliente.RECURRENTE
            "PREMIUM" -> TipoCliente.PREMIUM
            else -> TipoCliente.NUEVO
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
