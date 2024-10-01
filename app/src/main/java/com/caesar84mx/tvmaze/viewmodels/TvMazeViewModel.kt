package com.caesar84mx.tvmaze.viewmodels

import androidx.lifecycle.ViewModel
import com.caesar84mx.tvmaze.data.model.backbone.UiState
import com.caesar84mx.tvmaze.util.navigation.Back
import com.caesar84mx.tvmaze.util.navigation.NavigationEvent
import com.caesar84mx.tvmaze.util.navigation.Navigator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * An abstract base class for ViewModels in the TvMaze application.
 *
 * This class provides common functionality for managing UI state and navigation,
 * as well as handling back presses, error dismissals, and refresh actions.
 *
 * @param navigator A [Navigator] instance for handling navigation events.
 */
abstract class TvMazeViewModel(private val navigator: Navigator): ViewModel() {
    private val _state: MutableStateFlow<UiState> = MutableStateFlow(UiState.Idle)

    /**
     * A [StateFlow] exposing the current UI state to observers.
     */
    val state: StateFlow<UiState> = _state.asStateFlow()

    /**
     * Updates the current UI state.
     *
     * @param state The new UI state.
     */
    protected fun updateState(state: UiState) {
        _state.value = state
    }

    /**
     * Triggers a navigation event.
     *
     * @param destination The [NavigationEvent] to navigate to.
     */
    protected fun navigateTo(destination: NavigationEvent) {
        navigator.navigateTo(destination)
    }

    /**
     * Handles the back press action.
     *
     * The default implementation navigates back using the [Navigator].
     */
    open fun onBackPressed() {
        navigateTo(Back)
    }

    /**
     * Handles the dismissal of an error.
     *
     * The default implementation sets the UI state to [UiState.Idle].
     */
    open fun onErrorDismiss() {
        updateState(UiState.Idle)
    }

    /**
     * Handles the refresh action.
     *
     * The default implementation sets the UI state to [UiState.Refreshing].
     */
    open fun refresh() { updateState(UiState.Refreshing) }

    /**
     * Initializes the ViewModel with optional initial data.
     *
     * @param initData Optional initial data of type [T].
     */
    abstract fun <T> initialize(initData: T?)
}
