package com.example.streamdemo.ui.login

sealed interface BiometricResult {
    data object Success : BiometricResult
    data object Cancelled : BiometricResult
    data class Error(val errorMessage: String) : BiometricResult
}