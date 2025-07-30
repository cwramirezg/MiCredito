package com.github.cwramirezg.micredito.home.presentation.viewmodel

import com.github.cwramirezg.micredito.core.data.network.NetworkResult
import com.github.cwramirezg.micredito.core.presentation.base.BaseViewModel
import com.github.cwramirezg.micredito.home.domain.entities.Cliente
import com.github.cwramirezg.micredito.home.domain.entities.TipoCliente
import com.github.cwramirezg.micredito.home.domain.usecase.ObtenerLineaCreditoUseCase
import com.github.cwramirezg.micredito.home.presentation.states.CreditoUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class CreditoViewModel @Inject constructor(
    private val obtenerLineaCreditoUseCase: ObtenerLineaCreditoUseCase,
) : BaseViewModel() {

    private val _creditoUiState = MutableStateFlow<CreditoUiState>(CreditoUiState.Idle)
    val creditoUiState: StateFlow<CreditoUiState> = _creditoUiState.asStateFlow()


    private val cliente = Cliente(
        id = "c123",
        nombres = "Juan Carlos",
        apellidos = "Pérez López",
        dni = "12345678",
        telefono = "987654321",
        email = "juan.perez@email.com",
        tipoCliente = TipoCliente.RECURRENTE,
        createdAt = System.currentTimeMillis(),
        updatedAt = System.currentTimeMillis()
    )

    init {
        inicializarDatos()
    }

    private fun inicializarDatos() {
        cargarDatosCredito()
    }

    fun cargarDatosCredito() {
        Timber.d("Iniciando carga de datos")
        launchSafe {
            _creditoUiState.value = CreditoUiState.Loading

            obtenerLineaCreditoUseCase(cliente.id).collect { result ->
                Timber.d("Resultado recibido: $result")
                when (result) {
                    is NetworkResult.Success -> {
                        val lineaCreditos = result.data
                        _creditoUiState.value = CreditoUiState.Success(cliente, lineaCreditos)
                        Timber.d("Estado actualizado a Success")
                    }

                    is NetworkResult.Error -> {
                        Timber.e("Error: ${result.message}")
                        _creditoUiState.value = CreditoUiState.Error(
                            message = result.message,
                            canRetry = true
                        )
                    }

                    is NetworkResult.Loading -> {
                        if (_creditoUiState.value !is CreditoUiState.Success) {
                            _creditoUiState.value = CreditoUiState.Loading
                        }
                    }
                }
            }
        }
    }

    fun reintentar() {
        cargarDatosCredito()
    }

}
