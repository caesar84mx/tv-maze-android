package com.caesar84mx.tvmaze.util.navigation

/**
 * Represents a system-level navigation event, such as navigating back or quitting the app.
 */
interface SystemNavigationEvent: NavigationEvent

data object Back: SystemNavigationEvent
data object Quit: SystemNavigationEvent