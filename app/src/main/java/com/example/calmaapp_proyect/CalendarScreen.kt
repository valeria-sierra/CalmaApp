// File: CalendarScreen.kt
package com.example.calmaapp_proyect // Asegúrate que este es el paquete correcto

import android.os.Bundle // No se usa directamente aquí, pero es común en Activities
import android.util.Log
import androidx.activity.ComponentActivity // No se usa directamente aquí
import androidx.activity.compose.setContent // No se usa directamente aquí
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
// Importa 'items' específicamente para LazyVerticalGrid para evitar ambigüedad si también usas LazyColumn con su propio 'items'
import androidx.compose.foundation.lazy.grid.items as gridItems
// Importa 'items' específicamente para LazyColumn
import androidx.compose.foundation.lazy.items as columnItems

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview // Para Previews si las necesitas
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.toObject
import java.text.SimpleDateFormat
import java.util.*
import com.example.calmaapp_proyect.R // Asegúrate que R está bien importado

// --- Colores (Unificados como en tu código original) ---
val desiredButtonColor = Color(0xFF5CC2C6)
val disabledButtonColor = Color.Gray

// --- Estructura de Datos para el Log Diario ---
data class DailyLogEntry(
    val id: String = UUID.randomUUID().toString(),
    val timestamp: Long = System.currentTimeMillis(),
    val mood: String = "",
    val feelings: List<String> = emptyList()
)

