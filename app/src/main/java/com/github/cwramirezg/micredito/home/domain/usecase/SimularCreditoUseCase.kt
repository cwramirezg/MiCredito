package com.github.cwramirezg.micredito.home.domain.usecase

import com.github.cwramirezg.micredito.core.data.repository.RepositoryResult
import com.github.cwramirezg.micredito.core.domain.usecase.BaseUseCase
import com.github.cwramirezg.micredito.home.domain.entities.LineaCredito
import com.github.cwramirezg.micredito.home.domain.entities.SimulacionCredito
import com.github.cwramirezg.micredito.home.domain.repository.CreditoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

data class SimularCreditoParams(
    val lineaCredito: LineaCredito,
    val monto: Double,
    val plazo: Int
)

class SimularCreditoUseCase @Inject constructor(
    private val repository: CreditoRepository
) : BaseUseCase<SimularCreditoParams, SimulacionCredito>() {

    override suspend fun execute(
        parameters: SimularCreditoParams
    ): Flow<RepositoryResult<SimulacionCredito>> = flow {
        emit(RepositoryResult.Loading())

        try {
            val (lineaCredito, monto, plazo) = parameters

            // Validaciones de negocio
            val validationResult = validarParametros(lineaCredito, monto, plazo)
            if (validationResult != null) {
                emit(RepositoryResult.Error(validationResult))
                return@flow
            }

            // Cálculo de la simulación
            val simulacion = SimulacionCredito.calcular(
                monto = monto,
                plazo = plazo,
                tasaAnual = lineaCredito.tasaInteres
            )

            // Guardar simulación temporalmente
            repository.guardarSimulacionTemporal(simulacion).collect { saveResult ->
                // No bloqueamos si falla el guardado local
                if (saveResult is RepositoryResult.Error) {
                    // Log del error pero continuamos
                }
            }

            emit(RepositoryResult.Success(simulacion))

        } catch (e: Exception) {
            emit(RepositoryResult.Error("Error al calcular la simulación: ${e.message}"))
        }
    }

    private fun validarParametros(
        lineaCredito: LineaCredito,
        monto: Double,
        plazo: Int
    ): String? {
        return when {
            !lineaCredito.esValida() -> "La línea de crédito no está activa"
            !lineaCredito.montoDisponible(monto) ->
                "El monto debe estar entre ${lineaCredito.montoMinimo} y ${lineaCredito.montoMaximo}"

            !lineaCredito.plazoValido(plazo) ->
                "El plazo debe estar entre ${lineaCredito.plazoMinimo} y ${lineaCredito.plazoMaximo} meses"

            monto <= 0 -> "El monto debe ser mayor a cero"
            plazo <= 0 -> "El plazo debe ser mayor a cero"
            else -> null
        }
    }
}
