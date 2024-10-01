package com.caesar84mx.tvmaze.data.repository

import android.content.SharedPreferences
import com.caesar84mx.tvmaze.data.model.backbone.Episode
import com.caesar84mx.tvmaze.data.model.dao.EpisodeDao
import com.caesar84mx.tvmaze.data.model.dao.EpisodeEntity
import com.caesar84mx.tvmaze.data.model.network.EpisodeResponse
import com.caesar84mx.tvmaze.data.model.network.ImageData
import com.caesar84mx.tvmaze.data.networking.TvMazeService
import com.caesar84mx.tvmaze.util.Constants.UPDATE_PERIOD
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import timber.log.Timber

interface EpisodesRepository {
    fun getEpisodes(showId: Int): Flow<Map<Int, List<Episode>>>
    fun getEpisode(id: Int): Flow<Episode?>
    fun getCachedEpisodes(showId: Int): Flow<Map<Int, List<Episode>>>
    fun clear(): Flow<Map<Int, Episode>>
}

internal class EpisodesRepositoryImpl(
    private val tvMazeService: TvMazeService,
    private val dao: EpisodeDao,
    private val sharedPreferences: SharedPreferences,
): EpisodesRepository {
    override fun getEpisodes(showId: Int): Flow<Map<Int, List<Episode>>> {
        Timber.d("Getting episodes for show with id $showId")

        return flow {
            val episodeUpdatesJsonString = sharedPreferences.getString(UPDATE_TIME_KEY, null)
            val episodeUpdates = if (episodeUpdatesJsonString != null) {
                Json.decodeFromString<Set<EpisodeUpdateJson>>(episodeUpdatesJsonString)
            } else {
                emptySet()
            }

            val currentTime = System.currentTimeMillis()
            val showUpdate = episodeUpdates.find { it.id == showId }

            if (showUpdate == null || currentTime - showUpdate.timestamp > UPDATE_PERIOD) {
                dao.clearForShow(showId)

                val seasons = tvMazeService.getSeasonsForShow(showId)
                seasons.forEach { season ->
                    val networkEpisodes = tvMazeService.getEpisodesForSeason(season.id)
                    dao.insertEpisodes(networkEpisodes.map { it.toEntity(showId) })
                }

                val updatedEpisodeUpdates = episodeUpdates.filter { it.id != showId }.toMutableSet()
                updatedEpisodeUpdates.add(EpisodeUpdateJson(showId, currentTime))

                sharedPreferences.edit().putString(
                    UPDATE_TIME_KEY,
                    Json.encodeToString(updatedEpisodeUpdates)
                ).apply()
            }

            val episodes = dao.getEpisodeByShowId(showId).first()
            emit(episodes.toBackboneMap())
        }
    }

    override fun getEpisode(id: Int): Flow<Episode?> {
        Timber.d("Getting episode with id $id")

        return flow {
            val episode = dao.getEpisodeById(id).first()?.toBackbone()
            emit(episode)
        }
    }

    override fun getCachedEpisodes(showId: Int): Flow<Map<Int, List<Episode>>> {
        Timber.d("Getting cached episodes for show with id $showId")

        return flow {
            emit(
                dao.getEpisodeByShowId(showId)
                    .first()
                    .toBackboneMap()
            )
        }
    }

    override fun clear(): Flow<Map<Int, Episode>> {
        Timber.d("Clearing episodes")

        sharedPreferences.edit().remove(UPDATE_TIME_KEY).apply()
        dao.clear()

        return flow {
            emit(emptyMap())
        }
    }

    companion object {
        private const val UPDATE_TIME_KEY = "episodes_update_time_key"
    }
}

@Serializable
data class EpisodeUpdateJson(
    val id: Int,
    val timestamp: Long,
)

private fun EpisodeResponse.toEntity(showId: Int): EpisodeEntity {
    return EpisodeEntity(
        apiId = id,
        showApiId = showId,
        name = name,
        season = season,
        number = number,
        summary = summary,
        imageMedium = imageData?.medium,
        imageOriginal = imageData?.original,
        rating = rating.average ?: 0f,
    )
}

private fun EpisodeEntity.toBackbone(): Episode {
    return Episode(
        id = id,
        name = name,
        number = number,
        summary = summary ?: "",
        image = ImageData(imageMedium ?: "", imageOriginal ?: ""),
        rating = rating,
    )
}

