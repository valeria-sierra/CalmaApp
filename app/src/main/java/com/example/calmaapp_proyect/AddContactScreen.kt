package com.example.calmaapp_proyect

import android.util.Patterns
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

@Composable
fun AddContactScreen(
    onBack: () -> Unit,
    onGuardarContacto: () -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var parentesco by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SnackbarHost(hostState = snackbarHostState)

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Volver",
                    tint = Color.White
                )
            }
            Spacer(modifier = Modifier.weight(1f))
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .background(Color(0xFF5CC2C6))
                .padding(vertical = 20.dp, horizontal = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Añadir contacto de emergencia",
                color = Color.White,
                fontSize = 22.sp
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White.copy(alpha = 0.2f), shape = RoundedCornerShape(24.dp))
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CampoTextoAñadir("Nombre", nombre) { nombre = it }
                Spacer(modifier = Modifier.height(16.dp))

                CampoTextoAñadir("Apellido", apellido) { apellido = it }
                Spacer(modifier = Modifier.height(16.dp))

                CampoTextoAñadir("Teléfono", telefono) { telefono = it }
                Spacer(modifier = Modifier.height(16.dp))

                CampoTextoAñadir("Parentesco", parentesco) { parentesco = it }
                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        // Validar campos
                        if (nombre.isBlank() || telefono.isBlank() ) {
                            scope.launch {
                                snackbarHostState.showSnackbar("Por favor completa todos los campos.")
                            }
                            return@Button
                        }

                        // Validar teléfono
                        var telefonoFormateado = telefono.trim()
                        if (telefonoFormateado.startsWith("3") && telefonoFormateado.length == 10) {
                            telefonoFormateado = "+57$telefonoFormateado"
                        }
                        if (!telefonoFormateado.startsWith("+57") || telefonoFormateado.length !in 12..13) {
                            scope.launch {
                                snackbarHostState.showSnackbar("Teléfono inválido. Usa formato 311... o +57311...")
                            }
                            return@Button
                        }

                        // Guardar en Firebase
                        val user = FirebaseAuth.getInstance().currentUser
                        if (user != null) {
                            val db = FirebaseFirestore.getInstance()
                            val contacto = hashMapOf(
                                "nombre" to nombre,
                                "apellido" to apellido,
                                "telefono" to telefonoFormateado,
                                "parentesco" to parentesco,
                                "uidUsuario" to user.uid
                            )

                            db.collection("contactos_emergencia")
                                .add(contacto)
                                .addOnSuccessListener {
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Contacto guardado exitosamente.")
                                    }
                                    onGuardarContacto()
                                }
                                .addOnFailureListener { e ->
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Error al guardar contacto: ${e.message}")
                                    }
                                }
                        } else {
                            scope.launch {
                                snackbarHostState.showSnackbar("Usuario no autenticado.")
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5CC2C6))
                ) {
                    Text("Guardar contacto", color = Color.White, fontSize = 18.sp)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampoTextoAñadir(label: String, value: String, onValueChange: (String) -> Unit) {
    Column(horizontalAlignment = Alignment.Start, modifier = Modifier.fillMaxWidth()) {
        Text(label, color = Color.White, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(4.dp))
        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xD080B0BF), RoundedCornerShape(25.dp)),
            colors = TextFieldDefaults.textFieldColors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = Color.White,
                containerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            shape = RoundedCornerShape(16.dp),
            placeholder = {
                Text("Ingresa $label", color = Color.White.copy(alpha = 0.7f))
            },
            singleLine = true
        )
    }
}
