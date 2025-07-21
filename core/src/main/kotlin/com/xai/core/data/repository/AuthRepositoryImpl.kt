package com.xai.core.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@ViewModelScoped
class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth
) : AuthRepository {
    override suspend fun emailLogin(email: String, password: String): Boolean = try {
        auth.signInWithEmailAndPassword(email, password).await() != null
    } catch (e: Exception) { false }

    override suspend fun googleLogin(idToken: String): Boolean = try {
        auth.signInWithCredential(GoogleAuthProvider.getCredential(idToken, null)).await() != null
    } catch (e: Exception) { false }

    override fun logout() = auth.signOut()

    override fun currentUser() = auth.currentUser != null
}