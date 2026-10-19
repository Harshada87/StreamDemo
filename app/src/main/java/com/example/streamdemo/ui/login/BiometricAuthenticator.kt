package com.example.streamdemo.ui.login

import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

class BiometricAuthenticator(private val activity: FragmentActivity) {

    private val biometricManager = BiometricManager.from(activity)

    // Step 1: Can this device use a strong fingerprint right now?
    fun isFingerprintAvailable(): Boolean {
        val availabilityStatus = biometricManager.canAuthenticate(BIOMETRIC_STRONG)
        return availabilityStatus == BiometricManager.BIOMETRIC_SUCCESS
    }

    // Step 2: Show the fingerprint dialog and report the result
    fun showFingerprintPrompt(onResult: (BiometricResult) -> Unit) {

        // Where the result callbacks run: the main (UI) thread
        val mainThreadExecutor = ContextCompat.getMainExecutor(activity)

        // What to do when the dialog finishes
        val authenticationCallback = object : BiometricPrompt.AuthenticationCallback() {

            override fun onAuthenticationSucceeded(
                authenticationResult: BiometricPrompt.AuthenticationResult
            ) {
                onResult(BiometricResult.Success)
            }

            override fun onAuthenticationError(errorCode: Int, errorMessage: CharSequence) {
                val userCancelled = errorCode == BiometricPrompt.ERROR_USER_CANCELED ||
                        errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON ||
                        errorCode == BiometricPrompt.ERROR_CANCELED

                if (userCancelled) {
                    onResult(BiometricResult.Cancelled)
                } else {
                    onResult(BiometricResult.Error(errorMessage.toString()))
                }
            }

            // Wrong finger: the dialog stays open and lets the user try again,
            // so we don't report anything here.
            override fun onAuthenticationFailed() = Unit
        }

        // The dialog's text and rules
        val promptInformation = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Unlock StreamDemo")
            .setSubtitle("Use your fingerprint to continue")
            .setNegativeButtonText("Cancel")
            .setAllowedAuthenticators(BIOMETRIC_STRONG)
            .build()

        // Create the prompt and show it
        val biometricPrompt = BiometricPrompt(activity, mainThreadExecutor, authenticationCallback)
        biometricPrompt.authenticate(promptInformation)
    }
}