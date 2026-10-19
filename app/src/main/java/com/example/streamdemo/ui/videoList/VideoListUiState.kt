package com.example.streamdemo.ui.videoList

import com.example.streamdemo.data.model.Video

sealed interface VideoListUiState {
    data object Loading : VideoListUiState
    data class Success(val videos: List<Video>) : VideoListUiState
    data class Error(val message: String) : VideoListUiState
}