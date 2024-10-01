package com.caesar84mx.tvmaze.util.navigation

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class TvMazeNavigator: Navigator {
    private val _navigationCommand = MutableSharedFlow<NavigationEvent>(extraBufferCapacity = 1)
    override val navigationCommand: SharedFlow<NavigationEvent> = _navigationCommand.asSharedFlow()

    override fun navigateTo(destination: NavigationEvent) {
        _navigationCommand.tryEmit(destination)
    }
}