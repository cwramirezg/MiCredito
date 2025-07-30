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
) : BaseUseCase<String, List<LineaCredito>>() {

    override suspend fun execute(clienteId: String): Flow<NetworkResult<List<LineaCredito>>> {
        return repository.obtenerLineaCredito(clienteId).map { result ->
            when (result) {
                is NetworkResult.Success -> {
                    val lineaCreditos = result.data
                    val validas = lineaCreditos.filter { it.esValida() }
                    if (validas.isNotEmpty()) {
                        NetworkResult.Success(validas)
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
