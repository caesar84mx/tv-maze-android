package com.caesar84mx.tvmaze.data.model.backbone

sealed class UiState {
    data object Loading : UiState()
    data object Refreshing : UiState()
    data object Idle : UiState()
    data class Success(val message: String) : UiState()
    data class Error(val message: String) : UiState()
}
