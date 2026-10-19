package com.example.streamdemo.ui.player

data class PlayerUiState(
    val title: String = "",
    val isBuffering: Boolean = true,
    val error: String? = null
)
