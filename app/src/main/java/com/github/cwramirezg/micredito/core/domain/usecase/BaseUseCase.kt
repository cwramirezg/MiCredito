package com.github.cwramirezg.micredito.core.domain.usecase

import com.github.cwramirezg.micredito.core.data.network.NetworkResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn

abstract class BaseUseCase<in P, R>(
    private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend operator fun invoke(parameters: P): Flow<NetworkResult<R>> =
        execute(parameters).flowOn(coroutineDispatcher)

    protected abstract suspend fun execute(parameters: P): Flow<NetworkResult<R>>
}

abstract class BaseUseCaseNoParams<R>(
    private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend operator fun invoke(): Flow<NetworkResult<R>> =
        execute().flowOn(coroutineDispatcher)

    protected abstract suspend fun execute(): Flow<NetworkResult<R>>
}
