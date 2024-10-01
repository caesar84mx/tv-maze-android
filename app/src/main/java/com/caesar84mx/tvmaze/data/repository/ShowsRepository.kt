package com.caesar84mx.tvmaze.data.repository

import android.content.SharedPreferences
import com.caesar84mx.tvmaze.data.dao.ShowDao
import com.caesar84mx.tvmaze.data.model.backbone.Show
import com.caesar84mx.tvmaze.data.model.dao.ShowEntity
import com.caesar84mx.tvmaze.data.model.network.ImageData
import com.caesar84mx.tvmaze.data.model.network.Schedule
import com.caesar84mx.tvmaze.data.model.network.ShowResponse
import com.caesar84mx.tvmaze.data.networking.TvMazeService
import com.caesar84mx.tvmaze.util.Constants.UPDATE_PERIOD
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import timber.log.Timber

interface ShowsRepository {
    fun getShows(forceRefresh: Boolean = false): Flow<List<Show>>
    fun getCachedShows(): Flow<List<Show>>
    fun searchShowsByName(query: String): Flow<List<Show>>
    fun getShow(id: Int): Flow<Show?>
    fun toggleShowFavorite(id: Int, isFavorite: Boolean): Flow<Show?>
}

internal class ShowsRepositoryImpl(
    private val tvMazeService: TvMazeService,
    private val dao: ShowDao,
    private val sharedPreferences: SharedPreferences,
) : ShowsRepository {
    override fun getShows(forceRefresh: Boolean): Flow<List<Show>> {
        Timber.d("Getting shows, force refresh: $forceRefresh")

        return flow {
            val currentTime = System.currentTimeMillis()
            val lastUpdateTime = sharedPreferences.getLong(UPDATE_TIME_KEY, 0)

            if ((currentTime - lastUpdateTime > UPDATE_PERIOD) || forceRefresh) {
                dao.clear()
                val networkShows = tvMazeService.getShows()
                dao.insertShows(networkShows.map { it.toEntity() })
                sharedPreferences.edit().putLong(UPDATE_TIME_KEY, currentTime).apply()
            }

            emit(
                dao.getShows()
                    .first()
                    .map { it.toBackbone() }
            )
        }
    }

    override fun getCachedShows(): Flow<List<Show>> {
        Timber.d("Getting cached shows")
        return flow {
            emit(
                dao.getShows()
                    .first()
                    .map { it.toBackbone() }
            )
        }
    }

    override fun getShow(id: Int): Flow<Show?> = flow {
        Timber.d("Getting show with id $id")
        val dbShow = dao.getShowById(id).firstOrNull()
        emit(dbShow?.toBackbone())
    }

    override fun toggleShowFavorite(id: Int, isFavorite: Boolean): Flow<Show?> = flow {
        Timber.d("Toggling show with id $id to $isFavorite")
        dao.getShowById(id).firstOrNull()?.let { showEntity ->
            val newEntity = showEntity.copy(isFavorite = isFavorite)
            dao.insertShows(listOf(newEntity))
            emit(newEntity.toBackbone())
        } ?: emit(null)
    }

    override fun searchShowsByName(query: String): Flow<List<Show>> = flow {
        Timber.d("Searching shows by name $query")
        emit(
            dao.getShowsByName(query)
                .first()
                .map { it.toBackbone() }
        )
    }

    companion object {
        private const val UPDATE_TIME_KEY = "shows_update_time_key"
    }
}

private fun ShowResponse.toEntity(): ShowEntity {
    val genresString = genres.joinToString(",")
    val scheduleDaysString = schedule.days.joinToString(",")
    return ShowEntity(
        id = 0,
        apiId = id,
        name = name,
        summary = summary ?: "",
        imageMedium = imageData?.medium ?: "",
        imageOriginal = imageData?.original ?: "",
        genresString = genresString,
        scheduleTime = schedule.time,
        scheduleDaysString = scheduleDaysString,
        isFavorite = false,
        rating = rating.average ?: 0f
    )
}

private fun ShowEntity.toBackbone(): Show {
    val genres = genresString?.split(",") ?: emptyList()
    val scheduleDays = scheduleDaysString.split(",")
    return Show(
        localId = id,
        apiId = apiId,
        name = name,
        summary = summary,
        images = ImageData(imageMedium, imageOriginal),
        genres = genres,
        schedule = Schedule(scheduleTime, scheduleDays),
        isFavorite = isFavorite,
        rating = rating
    )
}

