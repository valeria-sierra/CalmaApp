package com.example.calmaapp_proyect


import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import android.net.Uri
import android.widget.Toast
import androidx.compose.ui.draw.clip
import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
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
    val storage = Firebase.storage
    val currentUser = auth.currentUser
    val context = LocalContext.current
    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("+57 ") }
    var isLoading by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var profileImageUri by remember { mutableStateOf<Uri?>(null) }


    //cargar datos del usuario al iniciar
    LaunchedEffect(Unit) {
        isLoading = true
        try {
            val userDoc = db.collection("users").document(currentUser?.uid ?: "").get().await()
            nombre = userDoc.getString("names") ?: ""
            correo = userDoc.getString("email") ?: currentUser?.email ?: ""
            telefono = userDoc.getString("phoneNumber") ?: "+57 "
            val imageUrl = userDoc.getString("profileImageUrl")
            if (!imageUrl.isNullOrEmpty()) {
                profileImageUri = Uri.parse(imageUrl)
                AppState.updateProfileImage(profileImageUri)
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Error cargando datos: ${e.message}", Toast.LENGTH_SHORT).show()
        }
        isLoading = false
    }

    // Lanzador para seleccionar imagen
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let {uploadProfileImage(it, currentUser?.uid ?: "", context) { downloadUri ->
                profileImageUri = downloadUri
                AppState.updateProfileImage(downloadUri)
                Toast.makeText(context, "Imagen actualizada", Toast.LENGTH_SHORT).show()
                }
            }
        }
    )
    //diálogo para confirmar eliminación de cuenta
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Confirmar eliminación") },
            text = { Text("¿Estás seguro de que quieres eliminar tu cuenta permanentemente?") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    if (currentUser != null) {
                        deleteAccount(currentUser.uid, context, onLogout)
                    }
                }) {
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
    //diálogo para confirmar cierre de sesión
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Cerrar sesión") },
            text = { Text("¿Estás seguro de que quieres cerrar tu sesión?") },
            confirmButton = {
                TextButton(onClick = {
                    showLogoutDialog = false
                    auth.signOut()
                    onLogout()
                }) {
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
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier
                .fillMaxWidth()
            //.padding(bottom = 10.dp)
        ) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
            }
        }
        // Sección de foto de perfil actualizada
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(Color.White)
                .clickable { imagePicker.launch("image/*") },
            contentAlignment = Alignment.Center
        ) {
            if (profileImageUri != null) {
                Image(
                    painter = rememberAsyncImagePainter(profileImageUri),
                    contentDescription = "Foto de perfil",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Seleccionar foto",
                    modifier = Modifier.size(100.dp),
                    tint = Color(0xFF5CC2C6)
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Haz clic para cambiar foto",
            color = Color.White,
            fontSize = 12.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Mi cuenta",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF5CC2C6)
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (isLoading) {
            CircularProgressIndicator()
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.2f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    SectionTitle("Nombre:")
                    OutlinedTextField(
                        value = nombre,
                        onValueChange = { nombre = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Ingresa tu nombre",color = Color.White.copy(alpha = 0.7f)) },
                        shape = RoundedCornerShape(25.dp),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            containerColor = Color(0xD080B0BF),
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    SectionTitle("Correo electrónico:")
                    OutlinedTextField(
                        value = correo,
                        onValueChange = { },
                        enabled = false,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(25.dp),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            containerColor = Color(0xD080B0BF),
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    SectionTitle("Cambiar contraseña:")
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = PasswordVisualTransformation(),
                        placeholder = { Text("Nueva contraseña",color = Color.White.copy(alpha = 0.7f)) },
                        shape = RoundedCornerShape(25.dp),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            containerColor = Color(0xD080B0BF),
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    SectionTitle("Teléfono:")
                    OutlinedTextField(
                        value = telefono,
                        onValueChange = {
                            if (it.startsWith("+57")) telefono = it
                        },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("+57 ...",color = Color.White.copy(alpha = 0.7f)) },
                        shape = RoundedCornerShape(25.dp),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            containerColor = Color(0xD080B0BF),
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent
                        )
                    )

                    Spacer(modifier = Modifier.height(30.dp))

                    Button(
                        onClick = {
                            if (currentUser != null) {
                                updateUserData(
                                    currentUser.uid, nombre, correo, telefono, password.takeIf { it.isNotEmpty() }, context
                                ) { Toast.makeText(context, "Datos actualizados", Toast.LENGTH_SHORT).show() }
                            }
                        },
                        modifier = Modifier
                            .width(200.dp)
                            .height(50.dp)
                            .align(Alignment.CenterHorizontally),
                        shape = RoundedCornerShape(25.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5CC2C6))
                    ) {
                        Text("Actualizar", color = Color.White)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { showLogoutDialog = true },
                        modifier = Modifier.width(200.dp).height(50.dp).align(Alignment.CenterHorizontally),
                        shape = RoundedCornerShape(25.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                    ) {
                        Text("Cerrar sesión", color = Color.White)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    TextButton(
                        onClick = { showDeleteDialog = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Eliminar cuenta", color = Color.Red)
                    }
                }
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
        color = Color.White,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

// Función para subir imagen a Firebase Storage
private fun uploadProfileImage(
    imageUri: Uri,
    userId: String,
    context: Context,
    onSuccess: (Uri) -> Unit
) {
    val storageRef = Firebase.storage.reference
    val imageRef = storageRef.child("profile_images/$userId/${System.currentTimeMillis()}.jpg")

    imageRef.putFile(imageUri)
        .addOnSuccessListener {
            imageRef.downloadUrl.addOnSuccessListener { uri ->
                // Guardar URL en Firestore
                Firebase.firestore.collection("users").document(userId)
                    .update("profileImageUrl", uri.toString())
                    .addOnSuccessListener {
                        onSuccess(uri)

                    }
            }
        }
        .addOnFailureListener { e ->
            Toast.makeText(context, "Error al subir imagen: ${e.message}", Toast.LENGTH_SHORT).show()
        }
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
            // actualizar contraseña si se proporcionó
            if (!newPassword.isNullOrEmpty()) {
                auth.currentUser?.updatePassword(newPassword)?.await()
            }
            // actualizar datos en firestore
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

private fun deleteAccount(userId: String, context: Context, onSuccess: () -> Unit) {
    val auth = Firebase.auth
    val db = Firebase.firestore

    CoroutineScope(Dispatchers.IO).launch {
        try {
            // eliminar datos de firestore
            db.collection("users").document(userId).delete().await()
            // eliminar usuario de autenticación
            auth.currentUser?.delete()?.await()
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Cuenta eliminada", Toast.LENGTH_SHORT).show()
                onSuccess()
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Error al eliminar: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}