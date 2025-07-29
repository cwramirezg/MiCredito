package com.github.cwramirezg.micredito.home.presentation.viewmodel

import com.github.cwramirezg.micredito.core.data.network.NetworkResult
import com.github.cwramirezg.micredito.core.presentation.base.BaseViewModel
import com.github.cwramirezg.micredito.home.domain.entities.Cliente
import com.github.cwramirezg.micredito.home.domain.entities.LineaCredito
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


    private val _clienteActual = MutableStateFlow<Cliente?>(null)
    private val _lineaCreditoActual = MutableStateFlow<LineaCredito?>(null)


    init {
        inicializarDatos()
    }

    private fun inicializarDatos() {
        cargarDatosCredito("c123")
    }

    fun cargarDatosCredito(clienteId: String) {
        Timber.d("Iniciando carga de datos para: $clienteId")
        launchSafe {
            _creditoUiState.value = CreditoUiState.Loading

            obtenerLineaCreditoUseCase(clienteId).collect { result ->
                Timber.d("Resultado recibido: $result")
                when (result) {
                    is NetworkResult.Success -> {
                        val lineaCredito = result.data
                        _lineaCreditoActual.value = lineaCredito
                        val cliente = Cliente(
                            id = clienteId,
                            nombres = "Juan Carlos",
                            apellidos = "Pérez López",
                            dni = "12345678",
                            telefono = "987654321",
                            email = "juan.perez@email.com",
                            tipoCliente = TipoCliente.RECURRENTE,
                            createdAt = System.currentTimeMillis(),
                            updatedAt = System.currentTimeMillis()
                        )
                        _clienteActual.value = cliente
                        _creditoUiState.value = CreditoUiState.Success(cliente, lineaCredito)
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
        val clienteId = _clienteActual.value?.id ?: "c123"
        cargarDatosCredito(clienteId)
    }

}
