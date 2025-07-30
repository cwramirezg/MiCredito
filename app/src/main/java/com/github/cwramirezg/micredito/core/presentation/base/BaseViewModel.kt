package com.github.cwramirezg.micredito.core.presentation.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

abstract class BaseViewModel : ViewModel() {

    protected val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        handleException(exception)
    }

    protected open fun handleException(exception: Throwable) {
        onError(exception.message ?: "Error desconocido")
    }

    protected abstract fun onError(message: String)

    protected fun launchSafe(block: suspend () -> Unit) {
        viewModelScope.launch(exceptionHandler) {
            block()
        }
    }
}
