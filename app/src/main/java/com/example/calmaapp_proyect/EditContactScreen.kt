package com.example.calmaapp_proyect

import android.app.AlertDialog
import android.util.Log
import android.widget.Toast
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

@Composable
fun EditContactScreen(
    contactoId: String, // ID del documento en Firebase
    nombreInicial: String,
    apellidoInicial: String,
    telefonoInicial: String,
    parentescoInicial: String,
    onBack: () -> Unit,
    onUpdated: () -> Unit // se llama cuando se actualiza correctamente
) {
    var nombre by remember { mutableStateOf(nombreInicial) }
    var apellido by remember { mutableStateOf(apellidoInicial) }
    var telefono by remember { mutableStateOf(telefonoInicial) }
    var parentesco by remember { mutableStateOf(parentescoInicial) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SnackbarHost(hostState = snackbarHostState)

        // Flecha hacia atrás
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

        // Título en recuadro turquesa
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .background(Color(0xFF5CC2C6))
                .padding(vertical = 20.dp, horizontal = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Editar contacto",
                color = Color.White,
                fontSize = 22.sp
            )
        }

        // Contenedor translúcido blanco con campos
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
                CampoTextoEditar("Nombre", nombre) { nombre = it }
                Spacer(modifier = Modifier.height(16.dp))
                CampoTextoEditar("Apellido", apellido) { apellido = it }
                Spacer(modifier = Modifier.height(16.dp))
                CampoTextoEditar("Teléfono", telefono) { telefono = it }
                Spacer(modifier = Modifier.height(16.dp))
                CampoTextoEditar("Parentesco", parentesco) { parentesco = it }
                Spacer(modifier = Modifier.height(32.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = {
                            if (nombre.isBlank() || telefono.isBlank()) {
                                scope.launch {
                                    snackbarHostState.showSnackbar("Nombre y teléfono son obligatorios.")
                                }
                                return@Button
                            }

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

                            val db = FirebaseFirestore.getInstance()
                            db.collection("contactos_emergencia").document(contactoId)
                                .update(
                                    mapOf(
                                        "nombre" to nombre,
                                        "apellido" to apellido,
                                        "telefono" to telefonoFormateado,
                                        "parentesco" to parentesco
                                    )
                                )
                                .addOnSuccessListener {
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Contacto actualizado exitosamente.")
                                    }
                                    onUpdated()
                                }
                                .addOnFailureListener {
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Error al actualizar: ${it.message}")
                                    }
                                }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5CC2C6))
                    ) {
                        Text("Actualizar", color = Color.White, fontSize = 16.sp)
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Button(
                        onClick = {
                            AlertDialog.Builder(context).apply {
                                setTitle("Eliminar contacto")
                                setMessage("¿Estás seguro de que quieres eliminar este contacto?")
                                setPositiveButton("Eliminar") { _, _ ->
                                    FirebaseFirestore.getInstance()
                                        .collection("contactos_emergencia")
                                        .document(contactoId)
                                        .delete()
                                        .addOnSuccessListener {
                                            Toast.makeText(context, "Contacto eliminado.", Toast.LENGTH_SHORT).show()
                                            onUpdated()
                                        }
                                        .addOnFailureListener {
                                            Toast.makeText(context, "Error al eliminar: ${it.message}", Toast.LENGTH_SHORT).show()
                                        }
                                }
                                setNegativeButton("Cancelar", null)
                            }.show()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB0B0B0))
                    ) {
                        Text("Eliminar", color = Color.White, fontSize = 16.sp, textAlign = TextAlign.Center)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampoTextoEditar(label: String, value: String, onValueChange: (String) -> Unit) {
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
