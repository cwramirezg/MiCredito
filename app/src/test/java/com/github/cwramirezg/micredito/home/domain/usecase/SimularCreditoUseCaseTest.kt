package com.github.cwramirezg.micredito.home.domain.usecase

import com.github.cwramirezg.micredito.core.data.network.NetworkResult
import com.github.cwramirezg.micredito.home.domain.entities.EstadoLineaCredito
import com.github.cwramirezg.micredito.home.domain.entities.LineaCredito
import com.github.cwramirezg.micredito.home.domain.repository.CreditoRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.kotlin.mock

class SimularCreditoUseCaseTest {

    @Mock
    private lateinit var repository: CreditoRepository

    private lateinit var useCase: SimularCreditoUseCase

    @Before
    fun setup() {
        repository = mock()
        useCase = SimularCreditoUseCase(repository)
    }

    @Test
    fun `calcular simulacion con parametros validos devuelve resultado correcto`() = runTest {
        // Given
        val lineaCredito = LineaCredito(
            id = "asdasd",
            createdAt = 65446,
            updatedAt = 64654654,
            clienteId = "465467987",
            montoMaximo = 10000.0,
            montoMinimo = 100.0,
            tasaInteres = 5.0,
            plazoMaximo = 24,
            plazoMinimo = 12,
            estado = EstadoLineaCredito.ACTIVA,
            fechaVencimiento = 3216546546
        )
        val params = SimularCreditoParams(lineaCredito, 10000.0, 12)

        // When
        val result = useCase(params).first()

        // Then
        assertTrue(result is NetworkResult.Success)
        val simulacion = (result as NetworkResult.Success).data
        assertEquals(10000.0, simulacion.monto)
        assertTrue(simulacion.cuotaMensual > 0)
    }

    @Test
    fun `calcular simulacion con monto invalido devuelve error`() = runTest {
        // Similar estructura para casos de error
    }

}