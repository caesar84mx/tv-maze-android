package com.caesar84mx.tvmaze.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.caesar84mx.tvmaze.data.model.dao.ShowEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ShowDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShows(shows: List<ShowEntity>)

    @Query("SELECT * FROM shows")
    fun getShows(): Flow<List<ShowEntity>>

    @Query("SELECT * FROM shows WHERE name LIKE '%' || :query || '%'")
    fun getShowsByName(query: String): Flow<List<ShowEntity>>

    @Query("SELECT * FROM shows WHERE id = :id")
    fun getShowById(id: Int): Flow<ShowEntity>

    @Query("DELETE FROM shows")
    suspend fun clear()
}
