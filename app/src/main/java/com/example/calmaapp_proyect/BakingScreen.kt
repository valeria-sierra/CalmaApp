package com.example.calmaapp_proyect
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.calmaapp_proyect.AccountScreen



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BakingScreen(
    onLogout: () -> Unit = {},
    bakingViewModel: BakingViewModel = viewModel()
) {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Home) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Calma App") },
                actions = {
                    IconButton(onClick = { currentScreen = Screen.Profile }) {
                        Icon(imageVector = Icons.Filled.AccountCircle, contentDescription = "Perfil", tint = Color.White)
                    }
                },
                modifier = Modifier.background(Color(0xFF5CC2C6)) // Cambiar el color de la barra de herramientas
            )
        },
        bottomBar = {
            BottomAppBar(modifier = Modifier.background(Color(0xFF5CC2C6))) { // Cambiar el color de la barra de herramientas inferior
                IconButton(onClick = { currentScreen = Screen.Home }, modifier = Modifier.weight(1f)) {
                    Icon(imageVector = Icons.Filled.Home, contentDescription = "Inicio", tint = Color.White)
                }
                IconButton(onClick = { currentScreen = Screen.Calendar }, modifier = Modifier.weight(1f)) {
                    Icon(imageVector = Icons.Filled.DateRange, contentDescription = "Calendario", tint = Color.White)
                }
                IconButton(onClick = { currentScreen = Screen.Emotion }, modifier = Modifier.weight(1f)) {
                    Icon(imageVector = Icons.Filled.Favorite, contentDescription = "Ayuda emocional", tint = Color.White)
                }
                IconButton(onClick = { currentScreen = Screen.Notification }, modifier = Modifier.weight(1f)) {
                    Icon(imageVector = Icons.Filled.Notifications, contentDescription = "Notificaciones", tint = Color.White)
                }
                IconButton(onClick = { currentScreen = Screen.Call }, modifier = Modifier.weight(1f)) {
                    Icon(imageVector = Icons.Filled.Call, contentDescription = "Llamadas", tint = Color.White)
                }
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (currentScreen) {
                Screen.Home -> HomeScreen { currentScreen = Screen.Chat }
                Screen.Chat -> ChatScreen(bakingViewModel) { currentScreen = Screen.Home }
                Screen.Calendar -> GenericScreen(screenName = "Calendario")
                Screen.Emotion -> GenericScreen(screenName = "Ayuda emocional")
                Screen.Notification -> GenericScreen(screenName = "Notificaciones")
                Screen.Call -> GenericScreen(screenName = "Llamadas")
                Screen.Profile -> AccountScreen(
                    onBackClick = { currentScreen = Screen.Home },
                    onLogout = onLogout
                )
            }
        }
    }
}

@Composable
fun HomeScreen(onStartChat: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Calma App",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Text(
            text = "¿Te puedo ayudar en algo?",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 32.dp)
        )
        Button(
            onClick = onStartChat,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5CC2C6)) // Cambiar el color del botón
        ) {
            Text(text = "Iniciar chat")
        }
    }
}

@Composable
fun ChatScreen(bakingViewModel: BakingViewModel, onExitChat: () -> Unit) {
    val placeholderPrompt = stringResource(R.string.prompt_placeholder)
    var prompt by rememberSaveable { mutableStateOf(placeholderPrompt) }
    val uiState by bakingViewModel.uiState.collectAsState()
    val chatMessages = remember { mutableStateOf(mutableListOf<ChatMessage>()) }
    var assistantResponseAdded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(onClick = onExitChat) {
                Icon(imageVector = Icons.Filled.Clear, contentDescription = "Salir del chat", tint = Color.White)
            }
        }

        Text(
            text = stringResource(R.string.baking_title),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(16.dp)
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(16.dp)
        ) {
            items(chatMessages.value) { message ->
                Text(
                    text = "${message.sender}: ${message.text}",
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
        }

        Row(
            modifier = Modifier.padding(all = 16.dp)
        ) {
            TextField(
                value = prompt,
                label = { Text(stringResource(R.string.label_prompt)) },
                onValueChange = { prompt = it },
                modifier = Modifier
                    .weight(0.8f)
                    .padding(end = 16.dp)
                    .align(Alignment.CenterVertically)
            )

            Button(
                onClick = {
                    if (prompt.isNotEmpty()) {
                        chatMessages.value = (chatMessages.value + ChatMessage("Usuario", prompt)).toMutableList()
                        bakingViewModel.sendPrompt(prompt)
                        prompt = ""
                        assistantResponseAdded = false
                    }
                },
                enabled = prompt.isNotEmpty(),
                modifier = Modifier
                    .align(Alignment.CenterVertically)
            ) {
                Text(text = stringResource(R.string.action_go))
            }
        }

        if (uiState is UiState.Loading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else if (uiState is UiState.Success && !assistantResponseAdded) {
            chatMessages.value = (chatMessages.value + ChatMessage("Asistente", (uiState as UiState.Success).outputText)).toMutableList()
            assistantResponseAdded = true
        } else if (uiState is UiState.Error && !assistantResponseAdded) {
            chatMessages.value = (chatMessages.value + ChatMessage("Error", (uiState as UiState.Error).errorMessage)).toMutableList()
            assistantResponseAdded = true
        }
    }
}

@Composable
fun GenericScreen(screenName: String) {
    Text(text = "Pantalla de $screenName")
}

data class ChatMessage(val sender: String, val text: String)
enum class Screen {
    Home, Chat, Calendar, Emotion, Notification, Call, Profile
}

@Preview(showSystemUi = true)
@Composable
fun BakingScreenPreview() {
// Preview ahora muestra la WelcomeScreen
    WelcomeScreen(onSignInClick = {}, onSignUpClick = {})
}