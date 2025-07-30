package com.github.cwramirezg.micredito.core.data.repository

sealed class RepositoryResult<T> {
    class Loading<T> : RepositoryResult<T>()
    data class Success<T>(val data: T) : RepositoryResult<T>()
    data class Error<T>(
        val message: String,
        val data: String? = null,
        val exception: Throwable? = null
    ) : RepositoryResult<T>()
}

inline fun <T> RepositoryResult<T>.onLoading(action: () -> Unit): RepositoryResult<T> {
    if (this is RepositoryResult.Loading) action()
    return this
}

inline fun <T> RepositoryResult<T>.onSuccess(action: (value: T) -> Unit): RepositoryResult<T> {
    if (this is RepositoryResult.Success) action(data)
    return this
}

inline fun <T> RepositoryResult<T>.onError(action: (message: String) -> Unit): RepositoryResult<T> {
    if (this is RepositoryResult.Error) action(message)
    return this
}
