package com.github.cwramirezg.micredito.home.domain.usecase

import com.github.cwramirezg.micredito.core.data.network.NetworkResult
import com.github.cwramirezg.micredito.core.domain.usecase.BaseUseCase
import com.github.cwramirezg.micredito.home.domain.entities.LineaCredito
import com.github.cwramirezg.micredito.home.domain.repository.CreditoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ObtenerLineaCreditoUseCase @Inject constructor(
    private val repository: CreditoRepository
) : BaseUseCase<String, LineaCredito>() {

    override suspend fun execute(parameters: String): Flow<NetworkResult<LineaCredito>> {
        return repository.obtenerLineaCredito(parameters).map { result ->
            when (result) {
                is NetworkResult.Success -> {
                    val lineaCredito = result.data
                    if (lineaCredito.esValida()) {
                        NetworkResult.Success(lineaCredito)
                    } else {
                        NetworkResult.Error("La línea de crédito no está disponible")
                    }
                }

                is NetworkResult.Error -> result
                is NetworkResult.Loading -> result
            }
        }
    }
}
