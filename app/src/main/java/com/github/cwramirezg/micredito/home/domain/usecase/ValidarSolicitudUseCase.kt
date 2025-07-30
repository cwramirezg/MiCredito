package com.github.cwramirezg.micredito.home.domain.usecase

import com.github.cwramirezg.micredito.core.data.repository.RepositoryResult
import com.github.cwramirezg.micredito.core.domain.usecase.BaseUseCase
import com.github.cwramirezg.micredito.home.domain.entities.LineaCredito
import com.github.cwramirezg.micredito.home.domain.entities.SolicitudCreditoRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

data class ValidacionSolicitud(
    val esValida: Boolean,
    val errores: List<String>
)

class ValidarSolicitudUseCase @Inject constructor() :
    BaseUseCase<Pair<SolicitudCreditoRequest, LineaCredito>, ValidacionSolicitud>() {

    override suspend fun execute(
        parameters: Pair<SolicitudCreditoRequest, LineaCredito>
    ): Flow<RepositoryResult<ValidacionSolicitud>> = flow {
        emit(RepositoryResult.Loading())

        val (solicitud, lineaCredito) = parameters
        val errores = mutableListOf<String>()

        // Validaciones de negocio
        if (!lineaCredito.esValida()) {
            errores.add("La línea de crédito no está disponible")
        }

        if (!lineaCredito.montoDisponible(solicitud.monto)) {
            errores.add("El monto solicitado excede los límites permitidos")
        }

        if (!lineaCredito.plazoValido(solicitud.plazo)) {
            errores.add("El plazo seleccionado no es válido")
        }

        if (solicitud.clienteId.isBlank()) {
            errores.add("ID de cliente requerido")
        }

        if (solicitud.lineaCreditoId.isBlank()) {
            errores.add("ID de línea de crédito requerido")
        }

        val validacion = ValidacionSolicitud(
            esValida = errores.isEmpty(),
            errores = errores
        )

        emit(RepositoryResult.Success(validacion))
    }
}
