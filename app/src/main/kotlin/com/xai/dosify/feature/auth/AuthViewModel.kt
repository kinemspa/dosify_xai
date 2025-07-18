package com.xai.dosify.feature.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.xai.dosify.core.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repo: AuthRepository,
    private val auth: FirebaseAuth  // Add inject for Flow
) : ViewModel() {
    private val _state = MutableStateFlow(AuthState())
    val state: StateFlow<AuthState> = _state

    val authUser: StateFlow<FirebaseUser?> = auth.authStateChanges().asStateFlow()

    fun loginEmail(email: String, password: String) = viewModelScope.launch {
        _state.value = _state.value.copy(loading = true)
        val success = repo.emailLogin(email, password)
        _state.value = _state.value.copy(loading = false, success = success, error = if (!success) "Login failed" else null)
    }

    fun loginGoogle(idToken: String) = viewModelScope.launch {
        _state.value = _state.value.copy(loading = true)
        val success = repo.googleLogin(idToken)
        _state.value = _state.value.copy(loading = false, success = success, error = if (!success) "Google login failed" else null)
    }
}

data class AuthState(val loading: Boolean = false, val success: Boolean = false, val error: String? = null)

// Extension for auth state
fun FirebaseAuth.authStateChanges(): Flow<FirebaseUser?> = callbackFlow {
    val listener = FirebaseAuth.AuthStateListener { auth ->
        trySend(auth.currentUser)
    }
    addAuthStateListener(listener)
    awaitClose { removeAuthStateListener(listener) }
}