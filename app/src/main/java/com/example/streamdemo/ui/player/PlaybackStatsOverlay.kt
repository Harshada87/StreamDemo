package com.example.streamdemo.ui.player

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun PlaybackStatsOverlay(
    playbackStats: PlaybackStats,
    modifier: Modifier = Modifier
){
    Column(
        modifier = modifier
            .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
            .padding(8.dp)
    ) {
        val statLines = listOf(
            "Start time: ${playbackStats.timeToFirstFramesMs?.let {"$it ms"} ?: ""}",
            "Quality: ${playbackStats.videoREsolution}" +
                    (playbackStats.videoBitrateKbps?.let { " @ $it kbps" } ?: ""),
            "Rebuffers: ${playbackStats.rebufferCount}",
            "Dropped frames: ${playbackStats.droppedFrameCount}",
            "Error: ${playbackStats.lastErrorCode ?: "none"}"
        )
        statLines.forEach { statLine ->
            Text(statLine, color = Color.White, style = MaterialTheme.typography.labelSmall)
        }
    }
}