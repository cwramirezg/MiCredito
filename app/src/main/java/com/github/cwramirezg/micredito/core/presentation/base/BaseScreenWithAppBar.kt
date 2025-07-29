package com.github.cwramirezg.micredito.core.presentation.base

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.cwramirezg.micredito.core.presentation.components.DefaultErrorContent
import com.github.cwramirezg.micredito.core.presentation.components.DefaultLoadingContent
import com.github.cwramirezg.micredito.core.presentation.states.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> BaseScreenWithAppBar(
    uiState: UiState<T>,
    title: String,
    modifier: Modifier = Modifier,
    onRetry: (() -> Unit)? = null,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    loadingContent: @Composable () -> Unit = { DefaultLoadingContent() },
    errorContent: @Composable (String, (() -> Unit)?) -> Unit = { message, retry ->
        DefaultErrorContent(message, retry)
    },
    successContent: @Composable (T) -> Unit
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = navigationIcon,
                actions = actions
            )
        }
    ) { paddingValues ->
        BaseScreen(
            uiState = uiState,
            modifier = Modifier.padding(paddingValues),
            onRetry = onRetry,
            loadingContent = loadingContent,
            errorContent = errorContent,
            successContent = successContent
        )
    }
}
