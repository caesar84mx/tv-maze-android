package com.caesar84mx.tvmaze.ui.screens.show_details_screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import com.caesar84mx.tvmaze.R
import com.caesar84mx.tvmaze.data.repository.EpisodesRepositoryMock
import com.caesar84mx.tvmaze.data.repository.ShowsRepositoryMock
import com.caesar84mx.tvmaze.ui.components.TvMazeEpisodePosterView
import com.caesar84mx.tvmaze.ui.components.TvMazeHtmlText
import com.caesar84mx.tvmaze.ui.components.TvMazeLabel
import com.caesar84mx.tvmaze.ui.components.TvMazeRatingView
import com.caesar84mx.tvmaze.ui.theme.TvMazeTheme
import com.caesar84mx.tvmaze.util.navigation.TvMazeNavigator
import com.caesar84mx.tvmaze.viewmodels.EpisodeUi
import com.caesar84mx.tvmaze.viewmodels.ShowDetailsUi
import com.caesar84mx.tvmaze.viewmodels.ShowDetailsViewModel
import com.caesar84mx.tvmaze.viewmodels.ShowDetailsViewModelImpl

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
internal fun VerticalView(
    viewModel: ShowDetailsViewModel,
    show: ShowDetailsUi,
    episodes: Map<Int, List<EpisodeUi>>,
    posterWidth: Dp,
    posterHeight: Dp,
) {
    LazyColumn {
        item {
            Column(
                modifier = Modifier
                    .systemBarsPadding()
                    .padding(
                        start = 20.dp,
                        end = 20.dp,
                        bottom = 20.dp,
                    )
                    .fillMaxSize()
            ) {
                GlideImage(
                    model = show.image,
                    contentDescription = null,
                    contentScale = ContentScale.FillBounds,
                    loading = placeholder(painter = painterResource(R.drawable.ic_landscape)),
                    failure = placeholder(painter = painterResource(R.drawable.ic_landscape)),
                    modifier = Modifier
                        .padding(bottom = 10.dp)
                        .fillParentMaxHeight(0.6f)
                        .fillMaxWidth()
                        .clip(MaterialTheme.shapes.medium)
                )

                TvMazeRatingView(
                    rating = show.rating,
                    showNumericalValue = true,
                    modifier = Modifier.padding(vertical = 10.dp)
                )

                Row {
                    show.genres.forEach { genre ->
                        TvMazeLabel(genre, modifier = Modifier.padding(end = 5.dp))
                    }
                }

                TvMazeHtmlText(
                    text = show.summary,
                    size = MaterialTheme.typography.bodyMedium.fontSize,
                )

                Column(
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .border(
                            width = 2.dp,
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.shapes.medium
                        )
                        .background(
                            MaterialTheme.colorScheme.inversePrimary.copy(alpha = 0.7f),
                            MaterialTheme.shapes.medium
                        )
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = stringResource(R.string.on_air_label),
                        modifier = Modifier.padding(
                            horizontal = 10.dp,
                            vertical = 5.dp,
                        )
                    )

                    show.schedule.days.forEach { day ->
                        Text(
                            text = "$day${if (show.schedule.time.isNotEmpty()) ": ${show.schedule.time}" else ""}",
                            modifier = Modifier.padding(
                                horizontal = 10.dp,
                                vertical = 5.dp,
                            ),
                        )

                    }
                }

                episodes.entries.forEach { (season, episodes) ->
                    if (episodes.isNotEmpty()) {
                        var isExpanded by remember { mutableStateOf(false) }
                        TextButton(onClick = { isExpanded = !isExpanded }) {
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                Text(
                                    text = stringResource(R.string.season_label, season),
                                    modifier = Modifier.padding(
                                        vertical = 5.dp,
                                    ),
                                )
                                Icon(
                                    painter = painterResource(
                                        if (!isExpanded) {
                                            R.drawable.ic_chevron_down
                                        } else {
                                            R.drawable.ic_chevron_up
                                        }
                                    ),
                                    contentDescription = null
                                )
                            }
                        }

                        AnimatedVisibility(visible = isExpanded) {
                            LazyRow(modifier = Modifier.fillMaxWidth()) {
                                items(items = episodes) { episode ->
                                    TvMazeEpisodePosterView(
                                        episode = episode,
                                        onClicked = viewModel::onEpisodeClicked,
                                        modifier = Modifier
                                            .padding(end = 10.dp)
                                            .size(posterWidth, posterHeight)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun HomeScreenPreviewLight() {
    TvMazeTheme(darkTheme = false) {
        ShowDetailsScreen(
            ShowDetailsViewModelImpl(
                navigator = TvMazeNavigator(),
                showsRepository = ShowsRepositoryMock(),
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
        ShowDetailsScreen(
            ShowDetailsViewModelImpl(
                navigator = TvMazeNavigator(),
                showsRepository = ShowsRepositoryMock(),
                episodesRepository = EpisodesRepositoryMock(),
            ),
            1
        )
    }
}
