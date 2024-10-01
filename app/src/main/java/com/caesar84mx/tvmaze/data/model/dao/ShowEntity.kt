package com.caesar84mx.tvmaze.data.model.dao

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shows")
data class ShowEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "api_id") val apiId: Int,
    val name: String,
    val summary: String,
    @ColumnInfo(name = "image_medium") val imageMedium: String,
    @ColumnInfo(name = "image_original") val imageOriginal: String,
    @ColumnInfo(name = "genres") val genresString: String?,
    @ColumnInfo(name = "schedule_time") val scheduleTime: String,
    @ColumnInfo(name = "schedule_days") val scheduleDaysString: String,
    @ColumnInfo(name = "is_favorite") val isFavorite: Boolean,
    val rating: Float,
)
