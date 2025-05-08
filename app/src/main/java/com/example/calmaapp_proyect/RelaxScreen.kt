package com.example.calmaapp_proyect

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

@Composable
fun RelaxScreen(navController: NavHostController) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
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
                    icon = Icons.Default.CheckCircle,
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

            // Inicializa Firebase Auth y Firestore
            val auth = FirebaseAuth.getInstance()
            val firestore = FirebaseFirestore.getInstance()

            // Función para guardar una tarjeta en Firebase
            val saveCard: (String, Map<String, Any>) -> Unit = { cardId, cardData ->
                val userId = auth.currentUser?.uid
                if (userId != null) {
                    firestore.collection("users").document(userId).collection("saved").document(cardId)
                        .set(cardData)
                        .addOnSuccessListener {
                            Log.d("Firebase", "Técnica guardada con éxito")
                        }
                        .addOnFailureListener { e ->
                            Log.e("Firebase", "Error al guardar la técnica: ", e)
                        }
                } else {
                    Log.e("Firebase", "Usuario no autenticado")
                }
            }

            // Función para marcar una tarjeta como completada en Firebase
            val completeCard: (String, Map<String, Any>) -> Unit = { cardId, cardData ->
                val userId = auth.currentUser?.uid
                if (userId != null) {
                    firestore.collection("users").document(userId).collection("completed").document(cardId)
                        .set(cardData)
                        .addOnSuccessListener {
                            Log.d("Firebase", "Técnica marcada como completada")
                        }
                        .addOnFailureListener { e ->
                            Log.e("Firebase", "Error al marcar la técnica como completada: ", e)
                        }
                } else {
                    Log.e("Firebase", "Usuario no autenticado")
                }
            }

            // Tarjetas de relajación
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                RelaxCard(
                    title = "Técnica 3 - 6 - 9",
                    image = painterResource(id = R.drawable.ic_background),
                    onCardClick = { navController.navigate("tecnica369Screen") },
                    onSaveClick = {
                        saveCard(
                            "tecnica369",
                            mapOf("title" to "Técnica 3 - 6 - 9")
                        )
                    },
                    onCompleteClick = {
                        completeCard(
                            "tecnica369",
                            mapOf("title" to "Técnica 3 - 6 - 9")
                        )
                    }
                )
                RelaxCard(
                    title = "Atención Plena",
                    image = painterResource(id = R.drawable.ic_background),
                    onCardClick = { navController.navigate("atencionPlenaScreen") },
                    onSaveClick = {
                        saveCard(
                            "atencionPlena",
                            mapOf("title" to "Atención Plena")
                        )
                    },
                    onCompleteClick = {
                        completeCard(
                            "atencionPlena",
                            mapOf("title" to "Atención Plena")
                        )
                    }
                )
                RelaxCard(
                    title = "Respiración Abdominal",
                    image = painterResource(id = R.drawable.ic_background),
                    onCardClick = { navController.navigate("respiracionAbdominalScreen") },
                    onSaveClick = {
                        saveCard(
                            "respiracionAbdominal",
                            mapOf("title" to "Respiración Abdominal")
                        )
                    },
                    onCompleteClick = {
                        completeCard(
                            "respiracionAbdominal",
                            mapOf("title" to "Respiración Abdominal")
                        )
                    }
                )

                RelaxCard(
                    title = "Visualización Guiada",
                    image = painterResource(id = R.drawable.ic_background),
                    onCardClick = { navController.navigate("visualizacionGuiadaScreen") },
                    onSaveClick = {
                        saveCard(
                            "visualizacionGuiada",
                            mapOf("title" to "Visualización Guiada")
                        )
                    },
                    onCompleteClick = {
                        completeCard(
                            "visualizacionGuiada",
                            mapOf("title" to "Visualización Guiada")
                        )
                    }
                )
                // agregar más...
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
    title: String,
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
                contentDescription = title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
                contentScale = ContentScale.Crop
            )
            Column(Modifier.padding(horizontal = 12.dp)) {
                Text(text = title, fontSize = 18.sp, color = Color.White)
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(onClick = { onSaveClick() }) {
                        Icon(Icons.Default.CheckCircle, contentDescription = "Guardar", tint = Color.White)
                    }
                    IconButton(onClick = { onCompleteClick() }) {
                        Icon(Icons.Default.Check, contentDescription = "Completar", tint = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun MainRelaxScreen() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "relaxScreen") {
        composable("relaxScreen") { RelaxScreen(navController) }
        composable("guardados") { SavedRelax(navController) }
        composable("completados") { CompletedRelax(navController) }
        composable("tecnica369Screen") { Tecnica369Screen(navController) }
        composable("atencionPlenaScreen") { AtencionPlenaScreen(navController) }
        composable("visualizacionGuiadaScreen") { VisualizacionGuiadaScreen(navController) }
        composable("respiracionAbdominalScreen") { RespiracionAbdominalScreen(navController) }
    }
}

@Composable
fun SavedRelax(navController: NavHostController) {
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    val userId = auth.currentUser?.uid

    val savedCards = remember { mutableStateListOf<Map<String, Any>>() }

    LaunchedEffect(userId) {
        if (userId != null) {
            firestore.collection("users").document(userId).collection("saved").get()
                .addOnSuccessListener { result ->
                    savedCards.clear()
                    for (document in result) {
                        savedCards.add(document.data)
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("Firebase", "Error al obtener las técnicas guardadas: ", e)
                }
        }
    }

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
                        // Usa un diseño de tarjeta personalizado aquí
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFB2EBF2), shape = RoundedCornerShape(20.dp))
                                .padding(12.dp)

                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {

                                Column(Modifier.padding(horizontal = 12.dp)) {
                                    Text(text = card["title"].toString(), fontSize = 18.sp, color = Color.White)
                                }
                                Row(
                                    horizontalArrangement = Arrangement.End,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    IconButton(onClick = {
                                        // Lógica para eliminar la tarjeta de Firebase
                                        val cardToDelete = card as? Map<*, *>
                                        if (cardToDelete != null) {
                                            val titleToDelete = cardToDelete["title"]?.toString()
                                            if (titleToDelete != null) {
                                                deleteSavedCard(userId, titleToDelete) // Llama a la función para eliminar
                                            }
                                        }
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

@Composable
fun CompletedRelax(navController: NavHostController) {
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    val userId = auth.currentUser?.uid

    val completedCards = remember { mutableStateListOf<Map<String, Any>>() }

    LaunchedEffect(userId) {
        if (userId != null) {
            firestore.collection("users").document(userId).collection("completed").get()
                .addOnSuccessListener { result ->
                    completedCards.clear()
                    for (document in result) {
                        completedCards.add(document.data)
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("Firebase", "Error al obtener las técnicas completadas: ", e)
                }
        }
    }

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
                        // Usa un diseño de tarjeta personalizado aquí
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFB2EBF2), shape = RoundedCornerShape(20.dp))
                                .padding(12.dp)

                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                // En este caso no tenemos Image
                                Column(Modifier.padding(horizontal = 12.dp)) {
                                    Text(text = card["title"].toString(), fontSize = 18.sp, color = Color.White)
                                }
                                Row(
                                    horizontalArrangement = Arrangement.End,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    IconButton(onClick = {
                                        // Lógica para eliminar la tarjeta de Firebase
                                        val cardToDelete = card as? Map<*, *>
                                        if (cardToDelete != null) {
                                            val titleToDelete = cardToDelete["title"]?.toString()
                                            if (titleToDelete != null) {
                                                deleteCompletedCard(userId, titleToDelete) // Llama a la función para eliminar
                                            }
                                        }
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

@Composable
fun Tecnica369Screen(navController: NavHostController) {
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
}

@Composable
fun AtencionPlenaScreen(navController: NavHostController) {
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
                Text("Atención Plena Screen")
            }
        }
    }
}



@Composable
fun RespiracionAbdominalScreen(navController: NavHostController) {
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
                Text("Respiración Abdominal Screen")
            }
        }
    }
}


@Composable
fun VisualizacionGuiadaScreen(navController: NavHostController) {
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
                Text("Visualización Guiada Screen")
            }
        }
    }
}


