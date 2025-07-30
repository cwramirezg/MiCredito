package com.github.cwramirezg.micredito.home.domain.usecase

import com.github.cwramirezg.micredito.core.data.repository.RepositoryResult
import com.github.cwramirezg.micredito.core.domain.usecase.BaseUseCase
import com.github.cwramirezg.micredito.home.domain.entities.Confirmacion
import com.github.cwramirezg.micredito.home.domain.repository.CreditoRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObtenerSolicitudUseCase @Inject constructor(
    private val repository: CreditoRepository
) : BaseUseCase<String, Confirmacion>() {
    override suspend fun execute(parameters: String): Flow<RepositoryResult<Confirmacion>> {
        return repository.obtenerSolicitudCredito(parameters)
    }
}