package com.caesar84mx.tvmaze.data.model.backbone

import com.caesar84mx.tvmaze.data.model.network.ImageData
import com.caesar84mx.tvmaze.data.model.network.Schedule

data class Show(
    val localId: Int,
    val apiId: Int,
    val name: String,
    val summary: String,
    val images: ImageData,
    val genres: List<String>,
    val schedule: Schedule,
    val isFavorite: Boolean,
    val rating: Float,
)
