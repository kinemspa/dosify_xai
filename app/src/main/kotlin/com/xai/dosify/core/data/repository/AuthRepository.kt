package com.xai.dosify.core.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val auth: FirebaseAuth
) {
    suspend fun emailLogin(email: String, password: String): Boolean = try {
        auth.signInWithEmailAndPassword(email, password).await() != null
    } catch (e: Exception) { false }

    suspend fun googleLogin(idToken: String): Boolean = try {
        auth.signInWithCredential(GoogleAuthProvider.getCredential(idToken, null)).await() != null
    } catch (e: Exception) { false }

    fun logout() = auth.signOut()

    fun currentUser() = auth.currentUser != null
}