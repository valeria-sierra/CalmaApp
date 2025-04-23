package com.example.calmaapp_proyect

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.calmaapp_proyect.data.Conversation
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun HistoryScreen(historyViewModel: HistoryViewModel = viewModel(), onBack: () -> Unit) {
    val conversations by historyViewModel.conversations.collectAsState(initial = emptyList())
    var showDeleteConfirmationDialog by remember { mutableStateOf(false) }
    var conversationToDelete by remember { mutableStateOf<Conversation?>(null) }

    Column(modifier = Modifier.fillMaxSize()) {
        Button(onClick = onBack, modifier = Modifier.padding(8.dp)) {
            Text("Volver")
        }
        Text(
            text = "Historial de Conversaciones",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(16.dp)
        )

        if (conversations.isEmpty()) {
            Text(
                "No hay conversaciones guardadas.",
                modifier = Modifier.padding(16.dp)
            )
        } else {
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(conversations) { conversation ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .clickable {
                                // Opcional: Mostrar los mensajes de la conversación al hacer clic
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val dateFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                        Text(
                            text = "Conversación del ${dateFormatter.format(conversation.timestamp)}",
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = {
                            conversationToDelete = conversation
                            showDeleteConfirmationDialog = true
                        }) {
                            Icon(Icons.Filled.Delete, contentDescription = "Borrar")
                        }
                    }
                }
            }
        }

        if (showDeleteConfirmationDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteConfirmationDialog = false },
                title = { Text("Confirmar Borrado") },
                text = { Text("¿Estás seguro de que quieres borrar esta conversación?") },
                confirmButton = {
                    TextButton(onClick = {
                        conversationToDelete?.let { historyViewModel.deleteConversation(it.conversationId) }
                        showDeleteConfirmationDialog = false
                        conversationToDelete = null
                    }) {
                        Text("Borrar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteConfirmationDialog = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}