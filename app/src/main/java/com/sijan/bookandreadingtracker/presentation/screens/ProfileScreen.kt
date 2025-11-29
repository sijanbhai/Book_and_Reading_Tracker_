package com.sijan.bookandreadingtracker.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sijan.bookandreadingtracker.presentation.viewmodel.AuthMode
import com.sijan.bookandreadingtracker.presentation.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val authMode by viewModel.authMode.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (state.isLoggedIn && state.currentUser != null) {
                // Show profile content when logged in
                ProfileContent(
                    user = state.currentUser!!,
                    onLogout = { viewModel.logout() }
                )
            } else {
                // Show auth form when not logged in
                AuthForm(
                    authMode = authMode,
                    isLoading = state.isLoading,
                    error = state.error,
                    onLogin = { email, password ->
                        viewModel.login(email, password)
                    },
                    onRegister = { name, email, password ->
                        viewModel.register(name, email, password)
                    },
                    onSwitchMode = { viewModel.switchAuthMode() },
                    onClearError = { viewModel.clearError() }
                )
            }

            // Show success message as snackbar
            state.successMessage?.let { message ->
                LaunchedEffect(message) {
                    kotlinx.coroutines.delay(2000)
                    viewModel.clearSuccessMessage()
                }
            }
        }
    }
}

@Composable
fun ProfileContent(
    user: com.sijan.bookandreadingtracker.data.local.UserEntity,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        // Profile Icon
        Icon(
            imageVector = Icons.Default.AccountCircle,
            contentDescription = "Profile",
            modifier = Modifier.size(120.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        // User Info Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Account Information",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                HorizontalDivider()

                // Name
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Name",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Column {
                        Text(
                            text = "Name",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = user.name,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Email
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = "Email",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Column {
                        Text(
                            text = "Email",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = user.email,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Member Since
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Member Since",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Column {
                        Text(
                            text = "Member Since",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = formatDate(user.createdAt),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Logout Button
        Button(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error
            )
        ) {
            Text("Logout")
        }
    }
}

@Composable
fun AuthForm(
    authMode: AuthMode,
    isLoading: Boolean,
    error: String?,
    onLogin: (String, String) -> Unit,
    onRegister: (String, String, String) -> Unit,
    onSwitchMode: () -> Unit,
    onClearError: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Icon
        Icon(
            imageVector = Icons.Default.AccountCircle,
            contentDescription = "Profile",
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Title
        Text(
            text = if (authMode is AuthMode.Login) "Login" else "Create Account",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (authMode is AuthMode.Login)
                "Welcome back! Please login to continue."
            else
                "Create a new account to get started.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Name field (only for registration)
        if (authMode is AuthMode.Register) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                leadingIcon = {
                    Icon(Icons.Default.Person, contentDescription = "Name")
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Email field
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            leadingIcon = {
                Icon(Icons.Default.Email, contentDescription = "Email")
            },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true,
            enabled = !isLoading
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Password field
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            leadingIcon = {
                Icon(Icons.Default.Lock, contentDescription = "Password")
            },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true,
            enabled = !isLoading
        )

        // Error message
        error?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Submit button
        Button(
            onClick = {
                if (authMode is AuthMode.Login) {
                    onLogin(email, password)
                } else {
                    onRegister(name, email, password)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading && email.isNotBlank() && password.isNotBlank() &&
                    (authMode is AuthMode.Login || name.isNotBlank())
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text(if (authMode is AuthMode.Login) "Login" else "Register")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Switch mode button
        TextButton(
            onClick = {
                onSwitchMode()
                name = ""
                email = ""
                password = ""
            },
            enabled = !isLoading
        ) {
            Text(
                text = if (authMode is AuthMode.Login)
                    "Don't have an account? Register"
                else
                    "Already have an account? Login"
            )
        }
    }
}

private fun formatDate(timestamp: Long): String {
    val dateFormat = java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault())
    return dateFormat.format(java.util.Date(timestamp))
}

