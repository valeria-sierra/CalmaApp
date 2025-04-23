package com.example.calmaapp_proyect.data

import com.google.firebase.firestore.DocumentId
import java.util.Date

data class Conversation(
    @DocumentId val conversationId: String = "", // Firestore generará el ID
    val userId: String = "", // ID del usuario autenticado
    val messages: List<ChatMessage> = emptyList(), // Lista de mensajes en la conversación
    val timestamp: Date = Date() // Marca de tiempo de la conversación
)

data class ChatMessage(
    val sender: String = "", // "Usuario" o "Asistente"
    val text: String = "",
    val timestamp: Date = Date() // Marca de tiempo del mensaje
)