// --- ViewModel Modificado para Firebase ---
class CalendarViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private fun getDailyLogsCollection(): CollectionReference? {
        val userId = auth.currentUser?.uid
        return if (userId != null) {
            db.collection("users").document(userId).collection("dailyLogs")
        } else {
            Log.w("CalendarViewModel", "Usuario no autenticado. No se pueden acceder a los logs.")
            null
        }
    }

    private val _dailyLogs = mutableStateOf<List<DailyLogEntry>>(emptyList())
    val dailyLogs: State<List<DailyLogEntry>> = _dailyLogs

    private val _entriesForStreakDays = mutableStateOf<Set<Int>>(emptySet())

    private val _streakCount = mutableStateOf(0)
    val streakCount: State<Int> = _streakCount

    private val _bestStreak = mutableStateOf(0)
    val bestStreak: State<Int> = _bestStreak

    private val _isLoading = mutableStateOf(true)
    val isLoading: State<Boolean> = _isLoading

    init {
        loadDailyLogsFromFirestore()
    }

    private fun loadDailyLogsFromFirestore() {
        _isLoading.value = true
        val logsCollection = getDailyLogsCollection()
        if (logsCollection == null) {
            _dailyLogs.value = emptyList()
            updateStreakRelatedData(emptyList())
            _isLoading.value = false
            return
        }

        logsCollection.orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshots, e ->
                _isLoading.value = false
                if (e != null) {
                    Log.e("CalendarViewModel", "Error al escuchar los logs de Firestore", e)
                    _dailyLogs.value = emptyList()
                    updateStreakRelatedData(emptyList())
                    return@addSnapshotListener
                }
                val newLogsList = snapshots?.mapNotNull { documentSnapshot ->
                    try {
                        documentSnapshot.toObject<DailyLogEntry>()
                    } catch (ex: Exception) {
                        Log.e("CalendarViewModel", "Error convirtiendo documento a DailyLogEntry", ex)
                        null
                    }
                } ?: emptyList()
                _dailyLogs.value = newLogsList
                updateStreakRelatedData(newLogsList)
                Log.d("CalendarViewModel", "Logs cargados/actualizados desde Firestore: ${newLogsList.size} entradas.")
            }
    }

    private fun updateStreakRelatedData(logs: List<DailyLogEntry>) {
        _entriesForStreakDays.value = logs.map {
            val calendar = Calendar.getInstance().apply { timeInMillis = it.timestamp }
            calendar.get(Calendar.DAY_OF_MONTH)
        }.toSet()
        calculateStreak()
    }

    fun hasEntryForDay(day: Int): Boolean {
        return try {
            _entriesForStreakDays.value.contains(day)
        } catch (e: Exception) {
            Log.e("CalendarViewModel", "Error en hasEntryForDay", e)
            false
        }
    }

    fun addDailyLog(mood: String, feelings: List<String>) {
        val logsCollection = getDailyLogsCollection()
        if (logsCollection == null) {
            Log.e("CalendarViewModel", "Error: Usuario no autenticado. No se puede añadir el log.")
            return
        }
        val newLog = DailyLogEntry(mood = mood, feelings = feelings)
        logsCollection.document(newLog.id).set(newLog)
            .addOnSuccessListener {
                Log.d("CalendarViewModel", "Log diario añadido a Firestore con ID: ${newLog.id}")
            }
            .addOnFailureListener { e ->
                Log.e("CalendarViewModel", "Error al añadir log diario a Firestore", e)
            }
    }

    private fun getStartOfDayTimestamp(timestamp: Long): Long {
        return Calendar.getInstance().apply {
            timeInMillis = timestamp
            set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    private fun calculateStreak() {
        try {
            val logs = _dailyLogs.value
            if (logs.isEmpty()) {
                _streakCount.value = 0; return
            }
            val uniqueLogDayTimestamps = logs.map { getStartOfDayTimestamp(it.timestamp) }.distinct().sortedDescending()
            if (uniqueLogDayTimestamps.isEmpty()) {
                _streakCount.value = 0; return
            }
            var currentStreak = 0
            val todayStart = getStartOfDayTimestamp(System.currentTimeMillis())
            var expectedDayTimestamp = todayStart
            for (logTimestamp in uniqueLogDayTimestamps) {
                if (logTimestamp == expectedDayTimestamp) {
                    currentStreak++
                    val cal = Calendar.getInstance().apply { timeInMillis = expectedDayTimestamp }
                    cal.add(Calendar.DAY_OF_YEAR, -1)
                    expectedDayTimestamp = getStartOfDayTimestamp(cal.timeInMillis)
                } else if (logTimestamp < expectedDayTimestamp) {
                    break
                }
            }
            _streakCount.value = currentStreak
            if (currentStreak > _bestStreak.value) {
                _bestStreak.value = currentStreak
                // TODO: Guardar _bestStreak en Firebase
            }
            Log.d("CalendarViewModel", "Racha calculada: Actual=$currentStreak, Mejor=${_bestStreak.value}")
        } catch (e: Exception) {
            Log.e("CalendarViewModel", "Error en calculateStreak", e)
            _streakCount.value = 0
        }
    }
}

// --- App Navigation Sealed Class ---
sealed class AppScreen {
    object Calendar : AppScreen()
    object SelectMood : AppScreen()
    data class SelectFeelings(val mood: String) : AppScreen()
    data class MoodConfirmation(val feelings: List<String>, val mood: String) : AppScreen()
    object SelfEsteemEvaluation : AppScreen()
    object DailyLogReport : AppScreen()
}

// --- Punto de Entrada de la UI de esta sección/módulo ---
@Composable
fun CalmaAppEntry() {
    Box(modifier = Modifier.fillMaxSize()){
        Image(
            painter = painterResource(id = R.drawable.ic_background),
            contentDescription = "Fondo de la aplicación",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alpha = 0.7f
        )
        CalmaApp()
    }
}

// --- Composable principal que maneja la navegación interna ---
@Composable
fun CalmaApp() {
    val calendarViewModel: CalendarViewModel = viewModel()
    var currentScreen by remember { mutableStateOf<AppScreen>(AppScreen.Calendar) }

    Box(modifier = Modifier.fillMaxSize()) {
        if (calendarViewModel.isLoading.value && currentScreen == AppScreen.Calendar) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            when (val screen = currentScreen) {
                is AppScreen.Calendar -> CalendarScreenContent(
                    viewModel = calendarViewModel,
                    onMoodButtonClick = { currentScreen = AppScreen.SelectMood },
                    onNavigateToReport = { currentScreen = AppScreen.DailyLogReport }
                )
                is AppScreen.SelectMood -> SelectMoodScreen(
                    onMoodSelected = { mood -> currentScreen = AppScreen.SelectFeelings(mood) },
                    onBack = { currentScreen = AppScreen.Calendar }
                )
                is AppScreen.SelectFeelings -> SelectFeelingsScreen(
                    selectedMood = screen.mood,
                    onFeelingSelected = { feelings -> currentScreen = AppScreen.MoodConfirmation(feelings, screen.mood) },
                    onBack = { currentScreen = AppScreen.SelectMood }
                )
                is AppScreen.MoodConfirmation -> MoodConfirmationScreen(
                    viewModel = calendarViewModel,
                    selectedFeelings = screen.feelings,
                    selectedMood = screen.mood,
                    onHablaSobreEsto = { /* TODO */ },
                    onFinalizarRegistro = {
                        calendarViewModel.addDailyLog(screen.mood, screen.feelings)
                        currentScreen = AppScreen.Calendar
                    },
                    onBack = { currentScreen = AppScreen.SelectFeelings(screen.mood) }
                )
                is AppScreen.DailyLogReport -> DailyLogReportScreen(
                    viewModel = calendarViewModel,
                    onBack = { currentScreen = AppScreen.Calendar }
                )
                is AppScreen.SelfEsteemEvaluation -> SelfEsteemEvaluationScreen(
                    onBackToCalendar = { currentScreen = AppScreen.Calendar }
                )
            }
        }
    }
}

