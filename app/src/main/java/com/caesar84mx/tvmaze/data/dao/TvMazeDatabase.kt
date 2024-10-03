package com.caesar84mx.tvmaze.data.dao

import androidx.room.Database
import androidx.room.RoomDatabase
import com.caesar84mx.tvmaze.data.model.dao.EpisodeEntity
import com.caesar84mx.tvmaze.data.model.dao.ShowEntity

@Database(entities = [ShowEntity::class, EpisodeEntity::class], version = 1, exportSchema = false)
abstract class TvMazeDatabase: RoomDatabase() {
    abstract fun showDao(): ShowDao
    abstract fun episodeDao(): EpisodeDao
}