package com.github.cwramirezg.micredito.core.presentation.base

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.github.cwramirezg.micredito.core.presentation.components.DefaultErrorContent
import com.github.cwramirezg.micredito.core.presentation.components.DefaultIdleContent
import com.github.cwramirezg.micredito.core.presentation.components.DefaultLoadingContent
import com.github.cwramirezg.micredito.core.presentation.states.UiState

@Composable
fun <T> BaseScreen(
    uiState: UiState<T>,
    modifier: Modifier = Modifier,
    onRetry: (() -> Unit)? = null,
    containerAlignment: Alignment = Alignment.Center,
    idleContent: @Composable () -> Unit = { DefaultIdleContent() },
    loadingContent: @Composable () -> Unit = { DefaultLoadingContent() },
    successContent: @Composable (T) -> Unit,
    errorContent: @Composable (String, (() -> Unit)?) -> Unit = { message, retry ->
        DefaultErrorContent(message, retry)
    }
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = containerAlignment
    ) {
        when (uiState) {
            is UiState.Idle -> idleContent()
            is UiState.Loading -> loadingContent()
            is UiState.Success -> {
                uiState.data?.let { data ->
                    successContent(data)
                } ?: run {
                    errorContent("No hay datos disponibles", onRetry)
                }
            }

            is UiState.Error -> errorContent(uiState.message, onRetry)
        }
    }
}