// --- Contenido de la Pantalla del Calendario ---
@Composable
fun CalendarScreenContent(
    viewModel: CalendarViewModel,
    onMoodButtonClick: () -> Unit,
    onNavigateToReport: () -> Unit
) {
    val streakCount by viewModel.streakCount
    val textColor = Color.White

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        Text("CALMA APP", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = textColor, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(32.dp))
        Text("¿Cómo te sientes hoy?", fontSize = 18.sp, color = textColor)
        Spacer(modifier = Modifier.height(16.dp))
        CalendarView(viewModel = viewModel)
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onNavigateToReport,
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(containerColor = desiredButtonColor),
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Text("Ver mis registros diarios", color = Color.White)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onMoodButtonClick,
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(containerColor = desiredButtonColor),
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Text("Registrar estado de ánimo", color = Color.White)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Racha actual: $streakCount días", fontSize = 16.sp, color = textColor)
        Spacer(modifier = Modifier.weight(1f))
    }
}

// --- Vista del Calendario (Grid) ---
@Composable
fun CalendarView(viewModel: CalendarViewModel) {
    val displayCalendar = remember { Calendar.getInstance() }
    val currentMonthCalendar = Calendar.getInstance().apply {
        timeInMillis = displayCalendar.timeInMillis
        set(Calendar.DAY_OF_MONTH, 1)
    }
    val daysInMonth = currentMonthCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    val firstDayOfWeek = currentMonthCalendar.get(Calendar.DAY_OF_WEEK)
    val emptyCells = (firstDayOfWeek - Calendar.MONDAY + 7) % 7
    val daysList = List(daysInMonth) { it + 1 }
    val allCells = List(emptyCells) { null } + daysList
    val monthFormat = remember { SimpleDateFormat("MMMM", Locale("es", "ES")) }
    val monthName = remember(currentMonthCalendar.get(Calendar.MONTH), currentMonthCalendar.get(Calendar.YEAR)) {
        val cal = Calendar.getInstance().apply { timeInMillis = currentMonthCalendar.timeInMillis }
        monthFormat.format(cal.time).replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale("es", "ES")) else it.toString() } +
                " ${cal.get(Calendar.YEAR)}"
    }
    val calendarTextColor = Color(0xFF006064)

    Column(
        modifier = Modifier
            .fillMaxWidth(0.95f)
            .background(Color(0xFFE0F7FA).copy(alpha = 0.9f), RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Text(monthName, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = calendarTextColor, modifier = Modifier.align(Alignment.CenterHorizontally))
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.SpaceAround, modifier = Modifier.fillMaxWidth()) {
            listOf("L", "M", "X", "J", "V", "S", "D").forEach { day ->
                Text(day, color = calendarTextColor, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp), fontSize = 12.sp)
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.fillMaxWidth(),
            userScrollEnabled = false,
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            gridItems(allCells) { day -> // Usa gridItems
                val todayCalendar = Calendar.getInstance()
                val isToday = day != null &&
                        day == todayCalendar.get(Calendar.DAY_OF_MONTH) &&
                        currentMonthCalendar.get(Calendar.MONTH) == todayCalendar.get(Calendar.MONTH) &&
                        currentMonthCalendar.get(Calendar.YEAR) == todayCalendar.get(Calendar.YEAR)
                val hasEntry = if (day != null) viewModel.hasEntryForDay(day) else false
                DayCell(day = day, isToday = isToday, hasEntry = hasEntry, onDayClick = { clickedDay ->
                    if (clickedDay != null) Log.d("CalendarView", "Clicked on day: $clickedDay")
                })
            }
        }
    }
}

