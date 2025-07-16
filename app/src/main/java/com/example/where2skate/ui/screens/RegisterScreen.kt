package com.example.where2skate.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email // Icono para Email
import androidx.compose.material.icons.filled.Lock   // Icono para Password
import androidx.compose.material.icons.filled.Skateboarding // Icono de App/Logo
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.where2skate.ui.viewmodel.AuthViewModel

// Asegúrate de que la definición de StyledOutlinedTextField esté accesible
// (puede estar en este archivo, o en uno común e importada)
// Si no está aquí, copia la definición que tienes en LoginScreen.kt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    authViewModel: AuthViewModel = viewModel(),
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    // Si quisieras un campo de nombre de usuario visible (displayName) aparte del email:
    // var username by remember { mutableStateOf("") }

    val currentUser by authViewModel.currentUser.collectAsState()
    val error by authViewModel.error.collectAsState()
    var passwordsMatchError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(currentUser) {
        if (currentUser != null) {
            onRegisterSuccess()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Filled.Skateboarding,
                contentDescription = "App Logo",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(80.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "Join the Crew!",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                ),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                "Sign up to discover and share spots",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // Campo de Email (Usado para login y como identificador)
            StyledOutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = "Email",
                leadingIcon = Icons.Filled.Email,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = VisualTransformation.None
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Campo de Contraseña
            StyledOutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    passwordsMatchError = null
                },
                label = "Password",
                leadingIcon = Icons.Filled.Lock,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Campo de Confirmar Contraseña
            StyledOutlinedTextField(
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                    passwordsMatchError = null
                },
                label = "Confirm Password",
                leadingIcon = Icons.Filled.Lock,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (password == confirmPassword) {
                        passwordsMatchError = null
                        // Para Firebase Auth, el "username" es el email.
                        // Si quisieras un displayName adicional, lo pasarías aquí o lo actualizarías después.
                        authViewModel.register(email, password)
                    } else {
                        passwordsMatchError = "Passwords do not match!"
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clip(RoundedCornerShape(50)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp, pressedElevation = 8.dp)
            ) {
                Text(
                    "CREATE ACCOUNT",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = onNavigateToLogin) {
                Text(
                    "Got an account? Login",
                    color = MaterialTheme.colorScheme.primary
                )
            }

            passwordsMatchError?.let { msg ->
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    msg,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            error?.let {
                if (passwordsMatchError == null) Spacer(modifier = Modifier.height(12.dp))
                Text(
                    it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}