package com.example.calmaapp_proyect

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp

@Composable
fun WelcomeScreen(onSignInClick: () -> Unit, onSignUpClick: () -> Unit) {
    Image(
        painter = painterResource(id = R.drawable.ic_background), // Asegúrate de tener esta imagen en tus recursos (drawable)
        contentDescription = "Imagen de bienvenida de Calma App",
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        Text(
            text = "CALMA APP",
            style = MaterialTheme.typography.headlineLarge,
            color = Color.White,
            textAlign = TextAlign.Center
        )
        Text(
            text = "UN RESPIRO A LA VEZ",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 32.dp)
        )
        Button(
            onClick = onSignInClick,
            modifier = Modifier
                .padding(bottom = 16.dp)
                .fillMaxWidth()
        ) {
            Text(text = "Inicia Sesión")
        }
        Text(
            buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        color = Color.White
                    )
                ) {
                    append("¿Aún no tienes una cuenta? ")
                }
                withStyle(
                    style = SpanStyle(
                        color = Color.White,
                        textDecoration = TextDecoration.Underline
                    )
                ) {
                    append("Regístrate")
                }
            },
            modifier = Modifier.clickable(onClick = onSignUpClick)
        )
    }
}