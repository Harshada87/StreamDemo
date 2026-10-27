package com.example.streamdemo.data.repository

import com.example.streamdemo.data.model.Video
import com.example.streamdemo.data.remote.VideoApi
import com.example.streamdemo.data.remote.toVideoOrNull
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteVideoRepository @Inject constructor(
    private val videoApi: VideoApi
): VideoRepository{

    //Remember the last list, so opening the player doesn't download it again
    private var cachedVideos: List<Video>? = null

    override suspend fun getVideos(): List<Video> {
        val serverVideoDtos = videoApi.getVideos()
        val validVideos = serverVideoDtos.mapNotNull { videoDto -> videoDto.toVideoOrNull() }
        cachedVideos = validVideos
        return validVideos
    }

    override suspend fun getVideo(id: String): Video? {
        val videos = cachedVideos ?: getVideos()
        return videos.find { video ->  video.id == id
        }
    }
}