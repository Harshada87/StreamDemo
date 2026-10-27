package com.example.streamdemo.data.model

sealed interface VideoType {
    data object Hls : VideoType
    data object Dash : VideoType
    data object Live : VideoType
    data class Drm(val licenseUrl: String) : VideoType
}