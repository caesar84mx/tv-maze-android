package com.caesar84mx.tvmaze.ui.screens.episode_details_screen

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalConfiguration
import com.caesar84mx.tvmaze.ui.components.StatefulView
import com.caesar84mx.tvmaze.ui.components.TopBarProperties
import com.caesar84mx.tvmaze.viewmodels.EpisodeDetailsViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun EpisodeDetailsScreen(
    viewModel: EpisodeDetailsViewModel = koinViewModel(),
    showId: Int?,
) {
    val episode by viewModel.episode.collectAsState()
    val orientation = LocalConfiguration.current.orientation

    StatefulView(
        viewModel = viewModel,
        initialData = showId,
        topBarProperties = TopBarProperties(
            title = episode.name,
            showBackButton = true,
            onBackClicked = viewModel::onBackPressed,
        ),
    ) {
        when (orientation) {
            Configuration.ORIENTATION_PORTRAIT -> VerticalView(episode = episode)
            else -> HorizontalView(episode = episode)
        }
    }
}