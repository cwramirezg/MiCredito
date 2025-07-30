package com.github.cwramirezg.micredito.home.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import com.github.cwramirezg.micredito.core.data.network.NetworkResult
import com.github.cwramirezg.micredito.core.presentation.base.BaseViewModel
import com.github.cwramirezg.micredito.core.presentation.states.UiState
import com.github.cwramirezg.micredito.home.domain.entities.toConfirmacionSuccess
import com.github.cwramirezg.micredito.home.domain.usecase.ObtenerSolicitudUseCase
import com.github.cwramirezg.micredito.home.presentation.pojos.ConfirmacionSuccess
import com.github.cwramirezg.micredito.navigation.NavigationDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ConfirmacionViewModel @Inject constructor(
    private val obtenerSolicitudUseCase: ObtenerSolicitudUseCase,
    saveStateHandle: SavedStateHandle
) : BaseViewModel() {
    private val _confirmacionUiState = MutableStateFlow<UiState<ConfirmacionSuccess>>(UiState.Idle)
    val confirmacionUiState: StateFlow<UiState<ConfirmacionSuccess>> =
        _confirmacionUiState.asStateFlow()

    val confirmacion = saveStateHandle.toRoute<NavigationDestination.Confirmacion>()

    fun obtenerSolicitud() {
        launchSafe {
            _confirmacionUiState.value = UiState.Loading
            obtenerSolicitudUseCase(confirmacion.solicitudId).collect { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        val confirmacion = result.data
                        _confirmacionUiState.value = UiState.Success(
                            data = confirmacion.toConfirmacionSuccess()
                        )
                    }

                    is NetworkResult.Error -> {
                        _confirmacionUiState.value = UiState.Error(result.message)
                    }

                    is NetworkResult.Loading -> {
                        _confirmacionUiState.value = UiState.Loading
                    }
                }
            }
        }
    }
}