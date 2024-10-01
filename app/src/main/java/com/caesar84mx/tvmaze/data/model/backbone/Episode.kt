package com.caesar84mx.tvmaze.data.model.backbone

import com.caesar84mx.tvmaze.data.model.network.ImageData

data class Episode(
    val id: Int,
    val name: String,
    val number: Int,
    val summary: String,
    val image: ImageData,
    val rating: Float,
)