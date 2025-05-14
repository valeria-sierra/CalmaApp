package com.example.calmaapp_proyect

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PriorityHigh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.net.URLEncoder


data class LineaCrisis(
    val id: String = "",
    val nombre: String = "",
    val telefono: String = "",
    val horario: String = "",
    val whatsapp: String? = null
)
data class ContactoEmergencia(
    val id: String = "",
    val nombre: String = "",
    val apellido: String = "",
    val telefono: String = "",
    val parentesco: String = "",
    val uidUsuario: String = ""
)

@Composable
fun CallsScreen(
    onAddContact: () -> Unit,
    onEditContact: (ContactoEmergencia) -> Unit
) {
    var selectedTab by remember { mutableStateOf("lineas") }
    var lineasDeCrisis by remember { mutableStateOf<List<LineaCrisis>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var contactosPersonales by remember { mutableStateOf<List<ContactoEmergencia>>(emptyList()) }
    val context = LocalContext.current

    // Estado para controlar si se muestra la pantalla de selección de contacto de emergencia
    var mostrarPantallaEmergencia by remember { mutableStateOf(false) }

    // Inicializar Firebase Firestore
    val firestore = FirebaseFirestore.getInstance()

    // Función para cargar líneas de crisis
    fun loadCrisisLines() {
        isLoading = true
        errorMessage = null

        firestore.collection("lineas_crisis")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val lineasList = mutableListOf<LineaCrisis>()
                for (document in querySnapshot) {
                    val linea = LineaCrisis(
                        id = document.id,
                        nombre = document.getString("nombre") ?: "",
                        telefono = document.getString("telefono") ?: "",
                        horario = document.getString("horario") ?: "",
                        whatsapp = document.getString("whatsapp")
                    )
                    lineasList.add(linea)
                    Log.d("Firestore", "Documento cargado: ${linea.nombre}")
                }
                lineasDeCrisis = lineasList
                isLoading = false
            }
            .addOnFailureListener { e ->
                errorMessage = "Error al cargar datos: ${e.localizedMessage}"
                isLoading = false
                Log.e("Firestore", "Error: $errorMessage")
                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
            }
    }
    fun loadContactos() {
        isLoading = true
        errorMessage = null
        val user = FirebaseAuth.getInstance().currentUser

        if (user != null) {
            firestore.collection("contactos_emergencia")
                .whereEqualTo("uidUsuario", user.uid)
                .get()
                .addOnSuccessListener { result ->
                    val contactos = result.map { doc ->
                        ContactoEmergencia(
                            id = doc.id,
                            nombre = doc.getString("nombre") ?: "",
                            apellido = doc.getString("apellido") ?: "",
                            telefono = doc.getString("telefono") ?: "",
                            parentesco = doc.getString("parentesco") ?: "",
                            uidUsuario = doc.getString("uidUsuario") ?: ""
                        )
                    }
                    contactosPersonales = contactos
                    isLoading = false
                }
                .addOnFailureListener { e ->
                    errorMessage = "Error al cargar contactos: ${e.message}"
                    isLoading = false
                    Log.e("Firebase", "Error cargando contactos: ${e.message}")
                    Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                }
        }
    }

    // Cargar datos al iniciar o cambiar pestaña
    LaunchedEffect(selectedTab) {
        if (selectedTab == "lineas") {
            loadCrisisLines()
        } else {
            loadContactos()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(0.dp))

            Image(
                painter = painterResource(id = R.drawable.ic_logo),
                contentDescription = "Logo Calma App",
                modifier = Modifier
                    .size(110.dp)
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(0.dp))

            CrisisTitle()

            Spacer(modifier = Modifier.height(4.dp))

            CrisisActionRow(onClickEmergencia = { mostrarPantallaEmergencia = true })

            Spacer(modifier = Modifier.height(6.dp))

            EmergencySection(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it },
                onEditContact = onEditContact,
                lineasDeCrisis = lineasDeCrisis,
                contactosPersonales = contactosPersonales,
                isLoading = isLoading,
                errorMessage = errorMessage
            )

            Spacer(modifier = Modifier.height(60.dp))
        }

        if (selectedTab == "contactos") {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            ) {
                AddContactButton(onClick = onAddContact)
            }
        }
        if (mostrarPantallaEmergencia) {
            EmergenciaContactosScreen(
                contactos = contactosPersonales,
                onContactoSeleccionado = { contacto ->
                    enviarMensajeWhatsApp(context, contacto.telefono)
                    mostrarPantallaEmergencia = false // Opcional: Ocultar la pantalla después de seleccionar
                },
                onCancelar = { mostrarPantallaEmergencia = false },

                )
        }
    }
}

