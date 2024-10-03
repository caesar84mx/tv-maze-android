package com.caesar84mx.tvmaze.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.caesar84mx.tvmaze.data.model.dao.EpisodeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EpisodeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEpisodes(episodes: List<EpisodeEntity>)

    @Query("SELECT * FROM episodes")
    fun getEpisodes(): Flow<List<EpisodeEntity>>

    @Query("SELECT * FROM episodes WHERE showApiId = :showId")
    fun getEpisodeByShowId(showId: Int): Flow<List<EpisodeEntity>>

    @Query("SELECT * FROM episodes WHERE id = :id")
    fun getEpisodeById(id: Int): Flow<EpisodeEntity?>

    @Query("DELETE FROM episodes WHERE showApiId = :showId")
    fun clearForShow(showId: Int)

    @Query("DELETE FROM episodes")
    fun clear()
}