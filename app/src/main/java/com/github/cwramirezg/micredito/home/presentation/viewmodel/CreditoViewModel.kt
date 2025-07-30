package com.github.cwramirezg.micredito.home.presentation.viewmodel

import com.github.cwramirezg.micredito.core.data.repository.RepositoryResult
import com.github.cwramirezg.micredito.core.presentation.base.BaseViewModel
import com.github.cwramirezg.micredito.core.presentation.states.UiState
import com.github.cwramirezg.micredito.home.domain.usecase.ObtenerClienteUseCase
import com.github.cwramirezg.micredito.home.domain.usecase.ObtenerLineaCreditoUseCase
import com.github.cwramirezg.micredito.home.presentation.pojos.CreditoSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class CreditoViewModel @Inject constructor(
    private val obtenerLineaCreditoUseCase: ObtenerLineaCreditoUseCase,
    private val obtenerDatosClienteUseCase: ObtenerClienteUseCase
) : BaseViewModel() {

    private val _creditoUiState = MutableStateFlow<UiState<CreditoSuccess>>(UiState.Idle)
    val creditoUiState: StateFlow<UiState<CreditoSuccess>> = _creditoUiState.asStateFlow()

    private var creditoSuccess: CreditoSuccess = CreditoSuccess()

    fun cargarDatosCredito() {
        Timber.d("Iniciando carga de datos")
        launchSafe {
            _creditoUiState.value = UiState.Loading
            obtenerDatosClienteUseCase("")
                .flatMapLatest { clienteResult ->
                    when (clienteResult) {
                        is RepositoryResult.Error -> {
                            flowOf(RepositoryResult.Error(clienteResult.message))
                        }

                        is RepositoryResult.Loading -> {
                            flowOf(RepositoryResult.Loading())
                        }

                        is RepositoryResult.Success -> {
                            val cliente = clienteResult.data
                            creditoSuccess = creditoSuccess.copy(cliente = cliente)
                            obtenerLineaCreditoUseCase(cliente.id)
                        }
                    }
                }
                .collect { lineaCreditoResult ->
                    Timber.d("Resultado recibido: $lineaCreditoResult")
                    when (lineaCreditoResult) {
                        is RepositoryResult.Success -> {
                            val lineaCreditos = lineaCreditoResult.data
                            creditoSuccess = creditoSuccess.copy(lineaCreditos = lineaCreditos)
                            _creditoUiState.value = UiState.Success(creditoSuccess)
                            Timber.d("Estado actualizado a Success")
                        }

                        is RepositoryResult.Error -> {
                            Timber.e("Error: ${lineaCreditoResult.message}")
                            _creditoUiState.value = UiState.Error(
                                message = lineaCreditoResult.message
                            )
                        }

                        is RepositoryResult.Loading -> {
                            if (_creditoUiState.value !is UiState.Success) {
                                _creditoUiState.value = UiState.Loading
                            }
                        }
                    }
                }
        }
    }

    fun reintentar() {
        cargarDatosCredito()
    }

    override fun onError(message: String) {
        _creditoUiState.value = UiState.Error(message = message)
    }

}
