package com.github.cwramirezg.micredito.home.presentation.pojos

import com.github.cwramirezg.micredito.home.domain.entities.Cliente
import com.github.cwramirezg.micredito.home.domain.entities.LineaCredito

data class CreditoSuccess(
    val cliente: Cliente? = null,
    val lineaCreditos: List<LineaCredito> = emptyList()
)
