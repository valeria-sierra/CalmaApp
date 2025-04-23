// MainActivity.kt
package com.example.calmaapp_proyect

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.calmaapp_proyect.ui.theme.Calmaapp_proyectTheme
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    FirebaseApp.initializeApp(this)
    setContent {
      Calmaapp_proyectTheme {
        val navController = rememberNavController()
        Surface(
          modifier = Modifier.fillMaxSize(),
          color = MaterialTheme.colorScheme.background
        ) {
          NavHost(navController = navController, startDestination = "welcome") {
            composable("welcome") {
              WelcomeScreen(
                onSignInClick = { navController.navigate("login") },
                onSignUpClick = { navController.navigate("register") }
              )
            }
            composable("login") {
              LoginScreen(
                onLoginSuccess = { navController.navigate("disclaimer") },
                onNavigateToRegister = { navController.navigate("register") }
              )
            }
            composable("register") {
              RegisterScreen(onNavigateToLogin = { navController.popBackStack() })
            }
            composable("disclaimer") {
              DisclaimerScreen(onAcceptDisclaimer = { navController.navigate("main_app") })
            }
            composable("main_app") {
              BakingScreen(navController = navController) // Pasar el navController
            }
            composable("history") {
              HistoryScreen(onBack = { navController.popBackStack() })
            }
          }
        }
      }
    }
  }
}