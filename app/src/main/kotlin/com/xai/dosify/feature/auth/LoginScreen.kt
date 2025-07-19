package com.xai.dosify.feature.auth

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseUser
import com.xai.dosify.R
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    onLoginSuccess: () -> Unit  // Add nav callback
) {
    val state by viewModel.state.collectAsState()
    val user by viewModel.authUser.collectAsState()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current
    val credentialManager = remember { CredentialManager.create(context) }
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // Override success to nav
    LaunchedEffect(state.success) {
        if (state.success) onLoginSuccess()
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Surface(modifier = Modifier.padding(padding).fillMaxSize()) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                user?.let { u ->
                    Text("Welcome, ${u.displayName}")
                    Button(onClick = { viewModel.logout() }) {
                        Text("Logout")
                    }
                } ?: run {
                    TextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") }
                    )
                    TextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") }
                    )
                    Button(onClick = { viewModel.loginEmail(email, password) }) {
                        Text("Login")
                    }
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                try {
                                    val googleIdOption = GetGoogleIdOption.Builder()
                                        .setFilterByAuthorizedAccounts(true)
                                        .setServerClientId(context.getString(R.string.web_client_id))
                                        .build()

                                    val request = GetCredentialRequest.Builder()
                                        .addCredentialOption(googleIdOption)
                                        .build()

                                    val result: GetCredentialResponse = credentialManager.getCredential(
                                        request = request,
                                        context = context
                                    )

                                    handleCredential(result, viewModel)
                                } catch (e: GetCredentialException) {
                                    try {
                                        val googleIdOptionFallback = GetGoogleIdOption.Builder()
                                            .setFilterByAuthorizedAccounts(false)
                                            .setServerClientId(context.getString(R.string.web_client_id))
                                            .build()

                                        val requestFallback = GetCredentialRequest.Builder()
                                            .addCredentialOption(googleIdOptionFallback)
                                            .build()

                                        val resultFallback = credentialManager.getCredential(
                                            request = requestFallback,
                                            context = context
                                        )
                                        handleCredential(resultFallback, viewModel)
                                    } catch (fallbackE: GetCredentialException) {
                                        snackbarHostState.showSnackbar("Sign-in failed: ${fallbackE.message}")
                                    }
                                }
                            }
                        }
                    ) {
                        Text("Google Login")
                    }
                    if (state.loading) Text("Loading...")
                    state.error?.let { Text(it) }
                    if (state.success) Text("Logged in")
                }
            }
        }
    }
}

private suspend fun handleCredential(result: GetCredentialResponse, viewModel: AuthViewModel) {
    val credential = result.credential
    if (credential is CustomCredential &&
        credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
    ) {
        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
        viewModel.loginGoogle(googleIdTokenCredential.idToken)
    } else {
        // Invalid
    }
}