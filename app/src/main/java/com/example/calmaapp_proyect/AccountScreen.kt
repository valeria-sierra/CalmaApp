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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.content.Context
import android.widget.Toast
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.runtime.livedata.observeAsState
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(
    onBackClick: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val auth = Firebase.auth
    val db = Firebase.firestore
    val currentUser = auth.currentUser
    val context = LocalContext.current

    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("+57 ") }
    var isLoading by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    // Cargar datos del usuario al iniciar
    LaunchedEffect(Unit) {
        isLoading = true
        try {
            val userDoc = db.collection("users").document(currentUser?.uid ?: "").get().await()
            nombre = userDoc.getString("names") ?: ""
            correo = userDoc.getString("email") ?: currentUser?.email ?: ""
            telefono = userDoc.getString("phoneNumber") ?: "+57 "
        } catch (e: Exception) {
            Toast.makeText(context, "Error cargando datos: ${e.message}", Toast.LENGTH_SHORT).show()
        }
        isLoading = false
    }

    // Diálogo para confirmar eliminación de cuenta
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Confirmar eliminación") },
            text = { Text("¿Estás seguro de que quieres eliminar tu cuenta permanentemente? Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        if (currentUser != null) {
                            deleteAccount(
                                userId = currentUser.uid,
                                context = context,
                                onSuccess = onLogout
                            )
                        }
                    }
                ) {
                    Text("Eliminar", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Diálogo para confirmar cierre de sesión
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Cerrar sesión") },
            text = { Text("¿Estás seguro de que quieres cerrar tu sesión?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        auth.signOut()
                        onLogout()
                    }
                ) {
                    Text("Cerrar sesión")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

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

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
            // Sección Nombre
            SectionTitle("Nombre:")
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Ingresa tu nombre") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Sección Correo
            SectionTitle("Correo electrónico:")
            OutlinedTextField(
                value = correo,
                onValueChange = { correo = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Ingresa tu correo") },
                enabled = false // El correo no se puede editar directamente
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Sección Contraseña
            SectionTitle("Cambiar contraseña:")
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Ingresa nueva contraseña") },
                visualTransformation = PasswordVisualTransformation()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Sección Teléfono
            SectionTitle("Teléfono:")
            OutlinedTextField(
                value = telefono,
                onValueChange = {
                    if (it.startsWith("+57")) {
                        telefono = it
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("+57") }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Botón de actualizar
            Button(
                onClick = {
                    if (currentUser != null) {
                        updateUserData(
                            userId = currentUser.uid,
                            newName = nombre,
                            newEmail = correo,
                            newPhone = telefono,
                            newPassword = if (password.isNotEmpty()) password else null,
                            context = context,
                            onSuccess = { Toast.makeText(context, "Datos actualizados", Toast.LENGTH_SHORT).show() }
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5CC2C6))
            ) {
                Text("Actualizar datos", color = Color.White)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón de cerrar sesión
            Button(
                onClick = { showLogoutDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336)) // Rojo
            ) {
                Text("Cerrar sesión", color = Color.White)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Botón de eliminar cuenta
            TextButton(
                onClick = { showDeleteDialog = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Eliminar cuenta permanentemente", color = Color.Red)
            }
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

private fun updateUserData(
    userId: String,
    newName: String,
    newEmail: String,
    newPhone: String,
    newPassword: String?,
    context: Context,
    onSuccess: () -> Unit
) {
    val auth = Firebase.auth
    val db = Firebase.firestore

    CoroutineScope(Dispatchers.IO).launch {
        try {
            // Actualizar contraseña si se proporcionó
            if (!newPassword.isNullOrEmpty()) {
                auth.currentUser?.updatePassword(newPassword)?.await()
            }

            // Actualizar datos en Firestore
            val updates = hashMapOf<String, Any>(
                "names" to newName,
                "phoneNumber" to newPhone
            )

            db.collection("users").document(userId).update(updates).await()

            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Datos actualizados correctamente", Toast.LENGTH_SHORT).show()
                onSuccess()
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Error al actualizar: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

private fun deleteAccount(
    userId: String,
    context: Context,
    onSuccess: () -> Unit
) {
    val auth = Firebase.auth
    val db = Firebase.firestore

    CoroutineScope(Dispatchers.IO).launch {
        try {
            // Eliminar datos de Firestore
            db.collection("users").document(userId).delete().await()

            // Eliminar usuario de autenticación
            auth.currentUser?.delete()?.await()

            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Cuenta eliminada correctamente", Toast.LENGTH_SHORT).show()
                onSuccess()
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Error al eliminar cuenta: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}