// Función para eliminar una tarjeta guardada de Firebase
fun deleteSavedCard(userId: String?, title: String) {
    val firestore = FirebaseFirestore.getInstance()
    if (userId != null) {
        firestore.collection("users").document(userId).collection("saved")
            .whereEqualTo("title", title)
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot) {
                    document.reference.delete()
                        .addOnSuccessListener {
                            Log.d("Firebase", "Técnica eliminada con éxito")
                        }
                        .addOnFailureListener { e ->
                            Log.e("Firebase", "Error al eliminar la técnica: ", e)
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.e("Firebase", "Error al obtener la técnica a eliminar: ", e)
            }
    } else {
        Log.e("Firebase", "Usuario no autenticado")
    }
}

// Función para eliminar una tarjeta completada de Firebase
fun deleteCompletedCard(userId: String?, title: String) {
    val firestore = FirebaseFirestore.getInstance()
    if (userId != null) {
        firestore.collection("users").document(userId).collection("completed")
            .whereEqualTo("title", title)
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot) {
                    document.reference.delete()
                        .addOnSuccessListener {
                            Log.d("Firebase", "técnica eliminada con éxito")
                        }
                        .addOnFailureListener { e ->
                            Log.e("Firebase", "Error al eliminar la técnica: ", e)
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.e("Firebase", "Error al obtener la técnica a eliminar: ", e)
            }
    } else {
        Log.e("Firebase", "Usuario no autenticado")
    }
}
