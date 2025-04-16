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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState), // Añadimos el verticalScroll modifier
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
// Logo más grande en la parte superior
        Image(
            painter = painterResource(id = R.drawable.ic_logo), // Reemplaza con el nombre de tu logo
            contentDescription = "Logo de Calma App",
            modifier = Modifier
                .height(150.dp)
                .padding(bottom = 32.dp)
        )

// Contenedor central para los campos de inicio de sesión y botones
        Column(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .background(Color(0xFFA2DFDD), RoundedCornerShape(42.dp))
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .height(48.dp), // Establece una altura fija para el contenedor de los botones
                horizontalArrangement = Arrangement.Center // Centramos los botones dentro del Box
            ) {
                androidx.compose.foundation.layout.Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center // Centramos el contenido dentro del Box
                ) {
                    Button(
                        onClick = onNavigateToRegister,
                        modifier = Modifier
                            .fillMaxWidth(0.6f) // Ocupa un porcentaje del ancho
                            .align(Alignment.CenterEnd), // Alinea el botón "Regístrate" a la derecha
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = Color.LightGray)
                    ) {
                        Text(" Regístrate", color = Color.White)
                    }
                    Button(
                        onClick = onLoginSuccess,
                        modifier = Modifier
                            .fillMaxWidth(0.5f) // Ocupa el mismo porcentaje del ancho
                            .align(Alignment.CenterStart), // Alinea el botón "Inicia Sesión" a la izquierda
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = Color(0xFF5CC2C6))
                    ) {
                        Text("Inicia Sesión", color = Color.White)
                    }
                }
            }

            var email by remember { mutableStateOf("") }
            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo electrónico:", color = Color.White) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.textFieldColors(
                    cursorColor = Color.White,
                    focusedIndicatorColor = Color.White,
                    unfocusedIndicatorColor = Color.White,
                    disabledIndicatorColor = Color.White,
                    focusedLabelColor = Color(0xFF5CC2C6),
                    unfocusedLabelColor = Color.White,
                    containerColor = Color(0xFFA2DFDD)
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            var password by remember { mutableStateOf("") }
            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña:", color = Color.White) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                colors = TextFieldDefaults.textFieldColors(

                    cursorColor = Color.White,
                    focusedIndicatorColor = Color.White,
                    unfocusedIndicatorColor = Color.White,
                    disabledIndicatorColor = Color.White,
                    focusedLabelColor = Color(0xFF5CC2C6),
                    unfocusedLabelColor = Color.White,
                    containerColor = Color(0xFFA2DFDD)
                )
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    Log.d("LoginScreen", "Botón Ingresar clickeado")
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