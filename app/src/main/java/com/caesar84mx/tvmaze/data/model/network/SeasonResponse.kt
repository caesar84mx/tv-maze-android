package com.caesar84mx.tvmaze.data.model.network

import kotlinx.serialization.Serializable

@Serializable
data class SeasonResponse(
    val id: Int,
    val number: Int,
)
