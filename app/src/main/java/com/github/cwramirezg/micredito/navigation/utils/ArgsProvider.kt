package com.github.cwramirezg.micredito.navigation.utils

import com.github.cwramirezg.micredito.navigation.NavigationDestination

interface ArgsProvider {
    fun getSimulacionArgs(): NavigationDestination.Simulacion
}