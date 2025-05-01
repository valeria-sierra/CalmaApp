package com.example.calmaapp_proyect
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.collectAsState
import androidx.compose.material.icons.filled.Clear
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.TextField
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.calmaapp_proyect.AccountScreen
import com.example.calmaapp_proyect.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BakingScreen(
    onLogout: () -> Unit = {},
    bakingViewModel: BakingViewModel = viewModel(),
    navController: NavHostController = rememberNavController()
) {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Home) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.Transparent
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_background), // Reemplaza con tu imagen de fondo
            contentDescription = "Fondo de la aplicación",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Scaffold(
            topBar = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start // Alinea el icono a la izquierda
                ) {
                    IconButton(onClick = { currentScreen = Screen.Profile }, modifier = Modifier.size(48.dp)) { // Aumenta el tamaño del icono
                        Icon(
                            imageVector = Icons.Filled.AccountCircle,
                            contentDescription = "Perfil",
                            tint = Color.White,
                            modifier = Modifier.size(36.dp) // Ajusta el tamaño del icono dentro del botón
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f)) // Ocupa el espacio restante
                }
            },
            bottomBar = {
                BottomAppBar(
                    modifier = Modifier.fillMaxWidth(), // Asegúrate de que ocupe todo el ancho
                    containerColor = Color(0xFF5CC2C6) // Establece el color de fondo aquí
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround // Distribuye los iconos uniformemente
                    ) {
                        IconButton(onClick = { currentScreen = Screen.Home }) {
                            Icon(Icons.Filled.Home, contentDescription = "Inicio", tint = Color.White)
                        }
                        IconButton(onClick = { currentScreen = Screen.Calendar }) {
                            Icon(Icons.Filled.DateRange, contentDescription = "Calendario", tint = Color.White)
                        }
                        IconButton(onClick = { currentScreen = Screen.Emotion }) {
                            Icon(Icons.Filled.Favorite, contentDescription = "Ayuda emocional", tint = Color.White)
                        }
                        IconButton(onClick = { currentScreen = Screen.Notification }) {
                            Icon(Icons.Filled.Notifications, contentDescription = "Notificaciones", tint = Color.White)
                        }
                        IconButton(onClick = { currentScreen = Screen.Call }) {
                            Icon(Icons.Filled.Call, contentDescription = "Llamadas", tint = Color.White)
                        }
                    }
                }
            },
            containerColor = Color.Transparent,
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top // Alinea los elementos desde la parte superior
            ) {
                when (currentScreen) {
                    Screen.Home -> HomeScreen(onStartChat = { currentScreen = Screen.Chat }, navController = navController)
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
}

@Composable
fun HomeScreen(onStartChat: () -> Unit, navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp), // Añade un poco de padding horizontal
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(3.dp, Alignment.Top) // Alinea desde la parte superior
    ) {
        Spacer(modifier = Modifier.height(0.dp)) // Espacio para el icono de perfil

        Image(
            painter = painterResource(id = R.drawable.ic_logo), // Reemplaza con tu logo
            contentDescription = "Logo de Calma App",
            modifier = Modifier.size(170.dp)
        )

        Text(
            text = "¿en qué puedo ayudarte?",
            style = MaterialTheme.typography.bodyLarge.copy(color = Color.White)
        )

        Image(
            painter = painterResource(id = R.drawable.ic_chatbot), // Reemplaza con tu imagen del chatbot
            contentDescription = "Imagen del Chatbot",
            modifier = Modifier.size(120.dp)
        )

        val buttonWidth = 200.dp // Define un ancho común para los botones

        Button(
            onClick = onStartChat,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5CC2C6)),
            modifier = Modifier.width(buttonWidth)
        ) {
            Text(text = "Iniciar chat")
        }

        Image(
            painter = painterResource(id = R.drawable.ic_chatbot), // Reemplaza con tu imagen del chatbot
            contentDescription = "Imagen del Chatbot",
            modifier = Modifier.size(120.dp)
        )

        Button(
            onClick = { navController.navigate("history") },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5CC2C6)),
            modifier = Modifier.width(buttonWidth)
        ) {
            Text(text = "Historial de chats")
        }

        Spacer(modifier = Modifier.weight(2f)) // Empuja el contenido hacia arriba
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

@androidx.compose.ui.tooling.preview.Preview(showSystemUi = true)
@Composable
fun BakingScreenPreview() {
    WelcomeScreen(onSignInClick = {}, onSignUpClick = {})
}