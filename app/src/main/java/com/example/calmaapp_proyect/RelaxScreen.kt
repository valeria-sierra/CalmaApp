package com.example.calmaapp_proyect

import android.media.MediaPlayer
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.horizontalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import android.util.Log // Importa la clase Log
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.*
import androidx.compose.ui.text.style.TextAlign
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.roundToInt


@Composable
fun RelaxScreen(navController: NavHostController, relaxViewModel: RelaxViewModel = viewModel()) {

    val tecnicas by relaxViewModel.tecnicas.collectAsState()
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Técnicas de relajación",
                fontSize = 18.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Botones para ir a Guardados y Completados
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                IconBox(
                    icon = Icons.Filled.BookmarkBorder,
                    onClick = { navController.navigate("guardados") }
                )
                IconBox(
                    icon = Icons.Default.Check,
                    onClick = { navController.navigate("completados") }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "¿Qué necesitas?",
                fontSize = 18.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                tecnicas.forEach { tecnica ->
                    val context = LocalContext.current
                    val imageId = remember(tecnica.imagen) {
                        context.resources.getIdentifier(tecnica.imagen, "drawable", context.packageName)
                    }
                    RelaxCard(
                        nombre = tecnica.nombre,
                        image = painterResource(id = if (imageId != 0) imageId else R.drawable.ic_background),
                        onCardClick = {
                            val destino = tecnica.pantalla
                            if (destino.isNotEmpty()) {
                                navController.navigate(destino)
                            } else {
                                Log.w("Navegación", "Pantalla no especificada para ${tecnica.nombre}")
                            }
                        },
                        onSaveClick = {
                            relaxViewModel.saveCard(tecnica)
                        },
                        onCompleteClick = {
                            relaxViewModel.completeCard(tecnica)
                        }
                    )
                }
            }


            Spacer(modifier = Modifier.weight(1f))
        }
    }
}



@Composable
fun IconBox(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(95.dp)
            .background(Color(0xFF5CC2C6), shape = RoundedCornerShape(12.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(60.dp, 60.dp)
        )
    }
}

