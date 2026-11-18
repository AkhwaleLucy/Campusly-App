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

@Composable
fun AuthScreen(
    onSignUp: (fullName: String, email: String, university: String, courseProgram: String, yearOfStudy: String) -> Unit,
    onSignIn: (email: String) -> Unit
) {
    var isRegister by remember { mutableStateOf(true) }
    var email by remember { mutableStateOf("student@campusly.app") }
    var password by remember { mutableStateOf("password123") }
    var fullName by remember { mutableStateOf("Lucy Vance") }
    var university by remember { mutableStateOf("University of Science & Technology") }
    var courseProgram by remember { mutableStateOf("Computer Science") }
    var yearOfStudy by remember { mutableStateOf("Year 3") }

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
                text = if (isRegister) stringResource(R.string.create_account) else stringResource(R.string.sign_in),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = if (isRegister) stringResource(R.string.join_campusly) else stringResource(R.string.sign_in_dashboard),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (isRegister) {
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text(stringResource(R.string.full_name)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = university,
                    onValueChange = { university = it },
                    label = { Text(stringResource(R.string.university_college)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = courseProgram,
                    onValueChange = { courseProgram = it },
                    label = { Text(stringResource(R.string.course_program)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = yearOfStudy,
                    onValueChange = { yearOfStudy = it },
                    label = { Text(stringResource(R.string.year_of_study)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text(stringResource(R.string.student_email)) },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text(stringResource(R.string.password)) },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    if (isRegister) {
                        if (fullName.isNotBlank() && email.isNotBlank()) {
                            onSignUp(fullName, email, university, courseProgram, yearOfStudy)
                        }
                    } else {
                        if (email.isNotBlank()) {
                            onSignIn(email)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = if (isRegister) stringResource(R.string.create_account) else stringResource(R.string.sign_in),
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedButton(
                onClick = {
                    onSignUp(fullName, email, university, courseProgram, yearOfStudy)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(text = stringResource(R.string.google_sign_in))
            }

            Spacer(modifier = Modifier.height(14.dp))

            TextButton(
                onClick = { isRegister = !isRegister }
            ) {
                Text(
                    text = if (isRegister) stringResource(R.string.already_have_account) else stringResource(R.string.dont_have_account),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
