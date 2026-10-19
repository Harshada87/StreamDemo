package com.example.streamdemo.ui.videoList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.streamdemo.data.repository.VideoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class VideoListViewModel @Inject constructor(
    private val repository: VideoRepository
): ViewModel(){

    private val _uiState = MutableStateFlow<VideoListUiState>(VideoListUiState.Loading)

    val uiState : StateFlow<VideoListUiState> = _uiState.asStateFlow()

    init {
        loadVideo()
    }

    fun loadVideo(){
        viewModelScope.launch {
            _uiState.value = VideoListUiState.Loading
            _uiState.value =
                try {
                    VideoListUiState.Success(repository.getVideos())
                }
                catch (e: Exception){
                    VideoListUiState.Error(e.message?: "Could not load videos")
                }
        }
    }
}