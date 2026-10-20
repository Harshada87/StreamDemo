package com.example.streamdemo.fake

import com.example.streamdemo.data.model.Video
import com.example.streamdemo.data.model.VideoType
import com.example.streamdemo.data.repository.VideoRepository
import okio.IOException

class FakeVideoRepository (
    private val videos: List<Video> = sampleVideos,
    var shouldFail: Boolean = false
): VideoRepository{

    override suspend fun getVideos(): List<Video> {
        if(shouldFail) throw IOException("Network unavailable")
        return videos
    }

    override suspend fun getVideo(id: String): Video? =
        getVideos().find { video -> video.id == id }

        companion object {
            val sampleVideos = listOf(
                Video("hls", "Test HLS", "https://example.com/hls.m3u8", VideoType.Hls),
                Video("dash", "Test DASH", "https://example.com/dash.mpd", VideoType.Dash),
                Video("drm", "Test DRM", "https://example.com/drm.mpd", VideoType.Drm("https://example.com/license")),
                Video("live", "Test Live", "https://example.com/live.m3u8", VideoType.Live)
            )
        }

    }

