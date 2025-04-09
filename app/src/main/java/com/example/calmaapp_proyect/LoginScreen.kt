package com.example.calmaapp_proyect

import android.util.Log
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
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
fun LoginScreen(onLoginSuccess: () -> Unit, onNavigateToRegister: () -> Unit) {
    Image(
        painter = painterResource(id = R.drawable.ic_background), // Reemplaza con tu imagen de fondo
        contentDescription = "Fondo de la pantalla de inicio de sesión",
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Button(
                onClick = onLoginSuccess,
                modifier = Modifier.weight(1f),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = Color(0xFF5CC2C6))
            ) {
                Text("Inicia Sesión", color = Color.White)
            }
            Spacer(modifier = Modifier.weight(0.1f))
            Button(
                onClick = onNavigateToRegister,
                modifier = Modifier.weight(1f),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = Color.LightGray)
            ) {
                Text("Regístrate", color = Color.White)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0x80ADD8E6), RoundedCornerShape(16.dp))
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Ingresa tus datos",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 16.dp)
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
            Spacer(modifier = Modifier.height(8.dp))
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
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    Log.d("LoginScreen", "Botón Ingresar clickeado") // Añade esta línea
                    onLoginSuccess()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = Color(0xFF5CC2C6))
            ) {
                Text("Ingresar", color = Color.White)
            }
        }
    }
}