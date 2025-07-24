package com.xai.feature_auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.OAuthProvider
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneMultiFactorGenerator
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import timber.log.Timber
import com.google.firebase.auth.PhoneAuthOptions
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import android.app.Activity
import com.google.firebase.FirebaseException
import com.xai.core.data.repository.AuthRepository
import com.xai.core.data.AppDatabase

data class AuthState(val loading: Boolean = false, val success: Boolean = false, val error: String? = null)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val authRepository: AuthRepository
    private val db: AppDatabase
) : ViewModel() {

    private val _state = MutableStateFlow(AuthState())
    val state: StateFlow<AuthState> = _state.asStateFlow()

    private val _authUser = MutableStateFlow(auth.currentUser)
    val authUser: StateFlow<FirebaseUser?> = _authUser.asStateFlow()

    private val _phone = MutableStateFlow("")
    val phone: StateFlow<String> = _phone.asStateFlow()

    private val _verificationId = MutableStateFlow<String?>(null)
    val verificationId: StateFlow<String?> = _verificationId.asStateFlow()

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

    fun signInWithEmail(email: String, password: String, activity: Activity) = viewModelScope.launch {
        _state.value = _state.value.copy(loading = true)
        val success = authRepository.emailLogin(email, password)
        _state.value = _state.value.copy(loading = false, success = success, error = if (!success) "Login failed" else null)
        _authUser.value = auth.currentUser
        if (success) checkAndEnrollMFA(activity)
    }

    fun registerEmail(email: String, password: String, activity: Activity) = viewModelScope.launch {
        _state.value = _state.value.copy(loading = true)
        val success = authRepository.registerEmail(email, password)
        _state.value = _state.value.copy(loading = false, success = success, error = if (!success) "Registration failed" else null)
        _authUser.value = auth.currentUser
        if (success) checkAndEnrollMFA(activity)
    }

    fun logout() = viewModelScope.launch {
        authRepository.logout()
        db.clearAllTables() // Clear local Room database
        _authUser.value = null
    }

    fun loginGoogle(idToken: String) = viewModelScope.launch {
        _state.value = _state.value.copy(loading = true)
        val success = authRepository.googleLogin(idToken)
        _state.value = _state.value.copy(loading = false, success = success, error = if (!success) "Google login failed" else null)
        _authUser.value = auth.currentUser
    }

    private fun checkAndEnrollMFA(activity: Activity) {
        val user = auth.currentUser ?: return
        user.multiFactor.enrolledFactors.let { factors ->
            if (factors.isEmpty()) {
                Timber.d("Starting MFA enrollment")
                user.multiFactor.session.addOnSuccessListener { session ->
                    val phone = _phone.value
                    if (phone.isNotEmpty()) {
                        val options = PhoneAuthOptions.newBuilder(auth)
                            .setPhoneNumber(phone)
                            .setTimeout(60L, TimeUnit.SECONDS)
                            .setActivity(activity)
                            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                                    Timber.d("MFA verification completed")
                                    val assertion = PhoneMultiFactorGenerator.getAssertion(credential)
                                    user.multiFactor.enroll(assertion, null)
                                    _state.value = _state.value.copy(success = true)
                                }
                                override fun onVerificationFailed(e: FirebaseException) {
                                    Timber.e(e, "MFA verification failed")
                                    _state.value = _state.value.copy(error = e.message)
                                }
                                override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                                    Timber.d("MFA code sent")
                                    _verificationId.value = verificationId
                                }
                            })
                            .setMultiFactorSession(session)
                            .build()
                        PhoneAuthProvider.verifyPhoneNumber(options)
                    } else {
                        _state.value = _state.value.copy(error = "Phone number required for MFA")
                    }
                }.addOnFailureListener { e ->
                    Timber.e(e, "MFA session failed")
                    _state.value = _state.value.copy(error = "MFA setup failed: ${e.message}")
                }
            } else {
                Timber.d("MFA already enrolled with ${factors.size} factors")
            }
        }
    }

    fun verifyMfaCode(code: String) = viewModelScope.launch {
        val verificationId = _verificationId.value ?: return@launch
        val credential = PhoneAuthProvider.getCredential(verificationId, code)
        val assertion = PhoneMultiFactorGenerator.getAssertion(credential)
        auth.currentUser?.multiFactor?.enroll(assertion, null)?.addOnSuccessListener {
            Timber.d("MFA enrolled successfully")
            _state.value = _state.value.copy(success = true)
            _verificationId.value = null
        }?.addOnFailureListener { e ->
            Timber.e(e, "MFA enrollment failed")
            _state.value = _state.value.copy(error = e.message)
        }
    }
}