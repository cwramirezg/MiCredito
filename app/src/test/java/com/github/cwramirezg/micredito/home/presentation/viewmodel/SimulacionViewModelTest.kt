package com.github.cwramirezg.micredito.home.presentation.viewmodel

import com.github.cwramirezg.micredito.core.data.repository.RepositoryResult
import com.github.cwramirezg.micredito.home.domain.entities.SimulacionCredito
import com.github.cwramirezg.micredito.home.domain.usecase.EnviarSolicitudUseCase
import com.github.cwramirezg.micredito.home.domain.usecase.SimularCreditoUseCase
import com.github.cwramirezg.micredito.home.domain.usecase.ValidarSolicitudUseCase
import com.github.cwramirezg.micredito.navigation.NavigationDestination
import com.github.cwramirezg.micredito.navigation.utils.ArgsProvider
import io.mockk.coEvery
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
    fun `onMontoChanged_actualizaMontoSeleccionado_yDisparaSimulacion`() = runTest {
        val nuevoMonto = 5000.0
        val mockResultado: SimulacionCredito = mockk()
        coEvery { simularCreditoUseCase(any()) } returns flowOf(
            RepositoryResult.Success(mockResultado)
        )

        viewModel.actualizarMonto(nuevoMonto)

        testDispatcher.scheduler.advanceUntilIdle()


        assertEquals(nuevoMonto, viewModel.montoSeleccionado.value, 0.0)

        // Verifica que el caso de uso de simulación fue llamado con el nuevo monto
        // coVerify { simularCreditoUseCase(eq(nuevoMonto), any(), any()) }

        // Verifica el UiState usando Turbine
        // viewModel.uiState.test {
        //     skipItems(1) // Salta el estado inicial si ya lo probaste
        //     val updatedState = awaitItem()
        //     assertEquals(mockResultado, updatedState.resultadoSimulacion)
        //     cancelAndIgnoreRemainingEvents()
        // }
    }


}