package com.caesar84mx.tvmaze.data.model.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EpisodeResponse(
    val id: Int,
    val name: String,
    val season: Int,
    val number: Int,
    val summary: String?,
    @SerialName("image") val imageData: ImageData?,
    val rating: Rating,
)
