package com.caesar84mx.tvmaze.ui.screens.home_screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ContextualFlowRow
import androidx.compose.foundation.layout.ContextualFlowRowOverflow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.caesar84mx.tvmaze.R
import com.caesar84mx.tvmaze.data.repository.EpisodesRepositoryMock
import com.caesar84mx.tvmaze.data.repository.ShowsRepositoryMock
import com.caesar84mx.tvmaze.ui.components.TvMazeSearchTextField
import com.caesar84mx.tvmaze.ui.components.TvMazeShowMoreIndicator
import com.caesar84mx.tvmaze.ui.components.TvMazeShowPosterView
import com.caesar84mx.tvmaze.ui.theme.TvMazeTheme
import com.caesar84mx.tvmaze.util.navigation.TvMazeNavigator
import com.caesar84mx.tvmaze.viewmodels.HomeViewModel
import com.caesar84mx.tvmaze.viewmodels.HomeViewModelImpl
import com.caesar84mx.tvmaze.viewmodels.ShowUi

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun VerticalView(
    viewModel: HomeViewModel,
    query: String,
    shows: List<ShowUi>,
    favorites: List<ShowUi>,
    showPosterWidth: Dp,
    showPosterHeight: Dp,
    favoritePosterWidth: Dp,
    favoritePosterHeight: Dp,
    focusManager: FocusManager,
) {
    var maxShowsLines by remember { mutableIntStateOf(3) }

    ConstraintLayout(
        modifier = Modifier
            .systemBarsPadding()
            .padding(20.dp)
            .fillMaxSize()
    ) {
        val (searchBar, showsList, favoritesList) = createRefs()

        TvMazeSearchTextField(
            value = query,
            onValueChange = viewModel::onSearchQueryChanged,
            label = stringResource(R.string.search_field_label),
            placeholder = stringResource(R.string.search_field_hint),
            onSearch = { focusManager.clearFocus() },
            modifier = Modifier
                .padding(bottom = 10.dp)
                .constrainAs(searchBar) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                },
        )

        AnimatedVisibility(
            visible = favorites.isNotEmpty(),
            modifier = Modifier
                .constrainAs(favoritesList) {
                    top.linkTo(searchBar.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)

                    width = Dimension.fillToConstraints
                    height = Dimension.wrapContent
                }
        ) {
            Column(
                modifier = Modifier
                    .padding(bottom = 10.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.favorites_title),
                    style = MaterialTheme.typography.titleLarge,
                )

                LazyRow {
                    items(favorites) {
                        TvMazeShowPosterView(
                            modifier = Modifier
                                .size(favoritePosterWidth, favoritePosterHeight)
                                .padding(end = 20.dp),
                            show = it,
                            onShowClicked = viewModel::onShowClicked,
                            onFavouriteClicked = viewModel::onFavouriteClicked,
                        )
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = shows.isNotEmpty(),
            modifier = Modifier.constrainAs(showsList) {
                top.linkTo(if (favorites.isEmpty()) searchBar.bottom else favoritesList.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(parent.bottom)
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            }
        ) {
            Column(
                modifier = Modifier
                    .padding(bottom = 10.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.series_title),
                    style = MaterialTheme.typography.titleLarge,
                )

                ContextualFlowRow(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .fillMaxSize(),
                    maxLines = maxShowsLines,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    overflow = ContextualFlowRowOverflow.expandOrCollapseIndicator(
                        minRowsToShowCollapse = 4,
                        expandIndicator = {
                            TvMazeShowMoreIndicator(
                                totalItems = shows.size,
                                onMoreClicked = { maxShowsLines += 5 },
                                modifier = Modifier
                                    .size(showPosterWidth, showPosterHeight)
                                    .padding(bottom = 20.dp),
                            )
                        },
                        collapseIndicator = { }
                    ),
                    itemCount = shows.size
                ) { index ->
                    if (shows.size > index) {
                        TvMazeShowPosterView(
                            modifier = Modifier
                                .size(showPosterWidth, showPosterHeight)
                                .padding(bottom = 20.dp),
                            show = shows[index],
                            onShowClicked = viewModel::onShowClicked,
                            onFavouriteClicked = viewModel::onFavouriteClicked,
                        )
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
        HomeScreen(
            HomeViewModelImpl(
                navigator = TvMazeNavigator(),
                showsRepository = ShowsRepositoryMock(),
                episodesRepository = EpisodesRepositoryMock(),
            )
        )
    }
}

@Preview
@Composable
private fun HomeScreenPreview() {
    TvMazeTheme(darkTheme = true) {
        HomeScreen(
            HomeViewModelImpl(
                navigator = TvMazeNavigator(),
                showsRepository = ShowsRepositoryMock(),
                episodesRepository = EpisodesRepositoryMock(),
            )
        )
    }
}
