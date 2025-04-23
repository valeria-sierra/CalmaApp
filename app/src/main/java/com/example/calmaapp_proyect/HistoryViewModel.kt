package com.example.calmaapp_proyect

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.calmaapp_proyect.data.Conversation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class HistoryViewModel : ViewModel() {
    private val _conversations = MutableStateFlow<List<Conversation>>(emptyList())
    val conversations: StateFlow<List<Conversation>> = _conversations.asStateFlow()

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val userId: String? = auth.currentUser?.uid

    init {
        loadConversations()
    }

    private fun loadConversations() {
        userId?.let { uid ->
            db.collection("users")
                .document(uid)
                .collection("conversations")
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.w("HistoryViewModel", "Error al escuchar las conversaciones", error)
                        return@addSnapshotListener
                    }
                    snapshot?.let {
                        val conversationList = it.documents.mapNotNull { doc ->
                            try {
                                doc.toObject(Conversation::class.java)
                            } catch (e: Exception) {
                                Log.e("HistoryViewModel", "Error al convertir documento a Conversación", e)
                                null
                            }
                        }
                        _conversations.value = conversationList
                    }
                }
        }
    }

    fun deleteConversation(conversationId: String) {
        userId?.let { uid ->
            db.collection("users")
                .document(uid)
                .collection("conversations")
                .document(conversationId)
                .delete()
                .addOnSuccessListener {
                    Log.d("HistoryViewModel", "Conversación con ID $conversationId borrada.")
                }
                .addOnFailureListener { e ->
                    Log.w("HistoryViewModel", "Error al borrar la conversación con ID $conversationId", e)
                }
        }
    }
}