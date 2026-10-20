package com.example.streamdemo.ui.videolist

import com.example.streamdemo.MainDispatcherRule
import com.example.streamdemo.fake.FakeVideoRepository
import com.example.streamdemo.ui.videoList.VideoListUiState
import com.example.streamdemo.ui.videoList.VideoListViewModel
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class VideoListViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `when repository returns videos, state is Success with all videos`() = runTest {
        //Given: a repository that works
        val fakeVideoRepository = FakeVideoRepository()

        //When : the ViewModel is created (it loads in init)
        val videoListViewModel = VideoListViewModel(fakeVideoRepository)
        advanceUntilIdle()

        //Then : state is Success with 4 videos
        val currentState = videoListViewModel.uiState.value
        assertTrue(currentState is VideoListUiState.Success)
        assertEquals(4,(currentState as VideoListUiState.Success).videos.size)
    }

    @Test
    fun `when repository fails, state is Error with message`() = runTest {
        //Given: a repository that simulates no network
        val fakeVideoRepository = FakeVideoRepository(shouldFail = true)

        //When
        val videoListViewModel = VideoListViewModel(fakeVideoRepository)
        advanceUntilIdle()

        //Then
        val currentState = videoListViewModel.uiState.value
        assertTrue(currentState is VideoListUiState.Error)
        assertEquals("Network unavailable", (currentState as VideoListUiState.Error).message)
    }

    @Test
    fun `retry after failure loads videos successfully`() = runTest {
        //Given: first load fails
        val fakeVideoRepository = FakeVideoRepository(shouldFail = true)
        val videoListViewModel = VideoListViewModel(fakeVideoRepository)
        advanceUntilIdle()

        //When: network comes back and user taps Retry
        fakeVideoRepository.shouldFail = false
        videoListViewModel.loadVideo()
        advanceUntilIdle()

        //Then
        assertTrue(videoListViewModel.uiState.value is VideoListUiState.Success)
    }













}