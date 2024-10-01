package com.caesar84mx.tvmaze.data.model.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ShowResponse(
    val id: Int,
    val name: String,
    val summary: String?,
    @SerialName("image") val imageData: ImageData?,
    val genres: List<String>,
    val schedule: Schedule,
    val rating: Rating,
)
