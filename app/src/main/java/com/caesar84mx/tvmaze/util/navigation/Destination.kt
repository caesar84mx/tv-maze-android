package com.caesar84mx.tvmaze.util.navigation

/**
 * Represents a screen or destination within the application's navigation graph.
 * Each destination has a unique `route` that can be used for navigation.
 */
interface Destination: NavigationEvent {
    /**
     * The unique route associated with this destination.
     */
    val route: String
}

data object PinCode: Destination {
    override val route: String = "pin_code"
}

data object Home: Destination {
    override val route: String = "home"
}

data class ShowDetails(val showId: Int): Destination {
    override val route: String = "show_details/$showId"
    companion object {
        const val CANONICAL_ROUTE = "show_details/{showId}"
    }
}

data class EpisodeDetails(val episodeId: Int): Destination {
    override val route: String = "episode_details/$episodeId"
    companion object {
        const val CANONICAL_ROUTE = "episode_details/{episodeId}"
    }
}
