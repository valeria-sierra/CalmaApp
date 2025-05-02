package com.example.calmaapp_proyect

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.calmaapp_proyect.data.Conversation

@Composable
fun HistoryScreen(historyViewModel: HistoryViewModel = viewModel(), onBack: () -> Unit) {
    val conversations by historyViewModel.conversations.collectAsState(initial = emptyList())
    var showDeleteConfirmationDialog by remember { mutableStateOf(false) }
    var conversationToDelete by remember { mutableStateOf<Conversation?>(null) }
    var expandedConversationId by remember { mutableStateOf<String?>(null) }

    // Reemplaza R.drawable.background_image con el ID de tu imagen de fondo
    Image(
        painter = painterResource(id = R.drawable.ic_background),
        contentDescription = null,
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
    )

    Column(modifier = Modifier.fillMaxSize()) {
        // Barra superior roja con la "X" y el título
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF5CC2C6))
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Filled.Close, contentDescription = "Volver", tint = Color.White)
            }
            Text(
                text = "Historial de Conversaciones",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White
            )
            Spacer(modifier = Modifier.size(60.dp))

        }

        if (conversations.isEmpty()) {
            Text(
                "No hay conversaciones guardadas.",
                modifier = Modifier.padding(16.dp),
                color = Color.Black // Asegúrate que el texto sea visible sobre el fondo
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp) // Espacio entre las conversaciones
            ) {
                items(conversations) { conversation ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                expandedConversationId =
                                    if (expandedConversationId == conversation.conversationId) null else conversation.conversationId
                            },
                        shape = RoundedCornerShape(8.dp),
                        color = Color.White,
                        tonalElevation = 2.dp // Pequeña elevación para dar un efecto de tarjeta
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = conversation.title,
                                    modifier = Modifier.weight(1f),
                                    color = Color.Black
                                )
                                IconButton(onClick = {
                                    conversationToDelete = conversation
                                    showDeleteConfirmationDialog = true
                                }) {
                                    Icon(Icons.Filled.Delete, contentDescription = "Borrar", tint = Color.Gray)
                                }
                            }
                            AnimatedVisibility(
                                visible = expandedConversationId == conversation.conversationId,
                                enter = expandVertically(animationSpec = tween(durationMillis = 300)),
                                exit = shrinkVertically(animationSpec = tween(durationMillis = 300))
                            ) {
                                Column(modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp)) {
                                    conversation.messages.forEach { message ->
                                        Text(text = "${message.sender}: ${message.text}", color = Color.Black)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showDeleteConfirmationDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteConfirmationDialog = false },
                title = { Text("Confirmar Borrado", color = Color.Black) },
                text = { Text("¿Estás seguro de que quieres borrar esta conversación?", color = Color.Black) },
                confirmButton = {
                    TextButton(onClick = {
                        conversationToDelete?.let { historyViewModel.deleteConversation(it.conversationId) }
                        showDeleteConfirmationDialog = false
                        conversationToDelete = null
                    }) {
                        Text("Borrar", color = Color.Red)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteConfirmationDialog = false }) {
                        Text("Cancelar", color = Color.Gray)
                    }
                }
            )
        }
    }
}