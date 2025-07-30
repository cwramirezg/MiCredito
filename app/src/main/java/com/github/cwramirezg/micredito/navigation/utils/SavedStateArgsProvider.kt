package com.github.cwramirezg.micredito.navigation.utils

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import com.github.cwramirezg.micredito.navigation.NavigationDestination
import javax.inject.Inject

class SavedStateArgsProvider @Inject constructor(
    private val savedStateHandle: SavedStateHandle
) : ArgsProvider {
    override fun getSimulacionArgs(): NavigationDestination.Simulacion =
        savedStateHandle.toRoute<NavigationDestination.Simulacion>()
}