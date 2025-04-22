package com.example.calmaapp_proyect

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation // Import añadido
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(onBackClick: () -> Unit = {}) {
    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("+57 ") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 24.dp)
        ) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
            }
            Text(
                text = "MI CUENTA",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF5CC2C6),
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        // Sección Nombre
        SectionTitle("Nombre:")
        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Ingresa nombre") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Sección Correo
        SectionTitle("Actualizar correo:")
        OutlinedTextField(
            value = correo,
            onValueChange = { correo = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Ingresa correo nuevo") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Sección Contraseña
        SectionTitle("Cambiar contraseña:")
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Ingresa nueva contraseña") },
            visualTransformation = PasswordVisualTransformation() // Error solucionado
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Sección Teléfono
        SectionTitle("Actualizar número:")
        OutlinedTextField(
            value = telefono,
            onValueChange = {
                if (it.startsWith("+57")) {
                    telefono = it
                }
            }
            ,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("+57") }
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Botones de acción
        Button(
            onClick = { /* Lógica para actualizar */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5CC2C6))
        ) {
            Text("Actualizar", color = Color.White)
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = { /* Lógica para eliminar cuenta */ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Eliminar cuenta", color = Color.Red)
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        color = Color.Gray,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}
