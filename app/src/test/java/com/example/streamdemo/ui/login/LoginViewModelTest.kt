package com.example.streamdemo.ui.login

import com.example.streamdemo.data.auth.InMemoryAuthRepository
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Test

class LoginViewModelTest{
    private lateinit var authRepository: InMemoryAuthRepository
    private lateinit var loginViewModel: LoginViewModel

    //Runs before EACH test, so every test starts fresh
    @Before
    fun setUp(){
        authRepository = InMemoryAuthRepository()
        loginViewModel = LoginViewModel(authRepository)
    }

    @Test
    fun `fingerprint success starts a session and logs in`(){
        //When
        loginViewModel.onBiometricResult(BiometricResult.Success)

        //Then
        assertTrue(loginViewModel.uiState.value.isLoggedIn)
        assertNotNull(authRepository.token.value)
        assertNull(loginViewModel.uiState.value.errorMessage)
    }
    @Test
    fun`cancel does not log in and shows no error`(){
        loginViewModel.onBiometricResult(BiometricResult.Cancelled)

        assertFalse(loginViewModel.uiState.value.isLoggedIn)
        assertNull(authRepository.token.value)
        assertNull(loginViewModel.uiState.value.errorMessage)
    }

    @Test
    fun`fingerprint error shows the error message`(){
        loginViewModel.onBiometricResult(BiometricResult.Error("Too many attempts"))

        assertFalse(loginViewModel.uiState.value.isLoggedIn)
        assertEquals("Too many attempts", loginViewModel.uiState.value.errorMessage)
    }

    @Test
    fun`no fingerprint on device disables login`(){
        loginViewModel.onFingerprintUnavailable()

        assertFalse(loginViewModel.uiState.value.isFingerprintAvailable)
        assertNotNull(loginViewModel.uiState.value.errorMessage)
    }
       
}