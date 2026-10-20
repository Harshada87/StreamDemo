package com.example.streamdemo.data.remote

import com.example.streamdemo.data.model.Video
import com.example.streamdemo.data.model.VideoType

fun VideoDto.toVideoOrNull(): Video?{

    val videoType: VideoType = when (type.lowercase()){
        "hls" -> VideoType.Hls
        "dash" -> VideoType.Dash
        "live" -> VideoType.Live
        "drm" -> {
            // A DRM video without a license URL can never play, so reject it
            val drmLicenseUrl = licenseUrl ?: return null
            VideoType.Drm(drmLicenseUrl)
        }
        else -> return null  //Unknown type from the server : skip it

    }

    return Video(
        id = id,
        title = title,
        url = url,
        type = videoType
    )
}