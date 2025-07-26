package com.github.cwramirezg.micredito.core.presentation.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

abstract class BaseViewModel : ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    protected val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        handleError(exception.message ?: "Error desconocido")
    }

    protected fun setLoading(loading: Boolean) {
        _isLoading.value = loading
    }

    protected fun handleError(message: String) {
        _error.value = message
        _isLoading.value = false
    }

    protected fun clearError() {
        _error.value = null
    }

    protected fun launchSafe(block: suspend () -> Unit) {
        viewModelScope.launch(exceptionHandler) {
            try {
                setLoading(true)
                block()
            } finally {
                setLoading(false)
            }
        }
    }
}
