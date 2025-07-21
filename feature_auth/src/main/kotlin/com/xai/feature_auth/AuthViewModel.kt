package com.xai.dosify.feature.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.xai.dosify.core.data.repository.AuthRepository
import com.xai.dosify.feature.sync.utils.SyncWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repo: AuthRepository,
    private val auth: FirebaseAuth,
    private val workManager: WorkManager  // Inject
) : ViewModel() {
    private val _state = MutableStateFlow(AuthState())
    val state: StateFlow<AuthState> = _state

    val authUser: StateFlow<FirebaseUser?> = auth.authStateChanges()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun loginEmail(email: String, password: String) = viewModelScope.launch {
        _state.value = _state.value.copy(loading = true)
        val success = repo.emailLogin(email, password)
        _state.value = _state.value.copy(loading = false, success = success, error = if (!success) "Login failed" else null)
        if (success) enqueueSync()
    }

    fun loginGoogle(idToken: String) = viewModelScope.launch {
        _state.value = _state.value.copy(loading = true)
        val success = repo.googleLogin(idToken)
        _state.value = _state.value.copy(loading = false, success = success, error = if (!success) "Google login failed" else null)
        if (success) enqueueSync()
    }

    fun logout() = repo.logout()

    private fun enqueueSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val periodicRequest = PeriodicWorkRequestBuilder<SyncWorker>(1, TimeUnit.HOURS)
            .setConstraints(constraints)
            .build()

        workManager.enqueueUniquePeriodicWork("sync", ExistingPeriodicWorkPolicy.KEEP, periodicRequest)
    }
}

data class AuthState(val loading: Boolean = false, val success: Boolean = false, val error: String? = null)

fun FirebaseAuth.authStateChanges(): Flow<FirebaseUser?> = callbackFlow {
    val listener = FirebaseAuth.AuthStateListener { auth ->
        trySend(auth.currentUser)
    }
    addAuthStateListener(listener)
    awaitClose { removeAuthStateListener(listener) }
}