// --- Celda Individual del Día en el Calendario ---
@Composable
fun DayCell(day: Int?, isToday: Boolean, hasEntry: Boolean, onDayClick: (Int?) -> Unit) {
    val cellTextColor = if (isToday) Color.White else Color(0xFF006064)
    Box(
        modifier = Modifier.size(36.dp).clip(CircleShape)
            .clickable(enabled = day != null) { onDayClick(day) }
            .background(
                when {
                    isToday -> desiredButtonColor; hasEntry -> Color(0xFFA2DFDD); else -> Color.Transparent
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        if (day != null) {
            Text(text = day.toString(), color = cellTextColor, fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal, fontSize = 14.sp)
        }
    }
}

// --- Pantalla para Seleccionar Estado de Ánimo (Mood) ---
@Composable
fun SelectMoodScreen(onMoodSelected: (mood: String) -> Unit, onBack: () -> Unit) {
    val textColor = Color.White
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, "Atrás", tint = textColor) }
            Text("¿Cómo te sientes hoy?", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = textColor, textAlign = TextAlign.Center, modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.size(48.dp))
        }
        Spacer(modifier = Modifier.weight(0.5f))
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 100.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            gridItems(listOf( // Usa gridItems
                "Muy Bien" to "😊", "Feliz" to "😄", "Neutral" to "😐",
                "Confundido" to "😕", "Mal" to "😞", "Triste" to "😢", "Enojado" to "😠"
            )) { (label, emoji) ->
                MoodButton(mood = emoji, label = label, onClick = { onMoodSelected(label) })
            }
        }
        Spacer(modifier = Modifier.weight(1f))
    }
}

// --- Botón Individual para Mood ---
@Composable
fun MoodButton(mood: String, label: String, onClick: () -> Unit) {
    val textColor = Color.White
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick).padding(8.dp).width(100.dp)
    ) {
        Text(mood, fontSize = 48.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(label, color = textColor, fontSize = 14.sp, textAlign = TextAlign.Center)
    }
}

