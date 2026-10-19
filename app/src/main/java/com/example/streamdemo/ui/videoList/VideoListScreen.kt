package com.example.streamdemo.ui.videoList

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.streamdemo.data.model.VideoType

@Composable
fun VideoListScreen(
    onVideoClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: VideoListViewModel = hiltViewModel()
){
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    VideoListContent(
        state = state,
        onVideoClick = onVideoClick,
        onRetry = viewModel::loadVideo,
        modifier = modifier
    )
}

@Composable
fun VideoListContent(
    state: VideoListUiState,
    onVideoClick: (String) -> Unit,
    onRetry : () -> Unit,
    modifier: Modifier = Modifier
){
    when (state){
        VideoListUiState.Loading -> {
            Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center){
                CircularProgressIndicator()
            }
        }

       is VideoListUiState.Error -> {
           Column (modifier.fillMaxSize(),
               verticalArrangement = Arrangement.Center,
               horizontalAlignment = Alignment.CenterHorizontally)
           {
               Text(state.message)
               Spacer(Modifier.height(16.dp))
               Button(onClick = onRetry) {
                   Text("Retry")
               }
           }
        }

        is VideoListUiState.Success -> {
            LazyColumn(
                modifier.fillMaxSize()
            ) {
                items(state.videos, key = {it.id})
                {
                    video ->
                    ListItem(
                        headlineContent = {Text(video.title)},
                        supportingContent = { Text(video.type.label()) },
                        modifier = Modifier.clickable{onVideoClick(video.id)}
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}

private fun VideoType.label(): String = when (this){
    VideoType.Hls -> "HLS"
    VideoType.Dash -> "DASH"
    VideoType.Live -> "LIVE"
    is VideoType.Drm -> "DRM (Widevine)"
}