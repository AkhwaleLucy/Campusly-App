package com.example.gossipapp.presentation.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.gossipapp.R

enum class AuthMode {
    SIGN_IN,
    SIGN_UP,
    RESET_PASSWORD
}

@Composable
fun AuthScreen(
    onSignUp: (fullName: String, email: String, password: String, university: String, courseProgram: String, yearOfStudy: String, onError: (String) -> Unit) -> Unit,
    onSignIn: (email: String, password: String, onError: (String) -> Unit) -> Unit,
    onResetPassword: (email: String, onSuccess: (String) -> Unit, onError: (String) -> Unit) -> Unit
) {
    var mode by remember { mutableStateOf(AuthMode.SIGN_IN) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var university by remember { mutableStateOf("United States International University Africa") }
    var courseProgram by remember { mutableStateOf("Computer Science") }
    var yearOfStudy by remember { mutableStateOf("Year 3") }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.School,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(60.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = when (mode) {
                    AuthMode.SIGN_UP -> stringResource(R.string.create_account)
                    AuthMode.SIGN_IN -> stringResource(R.string.sign_in)
                    AuthMode.RESET_PASSWORD -> "Password Recovery"
                },
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = when (mode) {
                    AuthMode.SIGN_UP -> "Join Campusly with your institutional .ac.ke email (e.g. @usiu.ac.ke)"
                    AuthMode.SIGN_IN -> "Sign in with your registered .ac.ke student credentials"
                    AuthMode.RESET_PASSWORD -> "Enter your registered .ac.ke institutional email. We will send a verified password recovery link to your inbox."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Error Message Banner
            if (errorMessage != null) {
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = errorMessage!!,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                        if (errorMessage!!.contains("already exists", ignoreCase = true)) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = {
                                        errorMessage = null
                                        mode = AuthMode.SIGN_IN
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Sign In Now")
                                }
                                OutlinedButton(
                                    onClick = {
                                        errorMessage = null
                                        mode = AuthMode.RESET_PASSWORD
                                    },
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Password Recovery")
                                }
                            }
                        }
                    }
                }
            }

            // Success Message Banner
            if (successMessage != null) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    Text(
                        text = successMessage!!,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            if (mode == AuthMode.SIGN_UP) {
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it; errorMessage = null },
                    label = { Text(stringResource(R.string.full_name)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = university,
                    onValueChange = { university = it; errorMessage = null },
                    label = { Text(stringResource(R.string.university_college)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = courseProgram,
                    onValueChange = { courseProgram = it; errorMessage = null },
                    label = { Text(stringResource(R.string.course_program)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = yearOfStudy,
                    onValueChange = { yearOfStudy = it; errorMessage = null },
                    label = { Text(stringResource(R.string.year_of_study)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            OutlinedTextField(
                value = email,
                onValueChange = { email = it; errorMessage = null; successMessage = null },
                label = { Text("Institutional Email (.ac.ke)") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            if (mode != AuthMode.RESET_PASSWORD) {
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it; errorMessage = null; successMessage = null },
                    label = { Text(stringResource(R.string.password)) },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    errorMessage = null
                    successMessage = null
                    when (mode) {
                        AuthMode.SIGN_UP -> {
                            if (fullName.isBlank() || email.isBlank() || password.isBlank()) {
                                errorMessage = "Please fill in all required fields."
                            } else {
                                onSignUp(fullName, email, password, university, courseProgram, yearOfStudy) { err ->
                                    errorMessage = err
                                }
                            }
                        }
                        AuthMode.SIGN_IN -> {
                            if (email.isBlank() || password.isBlank()) {
                                errorMessage = "Please enter your email and password."
                            } else {
                                onSignIn(email, password) { err ->
                                    errorMessage = err
                                }
                            }
                        }
                        AuthMode.RESET_PASSWORD -> {
                            if (email.isBlank()) {
                                errorMessage = "Please enter your registered student email address."
                            } else {
                                onResetPassword(email, { info ->
                                    successMessage = info
                                    mode = AuthMode.SIGN_IN
                                }, { err ->
                                    errorMessage = err
                                })
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = when (mode) {
                        AuthMode.SIGN_UP -> stringResource(R.string.create_account)
                        AuthMode.SIGN_IN -> stringResource(R.string.sign_in)
                        AuthMode.RESET_PASSWORD -> "Send Recovery Link 📧"
                    },
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = {
                        errorMessage = null
                        successMessage = null
                        mode = if (mode == AuthMode.SIGN_IN) AuthMode.SIGN_UP else AuthMode.SIGN_IN
                    }
                ) {
                    Text(
                        text = if (mode == AuthMode.SIGN_IN) stringResource(R.string.dont_have_account) else stringResource(R.string.already_have_account),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                if (mode == AuthMode.SIGN_IN) {
                    TextButton(
                        onClick = {
                            errorMessage = null
                            successMessage = null
                            mode = AuthMode.RESET_PASSWORD
                        }
                    ) {
                        Text(
                            text = "Forgot Password?",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }
        }
    }
}
