package com.example.streamdemo.ui.login

import androidx.lifecycle.ViewModel
import com.example.streamdemo.data.auth.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel()
{
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    //Called by the screen if device has no usable fingerprint
    fun onFingerprintUnavailable(){
        _uiState.update {
            currentState ->
            currentState.copy(
                isFingerprintAvailable = false,
                errorMessage = "Fingerprint is not set up on this device"
            )
        }
    }

    //Called by the screen when the fingerprint dialog finishes
    fun onBiometricResult(biometricResult: BiometricResult){
        when(biometricResult){

            BiometricResult.Success -> {
                authRepository.startSession()
                _uiState.update { currentState ->
                    currentState.copy(isLoggedIn = true, errorMessage = null)
                }
            }

            BiometricResult.Cancelled -> {
                _uiState.update { currentState ->
                    currentState.copy(errorMessage = null)
                }
            }

         is   BiometricResult.Error -> {
             _uiState.update { currentState ->
                 currentState.copy(errorMessage = biometricResult.errorMessage)
             }

            }
        }
    }

}