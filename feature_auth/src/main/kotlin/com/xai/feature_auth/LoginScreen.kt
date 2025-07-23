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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    onLoginSuccess: () -> Unit,
    innerPadding: PaddingValues // Add innerPadding as a parameter
) {
    val state by viewModel.state.collectAsState()
    val user by viewModel.authUser.collectAsState()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current
    val credentialManager = remember { CredentialManager.create(context) }
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val webClientId = stringResource(R.string.web_client_id)

    LaunchedEffect(user) {
        if (user != null) onLoginSuccess()
    }

    // Remove the nested Scaffold and use the innerPadding directly
    Column(
        modifier = Modifier
            .padding(innerPadding) // Apply innerPadding from MainActivity
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
            Spacer(modifier = Modifier.height(16.dp)) // Add spacing below app bar
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
                onClick = { viewModel.loginEmail(email, password) },
                modifier = Modifier.fillMaxWidth()
            ) {
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
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Google Login")
            }
            if (state.loading) Text("Loading...")
            state.error?.let { Text(it) }
            if (state.success) Text("Logged in")
        }
        // Add SnackbarHost outside the conditional block to ensure it’s always available
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