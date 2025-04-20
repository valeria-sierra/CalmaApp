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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(onNavigateToLogin: () -> Unit) {
    Image(
        painter = painterResource(id = R.drawable.ic_background),
        contentDescription = "Fondo de la pantalla de registro",
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_logo),
            contentDescription = "Logo de Calma App",
            modifier = Modifier
                .height(120.dp)
                .padding(top = 32.dp)
        )

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
                    .height(48.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                androidx.compose.foundation.layout.Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        onClick = { /* Ya estamos en registro */ },
                        modifier = Modifier
                            .fillMaxWidth(0.6f)
                            .align(Alignment.CenterEnd)
                            .widthIn(min = 120.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5CC2C6))
                    ) {
                        Text("Regístrate", color = Color.White, textAlign = TextAlign.Center)
                    }
                    Button(
                        onClick = onNavigateToLogin,
                        modifier = Modifier
                            .fillMaxWidth(0.5f)
                            .align(Alignment.CenterStart)
                            .widthIn(min = 120.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
                    ) {
                        Text("Inicia Sesión", color = Color.White, textAlign = TextAlign.Center)
                    }
                }
            }

            var names by remember { mutableStateOf("") }
            TextField(
                value = names,
                onValueChange = { names = it },
                label = { Text("Nombres:", color = Color.White) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.textFieldColors(
                    cursorColor = Color.White,
                    focusedIndicatorColor = Color.White,
                    unfocusedIndicatorColor = Color.White,
                    disabledIndicatorColor = Color.White,
                    focusedLabelColor = Color(0xFF5CC2C6),
                    unfocusedLabelColor = Color.White,
                    containerColor = Color.Transparent
                )
            )

            var lastNames by remember { mutableStateOf("") }
            TextField(
                value = lastNames,
                onValueChange = { lastNames = it },
                label = { Text("Apellidos:", color = Color.White) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.textFieldColors(
                    cursorColor = Color.White,
                    focusedIndicatorColor = Color.White,
                    unfocusedIndicatorColor = Color.White,
                    disabledIndicatorColor = Color.White,
                    focusedLabelColor = Color(0xFF5CC2C6),
                    unfocusedLabelColor = Color.White,
                    containerColor = Color.Transparent
                )
            )

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
                    containerColor = Color.Transparent
                )
            )

            var phoneNumber by remember { mutableStateOf("") }
            TextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                label = { Text("Número de teléfono:", color = Color.White) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.textFieldColors(
                    cursorColor = Color.White,
                    focusedIndicatorColor = Color.White,
                    unfocusedIndicatorColor = Color.White,
                    disabledIndicatorColor = Color.White,
                    focusedLabelColor = Color(0xFF5CC2C6),
                    unfocusedLabelColor = Color.White,
                    containerColor = Color.Transparent
                )
            )

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
                    containerColor = Color.Transparent
                )
            )

            var confirmPassword by remember { mutableStateOf("") }
            TextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirma contraseña:", color = Color.White) },
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
                    containerColor = Color.Transparent
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            var errorMessage by remember { mutableStateOf<String?>(null) }
            val auth: FirebaseAuth = Firebase.auth
            val db: FirebaseFirestore = Firebase.firestore
            val coroutineScope = rememberCoroutineScope()

            Button(
                onClick = {
                    if (password == confirmPassword) {
                        coroutineScope.launch {
                            val emailExists = checkEmailExists(email)
                            val phoneExists = checkPhoneExists(phoneNumber)

                            if (emailExists) {
                                errorMessage = "Este correo electrónico ya está registrado."
                            } else if (phoneExists) {
                                errorMessage = "Este número de teléfono ya está registrado."
                            } else {
                                // Correo y teléfono no existen, proceder con el registro
                                auth.createUserWithEmailAndPassword(email, password)
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            // Registro exitoso, guardar datos adicionales en Firestore
                                            Log.d("Register", "createUserWithEmail:success")
                                            val user = auth.currentUser
                                            user?.let {
                                                val userData = hashMapOf(
                                                    "names" to names,
                                                    "lastNames" to lastNames,
                                                    "phoneNumber" to phoneNumber,
                                                    "email" to email
                                                )
                                                db.collection("users")
                                                    .document(it.uid)
                                                    .set(userData)
                                                    .addOnSuccessListener {
                                                        Log.d("Firestore", "User data saved successfully")
                                                        onNavigateToLogin()
                                                    }
                                                    .addOnFailureListener { e ->
                                                        Log.w("Firestore", "Error saving user data", e)
                                                        errorMessage = "Error al guardar los datos del usuario."
                                                        // Opcional: Borrar el usuario recién creado si falla el guardado de datos
                                                        it.delete()
                                                    }
                                            }
                                        } else {
                                            // Si falla el registro, muestra el error
                                            Log.w("Register", "createUserWithEmail:failure", task.exception)
                                            errorMessage = task.exception?.localizedMessage ?: "Error al registrar usuario."
                                        }
                                    }
                            }
                        }
                    } else {
                        errorMessage = "Las contraseñas no coinciden."
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = Color(0xFF5CC2C6))
            ) {
                Text("Regístrate", color = Color.White)
            }

            errorMessage?.let {
                AlertDialog(
                    onDismissRequest = { errorMessage = null },
                    title = { Text("Error de Registro") },
                    text = { Text(it) },
                    confirmButton = {
                        TextButton(onClick = { errorMessage = null }) {
                            Text("Aceptar")
                        }
                    }
                )
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}

suspend fun checkEmailExists(email: String): Boolean {
    val db = Firebase.firestore
    return try {
        val snapshot = db.collection("users")
            .whereEqualTo("email", email)
            .get()
            .await()
        snapshot.documents.isNotEmpty()
    } catch (e: Exception) {
        Log.w("Register", "Error checking email existence", e)
        false
    }
}

suspend fun checkPhoneExists(phoneNumber: String): Boolean {
    val db = Firebase.firestore
    return try {
        val snapshot = db.collection("users")
            .whereEqualTo("phoneNumber", phoneNumber)
            .get()
            .await()
        snapshot.documents.isNotEmpty()
    } catch (e: Exception) {
        Log.w("Register", "Error checking phone existence", e)
        false
    }
}