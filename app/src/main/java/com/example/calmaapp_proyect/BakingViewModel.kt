package com.example.calmaapp_proyect

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.calmaapp_proyect.data.ChatMessage
import com.example.calmaapp_proyect.data.Conversation
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date

class BakingViewModel : ViewModel() {
    private val _uiState: MutableStateFlow<UiState> = MutableStateFlow(UiState.Initial)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = BuildConfig.apiKey,
        generationConfig = generationConfig {
            temperature = 0.8f
            topK = 1
            topP = 1f
            maxOutputTokens = 2048
        }
    )

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val userId: String? = auth.currentUser?.uid

    private var currentConversationMessages = mutableListOf<ChatMessage>()

    fun sendPrompt(prompt: String) {
        _uiState.value = UiState.Loading
        val userMessage = ChatMessage("Tú", prompt, Date())
        currentConversationMessages.add(userMessage)

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val enhancedPrompt = """
                    Eres un asistente emocional diseñado para ayudar a las personas a comprender y manejar sus emociones.
                    Responde solo a preguntas y conversaciones relacionadas con emociones, sentimientos, estados de ánimo y bienestar emocional.
                    Si la pregunta no está relacionada con estos temas, responde de manera amigable indicando que solo puedes hablar de emociones.
                    Ejemplo de respuesta no emocional: "Entiendo, pero como soy un asistente emocional, me gustaría enfocarme en cómo te sientes. ¿Te gustaría hablar sobre tus emociones?".
                    Ahora, responde a la siguiente pregunta del usuario:
                    $prompt
                """.trimIndent()

                val response = generativeModel.generateContent(
                    content {
                        text(enhancedPrompt)
                    }
                )
                response.text?.let { outputContent ->
                    val assistantMessage = ChatMessage("Ryo", outputContent, Date())
                    currentConversationMessages.add(assistantMessage)
                    _uiState.value = UiState.Success(outputContent)
                    saveConversation() // Guardar la conversación después de la respuesta
                } ?: run {
                    _uiState.value = UiState.Error("Respuesta de la IA vacía.")
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.localizedMessage ?: "Error al obtener respuesta de la IA.")
            }
        }
    }

    private fun saveConversation() {
        userId?.let { uid ->
            val title = if (currentConversationMessages.isNotEmpty()) {
                val firstMessage = currentConversationMessages.first().text.take(20) + "..."
                "Conversación: $firstMessage"
            } else {
                "Conversación del ${Date()}"
            }
            val conversation = Conversation(userId = uid, messages = currentConversationMessages.toList(), title = title)
            db.collection("users")
                .document(uid)
                .collection("conversations")
                .add(conversation)
                .addOnSuccessListener { documentReference ->
                    Log.d("BakingViewModel", "Conversación guardada con ID: ${documentReference.id}")
                    currentConversationMessages.clear()
                }
                .addOnFailureListener { e ->
                    Log.w("BakingViewModel", "Error al guardar la conversación", e)
                }
        }
    }
}