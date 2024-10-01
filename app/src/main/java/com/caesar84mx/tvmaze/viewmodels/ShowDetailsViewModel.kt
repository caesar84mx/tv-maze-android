package com.caesar84mx.tvmaze.viewmodels

import androidx.lifecycle.viewModelScope
import com.caesar84mx.tvmaze.data.model.backbone.Episode
import com.caesar84mx.tvmaze.data.model.backbone.Show
import com.caesar84mx.tvmaze.data.model.backbone.UiState
import com.caesar84mx.tvmaze.data.model.network.Schedule
import com.caesar84mx.tvmaze.data.repository.EpisodesRepository
import com.caesar84mx.tvmaze.data.repository.ShowsRepository
import com.caesar84mx.tvmaze.util.navigation.Back
import com.caesar84mx.tvmaze.util.navigation.EpisodeDetails
import com.caesar84mx.tvmaze.util.navigation.Navigator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import timber.log.Timber

abstract class ShowDetailsViewModel(navigator: Navigator) : TvMazeViewModel(navigator) {
    abstract val show: StateFlow<ShowDetailsUi>
    abstract val episodes: StateFlow<Map<Int, List<EpisodeUi>>>
    abstract fun onEpisodeClicked(id: Int)
    abstract fun onFavouriteClicked()
}

internal class ShowDetailsViewModelImpl(
    navigator: Navigator,
    private val showsRepository: ShowsRepository,
    private val episodesRepository: EpisodesRepository,
) : ShowDetailsViewModel(navigator) {
    private val _show = MutableStateFlow(ShowDetailsUi.EMPTY)
    override val show: StateFlow<ShowDetailsUi> = _show.asStateFlow()

    private val _episodes = MutableStateFlow(emptyMap<Int, List<EpisodeUi>>())
    override val episodes: StateFlow<Map<Int, List<EpisodeUi>>> = _episodes.asStateFlow()

    override fun <T> initialize(initData: T?) {
        updateState(UiState.Loading)
        viewModelScope.launch(Dispatchers.IO) {
            if (initData == null) {
                Timber.e("Show id not provided")

                updateState(UiState.Error("No show id provided"))
                delay(2000)
                navigateTo(Back)

                return@launch
            }

            val showId = initData as Int
            showsRepository.getShow(showId).collect { show ->
                if (show == null) {
                    Timber.e("Show id $initData not found")

                    updateState(UiState.Error("Show id $initData not found"))
                    delay(2000)
                    navigateTo(Back)

                    return@collect
                } else {
                    _show.emit(show.toUi())
                }
            }

            try {
                episodesRepository.getEpisodes(_show.value.apiId).collect { episodes ->
                    _episodes.emit(episodes.mapValues { (_, episodes) -> episodes.map { it.toUi() } })
                }
            } catch (e: HttpException) {
                e.response()?.raw()?.let { response ->
                    val message = response.message
                    val url = response.request.url

                    Timber.e("Request url: $url failed with message: $message")
                }
            } finally {
                updateState(UiState.Idle)
            }
        }
    }

    override fun onFavouriteClicked() {
        viewModelScope.launch {
            val isFavorite = !_show.value.isFavorite
            showsRepository.toggleShowFavorite(_show.value.id, isFavorite)
                .collect { show ->
                    val ui = show?.toUi()?: ShowDetailsUi.EMPTY
                    _show.emit(ui)
                    updateState(UiState.Success("${if (ui.isFavorite) "Added to" else "Removed from"}  favorites"))
                }
        }
    }

    override fun onEpisodeClicked(id: Int) {
        navigateTo(EpisodeDetails(id))
    }
}

private fun Show.toUi(): ShowDetailsUi = ShowDetailsUi(
    id = localId,
    apiId = apiId,
    name = name,
    image = images.original,
    summary = summary,
    genres = genres,
    schedule = schedule,
    isFavorite = isFavorite,
    rating = rating,
)

private fun Episode.toUi(): EpisodeUi = EpisodeUi(
    id = id,
    name = name,
    image = image.medium,
    rating = rating,
)

data class EpisodeUi(
    val id: Int,
    val name: String,
    val image: String,
    val rating: Float,
)

data class ShowDetailsUi(
    val id: Int,
    val apiId: Int,
    val name: String,
    val summary: String,
    val genres: List<String>,
    val schedule: Schedule,
    val image: String,
    val isFavorite: Boolean,
    val rating: Float,
) {
    companion object {
        val EMPTY = ShowDetailsUi(
            id = 0,
            apiId = 0,
            name = "",
            summary = "",
            genres = emptyList(),
            schedule = Schedule("", emptyList()),
            image = "",
            isFavorite = false,
            rating = 0f,
        )
    }
}