package com.github.cwramirezg.micredito

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.github.cwramirezg.micredito.navigation.AppNavHost
import com.github.cwramirezg.micredito.navigation.NavigationDestination
import com.github.cwramirezg.micredito.navigation.NavigationManager
import com.github.cwramirezg.micredito.ui.theme.MiCreditoTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var navigationManager: NavigationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MiCreditoTheme {
                val navController = rememberNavController()
                AppNavHost(
                    navController = navController,
                    navigationManager = navigationManager,
                    startDestination = getStartDestination()
                )
            }
        }
    }

    private fun getStartDestination(): Any {
        return NavigationDestination.Splash
    }
}
