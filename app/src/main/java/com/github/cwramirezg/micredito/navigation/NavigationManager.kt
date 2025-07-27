package com.github.cwramirezg.micredito.navigation

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NavigationManager @Inject constructor() {

    private val _navigationEvent = MutableSharedFlow<NavigationEvent>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val navigationEvent: SharedFlow<NavigationEvent> = _navigationEvent.asSharedFlow()

    fun navigateTo(destination: NavigationDestination) {
        Timber.d("Intentando navegar a: $destination")
        val success = _navigationEvent.tryEmit(NavigationEvent.Navigate(destination))
        Timber.d("Evento emitido exitosamente: $success")

        Timber.d("Número de suscriptores: ${_navigationEvent.subscriptionCount.value}")
    }

    fun navigateBack() {
        Timber.d("Intentando navegar hacia atrás")
        val success = _navigationEvent.tryEmit(NavigationEvent.NavigateBack)
        Timber.d("Evento NavigateBack emitido: $success")
    }

    fun clearBackStack() {
        Timber.d("Intentando limpiar back stack")
        val success = _navigationEvent.tryEmit(NavigationEvent.ClearBackStack)
        Timber.d("Evento ClearBackStack emitido: $success")
    }
}

sealed class NavigationEvent {
    data class Navigate(val destination: NavigationDestination) : NavigationEvent()
    object NavigateBack : NavigationEvent()
    object ClearBackStack : NavigationEvent()
}