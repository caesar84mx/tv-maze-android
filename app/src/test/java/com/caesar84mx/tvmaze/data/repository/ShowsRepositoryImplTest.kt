package com.caesar84mx.tvmaze.data.repository

import android.content.SharedPreferences
import app.cash.turbine.test
import com.caesar84mx.tvmaze.data.dao.ShowDao
import com.caesar84mx.tvmaze.data.model.backbone.Show
import com.caesar84mx.tvmaze.data.model.dao.ShowEntity
import com.caesar84mx.tvmaze.data.model.network.ImageData
import com.caesar84mx.tvmaze.data.model.network.Rating
import com.caesar84mx.tvmaze.data.model.network.Schedule
import com.caesar84mx.tvmaze.data.model.network.ShowResponse
import com.caesar84mx.tvmaze.data.networking.TvMazeService
import com.caesar84mx.tvmaze.util.Constants.UPDATE_PERIOD
import io.kotest.matchers.shouldBe
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest

import org.junit.Before
import org.junit.Test

class ShowsRepositoryImplTest {
    private lateinit var mockApiService: TvMazeService
    private lateinit var mockShowDao: ShowDao
    private lateinit var mockSharedPreferences: SharedPreferences

    private lateinit var sut: ShowsRepositoryImpl

    @Before
    fun setUp() {
        mockApiService = mockk()
        mockShowDao = mockk {
            coEvery { clear() } just Runs
        }
        mockSharedPreferences = mockk(relaxed = true)

        sut = ShowsRepositoryImpl(
            tvMazeService = mockApiService,
            dao = mockShowDao,
            sharedPreferences = mockSharedPreferences
        )
    }

    @Test
    fun `getShows should fetch and cache shows when update period has expired`() = runTest {
        val currentTime = System.currentTimeMillis()
        every {
            mockSharedPreferences.getLong(
                any(),
                any()
            )
        } returns currentTime - UPDATE_PERIOD - 1
        coEvery { mockApiService.getShows() } returns listOf(
            mockResponse(1),
            mockResponse(2),
        )
        coEvery { mockShowDao.insertShows(any()) } returns Unit
        coEvery { mockShowDao.getShows() } returns flowOf(
            listOf(
                mockEntity(1),
                mockEntity(2),
            )
        )

        sut.getShows().test {
            awaitItem().toString().shouldBe(
                listOf(
                    mockShow(1),
                    mockShow(2),
                ).toString()
            )

            awaitComplete()
        }
    }

    @Test
    fun `getCachedShows should return cached shows`() = runTest {
        coEvery { mockShowDao.getShows() } returns flowOf(
            listOf(
                mockEntity(1),
                mockEntity(2),
            )
        )

        sut.getCachedShows().test {
            awaitItem().toString().shouldBe(
                listOf(
                    mockShow(1),
                    mockShow(2),
                ).toString()
            )

            awaitComplete()
        }
    }

    @Test
    fun `searchShowsByName should return shows matching the query`() = runTest {
        val query = "Show 1"
        coEvery { mockShowDao.getShowsByName(query) } returns flowOf(listOf(mockEntity(1)))

        sut.searchShowsByName(query).test {
            awaitItem().toString().shouldBe(listOf(mockShow(1)).toString())
            awaitComplete()
        }
    }

    @Test
    fun `getShow should return the show with the given ID`() = runTest {
        val showId = 1
        coEvery { mockShowDao.getShowById(showId) } returns flowOf(mockEntity(1))

        sut.getShow(showId).test {
            awaitItem()?.toString()?.shouldBe(mockShow(1).toString())
            awaitComplete()
        }
    }

    @Test
    fun `toggleShowFavorite should toggle the favorite status of the show`() = runTest {
        val showId = 1
        val isFavorite = true
        coEvery { mockShowDao.getShowById(showId) } returns flowOf(mockEntity(1))
        coEvery { mockShowDao.insertShows(any()) } returns Unit

        sut.toggleShowFavorite(showId, isFavorite).test {
            awaitItem()?.toString()?.shouldBe(mockShow(1, isFavorite).toString())
            awaitComplete()
        }
    }

    private fun mockShow(id: Int, isFavorite: Boolean = false): Show = Show(
        0,
        id,
        "Show $id",
        "",
        ImageData("", ""),
        emptyList(),
        Schedule("", listOf()),
        isFavorite,
        0f
    )

    private fun mockEntity(id: Int, isFavorite: Boolean = false): ShowEntity = ShowEntity(
        0,
        id,
        "Show $id",
        "",
        "",
        "",
        "",
        "",
        "",
        isFavorite,
        0f,
    )

    private fun mockResponse(id: Int): ShowResponse = ShowResponse(
        id,
        "Show $id",
        null,
        null,
        listOf(),
        Schedule("", listOf()),
        Rating(0f),
    )
}