package com.example.streamdemo.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class VideoDto(
    val id: String,
    val title: String,
    val url: String,
    val type: String,
    val licenseUrl: String? = null

)
