package com.example.streamdemo.ui.player

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.ui.PlayerView

@Composable
fun PlayerScreen(
    modifier: Modifier = Modifier,
    viewModel: PlayerViewModel = hiltViewModel()
){
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    //Pause when the app goes to the background (home button, phone call)
    LifecycleEventEffect(Lifecycle.Event.ON_STOP) {
        viewModel.onStop()
    }
    LifecycleEventEffect(Lifecycle.Event.ON_START) {
        viewModel.onStart()
    }

    Box(modifier.fillMaxSize().background(Color.Black)){

        AndroidView(
            factory = {context ->
                PlayerView(context).apply { player = viewModel.player }
            },
            onRelease = { playerView -> playerView.player = null },
            modifier = Modifier.fillMaxSize()
        )

        Text(
            text = state.title,
            color = Color.White,
            modifier = Modifier.align(Alignment.TopStart).padding(16.dp)
        )

        if(state.isBuffering && state.error == null){
            CircularProgressIndicator(Modifier.align(Alignment.Center))
        }

        state.error?.let { error ->
            Text(
                text = "Playback error : $error",
                color = Color.Cyan,
                modifier = Modifier.align(Alignment.Center).padding(16.dp)
            )
        }
    }
}