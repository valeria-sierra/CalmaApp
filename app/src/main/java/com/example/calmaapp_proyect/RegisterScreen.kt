package com.example.calmaapp_proyect

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(onNavigateToLogin: () -> Unit) {
    Image(
        painter = painterResource(id = R.drawable.ic_background), // Reemplaza con tu imagen de fondo
        contentDescription = "Fondo de la pantalla de registro",
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateToLogin) {
                Icon(Icons.Filled.ArrowBack, "Volver al inicio de sesión", tint = Color.White)
            }
            Text(
                text = "Regístrate",
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 48.dp) // Espacio para alinear con el icono de la izquierda
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Color(0x80ADD8E6),
                    RoundedCornerShape(16.dp)
                ) // Fondo celeste con esquinas redondeadas
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            var names by remember { mutableStateOf("") }
            OutlinedTextField(
                value = names,
                onValueChange = { names = it },
                label = { Text("Nombres", color = Color.White) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = androidx.compose.material3.TextFieldDefaults.outlinedTextFieldColors(

                    cursorColor = Color.White,
                    focusedBorderColor = Color(0xFF5CC2C6),
                    unfocusedBorderColor = Color.White,
                    focusedLabelColor = Color(0xFF5CC2C6),
                    unfocusedLabelColor = Color.White
                )
            )

            var lastNames by remember { mutableStateOf("") }
            OutlinedTextField(
                value = lastNames,
                onValueChange = { lastNames = it },
                label = { Text("Apellidos", color = Color.White) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = androidx.compose.material3.TextFieldDefaults.outlinedTextFieldColors(

                    cursorColor = Color.White,
                    focusedBorderColor = Color(0xFF5CC2C6),
                    unfocusedBorderColor = Color.White,
                    focusedLabelColor = Color(0xFF5CC2C6),
                    unfocusedLabelColor = Color.White
                )
            )

            var email by remember { mutableStateOf("") }
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo electrónico", color = Color.White) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = androidx.compose.material3.TextFieldDefaults.outlinedTextFieldColors(

                    cursorColor = Color.White,
                    focusedBorderColor = Color(0xFF5CC2C6),
                    unfocusedBorderColor = Color.White,
                    focusedLabelColor = Color(0xFF5CC2C6),
                    unfocusedLabelColor = Color.White
                )
            )

            var phoneNumber by remember { mutableStateOf("") }
            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                label = { Text("Número de teléfono", color = Color.White) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = androidx.compose.material3.TextFieldDefaults.outlinedTextFieldColors(

                    cursorColor = Color.White,
                    focusedBorderColor = Color(0xFF5CC2C6),
                    unfocusedBorderColor = Color.White,
                    focusedLabelColor = Color(0xFF5CC2C6),
                    unfocusedLabelColor = Color.White
                )
            )

            var password by remember { mutableStateOf("") }
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña", color = Color.White) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                colors = androidx.compose.material3.TextFieldDefaults.outlinedTextFieldColors(

                    cursorColor = Color.White,
                    focusedBorderColor = Color(0xFF5CC2C6),
                    unfocusedBorderColor = Color.White,
                    focusedLabelColor = Color(0xFF5CC2C6),
                    unfocusedLabelColor = Color.White
                )
            )

            var confirmPassword by remember { mutableStateOf("") }
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirma contraseña", color = Color.White) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                colors = androidx.compose.material3.TextFieldDefaults.outlinedTextFieldColors(

                    cursorColor = Color.White,
                    focusedBorderColor = Color(0xFF5CC2C6),
                    unfocusedBorderColor = Color.White,
                    focusedLabelColor = Color(0xFF5CC2C6),
                    unfocusedLabelColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            var showDialog by remember { mutableStateOf(false) }

            Button(
                onClick = {
                    // Aquí iría la lógica de registro real
                    showDialog = true
                },
                modifier = Modifier.fillMaxWidth(),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = Color(
                        0xFF5CC2C6
                    )
                )
            ) {
                Text("Continúa", color = Color.White)
            }

            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text("Registro Exitoso") },
                    text = { Text("Registro exitoso y volver a la pantalla de inicio de sesión") },
                    confirmButton = {
                        TextButton(onClick = onNavigateToLogin) {
                            Text("Aceptar")
                        }
                    }
                )
            }
        }
    }
}