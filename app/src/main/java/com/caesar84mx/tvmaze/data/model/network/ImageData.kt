package com.caesar84mx.tvmaze.data.model.network

import kotlinx.serialization.Serializable

@Serializable
data class ImageData(
    val medium: String,
    val original: String
)