package com.example.calmaapp_proyect

import android.content.Context
import android.net.Uri
import java.util.UUID
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.edit
import android.util.Log
// Para Firestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

// Para Storage
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class ProfileImageManager(private val context: Context) {
    private val prefs = context.getSharedPreferences("profile_prefs", Context.MODE_PRIVATE)
    private val storage = FirebaseStorage.getInstance()
    private val db = Firebase.firestore

    fun saveImageUri(uri: Uri, userId: String, onSuccess: (String) -> Unit, onError: (Exception) -> Unit) {
        // 1. Subir imagen a Firebase Storage
        val storageRef = storage.reference
        val imageRef = storageRef.child("users/$userId/profile_${UUID.randomUUID()}.jpg")

        imageRef.putFile(uri)
            .addOnSuccessListener { taskSnapshot ->
                // 2. Obtener URL de descarga
                imageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    val imageUrl = downloadUri.toString()

                    // 3. Guardar URL en Firestore
                    db.collection("users").document(userId)
                        .update("profileImageUrl", imageUrl)
                        .addOnSuccessListener {
                            // 4. Guardar en SharedPreferences y AppState
                            prefs.edit { putString("profile_image_url", imageUrl) }
                            AppState.updateProfileImage(downloadUri)
                            onSuccess(imageUrl)
                        }
                        .addOnFailureListener { e -> onError(e) }
                }
            }
            .addOnFailureListener { e -> onError(e) }
    }

    fun getImageUri(userId: String, onSuccess: (Uri?) -> Unit, onError: (Exception) -> Unit) {
        // 1. Intentar cargar desde Firestore
        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                val imageUrl = document.getString("profileImageUrl")
                if (imageUrl != null) {
                    // 2. Actualizar SharedPreferences y AppState
                    prefs.edit { putString("profile_image_url", imageUrl) }
                    AppState.updateProfileImage(Uri.parse(imageUrl))
                    onSuccess(Uri.parse(imageUrl))
                } else {
                    onSuccess(null)
                }
            }
            .addOnFailureListener { e -> onError(e) }
    }
}
