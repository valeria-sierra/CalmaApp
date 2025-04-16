package com.example.calmaapp_proyect

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DisclaimerScreen(onAcceptDisclaimer: () -> Unit) {
    Image(
        painter = painterResource(id = R.drawable.ic_background), // Reemplaza con tu imagen de fondo
        contentDescription = "Fondo de la pantalla de aviso",
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween // Espacio entre logo, aviso y botón
    ) {
        // Logo en la parte superior
        Image(
            painter = painterResource(id = R.drawable.ic_logo), // Reemplaza con el nombre de tu logo
            contentDescription = "Logo de Calma App",
            modifier = Modifier
                .height(120.dp)
                .padding(top = 32.dp)
        )

        // Contenedor para "Aviso Importante" y el cuerpo del texto
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .background(Color(0xFF5CC2C6), RoundedCornerShape(8.dp)) // Fondo sólido
                    .padding(8.dp)
                    .fillMaxWidth(), // Ocupa todo el ancho disponible
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Aviso Importante",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Debes recordar que esta aplicación es una herramienta de ayuda emocional y no sustituye un proceso psicoterapéutico.",
                style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 24.sp, fontSize = 16.sp),
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp) // Añade padding horizontal al texto
            )
            Text(
                text = "Esperamos utilices de manera responsable las herramientas dadas.",
                style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 24.sp, fontSize = 16.sp),
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp) // Añade padding horizontal al texto
            )
            Spacer(modifier = Modifier.height(24.dp))
            // Botón "Acepto" ahora dentro de esta columna
            Button(
                onClick = onAcceptDisclaimer,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp), // Añade padding horizontal al botón
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = Color(0xFF5CC2C6)),
                contentPadding = PaddingValues(horizontal = 32.dp, vertical = 12.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Acepto", color = Color.White, fontSize = 18.sp)
            }
        }

        Spacer(modifier = Modifier.height(32.dp)) // Espacio inferior
    }
}