package com.caesar84mx.tvmaze.data.networking

import com.caesar84mx.tvmaze.data.model.network.EpisodeResponse
import com.caesar84mx.tvmaze.data.model.network.SeasonResponse
import com.caesar84mx.tvmaze.data.model.network.ShowResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface TvMazeService {
    @GET("/shows")
    suspend fun getShows(): List<ShowResponse>

    @GET("/shows/{showId}/seasons")
    suspend fun getSeasonsForShow(@Path("showId") showId: Int): List<SeasonResponse>

    @GET("/shows/{seasonId}/episodes")
    suspend fun getEpisodesForSeason(@Path("seasonId") seasonId: Int): List<EpisodeResponse>
}