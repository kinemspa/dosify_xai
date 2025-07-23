package com.xai.feature_auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.xai.dosify.R
import com.xai.dosify.nav.NavRoutes
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    onLoginSuccess: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val user by viewModel.authUser.collectAsState()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current
    val credentialManager = remember { CredentialManager.create(context) }
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val webClientId = stringResource(R.string.web_client_id) // Move to Composable context

    // Auto-nav on logged-in
    LaunchedEffect(user) {
        if (user != null) onLoginSuccess()
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Surface(modifier = Modifier.padding(padding).fillMaxSize()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (user != null) {
                    Text("Welcome, ${user!!.displayName}")
                    Button(onClick = { viewModel.logout() }) {
                        Text("Logout")
                    }
                } else {
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
                                val result = handleGoogleSignIn(context, credentialManager, webClientId)
                                if (result != null) {
                                    handleCredential(result, viewModel)
                                    snackbarHostState.showSnackbar("Sign-in successful")
                                } else {
                                    snackbarHostState.showSnackbar("Sign-in failed")
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

private suspend fun handleGoogleSignIn(
    context: android.content.Context,
    credentialManager: CredentialManager,
    webClientId: String
): GetCredentialResponse? {
    return try {
        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(true)
            .setServerClientId(webClientId)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        credentialManager.getCredential(request = request, context = context)
    } catch (e: GetCredentialException) {
        try {
            val googleIdOptionFallback = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(webClientId)
                .build()

            val requestFallback = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOptionFallback)
                .build()

            credentialManager.getCredential(request = requestFallback, context = context)
        } catch (fallbackE: GetCredentialException) {
            null
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
    }
}