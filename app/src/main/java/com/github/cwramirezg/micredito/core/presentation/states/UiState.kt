package com.github.cwramirezg.micredito.core.presentation.states

sealed class UiState<out T> {
    object Idle : UiState<Nothing>()
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(
        val message: String,
        val isOffline: Boolean = false,
        val idSolicitud: String = "",
        val throwable: Throwable? = null
    ) : UiState<Nothing>()
}

inline fun <T> UiState<T>.onSuccess(action: (value: T) -> Unit): UiState<T> {
    if (this is UiState.Success) action(data)
    return this
}

inline fun <T> UiState<T>.onError(action: (message: String) -> Unit): UiState<T> {
    if (this is UiState.Error) action(message)
    return this
}

inline fun <T> UiState<T>.onLoading(action: () -> Unit): UiState<T> {
    if (this is UiState.Loading) action()
    return this
}
