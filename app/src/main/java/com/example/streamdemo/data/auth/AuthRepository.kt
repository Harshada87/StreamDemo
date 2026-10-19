package com.example.streamdemo.data.auth

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

interface AuthRepository {
    val token: StateFlow<String?>
    fun startSession(): String
    fun endSession()
}

@Singleton
class InMemoryAuthRepository @Inject constructor() : AuthRepository{
    private val _token = MutableStateFlow<String?>(null)
    override val token: StateFlow<String?> = _token.asStateFlow()

    override fun startSession(): String {
        val newToken = UUID.randomUUID().toString()
        _token.value = newToken
        return newToken
    }

    override fun endSession() {
        _token.value = null
    }
}