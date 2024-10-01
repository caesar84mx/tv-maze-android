package com.caesar84mx.tvmaze.ui.screens.show_details_screen

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.caesar84mx.tvmaze.R
import com.caesar84mx.tvmaze.ui.components.StatefulView
import com.caesar84mx.tvmaze.ui.components.TopBarProperties
import com.caesar84mx.tvmaze.viewmodels.ShowDetailsViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun ShowDetailsScreen(
    viewModel: ShowDetailsViewModel = koinViewModel(),
    showId: Int?,
) {
    val show by viewModel.show.collectAsState()
    val episodes by viewModel.episodes.collectAsState()

    val posterWidth = 170.dp
    val posterHeight = 180.dp

    val orientation = LocalConfiguration.current.orientation

    StatefulView(
        viewModel = viewModel,
        initialData = showId,
        topBarProperties = TopBarProperties(
            title = show.name,
            showBackButton = true,
            onBackClicked = viewModel::onBackPressed,
            actions = mapOf(
                if (show.isFavorite) {
                    R.drawable.ic_favorite_fill
                } else {
                    R.drawable.ic_favorite_empty
                } to viewModel::onFavouriteClicked
            )
        ),
    ) {
        when(orientation) {
            Configuration.ORIENTATION_PORTRAIT -> VerticalView(
                viewModel = viewModel,
                show = show,
                episodes = episodes,
                posterWidth = posterWidth,
                posterHeight = posterHeight,
            )
            else -> HorizontalView(
                viewModel = viewModel,
                show = show,
                episodes = episodes,
                posterWidth = posterWidth,
                posterHeight = posterHeight,
            )
        }
    }
}