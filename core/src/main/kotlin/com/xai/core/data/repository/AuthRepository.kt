package com.xai.core.data.repository

import com.google.firebase.auth.FirebaseUser

interface AuthRepository {
    suspend fun emailLogin(email: String, password: String): Boolean
    suspend fun googleLogin(idToken: String): Boolean
    fun logout()
    fun currentUser(): Boolean
}