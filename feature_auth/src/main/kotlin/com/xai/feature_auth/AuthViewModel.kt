package com.xai.dosify.feature.auth.ui

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.OAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import timber.log.Timber
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

@HiltViewModel
class AuthViewModel @Inject constructor(private val auth: FirebaseAuth) : ViewModel() {

    fun signInWithApple() {
        val provider = OAuthProvider.newBuilder("apple.com")
            .setScopes(listOf("email", "name"))
            .addCustomParameter("locale", "en")
            .build()

        auth.startActivityForSignInWithProvider(/* activity */ provider)
            .addOnSuccessListener { authResult ->
                Timber.d("Apple success: User ${authResult.user?.uid}")
            }
            .addOnFailureListener { e ->
                Timber.e(e, "Apple sign-in failed")
            }
    }

    fun signInWithEmail(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                checkAndEnrollMFA()
            }
    }

    private fun checkAndEnrollMFA() {
        val user = auth.currentUser ?: return
        user.multiFactor.enrolledFactors.let { factors ->
            if (factors.isEmpty()) {
                Timber.d("Starting MFA enrollment")
                user.multiFactor.session.addOnSuccessListener { session ->
                    // Prompt user for phone (assume UI collects it, e.g., via LiveData)
                    val phone = "+1" + userPhone // Replace with actual input
                    val options = PhoneAuthOptions.newBuilder(auth)
                        .setPhoneNumber(phone)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(/* pass activity from UI */)
                        .setCallbacks(/* verification callbacks */)
                        .setMultiFactorSession(session)
                        .build()
                    PhoneAuthProvider.verifyPhoneNumber(options)
                }.addOnFailureListener { e ->
                    Timber.e(e, "MFA session failed")
                    // Show UI toast: "MFA setup failed, try again"
                }
            } else {
                Timber.d("MFA already enrolled with ${factors.size} factors")
            }
        }
    }
}