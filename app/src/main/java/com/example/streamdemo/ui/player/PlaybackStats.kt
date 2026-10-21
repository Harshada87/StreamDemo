package com.example.streamdemo.ui.player

data class PlaybackStats(
    val timeToFirstFramesMs: Long? = null,
    val videoREsolution: String? = "-",
    val videoBitrateKbps: Int? = null,
    val rebufferCount:Int = 0,
    val droppedFrameCount: Int = 0,
    val lastErrorCode:String? = null
)