@Composable
fun RelaxCard(
    nombre: String,
    image: Painter,
    onCardClick: () -> Unit,
    onSaveClick: () -> Unit,
    onCompleteClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(270.dp)
            .height(370.dp)
            .background(Color(0xFFB2EBF2), shape = RoundedCornerShape(20.dp))
            .padding(12.dp)
            .clickable { onCardClick() }
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Image(
                painter = image,
                contentDescription = nombre,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
                contentScale = ContentScale.Crop
            )
            Column(Modifier.padding(horizontal = 12.dp)) {
                Text(text = nombre, fontSize = 20.sp, color = Color.White)
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(onClick = { onSaveClick() }) {
                        Icon(Icons.Filled.BookmarkBorder, contentDescription = "Guardar", tint = Color.White,modifier = Modifier.size(30.dp, 30.dp))
                    }
                    IconButton(onClick = { onCompleteClick() }) {
                        Icon(Icons.Default.Check, contentDescription = "Completar", tint = Color.White,modifier = Modifier.size(30.dp, 30.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun MainRelaxScreen() {
    val navController = rememberNavController()
    val relaxViewModel: RelaxViewModel = viewModel() // Initialize ViewModel

    NavHost(navController = navController, startDestination = "relaxScreen") {
        composable("relaxScreen") { RelaxScreen(navController, relaxViewModel) } // Pass the ViewModel
        composable("guardados") { SavedRelax(navController, relaxViewModel) }
        composable("completados") { CompletedRelax(navController, relaxViewModel) }
        composable("tecnica369Screen") { Tecnica369Screen(navController, relaxViewModel) }
        composable("relajacionMuscularScreen") { RelajacionMuscularScreen(navController, relaxViewModel) }
        composable("visualizacionGuiadaScreen") { VisualizacionGuiadaScreen(navController, relaxViewModel) }
    }
}

@Composable
fun SavedRelax(navController: NavHostController, relaxViewModel: RelaxViewModel = viewModel()) {
    val savedCards by relaxViewModel.saved.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                IconButton(onClick = { navController.navigate("relaxScreen") }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                }
                Text(
                    text = "Guardados",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (savedCards.isEmpty()) {
                Text(
                    text = "No has guardado ninguna técnica.",
                    fontSize = 18.sp,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            } else {
                // Muestra las tarjetas guardadas
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(savedCards) { card ->

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFB2EBF2), shape = RoundedCornerShape(20.dp))
                                .padding(12.dp)
                                .clickable {
                                    val destino = card.pantalla
                                    if (destino.isNotEmpty()) {
                                        navController.navigate(destino)
                                    }
                                }

                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {

                                Column(Modifier.padding(horizontal = 12.dp)) {
                                    Text(text = card.nombre, fontSize = 20.sp, color = Color.White)
                                }
                                Row(
                                    horizontalArrangement = Arrangement.End,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    IconButton(onClick = {
                                        relaxViewModel.deleteSavedCard(card.nombre)
                                    }) {
                                        Icon(Icons.Filled.Delete, contentDescription = "Eliminar", tint = Color.Gray, modifier = Modifier.size(30.dp, 30.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CompletedRelax(navController: NavHostController,relaxViewModel: RelaxViewModel = viewModel()) {
    val completedCards by relaxViewModel.completed.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                IconButton(onClick = { navController.navigate("relaxScreen") }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                }
                Text(
                    text = "Completados",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (completedCards.isEmpty()) {
                Text(
                    text = "No has completado ninguna técnica.",
                    fontSize = 18.sp,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            } else {
                // Muestra las tarjetas completadas
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(completedCards) { card ->

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFB2EBF2), shape = RoundedCornerShape(20.dp))
                                .padding(12.dp)
                                .clickable {
                                    val destino = card.pantalla
                                    if (destino.isNotEmpty()) {
                                        navController.navigate(destino)
                                    }
                                }

                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {

                                Column(Modifier.padding(horizontal = 12.dp)) {
                                    Text(text = card.nombre, fontSize = 18.sp, color = Color.White)
                                }
                                Row(
                                    horizontalArrangement = Arrangement.End,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    IconButton(onClick = {
                                        relaxViewModel.deleteCompletedCard(card.nombre)
                                    }) {
                                        Icon(Icons.Filled.Delete, contentDescription = "Eliminar", tint = Color.Gray)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
/*
@Composable
fun Tecnica369Screen(navController: NavHostController,relaxViewModel: RelaxViewModel = viewModel()) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                IconButton(onClick = { navController.navigate("relaxScreen") }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                }
                Text("Técnica 3-6-9 Screen")
            }
        }
    }
}*/
data class TecnicaStep(
    val description: String,
    val duration: Long // Duration in milliseconds
)
@Composable
fun Tecnica369Screen(navController: NavHostController, relaxViewModel: RelaxViewModel = viewModel()) {

    val tecnica = relaxViewModel.getTecnicaByNombre("Técnica 3-6-9")
    var isRunning by remember { mutableStateOf(false) }
    var currentStepIndex by remember { mutableStateOf(0) }

    val steps = remember {
        listOf(
            TecnicaStep("Inhala por la nariz", 3000L),
            TecnicaStep("Realiza una pausa y mantén la respiración", 6000L),
            TecnicaStep("Exhala lentamente por la boca", 9000L)
        )
    }

    var remainingTime by remember { mutableStateOf(0L) }
    var showEndOptions by remember { mutableStateOf(false) }



    LaunchedEffect(isRunning, currentStepIndex) {
        if (isRunning && currentStepIndex < steps.size) {
            remainingTime = steps[currentStepIndex].duration
            val stepDuration = steps[currentStepIndex].duration
            launch {
                while (remainingTime > 0 && isActive) {
                    delay(100) // Update every 100 milliseconds (adjust for smoothness)
                    remainingTime -= 100
                }
            }
            delay(stepDuration) // Wait for the step's duration
            if (currentStepIndex < steps.size - 1) {
                currentStepIndex++
            } else {
                isRunning = false
                showEndOptions = true // Show options after last step
            }
        }
    }


    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
                //.verticalScroll(rememberScrollState())
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(verticalAlignment = Alignment.CenterVertically,horizontalArrangement = Arrangement.Start,modifier = Modifier
                .fillMaxWidth()) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                }
            }

            Spacer(modifier = Modifier.height(30.dp))


            if (tecnica != null) {
                Text(
                    text = tecnica.nombre,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(30.dp))

                if (!isRunning && !showEndOptions) {
                    Text(
                        text = tecnica.descripcion,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 28.dp)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = {
                            isRunning = true
                            currentStepIndex = 0
                        },
                        modifier = Modifier
                            .width(160.dp)
                            .height(50.dp)
                            .align(Alignment.CenterHorizontally),
                        shape = RoundedCornerShape(25.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5CC2C6))
                    ) {
                        Text("Empezar", color = Color.White)
                    }
                } else if (isRunning) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = steps[currentStepIndex].description,
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 28.dp)
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        CircularProgressIndicator(
                            progress = (remainingTime.toFloat() / steps[currentStepIndex].duration).coerceIn(0f, 1f),
                            color = Color.White,
                            strokeWidth = 10.dp,
                            modifier = Modifier.size(150.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "${(remainingTime / 1000.0).roundToInt()} segundos",
                            color = Color.White,
                            fontSize = 18.sp
                        )
                    }
                } else if (showEndOptions) {
                    Spacer(modifier = Modifier.height(30.dp))
                    Text(
                        text = "Acabas de completar con éxito tu técnica de relajación. Date un momento para reconocer este logro: has dedicado tiempo a cuidar de ti mismo/a, y eso es algo increíble.",
                        color = Color.White,
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Text(
                        text = "¡Sigue así! Cada paso cuenta.",
                        color = Color.White,
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Spacer(modifier = Modifier.height(30.dp))

                    Button(
                        onClick = {
                            isRunning = true
                            currentStepIndex = 0
                            showEndOptions = false
                        },
                        modifier = Modifier.padding(horizontal = 8.dp).width(160.dp)
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5CC2C6))
                    ) {
                        Text("Reiniciar", color = Color.White)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { navController.popBackStack()
                            if (tecnica != null) {
                                relaxViewModel.completeCard(tecnica) // Add to completed
                            }  },
                        modifier = Modifier.padding(horizontal = 8.dp).width(160.dp)
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                    ) {
                        Text("Terminar", color = Color.White)
                    }

                }

            } else {
                Text("Descripción no encontrada", fontSize = 16.sp)
            }
        }
    }
}
@Composable
fun RelajacionMuscularScreen(navController: NavHostController, relaxViewModel: RelaxViewModel = viewModel()) {
    val tecnica = relaxViewModel.getTecnicaByNombre("Relajación muscular")
    var currentStepIndex by remember { mutableStateOf(0) }
    var showFinalOptions by remember { mutableStateOf(false) }
    var showDescription by remember { mutableStateOf(true) }

    val steps = remember {
        listOf(
            TecnicaStep("Encuentra un lugar tranquilo y respira profundamente unas cuantas veces para relajarte.", 0L),
            TecnicaStep("Tensa los músculos de los pies lo máximo que puedas y mantenlos así durante unos segundos. Luego, relájalos y siente la diferencia entre la tensión y la relajación", 0L),
            TecnicaStep("Avanza por el cuerpo (manos, abdomen, ...) siguiendo el mismo proceso de tensar y relajar.", 0L),
            TecnicaStep("A medida que avanzas presta atención a las sensaciones de tensión y relajación.", 0L),
            TecnicaStep("Una vez hayas finalizado, haz unas cuantas respiraciones profundas.", 0L)
        )
    }

    val totalSteps = steps.size

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            if (tecnica != null) {
                Text(
                    text = tecnica.nombre,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(24.dp))
                if (showDescription) {
                    Text(
                        text = tecnica.descripcion,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 28.dp)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = {
                            showDescription = false
                        },
                        modifier = Modifier
                            .width(160.dp)
                            .height(50.dp)
                            .align(Alignment.CenterHorizontally),
                        shape = RoundedCornerShape(25.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5CC2C6))
                    ) {
                        Text("Empezar", color = Color.White)
                    }
                }
                if (!showFinalOptions && !showDescription) {
                    // Display the current step
                    Text(
                        text = steps[currentStepIndex].description,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 18.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Navigation buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (currentStepIndex > 0) {
                            IconButton(onClick = { currentStepIndex-- }) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Anterior")
                            }
                        } else {
                            Spacer(modifier = Modifier.width(120.dp))
                        }

                        if (currentStepIndex < totalSteps - 1) {
                            IconButton(onClick = { currentStepIndex++ }) {
                                Icon(Icons.Default.ArrowForward, contentDescription = "Siguiente")
                            }
                        } else {
                            IconButton(onClick = { showFinalOptions = true }) {
                                Icon(Icons.Default.ArrowForward, contentDescription = "Terminar")
                            }
                        }
                    }
                } else if (showFinalOptions == true){
                    Spacer(modifier = Modifier.height(30.dp))
                    Text(
                        text = "Acabas de completar con éxito tu técnica de relajación. Date un momento para reconocer este logro: has dedicado tiempo a cuidar de ti mismo/a, y eso es algo increíble.",
                        color = Color.White,
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Text(
                        text = "¡Sigue así! Cada paso cuenta.",
                        color = Color.White,
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Spacer(modifier = Modifier.height(30.dp))

                    Button(
                        onClick = {
                            navController.popBackStack()
                            if (tecnica != null) {
                                relaxViewModel.completeCard(tecnica)
                            }
                        },
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .width(160.dp)
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                    ) {
                        Text("Terminar", color = Color.White)
                    }
                }
            } else {
                Text("Descripción no encontrada", fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun VisualizacionGuiadaScreen(navController: NavHostController, relaxViewModel: RelaxViewModel = viewModel()) {
    val tecnica = relaxViewModel.getTecnicaByNombre("Visualización guiada")
    var showDescription by remember { mutableStateOf(true) }
    var isPlaying by remember { mutableStateOf(false) }
    var showFinalOptions by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val mediaPlayer = remember { MediaPlayer() }

    //val rawAudioResourceId = remember { R.raw.sonido_visualizacion } // Asegúrate de tener el archivo en res/raw
    val audioUri = remember {
        Uri.parse("android.resource://${context.packageName}/${R.raw.sonido_visualizacion}")
    }
    LaunchedEffect(showDescription) {
        if (!showDescription && !showFinalOptions) {
            try {
                mediaPlayer.apply {
                    reset()
                    setDataSource(context, audioUri)
                    prepare()
                    start()
                    isPlaying = true
                }
            } catch (e: Exception) {
                e.printStackTrace()
                isPlaying = false
                showFinalOptions = true //
            }
        } else {
            mediaPlayer.pause()
            isPlaying = false
        }
    }

    DisposableEffect(mediaPlayer) {
        onDispose {
            mediaPlayer.release()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Start, modifier = Modifier.fillMaxWidth()) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (tecnica != null) {
                Text(
                    text = tecnica.nombre,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(24.dp))

                if (showDescription) {
                    // Mostrar la descripción
                    Text(
                        text = tecnica.descripcion,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 28.dp)
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = { showDescription = false },
                        modifier = Modifier
                            .width(160.dp)
                            .height(50.dp)
                            .align(Alignment.CenterHorizontally),
                        shape = RoundedCornerShape(25.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5CC2C6))
                    ) {
                        Text("Empezar", color = Color.White)
                    }
                } else if (!showFinalOptions) {
                    // Mostrar el control de audio
                    Text(
                        text = "Escucha la visualización guiada.",
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 18.dp)
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    Icon(
                        imageVector = Icons.Filled.Headphones,
                        contentDescription = "Audio",
                        tint = Color.White,
                        modifier = Modifier
                            .size(130.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    IconButton(
                        onClick = {
                            if (mediaPlayer.isPlaying) {
                                mediaPlayer.pause()
                                isPlaying = false
                            } else {
                                mediaPlayer.start()
                                isPlaying = true
                            }
                        },
                        modifier = Modifier
                            .size(70.dp)
                            .background(Color(0xFF5CC2C6), shape = CircleShape)
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                            contentDescription = if (isPlaying) "Pausar" else "Reproducir",
                            tint = Color.White,
                            modifier = Modifier.size(50.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = {
                            mediaPlayer.pause()
                            showFinalOptions = true
                        },
                        modifier = Modifier
                            .width(100.dp)
                            .height(50.dp),
                        shape = RoundedCornerShape(25.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                    ) {
                        Text("Listo", color = Color.White)
                    }

                } else {
                    Spacer(modifier = Modifier.height(30.dp))
                    Text(
                        text = "Acabas de completar con éxito tu técnica de relajación. Date un momento para reconocer este logro: has dedicado tiempo a cuidar de ti mismo/a, y eso es algo increíble.",
                        color = Color.White,
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Text(
                        text = "¡Sigue así! Cada paso cuenta.",
                        color = Color.White,
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Spacer(modifier = Modifier.height(30.dp))

                    Button(
                        onClick = {
                            navController.popBackStack()
                            relaxViewModel.completeCard(tecnica)
                        },
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .width(160.dp)
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                    ) {
                        Text("Terminar", color = Color.White)
                    }
                }
            } else {
                Text("Descripción no encontrada", fontSize = 16.sp, color = Color.White)
            }
        }
    }
}




