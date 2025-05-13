package com.example.calmaapp_proyect

import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

object AppState {
    var profileImageUri by mutableStateOf<Uri?>(null)
        private set

    fun updateProfileImage(uri: Uri?) {
        profileImageUri = uri
    }
}