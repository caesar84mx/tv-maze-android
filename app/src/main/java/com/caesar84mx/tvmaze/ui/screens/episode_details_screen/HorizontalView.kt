package com.caesar84mx.tvmaze.ui.screens.episode_details_screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import com.caesar84mx.tvmaze.R
import com.caesar84mx.tvmaze.data.repository.EpisodesRepositoryMock
import com.caesar84mx.tvmaze.ui.components.TvMazeHtmlText
import com.caesar84mx.tvmaze.ui.components.TvMazeRatingView
import com.caesar84mx.tvmaze.ui.theme.TvMazeTheme
import com.caesar84mx.tvmaze.util.navigation.TvMazeNavigator
import com.caesar84mx.tvmaze.viewmodels.EpisodeDetailsUi
import com.caesar84mx.tvmaze.viewmodels.EpisodeDetailsViewModelImpl

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
internal fun HorizontalView(episode: EpisodeDetailsUi) {
    Row(
        modifier = Modifier
            .navigationBarsPadding()
            .padding(
                start = 20.dp,
                end = 20.dp,
                bottom = 20.dp,
            )
            .fillMaxSize()
    ) {
        GlideImage(
            model = episode.image,
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            loading = placeholder(painter = painterResource(R.drawable.ic_landscape)),
            failure = placeholder(painter = painterResource(R.drawable.ic_landscape)),
            modifier = Modifier
                .fillMaxWidth(.3f)
                .fillMaxHeight(.7f)
                .clip(MaterialTheme.shapes.medium)
        )

        LazyColumn(
            modifier = Modifier.padding(horizontal = 10.dp)
        ) {
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    TvMazeRatingView(
                        rating = episode.rating,
                        showNumericalValue = true,
                        modifier = Modifier.padding(bottom = 10.dp)
                    )

                    TvMazeHtmlText(
                        text = episode.summary,
                        size = MaterialTheme.typography.bodyMedium.fontSize,
                    )
                }
            }
        }

        Spacer(Modifier.weight(1f))
    }
}

@Preview(device = Devices.PIXEL_TABLET)
@Composable
private fun HomeScreenPreviewLightHorizontal() {
    TvMazeTheme(darkTheme = false) {
        EpisodeDetailsScreen(
            EpisodeDetailsViewModelImpl(
                navigator = TvMazeNavigator(),
                episodesRepository = EpisodesRepositoryMock(),
            ),
            0
        )
    }
}

@Preview(device = Devices.PIXEL_TABLET)
@Composable
private fun HomeScreenPreviewHorizontal() {
    TvMazeTheme(darkTheme = true) {
        EpisodeDetailsScreen(
            EpisodeDetailsViewModelImpl(
                navigator = TvMazeNavigator(),
                episodesRepository = EpisodesRepositoryMock(),
            ),
            0
        )
    }
}