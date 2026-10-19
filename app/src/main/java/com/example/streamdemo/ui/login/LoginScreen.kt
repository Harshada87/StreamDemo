package com.example.streamdemo.ui.login

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = hiltViewModel()
){
    val loginUiState by viewModel.uiState.collectAsStateWithLifecycle()

    //Get the Activity (MainActivity is a FragmentActivity)
    val fragmentActivity = LocalActivity.current as FragmentActivity

    //Create the helper once for this activity
    val biometricAuthenticator = remember(fragmentActivity) {
        BiometricAuthenticator(fragmentActivity)
    }

    //Once, when the screen first appears: check if fingerprint can be used
    LaunchedEffect(Unit) {
        if(!biometricAuthenticator.isFingerprintAvailable()){
            viewModel.onFingerprintUnavailable()
        }
    }

    //Whenever isLoggedIn changes: if true, go to the video-list
    LaunchedEffect(loginUiState.isLoggedIn) {
        if(loginUiState.isLoggedIn){
            onLoginSuccess()
        }
    }

    Column(
        modifier = modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Stream Demo", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(32.dp))

        Button(
            enabled = loginUiState.isFingerprintAvailable,
            onClick = {
                biometricAuthenticator.showFingerprintPrompt { biometricResult ->
                    viewModel.onBiometricResult(biometricResult)
                }
            }
        ) {
            Text("Unlock with fingerprint")
        }

        loginUiState.errorMessage?.let { errorMessage ->
            Spacer(Modifier.height(16.dp))
            Text(errorMessage, color = MaterialTheme.colorScheme.error)
        }




    }

}