package com.caesar84mx.tvmaze.data.model.dao

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "episodes")
data class EpisodeEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val apiId: Int,
    val showApiId: Int,
    val name: String,
    val season: Int,
    val number: Int,
    val summary: String?,
    val imageMedium: String?,
    val imageOriginal: String?,
    val rating: Float,
)