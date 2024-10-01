package com.caesar84mx.tvmaze.util.navigation

import kotlinx.coroutines.flow.SharedFlow

/**
 * Provides navigation capabilities for the application.
 */
interface Navigator {
    /**
     * A [SharedFlow] that emits navigation events.
     * Observe this flow to receive navigation commands and update the UI accordingly.
     */
    val navigationCommand: SharedFlow<NavigationEvent>

    /**
     * Navigates to the specified [destination].
     *
     * @param destination The navigation event representing the target destination.
     */
    fun navigateTo(destination: NavigationEvent)
}
