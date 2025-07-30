package com.github.cwramirezg.micredito.home.domain.usecase

import com.github.cwramirezg.micredito.core.data.repository.RepositoryResult
import com.github.cwramirezg.micredito.core.domain.usecase.BaseUseCase
import com.github.cwramirezg.micredito.home.domain.entities.SolicitudCredito
import com.github.cwramirezg.micredito.home.domain.entities.SolicitudCreditoRequest
import com.github.cwramirezg.micredito.home.domain.repository.CreditoRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class EnviarSolicitudUseCase @Inject constructor(
    private val repository: CreditoRepository
) : BaseUseCase<SolicitudCreditoRequest, SolicitudCredito>() {

    override suspend fun execute(
        parameters: SolicitudCreditoRequest
    ): Flow<RepositoryResult<SolicitudCredito>> {
        return repository.enviarSolicitudCredito(parameters)
    }
}
