package com.github.cwramirezg.micredito.core.data.local

sealed class LocalResult<T> {
    class Loading<T> : LocalResult<T>()
    data class Success<T>(val data: T) : LocalResult<T>()
    data class Error<T>(
        val message: String,
        val data: String? = null,
        val exception: Throwable? = null
    ) : LocalResult<T>()
}