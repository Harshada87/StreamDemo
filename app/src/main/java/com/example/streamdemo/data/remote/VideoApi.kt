package com.example.streamdemo.data.remote

import retrofit2.http.GET

interface VideoApi {
    @GET("refs/heads/main/app/videos.json")
    suspend fun getVideos(): List<VideoDto>
}