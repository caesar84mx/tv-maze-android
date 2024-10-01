package com.caesar84mx.tvmaze.ui.screens.home_screen

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import com.caesar84mx.tvmaze.ui.components.StatefulView
import com.caesar84mx.tvmaze.viewmodels.HomeViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen(viewModel: HomeViewModel = koinViewModel()) {
    val query by viewModel.searchQuery.collectAsState()
    val shows by viewModel.shows.collectAsState()
    val favorites by viewModel.favorites.collectAsState()

    val showPosterWidth = 170.dp
    val showPosterHeight = 250.dp

    val favoritePosterWidth = 150.dp
    val favoritePosterHeight = 180.dp

    val focusManager = LocalFocusManager.current
    val orientation = LocalConfiguration.current.orientation

    StatefulView(
        viewModel = viewModel,
        refreshingEnabled = true,
    ) {
        when(orientation) {
            Configuration.ORIENTATION_PORTRAIT -> VerticalView(
                viewModel = viewModel,
                query = query,
                shows = shows,
                favorites = favorites,
                showPosterWidth = showPosterWidth,
                showPosterHeight = showPosterHeight,
                favoritePosterWidth = favoritePosterWidth,
                favoritePosterHeight = favoritePosterHeight,
                focusManager = focusManager,
            )
            else -> HorizontalView(
                viewModel = viewModel,
                query = query,
                shows = shows,
                favorites = favorites,
                showPosterWidth = showPosterWidth,
                showPosterHeight = showPosterHeight,
                favoritePosterWidth = favoritePosterWidth,
                favoritePosterHeight = favoritePosterHeight,
                focusManager = focusManager,
            )
        }
    }
}
