package com.example.streamdemo.ui.player

import android.os.SystemClock
import androidx.media3.common.Format
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.DecoderReuseEvaluation
import androidx.media3.exoplayer.analytics.AnalyticsListener
import dagger.hilt.android.UnstableApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@androidx.media3.common.util.UnstableApi
@OptIn(UnstableApi::class)
class PlaybackStatsTracker : AnalyticsListener{
    private val _playbackStats = MutableStateFlow(PlaybackStats())
    val playbackStats: StateFlow<PlaybackStats> = _playbackStats.asStateFlow()

    private var playRequestedAtMs: Long = 0L
    private var firstFramedRendered = false

    //Call this right before player.prepare() to start the "wait" clock
    fun onPlaybackRequested(){
        playRequestedAtMs = SystemClock.elapsedRealtime()
        firstFramedRendered = false
        _playbackStats.value = PlaybackStats()
    }

    //1. Time to first frame: the picture finally appeared
    override fun onRenderedFirstFrame(eventTime: AnalyticsListener.EventTime, output: Any, renderTimeMs: Long) {
        if(firstFramedRendered) return
        firstFramedRendered = true
        val waitedMs = SystemClock.elapsedRealtime() - playRequestedAtMs
        _playbackStats.update { currentStats -> currentStats.copy(timeToFirstFramesMs = waitedMs) }
    }

    //2. Resolution + bitrate: ABR picked a new quality
    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    override fun onVideoInputFormatChanged(
        eventTime: AnalyticsListener.EventTime,
        format: Format,
        decoderReuseEvaluation: DecoderReuseEvaluation?
    ) {
        _playbackStats.update { currentState ->
            currentState.copy(
                videoREsolution = "${format.width} x ${format.height}",
                videoBitrateKbps = if(format.bitrate > 0) format.bitrate / 100 else null
            )
        }
    }

    //3. Rebuffer: buffering AFTER playback started(the first load doesn't count)
    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    override fun onPlaybackStateChanged(eventTime: AnalyticsListener.EventTime, state: Int) {
        if(state == Player.STATE_BUFFERING && firstFramedRendered){
            _playbackStats.update { currentStats ->
                currentStats.copy(rebufferCount = currentStats.rebufferCount + 1)
            }
        }
    }

    //4. Dropped frames : the device couldn't keep up
    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    override fun onDroppedVideoFrames(eventTime: AnalyticsListener.EventTime, droppedFrames: Int, elapsedMs: Long) {
        _playbackStats.update {
            currentStats ->
            currentStats.copy(droppedFrameCount = currentStats.droppedFrameCount + droppedFrames)
        }
    }


    //5. Error: which layer failed
    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    override fun onPlayerError(eventTime: AnalyticsListener.EventTime, error: PlaybackException){
        _playbackStats.update { currentStats -> currentStats.copy(lastErrorCode =  error.errorCodeName) }
    }




}