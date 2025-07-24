package com.xai.feature_auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.OAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import timber.log.Timber
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneMultiFactorGenerator
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import android.app.Activity
import com.xai.core.data.repository.AuthRepository

data class AuthState(val loading: Boolean = false, val success: Boolean = false, val error: String? = null)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val authRepository: AuthRepository  // Injected repository for login/register
) : ViewModel() {

    private val _state = MutableStateFlow(AuthState())
    val state: StateFlow<AuthState> = _state.asStateFlow()

    private val _authUser = MutableStateFlow(auth.currentUser)
    val authUser: StateFlow<FirebaseUser?> = _authUser.asStateFlow()

    private val _phone = MutableStateFlow("")
    val phone: StateFlow<String> = _phone.asStateFlow()

    fun updatePhone(newPhone: String) {
        _phone.value = newPhone
    }

    fun signInWithApple(activity: Activity) {
        val provider = OAuthProvider.newBuilder("apple.com")
            .setScopes(listOf("email", "name"))
            .addCustomParameter("locale", "en")
            .build()

        auth.startActivityForSignInWithProvider(activity, provider)
            .addOnSuccessListener { authResult ->
                _authUser.value = authResult.user
                Timber.d("Apple success: User ${authResult.user?.uid}")
                _state.value = _state.value.copy(success = true)
            }
            .addOnFailureListener { e ->
                _state.value = _state.value.copy(error = e.message)
                Timber.e(e, "Apple sign-in failed")
            }
    }

    fun signInWithEmail(email: String, password: String) = viewModelScope.launch {
        _state.value = _state.value.copy(loading = true)
        val success = authRepository.emailLogin(email, password)
        _state.value = _state.value.copy(loading = false, success = success, error = if (!success) "Login failed" else null)
        _authUser.value = auth.currentUser
        if (success) checkAndEnrollMFA(null) // Pass null or activity from UI
    }

    fun registerEmail(email: String, password: String) = viewModelScope.launch {
        _state.value = _state.value.copy(loading = true)
        val success = authRepository.registerEmail(email, password)
        _state.value = _state.value.copy(loading = false, success = success, error = if (!success) "Registration failed" else null)
        _authUser.value = auth.currentUser
        if (success) checkAndEnrollMFA(null) // Pass null or activity from UI
    }

    fun logout() {
        authRepository.logout()
        _authUser.value = null
    }

    fun loginGoogle(idToken: String) = viewModelScope.launch {
        _state.value = _state.value.copy(loading = true)
        val success = authRepository.googleLogin(idToken)
        _state.value = _state.value.copy(loading = false, success = success, error = if (!success) "Google login failed" else null)
        _authUser.value = auth.currentUser
    }

    private fun checkAndEnrollMFA(activity: Activity?) {
        val user = auth.currentUser ?: return
        user.multiFactor.enrolledFactors.let { factors ->
            if (factors.isEmpty()) {
                Timber.d("Starting MFA enrollment")
                user.multiFactor.session.addOnSuccessListener { session ->
                    val phone = _phone.value
                    val options = PhoneAuthOptions.newBuilder(auth)
                        .setPhoneNumber(phone)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(activity)  // Use activity from UI
                        .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                                // Auto-resolved
                                Timber.d("MFA verification completed")
                                val assertion = PhoneMultiFactorGenerator.getAssertion(credential)
                                user.multiFactor.enroll(assertion, null)
                            }
                            override fun onVerificationFailed(e: FirebaseException) {
                                Timber.e(e, "MFA verification failed")
                                _state.value = _state.value.copy(error = e.message)
                            }
                            override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                                // Save verificationId/token for UI code input
                                Timber.d("MFA code sent")
                                // Show UI for code entry, then use credential = PhoneAuthProvider.getCredential(verificationId, code)
                                // val assertion = PhoneMultiFactorGenerator.getAssertion(credential)
                                // user.multiFactor.enroll(assertion, null)
                            }
                        })
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