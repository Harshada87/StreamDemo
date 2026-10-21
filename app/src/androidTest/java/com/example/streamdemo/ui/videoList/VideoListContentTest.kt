package com.example.streamdemo.ui.videoList

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.streamdemo.data.model.Video
import com.example.streamdemo.data.model.VideoType
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Rule
import org.junit.Test

class VideoListContentTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private val sampleVideos = listOf(
        Video("hls", "Test HLS", "https://example.com/hls.m3u8", VideoType.Hls),
        Video("drm", "Test DRM", "https://example.com/drm.mpd", VideoType.Drm("https://example.com/license"))
    )

    @Test
    fun successState_showEveryVideoTitle(){
        composeTestRule.setContent {
            VideoListContent(
                state = VideoListUiState.Success(sampleVideos),
                onVideoClick = {},
                onRetry = {}
            )
        }

        composeTestRule.onNodeWithText("Test HLS").assertIsDisplayed()
        composeTestRule.onNodeWithText("Test DRM").assertIsDisplayed()
    }

    @Test
    fun clickingVideo_sendsItsId(){
        var clickedVideoId: String? = null

        composeTestRule.setContent {
            VideoListContent(
                state = VideoListUiState.Success(sampleVideos),
                onVideoClick = {videoId -> clickedVideoId = videoId},
                onRetry = {}
            )
        }
        composeTestRule.onNodeWithText("Test DRM").performClick()
        assertEquals("drm", clickedVideoId)


    }
    @Test
    fun errorState_retryButtonCallsRetry(){
        var retryWasClicked = false

        composeTestRule.setContent {
            VideoListContent(
                state = VideoListUiState.Error("Network unavailable"),
                onVideoClick = {},
                onRetry = {retryWasClicked = true}
            )
        }
        composeTestRule.onNodeWithText("Network unavailable").assertIsDisplayed()
        composeTestRule.onNodeWithText("Retry").performClick()
        assertTrue(retryWasClicked)
    }



















}