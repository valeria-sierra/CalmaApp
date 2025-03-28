package com.example.calmaapp_proyect

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BakingViewModel : ViewModel() {
  private val _uiState: MutableStateFlow<UiState> =
    MutableStateFlow(UiState.Initial)
  val uiState: StateFlow<UiState> =
    _uiState.asStateFlow()

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

  fun sendPrompt(prompt: String) {
    _uiState.value = UiState.Loading

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
          _uiState.value = UiState.Success(outputContent)
        }
      } catch (e: Exception) {
        _uiState.value = UiState.Error(e.localizedMessage ?: "")
      }
    }
  }
}