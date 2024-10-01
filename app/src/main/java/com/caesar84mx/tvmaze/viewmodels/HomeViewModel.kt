package com.caesar84mx.tvmaze.viewmodels

import androidx.lifecycle.viewModelScope
import com.caesar84mx.tvmaze.data.model.backbone.Show
import com.caesar84mx.tvmaze.data.model.backbone.UiState
import com.caesar84mx.tvmaze.data.repository.EpisodesRepository
import com.caesar84mx.tvmaze.data.repository.ShowsRepository
import com.caesar84mx.tvmaze.util.navigation.ShowDetails
import com.caesar84mx.tvmaze.util.navigation.Navigator
import com.caesar84mx.tvmaze.util.navigation.Quit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

abstract class HomeViewModel(navigator: Navigator) : TvMazeViewModel(navigator) {
    abstract val searchQuery: StateFlow<String>
    abstract val shows: StateFlow<List<ShowUi>>
    abstract val favorites: StateFlow<List<ShowUi>>

    abstract fun onSearchQueryChanged(query: String)
    abstract fun onShowClicked(id: Int)
    abstract fun onFavouriteClicked(id: Int)
}

internal class HomeViewModelImpl(
    navigator: Navigator,
    private val showsRepository: ShowsRepository,
    private val episodesRepository: EpisodesRepository,
) : HomeViewModel(navigator = navigator) {
    private val _shows = MutableStateFlow<List<ShowUi>>(listOf())
    override val shows: StateFlow<List<ShowUi>> = _shows.asStateFlow()

    private val _favorites = MutableStateFlow<List<ShowUi>>(listOf())
    override val favorites: StateFlow<List<ShowUi>> = _favorites.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    override val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    override fun <T> initialize(initData: T?) {
        updateState(UiState.Loading)
        viewModelScope.launch {
            showsRepository.getShows().collect { shows ->
                updateData(shows)
                updateState(UiState.Idle)
            }
        }
    }

    override fun refresh() {
        super.refresh()
        viewModelScope.launch(Dispatchers.IO) {
            _shows.emit(listOf())
            _searchQuery.emit("")
            episodesRepository.clear().collect()
            showsRepository.getShows(forceRefresh = true).collect { shows ->
                updateData(shows)
                updateState(UiState.Idle)
            }
        }
    }

    override fun onSearchQueryChanged(query: String) {
        viewModelScope.launch {
            _searchQuery.value = query
            _shows.emit(listOf())
            showsRepository.searchShowsByName(query)
                .collect { shows ->
                    val showUiModels = shows.map { it.toUi() }
                    _shows.emit(showUiModels)
                }
        }
    }

    override fun onShowClicked(id: Int) {
        viewModelScope.launch {
            _searchQuery.emit("")
            navigateTo(ShowDetails(id))
        }
    }

    override fun onFavouriteClicked(id: Int) {
        viewModelScope.launch {
            _shows.value.indexOfFirst { it.id == id }
                .takeIf { it >= 0 }
                ?.let { index ->
                    showsRepository.toggleShowFavorite(id, !_shows.value[index].isFavorite).collect()
                    showsRepository.getCachedShows().collect(::updateData)
                }
        }
    }

    override fun onBackPressed() {
        navigateTo(Quit)
    }

    private suspend fun updateData(shows: List<Show>) {
        val showUiModels = shows.map { it.toUi() }
        _shows.emit(showUiModels)
        _favorites.emit(
            showUiModels.filter { it.isFavorite }
                .sortedBy { it.name }
        )
    }
}

private fun Show.toUi(): ShowUi = ShowUi(
    id = localId,
    name = name,
    summary = summary,
    image = images.medium,
    rating = rating,
    isFavorite = isFavorite,
)

data class ShowUi(
    val id: Int,
    val name: String,
    val summary: String,
    val image: String,
    val rating: Float,
    val isFavorite: Boolean,
)
