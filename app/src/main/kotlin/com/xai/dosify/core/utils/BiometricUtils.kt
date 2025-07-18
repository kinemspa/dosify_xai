package com.xai.dosify.core.utils

import android.content.Context
import androidx.biometric.BiometricPrompt
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

object BiometricUtils {
    fun authenticate(activity: FragmentActivity): Flow<Boolean> = callbackFlow {
        val prompt = BiometricPrompt(activity, object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                trySend(true)
            }
            override fun onAuthenticationFailed() {
                trySend(false)
            }
        })
        prompt.authenticate(BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric Login")
            .setSubtitle("Use fingerprint/face")
            .setNegativeButtonText("Cancel")
            .build())
        awaitClose { }
    }

    fun getPassphrase(context: Context): String = "secure_passphrase"  // Replace with real (e.g., KeyStore encrypted from biometrics)
}