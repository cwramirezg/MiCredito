package com.github.cwramirezg.micredito.core.domain.usecase

import com.github.cwramirezg.micredito.core.data.repository.RepositoryResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn

abstract class BaseUseCase<in P, R>(
    private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend operator fun invoke(parameters: P): Flow<RepositoryResult<R>> =
        execute(parameters).flowOn(coroutineDispatcher)

    protected abstract suspend fun execute(parameters: P): Flow<RepositoryResult<R>>
}

abstract class BaseUseCaseNoParams<R>(
    private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend operator fun invoke(): Flow<RepositoryResult<R>> =
        execute().flowOn(coroutineDispatcher)

    protected abstract suspend fun execute(): Flow<RepositoryResult<R>>
}
