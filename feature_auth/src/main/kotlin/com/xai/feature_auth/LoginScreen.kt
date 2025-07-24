package com.xai.feature_auth

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.xai.core.R
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import com.xai.feature_sync.viewmodel.SyncViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    syncViewModel: SyncViewModel = hiltViewModel(),
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
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val webClientId = stringResource(R.string.web_client_id)

    val gso = remember {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(webClientId)
            .requestEmail()
            .build()
    }
    val googleSignInClient = remember { GoogleSignIn.getClient(context, gso) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.let { data ->
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                coroutineScope.launch {
                    handleSignInResult(task, viewModel, snackbarHostState)
                }
            }
        } else {
            coroutineScope.launch {
                snackbarHostState.showSnackbar("Sign-in canceled")
            }
        }
    }

    LaunchedEffect(user) {
        if (user != null) onLoginSuccess()
    }

    LaunchedEffect(state.success) {
        if (state.success) {
            snackbarHostState.showSnackbar(if (isRegistering) "Registration successful" else "Login successful")
            syncViewModel.syncOnLogin() // Sync data for new user
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
                        googleSignInClient.signOut().await() // Clear session to force sign-in page
                        val signInIntent = googleSignInClient.signInIntent
                        launcher.launch(signInIntent)
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

private suspend fun handleSignInResult(
    completedTask: Task<GoogleSignInAccount>,
    viewModel: AuthViewModel,
    snackbarHostState: SnackbarHostState
) {
    try {
        val account = completedTask.getResult(ApiException::class.java)
        val idToken = account.idToken
        if (idToken != null) {
            viewModel.loginGoogle(idToken)
            snackbarHostState.showSnackbar("Sign-in successful")
        } else {
            snackbarHostState.showSnackbar("No ID token")
        }
    } catch (e: ApiException) {
        Timber.e(e, "Google Sign-In failed: ${e.statusCode}")
        snackbarHostState.showSnackbar("Sign-in failed: ${e.message}")
    }
}