internal class ShowsRepositoryMock : ShowsRepository {
    private val shows = listOf(
        Show(
            localId = 0,
            apiId = 1,
            name = "Under the Dome",
            summary = """<p><b>Under the Dome</b> is the story of a small town that is suddenly and inexplicably sealed off from the rest of the world by an enormous transparent dome. The town's inhabitants must deal with surviving the post-apocalyptic conditions while searching for answers about the dome, where it came from and if and when it will go away.</p>""",
            images = ImageData(
                medium = "https://static.tvmaze.com/uploads/images/medium_portrait/81/202627.jpg",
                original = "https://static.tvmaze.com/uploads/images/original_untouched/81/202627.jpg",
            ),
            genres = listOf(
                "Drama",
                "Science-Fiction",
                "Thriller",
            ),
            schedule = Schedule("22:00", listOf("Monday")),
            isFavorite = true,
            rating = 8.9f,
        ),
        Show(
            localId = 2,
            apiId = 2,
            name = "Person of Interest",
            summary = """<p>You are being watched. The government has a secret system, a machine that spies on you every hour of every day. I know because I built it. I designed the Machine to detect acts of terror but it sees everything. Violent crimes involving ordinary people. People like you. Crimes the government considered "irrelevant". They wouldn't act so I decided I would. But I needed a partner. Someone with the skills to intervene. Hunted by the authorities, we work in secret. You'll never find us. But victim or perpetrator, if your number is up, we'll find you.</p>""",
            images = ImageData(
                medium = "https://static.tvmaze.com/uploads/images/medium_portrait/163/407679.jpg",
                original = "https://static.tvmaze.com/uploads/images/original_untouched/163/407679.jpg",
            ),
            genres = listOf(
                "Action",
                "Crime",
                "Science-Fiction",
            ),
            schedule = Schedule("22:00", listOf("Tuesday")),
            isFavorite = false,
            rating = 3.2f,
        ),
        Show(
            localId = 3,
            apiId = 3,
            name = "Bitten",
            summary = """<p>Based on the critically acclaimed series of novels from Kelley Armstrong. Set in Toronto and upper New York State, <b>Bitten</b> follows the adventures of 28-year-old Elena Michaels, the world's only female werewolf. An orphan, Elena thought she finally found her "happily ever after" with her new love Clayton, until her life changed forever. With one small bite, the normal life she craved was taken away and she was left to survive life with the Pack.</p>""",
            images = ImageData(
                medium = "https://static.tvmaze.com/uploads/images/medium_portrait/0/15.jpg",
                original = "https://static.tvmaze.com/uploads/images/original_untouched/0/15.jpg"
            ),
            genres = listOf(
                "Drama",
                "Horror",
                "Romance",
            ),
            schedule = Schedule("22:00", listOf("Friday")),
            isFavorite = false,
            rating = 7.8f,
        ),
        Show(
            localId = 4,
            apiId = 4,
            name = "Arrow",
            summary = """<p>After a violent shipwreck, billionaire playboy Oliver Queen was missing and presumed dead for five years before being discovered alive on a remote island in the Pacific. He returned home to Starling City, welcomed by his devoted mother Moira, beloved sister Thea and former flame Laurel Lance. With the aid of his trusted chauffeur/bodyguard John Diggle, the computer-hacking skills of Felicity Smoak and the occasional, reluctant assistance of former police detective, now beat cop, Quentin Lance, Oliver has been waging a one-man war on crime.</p>""",
            images = ImageData(
                medium = "https://static.tvmaze.com/uploads/images/medium_portrait/143/358967.jpg",
                original = "https://static.tvmaze.com/uploads/images/original_untouched/143/358967.jpg"
            ),
            genres = listOf(
                "Drama",
                "Action",
                "Science-Fiction",
            ),
            schedule = Schedule("21:00", listOf("Tuesday")),
            isFavorite = true,
            rating = 9f,
        ),
        Show(
            localId = 5,
            apiId = 5,
            name = "True Detective",
            summary = """<p>Touch darkness and darkness touches you back. <b>True Detective</b> centers on troubled cops and the investigations that drive them to the edge. Each season features a new cast and a new case.</p><p><i><b>True Detective</b></i> is an American anthology crime drama television series created and written by Nic Pizzolatto. </p>""",
            images = ImageData(
                medium = "https://static.tvmaze.com/uploads/images/medium_portrait/490/1226764.jpg",
                original = "https://static.tvmaze.com/uploads/images/original_untouched/490/1226764.jpg"
            ),
            genres = listOf(
                "Drama",
                "Crime",
                "Thriller",
            ),
            schedule = Schedule("21:00", listOf("Sunday")),
            isFavorite = false,
            rating = 7.4f,
        ),
    )

    override fun getShows(forceRefresh: Boolean): Flow<List<Show>> {
        return flow {
            emit(shows)
        }
    }

    override fun getCachedShows(): Flow<List<Show>> {
        return flow {
            emit(shows)
        }
    }

    override fun searchShowsByName(query: String): Flow<List<Show>> {
        return flow {
            emit(shows.filter { it.name.contains(query) })
        }
    }

    override fun getShow(id: Int): Flow<Show?> {
        return flow { emit(shows.find { it.localId == id }) }
    }

    override fun toggleShowFavorite(id: Int, isFavorite: Boolean): Flow<Show?> {
        return flow {
            emit(shows.find { it.localId == id }?.copy(isFavorite = isFavorite))
        }
    }

}
