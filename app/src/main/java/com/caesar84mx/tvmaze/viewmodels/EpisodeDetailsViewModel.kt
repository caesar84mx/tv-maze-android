package com.caesar84mx.tvmaze.viewmodels

import androidx.lifecycle.viewModelScope
import com.caesar84mx.tvmaze.data.model.backbone.Episode
import com.caesar84mx.tvmaze.data.model.backbone.UiState
import com.caesar84mx.tvmaze.data.repository.EpisodesRepository
import com.caesar84mx.tvmaze.util.navigation.Back
import com.caesar84mx.tvmaze.util.navigation.Navigator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

abstract class EpisodeDetailsViewModel(navigator: Navigator) : TvMazeViewModel(navigator) {
    abstract val episode: StateFlow<EpisodeDetailsUi>
}

internal class EpisodeDetailsViewModelImpl(
    navigator: Navigator,
    private val episodesRepository: EpisodesRepository,
) : EpisodeDetailsViewModel(navigator) {
    private val _episode = MutableStateFlow(EpisodeDetailsUi.EMPTY)
    override val episode: StateFlow<EpisodeDetailsUi> = _episode.asStateFlow()

    override fun <T> initialize(initData: T?) {
        updateState(UiState.Loading)
        viewModelScope.launch(Dispatchers.IO) {
            if (initData == null) {
                Timber.e("Episode id not provided")

                updateState(UiState.Error("No episode id provided"))
                delay(2000)
                navigateTo(Back)

                return@launch
            }

            val episodeId = initData as Int
            episodesRepository.getEpisode(episodeId).collect { episode ->
                if (episode == null) {
                    Timber.e("Show id $initData not found")

                    updateState(UiState.Error("Show id $initData not found"))
                    delay(2000)
                    navigateTo(Back)

                    return@collect
                } else {
                    _episode.emit(episode.toUi())
                    updateState(UiState.Idle)
                }
            }
        }
    }
}

private fun Episode.toUi(): EpisodeDetailsUi = EpisodeDetailsUi(
    id = id,
    name = name,
    image = image.original,
    rating = rating,
    summary = summary,
)

data class EpisodeDetailsUi(
    val id: Int,
    val name: String,
    val summary: String,
    val image: String,
    val rating: Float,
) {
    companion object {
        val EMPTY = EpisodeDetailsUi(
            id = 0,
            name = "",
            summary = "",
            image = "",
            rating = 0f,
        )
    }
}