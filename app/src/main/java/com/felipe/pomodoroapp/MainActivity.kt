package com.felipe.pomodoroapp

import android.R
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.felipe.pomodoroapp.ui.theme.PomodoroAppTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.selects.select


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PomodoroAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    PomodoroApp()
                }
            }
        }
    }
}

// ===== NUEVO: ENUM para los estados del Pomodoro =====
enum class PomodoroState {
    WORK,           // Trabajo (25 min)
    SHORT_BREAK,    // Descanso corto (5 min)
    LONG_BREAK      // Descanso largo (15 min)
}

// ===== NUEVO: Clase que maneja la logica del Pomodoro =====
class PomodoroTimer {
    // Tiempos en segundos
    val workTime        = 25 * 60
    val shortBreakTime  = 5 * 60
    val longBreakTime   = 15 * 60

    var currentState by mutableStateOf(PomodoroState.WORK)
    var timeLeft by mutableStateOf(workTime)
    var isRunning by mutableStateOf(false)
    var completedSessions by mutableStateOf(0)
    var workSessionBeforeLongBreak = 4      // 4 pomodoros antes de descanso largo
    var currentWorkSessionCount = 0         // Contador de sesiones de trabajo completadas

    // Funcion para manejar cuando el tiempo llega a 0
    private fun onTimerFinished() {
        isRunning = false

        when (currentState) {
            PomodoroState.WORK -> {
                // Se completo una sesion de trabajo
                completedSessions = completedSessions + 1
                currentWorkSessionCount = currentWorkSessionCount + 1

                // Decidir que tipo de descanso sigue
                if (currentWorkSessionCount >= workSessionBeforeLongBreak) {
                    // Cambiar a descanso largo y resetear contador
                    currentState = PomodoroState.LONG_BREAK
                    currentWorkSessionCount = 0
                } else {
                    // Cambiar a descanso corto
                    currentState = PomodoroState.SHORT_BREAK
                }
            }

            PomodoroState.SHORT_BREAK,
            PomodoroState.LONG_BREAK -> {
                // Termino el descanso, volver al trabajo
                currentState = PomodoroState.WORK
            }
        }

        // Actualizar tiempo para el nuevo estado
        updateTimeForCurrentState()

        // Aqui despues agregaremos el sonido
        println("TIMER FINISHED! Estado acutal: $currentState")
    }

    //Actualiza el tiempo segun el estado actual
    private fun updateTimeForCurrentState() {
        timeLeft = when (currentState) {
            PomodoroState.WORK -> workTime
            PomodoroState.SHORT_BREAK -> shortBreakTime
            PomodoroState.LONG_BREAK -> longBreakTime
        }
    }

    fun toggleTimer() {
        isRunning = !isRunning
    }

    fun resetTimer() {
        isRunning = false
        when (currentState) {
            PomodoroState.WORK -> timeLeft = workTime
            PomodoroState.SHORT_BREAK -> timeLeft = shortBreakTime
            PomodoroState.LONG_BREAK -> timeLeft = longBreakTime
        }
    }

    fun switchToWork() {
        currentState = PomodoroState.WORK
        resetTimer()
    }

    fun switchToShortBreak() {
        currentState = PomodoroState.SHORT_BREAK
        resetTimer()
    }

    fun switchToLongBreak() {
        currentState = PomodoroState.LONG_BREAK
        resetTimer()
    }

    fun updateTime() {
        if (isRunning && timeLeft > 0) {
            timeLeft = timeLeft - 1
        } else if (timeLeft == 0) {
            isRunning = false
            // Aqui ira el sonido despues
        }
    }
}

// ===== APP PRINCIPAL =====
@Composable
fun PomodoroApp() {
    val pomodoroTimer = remember { PomodoroTimer() }

    // Esto actualiza el tiempo cada segundo
    LaunchedEffect(pomodoroTimer.isRunning) {
        while (pomodoroTimer.isRunning) {
            delay(1000L)
            pomodoroTimer.updateTime()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Mostrar estado actual
        Text(
            text = when (pomodoroTimer.currentState) {
                PomodoroState.WORK -> "⏰ TRABAJO"
                PomodoroState.SHORT_BREAK -> "☕ DESCANSO CORTO"
                PomodoroState.LONG_BREAK -> "🌴 DESCANSO LARGO"
            },
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Contador de sesiones
        Text(
            text = "Pomodoros completados: ${pomodoroTimer.completedSessions}",
            fontSize = 16.sp
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Temporizador (formato MM:SS)
        val minutes = pomodoroTimer.timeLeft / 60
        val seconds = pomodoroTimer.timeLeft % 60
        val timeFormatted = String.format("%02d:%02d", minutes, seconds)

        Text(
            text = timeFormatted,
            fontSize = 72.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(48.dp))

        // ===== BOTONES PRINCIPALES =====
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Boton Start/Pause
            Button(
                onClick = { pomodoroTimer.toggleTimer() },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (pomodoroTimer.isRunning) MaterialTheme.colorScheme.errorContainer
                    else MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Text(
                    text = if (pomodoroTimer.isRunning) "⏸\uFE0F PAUSA" else "▶\uFE0F INICIAR",
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Boton Reset
            Button(
                onClick = { pomodoroTimer.resetTimer() },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Text("\uD83D\uDD04 REINICIAR", fontSize = 16.sp)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // ===== BOTONES PARA CAMBIAR MODO =====
        Text("Cambiar modo:", fontSize = 16.sp)

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Boton Trabajo
            FilterChip(
                selected = pomodoroTimer.currentState == PomodoroState.WORK,
                onClick = { pomodoroTimer.switchToWork() },
                label = { Text("Trabajo (25 min)") }
            )

            // Boton Descanso Corto
            FilterChip(
                selected = pomodoroTimer.currentState == PomodoroState.SHORT_BREAK,
                onClick = { pomodoroTimer.switchToShortBreak() },
                label = { Text("Descanso (5 min)") }
            )

            // Boton Descanso Largo
            FilterChip(
                selected = pomodoroTimer.currentState == PomodoroState.LONG_BREAK,
                onClick = { pomodoroTimer.switchToLongBreak() },
                label = { Text("Descanso (15 min)") }
            )
        }
    }
}