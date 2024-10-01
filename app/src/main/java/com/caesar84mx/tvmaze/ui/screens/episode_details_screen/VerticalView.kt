package com.caesar84mx.tvmaze.ui.screens.episode_details_screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
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
internal fun VerticalView(episode: EpisodeDetailsUi) {
    LazyColumn {
        item {
            Column(
                modifier = Modifier
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
                        .padding(bottom = 10.dp)
                        .fillParentMaxHeight(0.4f)
                        .fillMaxWidth()
                        .clip(MaterialTheme.shapes.medium)
                )

                TvMazeRatingView(
                    rating = episode.rating,
                    showNumericalValue = true,
                    modifier = Modifier.padding(vertical = 10.dp)
                )

                TvMazeHtmlText(
                    text = episode.summary,
                    size = MaterialTheme.typography.bodyMedium.fontSize,
                )
            }
        }
    }
}

@Preview
@Composable
private fun HomeScreenPreviewLight() {
    TvMazeTheme(darkTheme = false) {
        EpisodeDetailsScreen(
            EpisodeDetailsViewModelImpl(
                navigator = TvMazeNavigator(),
                episodesRepository = EpisodesRepositoryMock(),
            ),
            1
        )
    }
}

@Preview
@Composable
private fun HomeScreenPreview() {
    TvMazeTheme(darkTheme = true) {
        EpisodeDetailsScreen(
            EpisodeDetailsViewModelImpl(
                navigator = TvMazeNavigator(),
                episodesRepository = EpisodesRepositoryMock(),
            ),
            1
        )
    }
}
