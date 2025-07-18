package com.xai.dosify.feature.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xai.dosify.core.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repo: AuthRepository
) : ViewModel() {
    private val _state = MutableStateFlow(AuthState())
    val state: StateFlow<AuthState> = _state

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