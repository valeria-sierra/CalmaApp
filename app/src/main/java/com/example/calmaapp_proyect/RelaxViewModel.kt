package com.example.calmaapp_proyect

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class TecnicaRelajacion(
    val nombre: String = "",
    val descripcion: String = "",
    val imagen: String = "",
    val pantalla: String = "",
    val documentId: String = ""
)

class RelaxViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val _tecnicas = MutableStateFlow<List<TecnicaRelajacion>>(emptyList())
    val tecnicas: StateFlow<List<TecnicaRelajacion>> = _tecnicas

    private val _saved = MutableStateFlow<List<TecnicaRelajacion>>(emptyList())
    val saved: StateFlow<List<TecnicaRelajacion>> = _saved

    private val _completed = MutableStateFlow<List<TecnicaRelajacion>>(emptyList())
    val completed: StateFlow<List<TecnicaRelajacion>> = _completed

    init {
        fetchTecnicas()
        fetchSaved()
        fetchCompleted()
    }

    private fun fetchTecnicas() {
        firestore.collection("tecnicas_relajacion")
            .get()
            .addOnSuccessListener { result ->
                val lista = result.map { doc ->
                    val tecnica = doc.toObject(TecnicaRelajacion::class.java)
                    tecnica.copy(documentId = doc.id)
                }
                _tecnicas.value = lista
            }
            .addOnFailureListener { e ->
                Log.e("Firebase", "Error al obtener técnicas de relajación", e)
            }
    }

    private fun fetchSaved() {
        val userId = auth.currentUser?.uid ?: return
        firestore.collection("users").document(userId).collection("saved")
            .get()
            .addOnSuccessListener { result ->
                val lista = result.mapNotNull { doc ->
                    val nombre = doc.getString("nombre")
                    _tecnicas.value.find { it.nombre == nombre }
                }
                _saved.value = lista
            }
            .addOnFailureListener { e ->
                Log.e("Firebase", "Error al obtener técnicas guardadas", e)
            }
    }

    private fun fetchCompleted() {
        val userId = auth.currentUser?.uid ?: return
        firestore.collection("users").document(userId).collection("completed")
            .get()
            .addOnSuccessListener { result ->
                val lista = result.mapNotNull { doc ->
                    val nombre = doc.getString("nombre")
                    _tecnicas.value.find { it.nombre == nombre }
                }
                _completed.value = lista
            }
            .addOnFailureListener { e ->
                Log.e("Firebase", "Error al obtener técnicas completadas", e)
            }
    }

    fun saveCard(tecnica: TecnicaRelajacion) {
        val userId = auth.currentUser?.uid ?: return
        val data = mapOf("nombre" to tecnica.nombre)

        firestore.collection("users").document(userId)
            .collection("saved").document(tecnica.nombre)
            .set(data)
            .addOnSuccessListener {
                fetchSaved()
                Log.d("Firebase", "Técnica guardada con éxito")
            }
            .addOnFailureListener { e ->
                Log.e("Firebase", "Error al guardar técnica", e)
            }
    }

    fun completeCard(tecnica: TecnicaRelajacion) {
        val userId = auth.currentUser?.uid ?: return
        val data = mapOf("nombre" to tecnica.nombre)

        firestore.collection("users").document(userId)
            .collection("completed").document(tecnica.nombre)
            .set(data)
            .addOnSuccessListener {
                fetchCompleted()
                Log.d("Firebase", "Técnica completada con éxito")
            }
            .addOnFailureListener { e ->
                Log.e("Firebase", "Error al marcar como completada", e)
            }
    }

    fun deleteSavedCard(nombre: String) {
        val userId = auth.currentUser?.uid ?: return

        firestore.collection("users").document(userId)
            .collection("saved").document(nombre)
            .delete()
            .addOnSuccessListener {
                fetchSaved()
                Log.d("Firebase", "Guardado eliminado")
            }
            .addOnFailureListener { e ->
                Log.e("Firebase", "Error al eliminar guardado", e)
            }
    }

    fun deleteCompletedCard(nombre: String) {
        val userId = auth.currentUser?.uid ?: return

        firestore.collection("users").document(userId)
            .collection("completed").document(nombre)
            .delete()
            .addOnSuccessListener {
                fetchCompleted()
                Log.d("Firebase", "Completado eliminado")
            }
            .addOnFailureListener { e ->
                Log.e("Firebase", "Error al eliminar completado", e)
            }
    }

    fun getTecnicaByNombre(nombre: String): TecnicaRelajacion? {
        return _tecnicas.value.find { it.nombre == nombre }
    }
}