@Composable
fun CrisisTitle() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF5CC2C6))
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "¿Tienes una crisis?",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )
    }
}

@Composable
fun CrisisActionRow(onClickEmergencia: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        Button(
            onClick = onClickEmergencia,
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
            modifier = Modifier.size(90.dp)
        ) {
            Icon(
                imageVector = Icons.Default.PriorityHigh,
                contentDescription = "Botón de emergencia",
                tint = Color(0xFF5CC2C6),
                modifier = Modifier.size(70.dp)
            )
        }

        Text(
            text = "Enviar mensaje automático a tus\ncontactos de emergencia\npara que te ayuden",
            color = Color.White,
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun EmergencySection(
    selectedTab: String,
    onTabSelected: (String) -> Unit,
    onEditContact: (ContactoEmergencia) -> Unit,
    lineasDeCrisis: List<LineaCrisis>,
    contactosPersonales: List<ContactoEmergencia>,
    isLoading: Boolean,
    errorMessage: String?
) {
    val context = LocalContext.current
    val bottomPadding = if (selectedTab == "contactos") 80.dp else 8.dp
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(bottom = bottomPadding),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            LineasYContactosTabs(selectedTab = selectedTab, onTabSelected = onTabSelected)

            Spacer(modifier = Modifier.height(16.dp))

            when {
                isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF5CC2C6))
                }
                errorMessage != null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = errorMessage, color = Color.Red)
                }

                selectedTab == "lineas" -> LazyColumn {
                    items(lineasDeCrisis) { linea ->
                        CrisisLineItem(
                            nombre = linea.nombre,
                            horario = linea.horario,
                            onLlamar = { val intent = Intent(Intent.ACTION_DIAL).apply {
                                data = Uri.parse("tel:${linea.telefono}")
                            }
                                context.startActivity(intent) },

                            onMensaje = {
                                val mensaje = "Hola, te puedes comunicar conmigo en este momento; no me siento muy bien y necesito de tu apoyo. Gracias"
                                val telefonoLimpio = linea.whatsapp?.replace("+", "")?.trim() ?: ""
                                if (telefonoLimpio.isNotBlank()) {
                                    val uri = Uri.parse("https://wa.me/$telefonoLimpio?text=${URLEncoder.encode(mensaje, "UTF-8")}")
                                    val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                                        setPackage("com.whatsapp")
                                    }
                                    try {
                                        context.startActivity(intent)
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "WhatsApp no está instalado", Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    Toast.makeText(context, "Esta línea no tiene número de WhatsApp disponible", Toast.LENGTH_SHORT).show()
                                }
                            },
                            tieneWhatsapp = !linea.whatsapp.isNullOrEmpty(),
                            esContactoPersonal = false
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
                selectedTab == "contactos" -> LazyColumn {
                    items(contactosPersonales) { contacto ->
                        val nombreCompleto = contacto.nombre + if (contacto.apellido.isNotBlank()) " ${contacto.apellido}" else ""
                        val tieneWhatsapp = contacto.telefono.startsWith("+57")

                        CrisisLineItem(
                            nombre = nombreCompleto,
                            horario = contacto.telefono,
                            onLlamar = {
                                val intent = Intent(Intent.ACTION_DIAL).apply {
                                    data = Uri.parse("tel:${contacto.telefono}")
                                }
                                context.startActivity(intent)
                            },
                            onMensaje = {
                                val mensaje = "Hola, te puedes comunicar conmigo en este momento; no me siento muy bien y necesito de tu apoyo. Gracias."
                                val telefonoLimpio = contacto.telefono.replace("+", "").trim()
                                if (telefonoLimpio.isNotBlank()) {
                                    val uri = Uri.parse("https://wa.me/$telefonoLimpio?text=${URLEncoder.encode(mensaje, "UTF-8")}")
                                    val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                                        setPackage("com.whatsapp")
                                    }
                                    try {
                                        context.startActivity(intent)
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "WhatsApp no está instalado", Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    Toast.makeText(context, "Esta línea no tiene número de WhatsApp disponible", Toast.LENGTH_SHORT).show()
                                }
                            },
                            tieneWhatsapp = tieneWhatsapp,
                            esContactoPersonal = true,
                            onEdit = { onEditContact(contacto) }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}
@Composable
fun EmergenciaContactosScreen(
    contactos: List<ContactoEmergencia>,
    onContactoSeleccionado: (ContactoEmergencia) -> Unit,
    onCancelar: () -> Unit
) {
    Image(
        painter = painterResource(id = R.drawable.ic_background), // Reemplaza con el nombre de tu imagen
        contentDescription = null,
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Selecciona un contacto de emergencia para pedir ayuda",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn {
            items(contactos) { contacto ->
                Button(
                    onClick = { onContactoSeleccionado(contacto) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5CC2C6)) // Color del botón contacto
                ) {
                    Text("${contacto.nombre} ${contacto.apellido}",
                        color = Color.White // Color del texto del contacto
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onCancelar,
            colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
        ){
            Text("Cancelar",color = Color.White)
        }
    }
}

fun enviarMensajeWhatsApp(context: Context, telefono: String) {
    val mensaje = "¡AYUDA! Necesito asistencia urgente."
    val telefonoLimpio = telefono.replace("+", "").trim()
    if (telefonoLimpio.isNotBlank()) {
        val uri = Uri.parse("https://wa.me/$telefonoLimpio?text=${URLEncoder.encode(mensaje, "UTF-8")}")
        val intent = Intent(Intent.ACTION_VIEW, uri).apply {
            setPackage("com.whatsapp")
        }
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "WhatsApp no está instalado", Toast.LENGTH_SHORT).show()
        }
    } else {
        Toast.makeText(context, "Número de WhatsApp no válido", Toast.LENGTH_SHORT).show()
    }
}

@Composable
fun LineasYContactosTabs(selectedTab: String, onTabSelected: (String) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Button(
            onClick = { onTabSelected("lineas") },
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (selectedTab == "lineas") Color(0xFF5CC2C6) else Color(0xFFD3D3D3)
            ),
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 2.dp)
                .height(48.dp)
        ) {
            Text("Líneas de crisis", color = Color.White, fontSize = 14.sp)
        }

        Button(
            onClick = { onTabSelected("contactos") },
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (selectedTab == "contactos") Color(0xFF5CC2C6) else Color(0xFFD3D3D3)
            ),
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 2.dp)
                .height(48.dp)
        ) {
            Text("Contactos", color = Color.White, fontSize = 14.sp)
        }
    }
}

@Composable
fun CrisisLineItem(
    nombre: String,
    horario: String,
    onLlamar: () -> Unit,
    onMensaje: () -> Unit,
    tieneWhatsapp: Boolean,
    esContactoPersonal: Boolean,
    onEdit: () -> Unit = {}
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF5CC2C6))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = nombre,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = horario,
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = onLlamar,
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.White, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Call,
                        contentDescription = "Llamar",
                        tint = Color(0xFF5CC2C6),
                        modifier = Modifier.size(24.dp)
                    )
                }

                if (tieneWhatsapp) {
                    IconButton(
                        onClick = onMensaje,
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color.White, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Chat,
                            contentDescription = "Mensaje",
                            tint = Color(0xFF5CC2C6),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                if (esContactoPersonal) {
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color.White, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Editar",
                            tint = Color(0xFF5CC2C6),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AddContactButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5CC2C6)),
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Añadir",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Añadir contacto de emergencia",
                color = Color.White,
                fontSize = 16.sp
            )
        }
    }
}