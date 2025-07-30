package com.github.cwramirezg.micredito.home.domain.usecase

import com.github.cwramirezg.micredito.core.data.repository.RepositoryResult
import com.github.cwramirezg.micredito.core.domain.usecase.BaseUseCase
import com.github.cwramirezg.micredito.home.domain.entities.Cliente
import com.github.cwramirezg.micredito.home.domain.entities.TipoCliente
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ObtenerClienteUseCase @Inject constructor(

) : BaseUseCase<String, Cliente>() {
    override suspend fun execute(parameters: String): Flow<RepositoryResult<Cliente>> = flow {
        emit(RepositoryResult.Loading())
        val cliente = Cliente(
            id = "c123",
            nombres = "Juan Carlos",
            apellidos = "Pérez López",
            dni = "12345678",
            telefono = "987654321",
            email = "juan.perez@email.com",
            tipoCliente = TipoCliente.RECURRENTE,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        emit(RepositoryResult.Success(cliente))
    }

}