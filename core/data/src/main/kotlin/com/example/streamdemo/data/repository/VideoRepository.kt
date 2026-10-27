package com.example.streamdemo.data.repository

import com.example.streamdemo.data.model.Video

interface VideoRepository {
    suspend fun getVideos(): List<Video>
    suspend fun getVideo(id: String): Video?
}