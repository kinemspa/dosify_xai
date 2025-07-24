package com.xai.feature_auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.xai.core.R
import com.xai.core.nav.NavRoutes
import kotlinx.coroutines.launch
import timber.log.Timber
import android.app.Activity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    onLoginSuccess: () -> Unit,
    innerPadding: PaddingValues
) {
    val state by viewModel.state.collectAsState()
    val user by viewModel.authUser.collectAsState()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isRegistering by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val activity = context as Activity
    val credentialManager = remember { CredentialManager.create(context) }
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val webClientId = stringResource(R.string.web_client_id)

    LaunchedEffect(user) {
        if (user != null) onLoginSuccess()
    }

    LaunchedEffect(state.success) {
        if (state.success) {
            snackbarHostState.showSnackbar(if (isRegistering) "Registration successful" else "Login successful")
            onLoginSuccess()
        } else if (state.error != null) {
            snackbarHostState.showSnackbar(state.error ?: "An error occurred")
        }
    }

    Column(
        modifier = Modifier
            .padding(innerPadding)
            .padding(horizontal = 16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (user != null) {
            Text("Welcome, ${user!!.displayName}")
            Button(onClick = { viewModel.logout() }) {
                Text("Logout")
            }
        } else {
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = if (isRegistering) "Register" else "Login")
            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )
            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = {
                    coroutineScope.launch {
                        if (isRegistering) {
                            viewModel.registerEmail(email, password, activity)
                        } else {
                            viewModel.signInWithEmail(email, password, activity)
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isRegistering) "Register" else "Login")
            }
            Button(
                onClick = {
                    coroutineScope.launch {
                        val result = handleGoogleSignIn(context, credentialManager, webClientId)
                        if (result != null) {
                            handleCredential(result, viewModel)
                            snackbarHostState.showSnackbar("Sign-in successful")
                        } else {
                            snackbarHostState.showSnackbar("Sign-in failed")
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Google Login")
            }
            Button(
                onClick = { viewModel.signInWithApple(activity) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Apple Login")
            }
            Button(
                onClick = { isRegistering = !isRegistering },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isRegistering) "Switch to Login" else "Switch to Register")
            }
            if (state.loading) Text("Loading...")
            state.error?.let { Text(it) }
            if (state.success) Text("Logged in")
        }
        SnackbarHost(hostState = snackbarHostState)
    }
}

private suspend fun handleGoogleSignIn(
    context: android.content.Context,
    credentialManager: CredentialManager,
    webClientId: String
): GetCredentialResponse? {
    return try {
        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setAutoSelectEnabled(false) // Added to disable auto-select and show picker for any account
            .setServerClientId(webClientId)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        credentialManager.getCredential(request = request, context = context)
    } catch (e: GetCredentialException) {
        Timber.e(e, "Google Sign-In failed: ${e.message}")
        null
    }
}

private suspend fun handleCredential(result: GetCredentialResponse, viewModel: AuthViewModel) {
    val credential = result.credential
    if (credential is CustomCredential &&
        credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
    ) {
        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
        viewModel.loginGoogle(googleIdTokenCredential.idToken)
    }
}