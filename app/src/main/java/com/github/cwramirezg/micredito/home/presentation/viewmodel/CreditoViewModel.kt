package com.github.cwramirezg.micredito.home.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.github.cwramirezg.micredito.core.data.network.NetworkResult
import com.github.cwramirezg.micredito.core.presentation.base.BaseViewModel
import com.github.cwramirezg.micredito.home.domain.entities.Cliente
import com.github.cwramirezg.micredito.home.domain.entities.LineaCredito
import com.github.cwramirezg.micredito.home.domain.entities.SolicitudCreditoRequest
import com.github.cwramirezg.micredito.home.domain.entities.TipoCliente
import com.github.cwramirezg.micredito.home.domain.usecase.EnviarSolicitudUseCase
import com.github.cwramirezg.micredito.home.domain.usecase.ObtenerLineaCreditoUseCase
import com.github.cwramirezg.micredito.home.domain.usecase.SimularCreditoParams
import com.github.cwramirezg.micredito.home.domain.usecase.SimularCreditoUseCase
import com.github.cwramirezg.micredito.home.domain.usecase.ValidarSolicitudUseCase
import com.github.cwramirezg.micredito.home.presentation.states.CreditoUiState
import com.github.cwramirezg.micredito.home.presentation.states.SimulacionUiState
import com.github.cwramirezg.micredito.home.presentation.states.SolicitudUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
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

@OptIn(FlowPreview::class)
@HiltViewModel
class CreditoViewModel @Inject constructor(
    private val obtenerLineaCreditoUseCase: ObtenerLineaCreditoUseCase,
    private val simularCreditoUseCase: SimularCreditoUseCase,
    private val enviarSolicitudUseCase: EnviarSolicitudUseCase,
    private val validarSolicitudUseCase: ValidarSolicitudUseCase
) : BaseViewModel() {

    // Estados UI reactivos
    private val _creditoUiState = MutableStateFlow<CreditoUiState>(CreditoUiState.Idle)
    val creditoUiState: StateFlow<CreditoUiState> = _creditoUiState.asStateFlow()

    private val _simulacionUiState = MutableStateFlow<SimulacionUiState>(SimulacionUiState.Idle)
    val simulacionUiState: StateFlow<SimulacionUiState> = _simulacionUiState.asStateFlow()

    private val _solicitudUiState = MutableStateFlow<SolicitudUiState>(SolicitudUiState.Idle)
    val solicitudUiState: StateFlow<SolicitudUiState> = _solicitudUiState.asStateFlow()

    // Estados internos
    private val _clienteActual = MutableStateFlow<Cliente?>(null)
    private val _lineaCreditoActual = MutableStateFlow<LineaCredito?>(null)

    // Parámetros de simulación reactivos
    private val _montoSeleccionado = MutableStateFlow(0.0)
    val montoSeleccionado: StateFlow<Double> = _montoSeleccionado.asStateFlow()

    private val _plazoSeleccionado = MutableStateFlow(12)
    val plazoSeleccionado: StateFlow<Int> = _plazoSeleccionado.asStateFlow()

    init {
        inicializarDatos()
        configurarSimulacionReactiva()
    }

    private fun inicializarDatos() {
        cargarDatosCredito("c123")
    }

    private fun configurarSimulacionReactiva() {
        // Combinar monto y plazo para simulación automática
        combine(
            _montoSeleccionado,
            _plazoSeleccionado,
            _lineaCreditoActual
        ) { monto, plazo, lineaCredito ->
            Triple(monto, plazo, lineaCredito)
        }
            .debounce(500) // Evitar cálculos excesivos mientras el usuario ajusta
            .filter { (monto, _, lineaCredito) ->
                monto > 0 && lineaCredito != null
            }
            .onEach { (monto, plazo, lineaCredito) ->
                simularCreditoAutomatico(monto, plazo, lineaCredito!!)
            }
            .launchIn(viewModelScope)
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

                        // Crear cliente mock para demo (en producción obtener de otro UseCase)
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

                        // Configurar valores iniciales del slider
                        _montoSeleccionado.value = lineaCredito.montoMinimo

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
                            // Solo mostrar loading si no tenemos datos exitosos
                            _creditoUiState.value = CreditoUiState.Loading
                        }
                    }
                }
            }
        }
    }

    private fun simularCreditoAutomatico(monto: Double, plazo: Int, lineaCredito: LineaCredito) {
        launchSafe {
            _simulacionUiState.value = SimulacionUiState.Calculating

            val params = SimularCreditoParams(lineaCredito, monto, plazo)

            simularCreditoUseCase(params).collect { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        val simulacion = result.data
                        _simulacionUiState.value = SimulacionUiState.Success(
                            monto = simulacion.monto,
                            plazo = simulacion.plazo,
                            cuotaMensual = simulacion.cuotaMensual,
                            interesTotal = simulacion.interesTotal,
                            montoTotal = simulacion.montoTotal
                        )
                    }

                    is NetworkResult.Error -> {
                        _simulacionUiState.value = SimulacionUiState.Error(result.message)
                    }

                    is NetworkResult.Loading -> {
                        _simulacionUiState.value = SimulacionUiState.Calculating
                    }
                }
            }
        }
    }

    fun actualizarMonto(nuevoMonto: Double) {
        val lineaCredito = _lineaCreditoActual.value ?: return

        val montoValidado = nuevoMonto.coerceIn(
            lineaCredito.montoMinimo,
            lineaCredito.montoMaximo
        )

        _montoSeleccionado.value = montoValidado
    }

    fun actualizarPlazo(nuevoPlazo: Int) {
        val lineaCredito = _lineaCreditoActual.value ?: return

        val plazoValidado = nuevoPlazo.coerceIn(
            lineaCredito.plazoMinimo,
            lineaCredito.plazoMaximo
        )

        _plazoSeleccionado.value = plazoValidado
    }

    fun solicitarCredito() {
        val cliente = _clienteActual.value
        val lineaCredito = _lineaCreditoActual.value
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

    fun reintentar() {
        val clienteId = _clienteActual.value?.id ?: "user_123"
        cargarDatosCredito(clienteId)
    }

    fun limpiarEstadoSolicitud() {
        _solicitudUiState.value = SolicitudUiState.Idle
    }

    fun limpiarErrorSimulacion() {
        _simulacionUiState.value = SimulacionUiState.Idle
    }
}
