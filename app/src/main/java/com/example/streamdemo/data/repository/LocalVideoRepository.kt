package com.example.streamdemo.data.repository

import com.example.streamdemo.data.model.Video
import com.example.streamdemo.data.model.VideoType
import javax.inject.Inject

class LocalVideoRepository @Inject constructor() : VideoRepository {
    private val videos = listOf(
        Video(
            id = "hls",
            title = "Apple sample (HLS)",
            url = "https://devstreaming-cdn.apple.com/videos/streaming/examples/img_bipbop_adv_example_fmp4/master.m3u8",
            type = VideoType.Hls
            ),
        Video(
            id = "dash",
            title = "Tears of Steel (DASH)",
            url = "https://storage.googleapis.com/wvmedia/clear/h264/tears/tears.mpd",
            type = VideoType.Dash
        ),
        Video(
            id = "drm",
            title = "Tears of Steel (Widevine DRM)",
            url = "https://storage.googleapis.com/wvmedia/cenc/h264/tears/tears.mpd",
            type = VideoType.Drm(licenseUrl = "https://proxy.uat.widevine.com/proxy?provider=widevine_test")
        ),
        Video(
            id = "live",
            title = "Live test stream (HLS)",
            url = "https://ireplay.tv/test/blender.m3u8",
            type = VideoType.Live
        )
    )

    override suspend fun getVideo(id: String): Video? = videos.find { it.id == id }

    override suspend fun getVideos(): List<Video>  = videos


}