// --- Pantalla para Seleccionar Sentimientos Específicos ---
@Composable
fun SelectFeelingsScreen(
    selectedMood: String,
    onFeelingSelected: (List<String>) -> Unit,
    onBack: () -> Unit
) {
    val textColor = Color.White
    val availableFeelings = remember {
        listOf(
            "Feliz", "Alegre", "Contento", "Optimista", "Motivado", "Entusiasmado", "Orgulloso", "Agradecido",
            "Esperanzado", "Relajado", "Sereno", "Cómodo", "Aliviado", "Seguro", "Inspirado", "Activo",
            "Triste", "Enojado", "Ansioso", "Preocupado", "Estresado", "Frustrado", "Irritable", "Decepcionado",
            "Culpable", "Avergonzado", "Asustado", "Nervioso", "Solo", "Vacío", "Aburrido", "Cansado",
            "Trabajo", "Estudios", "Familia", "Amigos", "Pareja", "Salud", "Dinero", "Hogar",
            "Mascota", "Hobbies", "Ejercicio", "Descanso"
        )
    }
    val selectedFeelings = remember { mutableStateOf<Set<String>>(emptySet()) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, "Atrás", tint = textColor) }
            Text("Hoy me siento ($selectedMood)...", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = textColor, textAlign = TextAlign.Center, modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.size(48.dp))
        }
        Spacer(modifier = Modifier.height(16.dp))
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 100.dp),
            contentPadding = PaddingValues(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f)
        ) {
            gridItems(availableFeelings) { feeling -> // Usa gridItems
                val isSelected = selectedFeelings.value.contains(feeling)
                Button(
                    onClick = {
                        selectedFeelings.value = if (isSelected) selectedFeelings.value - feeling else selectedFeelings.value + feeling
                    },
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = desiredButtonColor, contentColor = Color.White),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(feeling, fontSize = 12.sp, maxLines = 1)
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { onFeelingSelected(selectedFeelings.value.toList()) },
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (selectedFeelings.value.isNotEmpty()) desiredButtonColor else disabledButtonColor,
                contentColor = Color.White
            ),
            enabled = selectedFeelings.value.isNotEmpty(),
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Text("Confirmar Sentimientos", color = Color.White, fontWeight = FontWeight.SemiBold)
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

// --- Función Auxiliar para Obtener Emoji ---
fun getEmojiForMood(mood: String): String {
    return when (mood) {
        "Muy Bien" -> "😊"; "Feliz" -> "😄"; "Neutral" -> "😐"; "Confundido" -> "😕"
        "Mal" -> "😞"; "Triste" -> "😢"; "Enojado" -> "😠"; else -> "🤔"
    }
}

// --- Pantalla de Confirmación del Registro ---
@Composable
fun MoodConfirmationScreen(
    viewModel: CalendarViewModel, // Se pasa el ViewModel
    selectedFeelings: List<String>,
    selectedMood: String,
    onHablaSobreEsto: () -> Unit,
    onFinalizarRegistro: () -> Unit, // Ya no llama directamente a addDailyLog
    onBack: () -> Unit
) {
    val generalTextColor = Color.White
    // val streakCount by viewModel.streakCount // No se usa directamente aquí, pero podría
    // val bestStreak by viewModel.bestStreak  // No se usa directamente aquí

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, "Atrás", tint = generalTextColor) }
            Text("Registro completado", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = generalTextColor, textAlign = TextAlign.Center, modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.size(48.dp))
        }
        Spacer(modifier = Modifier.height(32.dp))
        Box(
            modifier = Modifier.fillMaxWidth(0.9f)
                .background(Color(0xFFE0F7FA).copy(alpha = 0.9f), RoundedCornerShape(16.dp))
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(getEmojiForMood(selectedMood), fontSize = 56.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(selectedMood, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF006064))
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (selectedFeelings.isNotEmpty()) selectedFeelings.joinToString(", ") else "Sin sentimientos adicionales",
                    color = Color(0xFF4DB6AC), textAlign = TextAlign.Center, fontSize = 14.sp
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        // Botones de acción (no se usa onHablaSobreEsto en tu código original)
        /* Button(onClick = onHablaSobreEsto, ...) { Text("Habla sobre esto") } */
        // Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onFinalizarRegistro, // Esta lambda se encarga de llamar al ViewModel y navegar
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(containerColor = desiredButtonColor),
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Text("Finalizar", color = Color.White)
        }
        Spacer(modifier = Modifier.weight(1f)) // Empuja el contenido hacia arriba
        // Mostrar racha si se desea
        val streakCount by viewModel.streakCount
        Text("Racha actual: $streakCount días", color = generalTextColor, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(16.dp))
    }
}

// --- Pantalla para Ver Reporte de Logs Diarios ---
@Composable
fun DailyLogReportScreen(viewModel: CalendarViewModel, onBack: () -> Unit) {
    val logs by viewModel.dailyLogs
    val textColor = Color.White

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, "Atrás", tint = textColor) }
            Text("Mis Registros Diarios", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = textColor)
        }
        Spacer(modifier = Modifier.height(16.dp))
        if (viewModel.isLoading.value && logs.isEmpty()) { // Muestra carga solo si no hay logs aún
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else if (logs.isEmpty()) {
            Text("Aún no tienes registros.", color = textColor, modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                columnItems(logs) { log -> // Usa columnItems
                    DailyLogItemView(log)
                }
            }
        }
    }
}

// --- Ítem para la Lista de Logs Diarios ---
@Composable
fun DailyLogItemView(log: DailyLogEntry) {
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()) } // Corregido formato
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE0F7FA).copy(alpha = 0.8f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Mood: ${log.mood}", fontWeight = FontWeight.Bold, color = Color(0xFF006064))
            Text("Sentimientos: ${log.feelings.joinToString(", ")}", color = Color(0xFF00796B))
            Text("Fecha: ${dateFormat.format(Date(log.timestamp))}", fontSize = 12.sp, color = Color.Gray)
        }
    }
}

// --- Pantalla Placeholder para Evaluación de Autoestima ---
@Composable
fun SelfEsteemEvaluationScreen(onBackToCalendar: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text("Pantalla: Evaluación de Autoestima (Placeholder)", color = Color.White, fontSize = 18.sp, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(20.dp))
        Button(onClick = onBackToCalendar) { Text("Volver al Calendario") }
    }
}

// --- Preview (Opcional) ---
@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
fun CalmaAppEntryPreview() { // Renombrado para claridad
    // Para el preview, el ViewModel no tendrá datos reales de Firebase
    // pero la UI se puede previsualizar.
    CalmaAppEntry()
}