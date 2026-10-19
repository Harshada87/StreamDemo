package com.example.streamdemo.ui.player

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.streamdemo.data.auth.AuthRepository
import com.example.streamdemo.data.repository.VideoRepository
import com.example.streamdemo.ui.navigation.Routes
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class PlayerViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: VideoRepository,
    private val authRepository: AuthRepository,
    @ApplicationContext context: Context
): ViewModel() {

    private val videoId: String = checkNotNull(savedStateHandle[Routes.VIDEO_ID])
    val player: ExoPlayer = ExoPlayer.Builder(context).build()

    private val _uiState = MutableStateFlow(PlayerUiState())
    val uiState: StateFlow<PlayerUiState> = _uiState.asStateFlow()

    private var resumeOnStart = false

    private val playerListener = object : Player.Listener{
        override fun onPlaybackStateChanged(playbackState: Int) {
            _uiState.update { it.copy(isBuffering = playbackState == Player.STATE_BUFFERING) }

        }

        override fun onPlayerError(error: PlaybackException) {
            _uiState.update { it.copy(error = error.errorCodeName, isBuffering = false) }
        }
    }

    init {
        player.addListener(playerListener)
        loadVideo()
    }

    private fun loadVideo(){
        viewModelScope.launch {
            val video = repository.getVideo(videoId)
            if(video == null){
                _uiState.update { it.copy(error = "Video not found", isBuffering = false) }
                return@launch
            }

            _uiState.update { it.copy(title = video.title) }
            player.setMediaItem(video.toMediaItem(authRepository.token.value))
            player.prepare()
            player.playWhenReady = true
        }
    }

    override fun onCleared() {
        player.removeListener(playerListener)
        player.release()
        super.onCleared()
    }

    fun onStop(){
        resumeOnStart = player.playWhenReady
        player.pause()
    }

    fun onStart(){
        if(resumeOnStart) player.play()
    }

}