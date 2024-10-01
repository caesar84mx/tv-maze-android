package com.caesar84mx.tvmaze.ui.components

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.IntOffset
import com.caesar84mx.tvmaze.data.model.backbone.UiState
import com.caesar84mx.tvmaze.viewmodels.TvMazeViewModel
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatefulView(
    viewModel: TvMazeViewModel,
    initialData: Any? = null,
    refreshingEnabled: Boolean = false,
    refreshIndicatorOffset: Float = 0.14f,
    topBarProperties: TopBarProperties? = null,
    background: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val state by viewModel.state.collectAsState()
    val pullRefreshState = rememberPullToRefreshState()
    val viewHeight = LocalView.current.height

    LaunchedEffect(Unit) {
        viewModel.initialize(initialData)
    }

    LaunchedEffect(state) {
        when (state) {
            is UiState.Success -> {
                scope.launch {
                    val success = state as UiState.Success
                    val result = snackbarHostState.showSnackbar(success.message)

                    when (result) {
                        SnackbarResult.Dismissed,
                        SnackbarResult.ActionPerformed -> viewModel.onErrorDismiss()
                    }
                }
            }

            is UiState.Error -> {
                scope.launch {
                    val error = state as UiState.Error
                    val result = snackbarHostState.showSnackbar(error.message)

                    when (result) {
                        SnackbarResult.Dismissed,
                        SnackbarResult.ActionPerformed -> viewModel.onErrorDismiss()
                    }
                }
            }

            else -> { /* no-op */ }
        }
    }

    TvMazeBackPressListener(viewModel::onBackPressed)

    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(
                    containerColor = when (state) {
                        is UiState.Error -> MaterialTheme.colorScheme.error
                        is UiState.Success -> MaterialTheme.colorScheme.primary
                        else -> MaterialTheme.colorScheme.background
                    },
                    actionColor = when (state) {
                        is UiState.Error -> MaterialTheme.colorScheme.onError
                        is UiState.Success -> MaterialTheme.colorScheme.onPrimary
                        else -> MaterialTheme.colorScheme.onBackground
                    },
                    snackbarData = data,
                    actionOnNewLine = true,
                    modifier = Modifier.navigationBarsPadding()
                )
            }
        }
    ) {
        Box(
            modifier = Modifier
                .pullToRefresh(
                    isRefreshing = state == UiState.Refreshing,
                    enabled = refreshingEnabled,
                    state = pullRefreshState,
                    onRefresh = { viewModel.refresh() },
                )
                .fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            background?.invoke()

            Column {
                topBarProperties?.let { TvMazeTopBar(it) }
                content()
            }

            Indicator(
                isRefreshing = state == UiState.Refreshing,
                state = pullRefreshState,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset {
                        IntOffset(
                            x = 0,
                            y = (viewHeight * refreshIndicatorOffset).toInt()
                        )
                    },
            )

            when (state) {
                is UiState.Loading -> {
                    TvMazeLoadingOverlay()
                }

                else -> { /* no-op */
                }
            }
        }
    }
}

data class TopBarProperties(
    val title: String,
    val showBackButton: Boolean = false,
    val onBackClicked: () -> Unit = {},
    val actions: Map<Int, () -> Unit> = emptyMap()
)
