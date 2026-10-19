package com.example.streamdemo.ui.login

data class LoginUiState(
    val isFingerprintAvailable: Boolean = true,
    val errorMessage: String? = null,
    val isLoggedIn: Boolean = false
)
