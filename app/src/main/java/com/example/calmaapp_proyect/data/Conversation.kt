package com.example.calmaapp_proyect.data

import com.google.firebase.firestore.DocumentId
import java.util.Date

data class Conversation(
    @DocumentId val conversationId: String = "",
    val userId: String = "",
    val messages: List<ChatMessage> = emptyList(),
    val timestamp: Date = Date(),
    val title: String = "Conversación del ${timestamp}" // Título por defecto
)

data class ChatMessage(
    val sender: String = "",
    val text: String = "",
    val timestamp: Date = Date()
)