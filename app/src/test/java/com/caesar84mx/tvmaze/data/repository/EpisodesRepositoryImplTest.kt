package com.caesar84mx.tvmaze.data.repository

import android.content.SharedPreferences
import app.cash.turbine.test
import com.caesar84mx.tvmaze.data.model.backbone.Episode
import com.caesar84mx.tvmaze.data.dao.EpisodeDao
import com.caesar84mx.tvmaze.data.model.dao.EpisodeEntity
import com.caesar84mx.tvmaze.data.model.network.EpisodeResponse
import com.caesar84mx.tvmaze.data.model.network.ImageData
import com.caesar84mx.tvmaze.data.model.network.Rating
import com.caesar84mx.tvmaze.data.model.network.SeasonResponse
import com.caesar84mx.tvmaze.data.networking.TvMazeService
import com.caesar84mx.tvmaze.util.Constants.UPDATE_PERIOD
import io.kotest.matchers.shouldBe
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Before
import org.junit.Test

class EpisodesRepositoryImplTest {
    private lateinit var mockApiService: TvMazeService
    private lateinit var mockEpisodeDao: EpisodeDao
    private lateinit var mockSharedPreferences: SharedPreferences

    private lateinit var sut: EpisodesRepositoryImpl

    @Before
    fun setUp() {
        mockApiService = mockk()
        mockEpisodeDao = mockk {
            coEvery { clear() } just Runs
            coEvery { clearForShow(any()) } just Runs
        }
        mockSharedPreferences = mockk(relaxed = true)

        sut = EpisodesRepositoryImpl(
            tvMazeService = mockApiService,
            dao = mockEpisodeDao,
            sharedPreferences = mockSharedPreferences
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `getEpisodes should fetch and cache episodes when update period has expired`() = runTest {
        val showId = 1
        val seasonId = 1
        val launchTime = System.currentTimeMillis()

        every { mockSharedPreferences.getString(any(), any()) } returns null
        coEvery { mockApiService.getSeasonsForShow(showId) } returns listOf(mockSeasonResponse(1, seasonId))
        coEvery { mockApiService.getEpisodesForSeason(seasonId) } returns listOf(
            mockEpisodeResponse(1, seasonId, 1),
            mockEpisodeResponse(2, seasonId, 2),
        )
        coEvery { mockEpisodeDao.insertEpisodes(any()) } returns Unit
        coEvery { mockEpisodeDao.getEpisodeByShowId(showId) } returns flowOf(
            listOf(
                mockEpisodeEntity(1, 1, showId, seasonId, 1),
                mockEpisodeEntity(2, 2, showId, seasonId, 2),
            )
        )

        sut.getEpisodes(showId).test {
            awaitItem().shouldBe(
                mapOf(
                    1 to listOf(
                        mockEpisode(1, 1),
                        mockEpisode(2, 2),
                    )
                )
            )
            awaitComplete()
        }

        runCurrent()

        val expectedUpdates = setOf(EpisodeUpdateJson(showId, launchTime))
        val capturedUpdatesJson = slot<String>()

        verify { mockSharedPreferences.edit().putString(any(), capture(capturedUpdatesJson)) }
        val capturedUpdates = Json.decodeFromString<Set<EpisodeUpdateJson>>(capturedUpdatesJson.captured)
        capturedUpdates.size shouldBe expectedUpdates.size
        capturedUpdates.first().id shouldBe expectedUpdates.first().id
        val endTime = System.currentTimeMillis()
        (capturedUpdates.first().timestamp in launchTime..endTime).shouldBe(true)
    }

    @Test
    fun `getEpisodes should return cached episodes when update period has not expired`() = runTest {
        val showId = 1
        val currentTime = System.currentTimeMillis()
        val previousUpdateTime = currentTime - UPDATE_PERIOD / 2
        val episodeUpdatesJsonString = Json.encodeToString(setOf(EpisodeUpdateJson(showId, previousUpdateTime)))

        every { mockSharedPreferences.getString(any(), any()) } returns episodeUpdatesJsonString
        coEvery { mockEpisodeDao.getEpisodeByShowId(showId) } returns flowOf(
            listOf(
                mockEpisodeEntity(1, 1, showId, 1, 1),
                mockEpisodeEntity(2, 2, showId, 1, 2)
            )
        )

        sut.getEpisodes(showId).test {
            awaitItem().shouldBe(
                mapOf(
                    1 to listOf(
                        mockEpisode(1, 1),
                        mockEpisode(2, 2)
                    )
                )
            )
            awaitComplete()
        }

        coVerify(exactly = 0) { mockApiService.getSeasonsForShow(any()) }
        coVerify(exactly = 0) { mockApiService.getEpisodesForSeason(any()) }
    }

    @Test
    fun `getEpisode should return the episode with the given ID`() = runTest {
        val episodeId = 1
        val showId = 1
        val seasonId = 1
        val episodeNumber = 1

        coEvery { mockEpisodeDao.getEpisodeById(episodeId) } returns flowOf(
            mockEpisodeEntity(episodeId, 1, showId, seasonId, episodeNumber)
        )

        sut.getEpisode(episodeId).test {
            awaitItem()?.shouldBe(mockEpisode(episodeId, episodeNumber))
            awaitComplete()
        }
    }

    @Test
    fun `getCachedEpisodes should return cached episodes for the given show ID`() = runTest {
        val showId = 1
        val seasonId = 1

        coEvery { mockEpisodeDao.getEpisodeByShowId(showId) } returns flowOf(
            listOf(
                mockEpisodeEntity(1, 1, showId, seasonId, 1),
                mockEpisodeEntity(2, 2, showId, seasonId, 2)
            )
        )

        sut.getCachedEpisodes(showId).test {
            awaitItem().shouldBe(
                mapOf(
                    seasonId to listOf(
                        mockEpisode(1, 1),
                        mockEpisode(2, 2)
                    )
                )
            )
            awaitComplete()
        }
    }

    @Test
    fun `clear should clear episodes and update timestamp`() = runTest {
        coEvery { mockEpisodeDao.clear() } just Runs

        sut.clear().test {
            awaitItem().shouldBe(emptyMap())
            awaitComplete()
        }

        verify { mockSharedPreferences.edit().remove(any()) }
    }

    private fun mockEpisode(id: Int, number: Int): Episode = Episode(
        id = id,
        name = "Episode $id",
        number = number,
        summary = "",
        image = ImageData("", ""),
        rating = 0f
    )

    private fun mockEpisodeEntity(id: Int, apiId: Int, showId: Int, season: Int, number: Int): EpisodeEntity = EpisodeEntity(
        id = id,
        apiId = apiId,
        showApiId = showId,
        name = "Episode $id",
        season = season,
        number = number,
        summary = "",
        imageMedium = "",
        imageOriginal = "",
        rating = 0f
    )

    private fun mockEpisodeResponse(id: Int, season: Int, number: Int): EpisodeResponse = EpisodeResponse(
        id = id,
        name = "Episode $id",
        season = season,
        number = number,
        summary = "",
        imageData = null,
        rating = Rating(0f)
    )

    private fun mockSeasonResponse(id: Int, number: Int) = SeasonResponse(id, number)
}