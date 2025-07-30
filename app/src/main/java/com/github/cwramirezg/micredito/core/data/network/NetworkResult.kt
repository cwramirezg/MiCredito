package com.github.cwramirezg.micredito.core.data.network

sealed class NetworkResult<T> {
    class Loading<T> : NetworkResult<T>()
    data class Success<T>(val data: T) : NetworkResult<T>()
    data class Error<T>(
        val message: String,
        val data: String? = null,
        val code: Int? = null,
        val exception: Throwable? = null
    ) : NetworkResult<T>()
}

inline fun <T> NetworkResult<T>.onLoading(action: () -> Unit): NetworkResult<T> {
    if (this is NetworkResult.Loading) action()
    return this
}

inline fun <T> NetworkResult<T>.onSuccess(action: (value: T) -> Unit): NetworkResult<T> {
    if (this is NetworkResult.Success) action(data)
    return this
}

inline fun <T> NetworkResult<T>.onError(action: (message: String) -> Unit): NetworkResult<T> {
    if (this is NetworkResult.Error) action(message)
    return this
}
