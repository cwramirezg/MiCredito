package com.github.cwramirezg.micredito.home.presentation.viewmodel

import com.github.cwramirezg.micredito.core.data.repository.RepositoryResult
import com.github.cwramirezg.micredito.core.presentation.states.UiState
import com.github.cwramirezg.micredito.home.domain.entities.SimulacionCredito
import com.github.cwramirezg.micredito.home.domain.usecase.EnviarSolicitudUseCase
import com.github.cwramirezg.micredito.home.domain.usecase.SimularCreditoUseCase
import com.github.cwramirezg.micredito.home.domain.usecase.ValidacionSolicitud
import com.github.cwramirezg.micredito.home.domain.usecase.ValidarSolicitudUseCase
import com.github.cwramirezg.micredito.navigation.NavigationDestination
import com.github.cwramirezg.micredito.navigation.utils.ArgsProvider
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SimulacionViewModelTest {

    private val simularCreditoUseCase: SimularCreditoUseCase = mockk()
    private val enviarSolicitudUseCase: EnviarSolicitudUseCase = mockk()
    private val validarSolicitudUseCase: ValidarSolicitudUseCase = mockk()
    private val argsProvider: ArgsProvider = mockk()

    private lateinit var viewModel: SimulacionViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        val mockSimulacionArgs = NavigationDestination.Simulacion(idLineaCredito = "test-lc-123")
        every { argsProvider.getSimulacionArgs() } returns mockSimulacionArgs
        viewModel = SimulacionViewModel(
            simularCreditoUseCase,
            enviarSolicitudUseCase,
            validarSolicitudUseCase,
            argsProvider
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `viewModel_seInicializaCorrectamente_conElIdDeLineaCredito`() = runTest {
        assertEquals("test-lc-123", viewModel.lineaCredito.id)
    }

    @Test
    fun `inicializarSimulacion_estableceMontoMinimoYConfiguraReactividad`() = runTest {
        val mockSimulacion = SimulacionCredito(
            monto = 1000.0,
            plazo = 12,
            tasaInteres = 12.0,
            cuotaMensual = 100.0,
            interesTotal = 200.0,
            montoTotal = 1200.0,
            fechaSimulacion = 6546465445646
        )
        coEvery { simularCreditoUseCase(any()) } returns flowOf(
            RepositoryResult.Success(mockSimulacion)
        )

        viewModel.inicializarSimulacion()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(1000.0, viewModel.montoSeleccionado.value, 0.0)
        assertEquals(12, viewModel.plazoSeleccionado.value)
    }

    @Test
    fun `actualizarMonto_conMontoValido_actualizaCorrectamente`() = runTest {
        val nuevoMonto = 5000.0
        val mockSimulacion = SimulacionCredito(
            monto = 1000.0,
            plazo = 12,
            tasaInteres = 12.0,
            cuotaMensual = 100.0,
            interesTotal = 200.0,
            montoTotal = 1200.0,
            fechaSimulacion = 6546465445646
        )
        coEvery { simularCreditoUseCase(any()) } returns flowOf(
            RepositoryResult.Success(mockSimulacion)
        )

        viewModel.actualizarMonto(nuevoMonto)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(nuevoMonto, viewModel.montoSeleccionado.value, 0.0)
    }

    @Test
    fun `actualizarMonto_conMontoMenorAlMinimo_ajustaAlMinimo`() = runTest {
        val montoMenorAlMinimo = 500.0 // Menor que el mínimo (1000.0)

        viewModel.actualizarMonto(montoMenorAlMinimo)

        assertEquals(1000.0, viewModel.montoSeleccionado.value, 0.0) // Debe ajustarse al mínimo
    }

    @Test
    fun `actualizarMonto_conMontoMayorAlMaximo_ajustaAlMaximo`() = runTest {
        val montoMayorAlMaximo = 15000.0

        viewModel.actualizarMonto(montoMayorAlMaximo)

        assertEquals(10000.0, viewModel.montoSeleccionado.value, 0.0) // Debe ajustarse al máximo
    }

    @Test
    fun `actualizarPlazo_conPlazoValido_actualizaCorrectamente`() = runTest {
        val nuevoPlazo = 24
        val mockSimulacion = SimulacionCredito(
            monto = 1000.0,
            plazo = 12,
            tasaInteres = 12.0,
            cuotaMensual = 100.0,
            interesTotal = 200.0,
            montoTotal = 1200.0,
            fechaSimulacion = 6546465445646
        )
        coEvery { simularCreditoUseCase(any()) } returns flowOf(
            RepositoryResult.Success(mockSimulacion)
        )

        viewModel.actualizarPlazo(nuevoPlazo)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(nuevoPlazo, viewModel.plazoSeleccionado.value)
    }

    @Test
    fun `actualizarPlazo_conPlazoMenorAlMinimo_ajustaAlMinimo`() = runTest {
        val plazoMenorAlMinimo = 6 // Menor que el mínimo (12)

        viewModel.actualizarPlazo(plazoMenorAlMinimo)

        assertEquals(12, viewModel.plazoSeleccionado.value) // Debe ajustarse al mínimo
    }

    @Test
    fun `actualizarPlazo_conPlazoMayorAlMaximo_ajustaAlMaximo`() = runTest {
        val plazoMayorAlMaximo = 72 // Mayor que el máximo (60)

        viewModel.actualizarPlazo(plazoMayorAlMaximo)

        assertEquals(60, viewModel.plazoSeleccionado.value) // Debe ajustarse al máximo
    }

    @Test
    fun `simulacionReactiva_cuandoCambianParametros_ejecutaSimulacion`() = runTest {
        val mockSimulacion = SimulacionCredito(
            monto = 5000.0,
            plazo = 24,
            tasaInteres = 12.0,
            cuotaMensual = 100.0,
            interesTotal = 200.0,
            montoTotal = 1200.0,
            fechaSimulacion = 6546465445646
        )
        coEvery { simularCreditoUseCase(any()) } returns flowOf(
            RepositoryResult.Success(mockSimulacion)
        )

        viewModel.inicializarSimulacion()
        viewModel.actualizarMonto(5000.0)
        viewModel.actualizarPlazo(24)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify(atLeast = 1) { simularCreditoUseCase(any()) }
        val uiState = viewModel.simulacionUiState.value
        assertTrue(uiState is UiState.Success)
        assertEquals(5000.0, (uiState as UiState.Success).data.monto, 0.0)
    }

    @Test
    fun `simulacionReactiva_cuandoSimulacionFalla_muestraError`() = runTest {
        val errorMessage = "Error de simulación"
        coEvery { simularCreditoUseCase(any()) } returns flowOf(
            RepositoryResult.Error(errorMessage)
        )

        viewModel.inicializarSimulacion()
        testDispatcher.scheduler.advanceUntilIdle()

        val uiState = viewModel.simulacionUiState.value
        assertTrue(uiState is UiState.Error)
        assertEquals(errorMessage, (uiState as UiState.Error).message)
    }

    @Test
    fun `solicitarCredito_conValidacionFallida_muestraError`() = runTest {
        val errores = listOf("Error 1", "Error 2")
        val mockValidacion = ValidacionSolicitud(esValida = false, errores = errores)

        coEvery { validarSolicitudUseCase(any()) } returns flowOf(
            RepositoryResult.Success(mockValidacion)
        )

        viewModel.solicitarCredito()
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { validarSolicitudUseCase(any()) }
        coVerify(exactly = 0) { enviarSolicitudUseCase(any()) }

        val uiState = viewModel.solicitudUiState.value
        assertTrue(uiState is UiState.Error)
        assertTrue((uiState as UiState.Error).message.contains("Error 1"))
        assertTrue(uiState.message.contains("Error 2"))
    }

    @Test
    fun `solicitarCredito_conErrorDeEnvio_muestraError`() = runTest {
        val mockValidacion = ValidacionSolicitud(esValida = true, errores = emptyList())
        val errorMessage = "Error de red"

        coEvery { validarSolicitudUseCase(any()) } returns flowOf(
            RepositoryResult.Success(mockValidacion)
        )
        coEvery { enviarSolicitudUseCase(any()) } returns flowOf(
            RepositoryResult.Error(errorMessage)
        )

        viewModel.solicitarCredito()
        testDispatcher.scheduler.advanceUntilIdle()

        val uiState = viewModel.solicitudUiState.value
        assertTrue(uiState is UiState.Error)
        assertEquals(errorMessage, (uiState as UiState.Error).message)
    }

    @Test
    fun `solicitarCredito_conErrorOffline_marcaComoOffline`() = runTest {
        val mockValidacion = ValidacionSolicitud(esValida = true, errores = emptyList())
        val errorMessage = "Sin conexión a internet"
        val solicitudId = "solicitud-offline-123"

        coEvery { validarSolicitudUseCase(any()) } returns flowOf(
            RepositoryResult.Success(mockValidacion)
        )
        coEvery { enviarSolicitudUseCase(any()) } returns flowOf(
            RepositoryResult.Error(errorMessage, solicitudId)
        )

        viewModel.solicitarCredito()
        testDispatcher.scheduler.advanceUntilIdle()

        val uiState = viewModel.solicitudUiState.value
        assertTrue(uiState is UiState.Error)
        assertTrue((uiState as UiState.Error).isOffline)
        assertEquals(solicitudId, uiState.idSolicitud)
    }

    @Test
    fun `solicitarCredito_conErrorDeValidacion_noEnviaSolicitud`() = runTest {
        val errorMessage = "Error en validación"
        coEvery { validarSolicitudUseCase(any()) } returns flowOf(
            RepositoryResult.Error(errorMessage)
        )

        viewModel.solicitarCredito()
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { validarSolicitudUseCase(any()) }
        coVerify(exactly = 0) { enviarSolicitudUseCase(any()) }

        val uiState = viewModel.solicitudUiState.value
        assertTrue(uiState is UiState.Error)
        assertEquals(errorMessage, (uiState as UiState.Error).message)
    }

}
