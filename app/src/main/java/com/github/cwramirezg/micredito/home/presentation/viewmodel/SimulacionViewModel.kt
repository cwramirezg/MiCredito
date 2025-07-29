package com.github.cwramirezg.micredito.home.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.github.cwramirezg.micredito.core.data.network.NetworkResult
import com.github.cwramirezg.micredito.core.presentation.base.BaseViewModel
import com.github.cwramirezg.micredito.core.presentation.states.UiState
import com.github.cwramirezg.micredito.home.domain.entities.Cliente
import com.github.cwramirezg.micredito.home.domain.entities.EstadoLineaCredito
import com.github.cwramirezg.micredito.home.domain.entities.LineaCredito
import com.github.cwramirezg.micredito.home.domain.entities.SolicitudCreditoRequest
import com.github.cwramirezg.micredito.home.domain.entities.TipoCliente
import com.github.cwramirezg.micredito.home.domain.usecase.EnviarSolicitudUseCase
import com.github.cwramirezg.micredito.home.domain.usecase.SimularCreditoParams
import com.github.cwramirezg.micredito.home.domain.usecase.SimularCreditoUseCase
import com.github.cwramirezg.micredito.home.domain.usecase.ValidarSolicitudUseCase
import com.github.cwramirezg.micredito.home.presentation.pojos.SimulacionSuccess
import com.github.cwramirezg.micredito.home.presentation.states.SolicitudUiState
import com.github.cwramirezg.micredito.navigation.NavigationDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import javax.inject.Inject
import kotlin.concurrent.timer

@HiltViewModel
class SimulacionViewModel @Inject constructor(
    private val simularCreditoUseCase: SimularCreditoUseCase,
    private val enviarSolicitudUseCase: EnviarSolicitudUseCase,
    private val validarSolicitudUseCase: ValidarSolicitudUseCase,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel() {

    private val _simulacionUiState = MutableStateFlow<UiState<SimulacionSuccess>>(UiState.Idle)
    val simulacionUiState: StateFlow<UiState<SimulacionSuccess>> = _simulacionUiState.asStateFlow()

    private val _solicitudUiState = MutableStateFlow<SolicitudUiState>(SolicitudUiState.Idle)
    val solicitudUiState: StateFlow<SolicitudUiState> = _solicitudUiState.asStateFlow()

    private val _montoSeleccionado = MutableStateFlow(0.0)
    val montoSeleccionado: StateFlow<Double> = _montoSeleccionado.asStateFlow()

    private val _plazoSeleccionado = MutableStateFlow(12)
    val plazoSeleccionado: StateFlow<Int> = _plazoSeleccionado.asStateFlow()

    private val simulacion = savedStateHandle.toRoute<NavigationDestination.Simulacion>()

    val lineaCredito = LineaCredito(
        "id123",
        1756608068000,
        1756608068000,
        "c123",
        10000.0,
        1000.0,
        17.0,
        60,
        12,
        EstadoLineaCredito.ACTIVA,
        1756608068000
    )

    val cliente = Cliente(
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
        Timber.d("idLineaCredito: ${simulacion.idLineaCredito}")
        _montoSeleccionado.value = lineaCredito.montoMinimo
        configurarSimulacionReactiva()
    }

    private fun configurarSimulacionReactiva() {
        combine(
            _montoSeleccionado,
            _plazoSeleccionado
        ) { monto, plazo ->
            Triple(monto, plazo, lineaCredito)
        }
            .debounce(500)
            .filter { (monto, _, _) ->
                monto > 0
            }
            .onEach { (monto, plazo, lineaCredito) ->
                simularCreditoAutomatico(monto, plazo, lineaCredito)
            }
            .launchIn(viewModelScope)
    }

    private fun simularCreditoAutomatico(monto: Double, plazo: Int, lineaCredito: LineaCredito) {
        launchSafe {
            _simulacionUiState.value = UiState.Loading

            val params = SimularCreditoParams(lineaCredito, monto, plazo)

            simularCreditoUseCase(params).collect { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        val simulacion = result.data
                        _simulacionUiState.value = UiState.Success(
                            data = SimulacionSuccess(
                                monto = simulacion.monto,
                                plazo = simulacion.plazo,
                                cuotaMensual = simulacion.cuotaMensual,
                                interesTotal = simulacion.interesTotal,
                                montoTotal = simulacion.montoTotal
                            )
                        )
                    }

                    is NetworkResult.Error -> {
                        _simulacionUiState.value = UiState.Error(result.message)
                    }

                    is NetworkResult.Loading -> {
                        _simulacionUiState.value = UiState.Loading
                    }
                }
            }
        }
    }

    fun actualizarMonto(nuevoMonto: Double) {
        val montoValidado = nuevoMonto.coerceIn(
            lineaCredito.montoMinimo,
            lineaCredito.montoMaximo
        )
        _montoSeleccionado.value = montoValidado
    }

    fun actualizarPlazo(nuevoPlazo: Int) {
        val plazoValidado = nuevoPlazo.coerceIn(
            lineaCredito.plazoMinimo,
            lineaCredito.plazoMaximo
        )
        _plazoSeleccionado.value = plazoValidado
    }

    fun solicitarCredito() {
        val cliente = cliente
        val lineaCredito = lineaCredito
        val monto = _montoSeleccionado.value
        val plazo = _plazoSeleccionado.value

        if (cliente == null || lineaCredito == null) {
            _solicitudUiState.value = SolicitudUiState.Error("Datos incompletos")
            return
        }

        launchSafe {
            _solicitudUiState.value = SolicitudUiState.Loading

            // Validar solicitud primero
            val validacionParams = Pair(
                SolicitudCreditoRequest(cliente.id, lineaCredito.id, monto, plazo),
                lineaCredito
            )

            validarSolicitudUseCase(validacionParams).collect { validacionResult ->
                when (validacionResult) {
                    is NetworkResult.Success -> {
                        val validacion = validacionResult.data
                        if (validacion.esValida) {
                            enviarSolicitudCredito(cliente.id, lineaCredito.id, monto, plazo)
                        } else {
                            val erroresTexto = validacion.errores.joinToString("\n")
                            _solicitudUiState.value = SolicitudUiState.Error(erroresTexto)
                        }
                    }

                    is NetworkResult.Error -> {
                        _solicitudUiState.value = SolicitudUiState.Error(validacionResult.message)
                    }

                    else -> { /* Loading */
                    }
                }
            }
        }
    }

    private suspend fun enviarSolicitudCredito(
        clienteId: String,
        lineaCreditoId: String,
        monto: Double,
        plazo: Int
    ) {
        val solicitud = SolicitudCreditoRequest(clienteId, lineaCreditoId, monto, plazo)

        enviarSolicitudUseCase(solicitud).collect { result ->
            when (result) {
                is NetworkResult.Success -> {
                    _solicitudUiState.value = SolicitudUiState.Success(
                        mensaje = "¡Solicitud enviada exitosamente! Te contactaremos pronto.",
                        solicitudId = result.data.id
                    )
                }

                is NetworkResult.Error -> {
                    val isOffline = result.message.contains("Sin conexión", ignoreCase = true)
                    _solicitudUiState.value = SolicitudUiState.Error(
                        message = result.message,
                        isOffline = isOffline
                    )
                }

                is NetworkResult.Loading -> {
                    _solicitudUiState.value = SolicitudUiState.Loading
                }
            }
        }
    }
}
