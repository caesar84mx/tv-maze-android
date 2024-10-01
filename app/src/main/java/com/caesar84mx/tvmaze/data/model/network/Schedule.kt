package com.caesar84mx.tvmaze.data.model.network

import kotlinx.serialization.Serializable

@Serializable
data class Schedule(
    val time: String,
    val days: List<String>
)