private fun List<EpisodeEntity>.toBackboneMap(): Map<Int, List<Episode>> {
    return this.groupBy { it.season }
        .mapValues { (_, episodes) ->
            episodes.map { it.toBackbone() }.sortedBy { it.number }
        }
}

internal class EpisodesRepositoryMock : EpisodesRepository {
    private val episodes = mapOf(
        1 to listOf(
            Episode(
                id = 1,
                name = "Pilot",
                number = 1,
                summary = "When the residents of Chester's Mill find themselves trapped under a massive transparent dome with no way out, they struggle to survive as resources rapidly dwindle and panic quickly escalates.",
                image = ImageData(
                    medium = "https://static.tvmaze.com/uploads/images/medium_landscape/1/4388.jpg",
                    original = "https://static.tvmaze.com/uploads/images/original_untouched/1/4388.jpg"
                ),
                rating = 6.9f
            ),
            Episode(
                id = 2,
                name = "The Fire",
                number = 2,
                summary = "While the residents of Chester's Mill face the uncertainty of life in the dome, panic is heightened when a house goes up in flames and their fire department is outside of the dome.",
                image = ImageData(
                    medium = "https://static.tvmaze.com/uploads/images/medium_landscape/1/4389.jpg",
                    original = "https://static.tvmaze.com/uploads/images/original_untouched/1/4389.jpg"
                ),
                rating = 6.5f
            ),
            Episode(
                id = 3,
                name = "Manhunt",
                number = 3,
                summary = "When a former deputy goes rogue, Big Jim recruits Barbie to join the manhunt to keep the town safe. Meanwhile, Junior is determined to escape the dome by going underground.",
                image = ImageData(
                    medium = "https://static.tvmaze.com/uploads/images/medium_landscape/1/4390.jpg",
                    original = "https://static.tvmaze.com/uploads/images/original_untouched/1/4390.jpg"
                ),
                rating = 6.3f
            )
        ),
        2 to listOf(
            Episode(
                id = 18,
                name = "Reconciliation",
                number = 5,
                summary = "Julia takes over as the leader of Chester's Mill after the town becomes divided in the wake of Big Jim and Rebecca's plans for population control. Meanwhile, Joe and Norrie help Melanie search for more clues about her identity at the Dome wall.",
                image = ImageData(
                    medium = "https://static.tvmaze.com/uploads/images/medium_landscape/4/10450.jpg",
                    original = "https://static.tvmaze.com/uploads/images/original_untouched/4/10450.jpg"
                ),
                rating = 6.0f
            ),
            Episode(
                id = 19,
                name = "In the Dark",
                number = 6,
                summary = "When Barbie and Sam set out to investigate a mysterious tunnel, a cave-in severs their path back to Chester's Mill. Meanwhile, Julia and Big Jim face off in a struggle for power as a dust storm rages in the town.",
                image = ImageData(
                    medium = "https://static.tvmaze.com/uploads/images/medium_landscape/4/10451.jpg",
                    original = "https://static.tvmaze.com/uploads/images/original_untouched/4/10451.jpg"
                ),
                rating = 6.1f
            ),
            Episode(
                id = 20,
                name = "Going Home",
                number = 7,
                summary = "When Barbie descends into the unknown abyss in the mysterious tunnel to look for Sam, he discovers a world that is familiar but filled with unanswered questions.",
                image = ImageData(
                    medium = "https://static.tvmaze.com/uploads/images/medium_landscape/4/10452.jpg",
                    original = "https://static.tvmaze.com/uploads/images/original_untouched/4/10452.jpg"
                ),
                rating = 6.0f
            ),
        )
    )

    override fun getEpisodes(showId: Int): Flow<Map<Int, List<Episode>>> {
        return flow { emit(episodes) }
    }

    override fun getEpisode(id: Int): Flow<Episode> {
        return flow {
            emit(episodes.values.flatten().first { it.id == id })
        }
    }

    override fun getCachedEpisodes(showId: Int): Flow<Map<Int, List<Episode>>> {
        return flow { emit(episodes) }
    }

    override fun clear(): Flow<Map<Int, Episode>> {
        return flow { emit(emptyMap()) }
    }
}